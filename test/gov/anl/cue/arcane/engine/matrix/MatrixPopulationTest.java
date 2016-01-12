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

import gov.anl.cue.arcane.engine.Util;
import gov.anl.cue.arcane.engine.UtilTest;
import gov.anl.cue.arcane.engine.matrix.MatrixEngine;
import gov.anl.cue.arcane.engine.matrix.MatrixModel;
import gov.anl.cue.arcane.engine.matrix.MatrixPopulation;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * The MatrixPopulationTest class is used to test the MatrixPopulation class.
 */
public class MatrixPopulationTest {

	/**
	 * Instantiates a new matrix population test.
	 */
	public MatrixPopulationTest() {
	}
	
	/** The matrix engine. */
	MatrixEngine matrixEngine = null;
	
	/**
	 * Initialize.
	 */
	@Before
	public void initialize() {
		
		// Create a parent matrix engine.
		this.matrixEngine = new MatrixEngine();

	}
	
	/**
	 * Test get fitness value.
	 */
	@Test
	public void testGetFitnessValue() {

		// Read in an example matrix population for testing.
		MatrixPopulation matrixPopulation = MatrixPopulation
				.read(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR,
				matrixEngine);

		// Test the fitness value getter.
		//System.out.println(matrixPopulation.getFitnessValue());
		Assert.assertEquals(matrixPopulation.getFitnessValue(),
				(Double) 4.9);

	}
	
	/**
	 * Test get matrix engine.
	 */
	@Test
	public void testGetMatrixEngine() {

		// Read in an example matrix population for testing.
		MatrixPopulation matrixPopulation = MatrixPopulation
				.read(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR,
				matrixEngine);

		// Test the matrix engine getter.
		Assert.assertSame(matrixPopulation.getMatrixEngine(),
				matrixEngine);

	}

	/**
	 * Test normalize.
	 */
	@Test
	public void testNormalize() {

		// Read in an example matrix population for testing.
		MatrixPopulation matrixPopulation = MatrixPopulation
				.read(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR,
				matrixEngine);

		// Normalize the population.
		matrixPopulation.normalize();

		// Check the results.
		//System.out.println(matrixPopulation.toString());
		Assert.assertTrue(Util.isEqual(
				matrixPopulation,
				Util.readXMLFile(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
				"//" + Util.TEST_DIR + "//" +
				"//MatrixPopulation_2.xml")));

	}

	/**
	 * Test read regular.
	 */
	@Test
	public void testReadRegular() {

		// Read in an example matrix population for testing.
		MatrixPopulation matrixPopulation = MatrixPopulation
				.read(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR,
				matrixEngine);

		// Check the results.
		//System.out.println(matrixPopulation.toString());
		Assert.assertTrue(Util.isEqual(
				matrixPopulation,
				Util.readXMLFile(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
				"//" + Util.TEST_DIR + "//" +
				"//MatrixPopulation_1.xml")));
		
	}
	
	/**
	 * Test read invalid.
	 */
	@Test
	public void testReadInvalid() {

		// Read in an example matrix population for testing.
		Assert.assertNull(MatrixPopulation.read("",
				matrixEngine));

	}

	/**
	 * Test to string.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testToString() throws IOException {

		// Read in an example matrix population for testing.
		MatrixPopulation matrixPopulation = MatrixPopulation
				.read(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR,
				matrixEngine);

		// Check the results.
		//System.out.println(matrixPopulation.toString());
		Assert.assertTrue(matrixPopulation.toString().equals(
				new String(Files.readAllBytes(FileSystems.getDefault().getPath(
						UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
						"//" + Util.TEST_DIR + "//" +
						"//MatrixPopulation_1.txt"))).trim()));

	}

	/**
	 * Test write.
	 */
	@Test
	public void testWrite() {

		// Read in an example matrix population for testing.
		MatrixPopulation matrixPopulation = MatrixPopulation
				.read(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR,
				matrixEngine);

		// Attempt to write out the matrix population.
		Assert.assertTrue(matrixPopulation
				.write(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
				"//" + Util.OUTPUT_DIR));

		// Compare the results.
		Assert.assertTrue(Util.isEqual(
				MatrixModel.read(matrixEngine,UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
						"//" + Util.INPUT_DIR +
						"//MatrixModel_3.xlsx"),
				MatrixModel.read(matrixEngine, UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
						"//" + Util.OUTPUT_DIR +
						"//MatrixModel_1.xlsx")));
		Assert.assertTrue(Util.isEqual(
				MatrixModel.read(matrixEngine,UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
						"//" + Util.INPUT_DIR +
						"//MatrixModel_2.xlsx"),
				MatrixModel.read(matrixEngine, UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
						"//" + Util.OUTPUT_DIR +
						"//MatrixModel_2.xlsx")));
		Assert.assertTrue(Util.isEqual(
				MatrixModel.read(matrixEngine, UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
						"//" + Util.INPUT_DIR +
						"//MatrixModel_1.xlsx"),
				MatrixModel.read(matrixEngine, UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
						"//" + Util.OUTPUT_DIR +
						"//MatrixModel_3.xlsx")));

	}

	/**
	 * Test image exporting.
	 */
	@Test
	public void testExportImages() {

		// Read in an example matrix population for testing.
		MatrixPopulation matrixPopulation = MatrixPopulation
				.read(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR,
				matrixEngine);

		// Attempt to write out the matrix population.
		matrixPopulation.exportImages(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
				"//" + Util.OUTPUT_DIR);

		// Compare the results.
		Assert.assertTrue(Util.compareBinaryFiles(
				UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
				"//" + Util.OUTPUT_DIR + "//MatrixModel_1.png",
				UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_1.png"));
		Assert.assertTrue(Util.compareBinaryFiles(
				UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
				"//" + Util.OUTPUT_DIR + "//MatrixModel_2.png",
				UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_2.png"));
		Assert.assertTrue(Util.compareBinaryFiles(
				UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
				"//" + Util.OUTPUT_DIR + "//MatrixModel_3.png",
				UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_3.png"));

	}

	/**
	 * Test solid exporting.
	 */
	@Test
	public void testExportSolids() {

		// Read in an example matrix population for testing.
		MatrixPopulation matrixPopulation = MatrixPopulation
				.read(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR,
				matrixEngine);

		// Attempt to write out the matrix population.
		matrixPopulation.exportSolids(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
				"//" + Util.OUTPUT_DIR);

		// Compare the results.
		Assert.assertTrue(Util.compareBinaryFiles(
				UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
				"//" + Util.OUTPUT_DIR + "//MatrixModel_1.scad",
				UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_1.scad"));
		Assert.assertTrue(Util.compareBinaryFiles(
				UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
				"//" + Util.OUTPUT_DIR + "//MatrixModel_2.scad",
				UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_2.scad"));
		Assert.assertTrue(Util.compareBinaryFiles(
				UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
				"//" + Util.OUTPUT_DIR + "//MatrixModel_3.scad",
				UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_3.scad"));

	}

	/**
	 * Test Repast.sd exporting.
	 */
	@Test
	public void testExportRepastSDs() {

		// Create a parent matrix engine.
		MatrixEngine matrixEngine = new MatrixEngine();

		// Read in an example matrix population for testing.
		MatrixPopulation matrixPopulation = MatrixPopulation
				.read(UtilTest.INPUT_TEST_MATRIX_SCENARIO_4_DIR,
				matrixEngine);

		// Attempt to write out the matrix population.
		matrixPopulation.exportRepastSDs(UtilTest.INPUT_TEST_MATRIX_SCENARIO_4_DIR +
				"//" + Util.OUTPUT_DIR);

		// Compare the results.
		Assert.assertTrue(Util.compareBinaryFiles(
				UtilTest.INPUT_TEST_MATRIX_SCENARIO_4_DIR +
				"//" + Util.OUTPUT_DIR + "//MatrixModel_1.rsd",
				UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_1.rsd"));

	}

}
