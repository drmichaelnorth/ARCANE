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

import java.beans.Transient;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * The MatrixPopulation class is a container for a sorted list
 * of MatrixModels. The list is sorted in order of descending
 * fitness value, with the highest value first (i.e., item zero)
 * in the list.
 */
public class MatrixPopulation extends ArrayList<MatrixModel> {

	/** The matrix engine. */
	public transient MatrixEngine matrixEngine = null;
	
	/**
	 * Gets the matrix engine.
	 *
	 * @return the matrix engine
	 */
	@Transient
	public MatrixEngine getMatrixEngine() {
		return matrixEngine;
	}

	/**
	 * Import template.
	 *
	 * @param fileName the file name
	 * @return true, if successful
	 */
	public MatrixModel importTemplate(String fileName) {
		
		// Attempt to read the template.
		MatrixModel matrixModel = MatrixModel
				.importTemplate(this.matrixEngine, fileName);
		
		// Record the results.
		this.add(matrixModel);
		
		// Return the results.
		return matrixModel;
		
	}

	/**
	 * The Class XLSXFilenameFilter.
	 */
	private class XLSXFilenameFilter implements FilenameFilter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
		 */
		@Override
		public boolean accept(File directory, String fileName) {
			
			// Check the input.
			if (fileName.endsWith("xlsx")) {
				return true;
			} else {
				return false;
			}
			
		}

	}

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Instantiates a new matrix population.
	 *
	 * @param newMatrixEngine the new matrix engine
	 */
	public MatrixPopulation(MatrixEngine newMatrixEngine) {
		
		// Note our container.
		this.matrixEngine = newMatrixEngine;
		
	}

	/**
	 * Read.
	 *
	 * @param directoryName the directory name
	 * @param newMatrixEngine the new matrix engine
	 * @return the matrix population
	 */
	public static MatrixPopulation read(String directoryName, MatrixEngine newMatrixEngine) {

		// Create an empty population.
		MatrixPopulation matrixPopulation = new MatrixPopulation(newMatrixEngine);

		// Scan the given directory.
		try {
			
			// Prepare to scan the given directory.
			File directory = new File(directoryName + "//" + Util.INPUT_DIR);
			
			// Scan the given directory.
			for (File file : directory
					.listFiles(matrixPopulation.new XLSXFilenameFilter())) {
	
				// Read the next example input file.
				matrixPopulation.add(
						MatrixModel.read(newMatrixEngine, file.getCanonicalPath()));

			}
		
		// Catch errors.
		} catch (Exception e) {
			
			// Note an error.
			matrixPopulation = null;
			
		}

		// Return the new population.
		return matrixPopulation;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.ArrayList#add(java.lang.Object)
	 */
	@Override
	public boolean add(MatrixModel matrixModel) {

		// Add the new matrix model.
		super.add(matrixModel);

		// Sort the updated matrix models list in the order of decreasing
		// fitness.
		Collections.sort(this, new Comparator<MatrixModel>() {
			@Override
			public int compare(MatrixModel left, MatrixModel right) {
				if (left.getFitnessValue() < right.getFitnessValue()) {
					return 1;
				} else if (left.getFitnessValue() > right.getFitnessValue()) {
					return -1;
				} else {
					return 0;
				}
			}
		});

		// Return the default.
		return true;

	}
	
	/**
	 * Gets the fitness value.
	 *
	 * @return the fitness value
	 */
	public Double getFitnessValue() {

		// Return the result.
		return this.get(0).getFitnessValue();

	}
	
	/**
	 * Normalize.
	 */
	public void normalize() {

		// Create the requested output.
		for (MatrixModel matrixModel : this) {
			matrixModel.normalize();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.AbstractCollection#toString()
	 */
	@Override
	public String toString() {

		// Return the results.
		return Util.XSTREAM_DRIVER.toXML(this);

	}

	/**
	 * Write.
	 *
	 * @param directoryName the directory name
	 * @return true, if successful
	 */
	public boolean write(String directoryName) {

		// Prepare to return the status.
		boolean results = true;

		// Attempt to create the requested output.
		int fileCount = 1;
		for (MatrixModel matrixModel : this) {
			results = results &
					matrixModel.write(directoryName + "//MatrixModel_" + (fileCount++)
					+ ".xlsx");
		}
		
		// Return the results.
		return results;

	}

	/**
	 * Export images.
	 *
	 * @param directoryName the directory name
	 * @return true, if successful
	 */
	public boolean exportImages(String directoryName) {

		// Prepare to return the status.
		boolean results = true;

		// Create the requested output.
		int fileCount = 1;
		for (MatrixModel matrixModel : this) {
			results = results &
					matrixModel.exportImage(directoryName + "//MatrixModel_" + (fileCount++)
					+ ".png");
		}

		// Return the results.
		return results;

	}

	/**
	 * Export solids.
	 *
	 * @param directoryName the directory name
	 * @return true, if successful
	 */
	public boolean exportSolids(String directoryName) {

		// Prepare to return the status.
		boolean results = true;

		// Create the requested output.
		int fileCount = 1;
		for (MatrixModel matrixModel : this) {
			results = results &
					matrixModel.exportSolid(directoryName + "//MatrixModel_" + (fileCount++)
					+ ".scad");
		}

		// Return the results.
		return results;

	}

	/**
	 * Export repast s ds.
	 *
	 * @param directoryName the directory name
	 * @return true, if successful
	 */
	public boolean exportRepastSDs(String directoryName) {

		// Prepare to return the status.
		boolean results = true;

		// Create the requested output.
		int fileCount = 1;
		for (MatrixModel matrixModel : this) {
			results = results &
					matrixModel.exportRepastSD(directoryName + "//MatrixModel_" + (fileCount++)
					+ ".rsd");
		}

		// Return the results.
		return results;

	}

}