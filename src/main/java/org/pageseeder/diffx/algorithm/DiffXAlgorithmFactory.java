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
package org.pageseeder.diffx.algorithm;

import org.pageseeder.diffx.sequence.EventSequence;

/**
 *
 *
 * @author Carlos Cabral
 * @version 08 Jan 2015
 */
public class DiffXAlgorithmFactory {
	public static final String DIFFX_ALGORITHM_FITSY   = "fitsy";
	public static final String DIFFX_ALGORITHM_GUANO   = "guano";
	public static final String DIFFX_ALGORITHM_FITOPSY = "fitopsy";
	public static final String DIFFX_ALGORITHM_KUMAR   = "kumar";
	public static final String DIFFX_ALGORITHM_WESYMA  = "wesyma";

	/**
	 * Return a DiffXAlgorithm accoring the parameters sent
	 * @param algorithmType
	 * @param seq1
	 * @param seq2
	 * @return <code>DiffXAlgorithm</code>
	 */
	public DiffXAlgorithm getAlgorithm(String algorithmType,
			EventSequence seq1, EventSequence seq2) {
		DiffXAlgorithm algorithm = null;
    if (algorithmType == null || algorithmType.equals(DIFFX_ALGORITHM_FITSY)) {
      algorithm = new DiffXFitsy(seq1, seq2);
    } else if (algorithmType.equals(DIFFX_ALGORITHM_GUANO)) {
      algorithm = new GuanoAlgorithm(seq1, seq2);
    } else if (algorithmType.equals(DIFFX_ALGORITHM_FITOPSY)) {
      algorithm = new DiffXFitopsy(seq1, seq2);
    } else if (algorithmType.equals(DIFFX_ALGORITHM_KUMAR)) {
      algorithm = new DiffXKumarRangan(seq1, seq2);
    } else if (algorithmType.equals(DIFFX_ALGORITHM_WESYMA)) {
      algorithm = new DiffXFitWesyma(seq1, seq2);
    } else {
    	algorithm = new GuanoAlgorithm(seq1, seq2);
    }
    return algorithm;
  }
}
