# SAPL Web Editor Components Demo

This demo showcases the SAPL Vaadin editor components for embedding policy editors in web applications.

## Features

- **SAPL Policy Editor**: LSP-based editor with syntax highlighting, validation, and autocomplete
- **SAPL Test Editor**: Editor for .sapltest files with the same LSP features
- **JSON Editor**: CodeMirror-based JSON editor
- **Graph Visualization**: Policy structure visualization

Additional editor features include dark/light themes, merge/diff view, and coverage highlighting.

## Running the Demo

```bash
mvn spring-boot:run
```

Open http://localhost:8080 in your browser.

## Dependencies

```xml
<dependency>
    <groupId>io.sapl</groupId>
    <artifactId>sapl-vaadin-editor</artifactId>
</dependency>
```
