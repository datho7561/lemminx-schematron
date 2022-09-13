package com.github.datho7561.relaxng;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.SAXParserFactory;

import org.eclipse.lemminx.commons.BadLocationException;
import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.utils.XMLPositionUtility;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;
import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.github.datho7561.common.ISchemaBasedValidator;

public class RelaxngDocumentValidator implements ISchemaBasedValidator {

	private static final Logger LOGGER = Logger.getLogger(RelaxngDocumentValidator.class.getName());

	private final VerifierFactory verifierFactory;

	public RelaxngDocumentValidator() {
		verifierFactory = new com.sun.msv.verifier.jarv.TheFactoryImpl();
	}

	@Override
	public List<Diagnostic> validate(DOMDocument xmlDocument, List<File> schemaFiles, CancelChecker cancelChecker) {
		List<Diagnostic> diagnostics = new ArrayList<>();

		for (File schemaFile : schemaFiles) {
			try {
				Verifier verifier = verifierFactory.newVerifier(schemaFile);
				verifier.setErrorHandler(new ErrorHandlerImplementation(diagnostics, xmlDocument));


				SAXParserFactory parserFactory = SAXParserFactory.newInstance();
				parserFactory.setNamespaceAware(true);
				XMLReader reader = parserFactory.newSAXParser().getXMLReader();

				// then setup the SAX pipe line as follows:
				//
				//  parser ==> interceptor ==> verifier
				//
				// "interceptor" works as a SAX filter.
				reader.setContentHandler(verifier.getVerifierHandler());
				reader.parse(new InputSource(new StringReader(xmlDocument.getText())));
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Error while validating using RelaxNG schema: ", e);
			}
		}

		return diagnostics;
	}

	private static Diagnostic getDiagnosticFromSAXParseException(SAXParseException exception,
			DiagnosticSeverity severity, DOMDocument xmlDocument) {
		Diagnostic diagnostic = new Diagnostic();
		diagnostic.setSeverity(severity);
		diagnostic.setMessage(exception.getMessage());
		diagnostic.setSource("relaxng");
		// reports line and column start at 1
		int col = exception.getColumnNumber() - 1;
		int line = exception.getLineNumber() - 1;
		Position p = new Position(line, col);
		try {
			int offset = xmlDocument.offsetAt(p);
			Range r = XMLPositionUtility.selectStartTagName(offset, xmlDocument);
			diagnostic.setRange(r);
		} catch (BadLocationException e) {
			Range r = new Range(p, p);
			diagnostic.setRange(r);
		}
		return diagnostic;
	}

	private final class ErrorHandlerImplementation implements ErrorHandler {
		private final List<Diagnostic> diagnostics;
		private final DOMDocument document;

		private ErrorHandlerImplementation(List<Diagnostic> diagnostics, DOMDocument xmlDocument) {
			this.diagnostics = diagnostics;
			this.document = xmlDocument;
		}

		@Override
		public void warning(SAXParseException exception) throws SAXException {
			diagnostics.add(getDiagnosticFromSAXParseException(exception, DiagnosticSeverity.Warning, document));
		}

		@Override
		public void error(SAXParseException exception) throws SAXException {
			diagnostics.add(getDiagnosticFromSAXParseException(exception, DiagnosticSeverity.Error, document));
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			diagnostics.add(getDiagnosticFromSAXParseException(exception, DiagnosticSeverity.Error, document));
		}
	}

}
