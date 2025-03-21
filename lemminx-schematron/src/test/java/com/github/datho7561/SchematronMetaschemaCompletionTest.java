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

import static org.eclipse.lemminx.XMLAssert.CDATA_SNIPPETS;
import static org.eclipse.lemminx.XMLAssert.COMMENT_SNIPPETS;
import static org.eclipse.lemminx.XMLAssert.c;
import static org.eclipse.lemminx.XMLAssert.te;

import org.eclipse.lemminx.XMLAssert;
import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.extensions.contentmodel.BaseFileTempTest;
import org.eclipse.lemminx.services.XMLLanguageService;
import org.eclipse.lsp4j.CompletionItem;
import org.junit.jupiter.api.Test;

public class SchematronMetaschemaCompletionTest extends BaseFileTempTest {

	@Test
	public void testValidationElementAssertion() throws Exception {
		String xml = "<schema xmlns=\"http://purl.oclc.org/dsdl/schematron\">\n" +
				"  <pattern>\n" +
				"    <|\n" +
				"  </pattern>\n" +
				"</schema>\n";
		testCompletionFor(xml, CDATA_SNIPPETS + COMMENT_SNIPPETS + 5, //
				c("include", te(2, 4, 2, 5, "<include href=\"\"></include>"), "<include"));
	}

	private static void testCompletionFor(String value, Integer expectedCount, CompletionItem... expectedItems)
			throws BadLocationException {
		testCompletionFor(value, false, expectedCount, expectedItems);
	}

	private static void testCompletionFor(String value, boolean enableItemDefaults, Integer expectedCount,
			CompletionItem... expectedItems) throws BadLocationException {
		XMLAssert.testCompletionFor(new XMLLanguageService(), value, null, null, "src/test/resources/schematron/test.xml",
				expectedCount, true, expectedItems);
	}

}
