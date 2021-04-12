/*
 * Copyright 2010-2015 Allette Systems (Australia)
 * http://www.allette.com.au
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pageseeder.diffx.format;

import java.io.IOException;
import java.io.Writer;
/**
 *
 *
 * @author Carlos Cabral
 * @version 08 Jan 2015
 */
public class DiffXFormatterFactory {
	public static final String DIFFX_FORMATTER_SMART      = "smart";
	public static final String DIFFX_FORMATTER_CONVENIENT = "convenient";
	public static final String DIFFX_FORMATTER_BASIC      = "basic";
	public static final String DIFFX_FORMATTER_SAFE       = "safe";
	public static final String DIFFX_FORMATTER_STRICT     = "strict";
	public static final String DIFFX_FORMATTER_SHORT      = "short";


	/**
	 * Returna DiffXFormatter according the parameters sent.
	 */
	public DiffXFormatter getFormatter(String formatterType, Writer out) throws IOException {
		DiffXFormatter formatter = null;
		if (formatterType == null || formatterType.equals(DIFFX_FORMATTER_SMART)) {
			formatter = new SmartXMLFormatter(out);
    } else if (formatterType.equals(DIFFX_FORMATTER_CONVENIENT)) {
    	formatter = new ConvenientXMLFormatter(out);
    } else if (formatterType.equals(DIFFX_FORMATTER_BASIC)) {
    	formatter = new BasicXMLFormatter(out);
    } else if (formatterType.equals(DIFFX_FORMATTER_STRICT)) {
    	formatter =  new StrictXMLFormatter(out);
    } else if (formatterType.equals(DIFFX_FORMATTER_SHORT)) {
    	formatter =  new ShortStringFormatter(out);
    } else if (formatterType.equals(DIFFX_FORMATTER_SAFE)) {
    	formatter =  new SafeXMLFormatter(out);
    } else {
    	formatter = new SafeXMLFormatter(out);
    }
    return formatter;
  }

}
