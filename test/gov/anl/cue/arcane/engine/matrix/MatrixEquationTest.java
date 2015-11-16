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
package gov.anl.cue.arcane.engine.matrix;

import gov.anl.cue.arcane.engine.UtilTest;
import gov.anl.cue.arcane.engine.matrix.MatrixEngine;
import gov.anl.cue.arcane.engine.matrix.MatrixEquation;
import gov.anl.cue.arcane.engine.matrix.MatrixEquation.EQUATION_TYPE;

import org.jscience.physics.amount.Amount;
import org.junit.Assert;
import org.junit.Test;

/**
 * The Class MatrixEquationTest.
 */
public class MatrixEquationTest {
	
	/**
	 * Instantiates a new matrix equation test.
	 */
	public MatrixEquationTest() {
	}
	

	/**
	 * Test the grow method.
	 */
	@Test
	public void testGrow() {
		
		// Setup a matrix engine and model.
		MatrixEngine matrixEngine = MatrixEngine
				.importTemplate(UtilTest.INPUT_TEST_DIR +
				"//MatrixEngineTemplate_8.xlsx");

		// Create a sample atom.
		MatrixEquation matrixEquation = new MatrixEquation(
				matrixEngine.outputPopulation.get(0),
				matrixEngine.outputPopulation.get(0).get(0), 0);

		// Try several equation evolution steps.
		for (int equationEvolutionSteps = 0; equationEvolutionSteps < 100; equationEvolutionSteps++) {
		
			// Create a new equation.
			matrixEquation.grow();
			
		}
		
		// Check the results.
		//System.out.println(matrixEquation.getEquation());
		Assert.assertTrue(matrixEquation.getEquation()
			.equals(
			"INITIAL(((((((a)) * (a / this.zeroFill(a))) - a) + " +
			"((a / this.zeroFill(a)) * a)) + (((a + a) * a) / " +
			"this.zeroFill(a))), 10.1)"));

	}
	
	/**
	 * Test the grow method.
	 */
	@Test
	public void testGrowUnusedVariable() {
		
		// Setup a matrix engine and model.
		MatrixEngine matrixEngine = MatrixEngine
				.importTemplate(UtilTest.INPUT_TEST_DIR +
				"//MatrixEngineTemplate_9.xlsx");

		// Create a sample atom.
		MatrixEquation matrixEquation = new MatrixEquation(
				matrixEngine.outputPopulation.get(0),
				matrixEngine.outputPopulation.get(0).get(2), 0);

		// Try several equation evolution steps.
		for (int equationEvolutionStep = 0; equationEvolutionStep < 100; equationEvolutionStep++) {
		
			// Create a new equation.
			matrixEquation.grow();
			
		}
		
		// Check the results.
		//System.out.println(matrixEquation.getEquation());
		Assert.assertTrue(matrixEquation.getEquation()
			.equals("1.0"));

	}

	/**
	 * Test check and accept new expression.
	 */
	@Test
	public void testCheckAndAcceptNewExpression() {

		// Setup a matrix engine and model.
		MatrixEngine matrixEngine = MatrixEngine
				.importTemplate(UtilTest.INPUT_TEST_DIR +
				"//MatrixEngineTemplate_8.xlsx");

		// Create a sample atom.
		MatrixEquation matrixEquation = new MatrixEquation(
				matrixEngine.outputPopulation.get(0),
				matrixEngine.outputPopulation.get(0).get(0), 0);

		// Check expression checking.
		matrixEquation.checkAndAcceptNewExpression(
				"(a / b)", Amount.ONE);
		//System.out.println(this.matrixEquation.getEquation());
		Assert.assertTrue(matrixEquation.getEquation()
				.equals("INITIAL(((a + b) * (a / b)), 10.1)"));
		
	}

	/**
	 * Test equation type type1.
	 */
	@Test
	public void testEquationTypeType1() {

		// Check type parsing.
		Assert.assertEquals(
				EQUATION_TYPE.INTEG,
				MatrixEquation.EQUATION_TYPE.valueOf("INTEG"));
		
	}

	/**
	 * Test equation type type2.
	 */
	@Test
	public void testEquationTypeType2() {

		// Check type parsing.
		Assert.assertEquals(
				EQUATION_TYPE.INITIAL,
				MatrixEquation.EQUATION_TYPE.valueOf("INITIAL"));
		
	}

	/**
	 * Test equation type type3.
	 */
	@Test
	public void testEquationTypeType3() {

		// Check type parsing.
		Assert.assertEquals(
				EQUATION_TYPE.REGULAR,
				MatrixEquation.EQUATION_TYPE.valueOf("REGULAR"));
		
	}
	
}