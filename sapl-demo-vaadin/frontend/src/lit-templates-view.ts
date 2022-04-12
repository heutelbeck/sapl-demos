import {html, LitElement} from 'lit';
import {customElement, property} from 'lit/decorators.js';
import '@vaadin/vaadin-button/vaadin-button.js';

@customElement('lit-templates-page-view')
class Page5View extends LitElement {
    @property()
    buttonText = 'Button';

    render() {
        return html`
			<vaadin-vertical-layout id="content">
                <vaadin-button id="btnToggle1">${this.buttonText}</vaadin-button>
            </vaadin-vertical-layout>`;
    }
}
