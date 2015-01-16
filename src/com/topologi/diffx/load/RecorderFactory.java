/*
 * This file is part of the DiffX library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package com.topologi.diffx.load;

/**
 *
 *
 * @author Carlos Cabral
 * @version 08 Jan 2015
 */
public class RecorderFactory {

	public static final String RECORDER_TYPE_SAX  = "sax";
	public static final String RECORDER_TYPE_DOM  = "dom";
	public static final String RECORDER_TYPE_TEXT = "text";

	/**
	 * Return a Recorder according the parameter.
	 *
	 * @param recorderType
	 * @return <code>Recorder</code>
	 */
	public Recorder getRecorder(String recorderType){
		Recorder recorder = null;
    if (recorderType == null || recorderType.equals(RECORDER_TYPE_SAX)) {
    	recorder = new SAXRecorder();
    } else if (recorderType.equals(RECORDER_TYPE_DOM)) {
    	recorder = new DOMRecorder();
    } else if (recorderType.equals(RECORDER_TYPE_TEXT)) {
    	recorder =  new TextRecorder();
		} else {
    	recorder =  new SAXRecorder();
    }
    return recorder;
	}
}
