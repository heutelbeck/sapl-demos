import { r as f, b as n, E as v, D as p, w as b, a3 as g, $ as s, H as $ } from "./copilot-DtPsEJcm.js";
import { B as m } from "./base-panel-7vzvPGpg.js";
import { i as e } from "./icons-C54UeX_I.js";
const y = 'copilot-shortcuts-panel{display:flex;flex-direction:column;padding:var(--space-150)}copilot-shortcuts-panel h3{font:var(--copilot-font-xs-semibold);margin-bottom:var(--space-100);margin-top:0}copilot-shortcuts-panel h3:not(:first-of-type){margin-top:var(--space-200)}copilot-shortcuts-panel ul{display:flex;flex-direction:column;list-style:none;margin:0;padding:0}copilot-shortcuts-panel ul li{display:flex;align-items:center;gap:var(--space-50);position:relative}copilot-shortcuts-panel ul li:not(:last-of-type):before{border-bottom:1px dashed var(--border-color);content:"";inset:auto 0 0 calc(var(--copilot-size-md) + var(--space-50));position:absolute}copilot-shortcuts-panel ul li span:has(svg){align-items:center;display:flex;height:var(--copilot-size-md);justify-content:center;width:var(--copilot-size-md)}copilot-shortcuts-panel .kbds{margin-inline-start:auto}copilot-shortcuts-panel kbd{align-items:center;border:1px solid var(--border-color);border-radius:var(--vaadin-radius-m);box-sizing:border-box;display:inline-flex;font-family:var(--copilot-font-family);font-size:var(--copilot-font-size-xs);line-height:var(--copilot-line-height-sm);padding:0 var(--space-50)}', u = window.Vaadin.copilot.tree;
if (!u)
  throw new Error("Tried to access copilot tree before it was initialized.");
var x = Object.getOwnPropertyDescriptor, P = (o, i, h, r) => {
  for (var a = r > 1 ? void 0 : r ? x(i, h) : i, l = o.length - 1, c; l >= 0; l--)
    (c = o[l]) && (a = c(a) || a);
  return a;
};
let d = class extends m {
  constructor() {
    super(), this.onKeyPressedEvent = (o) => {
      o.detail.event.defaultPrevented || this.close();
    }, this.onTreeUpdated = () => {
      this.requestUpdate();
    };
  }
  connectedCallback() {
    super.connectedCallback(), n.on("copilot-tree-created", this.onTreeUpdated), n.on("escape-key-pressed", this.onKeyPressedEvent);
  }
  disconnectedCallback() {
    super.disconnectedCallback(), n.off("copilot-tree-created", this.onTreeUpdated), n.off("escape-key-pressed", this.onKeyPressedEvent);
  }
  render() {
    const o = u.hasFlowComponents();
    return p`<style>
        ${y}
      </style>
      <h3>Global</h3>
      <ul>
        <li>
          <span>${e.vaadin}</span>
          <span>Copilot</span>
          ${t(s.toggleCopilot)}
        </li>
        <li>
          <span>${e.flipBack}</span>
          <span>Undo</span>
          ${t(s.undo)}
        </li>
        <li>
          <span>${e.flipForward}</span>
          <span>Redo</span>
          ${t(s.redo)}
        </li>
      </ul>
      <h3>Selected component</h3>
      <ul>
        <li>
          <span>${e.terminal}</span>
          <span>Open AI popover</span>
          ${t(s.openAiPopover)}
        </li>
        <li>
          <span>${e.fileCodeAlt}</span>
          <span>Go to source</span>
          ${t(s.goToSource)}
        </li>
        ${o ? p`<li>
              <span>${e.code}</span>
              <span>Go to attach source</span>
              ${t(s.goToAttachSource)}
            </li>` : v}
        <li>
          <span>${e.copy}</span>
          <span>Copy</span>
          ${t(s.copy)}
        </li>
        <li>
          <span>${e.clipboard}</span>
          <span>Paste</span>
          ${t(s.paste)}
        </li>
        <li>
          <span>${e.copyAlt}</span>
          <span>Duplicate</span>
          ${t(s.duplicate)}
        </li>
        <li>
          <span>${e.userUp}</span>
          <span>Select parent</span>
          ${t(s.selectParent)}
        </li>
        <li>
          <span>${e.userLeft}</span>
          <span>Select previous sibling</span>
          ${t(s.selectPreviousSibling)}
        </li>
        <li>
          <span>${e.userRight}</span>
          <span>Select first child / next sibling</span>
          ${t(s.selectNextSibling)}
        </li>
        <li>
          <span>${e.delete}</span>
          <span>Delete</span>
          ${t(s.delete)}
        </li>
        <li>
          <span>${e.zap}</span>
          <span>Quick add from palette</span>
          ${t("<kbd>A ... Z</kbd>")}
        </li>
      </ul>`;
  }
  /**
   * Closes the panel. Used from shortcuts
   */
  close() {
    b.updatePanel("copilot-shortcuts-panel", {
      floating: !1
    });
  }
};
d = P([
  f("copilot-shortcuts-panel")
], d);
function t(o) {
  return p`<span class="kbds">${g(o)}</span>`;
}
const w = $({
  header: "Keyboard Shortcuts",
  tag: "copilot-shortcuts-panel",
  width: 400,
  height: 550,
  floatingPosition: {
    top: 50,
    left: 50
  }
}), k = {
  init(o) {
    o.addPanel(w);
  }
};
window.Vaadin.copilot.plugins.push(k);
