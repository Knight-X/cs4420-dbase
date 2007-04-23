package queries;

import java.util.ArrayList;

public class AndOrCondition extends Condition {

	/**This method will parse out the two conditions inside of an AND or an 
	 * OR statement.
	 * @param statement The statement to parse.
	 * @return An array containing the inner statements
	 */
	public static String [] parseConditons(final String statement) {
		
		String [] conditions = new String [2];
		ArrayList < String > parts = new ArrayList < String > ();
		
		int opens = 0;
		int closes = 0;
		
		//Start at position 1 because we know that 0 is a "("
		for (int start = 1; start < statement.length(); start++) {
			
			//See if it is an open parentheses
			if (statement.charAt(start) == '(') {
				
				opens++;
				
				//Find the close of the condition
				for (int close = start + 1; 
					close < statement.length(); close++ ) {
					
					//See if it is an open parentheses
					if (statement.charAt(close) == '(') {
						opens++;
					} else if (statement.charAt(close) == ')') {
						closes++;
					}
					//See if it is closed now 
					if (closes == opens) {
						parts.add(statement.substring(start, close + 1));
						//System.out.println(statement.substring(start, close + 1));
						//Now go on from here
						start = close;
						break;
					}
				} //End loop looking of )
			} //End if for (
		} //End loop for looking for (
		
		conditions[0] = parts.get(0);
		conditions[1] = parts.get(1);
		return conditions;
	}
	
	public static String parseLeftHand(final String statement) {
		return parseConditons(statement)[0];
	}
	
	/**This method finds the right hand side of the AND/OR statement passed 
	 * to it.
	 * @param statement The AND/OR statement.
	 * @return The right hand of the statement.
	 */
	public static String parseRightHand(final String statement) {
		return parseConditons(statement)[1];
	}
	
	private Condition leftHand;
	
	private Condition rightHand;
	
	/**This will create a new instance of AndOrCondition.
	 * @param newCondition The condition that the object will represent and
	 * parse sub conditions from.
	 * @param relationID The ID of the relation that this condition will be 
	 * working on, for schema purposes.
	 */
	public AndOrCondition(final String newCondition, final int relationID) {
		super(newCondition);
		
		//Set the relation IDs
		setRelation(relationID);
		
		//Get the conditions contained within this one.
		leftHand = Condition.makeCondition(
			parseLeftHand(newCondition), relationID);
		rightHand = Condition.makeCondition(
			parseRightHand(newCondition), relationID);
	}
	
	/**This method determines whether or not the AndOrCondition is true for the 
	 * tuple passed to it.  The tuple had <b>BETTER</b> be of the same type of
	 * relation as this object thinks it is.
	 * @param tuple The string representation of the tuple.
	 * @return Wether or not this Condition is true for the tupe.
	 */
	public boolean compare(final String tuple) {
	
		boolean leftHandEval = leftHand.compare(tuple);
		boolean rightHandEval = rightHand.compare(tuple);
		
		//If an AND then see if both the left and right are true
		if (comparison.equalsIgnoreCase("AND")) {
			return (leftHandEval && rightHandEval);
		} else if (comparison.equalsIgnoreCase("OR")) {
			return (leftHandEval || rightHandEval);
		} else {
			System.out.println("AndOrCondition.compare: Not an AND or an OR");
			return false;
		}
	}

	@Override
	public ArrayList < Integer > getAttributes() {

		//Get the leftHand ones
		ArrayList < Integer > leftHandAttributes = leftHand.getAttributes();
		ArrayList < Integer > rightHandAttributes = rightHand.getAttributes();
		ArrayList < Integer > noDuplicates = 
			(ArrayList) leftHandAttributes.clone();
		
		//See if there are any on the right side not in the left side
		for (int right = 0; right < rightHandAttributes.size(); right++) {
			
			int rightAttribute = rightHandAttributes.get(right);
			boolean duplicate = false;
			
			for (int left = 0; left < leftHandAttributes.size(); left++) {
				
				int leftAttribute = leftHandAttributes.get(left);
				if (leftAttribute == rightAttribute) {
					duplicate = true;
					break;
				}	
			}
			
			if (!duplicate) {
				noDuplicates.add(rightAttribute);
			}
		}
		return noDuplicates;
	}
}