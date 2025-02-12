# What is this?

This is a proof of concept for a [Schematron](https://schematron.com/) extension to the [LemMinX](https://www.github.com/eclipse/lemminx) XML language server.

It uses [schxslt](https://github.com/schxslt/schxslt) and [Saxon-HE](https://saxonica.plan.io/projects/saxonmirrorhe/repository) in order to validate an XML document against a schema.

## Requirements

- Java 17+ (JRE or JDK) installed and set up on `PATH`.

## Usage

### VS Code (and forks/rebuilds of VS Code)

This project is available as an extension from [the official marketplace](https://marketplace.visualstudio.com/items?itemName=datho7561.vscode-lemminx-schematron)
and [OpenVSX](https://open-vsx.org/extension/datho7561/vscode-lemminx-schematron).

### Other Clients

1. Download the LemMinX uber `.jar` from [lemminx's maven repo](https://repo.eclipse.org/content/repositories/lemminx-releases/org/eclipse/lemminx/org.eclipse.lemminx/0.30.0/org.eclipse.lemminx-0.30.0-uber.jar).
   (If there is a newer version of lemminx than 0.30.0, replace the number in the URL to get it).
   Put it somewhere meaningful and memorable.
2. Build the LemMinX Schematron extension. Put the built `.jar` somewhere meaningful and memorable. See [Building lemminx extension](#lemminx-extension)
3. Setup LemMinX according to the instructions of your editor.
  Note that you cannot use the binary build of LemMinX, since the binary cannot be extended dynamically.
  When you get to the part in the setup instructions where you specify the command to launch the language server, move on to step 4.
4. For the launch command, you need to use Java, specify both jars as classpath entries, and specify `org.eclipse.lemminx.XMLServerLauncher` as the main class. eg:

`java -Xmx1G -cp /path/to/lemminx.jar:/path/to/lemminx-schematron.jar org.eclipse.lemminx.XMLServerLauncher`

`-Xmx1G` configures the max memory Java can use; feel free to reduce or increase this number according to your needs.
Note that the editor might want the arguments supplied as a list of strings, and may have separate fields for the command and the arguments.

#### Examples

__Neovim__

```lua
local lspconfig = require('lspconfig')
-- you should probably also specify `on_attach` and `capabilities` functions; I didn't here
lspconfig['lemminx'].setup {
  cmd = { 'java', '-cp', '/home/davthomp/Documents/lemminx-extensions/org.eclipse.lemminx-0.30.0-uber.jar:/home/davthomp/Documents/lemminx-extensions/lemminx-schematron-0.1.0-SNAPSHOT.jar', 'org.eclipse.lemminx.XMLServerLauncher' }
}
```

__Helix (in `languages.toml`)__

```toml
[[language]]
name = "xml"
file-types = [ "xml", "svg", "xsd", "xslt", "xsl", "sch" ]
auto-format = true
language-servers = [ "xml" ]

[language-server.xml]
command = "java"
args = ["-cp", "/home/davthomp/Documents/lemminx-extensions/lemminx-schematron-0.1.0-SNAPSHOT.jar:/home/davthomp/Documents/lemminx-extensions/org.eclipse.lemminx-0.30.0-uber.jar", "org.eclipse.lemminx.XMLServerLauncher"]
```

## Building

### lemminx extension

_Prerequisites_: git, Java 17 or newer

1. Clone this repo.
2. `cd` into `lemminx-schematron`
3. Run `./mvnw clean package` (Linux, macOS) or `.\mvnw.cmd clean package` (Windows)
4. The `.jar` will be created in `./lemminx-schematron/target`

### vscode extension

_Prerequisites_: git, Java 17 or newer, NodeJS 20 or newer, __macOS or Linux__ (the `npm` scripts I wrote are not cross platform)

1. Clone this repo.
2. `cd` into `vscode-schematron`
3. Install `vsce` globally if you don't have it installed already: `npm i -g @vscode/vsce`
4. Run `vsce package`
5. The `.vsix` will be created in `./vscode-schematron`

## Limitations

* You can only validate with local `.sch` files
* Not tested thoroughly
* The `.jar` is prohibitively large to include as a part of the standard LemMinX distribution