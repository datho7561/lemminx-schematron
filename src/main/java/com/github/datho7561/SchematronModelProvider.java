package com.github.datho7561;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.extensions.contentmodel.model.CMDocument;
import org.eclipse.lemminx.extensions.contentmodel.model.CMElementDeclaration;
import org.eclipse.lemminx.extensions.contentmodel.model.ContentModelProvider;
import org.eclipse.lsp4j.LocationLink;

public class SchematronModelProvider implements ContentModelProvider {

	public static class SchematronCMDocument implements CMDocument {

		@Override
		public boolean hasNamespace(String namespaceURI) {
			return false;
		}

		@Override
		public Collection<CMElementDeclaration> getElements() {
			return Collections.emptyList();
		}

		@Override
		public CMElementDeclaration findCMElement(DOMElement element, String namespace) {
			return null;
		}

		@Override
		public LocationLink findTypeLocation(DOMNode node) {
			return null;
		}

		@Override
		public boolean isDirty() {
			return false;
		}

	}

	@Override
	public boolean adaptFor(DOMDocument document, boolean internal) {
		return adaptFor(document.getBaseURI());
	}

	@Override
	public boolean adaptFor(String uri) {
		return uri.endsWith(".sch");
	}

	@Override
	public Collection<Identifier> getIdentifiers(DOMDocument xmlDocument, String namespaceURI) {
		return null;
	}

	@Override
	public CMDocument createCMDocument(String key, boolean resolveExternalEntities) {
		return new SchematronCMDocument();
	}

	@Override
	public CMDocument createInternalCMDocument(DOMDocument xmlDocument, boolean resolveExternalEntities) {
		return new SchematronCMDocument();
	}


}
