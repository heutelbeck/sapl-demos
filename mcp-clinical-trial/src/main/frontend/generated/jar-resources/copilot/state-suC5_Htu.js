import { av as u, aw as l } from "./copilot-DtPsEJcm.js";
/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
const p = { attribute: !0, type: String, converter: l, reflect: !1, hasChanged: u }, d = (t = p, o, e) => {
  const { kind: s, metadata: i } = e;
  let r = globalThis.litPropertyMetadata.get(i);
  if (r === void 0 && globalThis.litPropertyMetadata.set(i, r = /* @__PURE__ */ new Map()), s === "setter" && ((t = Object.create(t)).wrapped = !0), r.set(e.name, t), s === "accessor") {
    const { name: a } = e;
    return { set(n) {
      const c = o.get.call(this);
      o.set.call(this, n), this.requestUpdate(a, c, t);
    }, init(n) {
      return n !== void 0 && this.C(a, void 0, t, n), n;
    } };
  }
  if (s === "setter") {
    const { name: a } = e;
    return function(n) {
      const c = this[a];
      o.call(this, n), this.requestUpdate(a, c, t);
    };
  }
  throw Error("Unsupported decorator location: " + s);
};
function h(t) {
  return (o, e) => typeof e == "object" ? d(t, o, e) : ((s, i, r) => {
    const a = i.hasOwnProperty(r);
    return i.constructor.createProperty(r, s), a ? Object.getOwnPropertyDescriptor(i, r) : void 0;
  })(t, o, e);
}
/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
function b(t) {
  return h({ ...t, state: !0, attribute: !1 });
}
export {
  h as n,
  b as r
};
