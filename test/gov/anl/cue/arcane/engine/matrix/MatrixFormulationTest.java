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
 * @version 1.1
 * 
*/
package gov.anl.cue.arcane.engine.matrix;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.anl.cue.arcane.engine.matrix.MatrixEngine;
import gov.anl.cue.arcane.engine.matrix.MatrixFormulationAbstract;
import gov.anl.cue.arcane.engine.matrix.MatrixFormulationConcrete;
import gov.anl.cue.arcane.engine.matrix.MatrixModel;

/**
 * The MatrixFormulationTest class is used to test the MatrixFormulation class.
 */
public class MatrixFormulationTest {

	/**
	 * Instantiates a new matrix formulation test.
	 */
	public MatrixFormulationTest() {
	}
	
	/** The matrix engine. */
	MatrixEngine matrixEngine = null;
	
	/** The matrix model. */
	MatrixModel matrixModel = null;

	/** The matrix formulation. */
	MatrixFormulationAbstract matrixFormulation = null;
	
	/**
	 * Before.
	 */
	@Before
	public void before() {
		
		// Create a parent matrix engine.
		this.matrixEngine = new MatrixEngine();
		
		// Create a parent matrix model.
		this.matrixModel = new MatrixModel(matrixEngine);
		
		// Create a matrix formulation to test.
		this.matrixFormulation =
				new MatrixFormulationConcrete(matrixModel.stepSize);
		
	}
	
	
	/**
	 * Test step one.
	 */
	@Test
	public void testStepOne() {
		
		// Step.
		this.matrixFormulation.step();
		
		// Check the results.
		//System.out.println(this.matrixFormulation.calculateFitnessValue());
		Assert.assertEquals(
				this.matrixFormulation.calculateFitnessValue(),
				(Double) 202.54340472435905);
		
	}
		
	/**
	 * Test step101.
	 */
	@Test
	public void testStep101() {
		
		// Step.
		matrixFormulation.step(101);
		
		// Check the results.
		//System.out.println(this.matrixFormulation.calculateFitnessValue());
		Assert.assertEquals(
				this.matrixFormulation.calculateFitnessValue(),
				(Double) 3.7569217731605253E25);

	}

	/**
	 * Test arccos.
	 */
	@Test
	public void testARCCOS() {
		
		// Test the function.
		Assert.assertEquals((Double) this.matrixFormulation.ARCCOS(0.5),
				(Double) 1.0471975511965979);
		
	}
	
	/**
	 * Test arcsin.
	 */
	@Test
	public void testARCSIN() {
		
		// Test the function.
		Assert.assertEquals((Double) this.matrixFormulation.ARCSIN(0.5),
				(Double) 0.5235987755982989);
		
	}
	
	/**
	 * Test arctan.
	 */
	@Test
	public void testARCTAN() {
		
		// Test the function.
		Assert.assertEquals((Double) this.matrixFormulation.ARCTAN(0.5),
				(Double) 0.4636476090008061);
		
	}
	
	/**
	 * Test cos.
	 */
	@Test
	public void testCOS() {
		
		// Test the function.
		Assert.assertEquals((Double) this.matrixFormulation.COS(0.5),
				(Double) 0.8775825618903728);
		
	}
	
	/**
	 * Test exp.
	 */
	@Test
	public void testEXP() {
		
		// Test the function.
		Assert.assertEquals((Double) this.matrixFormulation.EXP(0.5),
				(Double) 1.6487212707001282);
		
	}
	
	/**
	 * Test min.
	 */
	@Test
	public void testMIN() {
		
		// Test the function.
		Assert.assertEquals((Double) this.matrixFormulation.MIN(0.5, 1.0), (Double) 0.5);
		
	}
	
	/**
	 * Test max.
	 */
	@Test
	public void testMAX() {
		
		// Test the function.
		Assert.assertEquals((Double) this.matrixFormulation.MAX(0.5, 1.0), (Double) 1.0);
		
	}
	
	/**
	 * Test modulo.
	 */
	@Test
	public void testMODULO() {
		
		// Test the function.
		Assert.assertEquals((Double) this.matrixFormulation.MODULO(5, 3), (Double) 2.0);
		
	}
	
	/**
	 * Test sin.
	 */
	@Test
	public void testSIN() {
		
		// Test the function.
		Assert.assertEquals((Double) this.matrixFormulation.SIN(0.5),
				(Double) 0.479425538604203);
		
	}
	
	/**
	 * Test tan.
	 */
	@Test
	public void testTAN() {
		
		// Test the function.
		Assert.assertEquals((Double) this.matrixFormulation.TAN(0.5),
				(Double) 0.5463024898437905);
		
	}

	/**
	 * Test pow.
	 */
	@Test
	public void testPOW() {
		
		// Test the function.
		Assert.assertEquals((Double) this.matrixFormulation.POW(3.1, 2.2),
				(Double) 12.050240825798763);
		
	}

	/**
	 * Test linear step.
	 */
	@Test
	public void testLINEARSTEP() {
		
		// Test the function.
		Assert.assertEquals((Double) this.matrixFormulation
				.LINEARSTEP(1.5,
				new double[][] {{0.0, 0.0}, {1.0, 1.0}, {2.0, 2.0}}),
				(Double) 1.5);
		
	}

	/**
	 * Test square step.
	 */
	@Test
	public void testSQUARESTEP() {
		
		// Test the function.
		Assert.assertEquals((Double) this.matrixFormulation
				.SQUARESTEP(1.5,
				new double[][] {{0.0, 0.0}, {1.0, 1.0}, {2.0, 2.0}}),
				(Double) 1.0);
		
	}

	/**
	 * Test square step.
	 */
	@Test
	public void testZeroFill() {
		
		// Test the function.
		Assert.assertEquals((Double) matrixFormulation.zeroFill(1.0),
				(Double) 1.0);
		
		// Test the function again.
		Assert.assertEquals((Double) matrixFormulation.zeroFill(0.0),
				(Double) Double.MIN_VALUE);

	}

}
