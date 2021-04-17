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
package org.pageseeder.diffx.util;

import java.io.File;
import java.io.FileFilter;

/**
 * Filename filter for XML files.
 *
 * <p>This filter assumes that an file simply as the .xml file extension.
 *
 * @author Christophe Lauret
 * @version 0.9.0
 */
public final class XMLFilenameFilter implements FileFilter {

  /**
   * The XML extension to be used for filtering the files.
   */
  public static final String DEFAULT_EXTENSION = "xml";

  /**
   * The XML extension to be used for filtering the files.
   */
  private final String ext = DEFAULT_EXTENSION;

  /**
   * Set to <code>true</code> to ignore the case of the extension.
   */
  public final boolean ignoreCase;

  /**
   * Creates a new case-insensitive XML file filter.
   */
  public XMLFilenameFilter() {
    this.ignoreCase = false;
  }

  /**
   * Creates a new XML file filter.
   *
   * @param ignoreCase <code>true</code> to ignore the case of the extension.
   */
  public XMLFilenameFilter(boolean ignoreCase) {
    this.ignoreCase = ignoreCase;
  }

  /**
   * Tests whether or not the specified abstract pathname should be included in a pathname
   * list.
   *
   * <p>A file is accepted if its name has a file extension matching the "xml".
   *
   * @param pathname The abstract pathname to be tested;
   *
   * @return <code>true</code> if and only if pathname has an extension matching "xml".
   * @throws NullPointerException If the path name is <code>null</code>.
   */
  @Override
  public boolean accept(File pathname) throws NullPointerException {
    if (pathname == null) throw new NullPointerException("The specified file is null.");
    String name = pathname.getName();
    int dot = name.lastIndexOf('.');
    if (dot == -1) return false;
    String local = name.substring(dot + 1);
    return this.ignoreCase ? DEFAULT_EXTENSION.equalsIgnoreCase(local) : DEFAULT_EXTENSION.equals(local);
  }

}
