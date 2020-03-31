import java.util.*;

/**
 * <h1>Task 6: First and Follow</h1>
 * <p>
 * This is an implementation required for the course CSEN 1002 (Advanced
 * Computer Lab) Connected to the course CSEN 1003 (Compiler) in the 10th
 * semester of computer science MET-GUC.
 * </p>
 * 
 * @author Hamahmi
 * @version 1.0
 * @since 2020-03-30
 */

public class task6 {

///////////////////////////////////////////////////////////////
	/**
	 * This is the main method which makes use of {@link CFG#First()} and
	 * {@link CFG#Follow()} methods.
	 * 
	 * @param args Unused.
	 * @return Nothing.
	 * @see CFG
	 */
	public static void main(String[] args) {
		String input = "S,ScT,T;T,aSb,iaLb,e;L,SdL,S";
		CFG cfg = new CFG(input);
		String firstEncoding = cfg.First();
		String followEncoding = cfg.Follow();
		System.out.println("First: " + firstEncoding);
		System.out.println("Follow: " + followEncoding);
	}
///////////////////////////////////////////////////////////////

}

/**
 * This is a helper class that creates CFG objects and computed First and Follow
 * sets of the created CFG object.
 * 
 * @author Hamahmi
 * @version 1.0
 * @since 2020-03-30
 * @see CFG#CFG(String)
 */
class CFG {

	/**
	 * V is an ArrayList of all variables in a CFG represented as characters
	 * 
	 * @see ArrayList
	 */
	private ArrayList<Character> V;
	/**
	 * V is an ArrayList of all terminals in a CFG (Σ) represented as characters
	 * 
	 * @see ArrayList
	 */
	private ArrayList<Character> terminals;
	/**
	 * R is an ArrayList of all rules in a CFG represented as an ArrayList of
	 * strings where : the first element is the variables in the LHS and the seconds
	 * element is what that variables goes to in the RHS of the rule.
	 * 
	 * @see ArrayList
	 */
	private ArrayList<ArrayList<String>> R;
	/**
	 * first is a HashMap having all First of all variables and terminals in the CFG
	 * where the variable/terminal is represented as a character and it is used as a
	 * key to an ArrayList of characters representing the elements of the First set
	 * of that variable/terminal.
	 * 
	 * @see HashMap
	 * @see ArrayList
	 */
	private HashMap<Character, ArrayList<Character>> first;
	/**
	 * follow is a HashMap having all Follow of all variables in the CFG where the
	 * variable is represented as a character and it is used as a key to an
	 * ArrayList of characters representing the elements of the Follow set of that
	 * variable.
	 * 
	 * @see HashMap
	 * @see ArrayList
	 */
	private HashMap<Character, ArrayList<Character>> follow;

	/**
	 * This is the constructor for CFG class, it maps a string representation of a
	 * CFG into a CFG object, and calls both ComputeFirst and ComputeFollow.
	 * 
	 * @param input This is a string encoding representation of a CFG where : A
	 *              string encoding a CFG is a semi-colon-separated sequence of
	 *              items. Each item represents a largest set of rules with the same
	 *              left-hand side and is a comma-separated sequence of strings. The
	 *              first string of each item is a member of V, representing the
	 *              common left-hand side. The first string of the first item is S.
	 * @see CFG#ComputeFirst()
	 * @see CFG#ComputeFollow()
	 */
	public CFG(String input) {
		V = new ArrayList<Character>();
		terminals = new ArrayList<Character>();
		R = new ArrayList<ArrayList<String>>();
		String[] splitted = input.split(";");
		for (int i = 0; i < splitted.length; i++) {
			String[] rule = splitted[i].split(",");
			V.add(rule[0].charAt(0));
			for (int j = 1; j < rule.length; j++) {
				String sF = rule[j];
				for (int k = 0; k < sF.length(); k++) {
					char chr = sF.charAt(k);
					if (Character.isLowerCase(chr) && chr != 'e' && !terminals.contains(chr)) {
						terminals.add(chr);
					}
				}
				ArrayList<String> newRule = new ArrayList<String>();
				newRule.add(rule[0]);
				newRule.add(sF);
				R.add(newRule);
			}

		}
		Collections.sort(terminals);
		ComputeFirst();
		ComputeFollow();
	}

	/**
	 * This method is used to compute First for all sentential forms of a CFG. and
	 * save them into the global variable HashMap first.
	 * 
	 * @param Nothing.
	 * @return Nothing.
	 * @see CFG#first
	 */
	private void ComputeFirst() {
		first = new HashMap<Character, ArrayList<Character>>();
		// 1: for all a ∈ Σ do
		for (Character a : terminals) {
			ArrayList<Character> chrFirst = new ArrayList<Character>();
			chrFirst.add(a);
			// 2: First(a) = {a}
			first.put(a, chrFirst);
		}
		// 3: for all A ∈ V do
		for (Character A : V) {
			// 4: First(A) = {}
			first.put(A, new ArrayList<Character>());
		}
		// 5: change = TRUE
		boolean change = true;
		// 6: while (change) do
		while (change) {
			// 7: change = FALSE
			change = false;
			// 8: for all (A −→ B1 · · · Bk) ∈ R do
			for (ArrayList<String> rule : R) {
				Character A = rule.get(0).charAt(0);
				// 9: if ε ∈ First(B1) ∩ · · · ∩ First(Bk) then {//This also covers the case
				// when k = 0}
				boolean epsilon = true;
				String B = rule.get(1);
				int k = B.length();
				for (int i = 0; i < k; i++) {
					char chr = B.charAt(i);
					if (chr == 'e') {
						break;
					}
					if (!first.get(chr).contains('e')) {
						epsilon = false;
						break;
					}
				}
				if (epsilon) {
					// 10: if ε !∈ First(A) then
					if (!first.get(A).contains('e')) {
						// 11: First(A) = First(A) ∪ {ε}
						first.get(A).add('e');
						// 12: change = TRUE
						change = true;
					}
				}
				// 14: for i = 1 to k do
				for (int i = 0; i < k; i++) {
					// 15: if (i == 1) or (ε ∈ First(B1) ∩ · · · ∩ First(Bi−1)) then
					epsilon = true;
					for (int j = 0; j < i; j++) {
						char chr = B.charAt(j);
						if (chr == 'e') {
							break;
						}
						if (!first.get(chr).contains('e')) {
							epsilon = false;
							break;
						}
					}
					if ((i == 0) || (epsilon)) {
						// 16: if (First(Bi) − {ε}) !⊆ First(A) then
						boolean subset = true;
						char chrBi = B.charAt(i);
						if (chrBi == 'e') {
							continue;
						}
						ArrayList<Character> firstBi = first.get(chrBi);
						for (int j = 0; j < firstBi.size(); j++) {
							if (firstBi.get(j) == 'e') {
								continue;
							}
							if (!first.get(A).contains(firstBi.get(j))) {
								subset = false;
								break;
							}
						}
						if (!subset) {
							// 17: First(A) = First(A) ∪ (First(Bi) − {ε})
							for (Character term : firstBi) {
								if (term != 'e' && !first.get(A).contains(term)) {
									first.get(A).add(term);
									// 18: change = TRUE
									change = true;
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * This method is used to compute Follow for all variables of a CFG. and save
	 * them into the global variable HashMap follow.
	 * 
	 * @param Nothing.
	 * @return Nothing.
	 * @see CFG#follow
	 */
	private void ComputeFollow() {
		if (first == null)
			ComputeFirst();
		follow = new HashMap<Character, ArrayList<Character>>();
		// 1: for all A ∈ V do
		for (Character A : V) {
			// 2: Follow(A) = {}
			follow.put(A, new ArrayList<Character>());
		}
		// 3: Follow(S) = {$}
		follow.get('S').add('$');
		// 4: change = TRUE
		boolean change = true;
		// 5: while (change) do
		while (change) {
			// 6: change = FALSE
			change = false;
			// 7: for all (A −→ α Bβ) ∈ R do
			for (ArrayList<String> rule : R) {
				Character A = rule.get(0).charAt(0);
				String aBb = rule.get(1);
				for (int i = 0; i < aBb.length(); i++) {
					char B = aBb.charAt(i);
					if (Character.isUpperCase(B)) {
						String beta = aBb.substring(i + 1);
						// Unmentioned case in the algorithm taken from the book
						// 3. If there is a production A -> aB, or a production A -> aBb , where
						// FIRST(b) contains e, then everything in FOLLOW(A) is in FOLLOW(B).
						// meaning same as line 11 to 14 in the algorithm
						if (beta.length() == 0) {
							boolean subset = true;
							ArrayList<Character> followA = follow.get(A);
							for (int j = 0; j < followA.size(); j++) {
								if (!follow.get(B).contains(followA.get(j))) {
									subset = false;
									break;
								}
							}
							if (!subset) {
								// 13: Follow(B) = Follow(B) ∪ Follow(A)
								for (Character term : followA) {
									if (!follow.get(B).contains(term)) {
										follow.get(B).add(term);
										// 14: change = TRUE
										change = true;
									}
								}
							}
							continue;
						}
						// 8: if (First(β) − {ε}) !⊆ Follow(B) then
						boolean subset = true;
						ArrayList<Character> firstBeta = new ArrayList<Character>();
						boolean next = true;
						for (int j = 0; j < beta.length() && next; j++) {
							firstBeta.addAll(first.get(beta.charAt(j)));
							if (!first.get(beta.charAt(j)).contains('e')) {
								next = false;
							}
						}
						for (int j = 0; j < firstBeta.size(); j++) {
							if (firstBeta.get(j) == 'e') {
								continue;
							}
							if (!follow.get(B).contains(firstBeta.get(j))) {
								subset = false;
								break;
							}
						}
						if (!subset) {
							// 9: Follow(B) = Follow(B) ∪ (First(β) − {ε})
							for (Character term : firstBeta) {
								if (term != 'e' && !follow.get(B).contains(term)) {
									follow.get(B).add(term);
									// 10: change = TRUE
									change = true;
								}
							}
						}
						// 11: if ε ∈ First(β) then
						if (firstBeta.contains('e')) {
							// 12: if Follow(A) !⊆ Follow(B) then
							subset = true;
							ArrayList<Character> followA = follow.get(A);
							for (int j = 0; j < followA.size(); j++) {
								if (!follow.get(B).contains(followA.get(j))) {
									subset = false;
									break;
								}
							}
							if (!subset) {
								// 13: Follow(B) = Follow(B) ∪ Follow(A)
								for (Character term : followA) {
									if (!follow.get(B).contains(term)) {
										follow.get(B).add(term);
										// 14: change = TRUE
										change = true;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * This method is used to convert First of all variables in the CFG into a
	 * string encoding.
	 * 
	 * @param Nothing.
	 * @return String This returns a string encoding of the First of all variables
	 *         in the following form : a semi-colon-separated sequence of items,
	 *         where each item is a comma-separated pair. The first element of each
	 *         pair is a variable of the grammar and the second element is a string
	 *         representing the First set of that variable. The symbols in these
	 *         strings appear in alphabetical order. ($ always appears last.)
	 * @see CFG#ComputeFirst()
	 */
	public String First() {
		String output = "";
		for (Character var : V) {
			output += var + ",";
			ArrayList<Character> firstV = first.get(var);
			Collections.sort(firstV);
			for (Character term : firstV) {
				output += term;
			}
			output += ";";
		}
		return output.substring(0, output.length() - 1);
	}

	/**
	 * This method is used to convert Follow of all variables in the CFG into a
	 * string encoding.
	 * 
	 * @param Nothing.
	 * @return String This returns a string encoding of the Follow of all variables
	 *         in the following form : a semi-colon-separated sequence of items,
	 *         where each item is a comma-separated pair. The first element of each
	 *         pair is a variable of the grammar and the second element is a string
	 *         representing the Follow set of that variable. The symbols in these
	 *         strings appear in alphabetical order. ($ always appears last.)
	 * @see CFG#ComputeFollow()
	 */
	public String Follow() {
		String output = "";
		for (Character var : V) {
			output += var + ",";
			ArrayList<Character> followV = follow.get(var);
			Collections.sort(followV);
			boolean do$ar = false;
			for (Character term : followV) {
				if (term == '$') {
					do$ar = true;
				} else {
					output += term;
				}
			}
			if (do$ar) {
				output += '$';
			}
			output += ";";
		}
		return output.substring(0, output.length() - 1);
	}

}
