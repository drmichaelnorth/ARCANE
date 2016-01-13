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

import org.junit.Assert;
import org.junit.Test;

import gov.anl.cue.arcane.engine.UtilConstants;

/**
 * The UtilConstantsTest class is used to test the UtilConstants class.
 */
public class UtilConstantsTest {

	/**
	 * Instantiates a new util constants test.
	 */
	public UtilConstantsTest() {
	}

	/**
	 * Test the constructor.
	 */
	@Test
	public void testUtilConstants() {
		
		// Run a test.
		Assert.assertNotNull(new UtilConstants());

	}

	/**
	 * Test get string regular.
	 */
	@Test
	public void testGetStringRegular() {

		// Test a request for an existing string.
		Assert.assertTrue(UtilConstants.getString("Util.0")
				.equals("src-gen"));

	}
	
	/**
	 * Test get string invalid.
	 */
	@Test
	public void testGetString() {

		// Test a request for an nonexistent string.
		Assert.assertTrue(UtilConstants.getString("ABC")
				.equals("!ABC!"));

	}

}