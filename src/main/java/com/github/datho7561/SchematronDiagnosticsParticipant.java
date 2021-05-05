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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.XMLModel;
import org.eclipse.lemminx.extensions.contentmodel.settings.XMLValidationSettings;
import org.eclipse.lemminx.services.extensions.diagnostics.IDiagnosticsParticipant;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;

/**
 * Provide diagnostics based on Schematron schemas referenced through
 * <code>&lt;?xml-model ...&gt;</code>
 */
public class SchematronDiagnosticsParticipant implements IDiagnosticsParticipant {

	private final Logger LOGGER = Logger.getLogger(SchematronDiagnosticsParticipant.class.getName());

	private SchematronSchemaCache cache = new SchematronSchemaCache();
	private SchematronDocumentValidator validator = new SchematronDocumentValidator();

	@Override
	public void doDiagnostics(DOMDocument xmlDocument, List<Diagnostic> diagnostics,
			XMLValidationSettings validationSettings, CancelChecker cancelChecker) {
		List<File> files = getSchemaFiles(xmlDocument);
		if (files == null) {
			return;
		}
		cancelChecker.checkCanceled();
		diagnostics.addAll(validator.validate(xmlDocument, files, cancelChecker));
	}

	/**
	 * Returns a list of the resolved location of all the schemas as URIs, or null
	 * if there are no schemas
	 *
	 * @return a list of the resolved location of all the schemas as URIs, or null
	 *         if there are no schemas
	 */
	private List<File> getSchemaFiles(DOMDocument xmlDocument) {
		if (!xmlDocument.hasXMLModel()) {
			return null;
		}
		try {
			URI documentURI = new URI(xmlDocument.getDocumentURI());
			if (!documentURI.getScheme().startsWith("file")) {
				return null;
			}
			Path documentPath = Paths.get(documentURI.getPath());
			Path documentFolder = documentPath.getParent();

			List<File> files = new ArrayList<>();

			for (XMLModel xmlModel : xmlDocument.getXMLModels()) {
				String href = xmlModel.getHref();
				// Schematron schemas have the extension .sch
				if (!href.endsWith("sch")) {
					continue;
				}
				Path path = null;
				try {
					URI uri = new URI(href);
					String scheme = uri.getScheme();
					if (scheme != null && scheme.startsWith("http")) {
						try {
							path = cache.getCachedResource(uri);
						} catch (UnsupportedOperationException unsupported) {
							LOGGER.log(Level.WARNING, "Online schema usage not yet supported", unsupported);
							continue;
						}
					}
				} catch (URISyntaxException e) {
				}
				if (path == null) {
					path = Paths.get(href);
				}
				path = documentFolder.resolve(path);
				File file = new File(path.toString());
				if (file.exists()) {
					files.add(file);
				}
			}
			return files.size() > 0 ? files : null;
		} catch (URISyntaxException e) {
			return null;
		}
	}

}
