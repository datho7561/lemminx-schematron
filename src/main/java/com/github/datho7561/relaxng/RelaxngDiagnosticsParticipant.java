/*******************************************************************************
* Copyright (c) 2022 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package com.github.datho7561.relaxng;

import org.eclipse.lemminx.services.extensions.XMLExtensionsRegistry;

import com.github.datho7561.common.AbstractXmlModelDiagnosticsParticipant;
import com.github.datho7561.common.ISchemaBasedValidator;

public class RelaxngDiagnosticsParticipant extends AbstractXmlModelDiagnosticsParticipant {

	private static final String SCHEMA_EXT = ".rng";

	private final ISchemaBasedValidator validator;

	public RelaxngDiagnosticsParticipant(XMLExtensionsRegistry registry) {
		super(registry);
		validator = new RelaxngDocumentValidator();
	}

	@Override
	protected ISchemaBasedValidator getValidator() {
		return validator;
	}

	@Override
	protected String getSchemaExtension() {
		return SCHEMA_EXT;
	}
}
