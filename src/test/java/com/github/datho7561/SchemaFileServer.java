package com.github.datho7561;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.lemminx.uriresolver.ModifiedResourceHandler;

public class SchemaFileServer {

	private Server server;

	public SchemaFileServer() throws Exception {
		ResourceHandler resourceHandler = new ModifiedResourceHandler();
		resourceHandler.setResourceBase(ProjectUtils.getProjectDirectory().resolve("src/test/resources/schematron").toUri().toString());
		resourceHandler.setDirectoriesListed(true);
		this.server = new Server(0);
		this.server.setHandler(new HandlerList(resourceHandler, new DefaultHandler()));
		if (!server.isStarted() && !server.isStarting()) {
			this.server.start();
		}
	}

	public void stop() throws Exception {
		server.stop();
	}

	public int getPort() {
		return ((ServerConnector) server.getConnectors()[0]).getLocalPort();
	}

}
