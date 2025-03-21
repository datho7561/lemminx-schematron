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
				"element \"rupertGrint\" not allowed anywhere; expected the element end-tag, element \"include\", \"let\", \"p\", \"rule\" or \"title\" or an element from another namespace", "unknown_element"));
	}

}
