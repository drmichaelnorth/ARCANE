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
 * The MatrixFormulationConcrete class provides the
 * specific methods for evaluating a system dynamics model.
 * MatrixFormulationConcrete instances are written and compiled
 * dynamically at engine run time. This instance is an example
 * that is used to demonstrate and test the functions of this 
 * automatically generated class.
 */
public class MatrixFormulationConcrete extends MatrixFormulationAbstract
	implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -3272711829386440220L;

	/** The a_ converter1. */
	double a_Converter1 = 2.0;
	
	/** The a_ converter1_combined. */
	double a_Converter1_combined = 2.0;
	
	/** The a_ converter2. */
	double a_Converter2 = 12.4;
	
	/** The a_ converter2_combined. */
	double a_Converter2_combined = 12.4;
	
	/** The a_ sink1. */
	double a_Sink1 = 0.0;
	
	/** The a_ sink1_combined. */
	double a_Sink1_combined = 0.0;
	
	/** The a_ source1. */
	double a_Source1 = 0.0;
	
	/** The a_ source1_combined. */
	double a_Source1_combined = 0.0;
	
	/** The a_ source2. */
	double a_Source2 = 0.0;
	
	/** The a_ source2_combined. */
	double a_Source2_combined = 0.0;
	
	/** The b_ converter1. */
	double b_Converter1 = 0.0;
	
	/** The b_ converter1_combined. */
	double b_Converter1_combined = 0.0;
	
	/** The b_ converter2. */
	double b_Converter2 = 29.3;
	
	/** The b_ converter2_combined. */
	double b_Converter2_combined = 29.3;
	
	/** The b_ sink1. */
	double b_Sink1 = 0.0;
	
	/** The b_ sink1_combined. */
	double b_Sink1_combined = 0.0;
	
	/** The b_ source1. */
	double b_Source1 = 0.0;
	
	/** The b_ source1_combined. */
	double b_Source1_combined = 0.0;
	
	/** The b_ source2. */
	double b_Source2 = 0.0;
	
	/** The b_ source2_combined. */
	double b_Source2_combined = 0.0;
	
	/** The c_ converter1. */
	double c_Converter1 = 0.0;
	
	/** The c_ converter1_combined. */
	double c_Converter1_combined = 0.0;
	
	/** The c_ converter2. */
	double c_Converter2 = 0.0;
	
	/** The c_ converter2_combined. */
	double c_Converter2_combined = 0.0;
	
	/** The c_ sink1. */
	double c_Sink1 = 0.0;
	
	/** The c_ sink1_combined. */
	double c_Sink1_combined = 0.0;
	
	/** The c_ source1. */
	double c_Source1 = 39.8;
	
	/** The c_ source1_combined. */
	double c_Source1_combined = 39.8;
	
	/** The c_ source2. */
	double c_Source2 = 0.0;
	
	/** The c_ source2_combined. */
	double c_Source2_combined = 0.0;

	/**
	 * Instantiates a new matrix formulation concrete.
	 *
	 * @param newStepSize the new step size
	 */
	public MatrixFormulationConcrete(double newStepSize) {
		super(newStepSize);
	}

	/* (non-Javadoc)
	 * @see gov.anl.cue.arcane.engine.matrix.MatrixFormulationAbstract#knit()
	 */
	@Override
	public void knit() {

		a_Converter1_combined = a_Converter1 + b_Converter1;
		a_Converter2_combined  += ((a_Converter2 + b_Converter2) * this.stepSize);
		a_Sink1_combined = a_Sink1 + b_Sink1 + c_Sink1;
		a_Source1_combined = 10.4;
		a_Source2_combined = 11.6;
		b_Converter1_combined = 9.3;
		b_Converter2_combined  += ((a_Converter2 + b_Converter2) * this.stepSize);
		b_Sink1_combined = a_Sink1 + b_Sink1 + c_Sink1;
		b_Source1_combined = c_Source1;
		b_Source2_combined = SIN(b_Source2);
		c_Converter1_combined = a_Converter1 + b_Converter1;
		c_Converter2_combined = 4.5;
		c_Sink1_combined = a_Sink1 + c_Sink1;
		c_Source1_combined  += ((b_Source1 + c_Source1) * this.stepSize);
		c_Source2_combined = c_Source2;

	}

	/* (non-Javadoc)
	 * @see gov.anl.cue.arcane.engine.matrix.MatrixFormulationAbstract#split()
	 */
	@Override
	public void split() {

		a_Converter1 = 0.5 * a_Converter1_combined + 0.25 * a_Converter2_combined + 1.0 * a_Source1_combined + 1.0 * a_Source2_combined;
		a_Converter2 = 0.5 * a_Converter1_combined + 0.5 * a_Sink1_combined;
		a_Sink1 = 0.25 * a_Converter2_combined;
		a_Source1 = 0.25 * a_Converter2_combined + 0.35 * a_Sink1_combined;
		a_Source2 = 0.25 * a_Converter2_combined + 0.15 * a_Sink1_combined;
		b_Converter1 = 0.5 * b_Converter1_combined + 0.25 * b_Converter2_combined + 1.0 * b_Source1_combined + 0.5 * b_Sink1_combined;
		b_Converter2 = 0.5 * b_Converter1_combined + 0.5 * b_Source2_combined;
		b_Sink1 = 0.25 * b_Converter2_combined;
		b_Source1 = 0.25 * b_Converter2_combined + 0.5 * b_Sink1_combined;
		b_Source2 = 0.25 * b_Converter2_combined + 0.5 * b_Source2_combined;
		c_Converter2 = 0.5 * c_Source1_combined + 0.25 * c_Source2_combined + 0.5 * c_Sink1_combined;
		c_Sink1 = 0.5 * c_Source1_combined + 0.25 * c_Source2_combined + 0.5 * c_Sink1_combined;
		c_Source1 = 0.5 * c_Converter2_combined + 0.25 * c_Source2_combined;
		c_Source2 = 0.5 * c_Converter2_combined + 0.25 * c_Source2_combined;

	}

	/* (non-Javadoc)
	 * @see gov.anl.cue.arcane.engine.matrix.MatrixFormulationAbstract#calculateFitnessValue()
	 */
	public Double calculateFitnessValue() {

		return (a_Converter1 + b_Converter1) + (a_Converter2 + b_Converter2 + c_Converter2) + (SIN(a_Source1)) + (b_Source2) + (c_Sink1);

	}

}
