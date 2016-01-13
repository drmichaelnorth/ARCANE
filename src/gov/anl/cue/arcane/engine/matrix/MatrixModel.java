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

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.beans.Introspector;
import java.beans.Transient;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jscience.economics.money.Currency;
import org.jscience.physics.amount.Amount;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import gov.anl.cue.arcane.engine.Util;

/**
 * The MatrixModel class supports the MatrixEngine class by
 * representing an example solution. Each MatrixModel instance
 * is an individual chromosome in the genetic algorithm. Each
 * MatrixModel instance contains a list of variables and a set
 * of fitness equations. Note that stocks are also called levels,
 * influence links are also called variables, and flows are also
 * called rates.
 */
public class MatrixModel extends ArrayList<MatrixVariable> implements Serializable {

	/** The JScience initializer. */
	static {
		
		// Pre-load the JScience units classes to
		// insure that they are ready when needed.
		Amount.valueOf(1.0, Currency.USD);
		
	}
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The matrix engine. */
	public transient MatrixEngine matrixEngine = null;
	
	/**
	 * Gets the matrix engine.
	 *
	 * @return the matrix engine
	 */
	@Transient
	public MatrixEngine getMatrixEngine() {
		
		// Return the results.
		return matrixEngine;
		
	}

	/**
	 * Instantiates a new matrix model.
	 *
	 * @param newMatrixEngine the new matrix engine
	 */
	public MatrixModel(MatrixEngine newMatrixEngine) {
		
		// Note our container.
		this.matrixEngine = newMatrixEngine;

	}

	/**
	 * Imports a matrix model from a template spreadsheet.
	 *
	 * @param matrixEngine the matrix engine
	 * @param fileName            the file name
	 * @return the results
	 */
	public static MatrixModel importTemplate(MatrixEngine matrixEngine, String fileName) {

		// Declare the results storage.
		MatrixModel matrixModel = new MatrixModel(matrixEngine);
		
		// Try to read the template spreadsheet.
		try {

			// Find the node request counts.
			HashMap<Integer, Integer> nodeCounts =
					MatrixModel.importTemplateDimensions(fileName);

			// Find the node base index counts.
			HashMap<Integer, Integer> nodeBases =
					MatrixModel.findNodeBases(nodeCounts);
	
			// Find the dimensions of the template spreadsheet.
			int nodeRequests = nodeCounts.size();
			int nodeCount = 0;
			for (Integer nodeRequest : nodeCounts.values()) {
				nodeCount += nodeRequest;
			}

			// Attempt to open the template spreadsheet.
			XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(
					new File(fileName)));

			// Extract the fitness function.
			Iterator<XSSFSheet> sheets = MatrixModel.
					importTemplateExtractFitnessInformation(
					matrixModel, nodeCounts, nodeRequests, workbook);

			// Scan the variables.
			MatrixModel.importTemplateScanVariables(
					matrixModel, nodeCounts, nodeBases,
					nodeRequests, nodeCount, sheets);
			
			// Normalize the new model.
			matrixModel.normalize();

		// Catch errors.
		} catch (Exception e) {

			// Note an error.
			matrixModel = null;

		}
		
		// Return the results.
		return matrixModel;

	}

	/**
	 * Import template extract fitness information.
	 *
	 * @param matrixModel the matrix model
	 * @param nodeCounts the node counts
	 * @param nodeRequests the node requests
	 * @param workbook the workbook
	 * @return the iterator
	 */
	public static Iterator<XSSFSheet> importTemplateExtractFitnessInformation(
			MatrixModel matrixModel, HashMap<Integer, Integer> nodeCounts,
			int nodeRequests, XSSFWorkbook workbook) {
		
		// Prepare to scan the sheets.
		Iterator<XSSFSheet> sheets = workbook.iterator();
		
		// Ignore the parameters sheet.
		XSSFSheet sheet = sheets.next();
		sheet = sheets.next();

		// Scan the fitness sheet.
		for (int rowIndex = 0; rowIndex < nodeRequests; rowIndex++) {

			// Get the next equation.
			String equation = Util.getSpreadsheetString(sheet, rowIndex + 1, 0);

			// Store the equation.
			for (int count = 0; count < nodeCounts.get(rowIndex); count++) {
				matrixModel.fitnessEquations.add(equation);
			}
			
			// Get the next node name.
			String nodeName = Util.getSpreadsheetString(sheet, rowIndex + 1, 1);

			// Store the node names.
			for (int count = 0; count < nodeCounts.get(rowIndex); count++) {
				matrixModel.nodeNames.add(nodeName + (count + 1));
			}

		}

		// Get the next fitness function specifier.
		String specifier = Util.getSpreadsheetString(sheet, nodeRequests + 1, 0);

		// Decode the fitness function specifier.
		if (specifier.equals(
				MatrixModel.ZERO_FITNESS_STRING)) {
			
			// Store the fitness function specifier.
			matrixModel.fitnessFunctionType =
				MatrixModel.FITNESS_FUNCTION_TYPE
				.ZERO_FITNESS;
			
		} else if (specifier.equals(
				MatrixModel.SIMPLE_MAXIMUM_STRING)) {

			// Store the fitness function specifier.
			matrixModel.fitnessFunctionType =
					MatrixModel.FITNESS_FUNCTION_TYPE
					.SIMPLE_MAXIMUM;
			
		} else {

			// Store the fitness function specifier.
			matrixModel.fitnessFunctionType =
					MatrixModel.FITNESS_FUNCTION_TYPE
					.SYSTEM_DYNAMICS;
			
			// Read the number of steps.
			matrixModel.stepCount = (int) Util
					.getSpreadsheetNumber(sheet,
					nodeRequests + 2, 1);
			
			// Read the step size.
			matrixModel.stepSize = Util
					.getSpreadsheetNumber(sheet,
					nodeRequests + 3, 1);
			
			// Read the step size.
			matrixModel.equationEvolution = Util
					.getSpreadsheetBoolean(sheet,
					nodeRequests + 4, 1);

		}
		
		// Return the results.
		return sheets;
		
	}

	/**
	 * Import template scan variables.
	 *
	 * @param matrixModel the matrix model
	 * @param nodeCounts the node counts
	 * @param nodeBases the node bases
	 * @param nodeRequests the node requests
	 * @param nodeCount the node count
	 * @param sheets the sheets
	 * @throws NotStrictlyPositiveException the not strictly positive exception
	 */
	public static void importTemplateScanVariables(MatrixModel matrixModel,
			HashMap<Integer, Integer> nodeCounts,
			HashMap<Integer, Integer> nodeBases, int nodeRequests,
			int nodeCount, Iterator<XSSFSheet> sheets)
			throws NotStrictlyPositiveException {
		
		// Scan the variables.
		XSSFSheet sheet;
		while (sheets.hasNext()) {

			// Move to the next sheet.
			sheet = sheets.next();

			// Allocate a new variable.
			MatrixVariable matrixVariable = new MatrixVariable();

			// Assign the new variables's name and, possibly, units.
			String newName = sheet.getSheetName();
			if (newName.contains("(")) {
				
				// Parse the name and units.
				matrixVariable.name = newName.substring(0,
						newName.indexOf("(") - 1);
				String unitsString = newName.substring(newName.indexOf("(") + 1,
						newName.indexOf(")"));
				
				// Attempt to convert the units text to a units value.
				try {

					// Convert the units text to a units value.
					matrixVariable.units = Amount.valueOf("1.0 " + unitsString);
	
				// Catch errors.
				} catch (Exception e) {
					
					// Use a default value.
					matrixVariable.units = Amount.ONE;
					
				}
				
			} else {
				
				// Assign the name.
				matrixVariable.name = newName;
				
			}

			// Scan the sheet for the next variable.
			for (int rowIndex = 0; rowIndex < nodeRequests; rowIndex++) {

				// Get the next equation.
				String equation = Util.getSpreadsheetString(sheet, rowIndex + 1, 0);

				// Store the equation.
				for (int count = 0; count < nodeCounts.get(rowIndex); count++)
					matrixVariable.equations.add(equation);

				// Check to make sure that the matrix is allocated.
				if (matrixVariable.coefficients == null) {
					
					// Allocate the coefficient matrix.
					matrixVariable.coefficients = new Array2DRowRealMatrix(
							nodeCount, nodeCount);
					
				}
				
				// Scan the columns for the next coefficient.
				for (int columnIndex = 0; columnIndex <
						nodeRequests; columnIndex++) {

					// Get the next coefficient.
					double fullValue = Util.getSpreadsheetNumber(sheet, rowIndex + 1,
							columnIndex + 2);
					
					// Store the next coefficients.
					MatrixModel.setTemplateEntry(nodeCounts, nodeBases,
							matrixVariable, rowIndex,
							columnIndex, fullValue);
					
				}

			}

			// Store the new variable.
			matrixModel.add(matrixVariable);

		}
		
	}

	/**
	 * Sets the template entry.
	 *
	 * @param nodeCounts the node counts
	 * @param nodeBases the node bases
	 * @param matrixVariable the matrix variable
	 * @param rowIndex the row index
	 * @param columnIndex the column index
	 * @param fullValue the full value
	 */
	public static void setTemplateEntry(HashMap<Integer, Integer> nodeCounts,
			HashMap<Integer, Integer> nodeBases,
			MatrixVariable matrixVariable, int rowIndex, int columnIndex,
			double fullValue) {
		
		// Scale the value to be set.
		double scaledValue = Double.NaN;
		int cellCount = nodeCounts.get(rowIndex) *
				nodeCounts.get(columnIndex);
		if (cellCount != 0) {
			scaledValue = fullValue / cellCount;
		}
		
		// Replace the selected entries.
		for (int rowCount = 0; rowCount < nodeCounts.get(rowIndex); rowCount++) {
			for (int columnCount = 0; columnCount < nodeCounts.get(columnIndex); columnCount++) {
				matrixVariable.coefficients.setEntry(
						nodeBases.get(rowIndex)    + rowCount,
						nodeBases.get(columnIndex) + columnCount,
						scaledValue);
			}
		}
		
	}	

	/**
	 * Find node bases.
	 *
	 * @param nodeCounts the node counts
	 * @return the hash map
	 */
	public static HashMap<Integer, Integer> findNodeBases(HashMap<Integer, Integer> nodeCounts) {

		// Create the results holder.
		HashMap<Integer, Integer> nodeBases = new HashMap<Integer, Integer>();
	
		// Determine the node bases.
		int lastNodeBase = 0;
		for (int nodeIndex = 0; nodeIndex < nodeCounts.size(); nodeIndex++) {
			
			// Store the next node base.
			nodeBases.put(nodeIndex, lastNodeBase);
			
			// Move on.
			lastNodeBase += nodeCounts.get(nodeIndex);
			
		}
		
		// Return the results.
		return nodeBases;
		
	}
	
	/**
	 * Imports the dimensions for a matrix model from a
	 * template spreadsheet.
	 *
	 * @param fileName            the file name
	 * @return the matrix dimensions
	 */
	public static HashMap<Integer, Integer> importTemplateDimensions(String fileName) {
		
		// Create the results holder.
		HashMap<Integer, Integer> nodeCounts = new HashMap<Integer, Integer>();
		
		// Try to read the spreadsheet.
		try {

			// Attempt to open the template spreadsheet.
			XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(
					new File(fileName)));

			// Scan the sheets.
			Iterator<XSSFSheet> sheets = workbook.iterator();

			// Ignore the first sheet.
			XSSFSheet sheet = sheets.next();
			sheet = sheets.next();

			// Prepare to scan the node count column.
			Iterator<Row> rowIterator = sheet.rowIterator();
			
			// Skip the header row.
			rowIterator.next();

			// Find the total number of nodes requested.
			int rowIndex = 0;
			Double nextCellValue = Util.getSpreadsheetNumber(
					sheet, rowIterator.next().getRowNum(), 2);
			while (!Double.isNaN(nextCellValue)) {
				
				// Store the results.
				nodeCounts.put(rowIndex++, nextCellValue.intValue());

				// Get the next node count.
				nextCellValue = Util.getSpreadsheetNumber(
						sheet, rowIterator.next().getRowNum(), 2);
					
			}
			
			// Close the workbook.
			workbook.close();
			
		// Catch errors.
		} catch (Exception e) {

			// Note an error.
			nodeCounts = null;

		}
		
		// Return the results.
		return nodeCounts;

	}

	/**
	 * Reads a matrix model from a spreadsheet.
	 *
	 * @param matrixEngine the matrix engine
	 * @param fileName            the file name
	 * @return the results
	 */
	public static MatrixModel read(MatrixEngine matrixEngine, String fileName) {

		// Declare the results storage.
		MatrixModel matrixModel = new MatrixModel(matrixEngine);
		
		// Find the dimensions of the spreadsheet.
		// This is necessary because Excel spreadsheets
		// do not reliably store the row and column dimension
		// in the meta-information. The values stored
		// there are not guaranteed to be correct in all cases.
		MatrixModel.MatrixDimensions matrixDimensions =
				MatrixModel.readDimensions(fileName);

		// Try to read the spreadsheet.
		try {

			// Attempt to open the spreadsheet.
			XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(
					new File(fileName)));

			// Extract the fitness information.
			Iterator<XSSFSheet> sheets = MatrixModel
					.readExtractFitnessInformation(
					matrixModel, matrixDimensions, workbook);

			// Scan the variables.
			MatrixModel.readScanVariables(
					matrixModel, matrixDimensions, sheets);

			// Catch errors.
		} catch (Exception e) {

			// Note an error.
			matrixModel = null;

		}
		
		// Return the results.
		return matrixModel;

	}

	/**
	 * Read scan variables.
	 *
	 * @param matrixModel the matrix model
	 * @param matrixDimensions the matrix dimensions
	 * @param sheets the sheets
	 * @throws NotStrictlyPositiveException the not strictly positive exception
	 * @throws OutOfRangeException the out of range exception
	 */
	public static void readScanVariables(MatrixModel matrixModel,
			MatrixModel.MatrixDimensions matrixDimensions,
			Iterator<XSSFSheet> sheets) throws NotStrictlyPositiveException,
			OutOfRangeException {
		
		// Scan the sheets.
		while (sheets.hasNext()) {

			// Move to the next sheet.
			XSSFSheet sheet = sheets.next();

			// Allocate a new variable.
			MatrixVariable matrixVariable = new MatrixVariable();

			// Assign the new variables's name and, possibly, units.
			String newName = sheet.getSheetName();
			if (newName.contains("(")) {
				
				// Parse the name and units.
				matrixVariable.name = newName.substring(0,
						newName.indexOf("(") - 1);
				String unitsString = newName.substring(newName.indexOf("(") + 1,
						newName.indexOf(")"));
				
				// Attempt to convert the units text to a units value.
				try {

					// Convert the units text to a units value.
					matrixVariable.units = Amount.valueOf("1.0 " + unitsString);
	
				// Catch errors.
				} catch (Exception e) {
					
					// Use a default value.
					matrixVariable.units = Amount.ONE;
					
				}
				
			} else {
				
				// Assign the name.
				matrixVariable.name = newName;
				
			}

			// Scan the sheet for the next variable.
			for (int rowIndex = 0; rowIndex < matrixDimensions.rows; rowIndex++) {

				// Get the next equation.
				String equation = Util.getSpreadsheetString(sheet, rowIndex + 1, 0);

				// Store the equation.
				matrixVariable.equations.add(equation);

				// Check to make sure that the matrix is allocated.
				if (matrixVariable.coefficients == null) {
					matrixVariable.coefficients = new Array2DRowRealMatrix(
							matrixDimensions.rows, matrixDimensions.columns);
				}
				
				// Scan the columns for the next coefficient.
				for (int columnIndex = 0; columnIndex <
						matrixDimensions.columns; columnIndex++) {

					// Get the next coefficient.
					double value = Util.getSpreadsheetNumber(sheet, rowIndex + 1,
							columnIndex + 2);
					
					// Store the next coefficient.
					matrixVariable.coefficients.setEntry(
							rowIndex, columnIndex, value);
					
				}

			}

			// Store the new variable.
			matrixModel.add(matrixVariable);

		}
		
	}

	/**
	 * Read extract fitness information.
	 *
	 * @param matrixModel the matrix model
	 * @param matrixDimensions the matrix dimensions
	 * @param workbook the workbook
	 * @return the iterator
	 */
	public static Iterator<XSSFSheet> readExtractFitnessInformation(
			MatrixModel matrixModel,
			MatrixModel.MatrixDimensions matrixDimensions, XSSFWorkbook workbook) {
		
		// Scan the sheets.
		Iterator<XSSFSheet> sheets = workbook.iterator();
		XSSFSheet sheet = sheets.next();

		// Scan the fitness sheet.
		for (int rowIndex = 0; rowIndex < matrixDimensions.rows; rowIndex++) {

			// Get the next equation.
			String equation = Util.getSpreadsheetString(sheet, rowIndex + 1, 0);

			// Store the equation.
			matrixModel.fitnessEquations.add(equation);
			
			// Get the next node name.
			String nodeName = Util.getSpreadsheetString(sheet, rowIndex + 1, 1);

			// Store the node names.
			matrixModel.nodeNames.add(nodeName);

		}

		// Get the next fitness function specifier.
		String specifier = Util.getSpreadsheetString(sheet, matrixDimensions.rows + 1, 0);

		// Decode the fitness function specifier.
		if (specifier.equals(
				MatrixModel.ZERO_FITNESS_STRING)) {
			
			// Store the fitness function specifier.
			matrixModel.fitnessFunctionType =
				MatrixModel.FITNESS_FUNCTION_TYPE
				.ZERO_FITNESS;
			
		} else if (specifier.equals(
				MatrixModel.SIMPLE_MAXIMUM_STRING)) {

			// Store the fitness function specifier.
			matrixModel.fitnessFunctionType =
					MatrixModel.FITNESS_FUNCTION_TYPE
					.SIMPLE_MAXIMUM;
			
		} else {

			// Store the fitness function specifier.
			matrixModel.fitnessFunctionType =
					MatrixModel.FITNESS_FUNCTION_TYPE
					.SYSTEM_DYNAMICS;
			
			// Read the number of steps.
			matrixModel.stepCount = (int) Util
					.getSpreadsheetNumber(sheet,
					matrixDimensions.rows + 2, 1);
			
			// Read the step size.
			matrixModel.stepSize = Util
					.getSpreadsheetNumber(sheet,
					matrixDimensions.rows + 3, 1);
			
		}
		
		// Return the results.
		return sheets;
		
	}

	/**
	 * Reads the dimensions for a matrix model from a spreadsheet.
	 * This method is necessary because Excel spreadsheets
	 * do not reliably store the row and column dimension
	 * in the meta-information. The values that are stored
	 * there are not guaranteed to be correct in all cases.
	 *
	 * @param fileName            the file name
	 * @return the matrix dimensions
	 */
	public static MatrixDimensions readDimensions(String fileName) {
		
		// Create the results holder.
		MatrixDimensions matrixDimensions = new MatrixModel.MatrixDimensions();

		// Try to read the spreadsheet.
		try {

			// Attempt to open the spreadsheet.
			XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(
					new File(fileName)));

			// Scan the sheets.
			Iterator<XSSFSheet> sheets = workbook.iterator();

			// Skip the first sheet.
			XSSFSheet sheet = sheets.next();

			// Move to the sheet for the first variable.
			sheet = sheets.next();
			
			// Find the number of rows.
			matrixDimensions.rows = sheet.getLastRowNum();

			// Prepare to check the first row.
			Iterator<Row> rowIterator = sheet.iterator();

			// Check the header row length
			Row row = rowIterator.next();
			matrixDimensions.columns = row.getLastCellNum() - 2;
			
			// Close the workbook.
			workbook.close();
			
		// Catch errors.
		} catch (Exception e) {

			// Note an error.
			matrixDimensions = null;

		}
		
		// Return the results.
		return matrixDimensions;

	}
	
	/** The fitness equations. */
	public ArrayList<String> fitnessEquations = new ArrayList<String>();

	/** The node names. */
	public ArrayList<String> nodeNames = new ArrayList<String>();

	/** The fitness value. */
	public Double fitnessValue = null;

	/** The current fitness function type. */
	public MatrixModel.FITNESS_FUNCTION_TYPE fitnessFunctionType = MatrixModel.FITNESS_FUNCTION_TYPE.ZERO_FITNESS;

	/** The step size. */
	public Double stepSize = 1.0;

	/** The step size. */
	public int stepCount = 10;
	
	/** The equation evolution flag. */
	public boolean equationEvolution = false;

	/** The Constant ABSTRACT_CLASS_NAME. */
	public static transient final String ABSTRACT_CLASS_NAME = MatrixModelConstants.getString("MatrixModel.0");
	
	/** The Constant CONCRETE_CLASS_NAME. */
	public static transient final String CONCRETE_CLASS_NAME = MatrixModelConstants.getString("MatrixModel.1");
	
	/** The Constant RSD_CLASS_NAME. */
	public static transient final String RSD_CLASS_NAME = MatrixModelConstants.getString("MatrixModel.14");
	
	/** The Constant PACKAGE_NAME. */
	public static transient final String PACKAGE_NAME = MatrixModelConstants.getString("MatrixModel.2");
	
	/** The Constant PACKAGE_PATH. */
	public static transient final String PACKAGE_PATH = MatrixModelConstants.getString("MatrixModel.3");
	
	/** The Constant ZERO_FITNESS_STRING. */
	public static transient final String ZERO_FITNESS_STRING = MatrixModelConstants.getString("MatrixModel.4");
	
	/** The Constant SIMPLE_MAXIMUM_STRING. */
	public static transient final String SIMPLE_MAXIMUM_STRING = MatrixModelConstants.getString("MatrixModel.5");
	
	/** The Constant SYSTEM_DYNAMICS_STRING. */
	public static transient final String SYSTEM_DYNAMICS_STRING = MatrixModelConstants.getString("MatrixModel.6");
	
	/** The Constant SYSTEM_DYNAMICS_STEP_COUNT_STRING. */
	public static transient final String SYSTEM_DYNAMICS_STEP_COUNT_STRING = MatrixModelConstants.getString("MatrixModel.7");
	
	/** The Constant SYSTEM_DYNAMICS_STEP_SIZE_STRING. */
	public static transient final String SYSTEM_DYNAMICS_STEP_SIZE_STRING = MatrixModelConstants.getString("MatrixModel.8");
	
	/** The Constant SYSTEM_DYNAMICS_GROW_STRING. */
	public static transient final String SYSTEM_DYNAMICS_GROW_STRING = MatrixModelConstants.getString("MatrixModel.19");
	
	/** The Constant SCAD_FILE_A. */
	public static transient final String SCAD_INPUT_FILE_A = MatrixModelConstants.getString("MatrixModel.9");
	
	/** The Constant SCAD_FILE_B. */
	public static transient final String SCAD_INPUT_FILE_B = MatrixModelConstants.getString("MatrixModel.10");
	
	/** The Constant SCAD_FILE_C. */
	public static transient final String SCAD_INPUT_FILE_C = MatrixModelConstants.getString("MatrixModel.11");
	
	/** The Constant SCAD_FILE_D. */
	public static transient final String SCAD_INPUT_FILE_D = MatrixModelConstants.getString("MatrixModel.12");

	/** The Constant COMBINED_SUFFIX. */
	public static transient final String COMBINED_SUFFIX = MatrixModelConstants.getString("MatrixModel.16");
	
	/** The Constant NO_SUFFIX. */
	public static transient final String NO_SUFFIX = MatrixModelConstants.getString("MatrixModel.17");

	/** The Constant SHORT_SUFFIX. */
	public static transient final String SHORT_SUFFIX = MatrixModelConstants.getString("MatrixModel.18");

	/**
	 * Calculate fitness value.
	 *
	 * @return the double
	 */
	public Double calculateFitnessValue() {

		// Declare the results holder.
		double fitnessAccumulator = 0.0;
		
		// Check the type of fitness function to use.
		if (this.fitnessFunctionType ==
				MatrixModel.FITNESS_FUNCTION_TYPE.ZERO_FITNESS) {
			
			// Return the default result.
			fitnessAccumulator = zeroFitnessFunction();
			
		} else if (this.fitnessFunctionType ==
				MatrixModel.FITNESS_FUNCTION_TYPE.SIMPLE_MAXIMUM) {

			// Calculate the simple maximum fitness function.
			fitnessAccumulator = simpleMaximumFitnessFunction();
	
		// System dynamics fitness functions are requested.
		} else {
				
			// Calculate the Repast system dynamics fitness function.
			fitnessAccumulator = systemDynamicsFitnessFunction();

		}

		// Return the results.
		return fitnessAccumulator;

	}
	
	/**
	 * The system dynamics fitness function.
	 *
	 * @return the double
	 */
	public double systemDynamicsFitnessFunction() {
		
		// Evaluate the equations.
		Set<String> knit = this.knit(MatrixModel.NO_SUFFIX, MatrixModel.COMBINED_SUFFIX);
		
		// Disperse the results of the equations.
		Set<String> split = this.split(MatrixModel.NO_SUFFIX, MatrixModel.COMBINED_SUFFIX);

		// Collect the fitness equation.
		String fitness = this.fitness(MatrixModel.NO_SUFFIX);
		
		// Extract the declarations from the new equations.
		Set<String> declarations = this.extractDeclarations(knit);
		
		// Generate the formulation.
		this.formulate(declarations, this.filterKnit(knit), split, fitness);

		// Run the formulation.
		return this.runMatrixModel();

	}

	/**
	 * Filter knit.
	 *
	 * @param knit the knit
	 * @return the sets the
	 */
	public Set<String> filterKnit(Set<String> knit) {
		
		// Declare the results holder.
		Set<String> filteredKnit = new TreeSet<String>();
		
		// Scan the default knit values.
		for (String line : knit) {
			
			// Check for stocks.
			if (line.contains("INTEG(")) {
				
				// Extract the equation.
				String calc = line.substring(0, line.indexOf(",")).trim();
				calc = calc.replaceFirst("=", "");
				calc = calc.replace("INTEG(", "+= ((");
				calc = calc + ") * this.stepSize)";
				
				// Store a filtered equation.
				filteredKnit.add(calc);

			// Check for initial values.
			} else if (line.contains("INITIAL(")) {
				
				// Extract the equation.
				String calc = line.substring(0, line.indexOf(",")).trim();
				calc = calc.replace("INITIAL(", "");
				
				// Store a filtered equation.
				filteredKnit.add(calc);
				
			// Process regular equations.
			} else {
				
				// Store a regular equation.
				filteredKnit.add(line);
				
			}
			
		}
		
		// Return the results.
		return filteredKnit;
		
	}
	
	/**
	 * Extract assignments.
	 *
	 * @param regularSuffix the regular suffix
	 * @param combinedSuffix the combined suffix
	 * @return the hash map
	 */
	public Set<String> knit(String regularSuffix, String combinedSuffix) {
		
		// Declare the storage.
		Set<String> knit = new TreeSet<String>();
		HashMap<String, String> substitutions =
				new HashMap<String, String>();
		String equation;
		
		// Scan the source nodes (i.e., rows).
		for (int source = 0; source < this.nodeCount(); source++) {

			// Prepare to make the next substitutions list.
			substitutions.clear();

			// Scan the variables.
			for (MatrixVariable matrixVariable : this) {

				// Extract the next variable.
				substitutions.put(matrixVariable.name,
						matrixVariable.name + MatrixModelConstants.getString("MatrixModel.20") +
						this.nodeName(source) +
						regularSuffix);

			}
			
			// Make substitutions, as required.
			for (MatrixVariable matrixVariable : this) {
				
				// Make the next substitution.
				equation = Util.simultaneousStringReplaceAndEscape(
						substitutions, matrixVariable.equations.get(source));

				// Check to see if the new equation is meaningful.
				if (equation.trim().length() > 0) {
					knit.add(matrixVariable.name + MatrixModelConstants.getString("MatrixModel.20") +
							this.nodeName(source) + combinedSuffix
							+ " = " + equation);
				}
				
			}
			
		}
	
		// Return the results.
		return knit;
	
	}

	/**
	 * Evaluate the equations.
	 *
	 * @param regularSuffix the regular suffix
	 * @param combinedSuffix the combined suffix
	 * @return the hash map
	 */
	public Set<String> split(String regularSuffix, String combinedSuffix) {
		
		// Declare the storage.
		Set<String> split = new TreeSet<String>();
		String equation;
		Double coefficient;
		
		// Make substitutions, as required.
		for (MatrixVariable matrixVariable : this) {
			
			// Scan the destination nodes (i.e., columns).
			for (int destination = 0; destination < this.nodeCount(); destination++) {

				// Prepare to assemble the next equation.
				equation = "";

				// Scan the source nodes (i.e., rows).
				for (int source = 0; source < this.nodeCount(); source++) {
					
					// Check to see if the next coefficient is defined.
					coefficient = matrixVariable.coefficients.getEntry(source, destination);
					if ((coefficient != 0.0) && !Double.isNaN(coefficient)) {
						
						// Add the next term.
						if (equation.length() > 0) equation += " + ";
						equation = equation + coefficient + " * " +
								matrixVariable.name + MatrixModelConstants.getString("MatrixModel.20") +
								this.nodeName(source ) + combinedSuffix;
						
					}
					
				}

				// Add the next equation, if appropriate.
				if (equation.length() > 0) {
					split.add(matrixVariable.name +
							MatrixModelConstants.getString("MatrixModel.20") +
							this.nodeName(destination) + 
							regularSuffix + 
							" = " + equation);
				}
				
			}
			
		}
	
		// Return the results.
		return split;
	
	}

	/**
	 * Fitness.
	 *
	 * @param regularSuffix the regular suffix
	 * @return the string
	 */
	public String fitness(String regularSuffix) {
		
		// Declare the storage.
		HashMap<String, String> substitutions =
				new HashMap<String, String>();
		String equation;
		
		// Start formulating the fitness equation.
		String fitness = "";
		
		// Scan the source nodes (i.e., rows).
		for (int source = 0; source < this.nodeCount(); source++) {

			// Prepare to make the next substitutions list.
			substitutions.clear();

			// Scan the variables.
			for (MatrixVariable matrixVariable : this) {

				// Extract the next variable.
				substitutions.put(matrixVariable.name,
						matrixVariable.name +
						MatrixModelConstants.getString("MatrixModel.20") +
						this.nodeName(source) +
						regularSuffix);

			}
				
			// Make the next substitution.
			equation = Util.simultaneousStringReplaceAndEscape(
					substitutions, this.fitnessEquations.get(source));
			
			// Check to see if the new equation is meaningful.
			if (equation.trim().length() > 0) {
				if (fitness.length() > 0) fitness += " + ";
				fitness = fitness + "("+ equation +")";
			}
			
		}
		
		// Return the result.
		return fitness;
		
	}
	
	/**
	 * Formulate.
	 *
	 * @param declarations the declarations
	 * @param knit the equations
	 * @param split the splitting factors
	 * @param fitness the fitness
	 * @return the matrix formulation result.
	 */
	public boolean formulate(Set<String> declarations, Set<String> knit,
			Set<String> split, String fitness) {
		
		// Attempt to create a new formulation class.
		try {
			
			// Attempt to write the Java file.
			this.formulateJava(declarations, knit, split, fitness);
			
			// Attempt to compile the Java file.
			this.formulateBytecode();
			
			// Return the results.
			return true;
			
		// Catch errors.
		} catch (Exception e) {
			
			// Return the results.
			return false;
			
		}
		
	}

	/**
	 * Formulate java.
	 *
	 * @param declarations the declarations
	 * @param knit the knit
	 * @param split the split
	 * @param fitness the fitness
	 * @throws FileNotFoundException the file not found exception
	 */
	public void formulateJava(Set<String> declarations, Set<String> knit,
			Set<String> split, String fitness) throws FileNotFoundException {
		
		// Attempt to create a new print writer.
		PrintWriter writer = new PrintWriter(new File(Util.TEMP_DIR + "//" +
				MatrixModel.PACKAGE_PATH +
				MatrixModel.CONCRETE_CLASS_NAME + ".java"));
		
		// Combine the formulation elements.
		writer.println("package " + MatrixModel.PACKAGE_NAME + ";");
		writer.println("");
		writer.println("import static java.lang.Math.*;");
		writer.println("");
		writer.println("public class " + MatrixModel.CONCRETE_CLASS_NAME + " extends " + MatrixModel.ABSTRACT_CLASS_NAME + " {");
		writer.println("");
		for (String line : declarations) writer.println("\t" + line + ";");
		writer.println("");
		writer.println("\tpublic " + MatrixModel.CONCRETE_CLASS_NAME + "(double newStepSize) {");
		writer.println("\t\tsuper(newStepSize);");
		writer.println("\t}");
		writer.println("");
		writer.println("\t@Override");
		writer.println("\tpublic void knit() {");
		writer.println("");
		for (String line : knit) writer.println("\t\t" + line + ";");
		writer.println("");
		writer.println("\t}");
		writer.println("");
		writer.println("\t@Override");
		writer.println("\tpublic void split() {");
		writer.println("");
		for (String line : split) writer.println("\t\t" + line + ";");
		writer.println("");
		writer.println("\t}");
		writer.println("");
		writer.println("\tpublic Double calculateFitnessValue() {");
		writer.println("");
		writer.println("\t\treturn " + fitness + ";");
		writer.println("");
		writer.println("\t}");
		writer.println("");
		writer.println("}");
		
		// Close the file.
		writer.close();
		
	}

	/**
	 * Formulate bytecode.
	 */
	public void formulateBytecode() {
		
		// Attempt to compile the file.
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		compiler.run(null, null, null,
				Util.TEMP_DIR + MatrixModel.PACKAGE_PATH +
				MatrixModel.CONCRETE_CLASS_NAME + ".java");
		
	}

	/**
	 * Run the matrix model.
	 *
	 * @return the double
	 */
	public Double runMatrixModel() {
		
		// Create a results holder.
		Double results = Double.NaN;
		
		// Attempt to run the model.
		try {
		
			// Setup a temporary class loader.
			URL[] urls = new URL[]{
					new File(Util.TEMP_DIR).toURI().toURL()
			};
			URLClassLoader classLoader = new URLClassLoader(urls, null, null);

			// Attempt to load the compiled file.
		    @SuppressWarnings("rawtypes")
			Constructor constructor = classLoader.loadClass(
					MatrixModel.PACKAGE_NAME + "." + MatrixModel.CONCRETE_CLASS_NAME)
				.getDeclaredConstructor(double.class);
		    constructor.setAccessible(true);
		    Object object = constructor.newInstance(this.stepSize);

			// Call "matrixFormulation.step(steps)".
			Method method = object.getClass()
					.getSuperclass().getMethod("step", int.class);
			method.invoke(object, this.stepCount);
			
			// Call matrixFormulation.calculateFitnessValue();
			method = object.getClass().getSuperclass()
					.getMethod("calculateFitnessValue");
			results = (Double) method.invoke(object);

			// Clear the given class loader, which should not be
			// a child of another class loader.
			object = null;
			method = null;
			classLoader.close();
			ResourceBundle.clearCache(classLoader);
			classLoader = null;
			Introspector.flushCaches();
			System.runFinalization();
			System.gc();
		
		// Catch exceptions.
		} catch (Exception e) {
			
			// Return the default result.
			results = Double.NaN;
			
		}
	
		// Return the results.
		return results;

	}

	/**
	 * Export an image.
	 *
	 * @param fileName the file name
	 * @param variables the variables
	 * @param knit the knit
	 * @param split the split
	 * @param fitness the fitness
	 * @return true, if successful
	 */
	public boolean exportImage(String fileName, Set<String> variables, Set<String> knit,
			Set<String> split, String fitness) {
		
		// Attempt to create a new formulation class.
		try {
			
	        // Create the graph.
	        Graph<String, String> graph = this.exportImageBuildGraph();
			
			// Draw the graph.
			JFrame frame = this.exportImageRenderGraph(graph);
			
			// Store the rendering frame.
			BufferedImage img = new BufferedImage(
					frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
			frame.getContentPane().paint(img.createGraphics());
			ImageIO.write(img, fileName.substring(
					fileName.length() - 3), new File(
					fileName));

		// Catch errors.
		} catch (Exception e) {
			
			// Note errors.
			return false;
			
		}
		
		// Return the default results.
		return true;
		
	}

	/**
	 * Export image render graph.
	 *
	 * @param graph the graph
	 * @return the j frame
	 * @throws HeadlessException the headless exception
	 */
	public JFrame exportImageRenderGraph(Graph<String, String> graph)
			throws HeadlessException {
		
		// Define the layout routine.
		Layout<String, String> layout = new CircleLayout<String, String>(graph);
		layout.setSize(new Dimension(700, 700));
		
		// Create the visualization server.
		BasicVisualizationServer<String, String> visualizationServer =
				new BasicVisualizationServer<String, String>(layout);
		visualizationServer.setPreferredSize(layout.getSize());
		
		// Setup the vertex labeler.
		visualizationServer.getRenderContext()
			.setVertexLabelTransformer(new ToStringLabeller<String>());
		
		// Setup the edge labeler.
		visualizationServer.getRenderContext()
			.setEdgeLabelTransformer(new ToStringLabeller<String>());
		visualizationServer.getRenderContext().setEdgeLabelTransformer(
			new Transformer<String, String>() {
				@Override
				public String transform(String string) {
					return StringUtils.substringBefore(string, "(");
				}
			}
		);
		
		// Setup the rendering frame.
		JFrame frame = new JFrame("System Dynamics Equations Graph"); 
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(visualizationServer);
		frame.pack();
		
		// Return the results.
		return frame;
		
	}

	/**
	 * Export image build graph.
	 *
	 * @return the graph
	 * @throws OutOfRangeException the out of range exception
	 */
	public Graph<String, String> exportImageBuildGraph() throws OutOfRangeException {
		
		// Create an empty graph.
        Graph<String, String> graph = new DirectedSparseMultigraph<String, String>();
        
		// Build the graph.
		for (MatrixVariable matrixVariable : this) {
			
			// Scan the source nodes (i.e., rows).
			for (int source = 0; source < this.nodeCount(); source++) {
				
				// Scan the destination nodes (i.e., columns).
				for (int destination = 0; destination < this.nodeCount(); destination++) {

					// Check to see if the next coefficient is defined.
					Double coefficient = matrixVariable.coefficients.getEntry(source, destination);
					if ((!Double.isNaN(coefficient)) && (coefficient != 0.0)) {
						
						// Add a link from the source to the destination.
						graph.addVertex(this.nodeName(source));
						graph.addVertex(this.nodeName(destination));
						graph.addEdge(("" +
								String.format("%10.3f", coefficient) +
								matrixVariable.name +
								"(" + this.nodeName(source) + ", " +
								")" + this.nodeName(destination)).trim(),
								this.nodeName(source),
								this.nodeName(destination));
						
					}
					
				}

			}
			
		}
		
		// Return the results.
		return graph;
		
	}

	/**
	 * Export a solid.
	 *
	 * @param fileName the file name
	 * @param variables the variables
	 * @param knit the knit
	 * @param split the split
	 * @param fitness the fitness
	 * @return true, if successful
	 */
	public boolean exportSolid(String fileName, Set<String> variables, Set<String> knit,
			Set<String> split, String fitness) {
		
		// Attempt to create a new formulation class.
		try {
			
			// Declare the graph storage.
	        Graph<String, String> graph = new DirectedSparseMultigraph<String, String>();
	        
	        // Build the graph.
	        int edgeCount = exportSolidBuildGraph(graph);
			
	        // Draw the graph.
			CircleLayout<String, String> layout = exportRenderGraph(graph);
			
			// Find the bounds storage.
			double minimum = Double.POSITIVE_INFINITY;
			double maximum = Double.NEGATIVE_INFINITY;
			
			// Find the bounds of the space.
			for (int source = 0; source < this.nodeCount(); source++) {
				double x = layout.getX("" + source);
				double y = layout.getY("" + source);
				minimum = Math.min(minimum, x);
				minimum = Math.min(minimum, y);
				maximum = Math.max(maximum, x);
				maximum = Math.max(maximum, y);
			}
			
			// Attempt to write out the SCAD file.
			this.exportSolidWriteSCAD(fileName, graph, edgeCount, layout, minimum,
					maximum);

		// Catch errors.
		} catch (Exception e) {
			
			// Note errors.
			return false;
			
		}
		
		// Return the default results.
		return true;
		
	}

	/**
	 * Export solid write scad.
	 *
	 * @param fileName the file name
	 * @param graph the graph
	 * @param edgeCount the edge count
	 * @param layout the layout
	 * @param minimum the minimum
	 * @param maximum the maximum
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void exportSolidWriteSCAD(String fileName,
			Graph<String, String> graph, int edgeCount,
			CircleLayout<String, String> layout, double minimum,
			double maximum) throws FileNotFoundException, IOException {
		
		// Attempt to create a new print writer.
		PrintWriter writer = new PrintWriter(new File(fileName)); 
		
		// Write out the node coordinates.
		this.exportSolidWriteSCADNodeCoordinates(
				layout, minimum, maximum, writer);
		
		// Write out the node identifiers.
		this.exportSolidWriteSCADNodeIdentifiers(writer);
		
		// Write out the edge identifiers.
		this.exportSolidWriteSCADEdgeIdentifiers(
				graph, edgeCount, writer);
		
		// Close the file.
		writer.close();
		
	}

	/**
	 * Export solid write scad edge identifiers.
	 *
	 * @param graph the graph
	 * @param edgeCount the edge count
	 * @param writer the writer
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void exportSolidWriteSCADEdgeIdentifiers(
			Graph<String, String> graph, int edgeCount,
			PrintWriter writer) throws IOException {
		
		// Read the boilerplate SCAD segment.
		List<String> c = Files.readAllLines(
				Paths.get(Util.INPUT_DIR + "//" +
				Util.INPUT_SCAD_DIR + 
				"//" + MatrixModel.SCAD_INPUT_FILE_C));
		// Read the boilerplate SCAD segments.
		List<String> d = Files.readAllLines(Paths.get(
				Util.INPUT_DIR + "//" +
				Util.INPUT_SCAD_DIR +
				"//" + MatrixModel.SCAD_INPUT_FILE_D));
		
		// Write out a boilerplate SCAD segment.
		for (String line : c) writer.println(line);

		// Write out the edge identifiers.
		for (int edgeCounter = 1; edgeCounter <= edgeCount; edgeCounter++) {
			Pair<String> pair = graph.getEndpoints("" + edgeCounter);
			writer.print("[" + edgeCounter + ", " +
					pair.getFirst() + ", " +
					pair.getSecond() + "]");
			if (edgeCounter < edgeCount) {
				writer.println(", ");
			} else {
				writer.println("");
			}
		}
		for (String line : d) writer.println(line);
		
	}

	/**
	 * Export solid write scad node identifiers.
	 *
	 * @param writer the writer
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void exportSolidWriteSCADNodeIdentifiers(
			PrintWriter writer) throws IOException {
		
		// Read the boilerplate SCAD segment.
		List<String> b = Files.readAllLines(Paths.get(
				Util.INPUT_DIR + "//" +
				Util.INPUT_SCAD_DIR + 
				"//" + MatrixModel.SCAD_INPUT_FILE_B));

		// Write out a boilerplate SCAD segment.
		for (String line : b) writer.println(line);
		
		// Write out the node identifiers.
		for (int source = 0; source < this.nodeCount(); source++) {
			writer.print("[" + source + ", " +
					source + "]");
			if (source < (this.nodeCount() - 1)) {
				writer.println(", ");
			} else {
				writer.println("");
			}
		}
		
	}

	/**
	 * Export solid write scad node coordinates.
	 *
	 * @param layout the layout
	 * @param minimum the minimum
	 * @param maximum the maximum
	 * @param writer the writer
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void exportSolidWriteSCADNodeCoordinates(
			CircleLayout<String, String> layout, double minimum,
			double maximum, PrintWriter writer) throws IOException {

		// Read the boilerplate SCAD segment.
		List<String> a = Files.readAllLines(
				Paths.get(Util.INPUT_DIR + "//" +
				Util.INPUT_SCAD_DIR +  "//" +
				MatrixModel.SCAD_INPUT_FILE_A));

		// Write out a boilerplate SCAD segment.
		for (String line : a) writer.println(line);

		// Write out the node coordinates.
		for (int source = 0; source < this.nodeCount(); source++) {
			writer.print("[" + 
					layout.getX("" + source) + ", " +
					layout.getY("" + source) + ", " +
					this.getMatrixEngine().getRandomNumberFromTo(minimum, maximum) + "]");
			if (source < (this.nodeCount() - 1)) {
				writer.println(", ");
			} else {
				writer.println("");
			}
		}
		
	}

	/**
	 * Export render graph.
	 *
	 * @param graph the graph
	 * @return the circle layout
	 * @throws HeadlessException the headless exception
	 */
	public CircleLayout<String, String> exportRenderGraph(
			Graph<String, String> graph) throws HeadlessException {
		
		// Define the layout routine.
		CircleLayout<String, String> layout = new CircleLayout<String, String>(graph);
		layout.setSize(new Dimension(700, 700));
		
		// Create the visualization server.
		BasicVisualizationServer<String, String> visualizationServer =
				new BasicVisualizationServer<String, String>(layout);
		visualizationServer.setPreferredSize(layout.getSize());
		
		// Setup the rendering frame.
		JFrame frame = new JFrame("System Dynamics Equations Graph");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(visualizationServer);
		frame.pack();
		
		// Return the results.
		return layout;
		
	}

	/**
	 * Export solid build graph.
	 *
	 * @param graph the graph
	 * @return the int
	 * @throws OutOfRangeException the out of range exception
	 */
	public int exportSolidBuildGraph(Graph<String, String> graph)
			throws OutOfRangeException {
		
		// Declare the edge counter.
		int edgeCount = 0;
		
		// Build the graph.
		for (MatrixVariable matrixVariable : this) {
			
			// Scan the source nodes (i.e., rows).
			for (int source = 0; source < this.nodeCount(); source++) {
				
				// Scan the destination nodes (i.e., columns).
				for (int destination = 0; destination < this.nodeCount(); destination++) {

					// Check to see if the next coefficient is defined.
					Double coefficient = matrixVariable.coefficients.getEntry(source, destination);
					if ((!Double.isNaN(coefficient)) && (coefficient != 0.0)) {
						
						// Add a link from the source to the destination.
						graph.addVertex("" + source);
						graph.addVertex("" + destination);
						graph.addEdge("" + (++edgeCount),
								"" + source,
								"" + destination);
						
					}
					
				}

			}
			
		}
		
		// Return the results.
		return edgeCount;

	}

	/**
	 * Export into Repast system dynamics format.
	 *
	 * @param fileName the file name
	 * @param variables the variables
	 * @param knit the knit
	 * @param split the split
	 * @param fitness the fitness
	 * @return true, if successful
	 */
	public boolean exportRepastSD(String fileName, Set<String> variables, Set<String> knit,
			Set<String> split, String fitness) {
		
		// Attempt to create a new formulation class.
		try {
			
			// Attempt to create a new print writer.
			PrintWriter writer = new PrintWriter(new File(fileName)); 
			
			// Write out the main header.
			this.exportRepastSDMainHeader(writer);
			
			// Store the system model information opening.
			this.exportRepastSDSystemModelOpening(writer);
			
			// Merge the split into the knit.
			Set<String> newKnits = this.exportRepastSDMergeSplitIntoKnit(knit, split);

			// Build a graph.
			Graph<String, String> graph = this.exportRepastSDBuildGraph(variables,
					newKnits);

			// Render the graph.
	        CircleLayout<String, String> layout = this.exportRenderGraph(graph);
			
			// Define the stock tracking list.
			Set<String> stocks = new TreeSet<String>();
			
	        // Add vertices to the output.
	        this.exportRepastSDAddVerticies(writer, newKnits, stocks);
	        
	        // Define the influence link tracking list.
	        Set<String> influenceLinks = new TreeSet<String>();
			
	        // Write the rates and influence links.
			this.exportRepastSDRatesAndInfluenceLinks(
					writer, graph, stocks, influenceLinks);
		
			// Store the system model closing.
			writer.print("  </SystemModel>");
			writer.println();
			
			// Store the diagram data.
			this.exportRepastSDStoreDiagram(writer, newKnits, graph, layout,
					influenceLinks);

			// Store the header closing.
			writer.print("</xmi:XMI>");
			writer.println();
			
			// Close the file.
			writer.close();	

		// Catch errors.
		} catch (Exception e) {
			
			// Note errors.
			return false;
			
		}
		
		// Return the default results.
		return true;
		
	}

	/**
	 * Export Repast SD system model opening.
	 *
	 * @param writer the writer
	 */
	public void exportRepastSDSystemModelOpening(PrintWriter writer) {
		
		// Store the system model information opening.
		writer.print("    <SystemModel xmi:id=\"__VZtwGkfEeSjsq7j1gAbXg\" endTime=\"" + this.stepCount + "\" ");
		writer.print("timeStep=\"" + this.stepSize + "\" units=\"\" ");
		writer.print("reportingInterval=\"1.0\" ");
		writer.print("className=\"" + MatrixModel.RSD_CLASS_NAME + "\" package=\"" + MatrixModel.PACKAGE_NAME + "\">");
		writer.println();
		
	}

	/**
	 * Export Repast SD store diagram.
	 *
	 * @param writer the writer
	 * @param newKnits the new knits
	 * @param graph the graph
	 * @param layout the layout
	 * @param influenceLinks the influence links
	 */
	public void exportRepastSDStoreDiagram(PrintWriter writer,
			Set<String> newKnits, Graph<String, String> graph,
			CircleLayout<String, String> layout, Set<String> influenceLinks) {
		
		// Store the diagram opening.
		this.exportRepastSDStoreDiagramHeader(writer);

		// Note the item counter.
		int counter = 0;

		// Note the shape identifier for each variable.
		HashMap<String, Integer> shapeIDs = new HashMap<String, Integer>();
		
		// Store the diagram nodes.
		counter = this.exportRepastSDStoreDiagramStoreNodes(writer, newKnits,
				layout, counter, shapeIDs);
		
		// Store a diagram style note.
		writer.print("    <styles xmi:type=\"notation:DiagramStyle\" xmi:id=\"" + (counter++) + "\"/>");
		writer.println();
				
		// Store the diagram edges.
		for (String edge : graph.getEdges()) {
			
			// Output the next edge.
			counter = this.exportRepastSDStoreDiagramEdge(writer, graph,
					influenceLinks, counter, shapeIDs, edge);
					
		}

		// Store the diagram closing.
		writer.print("  </notation:Diagram>");
		writer.println();
		
	}

	/**
	 * Export Repast SD store diagram store nodes.
	 *
	 * @param writer the writer
	 * @param newKnits the new knits
	 * @param layout the layout
	 * @param counter the counter
	 * @param shapeIDs the shape i ds
	 * @return the int
	 */
	public int exportRepastSDStoreDiagramStoreNodes(PrintWriter writer,
			Set<String> newKnits, CircleLayout<String, String> layout,
			int counter, HashMap<String, Integer> shapeIDs) {
		
		// Store the diagram nodes. Note the following codes:
		//
		//     2003 Stock (5002 Nested Inside)
		//     2004 Variable (5003 Nested Inside)
		//
		for (String equation : newKnits) {
			
			// Extract the components of the next equation.
			String leftHandSide  = equation.substring(0, equation.indexOf("=") - 1).trim();
			String rightHandSide = equation.substring(equation.indexOf("=") + 2).trim();
			
			// Get the variable's layout location.
			int x = (int) Math.round(layout.getX(leftHandSide));
			int y = (int) Math.round(layout.getY(leftHandSide));

			// Note the shape identifier.
			shapeIDs.put(leftHandSide, counter);
		
			// Output the next node.
			counter = this.exportRepastSDStoreDiagramNode(writer, counter,
					leftHandSide, rightHandSide, x, y);

		}
		
		// Return the results.
		return counter;
		
	}

	/**
	 * Export Repast SD store diagram edge.
	 *
	 * @param writer the writer
	 * @param graph the graph
	 * @param influenceLinks the influence links
	 * @param counter the counter
	 * @param shapeIDs the shape i ds
	 * @param edge the edge
	 * @return the int
	 */
	public int exportRepastSDStoreDiagramEdge(PrintWriter writer,
			Graph<String, String> graph, Set<String> influenceLinks,
			int counter, HashMap<String, Integer> shapeIDs, String edge) {
		
		// Check for influence links.
		if (influenceLinks.contains(edge)) {

			// Store the next influence link.
			counter = this.exportRepastSDStoreDiagramEdgeInfluenceLink(writer,
					graph, counter, shapeIDs, edge);
		
		} else {
			
			// Store the next rate edge.
			counter = this.exportRepastSDStoreDiagramEdgeRate(writer, graph,
					counter, shapeIDs, edge);
		
		}
		
		// Return the results.
		return counter;
		
	}

	/**
	 * Export repast sd store diagram edge rate.
	 *
	 * @param writer the writer
	 * @param graph the graph
	 * @param counter the counter
	 * @param shapeIDs the shape i ds
	 * @param edge the edge
	 * @return the int
	 */
	public int exportRepastSDStoreDiagramEdgeRate(PrintWriter writer,
			Graph<String, String> graph, int counter,
			HashMap<String, Integer> shapeIDs, String edge) {
		
		// Store the next rate edge.
		writer.print("    <edges xmi:type=\"notation:Edge\" xmi:id=\"" + (counter++));
		writer.print("\" type=\"4003\" element=\"" + edge);
		writer.print("\" source=\"" + shapeIDs.get(graph.getSource(edge))
				+ "\" target=\"" + shapeIDs.get(graph.getDest(edge)) + "\">");
		writer.println();
		writer.print("      <children xmi:type=\"notation:DecorationNode\" xmi:id=\"" + (counter++) + "\" type=\"6001\">");
		writer.println();
		writer.print("        <layoutConstraint xmi:type=\"notation:Location\" xmi:id=\"" + (counter++) + "\" x=\"" + 12 + "\" y=\"" + 12 + "\"/>");
		writer.println();
		writer.print("      </children>");
		writer.println();
		writer.print("      <styles xmi:type=\"notation:RoutingStyle\" xmi:id=\"" + (counter++) + "\"/>");
		writer.println();
		writer.print("      <styles xmi:type=\"notation:FontStyle\" xmi:id=\"" + (counter++) + "\" fontName=\"Lucida Grande\" fontHeight=\"12\"/>");
		writer.println();
		writer.print("      <bendpoints xmi:type=\"notation:RelativeBendpoints\" xmi:id=\"" + (counter++) + "\" points=\"[0, 0, 0, -75]$[0, 75, 0, 0]\"/>");
		writer.println();
		writer.print("      <sourceAnchor xmi:type=\"notation:IdentityAnchor\" xmi:id=\"" + (counter++) + "\" id=\"(0.5,1.0)\"/>");
		writer.println();
		writer.print("      <targetAnchor xmi:type=\"notation:IdentityAnchor\" xmi:id=\"" + (counter++) + "\" id=\"(0.5,0.0)\"/>");
		writer.println();
		writer.print("    </edges>");
		writer.println();

		// Return the results.
		return counter;

	}

	/**
	 * Export repast sd store diagram edge influence link.
	 *
	 * @param writer the writer
	 * @param graph the graph
	 * @param counter the counter
	 * @param shapeIDs the shape i ds
	 * @param edge the edge
	 * @return the int
	 */
	public int exportRepastSDStoreDiagramEdgeInfluenceLink(PrintWriter writer,
			Graph<String, String> graph, int counter,
			HashMap<String, Integer> shapeIDs, String edge) {
		
		// Store the next influence link.
		writer.print("    <edges xmi:type=\"notation:Edge\" xmi:id=\"" + (counter++));
		writer.print("\" type=\"4004\" element=\"" + edge);
		writer.print("\" source=\"" + shapeIDs.get(graph.getSource(edge))
				+ "\" target=\"" + shapeIDs.get(graph.getDest(edge)) + "\">");
		writer.println();
		writer.print("      <styles xmi:type=\"notation:RoutingStyle\" xmi:id=\"" + (counter++) + "\" smoothness=\"Normal\"/>");
		writer.println();
		writer.print("      <styles xmi:type=\"notation:FontStyle\" xmi:id=\"" + (counter++) + "\" fontName=\"Lucida Grande\" fontHeight=\"12\"/>");
		writer.println();
		writer.print("      <bendpoints xmi:type=\"notation:RelativeBendpoints\" xmi:id=\"" + (counter++) + "\" points=\"[0, 0, 0, -75]$[0, 75, 0, 0]\"/>");
		writer.println();
		writer.print("      <sourceAnchor xmi:type=\"notation:IdentityAnchor\" xmi:id=\"" + (counter++) + "\" id=\"(0.5,1.0)\"/>");
		writer.println();
		writer.print("      <targetAnchor xmi:type=\"notation:IdentityAnchor\" xmi:id=\"" + (counter++) + "\" id=\"(0.5,0.0)\"/>");
		writer.println();
		writer.print("    </edges>");
		writer.println();

		// Return the results.
		return counter;

	}

	/**
	 * Export Repast SD store diagram node.
	 *
	 * @param writer the writer
	 * @param counter the counter
	 * @param leftHandSide the left hand side
	 * @param rightHandSide the right hand side
	 * @param x the x
	 * @param y the y
	 * @return the int
	 */
	public int exportRepastSDStoreDiagramNode(PrintWriter writer, int counter,
			String leftHandSide, String rightHandSide, int x, int y) {
		
		// Check the next equation.
		if (rightHandSide.contains("INTEG(")) {
			
			// Store a stock diagram element.			
			writer.print("    <children xmi:type=\"notation:Shape\" xmi:id=\"" + (counter++) + "\" ");
			writer.print("type=\"2003\" element=\"" + leftHandSide + "\" fontName=\"Lucida Grande\" fontHeight=\"12\">");
			writer.println();
			writer.print("      <children xmi:type=\"notation:DecorationNode\" xmi:id=\"" + (counter++) + "\" type=\"5002\"/>");
			writer.println();
			writer.print("        <layoutConstraint xmi:type=\"notation:Bounds\" xmi:id=\"" + (counter++) + "\"");
			writer.print(" x=\"" + x + "\" y=\"" + y + "\"/>");
			writer.println();
			writer.print("    </children>");
			writer.println();

		} else {

			// Store a variable diagram element.			
			writer.print("    <children xmi:type=\"notation:Shape\" xmi:id=\"" + (counter++) + "\" ");
			writer.print("type=\"2004\" element=\"" +leftHandSide + "\" fontName=\"Lucida Grande\" fontHeight=\"12\">");
			writer.println();
			writer.print("      <children xmi:type=\"notation:DecorationNode\" xmi:id=\"" + (counter++) + "\" type=\"5003\"/>");
			writer.println();
			writer.print("        <layoutConstraint xmi:type=\"notation:Bounds\" xmi:id=\"" + (counter++) + "\"");
			writer.print(" x=\"" + x + "\" y=\"" + y + "\"/>");
			writer.println();
			writer.print("    </children>");
			writer.println();

		}
		
		// Return the results.
		return counter;
		
	}

	/**
	 * Export Repast SD store diagram header.
	 *
	 * @param writer the writer
	 */
	public void exportRepastSDStoreDiagramHeader(PrintWriter writer) {
		
		// Store the diagram opening.
		writer.print("  <notation:Diagram xmi:id=\"_A__0\" ");
		writer.print("type=\"Systemdynamics\" ");
		writer.print("element=\"__VZtwGkfEeSjsq7j1gAbXg\" ");
		writer.print("name=\"" + MatrixModel.RSD_CLASS_NAME + "\" ");
		writer.print("measurementUnit=\"Pixel\">");
		writer.println();
		
	}

	/**
	 * Export Repast SD rates and influence links.
	 *
	 * @param writer the writer
	 * @param graph the graph
	 * @param stocks the stocks
	 * @param influenceLinks the influence links
	 */
	public void exportRepastSDRatesAndInfluenceLinks(PrintWriter writer,
			Graph<String, String> graph, Set<String> stocks,
			Set<String> influenceLinks) {
		
		// Store the diagram rates and influence links.
		for (String edge : graph.getEdges()) {

			// Check the end points.
			if (stocks.contains(graph.getSource(edge)) &&
				stocks.contains(graph.getDest(edge))) {
			
				// Store a rate.
				writer.print("      <variables xmi:type=\"Rate\" ");
				writer.print("xmi:id=\"" + edge + "\" ");
				writer.print("uuid=\"" + edge + "\" ");
				writer.print("name=\"" + edge + "\" ");
				writer.print("equation=\"" + graph.getDest(edge) + "\" ");
				writer.print("type=\"rate\" ");
				writer.print("lhs=\"" + graph.getDest(edge) + "\" ");
				writer.print("comment=\"\" ");
				writer.print("units=\"\" ");
				writer.print("from=\"" + graph.getSource(edge) + "\" ");
				writer.print("to=\"" + graph.getDest(edge) + "\"/>");
				writer.println();

			} else {
			
				// Store an influence link.
				writer.print("      <links xmi:type=\"InfluenceLink\" ");
				writer.print("xmi:id=\"" + edge + "\" ");
				writer.print("from=\"" + graph.getSource(edge) + "\" ");
				writer.print("to=\"" + graph.getDest(edge) + "\"/>");
				writer.println();
				
				// Note the influence link.
				influenceLinks.add(edge);
				
			}
				
		}
		
	}

	/**
	 * Export Repast SD add verticies.
	 *
	 * @param writer the writer
	 * @param newKnits the new knits
	 * @param stocks the stocks
	 */
	public void exportRepastSDAddVerticies(PrintWriter writer,
			Set<String> newKnits, Set<String> stocks) {
		
		// Add vertices to the output.
		for (String equation : newKnits) {
			
			// Extract the components of the next equation.
			String leftHandSide  = equation.substring(0, equation.indexOf("=") - 1).trim();
			String rightHandSide = equation.substring(equation.indexOf("=") + 2).trim();

			// Check for stocks.
			if (rightHandSide.contains("INTEG(")) {
				
				// Extract the equation.
				String calc = rightHandSide.substring(0, rightHandSide.indexOf(",")).trim();
				calc = calc.replace("INTEG(", "");
					
				// Extract the initial value.
				String initialValue = rightHandSide.substring(rightHandSide.indexOf(",") + 1).trim();
				initialValue = initialValue.substring(0, initialValue.length() - 1);
				
				// Store a stock.
				writer.print("      <variables xmi:type=\"Stock\" ");
				writer.print("xmi:id=\"" + leftHandSide + "\" ");
				writer.print("uuid=\"" + leftHandSide + "\" ");
				writer.print("name=\"" + leftHandSide + "\" ");
				writer.print("lhs=\"" + leftHandSide + "\" ");
				writer.print("type=\"stock\" ");
				writer.print("comment=\"" + leftHandSide + "\" ");
				writer.print("units=\"\" ");
				writer.print("equation=\"" + calc + "\" ");
				writer.print("initialValue=\"" + initialValue + "\"/>");
				writer.println();
				
				// Note the stock.
				stocks.add(leftHandSide);

			// Check for initial values.
			} else if (rightHandSide.contains("INITIAL(")) {
				
				// Extract the equation.
				String calc = rightHandSide.substring(0, rightHandSide.indexOf(",")).trim();
				calc = calc.replace("INITIAL(", "");
					
				// Store a variable.
				writer.print("      <variables xmi:type=\"Variable\" ");
				writer.print("xmi:id=\"" + leftHandSide + "\" ");
				writer.print("uuid=\"" + leftHandSide + "\" ");
				writer.print("name=\"" + leftHandSide + "\" ");
				writer.print("type=\"auxiliary\" ");
				writer.print("equation=\"" + calc + "\" ");
				writer.print("lhs=\"" + leftHandSide + "\"/>");
				writer.println();

			} else {

				// Store a variable.
				writer.print("      <variables xmi:type=\"Variable\" ");
				writer.print("xmi:id=\"" + leftHandSide + "\" ");
				writer.print("uuid=\"" + leftHandSide + "\" ");
				writer.print("name=\"" + leftHandSide + "\" ");
				writer.print("type=\"auxiliary\" ");
				writer.print("equation=\"" + rightHandSide + "\" ");
				writer.print("lhs=\"" + leftHandSide + "\"/>");
				writer.println();
			
			}	

		}
		
	}

	/**
	 * Export Repast SD build graph.
	 *
	 * @param variables the variables
	 * @param newKnits the new knits
	 * @return the graph
	 */
	public Graph<String, String> exportRepastSDBuildGraph(
			Set<String> variables, Set<String> newKnits) {
		
		// Declare the graph storage.
		Graph<String, String> graph = new DirectedSparseMultigraph<String, String>();
		
		// Add vertices to the graph.
		for (String variable : variables) {
			
			// Add the next vertex.
			graph.addVertex(variable);
			
		}
		
		// Add edges to the graph.
		for (String equation : newKnits) {
			
			// Extract the components of the next equation.
			String leftHandSide  = equation.substring(0, equation.indexOf("=") - 1).trim();
			String rightHandSide = equation.substring(equation.indexOf("=") + 2).trim();

		    // Add vertices to the graph.
		    for (String variable : variables) {
		    	
		    	// Check the next variable.
		    	if (!leftHandSide.equals(variable)
		    			&& rightHandSide.contains(variable)) {
		    	
		        	// Add the next vertex.
		        	graph.addEdge(leftHandSide + " to " + variable,
		        			variable, leftHandSide);
		        	
		    	}
		    	
		    }
			
		}
		
		// Return the results.
		return graph;
		
	}

	/**
	 * Export Repast SD merge split into knit.
	 *
	 * @param knit the knit
	 * @param split the split
	 * @return the sets the
	 */
	public Set<String> exportRepastSDMergeSplitIntoKnit(Set<String> knit,
			Set<String> split) {
		
		// Prepare to merge the split into the knit.
		HashMap<String, String> termMap = new HashMap<String, String>();
		for (String equation : split) {
			
			// Extract the components of the next equation.
			String leftHandSide  = equation.substring(0, equation.indexOf("=") - 1).trim();
			String rightHandSide = "(" + equation.substring(equation.indexOf("=") + 2).trim() + ")";
			
			// Store the components of the next equation.
			termMap.put(leftHandSide, rightHandSide);

		}
		
		// Merge the split into the knit.
		Set<String> newKnits = new TreeSet<String>();
		for (String equation : knit) {
			
			// Extract the components of the next equation.
			String leftHandSide  = equation.substring(0, equation.indexOf("=") - 1).trim();
			String rightHandSide = equation.substring(equation.indexOf("=") + 2).trim();

			// Merge the splits into the next knit equation.
			newKnits.add(leftHandSide + " = " +
					Util.simultaneousStringReplaceAndEscape(termMap, rightHandSide));
			
		}
		
		// Return the results.
		return newKnits;
		
	}

	/**
	 * Export Repast SD main header.
	 *
	 * @param writer the writer
	 */
	public void exportRepastSDMainHeader(PrintWriter writer) {
		
		// Store the type of encoding.
		writer.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		writer.println();
		
		// Store the header opening.
		writer.print("  <xmi:XMI xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" ");
		writer.print("xmlns=\"http://repast.sf.net/systemdynamics\" ");
		writer.print("xmlns:notation=\"http://www.eclipse.org/gmf/runtime/1.0.2/notation\">\"");
		writer.println();
		
	}
	
	/**
	 * Extract declarations.
	 *
	 * @param equations the equations
	 * @return the sets the
	 */
	public Set<String> extractDeclarations(Set<String> equations) {
		
		// Declare the results holder.
		Set<String> results = new TreeSet<String>();
		
		// Scan the given equations.
		for (String equation : equations) {		

			// Check for stocks.
			if (equation.contains("INTEG(")) {
				
				// Extract the initial value.
				String initialValue = equation.substring(equation.indexOf(",") + 1).trim();
				initialValue = initialValue.substring(0, initialValue.length() - 1).trim();
				
				// Prepare the declarations and assign the requested initial value.
				String variable = equation.split("=")[0].trim();
				variable = variable.substring(0, variable.length() - 9);
				results.add("double " + variable + " = " + initialValue);			
				results.add("double " + variable + "_combined = " + initialValue);			
				
			// Check for initial values.
			} else if (equation.contains("INITIAL(")) {
				
				// Extract the initial value.
				String initialValue = equation.substring(equation.indexOf(",") + 1).trim();
				initialValue = initialValue.substring(0, initialValue.length() - 1).trim();
				
				// Prepare the declarations and assign the requested initial value.
				String variable = equation.split("=")[0].trim();
				variable = variable.substring(0, variable.length() - 9);
				results.add("double " + variable + " = " + initialValue);			
				results.add("double " + variable + "_combined = " + initialValue);			

			// Process regular variables.
			} else {
					
				// Extract the initial value.
				String initialValue = equation.substring(equation.indexOf(",") + 1).trim();
				initialValue = initialValue.substring(0, initialValue.length() - 1).trim();
				
				// Prepare the declaration and assign the requested initial value.
				String variable = equation.split("=")[0].trim();
				variable = variable.substring(0, variable.length() - 9);
				results.add("double " + variable + " = 0.0");			
				results.add("double " + variable + "_combined = 0.0");			
				
			}
			
		}
		
		// Return the results.
		return results;
		
	}

	/**
	 * Extract variables.
	 *
	 * @param equations the equations
	 * @return the sets the
	 */
	public Set<String> extractVariables(Set<String> equations) {
		
		// Declare the results holder.
		Set<String> results = new TreeSet<String>();
		String variable = "";
		
		// Scan the given equations.
		for (String equation : equations) {
			
			// Extract the left hand variable from the next equation.
			variable = equation.split("=")[0].trim();
			
			// Store the next variable.
			results.add(variable);
			
		}
		
		// Return the results.
		return results;
		
	}
	/**
	 * Simple maximum fitness function.
	 *
	 * @return the double
	 */
	public double simpleMaximumFitnessFunction() {
		
		// Declare the results holder.
		double fitnessAccumulator = 0.0;

		// Scan the variables.
		for (MatrixVariable matrixVariable : this) {

			// Scan the matrix rows.
			RealMatrix coefficients = matrixVariable.coefficients;
			for (int row = 0; row < coefficients.getRowDimension(); row++) {

				// Scan the matrix columns.
				double rowMax = 0.0;
				for (int column = 0; column <
						coefficients.getColumnDimension(); column++) {

					// Check the next cell.
					if (!Double.isNaN(coefficients.getEntry(row, column))) {
						rowMax = Math.max(rowMax,
								coefficients.getEntry(row, column));
					}

				}

				// Accumulate the value.
				fitnessAccumulator += rowMax;

			}

		}
		
		// Return the results.
		return fitnessAccumulator;
		
	}

	/**
	 * Export Image.
	 *
	 * @param fileName the file name
	 * @return the double
	 */
	public boolean exportImage(String fileName) {
		
		// Evaluate the equations.
		Set<String> knit = this.knit(MatrixModel.NO_SUFFIX, MatrixModel.COMBINED_SUFFIX);
		
		// Disperse the results of the equations.
		Set<String> split = this.split(MatrixModel.NO_SUFFIX, MatrixModel.COMBINED_SUFFIX);

		// Collect the fitness equation.
		String fitness = this.fitness(MatrixModel.NO_SUFFIX);

		// Extract the variables from the new equations.
		Set<String> variables = this.extractVariables(knit);
		variables.addAll(this.extractVariables(split));
		
		// Generate the formulation.
		return this.exportImage(fileName, variables, knit, split, fitness);

	}

	/**
	 * Export solid.
	 *
	 * @param fileName the file name
	 * @return the double
	 */
	public boolean exportSolid(String fileName) {
		
		// Evaluate the equations.
		Set<String> knit = this.knit(MatrixModel.NO_SUFFIX, MatrixModel.COMBINED_SUFFIX);
		
		// Disperse the results of the equations.
		Set<String> split = this.split(MatrixModel.NO_SUFFIX, MatrixModel.COMBINED_SUFFIX);

		// Collect the fitness equation.
		String fitness = this.fitness(MatrixModel.NO_SUFFIX);
		
		// Extract the variables from the new equations.
		Set<String> variables = this.extractVariables(knit);
		variables.addAll(this.extractVariables(split));
		
		// Generate the formulation.
		return this.exportSolid(fileName, variables, knit, split, fitness);

	}

	/**
	 * Export Image.
	 *
	 * @param fileName the file name
	 * @return the double
	 */
	public boolean exportRepastSD(String fileName) {
		
		// Evaluate the equations.
		Set<String> knit = this.knit(MatrixModel.SHORT_SUFFIX, MatrixModel.NO_SUFFIX);
		
		// Disperse the results of the equations.
		Set<String> split = this.split(MatrixModel.SHORT_SUFFIX, MatrixModel.NO_SUFFIX);

		// Collect the fitness equation.
		String fitness = this.fitness(MatrixModel.SHORT_SUFFIX);
		
		// Extract the variables from the new equations.
		Set<String> variables = this.extractVariables(knit);
		variables.addAll(this.extractVariables(split));
		
		// Generate the formulation.
		return this.exportRepastSD(fileName, variables, knit, split, fitness);

	}

	/**
	 * Zero fitness function.
	 *
	 * @return the double
	 */
	public Double zeroFitnessFunction() {
		
		// Return the default results.
		return new Double(0.0);

	}

	/**
	 * Copy.
	 *
	 * @return the matrix model
	 */
	public MatrixModel copy() {
		
		// Copy the current matrix model.
		MatrixModel newMatrixModel = Util.deepCopy(this);
		
		// Note the matrix engine, since it is transient.
		newMatrixModel.matrixEngine = this.matrixEngine;

		// Return the results.
		return newMatrixModel;

	}

	/**
	 * Cross over.
	 *
	 * @param that
	 *            the that
	 * @return the matrix model
	 */
	public MatrixModel crossOver(MatrixModel that) {

		// Create the template for the new element.
		MatrixModel childMatrixModel = that.copy();

		// Choose roughly half of the other matrix model's fitness equations.
		this.crossOverFitnessEquations(childMatrixModel);

		// Scan the variables.
		this.crossOverScanVariables(childMatrixModel);
		
		// Select the type of fitness function.
		if (this.getMatrixEngine().getRandomNumberFromTo(0.0, 1.0) <= 0.5) {
			childMatrixModel.fitnessFunctionType = this.fitnessFunctionType;
			if (this.fitnessFunctionType == MatrixModel.FITNESS_FUNCTION_TYPE.SYSTEM_DYNAMICS) {
				childMatrixModel.stepCount = this.stepCount;
				childMatrixModel.stepSize = this.stepSize;
			}
		}
					
		// Normalize the results.
		childMatrixModel.normalize();

		// Reset the child's fitness.
		childMatrixModel.fitnessValue = null;

		// Return the results.
		return childMatrixModel;

	}

	/**
	 * Cross over scan variables.
	 *
	 * @param childMatrixModel the child matrix model
	 * @throws OutOfRangeException the out of range exception
	 */
	public void crossOverScanVariables(MatrixModel childMatrixModel)
			throws OutOfRangeException {
		
		// Scan the matrix variables.
		for (int variableIndex = 0; variableIndex < this.size(); variableIndex++) {

			// Note the next matrix variables.
			MatrixVariable thisMatrixVariable = this.get(variableIndex);
			MatrixVariable childMatrixVariable = childMatrixModel
					.get(variableIndex);

			// Choose roughly half of the other matrix variable's equations.
			this.crossOverScanVariablesChooseHalf(thisMatrixVariable,
					childMatrixVariable);

			// Combine coefficients.
			this.crossOverScanVariablesCombineCoefficients(thisMatrixVariable,
					childMatrixVariable);

		}
		
	}

	/**
	 * Cross over scan variables combine coefficients.
	 *
	 * @param thisMatrixVariable the this matrix variable
	 * @param childMatrixVariable the child matrix variable
	 * @throws OutOfRangeException the out of range exception
	 */
	public void crossOverScanVariablesCombineCoefficients(
			MatrixVariable thisMatrixVariable,
			MatrixVariable childMatrixVariable) throws OutOfRangeException {
		
		// Combine coefficients by scanning coefficient rows.
		for (int rowIndex = 0; rowIndex <
				thisMatrixVariable.coefficients.getRowDimension();
				rowIndex++) {

			// Combine coefficients by scanning coefficient columns.
			for (int columnIndex = 0; columnIndex <
					thisMatrixVariable.coefficients.getColumnDimension();
					columnIndex++) {
				
				// Check the next entries to see if they are both valid.
				if (!Double.isNaN(childMatrixVariable
						.coefficients.getEntry(rowIndex, columnIndex)) &&
				    !Double.isNaN(thisMatrixVariable
				    		.coefficients.getEntry( rowIndex, columnIndex))) {
					
					// Combine two valid entries.
					childMatrixVariable.coefficients.setEntry(rowIndex, columnIndex,
							childMatrixVariable.coefficients.getEntry(rowIndex, columnIndex) +
							thisMatrixVariable.coefficients.getEntry( rowIndex, columnIndex));
				
				// Check the next entry to see if thisMatrixVariable's value is valid.
				// If not then childMatrixVariable's value must already be valid.
				} else if (!Double.isNaN(thisMatrixVariable
						.coefficients.getEntry( rowIndex, columnIndex))) {

					// Use one valid entry.
					childMatrixVariable.coefficients.setEntry(rowIndex, columnIndex,
							thisMatrixVariable.coefficients.getEntry( rowIndex, columnIndex));
					
				}
					
			}
			
		}
		
	}

	/**
	 * Cross over scan variables choose half.
	 *
	 * @param thisMatrixVariable the this matrix variable
	 * @param childMatrixVariable the child matrix variable
	 */
	public void crossOverScanVariablesChooseHalf(
			MatrixVariable thisMatrixVariable,
			MatrixVariable childMatrixVariable) {
		
		// Choose roughly half of the other matrix variable's equations.
		for (int equationIndex = 0; equationIndex < thisMatrixVariable.equations
				.size(); equationIndex++) {
			if (this.getMatrixEngine().getRandomNumberFromTo(0.0, 1.0) <= 0.5) {
				childMatrixVariable.equations.set(equationIndex,
						thisMatrixVariable.equations.get(equationIndex));
			}
		}
		
	}

	/**
	 * Cross over fitness equations.
	 *
	 * @param childMatrixModel the child matrix model
	 */
	public void crossOverFitnessEquations(MatrixModel childMatrixModel) {
		
		// Choose roughly half of the other matrix model's fitness equations.
		for (int equationIndex = 0; equationIndex < this.fitnessEquations
				.size(); equationIndex++) {
			if (this.getMatrixEngine().getRandomNumberFromTo(0.0, 1.0) <= 0.5) {
				childMatrixModel.fitnessEquations.set(equationIndex,
						this.fitnessEquations.get(equationIndex));
			}
		}
		
	}

	/**
	 * Gets the fitness value.
	 *
	 * @return the fitness value
	 */
	public Double getFitnessValue() {

		// Check the current value, if any.
		if (this.fitnessValue == null) {

			// Calculate this model's fitness value.
			this.fitnessValue = this.calculateFitnessValue();
			
			// Override invalid values.
			if (Double.isNaN(this.fitnessValue)) {
				this.fitnessValue = Double.NEGATIVE_INFINITY;
			}

		}

		// Return the previously calculate result.
		return this.fitnessValue;

	}

	/**
	 * Mutate.
	 */
	public void mutate() {

		// Scan the variables.
		this.mutateScanVariables();

		// Normalize the results.
		this.normalize();

		// Reset the fitness value.
		this.fitnessValue = null;

	}

	/**
	 * Mutate scan variables.
	 *
	 * @throws OutOfRangeException the out of range exception
	 */
	public void mutateScanVariables() throws OutOfRangeException {
		
		// Scan the variables.
		for (MatrixVariable matrixVariable : this) {

			// Scan the rows.
			for (int rowIndex = 0; rowIndex < matrixVariable.coefficients
					.getRowDimension(); rowIndex++) {

				// Scan the columns.
				for (int columnIndex = 0; columnIndex < matrixVariable.coefficients
						.getColumnDimension(); columnIndex++) {

					// Consider imposing a random mutation.
					if ((!Double.isNaN(matrixVariable.coefficients
							.getEntry(rowIndex, columnIndex))) &&
							(this.getMatrixEngine().getRandomNumberFromTo(0.0, 1.0) <=
									this.getMatrixEngine().mutationProbabilityForCells)) {

						// Mutate the coefficient.
						matrixVariable.coefficients.setEntry(rowIndex,
								columnIndex,
								this.getMatrixEngine().getRandomNumberFromTo(0.0, 1.0));

					}

				}
				
				// Check on mutating the current equation.
				if (this.equationEvolution) {
					
					// Consider mutating the current equation.
					if (this.getMatrixEngine().getRandomNumberFromTo(0.0, 1.0) <=
							this.getMatrixEngine().mutationProbabilityForCells) {
						
						// Mutate the current equation.
						MatrixEquation matrixEquation =  new MatrixEquation(this,
								matrixVariable, rowIndex);
						matrixEquation.grow();
						matrixVariable.equations.set(rowIndex,
								matrixEquation.getEquation());
						
					}
					
				}
				

			}

		}
		
	}

	/**
	 * Normalize.
	 */
	public void normalize() {

		// Normalize each variable.
		for (MatrixVariable matrixVariable : this) {

			// Normalize the coefficients for each equation.
			for (int rowIndex = 0; rowIndex < matrixVariable.coefficients
					.getRowDimension(); rowIndex++) {

				// Setup the row accumulator.
				double sum = 0.0;

				// Find the row sum, if any.
				sum = this.normalizeFindRowSum(matrixVariable, rowIndex, sum);

				// Normalize the row.
				this.normalizeRow(matrixVariable, rowIndex, sum);

			}

		}

		// Reset the fitness value.
		this.fitnessValue = null;

	}

	/**
	 * Normalize row.
	 *
	 * @param matrixVariable the matrix variable
	 * @param rowIndex the row index
	 * @param sum the sum
	 * @throws OutOfRangeException the out of range exception
	 */
	public void normalizeRow(MatrixVariable matrixVariable, int rowIndex,
			double sum) throws OutOfRangeException {
		// Check for valid sums.
		if (sum != 0.0) {

			// Normalize the row.
			for (int columnIndex = 0; columnIndex < matrixVariable.coefficients
					.getColumnDimension(); columnIndex++) {

				// Check for assigned cells.
				if (!Double.isNaN(matrixVariable.coefficients.getEntry(
						rowIndex, columnIndex))) {

					// Normalize.
					matrixVariable.coefficients.setEntry(
							rowIndex,
							columnIndex,
							matrixVariable.coefficients.getEntry(
									rowIndex, columnIndex) / sum);

				}

			}

		}
	}

	/**
	 * Normalize find row sum.
	 *
	 * @param matrixVariable the matrix variable
	 * @param rowIndex the row index
	 * @param sum the sum
	 * @return the double
	 * @throws OutOfRangeException the out of range exception
	 */
	public double normalizeFindRowSum(MatrixVariable matrixVariable, int rowIndex,
			double sum) throws OutOfRangeException {
		
		// Find the sum, if any.
		for (int columnIndex = 0; columnIndex < matrixVariable.coefficients
				.getColumnDimension(); columnIndex++) {

			// Check for assigned cells.
			if (!Double.isNaN(matrixVariable.coefficients.getEntry(
					rowIndex, columnIndex))) {

				// Accumulate the value.
				sum += matrixVariable.coefficients.getEntry(rowIndex,
						columnIndex);

			}

		}
		
		// Return the results.
		return sum;
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractCollection#toString()
	 */
	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {

		// Return the results.
		return Util.XSTREAM_DRIVER.toXML(this);

	}

	/**
	 * Write.
	 *
	 * @param fileName            the file name
	 * @return true, if successful
	 */
	public boolean write(String fileName) {

		// Check for errors.
		try {

			// Create the requested workbook image.
			XSSFWorkbook workbook = new XSSFWorkbook();

			// Create the needed highlight style.
			XSSFCellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			cellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(
					197, 217, 241)));
			cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			XSSFFont font = workbook.createFont();
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);
			cellStyle.setFont(font);

			// Fill in the fitness sheet.
			this.writeFitnessFunctionInformation(workbook,
					cellStyle);		
	
			// Fill in the variables.
			this.writeScanVariables(workbook, cellStyle);

			// Attempt to create the requested file from the workbook image.
			FileOutputStream file = new FileOutputStream(fileName);
			workbook.write(file);
			file.close();

			// Catch errors.
		} catch (Exception e) {

			// Note the error.
			return false;

		}

		// Return the default results.
		return true;

	}

	/**
	 * Write scan variables.
	 *
	 * @param workbook the workbook
	 * @param cellStyle the cell style
	 * @throws OutOfRangeException the out of range exception
	 */
	public void writeScanVariables(XSSFWorkbook workbook,
			XSSFCellStyle cellStyle) throws OutOfRangeException {
		
		// Scan the variables.
		for (MatrixVariable matrixVariable : this) {

			// Fill in a header.
			XSSFSheet sheet = workbook.createSheet(
				matrixVariable.name + " (" +
				matrixVariable.units.getUnit() + ")");
			XSSFRow row = sheet.createRow(0);
			XSSFCell cell = row.createCell(0);
			cell.setCellValue("Equation");
			cell.setCellStyle(cellStyle);
			cell = row.createCell(1);
			cell.setCellValue("Node");
			cell.setCellStyle(cellStyle);
			for (int rowIndex = 0; rowIndex < matrixVariable.equations
					.size(); rowIndex++) {
				cell = row.createCell(rowIndex + 2);
				cell.setCellValue(this.nodeName(rowIndex));
				cell.setCellStyle(cellStyle);
			}

			// Fill in the main rows.
			for (int rowIndex = 0; rowIndex < matrixVariable.equations
					.size(); rowIndex++) {

				// Create the next row.
				row = sheet.createRow(rowIndex + 1);

				// Create the equation column.
				cell = row.createCell(0);
				cell.setCellValue(matrixVariable.equations.get(rowIndex));

				// Create the node index column.
				cell = row.createCell(1);
				cell.setCellValue(this.nodeName(rowIndex));
				cell.setCellStyle(cellStyle);

				// Fill in the coefficients.
				for (int columnIndex = 0; columnIndex < matrixVariable.equations
						.size(); columnIndex++) {
					cell = row.createCell(columnIndex + 2);
					double cellValue = matrixVariable.coefficients
							.getEntry(rowIndex, columnIndex);
					if (!Double.isNaN(cellValue)) {
						cell.setCellValue(cellValue);
					}
				}

			}

		}
		
	}

	/**
	 * Write fitness function information.
	 *
	 * @param workbook the workbook
	 * @param cellStyle the cell style
	 * @return the XSSF sheet
	 */
	public XSSFSheet writeFitnessFunctionInformation(XSSFWorkbook workbook,
			XSSFCellStyle cellStyle) {
		
		// Fill in the fitness header.
		XSSFSheet sheet = workbook.createSheet("Fitness");
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = row.createCell(0);
		cell.setCellValue("Equation");
		cell.setCellStyle(cellStyle);
		cell = row.createCell(1);
		cell.setCellValue("Node");
		cell.setCellStyle(cellStyle);

		// Fill in the fitness equations.
		for (int rowIndex = 0; rowIndex < fitnessEquations.size(); rowIndex++) {

			// Create the next row.
			row = sheet.createRow(rowIndex + 1);

			// Create the equation column.
			cell = row.createCell(0);
			cell.setCellValue(this.fitnessEquations.get(rowIndex));

			// Create the node index column.
			cell = row.createCell(1);
			cell.setCellValue(this.nodeName(rowIndex));
			cell.setCellStyle(cellStyle);

		}

		// Fill in the fitness value footer.
		row = sheet.createRow(fitnessEquations.size() + 1);
		cell = row.createCell(0);
		if (this.fitnessFunctionType ==
				MatrixModel.FITNESS_FUNCTION_TYPE.SIMPLE_MAXIMUM) {
			cell.setCellValue(MatrixModel.SIMPLE_MAXIMUM_STRING);
		} else if (this.fitnessFunctionType ==
				MatrixModel.FITNESS_FUNCTION_TYPE.SYSTEM_DYNAMICS) {
			cell.setCellValue(MatrixModel.SYSTEM_DYNAMICS_STRING);
		} else {
			cell.setCellValue(MatrixModel.ZERO_FITNESS_STRING);
		}
		cell = row.createCell(1);
		if (!Double.isNaN(this.getFitnessValue())) {
			cell.setCellValue(this.getFitnessValue());
		}

		// Fill in the step count and size.
		if (this.fitnessFunctionType ==
			MatrixModel.FITNESS_FUNCTION_TYPE.SYSTEM_DYNAMICS) {
			row = sheet.createRow(fitnessEquations.size() + 2);
			cell = row.createCell(0);
			cell.setCellValue(MatrixModel.SYSTEM_DYNAMICS_STEP_COUNT_STRING);
			cell = row.createCell(1);
			cell.setCellValue(this.stepCount);
			row = sheet.createRow(fitnessEquations.size() + 3);
			cell = row.createCell(0);
			cell.setCellValue(MatrixModel.SYSTEM_DYNAMICS_STEP_SIZE_STRING);
			cell = row.createCell(1);
			cell.setCellValue(this.stepSize);
			row = sheet.createRow(fitnessEquations.size() + 4);
			cell = row.createCell(0);
			cell.setCellValue(MatrixModel.SYSTEM_DYNAMICS_GROW_STRING);
			cell = row.createCell(1);
			if (this.equationEvolution) {
				cell.setCellValue("Yes");
			} else {
				cell.setCellValue("No");
			}

		}

		// Return the results.
		return sheet;

	}
	
	/**
	 * Node count.
	 *
	 * @return the int
	 */
	public int nodeCount() {
		
		// Return the number of nodes.
		return this.fitnessEquations.size();
		
	}
	
	/**
	 * Node name.
	 *
	 * @param index the index
	 * @return the string
	 */
	public String nodeName(int index) {
		
		// Find and return the requested node name.
		return this.nodeNames.get(index);
		
	}
	
	/**
	 * The Class MatrixDimensions.
	 */
	private static class MatrixDimensions {
	
		/** The rows. */
		int rows = 0;
		
		/** The columns. */
		int columns = 0;
		
	}

	/**
	 * The Enum FITNESS_FUNCTION_TYPE.
	 */
	public static enum FITNESS_FUNCTION_TYPE {
		
		/** The no fitness code. */
		ZERO_FITNESS,
		
		/** The simple maximum code. */
		SIMPLE_MAXIMUM,
		
		/** The system dynamics solver code. */
		SYSTEM_DYNAMICS
		
	}

}