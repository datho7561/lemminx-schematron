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

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.extensions.contentmodel.settings.XMLValidationSettings;
import org.eclipse.lemminx.services.extensions.diagnostics.IDiagnosticsParticipant;
import org.eclipse.lemminx.uriresolver.CacheResourcesManager;
import org.eclipse.lemminx.uriresolver.CacheResourcesManager.ResourceToDeploy;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;

/**
 * Provides diagnostics in Schematron schemas based on the rules in
 * `schematron.sch` (accessible under the Schematron GitHub organization).
 */
public class SchematronMetaschemaSchDiagnosticsParticipant implements IDiagnosticsParticipant {

	private static final Logger LOGGER = Logger.getLogger(SchematronMetaschemaSchDiagnosticsParticipant.class.getName());

	private SchematronDocumentValidator validator = new SchematronDocumentValidator();

	private static final ResourceToDeploy SCHEMATRON_METASCHEMA_RESOURCE = new ResourceToDeploy(Constants.SCHEMATRON_SCH,
			"schemas/schematron.sch");

	@Override
	public void doDiagnostics(DOMDocument xmlDocument, List<Diagnostic> diagnostics,
			XMLValidationSettings validationSettings, CancelChecker cancelChecker) {
		if (!Constants.SCHEMATRON_NAMESPACE_URI.equals(xmlDocument.getNamespaceURI())) {
			return;
		}
		try {
			diagnostics.addAll(validator.validate(xmlDocument, List.of(CacheResourcesManager.getResourceCachePath(SCHEMATRON_METASCHEMA_RESOURCE).toFile()), cancelChecker));
		} catch (IOException e) {
			LOGGER.throwing(SchematronMetaschemaSchDiagnosticsParticipant.class.getName(), "doDiagnostics", e);
		}
	}

}
