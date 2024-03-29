import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Stack;

/**
 * <h1>Task 7: LL(1) Parsing</h1>
 * <p>
 * This is an implementation required for the course CSEN 1002 (Advanced
 * Computer Lab) Connected to the course CSEN 1003 (Compiler) in the 10th
 * semester of computer science MET-GUC.
 * </p>
 *
 * T16_37_15881_Abdelrahman_Gharib_ElHamahmi
 *
 * @author Hamahmi
 * @version 2.0
 * @since 2020-03-30
 */

public class task7 {

	/**
	 * This is a helper class that creates CFG objects and computed First and Follow
	 * sets of the created CFG object.
	 *
	 * @author Hamahmi
	 * @version 1.2
	 * @since 2020-03-30
	 * @see CFG#CFG(String)
	 */
	static class CFG {

		/**
		 * grammar is a string encoding of the CFG (No idea why do we need that, but
		 * following the template)
		 * 
		 */
		String grammar;

		/**
		 * V is an ArrayList of all variables in a CFG represented as characters
		 *
		 */
		private ArrayList<Character> V;

		/**
		 * V is an ArrayList of all terminals in a CFG (Σ) represented as characters
		 *
		 */
		private ArrayList<Character> terminals;

		/**
		 * R is an ArrayList of ArrayLists X representing CFG rules. Each X contains 2
		 * strings A and B. A is the LHS and B is the RHS of the rule.
		 *
		 */
		private ArrayList<ArrayList<String>> R;

		/**
		 * first is a HashMap having all First of all variables and terminals in the CFG
		 * where the variable/terminal is represented as a character and it is used as a
		 * key to an ArrayList of characters representing the elements of the First set
		 * of that variable/terminal.
		 *
		 */
		private HashMap<Character, ArrayList<Character>> first;

		/**
		 * follow is a HashMap having all Follow of all variables in the CFG where the
		 * variable is represented as a character and it is used as a key to an
		 * ArrayList of characters representing the elements of the Follow set of that
		 * variable.
		 *
		 */
		private HashMap<Character, ArrayList<Character>> follow;

		/**
		 * M is the predictive parsing table for the CFG
		 */
		private HashMap<Character, HashMap<Character, String>> M;

		/**
		 * Creates an instance of the CFG class. This should parse a string
		 * representation of the grammar and set your internal CFG attributes
		 * 
		 * @param grammar A string representation of a CFG
		 *
		 * @see CFG#ComputeFirst()
		 * @see CFG#ComputeFollow()
		 * @see CFG#ConstructTable()
		 */
		public CFG(String grammar) {
			this.grammar = grammar;
			V = new ArrayList<Character>();
			terminals = new ArrayList<Character>();
			R = new ArrayList<ArrayList<String>>();
			String[] splitted = grammar.split(";");
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
			ConstructTable();
		}

		/**
		 * This method is used to compute First for all sentential forms of a CFG. and
		 * save them into the global variable {@link CFG#first}.
		 *
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
		 * them into the global variable {@link CFG#follow}.
		 *
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
								ArrayList<Character> curCFirst = first.get(beta.charAt(j));
								if (j == beta.length() - 1) {
									// last terminal/Variable
									firstBeta.addAll(curCFirst);
								} else {
									// if not last add current first - {epsilon}
									// by the way, the method to get the sentential First
									// was never mentioned in the lecture nor the tutorial
									for (Character character : curCFirst) {
										if (character != 'e') {
											firstBeta.add(character);
										}
									}
								}
								if (!curCFirst.contains('e')) {
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
		 * @return String This returns a string in the following format:
		 *         "X1,Y1;X2,Y2;...Xn,Yn". X is a variable and Y is the First set of
		 *         this variable. The string is ordered in alphabetical order. $ is the
		 *         last element.
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
		 * @return String This returns a string in the following format:
		 *         "X1,Y1;X2,Y2;...Xn,Yn". X is a variable and Y is the Follow set of
		 *         this variable. The string is ordered in alphabetical order. $ is the
		 *         last element.
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

		/**
		 * This method is used to construct the predictive parsing table of the CFG. and
		 * save it into the global variable {@link CFG#M}.
		 *
		 * @see CFG#M
		 */
		private void ConstructTable() {
			if (follow == null)
				ComputeFollow();
			M = new HashMap<Character, HashMap<Character, String>>();
			for (Character var : V) {
				M.put(var, new HashMap<Character, String>());
			}
			// As taken from the book **Compilers Principles, Techniques, & Tools (Second
			// Edition)**

			// Algorithm 4.31 : Construction of a predictive parsing table.
			// For each production A -> alpha of the grammar, do the following:
			for (ArrayList<String> rule : R) {
				Character A = rule.get(0).charAt(0);
				String alpha = rule.get(1);
				// 1. For each terminal a in FIRST(alpha), add A -> alpha to M[A,a].
				ArrayList<Character> firstAlpha = new ArrayList<Character>();
				boolean next = true;
				for (int j = 0; j < alpha.length() && next; j++) {
					ArrayList<Character> curCFirst = first.get(alpha.charAt(j));
					if (j == alpha.length() - 1) {
						// last terminal/Variable
						if (curCFirst == null) {
							// case -> e
							firstAlpha.add('e');
							next = false;
							continue;
						} else {
							firstAlpha.addAll(curCFirst);
						}
					} else {
						// if not last add current first - {epsilon}
						// by the way, the method to get the sentential First
						// was never mentioned in the lecture nor the tutorial
						for (Character character : curCFirst) {
							if (character != 'e') {
								firstAlpha.add(character);
							}
						}
					}
					if (!curCFirst.contains('e')) {
						next = false;
					}
				}

				for (Character a : firstAlpha) {
					// If G = is an LL(1) CFG then |M[A, a]| ≤ 1, for every A ∈ V and a ∈ Σ ∪ {$}.
					// (No more than one entry for [A,a]
					if (a != 'e')
						M.get(A).put(a, alpha);
				}
				// 2. If e is in FIRST(alpha), then for each terminal b in FOLLOW(A), add A ->
				// alpha to M[A,b].
				if (firstAlpha.contains('e')) {
					ArrayList<Character> followA = follow.get(A);
					for (Character b : followA) {
						M.get(A).put(b, alpha);
					}
					// If e is in FIRST(alpha) and $ is in FOLLOW(A), add A -> alpha to M[A,$] as
					// well.
					if (followA.contains('$')) {
						M.get(A).put('$', alpha);
					}
				}
			}
		}

		/**
		 * Generates the parsing table for this context free grammar. This should set
		 * your internal parsing table attributes
		 *
		 * @return A string representation of the parsing table
		 */
		public String table() {
			if (M == null)
				ConstructTable();
			String output = "";
			for (Character var : V) {
				for (Character term : terminals) {
					String mVT = M.get(var).get(term);
					if (mVT != null) {
						output += var + "," + term + "," + mVT + ";";
					}
				}
				String mV$ = M.get(var).get('$');
				if (mV$ != null) {
					output += var + "," + '$' + "," + mV$ + ";";
				}
			}
			return output.substring(0, output.length() - 1);
		}

		/**
		 * Parses the input string using the parsing table
		 *
		 * @param s The string to parse using the parsing table
		 * @return A string representation of a left most derivation
		 */
		public String parse(String w) {
			// As taken from the book **Compilers Principles, Techniques, & Tools (Second
			// Edition)**

			// Algorithm 4.34 : Table-driven predictive parsing
			// Initially, the parser is in a configuration with w$ in the input buffer and
			// the start symbol S of G on top of the stack, above $.
			Stack<Character> stack = new Stack<Character>();
			String output = "S";
			int index = 0;
			stack.push('$');
			stack.push(V.get(index++));
			// let a be the first symbol of w;
			char a = w.charAt(0);
			char X = stack.peek();
			// while ( X != $) { /* stack is not empty */
			while (X != '$') {
				// if ( X = a ) pop the stack and let a be the next symbol of w;
				if (X == a) {
					stack.pop();
					if (index == w.length()) {
						// end of input
						a = '$';
					} else {
						a = w.charAt(index++);
					}
				}
				// else if ( X is a terminal ) error();
				else {
					if (terminals.contains(X)) {
						output += ",ERROR";
						break;
					}
					// else if ( M[X,a] is an error entry ) error();
					else {
						String Y = M.get(X).get(a);
						if (Y == null) {
							output += ",ERROR";
							break;
						}
						// else if ( M[X,a] = X -> Y1Y2 ... Yk )
						else {
							// output the production X -> Y1Y2 ... Yk ;
							String lastOutput = output.split(",")[output.split(",").length - 1];
							if (Y.charAt(0) == 'e') {
								output += "," + lastOutput.replaceFirst((X + ""), "");
							} else {
								output += "," + lastOutput.replaceFirst((X + ""), Y);
							}
							// pop the stack;
							stack.pop();
							// push Yk ; Yk-1; ... ;Y1 onto the stack, with Y1 on top
							if (!(Y.charAt(0) == 'e')) {
								for (int i = Y.length() - 1; i >= 0; i--) {
									stack.push(Y.charAt(i));
								}
							}
						}
					}

				}
				// let X be the top stack symbol
				X = stack.peek();
			}
			return output;
		}

	}

	///////////////////////////////////////////////////////////////
	/**
	 * This is the main method which makes use of {@link CFG#table()} and
	 * {@link CFG#parse(String)} methods.
	 *
	 * @param args Unused.
	 * @see CFG
	 */
	public static void main(String[] args) {

		/*
		 * Please make sure that this EXACT code works. This means that the method and
		 * class names are case sensitive
		 */

		String grammar = "S,iST,e;T,cS,a";
		String input1 = "iiac";
		String input2 = "iia";
		CFG g = new CFG(grammar);
		System.out.println(g.table());
		System.out.println(g.parse(input1));
		System.out.println(g.parse(input2));
	}
	///////////////////////////////////////////////////////////////

}
