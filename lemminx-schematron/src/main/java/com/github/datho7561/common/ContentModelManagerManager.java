/*******************************************************************************
* Copyright (c) 2021 Red Hat Inc. and others.
* All rights reserved. This program and the accompanying materials
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v20.html
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package com.github.datho7561.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.lemminx.extensions.contentmodel.ContentModelPlugin;
import org.eclipse.lemminx.extensions.contentmodel.model.ContentModelManager;
import org.eclipse.lemminx.services.extensions.IXMLExtension;
import org.eclipse.lemminx.services.extensions.XMLExtensionsRegistry;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Manages access to LemMinX's ContentModelManager
 *
 * @author datho7561
 */
public class ContentModelManagerManager {

	private final XMLExtensionsRegistry registry;
	private ContentModelManager contentModelManager = null;
	private List<Consumer<ContentModelManager>> contentModelManagerListeners;

	private static ContentModelManagerManager INSTANCE;

	public static synchronized ContentModelManagerManager getInstance(XMLExtensionsRegistry registry) {
		if (INSTANCE != null) {
			return INSTANCE;
		}
		INSTANCE = new ContentModelManagerManager(registry);
		return INSTANCE;
	}

	private ContentModelManagerManager(XMLExtensionsRegistry registry) {
		this.registry = registry;
		this.contentModelManagerListeners = new ArrayList<>();
	}

	/**
	 * Returns LemMinX's ContentModelManager or null if it is not yet available
	 *
	 * @return LemMinX's ContentModelManager or null if it is not yet available
	 */
	@SuppressFBWarnings({"EI_EXPOSE_REP"})
	public ContentModelManager getContentModelManager() {
		if (contentModelManager != null) {
			return contentModelManager;
		}
		for (IXMLExtension extension : registry.getExtensions()) {
			if (extension instanceof ContentModelPlugin) {
				contentModelManager = ((ContentModelPlugin) extension).getContentModelManager();
				break;
			}
		}
		for (Consumer<ContentModelManager> listener : contentModelManagerListeners) {
			listener.accept(contentModelManager);
		}
		contentModelManagerListeners = null;
		return contentModelManager;
	}

	public void registerContentModelListener(Consumer<ContentModelManager> fn) {
		if (contentModelManager != null) {
			fn.accept(contentModelManager);
			return;
		}
		contentModelManagerListeners.add(fn);
	}

}
