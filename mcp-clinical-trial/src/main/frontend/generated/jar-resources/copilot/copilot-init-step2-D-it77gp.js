import { r as b, M as I, u as ge, v as j, b as u, j as l, C as K, w as p, x as m, y as se, z as Y, A as _, O as xe, B as E, D as r, F as X, G as ht, H as ut, I as gt, J as f, K as Q, L as Se, N as Ee, E as g, Q as qe, k as ae, l as We, P as Ge, R as ft, V as vt, S as c, T as Ye, U as S, W as mt, X as Pe, Y as Ie, Z as Xe, _ as bt, $ as le, a0 as wt, a1 as Re, a2 as yt, a3 as xt, a4 as Pt, a5 as It, a6 as Ke, a7 as N, a8 as R, a9 as At, aa as $t, ab as Ct, ac as Ze, ad as kt, ae as St, af as Et, ag as Rt, ah as Qe, ai as Dt, aj as Ae, ak as $e, al as Lt, am as Mt } from "./copilot-DtPsEJcm.js";
import { n as y, r as x } from "./state-suC5_Htu.js";
import { e as O } from "./query-BykXNUlT.js";
import { i as d } from "./icons-C54UeX_I.js";
import { c as et } from "./index-NzpD2vpO.js";
const zt = 1, De = 40, _t = 18;
function Ot(e, t) {
  if (e.length === 0)
    return;
  const i = Tt(e, t);
  for (const n in e)
    e[n].style.setProperty("--content-height", `${i[n]}px`);
}
function Tt(e, t) {
  const i = e.length, n = e.filter((s) => s.panelInfo && s.panelInfo.expanded).length, o = i - n;
  return e.map((s) => {
    const a = s.panelInfo;
    return a && !a.expanded ? De : (t.offsetHeight - (t.position === "bottom" ? _t : 0) - o * De - i * zt) / n;
  });
}
var Ht = Object.defineProperty, Ut = Object.getOwnPropertyDescriptor, B = (e, t, i, n) => {
  for (var o = n > 1 ? void 0 : n ? Ut(t, i) : t, s = e.length - 1, a; s >= 0; s--)
    (a = e[s]) && (o = (n ? a(t, i, o) : a(o)) || o);
  return n && o && Ht(t, i, o), o;
};
const de = "data-drag-initial-index", q = "data-drag-final-index";
let M = class extends I {
  constructor() {
    super(...arguments), this.position = "right", this.opened = !1, this.keepOpen = !1, this.resizing = !1, this.closingForcefully = !1, this.draggingSectionPanel = null, this.openedDescendantOverlayOwners = /* @__PURE__ */ new Set(), this.drawerDragLeaveListener = (e) => {
      const { x: t, y: i } = e;
      ge(this.getBoundingClientRect(), t, i) ? this.debounceHideDrawerWhenDragLeave.clear() : this.debounceHideDrawerWhenDragLeave();
    }, this.drawerDragEnterListener = () => {
      this.opened = !0;
    }, this.debounceHideDrawerWhenDragLeave = j(() => {
      this.opened = !1;
    }, 200), this.panelCountChanged = j(() => {
      this.refreshSplit();
    }, 100), this.documentMouseUpListener = () => {
      this.resizing && u.emit("user-select", { allowSelection: !0 }), this.resizing = !1, l.setDrawerResizing(!1), this.removeAttribute("resizing");
    }, this.resizingMouseMoveListener = (e) => {
      if (!this.resizing)
        return;
      const { x: t, y: i } = e;
      e.stopPropagation(), e.preventDefault(), requestAnimationFrame(() => {
        let n;
        if (this.position === "right") {
          const o = document.body.clientWidth - t;
          this.style.setProperty("--size", `${o}px`), K.saveDrawerSize(this.position, o), n = { width: o };
        } else if (this.position === "left") {
          const o = t;
          this.style.setProperty("--size", `${o}px`), K.saveDrawerSize(this.position, o), n = { width: o };
        } else if (this.position === "bottom") {
          const o = document.body.clientHeight - i;
          this.style.setProperty("--size", `${o}px`), K.saveDrawerSize(this.position, o), n = { height: o };
        }
        this.setActualSize(), p.panels.filter((o) => !o.floating && o.panel === this.position).forEach((o) => {
          p.updatePanel(o.tag, n);
        });
      });
    }, this.sectionPanelDraggingStarted = (e, t) => {
      this.draggingSectionPanel = e, u.emit("user-select", { allowSelection: !1 }), this.draggingSectionPointerStartY = t.clientY, e.toggleAttribute("dragging", !0), e.style.zIndex = "1000", Array.from(this.querySelectorAll("copilot-section-panel-wrapper")).forEach((i, n) => {
        i.setAttribute(de, `${n}`);
      }), document.addEventListener("mousemove", this.sectionPanelDragging), document.addEventListener("mouseup", this.sectionPanelDraggingFinished);
    }, this.sectionPanelDragging = (e) => {
      if (!this.draggingSectionPanel)
        return;
      const { clientX: t, clientY: i } = e;
      if (!ge(this.getBoundingClientRect(), t, i)) {
        this.cleanUpDragging();
        return;
      }
      const n = i - this.draggingSectionPointerStartY;
      this.draggingSectionPanel.style.transform = `translateY(${n}px)`, this.updateSectionPanelPositionsWhileDragging();
    }, this.sectionPanelDraggingFinished = () => {
      if (!this.draggingSectionPanel)
        return;
      u.emit("user-select", { allowSelection: !0 });
      const e = this.getAllPanels().filter(
        (t) => t.hasAttribute(q) && t.panelInfo?.panelOrder !== Number.parseInt(t.getAttribute(q), 10)
      ).map((t) => ({
        tag: t.panelTag,
        order: Number.parseInt(t.getAttribute(q), 10)
      }));
      this.cleanUpDragging(), p.updateOrders(e), document.removeEventListener("mouseup", this.sectionPanelDraggingFinished), document.removeEventListener("mousemove", this.sectionPanelDragging), this.refreshSplit();
    }, this.updateSectionPanelPositionsWhileDragging = () => {
      const e = this.draggingSectionPanel.getBoundingClientRect().height;
      this.getAllPanels().sort((t, i) => {
        const n = t.getBoundingClientRect(), o = i.getBoundingClientRect(), s = (n.top + n.bottom) / 2, a = (o.top + o.bottom) / 2;
        return s - a;
      }).forEach((t, i) => {
        if (t.setAttribute(q, `${i}`), t.panelTag !== this.draggingSectionPanel?.panelTag) {
          const n = Number.parseInt(t.getAttribute(de), 10);
          n > i ? t.style.transform = `translateY(${-e}px)` : n < i ? t.style.transform = `translateY(${e}px)` : t.style.removeProperty("transform");
        }
      });
    }, this.panelExpandedListener = (e) => {
      this.querySelector(`copilot-section-panel-wrapper[paneltag="${e.detail.panelTag}"]`) && this.refreshSplit();
    }, this.setActualSize = () => {
      let e = this.offsetWidth;
      this.position === "bottom" && (e = this.offsetHeight), this.style.setProperty("--actual-size", `calc(${e}px - var(--hover-size))`);
    };
  }
  static get styles() {
    return [
      m(se),
      m(Y),
      _`
        :host {
          --size: 350px;
          --actual-size: 350px;
          --min-size: 20%;
          --max-size: 80%;
          --default-content-height: 300px;
          --transition-duration: var(--duration-2);
          --opening-delay: var(--duration-2);
          --closing-delay: var(--duration-3);
          --hover-size: 18px;
          position: absolute;
          z-index: var(--z-index-drawer);
          transition: translate var(--transition-duration) var(--closing-delay);
        }

        :host(:is([position='left'], [position='right'])) {
          width: var(--size);
          min-width: var(--min-size);
          max-width: var(--max-size);
          top: 0;
          bottom: 0;
        }

        :host([position='left']) {
          left: calc(0px - var(--actual-size));
          translate: 0% 0%;
          padding-right: var(--hover-size);
        }

        :host([position='right']) {
          right: calc(0px - var(--actual-size));
          translate: 0% 0%;
          padding-left: var(--hover-size);
        }

        :host([position='bottom']) {
          height: var(--size);
          min-height: var(--min-size);
          max-height: var(--max-size);
          bottom: calc(0px - var(--actual-size));
          left: 0;
          right: 0;
          translate: 0% 0%;
          padding-top: var(--hover-size);
        }

        /* The visible container. Needed to have extra space for hover and resize handle outside it. */

        .container {
          display: flex;
          flex-direction: column;
          box-sizing: border-box;
          height: 100%;
          background: var(--background-color);
          -webkit-backdrop-filter: var(--surface-backdrop-filter);
          backdrop-filter: var(--surface-backdrop-filter);
          overflow-y: auto;
          overflow-x: hidden;
          box-shadow: var(--surface-box-shadow-2);
          transition:
            opacity var(--transition-duration) var(--closing-delay),
            visibility calc(var(--transition-duration) * 2) var(--closing-delay);
          opacity: 0;
          /* For accessibility (restored when open) */
          visibility: hidden;
        }

        :host([position='left']) .container {
          border-right: 1px solid var(--surface-border-color);
        }

        :host([position='right']) .container {
          border-left: 1px solid var(--surface-border-color);
        }

        :host([position='bottom']) .container {
          border-top: 1px solid var(--surface-border-color);
        }

        /* Opened state */

        :host([position='left']:is([opened], [keepopen])) {
          translate: calc(100% - var(--hover-size)) 0%;
        }
        :host([position='right']:is([opened], [keepopen])) {
          translate: calc(-100% + var(--hover-size)) 0%;
        }
        :host([position='bottom']:is([opened], [keepopen])) {
          translate: 0 calc(-100% + var(--hover-size));
        }

        :host(:is([opened], [keepopen])) {
          transition-delay: var(--opening-delay);
          z-index: var(--z-index-opened-drawer);
        }

        :host(:is([opened], [keepopen])) .container {
          transition-delay: var(--opening-delay);
          visibility: visible;
          opacity: 1;
        }

        .resize {
          position: absolute;
          z-index: 10;
          inset: 0;
        }

        :host(:is([position='left'], [position='right'])) .resize {
          width: var(--hover-size);
          cursor: col-resize;
        }

        :host([position='left']) .resize {
          left: auto;
          right: calc(var(--hover-size) * 0.5);
        }

        :host([position='right']) .resize {
          right: auto;
          left: calc(var(--hover-size) * 0.5);
        }

        :host([position='bottom']) .resize {
          height: var(--hover-size);
          bottom: auto;
          top: calc(var(--hover-size) * 0.5);
          cursor: row-resize;
        }

        :host([resizing]) .container {
          /* vaadin-grid (used in the outline) blocks the mouse events */
          pointer-events: none;
        }

        /* Visual indication of the drawer */

        :host::before {
          content: '';
          position: absolute;
          pointer-events: none;
          z-index: -1;
          inset: var(--hover-size);
        }

        :host([document-hidden])::before {
          animation: none;
        }

        :host(:is([opened], [keepopen]))::before {
          transition-delay: var(--opening-delay);
          opacity: 0;
        }

        /* Drawer indicator */
        #drawer-indicator {
          transition-delay: 0.5s;
        }
        #drawer-indicator::before,
        #drawer-indicator::after {
          border-radius: inherit;
          content: '';
          inset: 0;
          position: absolute;
        }
        #drawer-indicator::before {
          animation: var(--animate-swirl);
          background-image:
            radial-gradient(circle at 50% -10%, var(--blue-9) 0%, transparent 60%),
            radial-gradient(circle at 25% 40%, var(--violet-9) 0%, transparent 70%),
            radial-gradient(circle at 80% 10%, var(--gray-9) 0%, transparent 80%),
            radial-gradient(circle at 110% 50%, var(--teal-9) 20%, transparent 100%);
        }
        #drawer-indicator::after {
          border: 1px solid rgba(255, 255, 255, 0.5);
        }
        :host([attention-required]) #drawer-indicator::before {
          background-image:
            radial-gradient(circle at 50% -10%, var(--ruby-11) 0%, transparent 60%),
            radial-gradient(circle at 25% 40%, var(--ruby-8) 0%, transparent 70%),
            radial-gradient(circle at 80% 10%, var(--ruby-12) 0%, transparent 80%),
            radial-gradient(circle at 110% 50%, var(--ruby-7) 20%, transparent 100%);
        }
        :host([opened]) #drawer-indicator {
          opacity: 0;
          transition-delay: 0s;
        }
        :host([document-hidden]) #drawer-indicator {
          filter: grayscale(100%);
          -webkit-filter: grayscale(100%);
        }
        :host([position='right']) #drawer-indicator {
          left: 0.25rem;
          top: calc(50% - 0.875rem);
        }
        :host([position='right']) #drawer-indicator vaadin-icon {
          margin-inline-start: -0.625rem;
          transform: rotate(-90deg);
        }
        :host([position='left']) #drawer-indicator {
          right: 0.25rem;
          top: calc(50% - 0.875rem);
        }
        :host([position='left']) #drawer-indicator vaadin-icon {
          margin-inline-end: -0.625rem;
          transform: rotate(90deg);
        }
        :host([position='bottom']) #drawer-indicator {
          left: calc(50% - 0.875rem);
          top: 0.25rem;
        }
        :host([position='bottom']) #drawer-indicator vaadin-icon {
          margin-top: -0.625rem;
        }
      `
    ];
  }
  connectedCallback() {
    super.connectedCallback(), this.reaction(
      () => p.panels,
      () => this.requestUpdate()
    ), this.reaction(
      () => l.operationInProgress,
      (t) => {
        t === xe.DragAndDrop && !this.opened && !this.keepOpen ? this.style.setProperty("pointer-events", "none") : this.style.setProperty("pointer-events", "auto");
      }
    ), this.reaction(
      () => p.getAttentionRequiredPanelConfiguration(),
      () => {
        const t = p.getAttentionRequiredPanelConfiguration();
        t && !t.floating && this.toggleAttribute(E, t.panel === this.position);
      }
    ), this.reaction(
      () => l.active,
      () => {
        l.active || (this.opened = !1);
      }
    ), document.addEventListener("mouseup", this.documentMouseUpListener);
    const e = K.getDrawerSize(this.position);
    e && (this.style.setProperty("--size", `${e}px`), this.setActualSize()), document.addEventListener("mousemove", this.resizingMouseMoveListener), this.addEventListener("mouseenter", this.mouseEnterListener), u.on("document-activation-change", (t) => {
      this.toggleAttribute("document-hidden", !t.detail.active);
    }), u.on("panel-expanded", this.panelExpandedListener), u.on("copilot-main-resized", this.setActualSize), this.reaction(
      () => p.panels.filter(
        (t) => !t.floating && t.panel === this.position
      ).length,
      () => {
        this.panelCountChanged();
      }
    ), this.addEventListener("dragleave", this.drawerDragLeaveListener), this.addEventListener("dragenter", this.drawerDragEnterListener);
  }
  firstUpdated(e) {
    super.firstUpdated(e), this.resizeElement.addEventListener("mousedown", (t) => {
      t.button === 0 && (this.resizing = !0, l.setDrawerResizing(!0), this.setAttribute("resizing", ""), u.emit("user-select", { allowSelection: !1 }));
    });
  }
  updated(e) {
    super.updated(e), e.has("opened") && this.opened && this.hasAttribute(E) && (this.removeAttribute(E), p.clearAttention());
  }
  disconnectedCallback() {
    super.disconnectedCallback(), document.removeEventListener("mousemove", this.resizingMouseMoveListener), document.removeEventListener("mouseup", this.documentMouseUpListener), this.removeEventListener("mouseenter", this.mouseEnterListener), u.off("panel-expanded", this.panelExpandedListener), u.off("copilot-main-resized", this.setActualSize), this.removeEventListener("dragleave", this.drawerDragLeaveListener), this.removeEventListener("dragenter", this.drawerDragEnterListener);
  }
  /**
   * Cleans up attributes/styles etc... for dragging operations
   * @private
   */
  cleanUpDragging() {
    this.draggingSectionPanel && (l.setSectionPanelDragging(!1), this.draggingSectionPanel.style.zIndex = "", Array.from(this.querySelectorAll("copilot-section-panel-wrapper")).forEach((e) => {
      e.style.removeProperty("transform"), e.removeAttribute(q), e.removeAttribute(de);
    }), this.draggingSectionPanel.removeAttribute("dragging"), this.draggingSectionPanel = null);
  }
  getAllPanels() {
    return Array.from(this.querySelectorAll("copilot-section-panel-wrapper"));
  }
  getAllPanelsOrdered() {
    return this.getAllPanels().sort((e, t) => e.panelInfo && t.panelInfo ? e.panelInfo.panelOrder - t.panelInfo.panelOrder : 0);
  }
  /**
   * Closes the drawer and disables mouse enter event for a while.
   */
  forceClose() {
    this.closingForcefully = !0, this.opened = !1, setTimeout(() => {
      this.closingForcefully = !1;
    }, 0.5);
  }
  mouseEnterListener(e) {
    if (this.closingForcefully || l.sectionPanelResizing)
      return;
    document.querySelector("copilot-main").shadowRoot.querySelector("copilot-drawer-panel[opened]") || (this.refreshSplit(), this.opened = !0);
  }
  render() {
    return r`
      <div class="container">
        <slot></slot>
      </div>
      <div class="resize"></div>
      <div
        class="fixed flex items-center justify-center overflow-hidden rounded-full size-7 text-white transition-opacity"
        id="drawer-indicator">
        <vaadin-icon class="size-3 z-1" .svg="${d.keyboardArrowUp}"></vaadin-icon>
      </div>
    `;
  }
  refreshSplit() {
    Ot(this.getAllPanelsOrdered(), this);
  }
};
B([
  y({ reflect: !0, attribute: !0 })
], M.prototype, "position", 2);
B([
  y({ reflect: !0, type: Boolean })
], M.prototype, "opened", 2);
B([
  y({ reflect: !0, type: Boolean })
], M.prototype, "keepOpen", 2);
B([
  O(".container")
], M.prototype, "container", 2);
B([
  O(".resize")
], M.prototype, "resizeElement", 2);
M = B([
  b("copilot-drawer-panel")
], M);
var jt = Object.defineProperty, Nt = Object.getOwnPropertyDescriptor, Ce = (e, t, i, n) => {
  for (var o = n > 1 ? void 0 : n ? Nt(t, i) : t, s = e.length - 1, a; s >= 0; s--)
    (a = e[s]) && (o = (n ? a(t, i, o) : a(o)) || o);
  return n && o && jt(t, i, o), o;
};
let ee = class extends X {
  constructor() {
    super(...arguments), this.checked = !1, this.documentMouseDownEventClickListener = (e) => {
      const t = ht([this]);
      ge(t, e.x, e.y) || (this.inputElement.blur(), document.removeEventListener("mousedown", this.documentMouseDownEventClickListener, { capture: !0 }));
    };
  }
  static get styles() {
    return _`
      .switch {
        display: inline-flex;
        gap: var(--space-100);
        padding-block: calc((var(--copilot-size-md) - var(--copilot-line-height-sm)) / 2);
        position: relative;
      }

      .switch input {
        opacity: 0;
        position: absolute;
      }

      .slider {
        align-items: center;
        border: 2px solid var(--vaadin-text-color-secondary);
        border-radius: 9999px;
        box-sizing: border-box;
        display: flex;
        flex-shrink: 0;
        height: 1.125rem;
        transition: 200ms;
        width: 1.75rem;
      }

      .slider::before {
        background: var(--vaadin-text-color-secondary);
        border-radius: 9999px;
        content: '';
        display: flex;
        height: 0.5rem;
        transition: 200ms;
        translate: 0.1875rem 0;
        width: 0.5rem;
      }

      input:focus + .slider {
        outline: 2px solid var(--focus-color);
        outline-offset: 1px;
      }

      input:checked + .slider {
        background: var(--blue-11);
        border-color: var(--blue-11);
      }

      input:checked + .slider::before {
        background: var(--blue-5);
        height: 0.75rem;
        translate: 0.6875rem 0;
        width: 0.75rem;
      }
    `;
  }
  /*
    TODO: We should refactor to use button instead.
     <label for="xxx-switch">Edit Mode</label>
     <button aria-checked="false" id="xxx-switch" role="switch" type="button">
  */
  render() {
    return r`
      <label class="switch">
        <input
          class="feature-toggle"
          id="feature-toggle-${this.id}"
          type="checkbox"
          @focusin="${() => {
      document.addEventListener("mousedown", this.documentMouseDownEventClickListener, { capture: !0 });
    }}"
          ?checked="${this.checked}"
          @change=${(e) => {
      e.preventDefault(), this.checked = e.target.checked, this.dispatchEvent(new CustomEvent("on-change"));
    }} />
        <span aria-hidden="true" class="slider"></span>
        ${this.title}
      </label>
    `;
  }
  //  @change=${(e: InputEvent) => this.toggleFeatureFlag(e, feature)}
};
Ce([
  O("input.feature-toggle")
], ee.prototype, "inputElement", 2);
Ce([
  y({ reflect: !0, type: Boolean })
], ee.prototype, "checked", 2);
ee = Ce([
  b("copilot-toggle-button")
], ee);
class Bt {
  constructor() {
    this.offsetX = 0, this.offsetY = 0;
  }
  draggingStarts(t, i) {
    this.offsetX = i.clientX - t.getBoundingClientRect().left, this.offsetY = i.clientY - t.getBoundingClientRect().top;
  }
  dragging(t, i) {
    const n = i.clientX, o = i.clientY, s = n - this.offsetX, a = n - this.offsetX + t.getBoundingClientRect().width, h = o - this.offsetY, w = o - this.offsetY + t.getBoundingClientRect().height;
    return this.adjust(t, s, h, a, w);
  }
  adjust(t, i, n, o, s) {
    let a, h, w, k;
    const L = document.documentElement.getBoundingClientRect().width, re = document.documentElement.getBoundingClientRect().height;
    return (o + i) / 2 < L / 2 ? (t.style.setProperty("--left", `${i}px`), t.style.setProperty("--right", ""), k = void 0, a = Math.max(0, i)) : (t.style.removeProperty("--left"), t.style.setProperty("--right", `${L - o}px`), a = void 0, k = Math.max(0, L - o)), (n + s) / 2 < re / 2 ? (h = Math.max(0, n), t.style.setProperty("--top", `${h}px`), t.style.setProperty("--bottom", ""), w = void 0) : (t.style.setProperty("--top", ""), t.style.setProperty("--bottom", `${re - s}px`), h = void 0, w = Math.max(0, re - s)), {
      left: a,
      right: k,
      top: h,
      bottom: w
    };
  }
  anchor(t) {
    const { left: i, top: n, bottom: o, right: s } = t.getBoundingClientRect();
    return this.adjust(t, i, n, s, o);
  }
  anchorLeftTop(t) {
    const { left: i, top: n } = t.getBoundingClientRect();
    return t.style.setProperty("--left", `${i}px`), t.style.setProperty("--right", ""), t.style.setProperty("--top", `${n}px`), t.style.setProperty("--bottom", ""), {
      left: i,
      top: n
    };
  }
}
const C = new Bt();
var Ft = Object.defineProperty, Vt = Object.getOwnPropertyDescriptor, F = (e, t, i, n) => {
  for (var o = n > 1 ? void 0 : n ? Vt(t, i) : t, s = e.length - 1, a; s >= 0; s--)
    (a = e[s]) && (o = (n ? a(t, i, o) : a(o)) || o);
  return n && o && Ft(t, i, o), o;
};
const Le = "https://github.com/JetBrains/JetBrainsRuntime/releases";
function Jt(e, t) {
  if (!t)
    return !0;
  const [i, n, o] = t.split(".").map((w) => parseInt(w)), [s, a, h] = e.split(".").map((w) => parseInt(w));
  if (i < s)
    return !0;
  if (i === s) {
    if (n < a)
      return !0;
    if (n === a)
      return o < h;
  }
  return !1;
}
const Me = "Download complete";
let A = class extends I {
  constructor() {
    super(), this.javaPluginSectionOpened = !1, this.hotswapSectionOpened = !1, this.hotswapTab = "hotswapagent", this.downloadStatusMessages = [], this.downloadProgress = 0, this.onDownloadStatusUpdate = this.downloadStatusUpdate.bind(this), this.handleESC = (e) => {
      l.active && e.key === "Escape" && p.updatePanel(te.tag, { floating: !1 });
    }, this.reaction(
      () => [f.jdkInfo, l.idePluginState],
      () => {
        l.idePluginState && (!l.idePluginState.ide || !l.idePluginState.active ? this.javaPluginSectionOpened = !0 : (!(/* @__PURE__ */ new Set(["vscode", "intellij"])).has(l.idePluginState.ide) || !l.idePluginState.active) && (this.javaPluginSectionOpened = !1)), f.jdkInfo && Q() !== "success" && (this.hotswapSectionOpened = !0);
      },
      { fireImmediately: !0 }
    );
  }
  connectedCallback() {
    super.connectedCallback(), u.on("set-up-vs-code-hotswap-status", this.onDownloadStatusUpdate), this.addESCListener();
  }
  disconnectedCallback() {
    super.disconnectedCallback(), u.off("set-up-vs-code-hotswap-status", this.onDownloadStatusUpdate), this.removeESCListener();
  }
  render() {
    const e = {
      intellij: l.idePluginState?.ide === "intellij",
      vscode: l.idePluginState?.ide === "vscode",
      eclipse: l.idePluginState?.ide === "eclipse",
      idePluginInstalled: !!l.idePluginState?.active
    };
    return r`
      <div part="container">${this.renderPluginSection(e)} ${this.renderHotswapSection(e)}</div>
      <div part="footer">
        <vaadin-button
          id="close"
          @click="${() => p.updatePanel(te.tag, { floating: !1 })}"
          >Close
        </vaadin-button>
      </div>
    `;
  }
  renderPluginSection(e) {
    let t = "";
    e.intellij ? t = "IntelliJ" : e.vscode ? t = "VS Code" : e.eclipse && (t = "Eclipse");
    let i, n;
    e.vscode || e.intellij ? e.idePluginInstalled ? (i = `Plugin for ${t} installed`, n = this.renderPluginInstalledContent()) : (i = `Plugin for ${t} not installed`, n = this.renderPluginIsNotInstalledContent(e)) : e.eclipse ? (i = e.idePluginInstalled ? "Eclipse plugin installed" : "Eclipse plugin not installed", n = e.idePluginInstalled ? this.renderPluginInstalledContent() : this.renderEclipsePluginContent()) : (i = "No IDE found", n = this.renderNoIdePluginContent());
    const o = e.idePluginInstalled ? d.checkCircle : d.alertTriangle;
    return r`
      <details
        part="panel"
        .open=${this.javaPluginSectionOpened}
        @toggle=${(s) => {
      Se(() => {
        this.javaPluginSectionOpened = s.target.open;
      });
    }}>
        <summary part="header">
          <span class="icon ${e.idePluginInstalled ? "success" : "warning"}">${o}</span>
          <div>${i}</div>
        </summary>
        <div part="content">${n}</div>
      </details>
    `;
  }
  renderNoIdePluginContent() {
    return r`
      <div>
        <div>We could not detect an IDE</div>
        ${this.recommendSupportedPlugin()}
      </div>
    `;
  }
  renderEclipsePluginContent() {
    return r`
      <div>
        <div>Install the Vaadin Eclipse Plugin to ensure a smooth development workflow</div>
        <p>
          Installing the plugin is not required, but strongly recommended.<br />Some Vaadin Copilot functionality, such
          as undo, will not function optimally without the plugin.
        </p>
        <div>
          <vaadin-button
            @click="${() => {
      window.open(Ee, "_blank");
    }}"
            >Install from Eclipse Marketplace
            <vaadin-icon icon="vaadin:external-link"></vaadin-icon>
          </vaadin-button>
        </div>
      </div>
    `;
  }
  recommendSupportedPlugin() {
    return r`<div>
      Please use <a href="https://code.visualstudio.com">Visual Studio Code</a> or
      <a href="https://www.jetbrains.com/idea">IntelliJ IDEA</a> for better development experience
    </div>`;
  }
  renderPluginInstalledContent() {
    return r` <div>You have a running plugin. Enjoy your awesome development workflow!</div> `;
  }
  renderPluginIsNotInstalledContent(e) {
    let t = null, i = "Install from Marketplace";
    return e.intellij ? (t = ft, i = "Install from JetBrains Marketplace") : e.vscode ? (t = vt, i = "Install from VSCode Marketplace") : e.eclipse && (t = Ee, i = "Install from Eclipse Marketplace"), r`
      <div>
        <div>Install the Vaadin IDE Plugin to ensure a smooth development workflow</div>
        <p>
          Installing the plugin is not required, but strongly recommended.<br />Some Vaadin Copilot functionality, such
          as undo, will not function optimally without the plugin.
        </p>
        ${t ? r` <div>
              <vaadin-button
                @click="${() => {
      window.open(t, "_blank");
    }}"
                >${i}
                <vaadin-icon icon="vaadin:external-link"></vaadin-icon>
              </vaadin-button>
            </div>` : g}
      </div>
    `;
  }
  renderHotswapSection(e) {
    const { jdkInfo: t } = f;
    if (!t)
      return g;
    const i = Q(), n = qe();
    let o, s, a;
    return i === "success" ? (o = d.checkCircle, a = "Java Hotswap is enabled") : i === "warning" ? (o = d.alertTriangle, a = "Java Hotswap is not enabled") : i === "error" && (o = d.alertTriangle, a = "Java Hotswap is partially enabled"), this.hotswapTab === "jrebel" ? t.jrebel ? s = this.renderJRebelInstalledContent() : s = this.renderJRebelNotInstalledContent() : e.intellij ? s = this.renderHotswapAgentPluginContent(this.renderIntelliJHotswapHint) : e.vscode ? s = this.renderHotswapAgentPluginContent(this.renderVSCodeHotswapHint) : s = this.renderHotswapAgentNotInstalledContent(e), r` <details
      part="panel"
      .open=${this.hotswapSectionOpened}
      @toggle=${(h) => {
      Se(() => {
        this.hotswapSectionOpened = h.target.open;
      });
    }}>
      <summary part="header">
        <span class="icon ${i}">${o}</span>
        <div>${a}</div>
      </summary>
      <div part="content">
        ${n !== "none" ? r`${n === "jrebel" ? this.renderJRebelInstalledContent() : this.renderHotswapAgentInstalledContent()}` : r`
            <div class="tabs" role="tablist">
              <button
                aria-selected="${this.hotswapTab === "hotswapagent" ? "true" : "false"}"
                class="tab"
                role="tab"
                @click=${() => {
      this.hotswapTab = "hotswapagent";
    }}>
                Hotswap Agent
              </button>
              <button
                aria-selected="${this.hotswapTab === "jrebel" ? "true" : "false"}"
                class="tab"
                role="tab"
                @click=${() => {
      this.hotswapTab = "jrebel";
    }}>
                JRebel
              </button>
            </div>
            <div part="content">${s}</div>
            </div>
            </details>
          `}
      </div>
    </details>`;
  }
  renderJRebelNotInstalledContent() {
    return r`
      <div>
        <a href="https://www.jrebel.com">JRebel ${d.share}</a> is a commercial hotswap solution. Vaadin detects the
        JRebel Agent and automatically reloads the application in the browser after the Java changes have been
        hotpatched.
        <p>
          Go to
          <a href="https://www.jrebel.com/products/jrebel/learn" target="_blank" rel="noopener noreferrer">
            https://www.jrebel.com/products/jrebel/learn ${d.share}</a
          >
          to get started
        </p>
      </div>
    `;
  }
  renderHotswapAgentNotInstalledContent(e) {
    const t = [
      this.renderJavaRunningInDebugModeSection(),
      this.renderHotswapAgentJdkSection(e),
      this.renderInstallHotswapAgentJdkSection(e),
      this.renderHotswapAgentVersionSection(),
      this.renderHotswapAgentMissingArgParam(e)
    ];
    return r` <div part="hotswap-agent-section-container">${t}</div> `;
  }
  renderHotswapAgentPluginContent(e) {
    const i = Q() === "success";
    return r`
      <div part="hotswap-agent-section-container">
        <div class="inner-section">
          <span class="hotswap icon ${i ? "success" : "warning"}"
            >${i ? d.checkCircle : d.alertTriangle}</span
          >
          ${e()}
        </div>
      </div>
    `;
  }
  renderIntelliJHotswapHint() {
    return r` <div class="hint">
      <h3>Use 'Debug using Hotswap Agent' launch configuration</h3>
      Vaadin IntelliJ plugin offers launch mode that does not require any manual configuration!
      <p>
        In order to run recommended launch configuration, you should click three dots right next to Debug button and
        select <code>Debug using Hotswap Agent</code> option.
      </p>
    </div>`;
  }
  renderVSCodeHotswapHint() {
    return r` <div class="hint">
      <h3>Use 'Debug (hotswap)'</h3>
      With Vaadin Visual Studio Code extension you can run Hotswap Agent without any manual configuration required!
      <p>Click <code>Debug (hotswap)</code> within your main class to debug application using Hotswap Agent.</p>
    </div>`;
  }
  renderJavaRunningInDebugModeSection() {
    const e = f.jdkInfo?.runningInJavaDebugMode;
    return r`
      <div class="inner-section">
        <details class="inner" .open="${!e}">
          <summary>
            <span class="icon ${e ? "success" : "warning"}"
              >${e ? d.checkCircle : d.alertTriangle}</span
            >
            <div>Run Java in debug mode</div>
          </summary>
          <div class="hint">Start the application in debug mode in the IDE</div>
        </details>
      </div>
    `;
  }
  renderHotswapAgentMissingArgParam(e) {
    const t = f.jdkInfo?.runningWitHotswap && f.jdkInfo?.runningWithExtendClassDef;
    return r`
      <div class="inner-section">
        <details class="inner" .open="${!t}">
          <summary>
            <span class="icon ${t ? "success" : "warning"}"
              >${t ? d.checkCircle : d.alertTriangle}</span
            >
            <div>Enable HotswapAgent</div>
          </summary>
          <div class="hint">
            <ul>
              ${e.intellij ? r`<li>Launch as mentioned in the previous step</li>` : g}
              ${e.intellij ? r`<li>
                    To manually configure IntelliJ, add the
                    <code>-XX:HotswapAgent=fatjar -XX:+AllowEnhancedClassRedefinition -XX:+UpdateClasses</code> JVM
                    arguments when launching the application
                  </li>` : r`<li>
                    Add the
                    <code>-XX:HotswapAgent=fatjar -XX:+AllowEnhancedClassRedefinition -XX:+UpdateClasses</code> JVM
                    arguments when launching the application
                  </li>`}
            </ul>
          </div>
        </details>
      </div>
    `;
  }
  renderHotswapAgentJdkSection(e) {
    const t = f.jdkInfo?.extendedClassDefCapable, i = this.downloadStatusMessages?.[this.downloadStatusMessages.length - 1] === Me;
    return r`
      <div class="inner-section">
        <details class="inner" .open="${!t}">
          <summary>
            <span class="icon ${t ? "success" : "warning"}"
              >${t ? d.checkCircle : d.alertTriangle}</span
            >
            <div>Run using JetBrains Runtime JDK</div>
          </summary>
          <div class="hint">
            JetBrains Runtime provides much better hotswapping compared to other JDKs.
            <ul>
              ${e.intellij && Jt("1.3.0", l.idePluginState?.version) ? r` <li>Upgrade to the latest IntelliJ plugin</li>` : g}
              ${e.intellij ? r` <li>Launch the application in IntelliJ using "Debug using Hotswap Agent"</li>` : g}
              ${e.vscode ? r` <li>
                    <a href @click="${(n) => this.downloadJetbrainsRuntime(n)}"
                      >Let Copilot download and set up JetBrains Runtime for VS Code</a
                    >
                    ${this.downloadProgress > 0 ? r`<vaadin-progress-bar
                          .value="${this.downloadProgress}"
                          min="0"
                          max="1"></vaadin-progress-bar>` : g}
                    <ul>
                      ${this.downloadStatusMessages.map((n) => r`<li>${n}</li>`)}
                      ${i ? r`<h3>Go to VS Code and launch the 'Debug using Hotswap Agent' configuration</h3>` : g}
                    </ul>
                  </li>` : g}
              <li>
                ${e.intellij || e.vscode ? r`If there is a problem, you can manually
                      <a target="_blank" href="${Le}">download JetBrains Runtime JDK</a> and set up
                      your debug configuration to use it.` : r`<a target="_blank" href="${Le}">Download JetBrains Runtime JDK</a> and set up
                      your debug configuration to use it.`}
              </li>
            </ul>
          </div>
        </details>
      </div>
    `;
  }
  renderInstallHotswapAgentJdkSection(e) {
    const t = f.jdkInfo?.hotswapAgentFound, i = f.jdkInfo?.extendedClassDefCapable;
    return r`
      <div class="inner-section">
        <details class="inner" .open="${!t}">
          <summary>
            <span class="icon ${t ? "success" : "warning"}"
              >${t ? d.checkCircle : d.alertTriangle}</span
            >
            <div>Install HotswapAgent</div>
          </summary>
          <div class="hint">
            Hotswap Agent provides application level support for hot reloading, such as reinitalizing Vaadin @Route or
            @BrowserCallable classes when they are updated
            <ul>
              ${e.intellij ? r`<li>Launch as mentioned in the previous step</li>` : g}
              ${!e.intellij && !i ? r`<li>First install JetBrains Runtime as mentioned in the step above.</li>` : g}
              ${e.intellij ? r`<li>
                    To manually configure IntelliJ, download HotswapAgent and install the jar file as
                    <code>[JAVA_HOME]/lib/hotswap/hotswap-agent.jar</code> in the JetBrains Runtime JDK. Note that the
                    file must be renamed to exactly match this path.
                  </li>` : r`<li>
                    Download HotswapAgent and install the jar file as
                    <code>[JAVA_HOME]/lib/hotswap/hotswap-agent.jar</code> in the JetBrains Runtime JDK. Note that the
                    file must be renamed to exactly match this path.
                  </li>`}
            </ul>
          </div>
        </details>
      </div>
    `;
  }
  renderHotswapAgentVersionSection() {
    if (!f.jdkInfo?.hotswapAgentFound)
      return g;
    const e = f.jdkInfo?.hotswapVersionOk, t = f.jdkInfo?.hotswapVersion, i = f.jdkInfo?.hotswapAgentLocation;
    return r`
      <div class="inner-section">
        <details class="inner" .open="${!e}">
          <summary>
            <span class="icon ${e ? "success" : "warning"}"
              >${e ? d.checkCircle : d.alertTriangle}</span
            >
            <div>Hotswap version requires update</div>
          </summary>
          <div class="hint">
            HotswapAgent version ${t} is in use
            <a target="_blank" href="https://github.com/HotswapProjects/HotswapAgent/releases"
              >Download the latest HotswapAgent</a
            >
            and place it in <code>${i}</code>
          </div>
        </details>
      </div>
    `;
  }
  renderJRebelInstalledContent() {
    return r` <div>JRebel is in use. Enjoy your awesome development workflow!</div> `;
  }
  renderHotswapAgentInstalledContent() {
    return r` <div>Hotswap agent is in use. Enjoy your awesome development workflow!</div> `;
  }
  async downloadJetbrainsRuntime(e) {
    return e.target.disabled = !0, e.preventDefault(), this.downloadStatusMessages = [], ae(`${Ge}set-up-vs-code-hotswap`, {}, (t) => {
      t.data.error ? (We("Error downloading JetBrains runtime", t.data.error), this.downloadStatusMessages = [...this.downloadStatusMessages, "Download failed"]) : this.downloadStatusMessages = [...this.downloadStatusMessages, Me];
    });
  }
  downloadStatusUpdate(e) {
    const t = e.detail.progress;
    t ? this.downloadProgress = t : this.downloadStatusMessages = [...this.downloadStatusMessages, e.detail.message];
  }
  addESCListener() {
    document.addEventListener("keydown", this.handleESC);
  }
  removeESCListener() {
    document.removeEventListener("keydown", this.handleESC);
  }
};
A.NAME = "copilot-development-setup-user-guide";
A.styles = _`
    :host {
      --icon-size: 24px;
      --summary-header-gap: 10px;
      --footer-height: calc(50px + var(--space-150));
      color: var(--color);
    }
    :host code {
      background-color: var(--gray-50);
      font-size: var(--copilot-font-size-xs);
      display: inline-block;
      margin-top: var(--space-100);
      margin-bottom: var(--space-100);
      user-select: all;
    }

    [part='container'] {
      display: flex;
      flex-direction: column;
      gap: var(--space-150);
      padding: var(--space-150);
      box-sizing: border-box;
      height: calc(100% - var(--footer-height));
      overflow: auto;
    }

    [part='footer'] {
      display: flex;
      justify-content: flex-end;
      height: var(--footer-height);
      padding-left: var(--space-150);
      padding-right: var(--space-150);
    }
    [part='hotswap-agent-section-container'] {
      display: flex;
      flex-direction: column;
      gap: var(--space-100);
      position: relative;
    }
    [part='content'] {
      display: flex;
      padding: var(--space-150);
      flex-direction: column;
    }
    div.inner-section div.hint {
      margin-left: calc(var(--summary-header-gap) + var(--icon-size));
      margin-top: var(--space-75);
    }
    details {
      display: flex;
      flex-direction: column;
      box-sizing: border-box;

      & span.icon {
        display: flex;
      }
      & span.icon.warning {
        color: var(--warning-color);
      }
      & span.icon.success {
        color: var(--success-color);
      }
      & span.hotswap.icon {
        position: absolute;
        top: var(--space-75);
        left: var(--space-75);
      }
      & > summary,
      summary::part(header) {
        display: flex;
        flex-direction: row;
        align-items: center;
        cursor: pointer;
        position: relative;
        gap: var(--summary-header-gap);
        font: var(--copilot-font-md);
        font-size: var(--copilot-font-size-sm);
      }
      summary::after,
      summary::part(header)::after {
        content: '';
        display: block;
        width: 4px;
        height: 4px;
        border-color: var(--color);
        opacity: var(--panel-toggle-opacity, 0.2);
        border-width: 2px;
        border-style: solid solid none none;
        transform: rotate(var(--panel-toggle-angle, -45deg));
        position: absolute;
        right: 15px;
        top: calc(50% - var(--panel-toggle-offset, 2px));
      }
      &:not([open]) {
        --panel-toggle-angle: 135deg;
        --panel-toggle-offset: 4px;
      }
    }
    details[part='panel'] {
      background: var(--card-bg);
      border: var(--card-border);
      border-radius: 4px;
      user-select: none;

      &:has(summary:hover) {
        border-color: var(--accent-color);
      }

      & > summary,
      summary::part(header) {
        padding: 10px 10px;
        padding-right: 25px;
      }

      summary:hover,
      summary::part(header):hover {
        --panel-toggle-opacity: 0.5;
      }

      input[type='checkbox'],
      summary::part(checkbox) {
        margin: 0;
      }

      &:not([open]):hover {
        background: var(--card-hover-bg);
      }

      &[open] {
        background: var(--card-open-bg);
        box-shadow: var(--card-open-shadow);

        & > summary {
          font-weight: bold;
        }
      }
      .tabs {
        border-bottom: 1px solid var(--border-color);
        box-sizing: border-box;
        display: flex;
        height: 2.25rem;
      }

      .tab {
        background: none;
        border: none;
        border-bottom: 1px solid transparent;
        color: var(--color);
        font: var(--copilot-font-button);
        height: 2.25rem;
        padding: 0 0.75rem;
      }

      .tab[aria-selected='true'] {
        color: var(--color-high-contrast);
        border-bottom-color: var(--color-high-contrast);
      }

      .tab-content {
        flex: 1 1 auto;
        gap: var(--space-150);
        overflow: auto;
        padding: var(--space-150);
      }

      h3 {
        margin-top: 0;
      }
    }
  `;
F([
  x()
], A.prototype, "javaPluginSectionOpened", 2);
F([
  x()
], A.prototype, "hotswapSectionOpened", 2);
F([
  x()
], A.prototype, "hotswapTab", 2);
F([
  x()
], A.prototype, "downloadStatusMessages", 2);
F([
  x()
], A.prototype, "downloadProgress", 2);
A = F([
  b(A.NAME)
], A);
const te = ut({
  header: "Development Workflow",
  tag: gt,
  width: 800,
  height: 800,
  floatingPosition: {
    top: 50,
    left: 50
  },
  individual: !0
}), qt = {
  init(e) {
    e.addPanel(te);
  }
};
window.Vaadin.copilot.plugins.push(qt);
p.addPanel(te);
var Wt = Object.getOwnPropertyDescriptor, Gt = (e, t, i, n) => {
  for (var o = n > 1 ? void 0 : n ? Wt(t, i) : t, s = e.length - 1, a; s >= 0; s--)
    (a = e[s]) && (o = a(o) || o);
  return o;
};
let ze = class extends I {
  createRenderRoot() {
    return this;
  }
  connectedCallback() {
    super.connectedCallback(), this.classList.add("flex", "gap-2");
  }
  render() {
    let e = "???";
    return c.getAIProvider() === "ANY" ? e = "Any" : c.getAIProvider() === "EU_ONLY" && (e = "Inside EU"), r`
      <vaadin-icon .svg="${d.sparkles}"></vaadin-icon>
      <span class="flex flex-col">
        <span
          >AI Provider:
          <span class="text-blue-11" id="ai-provider"> ${e}</span>
        </span>
        <span class="text-secondary text-xs">Experimental ⋅ Vaadin employees only</span>
      </span>
    `;
  }
};
ze = Gt([
  b("copilot-activation-button-ai-provider")
], ze);
var Yt = Object.getOwnPropertyDescriptor, Xt = (e, t, i, n) => {
  for (var o = n > 1 ? void 0 : n ? Yt(t, i) : t, s = e.length - 1, a; s >= 0; s--)
    (a = e[s]) && (o = a(o) || o);
  return o;
};
let _e = class extends I {
  createRenderRoot() {
    return this;
  }
  connectedCallback() {
    super.connectedCallback(), this.classList.add("flex", "gap-2");
  }
  render() {
    const e = c.isAIUsageAllowed();
    let t, i;
    return e === "ask" ? (t = "Always Ask", i = "text-blue-11") : e === "no" ? (t = "Disabled", i = "text-ruby-11") : (t = "Enabled", i = "text-teal-11"), r`
      <vaadin-icon .svg="${d.sparkles}"></vaadin-icon>
      <span class="flex flex-col">
        <span
          >AI Usage:
          <span class="${i}" id="ai-usage">${t}</span>
        </span>
      </span>
    `;
  }
};
_e = Xt([
  b("copilot-activation-button-ai-usage")
], _e);
var Kt = Object.getOwnPropertyDescriptor, Zt = (e, t, i, n) => {
  for (var o = n > 1 ? void 0 : n ? Kt(t, i) : t, s = e.length - 1, a; s >= 0; s--)
    (a = e[s]) && (o = a(o) || o);
  return o;
};
let Oe = class extends I {
  createRenderRoot() {
    return this;
  }
  connectedCallback() {
    super.connectedCallback(), this.classList.add("flex", "gap-2", "-m-2", "p-2", "rounded-md", "w-full");
  }
  render() {
    const t = Ye(), { status: i, message: n } = t;
    let o = "", s = "", a = "";
    return i === "warning" ? (o = "bg-amber-3 dark:bg-amber-6", s = "text-amber-11", a = "text-amber-12 dark:text-amber-11") : i === "error" && (o = "bg-ruby-3 dark:bg-ruby-6", s = "text-ruby-11", a = "text-ruby-11"), o && this.classList.add(...o.split(" ")), r`
      <vaadin-icon class="${s}" .svg="${d.bolt}"></vaadin-icon>
      <span class="flex flex-col">
        Development Workflow
        <span class="text-xs ${a}">${n}</span>
      </span>
    `;
  }
};
Oe = Zt([
  b("copilot-activation-button-development-workflow")
], Oe);
var Qt = Object.getOwnPropertyDescriptor, ei = (e, t, i, n) => {
  for (var o = n > 1 ? void 0 : n ? Qt(t, i) : t, s = e.length - 1, a; s >= 0; s--)
    (a = e[s]) && (o = a(o) || o);
  return o;
};
function ti() {
  p.updatePanel("copilot-feedback-panel", {
    floating: !0
  }), c.setFeedbackDisplayedAtLeastOnce(!0);
}
let Te = class extends I {
  constructor() {
    super(), this.reaction(
      () => c.isFeedbackDisplayedAtLeastOnce(),
      () => {
        this.requestUpdate();
      }
    );
  }
  createRenderRoot() {
    return this;
  }
  connectedCallback() {
    super.connectedCallback(), this.classList.add("flex", "gap-2");
  }
  render() {
    return r`
      <vaadin-icon .svg="${d.feedback}"></vaadin-icon>
      <span class="flex flex-col">
        <span class="inline-flex items-center gap-2">
          Tell Us What You Think
          ${c.isFeedbackDisplayedAtLeastOnce() ? g : r`<span class="bg-blue-11 inline-flex rounded-full size-1"></span>`}
        </span>
        <span class="text-secondary text-xs">Give feedback or report an issue</span>
      </span>
    `;
  }
};
Te = ei([
  b("copilot-activation-button-feedback")
], Te);
var ii = Object.getOwnPropertyDescriptor, ni = (e, t, i, n) => {
  for (var o = n > 1 ? void 0 : n ? ii(t, i) : t, s = e.length - 1, a; s >= 0; s--)
    (a = e[s]) && (o = a(o) || o);
  return o;
};
let He = class extends I {
  constructor() {
    super(), this.reaction(
      () => l.userInfo,
      () => {
        this.requestUpdate();
      }
    );
  }
  createRenderRoot() {
    return this;
  }
  connectedCallback() {
    super.connectedCallback(), this.classList.add("flex", "gap-3", "items-center", "px-1", "w-full"), this.addEventListener("click", this.clickListener);
  }
  disconnectedCallback() {
    super.disconnectedCallback(), this.removeEventListener("click", this.clickListener);
  }
  render() {
    const e = this.getStatus();
    return r`
      ${this.renderPortrait()}
      <span class="flex flex-col">
        <span>${this.getUsername()}</span>
        ${e ? r`<span class="text-xs">${e}</span>` : g}
      </span>
      <span aria-hidden="true">${this.renderDot()}</span>
    `;
  }
  clickListener() {
    if (l.userInfo?.validLicense) {
      window.open("https://vaadin.com/myaccount", "_blank", "noopener");
      return;
    }
    if (S.active) {
      ae(`${Ge}log-in`, {}, (e) => {
        window.open(e.data.loginUrl, "_blank");
      }).catch((e) => We("Login processing failed", e));
      return;
    }
    l.setLoginCheckActive(!0);
  }
  getUsername() {
    return l.userInfo?.firstName ? `${l.userInfo.firstName} ${l.userInfo.lastName}` : "Log in";
  }
  getStatus() {
    if (l.userInfo?.validLicense)
      return l.userInfo?.copilotProjectCannotLeaveLocalhost ? "AI Disabled" : void 0;
    if (S.active) {
      const e = Math.round(S.remainingTimeInMillis / 864e5);
      return `Trial expires in ${e}${e === 1 ? " day" : " days"}`;
    }
    if (S.expired && !l.userInfo?.validLicense)
      return "Trial expired";
    if (!S.active && !S.expired && !l.userInfo?.validLicense)
      return "No valid license available";
  }
  renderPortrait() {
    return l.userInfo?.portraitUrl ? r`<div
        class="bg-cover border border-2 border-amber-9 rounded-full size-10"
        style="background-image: url('https://vaadin.com${l.userInfo.portraitUrl}')"></div>` : g;
  }
  renderDot() {
    return l.userInfo?.validLicense ? g : S.active || S.expired ? r`<div class="dot warning"></div>` : g;
  }
};
He = ni([
  b("copilot-activation-button-user-info")
], He);
function v(e) {
  return tt("vaadin-menu-bar-item", e);
}
function ce(e) {
  return tt("vaadin-context-menu-item", e);
}
function tt(e, t) {
  const i = document.createElement(e);
  if (t.style && (i.className = t.style), i.classList.add("no-checkmark"), t.icon)
    if (typeof t.icon == "string") {
      const n = document.createElement("vaadin-icon");
      n.setAttribute("icon", t.icon), i.append(n);
    } else {
      const n = document.createElement("vaadin-icon");
      n.svg = t.icon, i.append(n);
    }
  if (t.label) {
    const n = document.createElement("span");
    n.classList.add("me-auto"), n.innerHTML = t.label, i.append(n);
  } else if (t.component) {
    const n = mt(t.component) ? t.component : document.createElement(t.component);
    i.append(n);
  }
  if (t.description) {
    const n = document.createElement("span");
    n.className = "text-secondary text-xs", n.innerHTML = t.description, i.append(n);
  }
  if (t.hint) {
    const n = t.hint.replace(
      "<kbd",
      '<kbd class="bg-gray-5 dark:bg-gray-7 font-sans ms-auto px-1.5 rounded-md"'
    );
    i.insertAdjacentHTML("beforeend", n);
  }
  if (t.suffix)
    if (typeof t.suffix == "string") {
      const n = document.createElement("span");
      n.innerHTML = t.suffix, i.append(n);
    } else
      i.append(oi(t.suffix.strings[0]));
  return i;
}
function oi(e) {
  if (!e) return null;
  const t = document.createElement("template");
  t.innerHTML = e;
  const i = t.content.children;
  return i.length === 1 ? i[0] : i;
}
function it(e) {
  return ae("copilot-switch-user", { username: e }, (t) => t.data.error ? { success: !1, errorMessage: t.data.error.message } : { success: !0 });
}
var si = Object.defineProperty, ai = Object.getOwnPropertyDescriptor, V = (e, t, i, n) => {
  for (var o = n > 1 ? void 0 : n ? ai(t, i) : t, s = e.length - 1, a; s >= 0; s--)
    (a = e[s]) && (o = (n ? a(t, i, o) : a(o)) || o);
  return n && o && si(t, i, o), o;
};
const ri = 8;
function li() {
  const e = document.createElement("vaadin-text-field");
  return e.label = "Username to Switch To", e.classList.add("w-full"), e.autocomplete = "off", e.addEventListener("click", async (t) => {
    t.stopPropagation();
  }), e.addEventListener("keydown", async (t) => {
    if (t.stopPropagation(), t.key === "Enter") {
      const i = e.value, n = await it(i);
      n.success ? (c.addRecentSwitchedUsername(i), window.location.reload()) : (e.errorMessage = n.errorMessage, e.invalid = !0);
    }
  }), e;
}
let fe = class extends I {
  constructor() {
    super(...arguments), this.username = "";
  }
  connectedCallback() {
    super.connectedCallback(), this.style.display = "contents";
  }
  render() {
    return r`<span style="flex: 1;  display: flex; justify-content: space-between;"
      ><span>${this.username}</span
      ><span
        @click=${(e) => {
      c.removeRecentSwitchedUsername(this.username), e.stopPropagation();
      const t = this.parentElement;
      if (t.style.display = "none", c.getRecentSwitchedUsernames().length === 0) {
        const i = t.parentElement?.firstElementChild;
        i && (i.style.display = "none");
      }
    }}
        >${d.delete}</span
      ></span
    >`;
  }
};
V([
  y({ type: String })
], fe.prototype, "username", 2);
fe = V([
  b("copilot-switch-user")
], fe);
function di(e) {
  const t = document.createElement("copilot-switch-user");
  return t.username = e, t;
}
let G = class extends I {
  constructor() {
    super(...arguments), this.initialMouseDownPosition = null, this.dragging = !1, this.items = [], this.mouseDownListener = (e) => {
      e.composed && e.composedPath().some((t) => t instanceof HTMLElement && t === this.getMenuBarButton()) && (this.initialMouseDownPosition = { x: e.clientX, y: e.clientY }, C.draggingStarts(this, e), document.addEventListener("mousemove", this.documentDraggingMouseMoveEventListener));
    }, this.documentDraggingMouseMoveEventListener = (e) => {
      if (this.initialMouseDownPosition && !this.dragging) {
        const { clientX: t, clientY: i } = e;
        this.dragging = Math.abs(t - this.initialMouseDownPosition.x) + Math.abs(i - this.initialMouseDownPosition.y) > ri;
      }
      this.dragging && (document.body.style.webkitUserSelect = "none", this.setSubMenuVisibility(!1), C.dragging(this, e));
    }, this.documentMouseUpListener = (e) => {
      if (this.initialMouseDownPosition = null, document.removeEventListener("mousemove", this.documentDraggingMouseMoveEventListener), this.dragging) {
        const t = C.dragging(this, e);
        c.setActivationButtonPosition(t), this.setSubMenuVisibility(!0);
      } else
        this.setMenuBarOnClick();
      this.postDragReset(e.composed ? e.composedPath() : []);
    }, this.postDragReset = j((e) => {
      this.dragging = !1, this.closeMenuIfMouseTargetIsOutsideOfActivationButton(e), this.resetBodyInlineStyles();
    }, 100), this.closeMenuMouseMoveListener = (e) => {
      e.composed && (this.dragging || this.closeMenuIfMouseTargetIsOutsideOfActivationButton(e.composedPath()));
    }, this.closeMenuIfMouseTargetIsOutsideOfActivationButton = (e) => {
      e.some((i) => {
        if (i instanceof HTMLElement) {
          const n = i;
          if (n.localName === this.localName || n.localName === "vaadin-menu-bar-overlay" && n.classList.contains("activation-button-menu"))
            return !0;
        }
        return !1;
      }) ? this.closeMenuWithDebounce.clear() : this.closeMenuWithDebounce();
    }, this.closeMenuWithDebounce = j(() => {
      this.closeMenu();
    }, 250), this.activationBtnClicked = (e) => {
      if (this.dragging) {
        e?.preventDefault();
        return;
      }
      if (l.active && this.handleAttentionRequiredOnClick()) {
        e?.stopPropagation(), e?.preventDefault();
        return;
      }
      e?.stopPropagation(), this.dispatchEvent(new CustomEvent("activation-btn-clicked")), requestAnimationFrame(() => {
        this.closeMenu(), this.openMenu();
      });
    }, this.handleAttentionRequiredOnClick = () => {
      const e = p.getAttentionRequiredPanelConfiguration();
      return e ? e.panel && !e.floating ? (u.emit("open-attention-required-drawer", null), !0) : (p.clearAttention(), !0) : !1;
    }, this.closeMenu = () => {
      this.menubar._close();
    }, this.openMenu = () => {
      this.menubar._buttons[0].dispatchEvent(new CustomEvent("mouseover", { bubbles: !0 }));
    }, this.setMenuBarOnClick = () => {
      const e = this.shadowRoot.querySelector("vaadin-menu-bar-button");
      e && (e.onclick = this.activationBtnClicked);
    };
  }
  static get styles() {
    return [
      m(se),
      m(Pe),
      m(Ie),
      m(Xe),
      m(Y),
      _`
        :host {
          --space: 1rem;
          --height: var(--copilot-size-xl);
          --width: var(--copilot-size-xl);
          position: absolute;
          top: clamp(var(--space), var(--top), calc(100vh - var(--height) - var(--space)));
          left: clamp(var(--space), var(--left), calc(100vw - var(--width) - var(--space)));
          bottom: clamp(var(--space), var(--bottom), calc(100vh - var(--height) - var(--space)));
          right: clamp(var(--space), var(--right), calc(100vw - var(--width) - var(--space)));
          user-select: none;
          -ms-user-select: none;
          -moz-user-select: none;
          -webkit-user-select: none;
          --indicator-color: var(--ruby-9);
          /* Don't add a z-index or anything else that creates a stacking context */
        }
        :host([document-hidden]) {
          filter: grayscale(100%);
          -webkit-filter: grayscale(100%);
          z-index: 200;
        }
        :host([attention-required]) {
          animation: bounce 0.5s;
          animation-iteration-count: 2;
        }
        [part='indicator'] {
          background: var(--indicator-color);
          display: var(--indicator-display, none);
          z-index: calc(var(--z-index-activation-button) + 1);
        }
        :host([indicator='info']) {
          --indicator-color: var(--blue-9);
          --indicator-display: block;
        }
        :host([indicator='warning']) {
          --indicator-color: var(--amber-9);
          --indicator-display: block;
        }
        :host([indicator='error']) {
          --indicator-color: var(--ruby-9);
          --indicator-display: block;
        }
      `
    ];
  }
  connectedCallback() {
    super.connectedCallback(), this.reaction(
      () => p.attentionRequiredPanelTag,
      () => {
        this.toggleAttribute(E, p.attentionRequiredPanelTag !== null), this.updateIndicator();
      }
    ), this.reaction(
      () => l.active,
      () => {
        this.toggleAttribute("active", l.active);
      },
      { fireImmediately: !0 }
    ), this.addEventListener("mousedown", this.mouseDownListener), document.addEventListener("mouseup", this.documentMouseUpListener);
    const e = c.getActivationButtonPosition();
    e ? (this.style.setProperty("--left", `${e.left}px`), this.style.setProperty("--bottom", `${e.bottom}px`), this.style.setProperty("--right", `${e.right}px`), this.style.setProperty("--top", `${e.top}px`)) : (this.style.setProperty("--bottom", "var(--space)"), this.style.setProperty("--right", "var(--space)")), u.on("document-activation-change", (t) => {
      this.toggleAttribute("document-hidden", !t.detail.active);
    }), this.reaction(
      () => [
        f.jdkInfo,
        l.idePluginState,
        c.isFeedbackDisplayedAtLeastOnce()
      ],
      () => {
        this.updateIndicator();
      }
    ), this.reaction(
      () => [
        l.active,
        l.idePluginState,
        c.isActivationAnimation(),
        c.isActivationShortcut(),
        c.isSendErrorReportsAllowed(),
        c.isAIUsageAllowed(),
        c.getDismissedNotifications()
      ],
      () => {
        this.generateItems();
      }
    ), document.addEventListener("mousemove", this.closeMenuMouseMoveListener);
  }
  disconnectedCallback() {
    super.disconnectedCallback(), this.removeEventListener("mousedown", this.mouseDownListener), document.removeEventListener("mouseup", this.documentMouseUpListener), document.removeEventListener("mousemove", this.closeMenuMouseMoveListener);
  }
  updateIndicator() {
    if (this.hasAttribute(E)) {
      this.setAttribute("indicator", "error");
      return;
    }
    const e = Ye();
    if (e.status !== "success") {
      this.setAttribute("indicator", e.status);
      return;
    }
    if (!c.isFeedbackDisplayedAtLeastOnce()) {
      this.setAttribute("indicator", "info");
      return;
    }
    this.removeAttribute("indicator");
  }
  /**
   * To hide overlay while dragging
   * @param visible
   */
  setSubMenuVisibility(e) {
    const t = this.shadowRoot.querySelector("vaadin-menu-bar-submenu");
    if (!t)
      return;
    const i = t.$.overlay;
    e ? i.style.visibility = "" : i.style.visibility = "hidden";
  }
  resetBodyInlineStyles() {
    document.body.style.webkitUserSelect === "none" && (document.body.style.webkitUserSelect = "");
  }
  generateItems() {
    const e = l.active, t = e && !!l.idePluginState?.supportedActions?.find((s) => s === "undo"), i = [], n = li();
    if (f.springSecurityEnabled) {
      const s = c.getRecentSwitchedUsernames();
      i.push(
        ...s.map((a) => ({
          component: v({ component: di(a) }),
          action: async () => {
            const h = await it(a);
            h.success ? window.location.reload() : n && (n.errorMessage = h.errorMessage, n.invalid = !0);
          }
        }))
      ), i.length > 0 && i.unshift({
        component: v({ label: "Recently Used Usernames" }),
        disabled: !0
      });
    }
    const o = [
      {
        text: "Vaadin Copilot",
        children: [
          { visible: e, component: v({ component: "copilot-activation-button-user-info" }) },
          { visible: e, component: "hr" },
          {
            component: v({ component: "copilot-activation-button-development-workflow" }),
            action: bt
          },
          { visible: f.springSecurityEnabled, component: "hr" },
          {
            visible: f.springSecurityEnabled,
            component: v({
              icon: d.accountCircle,
              label: "Application's User"
            }),
            children: [
              ...i,
              {
                component: v({ component: n })
              }
            ]
          },
          {
            component: "hr",
            visible: t
          },
          {
            visible: t,
            component: v({
              icon: d.undo,
              label: "Undo",
              hint: le.undo
            }),
            action: () => {
              u.emit("undoRedo", { undo: !0 });
            }
          },
          {
            visible: t,
            component: v({
              icon: d.redo,
              label: "Redo",
              hint: le.redo
            }),
            action: () => {
              u.emit("undoRedo", { undo: !1 });
            }
          },
          {
            component: "hr",
            visible: e
          },
          {
            visible: e,
            component: v({
              icon: d.settings,
              label: "Settings"
            }),
            children: [
              {
                component: v({
                  icon: d.keyboardAlt,
                  label: "Activation Shortcut",
                  suffix: c.isActivationShortcut() ? '<button aria-checked="true" aria-hidden="true" class="my-px" role="switch" type="button"><span></span></button>' : '<button aria-checked="false" aria-hidden="true" class="my-px" role="switch" type="button"><span></span></button>'
                }),
                keepOpen: !0,
                action: (s) => {
                  c.setActivationShortcut(!c.isActivationShortcut()), pe(s, c.isActivationShortcut());
                }
              },
              {
                component: v({
                  icon: d.playCircle,
                  label: "Activation Animation",
                  suffix: c.isActivationAnimation() ? '<button aria-checked="true" aria-hidden="true" class="my-px" role="switch" type="button"><span></span></button>' : '<button aria-checked="false" aria-hidden="true" class="my-px" role="switch" type="button"><span></span></button>'
                }),
                keepOpen: !0,
                action: (s) => {
                  c.setActivationAnimation(!c.isActivationAnimation()), pe(s, c.isActivationAnimation());
                }
              },
              {
                component: v({
                  component: "copilot-activation-button-ai-usage"
                }),
                keepOpen: !0,
                action: (s) => {
                  let a;
                  const h = c.isAIUsageAllowed();
                  h === "ask" ? a = "yes" : h === "no" ? a = "ask" : a = "no", c.setAIUsageAllowed(a), Ue(s);
                }
              },
              {
                visible: l.userInfo?.vaadiner,
                component: v({ component: "copilot-activation-button-ai-provider" }),
                keepOpen: !0,
                action: (s) => {
                  const a = c.getAIProvider() === "ANY" ? "EU_ONLY" : "ANY";
                  c.setAIProvider(a), Ue(s);
                }
              },
              {
                component: v({
                  icon: d.bugReport,
                  label: "Report Errors to Vaadin",
                  suffix: c.isSendErrorReportsAllowed() ? '<button aria-checked="true" aria-hidden="true" class="my-px" role="switch" type="button"><span></span></button>' : '<button aria-checked="false" aria-hidden="true" class="my-px" role="switch" type="button"><span></span></button>'
                }),
                keepOpen: !0,
                action: (s) => {
                  c.setSendErrorReportsAllowed(!c.isSendErrorReportsAllowed()), pe(s, c.isSendErrorReportsAllowed());
                }
              },
              { component: "hr" },
              {
                visible: e,
                component: v({
                  icon: d.emojiPeople,
                  label: "Show Welcome Message"
                }),
                keepOpen: !0,
                action: () => {
                  l.setWelcomeActive(!0);
                }
              },
              {
                visible: e,
                component: v({
                  icon: d.keyboard,
                  label: "Show Keyboard Shortcuts"
                }),
                action: () => {
                  p.updatePanel("copilot-shortcuts-panel", {
                    floating: !0
                  });
                }
              },
              {
                visible: c.getDismissedNotifications().length > 0,
                component: v({
                  icon: d.deleteSweep,
                  label: "Clear Dismissed Notifications"
                }),
                action: () => {
                  c.clearDismissedNotifications();
                }
              }
            ]
          },
          { component: "hr" },
          {
            component: v({
              component: "copilot-activation-button-feedback"
            }),
            action: ti
          },
          {
            component: v({
              icon: d.vaadin,
              label: "Copilot",
              hint: c.isActivationShortcut() ? le.toggleCopilot : void 0,
              suffix: l.active ? '<button aria-checked="true" aria-hidden="true" class="my-px" role="switch" type="button"><span></span></button>' : '<button aria-checked="false" aria-hidden="true" class="my-px" role="switch" type="button"><span></span></button>'
            }),
            action: () => {
              this.activationBtnClicked();
            }
          }
        ]
      }
    ];
    this.items = o.filter(wt);
  }
  render() {
    return r`
      <vaadin-menu-bar
        theme="dev-tools"
        .items="${this.items}"
        @item-selected="${(e) => {
      this.handleMenuItemClick(e.detail.value);
    }}"
        ?open-on-hover=${!this.dragging}
        overlay-class="activation-button-menu">
      </vaadin-menu-bar>
      <div
        class="absolute border border-2 border-black/50 -end-0.5 rounded-full size-2 -top-0.5"
        part="indicator"></div>
    `;
  }
  handleMenuItemClick(e) {
    e.action && e.action(e);
  }
  getMenuBarButton() {
    return this.shadowRoot.querySelector("vaadin-menu-bar-button");
  }
};
V([
  O("vaadin-menu-bar")
], G.prototype, "menubar", 2);
V([
  x()
], G.prototype, "dragging", 2);
V([
  x()
], G.prototype, "items", 2);
G = V([
  b("copilot-activation-button")
], G);
function pe(e, t) {
  const i = e.component;
  if (!i || typeof i == "string") {
    console.error("Unable to set switch value for a non-component item");
    return;
  }
  const n = i.querySelector('button[role="switch"]');
  if (!n) {
    console.error("No element found when setting switch value");
    return;
  }
  n.setAttribute("aria-checked", t ? "true" : "false");
}
function Ue(e) {
  const t = e.component;
  if (!t || typeof t == "string") {
    console.error("Unable to set switch value for a non-component item");
    return;
  }
  t.requestUpdate && t.requestUpdate();
}
var ci = Object.defineProperty, pi = Object.getOwnPropertyDescriptor, J = (e, t, i, n) => {
  for (var o = n > 1 ? void 0 : n ? pi(t, i) : t, s = e.length - 1, a; s >= 0; s--)
    (a = e[s]) && (o = (n ? a(t, i, o) : a(o)) || o);
  return n && o && ci(t, i, o), o;
};
const P = "resize-dir", he = "floating-resizing-active";
let z = class extends I {
  constructor() {
    super(...arguments), this.panelTag = "", this.dockingItems = [
      {
        component: ce({
          icon: d.dockToRight,
          label: "Dock right"
        }),
        panel: "right"
      },
      {
        component: ce({
          icon: d.dockToLeft,
          label: "Dock left"
        }),
        panel: "left"
      },
      {
        component: ce({
          icon: d.dockToBottom,
          label: "Dock bottom"
        }),
        panel: "bottom"
      }
    ], this.floatingResizingStarted = !1, this.resizingInDrawerStarted = !1, this.toggling = !1, this.rectangleBeforeResizing = null, this.floatingResizeHandlerMouseMoveListener = (e) => {
      if (!this.panelInfo?.floating || this.floatingResizingStarted || !this.panelInfo?.expanded)
        return;
      const t = this.getBoundingClientRect(), i = Math.abs(e.clientX - t.x), n = Math.abs(t.x + t.width - e.clientX), o = Math.abs(e.clientY - t.y), s = Math.abs(t.y + t.height - e.clientY), a = Number.parseInt(
        window.getComputedStyle(this).getPropertyValue("--floating-offset-resize-threshold"),
        10
      );
      let h = "";
      i < a ? o < a ? (h = "nw-resize", this.setAttribute(P, "top left")) : s < a ? (h = "sw-resize", this.setAttribute(P, "bottom left")) : (h = "col-resize", this.setAttribute(P, "left")) : n < a ? o < a ? (h = "ne-resize", this.setAttribute(P, "top right")) : s < a ? (h = "se-resize", this.setAttribute(P, "bottom right")) : (h = "col-resize", this.setAttribute(P, "right")) : s < a ? (h = "row-resize", this.setAttribute(P, "bottom")) : o < a && (h = "row-resize", this.setAttribute(P, "top")), h !== "" ? (this.rectangleBeforeResizing = this.getBoundingClientRect(), this.style.setProperty("--resize-cursor", h)) : (this.style.removeProperty("--resize-cursor"), this.removeAttribute(P)), this.toggleAttribute(he, h !== "");
    }, this.floatingResizingMouseDownListener = (e) => {
      this.hasAttribute(he) && e.button === 0 && (e.stopPropagation(), e.preventDefault(), this.rectangleBeforeResizing = this.getBoundingClientRect(), C.anchorLeftTop(this), this.floatingResizingStarted = !0, this.toggleAttribute("resizing", !0), l.setSectionPanelResizing(!0));
    }, this.floatingResizingMouseLeaveListener = () => {
      this.panelInfo?.floating && (this.floatingResizingStarted || (this.removeAttribute("resizing"), this.removeAttribute(he), this.removeAttribute("dragging"), this.style.removeProperty("--resize-cursor"), this.removeAttribute(P), this.panelInfo != null && this.panelInfo.height != null && this.panelInfo?.height > window.innerHeight && (p.updatePanel(this.panelInfo.tag, {
        height: window.innerHeight - 10
      }), this.setCssSizePositionProperties())));
    }, this.floatingResizingMouseMoveListener = (e) => {
      if (!this.panelInfo?.floating || !this.floatingResizingStarted)
        return;
      e.stopPropagation(), e.preventDefault();
      const t = this.getResizeDirections(), { clientX: i, clientY: n } = e;
      t.forEach((o) => this.setResizePosition(o, i, n));
    }, this.setFloatingResizeDirectionProps = (e, t, i, n) => {
      i && i > Number.parseFloat(window.getComputedStyle(this).getPropertyValue("--min-width")) && (this.style.setProperty(`--${e}`, `${t}px`), this.style.setProperty("width", `${i}px`));
      const o = window.getComputedStyle(this), s = Number.parseFloat(o.getPropertyValue("--header-height")), a = Number.parseFloat(o.getPropertyValue("--floating-offset-resize-threshold")) / 2;
      n && n > s + a && (this.style.setProperty(`--${e}`, `${t}px`), this.style.setProperty("height", `${n}px`), this.container.style.setProperty("margin-top", "calc(var(--floating-offset-resize-threshold) / 4)"), this.container.style.height = `calc(${n}px - var(--floating-offset-resize-threshold) / 2)`, this.contentArea.style.setProperty("height", `${n}px`));
    }, this.floatingResizingMouseUpListener = (e) => {
      if (!this.floatingResizingStarted || !this.panelInfo?.floating)
        return;
      e.stopPropagation(), e.preventDefault(), this.floatingResizingStarted = !1, this.contentArea.style.removeProperty("height"), l.setSectionPanelResizing(!1);
      const { width: t, height: i } = this.getBoundingClientRect(), { left: n, top: o, bottom: s, right: a } = C.anchor(this);
      p.updatePanel(this.panelInfo.tag, {
        width: t,
        height: i,
        floatingPosition: {
          ...this.panelInfo.floatingPosition,
          left: n,
          top: o,
          bottom: s,
          right: a
        }
      }), this.style.removeProperty("width"), this.style.removeProperty("height"), this.container.style.removeProperty("height"), this.container.style.removeProperty("margin-top"), this.setCssSizePositionProperties(), this.toggleAttribute("dragging", !1);
    }, this.transitionEndEventListener = () => {
      this.toggling && (this.toggling = !1, C.anchor(this));
    }, this.sectionPanelMouseEnterListener = () => {
      this.hasAttribute(E) && (this.removeAttribute(E), p.clearAttention());
    }, this.contentAreaMouseDownListener = () => {
      p.bringToFront(this.panelInfo.tag);
    }, this.documentMouseUpEventListener = () => {
      document.removeEventListener("mousemove", this.draggingEventListener), this.panelInfo?.floating && (this.toggleAttribute("dragging", !1), l.setSectionPanelDragging(!1));
    }, this.panelHeaderMouseDownEventListener = (e) => {
      e.button === 0 && (p.bringToFront(this.panelInfo.tag), !this.hasAttribute(P) && (e.target instanceof HTMLElement && e.target.getAttribute("part") === "title-button" ? this.startDraggingDebounce(e) : this.startDragging(e)));
    }, this.panelHeaderMouseUpEventListener = (e) => {
      e.button === 0 && this.startDraggingDebounce.clear();
    }, this.startDragging = (e) => {
      C.draggingStarts(this, e), document.addEventListener("mousemove", this.draggingEventListener), l.setSectionPanelDragging(!0), this.panelInfo?.floating ? this.toggleAttribute("dragging", !0) : this.parentElement.sectionPanelDraggingStarted(this, e), e.preventDefault(), e.stopPropagation();
    }, this.startDraggingDebounce = j(this.startDragging, 200), this.draggingEventListener = (e) => {
      const t = C.dragging(this, e);
      if (this.panelInfo?.floating && this.panelInfo?.floatingPosition) {
        e.preventDefault();
        const { left: i, top: n, bottom: o, right: s } = t;
        p.updatePanel(this.panelInfo.tag, {
          floatingPosition: {
            ...this.panelInfo.floatingPosition,
            left: i,
            top: n,
            bottom: o,
            right: s
          }
        });
      }
    }, this.setCssSizePositionProperties = () => {
      const e = p.getPanelByTag(this.panelTag);
      if (!e)
        return;
      if (e.floating && e.expanded && e.height !== void 0 ? this.style.setProperty("height", `${e.height}px`) : this.style.removeProperty("height"), e.height !== void 0 && (this.panelInfo?.floating || e.panel === "left" || e.panel === "right" ? this.style.setProperty("--section-height", `${e.height}px`) : this.style.removeProperty("--section-height")), e.width !== void 0 && (e.floating || e.panel === "bottom" ? this.style.setProperty("--section-width", `${e.width}px`) : this.style.removeProperty("--section-width")), e.floating && e.floatingPosition && !this.toggling) {
        const { left: i, top: n, bottom: o, right: s } = e.floatingPosition;
        this.style.setProperty("--left", i !== void 0 ? `${i}px` : "auto"), this.style.setProperty("--top", n !== void 0 ? `${n}px` : "auto"), this.style.setProperty("--bottom", o !== void 0 ? `${o}px` : ""), this.style.setProperty("--right", s !== void 0 ? `${s}px` : "");
        const a = window.getComputedStyle(this);
        parseInt(a.top, 10) < 0 && this.style.setProperty("--top", "0px"), parseInt(a.bottom, 10) < 0 && this.style.setProperty("--bottom", "0px");
      }
    }, this.renderPopupButton = () => {
      if (!this.panelInfo)
        return g;
      let e;
      return this.panelInfo.panel === void 0 ? e = "Close the popup" : e = this.panelInfo.floating ? `Dock ${this.panelInfo.header} to ${this.panelInfo.panel}` : `Open ${this.panelInfo.header} as a popup`, r`
      <vaadin-context-menu
        @opened-changed="${(t) => {
        if (!t.currentTarget)
          return;
        const i = t.currentTarget, n = t.detail.value;
        u.emit("vaadin-drawer-opened-changed", { opened: n, owner: i });
      }}"
        .items=${this.dockingItems}
        @item-selected="${this.changeDockingPanel}">
        <vaadin-button
          aria-label=${e}
          @click="${(t) => this.changePanelFloating(t)}"
          @mousedown="${(t) => t.stopPropagation()}"
          part="popup-button"
          theme="icon tertiary">
          <vaadin-icon .svg="${this.getPopupButtonIcon()}"></vaadin-icon>
          <vaadin-tooltip slot="tooltip" text="${e}"></vaadin-tooltip>
        </vaadin-button>
      </vaadin-context-menu>
    `;
    }, this.changePanelFloating = (e) => {
      if (this.panelInfo)
        if (e.stopPropagation(), Re(this), this.panelInfo?.floating)
          p.updatePanel(this.panelInfo.tag, { floating: !1 });
        else {
          let t;
          if (this.panelInfo.floatingPosition)
            t = this.panelInfo.floatingPosition;
          else {
            const { left: o, top: s } = this.getBoundingClientRect();
            t = {
              left: o,
              top: s
            };
          }
          let i = this.panelInfo?.height;
          i === void 0 && this.panelInfo.expanded && (i = Number.parseInt(window.getComputedStyle(this).height, 10)), this.parentElement.forceClose(), p.updatePanel(this.panelInfo.tag, {
            floating: !0,
            expanded: !0,
            width: this.panelInfo?.width || Number.parseInt(window.getComputedStyle(this).width, 10),
            height: i,
            floatingPosition: t
          }), p.bringToFront(this.panelInfo.tag);
        }
    }, this.toggleExpand = (e) => {
      this.panelInfo && (e.stopPropagation(), C.anchorLeftTop(this), p.updatePanel(this.panelInfo.tag, {
        expanded: !this.panelInfo.expanded
      }), this.toggling = !0, this.toggleAttribute("expanded", this.panelInfo.expanded), u.emit("panel-expanded", { panelTag: this.panelInfo.tag, expanded: this.panelInfo.expanded }));
    };
  }
  static get styles() {
    return [
      m(se),
      m(Pe),
      m(Ie),
      m(Xe),
      m(Y),
      _`
        * {
          box-sizing: border-box;
        }

        :host {
          flex: none;
          --min-width: 160px;
          --header-height: 40px;
          --content-width: var(--content-width, 100%);
          --floating-border-width: 1px;
          --floating-offset-resize-threshold: 8px;
          cursor: var(--cursor, var(--resize-cursor, default));
          overflow: hidden;
        }

        :host(:not([expanded])) {
          grid-template-rows: auto 0fr;
        }

        :host([floating]) {
          --content-height: calc(var(--section-height));
        }

        [part='content'] {
          height: calc(var(--content-height) - var(--header-height));
          overflow: auto;
          transition:
            height var(--duration-2),
            width var(--duration-2),
            opacity var(--duration-2),
            visibility calc(var(--duration-2) * 2);
        }

        :host([floating]) [part='drawer-resize'] {
          display: none;
        }

        :host(:not([expanded])) [part='drawer-resize'] {
          display: none;
        }

        :host(:not([floating]):not(:last-child)) {
          border-bottom: 1px solid var(--divider-primary-color);
        }

        :host(:not([expanded])) [part='content'] {
          opacity: 0;
          visibility: hidden;
        }

        :host([floating]:not([expanded])) [part='content'] {
          width: 0;
          height: 0;
        }

        :host(:not([expanded])) [part='content'][style*='width'] {
          width: 0 !important;
        }

        :host([floating]) {
          position: fixed;
          min-width: 0;
          min-height: 0;
          z-index: calc(var(--z-index-floating-panel) + var(--z-index-focus, 0));
          top: clamp(0px, var(--top), calc(100vh - var(--section-height, var(--header-height)) * 0.5));
          left: clamp(calc(var(--section-width) * -0.5), var(--left), calc(100vw - var(--section-width) * 0.5));
          bottom: clamp(
            calc(var(--section-height, var(--header-height)) * -0.5),
            var(--bottom),
            calc(100vh - var(--section-height, var(--header-height)) * 0.5)
          );
          right: clamp(calc(var(--section-width) * -0.5), var(--right), calc(100vw - var(--section-width) * 0.5));
          width: var(--section-width);
          overflow: visible;
        }
        :host([floating]) [part='container'] {
          background: var(--background-color);
          border: var(--floating-border-width) solid var(--surface-border-color);
          -webkit-backdrop-filter: var(--surface-backdrop-filter);
          backdrop-filter: var(--surface-backdrop-filter);
          border-radius: var(--vaadin-radius-m);
          margin: auto;
          box-shadow: var(--surface-box-shadow-2);
        }
        [part='container'] {
          overflow: hidden;
        }
        :host([floating][expanded]) [part='container'] {
          height: calc(100% - var(--floating-offset-resize-threshold) / 2);
          width: calc(100% - var(--floating-offset-resize-threshold) / 2);
        }

        :host([floating]:not([expanded])) {
          width: unset;
        }

        :host([floating]) .drag-handle {
          cursor: var(--resize-cursor, move);
        }

        :host([floating][expanded]) [part='content'] {
          min-width: var(--min-width);
          min-height: 0;
          width: var(--content-width);
        }

        /* :hover for Firefox, :active for others */

        :host([floating][expanded]) [part='content']:is(:hover, :active) {
          transition: none;
        }

        ::slotted(*) {
          box-sizing: border-box;
          display: block;
          /* padding: var(--space-150); */
          width: 100%;
        }

        /*workaround for outline to have a explicit height while floating by default. 
          may be removed after https://github.com/vaadin/web-components/issues/7620 is solved
        */
        :host([floating][expanded][paneltag='copilot-outline-panel']) {
          --grid-default-height: 400px;
        }

        :host([dragging]) {
          opacity: 0.4;
        }

        :host([dragging]) [part='content'] {
          pointer-events: none;
        }

        :host([hiding-while-drag-and-drop]) {
          display: none;
        }

        // dragging in drawer

        :host(:not([floating])) .drag-handle {
          cursor: grab;
        }

        :host(:not([floating])[dragging]) .drag-handle {
          cursor: grabbing;
        }
      `
    ];
  }
  connectedCallback() {
    super.connectedCallback(), this.setAttribute("role", "region"), this.reaction(
      () => p.getAttentionRequiredPanelConfiguration(),
      () => {
        const e = p.getAttentionRequiredPanelConfiguration();
        this.toggleAttribute(E, e?.tag === this.panelTag && e?.floating);
      }
    ), this.addEventListener("mouseenter", this.sectionPanelMouseEnterListener), this.reaction(
      () => l.operationInProgress,
      () => {
        requestAnimationFrame(() => {
          this.toggleAttribute(
            "hiding-while-drag-and-drop",
            l.operationInProgress === xe.DragAndDrop && this.panelInfo?.floating && !this.panelInfo.showWhileDragging && !this.hasDropTarget()
          );
        });
      }
    ), this.reaction(
      () => p.floatingPanelsZIndexOrder,
      () => {
        this.style.setProperty("--z-index-focus", `${p.getFloatingPanelZIndex(this.panelTag)}`);
      },
      { fireImmediately: !0 }
    ), this.reaction(
      () => p.getPanelByTag(this.panelTag)?.floatingPosition,
      () => {
        !this.floatingResizingStarted && !l.sectionPanelDragging && this.setCssSizePositionProperties();
      }
    ), this.addEventListener("transitionend", this.transitionEndEventListener), this.addEventListener("mousemove", this.floatingResizeHandlerMouseMoveListener), this.addEventListener("mousedown", this.floatingResizingMouseDownListener), this.addEventListener("mouseleave", this.floatingResizingMouseLeaveListener), document.addEventListener("mousemove", this.floatingResizingMouseMoveListener), document.addEventListener("mouseup", this.floatingResizingMouseUpListener);
  }
  disconnectedCallback() {
    super.disconnectedCallback(), this.removeEventListener("mouseenter", this.sectionPanelMouseEnterListener), this.removeEventListener("mousemove", this.floatingResizeHandlerMouseMoveListener), this.removeEventListener("mousedown", this.floatingResizingMouseDownListener), document.removeEventListener("mousemove", this.floatingResizingMouseMoveListener), document.removeEventListener("mouseup", this.floatingResizingMouseUpListener);
  }
  setResizePosition(e, t, i) {
    const n = this.rectangleBeforeResizing, o = 0, s = window.innerWidth, a = 0, h = window.innerHeight, w = Math.max(o, Math.min(s, t)), k = Math.max(a, Math.min(h, i));
    if (e === "left")
      this.setFloatingResizeDirectionProps(
        "left",
        w,
        n.left - w + n.width
      );
    else if (e === "right")
      this.setFloatingResizeDirectionProps(
        "right",
        w,
        w - n.right + n.width
      );
    else if (e === "top") {
      const L = n.top - k + n.height;
      this.setFloatingResizeDirectionProps("top", k, void 0, L);
    } else if (e === "bottom") {
      const L = k - n.bottom + n.height;
      this.setFloatingResizeDirectionProps("bottom", k, void 0, L);
    }
  }
  willUpdate(e) {
    super.willUpdate(e), e.has("panelTag") && (this.panelInfo = p.getPanelByTag(this.panelTag), this.setAttribute("aria-labelledby", this.panelInfo.tag.concat("-title"))), this.toggleAttribute("floating", this.panelInfo?.floating);
  }
  updated(e) {
    super.updated(e), this.setCssSizePositionProperties(), requestAnimationFrame(() => {
      if (this.panelInfo !== void 0 && this.panelInfo.floating && this.panelInfo.floatingPosition?.top != null && (this.panelInfo?.height === void 0 || this.panelInfo?.width === void 0)) {
        let t = this.panelInfo?.height, i = this.panelInfo?.width, n = !1;
        const o = this.panelInfo.floatingPosition;
        if (this.offsetWidth !== void 0 && this.offsetWidth !== 0 && this.panelInfo.width === void 0 && (n = !0, i = this.offsetWidth), this.offsetHeight !== void 0 && this.offsetHeight !== 0 && this.panelInfo.height === void 0) {
          n = !0, t = this.offsetHeight;
          const s = window.innerHeight;
          let a = this.panelInfo.floatingPosition.top;
          t > s && (t = s);
          const h = Math.floor(s / 3);
          t < h ? (t = h, a = h) : t > 2 * h ? a -= t - (s - this.panelInfo.floatingPosition.top) : a = h, o.top = a;
        }
        n && (p.updatePanel(this.panelInfo?.tag, {
          height: t,
          width: i,
          floatingPosition: o
        }), this.setCssSizePositionProperties());
      }
    });
  }
  firstUpdated(e) {
    super.firstUpdated(e), document.addEventListener("mouseup", this.documentMouseUpEventListener), this.headerDraggableArea.addEventListener("mousedown", this.panelHeaderMouseDownEventListener), this.headerDraggableArea.addEventListener("mouseup", this.panelHeaderMouseUpEventListener), this.toggleAttribute("expanded", this.panelInfo?.expanded), this.toggleAttribute("individual", this.panelInfo?.individual ?? !1), yt(this), this.setCssSizePositionProperties(), this.contentArea.addEventListener("mousedown", this.contentAreaMouseDownListener);
  }
  render() {
    return this.panelInfo ? r`
      <div part="container">
        <div
          class="drag-handle flex gap-1 pe-1 ${this.panelInfo.expandable !== !1 ? "ps-1" : "ps-4"} py-1 select-none"
          part="header">
          ${this.panelInfo.expandable !== !1 ? r` <vaadin-button
                @mousedown="${(e) => e.stopPropagation()}"
                @click="${(e) => this.toggleExpand(e)}"
                aria-controls="content"
                aria-expanded="${this.panelInfo.expanded}"
                aria-label="${this.panelInfo.expanded ? "Hide" : "Show"} ${this.panelInfo.header}"
                theme="icon tertiary"
                part="toggle-button">
                <vaadin-icon
                  class="transition ${this.panelInfo.expanded ? "rotate-90" : ""}"
                  .svg="${d.chevronRight}"></vaadin-icon>
                <vaadin-tooltip
                  slot="tooltip"
                  text="${this.panelInfo.expanded ? "Hide" : "Show"} ${this.panelInfo.header}"></vaadin-tooltip>
              </vaadin-button>` : g}
          <h2 class="flex flex-1 my-0 overflow-hidden" id="${this.panelInfo.tag}-title" part="title">
            <vaadin-button
              class="cursor-inherit font-bold justify-start max-w-full overflow-hidden px-0 text-xs uppercase"
              part="title-button"
              theme="tertiary"
              @dblclick="${(e) => {
      this.toggleExpand(e), this.startDraggingDebounce.clear();
    }}">
              ${p.getPanelHeader(this.panelInfo)}
            </vaadin-button>
          </h2>
          ${this.panelInfo.expanded ? r`<div class="contents" @mousedown="${(e) => e.stopPropagation()}">
                ${this.renderActions()}
              </div>` : g}
          ${this.renderHelpButton()} ${this.renderPopupButton()}
        </div>
        <div part="content" id="content">
          <slot name="content"></slot>
        </div>
      </div>
    ` : g;
  }
  getPopupButtonIcon() {
    return this.panelInfo ? this.panelInfo.panel === void 0 ? d.close : this.panelInfo.floating ? this.panelInfo.panel === "bottom" ? d.dockToBottom : this.panelInfo.panel === "left" ? d.thumbnailBar : this.panelInfo.panel === "right" ? d.dockToRight : g : d.adsGroup : g;
  }
  renderHelpButton() {
    return this.panelInfo?.helpUrl ? r` <button
      @click="${() => window.open(this.panelInfo.helpUrl, "_blank")}"
      @mousedown="${(e) => e.stopPropagation()}"
      aria-label="More information about ${this.panelInfo.header}"
      class="icon"
      title="More information about ${this.panelInfo.header}">
      <span>${d.help}</span>
    </button>` : g;
  }
  renderActions() {
    if (!this.panelInfo?.actionsTag)
      return g;
    const e = this.panelInfo.actionsTag;
    return xt(`<${e}></${e}>`);
  }
  changeDockingPanel(e) {
    const t = e.detail.value.panel;
    if (this.panelInfo?.panel !== t) {
      const i = p.panels.filter((n) => n.panel === t).map((n) => n.panelOrder).sort((n, o) => o - n)[0];
      Re(this), p.updatePanel(this.panelInfo.tag, { panel: t, panelOrder: i + 1 });
    }
    this.panelInfo.floating && this.changePanelFloating(e);
  }
  getResizeDirections() {
    const e = this.getAttribute(P);
    return e ? e.split(" ") : [];
  }
  hasDropTarget() {
    const e = this.shadowRoot?.querySelector("slot")?.assignedElements();
    if (!e)
      return !1;
    for (const t of e) {
      const i = Pt(
        t.shadowRoot ?? t,
        "copilot-image-upload"
      );
      if (i && window.getComputedStyle(i).display !== "none")
        return !0;
    }
    return !1;
  }
};
J([
  y()
], z.prototype, "panelTag", 2);
J([
  O(".drag-handle")
], z.prototype, "headerDraggableArea", 2);
J([
  O("#content")
], z.prototype, "contentArea", 2);
J([
  O('[part="container"]')
], z.prototype, "container", 2);
J([
  x()
], z.prototype, "dockingItems", 2);
z = J([
  b("copilot-section-panel-wrapper")
], z);
const ve = window.Vaadin.copilot.customComponentHandler;
if (!ve)
  throw new Error("Tried to access custom component handler before it was initialized.");
function hi(e) {
  l.setOperationWaitsHmrUpdate(e, 3e4);
}
u.on("undoRedo", (e) => {
  const i = { files: ui(e), uiId: It() }, n = e.detail.undo ? "copilot-plugin-undo" : "copilot-plugin-redo", o = e.detail.undo ? "undo" : "redo";
  Ke(o), hi(xe.RedoUndo), ae(n, i, (s) => {
    if (!s.data.performed) {
      if (s.data.error && s.data.error.message) {
        N({
          type: R.ERROR,
          message: s.data.error.message
        }), u.emit("vite-after-update", {});
        return;
      }
      N({
        type: R.INFORMATION,
        message: `Nothing to ${o}`
      }), u.emit("vite-after-update", {});
    }
  });
});
function ui(e) {
  if (e.detail.files)
    return e.detail.files;
  const t = ve.getActiveDrillDownContext();
  if (t) {
    const i = ve.getCustomComponentInfo(t);
    if (i)
      return new Array(i.customComponentFilePath);
  }
  return At();
}
var gi = Object.getOwnPropertyDescriptor, fi = (e, t, i, n) => {
  for (var o = n > 1 ? void 0 : n ? gi(t, i) : t, s = e.length - 1, a; s >= 0; s--)
    (a = e[s]) && (o = a(o) || o);
  return o;
};
let je = class extends I {
  static get styles() {
    return [
      m(se),
      m(Pe),
      m($t),
      m(Y),
      _`
        :host {
          --lumo-secondary-text-color: var(--dev-tools-text-color);
          --lumo-contrast-80pct: var(--dev-tools-text-color-emphasis);
          --lumo-contrast-60pct: var(--dev-tools-text-color-secondary);
          --lumo-font-size-m: 14px;

          position: fixed;
          bottom: 2.5rem;
          right: 0rem;
          visibility: visible; /* Always show, even if copilot is off */
          user-select: none;
          z-index: var(--copilot-notifications-container-z-index);

          --dev-tools-text-color: rgba(255, 255, 255, 0.8);

          --dev-tools-text-color-secondary: rgba(255, 255, 255, 0.65);
          --dev-tools-text-color-emphasis: rgba(255, 255, 255, 0.95);
          --dev-tools-text-color-active: rgba(255, 255, 255, 1);

          --dev-tools-background-color-inactive: rgba(45, 45, 45, 0.25);
          --dev-tools-background-color-active: rgba(45, 45, 45, 0.98);
          --dev-tools-background-color-active-blurred: rgba(45, 45, 45, 0.85);

          --dev-tools-border-radius: 0.5rem;
          --dev-tools-box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.05), 0 4px 12px -2px rgba(0, 0, 0, 0.4);

          --dev-tools-blue-hsl: 206, 100%, 70%;
          --dev-tools-blue-color: hsl(var(--dev-tools-blue-hsl));
          --dev-tools-green-hsl: 145, 80%, 42%;
          --dev-tools-green-color: hsl(var(--dev-tools-green-hsl));
          --dev-tools-grey-hsl: 0, 0%, 50%;
          --dev-tools-grey-color: hsl(var(--dev-tools-grey-hsl));
          --dev-tools-yellow-hsl: 38, 98%, 64%;
          --dev-tools-yellow-color: hsl(var(--dev-tools-yellow-hsl));
          --dev-tools-red-hsl: 355, 100%, 68%;
          --dev-tools-red-color: hsl(var(--dev-tools-red-hsl));

          /* Needs to be in ms, used in JavaScript as well */
          --dev-tools-transition-duration: 180ms;

          /* Copilot go to source file link styling */
          --source-file-link-color: var(--dev-tools-text-color-secondary);
          --source-file-link-text-decoration: underline;
          --source-file-link-font-weight: 500;
          --source-file-link-button-color: white;
        }

        .notification-tray {
          display: flex;
          flex-direction: column-reverse;
          align-items: flex-end;
          margin: 0.5rem;
          flex: none;
        }

        @supports (backdrop-filter: blur(1px)) {
          .notification-tray div.message {
            backdrop-filter: blur(8px);
          }

          .notification-tray div.message {
            background-color: var(--dev-tools-background-color-active-blurred);
          }
        }

        .notification-tray .message {
          animation: slideIn var(--dev-tools-transition-duration);
          background-color: var(--dev-tools-background-color-active);
          border-radius: var(--dev-tools-border-radius);
          box-shadow: var(--dev-tools-box-shadow);
          box-sizing: border-box;
          color: var(--dev-tools-text-color);
          flex-direction: row;
          gap: var(--space-100);
          justify-content: space-between;
          margin-top: var(--space-100);
          max-width: 40rem;
          padding-block: var(--space-100);
          pointer-events: auto;
          transform-origin: bottom right;
          transition: var(--dev-tools-transition-duration);
        }

        .notification-tray .message.animate-out {
          animation: slideOut forwards var(--dev-tools-transition-duration);
        }

        .notification-tray .message .message-details {
          word-break: break-all;
        }

        .message.information {
          --dev-tools-notification-color: var(--dev-tools-blue-color);
        }

        .message.warning {
          --dev-tools-notification-color: var(--dev-tools-yellow-color);
        }

        .message.error {
          --dev-tools-notification-color: var(--dev-tools-red-color);
        }

        .message {
          background-clip: padding-box;
          display: flex;
          padding-block: var(--space-75);
          padding-inline: var(--space-450) var(--space-100);
        }

        .message.log {
          padding-left: 0.75rem;
        }

        .message-content {
          align-self: center;
          flex: 1 1 auto;
          max-width: 100%;
          min-width: 0;
          user-select: text;
          -moz-user-select: text;
          -webkit-user-select: text;
        }

        .message .message-details {
          align-items: start;
          color: rgba(255, 255, 255, 0.7);
          display: flex;
          flex-direction: column;
        }

        .message .message-details[hidden] {
          display: none;
        }

        .message .message-details p {
          display: inline;
          margin: 0;
          margin-right: 0.375em;
          word-break: break-word;
        }

        .message .message-details vaadin-details {
          margin: 0;
          width: 100%;
        }

        .message .message-details vaadin-details-summary {
          font-size: var(--copilot-font-size-xs);
          font-weight: var(--copilot-font-weight-medium);
          line-height: var(--copilot-line-height-sm);
        }

        .message .persist::before {
        }

        .message .persist:hover::before {
        }

        .message .persist.on::before {
        }

        .message.log {
          color: var(--dev-tools-text-color-secondary);
        }

        .message-heading {
          color: white;
        }

        .message-heading::before {
          height: var(--icon-size-m);
          margin-inline-start: calc((var(--space-400) / -1) + ((var(--space-400) - var(--icon-size-m)) / 2));
          position: absolute;
          width: var(--icon-size-m);
        }

        .message.information .message-heading::before {
          content: url("data:image/svg+xml,%3Csvg width='18' height='18' viewBox='0 0 24 24' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M12 16V12M12 8H12.01M22 12C22 17.5228 17.5228 22 12 22C6.47715 22 2 17.5228 2 12C2 6.47715 6.47715 2 12 2C17.5228 2 22 6.47715 22 12Z' stroke='%2395C6FF' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E");
        }

        .message.warning .message-heading::before,
        .message.error .message-heading::before {
          content: url("data:image/svg+xml,%3Csvg width='18' height='18' viewBox='0 0 24 24' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M12 8V12M12 16H12.01M22 12C22 17.5228 17.5228 22 12 22C6.47715 22 2 17.5228 2 12C2 6.47715 6.47715 2 12 2C17.5228 2 22 6.47715 22 12Z' stroke='%23ff707a' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'/%3E%3C/svg%3E");
        }

        @keyframes slideIn {
          from {
            transform: translateX(100%);
            opacity: 0;
          }
          to {
            transform: translateX(0%);
            opacity: 1;
          }
        }

        @keyframes slideOut {
          from {
            transform: translateX(0%);
            opacity: 1;
          }
          to {
            transform: translateX(100%);
            opacity: 0;
          }
        }

        @keyframes fade-in {
          0% {
            opacity: 0;
          }
        }

        @keyframes bounce {
          0% {
            transform: scale(0.8);
          }
          50% {
            transform: scale(1.5);
            background-color: hsla(var(--dev-tools-red-hsl), 1);
          }
          100% {
            transform: scale(1);
          }
        }
      `
    ];
  }
  render() {
    return r`<div class="notification-tray">
      ${l.notifications.map((e) => this.renderNotification(e))}
    </div>`;
  }
  renderNotification(e) {
    return r`
      <div
        class="message ${e.type} ${e.animatingOut ? "animate-out" : ""} ${e.details || e.link ? "has-details" : ""}"
        data-testid="message">
        <div class="message-content">
          <h2 class="message-heading font-bold m-0 relative text-1">${e.message}</h2>
          <div class="message-details" ?hidden="${!e.details && !e.link}">
            ${Ct(e.details)}
            ${e.link ? r`<a class="ahreflike" href="${e.link}" target="_blank">Learn more</a>` : ""}
          </div>
          <!-- TODO: This needs to be an actual checkbox -->
          ${e.dismissId ? r` <hr class="border-b border-white/10 border-e-0 border-s-0 border-t-0 mb-25 mt-100" />
                <div
                  class="flex gap-75 items-center py-75 relative hover:text-white"
                  @click=${() => {
      this.toggleDontShowAgain(e);
    }}>
                  ${r`${e.dontShowAgain ? d.checkSquare : d.square}`}
                  ${vi(e)}
                </div>` : ""}
        </div>
        <button
          aria-label="Close"
          class="icon -me-50 -my-50 text-inherit"
          id="dismiss"
          title="Close"
          @click=${(t) => {
      Ze(e), t.stopPropagation();
    }}>
          ${d.x}
        </button>
      </div>
    `;
  }
  toggleDontShowAgain(e) {
    e.dontShowAgain = !e.dontShowAgain, this.requestUpdate();
  }
};
je = fi([
  b("copilot-notifications-container")
], je);
function vi(e) {
  return e.dontShowAgainMessage ? e.dontShowAgainMessage : "Do not show this again";
}
N({
  type: R.WARNING,
  message: "Development Mode",
  details: "This application is running in development mode.",
  dismissId: "devmode"
});
const me = j(async () => {
  await kt();
}, 100);
u.on("vite-after-update", () => {
  l.active && me();
});
function nt() {
  l.active && (me.clear(), me(), St());
}
if (window.__REACT_DEVTOOLS_GLOBAL_HOOK__) {
  const e = window.__REACT_DEVTOOLS_GLOBAL_HOOK__, t = e.onCommitFiberRoot;
  e.onCommitFiberRoot = (i, n, o, s) => (nt(), t(i, n, o, s));
}
const Ne = window?.Vaadin?.connectionState?.stateChangeListeners;
Ne ? Ne.add((e, t) => {
  e === "loading" && t === "connected" && l.active && nt();
}) : console.warn("Unable to add listener for connection state changes");
u.on("copilot-plugin-state", (e) => {
  l.setIdePluginState(e.detail), e.preventDefault();
});
u.on("copilot-early-project-state", (e) => {
  f.setSpringSecurityEnabled(e.detail.springSecurityEnabled), f.setSpringJpaDataEnabled(e.detail.springJpaDataEnabled), f.setSpringJpaDatasourceInitialization(e.detail.springJpaDatasourceInitialization), f.setSupportsHilla(e.detail.supportsHilla), f.setSpringApplication(e.detail.springApplication), f.setUrlPrefix(e.detail.urlPrefix), f.setServerVersions(e.detail.serverVersions), f.setJdkInfo(e.detail.jdkInfo), Q() === "success" && Ke("hotswap-active", { value: qe() }), e.preventDefault();
});
u.on("copilot-ide-notification", (e) => {
  N({
    type: R[e.detail.type],
    message: e.detail.message,
    dismissId: e.detail.dismissId
  }), e.preventDefault();
});
/**
 * @license
 * Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
 * This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
 * The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
 * The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
 * Code distributed by Google as part of the polymer project is also
 * subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
 */
let Be = 0, ot = 0;
const U = [];
let be = !1;
function mi() {
  be = !1;
  const e = U.length;
  for (let t = 0; t < e; t++) {
    const i = U[t];
    if (i)
      try {
        i();
      } catch (n) {
        setTimeout(() => {
          throw n;
        });
      }
  }
  U.splice(0, e), ot += e;
}
const bi = {
  /**
   * Enqueues a function called at microtask timing.
   *
   * @memberof microTask
   * @param {!Function=} callback Callback to run
   * @return {number} Handle used for canceling task
   */
  run(e) {
    be || (be = !0, queueMicrotask(() => mi())), U.push(e);
    const t = Be;
    return Be += 1, t;
  },
  /**
   * Cancels a previously enqueued `microTask` callback.
   *
   * @memberof microTask
   * @param {number} handle Handle returned from `run` of callback to cancel
   * @return {void}
   */
  cancel(e) {
    const t = e - ot;
    if (t >= 0) {
      if (!U[t])
        throw new Error(`invalid async handle: ${e}`);
      U[t] = null;
    }
  }
};
/**
@license
Copyright (c) 2017 The Polymer Project Authors. All rights reserved.
This code may only be used under the BSD style license found at http://polymer.github.io/LICENSE.txt
The complete set of authors may be found at http://polymer.github.io/AUTHORS.txt
The complete set of contributors may be found at http://polymer.github.io/CONTRIBUTORS.txt
Code distributed by Google as part of the polymer project is also
subject to an additional IP rights grant found at http://polymer.github.io/PATENTS.txt
*/
const Fe = /* @__PURE__ */ new Set();
class ie {
  /**
   * Creates a debouncer if no debouncer is passed as a parameter
   * or it cancels an active debouncer otherwise. The following
   * example shows how a debouncer can be called multiple times within a
   * microtask and "debounced" such that the provided callback function is
   * called once. Add this method to a custom element:
   *
   * ```js
   * import {microTask} from '@vaadin/component-base/src/async.js';
   * import {Debouncer} from '@vaadin/component-base/src/debounce.js';
   * // ...
   *
   * _debounceWork() {
   *   this._debounceJob = Debouncer.debounce(this._debounceJob,
   *       microTask, () => this._doWork());
   * }
   * ```
   *
   * If the `_debounceWork` method is called multiple times within the same
   * microtask, the `_doWork` function will be called only once at the next
   * microtask checkpoint.
   *
   * Note: In testing it is often convenient to avoid asynchrony. To accomplish
   * this with a debouncer, you can use `enqueueDebouncer` and
   * `flush`. For example, extend the above example by adding
   * `enqueueDebouncer(this._debounceJob)` at the end of the
   * `_debounceWork` method. Then in a test, call `flush` to ensure
   * the debouncer has completed.
   *
   * @param {Debouncer?} debouncer Debouncer object.
   * @param {!AsyncInterface} asyncModule Object with Async interface
   * @param {function()} callback Callback to run.
   * @return {!Debouncer} Returns a debouncer object.
   */
  static debounce(t, i, n) {
    return t instanceof ie ? t._cancelAsync() : t = new ie(), t.setConfig(i, n), t;
  }
  constructor() {
    this._asyncModule = null, this._callback = null, this._timer = null;
  }
  /**
   * Sets the scheduler; that is, a module with the Async interface,
   * a callback and optional arguments to be passed to the run function
   * from the async module.
   *
   * @param {!AsyncInterface} asyncModule Object with Async interface.
   * @param {function()} callback Callback to run.
   * @return {void}
   */
  setConfig(t, i) {
    this._asyncModule = t, this._callback = i, this._timer = this._asyncModule.run(() => {
      this._timer = null, Fe.delete(this), this._callback();
    });
  }
  /**
   * Cancels an active debouncer and returns a reference to itself.
   *
   * @return {void}
   */
  cancel() {
    this.isActive() && (this._cancelAsync(), Fe.delete(this));
  }
  /**
   * Cancels a debouncer's async callback.
   *
   * @return {void}
   */
  _cancelAsync() {
    this.isActive() && (this._asyncModule.cancel(
      /** @type {number} */
      this._timer
    ), this._timer = null);
  }
  /**
   * Flushes an active debouncer and returns a reference to itself.
   *
   * @return {void}
   */
  flush() {
    this.isActive() && (this.cancel(), this._callback());
  }
  /**
   * Returns true if the debouncer is active.
   *
   * @return {boolean} True if active.
   */
  isActive() {
    return this._timer != null;
  }
}
/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
const W = (e, t) => {
  const i = e._$AN;
  if (i === void 0) return !1;
  for (const n of i) n._$AO?.(t, !1), W(n, t);
  return !0;
}, ne = (e) => {
  let t, i;
  do {
    if ((t = e._$AM) === void 0) break;
    i = t._$AN, i.delete(e), e = t;
  } while (i?.size === 0);
}, st = (e) => {
  for (let t; t = e._$AM; e = t) {
    let i = t._$AN;
    if (i === void 0) t._$AN = i = /* @__PURE__ */ new Set();
    else if (i.has(e)) break;
    i.add(e), xi(t);
  }
};
function wi(e) {
  this._$AN !== void 0 ? (ne(this), this._$AM = e, st(this)) : this._$AM = e;
}
function yi(e, t = !1, i = 0) {
  const n = this._$AH, o = this._$AN;
  if (o !== void 0 && o.size !== 0) if (t) if (Array.isArray(n)) for (let s = i; s < n.length; s++) W(n[s], !1), ne(n[s]);
  else n != null && (W(n, !1), ne(n));
  else W(this, e);
}
const xi = (e) => {
  e.type == Qe.CHILD && (e._$AP ??= yi, e._$AQ ??= wi);
};
class Pi extends Et {
  constructor() {
    super(...arguments), this._$AN = void 0;
  }
  _$AT(t, i, n) {
    super._$AT(t, i, n), st(this), this.isConnected = t._$AU;
  }
  _$AO(t, i = !0) {
    t !== this.isConnected && (this.isConnected = t, t ? this.reconnected?.() : this.disconnected?.()), i && (W(this, t), ne(this));
  }
  setValue(t) {
    if (Rt(this._$Ct)) this._$Ct._$AI(t, this);
    else {
      const i = [...this._$Ct._$AH];
      i[this._$Ci] = t, this._$Ct._$AI(i, this, 0);
    }
  }
  disconnected() {
  }
  reconnected() {
  }
}
/**
 * @license
 * Copyright (c) 2016 - 2025 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */
const Ve = Symbol("valueNotInitialized");
class Ii extends Pi {
  constructor(t) {
    if (super(t), t.type !== Qe.ELEMENT)
      throw new Error(`\`${this.constructor.name}\` must be bound to an element.`);
    this.previousValue = Ve;
  }
  /** @override */
  render(t, i) {
    return g;
  }
  /** @override */
  update(t, [i, n]) {
    return this.hasChanged(n) ? (this.host = t.options && t.options.host, this.element = t.element, this.renderer = i, this.previousValue === Ve ? this.addRenderer() : this.runRenderer(), this.previousValue = Array.isArray(n) ? [...n] : n, g) : g;
  }
  /** @override */
  reconnected() {
    this.addRenderer();
  }
  /** @override */
  disconnected() {
    this.removeRenderer();
  }
  /** @abstract */
  addRenderer() {
    throw new Error("The `addRenderer` method must be implemented.");
  }
  /** @abstract */
  runRenderer() {
    throw new Error("The `runRenderer` method must be implemented.");
  }
  /** @abstract */
  removeRenderer() {
    throw new Error("The `removeRenderer` method must be implemented.");
  }
  /** @protected */
  renderRenderer(t, ...i) {
    const n = this.renderer.call(this.host, ...i);
    Dt(n, t, { host: this.host });
  }
  /** @protected */
  hasChanged(t) {
    return Array.isArray(t) ? !Array.isArray(this.previousValue) || this.previousValue.length !== t.length ? !0 : t.some((i, n) => i !== this.previousValue[n]) : this.previousValue !== t;
  }
}
/**
 * @license
 * Copyright (c) 2017 - 2025 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */
const ue = Symbol("contentUpdateDebouncer");
class ke extends Ii {
  /**
   * A property to that the renderer callback will be assigned.
   *
   * @abstract
   */
  get rendererProperty() {
    throw new Error("The `rendererProperty` getter must be implemented.");
  }
  /**
   * Adds the renderer callback to the dialog.
   */
  addRenderer() {
    this.element[this.rendererProperty] = (t, i) => {
      this.renderRenderer(t, i);
    };
  }
  /**
   * Runs the renderer callback on the dialog.
   */
  runRenderer() {
    this.element[ue] = ie.debounce(
      this.element[ue],
      bi,
      () => {
        this.element.requestContentUpdate();
      }
    );
  }
  /**
   * Removes the renderer callback from the dialog.
   */
  removeRenderer() {
    this.element[this.rendererProperty] = null, delete this.element[ue];
  }
}
class Ai extends ke {
  get rendererProperty() {
    return "renderer";
  }
}
class $i extends ke {
  get rendererProperty() {
    return "headerRenderer";
  }
}
class Ci extends ke {
  get rendererProperty() {
    return "footerRenderer";
  }
}
const at = Ae(Ai), rt = Ae($i), lt = Ae(Ci);
var ki = Object.defineProperty, Si = Object.getOwnPropertyDescriptor, dt = (e, t, i, n) => {
  for (var o = n > 1 ? void 0 : n ? Si(t, i) : t, s = e.length - 1, a; s >= 0; s--)
    (a = e[s]) && (o = (n ? a(t, i, o) : a(o)) || o);
  return n && o && ki(t, i, o), o;
};
let we = class extends X {
  constructor() {
    super(...arguments), this.rememberChoice = !1, this.opened = !1, this.handleESC = (e) => {
      !l.active || !this.opened || (e.key === "Escape" && this.sendEvent("cancel"), e.preventDefault(), e.stopPropagation());
    };
  }
  createRenderRoot() {
    return this;
  }
  connectedCallback() {
    super.connectedCallback(), this.addESCListener();
  }
  disconnectedCallback() {
    super.disconnectedCallback(), this.removeESCListener();
  }
  render() {
    return r` <vaadin-dialog
      id="ai-dialog"
      no-close-on-outside-click
      class="ai-dialog"
      ?opened=${this.opened}
      ${rt(
      () => r`
          <h2>This Operation Uses AI</h2>
          <vaadin-icon .svg="${d.sparkles}"></vaadin-icon>
        `
    )}
      ${at(
      () => r`
          <p>AI is a third-party service that will receive some of your project code as context for the operation.</p>
          <label>
            <input
              id="ai-dialog-checkbox"
              type="checkbox"
              @change=${(e) => {
        this.rememberChoice = e.target.checked;
      }} />Don’t ask again
          </label>
        `
    )}
      ${lt(
      () => r`
          <button @click=${() => this.sendEvent("cancel")}>Cancel</button>
          <button class="primary" @click=${() => this.sendEvent("ok")}>OK</button>
        `
    )}></vaadin-dialog>`;
  }
  sendEvent(e) {
    this.dispatchEvent(
      new CustomEvent("ai-usage-response", {
        detail: { response: e, rememberChoice: this.rememberChoice }
      })
    );
  }
  addESCListener() {
    document.addEventListener("keydown", this.handleESC, { capture: !0 });
  }
  removeESCListener() {
    document.removeEventListener("keydown", this.handleESC, { capture: !0 });
  }
};
dt([
  y()
], we.prototype, "opened", 2);
we = dt([
  b("copilot-ai-usage-confirmation-dialog")
], we);
var Ei = Object.defineProperty, Ri = Object.getOwnPropertyDescriptor, T = (e, t, i, n) => {
  for (var o = n > 1 ? void 0 : n ? Ri(t, i) : t, s = e.length - 1, a; s >= 0; s--)
    (a = e[s]) && (o = (n ? a(t, i, o) : a(o)) || o);
  return n && o && Ei(t, i, o), o;
};
const Je = {
  info: "UI state info",
  stacktrace: "Exception details",
  versions: "Vaadin, Java, OS, etc.."
};
let D = class extends X {
  constructor() {
    super(...arguments), this.exceptionReport = void 0, this.dialogOpened = !1, this.visibleItemIndex = 0, this.versions = void 0, this.selectedItems = [], this.eventListener = (e) => {
      this.exceptionReport = e.detail, this.selectedItems = this.exceptionReport.items.map((t, i) => i), this.visibleItemIndex = 0, this.searchInputValue = void 0, this.dialogOpened = this.exceptionReport !== void 0;
    };
  }
  connectedCallback() {
    super.connectedCallback(), u.on("submit-exception-report-clicked", this.eventListener);
  }
  createRenderRoot() {
    return this;
  }
  disconnectedCallback() {
    super.disconnectedCallback(), u.off("submit-exception-report-clicked", this.eventListener);
  }
  close() {
    this.dialogOpened = !1;
  }
  clear() {
    this.exceptionReport = void 0;
  }
  render() {
    let e = "";
    return this.exceptionReport && this.exceptionReport.items.length > 0 && (e = this.exceptionReport.items[this.visibleItemIndex].content), r` <vaadin-dialog
      id="report-exception-dialog"
      no-close-on-outside-click
      draggable
      ?opened=${this.dialogOpened}
      @closed="${() => {
      this.clear();
    }}"
      @opened-changed="${(t) => {
      t.detail.value || this.close();
    }}"
      ${rt(
      () => r`
          <div
            class="draggable"
            style="display: flex; justify-content: space-between; align-items: center; width: 100%">
            <h2>Send report</h2>
            <vaadin-button theme="tertiary" @click="${this.close}">
              <vaadin-icon icon="lumo:cross"></vaadin-icon>
            </vaadin-button>
          </div>
        `
    )}
      ${at(
      () => r`
          <div class="description-container">
            <vaadin-text-area
              @input=${(t) => {
        this.searchInputValue = t.target.value;
      }}
              label="Description of the Bug"
              placeholder="A short, concise description of the bug and why you consider it a bug."></vaadin-text-area>
          </div>
          <div class="list-preview-container">
            <div class="left-menu">
              <div class="section-title">Include in Report</div>
              <vaadin-list-box
                single
                selected="${this.visibleItemIndex}"
                @selected-changed="${(t) => {
        this.visibleItemIndex = t.detail.value;
      }}">
                ${this.exceptionReport?.items.map(
        (t, i) => r` <vaadin-item>
                      <input
                        type="checkbox"
                        .checked="${this.selectedItems.indexOf(i) !== -1}"
                        @change="${(n) => {
          const o = n.target, s = [...this.selectedItems];
          if (o.checked)
            s.push(i), this.selectedItems = [...this.selectedItems];
          else {
            const a = this.selectedItems.indexOf(i);
            s.splice(a, 1);
          }
          this.selectedItems = s;
        }}" />
                      <div class="item-content">
                        <span class="item-name"> ${t.name} </span>
                        <span class="item-description">${this.renderItemDescription(t)}</span>
                      </div>
                    </vaadin-item>`
      )}
              </vaadin-list-box>
            </div>
            <div class="right-menu">
              <div class="section-title">Preview: ${this.exceptionReport?.items[this.visibleItemIndex].name}</div>
              <code class="codeblock">${e}</code>
            </div>
          </div>
        `,
      [this.exceptionReport, this.visibleItemIndex, this.selectedItems]
    )}
      ${lt(
      () => r`
          <button
            id="cancel"
            @click=${() => {
        this.close();
      }}>
            Cancel
          </button>

          <button
            id="submit"
            class="primary"
            @click=${() => {
        this.submitErrorToGithub(), this.close();
      }}>
            Submit in GitHub
            <vaadin-tooltip
              for="submit"
              text="${this.bodyLengthExceeds() ? "The error report will be copied to clipboard and blank new issue page will be opened" : "New issue page will be opened with data loaded"}"
              position="top-start"></vaadin-tooltip>
          </button>
        `,
      [this.exceptionReport, this.selectedItems, this.searchInputValue]
    )}></vaadin-dialog>`;
  }
  renderItemDescription(e) {
    return Object.keys(Je).indexOf(e.name.toLowerCase()) !== -1 ? Je[e.name.toLowerCase()] : null;
  }
  bodyLengthExceeds() {
    const e = this.getIssueBodyNotEncoded();
    return e !== void 0 && encodeURIComponent(e).length > 7500;
  }
  getIssueBodyNotEncoded() {
    if (!this.exceptionReport)
      return;
    const e = this.exceptionReport.items.filter((t, i) => this.selectedItems.indexOf(i) !== -1).map((t) => {
      let i = "```";
      return t.name.includes(".java") && (i = `${i}java`), `## ${t.name} 
 
 ${i}
${t.content}
\`\`\``;
    });
    return this.searchInputValue ? `## Description of the bug 
 ${this.searchInputValue} 
 ${e.join(`
`)}` : `## Description of the bug 
 Please enter bug description here 
 ${e.join(`
`)}`;
  }
  submitErrorToGithub() {
    const e = this.exceptionReport;
    if (!e)
      return;
    const t = encodeURIComponent(e.title ?? "Bug report "), i = this.getIssueBodyNotEncoded();
    if (!i)
      return;
    let n = encodeURIComponent(i);
    n.length >= 7500 && (et(i), n = encodeURIComponent("Please paste report here. It was automatically added to your clipboard."));
    const o = `https://github.com/vaadin/copilot/issues/new?title=${t}&body=${n}`;
    window.open(o, "_blank");
  }
};
T([
  x()
], D.prototype, "exceptionReport", 2);
T([
  x()
], D.prototype, "dialogOpened", 2);
T([
  x()
], D.prototype, "visibleItemIndex", 2);
T([
  x()
], D.prototype, "versions", 2);
T([
  x()
], D.prototype, "selectedItems", 2);
T([
  x()
], D.prototype, "searchInputValue", 2);
D = T([
  b("copilot-report-exception-dialog")
], D);
let Z;
u.on("copilot-project-compilation-error", (e) => {
  if (e.detail.error) {
    let t;
    if (e.detail.files && e.detail.files.length > 0) {
      const i = l.idePluginState?.supportedActions?.includes("undo") ? r`
            <button
              class="text-white"
              @click="${(n) => {
        n.preventDefault(), u.emit("undoRedo", { undo: !0, files: e.detail.files?.map((o) => o.path) });
      }}">
              <span aria-hidden="true" class="prefix">${d.flipBack}</span>
              Undo Last Change
            </button>
          ` : g;
      t = $e(
        r`<div>
          <span> Following files have compilation errors: </span>
          <ul class="mb-0 mt-25 ps-200">
            ${e.detail.files.map(
          (n) => r` <li>
                  <button
                    class="-ms-75 text-white"
                    @click="${() => {
            u.emit("show-in-ide", { javaSource: { absoluteFilePath: n.path } });
          }}">
                    ${n.name}
                  </button>
                </li>`
        )}
          </ul>
          <hr class="border-b border-white/10 border-e-0 border-s-0 border-t-0 my-50" />
          ${i}
        </div>`
      );
    } else
      t = "Project contains one or more compilation errors.";
    Z = N({
      message: "Compilation error",
      details: t,
      type: R.WARNING,
      delay: 3e4
    });
  } else
    Z && Ze(Z), Z = void 0;
});
var Di = Object.defineProperty, Li = Object.getOwnPropertyDescriptor, ct = (e, t, i, n) => {
  for (var o = n > 1 ? void 0 : n ? Li(t, i) : t, s = e.length - 1, a; s >= 0; s--)
    (a = e[s]) && (o = (n ? a(t, i, o) : a(o)) || o);
  return n && o && Di(t, i, o), o;
};
let ye = class extends X {
  constructor() {
    super(...arguments), this.text = () => (this.parentElement.textContent ?? "").trim();
  }
  createRenderRoot() {
    return this;
  }
  render() {
    return r`<button
      aria-label="Copy to Clipboard"
      class="icon"
      title="Copy to Clipboard"
      @click=${(e) => {
      e.stopPropagation(), e.preventDefault();
      const t = this.text();
      et(t);
    }}>
      ${d.copy}
    </button>`;
  }
};
ct([
  y({ type: Function })
], ye.prototype, "text", 2);
ye = ct([
  b("copilot-copy")
], ye);
var Mi = {
  202: "Accepted",
  502: "Bad Gateway",
  400: "Bad Request",
  409: "Conflict",
  100: "Continue",
  201: "Created",
  417: "Expectation Failed",
  424: "Failed Dependency",
  403: "Forbidden",
  504: "Gateway Timeout",
  410: "Gone",
  505: "HTTP Version Not Supported",
  418: "I'm a teapot",
  419: "Insufficient Space on Resource",
  507: "Insufficient Storage",
  500: "Internal Server Error",
  411: "Length Required",
  423: "Locked",
  420: "Method Failure",
  405: "Method Not Allowed",
  301: "Moved Permanently",
  302: "Moved Temporarily",
  207: "Multi-Status",
  300: "Multiple Choices",
  511: "Network Authentication Required",
  204: "No Content",
  203: "Non Authoritative Information",
  406: "Not Acceptable",
  404: "Not Found",
  501: "Not Implemented",
  304: "Not Modified",
  200: "OK",
  206: "Partial Content",
  402: "Payment Required",
  308: "Permanent Redirect",
  412: "Precondition Failed",
  428: "Precondition Required",
  102: "Processing",
  103: "Early Hints",
  426: "Upgrade Required",
  407: "Proxy Authentication Required",
  431: "Request Header Fields Too Large",
  408: "Request Timeout",
  413: "Request Entity Too Large",
  414: "Request-URI Too Long",
  416: "Requested Range Not Satisfiable",
  205: "Reset Content",
  303: "See Other",
  503: "Service Unavailable",
  101: "Switching Protocols",
  307: "Temporary Redirect",
  429: "Too Many Requests",
  401: "Unauthorized",
  451: "Unavailable For Legal Reasons",
  422: "Unprocessable Entity",
  415: "Unsupported Media Type",
  305: "Use Proxy",
  421: "Misdirected Request"
};
function zi(e) {
  var t = Mi[e.toString()];
  if (!t)
    throw new Error("Status code does not exist: " + e);
  return t;
}
function pt(e) {
  return `endpoint-request-${e.id}`;
}
u.on("endpoint-request", (e) => {
  const t = e.detail, i = pt(t);
  delete t.id;
  const n = Object.values(t.params), o = n.map(oe).join(", ");
  u.emit("log", {
    id: i,
    type: R.INFORMATION,
    message: `Called endpoint ${t.endpoint}.${t.method}(${o})`,
    expandedMessage: $e(
      r`Called endpoint ${t.endpoint}.${t.method} with parameters
        <code class="codeblock"><copilot-copy></copilot-copy>${oe(n)}</code>`
    ),
    details: "Response: <pending>"
  });
});
u.on("endpoint-response", (e) => {
  let t;
  try {
    t = JSON.parse(e.detail.text);
  } catch {
    t = e.detail.text;
  }
  const i = {}, n = e.detail.status ?? 200;
  n === 200 ? (i.details = `Response: ${oe(t)}`, i.expandedDetails = $e(
    r`Response: <code class="codeblock"><copilot-copy></copilot-copy>${oe(t)}</code>`
  )) : (i.details = `Error: ${n} ${zi(n)}`, i.type = R.ERROR), u.emit("update-log", {
    id: pt(e.detail),
    ...i
  });
});
function oe(e) {
  return typeof e == "string" ? `${e}` : JSON.stringify(e, void 0, 2);
}
var _i = Object.defineProperty, Oi = Object.getOwnPropertyDescriptor, H = (e, t, i, n) => {
  for (var o = n > 1 ? void 0 : n ? Oi(t, i) : t, s = e.length - 1, a; s >= 0; s--)
    (a = e[s]) && (o = (n ? a(t, i, o) : a(o)) || o);
  return n && o && _i(t, i, o), o;
};
class Ti extends CustomEvent {
  constructor(t) {
    super("show-in-ide-clicked", {
      detail: t,
      bubbles: !0,
      composed: !0
    });
  }
}
let $ = class extends X {
  constructor() {
    super(...arguments), this.iconHidden = !1, this.linkHidden = !1, this.tooltipText = void 0, this.linkText = void 0, this.source = void 0, this.javaSource = void 0;
  }
  static get styles() {
    return [
      m(Ie),
      m(Y),
      _`
        a {
          cursor: pointer;
          color: var(--source-file-link-color, var(--blue-600));
          text-decoration: var(--source-file-link-text-decoration, none);
          text-decoration-color: var(--source-file-link-decoration-color, currentColor);
          font-weight: var(--source-file-link-font-weight, normal);
        }
      `
    ];
  }
  render() {
    if (this.iconHidden) {
      if (!this.linkHidden)
        return this.renderContent(this.renderAnchor());
    } else return this.linkHidden ? this.renderContent(this.renderIcon()) : this.renderContent([this.renderIcon(), this.renderAnchor()]);
    return g;
  }
  renderContent(e) {
    return r` <div class="content">${e}</div> `;
  }
  renderIcon() {
    const e = this.tooltipText ?? `Open ${this.getFileName()} in IDE`;
    return r`
      <vaadin-button
        aria-label="${e}"
        id="show-in-ide"
        theme="icon tertiary"
        @click=${(t) => {
      t.stopPropagation(), t.preventDefault(), this._showInIde();
    }}>
        <vaadin-icon .svg="${d.code}"></vaadin-icon>
        <vaadin-tooltip slot="tooltip" text="${e}"></vaadin-tooltip>
      </vaadin-button>
    `;
  }
  renderAnchor() {
    return r`
      <a
        id="link"
        href="#"
        class="ahreflike"
        @click=${(e) => {
      e.preventDefault(), this._showInIde();
    }}
        >${this.linkText ?? this.getFileName() ?? ""}</a
      >
      ${this.renderTooltip("link")}
    `;
  }
  dispatchClickedEvent() {
    this.dispatchEvent(
      new Ti({
        source: this.source,
        javaSource: this.javaSource
      })
    );
  }
  renderTooltip(e) {
    const t = this.tooltipText ?? `Open ${this.getFileName()} in IDE`;
    return r`<vaadin-tooltip for="${e}" text="${t}" position="top-start"></vaadin-tooltip>`;
  }
  getFileName() {
    if (this.tooltipText)
      return this.tooltipText;
    if (this.source && this.source.fileName)
      return Lt(this.source.fileName);
    if (this.javaSource)
      return this.javaSource.className;
  }
  _showInIde() {
    u.emit("show-in-ide", {
      source: this.source,
      javaSource: this.javaSource
    }), this.dispatchClickedEvent();
  }
};
$.TAG = "copilot-go-to-source";
H([
  y({ type: Boolean })
], $.prototype, "iconHidden", 2);
H([
  y({ type: Boolean })
], $.prototype, "linkHidden", 2);
H([
  y()
], $.prototype, "tooltipText", 2);
H([
  y()
], $.prototype, "linkText", 2);
H([
  y()
], $.prototype, "source", 2);
H([
  y()
], $.prototype, "javaSource", 2);
$ = H([
  b($.TAG)
], $);
u.on("copilot-java-after-update", (e) => {
  const t = e.detail.classes.filter((n) => n.redefined).map((n) => n.class).join(", ");
  if (t.length === 0)
    return;
  const i = "java-hot-deploy";
  e.detail.classes.find((n) => n.routePath !== void 0) && u.emit("update-routes", {}), N({
    type: R.INFORMATION,
    message: `Java changes were hot deployed for ${Mt(t)}`,
    dismissId: i,
    delay: 5e3
  });
});
