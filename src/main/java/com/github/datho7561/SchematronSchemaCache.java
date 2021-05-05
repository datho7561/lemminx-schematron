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

import java.net.URI;
import java.nio.file.Path;

/**
 * Used to cache schemas from the internet locally to increase validation speed
 */
public class SchematronSchemaCache {

	/**
	 * Returns a path to the cached version of the schema
	 *
	 * @param schema the schema to find the cached version of
	 * @return a path to the cached version of the schema
	 */
	public Path getCachedResource(URI schema) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
