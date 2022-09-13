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

import org.eclipse.lemminx.services.extensions.XMLExtensionsRegistry;

import com.github.datho7561.common.AbstractXmlModelDiagnosticsParticipant;
import com.github.datho7561.common.ISchemaBasedValidator;

/**
 * Provide diagnostics based on Schematron schemas referenced through
 * <code>&lt;?xml-model ...&gt;</code>
 *
 * @author datho7561
 */
public class SchematronDiagnosticsParticipant extends AbstractXmlModelDiagnosticsParticipant {

	private static final String FILE_EXT = ".sch";

	private final ISchemaBasedValidator validator;

	public SchematronDiagnosticsParticipant(XMLExtensionsRegistry registry) {
		super(registry);
		validator = new SchematronDocumentValidator();
	}

	@Override
	protected String getSchemaExtension() {
		return FILE_EXT;
	}

	@Override
	protected ISchemaBasedValidator getValidator() {
		return validator;
	}

}
