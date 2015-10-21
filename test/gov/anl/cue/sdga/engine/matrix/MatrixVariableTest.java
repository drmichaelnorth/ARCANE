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

import gov.anl.cue.sdga.engine.Util;
import gov.anl.cue.sdga.engine.UtilTest;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Test;

/**
 * The MatrixVariableTest class is used to test the MatrixVariable class.
 */
public class MatrixVariableTest {

	/**
	 * Instantiates a new matrix variable test.
	 */
	public MatrixVariableTest() {
	}

	/**
	 * Test to string.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testToString() throws IOException {

		// Create a parent matrix engine.
		MatrixEngine matrixEngine = new MatrixEngine();

		// Create a new matrix variable for testing.
		MatrixVariable matrixVariable = MatrixModel.read(matrixEngine,
				UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR + "//" +
				Util.INPUT_DIR + "//MatrixModel_1.xlsx").get(1);

		// Check the results.
		//System.out.println(matrixVariable);
		Assert.assertTrue(matrixVariable.toString().equals(
				new String(Files.readAllBytes(FileSystems
				.getDefault().getPath(
				UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR
				+ "//" + Util.TEST_DIR + "//" +
				"MatrixVariable_1.txt"))).trim()));

	}


	/**
	 * Test valid units.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testUnitsValid() throws IOException {

		// Create a parent matrix engine.
		MatrixEngine matrixEngine = new MatrixEngine();

		// Create a new matrix variable for testing.
		MatrixVariable matrixVariable = MatrixModel.read(matrixEngine,
				UtilTest.INPUT_TEST_DIR +
				"//MatrixModel_20.xlsx").get(1);

		// Check the results.
		//System.out.println(matrixVariable);
		Assert.assertTrue(matrixVariable.toString().equals(
				new String(Files.readAllBytes(FileSystems
				.getDefault().getPath(
				UtilTest.INPUT_TEST_DIR +
				"//MatrixVariable_20.txt"))).trim()));

	}

	/**
	 * Test invalid units.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testUnitsInvalid() throws IOException {

		// Create a parent matrix engine.
		MatrixEngine matrixEngine = new MatrixEngine();

		// Create a new matrix variable for testing.
		MatrixVariable matrixVariable = MatrixModel.read(matrixEngine,
				UtilTest.INPUT_TEST_DIR +
				"//MatrixModel_21.xlsx").get(2);

		// Check the results.
		//System.out.println(matrixVariable);
		Assert.assertTrue(matrixVariable.toString().equals(
				new String(Files.readAllBytes(FileSystems
				.getDefault().getPath(
				UtilTest.INPUT_TEST_DIR +
				"//MatrixVariable_21.txt"))).trim()));

	}

}
