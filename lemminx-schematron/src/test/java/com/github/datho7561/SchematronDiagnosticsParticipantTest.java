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

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.eclipse.lemminx.AbstractCacheBasedTest;
import org.eclipse.lemminx.XMLAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Tests for Schematron file validation
 *
 * @author datho7561
 */
public class SchematronDiagnosticsParticipantTest extends AbstractCacheBasedTest {

	private SchemaFileServer server;

	@BeforeEach
	public void setup() throws Exception {
		server = new SchemaFileServer();
	}

	@AfterEach
	public void teardown() throws Exception {
		server.stop();
		server = null;
	}

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

	@Test
	public void testValidationSchematronThatBreaksXalan() {
		String xml = "<?xml-model href=\"src/test/resources/schematron/breaks-xalan.sch\" type=\"application/xml\" schematypens=\"http://www.ascc.net/xml/schematron\"?>\n"
				+ //
				"<Person Title=\"Mr\">\n" + //
				"  <Name>Eddie</Name>\n" + //
				"  <Gender>Female</Gender>\n" + //
				"</Person>";
		XMLAssert.testDiagnosticsFor(xml);
	}

	@Test
	@Disabled("Cannot replicate diagnostics working in unit tests")
	public void testValidationRemoteSchematron() throws IOException, InterruptedException {
		String xml = "<?xml-model href=\"http://localhost:" + String.valueOf(server.getPort())
				+ "/title.sch\" type=\"application/xml\" schematypens=\"http://www.ascc.net/xml/schematron\"?>\n"
				+ //
				"<Person Title=\"Mr\">\n" + //
				"  <Name>Eddie</Name>\n" + //
				"  <Gender>Female</Gender>\n" + //
				"</Person>";

		// no diagnostics, since the file is downloading
		XMLAssert.testDiagnosticsFor(xml);

		TimeUnit.SECONDS.sleep(3);

		// diagnostics appear, since the file should be cached
		XMLAssert.testDiagnosticsFor(xml, d(r(1, 1, 1, 7),
				"If the Title is \"Mr\" then the gender of the person must be \"Male\".", "failed-assert"));
	}

}
