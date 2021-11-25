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
import { PolymerElement } from '@polymer/polymer/polymer-element.js';
import { html } from '@polymer/polymer/lib/utils/html-tag.js';

class HtmlbasedviewView extends PolymerElement {
  static get template() {
    return html`
      <style include="shared-styles">
        :host {
          display: block;
        }
      </style>
	<script type="text/javascript">
		var baseUrl = "/";
		var editor = xtext.createEditor({baseUrl: baseUrl,});
		console.log("Editor = "+editor);
	</script>
	<br/>
	<div class="container">
		<div id="xtext-editor"
		     data-editor-xtext-lang="statemachine"
			 data-editor-dirty-element="dirty-indicator"
			 data-editor-enable-formatting-action="true"
		>
	<pre>
/*
 * A simple State Machine example.
 */
input signal x
input signal y
output signal z

state State1
	set z = false
	if x == true goto State2
end

state State2
	if x == false and y == true goto State1
end</pre>
	</div>
	<div class="status-wrapper">
		<div id="dirty-indicator">modified</div>
		<div id="status">Welcome to Xtext-Web</div>
	</div>
</div>
		`;
  }

  static get is() {
    return 'htmlbasedview-view';
  }

  static get properties() {
    return {
      // Declare your properties here.
    };
  }
}

customElements.define(HtmlbasedviewView.is, HtmlbasedviewView);
