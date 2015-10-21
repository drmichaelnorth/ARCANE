/*
 * Copyright © 2015, UChicago Argonne, LLC
 * All Rights Reserved
 * ARCANE (ANL-SF-15-108)
 * Michael J. North, Argonne National Laboratory
 * Pam Sydelko, Argonne National Laboratory
 * Ignacio Martinez-Moyano
 * 
 * OPEN SOURCE LICENSE
 * 
 * Under the terms of Contract No. DE-AC02-06CH11357 with UChicago
 * Argonne, LLC, the U.S. Government retains certain rights in this
 * software.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1.	Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer. 
 * 2.	Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 * 3.	Neither the names of UChicago Argonne, LLC or the Department of Energy
 *      nor the names of its contributors may be used to endorse or promote
 *      products derived from this software without specific prior written
 *      permission. 
 *  
 * ****************************************************************************
 * DISCLAIMER
 * 
 * THE SOFTWARE IS SUPPLIED “AS IS” WITHOUT WARRANTY OF ANY KIND.
 * 
 * NEITHER THE UNTED STATES GOVERNMENT, NOR THE UNITED STATES DEPARTMENT OF
 * ENERGY, NOR UCHICAGO ARGONNE, LLC, NOR ANY OF THEIR EMPLOYEES, MAKES ANY
 * WARRANTY, EXPRESS OR IMPLIED, OR ASSUMES ANY LEGAL LIABILITY OR
 * RESPONSIBILITY FOR THE ACCURACY, COMPLETENESS, OR USEFULNESS OF ANY
 * INFORMATION, DATA, APPARATUS, PRODUCT, OR PROCESS DISCLOSED, OR REPRESENTS
 * THAT ITS USE WOULD NOT INFRINGE PRIVATELY OWNED RIGHTS.
 * 
 ******************************************************************************
 *
 * @author Michael J. North
 * @version 1.0
 * 
*/
package gov.anl.cue.sdga.engine.matrix;

import java.util.ArrayList;

import org.jscience.physics.amount.Amount;

/**
 * The Class matrixEquation.
 */
class MatrixEquation {
	
	/** The matrix model. */
	public MatrixModel matrixModel = null;
	
	/** The matrix variable. */
	public MatrixVariable matrixVariable = null;

	/** The node index. */
	public int nodeIndex = -1;
	
	/** The formula. */
	public String formula = null;
	
	/** The units. */
	@SuppressWarnings("rawtypes")
	public Amount units = null;
	
	/** The equation type. */
	public EQUATION_TYPE equationType = null;
	
	/** The initial value. */
	public String initialValue = "";
	
	/**
	 * The Enum EQUATION_TYPE.
	 */
	public enum EQUATION_TYPE {
		
		/** The regular. */
		REGULAR,
		
		/** The initial. */
		INITIAL,
		
		/** The integ. */
		INTEG
	}
	
	/**
	 * Instantiates a new matrix equation.
	 *
	 * @param newMatrixModel the new matrix model
	 * @param newMatrixVariable the new matrix variable
	 * @param newNodeIndex the new node index
	 */
	public MatrixEquation(MatrixModel newMatrixModel,
			MatrixVariable newMatrixVariable,
			int newNodeIndex) {
		
		// Set the new matrix model.
		this.matrixModel = newMatrixModel;
		
		// Set the new matrix variable.
		this.matrixVariable = newMatrixVariable;
		
		// Set the new node index.
		this.nodeIndex = newNodeIndex;
		
		// Set the new equation.
		this.setEquation(newMatrixVariable.equations.get(newNodeIndex));
		
		// Set the new units.
		this.setUnits(newMatrixVariable.units);
		
	}

	/**
	 * Pick variable index.
	 *
	 * @return the int
	 */
	public MatrixVariable pickVariableIndex() {
		
		// Define the valid variables tracker.
		ArrayList<MatrixVariable> validMatrixVariables = new ArrayList<MatrixVariable>();
		
		// Scan the matrix variables.
		for (MatrixVariable currentMatrixVariable : this.matrixModel) {

			// Check to see if the next variable is defined for this node.
			int nonNaNCount = this.nonNanCount(currentMatrixVariable);

			// Check the non-NaN count.
			if (nonNaNCount > 0) {
				
				// Add the current matrix variable to the list.
				validMatrixVariables.add(currentMatrixVariable);
				
			}
			
		}
		
		// Check the list.
		if (validMatrixVariables.size() > 0) {
				
			// Chosen index location.
			return validMatrixVariables.get(
					(int) this.matrixModel.getMatrixEngine()
					.getRandomNumberFromTo(0, validMatrixVariables.size() - 1));
			
		} else {
			
			// Note that no variables are available.
			return null;
			
		}

	}
	
	/**
	 * Non nan count.
	 *
	 * @param currentMatrixVariable the current matrix variable
	 * @return the int
	 */
	public int nonNanCount(MatrixVariable currentMatrixVariable) {
		
		// Count the non-NaN elements.
		int count = 0;
		for (double coefficient : currentMatrixVariable.coefficients.getColumn(this.nodeIndex)) {
			
			// Count the next coefficient.
			if (!Double.isNaN(coefficient)) count++;
			
		}
		
		// Return the results.
		return count;

	}

	/**
	 * Grow.
	 */
	@SuppressWarnings("rawtypes")
	public void grow() {

		// Determine how many new terms to use.
		int newTermCount = (int) this.matrixModel.getMatrixEngine()
				.getRandomNumberFromTo(1,
				this.matrixModel.getMatrixEngine().maximumNewTermCount);
		
		// Pick the index of the first variable to include.
		MatrixVariable chosenMatrixVariable = this.pickVariableIndex();
		
		// Insure that there is at least one variable with which to grow.
		if (chosenMatrixVariable == null) return;
		
		// Find the selected variable's name and units.
		String newExpression  = chosenMatrixVariable.name;
		Amount newUnits = chosenMatrixVariable.units;		
		
		// Accumulate new terms to form a new expression.
		for (int newTermIndex = 1; newTermIndex <
				newTermCount; newTermIndex++) {
			
			// Pick the index of the next variable to include.
			chosenMatrixVariable = this.pickVariableIndex();
			
			// Find the selected variable's name and units.
			String nextName  = chosenMatrixVariable.name;
			Amount nextUnits = chosenMatrixVariable.units;		
			
			// Pick the next operator to use.
			double number = this.matrixModel.getMatrixEngine()
					.getRandomNumberFromTo(0.0, 1.0);
			
			// Check for units consistency errors.
			try {
				
				// Consider addition.
				if ((0.0 <= number) && (number <
						this.matrixModel.getMatrixEngine().additionProbability)) {
					
					// Use addition.
					newUnits = newUnits.plus(nextUnits);
					newExpression = "(" + newExpression + " + "  + nextName + ")";
					
				// Consider subtraction.
				} else if ((this.matrixModel.getMatrixEngine().additionProbability <= number) &&
						(number < this.matrixModel.getMatrixEngine().additionProbability +
								this.matrixModel.getMatrixEngine().subtractionProbability)) {
					
					// Use subtraction.
					newUnits = newUnits.minus(nextUnits);
					newExpression = "(" + newExpression + " - "  + nextName + ")";
					
				// Consider multiplication.
				} else if ((this.matrixModel.getMatrixEngine().additionProbability +
						this.matrixModel.getMatrixEngine().subtractionProbability <= number)
						&& (number < this.matrixModel.getMatrixEngine().additionProbability +
								this.matrixModel.getMatrixEngine().subtractionProbability +
								this.matrixModel.getMatrixEngine().multiplicationProbability)) {
					
					// Use multiplication.
					newUnits = newUnits.times(nextUnits);
					newExpression = "(" + newExpression + " * "  + nextName + ")";
						
				// Use division by default.
				} else {

					// Use division.
					newUnits = newUnits.divide(nextUnits);
					newExpression = "(" + newExpression +
						" / this.zeroFill("
						+ nextName + "))";

				}
				
			// Choose a different path.
			} catch (Exception e) {
			}
			
		}
		
		// Check, and possible accept, the new expression.
		this.checkAndAcceptNewExpression(newExpression, newUnits);
		
	}

	/**
	 * Check, and possible accept, new expressions.
	 *
	 * @param newEquation the new equation
	 * @param newUnits the new units
	 */
	@SuppressWarnings("rawtypes")
	public void checkAndAcceptNewExpression(
		String newEquation, Amount newUnits) {
		
		// Check for expressions with the same units.
		if (this.isCompatible(newUnits, this.getUnits())) {
			
			// Randomly choose to add, subtract, or
			// substitute the new expression.
			double number = this.matrixModel.getMatrixEngine()
					.getRandomNumberFromTo(0.0, 1.0);
			
			// Consider adding the new expression to the existing one.
			if ((0.0 <= number) && (number <
					this.matrixModel.getMatrixEngine().accumulateAddProbability)) {
				
				// Add the new expression to the existing one.
				this.formula = "(" + this.formula + " + " + newEquation + ")";
				
			// Consider subtracting the new expression from the existing one.
			} else if ((this.matrixModel.getMatrixEngine().accumulateAddProbability <= number)
					&& (number < this.matrixModel.getMatrixEngine().accumulateAddProbability +
					this.matrixModel.getMatrixEngine().accumulateSubtractProbability)) {
				
				// Subtract the new expression from the existing one.
				this.formula = "(" + this.formula + " - " + newEquation + ")";
				
			// By default, replace the old expression with the new one.
			} else {

				// Replace the old expression with the new one.
				this.formula = "(" + newEquation + ")";

			}
			
		// Check for expressions without units.
		} else if (this.isCompatible(newUnits,  Amount.ONE)) {
			
			// Apply the new expression.
			this.formula = "((" + this.formula + ") * " + newEquation + ")";

		}
		
	}
	
	/**
	 * Checks if the given amounts have compatible units.
	 *
	 * @param a the a
	 * @param b the b
	 * @return true, if is compatible
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean isCompatible(Amount a, Amount b) {
		
		// Check the compatibility of the given units.
		return (a.getUnit().isCompatible(b.getUnit()));
		
	}
	
	/**
	 * Find type.
	 *
	 * @param newEquation the new equation
	 * @return the equation type
	 */
	public EQUATION_TYPE extractType(String newEquation) {
		
		// Check for stocks.
		if (newEquation.contains("INTEG(")) {
			
			// Return the type.
			return EQUATION_TYPE.INTEG;

		// Check for initial values.
		} else if (newEquation.contains("INITIAL(")) {
			
			// Return the type.
			return EQUATION_TYPE.INITIAL;

		// Default to regular equations.
		} else {

			// Return the type.
			return EQUATION_TYPE.REGULAR;
		
		}	

	}
	
	/**
	 * Extract formula.
	 *
	 * @param newEquation the new equation
	 * @return the string
	 */
	public String extractFormula(String newEquation) {
		
		// Define the results holder.
		String results;
		
		// Check for stocks.
		if (newEquation.contains("INTEG(")) {
			
			// Extract the equation.
			String calc = newEquation.substring(0, newEquation.indexOf(",")).trim();
			results = calc.replace("INTEG(", "");

		// Check for initial values.
		} else if (newEquation.contains("INITIAL(")) {
			
			// Extract the equation.
			String calc = newEquation.substring(0, newEquation.indexOf(",")).trim();
			results = calc.replace("INITIAL(", "");
				
		} else {

			// Return the default result.
			results = newEquation;
		
		}
		
		// Insure that there is a formula.
		if (results.trim().equals("")) results = "1.0";
		
		// Return the results.
		return results;

	}

	/**
	 * Extract initial value.
	 *
	 * @param newEquation the new equation
	 * @return the string
	 */
	public String extractInitialValue(String newEquation) {
		
		// Check for stocks.
		if (newEquation.contains("INTEG(")) {
			
			// Extract the initial value.
			String initialValue = newEquation.substring(newEquation.indexOf(",") + 1).trim();
			return initialValue.substring(0, initialValue.length() - 1);

		// Check for initial values.
		} else if (newEquation.contains("INITIAL(")) {
			
			// Extract the initial value.
			String initialValue = newEquation.substring(newEquation.indexOf(",") + 1).trim();
			return initialValue.substring(0, initialValue.length() - 1);

		} else {

			// Return the default result.
			return "";
		
		}	

	}

	/**
	 * Gets the equation.
	 *
	 * @return the equation
	 */
	public String getEquation() {
		
		// Check for stocks.
		if (this.equationType == EQUATION_TYPE.INTEG) {
			
			// Return a stock equation.
			return "INTEG(" + this.formula + ", " + this.initialValue + ")";
			
		// Check for initial values.
		} else if (this.equationType == EQUATION_TYPE.INITIAL) {
			
			// Return an equation with an initial value.
			return "INITIAL(" + this.formula + ", " + this.initialValue + ")";

		} else {
			
			// Return the default results.
			return formula;
			
		}
		
	}

	/**
	 * Sets the equation.
	 *
	 * @param equation the new equation
	 */
	public void setEquation(String equation) {
		
		// Store the new equation type.
		this.equationType = this.extractType(equation);
		
		// Store the new equation
		this.formula = this.extractFormula(equation);
		
		// Store the initial value, if any.
		this.initialValue = this.extractInitialValue(equation);
		
	}

	/**
	 * Gets the units.
	 *
	 * @return the units
	 */
	@SuppressWarnings("rawtypes")
	public Amount getUnits() {

		// Return the results.
		return units;
		
	}

	/**
	 * Sets the units.
	 *
	 * @param newUnits the new units
	 */
	@SuppressWarnings("rawtypes")
	public void setUnits(Amount newUnits) {
		
		// Store the new units value.
		this.units = newUnits;
		
	}
	
}