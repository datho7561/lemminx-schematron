/*******************************************************************************
* Copyright (c) 2021 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package com.github.datho7561.schematron;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.utils.XMLPositionUtility;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import name.dmaus.schxslt.Result;
import name.dmaus.schxslt.Schematron;
import name.dmaus.schxslt.SchematronException;

/**
 * Validates an XML document using Schematron schemas
 *
 * @author datho7561
 */
public class SchematronDocumentValidator {

	private static final Range ZERO_RANGE = new Range(new Position(0, 0), new Position(0, 1));

	// Example:
	// failed-assert /Q{}Person[1] If the Title is "Mr" then the gender of the
	// person must be "Male".
	private static final Pattern SCHEMATRON_MESSAGE_DECODER = Pattern.compile("failed-assert ([^ ]+) (.*)");

	private final XPathFactory xpathFactory = XPathFactory.newInstance();

	private static final Logger LOGGER = Logger.getLogger(SchematronDocumentValidator.class.getName());

	/**
	 * Returns a list of diagnostics for an XML document given the Schematron
	 * schemas that it references
	 *
	 * @param xmlDocument   the XML document to validate
	 * @param schemaFiles   the list of Schematron schema files to validate the
	 *                      document against
	 * @param cancelChecker the cancel checker
	 * @return a list of diagnostics for the XML document
	 */
	@SuppressFBWarnings({"DCN_NULLPOINTER_EXCEPTION"})
	public List<Diagnostic> validate(DOMDocument xmlDocument, List<File> schemaFiles, CancelChecker cancelChecker) {
		List<Diagnostic> diagnostics = new ArrayList<>();
		for (File schema : schemaFiles) {
			Schematron schematron = null;
			try {
				schematron = new Schematron(new StreamSource(schema));
			} catch (RuntimeException | SchematronException e) {
				// FIXME: Use the path to the schema
				LOGGER.log(Level.WARNING, "Error while processing the schema", e);
				String schemaPath = schema.getName();
				diagnostics.add(getDiagnosticFromInvalidSchematron(schemaPath));
			}
			if (schematron != null) {
				try {
					Result validationResult = schematron.validate(new StreamSource(
							new ByteArrayInputStream(xmlDocument.getText().getBytes(StandardCharsets.UTF_8))));
					if (!validationResult.isValid()) {
						for (String message : validationResult.getValidationMessages()) {
							diagnostics.add(getSchematronMessageAsDiagnostic(message, xmlDocument));
						}
					}
				} catch (SchematronException e) {
					// FIXME: Use the path to the schema
					String schemaPath = schema.getName();
					diagnostics.add(getDiagnosticFromInvalidSchematron(schemaPath));
				} catch (NullPointerException npe) {
					// FIXME: Use the path to the schema
					String schemaPath = schema.getName();
					diagnostics.add(getDiagnosticFromSchematronThatBreaksSchxslt(schemaPath));
				}
			}
			cancelChecker.checkCanceled();
		}
		return diagnostics;
	}

	private Diagnostic getSchematronMessageAsDiagnostic(String message, DOMDocument xmlDocument) {
		Diagnostic d = new Diagnostic(ZERO_RANGE, message);
		d.setCode("failed-assert");

		Matcher m = SCHEMATRON_MESSAGE_DECODER.matcher(message);
		if (m.find()) {
			d.setMessage(m.group(2));
			String xpathExpression = getSanitizedXPathExpression(m.group(1));
			DOMNode node = getNodeFromXPathExpression(xpathExpression, xmlDocument);
			try {
				d.setRange(getRangeFromDOMNode(node, xmlDocument));
			} catch (BadLocationException e) {
				LOGGER.log(Level.SEVERE, "Error while building Schematron diagnostic range", e);
			}
		}
		return d;
	}

	private DOMNode getNodeFromXPathExpression(String xpathExpression, DOMDocument xmlDocument) {
		try {
			XPath xpath = xpathFactory.newXPath();
			XPathExpression compiledExpression = xpath.compile(xpathExpression);
			NodeList nodeList = (NodeList) compiledExpression.evaluate(xmlDocument, XPathConstants.NODESET);
			if (nodeList.getLength() > 0) {
				Node node = nodeList.item(0);
				return (DOMNode) node;
			}
		} catch (XPathExpressionException e) {
			LOGGER.log(Level.SEVERE, "Bad XPath when attempting to locate Schematron diagnostic range", e);
		}
		return null;
	}

	private static String getSanitizedXPathExpression(String xpathExpression) {
		xpathExpression = xpathExpression.replace("Q{}", "");
		return xpathExpression.replaceFirst("\\[1\\]", "");
	}

	private static Range getRangeFromDOMNode(DOMNode node, DOMDocument xmlDocument) throws BadLocationException {
		switch (node.getNodeType()) {
			case Node.ELEMENT_NODE:
				DOMElement element = (DOMElement) node;
				return XMLPositionUtility.selectStartTagName(element);
			default:
				return new Range(xmlDocument.positionAt(node.getStart()), xmlDocument.positionAt(node.getEnd()));
		}
	}

	private static Diagnostic getDiagnosticFromInvalidSchematron(String schemaPath) {
		// FIXME: place the error on the xml-model
		Diagnostic d = new Diagnostic(ZERO_RANGE, "Schema " + schemaPath + " is invalid");
		d.setCode("bad-schematron");
		return d;
	}

	private static Diagnostic getDiagnosticFromSchematronThatBreaksSchxslt(String schemaPath) {
		// FIXME: place the error on the xml-model
		Diagnostic d = new Diagnostic(ZERO_RANGE, "The schema parser encountered an error while trying to parse " + schemaPath);
		d.setCode("schematron-parser-error");
		return d;
	}

}
