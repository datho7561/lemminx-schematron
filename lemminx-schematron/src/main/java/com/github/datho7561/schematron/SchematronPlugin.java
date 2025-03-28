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

import org.eclipse.lemminx.extensions.contentmodel.model.ContentModelProvider;
import org.eclipse.lemminx.services.extensions.IXMLExtension;
import org.eclipse.lemminx.services.extensions.XMLExtensionsRegistry;
import org.eclipse.lemminx.services.extensions.diagnostics.IDiagnosticsParticipant;
import org.eclipse.lemminx.services.extensions.save.ISaveContext;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lemminx.uriresolver.URIResolverExtension;

import com.github.datho7561.common.ContentModelManagerManager;

/**
 * Schematron extension. It supports:
 * <ul>
 * <li>Validation using Schematron files referenced through
 * <code>.sch</code></li>
 * </ul>
 */
public class SchematronPlugin implements IXMLExtension {

	private IDiagnosticsParticipant diagnosticsParticipant;
	private IDiagnosticsParticipant metaschemaSchDiagnosticsParticipant;
	private URIResolverExtension metaschemaRncURIResolverParticipant;
	private ContentModelManagerManager contentModelManagerManager;

	@Override
	public void doSave(ISaveContext context) {
		context.collectDocumentToValidate(document -> true);
	}

	@Override
	public void start(InitializeParams params, XMLExtensionsRegistry registry) {
		diagnosticsParticipant = new SchematronDiagnosticsParticipant(registry);
		registry.registerDiagnosticsParticipant(diagnosticsParticipant);
		metaschemaSchDiagnosticsParticipant = new SchematronMetaschemaSchDiagnosticsParticipant();
		registry.registerDiagnosticsParticipant(metaschemaSchDiagnosticsParticipant);
		metaschemaRncURIResolverParticipant = new SchematronMetaschemaResolverParticipant();
		registry.getResolverExtensionManager().registerResolver(metaschemaRncURIResolverParticipant);

		ContentModelProvider modelProvider = new SchematronModelProvider();
		contentModelManagerManager = ContentModelManagerManager.getInstance(registry);
		contentModelManagerManager.registerContentModelListener((cm) -> {
			cm.registerModelProvider(modelProvider);
		});
	}

	@Override
	public void stop(XMLExtensionsRegistry registry) {
		registry.unregisterDiagnosticsParticipant(diagnosticsParticipant);
		registry.unregisterDiagnosticsParticipant(metaschemaSchDiagnosticsParticipant);
		registry.getResolverExtensionManager().unregisterResolver(metaschemaRncURIResolverParticipant);
	}

}
