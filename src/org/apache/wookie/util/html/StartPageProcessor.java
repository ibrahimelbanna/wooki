/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wookie.util.html;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.wookie.beans.ServerFeature;
import org.apache.wookie.feature.IFeature;
import org.apache.wookie.manifestmodel.IFeatureEntity;
import org.apache.wookie.manifestmodel.IManifestModel;

/**
 * Processes widget start pages to inject scripts and other assets required for widget runtime operation.
 */
public class StartPageProcessor {
	
	static final String DWR_UTIL_SRC_VALUE = "/wookie/dwr/util.js";
	static final String DWR_ENGINE_SRC_VALUE = "/wookie/dwr/engine.js";
	static final String WIDGET_IMPL_SRC_VALUE = "/wookie/dwr/interface/WidgetImpl.js";
	static final String WOOKIE_WRAPPER_SRC_VALUE = "/wookie/shared/js/wookie-wrapper.js";

	/**
	 * Process a start file ready for deployment. 
	 * @param startFile the start file
	 * @param model the widget information model
	 * @throws IOException if there is a problem reading or writing to the start file, or if the start file cannot be parsed
	 * with the HTML processor used
	 */
	public static void processStartFile(File startFile, IManifestModel model) throws Exception{
		if (startFile == null) throw new Exception("Start file cannot be processed: file is null");
		if (!startFile.exists()) throw new Exception("Start file cannot be processed:  file does not exist");
		if (!(startFile.canWrite()&&startFile.canRead())) throw new Exception("Start file cannot be processed: read or write permissions missing");
		if (model == null) throw new Exception("Start file cannot be processed: widget model is null");
		IHtmlProcessor engine = new HtmlCleaner();
		engine.setReader(new FileReader(startFile));
		addDefaultScripts(engine);
		addFeatures(engine, model);
		FileWriter writer = new FileWriter(startFile);
		engine.process(writer);
	}
	
	/**
	 * Injects default wrapper and utilities
	 * @param engine
	 */
	private static void addDefaultScripts(IHtmlProcessor engine){
		engine.injectScript(DWR_UTIL_SRC_VALUE);
		engine.injectScript(DWR_ENGINE_SRC_VALUE);
		engine.injectScript(WIDGET_IMPL_SRC_VALUE);
		engine.injectScript(WOOKIE_WRAPPER_SRC_VALUE);
	}
	
	/**
	 * Adds features to widget start file by injecting javascript and stylesheets
	 * required by each supported feature in the model.
	 * @param engine
	 * @param model
	 * @throws Exception if a feature cannot be found and instantiated for the widget.
	 */
	private static void addFeatures(IHtmlProcessor engine,IManifestModel model) throws Exception{
		for (IFeatureEntity feature: model.getFeatures()){
			ServerFeature sf = ServerFeature.findByName(feature.getName());
			IFeature theFeature = getFeatureInstanceForName(sf.getClassName());
			addScripts(engine, theFeature);
			addStylesheets(engine, theFeature);
		}
	}
	
	/**
	 * Instantiates a feature for a given feature name
	 * @param featureName the name of the feature to be instantiated
	 * @return an IFeature instance
	 * @throws Exception if the feature cannot be instantiated
	 */
	private static IFeature getFeatureInstanceForName(String featureName) throws Exception{
		Class<? extends IFeature> klass = (Class<? extends IFeature>) Class.forName(featureName);
		IFeature theFeature = (IFeature) klass.newInstance();
		return theFeature;
	}
	
	/**
	 * Adds scripts for a given feature
	 * @param engine
	 * @param feature
	 */
	private static void addScripts(IHtmlProcessor engine, IFeature feature){
		if (feature.scripts() != null){
			for (String script: feature.scripts()) engine.injectScript(script);
		}
	}
	
	/**
	 * Adds stylesheets for a given feature
	 * @param engine
	 * @param feature
	 */
	private static void addStylesheets(IHtmlProcessor engine, IFeature feature){
		if (feature.stylesheets() != null){
			for (String style: feature.stylesheets()) engine.injectStylesheet(style);
		}
	}
	
}