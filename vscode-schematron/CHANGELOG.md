# Changelog

## [0.2.0](https://github.com/datho7561/lemminx-schematron/milestone/2?closed=1) - 3 April, 2025

- Require Java 17
- Update SchXslt Java bindings to 4.0 (requires Java 17)
- Associate Schematron files with [the Schematron RelaxNG schema](https://github.com/Schematron/schema/blob/main/schematron.rnc),
  allowing for validation and autocompletion in Schematron schemas
- Validate Schematron files against [the Schematon `.sch` schema](https://github.com/Schematron/schema/blob/main/schematron.sch)
- Show a warning when a referenced Schematron schema doesn't exist
- Place "invalid schema" errors on the `<?xml-model` processing instruction that they are referenced from
- Fix bug where errors for elements with a non-default namespace had the wrong range
- Add smoke tests for the VS Code extension

## [0.1.0](https://github.com/datho7561/lemminx-schematron/milestone/1?closed=1) - 28 February, 2025

- Initial release
