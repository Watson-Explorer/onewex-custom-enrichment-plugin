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

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.codec.Charsets;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.es.ama.enrichments.custom.RTAPIResponse.Textfacet;
import com.ibm.es.ama.enrichments.custom.annotator.Annotator;
import com.ibm.es.ama.enrichments.custom.annotator.AnnotatorProcessException;

public class RealtimeNLPAnnotator extends Annotator {
	private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	private static final String PATH = "/api/v10/analysis/text";
	private static final String PROTOCOL = "http";
	private final String host;
	private final String collectionId;
	private final int port;

	public RealtimeNLPAnnotator(String server, int port, String collectionId) {
		this.host = server;
		this.port = port;
		this.collectionId = collectionId;
	}
	@Override
	public void process(Input input, Output output) throws AnnotatorProcessException {
		try {
			logger.info("Starting realtime NLP annotator");
			URI uri = new URI(PROTOCOL, null, this.host, this.port, PATH, null, null);

			HttpPost post = new HttpPost(uri);

			List<NameValuePair> form = new ArrayList<>();

			form.add(new BasicNameValuePair("collection", this.collectionId));
			form.add(new BasicNameValuePair("text", input.getText()));
			if (input.getLang() != null)
				form.add(new BasicNameValuePair("language", input.getLang()));
			form.add(new BasicNameValuePair("output", "application/json"));

			post.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_FORM_URLENCODED.toString());

			HttpEntity entity = new UrlEncodedFormEntity(form, Charsets.UTF_8);

			post.setEntity(entity);
			HttpClient client = HttpClients.createDefault();
			HttpResponse response = client.execute(post);

			ObjectMapper om = new ObjectMapper();
			RTAPIResponse apiResponse = om.readValue(response.getEntity().getContent(), RTAPIResponse.class);

			for (Textfacet facet : apiResponse.metadata.textfacets) {
				if (facet.path != null && facet.path.size() > 0) {
					String path = facet.path.stream().collect(Collectors.joining(".", ".", ""));
					if (path.startsWith(".relation") || path.startsWith(".mention")) {
						output.add(new FacetAnnotation(path, facet.keyword, facet.begin, facet.end));
					}
				}
			}
		} catch (IOException | URISyntaxException e) {
			logger.severe("Severe error occured when invoking realtime NLP");
			throw new AnnotatorProcessException(e);
		}
	}
}
