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
public class XMLRecorderFactory extends RecorderFactory{

	/**
	 * Return a XMLRecorder according the parameter.
	 *
	 * @param recorderType
	 * @return <code>XMLRecorder</code>
	 */
	public XMLRecorder getRecorder(String recorderType){
		Recorder recorder = null;
    if ( recorder == null
    		|| (!recorderType.equals(RECORDER_TYPE_SAX)
    				&& !recorderType.equals(RECORDER_TYPE_DOM))) {
    	recorderType = RECORDER_TYPE_SAX;
    }
    return (XMLRecorder) super.getRecorder(recorderType);
	}
}
