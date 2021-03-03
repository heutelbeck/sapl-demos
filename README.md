[![Build Status](https://github.com/heutelbeck/sapl-demos/workflows/build/badge.svg)](https://github.com/heutelbeck/sapl-demos/actions)

Please generate a personal access token for your GitHub account and add this to your ~/.m2/settings.xml:
```
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">
   <servers>
      <server>
         <id>sapl</id>
         <username>USERNAME</username>
         <!-- see https://github.com/settings/tokens -->
         <password>ACCESS TOKEN</password>
      </server>
   </servers>
</settings>
```

# sapl-demos

[The Tutorial **'Best Practices to Integrate SAPL with Spring Security Standard Mechanisms'** is here.](https://github.com/heutelbeck/sapl-demos/blob/master/docs/src/asciidoc/tutorial.adoc)
