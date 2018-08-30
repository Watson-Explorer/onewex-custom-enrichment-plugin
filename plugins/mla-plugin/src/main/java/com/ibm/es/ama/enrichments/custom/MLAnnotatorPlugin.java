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
import java.util.Properties;
import java.util.logging.Logger;

import org.pf4j.Extension;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;

import com.ibm.es.ama.enrichments.custom.annotator.Annotator;

public class MLAnnotatorPlugin extends Plugin {

	private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

	public MLAnnotatorPlugin(PluginWrapper wrapper) {
		super(wrapper);
	}

	@Extension
	public static class RealtimeNLPEnrichment extends CustomEnrichment {
		private static final long serialVersionUID = 1270279124430231547L;

		@Override
		public Annotator getAnnotator() throws CustomEnrichmentException {
			Properties properties = new Properties();
			try {
				properties.load(RealtimeNLPEnrichment.class.getClassLoader().getResourceAsStream("server.properties"));
			} catch (IOException e) {
				logger.severe("Error configuring ML Annotator plugin");
				throw new CustomEnrichmentException(e);
			}
			String host = properties.getProperty("wexac.host", "localhost");
			int port = Integer.parseInt(properties.getProperty("wexac.port", "8393"));
			String collectionId = properties.getProperty("wexac.collectionId", "sire");
			return new RealtimeNLPAnnotator(host, port, collectionId);
		}
	}
}
