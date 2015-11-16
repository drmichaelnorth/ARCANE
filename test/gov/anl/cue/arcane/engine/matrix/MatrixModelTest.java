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
import gov.anl.cue.arcane.engine.matrix.MatrixModel.FITNESS_FUNCTION_TYPE;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Set;

import javax.measure.unit.Unit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * The MatrixModelTest class is used to test the MatrixModel class.
 */
public class MatrixModelTest {
	
	/**
	 * Instantiates a new matrix model test.
	 */
	public MatrixModelTest() {

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
	 * Test calculate fitness value one.
	 */
	@Test
	public void testCalculateFitnessValue1() {
		
		// Read in an example matrix model using zero fitness for testing.
		MatrixModel matrixModel = MatrixModel
				.read(matrixEngine, UtilTest.INPUT_TEST_DIR + "//MatrixModel_10.xlsx");

		// Calculate and check the fitness value.
		//System.out.println(((double) matrixModel.calculateFitnessValue()));
		Assert.assertEquals(matrixModel.calculateFitnessValue(), (Double) 0.0);

	}
	
	/**
	 * Test calculate fitness value two.
	 */
	@Test
	public void testCalculateFitnessValue2() {
		
		// Read in an example matrix model using simple maximum fitness for testing.
		MatrixModel matrixModel = MatrixModel
				.read(matrixEngine, UtilTest.INPUT_TEST_DIR + "//MatrixModel_11.xlsx");

		// Calculate and check the fitness value.
		//System.out.println(((double) matrixModel.calculateFitnessValue()));
		Assert.assertEquals(matrixModel.calculateFitnessValue(), (Double) 3.25);

	}

	/**
	 * Test calculate fitness value three.
	 */
	@Test
	public void testCalculateFitnessValue3() {
		

		// Read in an example matrix model using Repast
		// system dynamics fitness for testing.
		MatrixModel matrixModel = MatrixModel
				.read(matrixEngine, UtilTest.INPUT_TEST_DIR +
						"//MatrixModel_12.xlsx");

		// Calculate and check the fitness value.
		//System.out.println(((double) matrixModel.calculateFitnessValue()));
		Assert.assertEquals(matrixModel.calculateFitnessValue(),
				(Double)2.4139874174668718E18);

	}

	/**
	 * Test split.
	 */
	@Test
	public void testSplit() {

		// Test copying.
		MatrixModel matrixModel = MatrixModel
				.read(matrixEngine, UtilTest.INPUT_TEST_DIR + 
				"//MatrixModel_17.xlsx");
		Set<String> splits = matrixModel.split(
				MatrixModel.NO_SUFFIX, MatrixModel.COMBINED_SUFFIX);

		// Check the results.
		//System.out.println(Util.XSTREAM_DRIVER.toXML(splits));
		Assert.assertTrue(Util.isEqual(splits,
				Util.readXMLFile(UtilTest.INPUT_TEST_DIR + "//MatrixModelSplit_17.xml")));
	
	}
	
	/**
	 * Test copy.
	 */
	@Test
	public void testCopy() {

		// Test copying.
		MatrixModel matrixModel = MatrixModel
				.read(matrixEngine, UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
				"//" + Util.INPUT_DIR + "//" + 
				"//MatrixModel_1.xlsx");
		MatrixModel matrixModelCopy = matrixModel.copy();

		// Check the results.
		Assert.assertTrue(Util.isEqual(matrixModel, matrixModelCopy));

	}

	/**
	 * Test cross over one.
	 */
	@Test
	public void testCrossOver1() {

		// Read in an example matrix for testing.
		MatrixModel matrixModelA = MatrixModel.read(matrixEngine, UtilTest.INPUT_TEST_DIR
				+ "//MatrixModel_7.xlsx");

		// Read in another example matrix for testing.
		MatrixModel matrixModelB = MatrixModel.read(matrixEngine, UtilTest.INPUT_TEST_DIR
				+ "//MatrixModel_8.xlsx");

		// Crossover.
		MatrixModel matrixModelC = matrixModelA.crossOver(matrixModelB);

		// Check the results.
		//System.out.println(matrixModelC);
		Assert.assertTrue(Util.isEqual(matrixModelC,
				Util.readXMLFile(UtilTest.INPUT_TEST_DIR + "//MatrixModel_9.xml")));
		
	}

	/**
	 * Test cross over two.
	 */
	@Test
	public void testCrossOver2() {
		
		// Read in an example matrix for testing.
		MatrixModel matrixModelD = MatrixModel.read(matrixEngine, UtilTest.INPUT_TEST_DIR
				+ "//MatrixModel_16.xlsx");

		// Read in another example matrix for testing.
		MatrixModel matrixModelE = MatrixModel.read(matrixEngine, UtilTest.INPUT_TEST_DIR
				+ "//MatrixModel_16.xlsx");

		// Crossover.
		MatrixModel matrixModelF = matrixModelD
				.crossOver(matrixModelE)
				.crossOver(matrixModelE)
				.crossOver(matrixModelE);

		// Check the results.
		//System.out.println(matrixModelF);
		Assert.assertTrue(Util.isEqual(matrixModelF,
				Util.readXMLFile(UtilTest.INPUT_TEST_DIR + "//MatrixModel_16.xml")));

	}
	
	/**
	 * Test cross over three.
	 */
	@Test
	public void testCrossOver3() {

		// Read in another example matrix for testing.
		MatrixModel matrixModelB = MatrixModel.read(matrixEngine, UtilTest.INPUT_TEST_DIR
				+ "//MatrixModel_8.xlsx");

		// Read in another example matrix for testing.
		MatrixModel matrixModelD = MatrixModel.read(matrixEngine, UtilTest.INPUT_TEST_DIR
				+ "//MatrixModel_16.xlsx");

		// Crossover.
		MatrixModel matrixModelG = matrixModelB
				.crossOver(matrixModelD);
	
		// Check the results.
		//System.out.println(matrixModelG);
		Assert.assertTrue(Util.isEqual(matrixModelG,
				Util.readXMLFile(UtilTest.INPUT_TEST_DIR + "//MatrixModel_19.xml")));

	}

	/**
	 * Test get fitness value.
	 */
	@Test
	public void testGetFitnessValue() {

		// Read in an example matrix for testing.
		MatrixModel matrixModel = MatrixModel
				.read(matrixEngine, UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
				"//" + Util.INPUT_DIR +
				"//MatrixModel_1.xlsx");

		// Calculate and check the fitness value.
		//System.out.println(((double) matrixModel.calculateFitnessValue()));
		Assert.assertEquals(matrixModel.getFitnessValue(), (Double) 3.25);

	}

	/**
	 * Test mutate.
	 */
	@Test
	public void testMutate() {

		// Read in an example matrix for testing.
		MatrixModel matrixModel = MatrixModel.read(matrixEngine, UtilTest.INPUT_TEST_DIR
				+ "//MatrixModel_6.xlsx");

		// Mutate the model.
		matrixModel.mutate();

		// Check the results.
		//System.out.println(matrixModel);
		Assert.assertTrue(Util.isEqual(matrixModel,
				Util.readXMLFile(UtilTest.INPUT_TEST_DIR + "//MatrixModel_6.xml")));

	}

	/**
	 * Test normalize.
	 */
	@Test
	public void testNormalize() {

		// Read in an example matrix model for testing.
		MatrixModel matrixModel = MatrixModel.read(matrixEngine, UtilTest.INPUT_TEST_DIR
				+ "//MatrixModel_5.xlsx");

		// Normalize the matrix.
		matrixModel.normalize();

		// Check the results.
		//System.out.println(matrixModel);
		Assert.assertTrue(Util.isEqual(matrixModel,
				Util.readXMLFile(UtilTest.INPUT_TEST_DIR + "//MatrixModel_5.xml")));

	}

	/**
	 * Test import template regular four.
	 */
	@Test
	public void testImportTemplateRegular() {

		// Test matrix model template import.
		MatrixModel matrixModel = MatrixModel
				.importTemplate(matrixEngine, UtilTest.INPUT_TEST_DIR +
				"//MatrixModelTemplate_1.xlsx");
		
		// Write the result out for testing.
		Assert.assertTrue(matrixModel
				.write(UtilTest.OUTPUT_TEST_DIR +
				"//MatrixModelTemplate_1_Test.xlsx"));

		// Check the results again.
		//System.out.println(matrixModel);
		Assert.assertTrue(Util.isEqual(
				matrixModel,
				Util.readXMLFile(UtilTest.INPUT_TEST_DIR +
				"//MatrixModelTemplate_1.xml")));

	}
		
	/**
	 * Test import template invalid.
	 */
	@Test
	public void testImportTemplateInvalid() {

		// Test invalid matrix model template importing.
		Assert.assertNull(MatrixModel.importTemplate(
				new MatrixEngine(), "NoSuchFile"));

	}

	/**
	 * Test read regular one.
	 */
	@Test
	public void testReadRegular1() {

		// Test matrix model reading.
		MatrixModel matrixModel = MatrixModel
				.read(matrixEngine, UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
				"//" + Util.INPUT_DIR +
				"//MatrixModel_1.xlsx");

		// Check the results.
		//System.out.println(matrixModel);
		Assert.assertTrue(Util.isEqual(
				matrixModel,
				Util.readXMLFile(UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
				"//" + Util.TEST_DIR +
				"//MatrixModel_1.xml")));
		
	}
	
	/**
	 * Test read regular two.
	 */
	@Test
	public void testReadRegular2() {

		// Test matrix model reading again.
		MatrixModel matrixModel = MatrixModel
				.read(matrixEngine, UtilTest.INPUT_TEST_DIR +
				"//MatrixModel_12.xlsx");

		// Check the results  again.
		//System.out.println(matrixModel);
		Assert.assertTrue(Util.isEqual(
				matrixModel,
				Util.readXMLFile(UtilTest.INPUT_TEST_DIR +
				"//MatrixModel_12.xml")));
		
	}

	/**
	 * Test read invalid.
	 */
	@Test
	public void testReadInvalid() {
	
		// Test matrix model misreading.
		Assert.assertNull(MatrixModel.read(new MatrixEngine(), "NoSuchFile"));

	}

	/**
	 * Test string conversion.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testToString() throws IOException {

		// Test matrix model string conversion.
		MatrixModel matrixModel = MatrixModel
				.read(matrixEngine, UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
				"//" + Util.INPUT_DIR + "//" +
				"//MatrixModel_1.xlsx");

		// Check the results.
		//System.out.println(matrixModel);
		Assert.assertTrue(matrixModel.toString().equals(
				new String(Files.readAllBytes(FileSystems.getDefault().getPath(
				UtilTest.INPUT_TEST_MATRIX_SCENARIO_1_DIR +
				"//" + Util.TEST_DIR + "//" +
				"MatrixModel_1.txt"))).trim()));

	}

	/**
	 * Test write regular one.
	 */
	@Test
	public void testWriteRegular1() {

		// Read in an example matrix for testing.
		MatrixModel matrixModel = MatrixModel.read(matrixEngine,
				UtilTest.INPUT_TEST_DIR + "//MatrixModel_4.xlsx");

		// Attempt to write out the matrix model.
		matrixModel.write(UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_4.xlsx");

		// Compare the results.
		Assert.assertTrue(Util.isEqual(
				MatrixModel.read(matrixEngine, UtilTest.INPUT_TEST_DIR + "//MatrixModel_4.xlsx"),
				MatrixModel.read(matrixEngine, UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_4.xlsx")));

	}
	
	/**
	 * Test write regular two.
	 */
	@Test
	public void testWriteRegular2() {

		// Read in an example matrix for testing.
		MatrixModel matrixModel = MatrixModel.read(matrixEngine,
				UtilTest.INPUT_TEST_DIR + "//MatrixModel_12.xlsx");

		// Attempt to write out the matrix model.
		matrixModel.write(UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_12.xlsx");

		// Compare the results.
		Assert.assertTrue(Util.isEqual(
				MatrixModel.read(matrixEngine, UtilTest.INPUT_TEST_DIR + "//MatrixModel_12.xlsx"),
				MatrixModel.read(matrixEngine, UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_12.xlsx")));

	}
	
	/**
	 * Test write regular three.
	 */
	@Test
	public void testWriteRegular3() {

		// Read in an example matrix for testing.
		MatrixModel matrixModel = MatrixModel.read(matrixEngine,
				UtilTest.INPUT_TEST_DIR + "//MatrixModel_15.xlsx");

		// Attempt to write out the matrix model.
		matrixModel.write(UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_15.xlsx");

		// Compare the results.
		Assert.assertTrue(Util.isEqual(
				MatrixModel.read(matrixEngine, UtilTest.INPUT_TEST_DIR + "//MatrixModel_15.xlsx"),
				MatrixModel.read(matrixEngine, UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_15.xlsx")));

		
	}
	
	/**
	 * Test write regular four.
	 */
	@Test
	public void testWriteRegular4() {

		// Read in an example matrix for testing.
		MatrixModel matrixModel = MatrixModel.read(matrixEngine,
				UtilTest.INPUT_TEST_DIR + "//MatrixModel_15.xlsx");

		// Attempt to write out the matrix model.
		matrixModel.fitnessValue = Double.NaN;
		matrixModel.write(UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_15a.xlsx");
	
		// Compare the results.
		Assert.assertTrue(Util.isEqual(
				MatrixModel.read(matrixEngine, UtilTest.INPUT_TEST_DIR + "//MatrixModel_15.xlsx"),
				MatrixModel.read(matrixEngine, UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_15a.xlsx")));

	}
	
	/**
	 * Test write invalid.
	 */
	@Test
	public void testWriteInvalid() {

		// Check an invalid request.
		Assert.assertFalse(new MatrixModel(this.matrixEngine).write(""));

	}

	/**
	 * Test run matrix model regular.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testRunMatrixModelRegular() throws Exception {

		// Make sure that the input files are cleared.
		new File(Util.TEMP_DIR + "//" +
				MatrixModel.PACKAGE_PATH +
				MatrixModel.CONCRETE_CLASS_NAME + ".java")
				 .delete();

		// Make sure that the input files are cleared.
		new File(Util.TEMP_DIR + "//" +
				MatrixModel.PACKAGE_PATH +
				MatrixModel.CONCRETE_CLASS_NAME + ".class")
				 .delete();
		
		// Read in an example matrix for testing.
		MatrixModel matrixModel = MatrixModel.read(matrixEngine,
				UtilTest.INPUT_TEST_DIR + "//MatrixModel_12.xlsx");
		
		// Write and compile a formulation.
		//System.out.println(matrixModel.getFitnessValue());
		Assert.assertEquals(
				(Double) matrixModel.getFitnessValue(),
				(Double) 2.4139874174668718E18);

	}

	/**
	 * Test run matrix model invalid.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testRunMatrixModelInvalid() throws Exception {
		
		// Make sure that the input files are cleared.
		new File(Util.TEMP_DIR + "//" +
				MatrixModel.PACKAGE_PATH +
				MatrixModel.CONCRETE_CLASS_NAME + ".java")
				 .delete();

		// Make sure that the input files are cleared.
		new File(Util.TEMP_DIR + "//" +
				MatrixModel.PACKAGE_PATH +
				MatrixModel.CONCRETE_CLASS_NAME + ".class")
				 .delete();
		
		// Check an invalid request.
		Assert.assertEquals((Double) ((new MatrixModel(new MatrixEngine()))
				.runMatrixModel()), (Double) Double.NaN);
		
	}
	
	/**
	 * Test building a formulation.
	 */
	@Test
	public void testFormulate() {

		// Read in an example matrix for testing.
		MatrixModel matrixModel = MatrixModel.read(matrixEngine,
				UtilTest.INPUT_TEST_DIR + "//MatrixModel_12.xlsx");

		// Check an invalid request.
		Assert.assertFalse(matrixModel.formulate(null, null, null,
				null));

	}

	/**
	 * Test export image regular one.
	 */
	@Test
	public void testExportImageRegular1() {

		// Read in an example matrix for testing.
		MatrixModel matrixModel = MatrixModel.read(matrixEngine,
				UtilTest.INPUT_TEST_DIR + "//MatrixModel_12.xlsx");

		// Attempt to write out an image.
		Assert.assertTrue(matrixModel
				.exportImage(UtilTest.OUTPUT_TEST_DIR +
				"//MatrixModel_12.png"));

		// Compare the results.
		Assert.assertTrue(Util.compareBinaryFiles(
				UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_12.png",
				UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_12_Test.png"));
		
	}

	/**
	 * Test export image regular two.
	 */
	@Test
	public void testExportImageRegular2() {

		// Test copying.
		MatrixModel matrixModel = MatrixModel
				.read(matrixEngine, UtilTest.INPUT_TEST_DIR + 
				"//MatrixModel_17.xlsx");

		// Attempt to write out the next image.
		Assert.assertTrue(matrixModel
				.exportImage(UtilTest.OUTPUT_TEST_DIR +
				"//MatrixModel_17.png"));

		// Check the results.
		Assert.assertTrue(Util.compareBinaryFiles(
				UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_17.png",
				UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_17_Test.png"));
		
	}

	/**
	 * Test export image invalid.
	 */
	@Test
	public void testExportImageInvalid() {
		
		// Check an invalid request.
		Assert.assertFalse(new MatrixModel(this.matrixEngine)
			.exportImage(null, null, null, null, null));

	}
	
	/**
	 * Test export solid regular1.
	 */
	@Test
	public void testExportSolidRegular1() {

		// Read in an example matrix for testing.
		MatrixModel matrixModel = MatrixModel.read(matrixEngine,
				UtilTest.INPUT_TEST_DIR + "//MatrixModel_12.xlsx");

		// Attempt to write out the matrix model.
		Assert.assertTrue(matrixModel
				.exportSolid(UtilTest.OUTPUT_TEST_DIR +
						"//MatrixModel_12.scad"));

		// Compare the results.
		Assert.assertTrue(Util.compareBinaryFiles(
				UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_12.scad",
				UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_12_Test.scad"));

	}
	
	/**
	 * Test export solid regular2.
	 */
	@Test
	public void testExportSolidRegular2() {

		// Test copying.
		MatrixModel matrixModel = MatrixModel
				.read(matrixEngine, UtilTest.INPUT_TEST_DIR + 
				"//MatrixModel_17.xlsx");

		// Attempt to write out the next matrix model.
		Assert.assertTrue(matrixModel
				.exportSolid(UtilTest.OUTPUT_TEST_DIR +
				"//MatrixModel_17.scad"));

		// Check the results.
		Assert.assertTrue(Util.compareBinaryFiles(
				UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_17.scad",
				UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_17_Test.scad"));

	}
		
	/**
	 * Test export solid invalid.
	 */
	@Test
	public void testExportSolidInvalid() {
		
		// Check an invalid request.
		Assert.assertFalse(new MatrixModel(this.matrixEngine)
			.exportSolid(null, null, null, null, null));

	}

	/**
	 * Test export Repast.sd regular.
	 */
	@Test
	public void testExportRepastSDRegular() {

		// Read in an example matrix for testing.
		MatrixModel matrixModel = MatrixModel.read(matrixEngine,
				UtilTest.INPUT_TEST_DIR + "//MatrixModel_12.xlsx");

		// Attempt to write out the matrix model.
		Assert.assertTrue(matrixModel
				.exportRepastSD(UtilTest.OUTPUT_TEST_DIR +
				"//MatrixModel_12.rsd"));

		// Compare the results.
		Assert.assertTrue(Util.compareBinaryFiles(
				UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_12.rsd",
				UtilTest.OUTPUT_TEST_DIR + "//MatrixModel_12_Test.rsd"));
	
	}
		
	/**
	 * Test export Repast.sd invalid.
	 */
	@Test
	public void testExportRepastSDInvalid() {


		// Check an invalid request.
		Assert.assertFalse(new MatrixModel(this.matrixEngine)
			.exportRepastSD(null, null, null,
			null, null));

	}

	/**
	 * Test regular node counting.
	 */
	@Test
	public void testNodeCountRegular() {

		// Read in an example matrix for testing.
		MatrixModel matrixModel = MatrixModel.read(matrixEngine,
				UtilTest.INPUT_TEST_DIR + "//MatrixModel_12.xlsx");

		// Check an assigned case.
		Assert.assertEquals(
				(Integer) (matrixModel.nodeCount()),
				(Integer) 5);

	}
	
	/**
	 * Test invalid node counting.
	 */
	@Test
	public void testNodeCountInvalid() {

		// Check a null case.
		Assert.assertEquals(
				(Integer) (new MatrixModel(matrixEngine).nodeCount()),
				(Integer) 0);

	}
	
	/**
	 * Test units.
	 */
	@Test
	public void testUnits() {
		
		// Setup a matrix engine and model.
		MatrixEngine matrixEngine = MatrixEngine
				.importTemplate(UtilTest.INPUT_TEST_DIR +
				"//MatrixEngineTemplate_10.xlsx");

		// Test units.
		MatrixModel matrixModel = matrixEngine.inputPopulation.get(0);
		
		// Test U.S. dollars (USD).
		Assert.assertEquals(matrixModel.get(0).units.getUnit(),
				Unit.valueOf("USD"));

		// Test euros (EUR).
		Assert.assertEquals(matrixModel.get(1).units.getUnit(),
				Unit.valueOf("EUR"));

		// Test kilograms (kg).
		Assert.assertEquals(matrixModel.get(2).units.getUnit(),
				Unit.valueOf("kg"));
		
		
	}

	/**
	 * Test fitness function type one.
	 */
	@Test
	public void testFitnessFunctionType1() {
		
		// Check type parsing.
		Assert.assertEquals(
				FITNESS_FUNCTION_TYPE.ZERO_FITNESS,
				MatrixModel.FITNESS_FUNCTION_TYPE.valueOf("ZERO_FITNESS"));
	}		
	
	/**
	 * Test fitness function type two.
	 */
	@Test
	public void testFitnessFunctionType2() {

		// Check type parsing.
		Assert.assertEquals(
				FITNESS_FUNCTION_TYPE.SIMPLE_MAXIMUM,
				MatrixModel.FITNESS_FUNCTION_TYPE.valueOf("SIMPLE_MAXIMUM"));
		
	}
	
	/**
	 * Test fitness function type three.
	 */
	@Test
	public void testFitnessFunctionType3() {

		// Check type parsing.
		Assert.assertEquals(
				FITNESS_FUNCTION_TYPE.SYSTEM_DYNAMICS,
				MatrixModel.FITNESS_FUNCTION_TYPE.valueOf("SYSTEM_DYNAMICS"));
		
	}

}
