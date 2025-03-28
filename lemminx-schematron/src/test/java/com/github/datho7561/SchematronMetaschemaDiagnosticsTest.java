/*******************************************************************************
* Copyright (c) 2025 Red Hat Inc. and others.
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

import org.eclipse.lemminx.AbstractCacheBasedTest;
import org.eclipse.lemminx.XMLAssert;
import org.junit.jupiter.api.Test;

public class SchematronMetaschemaDiagnosticsTest extends AbstractCacheBasedTest {

	@Test
	public void testValidationElementAssertion() {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //
				"<schema xmlns=\"http://purl.oclc.org/dsdl/schematron\">\n" + //
				"  <pattern>\n" + //
				"    <rupertGrint></rupertGrint>\n" + //
				"  </pattern>\n" + //
				"</schema>";
		XMLAssert.testDiagnosticsFor(xml, d(r(3, 5, 3, 16),
				"element \"rupertGrint\" not allowed anywhere; expected the element end-tag, element \"include\", \"let\", \"p\", \"rule\" or \"title\" or an element from another namespace",
				"unknown_element"));
	}

	@Test
	public void testValidationSchMetaschemaRules() {
		// test scenario:
		// is-a is supposed to be my-abstract-schema, but the user mistyped it
		// TODO: this is a good candidate for completion
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //
				"<schema xmlns=\"http://purl.oclc.org/dsdl/schematron\">\n" + //
				"  <pattern is-a=\"my-abstract-schem\">\n" + //
				"  </pattern>\n" + //
				"  <pattern abstract=\"true\" id=\"my-abstract-schema\">\n" + //
				"  </pattern>\n" + //
				"</schema>";
		XMLAssert.testDiagnosticsFor(xml, d(r(2, 3, 2, 10),
				"The is-a attribute of a pattern element shall match the id attribute of an abstract pattern.", "schematron-failed-assert"));
	}

}
