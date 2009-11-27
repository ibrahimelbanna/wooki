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

import org.apache.wookie.manifestmodel.ILicenseEntity;
import org.apache.wookie.manifestmodel.IW3CXMLConfiguration;
import org.apache.wookie.util.UnicodeUtils;
import org.jdom.Element;
/**
 * @author Paul Sharples
 * @version $Id: LicenseEntity.java,v 1.3 2009-09-02 18:37:31 scottwilson Exp $
 */
public class LicenseEntity extends LocalizedEntity implements ILicenseEntity {
	
	private String fLicenseText;
	private String fHref;
	
	public LicenseEntity(){
		fLicenseText = "";
		fHref = "";
		setLanguage("");
	}
	
	public LicenseEntity(String licenseText, String href, String language) {
		super();
		fLicenseText = licenseText;
		fHref = href;
		setLanguage(language);
	}

	public String getLicenseText() {
		return fLicenseText;
	}

	public void setLicenseText(String licenseText) {
		fLicenseText = licenseText;
	}

	public String getHref() {
		return fHref;
	}

	public void setHref(String href) {
		fHref = href;
	}

	public String getXMLTagName() {
		return IW3CXMLConfiguration.LICENSE_ELEMENT;
	}
	
	public void fromXML(Element element) {
		super.fromXML(element);
		fLicenseText = UnicodeUtils.normalizeWhitespace(element.getText());
		fHref = UnicodeUtils.normalizeSpaces(element.getAttributeValue(IW3CXMLConfiguration.HREF_ATTRIBUTE));					
	}


	
	

}