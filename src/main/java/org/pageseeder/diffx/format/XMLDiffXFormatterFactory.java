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
public class XMLDiffXFormatterFactory extends DiffXFormatterFactory{

	/**
	 * Returna XMLDiffXFormatter according the parameters sent.
	 */
	@Override
  public XMLDiffXFormatter getFormatter(String formatterType, Writer out) throws IOException {
		if (formatterType == null
				|| (!formatterType.equals(DIFFX_FORMATTER_BASIC)
						&& !formatterType.equals(DIFFX_FORMATTER_CONVENIENT)
						&& !formatterType.equals(DIFFX_FORMATTER_SAFE)
						&& !formatterType.equals(DIFFX_FORMATTER_SMART)
						&& !formatterType.equals(DIFFX_FORMATTER_STRICT))) {
			formatterType = DIFFX_FORMATTER_SAFE;
    }
    return (XMLDiffXFormatter) super.getFormatter(formatterType, out);
  }
}
