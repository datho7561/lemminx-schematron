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

import org.eclipse.lemminx.uriresolver.CacheResourcesManager;
import org.eclipse.lemminx.uriresolver.CacheResourcesManager.ResourceToDeploy;
import org.eclipse.lemminx.uriresolver.URIResolverExtension;

/**
 * URI resolver for the rnc-based metaschema for Schematron.
 */
public class SchematronMetaschemaResolverParticipant implements URIResolverExtension {

	private static final ResourceToDeploy SCHEMATRON_METASCHEMA_RESOURCE = new ResourceToDeploy(Constants.SCHEMATRON_RNC,
			"schemas/schematron.rnc");

	@Override
	public String resolve(String baseLocation, String publicId, String systemId) {
		if (!Constants.SCHEMATRON_NAMESPACE_URI.equals(publicId)) {
			return null;
		}
		try {
			return CacheResourcesManager.getResourceCachePath(SCHEMATRON_METASCHEMA_RESOURCE).toFile().toURI().toString();
		} catch (Exception e) {
			// Do nothing?
		}
		return Constants.SCHEMATRON_RNC;
	}

	@Override
	public String getName() {
		return "schematron metaschema";
	}

}
