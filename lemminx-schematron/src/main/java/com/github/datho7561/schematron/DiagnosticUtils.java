/*******************************************************************************
* Copyright (c) 2025 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package com.github.datho7561.schematron;

import java.net.URI;
import java.nio.file.Path;
import java.text.MessageFormat;

import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.XMLModel;
import org.eclipse.lemminx.utils.XMLPositionUtility;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

/**
 * Utility methods and constants for building diagnostics.
 */
public class DiagnosticUtils {

	public static final Range ZERO_RANGE = new Range(new Position(0, 0), new Position(0, 1));

	private static final String MISSING_SCHEMATRON_MSG = "Schema {0} is missing";
	private static final String MISSING_SCHEMATRON_CODE = "missing-schematron";

	private static final String BAD_SCHEMATRON_MSG = "Schema {0} is invalid";
	private static final String BAD_SCHEMATRON_CODE = "bad-schematron";

	/**
	 * Returns the range of the href of the xml-model PI that associates the given
	 * schema, or ZERO_RANGE if it can't be found.
	 * 
	 * @param schemaPath  the basename of the schema file
	 * @param xmlDocument the XML dom document
	 * @return the range of the href of the xml-model PI that associates the given
	 *         schema, or ZERO_RANGE if it can't be found
	 */
	private static Range getRangeOfXmlModel(String schemaPath, DOMDocument xmlDocument) {
		for (XMLModel xmlModel : xmlDocument.getXMLModels()) {
			if (xmlModel.getHref().endsWith(schemaPath)) {
				return XMLPositionUtility.createSelectionRange(xmlModel.getHrefNode());
			}
		}
		return ZERO_RANGE;
	}

	/**
	 * Returns the diagnostic for the given missing schema.
	 * 
	 * @param schemaUri   the schema that's missing
	 * @param xmlDocument the dom document that the schema is referenced from
	 * @return the diagnostic for the given missing schema
	 */
	public static Diagnostic getMissingSchemaDiagnostics(URI schemaUri, DOMDocument xmlDocument) {
		Path fileNamePath = Path.of(schemaUri).getFileName();
		String filename = "";
		if (fileNamePath != null) {
			filename = fileNamePath.toString();
		}
		Diagnostic d = new Diagnostic(getRangeOfXmlModel(filename, xmlDocument),
				MessageFormat.format(MISSING_SCHEMATRON_MSG, filename));
		d.setSeverity(DiagnosticSeverity.Warning);
		d.setCode(MISSING_SCHEMATRON_CODE);
		return d;
	}

	/**
	 * Returns the diagnostic for the given broken schema.
	 * 
	 * @param schemaPath  the path to the schema that's broken
	 * @param xmlDocument the dom document that the schema is referenced from
	 * @return the diagnostic for the given broken schema
	 */
	public static Diagnostic getInvalidSchematronDiagnostic(String schemaPath, DOMDocument xmlDocument) {
		Diagnostic d = new Diagnostic(DiagnosticUtils.getRangeOfXmlModel(schemaPath, xmlDocument),
				MessageFormat.format(BAD_SCHEMATRON_MSG, schemaPath));
		d.setCode(BAD_SCHEMATRON_CODE);
		return d;
	}

}
