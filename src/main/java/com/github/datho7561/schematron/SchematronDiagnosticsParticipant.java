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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.Map;

import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.extensions.contentmodel.model.ContentModelManager;
import org.eclipse.lemminx.extensions.contentmodel.model.ReferencedGrammarInfo;
import org.eclipse.lemminx.extensions.contentmodel.settings.XMLValidationSettings;
import org.eclipse.lemminx.services.extensions.XMLExtensionsRegistry;
import org.eclipse.lemminx.services.extensions.diagnostics.IDiagnosticsParticipant;
import org.eclipse.lemminx.uriresolver.CacheResourcesManager;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.jsonrpc.CancelChecker;

import com.github.datho7561.common.ContentModelManagerManager;

/**
 * Provide diagnostics based on Schematron schemas referenced through
 * <code>&lt;?xml-model ...&gt;</code>
 *
 * @author datho7561
 */
public class SchematronDiagnosticsParticipant implements IDiagnosticsParticipant {

	private static final Logger LOGGER = Logger.getLogger(SchematronDiagnosticsParticipant.class.getName());

	private SchematronDocumentValidator validator = new SchematronDocumentValidator();
	private ContentModelManagerManager contentModelManagerManager;

	public SchematronDiagnosticsParticipant(XMLExtensionsRegistry registry) {
		this.contentModelManagerManager = ContentModelManagerManager.getInstance(registry);
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
		Map<String, File> schematronFiles = new HashMap<>();

		for (ReferencedGrammarInfo grammar : grammars) {
			if (grammar != null && grammar.getIdentifier() != null
					&& SchematronModelProvider.SCHEMATRON_XML_MODEL_BINDING_KIND.equals(grammar.getIdentifier().getKind())) {
				URI uri = null;
				try {
					uri = new URI(grammar.getResolvedURIInfo().getResolvedURI());
				} catch (URISyntaxException e) {
					LOGGER.warning("Encountered invalid grammar file URI: \""
							+ grammar.getResolvedURIInfo().getResolvedURI() + "\"");
				}
				if (uri != null) {
					switch (uri.getScheme()) {
						case "file":
							collectLocalSchematron(uri, schematronFiles);
							break;
						case "http":
						case "https":
						case "ftp":
							collectRemoteSchematron(uri, schematronFiles, contentModelManager);
							break;
					}
				}
			}
		}

		return !schematronFiles.entrySet().isEmpty() ? schematronFiles.values().stream().collect(Collectors.toList()) : null;
	}

	private static void collectLocalSchematron(URI uri, Map<String, File> schematronFiles) {
		if (schematronFiles.containsKey(uri.toString())) {
			return;
		}
		File file = new File(uri);
		if (file.exists()) {
			schematronFiles.put(uri.toString(), file);
		}
	}

	private static void collectRemoteSchematron(URI uri, Map<String, File> schematronFiles,
			ContentModelManager contentModelManager) {
		if (schematronFiles.containsKey(uri.toString())) {
			return;
		}
		try {
			Path cachedFilePath = CacheResourcesManager.getResourceCachePath(uri.toString());
			File file = cachedFilePath.toFile();
			if (file.exists()) {
				schematronFiles.put(uri.toString(), file);
			}
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Error while collecting Schematron `" + uri + "`: ", e);
		}
	}

}
