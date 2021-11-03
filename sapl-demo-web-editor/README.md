# Demo Project for the Vaadin SAPL Editor Component

This project is a simple demo application that provides a simple view with the SAPL Editor and JSON Editor.

This demo is a Vaadin 14 project. It requires node.js with npm on your development platform.
Either download the installer (https://nodejs.org/en/download/) or use your preferred package management system (Homebrew, dpkg, …​).

## SaplEditor

The Sapl Editor is used to write and validate policies in the SAPL DSL.

### Add/Remove Change Listener (SAPL)

These buttons register or unregister a change listener to the SAPL editor. The listener is called after the document has changed. If registered, the changed document will be displayed in the standard output of the application.

### Add/Remove Validation Listener (SAPL)

These buttons register or unregister a change listener to the SAPL editor that is called after the validation has finished. If registered, the validation result will be displayed in the standard output of the application.

## JsonEditor

This editor is used to write and validate configuration files written in JSON.

### Set/Get Document (JSON)

These buttons set the current document back to a default JSON text or get the current document which is then displayed in the standard output of the application.