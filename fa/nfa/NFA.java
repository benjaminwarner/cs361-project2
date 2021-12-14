package fa.nfa;

import java.util.*;

import fa.State;
import fa.dfa.DFA;
import fa.dfa.DFAState;
import fa.nfa.NFAState;

/**
 * Set and Map implementation of the NFAInterface.
 * 
 * This class is mainly used to create a DFA representation
 * of the given NFA.
 *
 * There is only a single start state at a time; final and regular
 * states are maintained separately.
 *
 * @author Benjamin Warner
 */
 
public class NFA implements NFAInterface {

	private NFAState startState;
	private Set<NFAState> finalStates;
	private Set<NFAState> states;
	private Set<Character> alphabet;

	/**
	 * Create an empty NFA.
	 */
	public NFA() {
		this.finalStates = new LinkedHashSet<NFAState>();
		this.states = new LinkedHashSet<NFAState>();
		this.alphabet = new LinkedHashSet<Character>();
	}
	
	/**
	 * Sets the start state of the NFA.
	 *
	 * @param name the name of the start state for this machine
	 */
	public void addStartState(String name) {
		this.startState = new NFAState(name);
	}
	
	/**
	 * Add a new state with name to the machine.
	 *
	 * @param name the name of the state to add to this machine
	 */
	public void addState(String name) {
		NFAState state = new NFAState(name);
		this.states.add(state);
	}
	
	/**
	 * Add a final state with name to the machine.
	 *
	 * @param name the name of the final state to add to this machine
	 */
	public void addFinalState(String name) {
		NFAState state = new NFAState(name);
		this.finalStates.add(state);
	}
	
	/**
	 * Add a new transition from one state to another for this machine
	 *
	 * @param fromState the name of the state this machine will be transitioning from
	 * @param onSymb the input character required to perform a transition
	 * @param toState the name of the state this machine will transition to
	 */
	public void addTransition(String fromState, char onSymb, String toState) {
		if (onSymb != 'e')
			this.alphabet.add(onSymb);
		NFAState from = this.findState(fromState);
		NFAState to = this.findState(toState);
		from.addTransition(String.valueOf(onSymb), to);
	}
	
	/**
	 * Find a state that's part of this machine whose name matches the label
	 *
	 * @param label the label to match against the states name
	 * @return the NFAState that was found, null if no state was found
	 */
	private NFAState findState(String label) {
		if (this.startState.getName().equals(label))
			return this.startState;
		for (NFAState state : this.finalStates) {
			if (state.getName().equals(label))
				return state;
		}
		for (NFAState state : this.states) {
			if (state.getName().equals(label))
				return state;
		}
		return null;
	}
	
	/**
	 * Get the start state of this machine
	 *
	 * @return the start state of this machine
	 */
	public NFAState getStartState() {
		return this.startState;
	}
	
	/**
	 * Get the non-final and non-start states of this machine
	 *
	 * @return set of NFAState objects
	 */
	public Set<NFAState> getStates() {
		return this.states;
	}
	
	/**
	 * Get the final states of this machine
	 *
	 * @return set of NFAState objects which are final states
	 */
	public Set<NFAState> getFinalStates() {
		return this.finalStates;
	}
	
	/**
	 * Get the alphabet of this machine
	 *
	 * @return set of Character of objects
	 */
	public Set<Character> getABC() {
		return this.alphabet;
	}

	/**
	 * Get the DFA representation of this NFA
	 *
	 * @return a DFA representation of this NFA
	 */
	public DFA getDFA() {
		DFA dfa = new DFA();
		Set<NFAState> states = new LinkedHashSet<NFAState>();
		states.add(this.startState);
		states.addAll(this.eClosure(this.startState));
		String startStateName = this.buildStateName(states);
		dfa.addStartState(startStateName);
		this.addStatesToDFA(states, dfa, startStateName);
		return dfa;
	}
	
	/**
	 * Convert states of the NFA to appropriate states for the DFA and add them to the DFA
	 *
	 * @param states
	 * @param dfa the dfa to modify
	 */
	private void addStatesToDFA(Set<NFAState> states, DFA dfa, String currentStateName) {
		for (Character c : this.alphabet) {
			Set<NFAState> allTransitions = new LinkedHashSet<NFAState>();
			for (NFAState state : states) {
				Set<NFAState> transitions = state.getTransitions(c.toString());
				for (NFAState transition : transitions)
					allTransitions.addAll(this.eClosure(transition));
				allTransitions.addAll(transitions);
			}
			String stateName = buildStateName(allTransitions);
			if (this.stateAlreadyExists(stateName, dfa)) {
				dfa.addTransition(currentStateName, c, stateName);
				continue;
			}
			boolean isFinalState = findFinalState(allTransitions);
			if (isFinalState)
				dfa.addFinalState(stateName);
			else
				dfa.addState(stateName);
			dfa.addTransition(currentStateName, c, stateName);
			this.addStatesToDFA(allTransitions, dfa, stateName);
		}
	}
	
	/**
	 * Build the appropriate state name for the DFA from the given states
	 *
	 * @param states the set of states to build the name from
	 * @return a string representation of the state name for the DFA
	 */
	private String buildStateName(Set<NFAState> states) {
		if (states.size() == 0)
			return "[]";
		NFAState[] statesArray = states.toArray(new NFAState[0]);
		StringBuilder builder = new StringBuilder("[");
		if (states.size() > 1) {
			for (int i = 0; i < states.size() - 1; ++i) {
				builder.append(statesArray[i].getName() + ", ");
			}
			builder.append(statesArray[states.size() - 1] + "");
		} else {
			builder.append(statesArray[0].getName() + "");
		}
		builder.append("]");
		return builder.toString();
	}
	
	/**
	 * Determine if the given state already exists in the DFA
	 */
	private boolean stateAlreadyExists(String stateName, DFA dfa) {
		for (DFAState s : dfa.getStates()) {
			if (s.getName().equals(stateName))
				return true;
		}
		return false;
	}
	
	/**
	 * Determine if there is a final state in the given states
	 */
	private boolean findFinalState(Set<NFAState> states) {
		for (NFAState s : this.finalStates) {
			for (NFAState state : states)
				if (s.equals(state))
					return true;
		}
		return false;
	}

	public Set<NFAState> getToState(NFAState from, char onSymb) {
		return new LinkedHashSet(from.getTransitions(String.valueOf(onSymb)));
	}

	public Set<NFAState> eClosure(NFAState s) {
		if (s.getTransitions("e") == null)
			return new LinkedHashSet<NFAState>();
		return s.getTransitions("e");
	}
	
	public String toString() {
		return "";
	}
}