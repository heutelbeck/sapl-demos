// eagerly import theme styles so as we can override them
import '@vaadin/vaadin-lumo-styles/all-imports';

const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `
<custom-style>
	<style>
		html {
			--lumo-primary-text-color: #f8f9fa;
		}
		
		#xtext-editor {
			height: 100%
		}
		vaadin-button {
			--_lumo-button-color: #f8f9fa;
		}
		vaadin-select {
			--lumo-body-text-color: #f8f9fa;
			--lumo-contrast-60pct: #f8f9fa;
		}
		
		vaadin-tab[selected] {
		    color: var(--lumo-contrast-90pct);
		}
	</style>
</custom-style>

<dom-module id="header-select-styles" theme-for="vaadin-select-text-field">
  <template>
    <style>
		:host {
   			border-color: #f8f9fa;
		    padding: 0;
		}
    </style>
  </template>
</dom-module>

`;

document.head.appendChild($_documentContainer.content);
