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
		console.log("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		console.log("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		console.log("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
	
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
