/*
 * Copyright 2010-2021 Allette Systems (Australia)
 *    http://www.allette.com.au
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

import org.pageseeder.diffx.handler.FormattingAdapter;
import org.pageseeder.diffx.sequence.PrefixMapping;

import java.io.IOException;
import java.io.Writer;

public class LegacyXMLDiffOutput extends FormattingAdapter implements XMLDiffOutput {

  LegacyXMLDiffOutput(Writer w) throws IOException {
    super(new SmartXMLFormatter(w));
  }

  @Override
  public void setWriteXMLDeclaration(boolean show) {
    ((SmartXMLFormatter) super.formatter).setWriteXMLDeclaration(show);
  }

  @Override
  public void declarePrefixMapping(PrefixMapping mapping) {
    ((SmartXMLFormatter) super.formatter).declarePrefixMapping(mapping);
  }

}
