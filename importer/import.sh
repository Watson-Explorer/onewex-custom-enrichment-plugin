#!/bin/bash
#
# Copyright 2018 IBM Corporation
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

ONEWEX_HOST=${ONEWEX_HOST:-}
WEXAC_HOST=${WEXAC_HOST:-}
WEXAC_USER=${WEXAC_USER:-esadmin}
WEXAC_PASSWORD=${WEXAC_PASSWORD:-}
ONEWEX_USER=${ONEWEX_USER:-admin}
ONEWEX_PASSWORD=${ONEWEX_PASSWORD:-}
WEXAC_COLLECTION_ID=${WEXAC_COLLECTION_ID:-}
PATH_TO_PLUGIN=${PATH_TO_PLUGIN:-../build/plugins/plugin-mla-plugin.zip}
PYTHON=python3

fail() {
    echo "$1, abort";
    exit 1;
}

# Check required parameters
if [ -z "$ONEWEX_HOST" ]; then fail "ONEWEX_HOST must not be empty"; fi
if [ -z "$WEXAC_HOST" ]; then fail "WEXAC_HOST must not be empty"; fi
if [ -z "$WEXAC_COLLECTION_ID" ]; then fail "WEXAC_COLLECTION_ID must not be empty"; fi
if [ -z "$WEXAC_PASSWORD" ]; then read -s -p "Password for WEXAC user $WEXAC_USER: " WEXAC_PASSWORD; fi
if [ -z "$ONEWEX_PASSWORD" ]; then read -s -p "Password for oneWEX user $ONEWEX_USER: " ONEWEX_PASSWORD; fi

# Create file resource
echo "Creating file resource..."
FILE_RESOURCE_ID=`curl -f -s -k --user admin:admin -X POST "https://$ONEWEX_HOST/api/v1/fileResources" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"description\": \"ML Annotator (AC) Enrichment Plugin\", \"name\": \"ML Annotator\", \"type\": \"plugin\"}" | python -c 'import json,sys;obj=json.load(sys.stdin);print(obj["id"])'`
if [[ $? -ne 0 ]]; then fail "Failed to create file resource"; fi
echo "Created file resource id = $FILE_RESOURCE_ID"

# Upload ML Annotator Plugin
echo "Uploading ML Annotator plugin..."
curl -f -s -k --user "$ONEWEX_USER:$ONEWEX_PASSWORD" -X POST "https://$ONEWEX_HOST/api/v1/fileResources/$FILE_RESOURCE_ID/upload" -H "accept: application/json" -H "Content-Type: multipart/form-data" -F "file=@$PATH_TO_PLUGIN"
if [[ $? -ne 0 ]]; then fail "Failed to upload ML Annotator plugin"; fi
echo "Uploaded ML Annotator plugin"

# Get admin token
echo "Accquiring WEXAC admin token"
curl -f -k -XPOST "http://$WEXAC_HOST:8390/api/v20/admin/login" --data "username=$WEXAC_USER&password=$WEXAC_PASSWORD&output=application/json"
TOKEN=`curl -f -s -k -XPOST "http://$WEXAC_HOST:8390/api/v20/admin/login" --data "username=$WEXAC_USER&password=$WEXAC_PASSWORD&output=application/json" | python -c 'import json,sys;obj=json.load(sys.stdin);print(obj["es_apiResponse"]["es_securityToken"])'`
if [[ $? -ne 0 ]]; then fail "Failed to get admin token"; fi
echo "Accquired WEXAC admin token = $TOKEN"

# Get facet tree
echo "Accquiring facet tree of collection $WEXAC_COLLECTION_ID"
curl -f -s -k -XPOST "http://$WEXAC_HOST:8390/api/v20/admin/collections/indexer/analyticsFacets/list" --data "securityToken=$TOKEN&collection=$WEXAC_COLLECTION_ID&output=application/json" | $PYTHON -m json.tool > facets.json
if [[ $? -ne 0 ]]; then fail "Failed to get facet tree"; fi
echo "Saved WEX AC facet tree to facets.json"

# Convert to oneWEX enrichment
echo "Converting facet tree to oneWEX enrichment configuration"
$PYTHON converter.py $FILE_RESOURCE_ID < facets.json | $PYTHON -m json.tool > mla.json
if [[ $? -ne 0 ]]; then fail "Failed to convert to oneWEX enrichment"; fi
echo "Saved converted oneWEX enrichment configuration to mla.json"

# Create ML Annotator enrichment
echo "Creating ML Annotator enrichment"
curl -f -s -k --user "$ONEWEX_USER:$ONEWEX_PASSWORD" -X POST "https://$ONEWEX_HOST/api/v1/enrichments" -H "accept: application/json" -H "Content-Type: application/json" -d "@mla.json"
if [[ $? -ne 0 ]]; then fail "Failed to create ML Annotator enrichment"; fi
echo "Created ML Annotator enrichment"

# Clean up
rm -f facets.json mla.json

echo "Done."
