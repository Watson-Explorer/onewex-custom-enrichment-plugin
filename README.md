# Use Machine Learning Annotator Plugin to enable ML annotator in oneWEX

   Create a `Machine Learning Annotator Plugin` enrichment for new collections

## 1. Build Machine Learning Annotator Plugin for Custom Enrichment

  You will need to copy `/opt/ibm/wex/nlp/lib` in oneWEX to `lib` to resolve the dependencies.

  * Edit `plugins/mla-plugin/src/main/resources/server.properties`, Configure WEX AC server
  
     ```
     wexac.host=localhost
     wexac.port=8393
     wexac.collectionId=ml
     ```

  * run `gradle build` to build Mahchine Learning Annotator plugin, find the plugin archive under plugin folder.
  
    Note 1: Gradle 5 is not available. Use Gradle 4.
    
    Note 2: Implement only one implementation of CustomEnrichment 
  
  * Create file resource

  * Upload Mahchine Learning Annotator Plugin


## 2. Configure CustomEnrichment in oneWEX

  * Get facet mappings from WEX AC
    * Get security token
    * Get facet tree
    * Convert to oneWEX facet
  
  * Create Enrichment
    * reate enrichment with file resource
  
  * Enable Mahchine Learning Annotator enrichment
    * Enable the enrichment and rebuild the index


### Import Tool

   For Step 2, an import tool is provided for converting and importing the facet mappings

   Go to importer and run `./import.sh` with following environment variables. Bash and Python3 is required.
   
| Name                  | Description                                                                                                |
|-----------------------|------------------------------------------------------------------------------------------------------------|
| ONEWEX\_HOST          | oneWEX host                                                                                                |
| WEXAC\_HOST           | WEX AC host                                                                                                |
| WEXAC\_USER           | WEX AC user name, esadmin by default                                                                       |
| WEXAC\_PASSWORD       | WEX AC user password, prompt for input if missing                                                          |
| WEXAC\_COLLECTION\_ID | WEX AC collection ID                                                                                       |
| PATH\_TO\_PLUGIN      | Path to Mahchine Learning Annotator plugin, build/plugins/plugin-mla-plugin-0.0.1-SNAPSHOT.zip by default |


  * Example

   ```BASH
   ONEWEX_HOST=localhost WEXAC_HOST=localhost WEXAC_COLLECTION_ID=ml ./import.sh
   ```

