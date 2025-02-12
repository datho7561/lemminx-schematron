package com.github.datho7561;

import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Range;

public class TestUtils {

	private TestUtils() {
		//empty
	}

	/**
	 * Returns a diagnostic with the given values
	 *
	 * @param range the range of the diagnostic
	 * @param message the message of the diagnostic
	 * @param code the code of the diagnostic
	 * @return a diagnostic with the given values
	 */
	public static Diagnostic d(Range range, String message, String code) {
		Diagnostic diagnostic = new Diagnostic(range, message);
		diagnostic.setCode(code);
		return diagnostic;
	}

}
