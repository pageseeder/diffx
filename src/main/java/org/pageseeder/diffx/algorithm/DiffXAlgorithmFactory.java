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
 * @author Christophe Lauret
 *
 * @version 0.9.0
 */
public class DiffXAlgorithmFactory {

	public static final String DIFFX_ALGORITHM_GUANO   = "guano";
	public static final String DIFFX_ALGORITHM_FITOPSY = "fitopsy";
	public static final String DIFFX_ALGORITHM_KUMAR   = "kumar";

	/**
	 * Return a DiffXAlgorithm accurring the parameters sent

	 * @return <code>DiffXAlgorithm</code>
	 */
	public DiffXAlgorithm getAlgorithm(String algorithmType, EventSequence seq1, EventSequence seq2) {
		switch (algorithmType) {
			case DIFFX_ALGORITHM_KUMAR: return new DiffXKumarRangan(seq1, seq2);
			case DIFFX_ALGORITHM_FITOPSY: return new DiffXFitopsy(seq1, seq2);
			// We make `guano` the default
			default: return new GuanoAlgorithm(seq1, seq2);
		}

  }
}
