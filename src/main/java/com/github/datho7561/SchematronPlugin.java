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

import org.eclipse.lemminx.services.extensions.IXMLExtension;
import org.eclipse.lemminx.services.extensions.XMLExtensionsRegistry;
import org.eclipse.lemminx.services.extensions.diagnostics.IDiagnosticsParticipant;
import org.eclipse.lemminx.services.extensions.save.ISaveContext;
import org.eclipse.lsp4j.InitializeParams;

/**
 * Schematron extension. It supports:
 * <ul>
 * <li>Validation using Schematron files referenced through
 * <code>.sch</code></li>
 * </ul>
 */
public class SchematronPlugin implements IXMLExtension {

	private IDiagnosticsParticipant diagnosticsParticipant;

	@Override
	public void doSave(ISaveContext context) {
		context.collectDocumentToValidate(document -> true);
	}

	@Override
	public void start(InitializeParams params, XMLExtensionsRegistry registry) {
		diagnosticsParticipant = new SchematronDiagnosticsParticipant(registry);
		registry.registerDiagnosticsParticipant(diagnosticsParticipant);
	}

	@Override
	public void stop(XMLExtensionsRegistry registry) {
		registry.unregisterDiagnosticsParticipant(diagnosticsParticipant);
	}

}
