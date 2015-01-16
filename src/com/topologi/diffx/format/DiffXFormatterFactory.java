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
public class DiffXFormatterFactory {
	public static final String DIFFX_FORMATTER_SMART      = "smart";
	public static final String DIFFX_FORMATTER_CONVENIENT = "convenient";
	public static final String DIFFX_FORMATTER_BASIC      = "basic";
	public static final String DIFFX_FORMATTER_SAFE       = "safe";
	public static final String DIFFX_FORMATTER_STRICT     = "strict";
	public static final String DIFFX_FORMATTER_SHORT      = "short";


	/**
	 * Returna DiffXFormatter according the parameters sent.
	 * @param formatterType
	 * @param out
	 * @return <code>XMLDiffXFormatter</code>
	 * @throws IOException
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
