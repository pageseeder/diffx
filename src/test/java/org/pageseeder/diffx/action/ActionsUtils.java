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
package org.pageseeder.diffx.action;

import org.pageseeder.diffx.event.DiffXEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The action digester reads a list of actions and with an input sequence can
 * produce an output sequence.
 *
 * The digester can be used to verify that a sequence of actions is a solution
 * to a DiffX problem.
 *
 * @author Christophe Lauret
 * @version 11 December 2008
 */
public class ActionsUtils {

	/**
	 * Generates the list of events from the list of actions.
	 *
	 * @param actions The list of actions.
	 * @param positive
	 *            <code>true</code> for generating the new sequence;
	 *            <code>false</code> for generating the old sequence.
	 */
	public static List<DiffXEvent> generate(List<Action> actions, boolean positive) {
		List<DiffXEvent> generated = new LinkedList<>();
		for (Action action : actions) {
			if (positive ? action.type() == Operator.INS : action.type() == Operator.DEL) {
				generated.addAll(action.events());
			} else if (action.type() == Operator.KEEP) {
				generated.addAll(action.events());
			}
		}
		return generated;
	}

	/**
	 * Returns the minimal string from the list of actions.
	 *
	 * @param actions The list of actions.
	 */
	public static Operator[] minimal(List<Action> actions) {
		ArrayList<Operator> minimal = new ArrayList<>();
		for (Action action : actions) {
			Collections.addAll(minimal, action.minimal());
		}
		Operator[] ops = new Operator[minimal.size()];
		return minimal.toArray(ops);
	}

	public static boolean isValid(List<DiffXEvent> a, List<DiffXEvent> b, List<Action> actions) {
		int i = 0;
		int j = 0;
		for (Action action : actions) {
			if (action.type() == Operator.KEEP) {
				for (DiffXEvent e : action.events()) {
					if (!e.equals(a.get(i))) return false;
					if (!e.equals(b.get(j))) return false;
					i++;
					j++;
				}
			} else if (action.type() == Operator.INS) {
				for (DiffXEvent e : action.events()) {
					if (!e.equals(b.get(j))) return false;
					j++;
				}
			} else if (action.type() == Operator.DEL) {
				for (DiffXEvent e : action.events()) {
					if (!e.equals(a.get(i))) return false;
					i++;
				}
			}

		}
		return true;
	}

}
