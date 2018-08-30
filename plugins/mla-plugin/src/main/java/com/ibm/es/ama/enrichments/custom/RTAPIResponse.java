/**
 * BEGIN_COPYRIGHT
 *
 * Copyright 2018 IBM Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *
 * END_COPYRIGHT
 */

package com.ibm.es.ama.enrichments.custom;

import java.util.List;

public class RTAPIResponse {
	public static class Metadata {
		public List<Field> fields;
		public List<Field> getFields() {
			return fields;
		}
		public void setFields(List<Field> fields) {
			this.fields = fields;
		}
		public List<Docfacet> getDocfacets() {
			return docfacets;
		}
		public void setDocfacets(List<Docfacet> docfacets) {
			this.docfacets = docfacets;
		}
		public List<Textfacet> getTextfacets() {
			return textfacets;
		}
		public void setTextfacets(List<Textfacet> textfacets) {
			this.textfacets = textfacets;
		}
		public List<Docfacet> docfacets;
		public List<Textfacet> textfacets;
	}

	public static class Field {
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		String name;
		String value;
	}

	public static class Docfacet {
		List<String> path;
		String keyword;
		public List<String> getPath() {
			return path;
		}
		public void setPath(List<String> path) {
			this.path = path;
		}
		public String getKeyword() {
			return keyword;
		}
		public void setKeyword(String keyword) {
			this.keyword = keyword;
		}
	}

	public static class Textfacet {
		List<String> path;
		String keyword;
		public List<String> getPath() {
			return path;
		}
		public void setPath(List<String> path) {
			this.path = path;
		}
		public String getKeyword() {
			return keyword;
		}
		public void setKeyword(String keyword) {
			this.keyword = keyword;
		}
		public int getBegin() {
			return begin;
		}
		public void setBegin(int begin) {
			this.begin = begin;
		}
		public int getEnd() {
			return end;
		}
		public void setEnd(int end) {
			this.end = end;
		}
		int begin;
		int end;
	}

	public String uri;
	public String content;
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Metadata getMetadata() {
		return metadata;
	}
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}
	public int getPartition() {
		return partition;
	}
	public void setPartition(int partition) {
		this.partition = partition;
	}
	public Metadata metadata;
	public int partition;
}


