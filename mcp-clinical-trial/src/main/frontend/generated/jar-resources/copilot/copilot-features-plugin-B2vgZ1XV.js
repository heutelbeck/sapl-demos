import { j as p, D as g, a6 as m, a7 as R, ar as b, a8 as $, as as y, M as F, E as u, at as x, y as w, au as T, r as f } from "./copilot-DtPsEJcm.js";
import { r as v } from "./state-suC5_Htu.js";
import { B as O } from "./base-panel-7vzvPGpg.js";
import { i as h } from "./icons-C54UeX_I.js";
const S = "copilot-features-panel{padding:var(--space-100);font:var(--copilot-font-xs);display:grid;grid-template-columns:auto 1fr;gap:var(--space-50);height:auto}copilot-features-panel a{display:flex;align-items:center;justify-self:end;gap:var(--space-50);white-space:nowrap}copilot-features-panel a svg{height:12px;width:12px;min-height:12px;min-width:12px}";
var q = Object.defineProperty, C = Object.getOwnPropertyDescriptor, o = (t, e, a, s) => {
  for (var r = s > 1 ? void 0 : s ? C(e, a) : e, i = t.length - 1, n; i >= 0; i--)
    (n = t[i]) && (r = (s ? n(e, a, r) : n(r)) || r);
  return s && r && q(e, a, r), r;
};
const l = window.Vaadin.devTools;
let d = class extends O {
  constructor() {
    super(...arguments), this.toggledFeaturesThatAreRequiresServerRestart = [];
  }
  render() {
    return g` <style>
        ${S}
      </style>
      ${p.featureFlags.map(
      (t) => g`
          <copilot-toggle-button
            .title="${t.title}"
            ?checked=${t.enabled}
            @on-change=${(e) => this.toggleFeatureFlag(e, t)}>
          </copilot-toggle-button>
          <a class="ahreflike" href="${t.moreInfoLink}" title="Learn more" target="_blank"
            >learn more ${h.share}</a
          >
        `
    )}`;
  }
  toggleFeatureFlag(t, e) {
    const a = t.target.checked;
    m("use-feature", { source: "toggle", enabled: a, id: e.id }), l.frontendConnection ? (l.frontendConnection.send("setFeature", { featureId: e.id, enabled: a }), e.requiresServerRestart && p.toggleServerRequiringFeatureFlag(e), R({
      type: $.INFORMATION,
      message: `“${e.title}” ${a ? "enabled" : "disabled"}`,
      details: e.requiresServerRestart ? b() : void 0,
      dismissId: `feature${e.id}${a ? "Enabled" : "Disabled"}`
    }), y()) : l.log("error", `Unable to toggle feature ${e.title}: No server connection available`);
  }
};
o([
  v()
], d.prototype, "toggledFeaturesThatAreRequiresServerRestart", 2);
d = o([
  f("copilot-features-panel")
], d);
let c = class extends F {
  constructor() {
    super(...arguments), this.serverRestarting = !1;
  }
  createRenderRoot() {
    return this;
  }
  render() {
    if (p.serverRestartRequiringToggledFeatureFlags.length === 0)
      return u;
    if (!x())
      return u;
    const t = this.serverRestarting ? "Restarting..." : "Click to restart server";
    return g`
      <style>
        .fade-in-out {
          animation: fadeInOut 2s ease-in-out infinite;
          animation-play-state: running;
        }
        .fade-in-out:hover {
          animation-play-state: paused;
          opacity: 1 !important;
        }
        ${w}
      </style>
      <button
        ?disabled="${this.serverRestarting}"
        id="restart-server-btn"
        class="icon ${this.serverRestarting ? "" : "fade-in-out"}"
        @click=${() => {
      this.serverRestarting = !0, T();
    }}>
        <span>${h.refresh}</span>
      </button>
      <vaadin-tooltip for="restart-server-btn" text=${t}></vaadin-tooltip>
    `;
  }
};
o([
  v()
], c.prototype, "serverRestarting", 2);
c = o([
  f("copilot-features-actions")
], c);
const I = {
  header: "Features",
  expanded: !1,
  panelOrder: 35,
  panel: "right",
  floating: !1,
  tag: "copilot-features-panel",
  helpUrl: "https://vaadin.com/docs/latest/flow/configuration/feature-flags",
  actionsTag: "copilot-features-actions"
}, P = {
  init(t) {
    t.addPanel(I);
  }
};
window.Vaadin.copilot.plugins.push(P);
export {
  c as CopilotFeaturesActions,
  d as CopilotFeaturesPanel
};
