package com.github.datho7561;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.lemminx.XMLAssert;
import org.eclipse.lemminx.utils.FilesUtils;

public class ProjectUtils {
	/**
	 * @return the current lemminx project directory
	 */
	public static Path getProjectDirectory() {
		String xmlAssertClass = ProjectUtils.class.getName().replace('.', '/') + ".class"; // "org/eclipse/lemminx/XMLAssert.class"
		String currPath = new File(ProjectUtils.class.getClassLoader().getResource(xmlAssertClass).getPath())
				.toString();
		Path dir = FilesUtils.getPath(currPath);
		while (!Files.exists(dir.resolve("pom.xml")) && dir.getParent() != null) {
			dir = dir.getParent();
		}
		return dir;
	}

}
