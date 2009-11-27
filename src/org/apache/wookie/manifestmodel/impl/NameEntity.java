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

package org.apache.wookie.manifestmodel.impl;

import org.apache.wookie.manifestmodel.INameEntity;
import org.apache.wookie.manifestmodel.IW3CXMLConfiguration;
import org.apache.wookie.util.UnicodeUtils;
import org.jdom.Element;

/**
 * @author Paul Sharples
 * @version $Id: NameEntity.java,v 1.3 2009-09-02 18:37:31 scottwilson Exp $
 */
public class NameEntity extends LocalizedEntity implements INameEntity {
	
	private String fName;
	private String fShort;

	
	public NameEntity(){
		fName = "";
		fShort = "";
		setLanguage("");
	}
	
	public NameEntity(String name, String short1, String language) {
		super();
		fName = name;
		fShort = short1;
		setLanguage(language);
	}
	
	public String getName() {
		return fName;
	}
	
	public void setName(String name) {
		fName = name;
	}
	
	public String getShort() {
		return fShort;
	}
	
	public void setShort(String short1) {
		fShort = short1;
	}
	
	public String getXMLTagName() {
		return IW3CXMLConfiguration.NAME_ELEMENT;
	}
	
	public void fromXML(Element element) {		
		super.fromXML(element);
		// Get the text value of name
		fName = UnicodeUtils.normalizeWhitespace(element.getText());
		// Get the short attribute (if exists)
		fShort = UnicodeUtils.normalizeSpaces(element.getAttributeValue(IW3CXMLConfiguration.SHORT_ATTRIBUTE));
	}

}