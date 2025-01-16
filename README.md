# What is this?

This is a proof of concept for a [Schematron](https://schematron.com/) extension to the [LemMinX](https://www.github.com/eclipse/lemminx) XML language server.

It uses [schxslt](https://github.com/schxslt/schxslt) and [Saxon-HE](https://saxonica.plan.io/projects/saxonmirrorhe/repository) in order to validate an XML document against a schema.

## Usage

1. In VS Code, install Red Hat's XML extension
2. Run `mvn clean package`, which generates a shaded uber jar in `./target/lemminx-schematron-0.1.0-SNAPSHOT.jar`
3. Add the following to your `package.json`:
  ```json
  "xml.extension.jars": [
    "/path/to/lemminx-schematron/lemminx-schematron-0.1.0-SNAPSHOT.jar",
  ],
  ```
4. Add the following snippet to your XML, with `href` pointing to your Schematron schema:
  ```xml
  <?xml-model href="./my-schematron.sch"?>
  ```
5. The XML document should be validated. If the schema file is invalid,
  a diagnostic will appear at the top of the document indicating this.

## Limitations

* Requires Java 11
* You can only validate with local `.sch` files
* Not tested thoroughly
* The `.jar` is prohibitively large to include as a part of the standard LemMinX distribution