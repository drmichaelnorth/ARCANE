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

import gov.anl.cue.arcane.engine.Util;
import gov.anl.cue.arcane.engine.UtilTest;
import gov.anl.cue.arcane.engine.matrix.MatrixEngine;
import gov.anl.cue.arcane.engine.matrix.MatrixModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;

import org.junit.Assert;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The MatrixEngineTest class is used to test the MatrixEngine class.
 */
public class MatrixEngineTest {

	/**
	 * Instantiates a new matrix engine test.
	 */
	public MatrixEngineTest() {
	}
	
	/**
	 * Test reset.
	 */
	@Test
	public void testReset() {
		
		// Test matrix model template import.
		MatrixEngine matrixEngine = MatrixEngine
				.importTemplate(UtilTest.INPUT_TEST_DIR +
				"//MatrixEngineTemplate_1.xlsx");
		
		// Copy the test engine.
		MatrixEngine matrixEngineCopy =
				Util.deepCopy(matrixEngine);
		
		// Reset.
		matrixEngineCopy.reset();
		
		// Check the results.
		Assert.assertEquals(
				(Integer) matrixEngine.randomSeed,
				(Integer) matrixEngineCopy.randomSeed);
	
		// Check the results.
		Assert.assertTrue(Util.isEqual(
				matrixEngine.inputPopulation,
				matrixEngineCopy.outputPopulation));

	}

	/**
	 * Test fill regular.
	 */
	@Test
	public void testFillRegular() {
		
		// Read in an example matrix engine for testing.
		MatrixEngine matrixEngine = MatrixEngine
				.read(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR);
		
		// Test filling.
		matrixEngine.fill();
		
		// Check the results.
		//System.out.println(matrixEngine.populationSize);
		//System.out.println(matrixEngine.outputPopulation.size());
		Assert.assertEquals((Integer) matrixEngine.outputPopulation.size(),
				(Integer) matrixEngine.populationSize);
		Assert.assertEquals((Integer) matrixEngine.outputPopulation.size(),
				(Integer) 100);
		
	}
	
	/**
	 * Test fill invalid.
	 */
	@Test
	public void testFillInvalid() {

		// Create an invalid example for testing.
		MatrixEngine matrixEngine = new MatrixEngine();
		
		// Test filling.
		matrixEngine.fill();

		// Check the results.
		Assert.assertEquals((Integer) matrixEngine.outputPopulation.size(),
				(Integer) 0);

	}
	
	/**
	 * Test kill.
	 */
	@Test
	public void testKill() {
		
		// Read in an example matrix engine for testing.
		MatrixEngine matrixEngine = MatrixEngine
				.read(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR);
		
		// Test killing.
		matrixEngine.kill();
		
		// Check the results.
		//System.out.println(matrixEngine.population.size());
		Assert.assertEquals((Integer) matrixEngine.outputPopulation.size(),
				(Integer) 1);
		
	}
	
	/**
	 * Test evolve one step.
	 *
	 * @throws NumberFormatException the number format exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testEvolveOneStep() throws NumberFormatException, IOException {

		// Read in an example matrix engine for testing.
		MatrixEngine matrixEngine = MatrixEngine
				.read(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR);
		
		// Evolve the population.
		matrixEngine.evolve(1);

		// Check the best fitness value.
		//System.out.println(matrixEngine.population.getFitnessValue());
		Assert.assertEquals(matrixEngine.outputPopulation.getFitnessValue(),
				(Double) 5.999878227941653);
		
		// Check the fitness value distribution.
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(
				new FileInputStream(
						new File(
				UtilTest.INPUT_TEST_MATRIX_SCENARIO_2_DIR + "//" +
				Util.TEST_DIR +
				"//fitnessdistribution.txt"))));
		for (MatrixModel matrixModel : matrixEngine.outputPopulation) {
			//System.out.println(matrixModel.getFitnessValue());
			Assert.assertEquals(matrixModel.getFitnessValue(),
					new Double(reader.readLine()));
		}
		reader.close();

	}
	
	/**
	 * Test evolve ten steps.
	 * 
	 *  It is often commented out since it takes quite a bit of time.
	 *
	 * @throws NumberFormatException the number format exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testEvolveTenSteps() throws NumberFormatException, IOException {
	
		// Read in another example matrix engine for testing.
		MatrixEngine matrixEngine = MatrixEngine
				.read(UtilTest.INPUT_TEST_MATRIX_SCENARIO_4_DIR);
		
		// Evolve the population.
		matrixEngine.evolve(10);

		// Write out the results.
		matrixEngine.write(UtilTest.INPUT_TEST_MATRIX_SCENARIO_4_DIR +
				"//" + Util.OUTPUT_DIR);
		
		// Check the best fitness value.
		//System.out.println(matrixEngine.outputPopulation.getFitnessValue());
		Assert.assertEquals(matrixEngine.outputPopulation.getFitnessValue(),
				(Double) 6.17377150545036E28);
		
		// Check the fitness value distribution.
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(
				new FileInputStream(
						new File(
				UtilTest.INPUT_TEST_MATRIX_SCENARIO_4_DIR + "//" +
				Util.TEST_DIR +
				"//fitnessdistribution.txt"))));
		for (MatrixModel matrixModel : matrixEngine.outputPopulation) {
			//System.out.println(matrixModel.getFitnessValue());
			Assert.assertEquals(matrixModel.getFitnessValue(),
				new Double(reader.readLine()));
		}
		reader.close();

	}

	/**
	 * Test read regular.
	 */
	@Test
	public void testReadRegular() {

		// Read in an example matrix engine for testing.
		MatrixEngine matrixEngine = MatrixEngine
				.read(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR);

		// Check the results.
		//System.out.println(matrixEngine);
		Assert.assertTrue(Util.isEqual(
				matrixEngine,
				Util.readXMLFile(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR
						+ "//" + Util.TEST_DIR + "//"
						+ "//MatrixEngine_1.xml")));

	}

	/**
	 * Test read invalid.
	 */
	@Test
	public void testReadInvalid() {

		// Check reading an invalid file.
		Assert.assertNull(MatrixEngine.read(""));

	}

	/**
	 * Test import template regular1 memory.
	 */
	@Test
	public void testImportTemplateRegular1Memory() {

		// Test matrix model template import.
		MatrixEngine matrixEngine = MatrixEngine
				.importTemplate(UtilTest.INPUT_TEST_DIR +
				"//MatrixEngineTemplate_1.xlsx");
		
		// Check the results.
		//System.out.println(matrixEngine);
		Assert.assertTrue(Util.isEqual(
				matrixEngine,
				Util.readXMLFile(UtilTest.INPUT_TEST_DIR +
				"//MatrixEngineTemplate_1.xml")));

	}

	/**
	 * Test import template regular1 disk.
	 */
	@Test
	public void testImportTemplateRegular1Disk() {

		// Test matrix model template import.
		MatrixEngine matrixEngine = MatrixEngine
				.importTemplate(UtilTest.INPUT_TEST_DIR +
				"//MatrixEngineTemplate_1.xlsx");
		
		// Write the result out for testing.
		matrixEngine.write(UtilTest.OUTPUT_TEST_DIR +
				"//MatrixEngineTemplate_1_Test");

		// Check the results.
		Assert.assertTrue(new File(UtilTest.OUTPUT_TEST_DIR +
				"//MatrixEngineTemplate_1_Test")
				.isDirectory());
		Assert.assertTrue(new File(UtilTest.OUTPUT_TEST_DIR +
				"//MatrixEngineTemplate_1_Test//" +
				Util.INPUT_DIR)
				.isDirectory());
		Assert.assertTrue(new File(UtilTest.OUTPUT_TEST_DIR +
				"//MatrixEngineTemplate_1_Test//" +
				Util.OUTPUT_DIR)
				.isDirectory());
		Assert.assertTrue(new File(UtilTest.OUTPUT_TEST_DIR +
				"//MatrixEngineTemplate_1_Test//" +
				Util.TEST_DIR)
				.isDirectory());
		Assert.assertTrue(Util.isEqual(
				MatrixModel.read(matrixEngine, UtilTest.OUTPUT_TEST_DIR +
						"//MatrixEngineTemplate_1_Test" +
						"//MatrixEngine.xlsx"),
				MatrixModel.read(matrixEngine, UtilTest.INPUT_TEST_DIR +
						"//MatrixEngineTemplate_1_Test_MatrixEngine.xlsx")));
		Assert.assertTrue(Util.isEqual(
				MatrixModel.read(matrixEngine, UtilTest.OUTPUT_TEST_DIR +
						"//MatrixEngineTemplate_1_Test//" + Util.INPUT_DIR +
						"//MatrixModel_1.xlsx"),
				MatrixModel.read(matrixEngine, UtilTest.INPUT_TEST_DIR +
						"//MatrixEngineTemplate_1_Test_MatrixModel_1.xlsx")));

	}
		
	/**
	 * Test import template regular two.
	 */
	@Test
	public void testImportTemplateRegular2() {

		// Test matrix model template import.
		MatrixEngine matrixEngine = MatrixEngine
				.importTemplate(UtilTest.INPUT_TEST_DIR +
				"//MatrixEngineTemplate_2.xlsx");
		
		// Check the results.
		//System.out.println(matrixEngine);
		Assert.assertTrue(Util.isEqual(
				matrixEngine,
				Util.readXMLFile(UtilTest.INPUT_TEST_DIR +
				"//MatrixEngineTemplate_2.xml")));

	}

	/**
	 * Test import template regular three.
	 */
	@Test
	public void testImportTemplateRegular3() {

		// Test matrix model template import.
		MatrixEngine matrixEngine = MatrixEngine
				.importTemplate(UtilTest.INPUT_TEST_DIR +
				"//MatrixEngineTemplate_3.xlsx");
		
		// Check the results.
		//System.out.println(matrixEngine);
		Assert.assertTrue(Util.isEqual(
				matrixEngine,
				Util.readXMLFile(UtilTest.INPUT_TEST_DIR +
				"//MatrixEngineTemplate_3.xml")));

	}

	/**
	 * Test import template regular four.
	 */
	@Test
	public void testImportTemplateRegular4() {

		// Test matrix model template import.
		MatrixEngine matrixEngine = MatrixEngine
				.importTemplate(UtilTest.INPUT_TEST_DIR +
				"//MatrixEngineTemplate_5.xlsx");
		
		// Check the results.
		//System.out.println(matrixEngine);
		Assert.assertTrue(Util.isEqual(
				matrixEngine,
				Util.readXMLFile(UtilTest.INPUT_TEST_DIR +
				"//MatrixEngineTemplate_5.xml")));

	}

	/**
	 * Test importing invalid template units.
	 */
	@Test
	public void testImportTemplateInvalidUnits() {

		// Test matrix model template import.
		MatrixEngine matrixEngine = MatrixEngine
				.importTemplate(UtilTest.INPUT_TEST_DIR +
				"//MatrixEngineTemplate_6.xlsx");
		
		// Check the results.
		//System.out.println(matrixEngine);
		Assert.assertTrue(Util.isEqual(
				matrixEngine,
				Util.readXMLFile(UtilTest.INPUT_TEST_DIR +
				"//MatrixEngineTemplate_6.xml")));

	}

	/**
	 * Test import template regular with equation evolution.
	 */
	@Test
	public void testImportAndRunTemplateWithEquationEvolution() {

		// Test matrix model template import.
		MatrixEngine matrixEngine = MatrixEngine
				.importTemplate(UtilTest.INPUT_TEST_DIR +
				"//MatrixEngineTemplate_7.xlsx");
		
		// Run the model one step.
		matrixEngine.evolve(1);
		
		// Check the results.
		//System.out.println(matrixEngine);
		Assert.assertTrue(Util.isEqual(
				matrixEngine,
				Util.readXMLFile(UtilTest.INPUT_TEST_DIR +
				"//MatrixEngineTemplate_7.xml")));

	}

	/**
	 * Test import template regular with equation evolution.
	 */
//	@Test
//	public void testImportAndRunTemplateWithEquationEvolutionOutput() {
//
//		// Test matrix model template import.
//		MatrixEngine matrixEngine = MatrixEngine
//				.importTemplate(UtilTest.INPUT_TEST_DIR +
//				"//MatrixEngineTemplate_14.xlsx");
//		
//		// Show the initial equations.
//		for (MatrixModel matrixModel : matrixEngine.outputPopulation) {
//			int counter = 1;
//			for (MatrixVariable matrixVariable : matrixModel) {
//				for (String equation : matrixVariable.equations) {
//					System.out.println("" + (counter++) +
//							": " + equation);
//				}
//			}
//		}
//		System.out.println("        ");
//
//		// Run the model one step.
//		matrixEngine.evolve(5);
//		
//		// Show the results.
//		for (MatrixModel matrixModel : matrixEngine.outputPopulation) {
//			int counter = 1;
//			for (MatrixVariable matrixVariable : matrixModel) {
//				for (String equation : matrixVariable.equations) {
//					System.out.println("" + (counter++) +
//							": " + equation);
//				}
//			}
//		}
//
//	}

	/**
	 * Test import template invalid.
	 */
	@Test
	public void testImportTemplateInvalid() {

		// Test invalid matrix engine template importing.
		Assert.assertNull(MatrixEngine.importTemplate("NoSuchFile"));

	}

	/**
	 * Test write.
	 */
	@Test
	public void testWrite() {

		// Read in an example matrix engine for testing.
		MatrixEngine matrixEngineA = MatrixEngine
				.read(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR);

		// Attempt to write out the matrix engine.
		Assert.assertTrue(matrixEngineA
				.write(UtilTest.OUTPUT_TEST_MATRIX_SCENARIO_1_DIR));

		// Read in the output matrix engine.
		MatrixEngine matrixEngineB = MatrixEngine
				.read(UtilTest.OUTPUT_TEST_MATRIX_SCENARIO_1_DIR);
		
		// Check the results.
		Assert.assertTrue(Util.isEqual(matrixEngineA, matrixEngineB));
		Assert.assertTrue(Util.isEqual(
				MatrixModel.read(matrixEngineA, UtilTest.OUTPUT_TEST_MATRIX_SCENARIO_1_DIR
						+ "//" + Util.OUTPUT_DIR
						+ "//MatrixModel_1.xlsx"),
				MatrixModel.read(matrixEngineA, UtilTest.OUTPUT_TEST_DIR
						+ "//MatrixModel_1.xlsx")));
		Assert.assertTrue(Util.isEqual(
				MatrixModel.read(matrixEngineA, UtilTest.OUTPUT_TEST_MATRIX_SCENARIO_1_DIR
						+ "//" + Util.OUTPUT_DIR
						+ "//MatrixModel_2.xlsx"),
				MatrixModel.read(matrixEngineA, UtilTest.OUTPUT_TEST_DIR
						+ "//MatrixModel_2.xlsx")));
		Assert.assertTrue(Util.isEqual(
				MatrixModel.read(matrixEngineA, UtilTest.OUTPUT_TEST_MATRIX_SCENARIO_1_DIR
						+ "//" + Util.OUTPUT_DIR
						+ "//MatrixModel_3.xlsx"),
				MatrixModel.read(matrixEngineA, UtilTest.OUTPUT_TEST_DIR
						+ "//MatrixModel_3.xlsx")));

	}
	
	/**
	 * Test write.
	 */
	@Test
	public void testWriteInvalid() {

		// Read in an example matrix engine for testing.
		MatrixEngine matrixEngine = MatrixEngine
				.read(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR);

		// Attempt to write out the matrix engine to an invalid location.
		Assert.assertFalse(matrixEngine.write(null));

	}

	/**
	 * Test write.
	 */
	@Test
	public void testWriteEngineFileInvalid() {

		// Read in an example matrix engine for testing.
		MatrixEngine matrixEngine = MatrixEngine
				.read(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR);

		// Attempt to write out the matrix engine to an invalid location.
		Assert.assertFalse(matrixEngine.writeEngineFile(null));

	}

	/**
	 * Test image exporting.
	 */
	@Test
	public void testExportImages() {

		// Create a parent matrix engine.
		MatrixEngine matrixEngine = MatrixEngine
				.read(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR);

		// Attempt to write out the matrix population.
		Assert.assertTrue(matrixEngine
				.exportImages(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
				"//" + Util.OUTPUT_DIR));

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

		// Create a parent matrix engine.
		MatrixEngine matrixEngine = MatrixEngine
				.read(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR);

		// Attempt to write out the matrix population.
		Assert.assertTrue(matrixEngine
				.exportSolids(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
				"//" + Util.OUTPUT_DIR));

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
	 * Test solid exporting.
	 */
	@Test
	public void testExportRepastSDs() {

		// Create a parent matrix engine.
		MatrixEngine matrixEngine = MatrixEngine
				.read(UtilTest.INPUT_TEST_MATRIX_SCENARIO_4_DIR);

		// Attempt to write out the matrix population.
		Assert.assertTrue(matrixEngine
				.exportRepastSDs(UtilTest.INPUT_TEST_MATRIX_SCENARIO_4_DIR +
				"//" + Util.OUTPUT_DIR));

		// Compare the results.
		Assert.assertTrue(Util.compareBinaryFiles(
				UtilTest.INPUT_TEST_MATRIX_SCENARIO_4_DIR +
				"//" + Util.OUTPUT_DIR + "//MatrixModel_1.rsd",
				UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_1.rsd"));

	}

	/**
	 * Test to string.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testToString() throws IOException {

		// Read in an example matrix engine for testing.
		MatrixEngine matrixEngine = MatrixEngine
				.read(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR);

		// Check the results.
		//System.out.println(matrixEngine.toString());
		Assert.assertTrue(matrixEngine.toString().equals(
				new String(Files.readAllBytes(FileSystems.getDefault().getPath(
				UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR
				+ "//" + Util.TEST_DIR + "//"
				+ "//MatrixEngine_1.txt"))).trim()));

	}

	/**
	 * Test division by zero in fitness function.
	 */
	@Test
	public void testDivisionByZeroInFitnessFunction() {
		
		// Setup a matrix engine and model.
		MatrixEngine matrixEngine = MatrixEngine
				.importTemplate(UtilTest.INPUT_TEST_DIR +
				"//MatrixEngineTemplate_11.xlsx");

		// Get a test matrix model.
		MatrixModel matrixModel = matrixEngine.inputPopulation.get(0);
		
		// Test division by zero in the fitness function.
		//System.out.println(matrixModel.getFitnessValue());
		Assert.assertEquals((Double) Double.NEGATIVE_INFINITY,
				matrixModel.getFitnessValue());		

	}
	
	/**
	 * Test division by zero in main formulation.
	 */
	@Test
	public void testDivisionByZeroInMainFormulation() {

		// Setup a matrix engine and model.
		MatrixEngine matrixEngine = MatrixEngine
				.importTemplate(UtilTest.INPUT_TEST_DIR +
				"//MatrixEngineTemplate_12.xlsx");

		// Get a test matrix model.
		MatrixModel matrixModel = matrixEngine.inputPopulation.get(0);
		
		// Test division by zero in the main formulation.
		//System.out.println(matrixModel.getFitnessValue());
		Assert.assertEquals((Double) Double.NEGATIVE_INFINITY,
				matrixModel.getFitnessValue());		

	}
	
	/**
	 * Test stochastic run.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void testStochasticRun() throws IOException {

		// Test matrix model template import.
		MatrixEngine matrixEngine = MatrixEngine
				.importTemplate(UtilTest.INPUT_TEST_DIR +
				"//MatrixEngineTemplate_13.xlsx");
		
		// Perform a stochastic run.
		matrixEngine.stochasticRun(UtilTest.OUTPUT_TEST_DIR +
				"//MatrixEngineTemplate_13_Test",
				2, 2);
		
		// Check the results.
		Assert.assertTrue(Util.compareBinaryFiles(
				UtilTest.OUTPUT_TEST_DIR +
				"//MatrixEngineTemplate_13_Test//" +
				"//Fitness.txt",
				UtilTest.INPUT_TEST_DIR +
				"//Fitness.txt"));

	}

}
