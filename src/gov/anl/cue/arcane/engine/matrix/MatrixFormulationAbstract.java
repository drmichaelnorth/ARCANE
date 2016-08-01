/*
 * Copyright © 2016, UChicago Argonne, LLC
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
 * @version 1.1.0
 * 
*/
package gov.anl.cue.arcane.engine.matrix;

import java.io.Serializable;

/**
 * The MatrixFormulationAbstract class provides the
 * base methods for evaluating models.
 */
public abstract class MatrixFormulationAbstract implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -2677424125454304383L;

	/**
	 * Instantiates a new matrix formulation.
	 *
	 * @param newStepSize the new step size
	 */
	public MatrixFormulationAbstract(double newStepSize) {
		
		// Note the step size.
		this.stepSize = newStepSize;
		
	}
	
	/**
	 * Knit.
	 */
	public abstract void knit();

	/**
	 * Split.
	 */
	public abstract void split();
	
	/**
	 * Calculate fitness value.
	 *
	 * @return the double
	 */
	public abstract Double calculateFitnessValue();
	
	/** The first step indicator. */
	public boolean firstStep = true;
	
	/** The step size indicator. */
	public double stepSize = 1.0;
	
	/**
	 * Step.
	 */
	public void step() {
	
		// Evaluate the equations.
		this.knit();
		
		// Disperse the results of the equations.
		this.split();
		
		// Note that the first step has been taken.
		this.firstStep = false;

	}
	
	/**
	 * Step.
	 *
	 * @param steps the steps
	 */
	public void step(int steps) {
		
		// Complete the requested number of steps.
		for (int step = 0; step < steps; step++) {
			this.step();
		}

	}
	
	/**
	 * Arccos.
	 *
	 * @param a the a
	 * @return the double
	 */
	public double ARCCOS(double a) {
		
		// Return the results.
		return Math.acos(a);
		
	}
		
	/**
	 * Arcsin.
	 *
	 * @param a the a
	 * @return the double
	 */
	public double ARCSIN(double a) {
		
		// Return the results.
		return Math.asin(a);
		
	}
	
	/**
	 * Arctan.
	 *
	 * @param a the a
	 * @return the double
	 */
	public double ARCTAN(double a) {
		
		// Return the results.
		return Math.atan(a);
		
	}
	
	/**
	 * Cos.
	 *
	 * @param a the a
	 * @return the double
	 */
	public double COS(double a) {
		
		// Return the results.
		return Math.cos(a);
		
	}
	
	/**
	 * Exp.
	 *
	 * @param a the a
	 * @return the double
	 */
	public double EXP(double a) {
		
		// Return the results.
		return Math.exp(a);
		
	}
	
	/**
	 * Min.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the double
	 */
	public double MIN(double a, double b) {
		
		// Return the results.
		return Math.min(a, b);
		
	}
	
	/**
	 * Max.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the double
	 */
	public double MAX(double a, double b) {
		
		// Return the results.
		return Math.max(a, b);
		
	}
	
	/**
	 * Modulo.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the double
	 */
	public double MODULO(double a, double b) {
		
		// Return the results.
		return (a % b);
		
	}
	
	/**
	 * Sin.
	 *
	 * @param a the a
	 * @return the double
	 */
	public double SIN(double a) {
		
		// Return the results.
		return Math.sin(a);
		
	}
	
	/**
	 * Tan.
	 *
	 * @param a the a
	 * @return the double
	 */
	public double TAN(double a) {
		
		// Return the results.
		return Math.tan(a);
		
	}
	
	/**
	 * Pow.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the double
	 */
	public double POW(double a, double b) {
		
		// Return the results.
		return Math.pow(a, b);
		
	}

	/**
	 * The linear step function.
	 *
	 * @param x the x
	 * @param xy the xy
	 * @return the double
	 */
	public double LINEARSTEP(double x, double[][] xy) {
		
		// Note the results holder.
		double y = Double.NaN;
		
		// Scan the steps.
		for (int index = 1; index < xy.length; index++) {
			
			// Check the next step.
			if ((xy[index - 1][0] <= x) && (x < xy[index][0])) {
				
				// Use linear interpolation.
				y = xy[index - 1][1] +
						(xy[index][1] - xy[index - 1][1]) /
						(xy[index][0] - xy[index - 1][0]) *
						(x - xy[index - 1][0]);

			}
			
		}

		// Return the results.
		return y;
		
	}

	/**
	 * The square step function.
	 *
	 * @param x the x
	 * @param xy the xy
	 * @return the double
	 */
	public double SQUARESTEP(double x, double[][] xy) {
		
		// Note the results holder.
		double y = Double.NaN;
		
		// Scan the steps.
		for (int index = 1; index < xy.length; index++) {
			
			// Check the next step.
			if ((xy[index - 1][0] <= x) && (x < xy[index][0])) {
				
				// Use square interpolation.
				y = xy[index - 1][1];

			}
			
		}

		// Return the results.
		return y;
		
	}
	
	/**
	 * Avoid division by zero.
	 *
	 * @param value the value
	 * @return the double
	 */
	public double zeroFill(double value) {
		
		// Check the value to avoid division by zero.
		if (value == 0.0) {
			
			// Return a substitute.
			return Double.MIN_VALUE;
			
		} else {
			
			// Return the original value.
			return value;
			
		}
		
	}

}
