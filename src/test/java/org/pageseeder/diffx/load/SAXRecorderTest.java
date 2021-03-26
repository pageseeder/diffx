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

import org.pageseeder.diffx.config.DiffXConfig;

/**
 * Test class for the SAX Recorder.
 *
 * @author Christophe Lauret
 * @version 14 April 2005
 */
public final class SAXRecorderTest extends org.pageseeder.diffx.load.XMLRecorderNSTest {

  /**
   * Default constructor.
   *
   * @param name The name of the loader.
   */
  public SAXRecorderTest(String name) {
    super(name);
  }

  /**
   * @see org.pageseeder.diffx.load.XMLRecorderTest#makeXMLRecorder(org.pageseeder.diffx.config.DiffXConfig)
   */
  public XMLRecorder makeXMLRecorder(DiffXConfig config) {
    SAXRecorder recorder = new SAXRecorder();
    recorder.setConfig(config);
    return recorder;
  }

}