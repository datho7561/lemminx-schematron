package com.github.datho7561;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
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

import name.dmaus.schxslt.Result;
import name.dmaus.schxslt.Schematron;
import name.dmaus.schxslt.SchematronException;

public class SchematronDocumentValidator {

	private static final Range ZERO_RANGE = new Range(new Position(0, 0), new Position(0, 1));

	// Example:
	// failed-assert /Q{}Person[1] If the Title is "Mr" then the gender of the
	// person must be "Male".
	private static final Pattern SCHEMATRON_MESSAGE_DECODER = Pattern.compile("failed-assert ([^ ]+) (.*)");

	private final XPathFactory xpathFactory = XPathFactory.newInstance();

	Logger LOGGER = Logger.getLogger(SchematronDocumentValidator.class.getName());

	public List<Diagnostic> validate(DOMDocument xmlDocument, List<File> schemaFiles, CancelChecker cancelChecker) {
		List<Diagnostic> diagnostics = new ArrayList<>();
		try {
			URI xmlDocumentURI = new URI(xmlDocument.getDocumentURI());
			File xmlDocumentFile = new File(xmlDocumentURI);
			for (File schema : schemaFiles) {
				Schematron schematron = null;
				try {
					schematron = new Schematron(new StreamSource(schema));
				} catch (RuntimeException e) {
					diagnostics.add(getDiagnosticFromInvalidSchematron(schema.getAbsolutePath()));
				}
				if (schematron != null) {
					try {
						Result validationResult = schematron.validate(new StreamSource(xmlDocumentFile));
						if (!validationResult.isValid()) {
							for (String message : validationResult.getValidationMessages()) {
								diagnostics.add(getSchematronMessageAsDiagnostic(message, xmlDocument, xmlDocumentFile));
							}
						}
					} catch (SchematronException e) {
						diagnostics.add(getDiagnosticFromInvalidSchematron(schema.getAbsolutePath()));
					}
				}
				cancelChecker.checkCanceled();
			}
		} catch (URISyntaxException uriException) {
			LOGGER.log(Level.SEVERE, "Unable to turn document URI into a URI", uriException);
		}
		return diagnostics;
	}

	private Diagnostic getSchematronMessageAsDiagnostic(String message, DOMDocument xmlDocument, File xmlDocumentFile) {
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
		Diagnostic d = new Diagnostic(ZERO_RANGE, "Schema " + schemaPath + " is invalid");
		d.setCode("bad-schematron");
		return d;
	}

}
