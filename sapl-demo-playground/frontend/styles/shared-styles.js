/*
 * Copyright © 2019-2021 Dominic Heutelbeck (dominic@heutelbeck.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
