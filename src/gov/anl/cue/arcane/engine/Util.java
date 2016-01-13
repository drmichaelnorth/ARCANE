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
package gov.anl.cue.arcane.engine;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * The Util class provides a set of utility methods.
 */
public class Util {

	/** The destination directory. */
	public static String SRC_GEN_DIR = UtilConstants.getString("Util.0");

	/** The input directory. */
	public static String INPUT_DIR = UtilConstants.getString("Util.1");

	/** The output directory. */
	public static String OUTPUT_DIR = UtilConstants.getString("Util.2");

	/** The test directory. */
	public static String TEST_DIR = UtilConstants.getString("Util.3");

	/** The test directory. */
	public static String TEMP_DIR = UtilConstants.getString("Util.4");

	/** The input directory. */
	public static String INPUT_SCAD_DIR = UtilConstants.getString("Util.5");

	/** The standard XStream driver. */
	public static final XStream XSTREAM_DRIVER = new XStream(new DomDriver());

	/**
	 * Compare binary files.
	 *
	 * @param fileName1
	 *            the file name1
	 * @param fileName2
	 *            the file name2
	 * @return true, if successful
	 */
	public static boolean compareBinaryFiles(String fileName1, String fileName2) {

		// Prepare the compare the given files.
		try {

			// Read the files.
			byte[] block1 = Files.readAllBytes(Paths.get(fileName1));
			byte[] block2 = Files.readAllBytes(Paths.get(fileName2));
			
			// Return the comparison results.
			return Arrays.equals(block1, block2);

		// Catch errors.
		} catch (Exception e) {

			// Note an error.
			return false;

		}

	}

	/**
	 * Deep copy.
	 *
	 * @param <S>
	 *            the generic type
	 * @param source
	 *            the source
	 * @return the results
	 */
	@SuppressWarnings("unchecked")
	public static <S> S deepCopy(S source) {
		
		// Return the results.
		return (S) XSTREAM_DRIVER.fromXML(XSTREAM_DRIVER.toXML(source));
		
	}

	/**
	 * Checks if is equal.
	 *
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 * @return true, if is equal
	 */
	public static boolean isEqual(Object left, Object right) {

		// Return the results.
		return (XSTREAM_DRIVER.toXML(left)
				.equals(XSTREAM_DRIVER.toXML(right)));

	}

	/**
	 * Read an XML file.
	 *
	 * @param fileName
	 *            the file name
	 * @return the object
	 */
	public static Object readXMLFile(String fileName) {

		// Return the results.
		return XSTREAM_DRIVER.fromXML(Paths.get(fileName).toFile());

	}

	/**
	 * Write xml file.
	 *
	 * @param fileName the file name
	 * @param input the input
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void writeXMLFile(String fileName, Object input)
			throws IOException {
		
		// Attempt to write out the given file.
		XSTREAM_DRIVER.toXML(input, new FileWriter(fileName));
		
	}
	
	/**
	 * Gets the spreadsheet string.
	 *
	 * @param sheet the sheet
	 * @param rowIndex the row index
	 * @param columnIndex the column index
	 * @return the spreadsheet string
	 */
	public static String getSpreadsheetString(XSSFSheet sheet, int rowIndex,
			int columnIndex) {
		
		// Prepare the results storage.
		String cellContents = "";
		
		// Get the next value.
		Cell cell = sheet.getRow(rowIndex)
				.getCell(columnIndex, Row.RETURN_BLANK_AS_NULL);

		// Check the next value.
		if (cell != null) {
			
			// Convert the next value, as needed.
			if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
				cellContents = cell.getStringCellValue();
			} else {
				cellContents = "" + cell.getNumericCellValue();
			}
			
		}
		
		// Return the results.
		return cellContents;
			
	}
	
	/**
	 * Gets the spreadsheet number.
	 *
	 * @param sheet the sheet
	 * @param rowIndex the row index
	 * @param columnIndex the column index
	 * @return the spreadsheet number
	 */
	public static double getSpreadsheetNumber(XSSFSheet sheet, int rowIndex,
			int columnIndex) {
		
		// Prepare the results storage.
		Double cellContents = Double.NaN;
		
		// Get the next value.
		Cell cell = sheet.getRow(rowIndex)
				.getCell(columnIndex, Row.RETURN_BLANK_AS_NULL);

		// Check the next value.
		if (cell != null) {
			
			// Convert the next value, as needed.
			if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
				cellContents = Double.parseDouble(
						cell.getStringCellValue());
			} else {
				cellContents = cell.getNumericCellValue();
			}
			
		}
		
		// Return the results.
		return cellContents;
			
	}
	
	/**
	 * Gets the spreadsheet boolean.
	 *
	 * @param sheet the sheet
	 * @param rowIndex the row index
	 * @param columnIndex the column index
	 * @return the spreadsheet boolean
	 */
	public static boolean getSpreadsheetBoolean(XSSFSheet sheet, int rowIndex,
			int columnIndex) {
				
		// Return the results.
		if (Util.getSpreadsheetString(sheet,
				rowIndex, columnIndex).trim().equals("Yes")) {
			return true;
		} else {
			return false;
		}
			
	}

	/**
	 * Simultaneous string replace.
	 *
	 * @param substitutions the substitutions
	 * @param inputString the input string
	 * @return the string
	 */
	public static String simultaneousStringReplace(Map<String, String> substitutions, String inputString) {
		
		// Setup the patterns to match.
		String patternString = "";
		for (String key : substitutions.keySet()) {
			
			// Use pipe separators.
			if (patternString.length() > 0) patternString += "|";
					
			// Add the next key to match.
			patternString += key;
			
		}

		// Setup the patter scanner.
		Pattern patternObject = Pattern.compile(patternString);
		
		// Setup the pattern matcher.
		Matcher matcher = patternObject.matcher(inputString);
		
		// Create the results storage.
		StringBuffer stringBuffer = new StringBuffer();

		// Simultaneously replace the given strings.
		while (matcher.find()) {
			
			// Complete the next set of replacements.
		    matcher.appendReplacement(stringBuffer,
		    		substitutions.get(matcher.group()));
		    
		}
		
		// Add the closing results.
		matcher.appendTail(stringBuffer);
		
		// Return the results after removing syntax escape characters.
		return stringBuffer.toString();

	}
	
	/**
	 * Simultaneous string replace and escape.
	 *
	 * @param substitutions the substitutions
	 * @param inputString the input string
	 * @return the string
	 */
	public static String simultaneousStringReplaceAndEscape(
			Map<String, String> substitutions, String inputString) {
		
		// Return the results after removing syntax escape characters.
		return Util.simultaneousStringReplace(
				substitutions, inputString).replace("$", "");
		
	}
	

}
