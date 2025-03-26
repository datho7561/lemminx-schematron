import * as assert from "assert";
import * as fs from "fs/promises";
import { tmpdir } from "os";
import path from "path";
import * as vscode from "vscode";

suite("Extension Test Suite", function () {
  vscode.window.showInformationMessage("Start all tests.");

  // diagnostics take some time to appear; the language server must be started and respond to file open event
  const DIAGNOSTICS_DELAY = 1_000;

  const SCHEMA = `<?xml version="1.0" encoding="UTF-8"?>
    <schema xmlns="http://purl.oclc.org/dsdl/schematron">
      <pattern name="Check Cart structure">
        <rule context="Cart">
          <assert test="@UUID">The cart must have a UUID</assert>
          <assert test="count(*) = count(Dress)">Only dresses are allowed in the cart</assert>
        </rule>
      </pattern>
      <pattern name="check uuid">
        <rule context="Cart/@UUID">
          <assert test="string-length(.) = 36">The UUID should be 36 characters long</assert>
          <!-- https://stackoverflow.com/a/12301127 -->
          <assert test="translate(., translate(., '-', ''), '')='----'">The UUID should have 4 hyphens</assert>
        </rule>
      </pattern>
      <pattern name="Check Dress Structure">
        <rule context="Dress">
          <assert test="(@Size and @Colour) or @SKU">Dresses must be identified by their SKU, or by their size and colour</assert>
        </rule>
      </pattern>
      <pattern name="Check Dress Size">
        <rule context="Dress/@Size">
          <assert test="(. = 'S' or . = 'M' or . = 'L' or . = 'XL') or (floor(.) = number(.) and number(.) &gt;= 0 and number(.) &lt;= 30)">The size must be S, M, L, or XL, or a number between 0 and 30.</assert>
        </rule>
      </pattern>
    </schema>
    `;

  const SCHEMA_INSTANCE = `<?xml version="1.0" encoding="UTF-8"?>
    <?xml-model href="dress-size.sch" type="application/xml"?>
    <Cart UUID="00000000-0000-0000-0000-000000000000">
        <Dress Colour="Burgandy" Size=""></Dress>
        <Dress SKU="37"></Dress>
    </Cart>`;

  const SCHEMA_FILENAME = "dress-size.sch";
  const SCHEMA_INSTANCE_NAME = "references-schema.xml";

  let tempDir: string;
  let schemaPath: string;
  let schemaInstancePath: string;

  this.beforeAll(async function () {
    tempDir = tmpdir();
    schemaPath = path.join(tempDir, SCHEMA_FILENAME);
    schemaInstancePath = path.join(tempDir, SCHEMA_INSTANCE_NAME);
    await fs.appendFile(schemaPath, SCHEMA);
    await fs.appendFile(schemaInstancePath, SCHEMA_INSTANCE);
  });

  this.afterAll(async function () {
    await fs.rm(schemaPath);
    await fs.rm(schemaInstancePath);
  });

  test("schema has right diagnostics", async function () {
    const textDocument = await vscode.workspace.openTextDocument(schemaPath);
    await vscode.window.showTextDocument(textDocument);
    await new Promise(resolve => setTimeout(resolve, DIAGNOSTICS_DELAY));
    const diagnostics = vscode.languages.getDiagnostics(vscode.Uri.file(schemaPath));

    // the "name" props on the "pattern" elt are technically not allowed
    // schxslt ignores them, but the RelaxNG schema won't
    assert.strictEqual(4, diagnostics.length);
    for (let i = 0; i < 4; i++) {
      assert.strictEqual('attribute "name" not allowed here; expected attribute "abstract", "documents", "fpi", "icon", "id", "is-a", "see", "xml:lang" or "xml:space" or an attribute from another namespace', diagnostics[i].message);
    }
  });

  test("instance has right diagnostics", async function () {
    const textDocument = await vscode.workspace.openTextDocument(schemaInstancePath);
    await vscode.window.showTextDocument(textDocument);
    await new Promise(resolve => setTimeout(resolve, DIAGNOSTICS_DELAY));
    const diagnostics = vscode.languages.getDiagnostics(vscode.Uri.file(schemaInstancePath));

    assert.strictEqual(1, diagnostics.length);
    assert.strictEqual("The size must be S, M, L, or XL, or a number between 0 and 30.", diagnostics[0].message);
  });
});
