import { D as c, j as d, b as h, w as y, a6 as u, s as v, P as f, H as m, r as k } from "./copilot-DtPsEJcm.js";
import { r as s } from "./state-suC5_Htu.js";
import { e as w } from "./query-BykXNUlT.js";
import { B as $ } from "./base-panel-7vzvPGpg.js";
import { i as x } from "./icons-C54UeX_I.js";
const A = "copilot-feedback-panel{display:flex;flex-direction:column;font:var(--copilot-font-xs);gap:var(--space-200);padding:var(--space-150)}copilot-feedback-panel>p{margin:0}copilot-feedback-panel .dialog-footer{display:flex;gap:var(--space-100)}copilot-feedback-panel :is(vaadin-select,vaadin-email-field)::part(input-field){padding-block:0}copilot-feedback-panel :is(vaadin-select)::part(input-field){padding-inline-end:0}copilot-feedback-panel vaadin-select::part(toggle-button){align-items:center;display:flex;height:var(--copilot-size-md);justify-content:center;width:var(--copilot-size-md)}copilot-feedback-panel vaadin-text-area textarea{line-height:var(--copilot-line-height-sm)}copilot-feedback-panel vaadin-text-area:hover::part(input-field){background:none}copilot-feedback-panel>*::part(helper-text){line-height:var(--copilot-line-height-sm);margin:0}";
var P = Object.defineProperty, F = Object.getOwnPropertyDescriptor, o = (e, t, n, l) => {
  for (var a = l > 1 ? void 0 : l ? F(t, n) : t, p = e.length - 1, r; p >= 0; p--)
    (r = e[p]) && (a = (l ? r(t, n, a) : r(a)) || a);
  return l && a && P(t, n, a), a;
};
const T = "https://github.com/vaadin", b = "https://github.com/vaadin/copilot/issues/new", D = "?template=feature_request.md&title=%5BFEATURE%5D", E = "A short, concise description of the bug and why you consider it a bug. Any details like exceptions and logs can be helpful as well.", C = "Please provide as many details as possible, this will help us deliver a fix as soon as possible.%0AThank you!%0A%0A%23%23%23 Description of the Bug%0A%0A{description}%0A%0A%23%23%23 Expected Behavior%0A%0AA description of what you would expect to happen. (Sometimes it is clear what the expected outcome is if something does not work, other times, it is not super clear.)%0A%0A%23%23%23 Minimal Reproducible Example%0A%0AWe would appreciate the minimum code with which we can reproduce the issue.%0A%0A%23%23%23 Versions%0A{versionsInfo}";
let i = class extends $ {
  constructor() {
    super(), this.description = "", this.types = [
      {
        label: "Generic feedback",
        value: "feedback",
        ghTitle: ""
      },
      {
        label: "Report a bug",
        value: "bug",
        ghTitle: "[BUG]"
      },
      {
        label: "Ask a question",
        value: "question",
        ghTitle: "[QUESTION]"
      },
      {
        label: "Share an idea",
        value: "idea",
        ghTitle: "[FEATURE]"
      }
    ], this.type = this.types[0].value, this.topics = [
      {
        label: "Generic",
        value: "platform"
      },
      {
        label: "Flow",
        value: "flow"
      },
      {
        label: "Hilla",
        value: "hilla"
      },
      {
        label: "Copilot",
        value: "copilot"
      }
    ], this.topic = this.topics[0].value;
  }
  render() {
    return c`<style>
        ${A}</style
      >${this.renderContent()}${this.renderFooter()}`;
  }
  renderContent() {
    return this.message === void 0 ? c`
          <p>
            Your insights are incredibly valuable to us. Whether you’ve encountered a hiccup, have questions, or ideas
            to make our platform better, we're all ears! If you wish, leave your email, and we’ll get back to you. You
            can even share your code snippet with us for a clearer picture.
          </p>
          <vaadin-select
            .items="${this.types}"
            .value="${this.type}"
            overlay-class="alwaysVisible"
            @value-changed=${(e) => {
      this.type = e.detail.value;
    }}>
          </vaadin-select>
          <vaadin-select
            label="Feedback Topic"
            overlay-class="alwaysVisible"
            .items=${this.topics}
            .value="${this.topic}"
            .hidden=${this.type !== "feedback"}
            @value-changed=${(e) => {
      this.topic = e.detail.value;
    }}>
          </vaadin-select>
          <vaadin-text-area
            .value="${this.description}"
            @keydown=${this.keyDown}
            @focus=${() => {
      this.descriptionField.invalid = !1, this.descriptionField.placeholder = "";
    }}
            @value-changed=${(e) => {
      this.description = e.detail.value;
    }}
            label="Tell Us More"
            helper-text="Describe what you're experiencing, wondering about, or envisioning. The more you share, the better we can understand and act on your feedback"></vaadin-text-area>
          <vaadin-text-field
            @keydown=${this.keyDown}
            @value-changed=${(e) => {
      this.email = e.detail.value;
    }}
            .required=${this.type === "question"}
            id="email"
            value="${d.userInfo?.email}"
            label="Your Email${this.type === "question" ? "" : " (Optional)"}"
            helper-text="Leave your email if you’d like us to follow up, we’d love to keep the conversation going."></vaadin-text-field>
        ` : c`<p>${this.message}</p>`;
  }
  renderFooter() {
    return this.message === void 0 ? c`
          <div class="dialog-footer">
            <button
              style="margin-inline-end: auto"
              @click="${() => {
      d.active ? h.emit("system-info-with-callback", {
        callback: (e) => this.openGithub(e, this),
        notify: !1
      }) : this.openGithub(null, this);
    }}">
              <span class="prefix">${x.github}</span>
              Create GitHub Issue
            </button>
            <button @click="${this.close}">Cancel</button>
            <button class="primary" @click="${this.submit}" .disabled=${this.type === "question" && !this.email}>
              Submit
            </button>
          </div>
        ` : c` <div class="footer">
          <vaadin-button @click="${this.close}">Close</vaadin-button>
        </div>`;
  }
  close() {
    y.updatePanel("copilot-feedback-panel", {
      floating: !1
    });
  }
  submit() {
    if (u("feedback", { github: !1, type: this.type, topic: this.topic }), this.description.trim() === "") {
      this.descriptionField.invalid = !0, this.descriptionField.placeholder = "Please tell us more before sending", this.descriptionField.value = "";
      return;
    }
    const e = {
      description: this.description,
      email: this.email,
      type: this.type,
      topic: this.topic
    };
    d.active ? h.emit("system-info-with-callback", {
      callback: (t) => v(`${f}feedback`, { ...e, versions: t }),
      notify: !1
    }) : v(`${f}feedback`, { ...e, versions: {} }), this.parentNode?.style.setProperty("--section-height", "150px"), this.message = "Thank you for sharing feedback.";
  }
  keyDown(e) {
    (e.key === "Backspace" || e.key === "Delete") && e.stopPropagation();
  }
  openGithub(e, t) {
    if (u("feedback", { github: !0, type: this.type, topic: this.topic }), this.type === "idea") {
      window.open(`${b}${D}`);
      return;
    }
    if (this.type === "feedback") {
      window.open(`${T}/${this.topic}/issues/new`);
      return;
    }
    const n = e ? e.replace(/\n/g, "%0A") : "Activate Copilot to include version info.", l = `${t.types.find((r) => r.value === this.type)?.ghTitle}`, a = t.description !== "" ? t.description : E, p = C.replace("{description}", a).replace("{versionsInfo}", n);
    window.open(`${b}?title=${l}&body=${p}`, "_blank")?.focus();
  }
};
o([
  s()
], i.prototype, "description", 2);
o([
  s()
], i.prototype, "type", 2);
o([
  s()
], i.prototype, "topic", 2);
o([
  s()
], i.prototype, "email", 2);
o([
  s()
], i.prototype, "message", 2);
o([
  s()
], i.prototype, "types", 2);
o([
  s()
], i.prototype, "topics", 2);
o([
  w("vaadin-text-area")
], i.prototype, "descriptionField", 2);
i = o([
  k("copilot-feedback-panel")
], i);
const g = m({
  header: "Help Us Improve!",
  tag: "copilot-feedback-panel",
  width: 500,
  height: 550,
  floatingPosition: {
    top: 100,
    left: 100
  },
  individual: !0
}), U = {
  init(e) {
    e.addPanel(g);
  }
};
window.Vaadin.copilot.plugins.push(U);
y.addPanel(g);
export {
  i as CopilotFeedbackPanel
};
