<schema xmlns="http://purl.oclc.org/dsdl/schematron">
  <pattern abstract="true" id="starts-with-capital">
      <rule context="$element" role="information">
          <let name="firstNodeIsElement" value="node()[1] instance of element()" />
          <report test="(not($firstNodeIsElement) and (not(matches(., '^[A-Z|0-9]'))))">
Start the element &lt;$element&gt; with a capital letter.</report>
      </rule>
  </pattern>
  <pattern is-a="starts-with-capital">
      <param name="element" value="title" />
  </pattern>
  <pattern is-a="starts-with-capital">
      <param name="element" value="li" />
  </pattern>
</schema>