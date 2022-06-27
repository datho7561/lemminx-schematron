/*******************************************************************************
* Copyright (c) 2021 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package com.github.datho7561;

import static com.github.datho7561.TestUtils.d;
import static org.eclipse.lemminx.XMLAssert.r;

import org.eclipse.lemminx.XMLAssert;
import org.junit.jupiter.api.Test;

/**
 * Tests for Schematron file validation
 *
 * @author datho7561
 */
public class SchematronDiagnosticsParticipantTest {

	@Test
	public void testValidationElementAssertion() {
		String xml = "<?xml-model href=\"src/test/resources/schematron/title.sch\" type=\"application/xml\" schematypens=\"http://www.ascc.net/xml/schematron\"?>\n"
				+ //
				"<Person Title=\"Mr\">\n" + //
				"  <Name>Eddie</Name>\n" + //
				"  <Gender>Female</Gender>\n" + //
				"</Person>";
		XMLAssert.testDiagnosticsFor(xml, d(r(1, 1, 1, 7),
				"If the Title is \"Mr\" then the gender of the person must be \"Male\".", "failed-assert"));
	}

	@Test
	public void testValidationInvalidSchematron() {
		String xml = "<?xml-model href=\"src/test/resources/schematron/invalid.sch\" type=\"application/xml\" schematypens=\"http://www.ascc.net/xml/schematron\"?>\n"
				+ //
				"<Person Title=\"Mr\">\n" + //
				"  <Name>Eddie</Name>\n" + //
				"  <Gender>Female</Gender>\n" + //
				"</Person>";
		// FIXME:
		XMLAssert.testDiagnosticsFor(xml,
				d(r(0, 0, 0, 1), "Schema invalid.sch is invalid", "bad-schematron"));
	}

	// FIXME: debug schxslt and xalan to figure out why this happens
	@Test
	public void testValidationSchematronThatBreaksSchxslt() {
		String xml = "<?xml-model href=\"src/test/resources/schematron/breaks-schxslt.sch\" type=\"application/xml\" schematypens=\"http://www.ascc.net/xml/schematron\"?>\n"
				+ //
				"<Person Title=\"Mr\">\n" + //
				"  <Name>Eddie</Name>\n" + //
				"  <Gender>Female</Gender>\n" + //
				"</Person>";
		// FIXME:
		XMLAssert.testDiagnosticsFor(xml,
				d(r(0, 0, 0, 1), "The schema parser encountered an error while trying to parse breaks-schxslt.sch", "schematron-parser-error"));
	}

}
