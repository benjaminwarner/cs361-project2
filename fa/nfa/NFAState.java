package fa.nfa;

import java.util.*;

import fa.State;

/**
 * A representation of a state in an NFA.
 *
 * @author Benjamin Warner
 */

public class NFAState extends State {
	
	private Map<String, Set<NFAState>> transitions;

	/**
	 * Create a new NFAState.
	 */
	public NFAState(String name) {
		this.name = name;
		this.transitions = new LinkedHashMap<String, Set<NFAState>>();
	}
	
	/**
	 * Add a transition to the NFA state.
	 *
	 * @param input the symbol received to transition to the state
	 * @param to the state to transition to given the input
	 */
	public void addTransition(String input, NFAState to) {
		if (!this.transitions.containsKey(input))
			this.transitions.put(input, new LinkedHashSet<NFAState>());
		this.transitions.get(input).add(to);
	}
	
	/**
	 * Get the possible transitions for this state.
	 *
	 * @param input the symbol to get transitions for
	 * @return the set of states this state can transition to for the given input
	 */
	public Set<NFAState> getTransitions(String input) {
		if (!this.transitions.containsKey(input))
			return new LinkedHashSet<NFAState>();
		return this.transitions.get(input);
	}

	/**
	 * Determine if two NFA states are equivalent to each other (two NFA states
	 * are equivalent if they have the same names)
	 *
	 * @return true if this state's name is equal to the input object's state name
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		NFAState state = (NFAState) o;
		return this.name.equals(state.getName());
	}
}
