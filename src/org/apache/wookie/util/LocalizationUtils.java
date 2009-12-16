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
package org.apache.wookie.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.wookie.beans.ILocalizedElement;

import com.ibm.icu.util.GlobalizationPreferences;
import com.ibm.icu.util.ULocale;

/**
 * Utilities for localization
 * @author Scott Wilson
 *
 */
public class LocalizationUtils {
	
	/**
	 * Returns the first (best) match for an element given the set of locales, or null
	 * if there are no suitable elements.
	 * @param locales
	 * @param elements
	 * @return an ILocalizedElement, or null if there are no valid entries
	 */
	public static ILocalizedElement getLocalizedElement(ILocalizedElement[] elements,String[] locales){
		processElementsByLocales(elements,locales);
		if (elements.length == 0) return null;
		return elements[0];
	}
	
	/**
	 * Filters and sorts a list of localized elements using the given locale list
	 * 
	 * @param locales
	 * @return
	 */
	public static ILocalizedElement[] processElementsByLocales(ILocalizedElement[] elements,String[] locales){
		List<ULocale> localesList = getProcessedLocaleList(locales);
		Arrays.sort(elements, new LocaleComparator(localesList));
		return filter(elements,localesList);
	}
	
	/**
	 * Sorts an array of localized elements using default locales (*). This places
	 * any localized elements first, then any unlocalized elements
	 * @return
	 */
	public static ILocalizedElement[] processElementsByDefaultLocales(ILocalizedElement[] elements){
		List<ULocale> localesList = getDefaultLocaleList();
		Arrays.sort(elements, new LocaleComparator(localesList));
		return filter(elements,localesList);
	}
	
	/**
	 * Comparator that sorts elements based on priority in the given locale list,
	 * with the assumption that earlier in the list means a higher priority.
	 */
	static class LocaleComparator implements Comparator<ILocalizedElement>{
		private List<ULocale> locales;
		public LocaleComparator(List<ULocale> locales){
			this.locales = locales;
		}
		public int compare(ILocalizedElement o1, ILocalizedElement o2) {
			// check non-localized values for comparison
			if (o1.getLang()!=null && o2.getLang() == null) return -1;
			if (o1.getLang()==null && o2.getLang()!= null) return 1;
			if (o1.getLang()==null && o2.getLang()==null) return 0;
			
			// get index position of locales
			int o1i = -1;
			int o2i = -1;
			for (int i=0;i<locales.size();i++){
				if (o1.getLang().equalsIgnoreCase(locales.get(i).toLanguageTag())) o1i = i;
				if (o2.getLang().equalsIgnoreCase(locales.get(i).toLanguageTag())) o2i = i;
			}
			// put non-matched after matched
			if (o1i == -1 && o2i > -1) return 1;
			if (o1i > -1 && o2i == -1) return -1;
			
			// order by match
			return o1i - o2i;
		}
	}
	
	/**
	 * Filters a set of elements using the locales and returns a copy of the array
	 * containing only elements that match the locales, or that are not localized
	 * if there are no matches
	 * @param elements
	 * @param locales
	 * @return a copy of the array of elements only containing the filtered elements
	 */
	protected static ILocalizedElement[] filter(ILocalizedElement[] elements, List<ULocale> locales){		
		for (ILocalizedElement element:elements){
			String lang = element.getLang();
			boolean found = false;
			for(ULocale locale:locales){
				if (locale.toLanguageTag().equalsIgnoreCase(lang)) found = true;
			}
			if (!found && lang != null) elements = (ILocalizedElement[])ArrayUtils.removeElement(elements, element);
		}
		// Strip out non-localized elements only if there are localized elements left
		if (elements.length > 0){
			if (elements[0].getLang() != null){
				for (ILocalizedElement element:elements){
					if (element.getLang()==null) elements = (ILocalizedElement[])ArrayUtils.removeElement(elements, element);
				}
			}
		}
		return elements;
	}	

	
	/**
	 * Converts an array of language tags to a list of ULocale instances
	 * ordered according to priority, availability and fallback
	 * rules. For example "en-us-posix" will be returned as a List
	 * containing the ULocales for "en-us-posix", "en-us", and "en".
	 * @param locales
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected static List<ULocale> getProcessedLocaleList(String[] locales){
		if (locales == null) return getDefaultLocaleList();
		GlobalizationPreferences prefs = new GlobalizationPreferences();
		
		ArrayList<ULocale> ulocales = new ArrayList<ULocale>();
		for (String locale:locales){
			if (locale != null){
				ULocale ulocale = ULocale.forLanguageTag(locale);
				if (!ulocale.getLanguage().equals(""))
					ulocales.add(ulocale);
			}
		}	
		if (ulocales.isEmpty()) return getDefaultLocaleList();
		prefs.setLocales(ulocales.toArray(new ULocale[ulocales.size()]));
		return prefs.getLocales();
	}
	
	/**
	 * Gets the default locales list
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected static List<ULocale> getDefaultLocaleList(){
		GlobalizationPreferences prefs = new GlobalizationPreferences();
		return prefs.getLocales();
	}
	
	/**
	 * Validates a given language tag. Empty and null values are considered false.
	 * @param tag
	 * @return true if a valid language tag, otherwise false
	 */
	public static boolean isValidLanguageTag(String tag){
		try {
			ULocale locale = ULocale.forLanguageTag(tag);
			if (locale.toLanguageTag() == null) return false;
			// We don't accept "x" extensions (private use tags)
			if(locale.getExtension("x".charAt(0))!=null) return false;
			if (!locale.toLanguageTag().equalsIgnoreCase(tag)) return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
