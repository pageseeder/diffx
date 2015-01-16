/*
 * This file is part of the DiffX library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package com.topologi.diffx.format;

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
	 * @param formatterType
	 * @param out
	 * @return <code>XMLDiffXFormatter</code>
	 * @throws IOException
	 */
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
