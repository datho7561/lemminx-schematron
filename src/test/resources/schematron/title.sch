<schema xmlns="http://purl.oclc.org/dsdl/schematron">
  <pattern name="Check structure">
    <rule context="Person">
      <assert test="@Title">The element Person must have a Title attribute</assert>
      <assert test="count(*) = 2 and count(Name) = 1 and count(Gender) = 1">The element Person should have the child elements Name and Gender.</assert>
      <assert test="*[1] = Name">The element Name must appear before element Gender.</assert>
    </rule>
  </pattern>
  <pattern name="Check co-occurrence constraints">
    <rule context="Person">
      <assert test="(@Title = 'Mr' and Gender = 'Male') or @Title != 'Mr'">If the Title is "Mr" then the gender of the person must be "Male".</assert>
    </rule>
  </pattern>
</schema>