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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.extensions.contentmodel.model.ContentModelManager;
import org.eclipse.lemminx.extensions.contentmodel.model.ReferencedGrammarInfo;
import org.eclipse.lemminx.extensions.contentmodel.settings.XMLValidationSettings;
import org.eclipse.lemminx.services.extensions.XMLExtensionsRegistry;
import org.eclipse.lemminx.services.extensions.diagnostics.IDiagnosticsParticipant;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;

/**
 * Provide diagnostics based on Schematron schemas referenced through
 * <code>&lt;?xml-model ...&gt;</code>
 *
 * @author datho7561
 */
public class SchematronDiagnosticsParticipant implements IDiagnosticsParticipant {

	private final Logger LOGGER = Logger.getLogger(SchematronDiagnosticsParticipant.class.getName());

	private SchematronDocumentValidator validator = new SchematronDocumentValidator();
	private ContentModelManagerManager contentModelManagerManager;

	public SchematronDiagnosticsParticipant(XMLExtensionsRegistry registry) {
		this.contentModelManagerManager = new ContentModelManagerManager(registry);
	}

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
	 * @param xmlDocument the DOMDocument of the file to validate
	 * @return a list of the resolved location of all the schemas as URIs, or null
	 *         if there are no schemas
	 */
	private List<File> getSchemaFiles(DOMDocument xmlDocument) {
		ContentModelManager contentModelManager = contentModelManagerManager.getContentModelManager();
		if (contentModelManager == null) {
			return Collections.emptyList();
		}

		Set<ReferencedGrammarInfo> grammars = contentModelManager.getReferencedGrammarInfos(xmlDocument);
		List<File> schematronFiles = new ArrayList<>();

		for (ReferencedGrammarInfo grammar : grammars) {
			if (grammar != null && grammar.getIdentifier() != null
					&& "xml-model".equals(grammar.getIdentifier().getKind())) {
				try {
					File schematronFile = new File(new URI(grammar.getResolvedURIInfo().getResolvedURI()));
					if (schematronFile.exists()) {
						schematronFiles.add(schematronFile);
					}
				} catch (URISyntaxException e) {
					LOGGER.warning("Encountered invalid grammar file URI: \""
							+ grammar.getResolvedURIInfo().getResolvedURI() + "\"");
				}
			}
		}

		return schematronFiles.size() > 0 ? schematronFiles : null;
	}

}
