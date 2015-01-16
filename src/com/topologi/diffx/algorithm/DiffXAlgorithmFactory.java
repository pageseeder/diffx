/*
 * This file is part of the DiffX library.
 *
 * For licensing information please see the file license.txt included in the release.
 * A copy of this licence can also be found at
 *   http://www.opensource.org/licenses/artistic-license-2.0.php
 */
package com.topologi.diffx.algorithm;

import com.topologi.diffx.sequence.EventSequence;

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
    if (algorithmType == null || algorithmType.equals(DIFFX_ALGORITHM_FITSY))
    	algorithm = new DiffXFitsy(seq1, seq2);
    else if (algorithmType.equals(DIFFX_ALGORITHM_GUANO))
    	algorithm = new GuanoAlgorithm(seq1, seq2);
    else if (algorithmType.equals(DIFFX_ALGORITHM_FITOPSY))
    	algorithm = new DiffXFitopsy(seq1, seq2);
    else if (algorithmType.equals(DIFFX_ALGORITHM_KUMAR))
    	algorithm = new DiffXKumarRangan(seq1, seq2);
    else if (algorithmType.equals(DIFFX_ALGORITHM_WESYMA))
    	algorithm = new DiffXFitWesyma(seq1, seq2);
    else {
    	algorithm = new GuanoAlgorithm(seq1, seq2);
    }
    return algorithm;
  }
}
