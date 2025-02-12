# VS Code Lemminx Schematron

This extension extends Red Hat's vscode-xml with support for validating against Schematron schemas.

Under the hood, it uses schxslt and Saxon-HE in order to perform validation.

This is a proof of concept; it's not supported in any capacity, nor is it official in any capacity.

## Features

### Schematron validation

Given the local schema `assert-gender-title.sch`:

```xml
<schema xmlns="http://purl.oclc.org/dsdl/schematron">
  <pattern name="Check structure">
    <rule context="Person">
      <assert test="@Honourific">The element Person must have an Honourific attribute</assert>
      <assert test="count(*) = 2 and count(Name) = 1 and count(Gender) = 1">The element Person should have the child elements Name and Gender.</assert>
      <assert test="*[1] = Name">The element Name must appear before element Gender.</assert>
    </rule>
  </pattern>
  <pattern name="Check co-occurrence constraints">
    <rule context="Person">
      <assert test="(@Honourific = 'Mr' and Gender = 'Male') or @Honourific != 'Mr'">If the honourific "Mr" is used, the gender must be "Male".</assert>
    </rule>
  </pattern>
</schema>
```

*I think it's fine to use whatever honourific regardless of your gender, but this Schematron sure has different opinions, and it's one of the simple example schemas floating around on the internet. I'll try to remember to replace it with a better example later.*

You can associate this schema to your XML document using the `<?xml-model ...?>` prolog instruction:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<?xml-model
    href="./assert-title-gender.sch"
    type="application/xml"?>
<Person Title="Mr">
    <Name>Mira</Name>
    <Gender>Female</Gender>
</Person>
```

You will get validation based on the Schematron rules:

![Validation for the XML Document against the schema. The assertion error: "If the honourific "Mr" is used, the gender must be "Male"." appears](./images/validation.png)

## Requirements

- Red Hat's vscode-xml extension
- Java 11 or newer (it doesn't work with the binary mode)

## Known Issues

- Remote schema loading probably doesn't work

## Release Notes

See the [CHANGELOG](CHANGELOG.md)