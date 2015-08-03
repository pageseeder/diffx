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
package org.pageseeder.diffx.load;

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
	@Override
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
