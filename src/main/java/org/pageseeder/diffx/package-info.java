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
/**
 * The core set of classes used by Diff-X.
 *
 * <p>The only class that will remain stable and is safe to use, is the <code>Main</code> class.</p>
 * <p>It also corresponds to the class that is being called from the command-line. To to a diff
 * of two XML documents, it there best to use one of the two <code>Main.diff</code> methods, it
 * will be plugged to the most stable algorithm implementation and formatter.</p>
 */
package org.pageseeder.diffx;
