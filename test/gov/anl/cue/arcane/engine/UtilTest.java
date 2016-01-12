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
package gov.anl.cue.arcane.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

import gov.anl.cue.arcane.engine.Util;

/**
 * The UtilTest class is used to test the Util class.
 */
public class UtilTest {

	/** The test file name. */
	String testFileName = UtilTest.OUTPUT_TEST_DIR + "//String.xml";
	
	/** The input test binary directory. */
	public static String INPUT_TEST_BINARY_DIR = Util.INPUT_DIR
			+ "//test//binaryfiles";
	
	/** The input test directory. */
	public static String INPUT_TEST_DIR = Util.INPUT_DIR + "//test";

	/** The output test directory. */
	public static String OUTPUT_TEST_DIR = Util.OUTPUT_DIR + "//test";
	
	/** The first input scenario directory. */
	public static String INPUT_TEST_MATRIX_SCENARIO_1_DIR = INPUT_TEST_DIR
			+ "//matrixscenario_1";
	
	/** The second input scenario directory. */
	public static String INPUT_TEST_MATRIX_SCENARIO_2_DIR = INPUT_TEST_DIR
			+ "//matrixscenario_2";
	
	/** The third input scenario directory. */
	public static String INPUT_TEST_MATRIX_SCENARIO_3_DIR = INPUT_TEST_DIR
			+ "//matrixscenario_3";
	
	/** The fourth input scenario directory. */
	public static String INPUT_TEST_MATRIX_SCENARIO_4_DIR = INPUT_TEST_DIR
			+ "//matrixscenario_4";
	
	/** The first output scenario directory. */
	public static String OUTPUT_TEST_MATRIX_SCENARIO_1_DIR = OUTPUT_TEST_DIR
			+ "//matrixscenario_1";
	
	/**
	 * Instantiates a new utility test.
	 */
	public UtilTest() {
	}

	/**
	 * Test compare binary files.
	 */
	@Test
	public void testCompareBinaryFiles1() {

		// Run a test.
		Assert.assertTrue(Util.compareBinaryFiles(
				UtilTest.INPUT_TEST_BINARY_DIR + "//Test_1",
				UtilTest.INPUT_TEST_BINARY_DIR + "//Test_1"));
		
	}
	
	/**
	 * Test compare binary files.
	 */
	@Test
	public void testCompareBinaryFiles2() {

		// Run a test.
		Assert.assertTrue(Util.compareBinaryFiles(
				UtilTest.INPUT_TEST_BINARY_DIR + "//Test_1",
				UtilTest.INPUT_TEST_BINARY_DIR + "//Test_2"));

	}
	
	/**
	 * Test compare binary files.
	 */
	@Test
	public void testCompareBinaryFiles3() {

		// Run a test.
		Assert.assertFalse(Util.compareBinaryFiles(
				UtilTest.INPUT_TEST_BINARY_DIR + "//Test_1",
				UtilTest.INPUT_TEST_BINARY_DIR + "//Test_3"));
		
	}
	
	/**
	 * Test compare binary files.
	 */
	@Test
	public void testCompareBinaryFiles4() {

		// Run a test.
		Assert.assertFalse(Util.compareBinaryFiles(
				"NoSuchFile1",
				"NoSuchFile2"));

	}

	/**
	 * Test deep copy.
	 */
	@Test
	public void testDeepCopy1() {

		// Run a test.
		ArrayList<String> list1 = new ArrayList<String>();
		list1.add("1");
		list1.add("2");
		Assert.assertEquals(list1, Util.deepCopy(list1));

	}
	
	/**
	 * Test deep copy.
	 */
	@Test
	public void testDeepCopy2() {

		// Run a test.
		ArrayList<String> list1 = new ArrayList<String>();
		list1.add("1");
		list1.add("2");
		ArrayList<ArrayList<String>> list2 = new ArrayList<ArrayList<String>>();
		list2.add(list1);
		list2.add(Util.deepCopy(list1));
		Assert.assertEquals(list2, Util.deepCopy(list2));

	}

	/**
	 * Test get spreadsheet number.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testGetSpreadsheetNumber() throws Exception {

		// Attempt to open the spreadsheet.
		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(
				new File(Util.INPUT_DIR + "//" +
				Util.TEST_DIR + "//Test.xlsx")));

		// Prepare to access the sheets.
		Iterator<XSSFSheet> sheets = workbook.iterator();

		// Read the first sheet.
		XSSFSheet sheet = sheets.next();
		
		// Check the results.
		Assert.assertEquals((Double) Util.getSpreadsheetNumber(sheet, 0, 0),
				(Double) 1.0);
		Assert.assertEquals((Double) Util.getSpreadsheetNumber(sheet, 1, 0),
				(Double) 1.0);
		
		// Close the workbook.
		workbook.close();

	}

	/**
	 * Test get spreadsheet string.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testGetSpreadsheetString() throws Exception {

		// Attempt to open the spreadsheet.
		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(
				new File(Util.INPUT_DIR + "//" +
				Util.TEST_DIR + "//Test.xlsx")));

		// Prepare to access the sheets.
		Iterator<XSSFSheet> sheets = workbook.iterator();

		// Read the first sheet.
		XSSFSheet sheet = sheets.next();
		
		// Check the results.
		Assert.assertTrue(Util.getSpreadsheetString(sheet, 2, 0).equals(
				"Test text"));
		Assert.assertTrue(Util.getSpreadsheetString(sheet, 3, 0).equals(
				"Another string"));

		// Close the workbook.
		workbook.close();

	}

	/**
	 * Test get spreadsheet boolean.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testGetSpreadsheetBoolean() throws Exception {

		// Attempt to open the spreadsheet.
		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(
				new File(Util.INPUT_DIR + "//" +
				Util.TEST_DIR + "//Test.xlsx")));

		// Prepare to access the sheets.
		Iterator<XSSFSheet> sheets = workbook.iterator();

		// Read the first sheet.
		XSSFSheet sheet = sheets.next();
		
		// Check the results.
		Assert.assertTrue(Util.getSpreadsheetBoolean(sheet, 4, 0));
		Assert.assertFalse(Util.getSpreadsheetBoolean(sheet, 5, 0));

		// Close the workbook.
		workbook.close();

	}

	/**
	 * Test the constructor.
	 */
	@Test
	public void testUtil() {
		
		// Run a test.
		Assert.assertNotNull(new Util());
		
	}

	/**
	 * Test writing and reading arcane engine input.
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Test
	public void testWritingAndReadingarcaneEngineInput() throws IOException {

		// Run a test.
		String testObject = new String();
		Util.writeXMLFile(testFileName, testObject);
		String newCopy = (String) Util.readXMLFile(testFileName);
		Assert.assertEquals(testObject, newCopy);

	}

	/**
	 * Simultaneous string replace.
	 */
	@Test
	public void testSimultaneousStringReplace() {

		// Setup the substitution map.
		Map<String, String> substitutions = new HashMap<String, String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			{
				put("x", "y");
				put("y", "b");
				put("z", "x");
			}
		};

		// Declare the test string.
		String testString = "m x u y w z p";

		// Test string replacement.
		Assert.assertTrue(Util.simultaneousStringReplace(substitutions,
				testString).equals("m y u b w x p"));

	}

	/**
	 * Simultaneous string replace.
	 */
	@Test
	public void testSimultaneousStringReplaceAndEscape() {

		// Setup the substitution map.
		Map<String, String> substitutions = new HashMap<String, String>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			{
				put("xyx", "y");
				put("abc", "b");
				put("def", "x");
			}
		};

		// Declare the test string.
		String testString = "xyx def abc x$yx de$f ab$c 123 nhj";
		
		// Test string replacement.
		Assert.assertTrue(Util.simultaneousStringReplaceAndEscape(
				substitutions, testString)
				.equals("y x b xyx def abc 123 nhj"));

	}

	/**
	 * Test is equal.
	 */
	@Test
	public void testIsEqual() {

		// Run a test.
		ArrayList<String> list1 = new ArrayList<String>();
		list1.add("1");
		list1.add("2");
		ArrayList<String> list2 = new ArrayList<String>();
		list2.add("1");
		list2.add("2");
		Assert.assertTrue(Util.isEqual(list1, list2));

	}

}