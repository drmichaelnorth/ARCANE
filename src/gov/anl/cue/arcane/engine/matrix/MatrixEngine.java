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

import java.beans.Transient;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
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

import gov.anl.cue.arcane.engine.Util;

/**
 * The MatrixEngine class is the heart of the system. It uses templates to
 * create scenarios. Scenarios contain a meta-information file in the scenario's
 * main directory and a population of one or more matrix models in the scenario's
 * input directory. The population of matrix models is evolved over time. The
 * results are stored in the scenario's output directory as sorted list of matrix
 * models as well as optional export files. Examples showing how to generate
 * scenarios from templates can be found in MatrixEngineTest.testImportTemplateRegular1(),
 * MatrixEngineTest.testImportTemplateRegular2(), and
 * MatrixEngineTest.testImportTemplateRegular3(). Examples showing how to load
 * and run scenarios can be found in MatrixEngineTest.testEvolveOneStep() and
 * MatrixEngineTest.testEvolveTenSteps(). An example showing how to export images
 * can be found in MatrixEngineTest.testExportImages(). An example showing how to
 * export 3D printable SCAD format files can be found in
 * MatrixEngineTest.testExportSolids(). An example showing how to export Repast.SD
 * XML files can be found in MatrixEngineTest.testExportRepastSDs().
 */
public class MatrixEngine implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5929920694261593355L;

	/** The mutation probability for cells. */
	public double mutationProbabilityForCells = 0.1;

	/** The fraction of matrix models removed in each evolutionary cycle. */
	public double killFraction = 0.5;
	
	/** The probability that a new matrix model will come from crossover. */
	public double crossoverProbability = 0.5;
	
	/** The population size. */
	public int populationSize = 100;
	
	/** The random seed. */
	public int randomSeed = 43;
	
	/** The maximum new term count. */
	public int maximumNewTermCount = 5;
	
	/** The addition probability. */
	public double additionProbability = 0.10;
	
	/** The subtraction probability. */
	public double subtractionProbability = 0.10;
	
	/** The multiplication probability. */
	public double multiplicationProbability = 0.40;
	
	/** The add threshold. */
	public double accumulateAddProbability = 0.33333;
	
	/** The subtract threshold. */
	public double accumulateSubtractProbability = 0.33333;
	
	/** The random generator. */
	public transient RandomDataGenerator RANDOM_GENERATOR = null;
	
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		// Print a simple help message.
		System.out.println(
				"ARCANE: Please see the README.md file for more information.");
		
	}
	
	/**
	 * Gets the random number generator.
	 *
	 * @return the random number generator
	 */
	@Transient
	public RandomDataGenerator getRandomNumberGenerator() {
	
		// Make sure the random stream is initialized.
		if (this.RANDOM_GENERATOR == null) {
			this.RANDOM_GENERATOR = new RandomDataGenerator();
			this.RANDOM_GENERATOR.reSeed(randomSeed);
		}
		
		// Return the random stream.
		return this.RANDOM_GENERATOR;
		
	}

	/**
	 * Gets the random number from to.
	 *
	 * @param from
	 *            the from
	 * @param to
	 *            the to
	 * @return the random number from to
	 */
	public double getRandomNumberFromTo(double from, double to) {
		
		// Check the input for limited ranges.
		if (from == to) {
			
			// Return the default result.
			return from;
			
		} else {

			// Return the requested random number.
			return this.getRandomNumberGenerator().nextUniform(from, to);
			
		}

	}

	/** The input population. */
	public MatrixPopulation inputPopulation = new MatrixPopulation(this);
	
	/** The output population. */
	public MatrixPopulation outputPopulation = new MatrixPopulation(this);

	/**
	 * Import template.
	 *
	 * @param fileName the file name
	 * @return the matrix engine
	 */
	public static MatrixEngine importTemplate(String fileName) {
		
		// Create the matrix engine.
		MatrixEngine matrixEngine = new MatrixEngine();
		
		// Try to read the input spreadsheet.
		try {

			// Attempt to read the parameters.
			matrixEngine.readParameters(fileName);

			// Attempt to read the population.
			matrixEngine.inputPopulation
					.importTemplate(fileName);
				
			// Attempt to read the population.
			matrixEngine.outputPopulation
					.importTemplate(fileName);
			
		// Catch errors.
		} catch (Exception e) {

			// Note an error.
			matrixEngine = null;

		}
		
		// Return the results.
		return matrixEngine;
		
	}
	
	/**
	 * Read.
	 *
	 * @param directoryName
	 *            the directory name
	 * @return the matrix engine
	 */
	public static MatrixEngine read(String directoryName) {

		// Create a new matrix engine.
		MatrixEngine matrixEngine = new MatrixEngine();
	
		// Try to read the input spreadsheet.
		try {

			// Read the parameters.
			matrixEngine.readParameters(directoryName + "//" +
					MatrixEngineConstants.getString("MatrixEngine.0"));
			
			// Read the matrix model input files.
			matrixEngine.inputPopulation = MatrixPopulation.read(
					directoryName, matrixEngine);

			// Read the matrix model input files again.
			matrixEngine.outputPopulation = MatrixPopulation.read(
					directoryName, matrixEngine);
			
		// Catch errors.
		} catch (Exception e) {

			// Note an error.
			matrixEngine = null;

		}

		// Return the results.
		return matrixEngine;

	}

	/**
	 * Read parameters.
	 *
	 * @param fileName the file name
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws FileNotFoundException the file not found exception
	 */
	public void readParameters(String fileName) throws IOException,
			FileNotFoundException {
		
		// Attempt to open the spreadsheet.
		XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(
				new File(fileName)));

		// Scan the sheets.
		Iterator<XSSFSheet> sheets = workbook.iterator();

		// Read the first sheet.
		XSSFSheet sheet = sheets.next();
		
		// Scan the rows.
		for (Row row : sheet) {
			
			// Get the next row label.
			String label = row.getCell(0).getStringCellValue();
			
			// Check the label type.
			if (label.equals("Random Seed")) {
				
				// Read the value.
				this.randomSeed = (int) row
						.getCell(1).getNumericCellValue();
			
			// Check the next label type.
			} else if (label.equals("Population Size")) {

				// Read the value.
				this.populationSize = (int) row
						.getCell(1).getNumericCellValue();
					
				// Check the next label type.
			} else if (label.equals("Kill Fraction")) {
				
				// Read the value.
				this.killFraction = row.getCell(1)
						.getNumericCellValue();
			
			// Check the next label type.
			} else if (label.equals("Crossover Probability")) {
				
				// Read the value.
				this.crossoverProbability = row
						.getCell(1).getNumericCellValue();
				
			// Check the next label type.
			} else if (label.equals("Mutation Probability for Cells")) {
				
				// Read the value.
				this.mutationProbabilityForCells = row
						.getCell(1).getNumericCellValue();
				
			// Check the next label type.
			} else if (label.equals("Maximum New Term Count")) {
					
				// Read the value.
				this.maximumNewTermCount = (int) row
						.getCell(1).getNumericCellValue();

			// Check the next label type.
			} else if (label.equals("Addition Probability")) {
					
				// Read the value.
				this.additionProbability = row
						.getCell(1).getNumericCellValue();

			// Check the next label type.
			} else if (label.equals("Subtraction Probability")) {
					
				// Read the value.
				this.subtractionProbability = row
						.getCell(1).getNumericCellValue();

			// Check the next label type.
			} else if (label.equals("Multiplication Probability")) {
					
				// Read the value.
				this.multiplicationProbability = row
						.getCell(1).getNumericCellValue();

			// Check the next label type.
			} else if (label.equals("Accumulate Add Probability")) {
					
				// Read the value.
				this.accumulateAddProbability = row
						.getCell(1).getNumericCellValue();

			// Check the next label type.
			} else if (label.equals("Accumulate Subtract Probability")) {
					
				// Read the value.
				this.accumulateSubtractProbability = row
						.getCell(1).getNumericCellValue();

			}
			
		}
		
		// Close the workbook.
		workbook.close();
		
	}

	/**
	 * Write.
	 *
	 * @param directoryName
	 *            the directory name
	 * @return the matrix engine
	 */
	public boolean write(String directoryName) {
		
		// Prepare to return the status.
		boolean results = true;
		
		// Prepare to create the directories.
		try {
			
			// Attempt to create the requested directories.
			FileUtils.forceMkdir(new File(directoryName));
			FileUtils.forceMkdir(new File(directoryName +
					"//" + Util.INPUT_DIR));
			FileUtils.forceMkdir(new File(directoryName +
					"//" + Util.OUTPUT_DIR));
			FileUtils.forceMkdir(new File(directoryName +
					"//" + Util.TEST_DIR));

		// Catch errors.
		} catch (Exception e) {
			
			// Note an error.
			results = false;
			
		}
		
		// Write the engine file.
		results = results & this.writeEngineFile(
				directoryName + "//" +
				MatrixEngineConstants.getString("MatrixEngine.0"));

		// Write the input population files.
		results = results & this.inputPopulation
				.write(directoryName + "//" +
				Util.INPUT_DIR);

		// Write the output population files.
		results = results & this.outputPopulation
				.write(directoryName + "//" +
				Util.OUTPUT_DIR);
		
		// Return the results.
		return results;

	}

	/**
	 * Write engine file.
	 *
	 * @param fileName the file name
	 * @return true, if successful
	 */
	public boolean writeEngineFile(String fileName) {
		
		// Check for errors.
		try {

			// Create the requested workbook image.
			XSSFWorkbook workbook = new XSSFWorkbook();

			// Create the parameters sheet.
			XSSFSheet sheet = workbook.createSheet("Parameters");

			// Create the rows.
			this.createRowHeader(workbook, sheet, 0, "Parameter", "Value");
			this.createRowInt(sheet, 1, "Random Seed",
					this.randomSeed);
			this.createRowInt(sheet, 2, "Population Size",
					this.populationSize);
			this.createRowDouble(sheet, 3, "Kill Fraction",
					this.killFraction);
			this.createRowDouble(sheet, 4, "Crossover Probability",
					this.crossoverProbability);
			this.createRowDouble(sheet, 5, "Mutation Probability for Cells",
					this.mutationProbabilityForCells);

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
	 * Creates the row header.
	 *
	 * @param workbook the workbook
	 * @param sheet the sheet
	 * @param rowIndex the row index
	 * @param label1 the label1
	 * @param label2 the label2
	 */
	public void createRowHeader(XSSFWorkbook workbook,
			XSSFSheet sheet, int rowIndex, String label1, String label2) {
		
		// Create the needed highlight style.
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(
				197, 217, 241)));
		cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		XSSFFont font = workbook.createFont();
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		cellStyle.setFont(font);

		// Create the requested row.
		XSSFRow row = sheet.createRow(0);
		XSSFCell cell = row.createCell(0);
		cell.setCellValue(label1);
		cell.setCellStyle(cellStyle);
		cell = row.createCell(1);
		cell.setCellValue(label2);
		cell.setCellStyle(cellStyle);
		
	}
	
	/**
	 * Creates the row int.
	 *
	 * @param sheet the sheet
	 * @param rowIndex the row index
	 * @param label the label
	 * @param value the value
	 */
	public void createRowInt(XSSFSheet sheet, int rowIndex, String label, int value) {
		
		// Create the requested row.
		XSSFRow row = sheet.createRow(rowIndex);
		XSSFCell cell = row.createCell(0);
		cell.setCellValue(label);
		cell = row.createCell(1);
		cell.setCellValue(value);
		
	}
	
	/**
	 * Creates the row double.
	 *
	 * @param sheet the sheet
	 * @param rowIndex the row index
	 * @param label the label
	 * @param value the value
	 */
	public void createRowDouble(XSSFSheet sheet, int rowIndex, String label, double value) {
		
		// Create the requested row.
		XSSFRow row = sheet.createRow(rowIndex);
		XSSFCell cell = row.createCell(0);
		cell.setCellValue(label);
		cell = row.createCell(1);
		cell.setCellValue(value);
		
	}
	
	/**
	 * Export images.
	 *
	 * @param directoryName
	 *            the directory name
	 * @return the matrix engine
	 */
	public boolean exportImages(String directoryName) {

		// Export the population images.
		return this.outputPopulation.exportImages(directoryName);

	}

	/**
	 * Export solids.
	 *
	 * @param directoryName
	 *            the directory name
	 * @return the matrix engine
	 */
	public boolean exportSolids(String directoryName) {

		// Export the population images.
		return this.outputPopulation.exportSolids(directoryName);

	}

	/**
	 * Export solids.
	 *
	 * @param directoryName
	 *            the directory name
	 * @return the matrix engine
	 */
	public boolean exportRepastSDs(String directoryName) {

		// Export the population images.
		return this.outputPopulation.exportRepastSDs(directoryName);

	}

	/**
	 * Instantiates a new matrix engine.
	 *
	 */
	public MatrixEngine() {
		
	}
	
	/**
	 * Stochastic run.
	 *
	 * @param outputDirectory the output directory
	 * @param gaRuns the ga runs
	 * @param gaStepsPerIterate the ga steps per iterate
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void stochasticRun(String outputDirectory,
			int gaRuns, int gaStepsPerIterate) throws IOException {
		
		// Open the output file.
		PrintWriter outputFile = new PrintWriter(
				new File(outputDirectory + "//Fitness.txt"));
		
		// Output a header.
		outputFile.println(
			"GA Run, GA Steps, Model Index, Fitness Value");

		// Complete the requested number of evolutionary runs.
		for (int run = 1; run <= gaRuns; run++) {
			
			// Reset between stochastic runs.
			this.reset();
			
			// Set a new random seed.
			this.randomSeed = run;
			
			// Complete the requested number of steps.
			for (int step = 1; step <= gaStepsPerIterate;
					step++) {
				
				// Evolve the population.
				this.evolve(1);
				
				// Report on the current results.
				int index = 0;
				for (MatrixModel matrixModel : this.outputPopulation) {
				
					// Note the next result.
					outputFile.println("" + run + ", " +
							step + ", " + (++index) + ", " +
							matrixModel.getFitnessValue());
				
				}
				
				// Flush the output file to persistent storage.
				outputFile.flush();
		
			}
			
			// Store the results.
			this.write(outputDirectory +
				"//MatrixEngine_Run_" + run);
			
		}
		
		// Close the output file.
		outputFile.close();
		
	}	
	
	/**
	 * Reset.
	 */
	public void reset() {
		
		// Reset the output population.
		this.outputPopulation = Util.deepCopy(
			this.inputPopulation);
		for (MatrixModel matrixModel : this.outputPopulation) {
			matrixModel.matrixEngine = this;
		}
		
		// Reset the random seed, if needed.
		if (this.RANDOM_GENERATOR != null) {
			this.RANDOM_GENERATOR.reSeed(randomSeed);
		}
		
	}
		
	/**
	 * Evolve.
	 *
	 * @param steps the steps
	 */
	public void evolve(int steps) {

		// Make sure the population is the expected size.
		this.fill();
		
		// Complete the needed evolutionary steps.
		for (int step = 1; step <= steps; step++) {
			
			// Remove less competitive matrix models.
			this.kill();
			
			// Create new matrix models.
			this.fill();
			
		}

	}
	
	/**
	 * Fill.
	 */
	public void fill() {
		
		// Define the competitive segment.
		int competitiveSegment = this.outputPopulation.size();
		
		// Check for a population.
		if (competitiveSegment <= 0) return;

		// Add new matrix models. Please note that the population
		// is sorted in descending fitness value order.
		for (int newMatrixModelCount = this.outputPopulation.size();
				newMatrixModelCount < this.populationSize;
				newMatrixModelCount++) {
			
			// Create a new matrix model, one way or another.
			MatrixModel newMatrixModel = null;
			if (this.getRandomNumberFromTo(0.0, 1.0)
					<= this.crossoverProbability) {
				
				// Choose parents.
				MatrixModel parentMatrixModel1 = this.outputPopulation.get((int)
						this.getRandomNumberFromTo(0,
						competitiveSegment));
				MatrixModel parentMatrixModel2 = this.outputPopulation.get((int)
						this.getRandomNumberFromTo(0,
						competitiveSegment));

				// Crossover.
				newMatrixModel = parentMatrixModel1
						.crossOver(parentMatrixModel2);
				
			} else {
				
				// Choose a parent.
				MatrixModel parentMatrixModel = this.outputPopulation.get((int)
						this.getRandomNumberFromTo(0,
						competitiveSegment));

				// Copy.
				newMatrixModel = parentMatrixModel.copy();
				
				// Mutate.
				newMatrixModel.mutate();
				
			}
			
			// Add the new matrix model.
			this.outputPopulation.add(newMatrixModel);
			
		}
	
	}

	/**
	 * Kill.
	 */
	public void kill() {
		
		// Remove less competitive matrix models. Please note that the
		// population is sorted in descending fitness value order.
		for (int targetPopulation = (int) Math.round(this.outputPopulation.size() / 2.0);
				targetPopulation > 0; targetPopulation--) {

			// Remove the least competitive matrix model.
			this.outputPopulation.remove(this.outputPopulation.size() - 1);
			
		}
		
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

}
