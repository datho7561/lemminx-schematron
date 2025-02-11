package com.github.datho7561.schematron;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.lemminx.dom.DOMDocument;
import org.eclipse.lemminx.dom.DOMElement;
import org.eclipse.lemminx.dom.DOMNode;
import org.eclipse.lemminx.dom.XMLModel;
import org.eclipse.lemminx.extensions.contentmodel.model.CMDocument;
import org.eclipse.lemminx.extensions.contentmodel.model.CMElementDeclaration;
import org.eclipse.lemminx.extensions.contentmodel.model.ContentModelProvider;
import org.eclipse.lemminx.uriresolver.URIResolverExtensionManager;
import org.eclipse.lsp4j.LocationLink;

public class SchematronModelProvider implements ContentModelProvider {

	public static final String SCHEMATRON_XML_MODEL_BINDING_KIND = "schematron-xml-model";

	private final URIResolverExtensionManager resolverExtensionManager;

	public SchematronModelProvider(URIResolverExtensionManager resolverExtensionManager) {
		this.resolverExtensionManager = resolverExtensionManager;
	}

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
		if (internal) {
			return false;
		}
		return document.getXMLModels().stream().anyMatch(SchematronModelProvider::isApplicable);
	}

	@Override
	public boolean adaptFor(String uri) {
		return uri.endsWith(".sch");
	}

	@Override
	public Collection<Identifier> getIdentifiers(DOMDocument xmlDocument, String namespaceURI) {
		List<XMLModel> xmlModels = xmlDocument.getXMLModels();
		if (xmlModels.isEmpty()) {
			return Collections.emptyList();
		}
		Collection<Identifier> identifiers = new ArrayList<>();
		for (XMLModel xmlModel : xmlModels) {
			if (isApplicable(xmlModel)) {
				identifiers.add(new Identifier(null, xmlModel.getHref(), xmlModel.getHrefNode(), SCHEMATRON_XML_MODEL_BINDING_KIND));
			}
		}
		return identifiers;
	}

	@Override
	public CMDocument createCMDocument(String key, boolean resolveExternalEntities) {
		return new SchematronCMDocument();
	}

	@Override
	public CMDocument createInternalCMDocument(DOMDocument xmlDocument, boolean resolveExternalEntities) {
		return new SchematronCMDocument();
	}

	private static boolean isApplicable(XMLModel xmlModel) {
		String href = xmlModel.getHref();
		if (href == null) {
			return false;
		}
		return href.endsWith(".sch");
	}

}
