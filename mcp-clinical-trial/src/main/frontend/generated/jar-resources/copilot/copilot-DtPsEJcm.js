class Ha {
  constructor() {
    this.eventBuffer = [], this.handledTypes = [], this.copilotMain = null, this.debug = !1, this.eventProxy = {
      functionCallQueue: [],
      dispatchEvent(...t) {
        return this.functionCallQueue.push({ name: "dispatchEvent", args: t }), !0;
      },
      removeEventListener(...t) {
        this.functionCallQueue.push({ name: "removeEventListener", args: t });
      },
      addEventListener(...t) {
        this.functionCallQueue.push({ name: "addEventListener", args: t });
      },
      processQueue(t) {
        this.functionCallQueue.forEach((r) => {
          t[r.name].call(t, ...r.args);
        }), this.functionCallQueue = [];
      }
    };
  }
  getEventTarget() {
    return this.copilotMain ? this.copilotMain : (this.copilotMain = document.querySelector("copilot-main"), this.copilotMain ? (this.eventProxy.processQueue(this.copilotMain), this.copilotMain) : this.eventProxy);
  }
  on(t, r) {
    const n = r;
    return this.getEventTarget().addEventListener(t, n), this.handledTypes.push(t), this.flush(t), () => this.off(t, n);
  }
  once(t, r) {
    this.getEventTarget().addEventListener(t, r, { once: !0 });
  }
  off(t, r) {
    this.getEventTarget().removeEventListener(t, r);
    const n = this.handledTypes.indexOf(t, 0);
    n > -1 && this.handledTypes.splice(n, 1);
  }
  emit(t, r) {
    const n = new CustomEvent(t, { detail: r, cancelable: !0 });
    return this.handledTypes.includes(t) || this.eventBuffer.push(n), this.debug && console.debug("Emit event", n), this.getEventTarget().dispatchEvent(n), n.defaultPrevented;
  }
  emitUnsafe({ type: t, data: r }) {
    return this.emit(t, r);
  }
  // Communication with server via eventbus
  send(t, r) {
    const n = new CustomEvent("copilot-send", { detail: { command: t, data: r } });
    this.getEventTarget().dispatchEvent(n);
  }
  // Listeners for Copilot itself
  onSend(t) {
    this.on("copilot-send", t);
  }
  offSend(t) {
    this.off("copilot-send", t);
  }
  flush(t) {
    const r = [];
    this.eventBuffer.filter((n) => n.type === t).forEach((n) => {
      this.getEventTarget().dispatchEvent(n), r.push(n);
    }), this.eventBuffer = this.eventBuffer.filter((n) => !r.includes(n));
  }
}
var Xa = {
  0: "Invalid value for configuration 'enforceActions', expected 'never', 'always' or 'observed'",
  1: function(t, r) {
    return "Cannot apply '" + t + "' to '" + r.toString() + "': Field not found.";
  },
  /*
  2(prop) {
      return `invalid decorator for '${prop.toString()}'`
  },
  3(prop) {
      return `Cannot decorate '${prop.toString()}': action can only be used on properties with a function value.`
  },
  4(prop) {
      return `Cannot decorate '${prop.toString()}': computed can only be used on getter properties.`
  },
  */
  5: "'keys()' can only be used on observable objects, arrays, sets and maps",
  6: "'values()' can only be used on observable objects, arrays, sets and maps",
  7: "'entries()' can only be used on observable objects, arrays and maps",
  8: "'set()' can only be used on observable objects, arrays and maps",
  9: "'remove()' can only be used on observable objects, arrays and maps",
  10: "'has()' can only be used on observable objects, arrays and maps",
  11: "'get()' can only be used on observable objects, arrays and maps",
  12: "Invalid annotation",
  13: "Dynamic observable objects cannot be frozen. If you're passing observables to 3rd party component/function that calls Object.freeze, pass copy instead: toJS(observable)",
  14: "Intercept handlers should return nothing or a change object",
  15: "Observable arrays cannot be frozen. If you're passing observables to 3rd party component/function that calls Object.freeze, pass copy instead: toJS(observable)",
  16: "Modification exception: the internal structure of an observable array was changed.",
  17: function(t, r) {
    return "[mobx.array] Index out of bounds, " + t + " is larger than " + r;
  },
  18: "mobx.map requires Map polyfill for the current browser. Check babel-polyfill or core-js/es6/map.js",
  19: function(t) {
    return "Cannot initialize from classes that inherit from Map: " + t.constructor.name;
  },
  20: function(t) {
    return "Cannot initialize map from " + t;
  },
  21: function(t) {
    return "Cannot convert to map from '" + t + "'";
  },
  22: "mobx.set requires Set polyfill for the current browser. Check babel-polyfill or core-js/es6/set.js",
  23: "It is not possible to get index atoms from arrays",
  24: function(t) {
    return "Cannot obtain administration from " + t;
  },
  25: function(t, r) {
    return "the entry '" + t + "' does not exist in the observable map '" + r + "'";
  },
  26: "please specify a property",
  27: function(t, r) {
    return "no observable property '" + t.toString() + "' found on the observable object '" + r + "'";
  },
  28: function(t) {
    return "Cannot obtain atom from " + t;
  },
  29: "Expecting some object",
  30: "invalid action stack. did you forget to finish an action?",
  31: "missing option for computed: get",
  32: function(t, r) {
    return "Cycle detected in computation " + t + ": " + r;
  },
  33: function(t) {
    return "The setter of computed value '" + t + "' is trying to update itself. Did you intend to update an _observable_ value, instead of the computed property?";
  },
  34: function(t) {
    return "[ComputedValue '" + t + "'] It is not possible to assign a new value to a computed value.";
  },
  35: "There are multiple, different versions of MobX active. Make sure MobX is loaded only once or use `configure({ isolateGlobalState: true })`",
  36: "isolateGlobalState should be called before MobX is running any reactions",
  37: function(t) {
    return "[mobx] `observableArray." + t + "()` mutates the array in-place, which is not allowed inside a derivation. Use `array.slice()." + t + "()` instead";
  },
  38: "'ownKeys()' can only be used on observable objects",
  39: "'defineProperty()' can only be used on observable objects"
}, Ja = process.env.NODE_ENV !== "production" ? Xa : {};
function f(e) {
  for (var t = arguments.length, r = new Array(t > 1 ? t - 1 : 0), n = 1; n < t; n++)
    r[n - 1] = arguments[n];
  if (process.env.NODE_ENV !== "production") {
    var i = typeof e == "string" ? e : Ja[e];
    throw typeof i == "function" && (i = i.apply(null, r)), new Error("[MobX] " + i);
  }
  throw new Error(typeof e == "number" ? "[MobX] minified error nr: " + e + (r.length ? " " + r.map(String).join(",") : "") + ". Find the full error at: https://github.com/mobxjs/mobx/blob/main/packages/mobx/src/errors.ts" : "[MobX] " + e);
}
var Ga = {};
function Hr() {
  return typeof globalThis < "u" ? globalThis : typeof window < "u" ? window : typeof global < "u" ? global : typeof self < "u" ? self : Ga;
}
var hi = Object.assign, Wt = Object.getOwnPropertyDescriptor, _ = Object.defineProperty, sr = Object.prototype, Ht = [];
Object.freeze(Ht);
var Xr = {};
Object.freeze(Xr);
var Ya = typeof Proxy < "u", Qa = /* @__PURE__ */ Object.toString();
function gi() {
  Ya || f(process.env.NODE_ENV !== "production" ? "`Proxy` objects are not available in the current environment. Please configure MobX to enable a fallback implementation.`" : "Proxy not available");
}
function at(e) {
  process.env.NODE_ENV !== "production" && p.verifyProxies && f("MobX is currently configured to be able to run in ES5 mode, but in ES5 MobX won't be able to " + e);
}
function F() {
  return ++p.mobxGuid;
}
function Jr(e) {
  var t = !1;
  return function() {
    if (!t)
      return t = !0, e.apply(this, arguments);
  };
}
var Fe = function() {
};
function k(e) {
  return typeof e == "function";
}
function Pe(e) {
  var t = typeof e;
  switch (t) {
    case "string":
    case "symbol":
    case "number":
      return !0;
  }
  return !1;
}
function lr(e) {
  return e !== null && typeof e == "object";
}
function D(e) {
  if (!lr(e))
    return !1;
  var t = Object.getPrototypeOf(e);
  if (t == null)
    return !0;
  var r = Object.hasOwnProperty.call(t, "constructor") && t.constructor;
  return typeof r == "function" && r.toString() === Qa;
}
function mi(e) {
  var t = e?.constructor;
  return t ? t.name === "GeneratorFunction" || t.displayName === "GeneratorFunction" : !1;
}
function cr(e, t, r) {
  _(e, t, {
    enumerable: !1,
    writable: !0,
    configurable: !0,
    value: r
  });
}
function bi(e, t, r) {
  _(e, t, {
    enumerable: !1,
    writable: !1,
    configurable: !0,
    value: r
  });
}
function Ue(e, t) {
  var r = "isMobX" + e;
  return t.prototype[r] = !0, function(n) {
    return lr(n) && n[r] === !0;
  };
}
function Qe(e) {
  return e != null && Object.prototype.toString.call(e) === "[object Map]";
}
function _a(e) {
  var t = Object.getPrototypeOf(e), r = Object.getPrototypeOf(t), n = Object.getPrototypeOf(r);
  return n === null;
}
function ne(e) {
  return e != null && Object.prototype.toString.call(e) === "[object Set]";
}
var yi = typeof Object.getOwnPropertySymbols < "u";
function $a(e) {
  var t = Object.keys(e);
  if (!yi)
    return t;
  var r = Object.getOwnPropertySymbols(e);
  return r.length ? [].concat(t, r.filter(function(n) {
    return sr.propertyIsEnumerable.call(e, n);
  })) : t;
}
var mt = typeof Reflect < "u" && Reflect.ownKeys ? Reflect.ownKeys : yi ? function(e) {
  return Object.getOwnPropertyNames(e).concat(Object.getOwnPropertySymbols(e));
} : (
  /* istanbul ignore next */
  Object.getOwnPropertyNames
);
function Vr(e) {
  return typeof e == "string" ? e : typeof e == "symbol" ? e.toString() : new String(e).toString();
}
function wi(e) {
  return e === null ? null : typeof e == "object" ? "" + e : e;
}
function W(e, t) {
  return sr.hasOwnProperty.call(e, t);
}
var eo = Object.getOwnPropertyDescriptors || function(t) {
  var r = {};
  return mt(t).forEach(function(n) {
    r[n] = Wt(t, n);
  }), r;
};
function T(e, t) {
  return !!(e & t);
}
function V(e, t, r) {
  return r ? e |= t : e &= ~t, e;
}
function pn(e, t) {
  (t == null || t > e.length) && (t = e.length);
  for (var r = 0, n = Array(t); r < t; r++) n[r] = e[r];
  return n;
}
function to(e, t) {
  for (var r = 0; r < t.length; r++) {
    var n = t[r];
    n.enumerable = n.enumerable || !1, n.configurable = !0, "value" in n && (n.writable = !0), Object.defineProperty(e, no(n.key), n);
  }
}
function _e(e, t, r) {
  return t && to(e.prototype, t), Object.defineProperty(e, "prototype", {
    writable: !1
  }), e;
}
function Ze(e, t) {
  var r = typeof Symbol < "u" && e[Symbol.iterator] || e["@@iterator"];
  if (r) return (r = r.call(e)).next.bind(r);
  if (Array.isArray(e) || (r = io(e)) || t) {
    r && (e = r);
    var n = 0;
    return function() {
      return n >= e.length ? {
        done: !0
      } : {
        done: !1,
        value: e[n++]
      };
    };
  }
  throw new TypeError(`Invalid attempt to iterate non-iterable instance.
In order to be iterable, non-array objects must have a [Symbol.iterator]() method.`);
}
function ve() {
  return ve = Object.assign ? Object.assign.bind() : function(e) {
    for (var t = 1; t < arguments.length; t++) {
      var r = arguments[t];
      for (var n in r) ({}).hasOwnProperty.call(r, n) && (e[n] = r[n]);
    }
    return e;
  }, ve.apply(null, arguments);
}
function xi(e, t) {
  e.prototype = Object.create(t.prototype), e.prototype.constructor = e, Ir(e, t);
}
function Ir(e, t) {
  return Ir = Object.setPrototypeOf ? Object.setPrototypeOf.bind() : function(r, n) {
    return r.__proto__ = n, r;
  }, Ir(e, t);
}
function ro(e, t) {
  if (typeof e != "object" || !e) return e;
  var r = e[Symbol.toPrimitive];
  if (r !== void 0) {
    var n = r.call(e, t);
    if (typeof n != "object") return n;
    throw new TypeError("@@toPrimitive must return a primitive value.");
  }
  return String(e);
}
function no(e) {
  var t = ro(e, "string");
  return typeof t == "symbol" ? t : t + "";
}
function io(e, t) {
  if (e) {
    if (typeof e == "string") return pn(e, t);
    var r = {}.toString.call(e).slice(8, -1);
    return r === "Object" && e.constructor && (r = e.constructor.name), r === "Map" || r === "Set" ? Array.from(e) : r === "Arguments" || /^(?:Ui|I)nt(?:8|16|32)(?:Clamped)?Array$/.test(r) ? pn(e, t) : void 0;
  }
}
var ie = /* @__PURE__ */ Symbol("mobx-stored-annotations");
function $(e) {
  function t(r, n) {
    if (At(n))
      return e.decorate_20223_(r, n);
    Ct(r, n, e);
  }
  return Object.assign(t, e);
}
function Ct(e, t, r) {
  if (W(e, ie) || cr(e, ie, ve({}, e[ie])), process.env.NODE_ENV !== "production" && Xt(r) && !W(e[ie], t)) {
    var n = e.constructor.name + ".prototype." + t.toString();
    f("'" + n + "' is decorated with 'override', but no such decorated member was found on prototype.");
  }
  ao(e, r, t), Xt(r) || (e[ie][t] = r);
}
function ao(e, t, r) {
  if (process.env.NODE_ENV !== "production" && !Xt(t) && W(e[ie], r)) {
    var n = e.constructor.name + ".prototype." + r.toString(), i = e[ie][r].annotationType_, a = t.annotationType_;
    f("Cannot apply '@" + a + "' to '" + n + "':" + (`
The field is already decorated with '@` + i + "'.") + `
Re-decorating fields is not allowed.
Use '@override' decorator for methods overridden by subclass.`);
  }
}
function At(e) {
  return typeof e == "object" && typeof e.kind == "string";
}
function dr(e, t) {
  process.env.NODE_ENV !== "production" && !t.includes(e.kind) && f("The decorator applied to '" + String(e.name) + "' cannot be used on a " + e.kind + " element");
}
var g = /* @__PURE__ */ Symbol("mobx administration"), me = /* @__PURE__ */ function() {
  function e(r) {
    r === void 0 && (r = process.env.NODE_ENV !== "production" ? "Atom@" + F() : "Atom"), this.name_ = void 0, this.flags_ = 0, this.observers_ = /* @__PURE__ */ new Set(), this.lastAccessedBy_ = 0, this.lowestObserverState_ = w.NOT_TRACKING_, this.onBOL = void 0, this.onBUOL = void 0, this.name_ = r;
  }
  var t = e.prototype;
  return t.onBO = function() {
    this.onBOL && this.onBOL.forEach(function(n) {
      return n();
    });
  }, t.onBUO = function() {
    this.onBUOL && this.onBUOL.forEach(function(n) {
      return n();
    });
  }, t.reportObserved = function() {
    return Mi(this);
  }, t.reportChanged = function() {
    R(), ji(this), q();
  }, t.toString = function() {
    return this.name_;
  }, _e(e, [{
    key: "isBeingObserved",
    get: function() {
      return T(this.flags_, e.isBeingObservedMask_);
    },
    set: function(n) {
      this.flags_ = V(this.flags_, e.isBeingObservedMask_, n);
    }
  }, {
    key: "isPendingUnobservation",
    get: function() {
      return T(this.flags_, e.isPendingUnobservationMask_);
    },
    set: function(n) {
      this.flags_ = V(this.flags_, e.isPendingUnobservationMask_, n);
    }
  }, {
    key: "diffValue",
    get: function() {
      return T(this.flags_, e.diffValueMask_) ? 1 : 0;
    },
    set: function(n) {
      this.flags_ = V(this.flags_, e.diffValueMask_, n === 1);
    }
  }]);
}();
me.isBeingObservedMask_ = 1;
me.isPendingUnobservationMask_ = 2;
me.diffValueMask_ = 4;
var Gr = /* @__PURE__ */ Ue("Atom", me);
function Oi(e, t, r) {
  t === void 0 && (t = Fe), r === void 0 && (r = Fe);
  var n = new me(e);
  return t !== Fe && gs(n, t), r !== Fe && Xi(n, r), n;
}
function oo(e, t) {
  return aa(e, t);
}
function so(e, t) {
  return Object.is ? Object.is(e, t) : e === t ? e !== 0 || 1 / e === 1 / t : e !== e && t !== t;
}
var Xe = {
  structural: oo,
  default: so
};
function De(e, t, r) {
  return wt(e) ? e : Array.isArray(e) ? C.array(e, {
    name: r
  }) : D(e) ? C.object(e, void 0, {
    name: r
  }) : Qe(e) ? C.map(e, {
    name: r
  }) : ne(e) ? C.set(e, {
    name: r
  }) : typeof e == "function" && !Ve(e) && !yt(e) ? mi(e) ? Je(e) : bt(r, e) : e;
}
function lo(e, t, r) {
  if (e == null || tt(e) || mr(e) || be(e) || Y(e))
    return e;
  if (Array.isArray(e))
    return C.array(e, {
      name: r,
      deep: !1
    });
  if (D(e))
    return C.object(e, void 0, {
      name: r,
      deep: !1
    });
  if (Qe(e))
    return C.map(e, {
      name: r,
      deep: !1
    });
  if (ne(e))
    return C.set(e, {
      name: r,
      deep: !1
    });
  process.env.NODE_ENV !== "production" && f("The shallow modifier / decorator can only used in combination with arrays, objects, maps and sets");
}
function ur(e) {
  return e;
}
function co(e, t) {
  return process.env.NODE_ENV !== "production" && wt(e) && f("observable.struct should not be used with observable values"), aa(e, t) ? t : e;
}
var uo = "override";
function Xt(e) {
  return e.annotationType_ === uo;
}
function St(e, t) {
  return {
    annotationType_: e,
    options_: t,
    make_: po,
    extend_: fo,
    decorate_20223_: vo
  };
}
function po(e, t, r, n) {
  var i;
  if ((i = this.options_) != null && i.bound)
    return this.extend_(e, t, r, !1) === null ? 0 : 1;
  if (n === e.target_)
    return this.extend_(e, t, r, !1) === null ? 0 : 2;
  if (Ve(r.value))
    return 1;
  var a = Ei(e, this, t, r, !1);
  return _(n, t, a), 2;
}
function fo(e, t, r, n) {
  var i = Ei(e, this, t, r);
  return e.defineProperty_(t, i, n);
}
function vo(e, t) {
  process.env.NODE_ENV !== "production" && dr(t, ["method", "field"]);
  var r = t.kind, n = t.name, i = t.addInitializer, a = this, o = function(c) {
    var d, u, v, h;
    return Ne((d = (u = a.options_) == null ? void 0 : u.name) != null ? d : n.toString(), c, (v = (h = a.options_) == null ? void 0 : h.autoAction) != null ? v : !1);
  };
  if (r == "field")
    return function(l) {
      var c, d = l;
      return Ve(d) || (d = o(d)), (c = a.options_) != null && c.bound && (d = d.bind(this), d.isMobxAction = !0), d;
    };
  if (r == "method") {
    var s;
    return Ve(e) || (e = o(e)), (s = this.options_) != null && s.bound && i(function() {
      var l = this, c = l[n].bind(l);
      c.isMobxAction = !0, l[n] = c;
    }), e;
  }
  f("Cannot apply '" + a.annotationType_ + "' to '" + String(n) + "' (kind: " + r + "):" + (`
'` + a.annotationType_ + "' can only be used on properties with a function value."));
}
function ho(e, t, r, n) {
  var i = t.annotationType_, a = n.value;
  process.env.NODE_ENV !== "production" && !k(a) && f("Cannot apply '" + i + "' to '" + e.name_ + "." + r.toString() + "':" + (`
'` + i + "' can only be used on properties with a function value."));
}
function Ei(e, t, r, n, i) {
  var a, o, s, l, c, d, u;
  i === void 0 && (i = p.safeDescriptors), ho(e, t, r, n);
  var v = n.value;
  if ((a = t.options_) != null && a.bound) {
    var h;
    v = v.bind((h = e.proxy_) != null ? h : e.target_);
  }
  return {
    value: Ne(
      (o = (s = t.options_) == null ? void 0 : s.name) != null ? o : r.toString(),
      v,
      (l = (c = t.options_) == null ? void 0 : c.autoAction) != null ? l : !1,
      // https://github.com/mobxjs/mobx/discussions/3140
      (d = t.options_) != null && d.bound ? (u = e.proxy_) != null ? u : e.target_ : void 0
    ),
    // Non-configurable for classes
    // prevents accidental field redefinition in subclass
    configurable: i ? e.isPlainObject_ : !0,
    // https://github.com/mobxjs/mobx/pull/2641#issuecomment-737292058
    enumerable: !1,
    // Non-obsevable, therefore non-writable
    // Also prevents rewriting in subclass constructor
    writable: !i
  };
}
function ki(e, t) {
  return {
    annotationType_: e,
    options_: t,
    make_: go,
    extend_: mo,
    decorate_20223_: bo
  };
}
function go(e, t, r, n) {
  var i;
  if (n === e.target_)
    return this.extend_(e, t, r, !1) === null ? 0 : 2;
  if ((i = this.options_) != null && i.bound && (!W(e.target_, t) || !yt(e.target_[t])) && this.extend_(e, t, r, !1) === null)
    return 0;
  if (yt(r.value))
    return 1;
  var a = Ci(e, this, t, r, !1, !1);
  return _(n, t, a), 2;
}
function mo(e, t, r, n) {
  var i, a = Ci(e, this, t, r, (i = this.options_) == null ? void 0 : i.bound);
  return e.defineProperty_(t, a, n);
}
function bo(e, t) {
  var r;
  process.env.NODE_ENV !== "production" && dr(t, ["method"]);
  var n = t.name, i = t.addInitializer;
  return yt(e) || (e = Je(e)), (r = this.options_) != null && r.bound && i(function() {
    var a = this, o = a[n].bind(a);
    o.isMobXFlow = !0, a[n] = o;
  }), e;
}
function yo(e, t, r, n) {
  var i = t.annotationType_, a = n.value;
  process.env.NODE_ENV !== "production" && !k(a) && f("Cannot apply '" + i + "' to '" + e.name_ + "." + r.toString() + "':" + (`
'` + i + "' can only be used on properties with a generator function value."));
}
function Ci(e, t, r, n, i, a) {
  a === void 0 && (a = p.safeDescriptors), yo(e, t, r, n);
  var o = n.value;
  if (yt(o) || (o = Je(o)), i) {
    var s;
    o = o.bind((s = e.proxy_) != null ? s : e.target_), o.isMobXFlow = !0;
  }
  return {
    value: o,
    // Non-configurable for classes
    // prevents accidental field redefinition in subclass
    configurable: a ? e.isPlainObject_ : !0,
    // https://github.com/mobxjs/mobx/pull/2641#issuecomment-737292058
    enumerable: !1,
    // Non-obsevable, therefore non-writable
    // Also prevents rewriting in subclass constructor
    writable: !a
  };
}
function Yr(e, t) {
  return {
    annotationType_: e,
    options_: t,
    make_: wo,
    extend_: xo,
    decorate_20223_: Oo
  };
}
function wo(e, t, r) {
  return this.extend_(e, t, r, !1) === null ? 0 : 1;
}
function xo(e, t, r, n) {
  return Eo(e, this, t, r), e.defineComputedProperty_(t, ve({}, this.options_, {
    get: r.get,
    set: r.set
  }), n);
}
function Oo(e, t) {
  process.env.NODE_ENV !== "production" && dr(t, ["getter"]);
  var r = this, n = t.name, i = t.addInitializer;
  return i(function() {
    var a = et(this)[g], o = ve({}, r.options_, {
      get: e,
      context: this
    });
    o.name || (o.name = process.env.NODE_ENV !== "production" ? a.name_ + "." + n.toString() : "ObservableObject." + n.toString()), a.values_.set(n, new B(o));
  }), function() {
    return this[g].getObservablePropValue_(n);
  };
}
function Eo(e, t, r, n) {
  var i = t.annotationType_, a = n.get;
  process.env.NODE_ENV !== "production" && !a && f("Cannot apply '" + i + "' to '" + e.name_ + "." + r.toString() + "':" + (`
'` + i + "' can only be used on getter(+setter) properties."));
}
function pr(e, t) {
  return {
    annotationType_: e,
    options_: t,
    make_: ko,
    extend_: Co,
    decorate_20223_: Ao
  };
}
function ko(e, t, r) {
  return this.extend_(e, t, r, !1) === null ? 0 : 1;
}
function Co(e, t, r, n) {
  var i, a;
  return So(e, this, t, r), e.defineObservableProperty_(t, r.value, (i = (a = this.options_) == null ? void 0 : a.enhancer) != null ? i : De, n);
}
function Ao(e, t) {
  if (process.env.NODE_ENV !== "production") {
    if (t.kind === "field")
      throw f("Please use `@observable accessor " + String(t.name) + "` instead of `@observable " + String(t.name) + "`");
    dr(t, ["accessor"]);
  }
  var r = this, n = t.kind, i = t.name, a = /* @__PURE__ */ new WeakSet();
  function o(s, l) {
    var c, d, u = et(s)[g], v = new Se(l, (c = (d = r.options_) == null ? void 0 : d.enhancer) != null ? c : De, process.env.NODE_ENV !== "production" ? u.name_ + "." + i.toString() : "ObservableObject." + i.toString(), !1);
    u.values_.set(i, v), a.add(s);
  }
  if (n == "accessor")
    return {
      get: function() {
        return a.has(this) || o(this, e.get.call(this)), this[g].getObservablePropValue_(i);
      },
      set: function(l) {
        return a.has(this) || o(this, l), this[g].setObservablePropValue_(i, l);
      },
      init: function(l) {
        return a.has(this) || o(this, l), l;
      }
    };
}
function So(e, t, r, n) {
  var i = t.annotationType_;
  process.env.NODE_ENV !== "production" && !("value" in n) && f("Cannot apply '" + i + "' to '" + e.name_ + "." + r.toString() + "':" + (`
'` + i + "' cannot be used on getter/setter properties"));
}
var Po = "true", Do = /* @__PURE__ */ Ai();
function Ai(e) {
  return {
    annotationType_: Po,
    options_: e,
    make_: No,
    extend_: To,
    decorate_20223_: Vo
  };
}
function No(e, t, r, n) {
  var i, a;
  if (r.get)
    return fr.make_(e, t, r, n);
  if (r.set) {
    var o = Ve(r.set) ? r.set : Ne(t.toString(), r.set);
    return n === e.target_ ? e.defineProperty_(t, {
      configurable: p.safeDescriptors ? e.isPlainObject_ : !0,
      set: o
    }) === null ? 0 : 2 : (_(n, t, {
      configurable: !0,
      set: o
    }), 2);
  }
  if (n !== e.target_ && typeof r.value == "function") {
    var s;
    if (mi(r.value)) {
      var l, c = (l = this.options_) != null && l.autoBind ? Je.bound : Je;
      return c.make_(e, t, r, n);
    }
    var d = (s = this.options_) != null && s.autoBind ? bt.bound : bt;
    return d.make_(e, t, r, n);
  }
  var u = ((i = this.options_) == null ? void 0 : i.deep) === !1 ? C.ref : C;
  if (typeof r.value == "function" && (a = this.options_) != null && a.autoBind) {
    var v;
    r.value = r.value.bind((v = e.proxy_) != null ? v : e.target_);
  }
  return u.make_(e, t, r, n);
}
function To(e, t, r, n) {
  var i, a;
  if (r.get)
    return fr.extend_(e, t, r, n);
  if (r.set)
    return e.defineProperty_(t, {
      configurable: p.safeDescriptors ? e.isPlainObject_ : !0,
      set: Ne(t.toString(), r.set)
    }, n);
  if (typeof r.value == "function" && (i = this.options_) != null && i.autoBind) {
    var o;
    r.value = r.value.bind((o = e.proxy_) != null ? o : e.target_);
  }
  var s = ((a = this.options_) == null ? void 0 : a.deep) === !1 ? C.ref : C;
  return s.extend_(e, t, r, n);
}
function Vo(e, t) {
  f("'" + this.annotationType_ + "' cannot be used as a decorator");
}
var Io = "observable", zo = "observable.ref", Uo = "observable.shallow", Lo = "observable.struct", Si = {
  deep: !0,
  name: void 0,
  defaultDecorator: void 0,
  proxy: !0
};
Object.freeze(Si);
function Vt(e) {
  return e || Si;
}
var zr = /* @__PURE__ */ pr(Io), Mo = /* @__PURE__ */ pr(zo, {
  enhancer: ur
}), jo = /* @__PURE__ */ pr(Uo, {
  enhancer: lo
}), Ro = /* @__PURE__ */ pr(Lo, {
  enhancer: co
}), Pi = /* @__PURE__ */ $(zr);
function It(e) {
  return e.deep === !0 ? De : e.deep === !1 ? ur : Ko(e.defaultDecorator);
}
function qo(e) {
  var t;
  return e ? (t = e.defaultDecorator) != null ? t : Ai(e) : void 0;
}
function Ko(e) {
  var t, r;
  return e && (t = (r = e.options_) == null ? void 0 : r.enhancer) != null ? t : De;
}
function Di(e, t, r) {
  if (At(t))
    return zr.decorate_20223_(e, t);
  if (Pe(t)) {
    Ct(e, t, zr);
    return;
  }
  return wt(e) ? e : D(e) ? C.object(e, t, r) : Array.isArray(e) ? C.array(e, t) : Qe(e) ? C.map(e, t) : ne(e) ? C.set(e, t) : typeof e == "object" && e !== null ? e : C.box(e, t);
}
hi(Di, Pi);
var Bo = {
  box: function(t, r) {
    var n = Vt(r);
    return new Se(t, It(n), n.name, !0, n.equals);
  },
  array: function(t, r) {
    var n = Vt(r);
    return (p.useProxies === !1 || n.proxy === !1 ? js : Ps)(t, It(n), n.name);
  },
  map: function(t, r) {
    var n = Vt(r);
    return new $i(t, It(n), n.name);
  },
  set: function(t, r) {
    var n = Vt(r);
    return new ea(t, It(n), n.name);
  },
  object: function(t, r, n) {
    return Me(function() {
      return Gi(p.useProxies === !1 || n?.proxy === !1 ? et({}, n) : Cs({}, n), t, r);
    });
  },
  ref: /* @__PURE__ */ $(Mo),
  shallow: /* @__PURE__ */ $(jo),
  deep: Pi,
  struct: /* @__PURE__ */ $(Ro)
}, C = /* @__PURE__ */ hi(Di, Bo), Ni = "computed", Fo = "computed.struct", Ur = /* @__PURE__ */ Yr(Ni), Zo = /* @__PURE__ */ Yr(Fo, {
  equals: Xe.structural
}), fr = function(t, r) {
  if (At(r))
    return Ur.decorate_20223_(t, r);
  if (Pe(r))
    return Ct(t, r, Ur);
  if (D(t))
    return $(Yr(Ni, t));
  process.env.NODE_ENV !== "production" && (k(t) || f("First argument to `computed` should be an expression."), k(r) && f("A setter as second argument is no longer supported, use `{ set: fn }` option instead"));
  var n = D(r) ? r : {};
  return n.get = t, n.name || (n.name = t.name || ""), new B(n);
};
Object.assign(fr, Ur);
fr.struct = /* @__PURE__ */ $(Zo);
var fn, vn, Jt = 0, Wo = 1, Ho = (fn = (vn = /* @__PURE__ */ Wt(function() {
}, "name")) == null ? void 0 : vn.configurable) != null ? fn : !1, hn = {
  value: "action",
  configurable: !0,
  writable: !1,
  enumerable: !1
};
function Ne(e, t, r, n) {
  r === void 0 && (r = !1), process.env.NODE_ENV !== "production" && (k(t) || f("`action` can only be invoked on functions"), (typeof e != "string" || !e) && f("actions should have valid names, got: '" + e + "'"));
  function i() {
    return Ti(e, r, t, n || this, arguments);
  }
  return i.isMobxAction = !0, i.toString = function() {
    return t.toString();
  }, Ho && (hn.value = e, _(i, "name", hn)), i;
}
function Ti(e, t, r, n, i) {
  var a = Xo(e, t, n, i);
  try {
    return r.apply(n, i);
  } catch (o) {
    throw a.error_ = o, o;
  } finally {
    Jo(a);
  }
}
function Xo(e, t, r, n) {
  var i = process.env.NODE_ENV !== "production" && P() && !!e, a = 0;
  if (process.env.NODE_ENV !== "production" && i) {
    a = Date.now();
    var o = n ? Array.from(n) : Ht;
    I({
      type: _r,
      name: e,
      object: r,
      arguments: o
    });
  }
  var s = p.trackingDerivation, l = !t || !s;
  R();
  var c = p.allowStateChanges;
  l && (Le(), c = vr(!0));
  var d = Qr(!0), u = {
    runAsAction_: l,
    prevDerivation_: s,
    prevAllowStateChanges_: c,
    prevAllowStateReads_: d,
    notifySpy_: i,
    startTime_: a,
    actionId_: Wo++,
    parentActionId_: Jt
  };
  return Jt = u.actionId_, u;
}
function Jo(e) {
  Jt !== e.actionId_ && f(30), Jt = e.parentActionId_, e.error_ !== void 0 && (p.suppressReactionErrors = !0), hr(e.prevAllowStateChanges_), ft(e.prevAllowStateReads_), q(), e.runAsAction_ && oe(e.prevDerivation_), process.env.NODE_ENV !== "production" && e.notifySpy_ && z({
    time: Date.now() - e.startTime_
  }), p.suppressReactionErrors = !1;
}
function Go(e, t) {
  var r = vr(e);
  try {
    return t();
  } finally {
    hr(r);
  }
}
function vr(e) {
  var t = p.allowStateChanges;
  return p.allowStateChanges = e, t;
}
function hr(e) {
  p.allowStateChanges = e;
}
var Yo = "create", Se = /* @__PURE__ */ function(e) {
  function t(n, i, a, o, s) {
    var l;
    if (a === void 0 && (a = process.env.NODE_ENV !== "production" ? "ObservableValue@" + F() : "ObservableValue"), o === void 0 && (o = !0), s === void 0 && (s = Xe.default), l = e.call(this, a) || this, l.enhancer = void 0, l.name_ = void 0, l.equals = void 0, l.hasUnreportedChange_ = !1, l.interceptors_ = void 0, l.changeListeners_ = void 0, l.value_ = void 0, l.dehancer = void 0, l.enhancer = i, l.name_ = a, l.equals = s, l.value_ = i(n, void 0, a), process.env.NODE_ENV !== "production" && o && P()) {
      var c;
      Te({
        type: Yo,
        object: l,
        observableKind: "value",
        debugObjectName: l.name_,
        newValue: "" + ((c = l.value_) == null ? void 0 : c.toString())
      });
    }
    return l;
  }
  xi(t, e);
  var r = t.prototype;
  return r.dehanceValue = function(i) {
    return this.dehancer !== void 0 ? this.dehancer(i) : i;
  }, r.set = function(i) {
    var a = this.value_;
    if (i = this.prepareNewValue_(i), i !== p.UNCHANGED) {
      var o = P();
      process.env.NODE_ENV !== "production" && o && I({
        type: Z,
        object: this,
        observableKind: "value",
        debugObjectName: this.name_,
        newValue: i,
        oldValue: a
      }), this.setNewValue_(i), process.env.NODE_ENV !== "production" && o && z();
    }
  }, r.prepareNewValue_ = function(i) {
    if (Q(this), M(this)) {
      var a = j(this, {
        object: this,
        type: Z,
        newValue: i
      });
      if (!a)
        return p.UNCHANGED;
      i = a.newValue;
    }
    return i = this.enhancer(i, this.value_, this.name_), this.equals(this.value_, i) ? p.UNCHANGED : i;
  }, r.setNewValue_ = function(i) {
    var a = this.value_;
    this.value_ = i, this.reportChanged(), H(this) && X(this, {
      type: Z,
      object: this,
      newValue: i,
      oldValue: a
    });
  }, r.get = function() {
    return this.reportObserved(), this.dehanceValue(this.value_);
  }, r.intercept_ = function(i) {
    return Pt(this, i);
  }, r.observe_ = function(i, a) {
    return a && i({
      observableKind: "value",
      debugObjectName: this.name_,
      object: this,
      type: Z,
      newValue: this.value_,
      oldValue: void 0
    }), Dt(this, i);
  }, r.raw = function() {
    return this.value_;
  }, r.toJSON = function() {
    return this.get();
  }, r.toString = function() {
    return this.name_ + "[" + this.value_ + "]";
  }, r.valueOf = function() {
    return wi(this.get());
  }, r[Symbol.toPrimitive] = function() {
    return this.valueOf();
  }, t;
}(me), B = /* @__PURE__ */ function() {
  function e(r) {
    this.dependenciesState_ = w.NOT_TRACKING_, this.observing_ = [], this.newObserving_ = null, this.observers_ = /* @__PURE__ */ new Set(), this.runId_ = 0, this.lastAccessedBy_ = 0, this.lowestObserverState_ = w.UP_TO_DATE_, this.unboundDepsCount_ = 0, this.value_ = new Gt(null), this.name_ = void 0, this.triggeredBy_ = void 0, this.flags_ = 0, this.derivation = void 0, this.setter_ = void 0, this.isTracing_ = K.NONE, this.scope_ = void 0, this.equals_ = void 0, this.requiresReaction_ = void 0, this.keepAlive_ = void 0, this.onBOL = void 0, this.onBUOL = void 0, r.get || f(31), this.derivation = r.get, this.name_ = r.name || (process.env.NODE_ENV !== "production" ? "ComputedValue@" + F() : "ComputedValue"), r.set && (this.setter_ = Ne(process.env.NODE_ENV !== "production" ? this.name_ + "-setter" : "ComputedValue-setter", r.set)), this.equals_ = r.equals || (r.compareStructural || r.struct ? Xe.structural : Xe.default), this.scope_ = r.context, this.requiresReaction_ = r.requiresReaction, this.keepAlive_ = !!r.keepAlive;
  }
  var t = e.prototype;
  return t.onBecomeStale_ = function() {
    rs(this);
  }, t.onBO = function() {
    this.onBOL && this.onBOL.forEach(function(n) {
      return n();
    });
  }, t.onBUO = function() {
    this.onBUOL && this.onBUOL.forEach(function(n) {
      return n();
    });
  }, t.get = function() {
    if (this.isComputing && f(32, this.name_, this.derivation), p.inBatch === 0 && // !globalState.trackingDerivatpion &&
    this.observers_.size === 0 && !this.keepAlive_)
      Lr(this) && (this.warnAboutUntrackedRead_(), R(), this.value_ = this.computeValue_(!1), q());
    else if (Mi(this), Lr(this)) {
      var n = p.trackingContext;
      this.keepAlive_ && !n && (p.trackingContext = this), this.trackAndCompute() && ts(this), p.trackingContext = n;
    }
    var i = this.value_;
    if (qt(i))
      throw i.cause;
    return i;
  }, t.set = function(n) {
    if (this.setter_) {
      this.isRunningSetter && f(33, this.name_), this.isRunningSetter = !0;
      try {
        this.setter_.call(this.scope_, n);
      } finally {
        this.isRunningSetter = !1;
      }
    } else
      f(34, this.name_);
  }, t.trackAndCompute = function() {
    var n = this.value_, i = (
      /* see #1208 */
      this.dependenciesState_ === w.NOT_TRACKING_
    ), a = this.computeValue_(!0), o = i || qt(n) || qt(a) || !this.equals_(n, a);
    return o && (this.value_ = a, process.env.NODE_ENV !== "production" && P() && Te({
      observableKind: "computed",
      debugObjectName: this.name_,
      object: this.scope_,
      type: "update",
      oldValue: n,
      newValue: a
    })), o;
  }, t.computeValue_ = function(n) {
    this.isComputing = !0;
    var i = vr(!1), a;
    if (n)
      a = Vi(this, this.derivation, this.scope_);
    else if (p.disableErrorBoundaries === !0)
      a = this.derivation.call(this.scope_);
    else
      try {
        a = this.derivation.call(this.scope_);
      } catch (o) {
        a = new Gt(o);
      }
    return hr(i), this.isComputing = !1, a;
  }, t.suspend_ = function() {
    this.keepAlive_ || (Mr(this), this.value_ = void 0, process.env.NODE_ENV !== "production" && this.isTracing_ !== K.NONE && console.log("[mobx.trace] Computed value '" + this.name_ + "' was suspended and it will recompute on the next access."));
  }, t.observe_ = function(n, i) {
    var a = this, o = !0, s = void 0;
    return Wi(function() {
      var l = a.get();
      if (!o || i) {
        var c = Le();
        n({
          observableKind: "computed",
          debugObjectName: a.name_,
          type: Z,
          object: a,
          newValue: l,
          oldValue: s
        }), oe(c);
      }
      o = !1, s = l;
    });
  }, t.warnAboutUntrackedRead_ = function() {
    process.env.NODE_ENV !== "production" && (this.isTracing_ !== K.NONE && console.log("[mobx.trace] Computed value '" + this.name_ + "' is being read outside a reactive context. Doing a full recompute."), (typeof this.requiresReaction_ == "boolean" ? this.requiresReaction_ : p.computedRequiresReaction) && console.warn("[mobx] Computed value '" + this.name_ + "' is being read outside a reactive context. Doing a full recompute."));
  }, t.toString = function() {
    return this.name_ + "[" + this.derivation.toString() + "]";
  }, t.valueOf = function() {
    return wi(this.get());
  }, t[Symbol.toPrimitive] = function() {
    return this.valueOf();
  }, _e(e, [{
    key: "isComputing",
    get: function() {
      return T(this.flags_, e.isComputingMask_);
    },
    set: function(n) {
      this.flags_ = V(this.flags_, e.isComputingMask_, n);
    }
  }, {
    key: "isRunningSetter",
    get: function() {
      return T(this.flags_, e.isRunningSetterMask_);
    },
    set: function(n) {
      this.flags_ = V(this.flags_, e.isRunningSetterMask_, n);
    }
  }, {
    key: "isBeingObserved",
    get: function() {
      return T(this.flags_, e.isBeingObservedMask_);
    },
    set: function(n) {
      this.flags_ = V(this.flags_, e.isBeingObservedMask_, n);
    }
  }, {
    key: "isPendingUnobservation",
    get: function() {
      return T(this.flags_, e.isPendingUnobservationMask_);
    },
    set: function(n) {
      this.flags_ = V(this.flags_, e.isPendingUnobservationMask_, n);
    }
  }, {
    key: "diffValue",
    get: function() {
      return T(this.flags_, e.diffValueMask_) ? 1 : 0;
    },
    set: function(n) {
      this.flags_ = V(this.flags_, e.diffValueMask_, n === 1);
    }
  }]);
}();
B.isComputingMask_ = 1;
B.isRunningSetterMask_ = 2;
B.isBeingObservedMask_ = 4;
B.isPendingUnobservationMask_ = 8;
B.diffValueMask_ = 16;
var gr = /* @__PURE__ */ Ue("ComputedValue", B), w;
(function(e) {
  e[e.NOT_TRACKING_ = -1] = "NOT_TRACKING_", e[e.UP_TO_DATE_ = 0] = "UP_TO_DATE_", e[e.POSSIBLY_STALE_ = 1] = "POSSIBLY_STALE_", e[e.STALE_ = 2] = "STALE_";
})(w || (w = {}));
var K;
(function(e) {
  e[e.NONE = 0] = "NONE", e[e.LOG = 1] = "LOG", e[e.BREAK = 2] = "BREAK";
})(K || (K = {}));
var Gt = function(t) {
  this.cause = void 0, this.cause = t;
};
function qt(e) {
  return e instanceof Gt;
}
function Lr(e) {
  switch (e.dependenciesState_) {
    case w.UP_TO_DATE_:
      return !1;
    case w.NOT_TRACKING_:
    case w.STALE_:
      return !0;
    case w.POSSIBLY_STALE_: {
      for (var t = Qr(!0), r = Le(), n = e.observing_, i = n.length, a = 0; a < i; a++) {
        var o = n[a];
        if (gr(o)) {
          if (p.disableErrorBoundaries)
            o.get();
          else
            try {
              o.get();
            } catch {
              return oe(r), ft(t), !0;
            }
          if (e.dependenciesState_ === w.STALE_)
            return oe(r), ft(t), !0;
        }
      }
      return zi(e), oe(r), ft(t), !1;
    }
  }
}
function Q(e) {
  if (process.env.NODE_ENV !== "production") {
    var t = e.observers_.size > 0;
    !p.allowStateChanges && (t || p.enforceActions === "always") && console.warn("[MobX] " + (p.enforceActions ? "Since strict-mode is enabled, changing (observed) observable values without using an action is not allowed. Tried to modify: " : "Side effects like changing state are not allowed at this point. Are you trying to modify state from, for example, a computed value or the render function of a React component? You can wrap side effects in 'runInAction' (or decorate functions with 'action') if needed. Tried to modify: ") + e.name_);
  }
}
function Qo(e) {
  process.env.NODE_ENV !== "production" && !p.allowStateReads && p.observableRequiresReaction && console.warn("[mobx] Observable '" + e.name_ + "' being read outside a reactive context.");
}
function Vi(e, t, r) {
  var n = Qr(!0);
  zi(e), e.newObserving_ = new Array(
    // Reserve constant space for initial dependencies, dynamic space otherwise.
    // See https://github.com/mobxjs/mobx/pull/3833
    e.runId_ === 0 ? 100 : e.observing_.length
  ), e.unboundDepsCount_ = 0, e.runId_ = ++p.runId;
  var i = p.trackingDerivation;
  p.trackingDerivation = e, p.inBatch++;
  var a;
  if (p.disableErrorBoundaries === !0)
    a = t.call(r);
  else
    try {
      a = t.call(r);
    } catch (o) {
      a = new Gt(o);
    }
  return p.inBatch--, p.trackingDerivation = i, $o(e), _o(e), ft(n), a;
}
function _o(e) {
  process.env.NODE_ENV !== "production" && e.observing_.length === 0 && (typeof e.requiresObservable_ == "boolean" ? e.requiresObservable_ : p.reactionRequiresObservable) && console.warn("[mobx] Derivation '" + e.name_ + "' is created/updated without reading any observable value.");
}
function $o(e) {
  for (var t = e.observing_, r = e.observing_ = e.newObserving_, n = w.UP_TO_DATE_, i = 0, a = e.unboundDepsCount_, o = 0; o < a; o++) {
    var s = r[o];
    s.diffValue === 0 && (s.diffValue = 1, i !== o && (r[i] = s), i++), s.dependenciesState_ > n && (n = s.dependenciesState_);
  }
  for (r.length = i, e.newObserving_ = null, a = t.length; a--; ) {
    var l = t[a];
    l.diffValue === 0 && Ui(l, e), l.diffValue = 0;
  }
  for (; i--; ) {
    var c = r[i];
    c.diffValue === 1 && (c.diffValue = 0, es(c, e));
  }
  n !== w.UP_TO_DATE_ && (e.dependenciesState_ = n, e.onBecomeStale_());
}
function Mr(e) {
  var t = e.observing_;
  e.observing_ = [];
  for (var r = t.length; r--; )
    Ui(t[r], e);
  e.dependenciesState_ = w.NOT_TRACKING_;
}
function Ii(e) {
  var t = Le();
  try {
    return e();
  } finally {
    oe(t);
  }
}
function Le() {
  var e = p.trackingDerivation;
  return p.trackingDerivation = null, e;
}
function oe(e) {
  p.trackingDerivation = e;
}
function Qr(e) {
  var t = p.allowStateReads;
  return p.allowStateReads = e, t;
}
function ft(e) {
  p.allowStateReads = e;
}
function zi(e) {
  if (e.dependenciesState_ !== w.UP_TO_DATE_) {
    e.dependenciesState_ = w.UP_TO_DATE_;
    for (var t = e.observing_, r = t.length; r--; )
      t[r].lowestObserverState_ = w.UP_TO_DATE_;
  }
}
var xr = function() {
  this.version = 6, this.UNCHANGED = {}, this.trackingDerivation = null, this.trackingContext = null, this.runId = 0, this.mobxGuid = 0, this.inBatch = 0, this.pendingUnobservations = [], this.pendingReactions = [], this.isRunningReactions = !1, this.allowStateChanges = !1, this.allowStateReads = !0, this.enforceActions = !0, this.spyListeners = [], this.globalReactionErrorHandlers = [], this.computedRequiresReaction = !1, this.reactionRequiresObservable = !1, this.observableRequiresReaction = !1, this.disableErrorBoundaries = !1, this.suppressReactionErrors = !1, this.useProxies = !0, this.verifyProxies = !1, this.safeDescriptors = !0;
}, Or = !0, p = /* @__PURE__ */ function() {
  var e = /* @__PURE__ */ Hr();
  return e.__mobxInstanceCount > 0 && !e.__mobxGlobals && (Or = !1), e.__mobxGlobals && e.__mobxGlobals.version !== new xr().version && (Or = !1), Or ? e.__mobxGlobals ? (e.__mobxInstanceCount += 1, e.__mobxGlobals.UNCHANGED || (e.__mobxGlobals.UNCHANGED = {}), e.__mobxGlobals) : (e.__mobxInstanceCount = 1, e.__mobxGlobals = /* @__PURE__ */ new xr()) : (setTimeout(function() {
    f(35);
  }, 1), new xr());
}();
function es(e, t) {
  e.observers_.add(t), e.lowestObserverState_ > t.dependenciesState_ && (e.lowestObserverState_ = t.dependenciesState_);
}
function Ui(e, t) {
  e.observers_.delete(t), e.observers_.size === 0 && Li(e);
}
function Li(e) {
  e.isPendingUnobservation === !1 && (e.isPendingUnobservation = !0, p.pendingUnobservations.push(e));
}
function R() {
  p.inBatch++;
}
function q() {
  if (--p.inBatch === 0) {
    Ki();
    for (var e = p.pendingUnobservations, t = 0; t < e.length; t++) {
      var r = e[t];
      r.isPendingUnobservation = !1, r.observers_.size === 0 && (r.isBeingObserved && (r.isBeingObserved = !1, r.onBUO()), r instanceof B && r.suspend_());
    }
    p.pendingUnobservations = [];
  }
}
function Mi(e) {
  Qo(e);
  var t = p.trackingDerivation;
  return t !== null ? (t.runId_ !== e.lastAccessedBy_ && (e.lastAccessedBy_ = t.runId_, t.newObserving_[t.unboundDepsCount_++] = e, !e.isBeingObserved && p.trackingContext && (e.isBeingObserved = !0, e.onBO())), e.isBeingObserved) : (e.observers_.size === 0 && p.inBatch > 0 && Li(e), !1);
}
function ji(e) {
  e.lowestObserverState_ !== w.STALE_ && (e.lowestObserverState_ = w.STALE_, e.observers_.forEach(function(t) {
    t.dependenciesState_ === w.UP_TO_DATE_ && (process.env.NODE_ENV !== "production" && t.isTracing_ !== K.NONE && Ri(t, e), t.onBecomeStale_()), t.dependenciesState_ = w.STALE_;
  }));
}
function ts(e) {
  e.lowestObserverState_ !== w.STALE_ && (e.lowestObserverState_ = w.STALE_, e.observers_.forEach(function(t) {
    t.dependenciesState_ === w.POSSIBLY_STALE_ ? (t.dependenciesState_ = w.STALE_, process.env.NODE_ENV !== "production" && t.isTracing_ !== K.NONE && Ri(t, e)) : t.dependenciesState_ === w.UP_TO_DATE_ && (e.lowestObserverState_ = w.UP_TO_DATE_);
  }));
}
function rs(e) {
  e.lowestObserverState_ === w.UP_TO_DATE_ && (e.lowestObserverState_ = w.POSSIBLY_STALE_, e.observers_.forEach(function(t) {
    t.dependenciesState_ === w.UP_TO_DATE_ && (t.dependenciesState_ = w.POSSIBLY_STALE_, t.onBecomeStale_());
  }));
}
function Ri(e, t) {
  if (console.log("[mobx.trace] '" + e.name_ + "' is invalidated due to a change in: '" + t.name_ + "'"), e.isTracing_ === K.BREAK) {
    var r = [];
    qi(ms(e), r, 1), new Function(`debugger;
/*
Tracing '` + e.name_ + `'

You are entering this break point because derivation '` + e.name_ + "' is being traced and '" + t.name_ + `' is now forcing it to update.
Just follow the stacktrace you should now see in the devtools to see precisely what piece of your code is causing this update
The stackframe you are looking for is at least ~6-8 stack-frames up.

` + (e instanceof B ? e.derivation.toString().replace(/[*]\//g, "/") : "") + `

The dependencies for this derivation are:

` + r.join(`
`) + `
*/
    `)();
  }
}
function qi(e, t, r) {
  if (t.length >= 1e3) {
    t.push("(and many more)");
    return;
  }
  t.push("" + "	".repeat(r - 1) + e.name), e.dependencies && e.dependencies.forEach(function(n) {
    return qi(n, t, r + 1);
  });
}
var ee = /* @__PURE__ */ function() {
  function e(r, n, i, a) {
    r === void 0 && (r = process.env.NODE_ENV !== "production" ? "Reaction@" + F() : "Reaction"), this.name_ = void 0, this.onInvalidate_ = void 0, this.errorHandler_ = void 0, this.requiresObservable_ = void 0, this.observing_ = [], this.newObserving_ = [], this.dependenciesState_ = w.NOT_TRACKING_, this.runId_ = 0, this.unboundDepsCount_ = 0, this.flags_ = 0, this.isTracing_ = K.NONE, this.name_ = r, this.onInvalidate_ = n, this.errorHandler_ = i, this.requiresObservable_ = a;
  }
  var t = e.prototype;
  return t.onBecomeStale_ = function() {
    this.schedule_();
  }, t.schedule_ = function() {
    this.isScheduled || (this.isScheduled = !0, p.pendingReactions.push(this), Ki());
  }, t.runReaction_ = function() {
    if (!this.isDisposed) {
      R(), this.isScheduled = !1;
      var n = p.trackingContext;
      if (p.trackingContext = this, Lr(this)) {
        this.isTrackPending = !0;
        try {
          this.onInvalidate_(), process.env.NODE_ENV !== "production" && this.isTrackPending && P() && Te({
            name: this.name_,
            type: "scheduled-reaction"
          });
        } catch (i) {
          this.reportExceptionInDerivation_(i);
        }
      }
      p.trackingContext = n, q();
    }
  }, t.track = function(n) {
    if (!this.isDisposed) {
      R();
      var i = P(), a;
      process.env.NODE_ENV !== "production" && i && (a = Date.now(), I({
        name: this.name_,
        type: "reaction"
      })), this.isRunning = !0;
      var o = p.trackingContext;
      p.trackingContext = this;
      var s = Vi(this, n, void 0);
      p.trackingContext = o, this.isRunning = !1, this.isTrackPending = !1, this.isDisposed && Mr(this), qt(s) && this.reportExceptionInDerivation_(s.cause), process.env.NODE_ENV !== "production" && i && z({
        time: Date.now() - a
      }), q();
    }
  }, t.reportExceptionInDerivation_ = function(n) {
    var i = this;
    if (this.errorHandler_) {
      this.errorHandler_(n, this);
      return;
    }
    if (p.disableErrorBoundaries)
      throw n;
    var a = process.env.NODE_ENV !== "production" ? "[mobx] Encountered an uncaught exception that was thrown by a reaction or observer component, in: '" + this + "'" : "[mobx] uncaught error in '" + this + "'";
    p.suppressReactionErrors ? process.env.NODE_ENV !== "production" && console.warn("[mobx] (error in reaction '" + this.name_ + "' suppressed, fix error of causing action below)") : console.error(a, n), process.env.NODE_ENV !== "production" && P() && Te({
      type: "error",
      name: this.name_,
      message: a,
      error: "" + n
    }), p.globalReactionErrorHandlers.forEach(function(o) {
      return o(n, i);
    });
  }, t.dispose = function() {
    this.isDisposed || (this.isDisposed = !0, this.isRunning || (R(), Mr(this), q()));
  }, t.getDisposer_ = function(n) {
    var i = this, a = function o() {
      i.dispose(), n == null || n.removeEventListener == null || n.removeEventListener("abort", o);
    };
    return n == null || n.addEventListener == null || n.addEventListener("abort", a), a[g] = this, "dispose" in Symbol && typeof Symbol.dispose == "symbol" && (a[Symbol.dispose] = a), a;
  }, t.toString = function() {
    return "Reaction[" + this.name_ + "]";
  }, t.trace = function(n) {
    n === void 0 && (n = !1), Os(this, n);
  }, _e(e, [{
    key: "isDisposed",
    get: function() {
      return T(this.flags_, e.isDisposedMask_);
    },
    set: function(n) {
      this.flags_ = V(this.flags_, e.isDisposedMask_, n);
    }
  }, {
    key: "isScheduled",
    get: function() {
      return T(this.flags_, e.isScheduledMask_);
    },
    set: function(n) {
      this.flags_ = V(this.flags_, e.isScheduledMask_, n);
    }
  }, {
    key: "isTrackPending",
    get: function() {
      return T(this.flags_, e.isTrackPendingMask_);
    },
    set: function(n) {
      this.flags_ = V(this.flags_, e.isTrackPendingMask_, n);
    }
  }, {
    key: "isRunning",
    get: function() {
      return T(this.flags_, e.isRunningMask_);
    },
    set: function(n) {
      this.flags_ = V(this.flags_, e.isRunningMask_, n);
    }
  }, {
    key: "diffValue",
    get: function() {
      return T(this.flags_, e.diffValueMask_) ? 1 : 0;
    },
    set: function(n) {
      this.flags_ = V(this.flags_, e.diffValueMask_, n === 1);
    }
  }]);
}();
ee.isDisposedMask_ = 1;
ee.isScheduledMask_ = 2;
ee.isTrackPendingMask_ = 4;
ee.isRunningMask_ = 8;
ee.diffValueMask_ = 16;
function ns(e) {
  return p.globalReactionErrorHandlers.push(e), function() {
    var t = p.globalReactionErrorHandlers.indexOf(e);
    t >= 0 && p.globalReactionErrorHandlers.splice(t, 1);
  };
}
var gn = 100, is = function(t) {
  return t();
};
function Ki() {
  p.inBatch > 0 || p.isRunningReactions || is(as);
}
function as() {
  p.isRunningReactions = !0;
  for (var e = p.pendingReactions, t = 0; e.length > 0; ) {
    ++t === gn && (console.error(process.env.NODE_ENV !== "production" ? "Reaction doesn't converge to a stable state after " + gn + " iterations." + (" Probably there is a cycle in the reactive function: " + e[0]) : "[mobx] cycle in reaction: " + e[0]), e.splice(0));
    for (var r = e.splice(0), n = 0, i = r.length; n < i; n++)
      r[n].runReaction_();
  }
  p.isRunningReactions = !1;
}
var Yt = /* @__PURE__ */ Ue("Reaction", ee);
function P() {
  return process.env.NODE_ENV !== "production" && !!p.spyListeners.length;
}
function Te(e) {
  if (process.env.NODE_ENV !== "production" && p.spyListeners.length)
    for (var t = p.spyListeners, r = 0, n = t.length; r < n; r++)
      t[r](e);
}
function I(e) {
  if (process.env.NODE_ENV !== "production") {
    var t = ve({}, e, {
      spyReportStart: !0
    });
    Te(t);
  }
}
var os = {
  type: "report-end",
  spyReportEnd: !0
};
function z(e) {
  process.env.NODE_ENV !== "production" && Te(e ? ve({}, e, {
    type: "report-end",
    spyReportEnd: !0
  }) : os);
}
function ss(e) {
  return process.env.NODE_ENV === "production" ? (console.warn("[mobx.spy] Is a no-op in production builds"), function() {
  }) : (p.spyListeners.push(e), Jr(function() {
    p.spyListeners = p.spyListeners.filter(function(t) {
      return t !== e;
    });
  }));
}
var _r = "action", ls = "action.bound", Bi = "autoAction", cs = "autoAction.bound", Fi = "<unnamed action>", jr = /* @__PURE__ */ St(_r), ds = /* @__PURE__ */ St(ls, {
  bound: !0
}), Rr = /* @__PURE__ */ St(Bi, {
  autoAction: !0
}), us = /* @__PURE__ */ St(cs, {
  autoAction: !0,
  bound: !0
});
function Zi(e) {
  var t = function(n, i) {
    if (k(n))
      return Ne(n.name || Fi, n, e);
    if (k(i))
      return Ne(n, i, e);
    if (At(i))
      return (e ? Rr : jr).decorate_20223_(n, i);
    if (Pe(i))
      return Ct(n, i, e ? Rr : jr);
    if (Pe(n))
      return $(St(e ? Bi : _r, {
        name: n,
        autoAction: e
      }));
    process.env.NODE_ENV !== "production" && f("Invalid arguments for `action`");
  };
  return t;
}
var Ce = /* @__PURE__ */ Zi(!1);
Object.assign(Ce, jr);
var bt = /* @__PURE__ */ Zi(!0);
Object.assign(bt, Rr);
Ce.bound = /* @__PURE__ */ $(ds);
bt.bound = /* @__PURE__ */ $(us);
function Dd(e) {
  return Ti(e.name || Fi, !1, e, this, void 0);
}
function Ve(e) {
  return k(e) && e.isMobxAction === !0;
}
function Wi(e, t) {
  var r, n, i, a;
  t === void 0 && (t = Xr), process.env.NODE_ENV !== "production" && (k(e) || f("Autorun expects a function as first argument"), Ve(e) && f("Autorun does not accept actions since actions are untrackable"));
  var o = (r = (n = t) == null ? void 0 : n.name) != null ? r : process.env.NODE_ENV !== "production" ? e.name || "Autorun@" + F() : "Autorun", s = !t.scheduler && !t.delay, l;
  if (s)
    l = new ee(o, function() {
      this.track(u);
    }, t.onError, t.requiresObservable);
  else {
    var c = Hi(t), d = !1;
    l = new ee(o, function() {
      d || (d = !0, c(function() {
        d = !1, l.isDisposed || l.track(u);
      }));
    }, t.onError, t.requiresObservable);
  }
  function u() {
    e(l);
  }
  return (i = t) != null && (i = i.signal) != null && i.aborted || l.schedule_(), l.getDisposer_((a = t) == null ? void 0 : a.signal);
}
var ps = function(t) {
  return t();
};
function Hi(e) {
  return e.scheduler ? e.scheduler : e.delay ? function(t) {
    return setTimeout(t, e.delay);
  } : ps;
}
function $r(e, t, r) {
  var n, i, a;
  r === void 0 && (r = Xr), process.env.NODE_ENV !== "production" && ((!k(e) || !k(t)) && f("First and second argument to reaction should be functions"), D(r) || f("Third argument of reactions should be an object"));
  var o = (n = r.name) != null ? n : process.env.NODE_ENV !== "production" ? "Reaction@" + F() : "Reaction", s = Ce(o, r.onError ? fs(r.onError, t) : t), l = !r.scheduler && !r.delay, c = Hi(r), d = !0, u = !1, v, h = r.compareStructural ? Xe.structural : r.equals || Xe.default, m = new ee(o, function() {
    d || l ? O() : u || (u = !0, c(O));
  }, r.onError, r.requiresObservable);
  function O() {
    if (u = !1, !m.isDisposed) {
      var A = !1, G = v;
      m.track(function() {
        var je = Go(!1, function() {
          return e(m);
        });
        A = d || !h(v, je), v = je;
      }), (d && r.fireImmediately || !d && A) && s(v, G, m), d = !1;
    }
  }
  return (i = r) != null && (i = i.signal) != null && i.aborted || m.schedule_(), m.getDisposer_((a = r) == null ? void 0 : a.signal);
}
function fs(e, t) {
  return function() {
    try {
      return t.apply(this, arguments);
    } catch (r) {
      e.call(this, r);
    }
  };
}
var vs = "onBO", hs = "onBUO";
function gs(e, t, r) {
  return Ji(vs, e, t, r);
}
function Xi(e, t, r) {
  return Ji(hs, e, t, r);
}
function Ji(e, t, r, n) {
  var i = Ge(t), a = k(n) ? n : r, o = e + "L";
  return i[o] ? i[o].add(a) : i[o] = /* @__PURE__ */ new Set([a]), function() {
    var s = i[o];
    s && (s.delete(a), s.size === 0 && delete i[o]);
  };
}
function Gi(e, t, r, n) {
  process.env.NODE_ENV !== "production" && (arguments.length > 4 && f("'extendObservable' expected 2-4 arguments"), typeof e != "object" && f("'extendObservable' expects an object as first argument"), be(e) && f("'extendObservable' should not be used on maps, use map.merge instead"), D(t) || f("'extendObservable' only accepts plain objects as second argument"), (wt(t) || wt(r)) && f("Extending an object with another observable (object) is not supported"));
  var i = eo(t);
  return Me(function() {
    var a = et(e, n)[g];
    mt(i).forEach(function(o) {
      a.extend_(
        o,
        i[o],
        // must pass "undefined" for { key: undefined }
        r && o in r ? r[o] : !0
      );
    });
  }), e;
}
function ms(e, t) {
  return Yi(Ge(e, t));
}
function Yi(e) {
  var t = {
    name: e.name_
  };
  return e.observing_ && e.observing_.length > 0 && (t.dependencies = bs(e.observing_).map(Yi)), t;
}
function bs(e) {
  return Array.from(new Set(e));
}
var ys = 0;
function Qi() {
  this.message = "FLOW_CANCELLED";
}
Qi.prototype = /* @__PURE__ */ Object.create(Error.prototype);
var Er = /* @__PURE__ */ ki("flow"), ws = /* @__PURE__ */ ki("flow.bound", {
  bound: !0
}), Je = /* @__PURE__ */ Object.assign(function(t, r) {
  if (At(r))
    return Er.decorate_20223_(t, r);
  if (Pe(r))
    return Ct(t, r, Er);
  process.env.NODE_ENV !== "production" && arguments.length !== 1 && f("Flow expects single argument with generator function");
  var n = t, i = n.name || "<unnamed flow>", a = function() {
    var s = this, l = arguments, c = ++ys, d = Ce(i + " - runid: " + c + " - init", n).apply(s, l), u, v = void 0, h = new Promise(function(m, O) {
      var A = 0;
      u = O;
      function G(N) {
        v = void 0;
        var de;
        try {
          de = Ce(i + " - runid: " + c + " - yield " + A++, d.next).call(d, N);
        } catch (we) {
          return O(we);
        }
        it(de);
      }
      function je(N) {
        v = void 0;
        var de;
        try {
          de = Ce(i + " - runid: " + c + " - yield " + A++, d.throw).call(d, N);
        } catch (we) {
          return O(we);
        }
        it(de);
      }
      function it(N) {
        if (k(N?.then)) {
          N.then(it, O);
          return;
        }
        return N.done ? m(N.value) : (v = Promise.resolve(N.value), v.then(G, je));
      }
      G(void 0);
    });
    return h.cancel = Ce(i + " - runid: " + c + " - cancel", function() {
      try {
        v && mn(v);
        var m = d.return(void 0), O = Promise.resolve(m.value);
        O.then(Fe, Fe), mn(O), u(new Qi());
      } catch (A) {
        u(A);
      }
    }), h;
  };
  return a.isMobXFlow = !0, a;
}, Er);
Je.bound = /* @__PURE__ */ $(ws);
function mn(e) {
  k(e.cancel) && e.cancel();
}
function yt(e) {
  return e?.isMobXFlow === !0;
}
function xs(e, t) {
  return e ? tt(e) || !!e[g] || Gr(e) || Yt(e) || gr(e) : !1;
}
function wt(e) {
  return process.env.NODE_ENV !== "production" && arguments.length !== 1 && f("isObservable expects only 1 argument. Use isObservableProp to inspect the observability of a property"), xs(e);
}
function Os() {
  if (process.env.NODE_ENV !== "production") {
    for (var e = !1, t = arguments.length, r = new Array(t), n = 0; n < t; n++)
      r[n] = arguments[n];
    typeof r[r.length - 1] == "boolean" && (e = r.pop());
    var i = Es(r);
    if (!i)
      return f("'trace(break?)' can only be used inside a tracked computed value or a Reaction. Consider passing in the computed value or reaction explicitly");
    i.isTracing_ === K.NONE && console.log("[mobx.trace] '" + i.name_ + "' tracing enabled"), i.isTracing_ = e ? K.BREAK : K.LOG;
  }
}
function Es(e) {
  switch (e.length) {
    case 0:
      return p.trackingDerivation;
    case 1:
      return Ge(e[0]);
    case 2:
      return Ge(e[0], e[1]);
  }
}
function ae(e, t) {
  t === void 0 && (t = void 0), R();
  try {
    return e.apply(t);
  } finally {
    q();
  }
}
function xe(e) {
  return e[g];
}
var ks = {
  has: function(t, r) {
    return process.env.NODE_ENV !== "production" && p.trackingDerivation && at("detect new properties using the 'in' operator. Use 'has' from 'mobx' instead."), xe(t).has_(r);
  },
  get: function(t, r) {
    return xe(t).get_(r);
  },
  set: function(t, r, n) {
    var i;
    return Pe(r) ? (process.env.NODE_ENV !== "production" && !xe(t).values_.has(r) && at("add a new observable property through direct assignment. Use 'set' from 'mobx' instead."), (i = xe(t).set_(r, n, !0)) != null ? i : !0) : !1;
  },
  deleteProperty: function(t, r) {
    var n;
    return process.env.NODE_ENV !== "production" && at("delete properties from an observable object. Use 'remove' from 'mobx' instead."), Pe(r) ? (n = xe(t).delete_(r, !0)) != null ? n : !0 : !1;
  },
  defineProperty: function(t, r, n) {
    var i;
    return process.env.NODE_ENV !== "production" && at("define property on an observable object. Use 'defineProperty' from 'mobx' instead."), (i = xe(t).defineProperty_(r, n)) != null ? i : !0;
  },
  ownKeys: function(t) {
    return process.env.NODE_ENV !== "production" && p.trackingDerivation && at("iterate keys to detect added / removed properties. Use 'keys' from 'mobx' instead."), xe(t).ownKeys_();
  },
  preventExtensions: function(t) {
    f(13);
  }
};
function Cs(e, t) {
  var r, n;
  return gi(), e = et(e, t), (n = (r = e[g]).proxy_) != null ? n : r.proxy_ = new Proxy(e, ks);
}
function M(e) {
  return e.interceptors_ !== void 0 && e.interceptors_.length > 0;
}
function Pt(e, t) {
  var r = e.interceptors_ || (e.interceptors_ = []);
  return r.push(t), Jr(function() {
    var n = r.indexOf(t);
    n !== -1 && r.splice(n, 1);
  });
}
function j(e, t) {
  var r = Le();
  try {
    for (var n = [].concat(e.interceptors_ || []), i = 0, a = n.length; i < a && (t = n[i](t), t && !t.type && f(14), !!t); i++)
      ;
    return t;
  } finally {
    oe(r);
  }
}
function H(e) {
  return e.changeListeners_ !== void 0 && e.changeListeners_.length > 0;
}
function Dt(e, t) {
  var r = e.changeListeners_ || (e.changeListeners_ = []);
  return r.push(t), Jr(function() {
    var n = r.indexOf(t);
    n !== -1 && r.splice(n, 1);
  });
}
function X(e, t) {
  var r = Le(), n = e.changeListeners_;
  if (n) {
    n = n.slice();
    for (var i = 0, a = n.length; i < a; i++)
      n[i](t);
    oe(r);
  }
}
var kr = /* @__PURE__ */ Symbol("mobx-keys");
function $e(e, t, r) {
  return process.env.NODE_ENV !== "production" && (!D(e) && !D(Object.getPrototypeOf(e)) && f("'makeAutoObservable' can only be used for classes that don't have a superclass"), tt(e) && f("makeAutoObservable can only be used on objects not already made observable")), D(e) ? Gi(e, e, t, r) : (Me(function() {
    var n = et(e, r)[g];
    if (!e[kr]) {
      var i = Object.getPrototypeOf(e), a = new Set([].concat(mt(e), mt(i)));
      a.delete("constructor"), a.delete(g), cr(i, kr, a);
    }
    e[kr].forEach(function(o) {
      return n.make_(
        o,
        // must pass "undefined" for { key: undefined }
        t && o in t ? t[o] : !0
      );
    });
  }), e);
}
var bn = "splice", Z = "update", As = 1e4, Ss = {
  get: function(t, r) {
    var n = t[g];
    return r === g ? n : r === "length" ? n.getArrayLength_() : typeof r == "string" && !isNaN(r) ? n.get_(parseInt(r)) : W(Qt, r) ? Qt[r] : t[r];
  },
  set: function(t, r, n) {
    var i = t[g];
    return r === "length" && i.setArrayLength_(n), typeof r == "symbol" || isNaN(r) ? t[r] = n : i.set_(parseInt(r), n), !0;
  },
  preventExtensions: function() {
    f(15);
  }
}, en = /* @__PURE__ */ function() {
  function e(r, n, i, a) {
    r === void 0 && (r = process.env.NODE_ENV !== "production" ? "ObservableArray@" + F() : "ObservableArray"), this.owned_ = void 0, this.legacyMode_ = void 0, this.atom_ = void 0, this.values_ = [], this.interceptors_ = void 0, this.changeListeners_ = void 0, this.enhancer_ = void 0, this.dehancer = void 0, this.proxy_ = void 0, this.lastKnownLength_ = 0, this.owned_ = i, this.legacyMode_ = a, this.atom_ = new me(r), this.enhancer_ = function(o, s) {
      return n(o, s, process.env.NODE_ENV !== "production" ? r + "[..]" : "ObservableArray[..]");
    };
  }
  var t = e.prototype;
  return t.dehanceValue_ = function(n) {
    return this.dehancer !== void 0 ? this.dehancer(n) : n;
  }, t.dehanceValues_ = function(n) {
    return this.dehancer !== void 0 && n.length > 0 ? n.map(this.dehancer) : n;
  }, t.intercept_ = function(n) {
    return Pt(this, n);
  }, t.observe_ = function(n, i) {
    return i === void 0 && (i = !1), i && n({
      observableKind: "array",
      object: this.proxy_,
      debugObjectName: this.atom_.name_,
      type: "splice",
      index: 0,
      added: this.values_.slice(),
      addedCount: this.values_.length,
      removed: [],
      removedCount: 0
    }), Dt(this, n);
  }, t.getArrayLength_ = function() {
    return this.atom_.reportObserved(), this.values_.length;
  }, t.setArrayLength_ = function(n) {
    (typeof n != "number" || isNaN(n) || n < 0) && f("Out of range: " + n);
    var i = this.values_.length;
    if (n !== i)
      if (n > i) {
        for (var a = new Array(n - i), o = 0; o < n - i; o++)
          a[o] = void 0;
        this.spliceWithArray_(i, 0, a);
      } else
        this.spliceWithArray_(n, i - n);
  }, t.updateArrayLength_ = function(n, i) {
    n !== this.lastKnownLength_ && f(16), this.lastKnownLength_ += i, this.legacyMode_ && i > 0 && na(n + i + 1);
  }, t.spliceWithArray_ = function(n, i, a) {
    var o = this;
    Q(this.atom_);
    var s = this.values_.length;
    if (n === void 0 ? n = 0 : n > s ? n = s : n < 0 && (n = Math.max(0, s + n)), arguments.length === 1 ? i = s - n : i == null ? i = 0 : i = Math.max(0, Math.min(i, s - n)), a === void 0 && (a = Ht), M(this)) {
      var l = j(this, {
        object: this.proxy_,
        type: bn,
        index: n,
        removedCount: i,
        added: a
      });
      if (!l)
        return Ht;
      i = l.removedCount, a = l.added;
    }
    if (a = a.length === 0 ? a : a.map(function(u) {
      return o.enhancer_(u, void 0);
    }), this.legacyMode_ || process.env.NODE_ENV !== "production") {
      var c = a.length - i;
      this.updateArrayLength_(s, c);
    }
    var d = this.spliceItemsIntoValues_(n, i, a);
    return (i !== 0 || a.length !== 0) && this.notifyArraySplice_(n, a, d), this.dehanceValues_(d);
  }, t.spliceItemsIntoValues_ = function(n, i, a) {
    if (a.length < As) {
      var o;
      return (o = this.values_).splice.apply(o, [n, i].concat(a));
    } else {
      var s = this.values_.slice(n, n + i), l = this.values_.slice(n + i);
      this.values_.length += a.length - i;
      for (var c = 0; c < a.length; c++)
        this.values_[n + c] = a[c];
      for (var d = 0; d < l.length; d++)
        this.values_[n + a.length + d] = l[d];
      return s;
    }
  }, t.notifyArrayChildUpdate_ = function(n, i, a) {
    var o = !this.owned_ && P(), s = H(this), l = s || o ? {
      observableKind: "array",
      object: this.proxy_,
      type: Z,
      debugObjectName: this.atom_.name_,
      index: n,
      newValue: i,
      oldValue: a
    } : null;
    process.env.NODE_ENV !== "production" && o && I(l), this.atom_.reportChanged(), s && X(this, l), process.env.NODE_ENV !== "production" && o && z();
  }, t.notifyArraySplice_ = function(n, i, a) {
    var o = !this.owned_ && P(), s = H(this), l = s || o ? {
      observableKind: "array",
      object: this.proxy_,
      debugObjectName: this.atom_.name_,
      type: bn,
      index: n,
      removed: a,
      added: i,
      removedCount: a.length,
      addedCount: i.length
    } : null;
    process.env.NODE_ENV !== "production" && o && I(l), this.atom_.reportChanged(), s && X(this, l), process.env.NODE_ENV !== "production" && o && z();
  }, t.get_ = function(n) {
    if (this.legacyMode_ && n >= this.values_.length) {
      console.warn(process.env.NODE_ENV !== "production" ? "[mobx.array] Attempt to read an array index (" + n + ") that is out of bounds (" + this.values_.length + "). Please check length first. Out of bound indices will not be tracked by MobX" : "[mobx] Out of bounds read: " + n);
      return;
    }
    return this.atom_.reportObserved(), this.dehanceValue_(this.values_[n]);
  }, t.set_ = function(n, i) {
    var a = this.values_;
    if (this.legacyMode_ && n > a.length && f(17, n, a.length), n < a.length) {
      Q(this.atom_);
      var o = a[n];
      if (M(this)) {
        var s = j(this, {
          type: Z,
          object: this.proxy_,
          // since "this" is the real array we need to pass its proxy
          index: n,
          newValue: i
        });
        if (!s)
          return;
        i = s.newValue;
      }
      i = this.enhancer_(i, o);
      var l = i !== o;
      l && (a[n] = i, this.notifyArrayChildUpdate_(n, i, o));
    } else {
      for (var c = new Array(n + 1 - a.length), d = 0; d < c.length - 1; d++)
        c[d] = void 0;
      c[c.length - 1] = i, this.spliceWithArray_(a.length, 0, c);
    }
  }, e;
}();
function Ps(e, t, r, n) {
  return r === void 0 && (r = process.env.NODE_ENV !== "production" ? "ObservableArray@" + F() : "ObservableArray"), n === void 0 && (n = !1), gi(), Me(function() {
    var i = new en(r, t, n, !1);
    bi(i.values_, g, i);
    var a = new Proxy(i.values_, Ss);
    return i.proxy_ = a, e && e.length && i.spliceWithArray_(0, 0, e), a;
  });
}
var Qt = {
  clear: function() {
    return this.splice(0);
  },
  replace: function(t) {
    var r = this[g];
    return r.spliceWithArray_(0, r.values_.length, t);
  },
  // Used by JSON.stringify
  toJSON: function() {
    return this.slice();
  },
  /*
   * functions that do alter the internal structure of the array, (based on lib.es6.d.ts)
   * since these functions alter the inner structure of the array, the have side effects.
   * Because the have side effects, they should not be used in computed function,
   * and for that reason the do not call dependencyState.notifyObserved
   */
  splice: function(t, r) {
    for (var n = arguments.length, i = new Array(n > 2 ? n - 2 : 0), a = 2; a < n; a++)
      i[a - 2] = arguments[a];
    var o = this[g];
    switch (arguments.length) {
      case 0:
        return [];
      case 1:
        return o.spliceWithArray_(t);
      case 2:
        return o.spliceWithArray_(t, r);
    }
    return o.spliceWithArray_(t, r, i);
  },
  spliceWithArray: function(t, r, n) {
    return this[g].spliceWithArray_(t, r, n);
  },
  push: function() {
    for (var t = this[g], r = arguments.length, n = new Array(r), i = 0; i < r; i++)
      n[i] = arguments[i];
    return t.spliceWithArray_(t.values_.length, 0, n), t.values_.length;
  },
  pop: function() {
    return this.splice(Math.max(this[g].values_.length - 1, 0), 1)[0];
  },
  shift: function() {
    return this.splice(0, 1)[0];
  },
  unshift: function() {
    for (var t = this[g], r = arguments.length, n = new Array(r), i = 0; i < r; i++)
      n[i] = arguments[i];
    return t.spliceWithArray_(0, 0, n), t.values_.length;
  },
  reverse: function() {
    return p.trackingDerivation && f(37, "reverse"), this.replace(this.slice().reverse()), this;
  },
  sort: function() {
    p.trackingDerivation && f(37, "sort");
    var t = this.slice();
    return t.sort.apply(t, arguments), this.replace(t), this;
  },
  remove: function(t) {
    var r = this[g], n = r.dehanceValues_(r.values_).indexOf(t);
    return n > -1 ? (this.splice(n, 1), !0) : !1;
  }
};
x("at", U);
x("concat", U);
x("flat", U);
x("includes", U);
x("indexOf", U);
x("join", U);
x("lastIndexOf", U);
x("slice", U);
x("toString", U);
x("toLocaleString", U);
x("toSorted", U);
x("toSpliced", U);
x("with", U);
x("every", J);
x("filter", J);
x("find", J);
x("findIndex", J);
x("findLast", J);
x("findLastIndex", J);
x("flatMap", J);
x("forEach", J);
x("map", J);
x("some", J);
x("toReversed", J);
x("reduce", _i);
x("reduceRight", _i);
function x(e, t) {
  typeof Array.prototype[e] == "function" && (Qt[e] = t(e));
}
function U(e) {
  return function() {
    var t = this[g];
    t.atom_.reportObserved();
    var r = t.dehanceValues_(t.values_);
    return r[e].apply(r, arguments);
  };
}
function J(e) {
  return function(t, r) {
    var n = this, i = this[g];
    i.atom_.reportObserved();
    var a = i.dehanceValues_(i.values_);
    return a[e](function(o, s) {
      return t.call(r, o, s, n);
    });
  };
}
function _i(e) {
  return function() {
    var t = this, r = this[g];
    r.atom_.reportObserved();
    var n = r.dehanceValues_(r.values_), i = arguments[0];
    return arguments[0] = function(a, o, s) {
      return i(a, o, s, t);
    }, n[e].apply(n, arguments);
  };
}
var Ds = /* @__PURE__ */ Ue("ObservableArrayAdministration", en);
function mr(e) {
  return lr(e) && Ds(e[g]);
}
var Ns = {}, pe = "add", _t = "delete", $i = /* @__PURE__ */ function() {
  function e(r, n, i) {
    var a = this;
    n === void 0 && (n = De), i === void 0 && (i = process.env.NODE_ENV !== "production" ? "ObservableMap@" + F() : "ObservableMap"), this.enhancer_ = void 0, this.name_ = void 0, this[g] = Ns, this.data_ = void 0, this.hasMap_ = void 0, this.keysAtom_ = void 0, this.interceptors_ = void 0, this.changeListeners_ = void 0, this.dehancer = void 0, this.enhancer_ = n, this.name_ = i, k(Map) || f(18), Me(function() {
      a.keysAtom_ = Oi(process.env.NODE_ENV !== "production" ? a.name_ + ".keys()" : "ObservableMap.keys()"), a.data_ = /* @__PURE__ */ new Map(), a.hasMap_ = /* @__PURE__ */ new Map(), r && a.merge(r);
    });
  }
  var t = e.prototype;
  return t.has_ = function(n) {
    return this.data_.has(n);
  }, t.has = function(n) {
    var i = this;
    if (!p.trackingDerivation)
      return this.has_(n);
    var a = this.hasMap_.get(n);
    if (!a) {
      var o = a = new Se(this.has_(n), ur, process.env.NODE_ENV !== "production" ? this.name_ + "." + Vr(n) + "?" : "ObservableMap.key?", !1);
      this.hasMap_.set(n, o), Xi(o, function() {
        return i.hasMap_.delete(n);
      });
    }
    return a.get();
  }, t.set = function(n, i) {
    var a = this.has_(n);
    if (M(this)) {
      var o = j(this, {
        type: a ? Z : pe,
        object: this,
        newValue: i,
        name: n
      });
      if (!o)
        return this;
      i = o.newValue;
    }
    return a ? this.updateValue_(n, i) : this.addValue_(n, i), this;
  }, t.delete = function(n) {
    var i = this;
    if (Q(this.keysAtom_), M(this)) {
      var a = j(this, {
        type: _t,
        object: this,
        name: n
      });
      if (!a)
        return !1;
    }
    if (this.has_(n)) {
      var o = P(), s = H(this), l = s || o ? {
        observableKind: "map",
        debugObjectName: this.name_,
        type: _t,
        object: this,
        oldValue: this.data_.get(n).value_,
        name: n
      } : null;
      return process.env.NODE_ENV !== "production" && o && I(l), ae(function() {
        var c;
        i.keysAtom_.reportChanged(), (c = i.hasMap_.get(n)) == null || c.setNewValue_(!1);
        var d = i.data_.get(n);
        d.setNewValue_(void 0), i.data_.delete(n);
      }), s && X(this, l), process.env.NODE_ENV !== "production" && o && z(), !0;
    }
    return !1;
  }, t.updateValue_ = function(n, i) {
    var a = this.data_.get(n);
    if (i = a.prepareNewValue_(i), i !== p.UNCHANGED) {
      var o = P(), s = H(this), l = s || o ? {
        observableKind: "map",
        debugObjectName: this.name_,
        type: Z,
        object: this,
        oldValue: a.value_,
        name: n,
        newValue: i
      } : null;
      process.env.NODE_ENV !== "production" && o && I(l), a.setNewValue_(i), s && X(this, l), process.env.NODE_ENV !== "production" && o && z();
    }
  }, t.addValue_ = function(n, i) {
    var a = this;
    Q(this.keysAtom_), ae(function() {
      var c, d = new Se(i, a.enhancer_, process.env.NODE_ENV !== "production" ? a.name_ + "." + Vr(n) : "ObservableMap.key", !1);
      a.data_.set(n, d), i = d.value_, (c = a.hasMap_.get(n)) == null || c.setNewValue_(!0), a.keysAtom_.reportChanged();
    });
    var o = P(), s = H(this), l = s || o ? {
      observableKind: "map",
      debugObjectName: this.name_,
      type: pe,
      object: this,
      name: n,
      newValue: i
    } : null;
    process.env.NODE_ENV !== "production" && o && I(l), s && X(this, l), process.env.NODE_ENV !== "production" && o && z();
  }, t.get = function(n) {
    return this.has(n) ? this.dehanceValue_(this.data_.get(n).get()) : this.dehanceValue_(void 0);
  }, t.dehanceValue_ = function(n) {
    return this.dehancer !== void 0 ? this.dehancer(n) : n;
  }, t.keys = function() {
    return this.keysAtom_.reportObserved(), this.data_.keys();
  }, t.values = function() {
    var n = this, i = this.keys();
    return yn({
      next: function() {
        var o = i.next(), s = o.done, l = o.value;
        return {
          done: s,
          value: s ? void 0 : n.get(l)
        };
      }
    });
  }, t.entries = function() {
    var n = this, i = this.keys();
    return yn({
      next: function() {
        var o = i.next(), s = o.done, l = o.value;
        return {
          done: s,
          value: s ? void 0 : [l, n.get(l)]
        };
      }
    });
  }, t[Symbol.iterator] = function() {
    return this.entries();
  }, t.forEach = function(n, i) {
    for (var a = Ze(this), o; !(o = a()).done; ) {
      var s = o.value, l = s[0], c = s[1];
      n.call(i, c, l, this);
    }
  }, t.merge = function(n) {
    var i = this;
    return be(n) && (n = new Map(n)), ae(function() {
      D(n) ? $a(n).forEach(function(a) {
        return i.set(a, n[a]);
      }) : Array.isArray(n) ? n.forEach(function(a) {
        var o = a[0], s = a[1];
        return i.set(o, s);
      }) : Qe(n) ? (_a(n) || f(19, n), n.forEach(function(a, o) {
        return i.set(o, a);
      })) : n != null && f(20, n);
    }), this;
  }, t.clear = function() {
    var n = this;
    ae(function() {
      Ii(function() {
        for (var i = Ze(n.keys()), a; !(a = i()).done; ) {
          var o = a.value;
          n.delete(o);
        }
      });
    });
  }, t.replace = function(n) {
    var i = this;
    return ae(function() {
      for (var a = Ts(n), o = /* @__PURE__ */ new Map(), s = !1, l = Ze(i.data_.keys()), c; !(c = l()).done; ) {
        var d = c.value;
        if (!a.has(d)) {
          var u = i.delete(d);
          if (u)
            s = !0;
          else {
            var v = i.data_.get(d);
            o.set(d, v);
          }
        }
      }
      for (var h = Ze(a.entries()), m; !(m = h()).done; ) {
        var O = m.value, A = O[0], G = O[1], je = i.data_.has(A);
        if (i.set(A, G), i.data_.has(A)) {
          var it = i.data_.get(A);
          o.set(A, it), je || (s = !0);
        }
      }
      if (!s)
        if (i.data_.size !== o.size)
          i.keysAtom_.reportChanged();
        else
          for (var N = i.data_.keys(), de = o.keys(), we = N.next(), un = de.next(); !we.done; ) {
            if (we.value !== un.value) {
              i.keysAtom_.reportChanged();
              break;
            }
            we = N.next(), un = de.next();
          }
      i.data_ = o;
    }), this;
  }, t.toString = function() {
    return "[object ObservableMap]";
  }, t.toJSON = function() {
    return Array.from(this);
  }, t.observe_ = function(n, i) {
    return process.env.NODE_ENV !== "production" && i === !0 && f("`observe` doesn't support fireImmediately=true in combination with maps."), Dt(this, n);
  }, t.intercept_ = function(n) {
    return Pt(this, n);
  }, _e(e, [{
    key: "size",
    get: function() {
      return this.keysAtom_.reportObserved(), this.data_.size;
    }
  }, {
    key: Symbol.toStringTag,
    get: function() {
      return "Map";
    }
  }]);
}(), be = /* @__PURE__ */ Ue("ObservableMap", $i);
function yn(e) {
  return e[Symbol.toStringTag] = "MapIterator", rn(e);
}
function Ts(e) {
  if (Qe(e) || be(e))
    return e;
  if (Array.isArray(e))
    return new Map(e);
  if (D(e)) {
    var t = /* @__PURE__ */ new Map();
    for (var r in e)
      t.set(r, e[r]);
    return t;
  } else
    return f(21, e);
}
var Vs = {}, ea = /* @__PURE__ */ function() {
  function e(r, n, i) {
    var a = this;
    n === void 0 && (n = De), i === void 0 && (i = process.env.NODE_ENV !== "production" ? "ObservableSet@" + F() : "ObservableSet"), this.name_ = void 0, this[g] = Vs, this.data_ = /* @__PURE__ */ new Set(), this.atom_ = void 0, this.changeListeners_ = void 0, this.interceptors_ = void 0, this.dehancer = void 0, this.enhancer_ = void 0, this.name_ = i, k(Set) || f(22), this.enhancer_ = function(o, s) {
      return n(o, s, i);
    }, Me(function() {
      a.atom_ = Oi(a.name_), r && a.replace(r);
    });
  }
  var t = e.prototype;
  return t.dehanceValue_ = function(n) {
    return this.dehancer !== void 0 ? this.dehancer(n) : n;
  }, t.clear = function() {
    var n = this;
    ae(function() {
      Ii(function() {
        for (var i = Ze(n.data_.values()), a; !(a = i()).done; ) {
          var o = a.value;
          n.delete(o);
        }
      });
    });
  }, t.forEach = function(n, i) {
    for (var a = Ze(this), o; !(o = a()).done; ) {
      var s = o.value;
      n.call(i, s, s, this);
    }
  }, t.add = function(n) {
    var i = this;
    if (Q(this.atom_), M(this)) {
      var a = j(this, {
        type: pe,
        object: this,
        newValue: n
      });
      if (!a)
        return this;
      n = a.newValue;
    }
    if (!this.has(n)) {
      ae(function() {
        i.data_.add(i.enhancer_(n, void 0)), i.atom_.reportChanged();
      });
      var o = process.env.NODE_ENV !== "production" && P(), s = H(this), l = s || o ? {
        observableKind: "set",
        debugObjectName: this.name_,
        type: pe,
        object: this,
        newValue: n
      } : null;
      o && process.env.NODE_ENV !== "production" && I(l), s && X(this, l), o && process.env.NODE_ENV !== "production" && z();
    }
    return this;
  }, t.delete = function(n) {
    var i = this;
    if (M(this)) {
      var a = j(this, {
        type: _t,
        object: this,
        oldValue: n
      });
      if (!a)
        return !1;
    }
    if (this.has(n)) {
      var o = process.env.NODE_ENV !== "production" && P(), s = H(this), l = s || o ? {
        observableKind: "set",
        debugObjectName: this.name_,
        type: _t,
        object: this,
        oldValue: n
      } : null;
      return o && process.env.NODE_ENV !== "production" && I(l), ae(function() {
        i.atom_.reportChanged(), i.data_.delete(n);
      }), s && X(this, l), o && process.env.NODE_ENV !== "production" && z(), !0;
    }
    return !1;
  }, t.has = function(n) {
    return this.atom_.reportObserved(), this.data_.has(this.dehanceValue_(n));
  }, t.entries = function() {
    var n = this.values();
    return wn({
      next: function() {
        var a = n.next(), o = a.value, s = a.done;
        return s ? {
          value: void 0,
          done: s
        } : {
          value: [o, o],
          done: s
        };
      }
    });
  }, t.keys = function() {
    return this.values();
  }, t.values = function() {
    this.atom_.reportObserved();
    var n = this, i = this.data_.values();
    return wn({
      next: function() {
        var o = i.next(), s = o.value, l = o.done;
        return l ? {
          value: void 0,
          done: l
        } : {
          value: n.dehanceValue_(s),
          done: l
        };
      }
    });
  }, t.intersection = function(n) {
    if (ne(n) && !Y(n))
      return n.intersection(this);
    var i = new Set(this);
    return i.intersection(n);
  }, t.union = function(n) {
    if (ne(n) && !Y(n))
      return n.union(this);
    var i = new Set(this);
    return i.union(n);
  }, t.difference = function(n) {
    return new Set(this).difference(n);
  }, t.symmetricDifference = function(n) {
    if (ne(n) && !Y(n))
      return n.symmetricDifference(this);
    var i = new Set(this);
    return i.symmetricDifference(n);
  }, t.isSubsetOf = function(n) {
    return new Set(this).isSubsetOf(n);
  }, t.isSupersetOf = function(n) {
    return new Set(this).isSupersetOf(n);
  }, t.isDisjointFrom = function(n) {
    if (ne(n) && !Y(n))
      return n.isDisjointFrom(this);
    var i = new Set(this);
    return i.isDisjointFrom(n);
  }, t.replace = function(n) {
    var i = this;
    return Y(n) && (n = new Set(n)), ae(function() {
      Array.isArray(n) ? (i.clear(), n.forEach(function(a) {
        return i.add(a);
      })) : ne(n) ? (i.clear(), n.forEach(function(a) {
        return i.add(a);
      })) : n != null && f("Cannot initialize set from " + n);
    }), this;
  }, t.observe_ = function(n, i) {
    return process.env.NODE_ENV !== "production" && i === !0 && f("`observe` doesn't support fireImmediately=true in combination with sets."), Dt(this, n);
  }, t.intercept_ = function(n) {
    return Pt(this, n);
  }, t.toJSON = function() {
    return Array.from(this);
  }, t.toString = function() {
    return "[object ObservableSet]";
  }, t[Symbol.iterator] = function() {
    return this.values();
  }, _e(e, [{
    key: "size",
    get: function() {
      return this.atom_.reportObserved(), this.data_.size;
    }
  }, {
    key: Symbol.toStringTag,
    get: function() {
      return "Set";
    }
  }]);
}(), Y = /* @__PURE__ */ Ue("ObservableSet", ea);
function wn(e) {
  return e[Symbol.toStringTag] = "SetIterator", rn(e);
}
var xn = /* @__PURE__ */ Object.create(null), On = "remove", qr = /* @__PURE__ */ function() {
  function e(r, n, i, a) {
    n === void 0 && (n = /* @__PURE__ */ new Map()), a === void 0 && (a = Do), this.target_ = void 0, this.values_ = void 0, this.name_ = void 0, this.defaultAnnotation_ = void 0, this.keysAtom_ = void 0, this.changeListeners_ = void 0, this.interceptors_ = void 0, this.proxy_ = void 0, this.isPlainObject_ = void 0, this.appliedAnnotations_ = void 0, this.pendingKeys_ = void 0, this.target_ = r, this.values_ = n, this.name_ = i, this.defaultAnnotation_ = a, this.keysAtom_ = new me(process.env.NODE_ENV !== "production" ? this.name_ + ".keys" : "ObservableObject.keys"), this.isPlainObject_ = D(this.target_), process.env.NODE_ENV !== "production" && !oa(this.defaultAnnotation_) && f("defaultAnnotation must be valid annotation"), process.env.NODE_ENV !== "production" && (this.appliedAnnotations_ = {});
  }
  var t = e.prototype;
  return t.getObservablePropValue_ = function(n) {
    return this.values_.get(n).get();
  }, t.setObservablePropValue_ = function(n, i) {
    var a = this.values_.get(n);
    if (a instanceof B)
      return a.set(i), !0;
    if (M(this)) {
      var o = j(this, {
        type: Z,
        object: this.proxy_ || this.target_,
        name: n,
        newValue: i
      });
      if (!o)
        return null;
      i = o.newValue;
    }
    if (i = a.prepareNewValue_(i), i !== p.UNCHANGED) {
      var s = H(this), l = process.env.NODE_ENV !== "production" && P(), c = s || l ? {
        type: Z,
        observableKind: "object",
        debugObjectName: this.name_,
        object: this.proxy_ || this.target_,
        oldValue: a.value_,
        name: n,
        newValue: i
      } : null;
      process.env.NODE_ENV !== "production" && l && I(c), a.setNewValue_(i), s && X(this, c), process.env.NODE_ENV !== "production" && l && z();
    }
    return !0;
  }, t.get_ = function(n) {
    return p.trackingDerivation && !W(this.target_, n) && this.has_(n), this.target_[n];
  }, t.set_ = function(n, i, a) {
    return a === void 0 && (a = !1), W(this.target_, n) ? this.values_.has(n) ? this.setObservablePropValue_(n, i) : a ? Reflect.set(this.target_, n, i) : (this.target_[n] = i, !0) : this.extend_(n, {
      value: i,
      enumerable: !0,
      writable: !0,
      configurable: !0
    }, this.defaultAnnotation_, a);
  }, t.has_ = function(n) {
    if (!p.trackingDerivation)
      return n in this.target_;
    this.pendingKeys_ || (this.pendingKeys_ = /* @__PURE__ */ new Map());
    var i = this.pendingKeys_.get(n);
    return i || (i = new Se(n in this.target_, ur, process.env.NODE_ENV !== "production" ? this.name_ + "." + Vr(n) + "?" : "ObservableObject.key?", !1), this.pendingKeys_.set(n, i)), i.get();
  }, t.make_ = function(n, i) {
    if (i === !0 && (i = this.defaultAnnotation_), i !== !1) {
      if (Cn(this, i, n), !(n in this.target_)) {
        var a;
        if ((a = this.target_[ie]) != null && a[n])
          return;
        f(1, i.annotationType_, this.name_ + "." + n.toString());
      }
      for (var o = this.target_; o && o !== sr; ) {
        var s = Wt(o, n);
        if (s) {
          var l = i.make_(this, n, s, o);
          if (l === 0)
            return;
          if (l === 1)
            break;
        }
        o = Object.getPrototypeOf(o);
      }
      kn(this, i, n);
    }
  }, t.extend_ = function(n, i, a, o) {
    if (o === void 0 && (o = !1), a === !0 && (a = this.defaultAnnotation_), a === !1)
      return this.defineProperty_(n, i, o);
    Cn(this, a, n);
    var s = a.extend_(this, n, i, o);
    return s && kn(this, a, n), s;
  }, t.defineProperty_ = function(n, i, a) {
    a === void 0 && (a = !1), Q(this.keysAtom_);
    try {
      R();
      var o = this.delete_(n);
      if (!o)
        return o;
      if (M(this)) {
        var s = j(this, {
          object: this.proxy_ || this.target_,
          name: n,
          type: pe,
          newValue: i.value
        });
        if (!s)
          return null;
        var l = s.newValue;
        i.value !== l && (i = ve({}, i, {
          value: l
        }));
      }
      if (a) {
        if (!Reflect.defineProperty(this.target_, n, i))
          return !1;
      } else
        _(this.target_, n, i);
      this.notifyPropertyAddition_(n, i.value);
    } finally {
      q();
    }
    return !0;
  }, t.defineObservableProperty_ = function(n, i, a, o) {
    o === void 0 && (o = !1), Q(this.keysAtom_);
    try {
      R();
      var s = this.delete_(n);
      if (!s)
        return s;
      if (M(this)) {
        var l = j(this, {
          object: this.proxy_ || this.target_,
          name: n,
          type: pe,
          newValue: i
        });
        if (!l)
          return null;
        i = l.newValue;
      }
      var c = En(n), d = {
        configurable: p.safeDescriptors ? this.isPlainObject_ : !0,
        enumerable: !0,
        get: c.get,
        set: c.set
      };
      if (o) {
        if (!Reflect.defineProperty(this.target_, n, d))
          return !1;
      } else
        _(this.target_, n, d);
      var u = new Se(i, a, process.env.NODE_ENV !== "production" ? this.name_ + "." + n.toString() : "ObservableObject.key", !1);
      this.values_.set(n, u), this.notifyPropertyAddition_(n, u.value_);
    } finally {
      q();
    }
    return !0;
  }, t.defineComputedProperty_ = function(n, i, a) {
    a === void 0 && (a = !1), Q(this.keysAtom_);
    try {
      R();
      var o = this.delete_(n);
      if (!o)
        return o;
      if (M(this)) {
        var s = j(this, {
          object: this.proxy_ || this.target_,
          name: n,
          type: pe,
          newValue: void 0
        });
        if (!s)
          return null;
      }
      i.name || (i.name = process.env.NODE_ENV !== "production" ? this.name_ + "." + n.toString() : "ObservableObject.key"), i.context = this.proxy_ || this.target_;
      var l = En(n), c = {
        configurable: p.safeDescriptors ? this.isPlainObject_ : !0,
        enumerable: !1,
        get: l.get,
        set: l.set
      };
      if (a) {
        if (!Reflect.defineProperty(this.target_, n, c))
          return !1;
      } else
        _(this.target_, n, c);
      this.values_.set(n, new B(i)), this.notifyPropertyAddition_(n, void 0);
    } finally {
      q();
    }
    return !0;
  }, t.delete_ = function(n, i) {
    if (i === void 0 && (i = !1), Q(this.keysAtom_), !W(this.target_, n))
      return !0;
    if (M(this)) {
      var a = j(this, {
        object: this.proxy_ || this.target_,
        name: n,
        type: On
      });
      if (!a)
        return null;
    }
    try {
      var o;
      R();
      var s = H(this), l = process.env.NODE_ENV !== "production" && P(), c = this.values_.get(n), d = void 0;
      if (!c && (s || l)) {
        var u;
        d = (u = Wt(this.target_, n)) == null ? void 0 : u.value;
      }
      if (i) {
        if (!Reflect.deleteProperty(this.target_, n))
          return !1;
      } else
        delete this.target_[n];
      if (process.env.NODE_ENV !== "production" && delete this.appliedAnnotations_[n], c && (this.values_.delete(n), c instanceof Se && (d = c.value_), ji(c)), this.keysAtom_.reportChanged(), (o = this.pendingKeys_) == null || (o = o.get(n)) == null || o.set(n in this.target_), s || l) {
        var v = {
          type: On,
          observableKind: "object",
          object: this.proxy_ || this.target_,
          debugObjectName: this.name_,
          oldValue: d,
          name: n
        };
        process.env.NODE_ENV !== "production" && l && I(v), s && X(this, v), process.env.NODE_ENV !== "production" && l && z();
      }
    } finally {
      q();
    }
    return !0;
  }, t.observe_ = function(n, i) {
    return process.env.NODE_ENV !== "production" && i === !0 && f("`observe` doesn't support the fire immediately property for observable objects."), Dt(this, n);
  }, t.intercept_ = function(n) {
    return Pt(this, n);
  }, t.notifyPropertyAddition_ = function(n, i) {
    var a, o = H(this), s = process.env.NODE_ENV !== "production" && P();
    if (o || s) {
      var l = o || s ? {
        type: pe,
        observableKind: "object",
        debugObjectName: this.name_,
        object: this.proxy_ || this.target_,
        name: n,
        newValue: i
      } : null;
      process.env.NODE_ENV !== "production" && s && I(l), o && X(this, l), process.env.NODE_ENV !== "production" && s && z();
    }
    (a = this.pendingKeys_) == null || (a = a.get(n)) == null || a.set(!0), this.keysAtom_.reportChanged();
  }, t.ownKeys_ = function() {
    return this.keysAtom_.reportObserved(), mt(this.target_);
  }, t.keys_ = function() {
    return this.keysAtom_.reportObserved(), Object.keys(this.target_);
  }, e;
}();
function et(e, t) {
  var r;
  if (process.env.NODE_ENV !== "production" && t && tt(e) && f("Options can't be provided for already observable objects."), W(e, g))
    return process.env.NODE_ENV !== "production" && !(ia(e) instanceof qr) && f("Cannot convert '" + $t(e) + `' into observable object:
The target is already observable of different type.
Extending builtins is not supported.`), e;
  process.env.NODE_ENV !== "production" && !Object.isExtensible(e) && f("Cannot make the designated object observable; it is not extensible");
  var n = (r = t?.name) != null ? r : process.env.NODE_ENV !== "production" ? (D(e) ? "ObservableObject" : e.constructor.name) + "@" + F() : "ObservableObject", i = new qr(e, /* @__PURE__ */ new Map(), String(n), qo(t));
  return cr(e, g, i), e;
}
var Is = /* @__PURE__ */ Ue("ObservableObjectAdministration", qr);
function En(e) {
  return xn[e] || (xn[e] = {
    get: function() {
      return this[g].getObservablePropValue_(e);
    },
    set: function(r) {
      return this[g].setObservablePropValue_(e, r);
    }
  });
}
function tt(e) {
  return lr(e) ? Is(e[g]) : !1;
}
function kn(e, t, r) {
  var n;
  process.env.NODE_ENV !== "production" && (e.appliedAnnotations_[r] = t), (n = e.target_[ie]) == null || delete n[r];
}
function Cn(e, t, r) {
  if (process.env.NODE_ENV !== "production" && !oa(t) && f("Cannot annotate '" + e.name_ + "." + r.toString() + "': Invalid annotation."), process.env.NODE_ENV !== "production" && !Xt(t) && W(e.appliedAnnotations_, r)) {
    var n = e.name_ + "." + r.toString(), i = e.appliedAnnotations_[r].annotationType_, a = t.annotationType_;
    f("Cannot apply '" + a + "' to '" + n + "':" + (`
The field is already annotated with '` + i + "'.") + `
Re-annotating fields is not allowed.
Use 'override' annotation for methods overridden by subclass.`);
  }
}
var zs = /* @__PURE__ */ ra(0), Us = /* @__PURE__ */ function() {
  var e = !1, t = {};
  return Object.defineProperty(t, "0", {
    set: function() {
      e = !0;
    }
  }), Object.create(t)[0] = 1, e === !1;
}(), Cr = 0, ta = function() {
};
function Ls(e, t) {
  Object.setPrototypeOf ? Object.setPrototypeOf(e.prototype, t) : e.prototype.__proto__ !== void 0 ? e.prototype.__proto__ = t : e.prototype = t;
}
Ls(ta, Array.prototype);
var tn = /* @__PURE__ */ function(e) {
  function t(n, i, a, o) {
    var s;
    return a === void 0 && (a = process.env.NODE_ENV !== "production" ? "ObservableArray@" + F() : "ObservableArray"), o === void 0 && (o = !1), s = e.call(this) || this, Me(function() {
      var l = new en(a, i, o, !0);
      l.proxy_ = s, bi(s, g, l), n && n.length && s.spliceWithArray(0, 0, n), Us && Object.defineProperty(s, "0", zs);
    }), s;
  }
  xi(t, e);
  var r = t.prototype;
  return r.concat = function() {
    this[g].atom_.reportObserved();
    for (var i = arguments.length, a = new Array(i), o = 0; o < i; o++)
      a[o] = arguments[o];
    return Array.prototype.concat.apply(
      this.slice(),
      //@ts-ignore
      a.map(function(s) {
        return mr(s) ? s.slice() : s;
      })
    );
  }, r[Symbol.iterator] = function() {
    var n = this, i = 0;
    return rn({
      next: function() {
        return i < n.length ? {
          value: n[i++],
          done: !1
        } : {
          done: !0,
          value: void 0
        };
      }
    });
  }, _e(t, [{
    key: "length",
    get: function() {
      return this[g].getArrayLength_();
    },
    set: function(i) {
      this[g].setArrayLength_(i);
    }
  }, {
    key: Symbol.toStringTag,
    get: function() {
      return "Array";
    }
  }]);
}(ta);
Object.entries(Qt).forEach(function(e) {
  var t = e[0], r = e[1];
  t !== "concat" && cr(tn.prototype, t, r);
});
function ra(e) {
  return {
    enumerable: !1,
    configurable: !0,
    get: function() {
      return this[g].get_(e);
    },
    set: function(r) {
      this[g].set_(e, r);
    }
  };
}
function Ms(e) {
  _(tn.prototype, "" + e, ra(e));
}
function na(e) {
  if (e > Cr) {
    for (var t = Cr; t < e + 100; t++)
      Ms(t);
    Cr = e;
  }
}
na(1e3);
function js(e, t, r) {
  return new tn(e, t, r);
}
function Ge(e, t) {
  if (typeof e == "object" && e !== null) {
    if (mr(e))
      return t !== void 0 && f(23), e[g].atom_;
    if (Y(e))
      return e.atom_;
    if (be(e)) {
      if (t === void 0)
        return e.keysAtom_;
      var r = e.data_.get(t) || e.hasMap_.get(t);
      return r || f(25, t, $t(e)), r;
    }
    if (tt(e)) {
      if (!t)
        return f(26);
      var n = e[g].values_.get(t);
      return n || f(27, t, $t(e)), n;
    }
    if (Gr(e) || gr(e) || Yt(e))
      return e;
  } else if (k(e) && Yt(e[g]))
    return e[g];
  f(28);
}
function ia(e, t) {
  if (e || f(29), Gr(e) || gr(e) || Yt(e) || be(e) || Y(e))
    return e;
  if (e[g])
    return e[g];
  f(24, e);
}
function $t(e, t) {
  var r;
  if (t !== void 0)
    r = Ge(e, t);
  else {
    if (Ve(e))
      return e.name;
    tt(e) || be(e) || Y(e) ? r = ia(e) : r = Ge(e);
  }
  return r.name_;
}
function Me(e) {
  var t = Le(), r = vr(!0);
  R();
  try {
    return e();
  } finally {
    q(), hr(r), oe(t);
  }
}
var An = sr.toString;
function aa(e, t, r) {
  return r === void 0 && (r = -1), Kr(e, t, r);
}
function Kr(e, t, r, n, i) {
  if (e === t)
    return e !== 0 || 1 / e === 1 / t;
  if (e == null || t == null)
    return !1;
  if (e !== e)
    return t !== t;
  var a = typeof e;
  if (a !== "function" && a !== "object" && typeof t != "object")
    return !1;
  var o = An.call(e);
  if (o !== An.call(t))
    return !1;
  switch (o) {
    // Strings, numbers, regular expressions, dates, and booleans are compared by value.
    case "[object RegExp]":
    // RegExps are coerced to strings for comparison (Note: '' + /a/i === '/a/i')
    case "[object String]":
      return "" + e == "" + t;
    case "[object Number]":
      return +e != +e ? +t != +t : +e == 0 ? 1 / +e === 1 / t : +e == +t;
    case "[object Date]":
    case "[object Boolean]":
      return +e == +t;
    case "[object Symbol]":
      return typeof Symbol < "u" && Symbol.valueOf.call(e) === Symbol.valueOf.call(t);
    case "[object Map]":
    case "[object Set]":
      r >= 0 && r++;
      break;
  }
  e = Sn(e), t = Sn(t);
  var s = o === "[object Array]";
  if (!s) {
    if (typeof e != "object" || typeof t != "object")
      return !1;
    var l = e.constructor, c = t.constructor;
    if (l !== c && !(k(l) && l instanceof l && k(c) && c instanceof c) && "constructor" in e && "constructor" in t)
      return !1;
  }
  if (r === 0)
    return !1;
  r < 0 && (r = -1), n = n || [], i = i || [];
  for (var d = n.length; d--; )
    if (n[d] === e)
      return i[d] === t;
  if (n.push(e), i.push(t), s) {
    if (d = e.length, d !== t.length)
      return !1;
    for (; d--; )
      if (!Kr(e[d], t[d], r - 1, n, i))
        return !1;
  } else {
    var u = Object.keys(e), v = u.length;
    if (Object.keys(t).length !== v)
      return !1;
    for (var h = 0; h < v; h++) {
      var m = u[h];
      if (!(W(t, m) && Kr(e[m], t[m], r - 1, n, i)))
        return !1;
    }
  }
  return n.pop(), i.pop(), !0;
}
function Sn(e) {
  return mr(e) ? e.slice() : Qe(e) || be(e) || ne(e) || Y(e) ? Array.from(e.entries()) : e;
}
var Pn, Rs = ((Pn = Hr().Iterator) == null ? void 0 : Pn.prototype) || {};
function rn(e) {
  return e[Symbol.iterator] = qs, Object.assign(Object.create(Rs), e);
}
function qs() {
  return this;
}
function oa(e) {
  return (
    // Can be function
    e instanceof Object && typeof e.annotationType_ == "string" && k(e.make_) && k(e.extend_)
  );
}
["Symbol", "Map", "Set"].forEach(function(e) {
  var t = Hr();
  typeof t[e] > "u" && f("MobX requires global '" + e + "' to be available or polyfilled");
});
typeof __MOBX_DEVTOOLS_GLOBAL_HOOK__ == "object" && __MOBX_DEVTOOLS_GLOBAL_HOOK__.injectMobx({
  spy: ss,
  extras: {
    getDebugName: $t
  },
  $mobx: g
});
var ye = /* @__PURE__ */ ((e) => (e.INFORMATION = "information", e.WARNING = "warning", e.ERROR = "error", e))(ye || {});
const Ks = Symbol.for("react.portal"), Bs = Symbol.for("react.fragment"), Fs = Symbol.for("react.strict_mode"), Zs = Symbol.for("react.profiler"), Ws = Symbol.for("react.provider"), Hs = Symbol.for("react.context"), sa = Symbol.for("react.forward_ref"), Xs = Symbol.for("react.suspense"), Js = Symbol.for("react.suspense_list"), Gs = Symbol.for("react.memo"), Ys = Symbol.for("react.lazy");
function Qs(e, t, r) {
  const n = e.displayName;
  if (n)
    return n;
  const i = t.displayName || t.name || "";
  return i !== "" ? `${r}(${i})` : r;
}
function Dn(e) {
  return e.displayName || "Context";
}
function er(e) {
  if (e === null)
    return null;
  if (typeof e == "function")
    return e.displayName || e.name || null;
  if (typeof e == "string")
    return e;
  switch (e) {
    case Bs:
      return "Fragment";
    case Ks:
      return "Portal";
    case Zs:
      return "Profiler";
    case Fs:
      return "StrictMode";
    case Xs:
      return "Suspense";
    case Js:
      return "SuspenseList";
  }
  if (typeof e == "object")
    switch (e.$$typeof) {
      case Hs:
        return `${Dn(e)}.Consumer`;
      case Ws:
        return `${Dn(e._context)}.Provider`;
      case sa:
        return Qs(e, e.render, "ForwardRef");
      case Gs:
        const t = e.displayName || null;
        return t !== null ? t : er(e.type) || "Memo";
      case Ys: {
        const r = e, n = r._payload, i = r._init;
        try {
          return er(i(n));
        } catch {
          return null;
        }
      }
    }
  return null;
}
let zt;
function Nd() {
  const e = /* @__PURE__ */ new Set();
  return Array.from(document.body.querySelectorAll("*")).flatMap(el).filter(_s).filter((r) => !r.fileName.includes("frontend/generated/")).forEach((r) => e.add(r.fileName)), Array.from(e);
}
function _s(e) {
  return !!e && e.fileName;
}
function tr(e) {
  if (!e)
    return;
  if (e._debugSource)
    return e._debugSource;
  const t = e._debugInfo?.source;
  if (t?.fileName && t?.lineNumber)
    return t;
}
function $s(e) {
  if (e && e.type?.__debugSourceDefine)
    return e.type.__debugSourceDefine;
}
function el(e) {
  return tr(rr(e));
}
function tl() {
  return `__reactFiber$${la()}`;
}
function rl() {
  return `__reactContainer$${la()}`;
}
function la() {
  if (!(!zt && (zt = Array.from(document.querySelectorAll("*")).flatMap((e) => Object.keys(e)).filter((e) => e.startsWith("__reactFiber$")).map((e) => e.replace("__reactFiber$", "")).find((e) => e), !zt)))
    return zt;
}
function ut(e) {
  const t = e.type;
  return t?.$$typeof === sa && !t.displayName && e.child ? ut(e.child) : er(e.type) ?? er(e.elementType) ?? "???";
}
function nl() {
  const e = Array.from(document.querySelectorAll("body > *")).flatMap((r) => r[rl()]).find((r) => r), t = Ie(e);
  return Ie(t?.child);
}
function il(e) {
  const t = [];
  let r = Ie(e.child);
  for (; r; )
    t.push(r), r = Ie(r.sibling);
  return t;
}
function al(e) {
  return e.hasOwnProperty("entanglements") && e.hasOwnProperty("containerInfo");
}
function ol(e) {
  return e.hasOwnProperty("stateNode") && e.hasOwnProperty("pendingProps");
}
function Ie(e) {
  const t = e?.stateNode;
  if (t?.current && (al(t) || ol(t)))
    return t?.current;
  if (!e)
    return;
  if (!e.alternate)
    return e;
  const r = e.alternate, n = e?.actualStartTime, i = r?.actualStartTime;
  return i !== n && i > n ? r : e;
}
function rr(e) {
  const t = tl(), r = Ie(e[t]);
  if (tr(r))
    return r;
  let n = r?.return || void 0;
  for (; n && !tr(n); )
    n = n.return || void 0;
  return n;
}
function nr(e) {
  if (e.stateNode?.isConnected === !0)
    return e.stateNode;
  if (e.child)
    return nr(e.child);
}
function Nn(e) {
  const t = nr(e);
  return t && Ie(rr(t)) === e;
}
function sl(e) {
  return typeof e.type != "function" || ca(e) ? !1 : !!(tr(e) || $s(e));
}
function ca(e) {
  if (!e)
    return !1;
  const t = e;
  return typeof e.type == "function" && t.tag === 1;
}
const Nt = async (e, t, r) => window.Vaadin.copilot.comm(e, t, r), he = "copilot-", ll = "25.0.4", cl = "undefined", Td = "attention-required", Vd = "https://plugins.jetbrains.com/plugin/23758-vaadin", Id = "https://marketplace.visualstudio.com/items?itemName=vaadin.vaadin-vscode", zd = "https://marketplace.eclipse.org/content/vaadin-tools";
function Ud(e) {
  return e === void 0 ? !1 : e.nodeId >= 0;
}
function dl(e) {
  if (e.javaClass)
    return e.javaClass.substring(e.javaClass.lastIndexOf(".") + 1);
}
function Ar(e) {
  const t = window.Vaadin;
  if (t && t.Flow) {
    const { clients: r } = t.Flow, n = Object.keys(r);
    for (const i of n) {
      const a = r[i];
      if (a.getNodeId) {
        const o = a.getNodeId(e);
        if (o >= 0) {
          const s = a.getNodeInfo(o);
          return {
            nodeId: o,
            uiId: a.getUIId(),
            element: e,
            javaClass: s.javaClass,
            styles: s.styles,
            hiddenByServer: s.hiddenByServer
          };
        }
      }
    }
  }
}
function Ld() {
  const e = window.Vaadin;
  let t;
  if (e && e.Flow) {
    const { clients: r } = e.Flow, n = Object.keys(r);
    for (const i of n) {
      const a = r[i];
      a.getUIId && (t = a.getUIId());
    }
  }
  return t;
}
function Md(e) {
  return {
    uiId: e.uiId,
    nodeId: e.nodeId
  };
}
function ul(e) {
  return e ? e.type?.type === "FlowContainer" : !1;
}
function pl(e) {
  return e.localName.startsWith("flow-container");
}
function jd(e) {
  const t = e.lastIndexOf(".");
  return t < 0 ? e : e.substring(t + 1);
}
function da(e, t) {
  const r = e();
  r ? t(r) : setTimeout(() => da(e, t), 50);
}
async function fl(e) {
  const t = e();
  if (t)
    return t;
  let r;
  const n = new Promise((a) => {
    r = a;
  }), i = setInterval(() => {
    const a = e();
    a && (clearInterval(i), r(a));
  }, 10);
  return n;
}
function ir(e) {
  return C.box(e, { deep: !1 });
}
function vl(e) {
  return e && typeof e.lastAccessedBy_ == "number";
}
function Rd(e) {
  if (e) {
    if (typeof e == "string")
      return e;
    if (!vl(e))
      throw new Error(`Expected message to be a string or an observable value but was ${JSON.stringify(e)}`);
    return e.get();
  }
}
function hl(e) {
  return Array.from(new Set(e));
}
function Tt(e) {
  Promise.resolve().then(() => wc).then(({ showNotification: t }) => {
    t(e);
  });
}
function gl() {
  Tt({
    type: ye.INFORMATION,
    message: "The previous operation is still in progress. Please wait for it to finish."
  });
}
function ml(e) {
  return e.children && (e.children = e.children.filter(ml)), e.visible !== !1;
}
function Tn(e) {
  const t = `--${e}`, r = /* @__PURE__ */ new Set();
  function n(c) {
    return "cssRules" in c;
  }
  function i(c) {
    return c.type === CSSRule.STYLE_RULE;
  }
  function a(c) {
    return "cssRules" in c;
  }
  function o(c) {
    if (!c) return !1;
    for (let d = 0; d < c.length; d++)
      if (c[d]?.startsWith(t)) return !0;
    return !1;
  }
  function s(c) {
    if (i(c) && o(c.style)) return !0;
    if (a(c)) {
      const d = c.cssRules;
      for (const u of d)
        if (s(u))
          return !0;
    }
    if (c.type === CSSRule.IMPORT_RULE) {
      const d = c;
      if (d.styleSheet && l(d.styleSheet)) return !0;
    }
    return !1;
  }
  function l(c) {
    if (!c || r.has(c)) return !1;
    r.add(c);
    let d;
    try {
      d = c.cssRules;
    } catch {
      return !1;
    }
    if (!d)
      return !1;
    for (const u of d)
      if (s(u))
        return !0;
    return !1;
  }
  for (const c of Array.from([...document.adoptedStyleSheets, ...document.styleSheets]))
    if (n(c) && l(c)) return !0;
  return !1;
}
function qd(e) {
  return e?.replace(/^.*[\\/]/, "");
}
async function ua() {
  return fl(() => {
    const e = window.Vaadin.devTools, t = e?.frontendConnection && e?.frontendConnection.status === "active";
    return e !== void 0 && t && e?.frontendConnection;
  });
}
function te(e, t) {
  ua().then((r) => {
    r.canSend ? r.send(e, t) : Tt({
      type: ye.INFORMATION,
      message: "Connection lost",
      details: "Please refresh the page and start the server if it is not running",
      delay: 1e4,
      dismissId: "connection-lost"
    });
  });
}
const bl = () => {
  te("copilot-browser-info", {
    userAgent: navigator.userAgent,
    locale: navigator.language,
    timezone: Intl.DateTimeFormat().resolvedOptions().timeZone
  });
}, xt = (e, t) => {
  te("copilot-track-event", { event: e, properties: t });
}, Kd = (e, t) => {
  xt(e, { ...t, view: "react" });
}, Bd = (e, t) => {
  xt(e, { ...t, view: "flow" });
};
class yl {
  constructor() {
    this.welcomeActive = !1, this.loginCheckActive = !1, this.userInfo = void 0, this.active = !1, this.activatedFrom = null, this.activatedAtLeastOnce = !1, this.operationInProgress = void 0, this.operationWaitsHmrUpdate = void 0, this.operationWaitsHmrUpdateTimeout = void 0, this.idePluginState = void 0, this.notifications = [], this.infoTooltip = null, this.sectionPanelDragging = !1, this.sectionPanelResizing = !1, this.drawerResizing = !1, this.featureFlags = [], this.newVaadinVersionState = void 0, this.pointerEventsDisabledForScrolling = !1, this.editComponent = void 0, this.serverRestartRequiringToggledFeatureFlags = [], this.appTheme = void 0, this.escapeEventHandler = void 0, $e(this, {
      notifications: C.shallow
    });
  }
  setActive(t, r) {
    this.active = t, t && (this.activatedAtLeastOnce || (xt("activate"), this.idePluginState?.active && xt("plugin-active", {
      pluginVersion: this.idePluginState.version,
      ide: this.idePluginState.ide
    })), this.activatedAtLeastOnce = !0), this.activatedFrom = r ?? null;
  }
  setWelcomeActive(t) {
    this.welcomeActive = t;
  }
  setLoginCheckActive(t) {
    this.loginCheckActive = t;
  }
  setUserInfo(t) {
    this.userInfo = t;
  }
  startOperation(t) {
    if (this.operationInProgress)
      throw new Error(`An ${t} operation is already in progress`);
    if (this.operationWaitsHmrUpdate) {
      gl();
      return;
    }
    this.operationInProgress = t;
  }
  stopOperation(t) {
    if (this.operationInProgress) {
      if (this.operationInProgress !== t)
        return;
    } else return;
    this.operationInProgress = void 0;
  }
  setOperationWaitsHmrUpdate(t, r) {
    this.operationWaitsHmrUpdate = t, this.operationWaitsHmrUpdateTimeout = r;
  }
  clearOperationWaitsHmrUpdate() {
    this.operationWaitsHmrUpdate = void 0, this.operationWaitsHmrUpdateTimeout = void 0;
  }
  setIdePluginState(t) {
    this.idePluginState = t;
  }
  toggleActive(t) {
    this.setActive(!this.active, this.active ? null : t ?? null);
  }
  reset() {
    this.active = !1, this.activatedAtLeastOnce = !1;
  }
  setNotifications(t) {
    this.notifications = t;
  }
  removeNotification(t) {
    t.animatingOut = !0, setTimeout(() => {
      this.reallyRemoveNotification(t);
    }, 180);
  }
  reallyRemoveNotification(t) {
    const r = this.notifications.indexOf(t);
    r > -1 && this.notifications.splice(r, 1);
  }
  setTooltip(t, r) {
    this.infoTooltip = {
      text: t,
      loader: r
    };
  }
  clearTooltip() {
    this.infoTooltip = null;
  }
  setSectionPanelDragging(t) {
    this.sectionPanelDragging = t;
  }
  setSectionPanelResizing(t) {
    this.sectionPanelResizing = t;
  }
  setDrawerResizing(t) {
    this.drawerResizing = t;
  }
  setFeatureFlags(t) {
    this.featureFlags = t;
  }
  setVaadinVersionState(t) {
    this.newVaadinVersionState = t;
  }
  setPointerEventsDisabledForScrolling(t) {
    this.pointerEventsDisabledForScrolling = t;
  }
  setEditComponent(t) {
    this.editComponent = t;
  }
  clearEditComponent() {
    this.editComponent = void 0;
  }
  toggleServerRequiringFeatureFlag(t) {
    const r = [...this.serverRestartRequiringToggledFeatureFlags], n = r.findIndex((i) => i.id === t.id);
    n === -1 ? r.push(t) : r.splice(n, 1), this.serverRestartRequiringToggledFeatureFlags = r;
  }
  setAppTheme(t) {
    this.appTheme = t;
  }
  setEscapeEventHandler(t) {
    this.escapeEventHandler = t;
  }
  clearEscapeEventHandler() {
    this.escapeEventHandler = void 0;
  }
}
const wl = (e, t, r) => t >= e.left && t <= e.right && r >= e.top && r <= e.bottom, pa = (e) => {
  const t = [];
  let r = Vn(e);
  for (; r; )
    t.push(r), r = Vn(r);
  return t;
}, xl = (e) => {
  if (e.length === 0)
    return new DOMRect();
  let t = Number.MAX_VALUE, r = Number.MAX_VALUE, n = Number.MIN_VALUE, i = Number.MIN_VALUE;
  const a = new DOMRect();
  return e.forEach((o) => {
    const s = o.getBoundingClientRect();
    s.x < t && (t = s.x), s.y < r && (r = s.y), s.right > n && (n = s.right), s.bottom > i && (i = s.bottom);
  }), a.x = t, a.y = r, a.width = n - t, a.height = i - r, a;
}, ar = (e, t) => {
  let r = e;
  for (; !(r instanceof HTMLElement && r.localName === `${he}main`); ) {
    if (!r.isConnected)
      return null;
    if (r.parentNode)
      r = r.parentNode;
    else if (r.host)
      r = r.host;
    else
      return null;
    if (r instanceof HTMLElement && r.localName === t)
      return r;
  }
  return null;
};
function Vn(e) {
  return e.parentElement ?? e.parentNode?.host;
}
function Br(e) {
  if (e.assignedSlot)
    return Br(e.assignedSlot);
  if (e.parentElement)
    return e.parentElement;
  if (e.parentNode instanceof ShadowRoot)
    return e.parentNode.host;
}
function fe(e) {
  if (e instanceof Node) {
    const t = pa(e);
    return e instanceof HTMLElement && t.push(e), t.map((r) => r.localName).some((r) => r.startsWith(he));
  }
  return !1;
}
function In(e) {
  return e instanceof Element;
}
function zn(e) {
  return e.startsWith("vaadin-") ? e.substring(7).split("-").map((n) => n.charAt(0).toUpperCase() + n.slice(1)).join(" ") : e;
}
function Un(e) {
  if (!e)
    return;
  if (e.id)
    return `#${e.id}`;
  if (!e.children)
    return;
  const t = Array.from(e.children).find((n) => n.localName === "label");
  if (t)
    return t.outerText.trim();
  const r = Array.from(e.childNodes).find(
    (n) => n.nodeType === Node.TEXT_NODE && n.textContent && n.textContent.trim().length > 0
  );
  if (r && r.textContent)
    return r.textContent.trim();
}
function Ol(e) {
  return e instanceof Element && typeof e.getBoundingClientRect == "function" ? e.getBoundingClientRect() : El(e);
}
function El(e) {
  const t = document.createRange();
  t.selectNode(e);
  const r = t.getBoundingClientRect();
  return t.detach(), r;
}
function kl() {
  let e = document.activeElement;
  for (; e?.shadowRoot && e.shadowRoot.activeElement; )
    e = e.shadowRoot.activeElement;
  return e;
}
function Cl(e) {
  let t = Br(e);
  for (; t && t !== document.body; ) {
    const r = window.getComputedStyle(t), n = r.overflowY, i = r.overflowX, a = /(auto|scroll)/.test(n) && t.scrollHeight > t.clientHeight, o = /(auto|scroll)/.test(i) && t.scrollWidth > t.clientWidth;
    if (a || o)
      return t;
    t = Br(t);
  }
  return document.documentElement;
}
function Al(e, t) {
  return Sl(e, t) && Pl(t);
}
function Sl(e, t) {
  const r = Cl(e), n = r.getBoundingClientRect();
  if (r === document.documentElement || r === document.body) {
    const i = window.innerWidth || document.documentElement.clientWidth, a = window.innerHeight || document.documentElement.clientHeight;
    return t.top < a && t.bottom > 0 && t.left < i && t.right > 0;
  }
  return t.bottom > n.top && t.top < n.bottom && t.right > n.left && t.left < n.right;
}
function Pl(e) {
  return e.bottom > 0 && e.right > 0 && e.top < window.innerHeight && e.left < window.innerWidth;
}
function Fd(e) {
  return e instanceof HTMLElement;
}
function Zd(e) {
  const t = fa(e), r = xl(t);
  !t.every((i) => Al(i, r)) && t.length > 0 && t[0].scrollIntoView();
}
function fa(e) {
  const t = e;
  if (!t)
    return [];
  const { element: r } = t;
  if (r) {
    const n = t.element;
    if (r.localName === "vaadin-popover" || r.localName === "vaadin-dialog") {
      const i = n._overlayElement.shadowRoot.querySelector('[part="overlay"]');
      if (i)
        return [i];
    }
    if (r.localName === "vaadin-login-overlay") {
      const i = n.shadowRoot?.querySelector("vaadin-login-overlay-wrapper")?.shadowRoot?.querySelector('[part="card"]');
      if (i)
        return [i];
    }
    return [r];
  }
  return t.children.flatMap((n) => fa(n));
}
function Wd(e, t) {
  function r(n) {
    if (n instanceof ShadowRoot)
      for (const i of n.children) {
        const a = r(i);
        if (a)
          return a;
      }
    else if (n instanceof Element) {
      if (n.tagName.toLowerCase() === t.toLowerCase())
        return n;
      for (const i of n.children) {
        const a = r(i);
        if (a)
          return a;
      }
    }
  }
  return r(e);
}
function Dl(e) {
  const { clientX: t, clientY: r } = e;
  return t === 0 && r === 0 || // Safari and Firefox returns the last position where mouse left the screen with adding some offset value, something like 356, -1.
  !wl(document.documentElement.getBoundingClientRect(), t, r);
}
function Ln(e) {
  if (e.localName === "vaadin-login-overlay")
    return !1;
  const t = Ol(e);
  return t.width === 0 || t.height === 0;
}
function Nl(e) {
  return typeof e.close == "function";
}
function Mn(e) {
  return Nl(e) ? (e.close(), !0) : e.localName === "vaadin-popover" ? (e.opened = !1, !0) : !1;
}
var va = /* @__PURE__ */ ((e) => (e["vaadin-combo-box"] = "vaadin-combo-box", e["vaadin-date-picker"] = "vaadin-date-picker", e["vaadin-dialog"] = "vaadin-dialog", e["vaadin-multi-select-combo-box"] = "vaadin-multi-select-combo-box", e["vaadin-select"] = "vaadin-select", e["vaadin-time-picker"] = "vaadin-time-picker", e["vaadin-popover"] = "vaadin-popover", e))(va || {});
const Re = {
  "vaadin-combo-box": {
    hideOnActivation: !0,
    open: (e) => Ut(e),
    close: (e) => Lt(e)
  },
  "vaadin-select": {
    hideOnActivation: !0,
    open: (e) => {
      const t = e;
      ga(t, t._overlayElement), t.opened = !0;
    },
    close: (e) => {
      const t = e;
      ma(t, t._overlayElement), t.opened = !1;
    }
  },
  "vaadin-multi-select-combo-box": {
    hideOnActivation: !0,
    open: (e) => Ut(e),
    close: (e) => {
      Lt(e), e.removeAttribute("focused");
    }
  },
  "vaadin-date-picker": {
    hideOnActivation: !0,
    open: (e) => Ut(e),
    close: (e) => Lt(e)
  },
  "vaadin-time-picker": {
    hideOnActivation: !0,
    open: (e) => Ut(e),
    close: (e) => {
      Lt(e), e.removeAttribute("focused");
    }
  },
  "vaadin-dialog": {
    hideOnActivation: !1
  },
  "vaadin-popover": {
    hideOnActivation: !1
  }
}, ha = (e) => {
  e.preventDefault(), e.stopImmediatePropagation();
}, Ut = (e) => {
  e.addEventListener("focusout", ha, { capture: !0 }), ga(e), e.opened = !0;
}, Lt = (e) => {
  ma(e), e.removeAttribute("focused"), e.removeEventListener("focusout", ha, { capture: !0 }), e.opened = !1;
}, ga = (e, t) => {
  const r = t ?? e.$.overlay;
  r.__oldModeless = r.modeless, r.modeless = !0;
}, ma = (e, t) => {
  const r = t ?? e.$.overlay;
  r.modeless = r.__oldModeless !== void 0 ? r.__oldModeless : r.modeless, delete r.__oldModeless;
};
class Tl {
  constructor() {
    this.openedOverlayOwners = /* @__PURE__ */ new Set(), this.overlayCloseEventListener = (t) => {
      fe(t.detail?.overlay) || (window.Vaadin.copilot._uiState.active || fe(t.detail.sourceEvent?.target)) && (t.preventDefault(), t.stopImmediatePropagation());
    };
  }
  /**
   * Modifies pointer-events property to auto if dialog overlay is present on body element. <br/>
   * Overriding closeOnOutsideClick method in order to keep overlay present while copilot is active
   * @private
   */
  onCopilotActivation() {
    const t = this.findComponentWithOpenOverlay();
    if (!t)
      return;
    const r = Re[t.localName];
    r && (r.hideOnActivation && r.close ? r.close(t) : document.body.style.getPropertyValue("pointer-events") === "none" && document.body.style.removeProperty("pointer-events"));
  }
  findComponentWithOpenOverlay() {
    let t;
    for (t in Re) {
      const r = document.querySelector(`${t}[opened]`);
      if (r)
        return r;
    }
    return null;
  }
  /**
   * Restores pointer-events state on deactivation. <br/>
   * Closes opened overlays while using copilot.
   * @private
   */
  onCopilotDeactivation() {
    this.openedOverlayOwners.forEach((r) => {
      const n = Re[r.localName];
      n && n.close && n.close(r);
    }), document.body.querySelector("vaadin-dialog[opened]") && document.body.style.setProperty("pointer-events", "none");
  }
  getOwner(t) {
    const r = t;
    if (r._comboBox)
      return r._comboBox._comboBox ?? r._comboBox;
    if (r.owner)
      return r.owner;
    if (r?.__focusRestorationController?.focusNode?.parentElement)
      return r?.__focusRestorationController?.focusNode?.parentElement;
  }
  addOverlayOutsideClickEvent() {
    document.documentElement.addEventListener("vaadin-overlay-close", this.overlayCloseEventListener, {
      capture: !0
    });
  }
  removeOverlayOutsideClickEvent() {
    document.documentElement.removeEventListener("vaadin-overlay-close", this.overlayCloseEventListener);
  }
  toggle(t) {
    const r = Re[t.localName];
    this.isOverlayActive(t) ? (r.close(t), this.openedOverlayOwners.delete(t)) : (r.open(t), this.openedOverlayOwners.add(t));
  }
  isOverlayActive(t) {
    const r = Re[t.localName];
    return r.active ? r.active(t) : t.hasAttribute("opened");
  }
  overlayStatus(t) {
    if (!t)
      return { visible: !1 };
    const r = t.localName;
    let n = Object.keys(va).includes(r);
    if (!n)
      return { visible: !1 };
    const i = Re[t.localName];
    if (!i.open || !i.close)
      return { visible: !1 };
    i.hasOverlay && (n = i.hasOverlay(t));
    const a = this.isOverlayActive(t);
    return { visible: n, active: a };
  }
}
class ba {
  constructor() {
    this.promise = new Promise((t) => {
      this.resolveInit = t;
    });
  }
  done(t) {
    this.resolveInit(t);
  }
}
class Vl {
  constructor() {
    this.dismissedNotifications = [], this.termsSummaryDismissed = !1, this.activationButtonPosition = null, this.paletteState = null, this.activationShortcut = !0, this.activationAnimation = !0, this.recentSwitchedUsernames = [], this.newVersionPreReleasesVisible = !1, this.aiUsageAllowed = "ask", this.sendErrorReportsAllowed = !0, this.feedbackDisplayedAtLeastOnce = !1, this.aiProvider = "ANY", $e(this), this.initializer = new ba(), this.initializer.promise.then(() => {
      $r(
        () => JSON.stringify(this),
        () => {
          te("copilot-set-machine-configuration", { conf: JSON.stringify(jn(this)) });
        }
      );
    }), window.Vaadin.copilot.eventbus.on("copilot-machine-configuration", (t) => {
      const r = t.detail.conf;
      r.aiProvider || (r.aiProvider = "ANY"), Object.assign(this, jn(r)), this.initializer.done(!0), t.preventDefault();
    }), this.loadData();
  }
  loadData() {
    te("copilot-get-machine-configuration", {});
  }
  addDismissedNotification(t) {
    this.dismissedNotifications = [...this.dismissedNotifications, t];
  }
  getDismissedNotifications() {
    return this.dismissedNotifications;
  }
  clearDismissedNotifications() {
    this.dismissedNotifications = [];
  }
  setTermsSummaryDismissed(t) {
    this.termsSummaryDismissed = t;
  }
  isTermsSummaryDismissed() {
    return this.termsSummaryDismissed;
  }
  getActivationButtonPosition() {
    return this.activationButtonPosition;
  }
  setActivationButtonPosition(t) {
    this.activationButtonPosition = t;
  }
  getPaletteState() {
    return this.paletteState;
  }
  setPaletteState(t) {
    this.paletteState = t;
  }
  isActivationShortcut() {
    return this.activationShortcut;
  }
  setActivationShortcut(t) {
    this.activationShortcut = t;
  }
  isActivationAnimation() {
    return this.activationAnimation;
  }
  setActivationAnimation(t) {
    this.activationAnimation = t;
  }
  getRecentSwitchedUsernames() {
    return this.recentSwitchedUsernames;
  }
  setRecentSwitchedUsernames(t) {
    this.recentSwitchedUsernames = t;
  }
  addRecentSwitchedUsername(t) {
    this.setRecentSwitchedUsernames(hl([t, ...this.recentSwitchedUsernames]));
  }
  removeRecentSwitchedUsername(t) {
    this.setRecentSwitchedUsernames(this.recentSwitchedUsernames.filter((r) => r !== t));
  }
  getNewVersionPreReleasesVisible() {
    return this.newVersionPreReleasesVisible;
  }
  setNewVersionPreReleasesVisible(t) {
    this.newVersionPreReleasesVisible = t;
  }
  setSendErrorReportsAllowed(t) {
    this.sendErrorReportsAllowed = t;
  }
  isSendErrorReportsAllowed() {
    return this.sendErrorReportsAllowed;
  }
  setAIUsageAllowed(t) {
    this.aiUsageAllowed = t;
  }
  isAIUsageAllowed() {
    return this.aiUsageAllowed;
  }
  getAIProvider() {
    return this.aiProvider;
  }
  setAIProvider(t) {
    this.aiProvider = t;
  }
  setFeedbackDisplayedAtLeastOnce(t) {
    this.feedbackDisplayedAtLeastOnce = t;
  }
  isFeedbackDisplayedAtLeastOnce() {
    return this.feedbackDisplayedAtLeastOnce;
  }
}
function jn(e) {
  const t = { ...e };
  return delete t.initializer, t;
}
class Il {
  constructor() {
    this._previewActivated = !1, this._remainingTimeInMillis = -1, this._active = !1, this._configurationLoaded = !1, $e(this);
  }
  setConfiguration(t) {
    this._previewActivated = t.previewActivated, t.previewActivated ? this._remainingTimeInMillis = t.remainingTimeInMillis : this._remainingTimeInMillis = -1, this._active = t.active, this._configurationLoaded = !0;
  }
  get previewActivated() {
    return this._previewActivated;
  }
  get remainingTimeInMillis() {
    return this._remainingTimeInMillis;
  }
  get active() {
    return this._active;
  }
  get configurationLoaded() {
    return this._configurationLoaded;
  }
  get expired() {
    return this.previewActivated && !this.active;
  }
  reset() {
    this._previewActivated = !1, this._active = !1, this._configurationLoaded = !1, this._remainingTimeInMillis = -1;
  }
  loadPreviewConfiguration() {
    Nt(`${he}get-preview`, {}, (t) => {
      const r = t.data;
      this.setConfiguration(r);
    }).catch((t) => {
      Promise.resolve().then(() => Nc).then((r) => {
        r.handleCopilotError("Load preview configuration failed", t);
      });
    });
  }
}
const Rn = "copilot-conf";
class We {
  static get sessionConfiguration() {
    const t = sessionStorage.getItem(Rn);
    return t ? JSON.parse(t) : {};
  }
  static saveCopilotActivation(t) {
    const r = this.sessionConfiguration;
    r.active = t, this.persist(r);
  }
  static getCopilotActivation() {
    return this.sessionConfiguration.active;
  }
  static saveDrawerSize(t, r) {
    const n = this.sessionConfiguration;
    n.drawerSizes = n.drawerSizes ?? {}, n.drawerSizes[t] = r, this.persist(n);
  }
  static getDrawerSize(t) {
    const r = this.sessionConfiguration;
    if (r.drawerSizes)
      return r.drawerSizes[t];
  }
  static savePanelConfigurations(t) {
    const r = this.sessionConfiguration;
    r.sectionPanelState = t, this.persist(r);
  }
  static getPanelConfigurations() {
    return this.sessionConfiguration.sectionPanelState;
  }
  static persist(t) {
    sessionStorage.setItem(Rn, JSON.stringify(t));
  }
  static savePrompts(t) {
    const r = this.sessionConfiguration;
    r.prompts = t, this.persist(r);
  }
  static getPrompts() {
    return this.sessionConfiguration.prompts || [];
  }
  static saveCurrentSelection(t) {
    const r = this.sessionConfiguration;
    r.selection = r.selection ?? {}, r.selection && (r.selection.current = t, r.selection.location = window.location.pathname, this.persist(r));
  }
  static savePendingSelection(t) {
    const r = this.sessionConfiguration;
    r.selection = r.selection ?? {}, r.selection && (r.selection.pending = t, r.selection.location = window.location.pathname, this.persist(r));
  }
  static getCurrentSelection() {
    const t = this.sessionConfiguration.selection;
    if (t?.location === window.location.pathname)
      return t.current;
  }
  static getPendingSelection() {
    const t = this.sessionConfiguration.selection;
    if (t?.location === window.location.pathname)
      return t.pending;
  }
  static saveDrillDownContextReference(t) {
    const r = this.sessionConfiguration;
    r.drillDownContext = r.drillDownContext ?? {}, r.drillDownContext && (r.drillDownContext.location = window.location.pathname, r.drillDownContext.stack = t, this.persist(r));
  }
  static getDrillDownContextReference() {
    const t = this.sessionConfiguration;
    if (t?.drillDownContext?.location === window.location.pathname)
      return t.drillDownContext?.stack;
  }
}
class zl {
  constructor() {
    this._panels = [], this._attentionRequiredPanelTag = null, this._floatingPanelsZIndexOrder = [], this.renderedPanels = /* @__PURE__ */ new Set(), this.customTags = /* @__PURE__ */ new Map(), $e(this), this.restorePositions();
  }
  shouldRender(t) {
    return this.renderedPanels.has(t);
  }
  restorePositions() {
    const t = We.getPanelConfigurations();
    t && (this._panels = this._panels.map((r) => {
      const n = t.find((i) => i.tag === r.tag);
      return n && (r = Object.assign(r, { ...n })), r;
    }));
  }
  /**
   * Brings a given floating panel to the front.
   *
   * @param panelTag the tag name of the panel
   */
  bringToFront(t) {
    this._floatingPanelsZIndexOrder = this._floatingPanelsZIndexOrder.filter((r) => r !== t), this.getPanelByTag(t)?.floating && this._floatingPanelsZIndexOrder.push(t);
  }
  /**
   * Returns the focused z-index of floating panel as following order
   * <ul>
   *     <li>Returns 50 for last(focused) element </li>
   *     <li>Returns the index of element in list(starting from 0) </li>
   *     <li>Returns 0 if panel is not in the list</li>
   * </ul>
   * @param panelTag
   */
  getFloatingPanelZIndex(t) {
    const r = this._floatingPanelsZIndexOrder.findIndex((n) => n === t);
    return r === this._floatingPanelsZIndexOrder.length - 1 ? 50 : r === -1 ? 0 : r;
  }
  get floatingPanelsZIndexOrder() {
    return this._floatingPanelsZIndexOrder;
  }
  get attentionRequiredPanelTag() {
    return this._attentionRequiredPanelTag;
  }
  set attentionRequiredPanelTag(t) {
    this._attentionRequiredPanelTag = t;
  }
  getAttentionRequiredPanelConfiguration() {
    return this._panels.find((t) => t.tag === this._attentionRequiredPanelTag);
  }
  clearAttention() {
    this._attentionRequiredPanelTag = null;
  }
  get panels() {
    return this._panels;
  }
  addPanel(t) {
    if (this.getPanelByTag(t.tag))
      return;
    this._panels.push(t), this.restorePositions();
    const r = this.getPanelByTag(t.tag);
    if (r)
      (r.eager || r.expanded) && this.renderedPanels.add(t.tag);
    else throw new Error(`Panel configuration not found for tag ${t.tag}`);
  }
  getPanelByTag(t) {
    return this._panels.find((r) => r.tag === t);
  }
  updatePanel(t, r) {
    const n = [...this._panels], i = n.find((a) => a.tag === t);
    if (i) {
      for (const a in r)
        i[a] = r[a];
      i.expanded && this.renderedPanels.add(i.tag), r.floating === !1 && (this._floatingPanelsZIndexOrder = this._floatingPanelsZIndexOrder.filter((a) => a !== t)), this._panels = n, We.savePanelConfigurations(this._panels);
    }
  }
  updateOrders(t) {
    const r = [...this._panels];
    r.forEach((n) => {
      const i = t.find((a) => a.tag === n.tag);
      i && (n.panelOrder = i.order);
    }), this._panels = r, We.savePanelConfigurations(r);
  }
  removePanel(t) {
    const r = this._panels.findIndex((n) => n.tag === t);
    r < 0 || (this._panels.splice(r, 1), We.savePanelConfigurations(this._panels));
  }
  setCustomPanelHeader(t, r) {
    this.customTags.set(t.tag, r);
  }
  getPanelHeader(t) {
    return this.customTags.get(t.tag) ?? t.header;
  }
  clearCustomPanelHeader(t) {
    this.customTags.delete(t.tag);
  }
}
class Ul {
  constructor() {
    this.supportsHilla = !0, this.springSecurityEnabled = !1, this.springJpaDataEnabled = !1, this.springJpaDatasourceInitialization = !1, this.springApplication = !1, this.urlPrefix = "", this.serverVersions = [], this.clientVersions = [{ name: "Browser", version: navigator.userAgent }], $e(this);
  }
  setSupportsHilla(t) {
    this.supportsHilla = t;
  }
  setSpringSecurityEnabled(t) {
    this.springSecurityEnabled = t;
  }
  setSpringJpaDataEnabled(t) {
    this.springJpaDataEnabled = t;
  }
  setSpringJpaDatasourceInitialization(t) {
    this.springJpaDatasourceInitialization = t;
  }
  setSpringApplication(t) {
    this.springApplication = t;
  }
  setUrlPrefix(t) {
    this.urlPrefix = t;
  }
  setServerVersions(t) {
    this.serverVersions = t;
  }
  setClientVersions(t) {
    this.clientVersions = t;
  }
  setJdkInfo(t) {
    this.jdkInfo = t;
  }
}
class Ll {
  constructor() {
    this.palette = { components: [] }, $e(this), this.initializer = new ba(), this.initializer.promise.then(() => {
      $r(
        () => JSON.stringify(this),
        () => {
          te("copilot-set-project-state-configuration", { conf: JSON.stringify(qn(this)) });
        }
      );
    }), window.Vaadin.copilot.eventbus.on("copilot-project-state-configuration", (t) => {
      const r = t.detail.conf;
      Object.assign(this, qn(r)), this.initializer.done(!0), t.preventDefault();
    }), this.loadData();
  }
  loadData() {
    te("copilot-get-project-state-configuration", {});
  }
  addPaletteCustomComponent(t) {
    return (this.palette?.components ?? []).find((i) => Sr(i, t)) ? !1 : (this.palette || (this.palette = { components: [] }), this.palette = JSON.parse(JSON.stringify(this.palette)), this.palette.components.push(t), !0);
  }
  removePaletteCustomComponent(t) {
    if (this.palette) {
      const r = this.palette.components.findIndex(
        (n) => Sr(n, t)
      );
      r > -1 && this.palette.components.splice(r, 1);
    }
  }
  updatePaletteCustomComponent(t, r) {
    if (!this.palette || !this.palette.components)
      return;
    const n = [...this.palette.components], i = n.findIndex((a) => Sr(a, t));
    i !== -1 && (n[i] = { ...t, ...r }), this.palette.components = n;
  }
  paletteCustomComponentExist(t, r) {
    return !this.palette || !this.palette.components ? !1 : t ? this.palette.components.findIndex(
      (n) => n.java && !n.react && n.javaClassName === t
    ) !== -1 : r ? this.palette.components.findIndex((n) => !n.java && n.react && n.template === r) !== -1 : !1;
  }
  get paletteComponents() {
    return this.palette?.components || [];
  }
}
function qn(e) {
  const t = { ...e };
  return delete t.initializer, t;
}
function Sr(e, t) {
  return e.java ? t.java ? e.javaClassName === t.javaClassName : !1 : e.react && t.react ? e.template === t.template : !1;
}
window.Vaadin ??= {};
window.Vaadin.copilot ??= {};
window.Vaadin.copilot.plugins = [];
window.Vaadin.copilot._uiState = new yl();
window.Vaadin.copilot.eventbus = new Ha();
window.Vaadin.copilot.overlayManager = new Tl();
window.Vaadin.copilot._machineState = new Vl();
window.Vaadin.copilot._storedProjectState = new Ll();
window.Vaadin.copilot._previewState = new Il();
window.Vaadin.copilot._sectionPanelUiState = new zl();
window.Vaadin.copilot._earlyProjectState = new Ul();
/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
const Ml = (e) => (t, r) => {
  r !== void 0 ? r.addInitializer(() => {
    customElements.define(e, t);
  }) : customElements.define(e, t);
};
/**
 * @license
 * Copyright 2019 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
const Kt = globalThis, nn = Kt.ShadowRoot && (Kt.ShadyCSS === void 0 || Kt.ShadyCSS.nativeShadow) && "adoptedStyleSheets" in Document.prototype && "replace" in CSSStyleSheet.prototype, an = Symbol(), Kn = /* @__PURE__ */ new WeakMap();
let ya = class {
  constructor(t, r, n) {
    if (this._$cssResult$ = !0, n !== an) throw Error("CSSResult is not constructable. Use `unsafeCSS` or `css` instead.");
    this.cssText = t, this.t = r;
  }
  get styleSheet() {
    let t = this.o;
    const r = this.t;
    if (nn && t === void 0) {
      const n = r !== void 0 && r.length === 1;
      n && (t = Kn.get(r)), t === void 0 && ((this.o = t = new CSSStyleSheet()).replaceSync(this.cssText), n && Kn.set(r, t));
    }
    return t;
  }
  toString() {
    return this.cssText;
  }
};
const L = (e) => new ya(typeof e == "string" ? e : e + "", void 0, an), jl = (e, ...t) => {
  const r = e.length === 1 ? e[0] : t.reduce((n, i, a) => n + ((o) => {
    if (o._$cssResult$ === !0) return o.cssText;
    if (typeof o == "number") return o;
    throw Error("Value passed to 'css' function must be a 'css' function result: " + o + ". Use 'unsafeCSS' to pass non-literal values, but take care to ensure page security.");
  })(i) + e[a + 1], e[0]);
  return new ya(r, e, an);
}, Rl = (e, t) => {
  if (nn) e.adoptedStyleSheets = t.map((r) => r instanceof CSSStyleSheet ? r : r.styleSheet);
  else for (const r of t) {
    const n = document.createElement("style"), i = Kt.litNonce;
    i !== void 0 && n.setAttribute("nonce", i), n.textContent = r.cssText, e.appendChild(n);
  }
}, Bn = nn ? (e) => e : (e) => e instanceof CSSStyleSheet ? ((t) => {
  let r = "";
  for (const n of t.cssRules) r += n.cssText;
  return L(r);
})(e) : e;
/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
const { is: ql, defineProperty: Kl, getOwnPropertyDescriptor: Bl, getOwnPropertyNames: Fl, getOwnPropertySymbols: Zl, getPrototypeOf: Wl } = Object, br = globalThis, Fn = br.trustedTypes, Hl = Fn ? Fn.emptyScript : "", Xl = br.reactiveElementPolyfillSupport, vt = (e, t) => e, Fr = { toAttribute(e, t) {
  switch (t) {
    case Boolean:
      e = e ? Hl : null;
      break;
    case Object:
    case Array:
      e = e == null ? e : JSON.stringify(e);
  }
  return e;
}, fromAttribute(e, t) {
  let r = e;
  switch (t) {
    case Boolean:
      r = e !== null;
      break;
    case Number:
      r = e === null ? null : Number(e);
      break;
    case Object:
    case Array:
      try {
        r = JSON.parse(e);
      } catch {
        r = null;
      }
  }
  return r;
} }, wa = (e, t) => !ql(e, t), Zn = { attribute: !0, type: String, converter: Fr, reflect: !1, useDefault: !1, hasChanged: wa };
Symbol.metadata ??= Symbol("metadata"), br.litPropertyMetadata ??= /* @__PURE__ */ new WeakMap();
let Be = class extends HTMLElement {
  static addInitializer(t) {
    this._$Ei(), (this.l ??= []).push(t);
  }
  static get observedAttributes() {
    return this.finalize(), this._$Eh && [...this._$Eh.keys()];
  }
  static createProperty(t, r = Zn) {
    if (r.state && (r.attribute = !1), this._$Ei(), this.prototype.hasOwnProperty(t) && ((r = Object.create(r)).wrapped = !0), this.elementProperties.set(t, r), !r.noAccessor) {
      const n = Symbol(), i = this.getPropertyDescriptor(t, n, r);
      i !== void 0 && Kl(this.prototype, t, i);
    }
  }
  static getPropertyDescriptor(t, r, n) {
    const { get: i, set: a } = Bl(this.prototype, t) ?? { get() {
      return this[r];
    }, set(o) {
      this[r] = o;
    } };
    return { get: i, set(o) {
      const s = i?.call(this);
      a?.call(this, o), this.requestUpdate(t, s, n);
    }, configurable: !0, enumerable: !0 };
  }
  static getPropertyOptions(t) {
    return this.elementProperties.get(t) ?? Zn;
  }
  static _$Ei() {
    if (this.hasOwnProperty(vt("elementProperties"))) return;
    const t = Wl(this);
    t.finalize(), t.l !== void 0 && (this.l = [...t.l]), this.elementProperties = new Map(t.elementProperties);
  }
  static finalize() {
    if (this.hasOwnProperty(vt("finalized"))) return;
    if (this.finalized = !0, this._$Ei(), this.hasOwnProperty(vt("properties"))) {
      const r = this.properties, n = [...Fl(r), ...Zl(r)];
      for (const i of n) this.createProperty(i, r[i]);
    }
    const t = this[Symbol.metadata];
    if (t !== null) {
      const r = litPropertyMetadata.get(t);
      if (r !== void 0) for (const [n, i] of r) this.elementProperties.set(n, i);
    }
    this._$Eh = /* @__PURE__ */ new Map();
    for (const [r, n] of this.elementProperties) {
      const i = this._$Eu(r, n);
      i !== void 0 && this._$Eh.set(i, r);
    }
    this.elementStyles = this.finalizeStyles(this.styles);
  }
  static finalizeStyles(t) {
    const r = [];
    if (Array.isArray(t)) {
      const n = new Set(t.flat(1 / 0).reverse());
      for (const i of n) r.unshift(Bn(i));
    } else t !== void 0 && r.push(Bn(t));
    return r;
  }
  static _$Eu(t, r) {
    const n = r.attribute;
    return n === !1 ? void 0 : typeof n == "string" ? n : typeof t == "string" ? t.toLowerCase() : void 0;
  }
  constructor() {
    super(), this._$Ep = void 0, this.isUpdatePending = !1, this.hasUpdated = !1, this._$Em = null, this._$Ev();
  }
  _$Ev() {
    this._$ES = new Promise((t) => this.enableUpdating = t), this._$AL = /* @__PURE__ */ new Map(), this._$E_(), this.requestUpdate(), this.constructor.l?.forEach((t) => t(this));
  }
  addController(t) {
    (this._$EO ??= /* @__PURE__ */ new Set()).add(t), this.renderRoot !== void 0 && this.isConnected && t.hostConnected?.();
  }
  removeController(t) {
    this._$EO?.delete(t);
  }
  _$E_() {
    const t = /* @__PURE__ */ new Map(), r = this.constructor.elementProperties;
    for (const n of r.keys()) this.hasOwnProperty(n) && (t.set(n, this[n]), delete this[n]);
    t.size > 0 && (this._$Ep = t);
  }
  createRenderRoot() {
    const t = this.shadowRoot ?? this.attachShadow(this.constructor.shadowRootOptions);
    return Rl(t, this.constructor.elementStyles), t;
  }
  connectedCallback() {
    this.renderRoot ??= this.createRenderRoot(), this.enableUpdating(!0), this._$EO?.forEach((t) => t.hostConnected?.());
  }
  enableUpdating(t) {
  }
  disconnectedCallback() {
    this._$EO?.forEach((t) => t.hostDisconnected?.());
  }
  attributeChangedCallback(t, r, n) {
    this._$AK(t, n);
  }
  _$ET(t, r) {
    const n = this.constructor.elementProperties.get(t), i = this.constructor._$Eu(t, n);
    if (i !== void 0 && n.reflect === !0) {
      const a = (n.converter?.toAttribute !== void 0 ? n.converter : Fr).toAttribute(r, n.type);
      this._$Em = t, a == null ? this.removeAttribute(i) : this.setAttribute(i, a), this._$Em = null;
    }
  }
  _$AK(t, r) {
    const n = this.constructor, i = n._$Eh.get(t);
    if (i !== void 0 && this._$Em !== i) {
      const a = n.getPropertyOptions(i), o = typeof a.converter == "function" ? { fromAttribute: a.converter } : a.converter?.fromAttribute !== void 0 ? a.converter : Fr;
      this._$Em = i, this[i] = o.fromAttribute(r, a.type) ?? this._$Ej?.get(i) ?? null, this._$Em = null;
    }
  }
  requestUpdate(t, r, n) {
    if (t !== void 0) {
      const i = this.constructor, a = this[t];
      if (n ??= i.getPropertyOptions(t), !((n.hasChanged ?? wa)(a, r) || n.useDefault && n.reflect && a === this._$Ej?.get(t) && !this.hasAttribute(i._$Eu(t, n)))) return;
      this.C(t, r, n);
    }
    this.isUpdatePending === !1 && (this._$ES = this._$EP());
  }
  C(t, r, { useDefault: n, reflect: i, wrapped: a }, o) {
    n && !(this._$Ej ??= /* @__PURE__ */ new Map()).has(t) && (this._$Ej.set(t, o ?? r ?? this[t]), a !== !0 || o !== void 0) || (this._$AL.has(t) || (this.hasUpdated || n || (r = void 0), this._$AL.set(t, r)), i === !0 && this._$Em !== t && (this._$Eq ??= /* @__PURE__ */ new Set()).add(t));
  }
  async _$EP() {
    this.isUpdatePending = !0;
    try {
      await this._$ES;
    } catch (r) {
      Promise.reject(r);
    }
    const t = this.scheduleUpdate();
    return t != null && await t, !this.isUpdatePending;
  }
  scheduleUpdate() {
    return this.performUpdate();
  }
  performUpdate() {
    if (!this.isUpdatePending) return;
    if (!this.hasUpdated) {
      if (this.renderRoot ??= this.createRenderRoot(), this._$Ep) {
        for (const [i, a] of this._$Ep) this[i] = a;
        this._$Ep = void 0;
      }
      const n = this.constructor.elementProperties;
      if (n.size > 0) for (const [i, a] of n) {
        const { wrapped: o } = a, s = this[i];
        o !== !0 || this._$AL.has(i) || s === void 0 || this.C(i, void 0, a, s);
      }
    }
    let t = !1;
    const r = this._$AL;
    try {
      t = this.shouldUpdate(r), t ? (this.willUpdate(r), this._$EO?.forEach((n) => n.hostUpdate?.()), this.update(r)) : this._$EM();
    } catch (n) {
      throw t = !1, this._$EM(), n;
    }
    t && this._$AE(r);
  }
  willUpdate(t) {
  }
  _$AE(t) {
    this._$EO?.forEach((r) => r.hostUpdated?.()), this.hasUpdated || (this.hasUpdated = !0, this.firstUpdated(t)), this.updated(t);
  }
  _$EM() {
    this._$AL = /* @__PURE__ */ new Map(), this.isUpdatePending = !1;
  }
  get updateComplete() {
    return this.getUpdateComplete();
  }
  getUpdateComplete() {
    return this._$ES;
  }
  shouldUpdate(t) {
    return !0;
  }
  update(t) {
    this._$Eq &&= this._$Eq.forEach((r) => this._$ET(r, this[r])), this._$EM();
  }
  updated(t) {
  }
  firstUpdated(t) {
  }
};
Be.elementStyles = [], Be.shadowRootOptions = { mode: "open" }, Be[vt("elementProperties")] = /* @__PURE__ */ new Map(), Be[vt("finalized")] = /* @__PURE__ */ new Map(), Xl?.({ ReactiveElement: Be }), (br.reactiveElementVersions ??= []).push("2.1.0");
const qe = Symbol("LitMobxRenderReaction"), Wn = Symbol("LitMobxRequestUpdate");
function Jl(e, t) {
  var r, n;
  return n = class extends e {
    constructor() {
      super(...arguments), this[r] = () => {
        this.requestUpdate();
      };
    }
    connectedCallback() {
      super.connectedCallback();
      const a = this.constructor.name || this.nodeName;
      this[qe] = new t(`${a}.update()`, this[Wn]), this.hasUpdated && this.requestUpdate();
    }
    disconnectedCallback() {
      super.disconnectedCallback(), this[qe] && (this[qe].dispose(), this[qe] = void 0);
    }
    update(a) {
      this[qe] ? this[qe].track(super.update.bind(this, a)) : super.update(a);
    }
  }, r = Wn, n;
}
function Gl(e) {
  return Jl(e, ee);
}
/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
const on = globalThis, or = on.trustedTypes, Hn = or ? or.createPolicy("lit-html", { createHTML: (e) => e }) : void 0, xa = "$lit$", ue = `lit$${Math.random().toFixed(9).slice(2)}$`, Oa = "?" + ue, Yl = `<${Oa}>`, ze = document, Ot = () => ze.createComment(""), Et = (e) => e === null || typeof e != "object" && typeof e != "function", sn = Array.isArray, Ql = (e) => sn(e) || typeof e?.[Symbol.iterator] == "function", Pr = `[ 	
\f\r]`, ot = /<(?:(!--|\/[^a-zA-Z])|(\/?[a-zA-Z][^>\s]*)|(\/?$))/g, Xn = /-->/g, Jn = />/g, Oe = RegExp(`>|${Pr}(?:([^\\s"'>=/]+)(${Pr}*=${Pr}*(?:[^ 	
\f\r"'\`<>=]|("|')|))|$)`, "g"), Gn = /'/g, Yn = /"/g, Ea = /^(?:script|style|textarea|title)$/i, ka = (e) => (t, ...r) => ({ _$litType$: e, strings: t, values: r }), le = ka(1), Gd = ka(2), ge = Symbol.for("lit-noChange"), E = Symbol.for("lit-nothing"), Qn = /* @__PURE__ */ new WeakMap(), Ae = ze.createTreeWalker(ze, 129);
function Ca(e, t) {
  if (!sn(e) || !e.hasOwnProperty("raw")) throw Error("invalid template strings array");
  return Hn !== void 0 ? Hn.createHTML(t) : t;
}
const _l = (e, t) => {
  const r = e.length - 1, n = [];
  let i, a = t === 2 ? "<svg>" : t === 3 ? "<math>" : "", o = ot;
  for (let s = 0; s < r; s++) {
    const l = e[s];
    let c, d, u = -1, v = 0;
    for (; v < l.length && (o.lastIndex = v, d = o.exec(l), d !== null); ) v = o.lastIndex, o === ot ? d[1] === "!--" ? o = Xn : d[1] !== void 0 ? o = Jn : d[2] !== void 0 ? (Ea.test(d[2]) && (i = RegExp("</" + d[2], "g")), o = Oe) : d[3] !== void 0 && (o = Oe) : o === Oe ? d[0] === ">" ? (o = i ?? ot, u = -1) : d[1] === void 0 ? u = -2 : (u = o.lastIndex - d[2].length, c = d[1], o = d[3] === void 0 ? Oe : d[3] === '"' ? Yn : Gn) : o === Yn || o === Gn ? o = Oe : o === Xn || o === Jn ? o = ot : (o = Oe, i = void 0);
    const h = o === Oe && e[s + 1].startsWith("/>") ? " " : "";
    a += o === ot ? l + Yl : u >= 0 ? (n.push(c), l.slice(0, u) + xa + l.slice(u) + ue + h) : l + ue + (u === -2 ? s : h);
  }
  return [Ca(e, a + (e[r] || "<?>") + (t === 2 ? "</svg>" : t === 3 ? "</math>" : "")), n];
};
class kt {
  constructor({ strings: t, _$litType$: r }, n) {
    let i;
    this.parts = [];
    let a = 0, o = 0;
    const s = t.length - 1, l = this.parts, [c, d] = _l(t, r);
    if (this.el = kt.createElement(c, n), Ae.currentNode = this.el.content, r === 2 || r === 3) {
      const u = this.el.content.firstChild;
      u.replaceWith(...u.childNodes);
    }
    for (; (i = Ae.nextNode()) !== null && l.length < s; ) {
      if (i.nodeType === 1) {
        if (i.hasAttributes()) for (const u of i.getAttributeNames()) if (u.endsWith(xa)) {
          const v = d[o++], h = i.getAttribute(u).split(ue), m = /([.?@])?(.*)/.exec(v);
          l.push({ type: 1, index: a, name: m[2], strings: h, ctor: m[1] === "." ? ec : m[1] === "?" ? tc : m[1] === "@" ? rc : yr }), i.removeAttribute(u);
        } else u.startsWith(ue) && (l.push({ type: 6, index: a }), i.removeAttribute(u));
        if (Ea.test(i.tagName)) {
          const u = i.textContent.split(ue), v = u.length - 1;
          if (v > 0) {
            i.textContent = or ? or.emptyScript : "";
            for (let h = 0; h < v; h++) i.append(u[h], Ot()), Ae.nextNode(), l.push({ type: 2, index: ++a });
            i.append(u[v], Ot());
          }
        }
      } else if (i.nodeType === 8) if (i.data === Oa) l.push({ type: 2, index: a });
      else {
        let u = -1;
        for (; (u = i.data.indexOf(ue, u + 1)) !== -1; ) l.push({ type: 7, index: a }), u += ue.length - 1;
      }
      a++;
    }
  }
  static createElement(t, r) {
    const n = ze.createElement("template");
    return n.innerHTML = t, n;
  }
}
function Ye(e, t, r = e, n) {
  if (t === ge) return t;
  let i = n !== void 0 ? r._$Co?.[n] : r._$Cl;
  const a = Et(t) ? void 0 : t._$litDirective$;
  return i?.constructor !== a && (i?._$AO?.(!1), a === void 0 ? i = void 0 : (i = new a(e), i._$AT(e, r, n)), n !== void 0 ? (r._$Co ??= [])[n] = i : r._$Cl = i), i !== void 0 && (t = Ye(e, i._$AS(e, t.values), i, n)), t;
}
let $l = class {
  constructor(t, r) {
    this._$AV = [], this._$AN = void 0, this._$AD = t, this._$AM = r;
  }
  get parentNode() {
    return this._$AM.parentNode;
  }
  get _$AU() {
    return this._$AM._$AU;
  }
  u(t) {
    const { el: { content: r }, parts: n } = this._$AD, i = (t?.creationScope ?? ze).importNode(r, !0);
    Ae.currentNode = i;
    let a = Ae.nextNode(), o = 0, s = 0, l = n[0];
    for (; l !== void 0; ) {
      if (o === l.index) {
        let c;
        l.type === 2 ? c = new rt(a, a.nextSibling, this, t) : l.type === 1 ? c = new l.ctor(a, l.name, l.strings, this, t) : l.type === 6 && (c = new nc(a, this, t)), this._$AV.push(c), l = n[++s];
      }
      o !== l?.index && (a = Ae.nextNode(), o++);
    }
    return Ae.currentNode = ze, i;
  }
  p(t) {
    let r = 0;
    for (const n of this._$AV) n !== void 0 && (n.strings !== void 0 ? (n._$AI(t, n, r), r += n.strings.length - 2) : n._$AI(t[r])), r++;
  }
};
class rt {
  get _$AU() {
    return this._$AM?._$AU ?? this._$Cv;
  }
  constructor(t, r, n, i) {
    this.type = 2, this._$AH = E, this._$AN = void 0, this._$AA = t, this._$AB = r, this._$AM = n, this.options = i, this._$Cv = i?.isConnected ?? !0;
  }
  get parentNode() {
    let t = this._$AA.parentNode;
    const r = this._$AM;
    return r !== void 0 && t?.nodeType === 11 && (t = r.parentNode), t;
  }
  get startNode() {
    return this._$AA;
  }
  get endNode() {
    return this._$AB;
  }
  _$AI(t, r = this) {
    t = Ye(this, t, r), Et(t) ? t === E || t == null || t === "" ? (this._$AH !== E && this._$AR(), this._$AH = E) : t !== this._$AH && t !== ge && this._(t) : t._$litType$ !== void 0 ? this.$(t) : t.nodeType !== void 0 ? this.T(t) : Ql(t) ? this.k(t) : this._(t);
  }
  O(t) {
    return this._$AA.parentNode.insertBefore(t, this._$AB);
  }
  T(t) {
    this._$AH !== t && (this._$AR(), this._$AH = this.O(t));
  }
  _(t) {
    this._$AH !== E && Et(this._$AH) ? this._$AA.nextSibling.data = t : this.T(ze.createTextNode(t)), this._$AH = t;
  }
  $(t) {
    const { values: r, _$litType$: n } = t, i = typeof n == "number" ? this._$AC(t) : (n.el === void 0 && (n.el = kt.createElement(Ca(n.h, n.h[0]), this.options)), n);
    if (this._$AH?._$AD === i) this._$AH.p(r);
    else {
      const a = new $l(i, this), o = a.u(this.options);
      a.p(r), this.T(o), this._$AH = a;
    }
  }
  _$AC(t) {
    let r = Qn.get(t.strings);
    return r === void 0 && Qn.set(t.strings, r = new kt(t)), r;
  }
  k(t) {
    sn(this._$AH) || (this._$AH = [], this._$AR());
    const r = this._$AH;
    let n, i = 0;
    for (const a of t) i === r.length ? r.push(n = new rt(this.O(Ot()), this.O(Ot()), this, this.options)) : n = r[i], n._$AI(a), i++;
    i < r.length && (this._$AR(n && n._$AB.nextSibling, i), r.length = i);
  }
  _$AR(t = this._$AA.nextSibling, r) {
    for (this._$AP?.(!1, !0, r); t && t !== this._$AB; ) {
      const n = t.nextSibling;
      t.remove(), t = n;
    }
  }
  setConnected(t) {
    this._$AM === void 0 && (this._$Cv = t, this._$AP?.(t));
  }
}
class yr {
  get tagName() {
    return this.element.tagName;
  }
  get _$AU() {
    return this._$AM._$AU;
  }
  constructor(t, r, n, i, a) {
    this.type = 1, this._$AH = E, this._$AN = void 0, this.element = t, this.name = r, this._$AM = i, this.options = a, n.length > 2 || n[0] !== "" || n[1] !== "" ? (this._$AH = Array(n.length - 1).fill(new String()), this.strings = n) : this._$AH = E;
  }
  _$AI(t, r = this, n, i) {
    const a = this.strings;
    let o = !1;
    if (a === void 0) t = Ye(this, t, r, 0), o = !Et(t) || t !== this._$AH && t !== ge, o && (this._$AH = t);
    else {
      const s = t;
      let l, c;
      for (t = a[0], l = 0; l < a.length - 1; l++) c = Ye(this, s[n + l], r, l), c === ge && (c = this._$AH[l]), o ||= !Et(c) || c !== this._$AH[l], c === E ? t = E : t !== E && (t += (c ?? "") + a[l + 1]), this._$AH[l] = c;
    }
    o && !i && this.j(t);
  }
  j(t) {
    t === E ? this.element.removeAttribute(this.name) : this.element.setAttribute(this.name, t ?? "");
  }
}
class ec extends yr {
  constructor() {
    super(...arguments), this.type = 3;
  }
  j(t) {
    this.element[this.name] = t === E ? void 0 : t;
  }
}
class tc extends yr {
  constructor() {
    super(...arguments), this.type = 4;
  }
  j(t) {
    this.element.toggleAttribute(this.name, !!t && t !== E);
  }
}
class rc extends yr {
  constructor(t, r, n, i, a) {
    super(t, r, n, i, a), this.type = 5;
  }
  _$AI(t, r = this) {
    if ((t = Ye(this, t, r, 0) ?? E) === ge) return;
    const n = this._$AH, i = t === E && n !== E || t.capture !== n.capture || t.once !== n.once || t.passive !== n.passive, a = t !== E && (n === E || i);
    i && this.element.removeEventListener(this.name, this, n), a && this.element.addEventListener(this.name, this, t), this._$AH = t;
  }
  handleEvent(t) {
    typeof this._$AH == "function" ? this._$AH.call(this.options?.host ?? this.element, t) : this._$AH.handleEvent(t);
  }
}
class nc {
  constructor(t, r, n) {
    this.element = t, this.type = 6, this._$AN = void 0, this._$AM = r, this.options = n;
  }
  get _$AU() {
    return this._$AM._$AU;
  }
  _$AI(t) {
    Ye(this, t);
  }
}
const ic = { I: rt }, ac = on.litHtmlPolyfillSupport;
ac?.(kt, rt), (on.litHtmlVersions ??= []).push("3.3.0");
const oc = (e, t, r) => {
  const n = r?.renderBefore ?? t;
  let i = n._$litPart$;
  if (i === void 0) {
    const a = r?.renderBefore ?? null;
    n._$litPart$ = i = new rt(t.insertBefore(Ot(), a), a, void 0, r ?? {});
  }
  return i._$AI(e), i;
};
/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
const ln = globalThis;
let ht = class extends Be {
  constructor() {
    super(...arguments), this.renderOptions = { host: this }, this._$Do = void 0;
  }
  createRenderRoot() {
    const t = super.createRenderRoot();
    return this.renderOptions.renderBefore ??= t.firstChild, t;
  }
  update(t) {
    const r = this.render();
    this.hasUpdated || (this.renderOptions.isConnected = this.isConnected), super.update(t), this._$Do = oc(r, this.renderRoot, this.renderOptions);
  }
  connectedCallback() {
    super.connectedCallback(), this._$Do?.setConnected(!0);
  }
  disconnectedCallback() {
    super.disconnectedCallback(), this._$Do?.setConnected(!1);
  }
  render() {
    return ge;
  }
};
ht._$litElement$ = !0, ht.finalized = !0, ln.litElementHydrateSupport?.({ LitElement: ht });
const sc = ln.litElementPolyfillSupport;
sc?.({ LitElement: ht });
(ln.litElementVersions ??= []).push("4.2.0");
class lc extends Gl(ht) {
}
class cc extends lc {
  constructor() {
    super(...arguments), this.disposers = [];
  }
  /**
   * Creates a MobX reaction using the given parameters and disposes it when this element is detached.
   *
   * This should be called from `connectedCallback` to ensure that the reaction is active also if the element is attached again later.
   */
  reaction(t, r, n) {
    this.disposers.push($r(t, r, n));
  }
  /**
   * Creates a MobX autorun using the given parameters and disposes it when this element is detached.
   *
   * This should be called from `connectedCallback` to ensure that the reaction is active also if the element is attached again later.
   */
  autorun(t, r) {
    this.disposers.push(Wi(t, r));
  }
  disconnectedCallback() {
    super.disconnectedCallback(), this.disposers.forEach((t) => {
      t();
    }), this.disposers = [];
  }
}
const se = window.Vaadin.copilot._sectionPanelUiState;
if (!se)
  throw new Error("Tried to access copilot section panel ui state before it was initialized.");
let ke = [];
const _n = [];
function $n(e) {
  e.init({
    addPanel: (t) => {
      se.addPanel(t);
    },
    send(t, r) {
      te(t, r);
    }
  });
}
function dc() {
  ke.push(import("./copilot-log-plugin-DlE776dL.js")), ke.push(import("./copilot-info-plugin-KCWUuzGn.js")), ke.push(import("./copilot-features-plugin-B2vgZ1XV.js")), ke.push(import("./copilot-feedback-plugin-Dy_jft8_.js")), ke.push(import("./copilot-shortcuts-plugin-ChRrahnj.js"));
}
function uc() {
  {
    const e = `https://cdn.vaadin.com/copilot/${ll}/copilot-plugins${cl}.js`;
    import(
      /* @vite-ignore */
      e
    ).catch((t) => {
      console.warn(`Unable to load plugins from ${e}. Some Copilot features are unavailable.`, t);
    });
  }
}
function pc() {
  Promise.all(ke).then(() => {
    const e = window.Vaadin;
    if (e.copilot.plugins) {
      const t = e.copilot.plugins;
      e.copilot.plugins.push = (r) => $n(r), Array.from(t).forEach((r) => {
        _n.includes(r) || ($n(r), _n.push(r));
      });
    }
  }), ke = [];
}
function _d(e) {
  return Object.assign({
    expanded: !0,
    expandable: !1,
    panelOrder: 0,
    floating: !1,
    width: 500,
    height: 500,
    floatingPosition: {
      top: 50,
      left: 350
    }
  }, e);
}
function st() {
  return document.body.querySelector("copilot-main");
}
class fc {
  constructor() {
    this.active = !1, this.activate = () => {
      this.active = !0, st()?.focus(), st()?.addEventListener("focusout", this.keepFocusInCopilot);
    }, this.deactivate = () => {
      this.active = !1, st()?.removeEventListener("focusout", this.keepFocusInCopilot);
    }, this.focusInEventListener = (t) => {
      this.active && (t.preventDefault(), t.stopPropagation(), fe(t.target) || requestAnimationFrame(() => {
        t.target.blur && t.target.blur(), st()?.focus();
      }));
    };
  }
  hostConnectedCallback() {
    const t = this.getApplicationRootElement();
    t && t instanceof HTMLElement && t.addEventListener("focusin", this.focusInEventListener);
  }
  hostDisconnectedCallback() {
    const t = this.getApplicationRootElement();
    t && t instanceof HTMLElement && t.removeEventListener("focusin", this.focusInEventListener);
  }
  getApplicationRootElement() {
    return document.body.firstElementChild;
  }
  keepFocusInCopilot(t) {
    t.preventDefault(), t.stopPropagation(), st()?.focus();
  }
}
const Mt = new fc(), y = window.Vaadin.copilot.eventbus;
if (!y)
  throw new Error("Tried to access copilot eventbus before it was initialized.");
const Ke = window.Vaadin.copilot.overlayManager, $d = {
  DragAndDrop: "Drag and Drop",
  RedoUndo: "Redo/Undo"
}, b = window.Vaadin.copilot._uiState;
if (!b)
  throw new Error("Tried to access copilot ui state before it was initialized.");
/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
const Aa = { CHILD: 2, ELEMENT: 6 }, Sa = (e) => (...t) => ({ _$litDirective$: e, values: t });
class Pa {
  constructor(t) {
  }
  get _$AU() {
    return this._$AM._$AU;
  }
  _$AT(t, r, n) {
    this._$Ct = t, this._$AM = r, this._$Ci = n;
  }
  _$AS(t, r) {
    return this.update(t, r);
  }
  update(t, r) {
    return this.render(...r);
  }
}
/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
class Zr extends Pa {
  constructor(t) {
    if (super(t), this.it = E, t.type !== Aa.CHILD) throw Error(this.constructor.directiveName + "() can only be used in child bindings");
  }
  render(t) {
    if (t === E || t == null) return this._t = void 0, this.it = t;
    if (t === ge) return t;
    if (typeof t != "string") throw Error(this.constructor.directiveName + "() called with a non-string value");
    if (t === this.it) return this._t;
    this.it = t;
    const r = [t];
    return r.raw = r, this._t = { _$litType$: this.constructor.resultType, strings: r, values: [] };
  }
}
Zr.directiveName = "unsafeHTML", Zr.resultType = 1;
const vc = Sa(Zr), nt = window.Vaadin.copilot._machineState;
if (!nt)
  throw new Error("Trying to use stored machine state before it was initialized");
const hc = 5e3;
let ei = 1;
function Da(e) {
  b.notifications.includes(e) && (e.dontShowAgain && e.dismissId && gc(e.dismissId), b.removeNotification(e), y.emit("notification-dismissed", e));
}
function Na(e) {
  return nt.getDismissedNotifications().includes(e);
}
function gc(e) {
  Na(e) || nt.addDismissedNotification(e);
}
function mc(e) {
  return !(e.dismissId && (Na(e.dismissId) || b.notifications.find((t) => t.dismissId === e.dismissId)));
}
function bc() {
  const e = "A server restart is required";
  return dn() ? ir(le`${e} <br />${cn()}`) : ir(le`${e}`);
}
function cn() {
  return dn() ? le`<vaadin-button
      theme="primary"
      @click=${(e) => {
    const t = e.target;
    t.disabled = !0, t.innerText = "Restarting...", xc();
  }}>
      Restart now
    </vaadin-button>` : E;
}
function Ta(e) {
  if (mc(e))
    return yc(e);
}
function yc(e) {
  const t = ei;
  ei += 1;
  const r = { ...e, id: t, dontShowAgain: !1, animatingOut: !1 };
  return b.setNotifications([...b.notifications, r]), (e.delay || !e.link && !e.dismissId) && setTimeout(() => {
    Da(r);
  }, e.delay ?? hc), y.emit("notification-shown", e), r;
}
const wc = /* @__PURE__ */ Object.freeze(/* @__PURE__ */ Object.defineProperty({
  __proto__: null,
  dismissNotification: Da,
  getRestartRequiredMessage: bc,
  renderRestartButton: cn,
  showNotification: Ta
}, Symbol.toStringTag, { value: "Module" })), wr = window.Vaadin.copilot._earlyProjectState;
if (!wr)
  throw new Error("Tried to access early project state before it was initialized.");
function dn() {
  return b.idePluginState?.supportedActions?.find((e) => e === "restartApplication");
}
function xc() {
  Nt(`${he}plugin-restart-application`, {}, () => {
  }).catch((e) => {
    ce("Error restarting server", e);
  });
}
const Va = window.Vaadin.copilot._previewState;
if (!Va)
  throw new Error("Tried to access copilot preview state before it was initialized.");
function Oc() {
  const e = b.userInfo;
  return !e || e.copilotProjectCannotLeaveLocalhost ? !1 : nt.isSendErrorReportsAllowed();
}
const Ec = (e) => {
  ce("Unspecified error", e), y.emit("vite-after-update", {});
}, kc = (e, t) => e.error ? (Cc(e.error, t), !0) : !1, ti = (e, t, r) => {
  Tt({
    type: ye.ERROR,
    message: e,
    details: ir(
      le`<vaadin-details summary="Details" style="color: var(--dev-tools-text-color)"
          ><div>
            <code class="codeblock"><copilot-copy></copilot-copy>${vc(t)}</code>
          </div>
        </vaadin-details>
        ${r !== void 0 ? le`
              <vaadin-button
                theme="primary"
                @click="${() => {
        r && y.emit("submit-exception-report-clicked", r);
      }}"
                >Report Issue</vaadin-button
              >
            ` : E} `
    ),
    delay: 3e4
  });
}, Ia = (e, t, r, n, i) => {
  const a = b.newVaadinVersionState?.versions?.length === 0;
  i && a ? Sc(
    i,
    (o) => {
      ti(e, t, o);
    },
    e,
    t,
    r
  ) : ti(e, t), Oc() && (n?.templateData && typeof n.templateData == "string" && n.templateData.startsWith("data") && (n.templateData = "<IMAGE_DATA>"), y.emit("system-info-with-callback", {
    callback: (o) => y.send("copilot-error", {
      message: e,
      details: String(r).replace("	", `
`) + (n ? `
 
Request: 
${JSON.stringify(n)}
` : ""),
      versions: o
    }),
    notify: !1
  })), b.clearOperationWaitsHmrUpdate();
}, Cc = (e, t) => {
  Ia(
    e.message,
    e.exceptionMessage ?? "",
    e.exceptionStacktrace?.join(`
`) ?? "",
    t,
    e.exceptionReport
  );
};
function Ac(e, t) {
  const r = {
    title: t.message,
    nodes: [],
    relevantPairs: [],
    items: []
  };
  Ia(e, t.message, t.stack ?? "", void 0, r);
}
function Dr(e) {
  if (e === void 0)
    return !1;
  const t = Object.keys(e);
  return t.length === 1 && t.includes("message") || t.length >= 3 && t.includes("message") && t.includes("exceptionMessage") && t.includes("exceptionStacktrace");
}
function ce(e, t) {
  const r = Dr(t) ? t.exceptionMessage ?? t.message : t, n = {
    type: ye.ERROR,
    message: "Copilot internal error",
    details: e + (r ? `
${r}` : "")
  };
  Dr(t) && t.suggestRestart && dn() && (n.details = ir(le`${e}<br />${r} ${cn()}`), n.delay = 3e4), Tt(n);
  let i;
  t instanceof Error ? i = t.stack : Dr(t) ? i = t?.exceptionStacktrace?.join(`
`) : i = t?.toString(), y.emit("system-info-with-callback", {
    callback: (a) => y.send("copilot-error", {
      message: `Copilot internal error: ${e}`,
      details: i,
      versions: a
    }),
    notify: !1
  });
}
function ri(e) {
  return e?.stack?.includes("cdn.vaadin.com/copilot") || e?.stack?.includes("/copilot/copilot/") || e?.stack?.includes("/copilot/copilot-private/");
}
function za() {
  const e = window.onerror;
  window.onerror = (r, n, i, a, o) => {
    if (ri(o)) {
      ce(r.toString(), o);
      return;
    }
    e && e(r, n, i, a, o);
  }, ns((r) => {
    ri(r) && ce("", r);
  });
  const t = window.Vaadin.ConsoleErrors;
  if (Array.isArray(t))
    for (const r of t)
      Array.isArray(r) ? Bt.push(...r) : Bt.push(r);
  Ua((r) => Bt.push(r));
}
function Sc(e, t, r, n, i, a) {
  const o = { ...e }, s = window.Vaadin.copilot.tree, l = window.Vaadin.copilot.customComponentHandler;
  o.nodes.forEach((u) => {
    u.node = s.allNodesFlat.find((v) => {
      if (!v.isFlowComponent)
        return !1;
      const h = v.node;
      return h.uiId === u.uiId && h.nodeId === u.nodeId;
    });
  });
  const c = [];
  r && c.push(`Error Message -> ${r}`), n && c.push(`Error Details -> ${n}`), c.push(
    `Active Level -> ${l.getActiveDrillDownContext() ? l.getActiveDrillDownContext()?.nameAndIdentifier : "No active level"}`
  ), o.nodes.length > 0 && (c.push(`
Relevant Nodes:`), o.nodes.forEach((u) => {
    c.push(`${u.relevance} -> ${u.node?.nameAndIdentifier ?? "Node not found"}`);
  })), o.relevantPairs.length > 0 && (c.push(`
Additional Info:`), o.relevantPairs.forEach((u) => {
    c.push(`${u.relevance} -> ${u.value}`);
  }));
  const d = {
    name: "Info",
    content: c.join(`
`)
  };
  o.items.unshift(d), i && o.items.push({
    name: "Stacktrace",
    content: i
  }), y.emit("system-info-with-callback", {
    callback: (u) => {
      o.items.push({
        name: "Versions",
        content: u
      }), t(o);
    },
    notify: !1
  });
}
const Bt = [];
function Ua(e) {
  const t = window.Vaadin.ConsoleErrors;
  window.Vaadin.ConsoleErrors = {
    push: (r) => {
      r[0] === null || r[0] === void 0 || (r[0].type !== void 0 && r[0].message !== void 0 ? e({
        type: r[0].type,
        message: r[0].message,
        internal: !!r[0].internal,
        details: r[0].details,
        link: r[0].link
      }) : e({ type: ye.ERROR, message: r.map((n) => Pc(n)).join(" "), internal: !1 }), t.push(r));
    }
  };
}
function Pc(e) {
  return e.message ? e.message.toString() : e.toString();
}
function Dc(e) {
  Tt({
    type: ye.ERROR,
    message: `Unable to ${e}`,
    details: "Could not find sources for React components, probably because the project is not a React (or Flow) project"
  });
}
const Nc = /* @__PURE__ */ Object.freeze(/* @__PURE__ */ Object.defineProperty({
  __proto__: null,
  catchErrors: Ua,
  consoleErrorsQueue: Bt,
  handleBrowserOperationError: Ac,
  handleCopilotError: ce,
  handleErrorDuringOperation: Ec,
  handleServerOperationErrorIfNeeded: kc,
  installErrorHandlers: za,
  showNotReactFlowProject: Dc
}, Symbol.toStringTag, { value: "Module" })), La = () => {
  Tc().then((e) => b.setUserInfo(e)).catch((e) => ce("Failed to load userInfo", e));
}, Tc = async () => Nt(`${he}get-user-info`, {}, (e) => (delete e.data.reqId, e.data));
y.on("copilot-prokey-received", (e) => {
  La(), e.preventDefault();
});
/**
 * @license
 * Copyright 2020 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
const Ma = Symbol.for(""), Vc = (e) => {
  if (e?.r === Ma) return e?._$litStatic$;
}, ja = (e) => ({ _$litStatic$: e, r: Ma }), ni = /* @__PURE__ */ new Map(), Ic = (e) => (t, ...r) => {
  const n = r.length;
  let i, a;
  const o = [], s = [];
  let l, c = 0, d = !1;
  for (; c < n; ) {
    for (l = t[c]; c < n && (a = r[c], (i = Vc(a)) !== void 0); ) l += i + t[++c], d = !0;
    c !== n && s.push(a), o.push(l), c++;
  }
  if (c === n && o.push(t[n]), d) {
    const u = o.join("$$lit$$");
    (t = ni.get(u)) === void 0 && (o.raw = o, ni.set(u, t = o)), r = s;
  }
  return e(t, ...r);
}, gt = Ic(le);
/**
 * @license
 * Copyright 2020 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
const { I: zc } = ic, eu = (e) => e.strings === void 0, ii = () => document.createComment(""), lt = (e, t, r) => {
  const n = e._$AA.parentNode, i = t === void 0 ? e._$AB : t._$AA;
  if (r === void 0) {
    const a = n.insertBefore(ii(), i), o = n.insertBefore(ii(), i);
    r = new zc(a, o, e, e.options);
  } else {
    const a = r._$AB.nextSibling, o = r._$AM, s = o !== e;
    if (s) {
      let l;
      r._$AQ?.(e), r._$AM = e, r._$AP !== void 0 && (l = e._$AU) !== o._$AU && r._$AP(l);
    }
    if (a !== i || s) {
      let l = r._$AA;
      for (; l !== a; ) {
        const c = l.nextSibling;
        n.insertBefore(l, i), l = c;
      }
    }
  }
  return r;
}, Ee = (e, t, r = e) => (e._$AI(t, r), e), Uc = {}, Lc = (e, t = Uc) => e._$AH = t, Mc = (e) => e._$AH, Nr = (e) => {
  e._$AP?.(!1, !0);
  let t = e._$AA;
  const r = e._$AB.nextSibling;
  for (; t !== r; ) {
    const n = t.nextSibling;
    t.remove(), t = n;
  }
};
/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */
const ai = (e, t, r) => {
  const n = /* @__PURE__ */ new Map();
  for (let i = t; i <= r; i++) n.set(e[i], i);
  return n;
}, Ra = Sa(class extends Pa {
  constructor(e) {
    if (super(e), e.type !== Aa.CHILD) throw Error("repeat() can only be used in text expressions");
  }
  dt(e, t, r) {
    let n;
    r === void 0 ? r = t : t !== void 0 && (n = t);
    const i = [], a = [];
    let o = 0;
    for (const s of e) i[o] = n ? n(s, o) : o, a[o] = r(s, o), o++;
    return { values: a, keys: i };
  }
  render(e, t, r) {
    return this.dt(e, t, r).values;
  }
  update(e, [t, r, n]) {
    const i = Mc(e), { values: a, keys: o } = this.dt(t, r, n);
    if (!Array.isArray(i)) return this.ut = o, a;
    const s = this.ut ??= [], l = [];
    let c, d, u = 0, v = i.length - 1, h = 0, m = a.length - 1;
    for (; u <= v && h <= m; ) if (i[u] === null) u++;
    else if (i[v] === null) v--;
    else if (s[u] === o[h]) l[h] = Ee(i[u], a[h]), u++, h++;
    else if (s[v] === o[m]) l[m] = Ee(i[v], a[m]), v--, m--;
    else if (s[u] === o[m]) l[m] = Ee(i[u], a[m]), lt(e, l[m + 1], i[u]), u++, m--;
    else if (s[v] === o[h]) l[h] = Ee(i[v], a[h]), lt(e, i[u], i[v]), v--, h++;
    else if (c === void 0 && (c = ai(o, h, m), d = ai(s, u, v)), c.has(s[u])) if (c.has(s[v])) {
      const O = d.get(o[h]), A = O !== void 0 ? i[O] : null;
      if (A === null) {
        const G = lt(e, i[u]);
        Ee(G, a[h]), l[h] = G;
      } else l[h] = Ee(A, a[h]), lt(e, i[u], A), i[O] = null;
      h++;
    } else Nr(i[v]), v--;
    else Nr(i[u]), u++;
    for (; h <= m; ) {
      const O = lt(e, l[m + 1]);
      Ee(O, a[h]), l[h++] = O;
    }
    for (; u <= v; ) {
      const O = i[u++];
      O !== null && Nr(O);
    }
    return this.ut = o, Lc(e, l), ge;
  }
}), Ft = /* @__PURE__ */ new Map(), jc = (e) => {
  const r = se.panels.filter((n) => !n.floating && n.panel === e).sort((n, i) => n.panelOrder - i.panelOrder);
  return gt`
    ${Ra(
    r,
    (n) => n.tag,
    (n) => {
      const i = ja(n.tag);
      return gt` <copilot-section-panel-wrapper panelTag="${i}">
          ${se.shouldRender(n.tag) ? gt`<${i} slot="content"></${i}>` : E}
        </copilot-section-panel-wrapper>`;
    }
  )}
  `;
}, Rc = () => {
  const e = se.panels;
  return gt`
    ${Ra(
    e.filter((t) => t.floating),
    (t) => t.tag,
    (t) => {
      const r = ja(t.tag);
      return gt`
                        <copilot-section-panel-wrapper panelTag="${r}">
                            <${r} slot="content"></${r}>
                        </copilot-section-panel-wrapper>`;
    }
  )}
  `;
}, tu = (e) => {
  const t = e.panelTag, r = e.querySelector('[slot="content"]');
  r && e.panelInfo?.panel && Ft.set(t, r);
}, ru = (e) => {
  if (Ft.has(e.panelTag)) {
    const t = Ft.get(e.panelTag);
    e.querySelector('[slot="content"]').replaceWith(t);
  }
  Ft.delete(e.panelTag);
}, S = [];
for (let e = 0; e < 256; ++e)
  S.push((e + 256).toString(16).slice(1));
function qc(e, t = 0) {
  return (S[e[t + 0]] + S[e[t + 1]] + S[e[t + 2]] + S[e[t + 3]] + "-" + S[e[t + 4]] + S[e[t + 5]] + "-" + S[e[t + 6]] + S[e[t + 7]] + "-" + S[e[t + 8]] + S[e[t + 9]] + "-" + S[e[t + 10]] + S[e[t + 11]] + S[e[t + 12]] + S[e[t + 13]] + S[e[t + 14]] + S[e[t + 15]]).toLowerCase();
}
let Tr;
const Kc = new Uint8Array(16);
function Bc() {
  if (!Tr) {
    if (typeof crypto > "u" || !crypto.getRandomValues)
      throw new Error("crypto.getRandomValues() not supported. See https://github.com/uuidjs/uuid#getrandomvalues-not-supported");
    Tr = crypto.getRandomValues.bind(crypto);
  }
  return Tr(Kc);
}
const Fc = typeof crypto < "u" && crypto.randomUUID && crypto.randomUUID.bind(crypto), oi = { randomUUID: Fc };
function Zc(e, t, r) {
  e = e || {};
  const n = e.random ?? e.rng?.() ?? Bc();
  if (n.length < 16)
    throw new Error("Random bytes length must be >= 16");
  return n[6] = n[6] & 15 | 64, n[8] = n[8] & 63 | 128, qc(n);
}
function qa(e, t, r) {
  return oi.randomUUID && !e ? oi.randomUUID() : Zc(e);
}
const Zt = [], pt = [], nu = async (e, t, r) => {
  let n, i;
  t.reqId = qa();
  const a = new Promise((o, s) => {
    n = o, i = s;
  });
  return Zt.push({
    handleMessage(o) {
      if (o?.data?.reqId !== t.reqId)
        return !1;
      try {
        n(r(o));
      } catch (s) {
        i(s);
      }
      return !0;
    }
  }), te(e, t), a;
};
function Wc(e) {
  for (const t of Zt)
    if (t.handleMessage(e))
      return Zt.splice(Zt.indexOf(t), 1), !0;
  if (y.emitUnsafe({ type: e.command, data: e.data }))
    return !0;
  for (const t of Ba())
    if (Ka(t, e))
      return !0;
  return pt.push(e), !1;
}
function Ka(e, t) {
  return e.handleMessage?.call(e, t);
}
function Hc() {
  if (pt.length)
    for (const e of Ba())
      for (let t = 0; t < pt.length; t++)
        Ka(e, pt[t]) && (pt.splice(t, 1), t--);
}
function Ba() {
  const e = document.querySelector("copilot-main");
  return e ? e.renderRoot.querySelectorAll("copilot-section-panel-wrapper *") : [];
}
const Xc = ":host{--animate-spin: spin 1s linear infinite;--animate-swirl: swirl 5s linear infinite}@keyframes bounce{0%{transform:scale(.8)}50%{transform:scale(1.5)}to{transform:scale(1)}}@keyframes bounceLeft{0%{transform:translate(0)}30%{transform:translate(-10px)}50%{transform:translate(0)}70%{transform:translate(-5px)}to{transform:translate(0)}}@keyframes bounceRight{0%{transform:translate(0)}30%{transform:translate(10px)}50%{transform:translate(0)}70%{transform:translate(5px)}to{transform:translate(0)}}@keyframes bounceBottom{0%{transform:translateY(0)}30%{transform:translateY(10px)}50%{transform:translateY(0)}70%{transform:translateY(5px)}to{transform:translateY(0)}}@keyframes around-we-go-again{0%{background-position:0 0,0 0,calc(var(--glow-size) * -.5) calc(var(--glow-size) * -.5),calc(100% + calc(var(--glow-size) * .5)) calc(100% + calc(var(--glow-size) * .5))}25%{background-position:0 0,0 0,calc(100% + calc(var(--glow-size) * .5)) calc(var(--glow-size) * -.5),calc(var(--glow-size) * -.5) calc(100% + calc(var(--glow-size) * .5))}50%{background-position:0 0,0 0,calc(100% + calc(var(--glow-size) * .5)) calc(100% + calc(var(--glow-size) * .5)),calc(var(--glow-size) * -.5) calc(var(--glow-size) * -.5)}75%{background-position:0 0,0 0,calc(var(--glow-size) * -.5) calc(100% + calc(var(--glow-size) * .5)),calc(100% + calc(var(--glow-size) * .5)) calc(var(--glow-size) * -.5)}to{background-position:0 0,0 0,calc(var(--glow-size) * -.5) calc(var(--glow-size) * -.5),calc(100% + calc(var(--glow-size) * .5)) calc(100% + calc(var(--glow-size) * .5))}}@keyframes spin{to{transform:rotate(360deg)}}@keyframes swirl{0%{rotate:0deg;filter:hue-rotate(20deg)}50%{filter:hue-rotate(-30deg)}to{rotate:360deg;filter:hue-rotate(20deg)}}@keyframes button-focus-in{0%{box-shadow:0 0 0 0 var(--focus-color)}to{box-shadow:0 0 0 var(--focus-size) var(--focus-color)}}@keyframes button-focus-out{0%{box-shadow:0 0 0 var(--focus-size) var(--focus-color)}}@keyframes button-primary-focus-in{0%{box-shadow:0 0 0 0 var(--focus-color)}to{box-shadow:0 0 0 1px var(--background-color),0 0 0 calc(var(--focus-size) + 2px) var(--focus-color)}}@keyframes button-primary-focus-out{0%{box-shadow:0 0 0 1px var(--background-color),0 0 0 calc(var(--focus-size) + 2px) var(--focus-color)}}@keyframes link-focus-in{0%{box-shadow:0 0 0 0 var(--blue-color)}to{box-shadow:0 0 0 var(--focus-size) var(--blue-color)}}@keyframes link-focus-out{0%{box-shadow:0 0 0 var(--focus-size) var(--blue-color)}}@keyframes ping{75%,to{transform:scale(2);opacity:0}}@keyframes fadeInOut{0%,to{opacity:0}50%{opacity:1}}", Jc = 'button{align-items:center;-webkit-appearance:none;appearance:none;background:transparent;background-origin:border-box;border:1px solid transparent;border-radius:var(--vaadin-radius-s);color:var(--vaadin-text-color);cursor:pointer;display:inline-flex;flex-shrink:0;font:var(--copilot-font-button);height:var(--copilot-size-md);justify-content:center;outline-offset:calc(var(--focus-size) / -1);padding:0 var(--space-100)}button:hover{background:var(--hover-color)}button:focus{outline:var(--vaadin-focus-ring-width) solid var(--vaadin-focus-ring-color);outline-offset:1px}button:active:not([disabled]){background:var(--active-color)}button.primary{background:var(--primary-color);color:var(--primary-contrast-text-color)}button.icon{padding:0;width:var(--copilot-size-md)}button.icon span:has(svg){display:flex;width:fit-content}button svg{height:var(--icon-size-s);width:var(--icon-size-s)}button .prefix,button .suffix{align-items:center;display:flex;height:var(--copilot-size-md);justify-content:center;width:var(--copilot-size-md)}button:has(.prefix){padding-inline-start:0}button:has(.suffix){padding-inline-end:0}button[role=switch]{align-items:center;border:2px solid var(--vaadin-text-color-secondary);border-radius:9999px;box-sizing:border-box;display:flex;flex-shrink:0;height:1rem;justify-content:start;padding:0;transition:.2s;width:1.5rem}button[role=switch] span{background:var(--vaadin-text-color-secondary);border-radius:9999px;content:"";display:flex;flex-shrink:0;height:.5rem;transition:.2s;transform:translate(.125rem);width:.5rem}button[role=switch][aria-checked=true]{background:var(--blue-11);border-color:var(--blue-11)}button[role=switch][aria-checked=true] span{background:var(--blue-5);height:.625rem;transform:translate(.5625rem);width:.625rem}button[disabled]{cursor:not-allowed;opacity:.3}button[hidden]{display:none}button.link-button{all:initial;color:inherit;cursor:pointer;font-family:inherit;font-size:var(--dev-tools-font-size-small);font-weight:600;line-height:1;text-decoration:underline;white-space:nowrap}button.link-button:focus,button.link-button:hover{color:var(--dev-tools-text-color-emphasis)}', Gc = "code.codeblock{background:var(--contrast-color-5);border-radius:var(--vaadin-radius-m);display:block;font-family:var(--monospace-font-family);font-size:var(--copilot-font-size-xs);line-height:var(--copilot-line-height-sm);overflow:hidden;padding:calc((var(--copilot-size-md) - var(--copilot-line-height-sm)) / 2) var(--copilot-size-md) calc((var(--copilot-size-md) - var(--copilot-line-height-sm)) / 2) var(--space-100);position:relative;text-overflow:ellipsis;white-space:pre;min-height:var(--copilot-line-height-sm)}copilot-copy{position:absolute;right:0;top:0}div.message.error code.codeblock copilot-copy svg{color:#ffffffb3}", Yc = ":host{color-scheme:light;--vaadin-background-color: light-dark(var(--gray-1), var(--gray-5));--vaadin-text-color: light-dark(var(--gray-12), white);--vaadin-text-color-secondary: light-dark(var(--gray-11), hsla(0, 0%, 100%, .7));--vaadin-text-color-disabled: var(--vaadin-text-color-secondary);--vaadin-focus-ring-color: var(--vaadin-text-color);--vaadin-divider-color: light-dark(hsla(0, 0%, 0%, .1), hsla(0, 0%, 100%, .15));--vaadin-blue: #1a81fa;--vaadin-violet: #8854fc;--amber-1: light-dark(#fefdfb, #16120c);--amber-2: light-dark(#fefbe9, #1d180f);--amber-3: light-dark(#fff7c2, #302008);--amber-4: light-dark(#ffee9c, #3f2700);--amber-5: light-dark(#fbe577, #4d3000);--amber-6: light-dark(#f3d673, #5c3d05);--amber-7: light-dark(#e9c162, #714f19);--amber-8: light-dark(#e2a336, #8f6424);--amber-9: light-dark(#ffc53d, #ffc53d);--amber-10: light-dark(#ffba18, #ffd60a);--amber-11: light-dark(#ab6400, #ffca16);--amber-12: light-dark(#4f3422, #ffe7b3);--blue-1: light-dark(#fcfdff, #0a111c);--blue-2: light-dark(#f5f9ff, #0f1826);--blue-3: light-dark(#eaf3ff, #0e2649);--blue-4: light-dark(#dbebff, #0d3162);--blue-5: light-dark(#c9e2ff, #133c75);--blue-6: light-dark(#b5d4ff, #1c4885);--blue-7: light-dark(#9bc2fc, #25559a);--blue-8: light-dark(#76aaf7, #2b63b5);--blue-9: light-dark(#0368de, #0368de);--blue-10: light-dark(#0059ce, #265fb0);--blue-11: light-dark(#0368de, #82b8ff);--blue-12: light-dark(#0c3164, #d0e3ff);--gray-1: light-dark(#fcfcfd, #111113);--gray-2: light-dark(#f9f9fb, #19191b);--gray-3: light-dark(#eff0f3, #222325);--gray-4: light-dark(#e7e8ec, #292a2e);--gray-5: light-dark(#e0e1e6, #303136);--gray-6: light-dark(#d8d9e0, #393a40);--gray-7: light-dark(#cdced7, #46484f);--gray-8: light-dark(#b9bbc6, #5f606a);--gray-9: light-dark(#8b8d98, #6c6e79);--gray-10: light-dark(#80828d, #797b86);--gray-11: light-dark(#62636c, #b2b3bd);--gray-12: light-dark(#1e1f24, #eeeef0);--ruby-1: light-dark(#fffcfd, #191113);--ruby-2: light-dark(#fff7f8, #1e1517);--ruby-3: light-dark(#feeaed, #3a141e);--ruby-4: light-dark(#ffdce1, #4e1325);--ruby-5: light-dark(#ffced6, #5e1a2e);--ruby-6: light-dark(#f8bfc8, #6f2539);--ruby-7: light-dark(#efacb8, #883447);--ruby-8: light-dark(#e592a3, #b3445a);--ruby-9: light-dark(#e54666, #e54666);--ruby-10: light-dark(#dc3b5d, #ec5a72);--ruby-11: light-dark(#ca244d, #ff949d);--ruby-12: light-dark(#64172b, #fed2e1);--teal-1: light-dark(#fafefd, #0d1514);--teal-2: light-dark(#f3fbf9, #111c1b);--teal-3: light-dark(#e0f8f3, #0d2d2a);--teal-4: light-dark(#ccf3ea, #023b37);--teal-5: light-dark(#b8eae0, #084843);--teal-6: light-dark(#a1ded2, #145750);--teal-7: light-dark(#83cdc1, #1c6961);--teal-8: light-dark(#53b9ab, #207e73);--teal-9: light-dark(#12a594, #12a594);--teal-10: light-dark(#0d9b8a, #0eb39e);--teal-11: light-dark(#008573, #0bd8b6);--teal-12: light-dark(#0d3d38, #adf0dd);--violet-1: light-dark(#fcfcff, #110d21);--violet-2: light-dark(#f9f8ff, #18132c);--violet-3: light-dark(#f2f0ff, #291853);--violet-4: light-dark(#e8e3ff, #351772);--violet-5: light-dark(#dfd8ff, #3e1f81);--violet-6: light-dark(#d2c8ff, #492b91);--violet-7: light-dark(#c0b0ff, #5838a9);--violet-8: light-dark(#a98fff, #6d45d0);--violet-9: light-dark(#7b2bff, #7b2bff);--violet-10: light-dark(#6c2adf, #6f07ee);--violet-11: light-dark(#6f2fe3, #b8a5ff);--violet-12: light-dark(#361475, #e1dbff);--gray-h: 220;--gray-s: 30%;--gray-l: 30%;--gray-hsl: var(--gray-h) var(--gray-s) var(--gray-l);--gray: hsl(var(--gray-hsl));--gray-50: hsl(var(--gray-hsl) / .05);--gray-100: hsl(var(--gray-hsl) / .1);--gray-150: hsl(var(--gray-hsl) / .16);--gray-200: hsl(var(--gray-hsl) / .24);--gray-250: hsl(var(--gray-hsl) / .34);--gray-300: hsl(var(--gray-hsl) / .46);--gray-350: hsl(var(--gray-hsl) / .6);--gray-400: hsl(var(--gray-hsl) / .7);--gray-450: hsl(var(--gray-hsl) / .8);--gray-500: hsl(var(--gray-hsl) / .9);--gray-550: hsl(var(--gray-hsl));--gray-600: hsl(var(--gray-h) var(--gray-s) calc(var(--gray-l) - 2%));--gray-650: hsl(var(--gray-h) var(--gray-s) calc(var(--gray-l) - 4%));--gray-700: hsl(var(--gray-h) var(--gray-s) calc(var(--gray-l) - 8%));--gray-750: hsl(var(--gray-h) var(--gray-s) calc(var(--gray-l) - 12%));--gray-800: hsl(var(--gray-h) var(--gray-s) calc(var(--gray-l) - 20%));--gray-850: hsl(var(--gray-h) var(--gray-s) calc(var(--gray-l) - 23%));--gray-900: hsl(var(--gray-h) var(--gray-s) calc(var(--gray-l) - 30%));--blue-h: 220;--blue-s: 90%;--blue-l: 53%;--blue-hsl: var(--blue-h) var(--blue-s) var(--blue-l);--blue: hsl(var(--blue-hsl));--blue-50: hsl(var(--blue-hsl) / .05);--blue-100: hsl(var(--blue-hsl) / .1);--blue-150: hsl(var(--blue-hsl) / .2);--blue-200: hsl(var(--blue-hsl) / .3);--blue-250: hsl(var(--blue-hsl) / .4);--blue-300: hsl(var(--blue-hsl) / .5);--blue-350: hsl(var(--blue-hsl) / .6);--blue-400: hsl(var(--blue-hsl) / .7);--blue-450: hsl(var(--blue-hsl) / .8);--blue-500: hsl(var(--blue-hsl) / .9);--blue-550: hsl(var(--blue-hsl));--blue-600: hsl(var(--blue-h) var(--blue-s) calc(var(--blue-l) - 4%));--blue-650: hsl(var(--blue-h) var(--blue-s) calc(var(--blue-l) - 8%));--blue-700: hsl(var(--blue-h) var(--blue-s) calc(var(--blue-l) - 12%));--blue-750: hsl(var(--blue-h) var(--blue-s) calc(var(--blue-l) - 15%));--blue-800: hsl(var(--blue-h) var(--blue-s) calc(var(--blue-l) - 18%));--blue-850: hsl(var(--blue-h) var(--blue-s) calc(var(--blue-l) - 24%));--blue-900: hsl(var(--blue-h) var(--blue-s) calc(var(--blue-l) - 27%));--purple-h: 246;--purple-s: 90%;--purple-l: 60%;--purple-hsl: var(--purple-h) var(--purple-s) var(--purple-l);--purple: hsl(var(--purple-hsl));--purple-50: hsl(var(--purple-hsl) / .05);--purple-100: hsl(var(--purple-hsl) / .1);--purple-150: hsl(var(--purple-hsl) / .2);--purple-200: hsl(var(--purple-hsl) / .3);--purple-250: hsl(var(--purple-hsl) / .4);--purple-300: hsl(var(--purple-hsl) / .5);--purple-350: hsl(var(--purple-hsl) / .6);--purple-400: hsl(var(--purple-hsl) / .7);--purple-450: hsl(var(--purple-hsl) / .8);--purple-500: hsl(var(--purple-hsl) / .9);--purple-550: hsl(var(--purple-hsl));--purple-600: hsl(var(--purple-h) calc(var(--purple-s) - 4%) calc(var(--purple-l) - 2%));--purple-650: hsl(var(--purple-h) calc(var(--purple-s) - 8%) calc(var(--purple-l) - 4%));--purple-700: hsl(var(--purple-h) calc(var(--purple-s) - 15%) calc(var(--purple-l) - 7%));--purple-750: hsl(var(--purple-h) calc(var(--purple-s) - 23%) calc(var(--purple-l) - 11%));--purple-800: hsl(var(--purple-h) calc(var(--purple-s) - 24%) calc(var(--purple-l) - 15%));--purple-850: hsl(var(--purple-h) calc(var(--purple-s) - 24%) calc(var(--purple-l) - 19%));--purple-900: hsl(var(--purple-h) calc(var(--purple-s) - 27%) calc(var(--purple-l) - 23%));--green-h: 150;--green-s: 80%;--green-l: 42%;--green-hsl: var(--green-h) var(--green-s) var(--green-l);--green: hsl(var(--green-hsl));--green-50: hsl(var(--green-hsl) / .05);--green-100: hsl(var(--green-hsl) / .1);--green-150: hsl(var(--green-hsl) / .2);--green-200: hsl(var(--green-hsl) / .3);--green-250: hsl(var(--green-hsl) / .4);--green-300: hsl(var(--green-hsl) / .5);--green-350: hsl(var(--green-hsl) / .6);--green-400: hsl(var(--green-hsl) / .7);--green-450: hsl(var(--green-hsl) / .8);--green-500: hsl(var(--green-hsl) / .9);--green-550: hsl(var(--green-hsl));--green-600: hsl(var(--green-h) var(--green-s) calc(var(--green-l) - 2%));--green-650: hsl(var(--green-h) var(--green-s) calc(var(--green-l) - 4%));--green-700: hsl(var(--green-h) var(--green-s) calc(var(--green-l) - 8%));--green-750: hsl(var(--green-h) var(--green-s) calc(var(--green-l) - 12%));--green-800: hsl(var(--green-h) var(--green-s) calc(var(--green-l) - 15%));--green-850: hsl(var(--green-h) var(--green-s) calc(var(--green-l) - 19%));--green-900: hsl(var(--green-h) var(--green-s) calc(var(--green-l) - 23%));--yellow-h: 38;--yellow-s: 98%;--yellow-l: 64%;--yellow-hsl: var(--yellow-h) var(--yellow-s) var(--yellow-l);--yellow: hsl(var(--yellow-hsl));--yellow-50: hsl(var(--yellow-hsl) / .07);--yellow-100: hsl(var(--yellow-hsl) / .12);--yellow-150: hsl(var(--yellow-hsl) / .2);--yellow-200: hsl(var(--yellow-hsl) / .3);--yellow-250: hsl(var(--yellow-hsl) / .4);--yellow-300: hsl(var(--yellow-hsl) / .5);--yellow-350: hsl(var(--yellow-hsl) / .6);--yellow-400: hsl(var(--yellow-hsl) / .7);--yellow-450: hsl(var(--yellow-hsl) / .8);--yellow-500: hsl(var(--yellow-hsl) / .9);--yellow-550: hsl(var(--yellow-hsl));--yellow-600: hsl(var(--yellow-h) var(--yellow-s) calc(var(--yellow-l) - 5%));--yellow-650: hsl(var(--yellow-h) var(--yellow-s) calc(var(--yellow-l) - 10%));--yellow-700: hsl(var(--yellow-h) var(--yellow-s) calc(var(--yellow-l) - 15%));--yellow-750: hsl(var(--yellow-h) var(--yellow-s) calc(var(--yellow-l) - 20%));--yellow-800: hsl(var(--yellow-h) var(--yellow-s) calc(var(--yellow-l) - 25%));--yellow-850: hsl(var(--yellow-h) var(--yellow-s) calc(var(--yellow-l) - 30%));--yellow-900: hsl(var(--yellow-h) var(--yellow-s) calc(var(--yellow-l) - 35%));--red-h: 355;--red-s: 75%;--red-l: 55%;--red-hsl: var(--red-h) var(--red-s) var(--red-l);--red: hsl(var(--red-hsl));--red-50: hsl(var(--red-hsl) / .05);--red-100: hsl(var(--red-hsl) / .1);--red-150: hsl(var(--red-hsl) / .2);--red-200: hsl(var(--red-hsl) / .3);--red-250: hsl(var(--red-hsl) / .4);--red-300: hsl(var(--red-hsl) / .5);--red-350: hsl(var(--red-hsl) / .6);--red-400: hsl(var(--red-hsl) / .7);--red-450: hsl(var(--red-hsl) / .8);--red-500: hsl(var(--red-hsl) / .9);--red-550: hsl(var(--red-hsl));--red-600: hsl(var(--red-h) calc(var(--red-s) - 5%) calc(var(--red-l) - 2%));--red-650: hsl(var(--red-h) calc(var(--red-s) - 10%) calc(var(--red-l) - 4%));--red-700: hsl(var(--red-h) calc(var(--red-s) - 15%) calc(var(--red-l) - 8%));--red-750: hsl(var(--red-h) calc(var(--red-s) - 20%) calc(var(--red-l) - 12%));--red-800: hsl(var(--red-h) calc(var(--red-s) - 25%) calc(var(--red-l) - 15%));--red-850: hsl(var(--red-h) calc(var(--red-s) - 30%) calc(var(--red-l) - 19%));--red-900: hsl(var(--red-h) calc(var(--red-s) - 35%) calc(var(--red-l) - 23%));--codeblock-bg: #f4f4f4;--background-color: rgba(255, 255, 255, .87);--primary-color: #0368de;--input-border-color: rgba(0, 0, 0, .42);--divider-primary-color: rgba(0, 0, 0, .1);--divider-secondary-color: rgba(0, 0, 0, .05);--switch-active-color: #0d875b;--switch-inactive-color: #757575;--primary-contrast-text-color: white;--active-color: rgba(3, 104, 222, .1);--focus-color: #0377ff;--hover-color: rgba(0, 0, 0, .05);--info-color: var(--blue-400);--success-color: var(--success-color-80);--error-color: var(--error-color-70);--warning-color: #fec941;--success-color-5: #f0fffa;--success-color-10: #eafaf4;--success-color-20: #d2f0e5;--success-color-30: #8ce4c5;--success-color-40: #39c693;--success-color-50: #1ba875;--success-color-60: #0e9c69;--success-color-70: #0d8b5e;--success-color-80: #066845;--success-color-90: #004d31;--error-color-5: #fff5f6;--error-color-10: #ffedee;--error-color-20: #ffd0d4;--error-color-30: #f8a8ae;--error-color-40: #ff707a;--error-color-50: #ff3a49;--error-color-60: #ff0013;--error-color-70: #ce0010;--error-color-80: #97000b;--error-color-90: #680008;--contrast-color-5: rgba(0, 0, 0, .05);--contrast-color-10: rgba(0, 0, 0, .1);--contrast-color-20: rgba(0, 0, 0, .2);--contrast-color-30: rgba(0, 0, 0, .3);--contrast-color-40: rgba(0, 0, 0, .4);--contrast-color-50: rgba(0, 0, 0, .5);--contrast-color-60: rgba(0, 0, 0, .6);--contrast-color-70: rgba(0, 0, 0, .7);--contrast-color-80: rgba(0, 0, 0, .8);--contrast-color-90: rgba(0, 0, 0, .9);--contrast-color-100: black;--blue-color: #0368de;--violet-color: #7b2bff}:host(.dark){color-scheme:dark;--gray-s: 15%;--gray-l: 70%;--gray-600: hsl(var(--gray-h) calc(var(--gray-s) - 2%) calc(var(--gray-l) + 6%));--gray-650: hsl(var(--gray-h) calc(var(--gray-s) - 5%) calc(var(--gray-l) + 14%));--gray-700: hsl(var(--gray-h) calc(var(--gray-s) - 2%) calc(var(--gray-l) + 26%));--gray-750: hsl(var(--gray-h) calc(var(--gray-s) - 2%) calc(var(--gray-l) + 36%));--gray-800: hsl(var(--gray-h) calc(var(--gray-s) - 2%) calc(var(--gray-l) + 48%));--gray-850: hsl(var(--gray-h) calc(var(--gray-s) - 2%) calc(var(--gray-l) + 62%));--gray-900: hsl(var(--gray-h) calc(var(--gray-s) - 2%) calc(var(--gray-l) + 70%));--blue-s: 90%;--blue-l: 58%;--blue-600: hsl(var(--blue-h) var(--blue-s) calc(var(--blue-l) + 6%));--blue-650: hsl(var(--blue-h) var(--blue-s) calc(var(--blue-l) + 12%));--blue-700: hsl(var(--blue-h) var(--blue-s) calc(var(--blue-l) + 17%));--blue-750: hsl(var(--blue-h) var(--blue-s) calc(var(--blue-l) + 22%));--blue-800: hsl(var(--blue-h) var(--blue-s) calc(var(--blue-l) + 28%));--blue-850: hsl(var(--blue-h) var(--blue-s) calc(var(--blue-l) + 35%));--blue-900: hsl(var(--blue-h) var(--blue-s) calc(var(--blue-l) + 43%));--purple-600: hsl(var(--purple-h) var(--purple-s) calc(var(--purple-l) + 4%));--purple-650: hsl(var(--purple-h) var(--purple-s) calc(var(--purple-l) + 9%));--purple-700: hsl(var(--purple-h) var(--purple-s) calc(var(--purple-l) + 12%));--purple-750: hsl(var(--purple-h) var(--purple-s) calc(var(--purple-l) + 18%));--purple-800: hsl(var(--purple-h) var(--purple-s) calc(var(--purple-l) + 24%));--purple-850: hsl(var(--purple-h) var(--purple-s) calc(var(--purple-l) + 29%));--purple-900: hsl(var(--purple-h) var(--purple-s) calc(var(--purple-l) + 33%));--green-600: hsl(calc(var(--green-h) - 1) calc(var(--green-s) - 5%) calc(var(--green-l) + 5%));--green-650: hsl(calc(var(--green-h) - 2) calc(var(--green-s) - 10%) calc(var(--green-l) + 12%));--green-700: hsl(calc(var(--green-h) - 4) calc(var(--green-s) - 15%) calc(var(--green-l) + 20%));--green-750: hsl(calc(var(--green-h) - 6) calc(var(--green-s) - 20%) calc(var(--green-l) + 29%));--green-800: hsl(calc(var(--green-h) - 8) calc(var(--green-s) - 25%) calc(var(--green-l) + 37%));--green-850: hsl(calc(var(--green-h) - 10) calc(var(--green-s) - 30%) calc(var(--green-l) + 42%));--green-900: hsl(calc(var(--green-h) - 12) calc(var(--green-s) - 35%) calc(var(--green-l) + 48%));--yellow-600: hsl(calc(var(--yellow-h) + 1) var(--yellow-s) calc(var(--yellow-l) + 4%));--yellow-650: hsl(calc(var(--yellow-h) + 2) var(--yellow-s) calc(var(--yellow-l) + 7%));--yellow-700: hsl(calc(var(--yellow-h) + 4) var(--yellow-s) calc(var(--yellow-l) + 11%));--yellow-750: hsl(calc(var(--yellow-h) + 6) var(--yellow-s) calc(var(--yellow-l) + 16%));--yellow-800: hsl(calc(var(--yellow-h) + 8) var(--yellow-s) calc(var(--yellow-l) + 20%));--yellow-850: hsl(calc(var(--yellow-h) + 10) var(--yellow-s) calc(var(--yellow-l) + 24%));--yellow-900: hsl(calc(var(--yellow-h) + 12) var(--yellow-s) calc(var(--yellow-l) + 29%));--red-600: hsl(calc(var(--red-h) - 1) calc(var(--red-s) - 5%) calc(var(--red-l) + 3%));--red-650: hsl(calc(var(--red-h) - 2) calc(var(--red-s) - 10%) calc(var(--red-l) + 7%));--red-700: hsl(calc(var(--red-h) - 4) calc(var(--red-s) - 15%) calc(var(--red-l) + 14%));--red-750: hsl(calc(var(--red-h) - 6) calc(var(--red-s) - 20%) calc(var(--red-l) + 19%));--red-800: hsl(calc(var(--red-h) - 8) calc(var(--red-s) - 25%) calc(var(--red-l) + 24%));--red-850: hsl(calc(var(--red-h) - 10) calc(var(--red-s) - 30%) calc(var(--red-l) + 30%));--red-900: hsl(calc(var(--red-h) - 12) calc(var(--red-s) - 35%) calc(var(--red-l) + 36%));--codeblock-bg: var(--gray-100);--background-color: rgba(0, 0, 0, .87);--primary-color: white;--input-border-color: rgba(255, 255, 255, .42);--divider-primary-color: rgba(255, 255, 255, .2);--divider-secondary-color: rgba(255, 255, 255, .1);--primary-contrast-text-color: rgba(0, 0, 0, .87);--active-color: rgba(255, 255, 255, .15);--focus-color: rgba(255, 255, 255, .5);--hover-color: rgba(255, 255, 255, .1);--success-color: var(--success-color-50);--error-color: var(--error-color-50);--warning-color: #fec941;--success-color-5: #004d31;--success-color-10: #066845;--success-color-20: #0d8b5e;--success-color-30: #0e9c69;--success-color-40: #1ba875;--success-color-50: #39c693;--success-color-60: #8ce4c5;--success-color-70: #d2f0e5;--success-color-80: #eafaf4;--success-color-90: #f0fffa;--error-color-5: #680008;--error-color-10: #97000b;--error-color-20: #ce0010;--error-color-30: #ff0013;--error-color-40: #ff3a49;--error-color-50: #ff707a;--error-color-60: #f8a8ae;--error-color-70: #ffd0d4;--error-color-80: #ffedee;--error-color-90: #fff5f6;--contrast-color-5: rgba(255, 255, 255, .05);--contrast-color-10: rgba(255, 255, 255, .1);--contrast-color-20: rgba(255, 255, 255, .2);--contrast-color-30: rgba(255, 255, 255, .3);--contrast-color-40: rgba(255, 255, 255, .4);--contrast-color-50: rgba(255, 255, 255, .5);--contrast-color-60: rgba(255, 255, 255, .6);--contrast-color-70: rgba(255, 255, 255, .7);--contrast-color-80: rgba(255, 255, 255, .8);--contrast-color-90: rgba(255, 255, 255, .9);--contrast-color-100: white;--blue-color: #95c6ff;--violet-color: #cbb4ff}.bg-blue{background-color:var(--blue-color)}.bg-error{background-color:var(--error-color)}.bg-success{background-color:var(--success-color)}.bg-violet{background-color:var(--violet-color)}.bg-warning{background-color:var(--warning-color)}.blue-text{color:var(--blue-color)}.error-text{color:var(--error-color)}.success-text{color:var(--success-color)}.violet-text{color:var(--violet-color)}.warning-text{color:var(--warning-color)}", Qc = `vaadin-button{letter-spacing:.25px;padding:var(--vaadin-button-padding)}vaadin-button[focused]{z-index:1}vaadin-button[theme~=icon]{--vaadin-button-height: 2rem;--vaadin-button-padding: 0;width:var(--vaadin-button-height)}vaadin-button vaadin-icon[slot=prefix]{margin-inline-start:-.375rem}vaadin-button vaadin-icon[slot=suffix]{margin-inline-end:-.375rem}vaadin-button[theme~=primary]{min-width:auto}vaadin-button[disabled][theme~=primary]{--vaadin-button-background: var(--vaadin-text-color);--vaadin-button-text-color: var(--vaadin-background-color)}vaadin-button[theme~=lg]{--vaadin-button-height: 2.5rem;--vaadin-icon-size: 1.5rem;--vaadin-icon-visual-size: 1.5rem}vaadin-button[theme~=lg][theme~=icon]{--vaadin-button-height: 2.5rem}vaadin-button[theme~=xl]{--vaadin-button-border-radius: var(--vaadin-radius-l);--vaadin-icon-size: 1.5rem;--vaadin-icon-visual-size: 1.5rem}vaadin-button[theme~=xl][theme~=icon]{--vaadin-button-height: 3rem}vaadin-checkbox::part(checkbox){margin:0}vaadin-combo-box-item{font:var(--copilot-font-sm);gap:var(--vaadin-item-gap);padding:var(--vaadin-item-padding)}.no-checkmark{--vaadin-item-checkmark-display: none;--_lumo-item-selected-icon-display: none}vaadin-context-menu-list-box hr{border-color:var(--vaadin-divider-color);border-width:0 0 1px;margin:.25rem .5rem .25rem calc(var(--vaadin-icon-size) + var(--vaadin-item-gap) + .5rem)}vaadin-context-menu-item{font:var(--copilot-font-sm);gap:var(--vaadin-item-gap);padding:var(--vaadin-item-padding)}vaadin-context-menu-item:is(:hover,[expanded]){background-color:light-dark(var(--gray-3),var(--gray-6))}vaadin-context-menu-item::part(checkmark){display:var(--vaadin-item-checkmark-display)}vaadin-context-menu-item::part(content){display:flex;gap:inherit}vaadin-context-menu-item[aria-haspopup=false]:after{display:none}vaadin-context-menu-item[aria-haspopup=true]:after{background:var(--vaadin-text-color-secondary);color:inherit;content:"";display:block;font:inherit;height:var(--vaadin-icon-size, 1lh);margin:0;mask:var(--_vaadin-icon-chevron-down) 50% / var(--vaadin-icon-visual-size, 100%) no-repeat;padding:0;rotate:-90deg;width:var(--vaadin-icon-size, 1lh)}vaadin-details{margin:0}vaadin-details[theme~=no-padding]::part(content){padding:0}vaadin-details-summary[theme~=reverse]{--vaadin-details-summary-gap: .25rem;--vaadin-details-summary-padding: .375rem .75rem;color:var(--vaadin-text-color-secondary);letter-spacing:var(--copilot-letter-spacing-xs);font:var(--copilot-font-xs-medium);gap:var(--vaadin-details-summary-gap);justify-content:normal;padding:var(--vaadin-details-summary-padding)}vaadin-details-summary[theme~=reverse]::part(toggle){color:inherit;height:auto;margin:0;order:1;width:auto}vaadin-details-summary[theme~=reverse]::part(toggle):before{background:currentColor;content:"";display:block;height:var(--vaadin-icon-size, 1lh);mask:var(--_vaadin-icon-chevron-down) 50% / var(--vaadin-icon-visual-size, 100%) no-repeat;rotate:-90deg;width:var(--vaadin-icon-size, 1lh)}@media (prefers-reduced-motion: no-preference){vaadin-details-summary[theme~=reverse]::part(toggle){transition:.12s}}vaadin-details-summary[theme~=reverse]::part(content){flex-grow:0}vaadin-dialog::part(header){--vaadin-dialog-padding: .5rem .5rem .5rem 1rem}vaadin-dialog::part(footer){--vaadin-dialog-padding: .5rem}vaadin-grid{background-color:var(--vaadin-grid-background)}vaadin-icon{color:inherit;height:var(--vaadin-icon-size, var(--lumo-icon-size-m));width:var(--vaadin-icon-size, var(--lumo-icon-size-m))}:is(vaadin-combo-box,vaadin-radio-group,vaadin-select,vaadin-text-area,vaadin-text-field){padding:0}:is(vaadin-combo-box,vaadin-select){--vaadin-input-field-padding: calc((6 / 16 * 1rem) - var(--vaadin-input-field-border-width)) calc((6 / 16 * 1rem) - var(--vaadin-input-field-border-width)) calc((6 / 16 * 1rem) - var(--vaadin-input-field-border-width)) calc((8 / 16 * 1rem) - var(--vaadin-input-field-border-width))}:is(vaadin-combo-box,vaadin-select,vaadin-text-field):has([slot=prefix]){--vaadin-input-field-padding: calc(.375rem - var(--vaadin-input-field-border-width))}*:not(vaadin-checkbox,vaadin-button)::part(label){letter-spacing:var(--copilot-letter-spacing-xs);line-height:var(--vaadin-input-field-label-line-height);margin-block:0 var(--vaadin-input-field-container-gap);padding:0}::part(input-field){border:var(--vaadin-input-field-border-color) solid var(--vaadin-input-field-border-width);box-shadow:none;gap:var(--vaadin-input-field-gap);padding:var(--vaadin-input-field-padding)}::part(input-field):after{content:none}[readonly]::part(input-field){--vaadin-input-field-border-color: light-dark(var(--gray-9), var(--gray-10));border-style:dashed}[invalid]::part(input-field){--vaadin-input-field-border-color: var(--vaadin-input-field-error-color)}:is(vaadin-combo-box,vaadin-text-field) input{line-height:var(--copilot-line-height-sm);min-height:1lh}:is(vaadin-combo-box,vaadin-number-field,vaadin-text-field) input{padding:0}::part(error-message){--vaadin-icon-size: 1.125rem ;--vaadin-icon-visual-size: 1rem ;line-height:var(--vaadin-input-field-error-line-height);margin:0}[has-error-message]::part(error-message){margin-top:var(--vaadin-input-field-container-gap)}::part(error-message):before{height:auto}::part(error-message):after{content:none}vaadin-text-area textarea{align-self:auto;height:auto;padding:0}::part(input-field):focus-within{outline:var(--vaadin-focus-ring-width) solid var(--vaadin-focus-ring-color);outline-offset:calc(var(--vaadin-input-field-border-width, 1px) * -1)}:is(vaadin-combo-box,vaadin-select,vaadin-text-field)[disabled]{opacity:.5}[disabled]::part(input-field){--vaadin-input-field-border-color: light-dark(var(--gray-9), var(--gray-10))}[theme~=no-border]{--vaadin-input-field-border-width: 0px}[theme~=no-border]:hover{--vaadin-input-field-background: light-dark(var(--gray-4), var(--gray-7))}[theme~=filled]{--vaadin-input-field-background: light-dark(var(--gray-3), var(--gray-6))}[theme~=filled]:hover{--vaadin-input-field-background: light-dark(var(--gray-4), var(--gray-7))}vaadin-menu-bar-item{font:var(--copilot-font-sm)}vaadin-menu-bar-item:after{content:none}vaadin-menu-bar[theme~=dev-tools]{--vaadin-button-border-radius: var(--vaadin-radius-l);--vaadin-overlay-background: linear-gradient(light-dark(var(--gray-1), var(--gray-5)) 0 0) padding-box, linear-gradient(90deg, var(--vaadin-blue), var(--vaadin-violet)) border-box;--vaadin-overlay-border-color: transparent;--vaadin-overlay-border-width: 2px}vaadin-menu-bar-button[theme~=dev-tools]{--vaadin-button-background: transparent;--vaadin-button-border-width: 0;--vaadin-button-height: var(--copilot-size-xl);--vaadin-button-text-color: white;--vaadin-button-padding: 0;--vaadin-icon-size: 1.5rem;--vaadin-icon-visual-size: 1.5rem;min-width:auto;overflow:hidden;position:relative;width:var(--vaadin-button-height)}vaadin-menu-bar-button[theme~=dev-tools]:before{animation:var(--animate-swirl);background-image:radial-gradient(circle at 50% -10%,var(--blue-9) 0%,transparent 60%),radial-gradient(circle at 25% 40%,var(--violet-9) 0%,transparent 70%),radial-gradient(circle at 80% 10%,var(--gray-9) 0%,transparent 80%),radial-gradient(circle at 110% 50%,var(--teal-9) 20%,transparent 100%);border-radius:inherit;content:"";inset:-.375rem;opacity:1;position:absolute}:host(:not([active])) vaadin-menu-bar-button[theme~=dev-tools]:before{animation-duration:10s;background-color:transparent;background-image:radial-gradient(circle at 50% -10%,var(--gray-9) 0%,transparent 60%),radial-gradient(circle at 25% 40%,var(--gray-9) 0%,transparent 70%),radial-gradient(circle at 80% 10%,var(--gray-9) 0%,transparent 80%),radial-gradient(circle at 110% 50%,var(--gray-9) 20%,transparent 100%)}:host([document-hidden]) vaadin-menu-bar-button[theme~=dev-tools]:before{background-color:var(--gray-9);background-image:none}vaadin-menu-bar-button[theme~=dev-tools]:after{background-color:transparent;border:2px solid rgba(255,255,255,.5);border-radius:inherit;content:"";filter:none;inset:0;opacity:1;position:absolute;transform:none;transition:none}vaadin-menu-bar-button[theme~=dev-tools]::part(prefix){margin:0;z-index:1}vaadin-menu-bar-button[theme~=dev-tools]::part(prefix):after{background:currentColor;content:"";display:flex;height:var(--vaadin-icon-visual-size);-webkit-mask-image:url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" height="24" width="24" viewBox="0 0 24 24"><path d="M3 3C2.55 3 2.25 3.3 2.25 3.75V5.625C2.25 7.05 3.45 8.25 4.875 8.25H10.1997C10.7997 8.25 11.25 8.70029 11.25 9.30029V9.75C11.25 10.2 11.55 10.5 12 10.5C12.45 10.5 12.75 10.2 12.75 9.75V9.30029C12.75 8.70029 13.2003 8.25 13.8003 8.25H19.125C20.55 8.25 21.75 7.05 21.75 5.625V3.75C21.75 3.3 21.45 3 21 3C20.55 3 20.25 3.3 20.25 3.75V4.19971C20.25 4.79971 19.7997 5.25 19.1997 5.25H14.25C12.975 5.25 12 6.225 12 7.5C12 6.225 11.025 5.25 9.75 5.25H4.80029C4.20029 5.25 3.75 4.79971 3.75 4.19971V3.75C3.75 3.3 3.45 3 3 3ZM7.76367 11.2705C7.62187 11.2834 7.48184 11.3244 7.35059 11.3994C6.82559 11.6994 6.59941 12.3744 6.89941 12.8994L11.0244 20.3994C11.1744 20.7744 11.625 21 12 21C12.375 21 12.8256 20.7744 12.9756 20.3994L17.1006 12.8994C17.4006 12.3744 17.1744 11.6994 16.6494 11.3994C16.1244 11.0994 15.4494 11.3256 15.1494 11.8506L12 17.5503L8.85059 11.8506C8.62559 11.4568 8.18906 11.2318 7.76367 11.2705Z" fill="currentColor"/></svg>');mask-image:url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" height="24" width="24" viewBox="0 0 24 24"><path d="M3 3C2.55 3 2.25 3.3 2.25 3.75V5.625C2.25 7.05 3.45 8.25 4.875 8.25H10.1997C10.7997 8.25 11.25 8.70029 11.25 9.30029V9.75C11.25 10.2 11.55 10.5 12 10.5C12.45 10.5 12.75 10.2 12.75 9.75V9.30029C12.75 8.70029 13.2003 8.25 13.8003 8.25H19.125C20.55 8.25 21.75 7.05 21.75 5.625V3.75C21.75 3.3 21.45 3 21 3C20.55 3 20.25 3.3 20.25 3.75V4.19971C20.25 4.79971 19.7997 5.25 19.1997 5.25H14.25C12.975 5.25 12 6.225 12 7.5C12 6.225 11.025 5.25 9.75 5.25H4.80029C4.20029 5.25 3.75 4.79971 3.75 4.19971V3.75C3.75 3.3 3.45 3 3 3ZM7.76367 11.2705C7.62187 11.2834 7.48184 11.3244 7.35059 11.3994C6.82559 11.6994 6.59941 12.3744 6.89941 12.8994L11.0244 20.3994C11.1744 20.7744 11.625 21 12 21C12.375 21 12.8256 20.7744 12.9756 20.3994L17.1006 12.8994C17.4006 12.3744 17.1744 11.6994 16.6494 11.3994C16.1244 11.0994 15.4494 11.3256 15.1494 11.8506L12 17.5503L8.85059 11.8506C8.62559 11.4568 8.18906 11.2318 7.76367 11.2705Z" fill="currentColor"/></svg>');width:var(--vaadin-icon-visual-size)}vaadin-menu-bar-button[theme~=dev-tools]::part(label){clip:rect(0,0,0,0);height:1px;margin:-1px;overflow:hidden;padding:0;position:absolute;white-space:nowrap;width:1px}vaadin-menu-bar-button[theme~=dev-tools]::part(suffix){display:none}vaadin-menu-bar-submenu[theme~=dev-tools]::part(overlay){border:var(--vaadin-overlay-border-width) solid var(--vaadin-overlay-border-color);border-radius:var(--vaadin-overlay-border-radius);margin-bottom:.375rem;min-width:20rem}vaadin-menu-bar-submenu[theme~=dev-tools]::part(content){--vaadin-item-overlay-padding: 1rem;padding:var(--vaadin-item-overlay-padding)}vaadin-menu-bar-submenu[theme~=dev-tools] vaadin-menu-bar-submenu{--vaadin-overlay-background: light-dark(var(--gray-1), var(--gray-5));--vaadin-overlay-border-color: var(--vaadin-divider-color);--vaadin-overlay-border-width: 1px;margin-bottom:0}vaadin-menu-bar-list-box[theme~=dev-tools]{--vaadin-item-checkmark-display: none;--_lumo-list-box-item-selected-icon-display: none}vaadin-menu-bar-item[theme~=dev-tools]{--vaadin-item-padding: .5rem;background-color:var(--vaadin-button-background);font-weight:var(--copilot-font-weight-medium);letter-spacing:var(--copilot-letter-spacing-md);padding:var(--vaadin-item-padding)}vaadin-menu-bar-item[theme~=dev-tools]+vaadin-menu-bar-item{border-top-left-radius:0;border-top-right-radius:0}vaadin-menu-bar-item[theme~=dev-tools]:has(+vaadin-menu-bar-item){border-bottom-left-radius:0;border-bottom-right-radius:0}vaadin-menu-bar-item[theme~=dev-tools]::part(content){display:flex;gap:.5rem}hr[theme~=dev-tools]{margin:.25rem 0;opacity:0}::part(overlay){background:var(--vaadin-overlay-background);color:var(--vaadin-text-color);font-family:var(--copilot-font-family);font-size:var(--copilot-font-size-sm);font-weight:400;letter-spacing:var(--copilot-letter-spacing-sm);line-height:var(--copilot-line-height-sm)}vaadin-popover#dev-tools-popover{--vaadin-popover-background: linear-gradient(light-dark(var(--gray-1), var(--gray-5)) 0 0) padding-box, linear-gradient(90deg, var(--vaadin-blue), var(--vaadin-violet)) border-box;--vaadin-popover-border-color: transparent;--vaadin-popover-border-width: 2px;--vaadin-popover-offset-bottom: .75rem}vaadin-popover#dev-tools-popover::part(arrow){--vaadin-popover-border-color: var(--vaadin-violet);--vaadin-popover-border-width: 2px;border:var(--vaadin-popover-border-width) solid var(--vaadin-popover-border-color);margin-inline-end:.4375rem}vaadin-radio-group[theme~=filled]::part(group-field){background:light-dark(var(--gray-5),var(--gray-7));border-radius:var(--vaadin-radius-s);flex-direction:row;flex-wrap:wrap;gap:.125rem 0;padding:.125rem;width:fit-content}vaadin-radio-group[theme~=filled] vaadin-radio-button{--vaadin-radio-button-gap: 0;border-radius:.125rem}vaadin-radio-group[theme~=filled] vaadin-radio-button:before{content:none}vaadin-radio-group[theme~=filled] vaadin-radio-button[focused]{outline:var(--vaadin-focus-ring-width) solid var(--vaadin-focus-ring-color)}vaadin-radio-group[theme~=filled] vaadin-radio-button::part(radio){display:none}vaadin-radio-group[theme~=filled] vaadin-radio-button label{align-items:center;display:inline-flex;gap:.375rem;padding:.25rem .75rem}vaadin-radio-group[theme~=filled] vaadin-radio-button label:has(vaadin-icon:first-child){padding-inline-start:.5rem}vaadin-radio-group[theme~=filled] vaadin-radio-button[checked]{--vaadin-radio-button-label-color: var(--vaadin-text-color);background:light-dark(var(--gray-1),var(--gray-5))}vaadin-select-value-button{display:flex;padding:0}vaadin-item,vaadin-select-item{--_lumo-selected-item-height: 0;--_lumo-selected-item-padding: 0}vaadin-item[role=option]{font-size:var(--copilot-font-size-sm);gap:var(--vaadin-item-gap);padding:var(--vaadin-item-padding)}vaadin-item,vaadin-select-item{line-height:var(--vaadin-input-field-value-line-height)}vaadin-tab{background:var(--vaadin-tab-background);border-radius:var(--vaadin-tab-border-radius);font-size:var(--vaadin-tab-font-size);font-weight:var(--vaadin-tab-font-weight);line-height:var(--vaadin-tab-line-height);padding:var(--vaadin-tab-padding)}vaadin-tab:before,vaadin-tab:after{content:none}vaadin-tab[selected]{--vaadin-tab-background: light-dark(var(--gray-4), var(--gray-7))}vaadin-tabs{box-shadow:none;min-height:auto;padding:var(--vaadin-tabs-padding)}vaadin-tabs::part(tabs){margin:0}vaadin-tabsheet::part(tabs-container){box-shadow:none}vaadin-tabsheet[theme~=horizontal]{flex-direction:row}vaadin-tabsheet[theme~=horizontal] vaadin-tabs::part(tabs){flex-direction:column}vaadin-tabsheet[theme~=horizontal] vaadin-tab{justify-content:start}vaadin-tabsheet[theme~=horizontal]::part(tabs-container){border-inline-end:1px solid var(--vaadin-divider-color)}`, _c = "vaadin-dialog::part(overlay){background:var(--background-color);-webkit-backdrop-filter:var(--surface-backdrop-filter);backdrop-filter:var(--surface-backdrop-filter);border:1px solid var(--contrast-color-5);border-radius:var(--vaadin-radius-m);box-shadow:var(--surface-box-shadow-1)}vaadin-dialog::part(header){background:none;border-bottom:1px solid var(--divider-primary-color);box-sizing:border-box;font:var(--copilot-font-xs-semibold);min-height:var(--copilot-size-xl);padding:var(--space-50) var(--space-50) var(--space-50) var(--space-150)}vaadin-dialog h2{font:var(--copilot-font-xs-bold);margin:0;padding:0}vaadin-dialog::part(content){font:var(--copilot-font-xs);padding:var(--space-150)}vaadin-dialog::part(footer){background:none;padding:var(--space-100)}vaadin-dialog.ai-dialog::part(overlay){max-width:20rem}vaadin-dialog.ai-dialog::part(header){border:none}vaadin-dialog.ai-dialog [slot=header-content] svg{color:var(--blue-color)}vaadin-dialog.ai-dialog::part(content){display:flex;flex-direction:column;gap:var(--space-200)}vaadin-dialog.ai-dialog p{margin:0}vaadin-dialog.ai-dialog label:has(input[type=checkbox]){align-items:center;display:flex}vaadin-dialog.ai-dialog input[type=checkbox]{height:.875rem;margin:calc((var(--copilot-size-md) - .875rem) / 2);width:.875rem}vaadin-dialog.ai-dialog button.primary{min-width:calc(var(--copilot-size-md) * 2)}vaadin-dialog.drop-api-dialog::part(overlay){width:35em}vaadin-dialog.drop-api-dialog::part(header-content){width:unset;justify-content:unset;flex:unset}vaadin-dialog.drop-api-dialog::part(title){font-size:var(--copilot-font-size-sm)}vaadin-dialog.drop-api-dialog::part(header){border-bottom:unset;justify-content:space-between}vaadin-dialog.drop-api-dialog::part(content){padding:var(--space-100);max-height:250px;overflow:auto}vaadin-dialog.drop-api-dialog div.item-content{display:flex;justify-content:center;align-items:start;flex-direction:column}vaadin-dialog.drop-api-dialog div.method-row-container{display:flex;justify-content:space-between;width:100%;align-items:center}vaadin-dialog.drop-api-dialog div.method-row-container div.class-method-name{padding-left:var(--space-150)}vaadin-dialog.drop-api-dialog div.method-row-container div.action-btn-container{width:150px}vaadin-dialog.drop-api-dialog div.method-row-container div.action-btn-container button.action-btn.selected{color:var(--selection-color)}vaadin-dialog.edit-component-dialog{width:25em}vaadin-dialog.edit-component-dialog #component-icon{width:75px}vaadin-dialog#report-exception-dialog{z-index:calc(var(--copilot-notifications-container-z-index) + 1)}vaadin-dialog#report-exception-dialog::part(overlay){height:600px}vaadin-dialog#report-exception-dialog vaadin-text-area{width:100%;min-height:120px}vaadin-dialog#report-exception-dialog .list-preview-container{display:flex;flex-direction:row;gap:var(--space-100);margin-top:var(--space-50)}vaadin-dialog#report-exception-dialog .left-menu{display:flex;flex-direction:column;min-width:200px;width:200px}vaadin-dialog#report-exception-dialog .right-menu{display:flex;flex-direction:column;white-space:break-spaces;overflow:auto;border-radius:var(--vaadin-radius-m);align-items:start;height:300px;width:800px}vaadin-dialog#report-exception-dialog .right-menu pre{margin:0}vaadin-dialog#report-exception-dialog vaadin-item div.item-content{display:inline-block}vaadin-dialog#report-exception-dialog vaadin-item div.item-content span{max-width:150px;white-space:nowrap;text-overflow:ellipsis;overflow:hidden;display:block}vaadin-dialog#report-exception-dialog vaadin-item div.item-content span.item-description{color:var(--vaadin-text-color-secondary)}vaadin-dialog#report-exception-dialog vaadin-item[selected]{background-color:var(--active-color);border-left:2px solid var(--primary-color)}vaadin-dialog#report-exception-dialog vaadin-item::part(content){display:flex;align-items:center;gap:var(--space-100)}vaadin-dialog#report-exception-dialog vaadin-item::part(checkmark){display:none}vaadin-dialog#report-exception-dialog div.section-title{color:var(--vaadin-text-color-secondary);padding-top:var(--space-50);padding-bottom:var(--space-50)}vaadin-dialog#report-exception-dialog code.codeblock{width:100%;box-sizing:border-box;overflow:auto;text-overflow:unset}", $c = ":is(vaadin-context-menu,vaadin-menu-bar,vaadin-select){z-index:var(--z-index-popover)}", ed = "", td = `:host{--vaadin-radius-s: .25rem ;--vaadin-radius-m: .5rem ;--vaadin-radius-l: .75rem ;--copilot-size-sm: 1.75rem;--copilot-size-md: 2rem;--copilot-size-lg: 2.5rem;--copilot-size-xl: 3rem;--vaadin-focus-ring-width: 2px;--vaadin-button-background: light-dark(var(--gray-3), var(--gray-6));--vaadin-button-gap: .375rem ;--vaadin-button-margin: 0;--vaadin-button-padding: .375rem .75rem ;--vaadin-button-font-size: var(--copilot-font-size-sm);--vaadin-button-primary-background: var(--vaadin-text-color);--vaadin-button-primary-font-weight: 500;--vaadin-button-primary-text-color: var(--vaadin-background-color);--_vaadin-button-disabled-pointer-events: all;--vaadin-checkbox-background: transparent;--vaadin-checkbox-border-radius: .125rem ;--vaadin-checkbox-font-weight: 400;--vaadin-checkbox-size: 1rem ;--vaadin-checkbox-label-color: var(--vaadin-text-color);--vaadin-checkbox-label-font-size: .8125rem ;--vaadin-checkbox-label-line-height: 1.25rem ;--vaadin-checkbox-checkmark-color: var(--vaadin-background-color);--vaadin-checkbox-label-padding: 0 .5rem ;--vaadin-icon-size: 1.25rem ;--vaadin-icon-visual-size: 1.125rem ;--_vaadin-icon-chevron-down: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="currentColor"><path d="M480-344 240-584l56-56 184 184 184-184 56 56-240 240Z"/></svg>');--vaadin-input-field-background: transparent;--vaadin-input-field-border-color: light-dark(var(--gray-9), var(--gray-10));--vaadin-input-field-border-radius: var(--vaadin-radius-s);--vaadin-input-field-border-width: 1px;--vaadin-input-field-container-gap: .25rem ;--vaadin-input-field-disabled-background: transparent;--vaadin-input-field-disabled-text-color: var(--vaadin-text-color-secondary);--vaadin-input-field-gap: .375rem ;--vaadin-input-field-height: auto;--vaadin-input-field-invalid-background: transparent;--vaadin-input-field-autofill-color: var(--vaadin-text-color);--vaadin-padding-block-container: calc((6 / 16 * 1rem) - var(--vaadin-input-field-border-width));--vaadin-padding-inline-container: calc((8 / 16 * 1rem) - var(--vaadin-input-field-border-width));--vaadin-input-field-padding: var(--vaadin-padding-block-container) var(--vaadin-padding-inline-container);--vaadin-input-field-label-color: var(--vaadin-text-color-secondary);--vaadin-input-field-label-font-size: .75rem ;--vaadin-input-field-label-font-weight: normal;--vaadin-input-field-label-line-height: 1.125rem ;--vaadin-input-field-value-font-size: var(--copilot-font-size-sm);--vaadin-input-field-value-font-weight: 400;--vaadin-input-field-value-line-height: var(--copilot-line-height-sm);--vaadin-input-field-helper-font-size: var(--copilot-font-size-xs);--vaadin-input-field-helper-line-height: var(--copilot-line-height-xs);--vaadin-input-field-helper-spacing: .25rem ;--vaadin-input-field-error-color: var(--ruby-11);--vaadin-input-field-error-font-size: var(--copilot-font-size-xs);--vaadin-input-field-error-line-height: var(--copilot-line-height-xs);--vaadin-item-border-radius: var(--vaadin-radius-m);--vaadin-item-gap: .5rem;--vaadin-item-padding: .375rem .5rem;--vaadin-overlay-background: light-dark(var(--gray-1), var(--gray-5));--vaadin-overlay-border-color: var(--vaadin-divider-color);--vaadin-overlay-border-radius: var(--vaadin-radius-l);--vaadin-overlay-border-width: 1px;--vaadin-popover-border-radius: var(--vaadin-radius-l);--vaadin-radio-button-font-weight: 500;--vaadin-radio-button-label-font-size: .8125rem ;--vaadin-radio-button-label-line-height: 1.25rem ;--vaadin-tab-background: transparent;--vaadin-tab-border-radius: var(--vaadin-radius-m);--vaadin-tab-font-size: var(--copilot-font-size-sm);--vaadin-tab-font-weight: var(--copilot-font-weight-medium);--vaadin-tab-line-height: var(--copilot-line-height-sm);--vaadin-tab-padding: .375rem .5rem ;--vaadin-tabs-gap: .25rem ;--vaadin-tabs-padding: 0 .5rem ;--vaadin-tabsheet-padding: 0;--vaadin-tooltip-background: var(--gray-3);--vaadin-tooltip-border-color: var(--vaadin-divider-color);--monospace-font-family: Inconsolata, Monaco, Consolas, Courier New, Courier, monospace;--z-index-component-selector: 100;--z-index-floating-panel: 101;--z-index-drawer: 150;--z-index-opened-drawer: 151;--z-index-spotlight: 200;--z-index-popover: 300;--z-index-activation-button: 1000;--copilot-notifications-container-z-index: 10000;--duration-1: .1s;--duration-2: .2s;--duration-3: .3s;--duration-4: .4s;--button-background: var(--gray-100);--button-background-hover: var(--gray-150);--focus-size: 2px;--icon-size-xs: .75rem;--icon-size-s: 1rem;--icon-size-m: 1.125rem;--shadow-xs: 0 1px 2px 0 rgb(0 0 0 / .05);--shadow-s: 0 1px 3px 0 rgb(0 0 0 / .1), 0 1px 2px -1px rgb(0 0 0 / .1);--shadow-m: 0 4px 6px -1px rgb(0 0 0 / .1), 0 2px 4px -2px rgb(0 0 0 / .1);--shadow-l: 0 10px 15px -3px rgb(0 0 0 / .1), 0 4px 6px -4px rgb(0 0 0 / .1);--shadow-xl: 0 20px 25px -5px rgb(0 0 0 / .1), 0 8px 10px -6px rgb(0 0 0 / .1);--shadow-2xl: 0 25px 50px -12px rgb(0 0 0 / .25);--copilot-size-xs: 1.25rem;--space-25: 2px;--space-50: 4px;--space-75: 6px;--space-100: 8px;--space-150: 12px;--space-200: 16px;--space-300: 24px;--space-400: 32px;--space-450: 36px;--space-500: 40px;--space-600: 48px;--space-700: 56px;--space-800: 64px;--space-900: 72px}:host{--lumo-font-family: var(--copilot-font-family);--lumo-font-size-xs: var(--copilot-font-size-xs);--lumo-font-size-s: var(--copilot-font-size-sm);--lumo-border-radius-s: var(--vaadin-radius-s);--lumo-border-radius-m: var(--vaadin-radius-m);--lumo-border-radius-l: var(--vaadin-radius-l);--lumo-base-color: var(--surface-0);--lumo-header-text-color: var(--color-high-contrast);--lumo-tertiary-text-color: var(--color);--lumo-primary-text-color: var(--color-high-contrast);--lumo-primary-color: var(--color-high-contrast);--lumo-primary-color-50pct: var(--color-accent);--lumo-primary-contrast-color: var(--lumo-secondary-text-color);--lumo-space-xs: var(--space-50);--lumo-space-s: var(--space-100);--lumo-space-m: var(--space-200);--lumo-space-l: var(--space-300);--lumo-space-xl: var(--space-500);--lumo-icon-size-xs: var(--copilot-font-size-xs);--lumo-icon-size-s: var(--copilot-font-size-sm);--lumo-icon-size-m: var(--copilot-font-size-md);--lumo-font-size-m: var(--copilot-font-size-xs);--lumo-body-text-color: var(--vaadin-text-color);--lumo-secondary-text-color: var(--vaadin-text-color-secondary);--lumo-error-text-color: var(--error-color);--lumo-size-m: var(--copilot-size-md);--source-file-link-color: var(--blue-600);--source-file-link-decoration-color: currentColor;--source-file-link-text-decoration: none;--source-file-link-font-weight: normal;--source-file-link-button-color: currentColor;--vaadin-button-height: var(--lumo-button-size, var(--lumo-size-m))}:host{color-scheme:light;--surface-0: hsl(var(--gray-h) var(--gray-s) 90% / .8);--surface-1: hsl(var(--gray-h) var(--gray-s) 95% / .8);--surface-2: hsl(var(--gray-h) var(--gray-s) 100% / .8);--surface-background: linear-gradient( hsl(var(--gray-h) var(--gray-s) 95% / .7), hsl(var(--gray-h) var(--gray-s) 95% / .65) );--surface-glow: radial-gradient(circle at 30% 0%, hsl(var(--gray-h) var(--gray-s) 98% / .7), transparent 50%);--surface-border-glow: radial-gradient(at 50% 50%, hsl(var(--purple-h) 90% 90% / .8) 0, transparent 50%);--surface: var(--surface-glow) no-repeat border-box, var(--surface-background) no-repeat padding-box, hsl(var(--gray-h) var(--gray-s) 98% / .2);--surface-with-border-glow: var(--surface-glow) no-repeat border-box, linear-gradient(var(--background-color), var(--background-color)) no-repeat padding-box, var(--surface-border-glow) no-repeat border-box 0 0 / var(--glow-size, 600px) var(--glow-size, 600px);--surface-border-color: hsl(var(--gray-h) var(--gray-s) 100% / .7);--surface-backdrop-filter: blur(10px);--surface-box-shadow-1: 0 0 0 .5px hsl(var(--gray-h) var(--gray-s) 5% / .15), 0 6px 12px -1px hsl(var(--shadow-hsl) / .3);--surface-box-shadow-2: 0 0 0 .5px hsl(var(--gray-h) var(--gray-s) 5% / .15), 0 24px 40px -4px hsl(var(--shadow-hsl) / .4);--background-button: linear-gradient( hsl(var(--gray-h) var(--gray-s) 98% / .4), hsl(var(--gray-h) var(--gray-s) 90% / .2) );--background-button-active: hsl(var(--gray-h) var(--gray-s) 80% / .2);--color: var(--gray-500);--color-high-contrast: var(--gray-900);--color-accent: var(--purple-700);--color-danger: var(--red-700);--border-color: var(--gray-150);--border-color-high-contrast: var(--gray-300);--border-color-button: var(--gray-350);--border-color-popover: hsl(var(--gray-hsl) / .08);--border-color-dialog: hsl(var(--gray-hsl) / .08);--accent-color: var(--purple-600);--selection-color: hsl(var(--blue-hsl));--shadow-hsl: var(--gray-h) var(--gray-s) 20%;--lumo-contrast-5pct: var(--gray-100);--lumo-contrast-10pct: var(--gray-200);--lumo-contrast-60pct: var(--gray-400);--lumo-contrast-80pct: var(--gray-600);--lumo-contrast-90pct: var(--gray-800);--card-bg: rgba(255, 255, 255, .5);--card-hover-bg: rgba(255, 255, 255, .65);--card-open-bg: rgba(255, 255, 255, .8);--card-border: 1px solid rgba(0, 50, 100, .15);--card-open-shadow: 0px 1px 4px -1px rgba(28, 52, 84, .26);--card-section-border: var(--card-border);--card-field-bg: var(--lumo-contrast-5pct);--indicator-border: white}:host(.dark){color-scheme:dark;--surface-0: hsl(var(--gray-h) var(--gray-s) 10% / .85);--surface-1: hsl(var(--gray-h) var(--gray-s) 14% / .85);--surface-2: hsl(var(--gray-h) var(--gray-s) 18% / .85);--surface-background: linear-gradient( hsl(var(--gray-h) var(--gray-s) 8% / .65), hsl(var(--gray-h) var(--gray-s) 8% / .7) );--surface-glow: radial-gradient( circle at 30% 0%, hsl(var(--gray-h) calc(var(--gray-s) * 2) 90% / .12), transparent 50% );--surface: var(--surface-glow) no-repeat border-box, var(--surface-background) no-repeat padding-box, hsl(var(--gray-h) var(--gray-s) 20% / .4);--surface-border-glow: hsl(var(--gray-h) var(--gray-s) 20% / .4) radial-gradient(at 50% 50%, hsl(250 40% 80% / .4) 0, transparent 50%);--surface-border-color: hsl(var(--gray-h) var(--gray-s) 50% / .2);--surface-box-shadow-1: 0 0 0 .5px hsl(var(--purple-h) 40% 5% / .4), 0 6px 12px -1px hsl(var(--shadow-hsl) / .4);--surface-box-shadow-2: 0 0 0 .5px hsl(var(--purple-h) 40% 5% / .4), 0 24px 40px -4px hsl(var(--shadow-hsl) / .5);--color: var(--gray-650);--background-button: linear-gradient( hsl(var(--gray-h) calc(var(--gray-s) * 2) 80% / .1), hsl(var(--gray-h) calc(var(--gray-s) * 2) 80% / 0) );--background-button-active: hsl(var(--gray-h) var(--gray-s) 10% / .1);--border-color-popover: hsl(var(--gray-h) var(--gray-s) 90% / .1);--border-color-dialog: hsl(var(--gray-h) var(--gray-s) 90% / .1);--shadow-hsl: 0 0% 0%;--lumo-disabled-text-color: var(--lumo-contrast-60pct);--card-bg: rgba(255, 255, 255, .05);--card-hover-bg: rgba(255, 255, 255, .065);--card-open-bg: rgba(255, 255, 255, .1);--card-border: 1px solid rgba(255, 255, 255, .11);--card-open-shadow: 0px 1px 4px -1px rgba(0, 0, 0, .26);--card-section-border: var(--card-border);--card-field-bg: var(--lumo-contrast-10pct);--indicator-border: var(--lumo-base-color)}`, rd = '@font-face{font-display:swap;font-family:Roboto;font-style:normal;font-weight:400 700;src:url(data:font/woff2;base64,d09GMgABAAAAAzJMABYAAAAHKhAAAzHRAAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAGoh9G4KFBhyrdj9IVkFStDUGYD9TVEFUgVwnPACNBAiBfgmfBi+CNBEMCoaQaIW4JQuUVAAwksVgATYCJAOpJAQgBZN8ByAMhU5b+vGWBtZCUsmzWbd5wxGKO9FNwAK41USj2rbb81fhjiQjZqCcgOp3czKG1wFjqlX3H5sAOsZ2xMwgwuwaXs4zE2txlT37//////+3Jl9irj4Z3JfMDKUKK4KIpWFB73b/fQITtc45H0ZxkoYZPHIqxFotK3XkyiWldeP9d1qWtPKetXmybn0SU9fLJkZOXTM0W3L5hr5HYQoKdxkT0P6wZQkOx7QwAqwuIZnkp3Ndu+QCwhWEG1mdlFJPd/c4xF3/nGggt0nxkh5k1/V0FSVbkH0Ke7LnUthLV+TZLMyCenIKEBjYTnbs7WlvDR8EC6kwRzRdrzTuD6eLc9HFXZVZ0Co6sg50Ox0zjmO4N+IgoAq0Q77Ns6XX4/Tnb/bpxiZaSyXpvKmF2VA+eIA0pFiy0SnpW3xJex0bcVT2GtbnqGR39qIbHqwoqeZ6Df5jxsKmJQxvq5ui+23H0JwMZjEKzDDD2NU1oQjptyga6nZ+SnbNt1yYzY0Yjb9309IGri4ggAHg8epEhfJCqY3GZt35j6Q01OqcFLuDt95LQv1K1dyw3X8ZQlghYcKvYJIIubvAzso6AgkMoZ996BxizD2vwc7GY3RmvfQ0fB2/gHLetCXzi7st7H5jEUACEbCPCinYUoUUgKqQgq0+VrnzV1G+5hKt3hT++7ViFBgQHBwFWSaGKeCS58Mx3sRM3APWMtGP7VIWOlso6w5V2jgWD/Y78Ju1PLbGM/MBD1/AmzTIOtDbABq/iNAPYB5hKBHgDvbi/wYOISzmvK4bwrbFaJnVv+HPa6ieroUvcv3BFm6mlJSUha/CEixKVJEbnhW7nUAtH1k6fZUjwVwZLyyKi/Q0z7T0tZKNmbXS2o41gG5i08uvKAaXnARs1DzLKLckD+B/w3znTxp9DIP0f7RkN5YsZGZ7ktwwLUDJuFufVjUJYBLKGwXHa0uiU30Qpo2yM/vBMjM4SSyci4ibRy/Sh/STKJKhVT29kmwdIen8QJgxhqfwEShEzChk1M/za+vPfTXBFMMwDMMw4EAPkkOKMGRJS7vKsoCKfpaPTYYFoo02i7uYuYiI1ZRI6SogBqAY+PD8nP3/2nLEIoQQCFqhzHTEr1hPaOdef++bypEkSAjqAQ/upVD1Ue3MlY54/edpOf/cZ/NmZndWI7YRVIKEBEkQk98PX8ySbKF1akK/CzVHrP3QJpBAkAQLFhIktjbz8/x+8597X/6ENigxCrF6fl2FrnQtYiMoGD2H9tIFTjG+rowKnBEJaBP587177/nLVN+3Md3thQSAiARBCAySfsqhZC+5af+4KV0Vnt7j4XGm/rNMsmzJYksW25Yss0x0ts8HlAvgBRoo08a5OG1SorXrgNshdN3W/v6t7YALuBKmSQN0YB9PU+3HTricsEN2wICyCFeMuxLvasUrIMuyqI6c9hh79wHbI85RekmBKU3vAly/AdyyylinIrJi+mWs9TO1J4IFCSS4hSAaIKgMY9b6VETWhqdZ89/qiQOHWq8O/S0V+ssdUlGl7XdhL0ehdaACtIgdp7t7u56VbMwmPpaZzExGLRPRI1zq/6ykA58pgAV+AOwmHwki6ey0HxDaV2AM147hfAQSL80MD9Wy3iqzq2bmG2ehjKNaIXnIK3d6XQiZY/jQQXq0H3z/3PO/PXvmXHh/eWgJBEkPP6QYQlZESfBJy/p6kWPXOIDgAigps2r6r/NH0SFs3HSl4ihnmwFPwE9VNrvtWc+8Jvv5Yk9xOQdyWOrgQ9+m8bPswkKAmBGr5lLP9cRDkvoTkS+VL2OBQIIGk11YxTxISIhZXU56bn3XDsE2OxSxCqPyFRWLscbIFSZ7UBdRYFGKkRizGplYifa7LF9xYeekNkuybBlb/iQDNraBwG8giEhkZ72ffH//lTfbb5k6eLarrucJ7PSZVEC51WUt0uV/3ZLWfnCEQXRBkv7RyPYRhsEFMXBEbFzNdHdV3bdf20ybZQ987e+Jex4snSMaxXMiNh6hjf/0TbM/Fwq7W7JsD4RoFogf/xGhq1u2J/wAeRGTBcC8TGBphm3ZUteFT36/nqokp/K6BxQxO1hEFmbWkSKQaNmjHrX8OzcA6Pk6+vfcN5N9AQoHZBOMQAReVD+lfNeQZ3oEN6tZOHEm3pAOZkIIWRpCSOecce6EcybOzAjnnDHjxHjemHXiZCHMmBhznmNGyJ50ol2YW8j1hH51zGxySTDAu63HmaNciIqIe+FEUURAQMZWmSLDhQz3yNTQhuNZSUsbk2xCS8tsXuOyMcbv2ld3/Yvn4XW+XuUnXMWWSkSyP3AJ2YGt09JhIaRhBHxn6N38/8aTSRwp1Oix22eqf+v6tc6EVfejfisGpUCI2CQDD7xc+kP5kkqYsrcvlwKYQIVCC/+POO27kpPiZ9ACcRaIvUCcI8lOPxDRtjOJ9eAJ56LfCVCbpO0S3N0n9kDKofGZj8LKBw/fmv/emd3JZhP+wOx+XoFkhWJX2VM9dzcvv8CkKtmjrOqp4+MBc6jz+0Y4QsuSOXH44TLloO3YTrIf+Uqm+q6rCSvK8Hr/7y/kD/AJVwp4EeMgOEk5dezMYe48dZjeALit8OqusTUz8wwVQZAxpwLiWuhmCNsBCIroRhFUINGdttS0Oq285nXdWL/WhKf/tf7x3646jXPv4xAuH6EjZJSIMHHAGj3rCBcjyYFLPuCd6e6qDSEuS7Xdr+gDYAiumGUk+yqhQSQUXfXantT0v6+qY7oBzAx59fTW2WzDdLPkNRpDUut8kKT+f0kEDmjOMOGM+CJ9p3baZi+aZiGQAEmQExVWljbls/dCDEV3TYF+/Cq7b798+C/WNtTl32NPcsTMMjUriGHkWkuy90ggM3xR0dbv99oqu5ew10AylF6Fmr0YeAANQ16X+gzSoC/IjPHdK2fJl1EbmoLgSmf/dQtsuVuyrL9HIWAIFBFVPmBi2dajPeNoEiv1ph+Jb8GP/cx+3gC62EYcigXdNNPLV3JHYK1BdXYnmbtH90KjkASSAP2zBEIhAYVFLE+8ObQxD1UPYjLCTUGCjOozJG953NvONs5FFaeW+oDy5zd4+BE/5//PyrEE6lCxXDFP+977KhrgSsWuqFFaSk2p0pa2FEuwQIMESAmcc3ZnZtVG2o4DBw8lzit1yjBFSpHgCpRh6jJet1f35frjZAYoD19Lp0Np6p67M44VDitT2rKKqPucyni6SoSjjcc5IpGl/r2pVul/ACk2KWn8rrTGmcpBmTVaY1x0Lj70e//3RzsABECJBOgGlKEojQxntMOR1uB3N4AG0IRIkeI4zciMdVrjOdIZ66OLLojWnHVBeEFyQaY5ZyfccOuiueyC+II4vCA6+H/amyXt986E1v8OKotIRjvQIQOyKUFDI9D/Vcty11VP+DVRtVHtrM3trHVEGzIy4maSndkMM8zIAJkQI+r//0vV/n/PJkW5JNsdUO50IOcOUPoOheQOBd9+/4eScpd6HL7e+PODOucUCqiBLFQBElggJRCkZBCgnEKBlAGQkkFS9oVEyEtOnPvsJK+Xk/RLD1PSo3suAKRdBKU0SFrdtNTpJ9vJzXTnwd2dl5W+o988fr91P77eNH19vP/38Xnv+37/u1Spvi299+XeZUy7O9lmKWABNLDdBEYnXc6KT97X4dGxnY4x0Zi4MAD2cbtjDDY2oX2BRaQE1bBm/2uJjkG1xJO9uyXuh7iEIUaVkpEIhXIjD3eS/9v8Tzt36/y3ZnCX7XG8Lc0r3qTB2wakDTcNSAPSYDv7U7qOiooxcZEDafDV46WRqN5UPz4qES5CrpYRsjdhRfz/17Qyffd9fIqCUN3kR1UfEON9DyIQIuXuWdck9jkSAAoSQVBVWqqmtpmq0qxL5F0giSeSEtG7XKWeZZUdjZ06m5nQaeQzQeQkc5LK23JyO3OQpf57m2b7367Wt4a50RplH5EcwKKRnXB1dpcaqEuqL8hoVzJ8s6wDIkkBxg4A17zHMq8PpQPEKsQwfcpUZYqSuezTlCnKFC1VKRMwauFs1iUzRXhwFUL2cI87x//vq+r6Pj5JQXIKKbfKZJHSyk4prW/xmmV679538cr/H/j4IHlQCBkAVUDQ1AE/KRsAZYeAG+TaJp9MdqZWJhAkXVqbJtpT4ilzhm2L/6matQQf/pgiVjOEuWNJlyUHeS+kovJd5ZJd6SWApU6f32HpDNu8EJN0idutO9f3v9NKqu//7f2zt8UzV9MKQAFIcyy1I2J/SaO3O5Jn32vN6Y1ddaquO+yWBQaGkAsKBfH/v1St7S/SUhdl9zkkPYGdsjwhLjYpnP1s9rNDvfve/8QPBbJ+FSCgCqQJEJRFgla3KUsORz4FrUBNoj2JnTkhrtSTl50mpLBadiWABEHt4iBMIFjxFIPkJkkDDBNL5REf/t0KKt8PozB0QYuIFCJFCEHmvOfdyb6vlxZ0d8+BigwyiIiUXsmVUEI3hNAnMHi/H/TPt+ZXdeGJY+fko5kcEIlxsWp71jR45gGionFcYlwLUVT0/svhVm6l/uyn/ueYJrabd9O1zHasCAoCwnFk9esd+y16BwbQRscV16LlXv1FHvtp/YKYUr2Y0i0RARFlAEPIlfp1r5imM3iXFKaqE20/d4pcCqBluej3vql+L0krG9ll++tvpTnxVEUYFJ5AEIxAExiGYD95ebG2imoihXkdZ+6VNCIiIiIiIpIUUkhRFMUeQ2cO6I8xacywmmrGBAJ33IUKIVvjRuhr+Lv+yJr+olLbuiPuOu0623vzAAUhQC7IS/II+b8OH7C2oYajG+sYx6MQy1pVE92BnNpLOTP58ef63mCY6dsKe+dJFRAiBFLaSQjqffu5vtuHz3dP/G3f291S5mOQIoOISBCRIEFCkD1Edf+VU5FCgKe+sEq6ks60BTK8ZFm2ZN1p2v9v/oHHNj/uIyFiXA4fLXahlm3mEJvWydghbyuOfm+abEITtoXfAQN+JFmWRpqR4G4RVfsvHTtp4qZVXnprduZWlrdiS7Qkk5Q4AAKw/4cQ2xSxn4ht/kK0QKHJ0bsCxxfO5mLeejiYWAZcIJ4SW07/gWRB/oTkIwoRJYjyPio1a9CjUb8Dhm4eNoE5Rkqct4a4grjWx7/25Zsgt/u466gHPvVYo07n8muQoT7euOa9lJ94NQPRBEc4OmIQJkgSkW5Cq/qJgsc0iR02KcAuJXPS4BYNnxgExCosThEhJCRiQSgLQ0tLWnoYS1KQldLrT1OFZUQAGcIGYGNsXIxLKEesQFvJLDV66uQmWJqjsMBdFS+1fNTz12qFHdXiVZ2fNAX8K6416N9m3S+qs55vVdX3Oc0iCAqDYOMS2+yc0Z5Z7JuzarmlzQaHFFVaZ45kAlw4omc8HTgctuOwceDUJA8HjZRmHtX/74b8DgFyC+Aj4PngeAIIAQEDZ/fGyyeh8vMXL8FnvvZ1Bst+6knhh5+lGoN+9bmE9b+mOj1/e4+GYb+VNRT87Uew/f0fbOb+XIHo7acLirczBf45+r0K8Dr1lQSyd06tU7nz+bBl994I4dhDQxkGBAO6d/2KPfgB1J/v8FWOXK3paiev03cHCPs7/AYM0Of+EZPSGl4jKD1j0uud0++de//8+/Mfrn3s/2Tzk4gRMxJm0oTsa/Z1+6Zzy7nt3nHvuvfc+40HjcfeTnO3tdfabx23TzrjzqRz2p12z7qz7ry3MLFJbGZzLLD0zxicDCZLWy2DhIa0Zy5sXEMfBzayic98xdd8I7biRJypc3Wl7+yDffHvhvfDB/E+MKJMVIg62SBHto9tX7Vr9e7Ne3fsO+vA5YeuPfyJIyBegAAkm6CFnyw9fvymZ6589chrh18//PbB9/d/sO/DfR/tPbjn0J6P67frd2p3a93Vnqp9uJBcSiz37z152cnLT1xx4ujxK4+7aio9U8gYnc/MZA3534XfRX35v6qupmtNDSbntQvaU4fOHDi/99qPjQsMAW5NCcBXffDMtG1P5eLHYr+8AbbF24Pf/msHAc9H2A2LYPeYytQCGD+RNC2DGAHYQ8Cpiu+9eve7G+Qjfhp14PKEhGEZ7PwIsMRYuvEnCdhqyDL34H8vsHHL38b69QcpiA5Dh8UOJkwyT+D/luLIgmnRPJjDam8Ba/SpvxqffcF9Lqip0s3+avBRVPD0n3qzQcIJN7zwIwgaSbDebVmtg+yWTLgiTz5THaIC4cGowvFhjgdw8+QHgmEmIa3Ahaa0GZAWbSZ0zEFnczdhRQygRRUE1qEKGAwNU0VoGaReklMZDkNYMESkvDwb87dKZUwMwanZaRaht7ciDUfbBdsocOnhVJ599nId+9jvN/Q9wUlfeBVEbS8mbdrTnf67O/LXF5x04JgF9Mj2wpfPWVHZe+nbG1XOf7NnP0H8p+thf3PeRNmPPKyaeW9RZxrHEl49LTAf7ghs1dDQlDvrvPfjeJea9mKBBjP3Sc7ELJ4DBI+PvoyzqaWN671w3P+fOZdgboIZsdLZK3PMJAeL1z0glYegLRwMyLbLfaN5lOGNJLBWm6W2tvDrTJW5GucBHaglb/J/XKb8qrO8Nbo4lNT6B9TrTWMK/XP/K30aFTlv+3KNdWaa95fga4D2MgIa9DDtaECAKkd/aNWDc/9w4lrf0C9qkWHoeMThDcrDavO3X8+QkMdyRzb9DBquzIZd7Gi/yCVPCZ+nw9p+MV2yZh9v/QQBVVfKLOE1XE+DFx136U9tkffmPErlo6Qv1r2WeuaeH3hWhzaVaL9y33JT6xPwJQtQc/+7X40e2TTkOv/Ya95u+Vi8+ULuE33yuasy64nocRzFqeKmKbcXpX9/J567MqtFjXhUaioDL13v/eZR7YtPLVlOM9dvrS+z/rhfXRNa8upBr+/OTUPuiat3QWuf9dKKh1Ehea9ng56Zw0xfLaJemANxmGuNhsoTAY9qeOOk2T4e/QQBjoNItfDNQ1qY6b0wssPhQb17fh7aemFbG750h7Zln4vLd00v7wr9K12XFi3prVLsqwTPQqnevHWpfTtqpM3LkZ3yYGH2lsQlqKzPRnTreOaHuZgrJ9csyHFe37PXbf2yunDT2FnC101W7rNsUAQsznnxG1Cvs92ksWuhOhtr1//0KMfB74UHbw0+Xp8kC6jnxSS15Zb/dlW71aPqvhHEEAS4m8vxNcOG5r2E4BjvudqWYv8niPXfgIw7z5S/ktcozEfUCqgtVV1VC69bfFeudW3VV+RX8JF5DQjDy1wZNTH1yqG4/u8+XqNC+0gePlXu7dVyyfM42P8kGPrOYYW4/ih8ACRIVoquNQRr9UMaMlYgXfuAX2m8OC/tiJI+88UkiNbFLzp51GD2GDB5jxkFGgcueLz40ODniQWmWsJEkIiia346S7xEMhWK1TvieClB98pDuN425NEcKbTKyYLULMTQepwAq5mC9ayAzWyvUMxOKWznHvf5/bnPmh/O/eapnMdNMb/KY1J8ad5Q4cdzzpxZc0/MnQeYNx9h/nyCBQXA89oOKtoJKrsAVHUhqO7iAmq6FBdR281gR7diZ7djV5/F7j6PPX0RjX0Ze/sq9vV17O+bONC3cbDv4lDf/y8O98OOcqT76uZ4v1eKi/1Fs1zr37OU1j5ShL0+XpRzfQpcrIGjtXCpzopcbqMlV9otcWO9dzvpXN+jyBpi0SpZS6xEtuyzsnWfX5ptu2nSrNvPR2fLHqLII3t4qTy6P1Dk8T22cG7f4xR5w55cMsf2N+TN+0eRt+xfq+Stew555178R961l8fknr05Lu8nhK14euTux4u7q+M26Dr9yUmcYWengR3EAqhhyAq22G9sxX8TbOoxTj4bdDgjiBxiTswGYguxx+NAjSM6J5rmUOVM21wcLgS8UoIPP5iAFBNmHoZwNBEYImGiccWizMcSb5YELIk0FCI2ITajGhBbUduJjmup6H02QQwyMMjY5szISyCQUeTmIGI5hIIchjWfEJejiMnxiayUs+XMBHa2zkxrhlxXyRiovjAOelP+Y3Du1wybRuuf+i8DkGkxgjwnWYpnTpmCz0XzsLlRVsScNlMYc1sINffN45uHBXweQcxj8HkCMU/B5xnEPAfNC7B5CZpOsOkCTTfY9ICmF2z6wKYfbAbBZhhs3oLNO7B5D5oRsBkFzRjYfADNOMh8BMwnkAI4sShglNtnqJYcagBVAFrVgSoErRpAFYFWHfCqgttZfU9YqCF6DYs0wqA6owajRpk1h5awrbattq3W0mupGqpWA7Fujk3zFC2wbZFdS+xb5tAKx1Y5tWbRK7axproQOHUBq67gVAlBO9DocFd0q46RamK02jFBAWpQ3ag0okxIy/mR7G9NwuzvHCKynx5ERgvxZxevwOdCayojy4Te5fWearNje/+N3H65BrJtqYVa+Fr4Ajg7MbHfzDRHOvoD6P0J2l/+7CWVSKUxXShmKJNoZRGjbGKFI145JIhIknJJFoVU0UgXE2xwkQ8eBBChEHKj8BabEm+pKfOWG6W3wqi8aqPxak2lt8pUo8bUos7Uo8E0eptMM1p8Wl2bTzvS2Wd0h8TSuLEsfvSHjgHollN1fcdgkhgKixVJx1ZYd8kH4IOEw5AJPIqeROYwj6scW9WAqhHUSWIvEGqh7GVkZ+l6BVtrJ16rcR5Sb5J7C5RoYB2cj81nEGywwY437IIa6dgvQhiD1lUajHMDmWD0pOiph0XX2J6ddEHMReUNhbcwi1dasoMg4aouP5QynuUZky9CVCw4w2LpJ2rWLtdtaEwUo9DyVyBFawf/Lr/fj0X350UNHoEzcryq2B+2gNk8cg4YMAQMAUPwFHgKPAV+B2cXe32jyXaxT9Ql22Wga4ftCnRXYbsG3XXY7kLrJtcz0XVOIQgEAiHIJUCAgAWaD9QXbupztA26wDb0BSvUIyS7g7gX7wji1z9KSkoikYhAIBCI1woLx2EeuHvpVM3ULe5Vb+TSTsq2pa+Opo4rTDd/arV5S7m30ps0DoNdt05eKc2DVbMSJCAgICAgICAgICAgTKyVFX1i2O2yisG2OrY1cayNa1151pdvQwU2xrMpvs0JbEnoM4nck9hnK5xvpX6Gz00MHqKzdp2p6+xcZ+Q6C9eZt86SdTas14D1misNo2G0TzV2N/0gCdpYMq91T+CN67Ap1/UTz7wQBv7/qPiTQByC5PAB7f/dT6dpTxIe5EAGnJJPBUQDJMcx8mdQ8ec66RIbHoy4wM2xTHbizi+esOMpfV7/rP5thWOiUygZHOlP0h/871X76/c7v5/4+P134zfvm/z18Wvj9V9flb8if8kf41/HskPd+HEYj73L3qIRB2WX8Paj/gd95fP/ln0meh7hQTxptzXcMtD8Nmb471F3XQMsCz1af/z6JSohQHv3y3QEopqMPRZMR/J8Tm4OW+NV6T3mPg5Ybj81yfv6+N5QGUL3Dr/Ofh/SkUDuXQjiwXP3f/MIg9ed/3eBnr9HCL5w8Iek0xLFG5iWjqEvCBf12Cn1Fi1HND/eKJpD0zhPgF10USFfSKiH7mtnxJgJUxYs2VCwZceBkzmczeXClR9/gUIejjcOFyXGfHESJKnXNXdIQqebyZKnjGY8KicQmojjfLIR8blaw1rWMYGe9WxgI5vYzCRT7GQXu9nDXg5wnBOcZMaoMeMmTJryZiJe/ZIVETGrklIy1mXl5BUUlZRVbOhwwRXX3OJ+HvABB6QSDBIB7DyoqGzMhCnrNh2yZVuMWEEW0Z4iVZp0GTJlbTcTW6jEIksstUz1JtNpl4npcJ/9Sz/UMjEznyn115VW7FPVa9aqU3a4ciKWGyVajJixYseJW175FcSLnyBhosQVJk9RcSWVVlZ5yipSpU6Ttsoa83GspIpqaqhlPSc4yenIX8Rn419u0M4tbnOXBzziMU94Tic99PKKPl4zEEK3jAM46tKDE2SYfPuGKbB76nIxbCVWmZzQcghtgIluyos36ymxJ+o2fVIe+e9fK98kabu2dlhlJ+UYDDxsHAwcPIx9I+dG/McXNk/0QJv5zAIWferiSj78B8tYvr7KI7e5wRrP6ev87qZMcdkAeMoqz9OYLtc4F6nOJeIyV7ha2lJU7CZo5xYdeuFXJ+imlz76GWSYt7xjhDHG/dNYQdKKC4Go6JZYKwP6GLrxuOahrggQRQzziSeR5JKarhJpIJ1MlpDFcmWDHHK10sVqzuiCRSttpb31tQ7mjl7itgmIYRrZJpTBvcBQMuNm3drpBlyVR8Qs90uzUjPOA07A1saLhLIFIxVZFRWDUsq8fMTu+0f2AD13YaJMouJT8OmRqEIVNWA9vd63QrDRG9oyBT1r4RjH/UTPAQussMEWe3fsxRrWeffLoIDnFCWMnkqooMZrVyDwH/27Iq2i/USKA6O+rE3q3CTaueUdfQUIXdSvVNJIJ5MlZLGclX7hFBz8C4Cg9fd5b20qxA6B8/UUtN35G3SGNa8OzQcLWMQanvLne3aRS1zmCld5QSfd9NJHP4MM85Z3jDDGuH/qxTwiiCKG+cSTSDLZ5JDLas7Qam27ta/QAQ3uhUkp4Mmm5AdBqimc5pGYzwIWsaZtLdIepIEdKgqPYkops/JoHhISEhISEhISEhISEhJSgBHGGPdPO3wyQaZsOtqJhISEhISEhISEhISEhHROqje3TB0GBgYGBgYGBgYGBgYGBoYZjUPc8bU5M1IrC7h0ejEAzJl65552ZnL9mx0LKdZCy3oZ98w3zMbPZVHnRTsE4wE76LZqS2kte8RQj/IHk48ksIH7Vb6Upj69NcdnAgGh0RirZyvXQMyg99n+li/viMWJzztZfNayX612jNMt3ydubLHDb3bkyr2kYMIK8ztJKumebgfT5MtJjNzEVU8OZmdPz13LF/SikmpqS2fQWE+iUbyiO0BZFSo7ACpxgCpaDiNfSoID9y5B5HyQQ2ogDLNzqPEBXAMzCCaEUMKYRzhp/EF6nn648VwDJCQkZKrsAHBB5TVFs/JFRovJ6ejihXULA/jeqYQ4r97kTcb9vJKZ0L7rYgwf2WTu1wmSrkNFEMGk43+J50phGp9PN/DFfs3o88+aU9HjNkdNJVBRUYNYxZq0lqAN45dnbYotWT1Qw+hFzyt3+2JHWpKtpoKo8drPG9LbOS99gg6BbSulrkJQRDEllCItFSVWdhqc4SznOG8XQqNQUVFRA7Rxk3Zu0ZFuq3CHu9zTffBAD0uhV4+Ffp7aYCghKioqKioqKioqKioqqqkFOlbHhiRkRMmkKHJmJ0sK1iiwwwGn4mIW/D9gEMGEEEoY8wgnjT9I10R0+VlxN9ujcJQeBnNgMhcWriC4EYwHoXgRjg9s/DhCAMcIgkOInYyDAhUVFRUVNUAyqaSRTiZLyGI52eSQy0pWp7UU5TEFFFFCGRWpSoka1utk6VRwhgu6EqpW0JY6E80HLGIFDtQVqKLYDA44+B83QzVlf/wctYJ8VKDY05xqNNJWZpFPU+rd8FUZAPBKIFRlUZXdLvz+pY2yy0kWt2jPXRnTOPhUvriqqATV1KaeRHml10mXdF46LsEBerePakLIfXZX51zyuJeOWdHRHju8U696p7dW2tVsX34yfWv1+D5RWxXO1sntwlbq+472lcW1btILXsm8U4ijQty8BN2bXxKTBc+kpXpZuDN2d5NrNFixRs5qdpdVkTEvwd8f4K1TCP84xeRQMwyPUaCJ8q903kU5m5CGQLympAIzfe357210eKz+PMkOaxMhTdC4hQePrdyw3iP9wfNcXC4HtP5xDMG7+bn/cSDpAVCmg5XMd/PiWOVXF48ickRnKHK89ihLaWp/ATq+k4HgQO+B/BAlufS/aiO0whzgr7zQwLorKOxBT2C4+rG0k2KW1in3YtC1JaOmT1gf/srHM6xnHav+7dCWVTELQU+E7IeZQvjPcq4+7/DUnbuPFMfpsvPLlFc4SFvbr1Osv8LcAZ5KBeO/z7neyx9+znaBfBodw33v0qF7U+q5IpHG4cdM7UWo/vsM1oLaihdndebzZR/0WM5//y7p31jSQua4yex17jq3rf8n6RB6f/oHx76lO0phC+T/burNmDVn3oYdByH5X0e7MAqj4Y211lZ7HXWWrKdUvfXV30CDDTXcSKONlW68iaaaa6FRWPt7WWfClOlzJ7Fww8A1HdaxwLMZASdmkpNEF3CX6K6ltnaHCbefL6OGNz1IANVxAfbQbgMjjng0AmIPQOpByD0Csc3w9eeQegpyT8Pbs5B7DrwXoPUShF6Bp9fgaytkb8Bp2x+Q0cr7a9hkwFzv4F/vYa1RmOsbrGsaEWvpMqwwxuojy7EVy2CGEFSME2QZv1+FsiqFD6OQNQ5s7q01Wlkh9ULhesna1KH1r+u1b/qbfABp9wN4Wj0hbCTtiQ5vT058zAjhN/WeoUL4mTtJnKBKs3HTWkO9wmpHyqz6plyftU3kOpV/H4YUz3NEn90LNh95PI/+OfCMPuQdufO3duz74AoXPB+RPaZP935XLZz6FuWcuuTZl0/c2fxUQi83QeY3uDp4Yzi+sbluczWICdYNlCz6rRcsWbZizYZN2z78vT9g/8WHHXE0j9Yi/Wd6G5C7PJBqTB3X+o91sqGaQOJ+D+0sZO/uPm8hGgWuq3B5dcX0tjHv+u09k6fv0yL15mbRArY1R8bwT/MaW1MdGWTbBtmngXc8jrwifHcyaBMz3UTQIccd5+OuG9nNkOB9TGwM4Od8r8AvV73jnmtVO1b26C7mzw/H4KxoEbBEPqjpr8C1lrXjWnmjJd5APvsOpcZwozW5Iqblg/zdHnKpYCABPyDD2n6ez/AZF+PbsJjyNdGztx1iP9y3+pMbfeR8Ar7/EDC4pGkjv4aK4vtAQAO8nae9m0pB39XnbfYVWGuzdMijw9yO5NunwoIFC5axEukFEuXphSYjP4fKr5zP07XYixuKPSg+fOZLwwbSO/X0Pn6NWvpG5QeugW7FrndkG9xA47O7JV4N/SPMentM2r4CpSMOGqnEU1MBary29pz/eDQ186KqVPDMJVHZv/Y6I63ZV/Ddqjbamb2OuBlqu8320yn6VKcYlFLm5eO6ZMZ1LUVdraI2o0mPpuDTqxC4KfyCxhZBtCW2vFSvFzbRI7cqYkyWWnUBKQbLrZ8ROQ/zEXNmRiqKrlTMlFJm5dEdsz52SXNDQq2iNtKkX1Ow6d2t8rFzPJHb24hy9plh3vCECRc+5J71DtXktp9VJiIERGIBUtB4U1a1KCPKhUSXeynOIyS4QBgjdcNSW/QTBJ6DsUOnF15HEdO8PGqUBy4qQ/w9IGjNGZxtN452Z9bxOdUrODjIGZlwzciGGq8d2+gf+RFcgXkDFyj79GCIjGsHLQWEcCqdtuhnV5Bwolmzp4RiNgqmAwo67lqISO5X/LgX3a1GuSnK+F9/U0EtLdE4nEJ7nVXMvhL+lQWUCJOltdkHi/8HZJ3V538Hlbn3f+fUf1z2W/697xBg7+nzn9rcNN08HR0+6M6ZxN2D2dPGe+eCzQmSr8qDwZR/kmRJGEz5gcGY7w0hdAPH2R4HfFtTtyCKfeNbkJ74lz0BPk82N3FvIAOkG4mFYN3QAte6eazFIcPmWkVga1uXmv9QNxnjwlbx90yg8FXzwZII7qP3G+4PnJQ7jDwmbqf9mDWCeAqUrvOLpYZWDdzmZBW58OCq3g9C/eNfbRW7cbJpqrWTWRf0iHYUDfKkeFBeeVRGfX5A/jjzH/x9K/6KcwPBDtDgkM77p9ZK85NHlSXtbq7YHdQcT24qenL39znEEx5sHQanh95iHg4Xv2XYcu67mjdw16NZ8feVLBfc5KwqF1qbeWDUH3BL051AIuGiNz3hWQUPDtcibyt5Z9Sk0s8NPTScinNfI7ZhxD/OE35CQxu2HLB/IsUZEpvQOr5i0W5Pcw6mUzL853mkRd+9PK+ejfWkdtva4K8KtXOz+Blb2NiYapOvlSgEXun/m1lpFT8eIem2L0Wd81vjabz49DF3RsoHXc138n/sYDP4o1aotdV7O1zwrM6vxpte9agSFwrB48XlRAHGXmpYGAyGlbZAU+sD/wfN1pmFj/CUggcJZ1yDCtZmR7O4jzvtQ9S0bTWv3pnFF0bEZjUPaoL/e8JzoKSHhd/CauD2Y7Q5sGvCPrsKkoIfEs1tbwvP/B+zlTfUDd4LB491B+jzBo8GV1qWmuv7gkIWvg8MOzRIddUJ9E/qtFvre+mtFbal0IxkFK0s+ZqZnE2MF5RePUb479XOg4TCbPFLP22J6e6scsoRuQjwfKutOUPdfmDOl2HVdptef5rNQHRNLx6AdFBqHKrJ/xjOlA5B99HSGcTF9zf8sIhXj6JHQgZgA3C3G77puKjB6Q1YzmgQrllFNHCvAIYG9G7wB3Fdf9S+h3gUJAeGLS54pu7NV7/WHybpclFNwXoFzNFXKMfXR6+WZsji/ts6dNWY8dc6eSnXB+D4bNgNnA1xQHGnLpa/6kF+5cKPoSi5dBm2of6/vuGEZi3OOOucNuddcNEll7Xr0KkPIyB0RCImYiNR+ASTTiZW8imkmHKqqacbTTzxhUgo4UQucP7zmprGjPfPTTbdTLOFmmu+hRZbarlwkaLFWy1RsrVSZVovW658hYrVnyn8t/zVF0IKJWzhE37hiIAIRkRGRcfExhdMTEpOyShaolTpMllly5Xvzpy58uYPLxD5wVJKnwwixA8Q3w8IEjMSwLRF+NgTVsVscdp5sMlaWliQuYEjULoIgnHi0o4NWWjgumcUYgT5Wkk2LoEUBNOI6grGEbi2QcJi1fyWgIJYHLKD29iD2y0ZBAR0VKDPMHI96iyPcU/4PWPppPTw6+V4LWQEGUsN46qBT2mEz3zAFOIr8K2LMv27me+1n/8ifNkBiYUwwI8ToNABHjbgJQA8IqiFh0aCIKMFPNrAIwUWHeCZBTwyYJEDxQZUFLeh/U7owxEw4wwUF6C4AY87sHiAiido8QIzPlDNBARZBXbCwM9aoEQCJRoo8UBJAJXEAlqSQ4IltYCddODJAJalYCdrwr9vbFrjTW68yU1OzcEArOF2Bh+wRgjuGIE7pmCNHIwxg3BS4I45WKO4fkuZWu+Cwv4bTh8i2OfB2wXebljtgWovsvoqWPtAfY1I3wB1AKyDkB2C0WGwvgH1LYp6ANTR6/eWjTOiSSMCLKAk5OQCmQEsDTwbeD5g+SE3DGRBeCFEEWJ0qOo72NgR5BA2kD5HTPCKEQ0ANQjUYEhDKDMhDQM3HNIIytL4xsAa23eMKxvjy0V5+TEBUkVyZVAV4J5A9xSpe8jdC+p3U3tze2C+36fI/3wC8wuA+QWxX4jwRVFf1oFagPtfu+nOz8joP2Uy/p7yP9KHdonSTulgdKL6WGmOy/GO6wmS8D8L/I8wEVBci61O/EaDfWOQb+xklNVI0jcR2Js8MX1tMR01kvSt15TU0UwjO/Se/eoN+1WBZ+74XhAbujhxhwr+Dk9SsJuzbjMx2Tad4RqM+T2FvOOTF3Kme5FpIeeVoSRDKMQdEUsFhsJlxAsGraPzTkhYCsgWEywRssiBQaAECsJ6ER/PyxYOh4hKlaQozJuxVK+sPNFgNBidZajXbXzBJinANL0AZ30Z1UvIfjMxf3bz2SWH0fJiVfMleTcH6EW9CnCbN+4scLntctnltAtNPajPk9zF85xgrr93fhdifu17ZCN7o2YIZiOQ472caHFgr0R8B9Hfn/p86X2861vxNrzr33kP6++Le/z631t4t+1ZTdX5cRqO2e2O5nE7/rb0NlOmd4NjcCfL/R+ZrJ7Ntx2eYKZW2ll2HlCCRIyXzKv0ur53pq3DU7MOPx7LuaTdlt4/vn8osS6M/cMIshjE7WYMLb8Zf8lZzEvaQIuJF2Q0fswWW8z1kwpvyZ5dWHde8GIXQXEEsMB1AnpUq0di/K1uqrmTnaql03WyT+RKV7vW9W7U0c066+p2vfXVX6qm9r4QXlu7Vhn14rrZMlwGZMi1sdXwBKIuGEza4eRBk/tkMfg+SXomgDR2TwgOeTC1LUmfIGu1j8EN2m4Yik7pKvTkdOC3kxeRtzH6htK2uAEKiO7U9Y71nQItnfLa/gdpHyOajOu4ckNL46Z/iS6PqCzRa0HG/952PeVJnqsm5P3nGmEAs780z5IY3v9zV+LpTf7oXhiHuRlYero4+DlZDKUX4e9/BHpWkL8RQ+iS/LT47SfzRfzhv2UWsNgTfh615OzfCAJXOpufGFBvLG51R9qJftvg8NIZgm5KeWuSsKesY4CiDlEoO3XG8dz6RPusOK+LPLLZXalIBtxJJsUz+nlVXofWGSAN0yvf+jsbN0KFBliIkaBdjOIqJoTcHZ4LAdSWvk4B2tFlGn3kh3eQ/th6c8kdYCEVwRJLp0Yr2lxxbSWjTCtJm1nKrnI7NvJjoL1TPqttSQ4GW/6R/hCX8zAH019BC5ef2bWB4xzkSHo/HOUCR3k/B/kJH2G/39cf23f6Y3I332IvkruowFAYE4GHpTzBgP5/UGzPeCs90LeIYkop8/IZGUz59CoYjDZXkg3va/nTsb1HY0OotRatKyqG4VkdwZZag7yCTYtHjWYtKcy6OJf2q5PCnUrT2kU/KxX5p19of/18xpfH339vSibQO9o2QcgmmfPSBf7V5llINGbHp6z9ol+JjyDhSgykeX/HD5P80Z0lYNbmBiwfhpfb+Lu6qvm+++mMl/1l4Oi5vOtvtWhRplcu9vGNv7E1dddFi85sWwQ2Rl+P1snzcA5KqOWnZwOLwp8ztZp6SuSj9qCsidavznoQuIZcdasyOlEGtVaLEH2xsTTs/SYMgZ6KKKaUMi8fCDDl01eQxcSs6NRh3dmPTNyVKiuBJNaQokx5ssJcShTPGvmgf8qHBny0CorhJFUWOrKwZAW3qTCbKM/2+YX39EefRtOb9YEc2rTWHyGIq158NqaykqihH/5ER8M/0bMVI5OZsDtKWI+K6oQ/sdsD39Ed9fJQ7RoMHdco95uCgwVW2ERveMKeIBw/4uUD4/6lsm5WWVt3yh33evffsbDfXmcJreNZSAcczw04OrhrHCKojlQk43UndW3jjoXvRTRmwcYKCyKZFGWgLM4wUpanW6/qGmAf7jeCBgsxErQxKbZ5KnegUicrEMyqSiqMrmm1AIxqR7pTcH60K0FR1ea2QVNsdHg2yCltHt9QyYWwFebaXzxUQZ2wHUUdroE2JCY/kjDKSN7mjxClfb5knGgaFAFmrNmvSAILSP/lczmbT+ZyPFPm01ycC2GdUhE4ByvO1sGjevZU6pHuXT/JRO6ssFAlk1JIv0VoEZQBZflbUdg3B2HDhn2G/QnZtjjgiHt/UHyRKUtuySxI5nBF5b0TVl5lGOd1uXUGRIM3DTUMG437hvjxm78gTwdVBY3EYnEsCdosIUnskgRoF6M8FROSXGQRISvGpjhA+0bkVLIGwU28HAlkqb7N14g+ysWgxJ0QC2ix+oZAOD5RRPXaSBpIakkG3EkmxTKimaZKDlNEuzYtiBQDIWWYqkAMDRZiJGgXo4i6TQxyc4gKUcO44Bsg0NZjPDw8PDw8PDw885Z0EI9DGuNlxzzplug2c5duetS7HKqfSJBUilJavbioA4BEjl1+4l3JmeRC8rXkD4VDkSCa+LLY0/G+uvrBGx/pN4YeBsU2tAp7Up34lhrtb2MRSUmOjDYaZTk4cqZ9qwHt4WEAsTtdo8WtxsHBwTGHweVo23Fw5BxaeObJgrEymyjGzL823KbM0J+9Q1O3LN1m7tJNj3qD1E8klYK7lcBAPs3wGqtVkdPusSs74ISzXHh+HCucFElEEz8/oMtiqryP6J8f03ojuY5RYPhlIz1LBoy9b1lBw6MhL03oydG9Ot69I9ieq43ijVdQlLb6/NmSmiidwqjF0llO58i6Cd1JKIIWNoocVpeH4EWowvwkKNVoKZRVTHugXNKkvs8f68OTlJM5GUustbGso+IasKXCw1KHP8PnIlFQ4GHhYeFR4MMsMwn+7gqs8Ut2wfD3hxfbZuOOLBgWHrZlbkYuTB8nUPIjtkDUSp0KgJZeBKkAE424L789s3MwM/MpvdRtZeeJB6fttpX9wcS33xlNiU8knzHSPH/keBwHM7tt5ZBK97qj7cx93O8PjPyTbHVNivrIk6OQeZ7d5lSwxLooso3iKVSg139Oqmx3XPT5Ggg97HNc6Ip0OV3ORrbO91Pt/nRFo90svMQQQDwjHVLiEK5KbD7QJHS+y8kipzbLw8JLPh3OL7lMKAT/g0DccHQ5XYGBaBpCsFItMrU4KUtLVug2fxA7NqZ5aFwImgoyj44LgU6oxVYyxqjL6bIzY10UZU2FbR4pE7TZos4aY0BMJqy0LdypHU9rL3yydrp30WBlRiAhr5hoHt0koFMzJHXR52Vok1MQvCXZOhdDTUIrlFRwALQG9mziyZ5KGnjmlnbXlDQzaGDTwC5es3sNHHcyKVNsaRhcbDVaS0PWbNKggYuLi23WBR+8LKzrypF6IXLi5q9EBi+DthxFLKnLX/MB2JyjqnbHG2+jWyNpyAlCKwoFBxtG9EgTsWBJWLureKGbSmxBQ5oeaUHakA6YJUfgJi8kHwy++OFvQTHeBC24FUOGohak9ETKdEWFMeQELaiHnF4o0dW6pRHIpVehgsb2qFEnGJChS8Yw0xhyGGhoGGijEuAs52qImqi1cMZLRkuRTAh9OziYMaq4JfrIfOYLE5p8FqV+0jTx3X/1BUhpdl9yajacRcDtsPZ0xiA5Qy4sjyA8v6o8JuGncBCpaEO8EiIsMQzia3XNUX4CMBD2naVvSgcza2gy9EZyIyqYKWSiKthIPI5Up0FSQUd6QTIw+f2mKiyBdVEU3Qn7PUUyqWzBAKJpHml4WdaAK6ta4570x6tsVAVLfcKtobwG253Vdh/PJqXoIAoUKFCY4iyFAgUKUwRDitHCJmYuda7G59xBU7B9ljJFsLxcTwaS+4UCBYpLir2b6I4st3thiuAnBzVhisD+6JoX0m9KHJKeJQPIArKC2cxbbgrbLlvkJyLQuYIVK8J0TLP51IaT61rTd1tYQ1oauhF1wy89f5wGxsKY5FAYF300fOaLT8R6sRqAftI08d1/9QTkep5EH1SOUp5zHEXqjiaNNKWUmI9ppoMCU8bKQXiWOf5YXLrhf3j4CietYdaxWVviPDGWmy5funzp2t9lbDqmmSilzMsDhyNa3TnH1ZMoejZlrMzlctKgKWNlUm+EpuDT48Zco5B1fxFs72Lewuu2ahUxQUcDgjSIy+icdVsC66KoNbo7Zawope7NFlvK75KuQmB7IpcFY8dsrAhGDQVONqcxIIyZlTnjcT3okV0eFJ1lFo5KT473pFmfpCDVKembI5fmVYkcD1kaikUgNpRmZuFg9ATV0oxMoLUhuJKChRTfk2leJ8wXt73YJZYKrBjHoCeoh576KilWQP1mzQjstKd6Yjod5Li1pSjaIMyjg5YAJhp7Dpj9bKKOgzkmBnjblHqkIRupQdGa4jCkgQ7j5R2FMXUzi24Td+mmZyZPZlKSSE3cQyBAHRJi6HjTVFZTBGezZ2mzqTZTdlZzVFRWj2TIhWMBjOMRkyKMM9mvSEM08TPRwFyWLz7iKEFcL47+LJbcP5AiRYqUUv3e1CFyDngjUs/I3AYVzueYYWmENOrSZLSmyKdHgXnGTZZyTVALv5XEkSVvA19MyoSO9CJkwDLfwsgiVJaCFdbYzFelYk8D54tlizVgpXR9Lwh8Ni1Uh5yztPG6h4d9R88sX9nL5pt8+e7pd99x11+A4w1CzVS3ONZO2yqADcfILc7hfDtoe79J6UCCW9f3Lf+oXy4wMmQ9fw3km+eevrR9omEHR+ciugZy2fqe9O5/o/4208GKpreuDZMOAaG/7QSbHu0Na7Bnn/pUfbTry926cwUxamn8Nvq/i4mkzxcyTcMkoE5Ig4gmMS0S2gwYojfhltzzQuZm3OJRK9aG3KY9w4783S/yu3yuqCo16jQYILiLvD2rrmmcB9Gyldvy9uTnNJgd7q2NMeZY+Lnx5xeJJ52cmfcnd+rSLanH1vszCRoybMSojXeyJmuyBhHhVtU1tRt+iXh+pRGlgqX3LAvn4DzcCtNA+QqVqlRruHqj206ZZptvseVWK3zLeLauBuuTa3OR6GvXTlGvcAyjZANubW7uxo1kM5fefTPMco9d5A0UfkeaC1zGxpKNJQdLPzgGtNt8a4yBydu5yL5xR1et13oPrw5v4z1HqZd83+5Oz/z+rvZsMka9HWa4jTvoPxo2Di4eVXxqFWx88NqqrC2zO0awAimGLmjp2zpM5fAy6Wr+77Uewa7G0fZjYkmHuvfi3Axi72XtEor//+QVZJ21MZz00Y79p+/4LJiX8NbP4sQibv7wtT38mt38NbA0wKendOcw8lS8efgZlyZ00PeMGznzOitsT3Dcw9WO5zq+iy8FTluY/3eJA2hBT3Rj0zOz634d30nbIYWPKjy4cGDDAiFADQkUNEjoJvzOYGCitc43xau8SbyXljMVzT9xYhjY0P60Q5c+Pv965GUfgP+6v1Y7vVCo3+vBYti0XTP6/YcBN6Lr+XYGYcwr6v2H8ZnBlCZ6piSS3RuPycVMHf/L/2TnW1FFHU200UVR1H0T87rHb8CgIcNGPpDiFvM6dCjfa826DZsO2YL6UHBmZGZL4AQYb6p2fW8kns2Rg/w8DDSOtiCIDlPJfyaiVuofP80J1g/nUzj3yHlGyZ6fEE0iQloExLRJqK+fQFHExMDJJNAsNAM2i81Dn6Uuwph19pBr5mobLloJf6NlS1ZdsIaWvEmvUQQVkO0NmLNMmDRl2ox5e95Oi5CMPu9uAepX/CQahTw8/ZlpUybM2Hu4uTg4Z4ExlijRqzEpNl81JOokGq0pQ4Q48iASkhDiuz2IOpXPi7YOL4CO5mTBp0wBpX+REDg/AbMSKUmx/hoKM9a+u7jLQsIicuTKUwR23T9NORu2vPnwFSBchMjsu5fAxlfjAZjnequ/BcZCSMuXD7fB7tGT8pd3If51VQDi4qZwuoR1DRLprHbMq6sLnoK/ic8rRqvLyyh5OJDKc5jQvJAQDh0SJfDs/KA8bweRaPAn1gPNzqp6vzs+akN24YucfsZF6yw+Ff7rN47vT1rNBOxmWPUaKfx/3pvW2G0C8u83PvzOnPJE963DbvTofbDZ2QftF/NtaDA+1kgXyu7fyjTQ8cqli2DjnjXis/7CiFJD4nklmSyEn7+FCRUithxm2mV/9n8shxDrKswRAn1SInysVQbVtLhQfy0E6vBCoY4uKGlsTykcYAT3DrKSf/A3/nPvf2EFf+ev/Pve/2znWvPcTfUfdVOoU4QXCaJ+r/M+533xzm0e1yD+o7dH65FdUnuHiIv6GTnOCHDQ++HU59/bG4jqzbQTJx0lMmNIlzh9XFdo930Ftgbz/X8Pn5a6ECfPXc+TUnW9PPpTtTfDSZyEEA9zXUKtaybKVq6NNvP0V5DwC/00+AVVfvaGNTb6uaFIXOMFWPHaA3R5UD4+7AH36H791Cl3vOPGUjRtoY6+zre/2THqfqZfX+MrZPjSa7OEvLWuKosvEmJelHmz683xU/0k+IAY792dZuCNWbKhFjG3Ed4zyhgn736dHo6/WqJgzpiswXU98uBl/p8g//XQM+sIPNhDUif260U2FxD3aXxvCv/ATeqf57n9K+no7x5sZjgbGKqNwLVfISV+j+1cwXl3H9fuaXIF5Z4O/h/yZn8Ri/9NH30MYjWDL62psfz3NC9ex0Ok+/qp/1VffsrH/M2Hr/2nAv279M6fOH/vvv6Vv2AF3VlK19nbXVZu+8uqv4ckuMhsJAsKdHbm4Jj71zm+149QgCBaQkSQihJHXwJhKr97VKHHlHhcuSdUp09q0OEpD1LSPLG/oQLPWWXTth2XbrzKp5VpFnJmdjluPnHWkWdwBvRvrTRIAI1TghimBAeI+RFE7n9BeN8HYWshTcmCpMWDQ7Gq6olU+p/S6ebG/MqDPrSvYV/NvoodY0A84AoUVhgRi0iEYkZMi7KYEldFSUyKCTFORRqjUfZ/7E/4T7M/Y5v5TxHKjewF9jdiYnwzCDs906JzeD1P4MhJ98mcddEBDbfc8ea6XOAjsATg4Aku4hrXubXlEZGQUVDRCBEmgg7GU16DgIRiPdHpqlaN7/7NT5AiTYasubBhg9fWG0nLyMrJKygqKbv039pIU9fQ1PJPL65Pp6dvYGhkbGJqZi4QisQWllbWkv3adf7o2nWeI1eed9f/dwgVKVYipnS3uUFcuYQKlapUq1GrTr1GTZp3y0oaFiXZLbJcGztUgU+XuuiSa93ETfmKJVquZjaOnTh1V6+y6mpLLr0MuQ1QWG5ZLAySdajzl9t7Fq+aJ2m1ZgcItIC8EKEt16S6aGWUaDFivdfU+Oq922JxZSpUSkiqVa/RmnXjS3WL+h4mDlA9RPsjH5//1nLNS+IWF9ao5eVTv2RDDAWMzjkZhOlgihSiIcts2KcpldTSyHA00cUUa76/3fEYY5yDHOIwRzBi4ijHHN0+dtCQYSPObpVpM2aFHN78ipaFRcUlrElXEXHBRDg1DS0Su0UKJBoLC+dccoOb3PZ6O0PLerz9haEaMqKhadykaS1zFixZsWbDITtmizNHokZNHvWYJzzpTIMNNdxIo401rtC5Gq2f3r5UuUrVatVr1jY1ryF8wtl86eFiZfpia9K3aQNWF24h+Rwk2r+rn3dRIkeKGCF8uLCpy7m0gXHGG6LASqAjF5gSsA4SrirEDJ0eQRAh8ReradHMcWqREhXKrfLqIt2a4fK1NW41QZIrCh+/6SVrvlTMxaJy5qJW5JtnebSkBKKT060v5Wvo6poMekhbQgD2P9/2+D4bGK8z1nL2hNjzqutcrdjjBq6yAmyP7d+l6iTnRKsu5Nra87HeKvErvOHjPadj0sZMNGw3C/ZguzJ0aas1SiShh6NX3spYxXB7t3amfZ3dNbRL7Nvt1oaPjj5bm/XXL76YVmyyseOjuoZYI4MS5wP7616Ilza2fFHOtEluh8z07CjQ1fmfGB0X0pdx5F8zQ7LzLkuldJM61wpB/Oq6BzLM6qeZHzdfNZBhxyW+kEffGVvhF5tidiApyHqNJyo1hNncZFqsM7U/7pvBero39Xj2iaP7eMjVpKwvs0FtfKXB+njp8p7JTT9JHz383KRZWeenSV1uzdrGxfYQDuK37TGlMzrqItYaXSIetPDiDn/u6uKY3uq8LkgF+SpX6LOhi+h7598FhOdWUuxuRW0gDmJMFOFWpmjcmBjd0A2fvXghwvgG5CbPKF62FKiYjO1PkRjZbJGHiUPPfvH1BhsChoWpUyAC/W7oRcU3CSZ1ma64ub3EcrmkLRXleFaagNLtJO/6VqJLv5HkJIOs1bzkvDuyeJdUTwSlIbPzJa+Nirw+9c39KmI+0avosmv0rz1a4JUu/FQyVYnBr92wxBAymSkki41ThhYT30i0tjIgLcMQ+/efeBQwefTrldpQ4slGCLvkpbs7fU5ESTunP7Vkvk51IgVEsNuqrqIqVh5naN6r68qfB1F7Epunl8GDed4LGdAQ/8Dd4nHnUDk9k+sm6PhAopU4bLhRtycOnKCHHY/5RmhO1ziKSF+JdtkXBLs+zmS4mwDRpeMX2wAP3/4O1n7xVDdwRt9fK3GyX21vx1RIU7Lwlc3lV4VHHKIsESr7/ETe81PITPma6KH+wnKb7HBkFO7R1srp2r4dpkLi4W23f7wcq4+00Vx/LiO7K/925O/5+Dp6Ycf2OazFH/BCaQw/EFV0daS+gSek0+dd5eyGqUrWTT42MLrJZp6/btMuUcVndjUWKqtKlfOL4c1XeC7GVrwIRw81LgqNQraplhIwIDxXCza5byXMngvKfX6MmHz9WvBtxpnJvNumdVXY3GIu/5BTe+AJipHrmZ0oTdWw4gd6b6ebXS621GsADHrjccuKUoLlrhF+1SsP3lYT2tFy4l19mf2wbC6x/Uxytv5/yeAjUWd1RmDmI7yaoqzYsWO4zuCMoNjEKm+UrfjiCDYRt4rd7LuSZRi7iK9Xu8lNxDqpHQqSqOrM2ATpHh5LsfyzQwi6o/XY6rHiQdDNfi3SbaJ07Bgh8vm9jM5uhcx+3aRy+4/NBFuMVbYmEcl1vX2pxf5/GB8fxyoQ3w7FyQTZabzdDK48yaP4UE/ohlw9LGe8+oVolngjnukq+PQL3q3VOBkMynWUUDJb8d+aJMZqEgfunMgkCzZ196ibOp8ztp81WZLEObuho4kynVZgKzaenxv03iXjWTzlKoh/3yWd0yDcTBBYSUKtdkXlNcWq+SfxoPJJnTN/kIOyuWeSTthzECqxqS5CDs/xbIbpIjHdQ+r6ro1HFbqw83IiiDTffuhgll6HazubViQrMX1pipNnrSY6Kb/KY3NIWta+f3IAnlNiaUWIBYWB3QHdc2d0KwjPPkXezWau/OLzGDUDLBUUpJSxcDD3ev/Yx6UUrxK5IUCmJEzK2ieJ+inwHzWVJNYfroE7NCvk6MtY0Va/6+K+ZTSoPNSPqIKcoSAsPOVSwLtcw7igRkmJQo3YIswk8aQvV5tQVfFaNyzYWFLO5NoWU7GwoWUVyq6V+JbTctuY3Q9U1uPZrfWP4MTLju9CVoCSm2pY1Iy+keO6IYlvC2xubRL4Gd9sxjn5Pcy9MIi2VigH1Bz1YnWR0qp3ap927of/wb1h6LxX628ad0nuFD2/kJTCG+cbu6+hcICDumn/HuEheNpzvH7HB+DvXuLEmofhWc/z+pUn4CmxKp+wHlbcnx9gNkOJfb6f2CvAe/u8XkNRsFJV+YWj4ge8TbndI6fFvDc3r89QHub1gwniNkz4shNJ7gIGnxsgaGqHbWLeOfwoHiggYAAqzzu8plXnhAy+k6WqEXAf+dpACPAg4YZgP+CUkI3B/iqqvNs2hfvG1X6e5REyvvGUZgCecvkphQC/2dhfh3sB7bReDfdVbnP6NX/kwy6S1/716Y4HZrTbjF4P9Vf4kOXtaC+DhJlu0H3PigEe8bk/azEPbPDmP81YDuF91OfPMMHzWN8EQoJ6dL0vJuiw9oYghDd9903NELztzAVOBM+x7eYML3Oyz3R2W6p/U+SYP0Z4S/i4v2QfC57ynbU7RS6ZsYXX1S5nmrnJhzqcyIG4dId/fY+60m0aI+715rP+KIIahZxpEF5u0BGQYTPFmBUjfMU6YRkEBLoDJHgWu5xLJUtlw8a0m3pmRRa+sdKjWXA7utxq4TNy0xgDr0qQtsuyzQjY8dPtR4WLMTxjVM8nUVR7004jGcIhNt5cKv4+eV8Ec2kDTRqzKxkDm3w7LMiIP/UekBBfwDuml5smhl4CxbYOYWY5stI2EbThG+DXkfhj75QJQS+0M76ItxqMy1AM722h63P0KKCD0S8qOwNr7Pp/A0O4STSAFTSMupETifRlivnxwGgg8Brvp4F5/Zk3ez9xygmRp/mBj4FY4xy+4wlff+6zaVm2z7GMPX8m8CvLueXszXz5FGxbLfQ7xnjPX+R6HavoxJC8WmU9+26P7HNs4Eawpz+wiVt/xg8oW2DZX2xhY/tvC/z4pa9N1bibLU9F97rQ9ltqXgvB/ze304pj3Po/1QDjKQ6XAqE7bOL+SJ9yjv8729aaC/zTxY1RXON6kxN5pR/duTLaI9zg1zlBSnOTn5nWtmwFIFmhw2ubLHG1C0sCBKM2n/m/nSC4w+8basyyDhv/UArqtuuNTRNB+z3tAdxzg5jwwtMrn2o48owzN6l8Xm97/ZnhfqXZ/9yitaDw9vV308qxCe4KTc3GjtAWic1VfiW5I5trr2/rtODeW5N8WhrJIet/7ZvTJ/C5vSr/7pciVkGT/3Yrsrd/4f1NjRZc+2CMoxVzPqxeCzLPflQzRav99Gogclj3RYcvsnHtiyI/NYJvP3ZCzfS34weRzOYoQfb24aE+Zlv9LzrFyLO1KDJVQEDwtx1a5sjfjlzbT66m1NsxyjJSlo1u39l0xxkqjuz+gx67jO8eZ3YT7Nnj3ZBUrHJMML2nyqcKe7WTS2o9cu2k1cSBSW1ywfWDrT0QfbCvshE/OFyqpHn7wcU5wYbrh1pNoxhZsZ+yUYlDuc5UiBSqH27rQXUO31OLcXDkvMxvO1LuU4KLR89bRjN1dKCjJdPHEr5Zec27FfSKD6fHInG1OenPH9JtHmIaIWCR5eSRi0e9k9lDKufJL9qcUf2UHEvVztwgV9w40+cQ09LSA9VabteylRU6guCc1RF2zhXZVrp4ZXUoYMF5I0LtVrcN2Vpp/rYyaSPqF+9KRkE7uOSfQEPvUqVhGC1e+ur3RkTb9dtRqLV1SaPSe9skN821lThKcKdtmGX2Xs6h2Y4DZx4zXSIh2HF/azBaPfugURuZc93fNg1xPZuZB8lzn4jZyrVb8WKuT0cokRu9l0qfW5HOiTU9zUPSLW3GIHG7Z7Qhibq3y98m7B2jHdPXd9kma1925MJaP7cIDizWVukHVqVzRpp846Fd2MxfVZHnixoFx/Jfsw1xcPBCxCwM3jWEsnB9KlUOSarCzftAvEJ/t3Fs+GMbUg8nt422DbN3Qp2R8Jslz+juE7kwVzyr4SWL2k/z6umKmmDQkGvi4lhJZPJNvV/B5KOmk5ClDhthqVNrKlyluZuJqdIPK8jNq6+ZVumyg7T/6rlXQ9hU0xJtmV4beXOp/I5h6ZaewadEo77bd9oZtTLtKmHGxMvdDVaUk6NCOYOolcvHGPXKNVEtlDfYeuJ95d9oNPJI+Tc7qD6dhyjNJHPBsfDyaox6WJChSmR7xImowmhjTkbf0yZ0fERbfhDbhxYmzUyb0LPs7e9H5e3/KUmu2iZdyWlvdmTSmGOaSejToggHbIyCnNpw+WUOYThfaSZ174gea3kgbraiOSNW8uW1CX1SNE6xJvlz2iDnZq3kiZh9tIela3M7GcioPO89hci6+Z7jpe3VbU3rmOoEhcSd6h6GxtylWSZ0c8GAq5ZhK5MtI+tucNXSvJ7awnZDA2URDIHfOHYPw4ND2+yYwbdF/mavQwIfYre3qSUBTYI7OpdxSPmetzgZvOBEgYbkF1I59o0jTw0rULZyzo4Ru29ZeyA7f86fIPsZkGjp6Qk1LB3D3wU+EarMr/7Jc8VkfrG+4PHNatOcP/Cd+IL3Pg31CzN20ipfWvNPlpEv79upG/e1ARdKWb255AGFEm7YjuVASLzVY3FIvz3g6GcC0/57M1gsP8g+YFn9Qe62txSyDacUCgPD//4DKn08a9sXK5g/+sQ8rLPGJ7k9/ifrAcEVTr/gS198WKvFDqCPF19WKL8YgeKrjZ9qld/f/J1sVMPwhywjG2pOmYQFhZJcafud/YZVgD5s5FOmEY3bBGa27UivsIzaU/yBgXJP6T9TKPYmPTXyQP2YaJ0jiV2x5thrr0jSzR7Dsqh5yKOauZpngccDh7Y8axp+UBq3jGjJLyxavtEc0+usVuqsqAs2i7bUvTruSGw356ik3hvykKHoVK/EsjPvAXRd4gvDitzl9zkWP1Gf7er95UOmpd2u5wTxu4PPBrrBeeAx+sClkZNU7KOKcDC5Ph42TP5AIn8UtkNbPvMMCFixo1a2otjznEIh6+AUTQSBXJddoPhjtYaV+EK9CpNjt1HNt/IklOWp8EKV76xTP77wxY8wwvKbXiWJ1Od+q5CvZoF8LRI/9fH1sacYo8Ff3qcQvTlxgbx2wOMSef032zmvmj/2gESc+52HBeKCrlXIW+6BJ1r7438wsgcnPcWJ3jlrD2a6YR/iFzAFcAOlDTAQrAoC+RmlncOf/qpV3P38561mzY6V3cZ60+Ic0nu2fiJq38YGch9Y/Z3f8FSHYJ8tNX/JNE9mGZ2zNaHpfGVKc+hClqNYenGij6q3LWppLrUNIaTaRgdprnOJbSS6C0wks2GrC7Z/tyTXewu14VrfQIXlfL4XFQZ7DUL+oL7o1x4cpi0vFkpMy4PXJzmKOyPBTfmR8fqvnFYPJiw5mn9nJMcKqvVx7qWZmLAUVko22L0aM3Btz9SiMzTZAhNiI2YfzhkOsgYrTeIgVUaNeOXLfAFGykOtsDaT9wnrUSHYjsnbkzQdmuGA0cJkaRLTbPMpSliCL2Z1SMlkzIyuCbc44DdSFUlTrYLXZ3ji+nOmAUk9Ge20Y7gfXwzXApC2kW5bfLtp/ur+VJvGdfdI3LXy9uyE1Da7V5Q3PPDNc5jTziiaycCj7zyj7YmE+kjqHT0+VHiiT22QtbrPMybldKnLk8zQ2ewZ0+Yq3ufY0+ZFw/Y12vEV/Y13iEL4K6KoviLK2Qlb4q8MrAFAT+eYpPJ1RR8eeEShTf9Foko8+So9fj/Co7fFNT2758AcCZ1OuEQdrpBvZlpkYrIZRUoS1QAdWPb4tkT+vSC+dvsoeJCjvU5AlGKg3JbsoHxNlkni1hYH+OoT4a3yjdcqjxQBbLz+Xgdy2idev88XB8i7QR6/wPbuTfdhAA3k73Sv7uaH3PUevw2/WB699T0BXegtgHXj/XSdSRxSaCYCKWDt5mLYc96mWRwAeM5jZk0IkKGc7hQHASOiz5IzXxEWyrKaF8mHepHpnEsAGodgOkYs3MiPbS68ZUH78AYyg4awUGj9qfni+Zkt9AXesg+Lad4ff8s2rps9CC/CASxrDABd2M9SJzzWnXjAiGHqBg2ELQ/+9pfKe33pU0eTU2EO55oPt0ZpQmOjYBgnWRxaJ4nNrJ9M30L9/3Nsa+0xbsKWfce06+JmGalGTzRWKmnHnWhS6aaXLwePJcFcjwnPC8vOedyaSUndChzHGTgX1+uNFRWvpcEm2yjUYuHdscxGZx+14pq7Y8KZRi40ZsWlBg0ZM+/EnYHfKLdjNjlDvk7+L2tlSL0P7dG2DqmMKqshNapaal6tqQ11BOJDz/VXLdNy/a+u0fVmh4lorBbSDC2tnUG2sMoGbDgUDMmhRGiCfJN8z039N1588K8QvQ/arx40qJ/4TX4VL5+YBcb6xClAIUpTmdr5JGkZ8TEchFK/14hvpOUIzAN6D8DeYaw/6Go3u0+TF1oofwqEi7Mai540TY0ta3kb2tF0Rzu99HJDVx+2Igm/KQakLHm3lEqz9JYagO5eHVQpNaD+ap32nFqNj4G+6O/6j5HVuo5tGR6Cu/D4ExBcJ+KR3v+9t9FVPoGGvC4A6hhwEc5BC5yGk3Dk2x7z5HmDt84NaUQCgH4KwL2zzqbPZz7Gm/lh/kpmbWZN5u/MiswPgJ/7/2gAmR7AW5YmawDSHwMfwTvxr6VXAdL55BZcImf47w+TAf7a/AVfTB9uPf67d555PP72nx4//fY/vfHPHxazFOD8VyaD+YNJYVv/P8Ne/AuMwfSZjn7If3TnddrQn42KzEge0c+p1h+VEuKCfCw4fkdlEDS1vwepXsf8PCMZBgL+L+CfJf4Lz7nDbw13v/bJgLOX7nxcPg9/Nj8yt5vXHvVHjfyTz+nktjHm3vBM2IUXwkvhreSTFfiE+eedJ/YrH4Ndh90w2u047FHqtmNwuMLtOKJiBAqBlkKejuF+Fezpl40HR0FC1ahIzqdyGx4cw3GoUaO2S3E2zsG5OO+tsSttUP/S0vf0fR2bNPbpmibZXn7a+9RoNtUnazTWOH+bqoVp9wY6UIq3iLnkmJ+BNgvVLFZnqRFjubQaBAGnYJTO2MOp38H+BhhZ1FDyHbXjDU3SKOakOCw2t+KNaa72JKIIOUX6wz+hlUKcBRUgEqGMnGJp06Uy2+2E3QEUK6RXSJ9wY8KMm81EJE97WfjIxvHR7EJ0xCB2rIMgkEa9GRvLJ9MJGrE9LyRVcobP5Gt5Nbmi81POQzCRbUopp7KvzTyQUdo4osq8feOs0+ZArPvONI3m9runyNeCh6iBsFaq0zKXwLn8rZWzpQr9oew/Zs/3J9x38vuUshxorjkTmizxB4BjNn5K5o1W7czula3qxFDuBcGZsIUv2fZSn1jV4pX4Nt8sxK0bszZrd93L2nKCZ1DHaJZAgUl/y7e9yYpRIZt8X/y/VUp5pdxlUYAZUE4fv3/9llWOau1PPeu6x7WZMGczjQzELv1Cgy7RhTY9/cI0W0yaxvP/yYJVIaF+k2uNQgm1WvWZdMO53Mkq5Rp0GTS/xPgOUUmKRcdGe5f2dHd1drS3tXrcrp7urk6nw26zWsymDqNBr2tva21pbmps0NbX1dZUV1VWlJeVlhRr1CplYYFCLpNKxCIo0Be9OjleWaamztxan6xqFL+oG9SnuKKEybA5zw+HHSFNxI6m0mkba71h+6iBpZMejSu9nqt9pikKIpj6m41mPJrUQh29goTVN79RjzJ3i+mUB2VVwC6n1o/d3Yo4e7qQsxfRhkyAI1pWCZT45pNJeHxinX85i8POM645tizrvLbk4GxiHlrdWLtRST1QpjxeULQU/4IzorZa4MPG9zPngQSbVbMMm/i7iw6hZ14SwQgvxqITwCtsbiemwuFV4+EmTt7nIQhS8svzu24kgmYK5aujKZW/j9eSxwvvxq9OyK+ElEPTp8WKe5h3U4KAwFXCTxb7y/1Tz9HmTU5pbufxHZL5ZUbIuo7dJhFGyB9vJBGF16ERdpNgDo84v6McXeq4xPiKWyAETGUSJbBKx9MTnY7ScfrTyuK8hPfmVxz4H6GoEH5rcQRdrHOnlUns/uXlTBYSTBdOB1mno9UegvVctNey032fK7/2ZC2/tXCO83YiapvJP7hudBIdcuzSnPIgcTCsBW/4milDnIeimdBVg7F+CX27DAbWyybhtWGfpr+hTzo47DhdPYjBqYPBKs/VfdYBJYMld8PDAlgWM3jmlzeE+lqmOBpUdXXXELARvyeYmUhJQpbycSFPSKbt67BuognNwjOeFIqEYXYOOBPi593hDr3zQq/zgghpLFuz8RF+eWrenieHpkkMTWv9O+V1gzoJDrm2ah9NUvrMjZ+LxfAS0tKmxl6Myzg6oW0NW81/aGi3PbXu8LB/mmNCNNnxyMuwo76TI6287ROwEOA4H1Fd0zfPa9H2Bs/hTK7hU83IL6OhRW/07BLeSvu6Ubas6NsPRUQseXhBlpt1dfYFFXhxB77YM3ujW2w2GV044mFTeHBxQfdxZxWFVEdxkspTsbTyDhLBkSBxLStMxXmCEs6HdPzBzSgKFM2DaL8lu7613XQygOUOkigS19+yuGfwwLvyYRHdgtRXfJ+3WNPpsZ7Re7FbxleRNcNOPJxhK8nCWA+OL9M62DiKu5Jyfzxy/PAFGscTYkHSlSZ0Ywp+McHDjobWGEcXaRMdC16y/rgvxXEtPS5EPV5m4XlZMLAnJCm/ZnuDLNcSeFDvCVmGgOMCmKeFtS8xK93ENoKOMHGwwbTjieO4hg+ytAdgEym+AgOC6qbD1juFBBhBa7VMsNg+3lJDJFwMDlqudOrYdk/P3MLFqkgIlzcuXrJIFXXJzGE0N9CAHaDQPwBZKVkJsuLmH0Q8dOnioWQQTT92Ffe4eEUCHBMcKfcsZlo/tQo0624AOp7HY3XtsZEiqLJwEEpYiqxh5EeOTkQBs9pBvcBkGI8Y65OETanqNtSgZzZuyyrmwkcO+Bsqs7YrKXFwgRZLeqaCp62HSBMLqDChg3BVZFkv5dsPWg9WL6bIaZPJw85J+UaBEefQtOcaAWrk6+kIOgvzm3Z6gEK1STTeoP1deV6NPasuFDP2GXY1UcCV8rADGo5oCoVlJS89knyO3WR3/BVDcBC8C0TLhq/e5GuL8LkUVtkeGodDOhYdPu70er/8l4D6w8AAA7JedrnQeVPHPMwsqBj6ZVwpPFBkQVd16uBfCUoX7k5YFU+EtseZL6Km6vysXOdlOqzey8vHAthqHrpBE7aKwK721WtpxQnZn7fn3YVb/mtzenEHpYrY1/6YtiBaV5zWGLWINZlhBnaAbvqI4V4oRw0x5zUbWyEMRPcgHi38XeOUhuC9vyPUDAkw+HOk5bVWTBcvvLPQxitf82uo5R4soYIIi/7gj+BIs57L4CSaSyeCxmRZOXWaKs/7263F1bHMeIYCpqU8jFRGSqkBwLAWDZURIw5MwMYL201NmGwXkRcFpx56ahvwFW9WwaZHSYY5wVs+YGHyJkzIW+ayN2/foy5YrKDkPpTVeAp7D3V78NM2JjSjQ1pOQz+QxVuAVHwp+aBwVu/y+OhSePLdOBopvNGjmXeOycLB+UHm0LPQqDOa/LULfF6ylfFPuAMtP/8fbu7I0m95fydQ00PvwIU8WwfkFNtoZLdG7/SOJubyM4BU+B8YB4sbbWqN3kASDu+Zx+7LWvuzZc3XHwW7ryV6EcG5DNGVcCn6SJS8wRdcQUt02HiW37Tod0Q0mn4tvNWsFq6ofFhIzTcv9F1FkxBYxNF5zwOv5EEGQxW++WwvXTZXT9WVmxv9mixdxQ/dUrHHWc7LMJFCfMU4rYUGFO84o7J/Bo7ff+kTYU6E+nbqbv91ZZZYO4Ehwx3+zsKwLMMl/t664gxPX4p9z7dNwsH3vRaabfjeKXcjOyxhyNbCFyS36xa0bMGpfMfaj6B9eTUP5ye2Mnd9cV7DKa1zLr/zbXC3CM/vcIL9WJFhKyC0QjxJ7nK9oOfYcsAaLq9JFbhlGNSE1ox8uvoWSCCPuebjKBwJh94e8DbQANfVR3wFrurbCjIw7wf/2+eABhD+1/1sJxCl9mISsTGatlOI01fsNBJ0vphBcowRdhYpOl7MIT3Ghy0LGVq5P4/yR1YBpQRVHlGKanhEGWrgEeWohUdUoA4eEUNieEQl0oKHVSEJrbA68/WpBo2c+0ROTtRlNKbr6cgFAN/vajkTmp65puH7uHAFODcfCAysYTPPzXhlODx9DUhne075eo0LggDFZDw+Kq7MiZPhEMfI7SQDuBM7/1py3UBKcVmJLVDcuVRJFGcIneu5s3nyKFEODTPkxGC5yXQ5SUxZoRgoXUUKCzSICsOTD2OsEJ9W2RqZrTcVAiUZWb95HRbeToGO5D4zwD5SEvHCMC1y5CZEQPsRc+UZ8iCifFOwKmUSdoaJIUgEC0CVOHeiDSLivFOjoCMbHo0TKjnXDJ+whHz5S8UDbc9lO/gkWClty9rw0x6pFMikIjkSpKOoKkCKTAojvIIUTt3HW7ZisnrMRnLuNZeNUEwcwA1DtQ9RKc6yrEpVtRZPnmW9nibiDC/RBV7ES5OxEYqF43TtJ1HM//yEw6yi1Iq/ydyDOtjbYD1oWndG87vYTfLfnQRpR6J/4favE4HbX00SM0Hzxrm3WqVw4I0r8adCaHBzGfX/2b30OmeDq3fdtazdJxFAtNnfi+588vsIdOAGpoOIKk1UigRQbaQvQIgUWrb4xalimMumTDLsPHfDcMoFD8uWLUTekxe9ZJPjN/BVqpt0JLZmB9evsMAOjM4hIfwceHiydrW4kJK58x5o7c3nlECCyjrNfLSEpDoEyNew3LwenLWWQJ5UoqvD28B0fhq/7px8u5chlF28nLuS5+uiaGxtJbEmMWxkaisisVSVnAEKRMjdAZ75zguIbmVdL0Eh1U0VOOZ9lh/uomw7zQ8ZeftE32PbJgCXjAgUzxioGClKrfG8lnIzR+KgjMAqixjztrwERDx8HKagWmViblkUVQ/pWJLxgwcxNOrQwR0ZJFYTwC6TnHMmhrgoEKru8JKUxlKs+Lg5XDSMc+GyiMvi0HVyODTUw0H45+SwnVLheGpi4BvFI1Y4I0rVyRCpgYcq/xx3VoWhE8tTCcWNH6H4758yfVHE+XoJytviFipADSnd/munX8sYBz9M2/lytg6HPBwYM2MBJmC5ucGMecuqBIBDMoAHassHQN8LuThHhAYPg71SBjCHIZnjzj1sAMx2HAsBxzfhPdZ4WZmluBInQECELq0paQ6mxjhNmZ7vgdp2W69/fkKe+uyb0lOx2E6xruW+iFkzP/ctt/rp1GBZswYJuFmryjiRFL8SeR5v5ktNBFfCeddT/QWZbEKgwTMm8UIYCbdo/wk7BwRkE+1ipwyU8zHIAXVZT6oUo0V4j30gQQWIe5jtnP+wStK3udcbk6Vplc3wjTOXLSkZHO90q3P1vV8JKPb8LqyANEi1qSXsyctihxYB+lPkdFt1CxW5uptiyYAWva0Gu26VvRJS/2H0owh04BF1KsyE1z2VcfwNJR4GKl7mSx4Bue/9D/WVm3JJVm4yiuTl3gd99LX7IXaZSK6AXbiSWgM/avdnFgsgG7IxGXNm7c5M5GxeVr4rvuLJG/Yv4fDhUCQXo1HR8i6atNfhUXvSOuSafdKCXPS8GQt2iyeozwEqqEk4gAL7m5Uyen/yHh4b5to58Kw0r5qGEs24XDCUdW+p/XEz3ylr9VePTCHtob2GNqoAW76jf6pTqOBG5vM916sBHLESdPJ5tLCHRe53M/k5KB6AZ8rDThEb5PNZ4Zl4FKK+4nU/YpHZYZf2vyGvV9Ma6kGT6yPZb4hm3YLjMqDKCJ6J7KkOxC0IdaGC1CZo2diQlCoUxFJn5zhVboFjIYwjqK1xbqjSx6uCTXZN9fr2QJ2rKD1je16rkppAkJpTrKygqAdYiNByfWbN8a9lqhcD9dcYzVJuTYC25cyHljrQI7+gENX3rP8MiEXu/s/hSLaG1p6q9z6ZKRv1aUqvmfgT0lTcp7ThdvE8zxRziAVrpd4xnNFeEzRpwdFk7f+awnGpof42mZQ6i70F4msRujiu52tQPRvpUwkHsB63T/HdluRN7MKGD1PIaX+3rJbyhVJ53P7fnqzXxpBkr99fhCQn9wK2oU7xKA90mTE2uaKP6ILjNlSR1bKhadIRwNtIO9SF+uH+bZEsk0DIeJNEOpOxe7gu1+cVQoyiksMQUK4NFuaW1EvBe5z/evjK6W6IIqBnHvGYHa5wYGfpklh0iqPVWLUys9EmVBFVio8x3lG+WZ42+sh35PIgulnFwjOR3pFnvQR8lprbqmoPYoLoxpqIaSaCQCfS5L/m2CNZ6DlcLhQU5fCjucSEvJ9H2XE7RukSJMNlLo3zeLX7WSMpteys+pk1mBHk1E07z3g2p8WMssV4x84jP7dbMlpGyxTtB2YYZC9nger7JTAyIbK1bcp7pk5X8Ty5RYOm1tcklKJO2jcT6FbSSHFDrONyebuBb27ZCFJyIxQvcOU8bowBbGnbV8uhuPobaVOmixBIjFyTEYb3tkgvdSXl/8SOMS7jAJNv34YOUkXLjCeKBw/Ykiwn+bJANb6WsHzt4iQflXpwq0d+kX9sQJWmJpGDjCXpB/rIruG8gjo5cDxWOjULPReE21GiCtf7nV8r8gLXb6rRkuoVSS0r3w+P10u7zPm4slvNJqcYkrh5JB4c9OH89yxrL03BcHddlhixgacC2OJgr5k3H4Pl/T+mrDgmoJaWvAGfPRX/mpmQLjaKibgUf5eVbbRQd7OOzQTfjwepG/pXoWc0CmljpCTjLkwKo8SMObs4kraw8B1pQEvar8++DIvwqrRsy0UtdJv3kebjLtAvr94V9HAk8i21niSusZGDDFetHVrKvqPj18HRtIrxtHMGHeql/qqwW9C7+YLJ10WLAm3nQukdpQz2yRwMiyvmmP1qYl2i8oRP5bJH/lWUrxrpzQXNgy6Na5+ewlykGieEz9nzhQAbGhBhgVovT9XK7Xjqh2gz2B/2387dovMhcgVQhcYsR9L54FPqLZpvHP+O8mZMwyA1cGG+X0uKX+IVLDYugTwq25aJgyg8ECm/5oQrrFDti2+1IQa5T4xEwmhr/8QhM4HWYonKK4LNxtK3+IEXPqoTOdAjrJznupJIcBkCG46YrmLMtnhcsi0gpSVtlDaZ6Etb2FBCPsibHfWg8pNF/uEH+EdanfCbsHA12RL5AkfBQ26BZyP0uo2ajYyz/W9ReFA8OMAqAphszQ3ljs1+Wlm5y7UpTXegga7lu9B5yqdMXQZnRKDidKDuDxeOJWdgOX/npelXdCGZ1rimjNYoJ6grNkGO8XHucPm0MQ6tJT0kGcHEQgBU62G1Yp1xTguAz/OBcrR5MFTWXlNLFgqnwtRsT9DTzpV5maHB6XtQTUEBicOxcEbjiOJq4K9LJCtSnJqpXkdhKhzPPDT/pNUc1EYALdOFhyT9ALInN61n/65Il0o7bthSLujbpZPtbAk8PcIbGcy25abN7FC7LWb9rq3PwUFlCXlYYXwgANPVHUGt9lCLeqhX8LLkRBwsuMbdFULG/uKHYbiw5WuvW3xkq7bZmEe2aD3uQlf2GNQNSKLnlYXswA6l0bkJVL1wtkPlfe+8AaYEYctKzVCk5gXdg25UrRDXzQKruDghX5a11Fy6V9eFtisc2Si7/WFNv8yMJQefqGxTIQ3XZdEZYfZmT/bZ6+Vo0DSPf+s5qY+ON0Ll+bqgzXW2IfffA0+oyZfGVWxx/ddw/7RBW5UDZIJ7qExbKmxBZq3pIsvWezMmkGNMuQTY3636nph8G2vYEmYLZKuYNv4XwcamP+PqMiPABC6wphcJC2vLnNTMWiSevl1eAnI9Wc0mGmj7LEYQUbpSFkTVYLrsfE8jl/berUuui4rB8uEdCWmr96WzIvGoQNauNuK2Ts4Qm+zWeuRrd1QwTlESr1aH3gJhmfIr6WPf+zYdlpUYHbIyUMtdj8ArftI4ZgZUKJZty+1OtgxdmzDyTi4E4d9OiXpq+yA0XE2u1042TaCmJRME2sFKvSjHrcbICm5pdfIZ/DRtv3lMnSr7YKDsAe+Rl04npycov4s0d9Sf0b3n+t4eNG/euXj+/aRhgLrpyMWLoPIqRIZJjg4xbGRS2nD3lEL/dimRIVIBax6TMG+jrLJui94wf4XPediteiMpdZ+LSooIr+2GP3CXG1/zgAjtEXFp334W2TJMlq/P1Sz2at/y3Ef5GVfaPWYwmWMsZlZ+fUuTVoLbT/IMjGWnq9ySPcjGd96auPXLDj71dCpOsEw2E/rPfB6t/yzIYFZK0oirneeUL+a8/+DGW1bJcXoB5ViiuDH2n753M2Smuh0pFrcuHjNF7qFjbwgP3KMxyvQCZGj2TOMyValoyIqfjU7C2AphlpGeGwrgcVAmbJLWPiXFoP3xoySzGTjIRN2nqtGwgZkC4AQs3Ugurw034EeD9kM+LozlmDFtHjrkKvponU6w7ik96to2+TmAieYWI30WpYCQH9sz53kk1IybWWwi2YUq2k+00Gar55qQLxl1PIKtixylsMyqWJC0vaw3jVLLQP/uaW/9PH3DviO7suMK6Q9JOkEOkWrWuFW22SXczQrxucAjzfAOL5AQnTqjYm7rzRvyk+uHEZTmMVguDh2s5JYAjlno1Si2qh+wlzttDUIT8dwYKj9NLyyEh3BjMurPKHNtMN6t65IE1kmKNVf9XXVihkaaD8Jn1vPHtDGOam2Wc7REldi2d9ohddEHSiparl/2GTpI//Et/qG34oDK4ezIIbfqULrgCQs1piyF9ytmYYs5vwXFKiZ8TkBlVqGQoSscBGNLYb2el548ctvHFCibCge1oGUmhP8sYx2Oi8ExftrpypTs/OYQ75Y5Qodf+j5rcXa+Ocm/dYNPb7dsuMKlqMyB5kj8GG7Af4IZl5dtXoNqSd6tPDkGhBNkhd1JFCnLxWEClnjQ35PqkkUumgLhlnLtmocEey3wjGz0u0L6yqMRSNQJIqpQP2IevXUXvLK2TCLX3pxw+5YgPKaCmFg0U1rFuDaP2KGFOieoWUmTrVjh4NnGXH/czPOG8u+2kvjW00tKfQexmohVZQQZbW7VxK4Zt5nacTynlkNWpN9wii6AA4/8bHaiLZqxJrdaLbAJDoRL0HzZ9OKbgCoRuVqOtle4fURth2PLzzPg7Yity2RSFz4MDDGNsXpG3fnuxbFHvh2uSbd4QjxQo+6lulQDh7lhx7wR5w8kik8lBY6VMcQMeau+vfR1tsV6NdhK74106dVwUHx8FLnehKSwg14PYDcZqH2DFvjsYzvS3CYUN7aqzXJGyyHZ34tx+PQ2Zz/MXYK8eOrg6GWIDzpGPLyt2xBVtGnfdCvChgr01HHh+Dkjk+otoGiPx7Al+FYd3W70C3YU8CCusuVfphYDuI4ku9/zlC0nZ+kSCQ6Wk4Rr1xXLdaM/X3mIiQzBhEaRZ9VWs+SuwmlwtVHyyG8gsXcARW9TbX0ghnV6bIN+Nfk+Pa9CXsdK5i94Kwa0UUKsPZErZOFPifBFinMqbguwzdyV/kvv4d5DvXVRHY16D/+VaZG/WydL32uxwe8P4AzJEK36/kju5A/6yoqfN7XRP2sIzQsWb2lHqd/uBmM7Fnt5Dfcwv90IAehv7njR15zBc1Qfh8Qn30LS2N38vhCL/mBnZ3GArHmEPkBO6C9ucBKfsTlCKYzQYrm4H2BUGEM4Abdmq3YkFysb6q6VbgceX4ddto9hwwKOWbBKMYzSGnu8UDQFLoyQcpfjkLKa/Gu5sW25o5lUXXqQp83DlLt+HvnTBQkmREmNvdbHnUByp9KMSPqC0JB4yrF2eT/Y5B0h/JH4CPdKlzf0EfF211QSpG+1alnYerKx33hZcoN+1YNbdSKXA6UWOhU+7ZlR+Ps3I0/kjDCtTvQJqypDKdadQ1HgBheF6SpqE+y/ZTuhnvGydxGtKL+KGYMSjKwW/diur3BwN6MFsX1B7bJJanbqLszzkgMkthSEnHIi4PlQXbUAjOnMWn1UxAUzpFCE1MAauF4909y2ONbb11mSXhacgVIIoYrU/bQDUuDJt6QCPYqVh/Tku9lpmBe0PL1ptitPFLysdy9hLIiPBecz0laeJWeHAAa+O5/h5OEJWhflGMiHJB4zQH2kqSOQXh9ljR/BaXpJhwsouPFLfMnjmKa2+7TjC4rjezDGwr6KUvHqlJWJHrN/2chk5V71ADUqmYIoqvUuq6F+fJnFlntEcYicrxQPJtE7+oW58t0SZwLIdajaC41+WnaPldHgUoSiPY43o8fg3wgX5PaTAwGUsAbxOCPYqzMdS7YcDNcf0lhhWp/QFHUqoZvzBLJWpQFFj/xv+AmuUrlG2uXrDVdMFa9WPhY+3KLeKhSDy9iouz0qcwOx3ATO1N+EHcGuFGuYsKsEEkUi5iQRawrUlKHWZKhg7CmwS5hQcn++jsybm7dIb1yYLRBbNGeo7BREOCAsUVL12bN4dEM+ouRyy2b5Pgg95HUaxYfN+JlGOiYK3lzUtNmMbis1DwhzOMhXNayW5uf5Iq09tndhiZcpd0R7KcPmcNjgyl5tFeMT2oSOJsQsxEpGc5+aCJkl7ZhjTQtTubnPj3VxrjvHv3191+iuAt51mWmz2NA0I/O7devUdMjnRou9DZhjl7SQvNwdewFqjp2/v+MAPU7sfuvCx9k5f1YqUa8dJsunOoHeFhfcKm29AkTQw35wJNxukH/jPrhUR+42FxhBQ2deTSTcLnU9Go631lYlKeE6XVF417PVSgjcIp5lxuwMZ78E8kLbB3rqvbh84Wz95NBivXy72LG2T4mko6nusOnSjQkqfZnNTBNh+gqTmdTfVcZiNpeks4Xi+YysdTMizlA330OB/kvskDiPaOzK/+rCQB4RONFDAYiSEc1R/A8NeGw03FOpIIDsPKi0gwMj6EeF1oRLUsPhVGhpzF9NfRD6NMIJciOw4RQYObQLXYY0bXYAeYy+aCfBPsGAYQ//NHMD1hok6afZ4rizhKktOADsPv5dJhWYL14E6ik0zE+3UgoOasD1ArlazR76Pl0I3Gv48X7P3cI0ZCftRO0uCRoAuvyWNNbsulx0yy9YNU74vue89Zc4lLz+UtRIUEOGMzIsuF6cZ3XIJUw2mgrRHSFhycazJxUfBYlStLxboJCCBNL/VekvSQaUtJaAVb1XIrj4ajRG3fd4lAlDrHIIv6TGt2rGMQ6ufLE+3oLV/gGC6qMrUCIvijuU/pEblwqo3MeRr19dJEVQhWBUOreVev9ii9g6N0nZqd3TMUubC4b4JrX8GYBhLtGHIyw63wZJ5DaDXVraKjV+NZUoZpPrHohh9ljXThqMOpgCQkK0ixdwIQ8oLxtlU15H0N1x3AAYluQvQbLOrZfXwzMuQYRplvXmcx6Gp5vee+JSOoaFu+uAgQEgHpxR5ZaP1iDxVA+MGmedVeGOntAd1TzPT4aUdfkQCQ/DGs8D4vtNsAbWcE4726IsKK9WV1PJZftjMpdGDWJJyg4bdqfT4CFWKSxhoZPUzOlUXstlx5omeG7XtdGdFFgOdGKadPH4XjhjKw7vLVzt6jW1WA0XFtZTILUrupJSMk9b3NK2FNFBIRnehtADPkgIyxREpd8yiFXAQbNKiT2bn+qp7lgtbUEziiRw8qAom1UTel8z7X1W1lbuepKZUyWJsuUCDe3U2jKTWHwHdncaBGGF+VLxW8sOPpmHKBliqIlcnooTQ4CTyiLhWmvES6D9Hb4G+wQNoW+mW1B4plbDZinrjLJBeKN8OelGZoKGPg9LCmEMUHtiKqVcH6TWuFqt7CJ79M+Tj1ahik4Qga4mc496Hf2Z9LCVusan1lA34bkEqr0oYmv38xZWaNUWCevVKvc1KkhRUG6mW+K/i48DdmkUwUJP6MfaERDfqAeFnxCxenghuCom3DtgaphsRucG7ZJ0zRgD0MDl2rhfUsvZy2Wk96zJX7U7JfP/uVoCKmNRT0tUbqlWBBIPGVCZe5ir7skoc6B0m58rWFAW5v55pUUmxKfNYUhZiFfjPnObD8E8E/7Xj6VtPpgEjdTLQD3/ZZVgWKdJA+1b8kr4n5ZnkCYoaf0TahaCK9vxNawC61iz4RStZA5U9dPV8v4X7OdrYsKHLBd+nmPJq9Mo01arDBWAWzWldL+lHrfbXe7Eg/pDBfP+UoCjTXgYBR1kOaKQmq3O5q3kwkeLokPJY1GL+VKeqSe5RxWM4n9TV7+mm76toXT5NqNcbZ27qGKZs6KTZtlK95/6O65Z2JQnpMSIzYzf96F02KDms9RgsF4iQwsRe5ezg/8XRvp48Oi8WfclDxpw7nh69i4HnsrzX4NvnXl8/NGZUnDVSEbOOX+H7z2eikfZitW8qhTohv1n682vrQr3Rhyxzdsc5NjQ+vW1plx+8o5xllYxgfE0PJvq52qM+qNSZZP52EVa+db+4hr61EyUwRZiTZjF84nDXH3Vojx07Gb2ccEePGxLN0JmQXofjY7+nHulP0HGgEddlHnky+rBWarQenROnK0fqQ4aow1ZOzeIX5Uzd53uSyinJahovjZ+scq29mtXX6KmrhHHayxK1fL1C66TwDHRuvJ6MtM8gTK0ZQtuO0rryWSrR2FOj3W44e7Ra9ADQfknWNPm21tkvG9J/+XV2UFyfQkN7VeewBR/lTJQW7fEmGx1XNmiodyCm6uwVQpRsztTg8rC+pgBROiwNw+GRzW3K7jGc+D+alh0Bj0bTABcwkqV6g0aWpuInXrF00eYF+a5Qsw/Rc1i4Fuz3AEAotb1dIHjqlurUyu6PVD5PU2wg2vKWJebM9RoBAzG0Iasinv6gW7Jec24+FMtNu4jV602fJvOD3myB6QGdQ8hGitHhsBbuR+7gHo2/o9reORXXMs4tdAI38sJJM/2/Bd9ALAcMST1J8FAVpuWouduUs1qGj0dhk4JglKrkawuyMr+xrsl9zPoicFW0KABSB6ONCYRdVERusSkCvAFBWiZtkxdHbp2n2bj6Gl354OGFc5nDDYchmZtqLTn/IATqebCqHNAePC6uNE4G7zk1Iy9II2RKG5StmZ7gh+oi9adTe13n4n9LesZqjrancojfB9TZhEfwWArqfgSHAdf16ww5vpC96uES50dNsMX/ImSKWRPCe12r6kktoPBabiC5TVTuKWkt4ZzKRrxUIAhqB1Qd801FLVoSKQKawYvzUcnKNE5TBxHSsLFZzOiC2JymHjgEwmzEwWsGqjMdfgibgBlQE1g11MpJuDZmELEADLmdou/HISwV7Zy6w8uUPrm0Dtr3mJLFfSqwmKaMjEvPxEZIaa0psPByIcJm0l5jmlen6yycn6VodLHi1mjGjy2I7/qVtbNHAJXnCx1E6McraL64gVFS1A233C9MWibOvrJsrFlJPq36B2hL5VaDroEhWOjDYHnXoJKYOoAJK47HsDZfSulfu7E1j34YEjDrq6JpheomBMOJ1Cxslf1K9vBajzEbTtmI5EcJ9Hsvj3gQ7/SytS7Ojc0v0XFrzorvu1N7ZI82DwJ2koKj/tOnwNHFUTPRKlsyoATwrCOqGg8b86hSbAPPODhmp19ZuvlHtq2XGOgU8K9ET7/Qya/TfLvCJ5bfVLUZslp4DVgMySJoaFTKEh0DGovc8VeuziIIeh71Ppg6yhb9H7Fw/2Z3+pGNjFlxRMwjUA5A7sx9HQP+lPdqs3MUk4Qqs4vnIZdN/bDitwiEaUYbulVEkaOEZSCbjVVP0LORCmzhKVZRKwcL1Blb4GjRo6AyH3jteqhDaezi7Njo4Cx575LnqR+GKJMlSDtWqFa65NlBvKMrvKPrtdfGz1d2LblQOf3kzoaWtyB61CPKsSApJXDMucUky1qainVyeCpFQcm9f0PywcHzgiMD4aT45vL9DcEdQtQtX7sYFaJMb8/SyFIuCs2yFLxdWKbQjyrNSowFjerhNND49VVVcntoxRRuND5V4BXReiLT2MbA/ZsDPkyD6x2jCByB150IBxEWHi3kxoxVtuYp5IvCjRe+p66lLKt7bydU1M+j2mUKWZPyA2nGDkmO6UQ04wUcnK7yUKwg1Smx4iEtdhMxLdppwmn1WdEtgXcGcXXJMgITd6EJ6OlGEE+qJvIBXZl1TaQBJQBGbvrTZEEsMREXrDKgih6RUItH8bs4BVT9yp7N3LVzu36qmXqGUVZnBkC/OWog2HkO21B9wmng6Y8XAEw+lN/T+ORz8V+/zVx934gDzPVU75JUyz9QtG1d6BmCN0eoaakR8h3q4t5TjerlOti8Wrn/lWmvvFM4vI8lxhv6No67j4Tuj3PhB139zuIKFMFRyo1MBhqipxhqmCePLObff0wa8Abzhbo5/JOyB9JETFqcSORgpbhBHyQi8gFdmdpGyj8DENsMRunR5azOEu2rmtUgSGhkY1hdhWUUO9r+zcpas92o2oDf03Nt7z9C/qXx7d8LzyYAptfP+0+h/BrqsJnLeyDtS2xI7Ib3Zibr2zEbCT4rf84czbhaf0r15OJkkYHsf7OOc/lrtXsV9Km1KvjJlCSbk9uM4vzkFyErscKMY0iogjZGsbmgnDnkjTK5QNOlXe3SnjTwZPMWrr+XRTknV0a1ZJIaomCtyQRW2CGjr9b8GP2xeNRgVLYguHwL/ctosXDVseHWRFcqjTZl9OFGRwVaNqCZR0J/Vk4u7bnFm6sd4G8OVl/Hvsfq5XWI1H/KOYmll3B+8oPwE4HaXGIzyOEwL+YM/q2p2l+1jutN7P9wu8u7QDFbYn/ZB1LND1L8UFv+P/6/0uMIXI0BAH/9cBW6izMEl9SE1wVmqZ8ArGCxa+6smRTZNOWfJc50CZx4FjKJlYPu+EjIZqTXJesUOPXQiJxMVeDYs74HzS0QVMN3okx5u1t/l81PPTUx+vD9m7TmsVpLBdZUCJVMZqaUfVeAKvkWZDVzgPNNqJRxA4peFVTK3zl6YJbB5TrxAIfO7tapaY6mtgN4KQ19b1tNiEZ+VrtMqfp64UwbsdnMw59bXfetG77ropndug/xleO038A+26Jh9afNfV+y2FoZok8d3qMptVOS4uGsMerV+sjH/F1E3SJK1RACqUiYs3WRa+70lnyfn1BC/151eB1nFKyIBn6tejmLv+XPk9f9WvXvs8it1QDiNyZBVntwLCFiYTFhHkN+JsSUVm57YkfWv0/9XC3qWWm3/j4If1y/YdB/V96GOXPG5CtNUWfElYDDwUru/mgzcs2K37P1N8RTGw8ruz9wLl9djGV3DdB1TSHpeIR1OkmWy/tvYrRaI6w1ZsjjgXjGfMZeAvWhOsEXOu3ICaYqQtz7wnuDlscO7t90QxtRc2Zy4rBwSt8x6PJWQHf0UUuJHdxhL6UiA1irGCLphD4ye+APz7mL1o8z1RGoc/Eb3NnJiZuT5jLEyUOcZAbHsEXNsPnNh8LqkVXhR40TJntr095ShfuuqAbpsDoI9OWp6IWpI0PzsxGWyoUaarNbwkm1RaxgzfFgT5qPn+fY7H2UBWbXzvE9gXGY/wB0/zd3xEi4ax8aBonrJwSKobIZztFXeRzJcNTOF5tSCgbxp8DzyJ7Jnq0A2K0t0jO8Y/RteVMcosLnXqmdbzuWF5z5pkijYreagnsqyvP+sXdEGu+JY6pQjhRhUYxZtEF8KUbajdieDr5VOp8Piijr81WlmQUnzFbxy0ZkeBc8mKuZ1ygzgZYJc+ytOHXIEXZ6YA0Zw3iKpXMHkxVwPU+d6hNzVx/CUc42V92kK6Z/Elz5+vKp0xh74SkojXARJcKUBASk+1LLPBw+e9xqnWUQU6BeDIgN5Gq5M+T9WrRmnEml6qTCX1ZePkYmVAMRCQaQOTOCRC5s0ov16dmTBlm7kyuvPNRqYt8UI5+c/LOyTuVmvBsjqx5VswKW8G99z0Xrl/zO2gYgXwzeA5fT7g/4jdlcA857AL+sZzen1IdNLaYYzgrfY79qN8NYqA/e+YAfjbrZmXodx4xOK/+IT885jIXxOjPcJEbaqJqon548erNh88UlJaUH998ePGqp8OZloa7jd+65yNdkTXr+3FvWs6GxMTdibTJ3pQzgXUrKAd965kLa3of30PtwEkQNXwSqAct2ndY1hz8VpRBdmALmnoLND5TG4Gnof9hc4BV370A2mV6UzPxtfOgwKda2NfcxoERwMZ5FHTVYb1ZkF2swRiFjq+V4+X4NjSNEnWATNNqqhdIRbX3lIql7h9JLg0R52cfOZb5HDXLP0pruqyJg5Pi8VEsAUbA6ROTW7MK89uZzYrqcUa+aq2EN1WZxx/w5u8h1WHH58EEJMrxWlMqMzi1Nvl1efgaQ94aF5UGWMftawWnOjMOS5fG8IqWRGFZiHzBuALCgAezlHqWEt+CFtMRLVv+6Igj+HoYVXIdg1cOnICiujBqolik2Dkqrl17IqvquVdSO0M7VZFvRD6vnbnHafQetih3jqoNRzFofQdHVmLl8cxvD+eEi2EjGLWVl47X/AmyQplAFGDHGydIc/qHmtA8VdISFZUNMo/b1wlOOjMOy3pi+OK6ebUmzDhn7IcwfCdw5XqGXOAsEHtTZJ8ORSrwqha0iGxgCZVZQSgFbDmk6BzDnbSGD1Au9wxtJ/Ltm8ryIH6mUgOdUzsc8YkfqsirY7cnyIthM0XhBeGDTLmTqigaUsbz1OW5evE83WejcVcNe2mRqcriLPo5YxSbfYXOjoYXYH6qDcoGUxMmkdsChqrOXy4Zooz6r2DGCguNfHWETcz3ckW+2oyvIyHNyA9VoQRVmgykraIO084dobG+nQUiagvdCgI7mKsCBHoLYOTD2+bL58nmAJXr46qBJXKRyptfKURHys+YznKwLWKUCGemquSIfneIDEkzIZd4NzvQ8vpKpxOBz0IflWaMNOPr0nm2LaBhva8uYS1e3OSkxLvyFzU3bAZfrA0ITEVP+Dj/8cciZ4e5i09ojUciyA4L8eS/t8Ehirmv6i6+yJI2PqQZYF845gCV/eOKgVWyUuHNr+HjJxXXjOc4lOZilIhppHLlb/cf82oKAuQ7rCrbFmXFZYNIgQgdVpw1nedTmhRQMaomjE3wrfG7KGVpDmh7/b4kURrKd0qryX7dFrnP1FofFLVHTqHmwb8/tjzArLTeB2fPHhlwZwQFyRU2VgHZypJXQIIMd1VwxamsbJ5tcKAHTq7SMoDVyfMg8ltNr8GlgS5baYJf0cc5eEqrqZJ1aFh6za88RFqhXxCvNLIU6kfAuJyeTll9A0tU0Q4oFCHZACBPcu83gchvOYL3QZpTP1SHFqiKlrhIOcimgYqZh8qe+eCmBbUKGXVbtFQYWBDSo5T2q0UXE4vaML+6G9kiVfYk1FW6YsqIPMC1Auxc2HkFLcnj50AaeMyG4FuM7l5S+UUKNDbOJ9QfW0bRuVa4TCFkRGJjNodiKSkLY/md7/Al0RojMoEtI0sptaj8nNCrVK42W+40AN/JYnWXiWkOVXEUJR56T968XGk6kksrt+VSyYo8Is4dHIgcJA6+Z21MTHZn+KSFngKNRuFP47BaIq/VkIKL/Qdtou2DPiAnGSIfSHuWSdXKfqRQjIspTQfHiL61S0UQSG1fZXoccmOLS9fq4x5spHvJPSQYDLhubPYYQLrrTzpLG3vSdpz399XZXUQ6XC9yOtgCXkOin7WcBbUcSZlENi8hIJ0tyr0/rU3GD7jvCXSaRL7v+6vvjiTVEPC1nyM/R26Vg+ohV46pXb58YR4W+ORVrCBXoyPI3Pj2X4aRLP9jmXm+DDKEZ3t0h/NqQszz7myPn9DS6bEAAAZipD6I04e1yxBV005m7MfjXiFLUcHa2tyKE64rMtXKlniwR9vgNX5WynkQ77f7Wya4sheypg99qP0yEd4uoMkbg1ti+krzZSZkjKdIv8gxAKh3R0DUu7ZmXheDtcvAa/ozD5VeIcB1AqpcO7HNoy80XWGCx3mKN+Z8gyuplysVkDHmZbkqsc3TmTWNPuPlUvaDNX0wXNWJOVCQ+RBX4aSJ8waEFD6qBIFD5MRCCjKXzKjRAY7USRLnD0sJHG0RBptATswXw+ZLQM9RB68gRHxkZNnv22LYi2Bfk6/95C8fk4+igFdzdWvvatxfo3Wi4DbbhK3y2oaJDTnmcvSChPYr9bsJoaFMJ31kaku/7dS5Yq//HYlkyz7D5jXrkSxMGYfl9iOLRdx1W3NUlF8eFVW5Oeu1Ys7qHYRIKM5dvSXqqX62afstkctFaqs3+M96ek423rzjlhiUeOWmv0j+k+78H13LmIp3RYliZ+/5S8gs7g5dF2L8iB/fIH84pE0/aeF7CNiA5mx367fdvrjdgIHZTReOsN1T4PONGXi24sZbM1QUecmBh3HLd+P3JabyWo0Nz+Qj0f54Bm0kSe5tgN/E1tYyB4QswaBXvIfXjH9b6ngyZqTge4dk0vYAFS2m4bxzcsnD8SVeJu87+LpaVkBIZnVWF63BXf7fwNLj8QXRUyuBQh1FlME7vG/haqqZfmGeYMgr3MWoCw/go9L89e4jSbQ0QxHb6UUvXErGixloPyyNMpQogxvgd/B1dexBIYnuVGsSSG9rDl88330PlKu3OTCecqcT0+vGy9Zo2Xds2l7tfYay2N7aiVVSUUk1r4KLtOfaFWBDE6+6puR/L6P4LzbvzFD2NefWeWTPUv6zTTtuZji72VYZWKxwfNEB0mwKg7KuXwB38W4+vwPSimX/XvqfUmAQQsSG31KFC6kEwobFWHLbKF3I9TFzGqppWPu8e1PupBATSFXHoPpofOGzaHhp8BzUWf1XRnYRQlDv6ymWyVpmiVpP+jO/TZmbEB9zRQe1LvjpobtmbRdXEGAvQ+KhVKhg2Rc6OqRxiEzRlJKwHDFT4HJEaXGirMMbXLDW3NmUBF6EWXp+5bi52I1AxAh2pSkaRIx/MTW4ftDGRjGbI3TYREKhRcR3iMV8u0XAHBu12F0isoJIIhGIZAoh1/yBIeeeY7PyohEgdO2fW//07uaP8ceSw/fD2rlK4NnzNlmyS5j/p+Xi5dQFQo4jIBJVeqn8EikRxedUrW2tBDtNKtjYf4N0zf7Fpcri4BPY3IY+Ij3PjyNKpUROfg+hXA0uVDd50cwCtFTZOkrMyx3GcIrVCSQtnYeXMLHhRfhL2MhFB+7OBj2yD7JDsJEHyK3OP31/Yc2soU84ugBFpHBCitkZ/MCs6uLsvnQcLR1VUEliS6FDptVHQOS3zkKzTMfz1n+73vzl3qWa6/caFi84rL4dk2qHYnteGb0N0HNgXJRbhoaQc0lmuNxaFy0DfxTgB8G72JyvfjNA1hXcSumQVJ08dhLMKNYsRHmlOqaYC60XLFqYK/BiFQ7tymEGCiKmk5np1GRTQqnT88RUBjRymt5qMZjRSWhk8tbJWehGnBTfKMVJ0pojODxkgLnX9vOq+x8UFN7o4kvipu6D+GCyQ9lsBcINIhEV4al6MKZgMXLYj5Mhhjob0Ia9r/aKsrqi3wXMpjwsbrSYaOZ4fxXgBF5VRL8DxF35o7YHswerbTrG9zTMc3JxwZwXM268z9hCHGLdJHb91JjKoXU/c0Vt8U3S3S63mLoDzKFa2U3i3RMPRW3Zm1kddXkuhNyQy6IUdAPbWZ/XjZC4tdztatArUaPVQmWl/hsVpieuRsLGhQvssUxld2G6CytieLqVysuZrtj88v1yFWWtWDVJzq/YFEsidjpEFwfuKUCQcdxWZVuF0FJgbZi5Q9CQOl0bfENha4K6E/HUejScFkeNguprqkPNgPETVWHlqfy4cg6SjJMw8e3Q94eghNgWMUlIMuLpnIR3nPEoh6ZsHl1Yu1NSdMP3tX/jcqu8GhI9PwETSeYhREnm0Fo0TexQN7RULX3Aa7MuKBnTDTRKj6Z4A6F6tKLpqLJtinumFtGJfdk8uUEtAij389S9lD1NbW6wzEcR51EraoXtdEkpeIweqQ4ap5W56WV1IYkzp1dVSItFJzt+o6es3xYE2Xz+szL1sKhJ8FFL+CZN0b4law0IDC3VvIddS1dyJJZ7utLrVViAnx4Rz9HnSYgWbIEA6mdlQGpJ9QaqOKuZIVGCp7mynYZFW8efNla/fbmC0HjZAOVddzry8sjS1Nj1KSl7Y5k41pyl0V779WBUtRFWg3bxA/x/Q4H0aFsT29Shrt9BcGwJw3GRirgL23huLsbLmuDCybsMZhAVIRXzGWgBMvOHJ4e4BkCtxyzDusLMwGw6PhA2kmZELoZgnwe2w638L9jGQwPnzSaRe97UBrLVPnkWG1rIDz2Z04SJbyInMJMVzHf/psZXpkbXMNRUqQVmBvlmN2sxlbFUmyhc9f7n3frocup8iKEAlt5ZMm6VZX3O7LIPF5Xpul/zf4dqS9DPlVXJT0snopTC4iG0SIZbVFuYydemaHKg6M6Sdd9wVmfNzvsyUYBcacslbgaVVUuVy0EBaBjuDGXmNGbRQpZ9xIHGGaYUqnFrFfFGxLpEM0zhUCNn0fYLGOvwEkrzx9zwAu1ud2w37e4q/UWuMhVq3UoHwY1N1RxQtk0JmH3ASS/bh7ZQeQDNAZHSR9xb1pg+V+gpTXRrFBXMpjRpad5IHrnMQperIiwvQsu5A05BESStJut1c+QuU2vbKzImDnsJ6IewC8Iu4IBgZBUDggSViacQNiaQUykgSFSZiYn9+Od8TjCEZ7xdw1iaZZaChfYS9wnLM9pp9DiYAHN2Hzz8hD93h/wVaM6YafGvCV8W188LKhvmNCUR4mlNrYXwQh3ZZjX3byfvjB5sK0VfD6xa/xfFlocc0NrEtdWgyfN3ZBkQS+TgRSsN+dokTQ4knV+y+gX2rbPyy755qDd3Du6srFEtzTFLc0X2FMjKwEB4GiUazMNYYUoFZaODdXBT1DakFLChDebf4Z6b5SBzsFZ+k4AKwhQH9CDEt8vhGPbdGVXawgCn3i6X0C+Vp08XlNVbckjed3O9jnnygWRsSKHhwf3DDugSZrchAkODFSlhHbHDyitAXBBD/pxr7pxDuN2Ck3F1ljY762tJZ6pNdZLe6xiaSidaCJUkerQF5YwRCx1XqYLHc0MY8ooZ54H7HGjWd9BUbrdmZNvwNsD+q6Sry9Tvb30+y9tg93/pyp/Odi6BQbdGqTldAb5gbGm4dpSeXVCkUsc/f/xFTMNAwvZYXQcyRVhIZvKbuku/nKCQkKGtoQzBwePxaCwKPZ3/8WfxKcSUbQoWq4jMDonwi3+1pGFQ4UVnqOprSYRSrfYsIuh180lpqrGQzkRkeRGWh4cPxTRbSVM6JCxGBNorcj80PBUdklZ99UX0Ffvo2e3R83eiHVo/ExU1YmFjjVTaWCEUt1SUSnKyAv0dA4LM/AP9HEK2gK466M2C7MIC1ZwHuG/33R7QFtl/D5Uh6/487G9dSjKnK+Ig2HCFWDB33ufr87JY2yc/bUe/2V7hnJ1Qk29Qm1NFfyXS9thoJzMgHTGLjkkXvTKYt/9EqH7tCOVb7ehPgMhkrvZ7GYfYoFC/M6HzwXJDt+dfDT4vYq3aGnOFhrl1E+ETsHSLiDHLMcTjIKYfmrDIKWevEecrfomGVnewweuGuy3Q5gc2uNdP3JxIjN808XkigalZmNHD6Bg9mklKvmwpnr7BWPTCwn59SqKLfoO+9bxHyh/Vkz8g/QA/affD+xo2d16nRqcY5xpWQn+DKo/sartGjU6zzjauTH636Kfr17u2i1lbzgJ0UCQPksR7vNvr2Vud+aigWB4iOrMlwpH44Wn4nHI4vO4/5+698LZbpEQob6HPYYYjRcZKS0PEPLjrSJ1if4CwZFUnoOhVX6t2YO+K6wMMNmuAIaw/5ddU+/LP0MY7mLzTE+WL77V2vyT+vs0aVL5eve0C3+35mG++kNNfD1a2Sn7fNTfdWnpmImuJgVkwDjsTzJ348QRt4jcTQdKvkPbk62XxBFtR6EbW5fN7Ycs2hayTnWnvynvDOdKmeUX5GPmEcSmE4hPAyHUUObGVLlKg/I54zClJ5AhB1c6RKnR8vgMYR9yeAEcbCy852VmkuI28OOobQ/THkzy7ONArvqyU8MCpktY966B9/lLxQCEAb+HJJhsJUCmnOyqRV3tlvP5iPK1fy9JMvrnavPOktGTHJm6ffyLsqKgvKdfTsMXZDGJ5GV8tdXAEFgnA6q4RRN5VNzz4QHm1sQtsDOLWJh/JPAF1RTY5DCBdutCh/2yCEpDQJQsTfP7AZk6t0nOF5wXQP1gVxkzV+H4nzBXlUxArcg/GHl0nuNnrpkU/bQ7u0rS2PWWFQab2Uif/2WhHnutRNLSahm5FXXejBAwRSsz0AnIHHT7Nc4YobpwwF6VdJ+GeVmgZ0tLcYehi0n3lJyxzLpWMAkVjYxpgbEYdkcfF7udTfH3MDXmsojV4A4vXpRBQ2pTiRC4a9x3BDKyMRNEmF6TbtYWL0KKCa/B6Ft8h28dgyGaEiGNEEUjhoB2NBWnsP3ySrQzs+tobCr5umbRVEXsBx3y2tiOI6GoP5lGLiLRKegNDy/7RzhLvtG8Yn5qCtFI3J0rKybLTZOQnjQ+kzuYvw8fgEAVB1OUnxcyW+lQ1sUCRidGXJxLi5ms4B0Wkq40XuOlUmpomKs4adA+Uz3EmMBpZYRz6Z6+xN/Q6JWEqnsBx2BM4TbEyAbvuIGF/RohJljHWWKM8BVNQB7qjTQYn89zCEVqMojTvCHY7iRvGpcHtkBleCDiE5zYTVY1RlsIyFycgTcHJ7kxrGBqBMyyJ/zf92ez9IS5dIX94sql9p/A7KQz3O223PA9wrx5y8rBFF745t3mrO4rrxeVmlYJBHjXeP7Xf5G1zNQ9UPXjIO+XGBHQ1kXROYQst7SFksOOxgXQK1eVdprOvtBlxPQU5naIifq+37C5SHVNjW+z8UepKjc69R8APdhQou70SyYUOqBpsjWLSco7SNHQtQsf+0jdjiOdTzBX3RIPbalZtEydmLtMsRpVXk6A5rrRxwngj8iGSlIyhmx5a/6wDd6rJv4Th228XV8ep19shlYnvlg7Ec2VtCX6Fj3PwZKXTvf0gNS+89XNO0VMEsqwABVIGj2fVtPMVogYB0+TVjwKBX7sHPKd2H1YptzwGfqJByVaTDTyBOWs4J7ySGkwU1wALXd2REnbREaqWXo/Qsn40khdy9Vs2VL8u59Yy9ZXl4RUZz+tntjkO9U5ptA6T3fuR6/sofnhff/Z+vVsT+h3bxNKmqrZEOGePeFeYvcQVLeUBQ4RS9sdExKXnT6VJfmfHkeNg1CRytG9UYEQknf/q5fVbeO+8mpeMJivp1C4l5ISlk3EFHW5OEhKBcJJKJvOAoxSzIZwlrvH0A8Ran2OaKsqXalbuihqrpgkKJelGDdjwAGRFxGc0achy6ZayZyp3rtok/BDQf+XvvfU/UXg1+88qM/WJdp0mpbFrldiWM2KeTUVG5S2b8qYb7zeXHzaACEYjpAojR5qV7gmyYkCtLZ2Mr9vYO5XKGJbXvp1b6kXHqeYBvBmh4fT7hDW+JnUN78urJz+LDBPPcpZnOG82Wrw4B6JSjBCKJe1UBmpt0DFtVf4XgpU5obI0wBF40O/UhfVHCH19aXR6LjqdlY13usfzzYfolpwN7lNpTfSjb5TiHbw+ungVAY6JpQfS6b4+YUR/WF3MO4KWAIdb1s8QVFCnPXcKtODHqLXQnIATizbvVYKJRue+kf8NO6m48RlGYaEbUyZGjhboI+UXZ0r6myfZtXXsjVLKfhJNxA7U4IsKjWxat0JPcaHLhiHhmEAIE5esLVx/LOvHKxwzu/bsndnLI/50kS8RqyrKIgTs/BA4hnjuBBF+/h9iqsds25MXjuHXeswSpx64UCDnv2b7z37Y5R/PgkvpV7UHaIAQBIq6nM5IT3oagsu3EKvYFQ6GUlF8v5+d/6ZNy8WEHg2neeoXe/FHhIr+sfk0xcDW1jx14aqj0FiaE/Bv50/dkJdWmgrCtAeMh4y1ful9W9ld2R3g+E2vytwfry7JWGuqfMhoNpvl1sBEFKA+q07aN5WtZHucu3yTpA7GUkEv6sv02cnS6ARn4p+iUw597XfetG71XWvTRyvQz9C3xcPrT5u8illKA8MSq0q94qkdBaIufGFjSKHGWaZ7jeGp7znrcd+PaQZx5ShZkpCorFLlU/ICxlwvWPX+b7t+hT62M0G34W6V5YOOwszllvoH3Pqs20KniMsHNKgBIpHZ380sn8jD7adOLJrwrpXdqRgXbUl2ZZZHbiH7WkvlA/YITq5UiurCCZmObslwAgnRgJk0La83gJenm231zm0e6PXeYo8WZ4etaO138h0vJxHXX2+x5TV9qT5Tc0Gn2JQ/qJQLmen7NLPojoGoXldqFV23dK/M8qSvMHOhpfodVnS5QpTX1B3sQGQzfRMT1AmgxUkQVqEBmCVdMbfzAi+06nNzegxsmYUTrfxBD5MP56xQl5TzPslPsNZiSnmbuC82UyFEIKvL/WlnrcvV9a8ctmz96Hed+mVNB07Ozad9N7vj4Ys2nbVmM4aprY1T2fupIS2mO01bOnqZqAqCkG+dEGiKh1g0leSZsCAyVDJNltUcEnqLc2ItuUJ/n/QgxYu923US2yXGjlcpChOTFXPJNaIHmNWK/EXzLFHVPSkp7IgK6F0Kfn4Dk93jrd4C0lIQXylqOvPS3VP61HHGHQfZ0a/Ro3/RzXZ2UY9yaPlTD/AfD30TjjMTaylSK7zEg2s5bTW+8bNjalYUFG95Tn8QyhpAT2+09zlA8swP8vnUepO9dK3gZmdnwa0N606+9wE3IJ/Nvyn3Bscz5z+oFL7okiYsFmvnmEVfOg8oRS+7ihIXFdpZ2YFXE8gaaqQlBytoX3EG7U4aWkk1bCraTKG2NYZnZ7VcbwAvYw41KErvUAgNttuK17R6bzLZ+WzKEt3t14OvwgxFf1r/GlPUDtDMMUSdv28R66J58suLC6CNIB7AeUB6ZHzDvTLrk75CNO2AT7gzp30805UlZrE6haf+yvpjAQ6WLj8G07Gng/QcfQPnqaOCFAqRm7QTKv4E4mj5qhqfhcxWUYUbXaAZV5Kmz8pnHIba7j5tWh+6JY7epf8UAIv0Y8RwVezbftPa+034sJcruMMagAeUfnTsdVKQAJRL8rgoOBG3/fIaXfPpl/KFL4XgymTnDRvGOth4/1huiVBC1a75pDwOTEhyBorSrAqmzwfrUOI8TS4ZwzyyGwYBqDaKwH3Hp9XlFwscTP637j4uW5dW+RnTpaopqbSSuQUKle6zcWdgQXvsgIg629JZkxnnuSnc5hQTXVxyfh0muwkCugk1ZlL6snvSpLA/tzKbmh2fZ5cVXO/C/d+kEbmleBJUTWCayNi8PArfki+1Ertc54/IKM0QHAGBjyfIIQXQpNViDLnc2kVkmzEKiq1cEYNEJXQjx79uNCIdIJZIo+Ow1Xjqo4L5jtLVastZujWTibD6X7esz4gMII1Df1L/Jr9lzPySafJbuDle9Mj95G3PTQdcUt+83aPe/AHSiIwdL/J8e9Md95Nmk0jkm9rAHXVsluXZ5fsFBRAwLiY/nCREKdGtCAY59kCqCT6maGoj0GIqyU4h4yUS2SgMjjsrCedKc3BtWAWjS62Jw0Wl7+V4Zx1GLCpPRZRSW9EEWgwzhliUU3AExA4EIiAmpAnig4AcQR5p5U/HiGwDleMZGxfK0DnQoaQOSZbIcfvxPQGX+Ztgtta53vsJXVSmESJBrlZuehaG8Wx3CFkUQv/1LdYCO301ENnunZyRyekkaWYe6DdQC0ualo8yRoMXLEU2LU98jhwPGIchyC1oYO+CP/uCRvtGnbirTkIq494tGgiRC5Wd+UUMjNVG/NiGHNvNLHEglVQ3qVbOCvrsO9IIEbn0ButQSi6ckk6F76ypOvg1ymU6vojCknsFsDOU1JYMI3MwM5qdKaRYi6o46G5bxR82uVuULbYjCwtDiqIxnBHn5xlAysUUUXoB/PtGyZqnbCMmXsKnOTzjblgzC+yOQiaQxk2fN5mQbWMXbk9l6aKwtIVs6r/hI1BdmdXrImt/lC+/N5puTOclmooJcsJz1u6TZrtQgJZnNg1sSpvPjgVAU68C6z6bWLXiuQe4zjASuAecqxL693iCheZcfL9CMksq+gPZ/90D00VUGD8eGyYUsVS4zNwzi4cvIY1DYhBnhCun7WtGrrT79KyuQyhVokjEuiupW3siU1q3S+pnmKcqFOPPxewWR9v9bqtyx7SPjzYY2cJcA19oyhrFh4t9R7BqAw/BG/vzGPyraow9tuEC0og8Eoqkkd4SB7U2Ge65P2RE1pEUsA1a/lQEFFu8drA4p41FvkQaJ+1pUgKSFYUIoxfgxayoRjiJlebju3LEpM6SiigGHi+ZN/lgYYHSOw4B3FvnkM9331Xrgj4uJnEWx55/qBjIB44+68xG28esHWrYtxeTD3oVM5KxyIMqAvAAjWl4a9pq5ypw7Dfi0/FP45K1wu49Dn+mLgffo6iewUsvpXtMghkiKiwvChFm3Hg1Dkya3lhmACggnNfhu3KHBOYsni1gUobvVhUePKjnBuJ5oAVI/zpm+mWC9OiZ+7Mnd8fJDfP+pWmJ8uIQPi8vjFcWMxKl/s/9Md7aFC4ZiMpRUVlV+dC/+M/6L68wHfIbisbximJqHM17+WnTuSwgFqOg0iq/DPgkcvS2iDBeKOxHUqkUNa8AcVx023Q7Bcp6+S44XRhLKsqlfd14u1W7fpOplLmRVUJcEI06YawkISw/jGVIXqzL58f/DW5gT8xf15dW3dyTdX8Msr5kBJG9YJWYBZMmE8MUYoFKaOuNPgb8GM+yor8Hcp0ts7rv6n1aZSCwv55CKV/UjS+4mxRWi+rFn3dfgn8bYAF+yBBkiMPgY2/K7F2XUosld/UQvKqADIwIcvXCub7yvWFocvurAXbDy3J1o6/Q8L+Czc83H40POR4S+N+vtr9OwA4gdc6uyJRfrPQgbA55bwZgAX1Ba4+s9U6c5m0EbUyZNm2jTvjqvrhfygkvZ4AFYEeaj9I2eqQRGc2erVy2zd7mRH80vuKdbKy8j9oOdk8HVvp09YqaKbprht5Xn/v7kF9awdTG1BfUt1MW+eEbskDUgytLZtgJdOPaxtKZs5wx2sA8R/d042Rlo01xV+LKkeUMs+GkxR4ZG58ed8FjhZ/3r9J8XkUgvS3WjE2nfMUZhbFKC8TYyjGPrggLUb0hIp5zPwYrM7Nuz1ZFFkBtuP6aZadTPgvGfjSW9wBpRIZjkbFaWDKO4wy4kj2ENrkoVJ0r9PUVHiRqVtp0pj5mRgWBy9S3s+XxU4tWNp3Edouzx6sU4oRPuYdckqjCsnCJyYRc5HdJrg/qylRuAC1GZY4Qi6OEFXlyQl4DUhDRxOa7s9S6Y9iXVnb0z9NphI0rMkqkh1XTOFYJjS2pla5DFSttuvraPGJObNbNc8I0TtSYaKVlL6gex2rGKDidDs1ivihOnR1cPmeIijC6wIMT+XZQuJAI04PFujcOiKeUfTcudsERVnN48t7tQp2fOflWGb1UmZZ93UbpmOwCcTbzsalIuy6rHKZaJazk9tGKqmg3F6nsjqqoOcKX2KZnrsrVgxSfwGpq20pMTeuLfM2coBQnK0A4F/FFb/GjnLkVOtzRgHhcGV4uQPRYiQoXvOHjj230eODSGidQmG8xzadDyq6CtrP8w3VJHthlM+AXBshcqqR2BjhlgNqnrfOG5ZHbqzE4eE77xdUY2bmLPR67XDZTGs/kIv/6PzHN5f4SCYlTJPYbO+1HkgeMA+sW2duvzwPDAw9pTGb71RNqk2ngchfT+A9kHPpKaFt4WS9YGVsThEQsJLu0B6pa9R2M9iBdnDuodas86Zg/PqRO3xKMpuDYRdoFpghN6pch5d4dKVIySGwf7vb8LtEuSVZqGDcqkdHSc4dkcT/Q6y1EATpvXfGOQySPm4LTcKpQWpAfTIA2i1sOJEaTAyhEn45cmtzHz47x5ERbuDcyUp5x1T7PU/xMjDauzSALyQmum7hDsGyheg5HTUFU5ufwoNq//gqOyoUN+rBSfjXytqaQxhUqiPFi/1HBoZWC06Nj1K6HjZwQK/FvTYh7G/pxVEpdggjaF3xKWQa18gxk8O/5KMFs77fC76x/3IBUZQSgPMCzebJMljWAIyC44YL+RjnFu/nWgFSbTuAbeyekO+jL1IUx/HHlP720p8PbLgnca7+r8X8LNHIVJA+ZnqOpS4bEwl7shuQX1WcsvTqv2IJ6x3KGgKZ3Atq9JfWzLF3Nc2Zbp5XEmfcC6jCioRhcO5qtNvJ2k4xm0PiuiMvlWbewTTk3DqK3/UDnuyO7MffmJWX2TVxzzs2jpKmfjl6nekLj8TN1gbDWKQD8whxUvh+g7mg1Fk5vAuvi2oIY6lZ9R67PJeV6QpQgyBA7FJQXmewz5b5hrvkeoHJOld2lF/c3zuVyqDbQwCm70O4vWpa5TdtgbtOYoR8nJR6jpVtdZLkQC/RxcsIxo/dCHUR2aS76gJnZm2OwBLp+7vZg8XFPDXBgW6KD6B/Q/DkjuxuY9SdEFsPHGxj1mf67AxfJroba+rkX8RLL0foiIAfgZ0v5X1dOnGcDWGzqVxwkkqQwDi1gFO+0L0FSBJslAkDUlRGwhvOt1DS+UCwxz6SuBXXEKO8IU+L2xYAKeoZ+CtWLCvpGN7HG0CJz5Mr7EO/2gz+2GoTJAbz+9voQzQ4Gs1uFJCTW3TTdPJjeeN0DpIVyKrP9IrXxpu4+JR6h3VjWmXahdmyHUJM0zVBR+CWZ0zyQzCYuL40Ri6EPnm2yGX6pGfjymHXoQEFvSFGWglkpdDfwkD0gXKpmLJRT6C3xm8DsdS2mlwF1qu/SHTsCTvtjIT+GB/i99Vq/32apZurqZuqnGNsDSKMYWOdnuBnaiFdAzGOgfFfGxlbbQG1PXP0uKmjEALgCa+1vzTEhYT/uy0ECfR/Mb7T4by3flo9gBIAmkcC/AAf5ENn2Ww81Yrnlb2p6EXDKPhe3E+2vGcvVTqQMUwkzy3PZmY3/OZ60MIpqqh6pD+1bd+ihkRmQM7aUa883Oa/5oBkWVLLftKIU9BeObykfi+fIZSVm8L8dA3ixgY1H40sfzNaeXHpWQ3tVHpllCmpmVRVh2vmGP49I/zt8/Hcme/x56eph3q/Vu5ZmPW+4uSkuXmNJcAIPaFO6x4p8ppqcp0u7HqrbQ9mnKv/BhY8lKxgkAoKeQi6GjzJft2o+2zz8Fz1TenXlxMSv/H0LooXm1lr4NUalgyUlOvhF3fBVcWm3/DPd2llOZ8e9io4l/M0Wakv8bUGbT0xLQC5HsN1tWHp1WIJWY+ZIVF5WhRo1k69knC/xK1yc5evmm+PUkKYX8ea9mn0m3N7e5YYBMAIKq7MIHlyGmcck4NEJacAc4ByYhjRt+rzJiGwfc769JUsX9bG3t6r69/jI8bcHEL7LKiBUCiONJL3yNs0XE3R09mwlt3cAL/dNjyrv4dvw1hlq8dUBICAMFczS5f5pYcFt2ub7Iy/4iQ36876ffaznw1vmeBbA0g7B8B3sN0B7YcDWTxi4zGgICCTF/eICL+p64U9JzKaibUlS6V4nl0cH1VJUIjDIHFEwoi1yGocc+mJ6AgU227+kjGNDqQTYpQCGlyBLCnHiCdKAoU6QG45Nnd8JC7OABXtt/wjKMsDDtt5f/oAci9Bv9rUpkbrgLqkuaY5RQa2Ub0/lbc06Ki+ZJcj8+mcFt8H1L1217DxFv5w6/kEy/GrUMrQnv2O6qDQZs1LopmejLCBMarknRpX7ndJnamCYXXlxZR8sELHfMJS8F1+72DkLtQ1EGm98utG8utdVw6ui8w6DVuLPmBnpHUA/XWmC2+vDG0PmhhCdf83WjHfkbsAwpEkRZosz6EFNX1hae/9hn9WJEVBjf3HnqyEFYcGv3E8pz7uOWaagL5lnicJut6S4LiDAdynapQNTqNJBoC0W2WzmeMFz260v6o++3rDxubzr5eA7VaJfOY24sHT4mYwyvMsrLdEOCnO6C3Vy/3R6mQ4YyKoaX1he3l2Q3kuUMga6WeV1luPC+q6/X2V53S7JXGypfoftEthVpVvQlXcthbqgnppZ+4Z7JAaMd7355SA/KHLHd5mprI/NcyhxK3ppAnurwGZIkq76+GORT3+FK53H98AWbcSCk860Q/nSMH500QOYIh1ZLnAW9uTDI3D/uyi6eNEV00nHMDGZSMSByuylxZAQaD5q9P0QLj1wpTDsQzOF2Sj5cnfv9myxLukaMW8lc7b+hX5FZT8lU8uhZvMJPGuOunihtX6f0FzMiTanZEpSi9gcE0bgeCSzPKu76hfkN7hUhl3DVV4wNK0RJUWwQToOm5OLgaMMwmR2sf8kKBo1+n0Ilx5NLwz/0ExhNkL4o2l4EVvQlBTJYk5lVxl/YzZyjF2chyHG4eNoZagS6zc3L+47kSudbI7PZeSwdTECp/PZ2dmXPvPfmFkvSzazobRcHz0oGyl813zIB9LJEctmNDMTdsOuCpOYGIBcU5iaKzDnkDHobCFI6K8bmMegbXLwWP19YFOdjp5ZxVIxbT3sCnAUvP6O0PzOcIpXYYqaze3A8B2PrCrLmukxv85TbnkkomwYlsEWU+DcZekn7nnyrSo9B2iZzhru5dU99GdAn4tH7EYXuO15aGI8IYZWiiiFA0Ykion8CibZScbnMnI4OrTQEbM0jhX0Qpur65/PQXvU2svx4ULwk40QnKXAqS4JTRbCfvBlhM/7kMlv9/Xoe3tMtdahhwVt1YOHOZxnHPQlyuHvhmBLyGIFZBIUW8AeRytSLnf17tiWcVlasJwJnrNjRSfae3cAKHQsvx+/+PiJ6UmsG/Xcg5AUThyN79MHCkW1vk+8hNMAzfjwJ2aFZr3YQ1vLDJNF74MK83CjPq7XfX7of9ghYhQBkvyNsuSw8sS5BEI2qMROEiHwQ6Yw4YXTJyWIeatvdvkm/imbpY+G3ECakNNhr+Dw70I0XAzjR0dw2Yf29tg0QKtRlZAY9z/i2rEgiF1nPu6x7lnxji8y+my74VnhtP6PK/zz+EGx1INfF9nUIa5LW6u5iF8D6PDO9iXlm5V7PXDLO5qcF3zPjGc223vmniRV1SkC0U1ySBYUm8Ztu9udau2SMiXB67gy54PSlln2KWW+AfW8bmaLq60fzz4RMXS3PlNE1vMFZuAILlzkP0LQ2PmpMtzOQrsefqBgVKnkw9+zgErsBAeeNKP8dLkA5jQZFX4lmSfmr6ahXJGjA6OKx0+MT9DQOgqRqOFA6WQfAygbFfpd96JOp1MjVs0YZmbspk01Q2LRAG1GVXn4cfdIhNPJBmNl31VBJroom4orlIiDeXzlsqSgW/pUU7O0oJiLVbR+kbJGyV5o4UvHJ8SX47LZT7pPpyyX5vrlioJYqORiQuvKP0pW3GuZIVR2xFjilkVBrlVIguSzuP4cgREEDex2rsp6AXmIavg/Z6uSAGtdJ0ktiawaTCacDgDeAjaCLhDAagc00JFO/wHDQCr6Ke8O1oK4BhS0m9JoaJMguXGjjObjF5eC8py/qxnbJShubJuu42kHiy+QWCxmjFF2/OgkOFvICKhHZ5VIjIU9aaXbpH3big4do7Sb3pwtUUyYu+OHX62nw6xqSzDakGti3xuHhL0AlqZS0lTUAf3uOOr6JuABAO+/F8ZVmmrrjKZSWImyo67W2HFcXTa4j0LUsznqedoTTFF4MB7BxAgaeJpcOtgfUu5W8zYUb8jFKCMMd9JJ+UMeRunJUkKV9ar5Q50ciIkXcLBD8HK2UW2FhOWAD1qu3AtiQ7pqlx1dNRjcvcSlAPhhtXkmucBnUw7pwJPTq+T2LKo7thPo1PnMds9CwrsTOvM7j4d3Hdniyt4z95228njhvFX0tWutKt5WXejGP+oGl8GvxWnTVQ/iK0brJaWz5HOLode/Jyi/dY1LGnsLavEPdWApPB5Xla65V9Biab20bAbmJBccqKYpq8O5+iobPEEoaaFyaHYWu1f5a8d9IqgYnYtAUdG5RrAGfkEvet21Shlv0xba8VvN4AHEbqrKgesYqReWTpCP26xLdd4oet81rIzv6CnQ4O/2ghsQNylaH65quJ5fOkY22Rx2duxEsCoPOeuw6JznqMf05mrj31h1JyppB2BCB+LFGt6F1hR3uB9/LnKyEt4Cb6udGliAlAsbrGXb3abLlCbSOPVRVc3r9nbheXP28P1Wg4TBl3tDAhbxbUDVpxfGeFdBlHCN57ioogHzdXvw7S7MbfvsWy43DPb7FxLuNaWUGBiJBsR9e+n1WbDckHz20h58I/PuUrtnXRkL4TZzTjEdAHLjGuJDOS6jrkyZTszYZDoS/dCIzxCL0dgKwebBFk0Mx3HOZJUV/3EZssIZCF7f0nCOVXeGMDlFvb2SsqrgztDKOUIX2+70CXiDo9qFRm/H84rRi0qaAbPIL/bhFwW4t3xp+WzZ/KUZXjV3HCi4CiLzt5dD2rv8RATtw9jD5KjEhsWFvpZkp3ZQ05eyfhGuW1HC9Y0TKo+jJu7seVpien+wUfB0oeLer9MW2z9tyePfQ90oOEfnlYvf9SR/c5FF7NAtcQAtsPt7y4rCVQLpEP5c90Dd9FYqEOd9nzzsg7wzHiv1u4lpr89zwxW8kMCGl0H3MZNpkm0RP7hwYA4a1NlCB8J7fzvShPSI7Ux7M/GmYkyI8ZaJWX39rMpKM9nGc+qJxv6qYzmJPSnZGdY/0Hv6PffOyTjL1iMoK/OoBUNqiqakkjLXy9v6NtEYMZspRv/ImxlTEA1kfI7JySo99/WjH5V6MSmDP1qmV4yxpbXY64o3BXljuRI9XpCmDZcy82wkWGvWN9o+MG34JBreWfOgenw/+woPDjsqfwqGlW09zUj5JS5VH6dg5V1sZMKDJYM5d6StmniUU6qLljDjrRcvdUwyVsKKrZyXMiF8Oo6ZTo037aMa0/P4VDywNERG2acAVjuXglxVqoc3n1oJWi2Y+WxsoL8/YaCvnwc1D00M6jTQAg/18Gv+69WMQC76lVkxC/8IVVRxWDYUo1+sspd/ODJuyitq3VX1hshzNUeSPUGEBWM0qvAhzbmrNHec25vq5Y3oS3dftIimd/0/wZQilbeoLCaMr65UteVTE4VLKqE/dBxBc65aXFWqbVlXMO9CxO/Ux9RhLxlLjpo5tqBytzdrj9sz9uq7bxPb5go0JIkI1I3tLQA5JVNQzSKpB7QhyZaARTi+AZlOle8AqIAJRP72Fgnnz48NB9JM7VzmGMo6eOzzWMwzbjoCQS0K1l0Sf2moyIe1PwDIBo77f4N1m074vdjXZNPo+FIj9HsjOMWxBYghxt2KHDIqsC/bYnfzKgwrxbUB5TCmez58PrabNuLoWWGXd+yWtcUEc0d6YYzgILnCxJTmdLA0Eorer6Ygv9yvOkXPajCzxASTjbj9chBdy4h7mY0LEir020Ij9CHPYWdVpeFuWbuFHhJklg6WpAgztojaXnV+foVfzW5lak1gYSoqgv0Ll5eZFdOTeDlw+LzLyuseTnHZQblUFMAqvYaxOMc4XMPsdqBISRixUD7mfMtZ0LH+bmQWLaky/Pe72SGyLBf/188eN1G5pGLInb/4+Uozbhc/5jvLeKQqgjWO03B7GOXrmiDWfyLkmrLd6ghB0sD5U9P/n0b45Zfis2b2mbXZGxnZeSDp0Wg2kJPwdN/8v7sTJXk6Yv38OdBytaqlNp8AGPR2S8TNodtN5wfSWFlFZ/yNfv40Zpbm9EDj6m8M3WzKPHcAObt8do7GhBP3A00pui9NbyNIcjvx6pMDRqBJfONuXqNkJIcAvbr2Wn2WpK5NZ/iOMMgdvIM0ImGncSgxd2mNWYsYcB6F5TiU9NQ6z7kkZuWjEAWY9KO7QOQ3zsG5JUxADpIGM076gH7vWDxIW0AagSZRUJR0U4FrZxuyxHsraG30B7pk14qJBeYRuPUSN/ifu2e+k8iP+sLY8EiTgjzsJ/r24UD7n7t7cL6xNvLCaY7yVoPp/r8UPvWMG7R1FBSuRFTYD6fXzUqYNuBghhEb0ppHk+RXlYkYmSVBOrh73W8HMweP5hnbM7KxZVfTkk/v6f8Vx+IP2M7ZHV3g6z/u9NwGLZxYPVeOAZyNJWWqRX/zSUzTXpiT/0BLspy9oN6iuWndcCCtwOIKsC5ztbsqvi7/qf6p+vTPmH+irDZBtBBWfOZP7U8c9nJqNWSyys3uVoZI/qX9pan7l/afhriuX+pfr5YaBT1KXZWq3KDS1KDcsTAuyjIK8Mqhb4aE7U4TXhODH2NEQFxWQo47OuDHZvFO+82WfG6KIfURIkJCohPqQLgtZ81OmhWBNtfd9ieXFLrEZm1vd0urr1uyg9emXao+1wikhVxOKjALZQXQICkA8Avop6+kfbkecvu7FxC6/uxAbXtR+/XRkTaD22z8otujkJShBN1CbvjBeS/KOezND3e0Hj5aEZRte7gHdqw7mPhKJ+8vkH1xlwQlrgxUEiJ+Qn7FgIQYsX6CAfBrErZbsn2Kz0VGnuNuds9L3nydGb05iy+scmnbSZOXIiZYF3LA+ixJcS6GWCwgWbAcrEUuVJCq/AXp0NxQvLSgqCusKvdtKBm7bZq6dw5UPUa9pLgPu1OK7ae9Xr7/q8KU4o58gmKg3VZNNL+g7olcHTEQmHZNSQwpEMSQlRyW3chMS+OWYcSEdgyVm8ZI5ZQhpTntSBpnrRJDNZaqgnAOdxJeQheDMK1NkzskreWuTDG5X5FAoAkRrAx+OI6aTIqvGPyfDUEEngZnWOpK/eyBL/2qvprGHaIXhl0nnFp2McvUmAh3myzYjJGXQTDEYhZJiePjjApugcBGEQspeFlDlXNOJe1bU9J2WjV1ew9Ke3TrxeVu0qqQPch+u3L6ixxCga8OzCZurtgH+TpW+Eoa79ZK7nQrK7b3S4Z0h6S1oRPl06fz28uKCluY+IocAb29kq5ml0DTMhj66LRZZSDIdcUG/7KLqA86ow4iXutiGVBERiI5hlYIl2GaoFQS4qGwZHKfUV8VgZWzqD0MPFJeqJnFlUwgSie6N6JFTKg+XZ3ZW1UWR/A5c9AaJt9VdEBE0ssQklwdgsJKxwcScSitGxOebgCAoq5b63Jxxa05mlCVWtdYbiIRJJiHLYrNF9Jx1z6NaLYxGaLANlY1i8WXksOer1hh2kuSLPxq0g45vX5PF3JG2RtnForjTFUr/WnXQ3dS1RJjiAuvMSpaQG8kPWdlupDqPCgthUsRw9vFYh/Eq540TV4+BckNp7G8PA4sJwDgAQRwS+wApL4Zs+ZcS4yiAK/7z1Ubn0//Liv9/2plJlO99//oJeC+D8zpgM5r2nv6QX8WIHsQZgox3R8hLedCXkMcu+o6kN+dTo+22l3ULa5SwCBtLms6LKJpsUa2/x3S5pCrc8xuZCa3OvFHUd99Kf4XLcrmvYZ1gy65JfCzUhUoOHHHxJ8jpsXdzdOfF/T6Py3o3B1YfHgxF9vhIhSxegh8Vek5yHOd7oguZ/wr487xceFB/YbSMJVLvfu1DBWbZiKKmHZtySKqaPmrwvYlrYmewq8kVLCMcGJubM7fuChTmBJc7z6TpSgjWAhSlr1avQKX5t9E9PMYw9UmWhSDgWt3Z7C0UCrR4/ikbkfZFEpbQPNzuYIBHy8/TdefNBbOURMovFR2orCSUPnx6/4bEdanIOannUcceoT/sbmC9vuDaMyA+GrPKhO52vJQsWyaeLWbo/Tq0tVDtPZHip4w4erhM9hG+ByruJtYkNtJLC5GzEmrCBnllICHG+FyShdRU5If9RIeVIwrBBYCkZ9OTZRoGdWefzHqrg/Ni1rW9GzqgFa4jgHjplvDWY0qzLDBgd0dGlMkD2SVLqKM2pmZE254QmcMOq00HDzLunB6rnqPR9XQZenQlo+rrjcgUDfh+c+qI9ivOn1UbJVbigEAqoE3rk9cWugcXbE6JrrTPNIHEnD05ruBNeaPIfUworEra6/7KibyIVItW9hia5wPZeUxp0mYXKrIE6RXxuJ4S1Jd17K3TTcfGN84Hmh2CpdpWvDILlI9nCyh3CMvr54hjQfJl7vZXfw/OoeOkeojL0WG9bNlh29nSqop1cxuoqoYEhFztehZXnEvUdlsITg+QFe4xSZXMyBAAXSCthaOe4ZAquJm4mw5MZ/3KP4FxUzVYb9viXqu3Wj4TUNx97FUc5pzJKq26rbwbAAGXGPd7HkcYlEcvyxVT9DyO24Vsu/YjSVJzTwEOZoQgM3xaIPjpz0bHzR+53T9d+uFwfytpyv+b/nxO7nSlanu+P+VsmdI1VL9Z7xi/OwxwAXwzC6rQFd0WCJzVfQx8CKcMo7aPfBZAN1xSDAAwBLgPHi8b/zi428a7sjnOmAlEKIzzqImHTh9xTiQCcRe/6pEAJsU6OlU5ZDyEDg7qQyCyjwqal6nDdey7lgrNduRunOomgf7fjrL3CZFWwafYtVrHJs47NEOVfEYkJqqTsnljHuWDI9q9DmdSQfJXNAZUw51/v/77D2QtnnSyFTO9WUpxhRZRDyvrJ+UD3FvrKc/DLgm64w6n3T/kuCi4CG61EUs5RBm+MSNrJaAeZymQfT7RsPpDUHVHP18Q645+0n/0El6k4uTrkn2pcgc2I18rZ1bF5Sf6eGlWtXkx1aPGk4BlsAsEJJmqQP6ALcEnc6ky4LFqrVZXmd534uCtt3hsuxQT/4apAjgydJ4Cdl5meXJHSpCCcF7web79u3V43bjWz1N9hrN3Y/lc5aDio4g7nxLi0f4QdeQiVw9/b7MvHeq2nSCjDLW0OponQSVAhoSFpfnTuWWOigqWSeZpXRP7taZwFCLZs/X8aNMaSdWk/TGZPuGa2HZnnqHWYuibHPzyFNe07KBQQmoJIvE4is60zdIR3VXSTJOoKSWEmLmA1EU4MvZ9I0caNakendWKcupZY2bybGJamnxS3P9xi8bTy1n1KEVmn7Nb9sbkxubeWSzGl9C6L1g802f63prersJDKwEzKv4g3CEpmBY/q2cmDhPT8UJkkuq/P2Qb0bd5eJFokpCDTCUzctPebtXR1tGMyCBusOOBy37gv/MWEfVVBb+RnB0LvRbBifB5f/Icnna/ns64zd/1y9C429MbJzmESgpMC4fXT6bRrxhwLWIX2aTOorLYI8BQF/7e+3Ik/Bf11SA3bBcl18fABIdI1ZVD1aq83HUJHwyrQpVjdGDOETaQS7aadXEEDzxSm4DWdClEfLMTlUC/g477diBUjGJxswyore/aGuemBADdpMZfVrj0Syc0gwS8DxwLpOK9ydhMU3uvMHMk/zogbhOfrQ9DoLVPdSZ4hZKghXmyo6fqgMy8pE90dVdy+pJl4oeMOCTmqADUMCTtOmSY0qFWjbDXiNGmVIHVpX0xmTnptmwbGi7Q8SiyCP7sOvCDWj22iwiyekS6NO5Fv1Wqzegzc/HoyPzseAz+6t/8dXghWhPxD53mm/U+d0W9oqoZd4bymIteIYlnQI0T8BeF8Wyl/lt90alpkYe2aQmFBPbax1TWE5RAAmEPlsD/hXOm5ygxfTohXdT7BVRC2+/X7qjjiGg4WrgIFByYLfSBqa6k74ZYfO6flo7rfwWg/HlFvaQkU9f7YC98DB+UpHX/rUdt93qaU/OMNSTm+h2irw0dyq/uAYaois6CUqRg8assFOYTler917LQnaTomuyuuSlffAgWTv3sdx897TGfWB0fOU4LXLHLH8aIvaYw6hLMzeaWwNDkQvhJBxReAYt/Inc88Mb1gjfW7d8BW7OfEo3UU3urShNIBUXdaZvzh2qctsRzSpFlOLTr8iK+bbEv+Oq8TN/p7cAGUVxxdt+vS0S/J1Bl3Ia9gl749dbY1d/p5c0QqDZNTOKkaNf8CGEQrvW1tB98cLMsbr7wEHbsTW5zS7KOc6PM9OvH/k3eU41spTzZ0dwDeKBtG2KJ6ieZMta8Pv1yLDow+4dH/ihsXuM6AL9xWqfpejL9RPXCYoFtjRGWJQblvvZEm4rKqMUqeY2v87LvK9Flbh7YovIUGICORzLdncz4T7KiXjL6wxYjnMijipJ+l1ZrOdfF8ugaBXqMpvca9E9oA/w95J6RcjD2ZuPs+rBBg7wIfAuMBdTfZ26rpBDE+TZWn8n5UX/4FGy2vec2D+Sdbpp8FvSGYK0CDfGHRcmNCvJJX4ZCmIkTNfKuomlUtQYt0sdPlhQFCTKSkYx31JWDgpCR7xhgd0TbefJExOkU7X9neQXA4NHyao+3zINAgueCkIkQtfKujZXdFIk9HLorheHTnpDFllWp4BqTr0qsUMxWE92T80OhkpBdGfVSof6uVthYkIsu5vM8IsYrsgM6R2KKxBymOX16PKOjKCFCfrde90U/lpgTbyiV8GOnCxfBLAZtTGnMdOZ4SGy1V69aLiAPndCuegcVbEItOhYaIKHVdwBz4j7c3vj+8Zfv2z4TQP9h6daop4SpfIySvwOlqbxHJiewyr1OZZ2H7uplfPIdcyVedjYdperZeucPnFdHSO1o+dWICT5Of68E2UB/bt9B/xpRfdvG37zYs060/kpdxA5iN6rKdLM2kf52aOOVj0Ly59d2c9KgeQvLz9Fk5yXd8Q80jEFVgr2YHIIGMorZ+OkSdR4mYku40q3pwubN/QM6pBKukiqz5/zjGXYXjix1wcb9GhEZ5t1c8NR97KfXLZoeg2ZgLj/T+oGYlY86hK63QwhQVCceianSi1m93TIZDI/gSU/Il7Ly0XTiBlqFItCkZJdLtkIH7cu+6IeCqam0k4B52aB88Nv+IRm4EkCuNA3DNOHI/PLmBKOWUjuJBWIevpFvNphBWm8PrktZUWjXaeDw6WQSuHD68LV73Us1t/KVTQFcGw2iVBlcoFCGyByOBXmsq6RA2LfhoXfdQrwd34bHvbWb1lHBl7gwqkZblylYonxWqKig4RYklD5fEwEk7/Fev9R0MyWr9yv1p1tasJTy5nT5MCc66Hh532xYdC/bkRFZfnxbX+jncaVk/lsG4/mpBaIevsVvBq/AjfZjGhHLJWrVilax5TY0wFAC3DcxeroykPH7x36cgvTVC0SNlTLJA2VQlFTpUYMwgQEgAIMzQIC4qDIj4A2GBnOXXo5ljUWejm+ahwmuOZWolx+yu/1cffpl9vcpKg+TdE8Wdz2rNCwsl/nRkgXVdBlRAOdLkq4lJIuqKBICUbzwh2sFmK3f6X4uOoDdnUeypB0NKeK6kaa5lHt2l3wagq9MbmiwD0gyzpNIo+KExDUuGw7bGQrVMLpz8lPQWb5IIeK/Eqv4uxgMZXFT6oi0//YahZqsQshhoiItsxJSoeZM/b1uSc721MoG4kSio9qUZjMmkxeBXxsSW7QSWLYKEZTx+WMhkMClpJ5txTOhrFhMxpY2sPqeNK6kuoxWoaWWci0dbHKSV06+Vqy4a7I/M6wkBValGnTe1jJqaqtgyTy3QO1p8J5QPr8XajAqsqzj5bhqOEA0xIPQ9T36UP+CPBFs7q+F+f3bLlhEl7PH8N6po7fRO6wwCJtZtrJ327x2tAp3z2PXex49Y2BNw4aLrzaIIvzUzwRecDlMXsZwXgYgFuTChT03baQIV/9xkTOqqxXNqAxWscOMWvXhalDh4WyUTzfM+bZfIkcnqJdaOVsjP/93zyIZa15Arolaxv04l8ZnNUubLMBOKEYQiYlMZLJSpDqwVejblQ6AdZQ6J1Msdw+IZG9JcJ01c6lU0rgJEYqPTpPhlH9BRpwq657w0fP6pVqaG6V5hosNOjZW4/rb4/Dv+oaXCM6j80fFusb4RHdzT3AAcDL9mt8R1pENbru12VFGVeKM7rscmsMMRRBOEQUioaIRCEzGp2NLVYJBPe4m1+Na+ZdPGpGitwx4QwiBpuEDpqJ9P8czHLFhDFJOGxUdhA3MN/PxTGr84c7Dz6BW38d17YC/K3OHJnYPnHZsNzsXLuZq3/izQDr+SP6bfozftbyXRNmunNm7gGcd87WO+eMp407HK1HVtsAXcf5wCWo4FeIlQBdf4kXfiUoi0mD+PiN3I76snNu4vTEjsgvP8z3INDwnBwEMn5/dhgww4vsAg0nEFCI8D9PhfikB6Vw5q4ar/YmGZp7KyNVys+yzGizRECPYTUKL+wj8Hl+Aj4G099llIU8gYTlA61GHz1zdVp6fMrPmkEwXpQEFfvm7c12Lw4Vzl6Zdejrufuu9ab+OHetW7PNfNCB2lSwaP3EU/nw+qemwW87DJ1ykOWHY3Rtw7xKHaFdamY3hd0i9+klfgK5JlxAn05lfL4OH+2Q3iQkTGfQq67aXc6LPtguvtPGiSfSwd13Vf0fgNtnHaj0/jlqc+01lXaWtdAFV0busZZbJeM4mmQ4hxWXiqNfy2ARkuZxdY8LukLY803IPtyFjuUHiOrahbpV/ygy7hdbrkRsUlg+PgJK0ZgpCS9/XTifP5i2pz9U7cbbjgnK2x4K1nkrd1kP2yPbMx5o9661mQQl+kPBGq96j/Hg+M5oWdhtSp9OMoDP5c2raiO8P1pQzRF3aH2Goj48QZxQVUXYV5yQKzZUBfTXKbOw8+H+onB4yLSDr9LD49sj5Zr9vpdbEUH9Bon1MiwS5flCtFXdv2F7Xh0DAhoaun95mITmVp6TBuPiuFJSAa2bKaxSH1hqDj5RDzQD+BI+oRuPuSJIqMgtO2Ump3UUOUITQFWNL6x2eFCtPPR1sc6P9V/C9+jejy9FEPpzQ7Zi+M07tcJ1XWVR5DqhneFJAo01FKmQdt8pfcz/zkQivoPIr58DkV8X+hSNn7eWelzc2ZHUb2wLKgPJYZNHVR/jgFrFf3dg6AzdrzuXxEdS+zCIOzEyWi+5UooeomOhDkp5B1bMbchRurEDdaVBf1UdBddk/hRwDLixZzQjp9BPFPBppTmFrJnorByV6mhSlKwyd9PnBML9zMN8x82MZjaugbowwFtq0WFVZ3EFDbnKYTXmRstGC2DkCGPkWNsijFFhwfgWOY8qcvW3+uqNIe7RSeG4SBNkLuhsikKnqUUWrDStKUrOzx/m4yC9uVodhc90YhQ0cHVkm9lwlem5uNNwS9oQYB9WtYie9HatZxea7gu6x3IWhj4Ppj4vvTNdvY8mx4Qz/gvSuZR7X9wNmD4ftQytQ2/OIud9mKiT9u4fP+39dNjsyBD3xI6413+yjCpv3m4v/23wTRaEzRIqwB7LF9oJY09ZQHK2gIKVZnHpyvrcSvaoQzIPVdFoTkpX08ZufjXF5A4jrFK5Tuc+6B8+nltpfle+PEJ5sOrBUR4MEI2VsJEkPziyL7o0ZjG/zEWSk3pIFT75Ur6S2MUCZQ9JRi5MmRK16IBKNJQEgglHc6087gHgE9yreVzbiFwVsgFQr6siy6FWCytDzPNbmB0UzC030xUVzUxZObhSZme2mkRv6/clVNQxaTu+hykjiB/NdkoR6m+0ZDqLDYD5sD60UmVFXR/mrV/wBKXcQqx03/CETEKTsMjE+wbmo/r2LYxW+X48NZHcd2d9LFXmKWRENYt+7QRpRn9YF1o5/jzFglNcaU+8zYq6LswJYvWQQSEFK3BKq8G/7h4+zNRY94sMPoWah+X9ruEuQWsB/b5Q6YcFaSonS0G2s1Qq6EgHggOInW32ADvOesPdsZ2uu4StDO/UjQ5CqkKMCzVCJqnlti0ByStaWYWqPDqBE13ZqHRiGTZA5oe10+tkTe02c6BySJBaYQ1HRWzMcW7Im4Jqwd2nqeNTpDO1CB/lbE/f9hy+fVNSPoDfX9YAnVN7SuN9WkYduz1eLoFNCiPVMeO8Mj+dIxgjfkluPbRBghJlyPn/1lbcuaqdEx3Ix4eT+yUDEJzMI2WEyqBfTzsrwOqcZqZXzzjPgD9FWZOwrGoacurumWjcXcCBICFwmPAq6/RnB5Bh2W5Ap9nPMfpR7hIa25+dndTkxYWC1sTEMrPwqMWbZ0DDxGvNV44FOoSURtBlCCJbmMUjJ0/ownMCCTk4HoejQIBDszGCtpHd6ymc0oo6QnNWYaEjyBXXj0lynSIcuAAtd+d/vdzx3C02hlQIk0rn+aoe6qZiWyf5S3hN/NT2XPxXGGmIfvypltOFdgde5zs7mj76p+vIh5GoJ9+8d6oD1uBnw6p1joHXo/xR7Tnzbt0R82EdwgjfZty78dcCC54oaK2o/XL85fT9QdOy/IozmXaxhPZ8TVj4NJrw/BOs0+BsukwBXQCsG+PIyw+CPBH5HqpAxll1LhB3W6MeALTOoeiGP2zcC3en5nqcV2WQvPKpI3rqqf37ln8SpoH+JTab/8xn8iHo7+QGPjPSkSOdnx5P8p8o/CwohxFRGmqzUyToGZCOPARS589Bh52JZOGhCv/T4Di6nBfjs8OZcVfsKPlJmSumJltX9Wc7C19qiUebMs4HsXAMzhc3m42IyKj7U2DgRtU1jNtYGwSSAJaBI/dzZa8bAwFHvoTZVzNidz4vXJfjM7tisV1dJ8c8b8nU5WXAGX0eJjsjpLr+tY+dIgREsVHUw06fxKXNJuJWdWZxOO4sDXdAL8UV+aP+ICK8PL/IeTU5htPH3ZUgoAW4sqJ6Ctm+P5RW+tKxh3IPT39ZsEeNBWUwfO1ywhHD3aztKIPwkXxojvyDqiJ1tRPfDnps0cG463uiEdLOeTWY4tIUiAA8z0oR03EKjNyXJM4hcSLAZr+rU50RNZzZbWf36ttH3t+2oaYwrNEH23bc9xxcNZvQPu+dyBiK1EssdZhOxMrlnwAbeC5//Y+BgYcpDNyPA+qWPkbi3z/tGceh5oeRtDuofzLOA7okxhpPwJqTQ5EN2GGVywegNzdz+hNUrhFErat3ahRplkSu1kAN2BRxyVxgoNEYyCMuAMzTxbqf3aGNYRXoEIQ7T/qgs+LjWuWt0jVFZHdtjbGvFQ74HTX9IU/VLI8ovTV0mvv6U76GZopMg2MhX4Fnkg581AKzTOonZhzRP83qoh7UXHGz/QxTJIkMiKWveKbjjHv1F8Pbw30PdmFRr5nzb1oE6N4f82teT17+n28d3VhL4GAWjn5gxmH9ebT69w0e3+nFEbkj2bo4iDxqt2J7uNYSAbeiCbjEJbI6/VbS6PxcbW9jVklVNJWm0XTMcCXXZj2qTW3QrWM2hSqpiqbSNJquZ2S0a3QkppfSX7+jYCUxvd/LCfGexHJp59cE705f4fhaOgjO5FQWRalQd9qRDbZTSLkov+/MB6TZBRvMVEGarq9voCtYpMaxIU1lmWqfhzJDCJTFbKLPqh2aByOkBmqgBmpAgw/pjK+AYV/cH2r+5T3yL/2Yf+FX/kpq9Ku3T0Lgf1/1jS/BnEKw8s+f+P6xQELtpoarIBq/MX7q59znu+zxoIJ+SAMJi7CLqiw6qlyDSylupD2K2nyd08M70HtYTZwJbYTMF+JtNzkMS6It5F5lQalyJZcoLqKqZK2uMiGqfLlLXNkSzxhbgujU+oYDIWScPGQGHvZ3oDSiC9W0pFaIxmEXPKQZHkMuStSKoLIsORAtjt7WeyAKuELAimLw5ruUr8PXGubmL3StgYof+NQ+dSwHKCJaLhARxT9ybfhFd6g9jBNNTz7/HRl2Rhh119g7gD9Fy2SavlRYDTFKLARrFr/m9vl4eWiOdCkoRh8V4KQu4JeEUACGCHOgAmGOz0TQF1T9kTnUnr5xY3KhahnZh8hmCEhIrdgfecHT0CY94Xq9ohjc/ZpoUVVjqFxQFfqVi6dLKE1zA21NwB46vt5IsIQxt4mB0cT0BRS2aqcOgRDrqdfiarqOhkhMhMpWFOu3vySiJwzqXH4mmiH9CRepHy60KFJCdMvK+0V0PO5uKR+zpWGiXCLb8CIDIUdWPQ4gai5X9zlCuj4p8/SCrlcGE9A3zBoLdWUEOiD2JVDztcY/eEu5b/0W5BeaVNk70Tw1zYBAKVpiKck2mt8sHoVtuw7PrPZuSWK6nA5vX6nQYJ23IYnVLGsQ1kLHe4qRJr2Xjody2yaPrDuDROXmOPVEcFN2rRHH1v9b4UPv8P8p5YssVgUtTKoNhUu7Cny1lWZmgWhKdkXAvSTU+w9yZv3NIQ9EpJqaU8sSo3pVSC7UkA+HACQC8agZF96lqd+SxI+fNRXucGyDACVBSty+OaMlu0LPPdv0z5JcjemN2jxk4dOkd7nSXMFF/PnLhchtvL0QQVYjcIjV2wR8FOO3zW8fpLKaQ/oWScmxik3f5kcjZ0L6OViIBMAh00KDPUL5gRAq2JGkSuqG6jvCUQ17+Xxekapb5XRB+iJKxRwVgCKpOxxuUrBb8FtSJyYHSzzwE2klj17hR1YXSKM+UFIglPgYvyhZonokYyocUy6iiLXh0k9Di0s1DYUwESpeUazve6EjshzqRJaSvQ+iu2YxFe3UV9g3Xl8wBu5/O9VEjGIp6WtHveaKx2aK73iTh+DhMHjTXcrXnu/7Hebkn9G1CiZrbe9iDxjZ4paquNVsvKC5tOQymmuIXBjcf4/wbSZ2LyIfN+yE8OriCwH8PrAxl0qWtqy/eTYt6xg+ChOumLrkQmKJapDFCnhJixJCjje/9g4FyLgyu2Ob8T0N+obRUlW6uOXgfNhSg3ckH9XCm0ohMjC5uL5F11X3hvuY+oYBV7VQH4lnm9jv+FsRmCjmwbuihcceOh60bY3sDQsMvZutOg2WdNUv+IG63sz6gRqMP4AZ5tp/9Y4JwWR7rTbWddvfbs1a7dxdJ0tH9yWfU7auz/2uKML+TxzHBsdXo42+fNuULoWq23WuRlF6/bhqEc2ARbJSDwa0Epe4L8GNM31YlnPgwstJwx8NS9D7AcjFC2WpqwjeGiNHeDzkWxcWWOzlK86/S/rqUc8ZSbK4iGrhIIqm/pDMjxmOMmvx/nTn+KYrM3J178buOQSDp87moHt6U/uQhb0iob01TwRSEpkC2y9ziUrzWxjEibPVBwoKfBliQvJaMp6f9vXb1VxpWjdKcgWK/ZJAKcmkkEYGmWSRTT4lVuwQbTqs7SqOo/ObuNUhORlZkw7SLB9Jq9MJde3yJ5Uv3oe357PFHjoxmw5k7MGmswLXd03JIb4nCKsyDsrF96FOv9vL+P/072JniX2WBUPBtnYXYUhtjbmImbN4GFbyUD78OWdjzLhXCfcM3X+ZsEQGkpE1fHkjTue7L6pVvTud7QStGQ+6eJjU0FYXFyTZaTQfvap507ntf9FyoM69U5ds61x9DGmPkjTZ8QUJSpOusmypslSeYKzW0cDfGjwNos0aTOXHTl+2XYrcCqINFiSs+6VYb7mV1rT9V+LacUYDYURcDQTNT+MpSItDpoHt54j2PrK4uN01y0fw9xHiPf1nQumjK5B+opU/qXddkCATnTcMA1CLQOZnSa7vYQ/7WjB7t/wFe7D3UAhPpiln5Uuzhur7QKpyV1xq45LRhz6kSx//tr/dPOVsnhgihcWvWvv7LvNGnC6w78J1ffI7NtHHCsKvirFnMQj1EIJd6OIU3EKPXoxwf04DSgv5Y7Kk7zzni/0JSD6CS2ptnms2TwqRopwv5zLWCodheOSpfwNXxUevrDitV78TqvsALLzjY1SlhaMLnH5o9LM5PDTJXTPMfTRToUD7pMCEsP45euVH8oLwVv5PfGmmF9Ytr80PqF7jtrhkLnICHiSTSr+ArdAdMSuKW5defi1X6gvUZOWV65er9IfXZNVV76jVJ6x3V1/12HVdckLcwvFQF5+3tjJ9DltrKqJ81DOWH0eXpV+7bLnOXGQs10mWoPrqCbz66mVJVm4ZNhESD2XoLWGR7WRHxCE9vbikKXwDehIpnJWKjyMriu65vPzoIz2QcEhPobCYEMfgkJ5BYTExDOCgzgxQqyHtkJ6JMHdS3WTvaPiiHnZEmSJo3QeFIRHeua6LTiYhuudJhNzooCissdjsRo2YWTqOEwt0QXRvJyNOs9LzCGH/RqZmpRcRwk6Ft2api0NSrYaNQ3oxEO6su8nR0ZHXMAUQWfxgMxg/yDbpKgnPKtH75aXFqcatWIMzUhOR2JxyBRgZhGmcmXJuU4NhngpKX8DX5r8LTQWIkjV87fvAyNqo/7dL01USh6tEb5eXhtLK5SWJbUapL/LVa7vXPPUK7DuvhIfEbnWP2AoCn2XVOl1QxI3VRAhgBNXIxgVSC4tzJbAmzHBPoiJvOTKvUu4aLCn/Vy9BJam/34h+TfVp7NFPrOl9xoJ+72d5PdwxXWHegzvhsXcB/59LGPYn/yxA+C+re/asSRjVJ9mX4Af/JCBRHngigvMQHfM+8WRasOhQS4j2V3OE2yVB8HRcI7q+uOJSWAdY2TYpzNqHMxGZL62jTPJEBzvYHY2YwQgbYrFYiSSBLVpprikCpXJEWgqQQBPJVT/DecslwYKwzUQ0vqNC7kdD00Q41Q48h46HfNvmI1u66SVSVqxRQmTOoy1GXWjoD8WB9OKav3gBNsxRiuWuCkfpWkdx/IaIgQ8UDz1hEmI9zZenGK2+nHBKcYso7IiTh0BJaNTcWbkRleS0DJ4lfeAYSApsC0D5UG5TG3eDt9xARqkXy9fyFuzWWUZycQ2PXYjgodOM54kPv6MZkyPR6l8SOEQ8jM+/s9nM8JHrfLhZ9+abOvQBNJ038zOLPZdW9BK0yxp21ndBUz17ODtxI8Xxca/TldbV9ycWpzMNdcBFBikk0I8+9OIObqrOzSPZpuffPLh1C/RlWdhGGN0tff1MhFH6V7Yz13r1zKKSqa/ewr4VPdTZZlEsVA02zC9wDTO5F8NFXaAzvZktVmtpWd/Su6t9QZUt/faW3hETux6/8g5Kx39lEuep0tyHF8Y5UJYPOzZ029XO043IO574kqFV9ADjwhjfUCpTYaNpSyMeAktDXKCFEQ7sAhTMC6qOoK7RU/1mc4xudacAqvCfGeAGr+M2/0fOzUWNa8/u7FJcHmCW9+d+H2gfDx46FCtQaLl3B+4wgGaseDvX/uvvrE8/vCIvrDz11JctYp4TI+jt99z4Gj2i+p9c3sgchxlNT7CfTyMotQgrW7gy6t7KcULAG7OKt4BvFbj6ecbyqHgrrOZZ9F8FX/qT44/hvBj31uPYs9V1U8lEGURDdoMN2ojJIr+unQcy6qn3oWTpZ6fenjVlgHyC5GyAMcD0mEE4ROwIGU5iCUxNYylr8+sD7y2Sw1uoK3UCuJlArnyl1W4NsGRzAGo6VlAFptB6aHBqLBLsdXrdc1gtLQWXjvwSUwC+sbxl4c8/zr1HM5Kx5GQLRDKX1mQJRJcCJ6uV2czRg2Lw0rmZrZhMXlI0eoPTUzNYHszKmI8t0ByWFVHLdUrSAo04daunA9GlBid1ZZY48izWXtK5dGGmP1N2957t+SfKLLPOk+UJ9HuZDpWtsp1zsvgvn08kE6HEdXjWwc/1y0JY7p+fxlFxMMeauQIxVeoGBPz7oJMAQFIe7ZP0JScbITJ9ac0qgacuBU5WK7OBowfF4KVzM1VMJiwpejXH2YeIeMSN2QB80GjPaSLPeW8LoDaeuw3qr4ZYpeSMPI9XGW7JFjzbUS1w7XBHuc9XgNLWSb1zAiJsIEmeZ3SkLROavxFYeXpaU5yNM7w1agJZOu0uqhPUbjBJ+HeS3Z1sPuWc7QJ4T/Rfk5C3lY08SG0m58G7BHzgRjFpCuVMGTOMAepWme2MKaCDosqDLywCO6ZdzdrFBeDiRKhgEvW2QLDzvZkHExwuFDTZNTuoCG7iSwFqZltHBIpFuae+YNrzHgEzYOZ6JpduSlRfvidYZSQW+Bt7imafPl4Pzg1Bl9sEFEtCnGomhteD2Q0jEY69pX6VcXvmEfzvPEChdp5i3PHfIY6bM0tIKbL4UPGu+d79o7CnEnvOoOw6O7BqBpYY+mDmwQ2nllzQaDufMyi5jocvVs7la5NEynRj5kiLM6vPtRMrp3kvk1A0pzr7hzOz5ntBIMOyossESvep1FcESqN8z32i426y2pg/i3PJtUMgiicUr6vMP7NFnLS7pxNV7J62K5RLZE/rJsoCUZU+Hojb+DdXvs60tlogFTNNzRRR2HVyrNHa0LdiWSurcrT44jAruReO1P3JPTgBh5K1ExTBoD7RSRvyN2/kGCvpm3ap+FWxbQTZR1rhzs7vaGHlY04c6fWbRdhznA4iw7yXBad1VU0HYjh29oBDxuTiZOPwtA7/Rg6f3VxqXzP3gX34wbcki/BaS6PzgKwJGiWXmUCBcNoeMDPO99W4wkZH6v57sotndj8xCXMLNMkuQQixHnvpaXVcj85kEU7vriZ4KyTjXO1oYZaVGVKAdUKRVZtWdtsT0bTP9k/ZYiaa1yrtloJgbv1jtflU4JrSbXKftwTQHfQLGfH6SeCLwrJSpyJB8mDu42EP4IaI1chGFah/sueBaPJ6MxsJRgYzaQHvU6AP24vOyQs1iC0RjK0q4rzzISoipGvQKnZuiEjuvjHudg6vWpUibeWdeQU5hqQah6QuOZzNMNgqHRORlmXLdqVOQgpkDA8/1CiQ2PFewWp0iXg3qcsvhzupdbL1yV9HR9igixGIBTcTkasKzqjWDUas8wgpX5VxM4E/FKLiiI8IuOQ8ieUFozYaM0x0VXhb+wUw1KhwCJGVsYBgs9XreRcOJRgTOBgu7DDTAYk4cWRC3VEzy3x0mZ5U+KdakN0aJ5A/mjcLeUWTh5Cp16dCkI9h4DtjpUv/RBxN5dpL52bWgV6VjW5LGJJPZGLWuiKM07f/slWbzXWzNJ8s7TBlBN5yRVTMTScNduT6JAU9D8CQWuVaB31NUgvEAkm4CEhyb8wieKu658JVSn3Zq15Zva9kqyalFs+3Pd1uBpaOFau0/q5sTAGUA6mFmeFJbJJrruonUF2wI3lD5tY43k9u2pZkrUDtZmNfY6WeRm1uz9K0MogHxNLtzePm7IxsXRKwU9Ke/7IxDmWVcnFCRyZreKNTJRoyZ4xoON15MigXrsldLTw1Mm9UavePWRexdLa3BpN7RkBoGTRQGihUDReobi/X0jgj21i+Oa3hgmKcwK5UlkasudsTEFhZs6LMKSty4Y9ae+7Yqv/fOOe1uPoatP23uj6L7KK775PLW1XJ2sHHo/KDWOTj2O7kPY8M8F4xcdZkWL3nyJS7Mwqh0eBZNjWCPW/gCVimoaET4oMDye2b01OvuieAJd8XOLk71PB0Z3L3VslFHxoXkit/6FU1PNwd2sgTwuVYRE+qh1pMUo0NpOSJStxmN5W4rSHkntTZLJpnrV0rqnaLlawbDy+LdaxoldqlvpDO2KdfQZ5txYTq/Y/3KqN8qsmPyruzdxSB7YcR+M/Fth099T9r7VlRUryBf7KV2hKGpzXxWA4YLn6d0lXM1BMpiQf1lxT8qtieNqwvnHXkq/wN3xbgVrT3uEBdylouvK0IR6+T0kt+mi3bG96kRkdbguqOQ9xZzJSu1NnJJ9YXnmnnFcd8KfU317amnV0g1ngJHcHagbanvZEP5lEtKiGtY7J5tXXQmV6WpumTpCkiaj4tR1qG5pV92Fv0rngqDfdabri8tG6uo0IsTilMOvD3tbImOloAP8b4Kr3fk3qqU+H8FU+SJXqaLXCiLiO1pSSYjdqgLCR9N1+RcStq9m0zFt2Rp9Y9HQKyg2QnKqziAM448AtqW8aUWAF4VevEtLo0EzEOi6RqmDM4m7DscYDoqEXbjfJkIa9N9X0gLaBpsYSh7ukNn+jJ2CmnA+2z6wo1x/yIsxWLD2fvPiOB5K/nV70kP4R2Kg1jzSYyUO0n5AzxXGR3Abh+tmp0wnAVqvZ0YzH4veHuajPT5lg6THOM58qccLcnt8k5CX54egXceW2DQkYgLNTESGoOrDREAthYcRHByppEnDCTiYleTsZkXLJF6QAJFUR9lemV8aHbm/X0KGol9mpCzucbYsntNRHGz2TvS8a7q2zjgLifrySwtwxCZD2u15jw7Z90AzSjvZoi+NnR7wnE48qQLu7GOm0a0AKD0//5kNlUiyPUpw+/Ss0W8k6sGUA7V6gK26iJTv8hkPo/o1qBqvR9HOGuNdRl7hnUGhNWq+Bf3bnTE7M7c7lWwxkqkeaolyr7zEZv5AJl69nrdSaey1OyV/trhIf5Oc0tSDXHweXsaq2FlkSjyPasaROyfnuyvN3cZ2eS9eIqW+4qMz0/WO2NVHqlQL1P7z6AU8eO7o5qU4hZhPkUE7OcwFxZKpeQ1Q3GyfkHrSHGMrD0mQiDH33d3rlPaHnJgvdpeRx1EjMGaOulvAdZUtmKQrrYEs+OCVK5JtJLB7UWmT8/YJWxWPZwTxf3QDAL7FpzdTyJs/QkwpPcVVA91Pl+HeOLEdUMhjmMf0Sy29Rk+qqyHRO2XpnGS9ZJ+D3tOfxS1rz7Wu+dyvAJ3m0utVw2MbvCpWq4yV5zE9UbSvb1DuQhX/9GRWHSZg7k8Dx/vfLV3T6zReDyIvYTlImC0boO7JxBi+3kuachLSKQFgSV4Tc0Uv386dpzHOir0/09fnGO8Pm8niRoxTLXXCSnkhXFscii4gJcB7518w575tX9TeRdsJzoxqGKeBbBomWP+AXlFQP9FZJXV6s5pe/DB9GTDC1EyMWSp1ExLttyBP5ikjI7/LSZN0rtUP+/8Vb6FD0LuuVPqsxCuUUZvKBkAHthZ8Rkbc/ZePOhsoNE4aiT1IGfF4ZzxUCh2JMER/ZahC6xMTrrKpnPtScsOVnR9Iuuh6zT1QFMbSY5z113Lk/MoQzaDsBZtnyq0hNVIYq5umGha5oFACBAW+5UptOuFbrO8AfVFNDq/Dttv79A4PQuVUOEoNUVilH604jF+6eN7qvnRreNhv7R0fCJNk6ayayjMzleDt42e723zjPzdQVefhaxCugpJsoEPJ3qsoPoRGiAb6R6fKlhKQBmVELFvSDCm/WMeEruhvQ82hJwtlkURvzlY+rup7PO8n73nTR9yFfgRf5sIqkSQmZ/f6paWhMgtVJicvahiCqWLIpGquTZ5WPtfgfvtl7tZX4cUg+JAbZUpQOSQRHZVQHyNcRsnOVCYVwXI576vj/DPsmP2ACs9qYU6TulycLCLy5Qhr9KiFAx48gYZpFWM4eLmSGHkoRAnUVVShITiRYMcjJhA4uDHJJxhWVVs2Msic2JBV+Yq8WahAKTaz0wTV+p+UmKd3ZYu2jJLQWTiISItaZIitlxEjMeA4VD7fghIWxSw2baYERA6YROqEvYiwrdTqGSHai5lLmC3sWQWFyrGWStWE7UQPiUYC7fK+i78PH13dzyNtd9jnXu2LQfhwDxICJciVK/F43cKIqKQAXACTM8lUj113XRYiEvCy9kc9ExoV7kRSgYMnNZDt0lliqycBpLeY5Aa81NmoJAKw0n2TmLBxbScEN1Xb5DAnBSrt8XA8CwdamkqoBBTKAuoihIcUsMvEaAj5RMLjm7qw6LzqTz7p1/f0287WxXrFfVpSc1JWnneviz2vSDMafaAeaklKxyU9equ99FKVj2qiGLMndGw8bkZ9XRRBe4TfyjRDIXDOzUM9ZgnuX3Pfr8HbjNucLuOBaMaoCj6WpD/Ok+d9zzPpBc2fg5CW/0JIEHrMl9lk7aWDL2WiWrXFGuGsb+FaXTsK5mYRBucVXmSRRUatsz7c2yH5Fp1ggZHNFbIdYu1nuCCqw+JOI7rt3r0iG0LOFVLsEUUb9eh1c6nnNcicojmvyjPkD3/v5FsvEEHnpSqEc7RzF7TnCS12i8HW8Jm2X35HjFxI4cUJqzMKBoNQIzR+LYKRITc6ofHTmgVF1TN7g+cf5qkD3yc0IeITTBN9p2kJ5UKYeRhRAmLQSIq1OsM4h5MzPRlvM5XGeAgIDxR8gfZgHU+HjgQ8SHRSqT4MJjQ/aoDcCJv16Vw5NjvN300EG4oClVGZ2T4J2f5B+o554jRjyvDbeuEWGVOtI9i0/9jl3bMNREL682Q8Q6VY1ax0w0ZsdVspdEmigsXYWUWItasgbq9YfEvZzykxcA4/N3D65X/qX951Up0x6jqBDuc3Lu1zkpB3gGP9Fwu/klu/ZyKtB40GRZil7Oe33ibi6+IeyCxqBk8v+zvo8dRVlI2tEIOfy9DPM0vtGJEPtH1z568JcN/W+fUHcvvEUIhh+AbH+jMxVMWfE02/9HvffwLK7xkxFzKI6Z4SistdsD/h/Cnud+sMYTlHZsAfRJsMjzYRBZemJLg0nmHntWvGqhy3gEfTTUTgyaZL4jxd3IDVL3czx0ZIOC6EC5eaujleOMgYFvxID5vRArAJ+7AeQV2P2FuCwGYDOaB44B0rakJP9ob2Eb1XRVmvNWuPEJdmusIyUuk+NFwTm1+7V9EGIraQ2ujoDb1zVybbyseB7gQ9fxjgaGxiWA6cedu9XwpkbFJfLB6IrHLccvbm9F9Ptr9AbwY/PbnA/z/Xjo3aHnVfIeKr0C6ihShpbm4aLZhE/W/EnhkFobdE9sV2raWNoary8iePmxyS4Qro06BqDVAkJSDwd9UcmkNmlQTDRrftDjt7+9ec5c2zwHoaOKlGrkTqd34BJQolPka4NVNRHKqGtVvRRsiAr0IhAxUoTM8CVYwISElv/b7CsvqjweUyyAmxFQkbnSWPm+NTpT6dI315+Wy7GYSs33u3HNq4I1W1ZQZUCgaIt6qRtuvZJyDeCNZ0vGPvzZGXtP+LOGAAKr+Np/tFcwjY8lAM4Zkj5yyMD2v1tljdY77PNlv5NCN74r2vOZ0or/hmcij3sAI/IH0QLikpH/8XYgfg0QTY3+GJaVAqDSfGia6mZHZgAG7Evfrc2aE9GsH+OPaVOugx0xX4M67GhO4zEG5GOS0WlRnBdDOWqgN665B4zmSsR8rBGW3nJHo5YH+Eb9Xb6lx9EUidslqT8iuOLllSUAwjK9GPo3C3Zsreu2LKnxJLjoChDS2AHjEfr5ictSn49r0plyaLrUyOqnFYavXMuhsdbBCxd0LlJ7iNwzYGKs1GXXcs+NRBtYOHy6rl9FkZzKHXNSJne2RppEEvLtCRArXdC15CXD4Zw7YdhtbEFghAC17O0DoJti+PGgNEVORC+1ntzam4wBQ/wdS58sNxoQ8prPZx3bHrCNA9LgiMcmm4W2Ax6eUCOQR8zUWpwzxvN8TW8nLAV74FPYC22UDG7n7vEPn3nfZsTPXRqUF+CuTR/A3WSSZas2mtUhUGbc8oqdy8qhRIxNHHmgO9zl808ofDAjbig+KRc4D71P3qvy5sHcdFubVBn9k1WF37nMc0BWU4Z3kW0BWIT1aynBC0Y/tPLw99ViiV+5kHyB1o/h/9GJGNhI+KoY/2kj7HUOdMHgIdS1VnpOkbOZmSe/vzyTxp7kKkKM+xMFimlGCdDFNQlSdbbHo1uMQafkQSFO7lUE0zuP+Rx1wd+fmecrRLhWZGXsJpPUDDSpRLED4XWnEjfZA4u7FrLLwMHIKUCVPRM5RxYRiCuPtNkGbXV5fNjF/rC9L18HuGkWvdFDu24qe+R7yYFeIJib+qjy+RZ53zaRHQF1pxTU0Lg1+BBeniX+bCBWh0LAkhkhHZDmg9iI3dWq0yytsHtFgzRj8cf2hCifdnXke5o95DRTvK29nvWmboKSpyvlqjEAJG3YWj5Xk7xYNP9Y1oK8LkPshysQteMOnfAxxVlb81bKpAXPK0GoGsLQQK9ytlojP5vpNJeG//ycqE5HGluLGdMNYj2Tl/jQD/mhigFX4fJ1n7KmFvNC6Fc2peU0TgAlS354MgnhAoZ+/XxjvZpZHsyaIO88E8rZ/ecMMCVyyjVIxRZsfIBD1pdogwtXbWqwdugT9cAgs+/5H8xtfa4BHGbID/ACTfigFauMhsxPQV52ZBrqpQEGLE3OsixEBlzZbDlws40phVHKshHqBl9xMb42FX44jD7Ag4ZH8X3nGcQxqR1SMZJ2zW/ERlc/U/FReVd1KKfQ6pMp0t10cRO+STZUKcKYDZq6CUDVupORgJCkPAz5UZ9erqKoD9CX5tBeY9wf8nwK5x2ENnCfTW2/8B8XMh5JhoTJmG6NU2xt6jSsWTNVFFVcuJQbY5u8aEOZZ5gAyGadSrdREwlUpOuEDDRC2fn+XzxTF8uc/FQr4cjUcu1dM+KvEX2gNBxYVXzFnCGY8ClF1InJVA1dCbfo1VtKUKITN3Qxdv2Kqh2W32aJ/nn7PPFb4V8C/8I1xSnDKNAiZ5HrCGwYU1DAlYLG8bBSGQFDs2tiWYa/o07o7yhzBxoU3U1qNH4CwOTgunc+oeLhYqb9rkCcgqVIoVWIaHfyZinyQvf3OnZ6NBVhiHIIrmnNFMMOj1d8mplegWftbh6UCUC93upoY6J22cKdOAM8tirq5RLc9Hp2Lkp3l6zTfbmi0rR3zhcLTMyIF5iZTdszQeIoGrhuAItZQLKPDZeMQDddzi3Pgi7b9h6oF0rmO8S1A/FzRVcgiuxXQ2obuh6FupphiRC9XGl+kqd2JlCacFbN30Ts2d6csN/Tl1NsuqTNlkFVd6bk1kqABcRlA1NoCtV84EpCtcZ6XDx+GkvAXmq9WuX/DpXnpjQIENGJ3GCnupjq7LSinBLnTNNwrsPeVkwJET8YnhFTw+fTdUkyTLvdwSCcYplLeFj3D3e7HUzjoKPqfW2fpqOb4TDMG4+fbvClzs9h6Xr6n52d9fvDhea7wzxded1+QERRyasTt3JMMCYVvYR22276g8HtrflwuUjvDuOikrxa/WQp8mf4fOW5deu3L4LnhfXTK93hic5ucQQESvzwSDjgWbhQz+x29ruXwOtf3hobGd4tiUGacM3D6rW5sI9/3yy+2GsciL8Au7UIEhpCWd1c/r0blsqOQBMurW7MIOcnGWfsjPiu/HhpJrm1VNtuGUSGhs0R0/0OvZKLQnxwi22PuUy1x9Mh/tPbwI1nEnqzxjvjGL+cUKfVo5vE44Dum/c9d59mrxdvty1f7nYJmfZDs9nv8Q8PR7fxWtHekFih/nN8+4H37rPczaIg/srIyOG7xIOr+xPLGUZUfX1WL62xj0FgwFmRzVW9A4jDvwvbztaFy74BWMOpg6sHuftUyVFf/MoSWmnbx9tra08iISARbvLhPEDwuc8q+3K0ueQs4uH9EASOhIGlx9cUwD8KBtT1fz8Hz2uBOWM4usRtb/bY4d9t9gvMZdy7stLmiCIgkRinGGpLrXQlHQdn4HcRvbtJfHMBx3rh3EqlhfFYq7EIZ3J5trnWKofJVP0HLVedC88TI6NobS+BaNted9Jaj1rtkMSPcW8B/PtKurILeMS4omxC19l/c83OTNUMApRcgmYxPL4ByKh0a6BuF/7NxRVgUq/PFf3eSuFOkjtBrLgB9Sx3y8Hl2JH7QrLKojJsCz2QmGNMygqtL3PkoGXJypxXgXQRA6ejxI00i+L4x+TpeJCB9g0b/ilbrzdxHoH4prEsoCoZMV3icpjEmdgLZoQey6vWUknOop2fNO/ruQJmbiWKS6EDLQP5fGnPotPuTNrjpy8T4VLWmNaVfpA+0JXMUIxB7ZNAG7uIrcXiBZUDPHubSj/2DcG1T0ax2yirObhgpwhPJ3wY7ZtKJE+6Cxb6vEaSHni9lbEfrOts+j1l6AC2VdQillS3teWCK46Gangp1g3LhVhV1AzZ8aLW3vzqx/mGu/+Iw0FA4KzHGkyjjlxM3eF2Y9TpXA3iAb0znrXzfs7YaOzOztoHmN2Z3j8215sx18ktsJuojg1AUd96wwPDQwI0uiQoxj1yMZbNxcwc3OeOnbw9tD8DMIOjlGTn/WCsP7gZIQB9rhKwznj7q7l75gh9C2Zs5893esJ43sON09Xvv6LunnbdTKhVgpenf/7jx1RDbE8XfUncOjnwDHyi8T7d00+phdPIzxY3EAksP8ypHN02Ty66XQ3x64Pd1lPvJCpDttsOt7zJYO2dSV9YcBlM85EHXOJgklH12ZLqpneuXMIiMEtiYqtWiqrqRdcKem+1qtHZ4FuJaXDMVOVaXRj2zaYN+DO1//V1Z73pQoCvchomZ+fUuJH8QS48fa4sycFMEfCpPukrpTTaPrhbIE9uqw4dxxibEqlpgh0odeNE/dk4EB+lqpBchUxGuKlLMiLV/cBM5cSF+BxXXZrGd8PI+wQgL49cpEd8a+GVbho8aDJqLOMNpyf74fMNuT/fWzES278X7tbfAxOB2hjB3+R9EHQnR4MzJRmDx/ej+GVPijjgqj0shGDN0SB/BRGrsrNRD7cIrxHMXsV57pBwxLf1qOO0MSEYjY2tOh9mgQ0OTw4MGWbosjcaj+G+YYarfTm0yUR378ryT0ZIrBuEttEH164SZVwXJJNGsczdwzaZfKyABRFHLJJyZ2W5G1PDLpNbvx2Nhe7lkNZGu1OJg6h4VnOoy5DWa/ORUR9CCOKpAJ9kJ35xmIzH5u7+m0Dve1FkemXvrSu9aqxSNs3k1fe+4I6bpbvtQVxrw2p+c0MRRZFXeM7TIU0BJVh0p57A+DMoGrhHaxxAKbnft6GR66rTxe7RmR7kC2j539e1CMAcyDl6xpd3V3+8Ub1yfc66395i/R1E5eAs/oA9peNtDG/sTizZ0Cc9rJYzjffF0Mrv3dOfjlRDrsr+L1q6A2uYCshSVOesCfeMjGoFZ5juKRmybCL9zOueWmvqe2ErAdQ2Qq4ML+0FQHHfJa4KIFHt04KVL2MOTmy/j5izAnF99ZHiVBh0jTIrISXUOGnpmde/efiwE8o/4f1uN8HV8p942mfoht7mRsA/hB7L+OL17OvNQcZrryL5sddA9/s0SjFAd18dMeBXlNLo8n6nTok5I1FrexaVx/hfrvx4V1hVilpM15h8Ap6l1Ol0R3QxpHEuHYVI1Rm6yGeEY756GVq+1V7mRy6AGUSXUGHXe7IZuMYgRE4wwfSKeZVBv3dxr8bRYxPBaBdpcyEbzQH4ukk512/TYI88gbEh9DR4mRMvtatxdFsQfa3qRbODx+bhGVH+umNL0mD02ltt9932SHwWu1jMGn+k/vzzQ0+wE7UPH0YyYy5p3+0d0BiZX+A/QD18hv8CgMAv/yaMYPc7xmKInsXzDvOsM4bv6Xr3mA2fq3TnTfST9/Gz62rOrFDejI9FlIVo+UOJ/Np63vXuDUMlDSRaon+mMu0SibR02aEAYgm2+Bzrhvl45gBTp8PWM5Knx4f6yVaPiZ+2460ivCgLHpbzVU+HVO34yPAxo1W1tpaWhxGxrWLuJ41SOQjYLWEtztCIpudUDttFCRhyZHCkBYc9bkEH1IWKKQguFDjv4mL9xHR0OUR+u18dOWw4bgTZo7Hi9wQiSuBDWXpeh/5bljtLWITDftvgWq2v2khtwA4FxM8Hu9BFVeF3aXxxNgDS1Gq/uCM9AgEPltlqFgLlWRxNEbU7eFmjcFdi1XBqhHLkyidhCgsmGGlHjwndv2CYh/AwlBjEiJ+FSU4Iw7i1zCHHFBZGAbW4dv9wXiFWa9e6Ja8YISJuZ0YRtbPwksTXzw9r21ssHxi6WEQAmgrsI64c6FPaNd+cxohXXeUxNSzWan3f0pjNZzuPrMYR7HZ0Fas0LcwrcnrbybpAjJmYcwQG+6D7/K5ULqrqoFkdXvbBXEJaqIZ8LJEt/QV1nEo0JUDPQsuI4VO5lLnIED1dNK7cuKcNsOq2RGMzs0/68bVAgc5alet493KVh3bbXZg+w9zsF1Me1/abW/uhYDmlQtzZckotaIAvkMlHlGrkwrWbkR6NCtoHfAI7BIj5NFfnyg5PWYbzfZ4t2vD1o/loW5ocEIpVdNNgozRJOuT8kdPlDBT9ffn68NY1ngdLtRpkgy1EaFeIUC4tpJgPvKytLe/7ZJqqxeDYt9vWVpdDtsoGkCEhgXYE4V5+l6OIOmaqY8Jd1xeitc7MfTfwa6/4Guqa/TDh1883ftTWUuxOLmoFKcbUAQ6GiwIXgcX/9cyLTkXCtiqjrcScFq7FkCj9jWj7qKqbXqeK1x7dJbcwlQeTiNKMUBo/rVQm7gRTUQKGtX1YKXzE+hToPGVy2/oRjZiG6NS5tHqia83wOms1j7Gf9IhoqJR0u6rqpl1zKahwUuM/epD3Zh4Nwkq/7qqTtzgmXiGxEz4/pllBCLuhuelQSZGasA0isgMVQiCJOntPI+4Qu072bdxgLPBrzjOXnW6q8+ohhmdMlB8u4ytHpOnSAQwIFd1LacqUfAHSTDO/4geV93OG8ENfUScX9pk+8LmeKbaD+NKSm9Xgnft29onn68l0KWJD+gVr5+eUNg4JaYYlt62qTSycHNZ6bPJUJ5DAtkWSeU50oqsaHy5RFKZbO4UFJhASQSHP3qbdprRP3h7B7C6Fpv9UpVxvEbuxxwYq1kOfu9u2Q2IYGq/M4c3t38dpUY/R34cNOI/8qkGvxvObC6tCl1FN1y270gW3245naUVzV1TA3svVQak4rJa3rjLa8O19wTQGulEquc2SyzxhhjFgsRZ/5bL6JhayFqBLktpwOSw7dRYEd1BEFVFiKq4PKaKmb+G5yoF3qRk5WTCPGQKnpbrxzfIRhVrQzqPui55Bq7xeZDpSouMfOA2pfY1eDzzKLvQ6qnRNkXknJpJuL2uk2xWP1Z5YUCvTX0z+a+i/+8F1t9nGsf/tS530J5/4Q/z9ieBnTE16NsJTDJVByB98pV9zcq2qrNXvs5X7PiGPnwLlf1LVnZ0yTVO2CL0iSTFBtqpXEmghBQ9TmDbfE3Wc27lUI2UePNlJL89Ea3lwJNdE5dD7hF2v3cUnM4P4uyqo1hAfPEt1/lLKMc1bGuF+C/FuQGZMPLKNweOK0jrAHF46vTBkR0QrKE7tIvMpbCptcGPp8f/ie0yrXB8aDWM3pNaYnAgh4yFeSx14SqoRj0G4VflCwgXfhhLfSslypKCwEnnPLhW1b3v1fzwOEjq5Mfkt6tA63+YyzJwqHnhiEOlRyUH6cFyKmTLrYM5w43HrWIJy1j3x92lpyDI6YsyYlnOKleMA3wwR7resmO/une5rinwbZ7H5eLkw/qusisDLfb5DGLDjCQji0a3sZb6y4iio9xY3LYKhsu+Z7SyEaR00uDaHEKO1LQ1pS0fnbHtMFguLnKabjzAcOh20CZo/zlHmFDf39SFPMDoJl+ESa5XpVekcdPLcEz6UtKqtEhE8bavXa7SzbNRogFxd9gXJMSqFL241BLj8kNsni+JuUx9vo5X90f31OIUid4/TMCrJeUeMX9Qdng8edbv35PEBPz2S7IpNYFpAYFsLFHvlVGUOd1v3emXtT+HrSwE8q7d4ipa+R3VJ4hszdIZEGQSOGKg/Kb+1Xb+AJ9ROa+2pLtGJ9B/i1wjXc12LISiVm0anefLJoF91+gOw1yjxoooy5+my8q5Cc1zYarN+u8qqxHOf6xO20bZxzWjiCKuG3f3oMES4MBqA2wJqoxO/8OmU0ep4nGDsOrlWjdo0QF+Qj13fmy5aaCTO1/zdhib6rogsUheiaL3zyAqMbrrqZa8X4SQMwurv96cN8GX5FUmx1TqCA5HT4xqWyZZGIVQuZ7dyD9AljzyIn7ceoRSf/vPztI3hP5kQlMkiyTLeP2XXc2W58gY00WftuFZpfQFvmMaLWvxoTAZg5l6PLMj81HCgpv8/5nAdBlj40XFc+P11caU5wG0z34JNkzDHMaMknUIItCRkaD7UprHl54TcUpGqAFPaYQ2Yh5jZjOWQ57IHo2RuBqpK6oAh2gKTsR4cGnBf/YA3Lv8/x+yYbwCc9WBshSonbUJV0JKQiVt4sZtJ95G2S9ulH6FTJXGSNymeV11FkKEtUGvqMePNSuQEe2ppIijxr0y2SvmZAY9catkcleIkOThOfLiqOEI4oJ2tvaRkUMvChRMUZXjdc6kmnuwr8K/3vK4G4rc+eOVtt+UxXMefaXx45sA4B4X8ZMwI0eJc71xtm5nGnIgxN1OZtnA25gUqUyOnY56jMtaV6DP88x+sIgSQhuTY4oaHCJiQiCAb1hYAT/lQDI1WRVTcztwL6u/Y8kjQ/x/OPYRZSfe6RobNB/ef8e4U8v5lavgqT2kx8TJW0J+cM1Q3l3m5w31IKoARWgtFKoCJrFEdFPvo7ADgFVru5qHyG1EW7lEEDcshSZKT0WFwacdoADxaIq1/wokBYBmiHRSfPw6gkI6YgWkBiY8awAjglfuiEC6cOwLTknh5mi6rPgGhxiRZHNQNqkSBTgEwRqiYIiVAbKtFipkec93+uIUrMtcJia9JQE2ctF2yWXjxoRG24IYgG9cewQy96kGZLaGCBj63CYzqZURbVAk4SwTmpHJqAUPaKXnxEVkO89Gg6l2zhw6A2kd7gDkT890DbXjZtPYi/SEm44a/pm4JSDdCSfnGr2AMAdCTbAIEYuDCH3XPBSkC2tZOxFgRiIemAuDoIMg0xAJQOGhxo2LlWdD4zC8bJSypvSAsokGC9f9RotINNNjyhqUUq8erJTUQ4IPI3L/7DsWfjFJbp6JXp+GM2T6Mr6yTzfCvdnzYFNZFVGDI2pRiaRMjT2yX0R4OSC2HTgGtpeOR9ngCDXh7CNWICzXVs6Wx0oALLcMhjUoFFDX+sUgRDPtVDgSweSZo6kVahsvAE3MQsPUiLUeLc+g82bgUgW2RrYyW4RAKFm3rmMFaMcbEAU9a6JElbJR+C3dRjawJHJJEAZMBpR1jRXFJKoMsZUEaF5ZEpiTb6ly3xdSYecvyPilxrhuL+DbhgcE3bkx6laWgbLZikppREBSyLHUwug3HrUuZ5KxDWHcEV9JSQAmAUh7zKMnRxDFD7jOOI1a1YkY5OE+yl5L2QDYGLRpYUtgZFNhDh3evyChuuxWmF9uZ1zqwQloNMNpkuf1VDTjjCwwyKiKageUbEIcURaB8a1Gz/hCb5TbSJqLu5ntb0HK5TqnMSkqq2GmOvRJglAd0yDZp5zAcoDLkyyhcUGY9CaKsEcnUhFP1QzqgJvcllqaD0oM7xbi365T/rLLhYY7bWtqHjCWCf5dDwmxrj09zCGwExd5GkS/C6ZNylEBbuT2/YX1IkZPrJXpbzfyLBwZXLLrXLvlWVWFci4xcjev4s7BmzFeAuS+bd0olXP9Zt1yRWaXrXz+lVeDGMq5KUjLawyzSUuKeCbK8wlz3Gc8Ml3j9lCg0db4O5p2RgEPzpPTDp+yqAosHq/v9Qi7XeL6rhofjQ90gxRW4aZ4TUi9xdn1hzQZXVcPHwxeAbvq6tQcWudhTVmCyQu2t5Obbo5tcExp0PdRuwPXL0FpWgt0B3eoLKGnvuXPOdlqhLzwnFFE1RagXlpeYfXQ45j6aB3pfISx0R9zb8UK7QZVMgUOgST+CW3OgDrvPZsXNZ2rgUwity5ohP8sn7pv8DEOaT0MLivq+Rd28P5sQy9ZjWpi4wnhgGayJh6fYp/XBj8tKGiHuWCfgFynghlW1F3hFPMwtSlmNFKGgvGZV8zGnsIiohYK9eP9NDVFlYXx8lcuG6waZ3jvDasrfVDi2ZGzr2NK4jaBC9VxsULHCc7TkjaJwaKPsSycDgwhcPZyUsW8gU46qeYUIgiaTDxtLiZVC4Be/g3lRCupFHFhds6q55uXpPVoRqzYf5RF0mgqcHbAwVQdOWg3O8PcE591YbOWm7uTPkuHyJUu4bHgkFZNeFE93mHwMkU/F8yoDEWb+SHEsdVsXMxek+DWrmmvGftKHVvj12Y0ngyTPjk9QwiddWhvR58hSVJ89GwVaBeoUDWXaARMUBiOh3k73m5E2d1sd6Pb04fuK4aN2AiClLCVHzW1VO/tsDKGyQAtpJmi0GqK0ndMV5UDBeW/z4UjtI2NV7CMQfXntuZAE5EZGMMoaBJEj23sqJnSdNr2MKtcf1MX7r0Hs2cgK5XX0ksQ7P4VFrsHJ9l3GQ0BJTVPGYlV9WPokQyzbT7HlXoUOM5iFdlXXRGkFqgmFdUqsZvConCiGDDpdUTozMHSCXV7Ccw+rJ5/y0ZTyTERZBhzdlFc/mU/3tb0BxXuIoOHaP+2S5XFXrWy/8KrGQItQz7Hz6P8rl0YigolzOAwSp5dL+urMi0UmcT516UEMw+CNgJttmgTfnJfIY4rYx0hSUNxjM/oc3GKuOANtGK5DlZZNUYSu8EFsJO6/oX1TVcUsAilebOjrwBAsInTgr9g0TOGSdjp2oCBlI/M0L/9zaPrM6rwPzXwMq7Txbv3MNi0y/jJxUH22DgXVJyTwyuQYj7XVvVa/+uRl0vzaUqMxtI+CnI5s5ofBs6AStiyijCq4pj0Xg7E9mW8eaNmxqe4boXfiUizzSZaAtQAzqyFyQZHJ/CbWBkp09EtfXlt8+ZoksNH7JwF+dxvB+WNY6LYjqHAADIXdxm7tsOgIqckqiBJ9WFIK/N73NbU+qwmIefioblf+Y1c9y/erXkCp0NYEDpPps/eD671u99CMlRkZzmaG2f2p/fLBGD226NCiKY8PGLStte1RxumQkSFroURfR1+JIsnxo/o10H49/GaolfApFPws09n/MPWngAIILm7sQPAE/JSmFfj5/F3gZAi4LvZL8bukviZjRdb/yz4oZ0Hu7+VtTlXEqXSnZEvGTIkwdZuXHEzr9cpxM3LMLCnLmg5BnUG28cO8xKBidghLFV4lJlmsr/hmiZ2Si6KYcz26PqdliGQ6xBKzUXaFHGd59fKHFeUoIZR1K5+nYq8FoRbuULkylSlPUsdztaJgxtMEc9HSdit8rVpizTvrFWqTbdpm81db+myLqt2BbRp4GnM0DWuN196rU2TXC3sIXem6+XoC7RXpXa5P175x+z0dkBjodvCRI2JH+xzvKCimwrDQSLCz08Wc2GVcWsKazS48c7HKZWGrBUTr96bu4l+3fOnWH9x2dzvO7B6+a+clXnX3b+29if+dftD9f3roe49u8/i3nrzZ2fOeTmnekQrPBnfOlDrPx3qhyEsJXnH0ergLMd686+293lvqgy4fnffJQZ9u9PnhXthS0kvts3x11tcJvpm67/bDv/4Xw7s+2LLXvy72UZ0EP/n3+Q8Tftnu19l+r/Dv4/2w554/P/lL+e3/j9AGKMbzSJZvOXmM8qSDZ3Q9t9DzF0yp9mK/l5aa/tgMnpk7BL41p0SQXnCkeZ3CZCIKRSPFqiS8l3ROqrl0tkxvWfOyC+Way/ugoEixSomX0jJlFyzAWrhYpSaVDXctUqr5YYnAshkrl1ltaV2+2ts2kGx8bjOzNnN22d6v7q56IEMwGzT6aqFq9c5WxFftD3XW2/nZbpKuaj3fHy/2HdF/18Fwhx7fESX8crzVyWCnthj2cEbgrMa5TqPPjROcb3Zh0mWuay5u7PKWtVsO7iC9q3T3mPdbRW31SzffN1gzEjMOm4yY/pWZFbMPBv0NRrl/YLv8nTxr4aDFxy1fFvxfDEV+I/a28D9iVf04pyWA5EFrN0u9LfqjnI4ZJ2P9F3KflO8rP6x2IX2Sr2t8IPP9e5aG9uO6wMbDtj5vp2kv1eCHbDq88m30O7t28GBj5/ZNXHf8uU5+h9Nf3u4P8nftee5G7Ljvox647hHYK5gKb1qKV7hmYgUuN/uHoAOC+81rFcY2/6GI7tDr+EcMNyxvndu1Bf4LFzzqardh5/aR/77wi5cxDTcjpHNAYrOkjMiWzEcJpFPqzWj5fJc+E8OTVpn5ck/IK4vvthVcTrB4pC1jPqHlMMwk8meD4uePdF8ZnQ1yObnDqBv8nJkrpphiCgo+rJBpCz+oCrbIVY1lJSWcsmypFUqrAGu41jlYv8cGko0Ltty3PVA9kOEYPzJymazDjkotyx+rXZs3dGtvtdvBnsk8/vxn72u9T/XN1xfNnvpl7ND/2QDXoS2Oejpe/wrteU2rDH820umsm9FlXjM4j3FhjUtFLge6+t71dq8vbtOGPd7Qe1PlrU3eJnenkh/urHHX13ut3r/dvjWHPNjhQ38fgXrsRq2H1j3xYI/Pm325y/eIoo192fg7TP6q1D0UTX/fzN8JrZj/uMWa5TVhSex68e2I9+NY+670tMzfxH2YrjyueKvSSeWHVVZUv6z2dXVO47jm/2kv2fh6zfeQsf0TO6fsnra32eiy71/ZdYCKY1/r+NLJd7tLd8/b3fdOD5QeDnpZ2isekbaXuF4+ZHq1GZFlcnlrlsmcDw/IzL0quFnYMRF2oipFc8XEX6w3/BJfLbFW8ikpeqlKaUtjmHNb5hbZHnKOyCXLK0uAXeuO8Dlh5BTbwF3Fn5URlD+1kK3qg+pONd4Wz1uybWWWY6qMB3hqzHrknxZr3BF+1JCyZdyu0XWybeBzTZFTMF2zJXRbDba/V9eu4ZgdmzT/qe2Ejpt2WdtD1q3Qy6yPhN6BqwYABz86THZ0h2NFjl9uwhOyk7GGXJ3eYDipyDAfmyFtE/80y8XQnN2Y7ClqsyJOFHObinPF/dfMza7FbhmPm0duFe9LELaJf1rIx9Ci3Yo41ZL36rnacvhUHGjFYTJMrTqthPnWAtfK/ydkuCnaXjLYFv/lMtFW2oQY2saahGV36Nyjby9r7zBlYO85hvYhy0XXNGPv0LbfdQL6PeDrIXrGKDoyEfeIyo7ajI+6Y65LZbjj1AkxdIK1D2Cd5Owrs80wloqyWXAxdHTKfaloO02dkPnmwlfCamdOIm+esOugnbUYe1/Z7NxF/un8/nFp6gK4OHq76Lt8proE7zXz/RG8Ipa7fJKfXQF2EUVX945LXddsxkfdn55LR9t1+lIZbIE6ERpvkboTR113gV1G0b2D4/K9+8AuT4q+B8yJme9h8EpY79F5yntMHI+mnpCWgKanDotC1bOD4/FXz3fuJjW9sFsUaC+BsfeV7V5dBu213fiofOPaO2DsPae/906LQtX/iPD66/wkLPcRmQSsTxxkfSaMPSG6vjD3DmNfQydhsW/sfWa279SJDvfvUCZlvR9n+aufO3f2F2D3Hw7I+nf3blPjL6v+814U6n4nzfHzDeS72cM182f5TGWOrAgsC2SCoWBZIktaJeas/JdEZdYW40yIvkXIJGDZHNtn1rMNXCaa7MDxaMkeXAwdLXZfJmM5yjHkhEwAmrPVru4n+lygXR0KFhiZCFWuhCEToQpiM85kLOsWnAdv5ZiCsidFm6f7UkGDHRyXprwcdpPK4MTxUObttODlMpUPvCg68rVbDF35eS+bifzhZdNbgPdymSgQ3gewgk5PzmrBoUtlrBDGYtAU6rt8Zgujj8s/he/e7RWxXMRp/ipy927TUJTd+KiLdl0mXcXsHZeWYkkTMFwcdSL0xbP2kvkQ4ZMeboI7y0ZXYuBEzJYUPAnLJXOQlWKx66hKpe0lsyHDV8JqaaeRl75zUWhDQePRVMbO3eZnmYTdpCa01R6hLIu026jDOC0GTdk2e0BbWGgxdIVzXzpj4el7yWw5oUgj7F0cvRF9l4suUuBEGMoNnoSpyPBeoo/CWDa6qKyVsBztNPLou3cSWYzd41IT02oPUMbyXS5TsZlLRxuHNiEGuWTzyisIRyOPJz9VgVDhA0WPFMdV+ARZEsJOToSqIsKQCYaCJaVOSG8y3+UzkTxwz5lPEboSzBUf2Wc2KzmLvNLdu0lNZTZ7RFvl0BJoSOkwPh1VkMc73lUR2JGJ0aamLonhNNSJMKRFVoS5SvaeM19V8D6wWHXgBDRVQ5qA4Wrpe4mxuiP7CVY9siTKawDGWNwUmGr0HnefMdXEmJjFmo/sJWO12I0/GeZaj+0D+tpIu7hyFmtnLmHlLNbhOx7ldtfYE1OvFGPLjk2MqT7mMumo32oPaGvg4B6harnrUulP57QoKhOHD77yu3Wa8ZiLfM04WuBVQV/P9gL+gssKAgTCfAnm0NHESOdoeAQTbf/LiSfv9/k+tpX+JzQ+CdYblPZRHyQgtydMSRh1hkR8kouACz5eJ0w5yRgzkvOMnz/t2m1TrqcNkmUF1uFisIINQa4MK/5YXgCjP/ERzd2iOjIff2F+3iPIJfMH4YiFDyKD5wUvPqj+3jX+zrNf4NOnF39BaBIT1bvLrebbsurX/sCTt0lx/lrs0rT+xNeU7XovFY+3u/mdlDjMqTgwzCi0ywHkqkXRK5YWnMvVVNtKWSvTaGiLFUkLV2XTEJckYORN4yRWDqYLpLU2XJ0UurIx0lEBxd4iA0XWkZoRkfl/uodWGAxZGqZ1KcxQfb5jwrfTq4WaIkBYShYWEgQICGnO7icwWz2cp69fOkCSHYR2s05/BoIaVLjg0Nfb0U9PzstNo0Qhn0jw9e7pNzuAn9Gg3rv4E/iAnf8npk/TJT9nk4SFkaZVXigzHpnEClIbioFycWfyUv2QE6mghUlm8Rt62EFhQdz84QiMwD4cjsM3NmOg4whW9LPqKWfaIbtwj6pt+9Tbh9ZHYcA6g8R3536U0aUtVMjpPCBXEkhe4K6X+2qV6iFlofV3z45rgfx7rNTRUYgpS623InPcCVDbKqEj1t76Y1nN1sVswWteJcOLCcUOWciZIb9D0h4HXIFLc47sGblouPcokliEuyo7xUwnvIRvA2MTsIg34PVb+P2ubU94mXhfbUvQNg4RR92YyvXatNM3I3YuMkv8cUxzU7ztYccz//dYWbZrkrv53VpzzTPD40GYB3n8jtyb6Iz4NtNfMavNlCiTTtEhu5vRaHGWzNO3dUzyhmgyAdcZuw500YnHWmUBJ+grMusbaWYqimGI4jfI2JmD7ojFv2GyYrpjsfwqlx5v+vldS/ASXJifr3wmcnFTcoU8/z/tnINj2v7jJfAEzaFd7qJTFGreNStniVq9kZggxPuGKDu7StN3eMocV/K1pC54DSIkkrVuGJUV/jB+1vEeuj2bTodCDNODlHPmkYmW3NFjkFkNYf1agOTXqorYyfggb9VsD1om4T/wZLyzjtB0iWpAW96eYMEVRshCBqO0qZl9zNYuZZLbk7m4Xw+LbH1Z3MM8FgqBvm61Pdxg85KMKjASGwPzR6HLMJlYnuAlB9kSycGzZ+XTyuFG5aDKTpZcMlTPS+a8jLZ4hn6bAgtABRmvfUBlFIJL5oSXogoNG8SmxElX7ZN4i/zIgIUD40/NzPhF3WTwRJNbmhUGPYOhYjMp1ozDGNwFW6nmkXAreqKsWQm9FaJizjLETMWMHZCM4NyW8LDQzPNON2EMX4IfAPUXoYxX4aVGpkrCaGnWkRIfYc0u9fxlWKFqjgejfAGaE6HlV8IKpkS4EJ/5kpmvvFVlccma8HpvGvowsiRvyInirSEAw7agphyQP2EPRdcLO8a0fzJYfcQZqlFZY572yGE7PFbzNjsvGDpP8Eo3+cK8guQt60ilykmF1mu+HaP1TIbmeSuNpt9WFaMW3Qux2IGhj4LUs8JpTTugAXVsC8KSQ/KT+DxaXQ79QWUb7u+FEBg4nGqVUoQN/HNOldXQvDfYTwzLWDi5GuPTDZhtodNh8Q6c2sjzU4MwYxe3/Xs3Poh35UQ4yAg3STCiqrCsztj3ipr9PN6Uac87KYnHCa5ORBJLmm6nkOoKCzoH9+cX2zlqnTCLCE9+uIj2L3dTwMzJzBXZlmUF3rL09N7csPXFV+1eUTpXrzVr2PiIyx5FcTas+K/OKr4M6HNvy4Or+s49pSb6R68EBFQFXBIsgxJBZUOuOqrsXjp+JPwccchrxfoXwY849ZQxgO4098ARgEP6Rkw0KCJOGCsRZ4oBpvqg4ScbTZpFpVjhQ4lF+MYhG2mAkwPhX+kaRlWBMGaHbGMAvggh6OJdI7pz5Z3yUELQpLRICdUYkfzKysV9PU+flhHe6JTLyekND2rfxro8GJRHNM9UEN7LgNqjzzcIapPmOQND1F8dkExyhLKHW2iGKKcYbJtKJJNMWv2cC8lioTjs42EzNHuJcC8uehBFaY/7ne2otodfXUNzRdg3svS77GVC+n6lNi60utz231SLpVqYNLAYPcPLFtxvOIu5S4HDhzSuhmGfsGrGvW4z9qZzHy4Xi5P/DZ8Dv6bhRi/vb8fleLqqCFIl8ZljN2Hkyb5wY8wG0zqMl7KFwN3dMhtblXOLBKEVVnTwrJpVrJRHQqmSvobbl0LKEMnUsLo3WUieQAyb+xNFVoVHJf1xtYSvdvomObdoMy5sxUX75JgWVuE6IQDkxoUTyozbwXbigNI2WuN+oPDkTE52XCIpWPrmYnXeMpK1OYnfQ/jXGa4Tg96z9V/a3DUZwo0BurhYjXpjM1dT1R2gYDtCDVGscBBSkJ0Oud0WR/l0g2QqtsF4B7Vxz52olM1kIf2UclKrWB3gM66NJUDFUfTLRinJ3xlbCrFvFSdXRKriZMIE3WMMfv88WrlSxYgIkhq9Zt39WTqzdQssK78YmqDxipE4UxbUTRaMlLNyvY2Z1yQszKOLhhguPb3EQcZYpam6FkR4B29xf0K+ZSQvw31pqnCzaYIPKOATx7GnT6EJMvGncBjc+AqefyFwSUCYKhu4eELQNotpftuCicbJMZiiHryLJaEUNSsmJEEJJCjiBbxg8Jr5I04nV5bnghb93bIEU8srm8ZB8E07qrqvLNQeaPeW/Sd0f7EgP7c5C+pfnmZ6XbVEdmvioMuZ1TEvkckxy2JNcEHMC31XgO8eYcjqXG6FaIKKpXaqa9pGYdohcWZkvBX3F5cxpeE3xMKrlXXKlnASXxc0EY7obVl6S6qlRMeFoWKn5nnWsF3Mh9kwsseuUCxEAmRBOwMXnjuSHEuIyhDKKpgj+jzRhZaT/xJ204ABFRCt94DCCJ5JFhbkJyR8Do8YER+/7kPowwbjEZr3kgQkYYzqihNw2rdhHUwbGPjTF2d2+Rb9bX53KnegFsgOEe9u5IiOJ9gak8zXM01iWy+OaSR6WN/rKyZphqKndGzmlAEmy7cizsMkTqgVP1uRFXHkm+IZWfIijUm/pXqVFGg1oZeCGcbeyQ8cOlt0rGgNxqobbFZnSc9bs/PmWl4fwFI8r+yiIa/jkeGCLOw8NSdWOQV59ckCl1KQcAwGooPDDBtgKK8ItaAiALxAOAyH8EA84DPkZNmCDm9J/A2a/GD5o3uWITBMffUt3beyI+9N59PjC/dsmu41sisHxM7kSsVMRB+eSSdAznLoBcftOykodr1el3KVIhF0nXQaWAVcl0bp+Bw7DD2ND1ayAyEUyyHbEDE41a6B8KRfUBG7kWZTpa8J6Qrmhu8XZ3UVGIOLPYPAEQG8gODIm/y16hROcf4IAKpeQJfm9qd94n+Pzvrm+1nqlt3rpNXDjimTyss2dZdWrlqksG4ccAbWJl6dnlNA3vGwCbfyzeIhEcQT+PoEcWD4Tgzvdu8LzWpxD+AA6vFy6TL9xaKLVsI+LbtQcDtuZukouwFtKVSTXw+/3NE3MLRy9dZCXeUfr4RYDAJVADlWaVylSwpsEFBfE7xMeyZWpYsDNFZXSASISrEc0NpolaEIMHg5JIAbd2Owafd59sl3x2LQ96yCYzql6+4fD167D4DzrC3Viicoo8QYdqgQhoKGbOGs3DwZkluVjW4nWsmD8dNV8Ro6LU3Al9QurbYQXLosTAvv9xlP+cvMxqte+mnybeCN1mN9naBXGrJVXt8zgABJ2/kMmxxoZopFXAucHWU0s6UCEGjE7jHIdD1JColw2JCkkLIgKPcgTMV5xPkEHvBgHnGRoS/gPH4uFAseo4zuPHD5OKImZlku/M0c/4ENZZjujOEKM0lUsDyZ4ZCThgBnw1nQhTPxzfhKI/XjyhNgVOdwSRXEQEXniMdCDfuGkT7djGdZPmfh1ov19CHHrawON8/3GPHWHoHcM2jvc7aDyw5S2pgNRhztkaPeYmoFpYdq53HXc8zyiwS8zOmJ3nCfSFGcp1CVyqyH2rMoLpJrDN9NZKRYHbRT6/JLr1WFNI5TJ6CVGPAESPjbPe4WQpnTlD+yZBdBruGk0GJhu93Q97BVOpsvehfGooyVBHvJd1w+2R4JWISpk0Y3EANCZiOh4varUkoDbfY9g86fajJmi9VpH4/7Y3IOqzAwsG/lXjXsVnFtTUIPqXSve7S+bCoE6VWuGRuZoyXIpD7LKKKcIM1eDsldeAhYgusqVnvmRulTVzqmvFbXZVUzmj5yEMe5pDWNmCqLWSdVa5X4pNBS1lugBANDSFZFFQV0Qk1na7Z2Xo4AyQviYfWh4e8DRH+oe0E/5qj6KIvNIpryk7KDTaGBT8KiMKBxGsc6wKeuvtkTx/8O3pHUr1UkOjSh3pJPHpV7qtSXPwlPAH7EpBndm+1A2jCWy31f3GIFM3VYGarxqR50wgYYbDZWsavkoJedALCzpDkSHdC1o0nobWlEgAMjzIEUx+LpOb/s7E0JhAojfnP2Z9FFu3xDLNOnirEQRX/S5eJF2dPoym0rafUAnV9PuRfzKEH4iBBqvOnlM5F9XrU9UXmNRrbCdvyYM+OZXY3GqzXPRM6Xm9RVYt7wOKRyGj1/Q88AFrOsobk5h3x7EUVZD3o31nY5HVkgfrN+VNycx8XebSkJx964NzcidwphWxQDUXEvAhWeY7P7rufNAvpR4betbEeFODcBivIuYpM29p5p08IV5YntZPjX4M5DtTrTWW2Y0oxoFLYbEIvMPUjy0B5ozAqiJTczcgRsEv4guhjvUMsK45JlTK+ExQxhanWiimxXhjZR+U4aALM1Q2ctD46C1zhQps5L64rpEZqFz/xu9Zb6Kg/D2HDKVQjzWkOHiuma568dryfJNKx/50Tg2uHCmU0ExCEneJPGS0VhkuDbu87nwGCw2IgWMUkst8Av95LD/g7umto5BT42qNH0sUA3PvHD2m49k+m9qWw77gbwG2/65V212kvVhuHa7PgIqVnZkfWhd45Gmpv8zVzWyUuuUPhJNsDBT1d2EFT3GCCQwXY8GXzOeNnBD52ZdrUxyFc4GNSVJvfabv/VqGTHEIpGs1eh6dwcg09MiNV0LudrvRKIYiC9xU6myTdTtnHGz/J64YkjQ/ZAbsxFgSTej5DBjkjDIokK3cT2qjdMr7For3pUsuJ4ziCwFs7ex10lAjEXrRN/UE6I2jWMKYvWV9FZvpugvVmYGXeOHBtZU67v4iwzL4U4BCo3Kp+1HBt8YIb5vGKLhGYx6PFwLD18gQU+qEQC2zGWiZ24i7wkKhjNcUYe7koUUOjZ8BKQswp3XVpqYWLQPATdtUI3McnNkmwGGsTxtTMf7oKX0CuTzS1YY7LiA752zjafQLoKS35YOBu7Ir/pMIHPA9wdMIR74m5/gSzxvMulibf2fm0l8iE9zAPfubd+YAb6/IXHpDFs0PXv9/zmv06y/XYGeAK471vsg8tyw93nR6MMjNKt/YzZ9vBPqpXK3bmueZmGtZ9YFvfWpE4XhypI1OnLOLqin6zGIIhr1SDl7yPvdtgiicGmmncZ5H8v7/EHCrPAD0pw4Eyc7RfxsjPyoPG6W56Hc5lcMX2Q+pDC7kpQ/BYVPzUlNWa99GPis9B0CcwDdVTmRRMX/rhE0IEz+9+Nz2IoXsZvnJlTQNW9Y9Ff0TsY+7Xk+phIRgmgHUGW6IPHaquXWYrHv3Uqzzur+b0cest9TUxvg4FA1/UZ6X/sSrnrb9gm9QiPaLX1NEVpnMEYkfQJZP+8HHb5eC9M7kT56B6OCHR0mRP3K4lvdoY7LxDKzSpeNtXI8CVKwu5VjCoDOO2K1agMLiaelziXGv8TPVO41i+9hUEsQxT9/+RPQvjLJTYgEodfY2pkpYHSIlYKBOcEmguKz6oAXDBrDw2FhMdmICSGu+QR7WCrB9j+BgFm52W6fTnWyFK3cBRHOswXmU0nB0L7OxSbv49VmmzMApbv03QRFfQhonHAKK1QAxOU8DK8ZD3rZkcVtXfOjkP/VXde6MgYlZ/2p08zyzXNPTMK09x/G7OQhUo2XUCJgKv6GMnAVfbdqyXqo0F+Uk8M4D1j9j5yq7AZ0SOIzI6cE2PBhMA9y7BH6+a4v90lnWgkfJYKFm7dVdJs6f0pWKmUzpvinJmahMDBIpEwt5HpI1amu1p+C1mh9MLWGqNGbyFCYtAUaz/KeeyfsMATPBdJPYhwdDzEHdpbnssJIvojuPsksSAo86Plj+Vp4WVooYAbj4QVbGu9tSMkoHpNv5kkMueg0FIdptXwUgiHepWT3h20uOga387ZMjq6zIr0TB6XwqurKmGjZdpVrCe5M7Q1qryjzHzCwsLTyX6HZgrKgNyF7IBge/W9PUwIVSq3sDGzTsYqULRbNEx0lkQACiwl2ILRQdTM67USPcUikXaXguXFE9DkdrYvr8VhcbLQF6k0OmO464b9IUG8dbDWcCoKV/x8dwjJ+oLFBVadYjnmH5nVQ12OTtauKRHfV9SV/Qx+z3dMuiGWtuz0tEbdf5nbcODM38AQURDRywsFfICP8AFPuHt5qsxLkq880Rch/Q0Lb0DSqRW5mXnmifJ3BKD7jXZm8xqjwmJp3K0fR52Uo0paJltzJH4EGyJtqcy5oREowjRIODk2bcDojMxUUGqjcDvsh+QJQwdvxzVF92EvS2fR3KjUKo/i/DcWHduOI0+PCarujy/e9PzZxY911XDzolm/TEay/g8l061HSs+OVbl+o/aq60+XYB5etaOC4pATRxORC0+2ToC2O08Cr3MuEjzf8B7Y6ShYW2GVaJK4H5ul5NmniNN4uh3eCY69MlQKUc7FTT9jYFNKfug7bMFtl87q6JPkUx8HBnuHogjlnNfxMSZJrTxMy/9Pn2nvS29bot/wLHX6hfEmNIKqVIMfCRGmRygcHk3Kns9GedQeaWUzwwsIT0x1620To4kiNPaKF5t46cvi8KqcQ+2HEcW26a3o4Moly1M96KT8g27pNt7UebNyZPWi0ktxmFkfrhTb8YXK3CwfviPUhkRjaGOkXFlo4Dlv1qTXLgheHumk/aM1hh1/Kyrqybv1eRCAxcdUngDWiFjw2+vhmY1OBAMVw7pO1S0R0Yy7KhZ0cAmz63p5qJiICqCvgqxFWg5QI8C1pU11Qpyl3pPQ8VQQ4TUr5LqjlOftNLPWPMlBpBOVttvvi0X9gNAOqgdYZLN3tm4xN/iEF0dIFKlK9TZHjehZR5iTsjvCm0qi5t+inqjSPe1BYGLiaADIRLBG8JoD9FTloP0rptIOWt7EdMRBHXTADXm8CleWIyd3Oy9M5yggDTsOoZfscD/y0JR8zQqSiowaWsCntVkUE8FoqVYRSvYqbhTndp8GFxiaj1DkPH3DiaNDm+IC4/BJsbzgFgg9E/bc3sHqej8iJ9qRtIEWX8riP51GHcMGEb4GMXrzn0m6w6B2WK3sWFIBf9LXwe+yFkF0ubySSaWKebGDbKlmRL8VDvt7hVVeI+8EC1N9MJBXBiKcP5Ep3ARdO+8U6H0twfGtrpDDvJ99xJaYUQREEbcihaJ+xQguJF8wOYYCXoo8oWIvT/VpnOrlencg12LdqurSNrQza4f71Hk2KuX/sqXnDOVzco84SClCgQTB7gfHZxsvCTcpnmPNqu/pkdQJCpFuO10SdOZD5dnnnjA3NHVPvLVOCJmCLulFe38RaXNyTQt9NDj37re2tGwpQeuPPZknu19bLhJiyix9IqxoApguUVCyO9efxiy2PqC1+lvE29B4cVg6DQNDGc5RDdzdCqsAWHSXu/6AQqqXlg8XcGwtoSPrHDE/0YWFiv5kBGT4ZMVctWe2RtE0TmuA6KSBdc0ABzss3N4tBBw+wVVvDj3lwoq1y0LfxOwNrp3QhqIsOAhe86tj4kSHEMCwQBKx2IYwkEDwDrmPx93WvsKDoEX0zbQOuqkziA1zJrHfO2NIcAAjOxCAZCYE+rcNZgoEwX80DucN75H9FJKv+nHYLvCh2F5ZOrzytzum3mR92tSJ2DvCG3OVAY5Zj1u3HO/xLXnjTLZCORRbUgtBW+Sa1KQQzKZVmXAhXkEnOPgNFYg+sX3Kh1dxMrHE2PL8BlGs9CEiFcBo7h4qsSAt41EAH/jAN9K3KgI/7Fmi8djn+L/8/cUWx0ffb10D1w5IP/0eHLKGT90ef1i5BliuIROiwrgg7BlXEI4ioufnLJ2FyDmQIHC04Ihg+Yc/eo9h6oPBAHNuM+K8twffdvXIjy+Z6Asz35PGsQePo17vkmCm6/U7Fv2dQ/cO7Jx26SwCz6vi0VKxjknKRCaWSb3LAuneKeLlcauoMVEQZ1p3BGVTdPZuVqN0sVvMJKMYYJarseDHdvn3zEcu9cWCCLAhhPMxphco+t7+JmEhYFjp+Cl9dssnxVZHeGK2vB6lTbmcXKHWx/Tp4Kq/Aj1cAQdd629uw6VEmR9242vy/vaN220QI9HKxjBPTYRFqF+ix5HPzjlDolhKbes33m7H1xcLT1fEccoYxfGK0mQk8GRxXG1ezE5kKnQTBNLaPp8oN+OhAHhZbE3uy0kXQ8KoPifSyhbCmlZpB7v6B7FDe8Dj0iPnK5V8ZCPjMODnSg3itsSKM8HT60ECLx0VL2UwrJzPxFknky9jYKvBl8QuKiqrQ494RIb4a6y3TC0ZeYYt5NwCvUxOV3axpS7dwtwik0rIOQyStJHXiWgUd7x3bQTSF3i3avh0zIA6LOAg6UwIbXF4ZUSC/y3SldNE4DbSrR0is3A2+CyG2yCnBj3avo8BrWwzIEDS8TWPtLAdBj0BR3AdnobfwqP4yRHHX0ErVYtWxhzBSJDwXe2bhXCh0HFFU6l0KmLjqkHoMLmcizB1smfjd6hKtPfu+KyoqolcOm4ov8iEmRauH0pP+jjZbEJA3F5QpIpnmaBK2sqvPdEoOkqvgp0cicI9BEIo7d1qt1zPhgKLCtP0KLqVGsuWPIqZMA7MG1TSj1c6DbQ/NqxqKtMQWqlAW47d7ZDQGZkb50gPhkKsOy1MQ4oOa/2Zibumleq9kUgKBWfbTZ2tFAo3qZqAO8AnFflMKpkp1qwKzCOBIOd5FqNNoRWyTrWc98+WEs67Ffvm+TiZyiTDjA+crirmVdXJ8rHGY4ViBSWFFvGvKS6macjaTIJ/Ya01jIh9pr1b7cv17rl8BWE+FdXRSTke8gjHIEpzmFzo0E1W2DsU0DhO09SR0GwymeTCqZzQYBzwHmPXnkIT5KFjKESs/1Qw5MtP1/rB3y0d0ndeBeEbE4GYpIALCnAxcQ6zVmqC5N+4ULpEJyEa4C4xEahKbc336RHR683mDwISaACvwdmYeNTXzPaV2BYM/+8dEg3gVX5zlEyAvsy2ZPxODcbfjS/KCsG5+FDKq+eYNVQ0mYj7LdK9eFtZnJNp3s5TKjpjv5I+oHz7r+Gv942Dk+x+v/XWM0lk2JbS/0WTXLdFzX6qEEXPaG14gbgpCsbd6lEZfdlu98fS7nXpEd7dqAqd+oZBlyfMvGhp07sXOysIXcNNstpB1Qig8ezX0mIPsBRoYR+9or70eFBaVOYJ551H5yiZ2gpoZqNfGF3BDSQJ9Pfp3W2tMnVec7WSKmop0oiR9wN50srZ+44dMPnsbDknDY6Xr0p6HP4VKlINZzy8Tkbog7tHpWGqirp8iq0OX5035hM9RtHIcRw2EEh9OeJqTpoPY1dnD46ADszx/MtkhKLnR8o8USxUsULM6qF0tgrCW3FXh9Q4iyP46NKja3l/xXQBAAlOPEmgBscK6+8HYZA4WWCyuDTAZxE8MFNW5PC2jOfUU1qL5WMFmAW/k8HvRPjiPaV/+kCrBIhhYfreB/YZfxv4ReZHCd2Rm/4TdvoXLP1+AzKbbxOcr5iSW5v8QmIMg9/3Mp9qQG6GWoCHJetxse/s9tXdJDwD2SCTlfPs4YJx5Yccc8o8gG7QC8G4uKg50NWjhFxTQoj+D/1fnF9qnkZINxnQ4IIILsN5VfHT59IjwH5hPCcPBH9v35H4WKCWD+FRHdpLjb/75tLpFB6Wvcu3R5w7pTKJkDHBcyRNjMd4WcuPr057r6BAuOt6JDPfkk+OGtOth2TiK3C8pDdk8+lwrzlVC+vlWa83vpA+FWuYmUkcj5R/xodsK4cu4VF1/OVB91BltQ2haRZEIykIHeCVWGzJh3kj3ayVJGmfNWZacnBFuOTpSghuOG4raohqitVfmIsE1Psd7hh9t8xQMH5JbcT3DQr6AbzzehgEbvc1/Yy95afYRPzm41wr83s7JxuyIVumggOWqZpfptMc0Zub2N4xWxvruSd6KqGgBqjUTjKjhuJyPCFDF0MKFs49CaV8C4MiI4n1pWjErS+/HtVc1U5bN2bbuFbSdUBfzvVnYQdOl2JW+A0OBH26osIRpTuFHByDi4QDn8qIcuVEXXbkw/jI7BJpMb7YVYuEfuY4U93Jui+2Aq5At9JGXzWa9CTsDfRhgxfi4MWpOLNd2NpHoXjRbDGCo4Own9XSsUntl3HWyVrFH5TA7agKZu8lczyr30qTEE+izOsRdwg+ItpiwoFAeXNaYdTK018Ov+7TbGKt+Yb+GO4adqgJlofU5mVCkMcjxfw9ZHJK3tPYXlF7INW7Bk/Pk8CSGThBxiExQPiK9qrSVgQ/YQki8apB6KxS1C2ctWGhJsjujVvBhpqs4pH00SZFMSM3I7hAU9drxw2Qm2JmrZrFGeqPEE4ofRtDjlaszesFkl/Ihe/LCYiAbPaEc5gC63qu9UeI8+q8TnjE0HFLbzuqKCLBEa3nbw4aICuSzUUkh9HNPKdMUnvN7f4lx73aVXjvet++FN4/z6EJzJ/9skGWpwAYV6YybyiaQFL2so8qB4NEvv2TqGcl23TSMCkTw/ojJ/Y1rE76DMc1UKHSO5eJeVkO76VTicfz48vHl0fbq7OM1z1C7/ftv59PPenkukAKFKjBDFIciafX+Dof994ZW7WAXAw7wF7MHoIzU4dtdvzayQF+YS/NV+Ctld6e7RLpjkdRag3ST7FefYMQu37uf0+Pw4u2p5tSx4cIaiawviI2R/h5jwOMgi3NUWAbRiNqI4Cx3+CmiwTGvgfZ+am25BsQQS9uqAc2GbmiLwZ2K5AZnoiTXXgTh7ltqLa5qhc82kx/YXqauA6NSI47fl6eNB7vER9GmTOpP5GObkRa6q3tsDE6zYT9CT1E3Q1Guc3E1+iBkRl1wUhmgLYIoq/BSQ7VRPVieCVqzIRyzFsvPMY/bMZsXzsCbpSKbgy9BJAKbZhqIHI7yvr7LyQ/tOlDH/ACK87qV4oVetUXf9dYOMbp6tXIRb607V8fXpYjsR84pc65Fjv9CHKgp++qtU3J/OOoUfeO/0s/Ff5vL2XNB4gu+gTi1cS0yAwRSizUE3gvb2f0LYkyBpWJdUifZN6+2xVggIEfsBMCNYaNuls6t+f/oDDu3sLFD82kb7+VtiwOOeVgij38mE+P3V1rk19qtuasfhySvzlvkq5+oxbkI3nyMLFYIxjvogagIp9guapuPWm6vw5kCgGEgIHwCQUDjN8WFNWt2yfKSF9mpLm5leAdVSMa/VstPO0osTVKdfJXpaEzWms0bljCwXZ45B7bEBKWTOYjLNIhuuSOqlpruGK422h8OLke2oYrVbw98qjqxa7VjC/qTGgclc3FNly6sdqofDdWkHent1Rbyj+h2k+8sRDQau+VQDNwx2NP1oeHiO7Omw3Ujg46l0uct5TQUaybh9ThWLkNadW8Tr+rN25m21DYVRCIzLgIbSJ0meIPCJCSbQXWhBWlIhNKG21GxUsBcZ8IGZ8LwZgCAMu6qmj3D8UetLKynv7C1DSxB9LNIWQzBBqRHHP8vDJpGhhQrut9tS2Nbd5ZndVbLk3abCRfNqMRJDfCpOx5OLCT9ftu8jcJ3K3spixalr6XwL0QDelDS1+kq8vh17SdoM5ER5wy0/RiRq+Lv5IopsT9fRuRRyMci1jrntnhmKHA1nSbeBLxGFZ3eDDYRBq4TVfIzWn/24Lj/50VZyW41L53k5lbdc7dQS6W6VREHtW0aSHhCd6oT66REqGr1Qq80ev5zd8ePC5++QNk9ddT4pMDehpXfDB25ck5Jnf+gb6OsfFRnouSC0B6DbHNX3sAUEV4U7UpRnBtQu7In2cNzMBIFGunvFu1lq14MIwjYN6Rkz/sCt4vFzKZx+4DlzLsKW8Y4G00hAuKgkSA0Cf9l7T8AEnIggpR3MSjOaaOPZvdk8bAParr97ePXBscHI7BRyAcGu4Tm5pMe6VN/vz7Pm310AkPnhbEllF0fbC28ypLEOw/fxrRIAmaJGO9qhzV9fLmvF8n65EF5aV6xHWnbuok4gFpKqIzoYWWcn17oSUIlEIEu22r0rQj7U6AAYESol1eVJ7TDOJg4D9Wt0/ool04OZJg2hTTiWCLGPnhIhEiQYKzeVCPqqJdYD06ZYIEBsgg4hIeVPzN584frduWuK8knszbA5JEWZ0L37pik3ffKOIN4/a+qgSYn2MW9tVS9bZRv46pgM8Inth6akD+bLfJxN22VhDLtmlETyBaMM6GjyPH3JpVp/staraoDI+cShO471GbCwJGFHL0NcMSd7NoFdh0RxydCxFiTgiX+BoMOMCmQb2EaTcUmA1PBQSQVRHYRyd2qu95uqnw6iPIb3g0+gOnaHnIiaZaUe+5ordya4zwDMEjN3P30iFoxMxC0sYxCf1HwsWADlEIgYIruHLb4MhA7e7Bn3508VDiT5uaCtv+f+jjFjiBbVKdmeEtX0aLzh8CLcR+/PbFt72IfSSROHXR7H7cC/UKF79pL2l/pNX9xyPGybvtxF/yRoTS+GFCjyIe03RLacxQYmhtsodzJgJeBa9qMGxoDSSkUoYLpo1Af1jZkrQW1BESCaa9klqsj6lBzR6I+zW+AJQzbILeTXwirDBBAAIcF4DBGZxxeG9Gv//Nps1O7vmHl8JtHDM3kmn7qjUxbvw8+/07y/ByjzM2lyNZUpw0O/DzkIU3P2yMLKPy2Dm3T5Roi8I1EXhx4E1wGNijid45wolAI4pYCChdSI94FzvYUPplnvpuvNCFEgRx9ghuPLkiUpW6rHcoJmSlLtcr6egnI5Rj0Bg6M831l7mG5TGjbYMJkF6PIZbc8Zl1LnhfnMh1Sn78O9ecihRKGDHJWo+FV2iCZ6NLvsMdI+JBD0o5OCdEuCbyrMeLt0eYs+fH2daO+p5jchY63gpIga4ZpPIwqmOBUt32DjkFBtm8k5jDVgiZQiM9Zx42Z+LFyrhlvHdCh7ZhiCrOMUfbGmry2kaI2C1JZDtMszYyeVWEmEN2I8X4gp8jusTRbgfTITdRpN/PypPGSbSivJ4Su5TO90Z4cmxlajtx2PhZeeqksrKuevGfXnIsqbe0zCG193S9P4KrVZFsuzXqttyLG1mt6mqOMDNeD5vmyM3iLq8TDSMZTvUgSbEjrbneahYxkf2oAnUHLboiK0TuDtX/1mCJ7w3i2V8f6FzQvE3hoF1DAO4VT7fer540R8D7+XVy5z13K1lCNwRrhtj9ziYlWkumxrIaAALyXEpA+oTMVjxfxWc/7T5vXpeJLRzKctncP8LzjMLMClhCkpJ8vFgbpzLrFCa+fG2dUuLJXlfNAf/5l/48oe3bdkpLzpEHowqZJdACyY65fUX/CRQg9R/d+TUbUDa4qEI2/Cm5WYOhOud5FO1j/Pj8zQ0nJCqM4URsRU19UlVzoTgGgVcUNTZdRRPhxZaTrMINFzB/667XqdSMkZBqdHIMtuRjrbTVFcYYeIe8MFjPFfCd7nQkk0Whzg8u2NAN/bNgaFQZqyk4zDZpzRRcAJ+Es/Ed+EZHtuOl///cPyuiVFOd1HGOCf14LxmRZehLESYWsjw77zH/Q8ubvuEYYyZxmEC4pP0Lo7I69qgveGp+1Ai+TqPwDIML6+YYuqmXOt9fJ7bp2CWTOTWw6T+86lHGYXT5/Dkf4CO/PX3gQNVtByauxyLx+3EaZHX1uAon4kS2y2yWWArYk47ixD/eWicPLJnFVW3/z0mn8yC5p+nn5L6fV6r8qmH4633xe9SbZlyr1me5R67b3eWEDCZLb4QbxaMYWK00I08UvietXZ+M21epLNMxEDBHKCgyJMOocknKtYoaK1TGFPV4ynZi0If4ZJUHUBLcmhx5zVDFLA8i9JKzl65FbyJ0ibf8TBxVO72YVlkzAsmQATIQgegElARPdviYwnNOigeJc70fb3j9LbpC3DLt94fNm6dYTnAcCEa5hpq+lxp9TXvY4tw4Ya3XTnjxKszxvf0nb9FrU3pY/ckJ1F4/ETlP/7lbV59Fn/pd+tvH1AAJ1Wpc5hiRXso/BQ+dkIlXzVtB7VW2u5fnO9fYL2VF64PAdWhupf9R3V1VTdIDOmX0sua9FS3FYOtRWuDs0EnskTsEqbSkOsC5qQRapr+4Htm3DRMvAUSQut45OghFRnfDXIeUObhfinM7ZYW15d9LrNgVshTV0DYDdlcufXhoxXXLkIcQ57BT5nNxS+TRxzqEhOoH06Y7uI4EMF89HAUcqngVXtUm7Uiv4JzhGAJnDUWhZMZKUlfY7dg7M6GApyzIGCo0ijPdQ8jYA6J37oJjrIOkjT34CKN8VHhIW6MSJnXTCHpFZziPGJdWpmsvLo4p2mdYHhGdeKK7ZaB7RclLTl3ANfiEZnzdG+PxYYsSaK2zi56rp+O1FdafpxuD7emGGlBazZ9ODWHCasfoYq0sl1CRwjS7F3ejO8nDSXiuXQC9SCOhCV6AoDwiuNK1i06nFTcuzDrcCzVu9PxwppBL6UEKQSpwoA9yueYqMsd0gNMb4/nVcQX9wWLNrW2TbOzMeQYgNAues551qSrvWT6thS762f3f6jr7xPwGBQLZIx5xyP4U2Uhpe/pdREtmmpK1EbZSIoc2WpblKxTh2cksBteVnPuYSKu5SUARm3u1601OSwQ3Tn9EErnVfSgqahhR1hEk3S5NLIUmGTvAjiRIx4DRsvcPyeTO6gtRXrAAkjeY5JgnBUw6y9GcEvJoXehAphiLVGp8Y13XGuJpj0qKwhJzIKKBbuukRHSbKldfFrjH8zjGzGpGHBmffRAqblyFZtVkc67LspxLtgWZTZYO974y9SF58sm2zqBAtxshcZW6aZu8sg8aAGnC+dQ9e5y9l2P4/3S/5sQ/U1wwAp71cMi5D46wULJ5sZS/LbEfWLlKqdYLJDjjsa98cDiF07NYNRpCFwUWWFAHC0/iiUzg32fDc9aJgJ5C3+6y19ob39puw5MEkUCxykqTRXu5Tf7CW8MwtOxHvKGMf9Guof+vjU77zz/S0YEGuYFk0RuwXRnKJde2lV0cFod6J8QQ1Mncqh3eeKMpmrqrG8MNparyChFo4rPAbVSLl53ArYtjn0xjxN1/kK23CuAZ1GHqya1N+mmr7uZFwYMkgs8omyA+RYvRJQspinP52JJS0ByoW8m7zo3V2bCJEQV+TudsNnN3ZmMIAL8W4wKH039NOfyzv9svuFFpTKd1BT8R2+bYtqCxX/yLQwCpo3tSff5HrwJrMDVjaovNK6J8+4kFYc/0ssfSNrqSmuS6KCEGMTwDnsFbTaAyhNaQwAIgGWQ71yCrZ3iSqEg7pbVO7dCEJCTxJG6+gs7q6e+pKx43avdT+DFYOQBtL+rA+55eRn3XSAQY4HfNaBpTN6Q66+W7NxcXt678i+v4V2OAZLLp7gmEcY17V924xeq2iCBzPsBfMUvGsCq8xyyz3FI0EfG7zttLDkZNrX3ZTnLsYSi91w/NblzeYAmzn6bBG1/rayMKdxnxjRA6OAb1CTZT37UhtdGFBj7AgRxhRrC8srOVhTz35C6BXUWlrfknQNSgAT3MIiQ1Z6WsE2K5QBGfMwMXK+RqoAbYI6p+os+gas3Rt4QwSNcSDPL1T922pXavfumP1Se/+5oSBfo3H3Tgj1hYKDTMIELLxtTN7b962PgddfqDdeeAIhbeAQjsFJgJv+uElJCfHP3oMUdsr5AtbUtUjaE7aXMjLgIy7FjgAAAFkA2GpI0mtMAOC1igAAEGMHuSMy2Ax0N0BBsWYgCAUqD+YcADDzQQOD0ycMPOta9/77fTi5HBFnAaC3NVJZtlM8I6/id/dkvp4Ts6sN9VBz/3lqWZ0P+LIPnG5OTSkvePUKoAGDVNjIUKqL0+AWlTgROPy/oUsIQLBDAbwDKsOdEXUhq3rYr5v3wDxREiJvE/+5p1j9R7PLR8Qsj/Z9EzEt/OR7/h9tNP7u68Kk/IeR6MRD+loq3dfjAeuBegnIxV/bj56xKker7ObQncw7JcBCaPCdgJnkBHcCWUkUeWnfGEvoKKZJ4PPg4LHBmayWzQt5zs5a4X9m5tIOl3ObzFlZMv3Ea86z1dbwLguTRlmuMFrRJaJJCNcZcLa22L0NG/ERoRxfgPHHcLByPYe8tKdQE0tLgn3LkKfn87DoUdC/1zFR59u3Ua/A02vq1WMtwhUQ1YIu5RiRbbfrP3zwQCJ75mqvY7/duPL2uwqecfpbP39WvX2+GHv/SeO7k1mfek0TwcEz/MDIqfQwU0UY478YvjiDjiBhWl35w5qJ7XpVsrfyywQF+Ln0/7eRdvyq3/0n2fl9+bB5uYLTEPfS20uNJb3F3sE9QZUfgQ3Mq4Ne16qV/5CPT69Ur2OazizR6GUSlcM3CBFFDyJLDcAAcmgHHTWrO1VIKBOiApFlczMnC34g6PcFT3a/qSr+9AOrSiNwHZQt68KhgNvmEZtdU67FBTLAGhkglOExgBUMABxVEc3a7zzJX8r8CsReLEvZFALChi+vCFuE2ZdkqgakurlBxQGJpj9A+g/R44MYftadgLbKcHNVjE6/E6Y9GH45q17cWeHHWManM67mXTOlJMRXGD4Mbs//UxYNgb8efGEdDG5ue9bGR0+IKJ25y5WoUQwYSnlVpoRRpBp1dGyZuGIWLL4iOQ7M5BxegMg6p5+Sr57nk54aeIvpT0/J7Fhgwkn8zDqVQm4zs4j01NQ/KexpbgveEULFjVXlV10k3zXiMXw2JThy183fq9J/HX1aA9rUdEWZXFYiYVrE7+mWIFDCMd8D4Mgu0zj1OUpbgXGFb/jsNDMd8WUXHekl9ElEGrPNvXvDvcCcHGSZQNPRtYM0UD0U9sqg4V81I799WAAO3M2hBlQj0DC17bDToRn19H9ZZ0W8nF/DYlEfRS0EEzXhql280WGQMlgdIqVXQnyGhnPrPLc/3Q/ks3NfBuWQ4sZZzzOM0abSHU6N0wkqs/sS1bx1SnijKgGuLfYnb6COXaCr/Dxv073P7iynriI0xS/e9vKLzjKXL19T3BkrbzxexYUO7DMLp94nb/VfayumR5o5eX5pbbMqON27l+0cBWqP37GMc0+iZeDaiLdqQgfGamUyr2ziAkpt2K9Fh2Bb9dJWi3OJ89ZPSavh7XnxYLxcd4PIbbs3sOAGjuCxu1hUb9erdF/98CO32/Xs+YI911DHF9Vy8wK0xn8S3X/jWpj+sXL+na8faI271JQgTgcohDEWK4EBc74yzv93q2n7SveDpfYVRVEkXdDXcruWQ89mEEEgMjB8HFzA4jDWOSjDcn4BTqToJkDwNSHnJ7WnywPLAeq0q+0OkOBMtpZ/NbSqChI+O3rhsb99py8lVX3M0KY+XXwqmNTYiPiM3Mpcudm42JiEfEhmvAPhOz76wgCHp5Z2czKw195yLxug3omMGAd0AobgYUSM4tM/waVndEiPfItZ/FzrrLbQ8nwl+Zdr3QY3lUDwlZUay8bvTqYjZWl8cImibA67QVy1uFrCPnv4Jlr9VFVwnFjtJoO74ckiFhLT/l6s5mz0OofhLlrZlyFPdggUaaueePZU1qj0AyWuyHVhnQcjTnG0aXx8oitwATXRKT15u6eoI8mWeEcXkMA04K/HzsHEKvRuSOef2ffDjSNmu7b5sYOZVte41/jE+U1XTUcAspI1LSe1ByYNf9yTeBCeVNcWBMxIS72OMTmnXQiCk9Ohb8tnT13dmKtiZlr96uGTG4dPleJZuBFhhSGmQlsl2R/9dnX1ZsyNY75vtm3JuTKbzZocNWnDusj3twEaJ52HTcAJcFfgiCA6eMYHevtlYr1Udr1TJpDAkpNFo16p/Uvts18EE1O2sFcReIwAh4BeohHHVn2dZP8kJu4VOrrQlxhOB3X7uF4mUFtPvn4eINzgh3lCDnJljava+F25Y6R6CqhuA0K58jfanazuHGpT2NM0rYe1TuBVcQMxm/JJkkeyjvC0869OyiCRYwHDlt56WTDaXJ6eIGpo8czqj5vB6EpksTWRsoD1LJue2EtEdTbZZ9k6pgdohvSPk85x0IFNiBnDk+8TYDqQLLoceyUXIPZLWGkMMSwoioZT6fx3S9uOzkYbatS4bx2RC+U+MmNWxIatE8ViKXGpUZwTTzaid22eGT2UKpRLPoVkvBYP0zWo0cjxQYmVEbb01g+M1Dbo+ggyWcLNPPJnWFF7N5GSRwE65vmIDMg5hslGtMcKQv/UrpO4715fXvT9fa4xkAQ5WZzItk8cz2/I7JAw4FsWPlRkiM+aa18T6h+mJQXSs1yWjESZTYmsvqCl9kV7bIIGpclUml0knxFJpeojXAsayGMSx44eUrxbnkUUhRN8B7dvw4wD2p0llcr7Wqr00mE1TvK8efzpdbf++GfeUSyVx0JeELhbw+upKLx3Pw37L7kIw8Sfh9CIKTBabVaUfy8Al4SorAivvpsYYpI9eGZWzUhgPef/SGlALcaP6/fNihdlMN3QfNndBzdap2SNMVEHKBhBjDPm6g9J4a2ePmeG5YbIG+ZKJWdqFKzoEgRKgJ3MnX7Qz8Kn9G1Z23j+VMag9Kucd8YLbqQCpHoWPP51D9pMCwR1eSCBG8SDPbG1Yibdu8bMK8f52tNxH2hXYnfKEoPyucJCMtCTlqH1iePnSdy5fm99MibfmVnwjCr+L/6l63zU/FO3TdbqaKYqIUi9l3ku9SHpMX5C/iBpakateui5N8D7nuXhZlIEu01NGrUHzex2eG9dTNuCBnsns5zYHWUJQsbxPIFH7xn49vtSPW+veEspm2179/9jXDmTnic9z61aKZpHGeRMIRJBCEIARwDp5X5tVoS9QQpENOzPrnKi84l9ozYxk7LmZemehzfLxbszDawsr64M9nkyOgA6ybMNP1HWUvyHdcqI4QxKVWcekUpdA8SxP+DJoj2orMx2h79Fg8EvK0EpTzyhFcslptj7q3TUXz3EG2esaiukuFgDTmM7SX5vQg8AbrViwDNfbEE/mKqYMHM6ZdLTB3FaHGPztEVGUu39Zp+titmp79W3yXqYWVmN1OqzhhWTLaNcdw3JttsY26V7qT9GcMCoiv8gGQYI56aeixfJzaAzvXZbrHs/X8Ao1K5BvGnohPKtZcQw42u3ichPv8JLtXC5FnRWMoX9PGz7HIAx+omxYhXE/uIlsifdTz425muJ4WduDrxv+Azu7U8f/kiU6asr8g2ShLfhAIIjlL4COaK6w5tfMcdWF3xpCCVVleg1esuwgJ20naXvIwRsQ2v6432wrZuTyOxvMvDKfKm40o8mdiFm7dVZdOdcvFMUPIlXTLpKM5xMAc7IEpPAqPzrhfac/enQdQFoLyycxWxkQQg+kpJ/izYHszwSMkoH7ozzQehMQEeV9f6fjy72yrqA04K0bj1XLHFQzq9Y/ngc7VwDOza7PePxCHd0gA+9+UBHa9HSazXQ6QVDCRbeAsu1xYpIO/4QEWnGwAWzLq/ZIsCfITdbvbKBqJ0T3rhhEGg+Mj8Emc90Aolg2iGmgjSQ132iGUagRm07/j2JKCd0XRh+0F6vl3xqJqh2GagMT4foJeuB0+D8t4M96ayQz4U3xgw3vqrEXe54QxpTkG2tUXjek0kwXNWUZTq6y4rA73BQTmcBPRL4+bfUS5k0hqIagEgBdg64MDW8JzoPD1tPwjGT07BsRBZuTQaX3mPac4JihXLtb1WnAWNNMXRrKidVSMplKRRKUu1gkM+Xv2lHBM4cvUztkIPcQDnseaVBEAONj6SB+F8HAu3ncm9RHx8OWD4mIUlFe/Lo7nc8pIn+XzwVk7p5ZAXQExF0hIxEdC9RM6Ocm0QX/MeNdq28mFYUSkCf4ty9k5PswPay+iLcFNPos7Qq66eFKAOMmMUB5keOmVPdFljKU3N1AJwHz5Oltdup6W2o1lUAa1+Fb9vJiJVybX1gnpHnj5s9iD+Qg9/lWEBkdVGnVypxVq35HM7Bc/G705RXM/taylQ05TY4WJ1KtW+EgIIo64Qmp3mQnxQBpRZ9nZd17YA5vrlmKeJ6N9k8E+CG5Og9IAEL5XkZfNX85bwZN1NVb0FJBLASGMAQ9PHBHrd9e/I/HRL9qy6JEUReBB39ybdoT4OB8JgW+4nR0uMI94BCx6svRl35s2p+hLd1jo9QyOP5T+UYIOOZTCSrNrAP2sNj2rtwYTKdj6JMRaGjnIvW41dy9xzu0OPQ3NJ62ypIyLbIAGF1kZ1KuiRkGslDkj5apsyy3rEFu1zll+IhXCUBTGN8RY6FjY83Q7f4Cp9n2emn1trl/HQEkGkk0p5p51gNQjFMUo+nMu1iFv0QS9xLgg8eNJZEKDivFF3x4i2J6F3bh/czjlCS7jqhdxy9Mb73cXFzbo+M/ebrv4pSqovbRHEJ8fp/HW95uy8s9UcjnQG3YBnhIJXNEoKnFwf5Ygdaq66X3X11SutzJjimQm4zKd1MCTSCdJreIQkP76jL5kXIhwLxJAC8lIPiVkint4esIJluC8LAwal4QuK8HDFaNA5xPJJRZGlksVXFsQhhpevaKKMjCcv52+AjhF5XOFbKH26JBpRiK+WW4N74vG/gi4zmQd/bteD88qfgB+90z/s/+zL//eBamezZRyCFEXBJo+nJ5usbV5Fn8NyMF43yjYPW0cZJw8NRnZL5n+gG5fHbtANhNxUxX8qiy1zuOJRBfdTjQUQ/Yz52dLRGxT+AfpQFO01AuwERDKxMDB4JbtvaQE0MmQKIWi1BE2IsEk3kojF7wrsGPVYEtXtae2IGJSg70hN2nINfLkiXlA9WO1ltvjEACxtVhkLM4HHLt1+D0iV+V7yOrvdH9WxMHspgoIBiAIpc9t0FefWTiTeaZyPwgxEijrJ15Iv6UNFX8lfAkAIQD+++Tok4JeeiO9KlLqxG07Xv+19nb5OKoGMO43bwA6xcUPzTxp2Tfs6iC+L1JnBf/MV9hJHrjcqLNWVNod/MKoeWBd7oOUH3r941Kk6iThNyBlJAl/QEpC0v4Bkvstm1LBjO/QXzxYJujWIaUqgAEcSFyI8/1Dr9jWx3wle6mhqoqQNawc9KU+NKMstBNbvvHW0rCaHw2JXIBsF3R0Y341MiCAJzgwBlzjjWoQImrGa6/a9uGuRS7EWeQeGJTxfLn21vXpVNI8LfSeG114DvlerlYt5+mI36qWBn27Z/by7vSGVG5XqZiJhUMMMnhrvx0li42TsDTD9W0OPAbc5KfqMGA6Iy3MzX3Y9I+VCXMgGFFJfUZYlDiTuCWtXgsvr7aNidMyH+Hh5RCOeOc0Mid3KHNFK4TqvrVLOuHaf9kCmoVCjsRvgcqtrg1F2dTJrIS6vus1g2Xx2QEt7URDfkJaJFfOjUVfpHnYMHeKe4pUwHfwtOPaWj3fHl7UAN0Zspe+xR55yt3SnZqUFLoG75zSBGzFiD0FVTRQ6qnbL3zLp2ahr1cKnDW9dU10IXRAkW4i0XZbvsmkniWKM9GWuQ7qJhHggjM33xVmlnQsg1Qy8UBXvkP1iRKE9EsFEYwDH0/C4xW4wG3pAfptmiR6w94h9MOZr6STCTLotpv0eurOzBmjoSelQe8iAh7CgmhM7s2PcxcH6q8tovcGtl0W/dY3zqgHey+THjtKek5q0+YGscs4LdnnqD0z2iEua3hVUJA1x9yVcAHOUyG+qUqNMVdF2UKC1RqS6+9du96amNImEtMhVJqnoGnlorHOc8+ABPb6uDaKxy0j80ojm31EN5YY90dWVRNbBl9ZQyXMJeR7/XfegZRblKALhCNTiAlFkFRgGb1k7atyZN4DL5xoObVtZbgGDEqDJgSoBJAFbRG4I7WwqhV+MWNTQzVid2dZ8QklQsDZIj/pw0CPz+vzpRdkJh0gIlpcPCv5ozjUryCL68gjmQmjK9vvGqJAnEEjolkZfaa5HM19UlS0NSrZFCx28UNmf/yTidCfc70tP/4r1Lc3/v98/1t9hD+n8/v/xNvItpv94Gf+AAR8XK/Pc988nl+5PV5aI9W3L49ru9/zdPzmfj9TXiC1j/x4ee0JXRkbSkE12Vp66rKGms1J9CWxmBF3DNrEdlBiX9VMDo/a0G1XKn92rE33sGnTPR+S+1RwRH5OojleSIUh4W9UjBm8rp+dPvuyxd5O28t/fnYRTWZ7vJxVTsXqur+PLI0c06EEMZyPc51xt73bE76/Ef9kpgDXDXsxM70i6yCF9G38YwpUbAGbpTbKzxsfaTtibthsGPmJoVWpn4/iTnMPJ23GMsi88X197ln4h9O1STqTTuZGPEGfL+jWcKJMKnmPL8dRcT11aEtDCTBwnaU/hB9PvO4AOmbUnWpnGAo0wixlX8Y4V4QAYUKWGe7gdsWS/u+fS/q5y8u4HiIHXBkZRu6tSdzg3a8eknxTiyt2TPxrikYDEvTBLBuPEUv5TbyJ0OAag6kuCtEygGy2YZIMYQYCVdnFVcjQyFfLJ/vBPujmmSqk4KyvimQvrNbR5q6p0cYEygps+cDIWxAiXwapLxNsRte94xhXyLPbCl1NlokhZgsFcki7daF9Rt1ZfQj30UBgDgHENeNhOy04zblyWPHkjO3hmu6vN0geWWCWPBnfAUcgJJM42TR1Ve8Q2Auc+FtD9Yp62IhE9Ah7rJJYoHhnpsZ3zhIQYbYoBVo0CJVhexe76FfabFbMpNKxlZDV5bI4Y9X0n+WE1AUlWESvW3qCir4ihUsXWddrbsvDLSzgShxMEIQBWssi+A2G6hcdgBhcaWxm3CGL7Z5ixGYyqWxaAZ07gYSRqsyX8hc/KOR2Zk0DgLr7gkQ+R6oK0wYG64jGkg7/PSGaULb//0ifEIrep1tg/dKv1tnZl8pEuBKC8hu/CD6zqYu0HomVuYJVDnvBXAwgc5LhaQek3qEqwqyDmD2j4n7ZdQ631r34uTMlYzhY+jP4+ZcrGMDkiIUdsYvZM/lHDhN++6ymszKOHXiIAyHsBRYejYcrHpLMHr/xQ6LYfzFyMeTLZGqoVTvHQB+4KpXSb2930jk48tLKZcarA3T4OiX5+q8HfX55R+eO/Z+R20+pQDq8LvgSwNgC7Q8iQDqSd4sj5HD7b7PXp5AWzI5n5xP+aLxCKHZYljMqvprzA4zajTXItAsvaRGOlg6AUahvHhPQ6rMsVJZq+ZKphKi24EdhBabC7S9VxnAa7VVvsZ6tvQmvMaZkCaswJ/dznuVgY0kzzyOlsgju6KCBEhSgiKuxYi9Q8kzNPBY52vXqi3OFq+bT6U9Yea6DyJ+18FFNcrqYCL5q8RIlugS1v9dAjovT1ggcQlLitnO1etlKJiIMLDqcG5KUb5H0beeYomOGl8owVmXtMlBL89HoEmPUmznue/kPm8ycKCVjQarCWcoYriUSt2W+af2q5hh4Uhma9LccnDeViMxf7sJD6uFT9qmzNuABerhzXMpDRbQuqI0qCNHaSaYlv32PyE5ZxYVwqhxDLY5Fne6h9nPaevYqUnsvLzSj2kSNaSjjUipGF5w8nlRqiFYQrQk/6Wb9tJj3ymBmuow1qVP2hvnIoh8DOC71t6IY50jcyw0NC75iqBAV9bLBdzwvTp0iTNlAMJu+JiBmK3wSBwsbXiYbM4RWmKZZIWWMUsjDzcnbx/YlBOmpb/sR4TD4lWLD7S+v/r596ZIqcG/mF4d/LzGXe0vRHeCCD2vzqYNFTgX6lcY1y6MlC4abhiMcLMErJ5+fI0tyqREsETC+/hwvLyNjDAmIiIBoAo9vMCqu3e76GRreJvNpUIasq3OONvXz9S38hMV+VfSmRb794bUnmx0IdLcIA8KVohvFuCupojAb0f9/fe70GhrGq8Z/nXqZvUSm3V/rjGuz0gOh87AHxvEoPMrpnp68QVK+Yp/uTKMAkTwvTEC+ooC3KJYPwGE+dzJ32qU/Ib8vA2o2izD0pn5t539/i02dFl/I7nbJjPBiOR4HssvV/zLAgulPkdmMuXh8U7sB5/m44/D8cucZb49aMR0LVBLpWhoWdN1EoWhLignN0saZGfOsNJFfp0IxfvPyLVVyBB0GIdYlDBZDds60AkiRHTbRxyqsRJIAcyPYShKMgTzmrqbWGbH+bbL+Htx/qUAUivihIw7yvH796zdfCNVzU0VRLVBQN/fl2pFtYUy7PgL3ETqTrCitIJJvCrZzz55t05jc13fn/G8X/n5sfX1hfheBy8W86OEt/KrZ2OBvl1AfOBILDJxlCMC7fe0sFQJJPWA+Jk1p9HPQrX64t2lqO1O3VVC0cECkg2dr3D4f8dnIUi5KsQY0KtB5FpY+n8o8AVsUdekxHy1maFiGk90shR3VrQlX4y6fxYnYNwHxrZiK3Bq484N6pGWgm2i4AQuw+06umjt4flUUC6MenQQ08NdE4iLvzvzPnrl3VcmbtTuZOALy51ixoK7X/KMhcGit/ak5+bs3AHqD3Nt4vSWnHjN1DyeTE62h1AG4ufKkHB0H78iwgDgSWbSJl+QcOyyJKFBLUC5B0PsoSppBCgprmeh1PPlPisBX5t9dPL3vaNdLx4y3+l7pfsPj5S8/u+4I8IIp4N/GDNxgekYG+Huu6a/OLL6H+MW15H8/3DTh19+48e6R2+k/fv4cGMEMpIl7agZvzOjmmICJtar/P9eculAeXMhWdvkiP56EbZK+ccyLSGZdJ9zGwqn7pOF0l/XgueRjS3hruHBDAzUaaCIrQvTcDYwIKhIUJ7pQjF6YdiW+vlw+sRh4txtrhHg6ACbgJWEuo4UmjWvkN/OBCioQrQbCAXwW3M2fvvv15svf/ViJmt14HAtexMJ6oYJhQM1s3P6/fRoDZfeTB8SZbza+CwzOBtMG1r8KBn/2t+72I4l9IHr5brjJGkNVeKYbklCBVMc65J2lkcEJTGe4AOYyOH0f79ZW2NZe54Kyk2DnjKMaP4MaNECDIp7C832CzgDQHLpSonQvN/Ndt6lFzNh7n9eLz09sBx4QhNYqClXD/F1TQ/4WG91Yhtts7XDDK7D/bXcwYA+nZwnZOm6xFs8Y/kuD44AkN+95HgQCF0xxQr3Bve+OxeNxgoeDtcd82iTHHzN1nvw1UyTUlnbVyaGaUnBx8vpO/ADcbGcYwdJzau6yOnBB3sCJmmkA9F2M9FShRLsAjDWjCOiYaACG22wEsaeoomjYATHk4kKn2w5LstLRHehIvpRAaGAPVonxZVpHLOzH4BjwZBVD/w0TCMIzscLcd29HjrzI8/zl2EkXNjn6qR0f3rx+8h2LMh8BG90jcKz+t5icJIq/ozyZJICbrHuL4Cxw9gEfctHfxwZguIITtCSKIAYOaMvrThFuDFZBAlbgw/jIbHTu/JnzDNt6bw/jqkyFyZ1sHDcvigFPejCwYU87HPiJJb4uCYLn7/N8tm3mNOozlfYLC3Pnwh2u6Usfm61dRC/1lJkWjyX7rw+u5tsxSwMZWpleue5f+WTsILdm/+CGnRKHwCSWWJBQAbSiwlKSAccQeG24vyLofgfHBWubcCTV7pdsSu8iDGOa9Xhhx4UEz03sIBcuuVuPezsbumT9OOIoXDVaHrS8jVz2jsTqgpOh0xMOSdF4A5rkAJMWNBFuJq6RsikK1smOyCDlm1pYkkUXpOtktw+L5qj2MM0hJsZISTDbualSWJN6E1biM+8a3iDVdkKc9fjAcLuvWKZg04LS0dL9g4YNtpZUwJiwZD7Ndcng38MSleUB5EZjKR6muFcMiYlO/gAccMI02HDMCMJsP5ry5xieamFY51K5IuK4r41KL+Knp0p0Qw+mCV4BBnL97cxGJtRLdqOA79761WgUGYjVs9mdjVmwqwRdqI3E4EXR6Uw2x/AoOd9UVoq7kM6LmQX0oljSZoT1NtytbAa8ABTjByQvnEyeaqPrAGfL49Mj2AzjJtT08Bdxi7grggXPRRHLAhleiazM1Guiy8IE7Qwzo5aYKoOzTsIAvK/nGcHKkkHca9eMIc6IPEPvqDMMIDsTOepej1i6fIoqdmO2CanuUB+NytkZ/sBqFMUjIrZUfbAlv6EPNhenBcAa4htPEwwjpxXgFVHvOoYhQ4O4RtgcuRtrbTQyZSng1IyhmSd4GU8NZMWDBbigwXE49m76LfnEHPhhIxwz/FJAArnItniskAS5Lg1WjMqkHdbQ/0JouoqZYKYw4RDoZ7mgN5XZIIlGNpQyD3iFzcejtUwqHs3UCzWzZ6mx2EtVgqrzeDntjxlFPTLMAYzuiGWWeQLBcjmzIwjnKX6womjBRKLUsst6L1xHYux7LlgSgA/X+9yRch6XuzL1xaiaD14IEpDHukRweDEz5DpKP6p9jpZrGRV1lnvQqbRHysFsYo8QlAfSZXQYlmIeVPQfSY5eOi/0sqyQm2mZ+JvUichQ4sMp29chOQ37FOSUAUzvMr4yt3t2F7maQ7BQR+2hVnA60dOZbRZMkulhtYJbSZv3Ryh7MYR/usLlSp5XJ+zyxeysC1A2kLblJ04mK2jkKpDXNT3eY/D3HwI++lbofiFTPraPK5ubMRBm8B80+J4Ke//6mpIgu0iOtf20rynYmpmaZidSbyTzeAw+wNaEoIdDndeXOg8XLkrZJoDoRlI2v2eTn3OkmpFzUck6oQTPimKfMNFWhONV1DELbFkPmuC1Gs0qkDxZUIAdL8IFxbhf8z2DZfcLu7OrnM8hlOaxgMQKrCMii5PazzjnoIiPnfPgHxZUTQQTWB3NTHAb4W1UdvGZ5jFBWG7izdOngy+2z4UojDVduRWqoD4fz6e+ztVcHt/W04CM/kOBXQycBgUoOlgfxAwqwbSLkh25dHpWTFA4yPaQWYu7MJRocE1FV8Em10JoWqh2qLMxmSdRo4wmesyoBFAa1NyrQpdGLqkzBlESEGM+ZSGLyPWwmXwgLvEwbLyKMP2TPyHQXh38rdjJVW2pXM7GFHZL8vnGUm5SQgkzruUHpmUulWeRMB3K1EAPWOl+toI1h7yJoRMMA6KyN8eYFDnR4pTfSPYcpLdMrJpJJSLynRwfQ52IPcL53uzZgbo4IIAPajwLx7yagT8R6R/uTzqenItQsezLbfLFfKEgnBCIVKC2d5aHn0BXo8AM2Ip8TzAaf3D5Q5rW6CyBHG8hQZdWp/JnqeNgD27jNV/SBl2wW+8EHU48kIDrehoa4aScU70dH3U47ovzVyRnlADjzMDTYCtyyidCb4qDGOdJdblkGub3SJG54DEs+IKJDHXMtxO60kXfirz917f5K/cxlBbtU45heB7ejKr61YQeoR0Gjt/DjJJAabFykt3TxY9ZfjZ9T/ioQkUvGGtbt+34J7xUR/BEoGnSkTn5LTl9iFo48Ec5P50EVj0VGo8v1YPBMaRpQ66h7erJb1LVRh2nrM8W+FrS2rIB7hlNlG3VfQzb8lmbY0QmOE9502wpFV7TmkyRLaPtE7j7gBNwUWeb39tdddfQphkVsM89jmeaR5Ao8bp9UcAY5PLP/BfSP01+Javoqhp4FsXq55s+QQS69Dkgmkk3/GjNrrugQWQfWhkEO1YF9Dz2aokicjSJVwp5hYUKTtKcqBBADhLyIlcqCsLaLJZW0VU1uGYIVorltYog10JdHv/jF534CbmPTtY+2No174okSdQPLRMGcyIjp62gHl8Fxc7cfJB8Z8PLQ3OKUhQrvVP/TIYX9WisavMn30STKUllO4Gt+BkLXfSKp5B0tmPpiWMYl4r+ywhHEsM/+sTdwyn8LpWv9f49yPIXN/9KVqb7lgdtpHFNLkR+CRbABDYwEYpgQA7P4hnF58efLF+RB4L/OP/tCkLZ9Pox8nuR4+3BXYTH93jeHECk1/9yhNblAU0Z2+1draRl4j8F0s/YF0r63dYGXp1E/qf3ubETITDwB6H8lsiU667odGvq6A54E8bHdGYaTSuUFUS/I6O8ljryeW5v6omRO/QHVxQo9RDUROAlpENpXTyW1IVWcGMDGrEP1CuBehapWprR5qzFPfNe7YqEVkkZRRXlsQy/GmpaSQdePd7m1mko2LXKUROS+pMueQOvsqCNqtRLAAM77Mscio8R/X7n46faX2X4U/K/f9I2Yb5Rhnxp9bSkEl79D1rwprCRhN6BPCZ6A8FBeuwgpzgPr8IX4Dg+jc8UMwecvfX2o5ONRf0GxcWX9Ei3j0DQZ5xS8mjpjAOBxhYrR+LpbHmRUNzozbNbb2Nkxp0QJQi67Z8lshWKGslxregssm7rucIdZkkiYUi7pDwVaGWk7Rt1vQcCb6iOmXXyrBKxgrHAJVPk3bROUHDElLJ5XGyBwK3J7NNvSSoI+FdVZquQLEKUNn64QyezHm1xRvoOTWpNS4qaEYmBwaXWatn3fBGdmQ8iIHPz3XQiMFVig6CryVLpSQdz+UB255KCclis6clsvluUOPpZV0oDHBS0WjSd6OQ3xqx22xplqYiIewh5O+hRxdmQJog4bq4qyFdarpcq3EtDa8NLjLScNmMzIXJZp7EqxYqqlSHDF0Lp8YLtlfFOqsNevNpRqzClVpxJJBwHnDnhHxRGnT6fx27Q3MiS1FTXUsaM6iN1DShxkTp0/LWWzbszfy6dJnJDqQa81Zw8FqMaZtWgcshYhbKKFGdE6JI3KmjCEZQ4KxsP1Vi7uo1q0YUgMiDGIB4hbVUvhSp6W1fSis0HuSm3VD/zhV1AR4tkA9qKzhjdWXol2weQ7qXnBSNlpiFYQMx7PZiqOXYqMxG8R9JQWkWgtye5bBsh4ZbxudlaX9bb+pLmQx8/9FiuFH9u/eGuu4q0AfQEsKCUFQUhCdHlI+4djmF9yLxG+JLuggh8nNRPZ8dOibLxqeRqFKgHXDRzm14uI6+Pa+vbK8Q1M7IRH6CA9prOajUlKBg9Ay3XeEOvVS4W1S7pJR2horp1gpceYX6HjaZfThv2vPkeNnUgDnm6IkVWZjkhIrClX+4VwhSB4ZPk4Ja8qOcHYpACkECJU5lkwlQ30vRLGxNNSFfG2Iju1hbuwq8dQR7/bdVxKDg4O/wm2nabM5bIxUht1PKihH1G+MnJ95JDSUoyH4cQId+TuKMP0/2I2JSvD1J8BccR88o42UfeUVn4ofMAcfnu8V6/MVnjpUSEq2akCQT5pRL/uX+24gIjKb8gcr3+YTId7hK9dWCxpBhvrilqOmBJb8iobBbn4YSk92tHuVcziunzcvA6iIqGgkim3mnyw1kDSIhSPqM/jGVnzyscDezREnHwqO8/YdhAUl9TC7jfyAVSKYmWgcikj0kJTkLoWEFhusuSENEzFJ32GfRgP8y39Eknl5ofruVHjGy9zfaSrME49Pso5Sh9sqf8WgeMDvX2oDGKfBNvu0J0dGGTCjU4yN2koyonoOqenxcooMozEWT/Qe6gIz8EDq6GPpvkWZDdkd4RpMkGLLyk2Hbxwr4/Plb721iqJIr640gaUNuCM4veZa3LVJhWn1dFnmUnLxZfnIikcScq89vVXr/bZHVRTK+8Ga7LcpkSRIzLEyd6VMhj8LucbPSEs+3cgk/E9ijaCh8iasfRr2KswmJhd+NBxuCPS/S5P4ddLy93D6eqnTYl2deF5Nv8B0RqRPtIEGTklSWjj/gAB9d0M8DpVTt59Oey61qr/oAxCVgt8l4xE67Z/xOK5yhWyXp1dPfRNRs1gkln+Mmgn4VZdCuarADV3W2MVzbGXb6bSqggSpa/lTWszLImttiQcDbOMBCR2STljdLPertkWLh9rKg8SsW/HopGbePgGXJVc5R7Lk+mNIahGXihEJxhGAdHOx/fnONTAWseC4WRWelisUCqx+S7qktD103TJ6iwwooK4x/Nc/Gy0UwwNewwLy0fMa08ju6PUzuHnldhyNKrELHMGDyRKidzBEH5I++fTHB8Q/LhfIqAyJVN0FQgYYWH2eii3O5qSbRp3yoklqMeJxmrFfnOeTs41qwg3WbwPR4mULQ+DRIRJ31EC+T5DeobCFknwpF5zKnCRQhq9smEAXuO3/bhoJux0q9faxUX6QtBvpUTbgqTFYkv+A2TyTjErULhyFXMKM4959RykAGWd51qH9Si7VHFPg/EF7Np4gjbpSC+wmSLCPjOBYnz5PnNA8uhj+XG3L4AJMFpSCHlY/feBtu+OSrrGS+DNGNMuPBcm4YKJvE+FfpiWB50/r3GQ/rFPeqHe2e+/Box999XjABoZXKx9JZedQDg6BCoqspKuG2xEv8JLXJUHoXxx2BsvFVEBQdbJ84gGC+Hb8MP4A58z4n7b3Guh5uTPqK+NhYvHJta7H2jc8rkvbx5piSskI9zkdsTDvHCXibElpUbrk572cAsCnO8L+sRV58oXOFl9ecBNCOxgX/lIKJt52dbFh7Oqwv2kMqYj2Uf/UDRWvxT7i/k8Hq/hqrQZEc0XTTNsZp08r/fAqYSTw2TbUTJ9u1E7Ml7O30RJybqT7mekV8vST+Zqms5MWPbW0XPlNageu2cI73S7l7+6ldtjfyW1c3BuZXupRK3zwvbtd+3NhBBnFesVaU4Xmy02lq1pJmrYct7Y3J5K8b+OaWrvshhv+p3DRAPpDEzIsCyr/uHgb2rDh1oIT6tkGkAuV99xjjXvau+kluUdS1kj2isPh2koZjd/BqwxXc0UU4pPhMXtQezX984Ii9b3x3ME6z85npXMRNij5leS/JRsO+pH+4Ao8mwAiio5jV4FK6Oqvz+9t+TfiMQP86+zvRAhrB/3znid0ee8CSxhR12OcKBnY454J8BQ1DC43iD/Ks+KCCMFmk8pVB5g3+6bE5LZTwl17wMmpFFYtTgPU02OC0a0jhqkQr22okJr6ler9kT/k3BSOFpCDQKl0f5AE1gLJnRAR7EIYM1O96jIVeFDVU5RTODqiILE3K1fIKrw0ROaFy0CC4TESY8BuyolnIyXdlwwmaGGoIJuiUt/nOMFRY1Tkgms3SYf11aktyj42xGob5vTLCSbEBbrmwl6W0Bt/gQRl8MCDTVBPOzhBtvBLH4B8S5oppkOnbvrny64juIxdH7Qvr27yoPdlO/DL3rgcsFdMIeVA4lvXJlJwgUVk7TDjfVpP34EjLcLlMkGQMiYJ2yHGuOanNEDrsc8ULTYMqpUEEb2lxI0MLbWN0DRNC7jr4qwIdztTi5I6UL8bVR5i62IgX7yRRqT4IL9Gz+BR+5yXgamZkhMXNm4No+PJ/PEFjnx6rtTie0pyyO0dECRQHFI06uZIWKrtkHEn8OVgFepqsp44HS7Rbk1UzTlaIPcKV/KIrM23t9G+Pt5HYmuEnHVlUb1FVXJVVzW4O6dY18m1sId9tbAGPVfXnNiqrFg6bTuKh0Hg6CrIPR0Pljx53qKu1J92HdYvwL8YygogXckMzlah4sQi4sctGh7Fl+8KRfQpSKBrARliB+WoJK5Vqvk+PQc1Ove0UHLtEvCk3d6sWJ/BzF3v7y40fifWjw6txUKJb08EGfJFijucngqVqtHtIG1XbG+3761THZlICD7sMA9X5IneA8ZGbi0PyRSSBYXPmTMK4+WWFZkpUGQ7osz1z0Pq8O9sty7znbuv00i3e3fnU+/3FkKtJdZRAM4II3eIVnPeot+lgPRupdliKGtxBY3jogyHKFwjLbKszJPeO6nRZ6KQLZa9tK65zaaaBfJ8NWeUdI+DDLwudekIXQOvY5XFKv5w86ODTJh9z6ZUKqIJFdn/ReuYhxnVowexRLrCLcKZKgzqUjFFiyhr0CDEjQs+DcHwBqhsqg481KIj2HQzhQW+w6daUc0bYocukmZej2F4iUldpupp8wTWCw6TkFJDm0yL/oG48xsKd2LMw0MWAEOgsYASKTjbk540bqHYMH8BwrUnhJe+52eBvdU6ljDMyKJ/FI5xhNi83ynAVFdaT5JKogH1OXWZ/ZEyjAAMNRHFtkq+oKMtLjTUwi7qddEgrZh8PwGihT0SsewNODG0EFJJsxQniJIu6fJyHYZ6xoQwOGIQ1lvIBn96Z+7qHb9wWzZJn3XWxhix3pu7+q6lq6wfQvTVj4z5zP8jxlrQTPa6/BoDE3ouHb6Z5UT180H0Pog8HbVkNT4ZdMyEvZfLVRrU3Z9HTpTko4TTMYlh+93EcoAShprDJs16QgPrEmU8pcTLV7H+woJjIa/dHfk17D+OZ5vqmKKOrjycBEU0X0i4F5ebD7iqgV55iC5qPdgRB4Xp0qVRAQyn3V/5yLZuHr38fCTPiIShHPOCIE9T5MqgxcxaMkjZ+9q4o7pFEnTHFCcJR7KJCPFHFvzswS7xz9DMe7UgNHhegITgTWromnqw/IBRL3xocOpNufH4XzbUieTKaTzOdJYMB7cO6SSi1cdjxd519bbsOv10vTIkpvqCg0ACGk0xRrVjMFtkwBROUpdzt9EYSBOF4+jq5yzlPhs/Ha5aZ0wGOoLwuXCF6VgUa06vzFda3NKuAuPNMXisgJ2LOYhvi6fqCCXy773cKKShook6kqvIhqOBncxyzFOLN5MpilPDq4d92bj72dN6FfTeUe15k5CuWBwrQImSAGW2mIdBiKAh6ieBSPlDGyfzZX3QY/UyZJfldk7wKm4yCY4KVmn9k5+vLJWK5uVVXfZAg++EqAwBHYpBqOL2HhvaQqJHiLFqvBTHwwoqvVN8UNkUYJBk8WmJsPUQWU0mOGTZh1pabCX3A1ikmug+YWiZS8UQGMzUNwKdE1T7ZkiQUb4LMnPKUNTs4Q0Wj6ZGdHADUD3MwcNCrcLd5jas+mHbwx0OCjhe2ypo/U4dzQMRPW3wk+YvA18GuRLXes2lgHtCvoDHAG/AI7eLVVh/KlHIKM+DvIq+twIso5gsuK6nhMtpEVynfyFgKE4ZDGL/TI3PEMG7IUqDALMRjDu3jX0E0S5m/SPgrxUcltlvN+GFa55Zoj6n5Jb7S+GQefs6f8iI8Y/Md29pFbMf+e7v2I3Iry5+7rR0LDvmnVwd4+VQjRxFNKempsEgqqVQK+kLLMPsDNIbCfTqMYKrA6wVPEBzGYQCYsZgmL1tObIR8/VN2uWjING2HprRjdPj63jaku+NOQahcVyTNsHif8EjB8sKrZaN3rObfF3JP9Lh1/PsghcTuMgFBol5AUYIvuydQRs9Qmt5thChLWqkoxbcpNz2W1we2Kqo7WkjWA+Gu/MooISh6/DhnTecfU4Llfi1tt7bPvAV3EmPej4IxPnF1BehA8kJBFOZgV+k9IA/Cxaz/geOvSGfCTOFfz63H5s4aJU0/BbfJNiHsBXpp6aIOV9rkQAogiz3kC5wucN4AHd2AO8shyOLqrnwQ15Y6klS72wAgWtAx661g53Ca4rVBZA+xrITGlT4jK6eAFGNCren95vM5u7FzGAaviifeeWIQfeCvHvpl2fFnB5B3QwPBhAxMFEhq1TwEOV9Gfc7vEXSk9bTgppXEBzveICflVoVFjoEUmfrEZ82i+UvtNsUya44KJp6JZQ23R41Gyt5alv+AiGQVtHHa5z3RDmZCCOoTMu+GnLo68sl/ppuNszE6dlld7melCnTdjbL0edn5DracHDCUyFBjON+Ov/5IfWa5z/9vO2uQZ+mE1Yrbnt98GJvfaHePkOgh55eoiBqYM7OPwRQhJhNEq3zH1mXj4fp24xgKz3yDSfTRhBR5JA8jiRy+doUimQpnd2tsMT/k5rVYmszBNwsN2nIvVUia9NVeKpXO8cDoW3gJKQy+zdYSDOwmqU5EocgJfB4OP7LJFzDmp2r0Mxs4CscbmIY2MPGWTrvec+4BBxpcWrVwUHzN8JwynKGT2ac796Co6ixp2heh2Q9UHFZRO31oDLw5cedxR+SHw+qxP5MplPIgu6BUh97oirISzPitZlfQ6Vd72AqEtswCKwykrZwWuGKD6NPMhndZpfGWM7ZMwT2GeHvxrZHDtdx//jnjm8MvW1XZjaKJvPbrLoZffsWD5oWSuyNIX+gCKK+rc8iFX5fi/e26o7ZHfDycTe/Eh+AN8/1HL7bhtKWj7AtSzqLrmxYvVT612DvBvxyvSMt5yK+AqWIN+5XmmaX3p9Ie9UjQHhH3XYB0rvpMNtFtzrfvXTzcHUGJkBIyUjm7xOnE/k5j48GRFTlVUKIWlIlE2p5lwIt8q4VrfDWefWXXGGRizYC/DH0+uVIkFWhKllfUw9RyQgQUCWJzF2YznFOWfz8EG/ymCj/zC/LY+3NiQD9FD9NyHT2nyMnHnS+w2PXASKfjR/Kms95FCTxTeSceFYZfeV68SigTnrBtFBoJkyFK7o1KGCqLGKGxpBkmJ0Tc6CwZGCFCIujJJztm5NysO3EXirhCs5y7QCecO8G3g0+Jp2wfIBH5HWhPm6AQtFssn6ZGWKy7RqVgXnqCUFuzdpZeRH85l0RwU6R0ZMXppuz/moenYnzDQj+y/f+zdZ45ILFF00lbTNfokR2KybWtTdN4bF5RLVLW9mzRDZy4khDv0IFf0SntEBs966CzS8afpw3ITudJ+F+JT+ZV3TCJtlukxsbXIJus7SzVhhkET7sDmzu93hUfb1CFmRvFYnjHQSqKwAYjaEdm7+KaYJ6+XG5210UdCSGKEGAh/kTiBWGT6QNdQmNkD4pfGP8pVa+jncRKglxyA73Fe0VuZyu4ftEt/nWwakJ9rSRrHrtAajCcBK1J+PsJI5jkvFpvS83ZQbzEj8uWYLInoOYCdc+RIqAB+pe258tnRWgGVdX1zYlKLuZzQPIzeg0oBHk/VMRoC0EgGAcwBH0/EkzxgNl8lHr3Zm9fgCGtm75e6P7ps1F+v3bv4CyyaUa3DxhUKUIc6ol/whhwQCljh1Zdd/FHqUeRqaxWJ0MHizk3tWkITZWi/fz68cyfeUJ7gA+ebCPQRHhsHf2WvbAEv8KpqI7y9tR/GpnXYq9M3k9vbAkioCSkRq0lfw5p0wLe42Dd5Ld5WNr95pruFnXko08qDLKLjNiHNVpng+WYOk4KRN1aDYKf5II2ItAsM6NHp0ZH/qA8yBC0qCEHAMu31xrpNW3tmWofGq2mnUmhiTF09wVr4Wm7T6/RqQi7tunNj+EjKoP6AKqA1mIPOAJ328QaHU7FQcL4wisnXtAhWs7FwQCCRPtIaGj/rBI4CdvBSIr7sTPC3dDPbbEQ8qGxm8bgDBxxxsvcA3jN5dmjwkoB71oy4VSt1xqNQze+NRo68Mu1S19dWu3YwQtOvzIu74vk73hIn0CPx425PY5fGXQ4qrNYN91sGK0FrKGCpZF4tjcSUDD7yDPwBcIX22hZrsO+CJ7hhJ+i/WCgXsvkUs6cnoKufcOfxD6hEGZVGwDWFZY2shU774EcP+VqrbVVMOsXoWqiHjNbi0tQJMEmqu/CrJzXrLxaRUmaRU7H7OkoK7XIHHYhJ2iyM88lSQQhRFPR07v22OAE8o1hxhq1so7V3Z2DVhi0MrUnVuuQYFMThAfAwSOFZhnPqF8mkankIhRbDVJTj/YZx6Gw1Qo5ZwMg1lAUhszkhgIUOiXO1ZyOOmczSsFJDQ7/zfGGeY1xjfS1afzM5W4JP3c0oVbTCWt0HiV3AHOlpVqyLk5zT6yMgFf2u7H2Oyla2trjx+T0dY8aT4s0EJ+EJuBl+hv8I/5bRksSXjR+BXtTr653RiqSbD8O7y1oqvmRYl7ko97h/jf3IyjwNTG0bsdSPGLkqhCUrGmJctNq0UtDhmg2DhSFskguinyTu0sRS9lFB7AIZG02aoeBbR73fd8j4VBjXPc1dJPg+p7UjEoYdVslYMbxSC5pWs8dJCDnmSiucfK50Waqjk+O1dTLg/e075wMREjOtUIm4VFRyetnpRV2bxzgoeXKiNJ2sWveWcM/qugp41P/yVNIu5xcE/kbVSOXFvmFk2xhYeperobAgX1R2u/I6HzjWuDEcv7Y7UN1c7Ie2TrPg3aLeI04UfApJeeGsnXziuqaLVGcJcyegpVAeP06HW2tm1FMUom0a95NNdKAoG1wmHfJnfXityieC0y764vpEb50auL/sRRvyJTRr0f+ZiCu/+/ozi0W1nftHAmI+OO+agmcyLVTMvm+8kQle8FstU0aivFNrUzZZjQo796M+EtBP1quek2fkiAzZEUFB6DvQbVWnfTqrqpg+m4q9okKmlGdl0WyfZ2QZ99vBB0trU+7jqlFH+uUqyU6ep+pkcY7KCV9wxBDalS+iVPJGN9MrqxW7OXpfbo8XEu44LZdFwD+/9G2dve9+X/HMxCGn8MOm4uyxWufonT0N+eLqB5uGVVCaeyqn9AHE+zCQCU66aRpx6aj4cmt1N/srcl8gB7dnFiZJLIRvJcSX0UU03yRQPaZNgMl76/aAGPbYL/Vac7UK6sGG+FZn0foDcRla/BVmW0PHTRpPtngtOM7WThlIJscKeY4Zd88ctHuOL+plPpMwVi524NxxK4fyCbuTbPTDlALblHBSPlPROjg1y4BOuhT4/bwCGSeBFM9jifkgPiExLyyOrNMRzHA0lUlvQ+SEYKrTTqWC8tTHD8H+pNbKPQ203CZCKVKWDK/pLc516wgyrrM4b88eG/IHHhdL0KtbqLAvNSrgiY5aTc5T2/PXFDCmnukS1787+xffa0+uFAyrpARxZkGlC4lwnT+XrCeSRXJMn9G/5obPnQhUhIXlPiXD5/E9WcPDYy9WBuAsdeufCpfZCtRSHPxd+00vniHoxhdr5Q1GKXAUJMC+ax1zyxHdETLQr+7Mva0O4pA9rAacA0oCWgcbPxjIHTYQvfPIyBIF9ffcRKkuG0pHS7hkplqAAymLuF/zLMsKhkPVj3+4pRg7jsohBIvVNdIkKpcsT4VIJumxFSeIRxJtFPqYrh3WHLOer2oESzmKjj+mhZb0uiFMUtgaJ7woyo6qO1f3wGq/JnOfJ/q9sUbir/KoCU9DAEZZ+QfWZ+G8wOObfn4zVbNIJND5OD81Jq/MXTx7hJr06HUPuyRHqNLFPeBVp+iHbefYje/UzJ7MeOrcpyWIQQWnWM+VtlbVoAY0E0SMAuR4Gp6WhMuS9w71BHyc/SBxd6y7L6Z82/0K7pB79Yu+J/Xysib1yTBHe7Gq145P5WSv9+aH497D6xen1Bn/GnZONqdvwSfkxj739Kpj+83ii/Eth3qdDHqRiTobNuMb1fuuvnn4P9cYOuL3gyI0k/tWD7As1MA2zWsvPFKKnX69OKF4yhExRz2JbFNkQVqo5jx8VVeWjZr2r7wGF1OaXajwDMQkbqqIu1SN5jZN+TUTwN9QZBeEcW5yrhP2ln6TCOCmhDTowY1zcVIRmpXZDWe+ChUPGBSmCzFatgtrsGfWrjdFx2VgPNePKsP44P3iOR1rRhmdDz9S1rTZgHcs7HE699FBmqsMfbyE8B/IozwxMI+Vcu10wf1e6wv5tD8qLir0aFldLrdGmEGxxyqH2W5q4ZQM9GFxncS2ephGvSEE5gnszG6Ynd+jZJRU8iRHHkqcS3ixqCt8Kuki9VBEmodv/GOqU9KiyU5RFUrMmAYX+b3a/Bk97iltqO6SGmwWnDt94lhS3knZRTLK86xy0AfeAtI6NyNrIZTJELxsVZ/5VhFHLpHVxHodNjqwAoJJ0IEb54zIK1yyrIt+NI58OJHF+H21IGjWKQElQrfx3hKIYqTzMLiQ/q0PpKc8SsWqw1BBBdF41M7JrNOcOIYHxx9M3VLucCSsGdVJC1bVBmE0FA6HtBud1DLnyW9+0jhLOSdbfhQ4icpA41GzJ88PJ5IBeDmxS1UN3eT7GOIRgq3PiXtoeJoHmnu1kNAlfIRdZDXtOZlnu2y2DqvMhu3HdWy8JTvj4uJHCkGM3JqiytSifj17EEKolGlkFkueSnPXmwgtxMmnzsJXR6WzC7tQp4+kkgJKWuXBejqUMjU5G1CcgrvQvSR5BSgMy/azcJcM4188A7fQz4QmTDT02p0HE7AF34pvf1KLLoV+gtLplwiQAbTYUq4LiZ+i/7zL8p++51wL/bgR2uebSHcvdBw5H3R0zDjqYmhAtGc6KluVfz7b2rovb2ifealc70Iim9s7Bc5Tlp6zIKQ6cR1JLnPSpm29B0qVCLnnDy0v1MonFlKxRAooDsukzi27DloGo3iZxNKaQ2dWSToKJJVquxKKpCjkGjTyljNUle0CYPbaUk0oS7GC46AhnTwl6jyIFEVKuuvDuuolRRQBZsYfmp6mMZWmsgwZKKA4XCcI9XsM1Oh0/Pn1n320g2rKy9c3PXd1gvi34XN1inWwyyPbABdPPvh/P6I3zvz1yTmlJI2wufw80cd4hJkuZSdc/cBdaIVGx+Z3KLXa2/EAlKSie1VX4bSmZh1H7l+fjH5FBLv3S68HZx/hDNuxOacu+FLYlObrfcDjQKUJvQOKH6oiPhaFOwEJ9LUkt8RXCNhPNt2y2hf2QHrxJneCfq1sBwyo4KvqZ5+9rJbPn2Ljqi913Q8qriiynFo+deJ/dAM/hn4dfm6CG8efn+QuwcRcZFTvpX7ZTU89amrFrXwPJbSEvC6iJiidDFOpMe+5RMwgMDantKWFSLqTYGRYkDOLPDeArLJcKDSc6nkNXXGFMMItqMsQRcDjPMbdDYA+KPB8LPZEXXV1KBY8C3/2JtN3nrGRf0X62H2p9eXTyhigar9sEVLwaXjTI8k7Ll9Gzkgt8ldGLwm4UT3/NPFW9M6J4otgzfZ66jkS2djAt3/KF/y2Gw2GtsCTAjoirB8wayYiqla+pNAZEMACCgSO4+QuPbcThrEw+1xH8C8s0qu1n2VkL7dVkz4FxUsZXII1/lC9o1Q5GTui+q5omuAjCSK7IlCpZK5POfn0JHCchDWaNyhbmh7hdsOOpMFdKEIeCq0WoPa8hJd/qeOI8Y8F09v/iqddukKQZEkk5MY5N33BDlzaD4INNZEChcpfdrXqN/lCL8y99Lf5x37+7PY/GPsVnhzkdD29tbF/dmtl2iSSbwRCUKt6n0vslcYUy1VMeK19gbtdSkYfQTbfm1TLcjMcY0l3KauqLHteb5ksCCNcULA2bjfrt+3y1gPwONprYfmGvl+EnT0NNIJNRf/1TGVB7ZXuZO1ZOq1KlJ08X2RP2yKBptsSl3IOBA7LjHnOpYmX0rrUDoVY0GJLwmUDyc0C11pCZL04ZvPIa46Ndb65GgptzVUraDmXZPsia8o0USWr5oHyjNqrzKTQWT4aBS9Qd5ZodkEBM62t0qMtFP+cl0DgPw5eKA5tTXoZ9h33PZoWuz/x7fCphiQDHrO8VkH70BID7yzmMqaIz25UXb01Y09kFA/DPaIy5k4C1DrzUTaxtymdVIZcpnfyi6TJ+32mZ5woFW9Hl0JSuA823+RyvY8MJLNpxk8axIgsEmrISIXUnJLMTL5QKEyLS5k5J4uvB7qPqLzJiK5ilLnqNE3nGu8UGofNRBEqm+ZEzVGZY+yQdVEsEJ1TiqDWuytIFBUyZKdRXUqEU8+0O9MItXypNYa8KgYLOPyMASEF9CGmIXEXmW90mDpiWTwTji6bUqU9DVumEK6GdbAT34yvbR+u14aaYWMmc96qpsMa3WHEiQmqU0oHGfDmxXCGv6rX7x9sOo9+GnXW0QJp3Ns2jsx093bN+oZS4Cya4qLbZYAwh/NpBWfvzv4V/M3PV0Q96pVsUK03ZhktgBiSHLZxlG0WVVFlKkkOjokH7UteXS+VlFCtYt80r218i0PGi88UPPdY5h/1klCj9oH5up1IngaWeWDvEiw2t0iCNn9O3AWGHlHztqQ0Kug1XZNKLRBWKRXRxs4cNXEFzFOLdBPBsbcdemg1awf4t1HBE6Vz2qvkx9hVC4e8WH/pRckkeUfoAIyAhmJYMIyIx+Nmukf6gnxL4d2T1nPII1FTWpyQ/Q419964pEPFiGNaJd+iH+QLcoXCYYLEe81sY5BP5N0F9sIeS5dXl3W6ib+mQBsoslhV4BGuqmrwwyB699tg5i9Mquxg2Y4CJ8yHYI/3s+k12TCMoqrJ94OW9SW5/n2XRmEGjsP34T/wrXhvGTgaz2QSFMSCeEDDq7RKwWAXiknNrkOjAzL8MIMu7V654wxT/HiaR1umkwHLibiI42Sn5NpDmFu73pw67Oj5DVlU+TjwsjpvYM183wFY8IUDobVtVTe/xTOGN2enF/vH2gj27bEk9kyii8Ts6VX8pKzlf3/CYaVAibvq8VyVLy3l+9BJTfDJNzbKna0qvDKibWnCkklvfEKE7lFKzZRrncqWSrkSXb0fIFI3zXZ2I5PU4qmle+tv/kZ/f3xNGQMooxoFiHH6mT962To4QNUWPw8OEhiuNwp7l26r9tIztuZLXs15T7Q8AJN4y0rwUtksoFuBVP0rM+o2+NQXlrM5OGnCrXDVnEaEG2Uf3NwcO7tiXjw7sxbNz5yluUgS3tzG5UoJGV7n4LPRYTftH616XlMh8WjeSY6unhe61ZWZtJVaQLVjvQft3dvXpVfPsQ0LK6Xhb7UfA8pnsv8F3se2aefUC0YlUSUmRs5zbQkNOtg1nwSBxQGBs5kWuv86LC1PfYRX79NBbXYEezUVKVxPkw7cB+Ruk4H2i3zVIm/EH62LyAFrNdUuwlsd3egLiY+dp1em5qBsIAWULDrAYB0DLQ9IYwEn3w8GsPFZunTu5opaWzQD0CYXx4eUY734p/J9rmHNFMTz1P0euh2huoDrZokB69PmAECYjzPIEHXqMbK1y4Ce6W3g1EA8ZgQTjLUC2sa4kaE2NXJTu7+ST0crqej6REtlJda7v3Qosn4XSRn3musF+v/Fj3O1e1iKZTJmLFKc9vksX+ab0qImqK/oPu8LVLbD06lOxzYXl9f30vACAb+0mwLqhhBTnbQJLgQjEuFydjkbFEIbuZLhJSZc5BpLl13t+XJMYQDL9g0iy1Q7uBxnDM5ceD5aDlFBjSz/8qtyF8cFR1yukJWARrO30dV/Q1MZDSLAGBxKtrETytXL8kQp/3UITiPmyQhgOOygmBQsJD7eP968P3/uXHAw6G3S1jGFU8ce/IXC5zXq+lTkPt0cmCck/bJM85BbUFO2yd5EJPoLeyzIonbPlPCcN3zmlWnZNFC6wVOB2lz18e7lheJYSu04HmaAIwbyY3NEWOV4olDuvvFiFQjIxdVo2TJ6B5jdJi/uf9lv7Y1FIJGfXqk/z1glD3L5zDW9V/zrUYdO/HrDXuGSf/I0B4rexIfCLpM9xbuSLhSWAg+opSr9YFI2wZRJTCVNHxloKJWzLtQnTZRsewDNFWJtEtOGFTEf6xlHXyt4pZluMzWhYTIaJPpMNVHiVyHpojbSgnnS7mtKWPDUONvZ15VRAY+yVcLQ2IMgvVS1G/c1SAmMQ3JUy3oEqq4t5QWG9bpshiziSDXdmOqeLF05rokYcxV3yVjJ3iBSdLDDDOhwAo7LhfXb/+KwK1Hih8LLy/X9WgDHtbb0osLTVZlmKsn54/ZAE0XbAPyYmOJfp+NM9DhYyceg+fmJtqWofe0dyi4laNojCAdsUSNrKyqRSQDHM05mt5GqtOpDrjYTSegHH6Do/Jd5qxE6Eo3KM2TCGv6ldLAq+ZM4ZeHU0K7CKTsYEwQrhVWZIepap1UBcx/Htft/VLkL/xnLZj1OqpVcgRr/KliCoMwXO55pPbP0tKGTkt0dO8LvM9MPNkll8aVOw2uqZuNRMSK2iJh39hUhBnEl5wGwSWIcYZQPf1op5ybR30SxClsdmgN7TO15qkpmRCsVEIMHUK8VukDCAl6LV/aBFq8Tw6Zn/f6qErjwTvsSqSskLxpWdcijFJd4CgaXh3+PyDptmeaXkZ2VXpnB+jif3VDqwgvUX8ji8PpI3jIRKegdFg7ZXmpVHhSa2aD3fVkkT4ssWtt9qW5122AFs9Qx/ofr+QrkGBtbXVUDP/l3nb1pcd0RdrGwSAOvUH+fyK7GqHoCgnUQZ3/dIwkcKZhmOT5ia6NOkS4sYZNQoSHWMQexGFmh8L0gCoZXlZq0FbEoG0HFxgWbXkMyNU9hvVKWD2iNfd7c+348bduCqjux8FRYvlGkhryOqNmE5SH0QvXUkcbquZaC0ydON9F62gZPCJUKmIY+CFIAFlchlQW2hl3FHOuhePKkqpicH7tMvUlW1G5Gd/hTMXkkuYshfUQEpTSpSHFIVjyB+ouwX37q4SrYAGfhZ+NnfbFXXP+egdVetVAal/998bpIvWnmBhSLJ1tZLgXsMnSzU3Sz4XAk+0AVVdHDgQ0q8KNLfRyG/36doNCXbC6Yx5WJT56ee+BZFVuKsBXpi1/81EkEg6lUrioRnD1BQ5JKFSCa+BgS1H85tSQgtMsvK0BdpAL9BIoJFW8DMAjE878CpD+SGKYVjv3ySUS5oaaqAyiC1pjqBIiowkkl/VB5DOjrAHLA/CqlMmZDAwg23sUMimiRZoCDUajD0MsUtiY2L6Yp/XLPzHvmNIPgU7psBZGjOzGO4xcidL4q/WY50GCVI4BrtRah5rWSdfy5P/2/t/J5XW/641fa/useivWlvMnMMzDk+aR6TEvT6GbdJ/xmzAJyQrJk+R7aVY86O9hB6VJyunCewgH0imttm+6nKLNFjpNReOEuL6sTHHffJU8GQiBrNiYZH3xLDIp8xMIkauFcAjrMQhsmE8cSR36zGx1P2bzpzWSGUNyxoHzgN5somgllS6WFbjVinSU7aH948rVTRE0eWnZafvPdo87e7m9ks4rSIBZb7BM+veK8nIjS2SMp/VwTJy6TmUYG7XYMrWiML7g7lyE/rr3cKLh1sUK6oFzTNaiDl5EgUrO05Y6tl4KHyNyOQIitvowLkCA4jWhs/4VmYf+moutBCFdGhAyN78uwjUnVJVNk6kLb9gPxjwkZ1typlsJ/r52QYpVeNq3qoj3lKAr8ERivBTmHjsoxxknNfstBsZuOXaCZqURMEfyqKOAJjpZCkfa8DzI5NQeWb40JmcmJ+SDJByU6PuGXI5lJtOauJ/LEc8e8E6EL1W6fHZRQXAltBJRgFl+HryqjBohUCqKghBGYOayy9FHedXDN8Z5VrHWp68ItScUlZwcuDLjEvzkghkJ161BQnmkHg+dSTeGew2VcYXxdGbbCbW+sbcBwvu6G1kFrTwkpQTovLw3yARU4jF8Wjh9acl4+re/qjCwXyyZZ4fYCh2Jlf6CufITxsz6f94V+TstB5Oh43gxpmpix42dfg8p8karEZiH7f8Wn5cejDxyFlZLaZwkU+2l4dCayEjCp/UyB4grqVHzpAlCZrN/pSdkt7hvRVKMtrYoeS6eJuQZHC/o6Dy4EdDL7jLJqdNrIOTF3jEqO3BIzwUOVyLtPWtsM2iofgwCYgAifVq60YZ1Ix01yTU7HgBhGyR+p+oyIK8vksW4MgCg8Ccw5zHOuzyCsfkrM4NGTzzOM2C5PBIHDBIGWAv2j/HeqmHrod35uUEBuvArL9yV+YqiafNkHfv+D4Ox9q86pyEMlvmUMhSCY56fdgWmqV87HZq450p4DEQ116oYt80QO6AUKf5RnzQWElrSekwIDrfRsEBTDZIqEWrBfMfiL/HyHxKOxI5jIfc43vz+Ren/SgcItHi+9y9LtvMX6eK0T0bkzDbx8Uxr86nyInIAQeblsUL1nlhOJS7gGbO6Jxn+PpgncQ4wFngUTl1MiJ9J06BOj8i4RtcnLx6/Y2Bx0J8WgK1tXg6MszKNQfRz4vwFuQIp+Xh5SFgS/LtxW/7nvS0ZLVkLEzzJX+hcn4CqJQBy3EsLJxd1MnC//ehfmCGHY/ph4S9IMq0GQSKFlQkGxocsSiWLnIl5PzAXBFijh/EI5AJVgHtOosP8XJWcL+jBAO22tXJU9HSSbR6HHDjU+T50eLA9QCiScHyE4Vprsn/dTcVRutRz80kw8fGpwt5RbKWT3pA/ICTYx65hi7U/AqBnrNXmDCpLuxJuS44B25h8nMlGsrJMCWNy/10WI8OFKDdJIPGZU7R0XaKBWQgpb9owBCTXInZDailerhPHOFM+ukxHEF7J053ImiyBxqM4Av11PiWzYEMYbQ+108WKH5JDiVXGqDnwiZQmTmTL2ScV6gEY0t7SuYuRN8I2cn3WTarPOOG2zos1ocuO0UYehTiWDDscizj6gsK/84RhObPhvy9fMtib0k2fmYF6PuqIh0O0edvh9jD+xwrzQLxFf4FEcv3uA4CDHoBmNFNJgDmollTAQDzkIQralnKuyQsVGMRktr0R5YSccTVelZhcfwQl+aNs98OS5scQVOYSSb/D018WI8XVs6Y8OVfCyDMWjXatKgvsEn4of/DFtFDjXyzVHDBxSKoi+JTPMegL2sq60hM+wR3WaoFVbzrEEh3UfT4fUDBHfnJOsMIx0dIELPMuIfUjVg6LMid40V9kb2UjSWYpgEwBIek2OoWzy0B6uZJ7QsMCaDB3AHjHHw5Oq84DuZAAYqA0peA2v3Umw7lkvO7MUqMxkUiGP5yFVCa8tJpLJdInuxuTous16bmECerbGayQN+ng9gGejdyHhkxnvLpcyZspUeMksFI57JVbITzb89BBi6EqEI2yBURe8hj3trogFUtniJ0vtydYFAqFcdTWH1mVb2qqq8YCBM+HNfARw4jLZyCkhO6FAlCkWDb9HBQcvrpHH0wxI3Zq2GMi969hmlV1QUPf7ozk9LqSrTxvuwxqszGkVfxvHMohdk0sWJY/ScCtxT1eqSxZthmihAIOjI1CPqac9zB2fcK0FWpDy9F3MlU7ZT7YeFN4dAfUAGEJlnm+4cuscvt2/l7LBBvUiIjGY9Tig22SRL38yDPRiSIDIoP7JXT0Rgt3RrBF5R+SQwcLLeNSzjSFNC7K0H8cmG9RAqDvKOuCNQKf3OBanZmgcpsbnvo8TdavhkPVHSxeHVXwrIdevCas0DH10RIC2P6VIwqt1klGwJIov7YYcf3NAo+kCpeXmVOPpaplfqjSvgaXedTo0axqE5SKS7Iqm9xS2nLXde2pYa60C1OsVdyxBXC6WfhkgSds7tZKFuNCxS4sdZjBxCJrIFI/QJ/VxMSAQ8YB3yHu/d0C/NZAK+/W5dyzrIz6rYH19b3b4vD3b1AwYBucwELZlzLQqSRBR5xRlF2YO7XegqVaAA/26uzUf2L9i/+XXoaEoxCZ+FO6frc+ynfpUv4z5Fej0Zh6TFfY0O88EifEbfQ0OcIATHLgDdzjAjhTFpXHcDP6bqAWoSNB7gDSkgYMknsaTTvredzuMB+gI3ZKILJNIas2DnW6X3g9vnUVZoydU35nOf4+LLLXnqP+1ijLXjrC8viuV2vxgWt0I8Hokh+AQZGVk+kr1ES7AktjxYFBHZq2WCfoiDKfrraVSzG0VRJl+a6ZZSihAPuEHA3d8qkPbn+diTl9oHnNZiCr17sBmOhgyOTTon6t7GdW2Rn5ZMxVcKgUerDutMVt1vZSl5W7LVwE4eK+FzEj9Q4coYW3Uy9it3HFkOSYk9jfjGLfY/qGfTcn4nhIC/B36BTFAmHRQw3XBbtQuIkAdGYsJJCVHzNoQ7t6QeKI4wGqn9wsOUXCpHWH/kuqqvhbn+nxwWmKtiLM09i6fDf21JTzs+vyaq4WFa8Ewvrgb7x2YHWoHM1WySkcRqyyNab0wPgSOhzdvk1XYqkkJdqyaQ+lXEeqzZwwYUIC14oM2eo2cYfz5IbACa/Gh+qZC0mWjEubEyGY+gYVcOQgFYYUcCPTCAnp9U0E1FLgxCAqGRK+gCxOgwRDewKvfj54719zYVvOeex5IOaUI677whpPaug2SzdXORnVT8T6kXbQN2LTXseyQHB1PxzGZPfbp2haaUYE4+futzKSDYtAW21zXj+dugYyvz7+Q2tr18nhsb81D1uz81snRwVRnXKnB0UnkOc41JUuMYpBaJVR9yF7rjufg3Q8tF8wcvPy0iDURhMZ8TKG9COM64vDzY8EgIJGZmk96xcEywD7ZXLe5nQvZdnnm5h9vI2fg2MC0gfsDKvDV4YeWFaH31JajYkz9zjzqrQQRkUXN/YlZbmvKzIWu6i7qR2b1FuCIzESwwngkn9sihJAoNstawiAfl7zrfWvCcc9+l3X6rjvvd/7ZDAqBnMEjVMzuGMMHYEjVj3y3Ln5aCVIWpJOx/+/BF0FERmSESthu+Rpi+5dekzZ7hrpLSXSpkyv685de57WF6kZPPnYhOAcvIBkiYjp0e00U1aJcgxylpGZsYr5glYwSQV9ObwbxOr0RAqS3oEOI4IkvGyEuqJn/slNJJM47ha3ivOzBInUn7dNo5ZtEejXTvoJcDSq9DFSx3vFLqyIw7z+rh/oT/5Bfzf3+ZgleTUpkfNLq1WWB0uKMECX/YnkCqDuHDjML+YX4+u6fzca/LT+pjurUnZlBHiS1WSNC8XGprWilBIEKWJVfTK7xSVJtsjJq5VZCcjZciik2QQhYE7JA93DyKh41a+7IlGyaRFuDlh4tlUygWfyPH+cyuoT+FjsyJ8IE/YxhZqpA0h/JNFZBImZEEPmj55s9cM/IBJYomiuytIrwXjVVM0FQyApE+qS4QriVXG2/U6znFU8UN/vfF/bCrz0JxkHj9pDV8if//VhtlhvVhvcT3z4I3sZkXk2Gu6H131T/Z1QYyInJIZNz9vBVUlNDdb2IT+0hhWheajV+MKnkVY6uUaSqTIMmJyCZCELeU962xAcc/IDj+Jo8uvj/bl2cO9C651pueitWDy3na8GtN1NyUMTVn4XNJ2IhFmiAyvnH5gOlRbVzS7BZstT4EdCU2p1MMDJjR2CHcFPHTiJjQP4hp+u7KYWhdsj3dSdq3oe/9fmsl48IIu9wz9945qNHzCbi77wSfqAjVcjhorU5KUL+P0VmvAAGDrHXMLs1GSh6yQVK2YElZwVQwMANXPcjb6isLZ9NatpzXQP304/9u9X/wiWwRHEJRv4C9r55nq744eBjuk3cw5ymvnYb9VIbiJCVxyAa6d0TbhBd/usTecyXQVcCPHvgRzCYkCPkc/UNrv8+JljzizvVCSDNxsEmvHrmur5T3SWv4jt/txfysnSkD9qrnuJM+T3wUI03cIcN/NCWCbcd0TaP5oU7nFecaKr9KCUp5CYbYsPLxUwaaB4qbA8efBJGYA7fjG8uT31+K1UKsOTHur9aw6gan8h84DeP7zlHYB56YXG8edTsGbt33S9yb/YE/1LSADMS8UApoSEolRO4RtceCr8xdPPLa+L+2n1+dCq5ppVHUjcUULRkCy/PnAIBdljSFBKvCG72iMHqi0nRLSK1czAQhGZzGuBhJ3xTmGJWgtsj2hq05XawxvAoyvRcFitgCGeZzJxvCnftXFCir5ruLPe46049c+lOyYpU+X6LCP1S8GVcc4pCjy3iXg0xZKcCwkk9JvVTzD7ZRc1/ANNoXFhdWTRtONBKHfZhlyYGZl3DbbcF26p1SSdQOgdqrHCvpnFzJvnUgYbxmSZE7DWVfc5/oKe6XdQSY6ZVjFSTVJvH0pmJFCigIl+TO4O5TpNwhMhceKnZkH2/3t/Iulap95hYIEj9Kixdv0u0F0n2IltPV5NkQFSBdEbVaexynFIjwdICOVDcenGWuFqFncXrGWXn8xFIE5QYzjWG0R+2uc5OC6QxQ9OPQl8mYAl2XZhsVdVUIR7EV1OuxWYRC4VxfB/omJU6vGaI4hya/XI6Jfqxr1fIo28DF6b4zs5AByytbSE7XAJWN8DAjuIm6do0vswbEFvQghAXuUgZBuN3njBXsnOv1uuDdOGGLl48SW23uEQHp8YYZ35zh3dxHJDbiZIyzNPNwJoWgBsuR2TyeEzvO6yMxsyvZMEq8QzZd1WnRY6EXL8o26s0NIxmY2+BfKeJ8sonnkV0XNRWfSa8Y7XbMcltk1b3Tk8mcDBxRUTC9ERmxfsMYtPAUfBCGq/Ay4zkNYkwzHqW1WWPMNKWG0unzSNBLoKEK6eP5VPViYLbC1CRNn4y3xHq0h7rC24iU3j7sBWpuovWCR/3Z3BoK/bLHvDigWlPCtL7qZaSP8WDxunz6xdwE3iew6BxWj1OPtsQOPadg6VrteCk9qJNZNsM9X45oyOhRORYdzZlI75X4oQ+qM+h9OyoBDl/MrhUgNBABq4ce23fQvg5HX6GUndzxBQpN6sFBleK7glpD1LMTur4CTs4U6g2yxET/PUvy1A3vTJOmdZ6t90+b2ZW9Rg5wqIbs7Wa3aybbGzHNbuV6PBEnHywxbxaWa8aD0VapgdTGdcGXzAAVsmOtSynE/Z66w8AoZzYrplHFRZO4spt9bBF2XBdx6AwAOBaj4eF1aRJdKK/0gCeCHoaReFd/6hSj5AVjqSjD6m4dHsqWHEqNqZPPZtqqlTBNDTHBK/LjPNDRGmZuOQV9o+Md4XruLhZUKIOcJMT4kNapJ6TYRwXM+kRKx5oM+ibFfMpENcvYHsbZBQeFQ1IOwCfBQo6eAdeo2Tm9n1RqAYuDeZW2JTpcmh8hr07/ZnCgyg3bCuiKLGuUzS9EJC/cYp+s4joVHXC2YqVK60x85r9ZrgHj6I/jAlEbsdn0xgqjfNOU2SjLd/LHU9cIBRqFLj8azMGD+Xh103M192OnXVj+MiIyx3BmdNCd/VG7oDQzlh+O2E1Wa1GBz2EDUWbBCLRl3YPeJVw2shmC8zZs0B7Trz8nd31Gik8y60OjbDok794atkD/5gAP8PDCR/j/rBPQHLF/SzjbbtwdjNCGw7nFbomBkd4cXbOHhnZ6QzVd72R2I4j4XM24EAHUSWuAtGinwvkKau2H3OCeKN9qaSR7g3CqLksbprY5SeMvsyilId/rl5WlmT42D7nzK6QWU+KrVeVccpYeODcN9Ymaum3XgwpXPQip45z2o89H42Jp4K/dDuB+7TIBq3K4P5hOynWhwh/0+8eFUQ3nrDSmbhrifL7S93J/LnLJdObTtDP5rV65IAEluluh1ZolUDPU3qCnSLFg3EjLqZAV08U1D1zBRvLfj+rXmZBZgLY0kx+gTmnFQT3HkI0sUzHguzNug4ngFTOfugwmL8X4U33dVslYW7vK5Rxx0sfgG6gFKTzEnihhNfjKgeyFBENOVO6s9SvajkapUbyYRsZBaaNM4hcg6LjFVGeM3sLE/IE6LiyFECPhchTvrzbPBhG+ZYqWa+NhpHTOFx5FEgs9JhHJKuFUtF8CN6I19anKD/NrQdYVuvAvW/0SFZX+KiimTjplFvW/bydJfUDoK9qcd4bleyKcAkDuE/aUAbPRXj4uFI5s4ve5TccsB/0sZ+VRwUGftkPZVznV+XhOfcvjj42LwA32+zG7VBoVIypavqccmC3fJJhkvLoXnr6Eryiai17FbMmmRyXmmkUXdyZ9RgPUmNyUev1mfoKww8e1OOyj6LriVVGBIYnstGVmoY8ezcO0LVn5j+QPMu1lQMDBGlEDvaSVQHTnqgvsnV/5yuS7ERdb/DAyl61Zt8gTMqMstukcvr8N+Bi7zTA45l6IKfoBZtllpcyrCRQXsFEuUWGztGMJoYab7JhFFO6YnPMFD/5fmCiD9QcOztzMhHRxhaRElfFEteMtIv1nQ3bJPwQuoUoQd4+ssEYyrI3z525bQ7ZmYCRw0aMzwEL+Dx8Ej6D93iD+L8A4L17DzWUp+878fWK5ujZP/ADXoMfG8feqJxCaevt5/jd0ud/+YORqPkE/qtrQBkUQAouL4K9FQpCqdRWObqVYGryfpCg2bB1LW4QM3u/DMqlwAqEmaY/0pm7LTW13KcZMDF4IFIEtDY95lV5OkwBWpuiOKf/BmReAD4PfjiKd+A9N1vS7apomx7x80T7EHom8Kma+7wnPVom6oygKE7zKl10WFBlxZ52j6E0qV33Rae0icyaFDpYx12qNT/JksL4FHqa0VBjSA6+lKL/fSekYe+MoflmmM6fbEobHGTtopyPlpIhM1cvePvs7cWMTXpxn0/PYnsj18tUA9unXgol3rUpt+GGOddJb8OKiCsYyni8LMiGtyrVYvknFWHNXugkaLjen6qpQCz/jnAW7pH33COItsc/oVD/apSqsgWpSMpQgxRDIDjYBRCqUhdFSVf5O6LFFaSW2R6tvKjOnK6X8nnS6/Taapzd1Hqc4KNKd7Jalep5WCSyjA9oOs7V3G6NMi2pxyCIsXQaV53m0Xk75Y/i4DCSlibYQcVr9YZ0BtTLVryySJ+kvksmXmJ0N+zksBf0L2sZes4FCWNr13Z82oTDHfBA6CLRtEYCxRL0nuTuZin3YWlQVCH7x8Wm25uZhGUfl2xfUcqthT5wNe97h9umJ9zinAcYO54lfLOZTPjG/AShrQAmiPOmJbzSCZ1egPb3TANDfq2GcnZ5WxzTjlt8W6JbhiDZ3nVXQ4eCoCGWMFc04/tMXXkA6IDV4+Hr2RqzAH1OhW26FPbDGrgW/+yIc5wmb/MH3j5vSJ2v9M7w4palJW3MPJKjkRpVotbs7WsI39cs5+qKr+q3W/7hPvyaOd549dMDb48BgtZq+/3wQ6456g+TduaK7uLB1Z+LbhaTwaWZaXbCZGXXybSBX8cMCU3wYnX1KeAmxgitAYTp5CVgx4epZvmPIOhHKYqw6EQyHHrB7Alg7sZ5N/VYdcwajGcyj+XUKzOzLTXgE29jmhHVjIIkGoDuDd9xStJdV+BtCvZGKew3ElSEQ6bpboCTX7hEaxO0sqhXfzBFzl5UR+lgJrUZMZrb2iUA3Ocdqeo214JEi172peoO3zwc3i69mXQqR+O9itOx6iUZsUP0NZ5ygzwidlW7RTyQBfPWSQK07YVLupY3icxIhh8C621kAl3pwyAx9a9IJJ1Cz+oNXD30R4LU3NmdXjo6K2pcyhlfRMmGhdpfh3O7ukGmRJ19uuiVElE5rkB/pRnAFrwCRnBsyNfQikh784rLJU2ViminIESyI3lODaSS8h0wxoBKhOBJGcamyy8+AmU8oCZo4x8BQTFk1mKDKuGivDm1mOM6TYwRHJRXf9jL3n9Hf9CGwq/U2JvF3JRAI2+1LKpvv0bb6ej6TmYdKASf+viPrJyayEVWQ3qPzxEcjUFS+98uZKaNwc6po5WEluatRPC0Hfpy4AChxZjgWfZW5Wr53a4TYFnH09BDqKz2ZyD5WLftI8PN6ALp8P4zreU1nvE0QCvYUye3rnQvlCptk9OZJA3sXIKz59fzAuvhGqpbEtQuwA1E0hmyyAG/9LOGO4QrZnrJ1fh61CQVCPgyvcVlE3q4VTaKGWQcQ8idWqWDNxuWW75s8DEUzHQv8tb34sAYvkZ0oEoXnUCVHGXqyrR3UbTZViUdUS3njdD9uaMKEPQrHKP6ZfEq/C4edl/mpJ0F2UypHaqHdqs7M7lFhSfPNYYaDZQcqFZ1B+bdD7NQXY5NV3mDuBILQrZriPQpNsVvwszYnWGM+YTvvo43ROogeuALAvIepqB0Bzs8SlUwBYZBJV6BD9sw9uyz4fy/v3a7+dihI1AkEvjIdx0eeGPRMxCOl3Z5++dzZm5ckJKXxDfEIEd+37yEhKQkCWTkd6emzJnXKWBCiYwGzv6vwwAvrn+mC1FjFtyWaRGZhCSUyLv2z3XcPvhtgax4JXqcCHiIEt9DqKN1tPl0QnMBDdnId+t1d4noIkyIoiqucgkGJ6pAUA5n3CNMgK3nd1j0NSZAHBIgfA9+sdZKd334V8srdI2npyIGc+v6h/3RghJhQcRf19fTLXgGR3+uZULqA/BWN5RbxI3LHmlZwOHmfFartt2j/1fsE7sYpESKxTvwpbYRuyg7m3RdzkzjsY6PuosgMxOy/aLUYbm0FTXC25KjhY7ZxTYWJTcaEjGUO6qTdLCNUOOsjgrQxBjtt7ZYUPaHvON8cNH6Vv0lBFTmEAKABltEmLseeYqQPRmUMG9AlG4X/AyBrz8RPHGlVa1a4sex07bf3ODjuB/GMSyO78pvYPrwrhAODgDE03bwNG91xUqo7FUU5gMsvLxCqi6frna9lgSuk4yRSt6pCT9348l0ylWCqn6jyjuuTsFZo0krwdpr50FQbUwClFZbfrYxgc+zWmsqhw9ufi14TXxE8qkkx0xa7FmKYboE307ivng8CeHTaJZyWeDQPrD7kor9uNvd2e+Aq+7qDAeqPFw2D+ad29O9L1LT6KdcB8/G8yXJwFmZN4t2D7lP345Hfx9pK+rATVdJ7QGR1y3GHhmQux6sTAzEIAAxPIZHS+DdDyN92XfU1LVuBxOPJ7aSqCgCXKzBPHXOH8vtCXpr+6Z1GFgrP7GVTZYamXxOdOFPLoXDYM8lzLA/lZP3JxKbYb41vT4hetv8S0i4kIyG48UKbRUKLuJoATcUaY4uLKhi0Gvo993pWPE/Frgj8F50Nna5LmW1sThFl52O/dVz0lVkS1h0B1AFoZjWRS06XcrCgAWboA4r8El8zWfSHPtZdup0ft1oFulEW60N880YaJLZfaxtVjekYm6Rokoyi58CdzBTjgNX6HscCHk7Xm4EYlvtiwqtqleP7gZfVJu7fDWikcH20XPhlz61JmscSZQ6anbGqjSb5UouREK7K+HIGd0YVH0ZSYWIQ+AMqT8ntPei5WFGjc9gAG0eFyUUCV9qUcQVWeH9aKCS3FVic4NoT3IEsv6b/zMWzsSjP5/YZwA5GxFakCiaWAQcuV6db8Yzw0WM041GUuSvluYafUbUiMtmLHaUZtvWvdJtxrunhmJKH4GOjeZuS1C/OJzcZ8sCUpTOIdN50zNuRj5tChGfbIo3uvM63VdJIF1IpTNS5R40XZEVvNdxdxNzu4oeWxvwja7daNSHAOB/2GCxgJ4x0ZVSoqdog7QYLCqwm2H4H7DB5/Fl+EqPTH0zzDgbLrSgV2pYHXVoI7EZLeBOqRjCxPgpk8y8pG/6M+pWxGmO40VLfplkMoYwGvJr1UL4heEOCusBgzNd/i+HQTec9nEvONvOi27PawLp7UbrGMLFade7fO2lim03ModbrbbYSIrexb2VSRKCZDQyctyGBiGmLBB8iU2mOqq8FmykE28K/oYPpyqVRMLUI1aFZJ7Jt0b5N5Wl9RqA0U/c/DI7447UQHIQWLUx2zRzyJnQEajTCgs3I/VE+gC00lX9ixqpCL4tn2Km5Dh61weJ7STLUkscEp0su4u9l80r0huL5XqVNMrLIR98JhSPYJpm6O5GEB3CZuuJ+vBYroKntFMF161edGE9LJe7mSnJLnevvBm3K7g/ZTInmrra0XlaJfBK0qHUmg2EUKFU5wgVyN1HhovjtzYAzxz6QSHP7VrAX4BBJfc22vV+IcoWebVtRx+9d23juMDtUcQ8CidABlGllIVi0qVOWb+ZGtFdApvgRO7N2iEa48/5QD0brP4xcV7id0j6yBr0wj9uNSssTnjkWQYBHgozqDY3v94kwG2dmMkVPigKkjN9e6lADLc7E4LyM6Q7Mza7Wh86W/HfYexnig7DPWPthZ7IrRw8FVUOtAkHyNO/1GnNCgfNVK85KMQwYE+1s4+aCvvXDMwgCKbWyiKoHszLfUqqDjI/GHuidfvP2eIMKdvTQcX5G0LZNgZk+H7SqTtKGyUix3WESKoD8ffImKTavho0LPmPyg/MZSGLWMBP/FhNiIa4KBHaUmmRNcPx01EmImr1NTX/f11Vf2lH/TXUoJrqNF7w8SK+xeh8ZXESCRs7eWs2a6UtzBZmf7M5jlbsCrBDiQADg7fYOWmVhGl0K3IBIZjn7yy74N4nofqJzwnMzvVUSThcw31Ac9dyc9CjwqZrJzOaoYA6CoiDFkhcjHOMONNE0F4MTpek4tuLJTJ5UrXDaFpYzIWaLg+k2ya1O5mJgtc/k2q1CphF0dE1wcJCYGHjRggKeKXBk6M4hNONFZStGW/dm83FxvxU+EELT987fOkhmZLb41Ujsa24aWRnyFwyky+WgxIJmYdfs6uimTkFJIagO6bgHnZ7ueSKI8R6tYoin6lfvNoMaZVVDlPo9pmgcmTOPJ1xBeL0D7O1U8JgKqdEZJmTDIA2S5DOR7ZbuVi5LpdFmkY78Ted832Xx5CCWOcz2TQts9+CzaXUFk2+Sm77vAnFnXPzglHlSZLAtRgoZy5H7fyiJlaOerXnu+yZVvTm2EfDVS2nN0+Uo2uShPjAexfKbIEXBQ05reIGTaKYJjTUCp7nlZE078ThmsjjiKxPpCGKEQZhnMDkS4sKzI/xTYxKhUu4d9wX8BQw7FUqhAugCpvxOXzbZpn/B1mO+2aE51b94Cs2+qVv8jp0LhorrEIHnBsGYaZKhDTmJjQBg6PYYm2S81O3+q3Vb8fVF/RGs86eKl6Qe/89l1yKw7xuTUeKcWvTeW7l0A6T12uIC+Z0ekWUtyRbSPLWqbXc2SZPo2CgmQ+mrIxMvsHyBj5OqsCAGbEDfhaGI6/5rdXTfMzWZNiqUpRDXmDYJaKjHjvtO2cYt0pzundEr0+56NM9Sku0tEGPG4WjG19gHKbwHUEmyhHuMDyc29LGyPV+OxmZnJg5iuk5MptIq3zgBdvgWOkwZef3mx4O6jEJPOxUg955v4QQwkiMGnFRXvt22s3WDMJXqLctEy1v3eIOHfrSbfAsEbNWtzE6Ay5RJxVGyZBMeuMK07nwKzJHz51Or1bXhfAy5fg7wo7d4bUWVQ6zvEaKbrClIpyaRFYzWipdbsbZb3tHVjC4kWygdT4R2fyl+WsCjYDuldI42dSu+2M39In0mqT8l6Zr1n4GX9DKZf5tRtfBdZBAw45SNHr9qNnnU7ElRJog1xjJvfnVjCgITpmV3VeqU5lQO6eM5O4Ye18IIh2Mq+XCjy/Ehfjp6jmtI61PYla14/e7fm1jZpC3NBAGAga4ctRfWskuo1q/TuOSzW4gkgdyuVxuFD3tqBy3qoJTRi1WfF2rXtpfTIVZ+0mVVMJcbjE9kukzt1YkKLpjlS2u0VyvbJI1lVWfMytmlMMIqXhcTZQsyPORPIYbdcAjCTdHD1NN471TbZ/GaOVYdApK6pUMkDRZxFMlezV3m/c07eItJ+kyJaNEB9Eypsg6B6hVgCg+DBiFxAnMhIXVPJgZYxeDpOAnMaUxoawWqCzjWoCCRbIsqvfH/BdKgXOdo9BKlirH2DEmxIG1ltzLDE9nAnX0741rvp6dZ29YUUqbjlsmSqEQuIUCGhg92qB/zmPwklF7y1ZEUE2n0pPxS+e2UJSqw/zOXDIXd1y9lDktl+c1KkUfRtPoEg2+3RI0pjzeNxOfPcJ6SDnbTjABl9XiIOj0YioF1Howkm0ZRXShu07rkeZQVZ2YGT5lF4TJauKRz5IzW11YxVuVhE65gqSNUOnkt1HE5kwN796V2VsOAPL3sasNR+gjVRQDTwDmyYZhSEAXb+DZjGp8j75k4TLbX49nMtlS7be5RJR+4sUtQpskOA5cB5PcSixyAXzZ7BrssDojpKPzFcaBCA5nNOIbuPY3gpCCWaJWvMYjdHJPo/AOQqKvLvrVUgk8xZZULU/bBu2zWxHjgOZLgK1a5CpgwuRRmBECnNo4E17EzZOTWpvXrFcP3bNL1rZnl3OsXahh2jfTUAPocZPzuZryMTdO72CX940YmB1bIirr8LhAGb0q6ybLh3m3g36PJ2tWtZQQxDBUW6kosKw8hiyAVLBm3vWcUeEC8Z9Uwl7kzx0R8f4UXNDOisGtcMOMgytgAjbj2/G1RtY6/MDJUuKiY0Kgcc4/Hj2/KAi6RsnHfPRi2qd8WeKa3Mw2Rkvh57gLICuMb3+P70GIKNO8wCfzBmjSgN9KGW87EElsI2yEHzsJNMLXUzz1gUFP1pQvFzodHZB2UFunAt0x0jR8MATKHvnt4GtvR5PpdMxnMQkP+/N+atNCuZXRIwP/RbH0wOVeNxPQsgdx4al1wwk9JNKnRtNrv5Sy9SenZohBJ5GzCYUyyIINj7YppdAurovU+SLx8XYuOxi6m26V8lXNS/Ipuc0JBziLqg1RgKdVtkhZwCRPzK3SsBDnaM+LebmvrahXunPuOpqTvlej3VKUFng2VH/8Daz5uadk3Idm6/gsO9ypO2zzlGpOvemQ+U2jbRaKqJY+VPYYSBPMP5RHL3y13OU1DYiIZcB3CM3ATadoNVUt/U6xwn0Xo0Fct4MYOeNo9tw+FSsNRUIAYtgSMpV5vITBHjUNNkMdJvFxvJ3xv/Vn6VhaI/VR3yRJxlFUi3LVdr3ZHdqZdb7bTxYKsQe4IcsxMYnRiVzA4LC3C52ORM9LYUNmXaUVRGaV9km3Wq/hzj+7u2LCaxSHaZ81ucIYBylOAjBJVwJ9NcLE8Z5iWN8JpxkYLJ9LTOJfrsQ83dvD06ICPBTlNSmq0bBgnmpQZHEZmWXpMiivceZfF7NOA+M85kLuGi4wKeevEggm81D5tiXtzf10LGM03F7Rm8UGPhfp5tTddPo/eUnwSNs1E89MKjZaFyMToU1d9sqB7HToumeY5LqHrQCcMkWGGEqlbQJki4Oc2vXEJWKJQQW/gcS+O5LD5EhdF9aiRI33ayms24DLOgK3RQxX2Qs1cHvzbVtycjLshV0wj39qxLmFve/zohqBIDR5kCIw9OY95DjQ7DJNl0rV3Mg3cKfHQ2Y2hTYfVBsDXROWheLpNeGQbIUilcI6aTBtxeK4navX5bRUpzvPP5cNeJHicpifcNH7wiF/qhRYFF93nDqPs6Ix9tvrlOOxupuHx7YNppbqEVTTLPjv2S6JvrIHK58Gby+6xLJM+u5nPdFIBDdwWMccWomC9gslwoXPb+SS8dvKwGPgFLaMQHaHpRFE6Xhm6Jzk/tzKuPGctoYRJxryCb2isS4R5+J2tvxgWp7hPEapd1FihREPv7WnIC1MWqo6BGIVj0ERp+qCJh3YWTnqlMKBZZOMpaPw7u0q70Y4k8hMcJV1W9CU0BIsLGYpoyW3yEdADkHCF/sX8+3+SFdc0GiEO+xsypxrTTCPFsvDMPfSg5plxGcROYcNAwOAmgqyjjceDuE5Q301HrTZYN5yvoLiZK71sMKEBWqgRFGD0N9VMFVgLWuQCkZO3LKUcZXMrOOABCprYnXuXcWNL9GGVSEVuWZCG71Jcv+vMpEf4FbFB5vmcQTUx8g1IosjXtzK5GgAqaczoRyN59vZ0aM6uW4OHREypH89fA4cUMO1eE1t8lDL6Q/iRpzhMnTCE8UMH65Lnk5xNDzHKWlfdRbhqkn7SXUr5G2vDtA8uMQr5tdjYfhOoZnWOVLgqXXOPrLg4kxT7LFs/++rAYw77IOp5llL8pOaWVNwJO1M6P2adB1AthRcrRMaDaebGuAiSaq2TLKwIoS9Ev1eWInQj56sLVWnk6PBjhpw4tPJ4SGXZuA1DvZ8pBaSTzq8sYY8EKSt1EZfQ8rmYfU1+QFme+9EhYVpzf63Ei9MaC5WZW6kuYMeLKg2QjVst0ID0EyGVRRLM+tJKwb1qZOqtzdHMoN1V4qyiS60ZcOgXCC3+ePVPOydv4xhC6xHH3KBVsVK0bvk/Zk2b+LRFCl7slbO2VqaJdFdx9fmWXbjBDttr2zVBVgGJgHnGA8OjYDXax0e3nNSRc+uXy44qtW+hZtA6EohQkWcyuklJpzC/fRoMadiRaztaBuKUigpA/Tjpn7Vo3ivqFzTlcaPHKcrUo6mqsH/oTlNAK1liVnTvmCY/llLVD6cqdpxXhN+zkpYSpka6MiDCsjBibPwgs9PREbqc279WCJb7cir8UrO4NB59PzNd3wEiR9hHBM+cLaF0SkcrUsy4M6Ai+wvue3ZjgsFiT9TaFa3gnjxI6Fn3rTVeO19348zq52D7LTq6z21WUfT3z0kVM6xAYwYW5+wumEZyruNQaDFy7elmozxr9koabKg4RSJh7MrmhjIMytJgXXsj35kBe61E8K6P4q62tqcAUI2HLAQAbd9PeXxrXcd7NZ+6EN3z+RyYVzE3ifBRkWE6/yR0KJPVgA+79XT6VPhtfmLlqz5fD4oBQkfimWgNKSJ54XOedEs51p9kQl1WdYlqMi6g8HTOWXu6iagYe2Nu5SNOzTBcbykBPkhWtZfLTBxs6q/UkQrBTXxy7LIuxSQXrEBGyqcQx7g3mlnhynTr7eipT9MwShRl320oj11iRsdSFoepwyxVTNjpkLphEjRsTl9yqe8wHp8khG9yCjJ1/qVyds151nQ7AJ/gD/BN2AnHscf9OZVWWfOVEPzXLnSjFCkI+MmfJZCb9edPpOwrl/6zYCdLbri822dC062LM66L6hrLP3pn7du0UrMavHuCKNZTT+vccOkayFB13CxbRvyk6mvgjBZ5pvJ/blc3hlkisnIPnLdSiQTSea9e1wffektsEISIfGiQxQXeJXlNguRm2S7ICceomPTKKdRktbV+Kae/bgt4Pd7zHIrGhgcFEDfqfCq56K+cURwOsfzQCmO7Lb9FWgbA3G2bklM7n8R/9AJr1RIogsrku6jvhCoHOeD6JQt77HVyNIdk9OB6nqdS6BZFYQpfHlDwv1W6ZgsjFuS/TZS6jsfYLRuofJpvZTYihkvxiURqpzpeol3ZWFhsp/KMX/CmC9e3942yboSupu6eddb9o7N9wrG0I2TlJ7OKFcx6Cw2Tr4KbjDMOHtsTMZpG1gLSlb5ffQ/htn2C0mfbTksvNPMRIJD3hg7LKydIwdCtswXlaMNI2CL5JDPcGwqGZbJ9w4/qvmydMTm3z1R5Ae5pLzdDIMZr8rtrkTRgmcQtYE6wpTvH7EzFT7piIANQfJ968c4AeI8AmIksIO2VR2wqkdQp6qZbfQeVo1Q9KANtwprk6bCz+Pwv336vW5urRcAb5C9/xJrNNy04p5dobM2+It4ZFw1b9+YVx3zrEAj0ivNtfL6cjercXUh8VgoEp2zalftpYZ02fyZqtp/U/OrxJ18rIGv5O5qdTCi0GPR5yfJU+3v49kPnSzPEg5H4IWpofi73aLljSyu/Z01wwiva7vw7+odw0w1zSgf6aMb4nbpuzLM2JGfzGDUig+obilsonx1/Da8bzAzN9xc72mUVg/U5JBbPVOqi1yWcpN7LmsZsx41HalLO5lhBxl+nNL+aRLzKs2ZxnRUUlWdc+JghKWoM3/NtTyqdTaMgwBOiOiTwzxEO2IwsLx7JmBHjQqJsNd012rA+ng+DpJKgVYDGkQQBS348Xyct1l0UfkPjq++lQxDiouxLh0Y+KA8FprUft49fRTLL/sE+JLUwSartF6wHuqgl99d/vcIGRy4U8Ta3Ta+8XJilky2Yav7jmzPMozk+fZSv9OvZFKyeR6l2cFWJnWHPzrGThbXJ9WrFDLNokXugh8Yobnto7VX4yU/VMwidtVXlGYTZHnr1Wpc+9nDVaNqN/N6a0vRxQS7B/9svFceHxeDep6wysLpw/aZI17fDMsGJmIiwZ4oCuTEbDg5JTfomtDZlTCBm1lVImZgpcW6sMVI7lSZLs4ZnlCat034xPU20CW+dSWewFrE0UyVAJA4eVR6wFe68YhoT7FmmjHJnDx7R0+AHeYNdp7IBQWMoPcUp7oYjjJUMoY52b68rekqpzISHfNg+VB+1NS57Gqbvi9elsgA6emoSnUR/KKZdZFGOjCSuj/jPUWjt8DugZxiSIMHgq3kudu9jdjo3yVJb2KwbhGHqqxxoVSe51n2XsZScngSOot9W/ydVvsQEGVv4I8HLiAn2NmXz8FQtOY7caku+8kHQcnPwYsw3kRosztS83nuMiKM0516+87/mKacojBFu7f1IeByuTgO/Rp161+mDrXNd9PnCWd+8+46pUHpxCqOe2us8RtoAOt8ilQYR2TmVKrlXNQsB0OPAN+N0CmzdMo9qsvhhWSW9iDZpEPcf3T/x0cbW670yyHc6eKj003HawCztxAvHF7vUTzMG9xBcWI5e07c4Zs2CRoQfMdh3uSOiLPjN1C4WYfKVNfGmbNPDsfnWAYdYNntyEHKzV5eY1lzUYq56o/HKx8neoxuLHy0SjP5LOq0dQ4joHmXXsYeILMtoQkhnNJUlJR5rmL+fyDyE216jDYdbX5Yav/PSFqrzDIq/FvXgsZRupG7MUlLzHVF6n3Z3Z6kdUqpGcGUcuzWSPiBDw5GlqpCsmOGmWKIadgnn2YnFluGRo49RNAxpMUxONrHfw3+OR2Xuyr8mTpVycazgFH1rwUfz9f3DW3AT+7dq4AfsB3d9X6hWngPIVmXzVbdVBfyyHG5vz3s1+8o1yfsnyX+NHvscU+ZoWduycFXKF5rt/8UmyFHiTj2tC3/AbhgR8x32PWzx4KR+jTFp+YKnlFdYVSwqIWmHKVXIcpT/rrVga7bdyDm+ZS30iG9JQ4tIz8VlxHw6oyD7e230oo2xSfiaKd5K4ruuXpgODMDa1tx5eVPX2MqWYgxF/GWes3I3KvDW2JucUqpvd1s9PtHp5W24uSY40qj2/jsmCU4NT5uZHdYlMdE6PDTpNTZxehpCF5C0qyOS9gwIkBIh8vi1jEkLH/EOnVoRsN8NVV7VN8uIaIocIAQpHg6jsr41DwcgXHLR05WppGPFou6xtVqr9Avpb7OXFdDC9iaDWsNTyFB4P7fYp0ud5b0UBWEEhibhR4veg5Zg5J5bLghSZnghlPKWEVc8HLhcqozRKvkEqWJknjb2QYwrm5lIqSzzcJxUdrQsvhCg0Eo7XJLfkoGMNoBZbbVFs1kYvZxi3Ei1uxKlGDsb8vF0IlmQYFuaXRo26FKhRz1f6JOJDpV5ec3bbsuhEJsTQ8ntsQaOaeLwqlqqwIHc0Ny71zylVQLomvqXOsihLADlGFvaKyitAJwoJAydQo6V/ih2W4k75Jw3kwBWNyTlqKAcT1RyQ84kOAHP+5fk4XOqdzY93c+anq2KHIOnaz/CuXA0kcKe3z59nkH4KBxMvGNTwg5to6uQZPmXBRVbhK3W/Dp7z8XJxInbXj5J6CRULj6xKzMjhg2QUPoEOs5jEL5aA282UEtRMBfNW5PuI+11C1yuNbquHDcZXMMA43kweWHbs/DHW0j0keFu7xK+tRhTEt69zoa6xdkH84cfazYX+UF3FMomM3+DfP3l5T5/TxMFo6Cr1htC83Qwq9cqVwwbFNN36hRurpwRfeS9I7WlKKay2KUICJUQbUuedFi08lsbbWZW6+OklKPaF97xImb1Q6k6PLbWna5oHRaWtbELjk9tWP+vh/NHcsup5uphnI+svkaCtnV5GNMihpQoRVy8YFC7Lw4TDrlFhvCzxejYTPL2xzIaEusnFcPFJ3BVqPDamjCDD6FT31m3GPzv8OBwHPN26h57RZqX2fZKz4e/ZRdf3w1tFkoVC/VexWZPdaME8QCj+L8NpE++gu1P8Xf/PwOwZS0dhRrtC/ebm1lbr3rk33RjcrtkU82z21cf3q08u7S/XTnnzpHen2TWv8+7jLIK8Va+95wOp8JrlXAp4B/RZxTHoDKH519vcMe0Qh826RtFXHzgnLnLEdsSRgxeGMqbM5k/EVou4cRRIfJUugBNEXcbrbHNxeCyWJN8QpPHOQ9JJ0ERDEjgwyhC5b7qa++faFjT28mtxjZjqOKtgnzBI3oz6kYeMxZG/Gm/p9OGeJ8as46d5Sc/+9MNpBgC1Gs7ikKFZhtAcAdSGxa+V0RNe5FKe8yUqY9nnI1CIJmQ6tPLpZPO6cNSOo8kp7IU0wGksSM6GgOZigpey8L+UeWQ7bfXX+hVIYmn4Q4WHgOT/e2veLYCv/y7nMn9ePIEV98Ges8Vc1vbLVn+0hf/m1+k30RMz438YvoDLuQfbfZPHbNWefyT2aGpPR/FhDbR45DoOjPT3QxiIWqv4jkmH4419bIwDHb5eAjjek/l0aq4LhbT5ruNqvgPFbJejS+fmkGdzJb1Wtmnlz0Lzxjq2b54QOJUTH8x7FYMhmxwb4/Ksqel7IVaj+GNrBluq06cmAPbMutxQI+b5lfc91f1u3+tYQEAW0uOzgZdnKZ7dqOr1CZ70EwjhJXq8nzamxo1nmuAsJieLLr7eQKDecS5Nmq2PrOVsABG/4DD6zBPzmgYKrgAHsbb4Sz4LMtfQoCxDnIcc3tnd34J9me8ezUVGsURaLcrYoUoSLbdiUfZs7SVsxuRi1U3aiLPd6TOatnbd608wQHgJNE/B/yiH43tyGVco+aWC2GqD7dJPsOBO0c2S2WYjyYz0HzO9q4LJ2v7kkHbBJd9fFab1WVv7iufrEShM1rZ4XRxBEhnKkMWdfROzb9gVOvwjoUz6wztCNdUE60wZWNchTC0a1bnplLw00PmOSQGH4kPNW+PHvW9JDYbTssDPFgDzmDFBqM+NUVvn8ag59HIbcP2XGtuOTDT60Gi3xd/fMj3SQQdHErvESp9ahfiDt0yt4ri4TrUd0zhNMzqKq+/BLVJGJpBsklf4GtustM7jSz6pUwxy+uKUcUok6k6UTDe6Ci0JicYEyNI0iWIUgrFVOZoGp1Co45yQgWklLvtzzUZgAdaZPlUT1SxVqTg5dz+mWstxDWCRyBFhP0G+vhZ0V+Q7ouOSeqdaMeM1mS0vbJk/5un2Oi6nyobmyWJfQL/Kf4EeA7C+B6jA71z7jFlKwIZQ6QUjye0UBZJn04iG4GBcUhzn+u4h81e7NBnfro4gBRKnrADdhr4uAI78Lm/+Yn5m+YL7TH3Ay+xfTRUZ4bI+2tO/Y2ul5z5rHImUz3gQwm96ML8MlHEGOvnRowOvxJIx4pBwYJH6o5qwbjl0U/JghHIidze7ZPOalDE34aacZSXS1/IOiQnB1NIA8HBglQgDhkWkrqjd/RNnIt8gn8B4m57/hdXq58qeYsJ25TDQoaeFtl0e+oBe+XdtqTGd3/Ghgd/Ad94adJ2i3RGfa4lw5wCGXL4GcGRdfUaPdLrGaSlfPZkow0fh3bfxK5Arxt1zecSbK4U8MQWf9Y8UaFMTeNCFpdTWXH05SvSqshyk7Dc79EsIaabBvFAs5eUhfoenI+lbXm5H40kqfGnF4OKWKVODnebITR0NbqivuxEW8lVCECxZZywbSg8VfXPJX+ZL6QSWZmlrbXU8uVbbdvtNH+OC4+8DR4AqQw5zUKvkdK3F91xuXk1b8kAcdOxyh8+fU3PrFJDArzKFdph7ykacrnP3jzf1aBEIwBd0txsFCZ/gW3/5+nmCcMLNdie/nQXsCuEJiefpKpbFQlxU3SeChWU1tlZN1ArMHSNlH93SCDS19ajoFcTQcGwW3pgl0NtarTHrXyLLESZ6O5vkzyxo737B1lmiO5Y5tzKqugcRY5V3vMRNrkZ/J6XaIXEqMg0fOZdL4CPszgvTe5CiBZA5+CCdiFb8HnTGCHEskZW+zGjNtdxpVjhi8MNMcFY9nFLc8Y68jbyen31srF0PsHyzx4Q7BKqmgu+4z5iRb7aqQUzuWRiMpCOBzE4HUb871h5R6MPJgLwHFn1I7nVxyYP/UvIvgUgGgxYVkmTPE0NlDM+556O2H/tEtVAE88i6mxWddbAaze39bPVGPwnOomMmWjv1DAUYbUtmdwZ5mhgHbEUaYrwdvau2ylj8LrOpUzFuiwbV/Qk2e33Xlta517tMgc82SR6C5VbVWGc/M9PV5VPRAuqycRL7n5q8pdKij2pl2m2ATQqdPZ7XCJVZmXqWnbrp+Q37Z74I0MMBkVbGarN6VpitTYtj6jCj/honJxNHRSAnuMnhuRocA9YPtEj0CdAa4uVR4vNnMxcWRplAT42NlXIJFiVxpSlBB1BTQLejSPQrw+ybHL0YslAhwXkX6Dz+Oio3ib2xD8w4eyLg1UmUjhdm32y95O1x4Dt3oMzPf5rBqUwkj5kMVx/5uEegaeJqanU8TXz4e+eO/9czzPRZ38S/edRZ40D/kNXD+Jn7whSa8PHx9ftouSwBowMmFkJDnlO0qZ+q0D8fnkfuTnoLmQVdh8w9kZui5YdKdxLMfSlkJWh2Q9rXxKYY/IjUgLsqyqIZWEpt8ViZ52+glBfSiNF0ZkjNd+I9vY6VzB0zQnCEECmV6tGMlkYumjll9oB0ZYh83i591PFgo2cRfnMuk7UflhEC+3P2dDNimR/IsLmAe11+zU3HrIjH4vfhLU7uXsN3XtEZpUvMyj+0Wc4lvVdlf2jafJ7ZGeTiUaE2+F6Iq3S/aMbLPZ49JRVlDTvO1gVy46Yx6aEBp0ypafalvPv3hrQBImWS6GwHJi1/LLv2RzZIBFwZVCvoUmVFKYypmOdPikijRlUmVA9mkJUOwAghSTqKyMGg1hDlaInst9gXACNjiRqgfwCZL6thxeFnl89xGaj/xG3PNX30l/dRV3N82BP8Y/cLHw3sTnqNO+lbuD5iJBc11ew3f7P0haT0vayPLE7pfgeV8XIAji24UOj5gSEDgtANsCm32D3jnYNHpec/kfFo+4n5u7CR65B89vSSQ5DlVNuk+zHgNTNWqA/Dg82etU3afEj/X8VZcsr/S0F9kb1UjCl/DUCyRYhg9krqiNEhX+hkIBLnwg84ZDiRiJoH/N8HeSpnkSaEKHqwuqrTTiS1EnX5r6auCND2rgP54Ep5k/Sr3SY169flNkvmBMuw4xYATw14jS21w8Gm3mj+dMl+rjZ62WhRyZw54H1pbvB86t6/h+odxsvR+eBdgV5bW3uJB6wYcrc74FljSOuD9tX7MCh9rWtorFM8mSTpcXJDG+bFnLHOR2pYJ9swzLj80QZbTP93aC5yrB19qOrWn9nlTyWy4ce3hWap5xTkl0J0rWLuKUeTm8los5/BEKkGtDDT7QPr9K1+efrrpzWQBVDLShDEW8ilfO2Tdpto76ivfa09ucDa551Fn7qp7pc481T5lh/gmxEOE6OWFl+xqlP9lRr268t76Vrlb/5PyfT5+Q1n++YNI0PY0MjJ4IgXpawd7bx3i8LzCFWscVBLBETc3ushcq2KeZ3WSNRcxq2Olk2XnIIKubuE5eHQi4X7lN1d2AJZ4OzfeLrtkpI7adXA8IIJPxcy5pHC+9ID86jB9WBxVxqw8AdqaDJyERapCGLF7ECy0GuZEtLW3696bX1AbZ0t8b/Z32gaWN16fp725vXNO7nVtteYwtGdv4ympZHGF8wLBzDgpIvyqYIYjzcaHL8tbQQcPFxFVrL3etFau9jTzdUQa8a0w+saqdczjq6mK3vJ0msZNgqlk0u0s4RM+t+1rPmWbiFexF1a4ScWduHKfSfioDgkAgzO5BJWvDeFn0ZECeEz7YakyU44j4LVhTnXftFrLgOOk0QQtwvIJ6Nt85JlN29GIqWarS7LdWCc3HxSxggLBbu/81da8k5TYVqXPOBNRXarnVWP68Zo5atVpvCF00h7WbGRgqZjENRq1YpWpsmGY7wfPe3RrlGrbsbZLPPIIqMG5Kths+7470z4QGR8RpUek1sZIgwJjEWrlh4gpJgpklFCVq0XNRMm9HnSjEGvTzuoQnpi2YJsAIkqEaptJKXQgEqSJW76xdkIKsUYOBfSSGLxoKSTUBBScOpmLrUTVYxuohGqIqYnK+y97QrSrsccma7D1jTppkPGepXB4Y9JJrFrVEyu+0KvkLtA2EjRqQuiIbkXoYAzGl0IE7IIXLcKlH4rPomx3/nplNziaClDwJvqs1IY+C68P9GvqdCI+YWWI2aE0HO3Oy36muJaEH/PK769ux1bE9caKDyH9EDvlHyR/k2um0XMCt50VVeprFcXeAaWHArYop2m51barHwWSI6uLfXR0D6wx3C7EFs8cJ5DKrEjb4ZX5XtE8UNLI57ouJd+ofWlUTF0e7xmg/g6k4PVZh+5jl14SIdLgMPlN5Wli2d5iYETPGjQMZZhjDyXMSQRLX0j+VnC2zKL8shi8KrONY8J9Q2Fpsv513YoIhFPEhtdM1mOlYqIkk8sgiYfUjFG2iGPF8IRx49FRUqb4kqXOR1EpOJqyupJo5/UApsfL5kRBwhPi+IGKWp9LvMpv8kufTKcv+/cVtL2n5xowM/oKfxhWSpXFG+0VFAllDY5BvxWILOvnfdds9Y8IuB72qu4L0S+dd5BettpN+TYaSh5YfHT4UOxSUACEQjo9IiG8PL2Q3GJ8HL7G8kYC+JPJz6gbiHnEC7Wpe/6WbV9uxn3rVcqH3NjTgg61OavWC04lMkXaqVYF7oXIVGX1QHd+dAR8kuSjtQBxelWxifhzsjQzz0kxfVctOZvxHL90t9sgRIDdbiR5Jd2JsaWhbF/nBdiEmiQGQUBQ1wKIppvVo+UzajzIUZhbhSMlKVdGhApAHXE02eeqEpCJ3ya4lU4gTKWJ0rf2YcL2fhhFZ8ZbDZQLRTFqarD7KKBiFe2An3I7vw/e+vJz3PyFSVQkoUAjj+DO+S2neVbf+W6zymkTIkt5UIvbYf3V5poY377+ouV7bLfMPm9bkLUV8joMCHziOPdJqxqI1u9n2I3tm2M+itMiwHb3TcBIp26JdCqLDuMe1gWtdjnfMPmXnYXkb2UY1k4nN35x7JXT20I69//za/sTmGv7i6PpfIj7faU1XREnjI2eOdSJRo1EXQSWr0e7U1NZXhytKx6Pelj9bDUhSOx7rnqKXyisMPoP01ACUM6Bq5zXHLlbFmcHGtrmDw7aWyFgJHkLg6sDEsDMbFMmeEo/JbjmuidBPOvwz93JTuQ2MXX1s50wnthgFDPAG/FN7us5O0lXKrv7+2UlSn1ilJbykWzQw7DBpWY8fN4cSDb5LaCQEV1vT6xe0fpu/YLKTWAtjmGIaI41APtZ8+dth0DhmCOqnJPXGeZHCUyoDBbqayxublEudmkw3A/dhY8xDKSZzG2jHPxVBLljYH2atnbvwYg/hp0tLy8Sm61qu8evTlh+CvAqRi2NqTf11wbmiac/PitGY+goUP4ZuDIVDia0Wxnwft9sH5Yrg7HMYSl+AvCT1YR4fKC2zvJfbpcLXeNIRlacBw5CudjTWGc7SYrGihKb7Yru2Tpz0t7Q3sK+RfmCGjC5VW4H7IwBly4ABC2TwMlxUldi8mLu3lG4nz5UW+MhIFt4msqFNvQrpKs8bTz4evmgKY6b/4zBadxPBqB6xDAbuIW2V+hd8ZlKyJomoc0VINNvHW7Rl5ltXlSswpvbXUtcb7tIN48xQU6BrQd+zzxWVPl/cmDgxflnpeXTcZdquXlpeL0/Qin1n1oZH+MwETf8X6W/F8TBVArxD+oC8Br5nUozO4vJJZCEy0bb2/Z2Bi8KQd0peT2SuHrbvbt36dXL/MAZnY1TjT181WtnDEwq415eIDdh6nSN4K7m9lz/Q+/rIDwaPEC/jMhPf8i5bzmojonxyI2hTg5WbzL00PX/PxyhEEEY0gnVwkuqt6AJTF+IsOQAilfJCT9HPbYXCVWvgPiFa+H0pKJO/Rc7AP66if5i7VDdGRukjgVfxSkTL4pJ5jtDgoClDxnGkGdhXT29HIpGYPVlT5vwrj2aD4iMm2HF0NkUjf2RxBdVW6Prt+gYeabVG4bu9S3sensOcv82xnXU4X/ga/OHoKO/0b/wqdnWyf9NfvoO/bqBuXQsJWYJ/tgLK6RM9FIM6CuMmUht3CTg6t4JwDuMwARlo4GW8vmt23S/U17akz1GXc2xveOus9x8HH2rmbeNymY376RAWP5E0sFebxwdxNxVxZXdL307r8p4woX5Xt5XzOwrf+NvFVlK8dO4Qvr8vAN6+Zyo1+dj9qZH/7bwzJI297VgOfLG5++DiwPLJSEYFIMv4M2RifWHN8L+5oadqdNxN6LtbrMA1pYUu2/KpR9UqmhiuJRa7uc/j6VDGIsukGSRbNlUIvN9JpmqVUIFwHM2yTSaBpQpLuHtNEeoSPOIsqEhP96QljsBkItdM4G6yNKRsRP9M546FaCrIY5DYvhy9uYsf6ZE3mBuGLJAgN+6lRJq80TZfBiGk6jE9S2xVc0Xj4Wg5PHf3Xd860MsxjyptRN0Qfv74+rFer7z01sTgf2KgKx8+/gVUQFfY+cx75fO4nVnX6g/1Ye7PAtL4g+MQLIYR1hgGJSf5eSQnNOb8jJoBc0DA//RefW5VSgUtV0Tq7jarrHn8I+te/9blGdzM7J9+qZ9zOzUOCOa9ba3z3PXKxsbOzogJV0VzS0nZnSvu0+oOTNN0fFu32ScnGjGYDkRdkzR5TZNWvV5QGFpFBdwTOtLnhwhw3RbE2LD1AjYMrjmlL0fRIQ+TrD1oCkAFEZeIi5VV0mgFclqQ3aH1bfSajs7gc3At1xFchVO7Hpe4UE1Xsoeuj7imL354iV+2y9fHUG7ZTqZahKY9vfIt1TBUl+EUWCMEO8Xw/ODC+ia3x65DV4J/+bHTuVLT5KCVgrmemCXJiBtMUcN1ZF8YzAwTv/EmenGB5gXHzGk9L+y9+TY759zfzmw52FnN7eh03ubUNvI/tzHx4kH2pEHvkJp+VGrz0SVHeC44+PdCeRWFqRwA04yDg/DgkM4vc8XcKTdS8n1GGuPGmHMx7aA8K0fFckPoaziGYaE5l9hX7S/kpVbmP1JRCC76RQhCkT48F5x3C8AMqoss8MwJe9kdRX8bR8tWBoqoGUo5sWIisUrxwuSZks7oiVlls2aEhzzwuHmM3rQEqefAPUbMzE4Hm2AXjHQN1O/CT70dez0Kxr0ZyqqCBDkXW9vclx4PG9qUis8gg5ICzbsqV2NKtloIFreSRF7HCMlEfRXqEsH7wlgAqgwkKpGofedEBV9/ND7+yBhVmDwbQBLG8/Ed8tb9m8kloE1L73xRH8Hm7xTTdovv78y/3PmWBtftWFawjSpc/y9G3GL8/Werv5/jYJlR/oeqPP2ww0TMtDAFyckaBYqTvKwvNHaX+ScJfGWE+41BJuKjJ3wxz1h1EahA0R82sXgLIXIsfoT029q8qt75u7P5q/Xbx175WRywL/yRnPIL2MNK8KEsJ/u/qznb/0uJ+QegL0Di9kav/kJxOX0cZQNMjLQV53RE/P2hUwOfr4UQonorpEhpnAS8hAfYIB6+q4Cd5ZoWoIN1PPkAHlA0Xjx3eknSF0k6VtoghBmhaRx7U5TAKZC+LJLAD1W86vcaw5QpsMp2L+sBmc/fFr5JH365YykpE1733hqeWk1EIt3aCAoVt6gp9KCuN63nwFgsZBfx0MBm2i/QPYge7uI1BK9SvDmsd13EiSdEjt2fM10B2SlZ6DRTqcqjwBtAOCpn0RqZ6YO5EkT2tg6HiKLjjrLckpYnGageEvEibkC4ITftCOk7mTNXCcNeOAFd54JymBMxY8TNgtkC+CEFZ3Gyk/Ppn/kzet1/x1HlWtV7hxOGkzfAe0yhvwXrnW/c2/kOJXBrJrnj6tKfnQRvghnbYIU1S5dm39776ahiqx5Xvemtpp4madX7FG0CU2RTu1b9ZAIfYNvIfNi5aBLwBf5vHgUKSNtYkvDiXukspE+0uI1u2zh4XwuNN9gxKY+HfKXKu+sH/4+5v4lvEfA//eAPr41df3sRm3U/kqsjbceO9YFAcoGVCGhEmuiXQ9z1nPKHYFq67u2PF13+TLin3FK98/vHwdlGLBzvn//wT6+ClFU285PZATnmqgT7Y03EO8wA/Hkd6JgYdp1q4oDLIfpasHB03ITTfOX4+/JJ/ZpGycDoCp/02oJzXxkhT+jnR7LZRyyPE7ea4Bon8wUSnPPE5V2jiPpXQjyLQVZB2szCU3AU/h/fiq8xMtvTnyjIxpRRG9XrjdiEayYLvCj/Df0kcB3boBDHw7GOahRWxJgd7IjjOQZfwOOBxWA4U+t5jMgF7J25LYlJRnQK24yubKe0TsrRmprgl20zNwJVQCoCFVKfh7kptoe3d+7pkwyPTj4ytDrk0uyRYOabtAP48Q2mcYQIKHZSDTqm/8fskPjHJ9udoc0zK6Bfnu8/y+wb6Z0+FPVKyWBR2gwrvm60VK41ZuL/az+hIjnOI9WgDletOr9cWgmP0WepguC4NoauiCLjU2Mcu/6ZYuFhdh7S7IfaYZUkZXVFCWgwFk6fjH4jDKsQwUfuVYjooeeqIcptO2jEj9w6KQGNkxmxjPkpu2QM4LwubcKYG/+RsZ/lGxdWjRAEpiv15Obvxu2g2zlx47Qj7tRIC/QNtCBgVT+W7uh9l3qnaunv/rlV0Jc3zAIHnLEadyAYDLhN2K/B+Aj02T9Tu0u1G8Wt9iE4TgCxabRajQzqzxh2Ya3jr0e/0NmCQutxm+HBfJ2VYkpF2Q5bx3Y+tBa1mM0Wd6wYcwzv/+hTqwGWbY1qxp1GP6d025ae7S3ciO4s2Wzevq9lLloKOGdWocsF0bi834XPG7pg4zX4HcYP7O/eIfCJds6LicdEp2HR/hnzTbqye2L8SNwXFtrMWfV+tX9wXh56uFKzsXFj9KmXTh2g5Z1r1xHzO/3qwlGTR8O7KIM6SF5+jDx/1IbyCU4mXj+sPX8T1p4Si+ovDx2VIzf6I7wszvyl5aYyHyncj8artRuhuR8zj8FBT7FU4WnUx0V8CB218Wl5vSpWmP4lGQg09E5y5+A916f/XcZq0sftnrJ+lRrP/zscNHuPL+5Z77/0khy0CzdvLebEaiBxwQDiCcH6RK5OrOy29/3Ov5yvSfnC4nHwTR3i6XbgaX8K3oPrf+WMR7nDjTZ4vqfuYsCi4TIZlBHtGjQeZngRrUpEey74zLF2YRtqEMm5a9JKEtgDjiPiQ5xw+6wtscajYLeVBBXLr0EtYg6DMtQGekADgDxUQGk1F+URH/7c0EPaM2Pj31NvQYtBt/Tqvj45s3YH/8EN2H4yF8Hu06O8anX4Ddsfn5kOzw8eZMHVrfJorL5DYP982fcDmNRWOWRMvXMJLn9SaPdEplAqYb5MDuli8+qhfLXQy1aAry4vr04tbt1yhejAOS4FFrqrsEUdS9Mj9EqOEBotKZHgKB6QIwqKCmEZWGe9og9MpioiqSeeClWs5KKcisqoNomOA2nM6KxtdImprEUCpzILcBVyqF1BlXyJsefAXzlo+xydJd9WI/zpbd/f+dc/vRbwwMM/+FdeS3w8jf9NMuE3bWUR/XVVKY64mPnDVM1uMvk9ZFDJvwsEuFDBbotqgKOeD6icEk5Q6uPS1nvx3juDh+4tfeEf/bD1hdiL8JzUm2nHjfcnP5n+8p+4T+g8PAOe5nLyKDQkpPCEUaLp6roXJk3TFMxx9u1M32f5pWthLKD9RZLijyijWERFK8uI4qAM6nANfRP6PzAJhaPVRzTqzgnuZd7VL2a+TGZsadeOXy9+c82GUrr6Di49A/SxtHQKRt/BKSmuaaoeefN37drDarleJJx2YVL8U412LgQe98SQyDhRXK+wafeb4EYRvdUeowKuwNL+aMC7EarnJMrNczWD5+Ea0tL2Vj7hQvYg571P/lxPnDfZOiy/j9ZfL6VST1onZERedMwsVv1lgDK8ej3NXwqXR+d1sRiKj14oIhsj3NDqAcVkYw2HX+hSklONKIg7z7OLvmGTEMawlcpZTfesVLxqrqDOmQNTt4UbBIhz9gEILT5rghIc6hARL/3pSbTCjbcXbI7WUBqv4BWHfP9OOZuMZzoH41cF7rbfAccX8QXBAXf3Y5jBpgBQuEec+zrrIxdnM+b8CglZdPxjefO36yVz1YZR0FUc1l0/rBpmYwvC/fDwY7D3PveS3uu8JVL8oZwaq6xRmZKRLX57vFPtFgMGQ267gn0S83HQBkTsSxSwqF0223nlCYrlmtLWT++qW/6AtiP8ZEK80lp9cvIkgXefC4DwO4Ikd3xQ/OEBA7f64ElIhCLokG+poPVzeeRpgUdb/OvW635aIHo7j/AdY6PmQp+ev3vzPg24Ny02rJyyWzE368hwhHF5m1TisIIQzoEqrMc34Wt7pFDUhLiBG9bH4I7SHvDUIShutRCYRGfKU+d67fqmtLxKuIhQlMej4DNSOhUWIcYGvYen2sawye9qYYNnfCt74TVjPf2mT86giaMB6r5j5pO+gZet563zPJCMlb7GNZwSB+HJVQKaRGeWsgYfVi7o+xTLaSOKTQgo3fZRDZxFMCADHxg2fQOUNM05mxyK8NWefRPNLfkd4ALGk6qpc/bu5+KunqQgJ3ZDetSsELrkhO/Nfe85g4WiqSZd3s8HF7woxEpUG2Kzj9V6LJdSL4oanl2MJsxzxvrNm3qjMs+SyDzhER1QqciABtE/LD2kUAUdIpMUc6jfGiqvCFILc7LjVdGczHJnj6nHEEkUtorwyiTI7VNpGeaUzAGeik7liCM9ZYTFKdHqxNybEqxrmyD7J43URzGAXj0xiSO5EQnvMZYTSyYBYmqgAEEI4RK8gEpB7Ltba5MgjHlFGfUppEwLCCVZudoxoXDtekN4TBpuj+Hy78p0o32pcGaSBQ5wC3a4P/ER1VhwKIMDPyajq5yoMBwVXOA/ggsJFJxrwSqVv3k0wUOi6pzRSue/C8ri9BHXKG5i4Aas+z+y9/ZGhqMo2ZK6W7SfxO/JzTx1bv6dliw/mRg58Ylp2CRjad+cXhjX6K9/Yrgpcx7DYkxmMzoCz/jNpa5/HO65WnrO1jiHiI3niNVNqNU1lq7jjdR+KIqFE1mdUm4KQLGRVYMkHk0+k4foVJTw8Bmkflg/0aV0siPVbgisoGqv4kVHvGIpIFSDLQ3bHtI+NY8rLRL8sQgbvqfPvIIx3jrQhPZaOtKDETXvD/7xxtc33ax8mdi037A+jizGY4H03me84BWkynhaYQq8AbqNPto6EJ55DMtPbaLe/oMsgZme56TknzBAtvHbRrU/Te7pTRXwSDqlEHqmaT9G8OS6XeAL1DB8XWsePLq6jbsIvdEOJ/PDPfIbFVyONZrHH1FMTS0UJUePARz3qdbLYqMim3agMCY7k4o+BS09KzOrTERzWlUiwKTwELX1unFCpUoJtgBMnNtWHx9xVbbPDCib2iJt9mQNNXAPrIeKzGm/cHHBo2X74yhrbprhgWhtZzYIVcSYKDtZiqE3gkLYBcMwh2/Dt2Zym1/M36es26h4ohracHMiEolzjZVKjTgg2RmNKPGWj8HJPbr7v+8s/Nf3/BYpj5d30cmHQ9m5pa387nLjYfObwMk/UwE9IDEswrzlMbK5R7Lw4LviwR7UWlb63VNpKCPD6uNng7Xpv1/rqtu6PJF/nqC6Fhkbn4jLHVUbsvopLDj2GiN9co+9EPZq/jQuaqvE8Dte7sjbkNVSdmjwgm9ZPFD2awFVYswDOakA04rFqhG6QVLB5IMHTsV7vuXjdnOPayw/QADnQh2m8A34xl2k/As4/npjs9H8UySNMg42aYHPbqcCk9q/bgoH6eKiSwO3K3cVfIOWKZ6IwNZt6CLkZVIxFW9F/jSuXAJ/vwDEkxSeiLEuVcBqQgsY6Elq02bbutF6/kwEXwwsU6fAKRSiYgYMa71OuLzziXlo0hFVD1TVdFN6dkfsIHEddDObMZN8YknV1TF7orrtG+lEsptmNdAUWjhF+hiZDMtHGerpCXE+5Uxdg1ggWoCnlhQwmPWyIF+/hus4YTXTqZ2QipxDRsDDZljFaxDA+jb7oNlz9NHPRBnrkB/E7/Kw32nVg1enGchgO6yEzXh8M751T1Gju/0Em12G6V6VF1gNHA3M+dci99/A+q3kJuucdDnvOeSG4QWV5eR4+lqw7x0gA5Mw7c/UQf34ECep8VXhpS9JwwvVvfWObXyjW90KsTj9HlfLbWaTibhA+WS1dTh83igGT+iqlvK/tvgOtJoyX7zAt1wWSsKo/ZkFcM9T4C9qkUeelH14rpofN5Nhp5R3EGYVYwRJC7ZK02nixUzmuRnsIXXyLEBeluFedlu2K5WMykTsnMpR5SkPCXCSEJZuo6KE3N4vs2KIzbVokI3HGijKeYoxt7xcc7Ht1Y4FElbwhDUPKeEid7W2UGgYJo3yBB3eRyyUSX/T60IA1QqEX3M2F0fCngxiUROc03QhCCtjuYNJhamjTjaDNc4Zu2g1IZ2TflXkShTHLzHYSRqGNxeYLSu349QSKKSmTWwOvhLQCrKgsz6dAdUpup1uMDS47uolUyWn0KzMfLLyq8KTqxUXftN4RxajPvjWu8vP+YQp5TuAiuiZnk88x6vn/ddGt8oii69pOt3u5IcZ44S/aYxM5bwPrl2LfxPKaWFAnuWc8PQyHvzKyc63XcvBJbsCXSNrOukcGR/gSxq3w3PCmkkK3Ps3jWrOTQp0amoifLDOBd2h3Ir2cl7gk34/jFTPkiNuQ3hPBDPIz7VK2AhtQXtzsggHj3pW4STIlvdCNRzTEoqoost7DNDQhgQ0Exu96kGZis/KUv59AdxCxUac1NfuV73uG2AaJkaNPyfA04GYhI/R1YftIz1pdUD7u1UxyMegJ39k9XUJuSV+l0NOV/DoGnEfXqK5Jbrv6RsU3vcRAeKErbmkcoGt+E4xDxCT+bfF8/zyxFdFYnT5hh/j0b2FExddJzP6uoUfNS3ag/8EVoK7yklGUU+JJsNNGZcbkiKBiih4iEJnUAyf09c3QfHN+FmZ3NIPIzAa0tHrvITu8KAWlXGuvsrRPQefN96o4YiOHq07L+1Vamd3u0V39Ns/gFIict0xZV6UaLs+6y1z9sdv74mBrzrdA82mgqt166aQbp79STLoh12poNetGyPJdVq4mXjcEIUiZVo4htkxcJO7EsSBBkU5hfZ4gKUZMPhr0wQmmPNhU+Fl0dfRZL6skYmdPwzva/XQqsk+0MQWqPpwvStRWa8ZraBJdwGN+WM2jeasz6GJerm5VYWFYzGMJtq5W5QBsGF2x7dFrhXZKfcy4xrDTkDTF/lOGsyV6jsqmTGSpG8oYdCYA4or0CqkoeCwZsNqP2ebMn4mhc7giUjE+q6D0lMIJew7UpIgyDqIfOwUYBdg5Z8lAYg05grqibCE/UQN77G2lcZuJlSNQbUPNrNnR6RRlA14wtf5cIVKukoqmKAW7oBZ2I7vxM8ptiVf34EGiqUyqqBfVq4r15bWtFWpJfP5ahfKLy312gvlIrrF+7rWeT0NqFnQA33e+h7KMw0Gd44xbsHUI7HyvKgz/fIiobGy53yW11n3rRqq65K7d0W5B7pngHrqNq2bHrNxXHXW+D+kazrktBnmgsGVWjg/8biiENqU7ZDmzY5DiCicNZ7FaMHKMSG73cMlNyYPOw7mxaknWTxzIT9OVZNLDNOFBfKs8OXw7/Z1x5nSos22nT+ryJ1Jz22mza5YYivlMRrPuCi49DJ1odgAgvpeO2jd7hl9ErGCGujUnbiQUHP6VllAcZDsRVEXqO6w1/B+NEAQEiws8uAkxP6f/wkQoR0CXBTSWShbFLHbv15XHVvAlJY3Rk4o380gBjVg1wzJtKzFLN6lw4/gShF1LPjpISuuqwVbyHPaR3kOG7dTlBWVtjlUZLd0sO9Jr31pLpTcHDaw4rJo6jxsLdKcFNIq5VXe4h412hriKAtNMKGGN/GqQ3p4rwKl4wVViGv4LzjvjRdX4s2DHr+G4/VOvA66E4+5bbjmCKNMex5eaoeyjSyvr2KAaHvjfZdW5vCyH/Y9xP+lduP3FyscIZtWER4CMaDdPYpG0At7n0R+DJ5rPsweyuWP2eBD+ef392vHj/toc8LMnHpwtbNOF6w00HsgpGQK5oSqIgHUnAMNqY0jnbhjdI763qgiJlpRTkFBnrVFolJ2uQ6Im6Xae7gxps9+avC2p1O1uRkMybMhhmN0Yl7Iw5726lBEQBFcA3Otbo2D02Y5qWol8r3n0SLZRSExTIeI5QO4MKn9HiWBxlTknBt1ododp6xWntUd8mcFviHdubf+whjojsyi4M504X4MiRM17X1xX51xPNnvUc3qj3DrsVy9tWlJCSiTqbTljmidYQAZedaXu60SxtAb4ZlwZV+m+78I+Bap0W+Ke2Oke3wHpoPRLpmo+8C7e8hn+gfRpxBbsGuNIyueitNDWWa46V85i3GimxpHrXE+y4qZKGajpO+OsIxyqE7wfKK/a7E9/qud5fZqmT8yqDcv7u2XWA7F9FAIl/QxrxF737b1f29Cwkjj3uEZdJzKaDVHlOY3KOgTyMqHG/dBljeNerWtSd3cox01SS9zSFmDh5gIVbCSxdpDVBQzLsMgtM33G5lJ+fxNA0UiKrFNNdcMOaSUigzxHw4pwMk5uxoAoQ+N5L42+A6RLoQUuMCJC3H6LuIyUqvFeW+rOfR4NIPVXYLQAg7p1M5k3geGxEN0ibeSVUcng4z5qfAN7ckHdW/zV7bFexwH+zdVsd3Nmb5ZvcAMC5Sft9QOhox026j+bfXIbrld1YICZ9JRUY1XxuMPJIEmdUz145+ad46akduF3G58JUK4zDlgX1kFVr4MZT4dy9KOWfnXJcxE4nmTaGVkZjjdbOMRvhizfmPvR1nQNMTEvDn3mEaokt5Y0wnMuCrUdCFtqVP1Jj1eRgeSqg7Z0S6L+zJz8jFDB9o5ZuO5TCnrhMTMJcREP6rxKO4JaoVRdxmyGbIe+uFcIQLSpthxNNxW0drfHif3ZXAXYpABDXQ8hBt/UYO7Zor3Fhbv/tjwB69sEwRNP91amAiOAymDRX77Ie3zEz58CdkD4Ka7id1vzK8oQUte9yPXO0PfIW/Q0BYtmmW0iDz5XfHW/hk8SnyP4IqF8sCE4ut4Vbn7QJmdXF6GTujYFNAjoGkvLrZQ3VbTaOySvzaFSZwwH5ACJgIqREXXlNg4Q0jIEBW2ced2A8DQa9aKOT4qY3ODZNDI+680Bp0MOmRBAR3Xcf3zRN3wZLDZPz+9hf39RQwb0p2zz3YAb4HPMj5LLJ2ClSAsIWkA0uR76q5r9+h3QcpXARu0Pxa8WVeSwAym7hq2e8ptnyb3/TuD44mfmeK+ZdPUzuW9qkv10NDwkb65fiqiOORqCih+dHreEM0uFguPFOBY2TRuC6KQj0aFs5o0QRfiCu+so8Jf+kgBGGjgaaQUZCvjgV+gQPT35ExnWwOK0IAiWHgKzxYd5wRLpUxy1EdSGRbdv48H1M9nlv+SGbHJi6NGBwSsTDU7t4D7D0qIsHGQkf108Jv3HgpPm382Bbe88qS3+ZvTjmPEFjW9L1d3KqZXfyku4Ab7FtzlmQG76T0s/rDpuACKK91muwIqlMGukBp8p63GNsY8maFdSpPJDzm4P7YTzl0ZM5Y1CplzzyTZLQE5IAcxecdKAo6TJieVnb01hAPUflL+TrYq2bbTPRiZbIXstm8EWehCDrK4hWfiQr2HcjnLSQW2Yl0uz3q5USSO9/9Sj/PL/kEHo31b+88M/DdaDaPK4owG5xB9TywcltS/2Qe3uDLrZ2p7M2P9SPx34PfmyY3TED+lGq7L7KYrU8rk2RgcHBgX4VN9Cvr7spPAirGrK1eLYzHrC9vaMtaX+z1i2yK4HgShrpRal2/e+H1EG0GZPc4IYfmjMqTX5dnWOIPWJbqWaHvUq0sdz1w5jx8Tzmd5Bt63h62jgh0wBqvxDfhMBt40M0VoJ/bSJK4NZb4wpnu1OL9Krr2m032d+OHWQRD7Ekx8/huK0LQh3zUBUE6fmTpwm6WYKPJYPsTd8432vDSS1eIBR1lWKH8f9IsA/Cx4dfoo9Q+9P1Z82BeLczn26LNDjYvc0NmJbXSjof754cxYG9C6XeI95d8bL+LgBTDRtZpiqZsNa6UZ3rVL+q7rHSF4HegfAD2guxeBTHtQ4wGdmQTxrLAH2TXWS5WF0WQ5WE2MSnViSzetxfWob968U3/CqIDH/o0faXv0jMXz8RNjPx4gtxsVP4DzUrYs7y+/FBlwyh3WhFS6crH+4j/zj62b2YA5fKAVpVb2ENAb7Gw4BpiG4VbwFKHyTMg3YrWM1n5GgxMQPRpntOzM6nlILPyPap+RHobhvRdWgfNV8INYUNAEvY77BAMYoAvFbmWMeGXWSPQQYayPwUhKi7ibPS16joS9XLo4whzHUu4Ghh78oAUvzsTZGdvfNGYBnKkvOTk8QZ99f0ij0EufpF8ySWbRrrgw9WvaerrZBkrMk/ZJZZcMPi+4qKeypDOyN2jO1/dsjlrj2k+vwc5fkaJoWgx+2rGNlbn61T/Cz/GemtxooXtflv/IqOfq0VBo1Hiq1PiTXx+z8OG1H29P7p7+WQq41U7VY/w2It1ucL6+3sfh+U1K1trgn9CIFfR8BqBg/GVvtt3Y5kvDzJp1r8sOdb4cFLxNWo+XBjk7vB5ZvCZmvv7yKJvVZA6ffJ10X34xSJIAywaaeF7CUjYZay9tZjCEmlwkAoiC5+3W9pmeVgfM5Ib4WGY/hVNplT2wgIr2ZMdQBSk6UcHZSeBFquMMDtW8UxOPOV0+07KmLIXsExPfvAp5i8kkOYXtXlclI9JCdwkTewGcPrCDmmz5yYSB46ueMHfqLBR50NP7/Frb46qNz9HhDtmjrq4sLLQRPT6OTfhTrt2LFLnkwugvq/F03Ynv7L8de+xu4ULmBJrrpuIz2ENtd70vHmcZPh07yRO+sx64BP2iURU7dqvssUdznaLULvdKLd7GheOgqowsXXjstC2lBiE6sxU8vDBrLg2J67+lTG/oCgXX1QeiZTlfKF0MDQ9JHfCwvnB0OAtWGzj3V4sk6kuFff0jvZZn/GUwWzT9maaEfeA6Gm4kg+3xiOcSVWen3dkZKZ6+4o7q35FtBY1GPY36/EA/jX/rKBU0Vj/NUpmZF8Lw07lVjjqLcOR2k+WDQcw4TdLw6maEf9pOj1P+jZslaDrCeBvwspJrPVlw2SIorzf9o5mxaLUqijj9E35OdEOq/WJnSy3HRSo6MbWdpyDVqnlytouxiwUq3hOVo36Ajokfx6w4CzPruypw/UlgsJBiO1dTkTTflllhJ75zxHaj6O/21NWd6kW0MtzxsmQmu+p060klaAtj+Yeaqvu19Hb8q8fykOBq+BzihuK/bjyE/GZcsvV4QzsiVRkxjpn/7kr5FxGnWPknrB8X54FFvF4myQDYCseB7sZnQWxo1p/uOgw3qmrb1J88ZytrwMRui3JfSpGSnN18HOzk8YmsxTe+XW7PLb8Y6QkrYY5Xrxlzc0qK88G+8VlRgzRuX85Go4nfaXCSUSV0KrcY1w0l3I2lsn96OqL6ta3429TvkMZItkv58TEpNo7ffYxqlGa8zumvFZnryCyTEU74yepUNpqI5gPKnELVmIrRqN0TG/pzDjybS2LQ7L/4+PPT4CyK/79qAu1qQ9Sa1xbH+BmSagmJ7qKjEXOChj1mjvJd1v6Pau21PwtXhTWnzXn1JYvARcz3Jt3FviQ7n5db5uk5gIl/9T/T33NaFRI4lG2wA+QwhEfwcMU5FJmuVPzPz3RTSSorJU4BYC90LAv7ZA1k1CSn1hRIwmcivDz+fTx6C9fvPfaKySJIpTDAlxbVggPs+dO+jdDALiFUpjBL0vfTwdWbGg24g7NTtjsGCNRAl3nVXiezmtp3+nCT+jubeDKQHpIrbMl+llnKKkG8hGtjQjRx+s0lAfL+YkPN3C7hxdZGKk9iqRRy/oQEXs7Lff+O1M0XM2RTJ8Mn4Fym+x2+V2n3tkr5BQYqMrbld9PbsSDeZCu1kvW/2Oodw5S9Hso9Am7j7LT1Beq05PY+/qnwovaTylndUPswQGWTeTaUa7QWmvOZjjO9oAQJ7RC0QOLB0+OGKwyABUgwdMkO6nNukNj2cAXDYheNIJgvEMXRYvtBdN7S93/cUHXZ7YrVypwb/pCAMdrsL8x2y/EVD7eK1GfnuKNVrLhoFwr9icNVkGdOLFtr+2UECkiZxVlRDPem43YrRjalosFIOTibhCXVAGFQSr2A5cWrjW1llB/WZtRYg3xRXx58OS11GGQl0jHYhDUwG5FaqXQAoRYYlfOEqeosj8hyxewgWvL2h+kSLnbiYf2k+Me/5Z1qWCPyN4qVuaOJPkvWhkQB7GOo/yCLxBqFq/mJbpubX5CHkZFu9kRpAzUO+dzVTj5slclaAJ0ugtONsbEgNln3GvNwZVka/Xs1QzpoQXSRgJHvAhd8AV+J9zsz9W2wdEapdyVktPHlO9ZbXc/p99s6ZxOkVjpZNAfBm5H4++Ppe/NiOBJxPIIg843rC+wppt7tqw//Ub/6R4mx3dEosJ1RrtM6IdOMqBLotaLlm8VpDuUPfscByfIPwqrUsAYEHtVW+S9mepXaSyz0Qp9v9aqr41zso7L1ZrH1/0+aIfL8Zve0K37nTa2/YubkW2bArfUifGrJHWfWKoJEyFvyx8y6vTqZWa142Ke/DXjwkvuFXxmAXSjXWTsBYknTXdAinyYiBW3nn8t9DgcHXo3f0yodtcECgYan0XCmy4GIFOmwMVuJ+EZYbucMJEW90lUoCkJpm/I/2sOTz/WPigEaFxRfTlbKVa3Lj1oEJTSRoJTwcvSiD+f7V+qo0v6nBC07yzrqRS+BB8xJZFnHuuEg3EY11bwbcvgq2j52TlWIPgd7VycmqzxGYShqryiztmG1W7UP0KvvYBxg19tSpGz60ZmY7NJg9KRiEPp4uqootChrjk/XzsyBl83ROvwIpaNib1fjMYPsFPrzecDDy+APBFkru0HFhQoOBebG0uEriBnWSKLPmnEsdzQ+rL1LEkVujbCHQqJv+8zLVpsvxbf0UXVxTLHuVaJ+dTCAT56OluNRr1eLuju35CU1Okrt1dBrV90oXt6/enh0dNhunVbXJL3yTyi+qF1bX1c3E1m1srd/84KLNuKtNiP3RNSDVEuKoP96NX5PE+plvvk3jKkKh/fcNAN7xSGRSeGBDvdbVnd5+ockYCgljJTRoP6R4zzSnW7UxGyosksLEKqkoJxsilPejJmbDrkebiZ8fn+gP3LSgShnhPnADWwhgm0GqDl2ENd2H/D4Y9aS4zOqrmscfSz6smWfj6l3nAYdfVHh1M6PU8U+tMa1x3ttGd/2UY6LY8OMFedF8aWzcQHR5QILeV6DXRkbecIav0oaW5dPZdzOrwCWjdXTZbo9SmgNBX8dq9umiF2EtyVtiPm1DN2Op96or+IH99z68uq6wZe8zhkFJusEbbaRlvDE61r3tVmUFaBw72Ss0vrrdDNVJ97/8eUyaTs4Zpxvc9miH4//Mn/TPXOFTYbEPXRFzDo5og5U8fX1ORshs8bZcb26zcx+03LLuI1YvXrHyz/ZoV7087/iONvc6mr9l9lZry6+/6PLZVI2wYzcXN8kwOKMzDdj9q4clcJv12Hjp0m8BF7fBMq9u1i9nOnba9bM+pc62Of5flGPneZFq+i+ygJXCYhWgxNn4IyreniqJPmvZKaf3VLyC8jLd+OEEB5PF9eToY0CDmna1rDT4mODBAoUUKmUf/MC6oe63W8mk+w1Y9RdRPz4kXiceHWVgLf4pSR5q1RmlzcxRSXG64AX2YezBnCMrvDHcljq3x6TlU5AirNNwzage/+mtHU4TLxu/80N3plFmu69aD3T5/meNLRJnikVNyizK0dcXwTE4WW8CmQHpYR75uMbE+Hm9CGLNfWYz0kxT5QempoUmQH1NrL5mHIK8BCXK7G2g9VNMUxTNdTrQ8AIruBiktl0BQ4xICvXgsYQcwo6QBILlfirVD9m2yE6dBKT/Dc4PAZYnSMepR+zj6I2cWRHxGBSNV4jz8lcWg5izhDbneTqDPKQOZaJx+4SdozXNpn5HhBlPtAQd946jhpjveLDA/AEpl2HR4UxIv5Ndx8DMAya3mPaea43eL7afNesChtN5xvTKrDRVbelfqJ/WXAdetiPV14doW236VDTuewgqiX77MactIELPbgGru4pNc398s7Uvxu7h8jlz7cENbfAEwBIhaabJuehSSssoQXFATjm8YBQm6EHmjUdURTTjGkzVbH5g2k8EEZQ2gwCm5lsr9oIBs6YC8ilwNISsULFlIatTk4SbuHZHZGP3Wsrw0LLkRtvlwvJjcnUu7BZAxNzX1et41v3m4PxgdCyy0/k67l2Gjs6XrtFjvkvA+ufvjcZ17UwTvt8yD8x8D/XaX97DShhBhrPS70whoizqaCpuvIyZ8e94Mefa6+PSxsTi4Hj5SOpafC4ZbmJ+uazhcmnLTgAN9iR1aqoEqppRVbmJogL3jJxxZevMUNqugL9XQDRZglGHZdcgFVsQl1vZvjzTSocafqYCUDbrXqoRQAY2WAj0WahyRiwLvS4AmQJj8v3fVa4Cj4NG+AK/Ar8wmL1qKmn2w2faND5vzh1/VuITYPf4n8Dz/+5HXVOnJsRnrS5ppvx+M88z8pF+YCsuK338vzvMufXY1zzIfentqcQg17x84qP/26j/FM26Belf/rHxob/vbk6ufZDjJqeaBj6VK7ij2e/+Iym+zx2erINjr6qrae++4gEkn4gRJg8g5U3ubQRccO+KKXWjzwLcocvmuDAhZQ0V5gOTx+ugstgEnbgO/GNRenQ9OOpwz8WvmL/YtNtbO69hhvIx/xZKdBf9GXaZeRb3F7EZ+xfPBr/Xe0nv32l89s3xcZdhVxu+MfSKTSz+KYiIEmeSO+xVUSk6b2agUNoo2iV/xN3bVjdOw74O4l/MeQSaYaeox54lU2876N+ODxu/8/gHpyFn8HfTjglwS94Zkbp+tDQmhQTmqOhbR7BEB2xYzf16wwYg17/6MYkhPbmLHrgyxTERYmUWaRDY6TuutDDOrzGk4s/7qML4iSdXF8zv11ZjvdOAx8AV43t3xWo9x3uVjiwHi55zlpZyFvSFgpuRsfULvZ/mDTvh2opMmwx/9T9Ky8vHwj4SNCI/YFjUwus7kuHQGVLVu9347brF6/tjSxLAxSob0j25YFDOqUCLnzYo3FMaaEV1OxULRdqPQy4W19XvEBTH92gd7u9kaT+eYN6+j2j8qT25o14Xe8nh0T/0uOuOFxNwI0shYnerZnVCocTSdxrN6jhabB7gaWTvHQqvkNgey7F5YOBUCT4kHA0HonFk/F1oxFQ+4gp2Xh3l8goqVsXPJ12P6F1LVX+C2yBnXYHxNLD2VQQEW0NIRIxCPWuuBVWYbDtoGxZeU4CBCzAzDYbg1wEa3EnuFAMOE6NtnKCCSDLQfi81bq3UtmkdRX4mII8NpeVvcyn0ED+4F1RAdj4YdGNOhVCcJKYR/t+cxqIpSQf2pckS8uEHG3QmnKvla3IyEjMCvNJOVGhoXrg98gSCygr0tw16VdQsc/xG9KuS3VNcXdbhWZQM8ME1LY+W6HxBlG6BgfU3RiLal1KhXCfaCMnXy79neruMHoOf9MZ06A0AZXN3ae7CgJvneT8sgig7wWYgDk6u/cjYP0oFsfZrUaSnLJFAX5weCm01w4vt6yk5NTMC8ylVCn7E1q3Fv0b/2mHhFAoBBD0rdB3cZ7KC2vsT1HxK2Y02+xQQ02qHUY+J12e4SmNXEBbXqCYZx9YHj7L78w9/Ttpky3cVp2yvZeYkiendBkLcaw1zDzEGmkKZeonttyX9payLP8CSnOyGqe2BbpNlAtGGSYSGLKlsjdaHvjI9z36ha+m0DNz7nRyXB8s1WiBBJ3jQdNMijCufa8xtEHPhfPdDfHkZrvANwokPmuZ5sBQQBAlJCckd3adx+1icfdvxcvQxQ1dqbWupAcTRfE087wudsds+H9e3p7tKtCzhrkN9wNTsi3cjS+MHxJiI+bGkdSQ84uThq1HLjudm1NLsKbNagzbbCaa3TrSBvnDdV24bib7UA2JtoLcylgmMNJZ/fcV1yf7A+9OJWbsekmhPDQCYQ+RVkuNcetuEsm2GOzU503t+7bTg6CRDbrVR7wV+uG5WMjnluIztcHhdVe7l65paYC4qoa2DJVHwxLPQp+rLHgFUcWPZq4XWqgW31cekHDUjTyjghRAceQhWJU0rDCJX3Rm7P0K7k4UP2QPJdUcToc4UVGiqtm+VRk/UfEeKNRZ1TC/ZIWKTz1CvtRGN5lkyL5dtJGwGeBdTvX5RU8KbWKoMMFGnQeKlJnEcK+0dsy/uNcrl4S44SKiGMoLzsJRLRta5TSAoUqWWBFiqW/MTkpd+GQBeaEFX5QySEQjdcaXAGKRFL1/mf1bdXEstJHKiA4Smajb7DTtqq30YxM/dyLXewT7Dib/Z7ZMIsO+ZIdIK4pihlLsWlZL95Xj6srBgO1NpMy6olhzZCiXngsL9w1Mm46YXQ+PezWil8HJmikTSSZ+WHMkushMzekS4q++uIXzuXfSmJ3JV3kyJ2WfJia9CJVXVMt/KFELvjvNZ8FNYYk+c8b8SW3Fg5ChZhNnI6c7cA2N05jnVZCSOv+qDKJI1iXemwI2VXqbLnTdKM379AJMi8r6VkAh2ebDvjUtAWbQEPz4WT9TkAcYDSCmPWgAXR3Nd98553h5tI8Blytei1VQ37oo8wyb5+ytk5RYiQkMJxvjVkr02s3IvUJxq8wNVVNO9/OpKsq04aeA/plwceKFiVd9vtc+0Gz+A8pm71Pym5DCif+Rx0WORuX9yvTjI3oLaQ+dhUVazC37NymhvejdDKmpCuAOOn9l5XhR5qHW04syhcCB9R9J3eltm5IqBjrS/uliEMkImj7gwthekhUcktCWl5qoDLopsl5tH6sOopUHjNRBhRBugW/CZrgs8YrEa3GLvzyLuUVE9szOUm+FwgmpfX4wOC5uKiVbmJU/9lCK0dGoW8ZwL/KwNsMeor6qhpP0l+2dEyA+v2ZeXBerVjv8bXzD8nDbSM5B/VDO/7WSRCwKQCaj2+1LJOoZOZ9ltVNMLAeDSi5RbZcKivz36aytrKKNDFbDBijCbOJ04nSimmiHUF+FDXKLAOmfLpKthtA/CakP+5MgCNw2pgu2tLVSzwDSAhoXqt3kTOzXm2JbZLk2WLYkGyf0TLy19JdHHIu93nZb/BdVqx6QaKlUhrnNcqFSFVSBvx916QG2h+62XzJX4R1TupFjX/D+rgjYBP6I0HTHXP2GIOkW7XItCNg8tJldTUpvtLZGY+XyHuHyfv3acHxc9OwcCl5YYyXZAFESIa72e9fb2fhkGbRCWjye1wusNCKQzRkaqyg6tECua+h4HPFfit6jm8gCqkI5laWz0J5XNoKI4EDx0TH03U5mutmXRHMogKhjmikthbjMWbdxfGwxaP1R+aHR67qIvBYqjKE8EX54CjOwhbkNOaEruppwATNhxz24jDoesKUiIJYCOMyAqiUclUIzsshoO9ezKekBnzTx/Dcj2HbwJuFVO1+/vaSkUp+wwijAqETa9VECLNgMWrxZW+OOxK+qNkYzrtTQ2NHwKBWyisUHQ62Re+cTaoCv+DHHPdUBxdMTuQKV/vo3oLwngYZtc6+p0r2YXdxF/fq9pTrbBPI6gf5Kh13GymkP72gncSmTTPYtQ67DxRIZRD4NxZ8021TPyXumzPR9+BYAoL0CZ9M0SYWBVqLti9w0EumsBT4aMaUxBhZZvpxEkKl5WkGzJuLhXUS4L6VIo2TCpiktq4kdLSFHccSR4+0BSdBaTUYNoIp5qlAwTeEYC8kXiXfiHqAfHvcWd72kR84kZ1iCNtnCoKmtKJ6zpOwlbEzNB9Y7XCkksiuJiJ3X0PPnWrLhJGO/0suffely8tkTGlK+LEapTFWx7i1BJeQoYmSIfGdJym2GD96gg+RYZGhTtBJSIRpLLLFBWQeKH9xpjoTWAelp1KM2OteT3BWBFcUqsLb9bpIfhcwgwAHhHB5OoOsmD9uho4KtIriv2s/6lYa0+S+flJrO5/Jcd8hS9KBql1sOqgDqjKEPgZBFvJmQm8DQZ7VuykVvuAvVqYYgSoNpAPE3D7bkuC73aw9CrSZqQT2YDW127k8kz7PQWzoQb1cwYxy7qscRaK1WT5KnzUB2fW0BkYI+dAg6TxvdxhEa+QrTTda7/JY6WBgYu+iDHCVV7GKTec3vi7Gwkt+ZORpL4bp5qFYzQVClrgpku3N/tW1SwDyojYH6AJGkViIVcvLdSHFRBsYU26OcoF4tilLbibShtIn86ZaAD3i49Yiuc/Jmxtl7At5puYdb4BnAWqk9tfNZdrn013TOJ6QCcjXKc+J9aLH4fB6eAnGD/tzuhy0t5XKxfoXAlEJLISOwuW1OsfOHVCgGrVcRf9Z5ZISNhicHHEdH+D1PeWWa1b3StXiqZ2OJVuZsnPeTOKOkOqKVfVGoZrMBHRLGaqVZZH/SsfDvMQltETQFFM0PIwjG/GxuEQkVbEKebAaeBsbvD/nNu1Lym/dJ+OYRCvXkiDg6gC96QqCWqmD9yGy7ywPO0VMBTr4yHEyeVEakM33dhj/7K5d4MioFtbCfvlFrjYwDFHhfMVKXwe4oad784brLIuQRPYsdNYErFvFe3bNwcUCy5c/fUnGLkQkYbLBEgZkOCkHYXRCFE4vMJvFnzQR5Otte0vGDevWDqMOS9BFgd3HThp/AItHtGC17FCPeeu0pyI7yXAdHJ2gNQaOVqROz/RUdD6/tfcIh0OEaXb6293L+axV8vuVUeic1hMr8rLf3RlB1zfS1vYJuCXbSRDQwoBi1TFrKdwS+O5N48f17sSfq9qUrjC1Fvxf7TXV2nSc2091+E6Q38Azf+lduGQv8xz2ZWi9ft/8w/B89P3pl9pBJx8lo4Yv73trjIQD58mDxiqT86Bq/n34xFCDWOFwm84twK+BrONszWD351jXfrFcc8J8Z8QSz6dSOtQnIdA4n4R0TkHG/KbU1wboTb/t9BWgdGtTY7aNk5CYYnqWbYs87ohJEVVlk88nholD+WQWET1pZN04gcClcCrOwdV223jilp//1NaqQNy/v1YbZ5+kd2KLZLzZOEVFyZz28V4p4HDfknb2xXrGSfZ55YcPO4KtWaCnBuJCWl3Z2Gp+Sgxhln6JUywGlS0QP8qfrhKkvBNEUuauJ5V4wrgGX7yFVcnPBtWFqkDG7nnku8MwmAZwbxES30VWCrzbaHFuuJoZTrBvnhFJuHooyu2lXrC0jukrAB75WmA+jZ5N12dUn5j9ct/z98vtC4Xzv4fOOCh2+UjzqEQH2h88udKWS1cbiXnPl9GM+E1TA07XGytoLDGwH1o/cQd7AZ3nvnPHC2fep1QtuYEs+vS/H49Jv/vmncNH4AsGuDe+D32al3W9JTKUiW7qtzVxqr380KagUnAUWlv5hiPLiRsjr1Mq1ePJcgCjNOz+ALlh3/YpQhM7TadceuOWeMh+NvbhQ+nlf1q2Ud9Z/w3CaSQ/qe0RRcjwb2gYcS8EyHekX3Y06H4BeMTLls/4gXtemoO3ZKEX1+6PoOf+fZo8QH8Ewg9ONfeY978hmPbVWYhryzZPwnJVgL33D33yZ2j6cXYntb8xui8+U2Tex0Cxl7Q+kD5qZ8SWkHy/xXDIatYv78L4pOdhN11rynSZ3aNzXJYDM8cqNa4tBI5KovWSTQgQ7bZLACrIcRpw15mIpfCC/iA1BimmnoOUpsvmVqLlpRd5R4knhK1PIvlOHTPfvpqeQqo5KEhwwsBLM4vdaicO5tGRun+irWYEYbUBzGIR4Mp66Yy9Fdg0qIQheVwztgDVLBGOCI7lW5tyJA7yj4/XnvbouOTmY52TpV8XDveyKoVEt/OiHd/T0bNrW2y6X1+MOpK4VVspMatyBLHvtAGF07O3uUxmSvNRewfv3ducIbwytlQDFU9ytPu/0lgsEQS5BprIeAkUFhtzk/HjhDo1b+94RdnSmUJBtUlS9ppwzM6ogfEprvCc1rK6bXAh7tWNnCdvWG4A6sZYDJhiSVf2MDiTqgJpmJMmfXstDC5vU8pY+nsTfDgMa02FfKX04zF/FxjQgl9bABS5wPT04EOuVhxwxKK87ilYk11zycsQWnoLcGAU4YsiNPsjeBwQgHek3DBzEJ4Ea+N/o6/B5F3toXvYBO4hJA2RvVDOkIZhz6QTX4GjuXn8coG68BgA/ADBQgLy7GV7ENrMxbMtLYWAgRywCPG/yaPU4qPXYHVdgtjGLNAFt0R/QvAPWylMbsDM6xmSUln3YhwQkQYVwK7o/6n/jecIXmoue/7zi3TsrTK3lonNX2hRc0n8lGV2mp/OJL/RRPff8Ladi8vm/YR4OSiSs9yvBXE5oFaYyHbgsZjwLcLjsbeNft2xhw9gvph99+ajkHWuN16LR3AmUR47JP0wXF6YjfMjSBuqn+mYmOYufNMwhdo02EBtGQAG4GTJKASms22yTXbFJ0FgFt1gQ5LDxtpUPcJ5p15udGf0udJmkI/q8C05rXDuHDKRAgTAexsMVjlVurfbdCIht77jZWpHo1Ys3N1dM1GKRBVrnMkd3xpwRDI5Gd50ele/9c/WlKK/Xd2+lwmY7mROnRuwYPKx++WeYfjHYNPr3DOl6NLqLjkTQ9zqTTlceMy2NxfvpA+uLZrHJdoTS9dDk1xwCac7qp1Ep+XYS4MMlaElQMXPjxEDIoR0kwOtj5IwzIIKqK9WhGiwEmGE6qghsdOVlJOiQWSukq2eEmn9NgucRFO36xLnXptBPlz7YfR68M0Tqc4x1qfwjO0OSvxuVI8i18z+Unnx0jxvYyQ3jqBWgNvm3O7jqXQFzlfujIbht5s9O+ov0xlVF0qYJQf6P8fl5086ln56MRxwmxs1R023TdKiaAoRHbGSRmde74EPMU2BYhMo2A6rkEzI2A4IELQ3bUiAowQkJCvuGmGnWlMyBxaVl0kjHRFOvbvRW0DRVj3iDn9mtJCcEppMly3pfrjWx43pJ69LNFzR76/D7+xojzMoT8/8ucpe0HnKPxin5a3e99urvummY+eWMyI+q/qwb8EM/BvvP5a5pq6UL2QPsu2O5XBWeJXUu9VrOkYnhvsAEfoGemNvW+0KOOaVpYgZw1miqNs+s722co5cC2nG1PYqc43mACqY0lkJgoIIByc5fZiXTtU+CJ6oa02B3WHJ4QImtW9F3sTV4Pc5WFnWB+pkdhOLdpAxHrsohAdzguezRstEMd8SeXQEer/0OOUBMhKBMhuwcLSCB4YRwgly/Sao5ZHaUQi/l+8vPycO/VhM+83kMwDKdgsV1Somv7H47zktllcXSHzSiKh4v2VFf6/tm8w9j75h5NLGVf65oPIKtmqjBW6tJBJDNR36IWLREueYsqGK8z504pQl9bNhX8pR5YGh6YIlBCQFhUpEBCSQnhdZIgm6W/xGd0uOQ5nHfbQkffWDmjOu990unz+p8xEq+37z+48m6JhS8oYjQm+/7PDTr8124/hspuFSsPjhxljWpCGyOqhttSQm3q1h3BMECgKowdb4S/EH1BA0aF8abu4A5T+jWeEexx/iISvt4vLDLyl7p2c+13VQOFgyHwSOpZR5PbzMHBAR9oLPwq2iFqj5/ocHOGBYtA3b06K1/XZ0ThSqLxZ+dlDQ2xuMdHv74MVRQ3WQ0Gq2tZLYBuaZ2y2WYn3I+G3pwbn4dKynhPHEX93s/t7YcrpcsUK/BgbGeHve4JhdlMV7D5MkXzxE3S7vDs4Pwi8PpSLcr39FXc+oON3uMRhY3kplmqc70H0CJykrJViC7na7xKbTJ2RFzNKidAY5TIB5lLiLAkfZ5Lhi1Wja3eRoR7OkSO9VwW8sTlzGwXIyhsq92lJK3iVGzRi2jjGWPyu1CKbut43IEUgaChmqKbxaW45kllclzLcGo3vGKGW/hQ7cRGpuH5qdnZubWdhNRYY9xrlBlXtgoHX6ntXBMECx6mpAsIPf8gvF7/fqt/vJ0hvuPhX+T9AuC7GbUN+7eNrl+TzvIKSh73G11XytqCBXc7s+LZJTxH1OD8IusIoHpnw+FQMpTVWRO9C+hmBzht6lNbIOmbIKFgIYTwEpYgdo7zifZVjQ1hI1yMdy2swYOKh7Avr2G+49ERCVzElY0si9jm82u0K1hW9XmbeEmQVQ6KCF9jIgvyeZRVegeu0W5dW3+UUGOLBmFIFcqr6NLI3pKPLwHJWS6TFCKFLD3874/glvgJ3DJ1W6JliK+r7yVMXuMFudDjBApmDTNmRbhyjpKqd2JpAi/0zq685fJCG322gFzda4hSJuGHePgaU9/XfkUPQ1oM21LBQqpRPiAl1SeRUp4/fc4pUlQuv97RYp3ODC6QLn1hsHLkVdtnbckZ6UDXmJ0EQveu5XweS/cS5PprKSX4VTzdeqseDivBwF6FElq+OPREfXOkzw2dC1rVOtf1AKGLquVjb7LRnSjJIwNu/GpcN0ABhLU2S20igv4Misl2sZuowNyNC2ZsqSUC+E3hSg3Fez+t4t+Nhz4QWcZnIh8uKu6fYEw6xXHP3LuXTR28x8Z0aNHyUKlP64NrGyGMugTMO0KkaLxBPV7bDsWv/HhXfl1Lx6dcxQjMya2UigTenPDQXEB5+yzocd2Vhxmho432lSQ4Xa2xyrWg34UmN+X6/fq9um2NrfonEBhEYeo9ECIVmJw0eduzMxdhLK4u+FPiO12u2ODXaj99cOGPoOriCJ5WY4lUI222LPuqld2+erqncKOlB4bb1tspqPjRAb+SGvQDfFcf6WdqCxbFDy5V7Dmewrl8p+Z/G9ZtOOKyvdgTh47gfb8gFu6KOdpCOUUiVsGus6VJ0GHdiCDYuzdQNwB7LKo03Ki+vVPeDlnSiVvQP9ClXaVaF2OSa5IBubqhx8DBXfiPXhbeWZagugYJCP8vPYmTBWLqMleLWDbj0R1WA/G0VdMEfrE/GClujossLCQhFj1Y+6uhV5vHnQ21qhcHdQFQTVxhCzqfKYa0Rd1M8NqVR8dILnEyYJ7OSuMmtCZPWqoE5nV9LbsM9CpUF6jNXBPQMD+V2q1lRt6okylowccK94sZ4I/vneKh5xW6Vb3FlfaQxU1dF3Yq4U3jYvYlVMCzYfwkVLXEc9XdlNvAUuw63yaJhBQ7IBL4biVovGlxRpps0YpkddvBl3qPGjvPLax7CLf2Acx8N/v4J9b+Vdn66YXlMbcaRYUba7ADf0pAyHYBeNhZFvHnkNFK4wX+IMKnEqVWNdhZqdEZInOmkCECYnnaHkR2vRUNEmmh72K7t9Um6b0itSy2yjzebLnUJOb2gr+BXfj3WPkoXxckeomN6IHNr2S7mAYkV8VVa2aztiHRnbQtKES2Gl9j6uQlLu0G+hRUuazTTi/Nn1Z3py5LKw5s9D5+wjehw2bHyxJRyxcukwk/TqXuBxns1rLKb+l7wAQ2yBX4ApKhdoi8XUM8tlcjULGnyo3GsZwIkwKV4sUZKes1hL0fq82OrfK7Ej8grGuDoQf4jaDOV1gd7wGOrbwm17uWwxnUYes8LLDNVmQdH35ZBZe6R7loUKQaF8Artf2Qix8d0gFeoiBDhBAKhIuHK4zgvfZOHVluCg+yvyRJhPTlzfLH66tnj2uQMtffaeSDkKWAS6//a/kcIb9buzE0KtmhzqRf9T5fYyEjoQ/xhOW2iojCXLxnuj9iMXEXClWmnkCRd/NbGqFcsComCDHlielSsBIKexRswLyY8BsKKxeBLb+FgnWO6vBE0sGUOD0wCkBIiPCWE0ejav4INHmS/PBH/71z/7ZuTvgOMPvbUZ/ksRz4S3E7LvJPPGNbz6RFjk1eDf+ew/gmxuUcWXGX6pC/tp3v/QQ8L+uDgEM/IpIOphAD7btpKMYf3xfhqrWETr1+I/5OZ5W2tF13cKqWOwgpVR9XAnn1NwSiqPvlZ6qJ8Po0cDxRJRQeuZNFHJDS9CW/IVoeVRiJYkPRADmgQDkY1ICHvBck7u8TwmYrir5G2gN2Tz9BbOTL3cAH6FkgsiGGJvdEgrQ8vf0dWqZA+bhT8xxO9xuN2btoav/KZ/PR4Al/MeLlo8f4bKzySZMADuTtyORZ1/pcj/4DNblLe49r3/EJH9TFAX5kFQsPF2MYR9KgOovx/pDtb2wfXkWIOrJppiXAQKR7JppAvmxjbhQFDREQBHZuj/fEKyZgYnsO5AQZaGDtaaA81yRyPwne99BvEJ+O7Zp0SaLbVG8CX3n75Pk1ZuCiTj4GyY5Duluv4/eSKo//Gb3H8pnhd1cYuXoETT0D3+fxBx2tvsUaAKn/m72pS735+DNSZpGbySFqCOm47zO448Pq20oqthNrSaUsOotL7BMIootCG3CZFWG1H+lPESYFCsBLqcj/onAB+V0dCrMMURTIynVWCaQq3XJrzTcWSYCSEHznAhog4QPWPZdw/yQCLK1pn0jGxFvvH7NNoJvbC3F4+GdnWCqkN6qxgKBxNKfgIzgmiXZE6oeDvOX9Tghx1eo1LzF45+toxxojnlySv2Jm3RHUyGBbbC2VsPAaIiWeO9myovbcf27BgsnxObJE/M1HewEVUUsWkUkOxygsaUY0SuZ6+e5y14ydgXAnYRnIl7A5n09Wr3J5MUt3M5cUAfIMZCHEw/T44O07R7bQs/S6RZLWiS4zjRcRsTP5Psg8R9Qdxwe5jDuohhEAFrvK4R9mRYXbWCCZfAN8EEJ1+LlWdDFGICG7dH5BitOltK/djqCNAUhL+kxaowOGp1ZI75SHfPhVlSyx0avq3T1GapaDcxiYlxCgue8QMy1oc5MUpT7WjEYY2nUypf7NaT8hdZLVoZVZwygETGXIQxpj1BPJxYo7Q1jR0+MoziQSyw945P3Uwi2MQWJYUhkACRNqusiSxVTCCMo7fG8E/VZG4b4+LWlgj6B41MPnaT9teMUw5a2QR0RQOL0uYcrTXkGBYhJ269hIWaNEf8q81Jq0vUfT+r9uzhKKjf3O86tqre7WV9OKb6ttEbBSOm3NtWqNk1d789O15mvBEiJjLwi+qtABu9QPSEf2jV00G7Aimoy9cxexGkWNj1lf2kRYw7ZMQJHOuSeVkXxcq18dhBJBCtMJCnmsnIo1i2y2FrbjYuf5P1h0HYeb9hZ6l8l/OJUPDBDQmSisQyHJJamKMfYtIfz3AQFXDl2xrQQN5hKviI4muQFrMlihsxa/5opz0V15PtxwkRIgkvW9UhSjkiEkg+Mj+b/RVuj+XEsxecpVv4VssQEDgmk5RLmCQsV/SbNZrLCRViQlYqbAjVhTo3kyJ6aQ9I3wQ4ZvHxEySY+T5VYQZiIZAfocZ6lwM/AGglG6fbp9PfplJuF4pMAifMNd1UlbzHH/zNbUcEEstnLtP5qM9cfc5l7Ct7v3tr6rFB8qS8XRDRM7kvqKSUoQI2LU0Y8s9hpOvm+rHk0NuGoEnKkha7E9g0nehPjOo0yk414YCVCVYMI/ePciBWRmXsK3q/j4Xy2WVaEh2c872rkt/MjnMWlncJKSA32CffTbLmofCaDStg4XldsiHL1IpleZBRtXWZi3GPWsJ1tv79oWThc3Asz1HMr00gtYUqe7n81BuTcSvfwdWrb1tRpbC1xLMo/qS5iupPey4WFHmZQNqdal3Rwm/a6m0TX7N1vmgVujNEMquh8zi7UXCrt6XGHVNkZp16mMlUZB9ymj0HEKmPGtZHJmPRIizU5b9RFwuCHkVuLtRPQ/y6GtrNi+yY6uPZNCFf25RTp3nJioAYJLGspvKDA8b0DrjsfSbo9N+RM3vz3jFwO7rSRvoeaomi9QW3WZufhH8vgksiqHo0n48A5w+KqpiJTLHjWCCqgKmnmZtH+duVa/z0kk3k488Y2cOO0gqyNZtcD2je93kjKn195cu5UVxyrJhIprP+qM+B91Fv/Zd7sCDZWqua2kfwa/8F6+ZGkfJ17Ew1dJC1wR5nWiFeQYivWLHkc+e5wdYlkD7ARMyIRUmHFQbN0gGJqVU1KoeXQUYMnHdnHBS+t/HlK3pZ+FkhIs7fSMPKNEdkL4YlDgCeMw+TiSzehBSI9CrxwcvGAvIDsS15DeVMUAX800GfRdh4Q1ClWrV62JiC4blgtVNcoF8EwUJp2N7ZRrT/0R2yOwzgwe1aRoMew/h7rRj4BTf1a08ThXpHOCBlpB+7K3c6d/84vJT0U5aczhcDD8VAinfffXAJpkU11khngeYudcCJK8Kqu+jTBORRYzNT0EcTScj9VQmVCw9v8/tfJkcIcwxQLTB0DsBf1Qb5Yrmj7OMBAvam6MgO+iV+BJ+OqJINExjxS5LLLCR8Mr4nLIxee+sX3xaurlg4/NwON9ljCJkYQWJUclkZJ4X3ND6eHZDWC+I9TQlKUjJR36+CFTJkDIaTIrqi9uiQodDod7g8wK+8UJWGyZpbUoXslBx7CIq8BVvdfJ86aIWeSENCaDmcX2Dx0laBpHA0xuZa6QL5+c0aAamUWCvka5G/0e3KiXFaQj7+EqwF+/nzK56/k00SkfwDcyRF+/fatHEihXRmwA0//2BioSjQLOdZHOlDe04MkKOj5AvFxFYo612v+7MKYFGuWskwh4at+DoteWNxkeQIGXYRm0EnDNI9XFa+THfjDc77reZm3dorIr0UieWc8RHhEt4v/BL4Ag9rn58lJ2y96nSdCj/J8Y+1DiPc7zBlSe6zS9Iaw0zUxIUBx+4jmWYCdkuFBANJZsxJbpZJUa+dCMHhGLp2Qk4bZosO2rY4/oXHhIG+ZxV9Z/IwKynMAZQ2ftFKlVQ5qNHqGBJem8wzbrZmxIUlj5TKqIJQ2uoyuIMRBAQzI4tkrChPYYXqva3z8b/JJnOcURwowP5HqNvttV7fltn7mo/pjZEZ5jH3omOz0t2Pfch7hzR9F0yvhUNYRZXxO0QvEBO+MDbXO4MSzX3TJidCjSDCKViOv037b54+mitVyMRklOnfSSAjbUsuKRUOKOiQ5rT7sbcw4w07keCMpURZuUi6neg2sxHTzzg30Q72rX1ZzgbMNoxztPPa35KgclJjypFNcSXb1ovtYsfLssQbOZruOqqU3f9+ZDot+Ss8NurgvO670RcZW1j2vcZhgh3P2YtZeVQGPkG5H9NNAGADeb1io9sOYSRCzmsw7l+Ct4Mbg3LSI21Y5tLeWegVj4sgIbl0zTSLZpxl4OrjEpG7L82qtGy91ngKSZOO2dfiKWceYbyCYbS3Cvpz2ra5rHOOt8CMIblVH9tYOQMO+mcku0luz8wWrondRBhPnciYD0Vv7bD8ukOi6uteBKooRNu9dyw7SW01zj39hxVALn8FHN3WO89J9eq0Nhl3mp1H7VO2fxi104nSdvKFi6MXB4foKv16FTtTAO9ieLqudZ9LS778RZVWzFQaUiBQd6H/ysFNezz4N/cItGJj22pq7OvNPbLWt7G8RhxnvpS5B/NdSlM2cfGXVnk4L/XkcxTaWeV3Hb6sJNI9pOMR0Uo9fXcbzT2Nyrp/1gQdWLJ/bqVQ83x759sW1lOuvZPfuqB1d3q81fDFQ9sKF3YjYnjqtGFw5hxTQmVHryECWJvg8PiKxHDpB/oC0510aGXFbgy3743p0/YQwjNZzsPqR+oFiDIhNG6tc39xd1RByXiMT2e+z/QRQfsyRNGU5EDrsn3wBK4gDqJLfTfRlMMIDHf/vxAQl+82l0w0SBI+r95kMiTfhpJGYUM+qz9RRoHDvcaEQMwERtrPwVbeZqaPDlepU07Pu5N7euXZrXJ90CqE5DCGlUMOCwzusHarNio5A5d8ywHE+MfeV2mZn3kZhiEFSgST/vRGez8j5ywwqo+DiOh6pY+Uex4aUZOg/mbhZJsM7FYlobbSBT1FyMJkMfZrc4hB2OChDPFagQrnDwXZfF25K55womm2w0tNNiJ4o2mjsvs9tk6WS0xjMDlpLNDOpNpOnKHOtJP6sBYc6ehqmsa6hSs5QVEg4aRmzCdhG5/slZXkjh69BEMMleDOIte1qymrgydYNNv/eOHMPxRjvWQasFYxMlWYvW2w7/Y3jSD6xnmbdG5P6rx8Hb0Y8Fo5mi0ZsZ5THuUQt0PGIAC01S0Rgv4DYMnXEJjg2xKuzWtsx7x9Huom9bU1F5uL5r8LH4Aqa81nEAvyz8cA6TPs2909zURhD2d0kXNkYrkU9go0/Gpvs+l62yOfAkqXicajXEbIyAqhULU/cSxls3EZP5phfsdBIwIZ8tDVnOKzMOA7oEQAvuhxUK9B04SyNqepqIISWvMxxJ5CQz2MVGsMBiyiDwM9JdhW24bmOkMgZz+6QBT4Wye6sF+DOV/9pHY9rAETQy4OTweoCfq7Lhic8+v0ysJqRDE9dwqtqVnNrObMisP2srK2IpPog2/bjeiV7owFtMIa3VT56FnL2ZsfQeA3PCttRc/9mlXMZIrytHMcx3BJT6vH6AerpC6sV7XeHxHvGLxnSY0c7ht+BIyPoR6cr8UC/Hgng4z4Kuju5wx4RCFxnXoCNQEtxf15LZx42Jyzrvu6hobeWm5QRnOuyhAgCOi0tyMorXKl1XqBpoXcHm4dLJG+axgp/rmSZzbpEHQGC1Q863aOP8RnMKoJQlXE5M8MEVxWavHMTKC2YjXjK4SV66nqWLNrC89vwgPVwEVoH3sdgt7htYaHK5jUJ86yTyq4qraoqLaHn8Ge++NPQkfxEDZv8iDzALNzVMr1xfUUJVMXFxSt1EKcmfWW5+zbXt2fkeN+nMkT33H3zof1TO4JBuaU4lD7SS8eurso76aK7VyLqcTupWviuk7Xu47Lr05mxpWlZlFpo965+eQkP327JH7/so5ya+Ob2q/LXEAPeLyJF4DkT5CAvqrzQJPW2ambTrXjxW+reCY4J/bzs/+ckqF1CJxvnqAlBaIAlYSi9pQrW9a6DpqOmz2gH57TdEaPdbZl2b3emaxMPGPCQ0svhK1CGZbxlzXbCNfKXR7SI3eNTLGCLjaOJWCweC55KPb98+uP5Qq2ls4lqx8cX1R6Zy2YXHb4gAvpNrTVwh3aNd8G2ixNtbCgnjJMdN0p8gWzSJi7DRA+9fm7TXF1VZiDkP61robhzMiRLuVyxCOUQ0UG4Xar5eCUm0yJcvl9PLmTzJXGCL+1oY4Bu31U4sGAVc7aDVbcAZeDRJO1SgXLDSXIVnKVfo7vQx1fS2cN8wCc6SZppEykCl6LzoyhlVS+lhjvPnxe26ETppn7OLZC+QbxGwIJztab2JxruYdQkUj4lmuWrsW8wl+n+cwMtgU4dfOeohBBZ4BJHc2MSy+70g1ITHmDub8UHtaxv2QKJDRoGvySDMHTplpZMaQ1I4ETeeFu6Nxdeu8StEakcHCXUv/ZNn4eL/Zj4Bo0N4mLFaLiTYGh4JRvXlnbd2VkHFDsuiwcmGz9hoy6uo+5dhKbkFTn7Ef9ZME01/TtJUZGmBEb8EokfLFdIhej6e/pZQs8HJhvvjHkJQVfsNPN7hDu62w/jRsbtDDHRD3pqTQIdfAEScAxfivcutOqN7CPDNLqBF1we6zqw2Ku4Yu2oGWXigPlERD642OvEl+L8At9fHSFLoVTqHV5w2bXbP2R22JwzPnJ9FH9wvj6Gk+l+j+7Teql6JWNohmV1nC6vF53t32P4Do+1nZS77cFrC5560kcH302jgtj3jIQuzdw6u4hkjkeJ7bTI0/v0Sqe8xJLdz2NKa3UzIrInOsVyum3syIL2oFm3WCLBsBe4H7xhe5oOLsWBGxgXoQfwOdRifd0nPAc/IfptGGY6meyVwlGWHrz2w0NRzJ5JM7ltdf3F1bv38ZCGAZGc4Nh5Vc2s+kQ6yB354gH0ZoDCXJNsh5tC6Uw2vxNP/hkRzlQruc6ocAVR8OVuQvY39IVgXS1/37TEs8mOdRszpzSY170+h5oNfVgwtYXyckBiabUQ54o5+uXOrUNGlveW6MPWDvCfsHY33CAhmtq7zmJuPhmwPezqkAbckkM7gn/bO7pcvGmK7T3WX+nJze52G/a0ptwuhKd00uCo9s1RS/VR1bO2LINGQnSHJyBu2tjzH4DFRtWwV6Ca3ijRKAygpBtE7v1TyegFAyf4DmzXLHd0D1fOp84LgcnluHKvCvY9eo8EsrTZWXqilUbPISku8MgOvQSahKGFt+DlRuak4FXxEWJY7K+Uqd4LFW9hlXD0JE+8OTdLZkN08H1JFNqIUWOltkbr5FKLSG7yietOMIH+hu6teC1fSq+M93VUSuxeMID7u2w4m2u0lwXXX3U+WFceFxxHAoWhyDqoPE5cjrzYCPi9Frr5S39q1XHq2/sudhlw/V1+MFJsDQ21uLaWJwg77q2FHyCpHgEmrQXeCs/Fg0OHWiNCrRZuox+Y3Zh1H5faiTgJr+scrm4zMJ9J7exVP5mqnLL3RxEcph/yAdtlp9PbfXeEb7JW4yqSY/X96B5e0zkyUsw0nYXKCJ3A6ufjMKiuSrOLHEt0Jytlp2lrHZrlBquZCtCRHK7VEvOPs1gAhZ5Yk/ZKHfwbgHyKGU1eweCussAWYrm2+2Tt2+3mDWhtiAWecV0osK0Y7DZhRvZl9oQcgl4+YHKH24uMEoJ2WlWgC6D3wAlWKXnqXV9xgO7EFcSWhle675adOBqdCLnu7V6OURhE5tNvHDqKgnWk9SRtVXVXFgkQFXR67A82uFeLhCHSk76+71gLulNh0fHwbI5SbnfDWQHpPzH+E8sJl5tPq1RgkDH4EjjhdrwO7+5meU8+X96pASm2QQdk9wczipKKhlS+rhYOh4Ikzur5WITn9ahp6nU8kyBcSpoTD/vfhyftX2Uz6fhwh2Ptcv/muV0mu5ZE6FarawExSGG+IBhWNl2o5A5oVgke38tIHGFTl9zNuP6wHgmkLEYR/MYHf559k1zfIYSag8PSjdEuIcB9HpfD6faEx8i1B4hUPoIp1GrPwpVyieu+Eyr7C64jODtD/Cf+AIFPSPY94o/i+afFTVLuA232KbIkK6oiCqWAXDwUu3D+qkji5Tqz0PMz+D6OercWWx6uKl6KhiKxOn13b+i8aVY2iLLaqszkmO32hocnJ5Rbo/9nu2I0mkylocwlGw8Wh938bLYlDtHyZA3S3aulaM5DrKo+GXdjBdlxdsvkIq+xS/85zX/ObRgSmjBsWsg1D3UBdapJ4C13klNhc0zeiXcbpG7LuXRaXTDFMszF/vXnroh8Ae3Qhi6fB/PnPH+XBc8YhND8mgjQlMwibrQKvNdhxlMZTkMhQ97eMuoshwUWMM4vDMcYiHcTJwSxmTTrE0svRUZ03FqFYEYkh3/M/AWBZfR3Bhi+a7ShRt+KE7uAptFD7UTGyYDl1NRUyB6/YpHviYa9cDOMwSfxq/DPVrXWReI1w+mmurTqwCHnStatErSZrTQ5j4ZnNwwv7yeIGOGZzKfjlYVAXHELb+LqlQLcJwA4jt6x+AyPm9KprIt+Bo/myN0RjvUS3DOPy1ulevFIUqCD0xOHSgYhA6Q/Eis+vJ3aIxzRyVzGr3o95anC4fd2lnnkpcML+mEu3bHbzKwSZDKWlWkOHX5Tm4gzImRRtL5ZrGdKFeFBiJs/rtKg7PQ3OyPlgSX/ShC/X3EMx5Zw9nnaFDLqRCW2lqSobH5nLRs7TuGFtDpscuVnGF40Sie2C2/5kJUUnWCG+Ht6Iyfd+FX/GzxsqeEJ6GogrJbaTGEKplK5fDgSScYXfTJGSDGJRKXRaAMORsDDtWadAu8oBV4d/N0OIi8VX1CEIo5NCJ5gLAAANFVFgjKyKH53c6ygGqwQWhyT1yzovD3u6qAfMU3Vhc1+un3hGu/SjZ88bZm42SK7WPPQ28263iEfjrqDon4Gs85HEg7gbDrxjsEPnBUeOr1pIxF3Oi/aea/Ubsc9zBwjFHovBy8C4HTte8uWfvfamQGWqdh+F7qlM1xtpPDSdCgfeWCo6wsvD40v0yaK63zjQI4IhADHmTh3DwnBP2bNtkARE3tVYWv+1Uh8XH+t953+R3WEhT7nrbbBoMeUu9ZV2RFxzHNh1D/JHkSOkJD2Z4XEUlCj0bqLsopHmsNO7l37+1yGwCaDwumOnNYJHEfxis7cNnXQQ1QzxOD1BRsUeCRRfWyQcKp+eA7ccB3yHt1r4AHgBUQpzU25erdFa/fcAumbvabz4VSmgPEt6uGk1Bi8XDxROJmWZurrvkEqXXUPiFXMXQlkaNDK5WQmMdreszlNhlPMSp616063aFolOM2izSxazwxsaZVENyxUbJUJ5VkRB27ko8hi0fIlFinlGZ7jKbz2IBI3qScKbkv7tMlBrmKQHGdOxcVRCT6UD0Se6lRLS9ndgUjcXHeo0JiMZdIskvJMQDTDysnl5M4jM8iJJ8FvyBGO6CqWjEBJu58qZBBhKeTAA/6WCv346F9+OCXEzcEyJEl1VnGl63hezEFeav2QIJxqFuX9EStkmj+M+14kUSkUxT0hC3wD64bX1a60FK6xRqPJ+b94BeFxZNkjY1U3PnfqS16fWZ5NWnalFCSbTMSPMjz/2/IXEhglccW1E1tPMGtSj/JYSUyB9+Lr4L+A3e7RO3zlUeA5WlvU16dKV05q9uJ5TJ0puHPZ3ep1F6FVghKsXkWenUwi7U8ljns09mL1F5W2bf+1Y+5gcVqMB4Yj3luZkFOS0iRNBmqSvs8bx2QtZrQSbhuppQBNzpOMshnZCvK+FEqAH8+2uUlc/Bt3SUYwRvgwToHa8Da5LQoDdRkmoFN/IlOckk4X25GIydzUbPqq5AlVA8o2vO035GZfQOfDHQqzGdsmIpHjPVU87QGfrKRLFgVMMWD3TAz8SZUVbyPcaZcM4zinvRK/bk4XM5iFLsxAHa/gQxvA+zxA+1x4uh+SPDe4+fXKwcV9Umtk6Vh/COVkB51ah8aJxYuWo/g6PHz3OxXXyeL103Wt2m7e5Eewdr/d+/au79Mnm3+jXkgvbc5Vl+oe4P7E7u62ChMkSeYwXrXDs+OogrXP/zH8qJwyTK+BKqn1qmQP5Y2/K/tHzlBxvnuxIIMsQd/dRinIZ8rUJgxwtlTLynjKjkmfTkwKgwEoFtsfLq5KKJr3luaB6Ew5r5tMNmgmQxH474oUEygg+F+xW1o2t/Upt1abF53OMteKaEGochc/BUVwWsbWxKAf8weQOCjBZnyjAWNJ3UXNkiHQF400ajktE7eOQDSWwRishDEYwsfwKc/oWuzdjRUVhuNq+X6CnvTSNtLZKx1imPt3NTC7uV1pLl8JjsNuv9fx+ccChesUAa2wHnCW7JgT5K81f3pHo3lxAu2+NCycq4tKxmLraDennBeGpbnmGLywPk88vL+/rEPyqCRaK+rzyz4vY/AjLd1vbqvhinvKw2GJoTUH3IrJoRptwztdCjcORMpSzQFqH1/DCap+gJ9ZD1+LJ+mbxc2/tAJrws20X8hRsJwuu9JKy9pjJwczE2hi1u28B+HVdNo+JOl3saWYYyWZprOUHUZip32CNiyPA+HokaYwA6thDGbxFfhEWWrsbcgRRsiUYzkhKj2ljb8wiTun/Vbvam8CCoXimTFOB6UBOVWK7eh5vIqQsPNKSfTsI5u1z742wgzYXlcuXoj6IdzDGtWQcvCHL6+sIN7eKgy6vhhd6PW7k+w2m5gHJlUK1u9wYffmezNqnWGqvoMwh8WdhSszFtO5vnrljjk2FTN0faiptCy8duG21Tq0fENyWNYvrw3V/WZHTfgtuDiNQYkKDtRSGNCViI2wo4xIPgVZTbtJP+19sOufTVQWmUPS8PNslUJ4GYp12Ed2pCQ//ScJJ0fRUoeEdSMN5dkNmE+Q1yy+jk1V5HeeZlAofHKbgb5D6ZGkY6ShOdtJ5ZipP3YYbia/LaOx0pJhNKzEoaBM8/touxl9Akm5oMdzriTO7sxbzu5689LCtcMeIaoRAumoGiOxd3g3EDaMU1URnZgnkLvyJ5HKvijRcsImfGoeLk/NEvCLcctoggeT3w23z4nIjCjahLmv6ypDFD+ncqviDKNXts8C0QiySxFFTPUOEftmTPD8oAj5T9+Dn62szcDRzW2lPrqbFsNMhHdyjaiND8pLq7c3snSt7jXk5bT1x5VSuT7ANBmzyYumVAzkG3iR0AtFA21VpnsmsY4YzsiciCMl+IwF1m2o7/+nQrLJEl1ne8WyAaExI5UTYZnF4lKbl6I99e9q6mZ3hhWFqp9p5P7AMypFwl8M54XZBQZEDPIvDVlDSfx7fKn6eE4pGLUrwCFnghRkIMQRODLpP8kdfLn+L/OZKqx3uWpVK2Ur++Mu2yuz01a5Lz9mc5Bc0FcDG1KZJdchBu5f/qqfmR+Ex36fSCgUiZ9tbi4vGuZQfZH1gxtYHcX4/VlkpZXMjrLC/euiX5404ShyUER0fpxi0asCzr/qTJlJHZAjW6wc6qKMtD/6UJkdVkNhb87ISCcndplkGSUxuMBcLCQF8WU1TFli9k5ajkaDCToauSMzSQL/v3xbW+PvE5KylCNI1Ux9A13ajI7rk4qO7ekRbLkummNJRqaLDHy08HPPPKaMRerjep2i9bgOkUiOlJVe18zNhFLbIHKludZy88BwpxMZmrVjh2m/jr53t8HAP3e9yTdx8CrhjO+QQf9Gm6SEgA9SLhLAbaHbwXpTPWNX5c6+u57LVZ2G5y+K1MhBvAj8dzxwMe3QiA8q8fUYxP1lGhsdE1JlZbyxMog8tkiuagH2QeaxlCDkwsisIAPbMCw9sxIYSG3z1NCaUqbQL23CiA4wvcD8oGy0h20QpWJubKoMZjSjGBCfA5vs23wmsjKj0t2iBOrplKEFUa2xtg1pzJqMZ7KtFkNhJdwJfrgdb8WbqqwaVpS8/tyJp5hUZB9f3LNITVTWex9KQ6UqxSpWxBiLP6bikajNjmjIUlj+SvTqo3jg0Uesj7p5L815ZwGHaCzT3kmQlqFpeYnUhoJHZzqoBpHVJWzfFV5+HQeNgND22jS+j81Ax3DWw1f/11fxRve5E4RZw8QitfRXjJJVyut1rDEVGQpN7vgJ42H3jX8fCcUZRz6FUqx+RT1m17errImHe9tH7YPXzDJhgw9pJOgqASqPYZcm4+V3cO7b3wBoqKnnIfJWHF+4YB6lOO00B2N+fLjr/9PyHIlpEvLrhvAaK9nF2yen+HuEVsDzPcqsL/mZdHPzEZuZM9FDLaHrLDbQTFWlNrvOaJax+brDGMIXORxPofSemuj9ZGptF0Kgv4HoY0DZv4/7Jiw6CWA8H+L9XiAT0Zw/Lr3hiVxHhYhNpueFh+rgTwmaCaNELtegW19NSmCn1UGcAaiPsL3jWwRImuVWe7xjF/3MDnLc/pasNdk5uHL4taoxcfq4BsdE665skwwlvNMPeNHnrfZLMhIksCSemCxZdvx2EYN3Xg39K6R987+ZIuBinAjlQ83mROUw4mYXLaP8jz62V9mMqXnidSw3ErX6aVFW8RL94YjfJI+whvX4ZlhuUY+X0ksuD+2Chiny0BdhNEaGLUE7t66XNu85d1W8RovK+rV6+dIlGqBsurzSKmV4n9u7t6UG7LUP40jpktoy3qJi9/VaommaAosQTD3B802scdYj/Xjg8z/hMuEwsYtWiYqJbWr4S7App33XpY5iaYVYCX40mPtQpWgSZEXyV9GOWvqeksB+iiD1gVWqwmDaL6lUFLCjkYNRw6Gg0Y7K8nf0pIpZlYy+ut3M1LWgvgFG7nNMlz0reCbLLgr+x8HIeC9LEBMmfJ1J58hVGiYdO1uJ1S4YX+yS8tMauMQCj/wKaoNbxJ4yBpwFXTK/f544orZZ/dNhF2dS8knfO1op6mgADHgROHIst4UR/r7jUI/HBz8I7S/7+E66p8QybFGr+sop1SQzG2IMxlcBqAjhpYsncD9N4ldXuDv2U6KG2aNdh3buclBPe0k9AnnjsWBSTO+6cbhyEP4+/kvbdi3zZ8701vhEFvV125a1GyfuNsHlcQhrrCSFM/HLj87IXbOYAPI8EvkxdgMRcBCPhye13Pm98zsnXO03uqsnVqsOtNlZSH1HZtIa7R8ycByTmdasuT7gp6jFe3mBAI0eDbju8ekUzxYvE9eEQU61DhrT2HMeTlTJaf2HRXQI091ZIFSdFaS+IBs6St0hA+GWJtza57QGotIggAB4qwJJnYZve7y+HfQGLdYfPKAl1+VK2zKYSA7u1V/HExUapD0ksWlfeL8CQUUsn6cp5W45EekQHfuEAk0CcS2KZckvh9Ss66gUVZrEoNOWDJOduYlMavsHMAliXxcuX0j6XkG8VnkmGL6IVzu1v4/h9oC7VVMZOjeXkJWCbLqnDaEon9M/pwaZoP0Ln1c/ecswvM6x7vH1uj+ednzn60mQf4DGQjNnXYDgb6eguYL0b8jOwSO3KV2mLCVxHnPlcNQQfWmhmgW4MII31rxLeXXVFWmXOgM/W64nV9uZMJ/je7r/Y+WrsX7NPdfEkX1hUT6PFSGoQnSTlsZkE1eC1+uZGHhKiCGbzkMqi+tRZjOMifspxHRg3WOxUwqDkmekCe8gY/77PBle4XEHODJQnguEhpsUbyDVTzG4F3z6GQYN8IYJ8ucZODiERkzH0MDQMnkaKeoW5Y/tyQicBa03orUEQBx4rhlZfLIvQusG1RtyJkd1VbkvHW47UMGKo6LmBYt29QH24KdFPnUIRYVCicUrOwoOckOql/A7y9IwPORLB5Y8BmMPyQzNqQzjHu5d3wstfPG38ChRtUfs9wVfaQvNiUUTkSX5JkCBzmP4HHKuONlqBn6S82dh0Pd5aVv1FKAJgsGuzsPuk1ho4353yvEAvlWZDxJVLxxR2H9qHDymaeUla3DNKL77tSUWfdND13o2IAOh8x1fu6fDM/hRsmujFNMW28+vXKVgqUACwcdKIfLenVzXuyII3rhrbEgeA7C14fjBMkCz4v/naZMfpXOSpN9l/2T8u0+qIh6s26J/MyK7N0/ItO5h2c+eixqFIpULkoVqNcY2HWUAh1A4Kv3HCePMkAbrdFFCCEKg4SKukGqhqeURHqMnpvb3gD9jPEbjC0L7KNuYC14MvezwkdRo1FM7yy3BcQyjaFwnhOzpbj4l+ELuxPYGLioUEVhyEyi6vzGWDPawkIISPkrHGITuNHsdNcFWlZOFmio8k40CPP7VsmB/8UegqWGqPgPpZpnxMPI/h0UV3iCMYJ4BgUIKwK34FHmWRzfE/8UfBfCzcaI+ow1INjM3TzHjYbuE6KyoNsB1CEYDkr3Kko8MZIgCATIu4LITXY1c2NgaUr/xUncfeEitdRlS7mhmhKyrps+S1FUFDQg5kPn8WIx2DIO2DqFZb+oWPIKczpmGTycVFcytKA33cCIELboiqHopBhpoQIbbqMFDkkDBFRHFVRZti1m0TtIKvB8rGleJcIy0WYefwlPwf/AN/AH8x0brDcISCg2vFB/rm0o881KYCOCvaM1UYGM09GD111MUFj0m42SDHRxFJuZtYJSyEc/EZ7GwxemwQn44XWsr5afDXCk7Gk5/wYc187KraGQftADhJLcZEi/rtLchMa+dDUdSlSHyK6lIWNM2hVl5EXLB5Hf9SHGGOPeVqoJ2g0UapbkrkOzUUXqKone/lrNdWy52eMGv84hIMynEFwkPreznw8oawp+v8OhW44rcEHVx54b3LBlZEteD3pFy8wkwePoXQddLVh2GhU6im9oKN+tJa+CPRd44lucHs03WzX9ugb/7k9KCMKM6He4mw8kI0L08GWpC0GfjRDhHcwke1Bn3bBTfTix0Mt4oTKXSwpZzOaV5ogYMrRMaTHh/OGZLPBuZyrPPfE46IsuY+GxwYavWJuQyxGKKrOu2XNvwrObsE9u0tijJ83S9qebkvcHxSJcKFaCSBOPnN1uspAJDULGEHxREKcSX0i4KZKbmy+1fPLnaEKD0CX4T5y23P+Jv0s9ZeCa5+Mr4To09lZE9xfgI+yKcloIDatzJr3os4g2ZQvU6RXCyM1jcDomXf9LgQAB48AOJ8zi9me3kRofkLbvwoQTmUjCuFBf/gbQYWWYlD05+cBKNUB4SosOoVseufdrptisfaj9fnsDxJEmi+MKI9zDEJmIETiqCNMOY3KYVwEOGddQK/XU7ykRr9HsdgkN+nBnBOlcpPBlPHG7/6OudcBxERBpxslzTas/foJ0/+pGN83kAQ7lLUPFIuVX7NPrBZLLtLoZSGs8qGTF6YhIN0SICExbElemMQb5KA+UzRDsi8uZced2M4ykrsYIHfC1hlJLyLk1NkXiH5ouLBS1+sAR4sHVKb+E3qv8xFlsmaAyX3EyyOYGxqhVD1mx6hhi2rxiVobj7a3SFupeAJxvhfbSzglggOYbZ//2CBy43YfcExOfqlfgU6F+ze1LrX7v6AbQH8/ZJ4tRNM3YAz379kvq+PI6D8kJRF5aPM2QChBwTINmeMRDnYJVIRAF83G7ey7EnvovCnoXEf+66sOPmJBUPZ1XAh3jKJmBFAOxP/J9GAzRSKPHFxakoVIfZ3ms+X/4akKdhTyVKQAP8tt5BUXdgg+xWlKzQn+/tm1rDoNic2zM8niwBi1fuWS6vNgLLvHubF8X+MqDVI2AJgdZB59piG5DRNNXGB8yfmSt3L+CtRjeEmPA44jd+27HdJYOiYA2OPeYWPkjwEhQNrK8M9c2ehXK45Cq7yzAk7AuB8L1dpEga7XzSx4ueL1cIvEBbqHxkTvaeU75IADn05RygkNCH0yzgTAgtxSOdw0DNJSwfpzVAv1OLfuyUAtVRDb4VBoQGea5ICOpAtQorAaJznjxGUFRi77sgnHyQPa4kMbVR5nwZqLxrhLUYhwMfdWaAyh68wp3ZP5vxVZ4Zx8w/Yq6LAVJ2jMJnp5t4h8JbnXmw8ARbrzQIPvS+jqTAdnoxh9EM21ezWg5K+cf6h1bWNrbjvt9RzjadsBozXCxYPM0Hwxqa0A3hacagsfy4wnWinrYkQub4JDz+xELjaNuouHNNuYajpscCRs54EQdW4UF8ADu6UjSKgnEM/oEu/tZqVeGvGqlmzLNOaqFEQ0yJIHstikF2X2GrGkr+s7W3d3h2J1VhFehbvQ37bPcutYLhENndJjWpi6cv1u3p5Sr+nelxl0YLvplRJPT8UlrRbazgKMo5Vbcokn6okIPKV52QOIfnFdJ+IDREMtujXaGhraZ3slVLw7/YRWRXQrGxaERHFEa0GzttwzIz1W2iTQPwUbCnd+5MPA+M1HuEfZYj4UWz1EkaUQTtKqO2LnBHh/tSoJwfq0Stjc8VtPsNOaWMu9BSf/2DJeJp0rFbyedDTck/dz+U7iOCJAhrPdw524xPztL60Gg0DnusGms/e5JPZ2ofjmzV3avAKMBCMEeWs9SKFTmEK9mN4XW7TOpqovk2yxPAc3/7vwy+ngzp4ffODXzcOxS3elnslIaf9plZIjM4CocDl1hZ6S1frrgdrdJHl1+emhLp19SaJaeHMwlBMj+qG6VqZCTLN4NyeqTEgZSDMpQjMuso9Rilehn5IOYtt+7Cn7RGn2yQOa4U2wjeOyYxZOp0yPDxPRgtMkN5Evzqk88/kE5kdygBY3H5ljRM82M5M8zBdO97231mvpA9rtxVPHY4kOpQYBdGMKI+ll8aam/iFgOaLGcJMoc8iR7AI3CigDDdmmCR0tEoi6nh9XWl6PbWNitXZ4kt9fejnGplnq5VZk00Y4F3M4HLgK1Qg2l8Ap9SzDg4Xm4MHPPbO/tUhjaegxqPxt49viV/3E1/c+Ig523xcGy1yx64XD5drN+zbCGHJEFaYywcjxJ8nnfS4xMTfXpPTBxbvZPsu51/sZdLQ0qa57y0bK803ztaPig1rk96pFLzmRrHuDgOebJVUAn/j2sAA8WIjaOKWax8/aOa2BqVmyFPLNCH7GRbWi1EjoMl3YgjuM8dNT2FyRFTLa+oN8ZETkABBSLA4RymM1MdiOi8afyZcnBu/2kfVHB5ccSPvsVS/15uxsyNHLZPoROS8+t/Vxrp8my9ww5z7HtDnDpvGiszR4aPQzOSlV6EjjSs96f6K9jybYM/gWw4wzj13v//C/x06+ztlelczgYr5GH71f+/f0fpqrHWqDHgAZnxCmkFiS0Xi3cI+GQWTg8+Cv6j8C7ThgPJ1wBfqzhpEFUxjS+Rpya6BUn1mtnxRKM3cUpNQOPh8JUsf29Xa7yUTiAURferBlz0nN7MfCIeCQRus9TPfaVNdjrBsAmCSlulyS/JOO9vVMumZqcMBTjre9UpZjSXrd3esRn6fPzcv38qAj6q2wjTbBgH5NKDqL5ddEzKMmhGdHg+kitnwZucP0vG/tz/aTNMmUhug9DUXsA/5iyfOGdhLspK4biGmHzijFYP/bTwwl+lXz3xif4BntlueuwqyYrThUXlFB0BUHmggI/+bpR5uddJcQ5wAmmCI+F/p0RMSeLpIziZdl28O6gtYRDT8KITXBdrpKXNi67vRhAEW+Xm+xhh0yz0vapRX01KaSUpDJCsiVgatBLtfQxrNfOfrQUhsw1qhEiyaowbClXbdNDLNJvP2kPoBERntiqr6AfZKdWUIbSuAeG1vTaYLfpqrvpQAN74ii/hK8OOrXLIBuJ9mFCKcEEXzoZyq01DLkX6E4ZNyvDu1qXvH0JePfBK6+PmxjoGwbKF/OH7QjHQFG+wwWqU4vtVb3CzSrdgn5g0HZ7T4o8XFONzJKN65Zj5/Wv0XDDFZokNGrijLS1qR4yy1TrFkc4DwGYdHtaUbU6e+ZdqmNlf5JaG/ZFl9xzbk6ufs9ssF2yz+hdLoDzvwquJl+LCP1uJ2DOyMZm8RzdfxvtCz9R2ne6eiucnfBHFa5ZuzpePyWKaykSrBe0nU761btZV020wLkV/R9eLonMsDi5ErEmZIGIqtXa5M0vgQMqFzAoaTlTWPlEaRFynNAwCbkKJk8KKrAb3V5ORIHFx5cKgZBvOm2hcytpYH7TG6HpreGIlitOJps3903XwMYgAlQkVCZW6KZs9hfOgBcMtjceZ07HRd4aHB/qgI74B8PYtz7YXHL93zkPLn1cf3oX+1k8oGgU/cHRcvk5b5fjagGgc8YqW7Czmb1+RzxxE7jHHzc/CDXfM9jjbo23XD+4FN6o/x/PRa0/Kyj9L40y+Dlw9hGqta0A997r4XfnA10MqaLun5x09ODVbB5njXQCHBVcq0/7G67Yc8Bamr5PwnIf8+CEx2+ZXtY7M/SFSGxeXhYXx8d8ooVj1qarCQaIVc9QsztdQBobDQaSEMYbLe2uKt9KC9FkdBdGndxaIxKQ5NUiaSy0SaPFcZ/mjrD1qI86Z9jLOahEuBYUDQIFY9cxhKKGdM6m9d+XBYKa6wWXQBl6yDiDrGcpNQ5ubtKsR5KJOZIh1kWpdSnarrCIM56zV0p87Yorkc488ZmkWBewviv6ymToSsFtzVa721d2smnx1Z0eWhwS0RiKfB4CdujEVbhB4ZB5Ebjh/uWHjrPQOsslqZq4Xn+PfFH/bJ+urmY3US4kI74wV3kLLzY5imCSSm/STFS2ACLI6qD8ujGGXWv16GMjBsZVCYYA/tG6NOS+RlgN/8Z5kZ6bUSe+g96KNlR3HGoegp7XxHvT7pH2OgYiovAqsjkpVQoGInLYKLg1iDh6mbPkKBXd55xWnErqiX1Ddj9ZUcWfepBFxLG4ScT1HKZO8L+bffC7GQo6lHjr6jJVJ4HgYoMkkFCQ7sJJyFdLzYkiicp5OUvJNQzNj8i625UUfPjmXIPqI6Vzl/9KmY5rNvGNIo1IAIk4vKPGoeV9B4uR+9NA/lUnVtj2rT7eVZFCCXqExncToSGRFMOhCo9VWfMyUzkltkQ0lvUfPna+MRlde3KXFy/3JdcvSbK+cAqCXsgzB5rs/H63sm5Hr0TdEHwdGMrsrSUWMEMXbNpD9RdwrcmrvZ9qv1vbSqba/D2ZoEElrgb9SpBCEgkfCnsu926l+Eg+nin+8p71mPLGMk0E+jsZBOPLkSO3RXQj8WVcMLde46F0RZH84TNS43FYSM08EayYTKL9T3o+gai9JMehvChomVo3Emc0kEgLxtRAgExoSzF/GRXGlMOOhMm6ayCOzUb7JOskkCuL98w7sJ2mEXgUTYLTajZqsT9uK7a6mwPdb7Se4UdX2gWXuUPgDKjkcVcx7q5XKsu7/x/FCjgfQ23j1fbX/gicnNHdjzP+odafhSV0zi0QlCRrSCOTZVZ9ann79W4xe/jNG6L/UrBZ7aa98Z9kLMLBW71v30W/pQAa/5tHNr+FAg/palUDwaDzpn5XZ1NnxwzPJ+cyp3fN1deo/fJIPKhuJhPP3bMbOra+zxdxl+c++W7xxh8zuAT/dS7geW2AzGZcdbSfxbSK0AVl9tefig0LDnrIzB3ZuiagBVdAjfMyEl1TytINIBEbmnCTAT37WzPSSITYRk37kUd41aq+EcTpx6eGA5S2wQgxrDUnOz3RcHnGTKUb3k8UlsScb9gKAyzi8VqgYhZeVjtHvSnO3unbavYFa1AdezgCl/OKJsdqz5nSpNGp/ZKPPMZQqOIU0XI0y/aA0Zsh1j27wrTfym3DpODLcva+9nT6BQV7nUt87pnvid/5X73LUZthcHT+zv3OM425U1xddcCEqob9Ysp1gkaLoVOt9BFv54McYKGKdD3+CgartW8KMPxr19qYv86Jm8MUl0ums1cS2iyJuNdgHDkBJNq9X+s0UhJNEfH4yqdQjTcRHhS2CT3AxtoC7vJPjkSN5KADlSqWeqSnfmLFeU9CQ6BLJZhAJWf8leYcnxcbkaBSsGgoLZAoHJ/LErjX9pbzLnJWIwZmmGdbRf+7OvMRVGENoJK20npuMVvpVbqdy0ltsrjt4JCbKfcO1SgcMjiArM691A5+xxR2ylXGcMuWUWYUopziLPGHJ7Q2FfbsynhTCtOMOKK1LV9FLHeTR9YNZXRHJZiPB9gKnwMQvkM2I39lUIHdF0jnVissuztOZIj120sKp6zoK4TLvjVRv636fFy9ubke8IAeojgxoyhaOHUwmW2UhNSVpIGYp8yYMSx/Ns92FeiqmIjUBCe7p1YVSCKUisdPgmbsHDL9mJdtCo9bzspS0y7gtFoi9Zj64mwMbp08KGWMX8ZxdAwFMXJP+nKw1+sWOYTCNtvZQTDUM+w5dyEIFr+OlKu9Riv7fN8coE8v5OVhbHTz+70H7VBc5uEpFpONB2X68jZ7ErY19idTpmmgmmc92tvfE7hGtKB3ByktHO0q8wZx1Xb5tSrazKC3wIiSVUb1Nxh8O6mA+9rrJG6EW1VzMJzZy/EVN/Nn8P4/zH6tLpGNG494weCWPti0zkfRwQ3Ujj1M2UUzkOjqrxtx6zo5dt0dEpLYNYxZZsT2cb45R/JfkoZcG4hcXuohcRd8EDShqhxWbHQRZ3aVjRmjy2ijogz0W8vzjgNUU244zRkO3S70NbR+4Bi7DrxlxjebEmq6kLFlAPWPR8+iY9c5JP80faumuuTcqnS4R2g1qWhBu0SwIdEHZ5vcrtqtM07o6NBRqWrrYjswoOORteqlHkDnRo8E/S28K4R4jjfKHKruSt0IM6hKp7VO7Qe2PoeBbmHPShVsVI02SjbE2FFTXNaqdv9oc3pNMlrbguodY3GvBqTXRVCjrXb5E/j8i2oZjqyLq2oei57YRpdbTqZEZ8wLoVBRqowyn4/IF1BUbL8lqdSDUmMs2dG969/RXbbDSIoZ+qG+t7T2VjWyLJ4BDXQhUw7/U626Xqftkzf6ry6jrmVYT8XBOxWWgVyDmx8I1eATVBQL5l5OPVG7/I9oCSgRrN3U5baPoGArhCijADjyFBXh+eWyL0OlGs4wwyBN8ZYPT5Sy+3MylKc/gFUSw7iWPhpcTryeSh8EzgHr16Yqij1ZUCKzPfQX3VoXWbHd60mdz/YxrBqbLFt3wKy3iy9tNECPk0ZP9VmUNWe6ct//ni8H4f0JwERGlceBuJGV1yKSTIAV5tKuJwhS0001suDvxWmV8nrmpTZKkJJ2KPKXl75FA0+Fwst0BS5AV/M7lbSVI0bavrOYitXIVEXB2Zy3j1KvbWKMjjg6fxS6SjLB7IolcDSpcmg0vAMLwK4Jmcpubd0DCHDZ8o6PRiiSKJbfB8MclVuDqTnUXo8B35sAB3Dxssqvg+T6Jdz578repqOe/BeiXoZ35pO4oxqPQjLRlUQ+BztayOTMWM54ZX8bpqq/hJDIPYvbPwK2f//+oyGN7xRc9rENXrUZoyjwrsjPrASLWTjzMnpDn4gE8uAgvcpgRxMxRVVitMXBsk1+BGW2g3R/mrjKd1djdC55mkue8zdS3v+/bnnDDry7AUQSXj5n/bnHHCJZ4Nmhm8D0nxfB5pZLY4kEQuDYL7pHniFTu5oahc2lFcWVEjWJYjvw6Q9WIqhgm1CkCQ7bGCKouiFfQbRS4X6Qfhf5t8+rsxZi4hmQPr84jH6peIPj2zqPqSB+wHlkyGribsRZ4NWVrGN+eg5OmjYOZj3JRoo2FigNKTe7HFImW3v/bVplRT8P4VASoTs7DhuMRsdlsVZYy1dprTINhwpWzZVZeZ33zXnN8AhU28oVnwgkkR2F5Ml0jqArS33U5WmTSuvziqJ17poK3kRBGqa4KXPeW4ttFMinDjgR8iZZg5kcDZe7CLFkU1AlTQgJTSJ7Wmpiu8rFLBGLmqRMExrhjBCNByYRweeiVfDAwfUcrP7QUj/XJQ9duifLhCJp4xynLOVTl+sApo825hDj/ggDlCZHlNyS4VIAIxE+I1njslWJfbqDdpfwI99EBc7vMiEVzhewOeAULLUDXhOOJ1OHmw8Rej+PfTBQp7qx7xAb+romLdTNn8VavzSS9b+jqp7vwTi3cMbw4g8SEe19RPz6+sNAwoFTS0/E/7IZrNVEcx9A2slozFCX1nnlQVYuomYUKpIO8Pp0g/PZ7ZK7b4LCcM8ESW8W+EZYNMIcQgESGUB+pYmn0jNhKQ26+zyXQBJr2GAwvwcmGXZfVr9TuIc/x0uQBNXQWUIDsiMp31wbyCU901UfWQs9DfAReNApSw/QTj8LcNxNQIfp4WCZdEHTY9PJYPmr8sNUJpPCyPbMATZx4HzGHL1duvru8m+D7m4LVgl4Ve/XLFMHRHTpm5BSYAo6Gh8kkgIC9IfzJ46gwTdvCnyPDjxgQi8o8QPzYcWJxCBqaerC/4XLpOPSFNgBvJrgaFjaUB23FQsgF3oQWGnk7jhzhpY8KLFkfNNI9gTvhetgBn2/lk25B7wY086IlFYDMZu62GXkr15DCpuBtro4TC7liMxtfpxcvuCeDKOeWZim9bPFC/KIgKxxv+t2cg/ZeaRolJ4DlDLbl5CvXbpj12NjJOER/L7qOrgOrYqJgddpyPweapL4GAtQPnRz0nLj5uCuJ+cM6pdTWUv0RnxCKBtZhbaT8xa5+iBeCFNaS4oWiyHwmyvvRUhEjBMk4ZMzme4K8fdSf5ebwngRBnRwDG2Z6o2irGlqqEZTqjCTil9FBuZyRLaY+lSCt979cHw5gyRizW/R3w9E6DStJ9+kj0EHfNHpG1bYn0CffIEKxGQlItkelwimGSibWY8RurZUQ6aZdBLndHo7XuTZmRM4mVtdpu7ww3omvcqIBaU6RXotuZCQcwDJfa5L3IkWFGyrtuTEfyxNjZ8ljXzknm1ZDsKFJnOdPcCyMtVtymRDM6yulpoI6WUBD9OMq2Ey58rpu07R6KfOxlzFdKF7W0foNkmAg28wtx0oOUewmwi933RVuA23uhYT4NJGK/PdjMu7D7A7ptRgBlOJuhTZoKZoq8XXWk+tNHgYgQa0MgzANagUYHjSrJi1eIxI+gxeJJb6K1W60ckKAN/D0OE2pb7PTPRcF89XHNDdK5d53T+ZD+SBiGhFCifPWv5yesZBnFGMUV4RXHu6DcMrVrT4PmptJz6NTuOoILl+lQTXEW0lhLI9Hytx30/7V+6QYiBqHxa94b19/LwrxeB6gPSPA/TG2V2a7U5zlne7Z2ucXbLankCWWX/qovYWZNf7Ev4icttgpsstNa7Hj8YOoD/xd377/gELS3urZyNy2L54jEnge5GydKZDvpTNunDxDM8D34QAb9oEIzCJHHZErVZBcIURamIZTSyCG6Ik2oX9gaeyXz382G6q7qt88oHJ8YMKTtxgxZCDOsQIVVzF5l8B23R+ed5uz3H3Mcmgsm/fGMUeIykz0qAYhVKAXfxmMrB3TDmcjeoBat2b+Q7t1in5q/0T+tYuA+2JzCugzackcR6WZTOwj+T+X6oWbp3PW6a2708jnYnzth7xZ+Ev4/ivb+7amyh8teEtj4GnouZxYZqg4E5UFiZ9ld4AiwcGjhqoGtdzkFu4cbDh2oBt0wh0IpQ1yhDfhYfgJ/ALfh+8z/FyYIV7XUm1Ypq5bjmUUclbDsQc2C9XNvYm5GgZg8V2z2Rs9do4nppBZ8hK3O18bEU+lk4k4nUrRdDKdeTnrt+m1UQd9hq+KI0DD4bpdS1YiZRKZI2MpNcCQ5EXHUfBXEP8+RMMC5p3S+Ey1DCrqRaecP2EWlwheRacvzNc/Uw4N7XJ7llnSm7YUBUw2LG0Imy2FgYdqRrqPTOd8r4PcPxFmRPW5I6FeWgvjpq/DdGscj+uanqqNmUw6cToS0jlYGR5E7WHtjKxNmqRtkViviy5i0o3ZXBcDfv/D+rRruUYsKpPO4R7Jl424URyzOe91N6H6wJVLk/5ENpvwm4SKv2agMHADkatJDxWvhuspCYBH6rwLCWXbmgrfiTc65HxSlQJfipa98LtPZc/26MFOhQ1GfL3ZBnAPndjTLXNCK2EbJ/6sJjbpgoh1Y4ObbsSVu1PgrUy3Y8sMujt+RFV1yJlQJw/YUSZWZdw6l5mQ0ZtIRLKoJJVQBHHVbkHIxiqk7n3NavKm2+3h0Yhsr5Cm5kQ1kCAq15GZsZMoJ9iqMLyVpdzduBk/HI4/9U7DLILj0VhgPBpCVckobjwig5TDms3QXcq6oMu9iwiC50gc27ALcyIFjRfxRYRFUHs7ZI71MpxwbnO7PzQx7SLdlkz/TR1PDOACJ4VMBrcSxj9B9R2aSxx0NcKagszBSpdI7NYsCOBBLx+CUKljErbmSj0VstWsTeN0B3eNw5z+3JBF/n5QEgqUDX+/9HUPa15B7vywEbskfoTft/PD0g9C41T3KDXeCYbsSpLGcsu2v0MXp2blbYHReAhT5u00O1o1TS4zx0C3EuWjDEGUCEEBFQfxD1g1fhjM1uEU/p5p2HiY9jnxX0TpTdkSwsWnoTpT8gUm7PLp2gkZENDDghaOAIan45mb+p2clFl9vnDY64/kZnsWPRuulEtGSonwakx5ozNwBxpBeoQE4K+TIix71419Bg8TvbQLj8jo7crK4ywGKVTBpOxJrPWe8uF0oTWmlbclM/FtZ/gZvI/EzJ4uMfzpLIYYroDrHaMLi8BPORVzZIv37cruCxdLIzUJLsFzjCSj/gLNlJJl4UYJPCuFft0LEqDGtf1k7gUXYvkYuhCTDSDSHB4DjdiRDDzRxZR9IKsPQ8Srb3LHZMwyI5DF5CUN4UQvZZEpZePliJ0eB9H/lVn+7tRhGUrkoZsZk39Gp5U4YyhdBe7cABfDOJyPn4Ofk1U3BjoKlScaf7f8l9M8Tan9jyo5bSjK1BW9riB6ZF1B7mplHeH6/xU4jmHM1teTVqvTqSZe0mZg1m+QbZCm73K5QiE2+OnIgjIWSoVCIU0rIw15Q82otH60MHAxEAqlUADZHqpgH45fO+y3mIvE01fzBWUU8+ejhvpKrl+GcgjqlfOJAVUCkQ+uJs1yNMfVShhbzU6PN9hMPjqfI4QuctRSrRBeT1uUorYWEHeFBbWb/fFioSb1IpShzoqbBbV5tYxaZgYKHMWkNNp1GlIhZ5IK6Y+hYTtdUFHFt81chlklc1pWwp1w4WZn2xbQqqYC363UuV5/4dqJhpCrfS8sFgMYik5HbSX8dC5u6uAMLh6OC/tcCZOhwBjWVZer0bt3r9VUWK5ntOpmbJQX/CY4Hk3dPQEA2A97YN9xwc9DyrX9njIg3A5L+g/iC73xP6882fhh1TLtPYffeUj7vnT7EkEg4mPzucqubzPdlb8yXOul8rEf3taf9CXQ+ha/QxmHHl2uGkEXa+OiKlhboS7FRWYyzpgErYQ1Hm283CYYGVBVcUs+QEygFjpL49BCasdBh9BalPD5cLXJL02rBMY3Cj74688FV7ltyFSfxEZGf/gA7bH38tTqDZMnBgMN1IjzDx38QPvNn46t/RvL+Cx5+Wr1fK4ra9htnZezVknvobhyhv76Acdo0SgvBB3Ya8UIePtSDeIq+ypM1FbFErZi0g36UgZdteWyAFtZNJu5l6DfJwU8NCLUICOrBM83FsyBrgMg+52pNY85G5+j/BGBv+aUFRw+oatFBzQwg6H879cfePXr6x95+UjsAPnWvqIB8phZnumJm2NWH6UbbKvce072+ZiAm9cXSU2EmsR/+RwEemRuW4/ZdGHp3GVTQAWnTZpxVm9AYYRhwxAFiWW1ikO6BgXC18JJKeizTKyTelt44bX6G4Vn7A0+xeauAgcO7Q+kdB36L/grtn+9yf/d9OTDcn7m06+PbXwTpHBM3YkZkKkweV/q+t3b28MntvYiP9vd5ExLy9wRkGzwC/D6Xty/r+Rc9nKnAYp+QICrAc6p0pBL1JlGyaEc3IrazAJDuN1jUlWYooDBUSAAjQkdJ99c1UfDM1Y3I8oJ3SsClDVeOeOrgrcQIMHZXn4Yq20PkBkLRmoHnF864zE974DAsnIum8Guor5GrP7Ha3qlpw9K+8uxmb+EPViseLjd9a4eyMAr22LpC1eL1Sp4T4LLll1kBgQXYIp1KhDQKyFyTgQOElLSguAJbD2CiZ8mdK356UrRBzyJMresbA+ZHa4qixUPFur3ngnGfUC9SmolTOIBioymi78oZDZNESdhE3G5w0roh6CCwqSEBbKhKD82KXUZuqVGPJqmLVbagYTS6ps2FsnmgJcVuCHp6nyoGOTab8HPnUrCxoH8GRnYbxrMWA6UAaQwDCw8Bo/OeL457lY18LaOrjoHRg2bT9+Irwk9GHquBTiWcS+H2Eq+sl5K0bbDz30Gnf5mv8bei9ZnVG9OIIsp6v7xzeFmu12t/utTRF/cXbIOjd3ai9f+YUg8lp8/fb8phe5Kb052MVDE7r15pkfFGXektH01fd9dZnMysfM7gUDBXgQKE7qmevEk3krSivD0LTx+9pJL44nIXApL7H57BSdjuRnjDBtNwBJ9RxDpXtrARpHovbCfsDi2pc90VtIW2fyP8na2B8geRGrSKr2pPKwLqIHI481SzhTuiDf8/Z3lP3frKtv/fzxOzATVEcrfkSFl3i4QhtEXTZJqYgek7XfzzpxpALJa1KQY0bd7pwZCweCwH42tJhg9Mo5WrGvz5rsJ4vGisb0DHbZjaOEmhF6KkcIX1LKH1Luvk2a3F/tr48m22c1z+JL/bU08/sCS1Uz36oYSM4m4kNtJf9CiqAq0htK/RUQdxB8niXwkNSeTgUwynI88jf5mKbAy2rMeBgQnZC8Dc8c0CrO0UwHLisISm3DODrifjwsNguMLEfxI8xbUv2BTS3ONzquIo4QrbkyoIuu0Wo/6Vxcubp9dm3u8f85969tcwr28KVnr6By9RlBIJZysWTgOyTLJdqsXWUDOak7qk4bUwk6Wspt9Fm3lhdR0ZyD7xw/9VhdYOsu9krWcBWGNsBhLuNcGCTq9Xd1fYYQ4SVltJpRZd8MTEY/X+WUnJo73+BnPH+gehDVmxVsyp1rCzlBd8pN2OkUulARcmHNug5rfE0hZl6zVL1urcL1khsgPJ8Yaw+KWFjoib7Y6C0zWCJbCNQ7zwuTUiASGi1nIPodLUxbOVeVi0kqwvMKO5s+G44xvx3eskZuX7ns81fvw9MpqusaeORk629+0GFwq+uvp9e1o9SV7tXdvkja4+iUg7fbciW3HwTua0cdXyv6ZKh9mdNWKRCj9AcbU9OjyVgirzVr3vdKvRY0I1EBsnKW4rCbrtTrldSMefx5H/ozMZigVvDvG/IMiJU8JgKKVr1FY17Rda+Q8yDx2GaPV1zKrXw/kDVi7LOUZUqTSSeazyxpdxgqpxSMjZuY5h8ZYvma77ZejLmf1pWICUS6DBdC06kaoYpH1q0yX/DT/KIKg5fs36tV0aOX/9i8yF14Ez39lOEHXHxiex9trbINcZna7yUq0dJcFGok4yx0VO7ak1voz4EZHLQx5F9FBqC/8O3/dBSowGaXER3gbFbO6MoD+blUGQKucj++ab97XZ2Y7DQQGbCjRLi8VqbIyqiHMAUEUbf8y0JKydmX+tSWjbnQmbGUo3YjuT/k9nn/K6nBcpzPTsxwZpegMUpZmajb6Q0u4J4+5tTk+FibONI4ERJhdGWq42D+ROUc25bZxcnvLzvG9w/nNzCEITt2O3E8ZuxFAi+6TXGzjieNDaZeDBCPZluVE3Ns2/qi8d3Ytn1sOslJjL7xMM1B8DduIFHgLzpYhHwlbtdfS5ObDBehnm2X8l4MQceRciSAQ519ZzFuomCIRUzgg9jG8ob0h4LHE8ypOuCJxLE5dha7GlVTkMHmWNrB4vlYtJ5pTPBGedQldTL0YvKg0g/Ouan+8K+i2R9KXfqQxVGymzLRQJifeRd6KlzTnHoYXIn6CFmNIUretupCQolz/4M382RYjKzjBL/IE8aBgY9uYSNmiKG1xWoQlDFZRnzZy62omn1WTarvTWGsBpeWiSjZ+eflQ+PAg+P9eIEyvK4HNvMuT262k3XJ4OY65cFEglhke2aqLXmEQHXjVhsg61D61yTV7KprFFTPRI8Q/dQU1QH/kvY4bi+y5mJXnlFc3sD5p/1ZFRijuJwmSH0rdKoHlxxiVysrj0b9qi4W5B1TE5TkGlZDIYta6vptQxL+s9odbkuWti3uUU/vm3Xx4rjEWq65un92tKA38qJ1eSyVi/y13snv+JNJw5VD1odCl9iTctr2YNWf3B5T6NHBBu0orADTewJCV60Xqh//+qDDrSXmhDNSlk2BIo4vIw11tE+di9DMTQlrtzNKNtTuRbaq2msZtv4ZFHvInScuw4Mz28iX270/6T5hAbyC7X2qBT7H+qkwpin2rit740GmhLAzopjNf0DjrmO/wf2PxZfPPaPTaBoFCAcDgr62u8tb5f2/RATlgUKBkvTOldpe9gcOlnhSI254OWOosjLA07WcIeX405zY3FdvpQOkpUdSoaQyJdjdI79VRXraJNcqIqFR78GPLPXnoYZo5JtXIhS7XGPnnFR1oemj4xGMb4GXjxu12T250sHGG77SG7vcvyeH0rym2223G7066fr5dc7nhrOYSv/4Xb3907elJc35GZ9p0hmp3XbO0Bss7nABWflmZx83Sf7fKeoH8JnzqU09BYxRdH33Fk6jiwcv3jc7p0eLTv+lrAeBG/Zr1mnDM0r/my6//U3zAt59Vx9brpZM/3sXLfiVFhf/yial5AL4+/dWgE+XJLf8h+IPZH9G2/PDrXF39f95y2+Ryx+Xla1vG5pdXvdx793ygy7kPMD4EcMGT2vL3A4H/s57y9Wg6sOtv1WX8PUPAvjVIjvrMa6b6oH/1YEeGP99+yx29cJ4aOebJZLkCP7V4woj0eIUw3olKF+XjrBF38AfCOMQxdJndKz3jevkuXAAW4zATfEVHTVsHAaPdOL4nvwqf40n2nsrOh4baQXFCGOvTSenytQdbPfnlML6t86nyXlMcnHEEUAUOO+BUgWA648MIFi3UiHefpgv4dEc/Ni5ZIQkK9m0JS7YwsUv4lYRn/KnE2XwOFwSxcxEGBUpAVBeW1FnXTIfBortNh1kWyFn48ekPFLwvY2Cu3BCSbqNkynWHFMt2gRo2RFU/TRagHfdWzCIfTpsu9gmK2c4oqrS0cZUpNdJ1QsrmDzgfl4MWUkCBN+Mt5YQf1o4mWA75BaTT7+9t6PHdcOnVkhQKo5DCCk1mhLVQJdw9lKv5XLE+HqWnF8EF9kZqkKv1zx96iORixaG89RSzhvsjEkd6YPVrXKW3oUB1rPLow1xF6ogIPVHs68upxIJhqxPulrpH+O+Z5BbGbA+3y1fbY6ZNlrTGdvNEbnbna8x1jDGe/1xudonvNHDd4RvjG9srEsilj7K3+XKXdz5vOJUQ9mLSonbOU3QjwmdfcLk5n7RC7WRqiBl5jk/SzygxCYrq4o1qkxWe4fE1Usp/IcLT4hRQIv2GiCzROlNSsEq7qBQqMcQrVASks4ZL+BQTNr62ppuoaCHOP5cwqBBFITrAsc+DpuFrIZE4KYmXAxQ6DnsPXH2XC6l+8Qv+wa76zKsOs0Zh4HUlhm669PrgeMyFWNrqTjL2igaC9WEWq+et5VF5QOqRkTHXT1AKAOVUnzWYijLU4PPRwmiArzD57EtvDFsP7dYN046ywmqNS5XpVFN5eRe47WiFbf9nXvdfLzNVepcisedEIbWhSsei8OOM7taj5b8MYdt3NFx1vbEMzAN7PY3BQ6yZxRNZii/r9vFgkJmUl0X53hV2I7ZHNoOjhk6ETa4VmyHj2afIJTLbkewHx3orZXmVcsw5fNemb1Fiqtr5/2jZwtko4X00cm58XJZTKU8mMz4pxhSl17p3x5rddmZbs9kMr3+RWkidB08AK1Bx8FwA9a6MYvwDeMe+ld4tl5OCE1r0Z6a6NRkqZUYlB/uLWR30ruYqhPposdJgLiblQngZR9koqomUysawoWkptOBBuVzMHCvmhKYIVZXDCy4uhhmipaLcysphIbSEYwDXHP95CdvSWoZB28MYqbP4vvQoGmcNpgSxl9BMfdTLogsze/S1sFgZweptTWmqkLp1vQ6MhjsinM+zzWE4fAf9RzFX0UT5R4M51pLrVY3/jC+okTmMGDBU4+Q6oAxeljxNuLCsJJN8MVeRjCOeh+jgRCGpNdd3ddfuCKQzWP/yFukBQvG6AuwLPSypgsB8hdPdVvCV+P3FXrLNOH1XFA5H3SeQQTLg/+sAfHnm1Sc/XsOEt/feTaNn3vt3tqgpKs92X5V+rei43kpuzd6oh8a4vbppiJdY5my/zA7vcB4uQhm7gtuD2wPX5lN/xE0TbC2Zydcn5oFm1IbO2NaPq/MONWn1lArJfASWo+LQEzgSHpnuNHjGXoV9FWHSuL6J5jciBl726pQ0EuCbZoJ/IZ2yH8cFYSCs5VB0PJyRXAESWRV+IQbc41MTJTuL9J7i1uPD76IbOxIqXry6Vq+3F85haM5+mPXYP2WHJ9kXRX+fcHzA3ib6L12f5XlmiH7iBX70zME/bBu7d98NuLEKVDnjreEjXiHdO+7x6YzBUdmr5CKV3EoqYWrL+fM56bwKcZzfjskg4TIq02I9tSiqLAKusdcdI7UmYP+joYwFVeBctk3FhdbA3zID5G1hY8OOQAzYWYYyNZPFnZLjdua5CyJhFzpIm9/T4SPHg/FXMSwDXBlY0DFiOvBwSTB4VwP2NTW4OVRqhSSY5YCR5wkJbEiJ67gr+6zNs5ozh5I3Ys26JFS0Z7XJxs0qxwCKevuDCchHAStMgBrPxYk+5hc7688PrHepczO5PMyHseK6oGkPhI421scJfWgPQAd+MA/URpgXZ2QfLvO8tVZjbOBjHa3feB7PO9Fu5Ud9Zb19yd+vLLW/qBw1pagh86H6OrUVo7+1sOUj5IxGyTKqtZkkBquMGfFqboVCctvdgITvaURvSamvmmfZZB8P8858+yq9yUnlKV3KBRIyEATF7mxLaJfxrLgdO9TZ5c8XBSGwg2M6Q8xYaKISmJR2NZTuoUerbmoXmcmGV38nNLe/Z1SWdmPhIE3paaUuE8iHG9vHv4u0wnBcMAPaNMaLmJoRhxzb2t04jgmkAhyLFIm3YPdR1cralvxt1oWXHCtJtfBYuwt6RKDj89nBwOg7V31zOfxB3asfOr10l/VKPuGxRDh13yP/Pev3Zv2hguy+4Ch5+oYo/kNX8kl2l1qNUAtPM3t1CTwVLq8ZrTvMjqxNLk3uOfeJiMAcTEeVEr8b/bW0223E1V/5JeEdLYOB/EeSr2fCOVl1u3viJNoFjj5cF0ILMHs09GY2iSluq6r5jwXpPamO9KcHUnPFhGE5MwB/o/FKUZjAtXHkQf32LQ8c3mcimOaVfRP3mGP+hldxpinwI8iZ+Hk/wS970xzjY5lMbyQh6ry3o4gO/OxWdwXOhqGOFafxQthktC7n66JQ5VHiAC+wm887fFzJ8zTYVF47EvVGd2KzWNQKvYSwxE07rJheGGzb52yhPh42u1+XIYgIvxiPxmLDsa6UdiywrLGsb+ROApBJX19mqSi3c0FnT4eXA2at0DRZXsdC8VXCoPI34LnFLN+hcsRJKTTvNsg72NmNLY3OTDGFDPjYyhiRUS1CXNWcSW8gxFG7uRdMOUi5y2z5FZyVqSOsZOU4qTF68yMj4dYba+G41FtIZ9Y3D1No777OCb4Y7K5H0SFl3+vzA3o2zucvYuhHKuMdEMHgYJJq+IOn156pKs3gUFjsxEgFeNOW24KIRROfq8PZqCp3XqKJjsZkxbfQsQ5DJXs033Jo1hL2WeiBfDDESw+HAcMxeHK1b17+JkU1yqN89r5TuGCXb6zjLwU4Fja3mQ9FQb42Vq/YuoU4Ntcmc1gmOY5wPgtsXYr264QHtiXIw+hWKDc3eMGOKfw1SjT1valgI+3eG0y3+TWy/nMMFPd92I6a+3s3dmBn/5BLxIJr/3p9Ua71AJ3yk858Y3vvIWbPuHLxfHSpvcrNG3omF+/Dmn1F1jyiK0qIKYLmF6pYheioAevMMcejJZS1KYEoqPBVe/BUZBpBlWjKT70xT848O4rjhAvk2aVCyrtTgjKIeST5rQNIqxIlY+dzo1OGsffZETmTIEVUIB2zJIACFnxA4ATuVmCHJW8QNrkLjOOYUYZvh/bKGdmnn/TtT4j7ERJ18s75jY0v1b0v52pkPRGZTdsFDpWU5sx+tDlYXQyy2ht8flxBRG2QhRl85Yip+I0iapKdpKbSCd0yTeIeCX81wsbDzp+50Fjh3zhx01l68mwA5uHOM4OqW3vLfpwdhFPLg2xDQu3/1UdIVdJtnXLt1Q7OjPNzNzthSJuGKJbtPRyP67Tu/AYfq3YB0z55pfUmk/fMTIdeWxnfQ5P/kEY3kIwpWbhT7JU+mzaXv3X5txfPNBKOnOvsf6v/vnUMzzOzFD42N6K6c363qHQd4eGwFFnuSsaSEY2pxVMF3lFdk04+vJxqSGKeU7vmKE7Usx4KocYmpAgtdPKda9HPmQkwbWwuf9PbLGv7hkxIp+qDFcVyV3VZR2vo4pGRwJa6aGZz829OTwIiX7INfDn721/D3CFC9MBNUz2gKWBiZ1O51ucM2LcKjauDpOP64GQ62zZXVUEfPkl+YV8Jv1iVluQunLrA8/xZwqQLOx39ultOP7ur62jYqJdAT3d3XGyd9puDhMGmuay+Co9q6mpt1BEiCpqopGTFETtsMT40grdc9qVrBbqqjtC2/8Hi65e/88f/8CH84dUfdweHQV2kplEgBeO3Ti+z5ZG/+N05u9sA1w3Fl+WH04/cBbbgPaTnIsCAJmRn4pBncvXlm+u7U/9unToEEgntruoAScG52UJ/6yLthrgJxBhOMjOWissgAqQ+8uJ4QQhi2ENkiaCOFeyZW18NJCRB4BX604b72koA7UA88BdA2xZ273zheOvW3/pFiZguPQ7D37Dwr1DACKBkI/uK76GB8pu/+sW3N50H8ljom7H1CsU3zBxX90fPFf8UbI+z3rIX2tarGkN1lFPDGgAZblgQAIAEIKsceVyGO2D29xQQVrGkCL7zccQJCEjRjK0oSEF1LZMsVzz+56539c5FZoXN23zjjfi3YoD3Ig/d83RCsJMtgggOkiNqI6ozKEjO6uaulcjVgstykP7xBPLDx2v7bt0sviOCVC08xDRdZtDGtCdwu1dl7pd3MFdPP1PbfqEy/jIL7/dFdmNka4V8xcboRCiT2Hx8UttCw4bZOyMcvfq2ZW+Le2BnB5HAAQKI1uid4OM6xmAkshJEuPk78ADalsfSKhDC5qh52B2qBnIWMJEXqBHeiNr8k7RGBr4VGptDnHI5bn/fPwME3NWyl1czdTzUvXbJ1ZQ41/hdf/bg7k9MsT9+FZiDxIzkFgdWNJX2q7tTrVt7h57vO07KiubIQT0XWEEow2hlTtoSomXe3DRDbkGihAzIEaBfLxc9GOgQx0MjosbvT+O1a82maqIHak858zesKZVgPQJ+iQEui7x/a7U7Efx4M+OvH+I10/7DDoX+uzY46b/4xEspJOBggyB3uWY1qNaM5UVa7/gCS26cd9z+xXNyF0pKrfjKYCLMQGWaOOfaLL8VL6oSbDFmmpWg4JASHZmkNTrZlGqVssmKAviciuQK/vRcFkkupRbE8F8UgY+B+YRMZHf36ixIsVXEunUMovDqn1ZVLoXSQxmPBp0/gRZxB09VVttS++0VOPAxysKUqL5mtWE5mRkEigzSwIYQFZs/Y06DX4rvUuoIEJT2OlxMV6uNig+pFJphfpDQbF0mzugIxLrMYaTKqnxJsqXrdV6F751yjXry0FQRbmBUjnL2rgwNlZOaLBBu0ak7nF/IxUsP84xCDz7Ay3BNMg0rWJy6gCZpWnGJqW3pOOs7tj1Ll5nuMSX1JOjnuJl+oVb7Fh2b35vcWidImYehjmnlipmYypN7cShQ0zvtjrVzMtQLC2An0ig4FfAyApcxhiydqmynnXgbmwl7iU+aPU/LVirVUlyiYCQkYsw/kFMdq5tpZWMHtZVA5c8NCy0h+PnMdlQ0z0i6RT5QxajS60/ZzoLZaYubf/V+rzOYabAd5mqGyWZiKy6DAMiMSGtCGAoGTipt5+M03Op36OArFFU2zBduyowwP0PMjGb0gAhBnnLiNlTjDsyDruSo77+x5rz3PedkgTidcTddeIk6NMfnGSlG8JYITXFxgtDdjwaLd8s/Gls4gLRLk4bA04DcD2S46HljAwKLUeDfD4kDG0ey5FnB2pvxDsWWrZtcHmqAKDY0fzxIp685FHFMnQdWqbZ+Z+SJrhsT9qDZAf2Me+sY/d8ZBlihWgNENzSjaxh/wqMm8PhKyvMo5PVWi07dEs40x3kYffS419w4mZjTs+Ce3x697/e5rx9dBQSUa58150J1ToPBt5Zljks17XmoPdRjJeQ5/xjOpmrYo+dxUvfKu/pEbF0lbuZ6pFcQFj8rq0dlmPI8Qj0iqtZA6o7aPZTyE3Wz3s9VrXcMXgIVSOQh0MdOoA+LZsXhFeeyv1UDEOuxVuwLEWmo7au5bw9d2zn93dhzHehFWogAJPVK7kGJlI9KsqtyJaX+QDRCQ2ZcPEJU6LmIdPuXiAqlOpmHB4R+mrujWiX3mQjNNBJV10jlbZrjhxyeHHHCEYRrMw+jD/yHdutA4u524e0quu1u2Od+5vIjOE2IaziZ/2Q3InoPMjXkG1a3n7n0poh8mPShBUsIWRuhrVC++e3KcKtha2N0eGDb0Q77oswlBuicEb4x+dG+owd82r892j+AapYdea588QixwYbO5U4dcWx9PISAu0t/pAc01x08l+Zvn4ZqQeczdI1tI9ZmXHyF4Q8AKQmh0kwXh25otI24+FZ2SNzj7wwtXZy5VQLne9MNCZPZdl4q32YjgbZnh+HF6n/Jd5p76KHgYtfIzjZEQLpOCHIzX3l3FaV30sXKOmZQhEGsEhO/yjRaDousS0gEocXStmaxrCNW82tII21z8SfFxLPyB9kyRwNKG4xXKzoywO2DeEguUDGLhwaUlE2qaFcHHPKOtq9UcUTRWXYueqtzocAOnUZRyso9n9vKVldaKYINJG0zG1Pkd/PW6h4W/AX6JidH09led9Ve6+yLLxp5Bbesz4meilnlvggP3pNDUyR7lnbLo+NckBKU3mpB7UYuF98uqpr3fZc/MGjU1Jps2T/xkw8MRNuYt4q6yIEHBHPg67SAB/CEN27GvTQHDw86YfwtYiGAJiicGsKe5sSGLaAOORm+6Srvo+CtRUBCAplkwzDZsSQF+3aQJbbH3WcaWqGLES31VBNW49fqEjJ8xQYVzxCRxwXdaebDQkSlUwHPpT4eoYg6Z5VL3mkPX+aaPhFtzcCRnqhl1TjcQUQ86EH7HTQvRPgW0pfFCvfm2QV5HAAAMvliJWGg451XXQCBL6Nj48vmCVODKIGmIJdYgDm/G4Zm1ihLDcdWuAcq0nOpwzZT1CWkUDEeL0Swm0UgJSsmnK1F7WDLZEbmXQlW8gMtkHFsF18Y09TLlv55Dv6ZLhUhfF+Ava7JQlil7PB6kTa1bqvCD1f88p9fBB8pqKEdu7F+88JV/NYXDOBTBVsjqnybxHwpo0UJyn9iPckSIwJgxM/+nLXA9ExBCkz0xJufbRTLUhClNDyhwns5luWhN0JAB3dSLYd/fPHpm5Xf4Hh/6uPRDxvg7CuggUAEegWU6Od7vOWF2Acp6lRgpNd3WCB8BW64iC4G/JO/uPocBHd3vBtx2XiH3R6NiYCnvQhzPkdvlRCsPp2GHiUCPUF8xFfRrkGSBCyRxsM9YWCIf4mntAWiDZkDz2Z8+9/oIPoy/WWqMqyIgubjIGAweN7ffw8RoUjv55yaXvT3EYg2rfwuTwZQ+XP4zUn1xse9BwlF2sUpMbn335+3IEQgFNciyzhK8c8UO/+9tfio/vnky77zACUKJ6x7dQOD3B+f4s8a2/hEDwNF+Q17v6LXDYGYAjyOgycr986cvNAFXNSd2u1G9PoJAAIq5Cp0HHkEv5uWhpml9EkSUYMvKuWfxehU9AE13gPkKGqVFHRRYGkkjvNJjfJzleG8+ibWUxDZSlYwFCP0zFriGTupGa1mr6J8yZe43vIam+A+qGiRbWqHLortNQK/8NAnXp8eK87xlC06fJCEFF43nEUtJM+B1vqljrz32PieOtuN1eqKeI79S/Z+23I//HnWTg2210X/upgJRPzAyG4/RtPseTbEKu9NU3C1K9Tf8idJzeolIHYZu/0GUStt5NjClZyxGInFU6yboCKWHuS9Bdc2iFZthDPs6Onj7cGzsJGZx/Pg1QtKVTeCh3zRaR7SAlTUr68NiCnwmJTHV/+/v//qF86KCf/ZtHm+d/upO2/7dOyJu5UVGWxDVWelUiagwdu8jJDnVkIxMshlu9GbTLiVNwIebIEurMXXYWMdQbJdNlQLaGuH40UuQtmz8zbSj9PuUVt8Zo1Fr+ym3O+fyzA3SuCDnWRPWU8JVR97VxjDfbbS1bR0p6Fqn15YfKEUiBE2L6/erD7hO9SlkvZGaspUceblLsqIh7Dyh6kVdXdqU9RMRkRe1d9ew/HPDfD1BO0ig0MIhe6cZBftbefzaRnT1YVXeduOtbYMwpLD3mDeMYiJCmVTS5l/erHm1CykW+FFvOGGEOXzm7o7u4hrmfOzAynX1k48P7RQm/R0shVF8lAzSvDIdtAE1RenLtyN92pWFTynvIOnCwybasiuKec9LmaTdtdhNZTSV/KLPzKi9wr0nVxEYPF852IqFSttbm35bUHMLCBUhKpNUy8SKNNZnNFjog+MkIZPH6hrIdG3ojkVKzAD5zrIAsFhCOM4a8Auf+6M7jwdv5W1etdMXu/Xv7ssyPOeqP85x2fo/3fdg8Eb8w8+i85o4ns+f3jlo6lvg1w9d40cPC5LmYfvrucbbU8NdBiT/TNZo8Zv2LVBBuuDL4ADllppI9C5EpOZydHbI7d10dzY9knZ6uhr72R9cLbV4Uy+EicSxG1963II4YibxpipV+t0Jhv3V4qhs/rX7eRzHNxbcVQ/HY9gBu9i4ZPanpHAB29rX+uTtTpWBqy+9rU/VLnB29rVAom/cJElpDaockAVoGAWjIm5ibT/VRcv3ScXZ1M3k/8eIa18Fv/2P9sNxeOmKWFKu2+uFt/rVhQW6O2oDXAKdnmIz3i/oJ9UgMnGn+cyc96QiiHvYJM1iUSCUPA9dNJ++dpmcxc3DAGixMff7fjXjSLP0kyAU9JjXkYFfJQoE+tfMurPqUCAGX9mRSy4LpPZ/xp948H3ZUttjMnhclM/6BQUaAGhWo28HoLfLwEz55NrP3Zr3xE0L3cesHXeltW+O1FUTRfx6BorwTv09CpPolyheFChR4jYYfXKD2V112kt6qdoRMzXyWB60QyTTHRVFMRaVzXVqblRGcUJzTxMSxU5xJCy4EWUrIwli+PETLY1NK9tGdzs/hu2QbpU3sMZbnZvo7V7Wpsb+CPOmDGmlXc2rnazgFvxRLXsF/mqnKSkmbTeQi9eZS3B3cMVlsqPzWJ3eyfP/szZ+K6S3c6n8T+GH9RAB/L5w7y/j3r/hPY2/e1Xi/I7pr9fU/+hs1Y0Mthda4KsruO+u/lNp/1+GVbksdzCFdJl/Oz+wGLotzpG1S289dfRvez0nXj20I0oBIEk29IoLw6G6mWVcMu1Fsw0I3NBI3s2KhIQoD2EMUvGqVDJgJsGyWkxBt+8T/TL/kLkZcucxGwXKfodNUGB9SGlktt4+IRXGKK7bM3/8IaGaLadTliZ0N/UrMU/K+DPTUhEcK6Awq8n5MBekACZFryGEIhDIi7iQgbeUFpWbLR6Ay3q6YZb2rkZ3cJbRH8tuP9qXTMslsnAyYyHrZ2k96SkhqzxOl3m8sYsinMWqgWUVn8qUaMWWQUz3tBR+9yNjVvi/oNaxNJybejQBL5jxLaP5Bsy/7ejWfkRk6/fY/e0+khumfqzkzQsP1Jy8z9sPKx+lNmc6Bv1uhZz7qn6lAtL1rfETLgQqQvdFpSqd7kwGSrpQq/bPCntS1+KylNGA3OcjbzJYlJUZEbF3T8kNVQhM8xM5//USfHLkCGwH/Kx+IKX9UNvO3ozu5nnyQvfXRaTGeQxmx2UdrPMk5abbTi2SErqn3d8Ib9z425rRNnedJItxt3qFpHDq4toyGh1Rj+G0IMslNM4HzViqYdTAWmxauW3ICYcE+Oe80P7wmdO49SbCKs7RPwpbLl71VTNawqt7HXJ84qk7ZyPdPT9bIHuXoadAM+c/swqo0uGeySvdIjHQyMMr0eVrY3X6Fb5T53TH/Hju+LtKMbjVo04VD6wzo4qPfjbsS4GqvChcx+iov3+/QGKGaEf/w8iNhw4vr229l047sQF+Q07UyrNO/apONz0CmpRFXXDbIyXqL6OSKgChCdIp8S0lW43S7ZKuM5xIruXgYP4aW44DK5MJEaulPd8maTrTVyOLrYGN8ysPkM7Vn9VUgWKUFAl6nKK4ozv9Wnj+P01/Pk/7H91zHmHp9T22aX5H3QBH6PE8QpQZEBkkUGyHvsNJKXTv3w4cDbQf+ZZlvaGhzM7fysJ3Kdr008Bn+Uc4Iq+3SReL/HlW8eaptTRjbx+CHc/Xtqez2WCfLwNxs4UJ6bobKrAGMR1Ntpb1NSMEmp+nHRhLErj6E2oNpkm+Y/xHWWXTseFgnK/O0vj4HmR176eke5Z0oXYDdoIRKfbGU/2eBPFlNqOFnlWY3RC95wV9rMzrp+3g5CJg47iI2SnXtQeTthb3FzXtZLZ5qrCQtTOTly4plWJnsL2KaRIzYTHo9ndmdFD1p2ce4EFQsQUs8u23vKZToAnFGmliU4cWnpiqZwYNOSLVzyLb35w8H8P7rt2alUZQo0S1R/zb+GgANYyx1FCCNpmbLHy56nfD7IFwo6DCn71p2MlCq88o9h/g6Bwtc4qXT2Ebt2dJ3WRrXudSOQ+d3c9MC1WF6rm+NE5TkDFaipaTwW4+IrW+W6c/SjMT7xuaJ7YMMYxwfKhH3fH5fFoB9Kk4mSjEQA2mkER+rWm6nnTEafseil/NRWuzhDIa0aD+8DXU7VpRb7em86tbUDJRpK3GV1Me8ZIHY3iYqyjMUrlyH7FImiCD17QZ8PBiV5xKKZQzBrkLALTgadpEeKTmisp40IQZUWeNIpGEQI5nKhmlVry9K4y7CkDuY5xb0tZKlI+sx9zXR0RPau4GhUkoTV+RkAFV8PqVneR72eU5oqQmXC0uR0JLEkt5iHI7YW9QJoeHO1VeMlXFcm5qzwRn/jIPYccxG56gI+RAYoPnU7o0zwOI3qgmNq59KVL56ZjULUU+IY/OnTGhvzsHXtZChAvY9Ckq4cwr/3kOmpbGEtM+hlpmILToppYD0SslWhB72kBrwun4wgS7yUdC8ynXl5yAWum8DEy79j74TM9+Ny1kjx27OrpXL3YaRWCKm5GgKFoBH0zWxgOFJY6xUbsMND8fhk6plfGIaupRfd9gVCxqrqMHBHpYDDLONVMvBW+xHUg3ZzWxY5tN5uJHrQs4sL0i9rAzwTxYQmw0LjM1aCAJtZMzkfpmaoeapgi3qrywtbscF3L+d1dQsQ0XrZPn6uAE1+SUu9KDKcSbm0jma0pKi4YkmIU9Qx4GNFYM02Qnyy2F9ASOv/WHpJ+XQ1CP2mSzBaTPnBIDvlLjPaekVdawu6iJyDpJJ9g1o54Gp3NRs+QziprWUrBTqo0EYGBTLlgBkWrZvLwlaAS1/sd0W/Rx2qpaCqejm0T1mvjkuRgzwwvGj0Uz2zOAYWInZ0rEvZ8LxXLv1iuqR1VnIfQmjPHXYvTHyJ9xUMTF5u77P+0JfWgWoynVmQJy0W/rBk8hJcWYaLrJ4uATUyYE1m6uk5GNH/O6gOUdyj5Mmjn+1NkzmsHRju8bJZyhetaLhaLx4PzXsxmuiqiEsL8Mc1jQdRw6it+dufpzSKFymHeUCm15vVCLrG1k2OxtPA0Ggf7FhQ0tesh3tJvXXczRM/Km5xSt+CBlGXbN0IpVYRFf3BmA0HK9AoX4Fxo/7BgqsUieVoyPv+G8pS6LNE91E+Z9lTC6qQE4Mzcc+f6FUDNA5ueuv5Ll6pp2yigXhjk4Mf5I/I3MQo8U/nCE/ks7lRzFM/NKXciw9l84QsuFnhTHXzdxZ1QDdGB1jp4gHTlnYX/6HrdiPG284C6JYBg0RYuc1zez/pqr2paezW/FjLaBtXyHf4/8m8L3z7aSYUaHS/kuHmlJijj4Vz2fjY9q+ypzGW4i4Ai0K8sscCSGbTjX1PvkmVMFqFv7aqvqa6YI8mtgDG8OztD36yVN0Sac3vzPXiZ76E+hgpiUo/Q8TrrBYKfTazRnIkSZ36rpqNsolYz25w3q8VWWWEPPErqWy3SFIrQZhoLdCyQZ/u6jGUuPlI65UJYYKqV7yyOSzK8I+egHEzWCBLb7bbcE5laL3WyYliOgan0iddzgXkIUtIwDlkyXxw0bSbrrdAd7+tM4NH0fUZpq3olcWck4Wv8szbDwDxEJ6pSwBBfNPeoWtjiWKErBCDthADOxSm5GMv7/Yeetugo38uTRFcpSWGR2C5qXd9i29jx6npvHIoBHvv3DCHoewUfgnV24vstHXh/m8bE2vRn5PeyFYqT7LJpeIQKXHfTUv7hZ3e/R+//W72ScrFd4+aUq1M1skY1eXZFXtKwI91Y+8oZF1PLipm5pjJnYHsFfLDOVtRSUwfetEQuGXITCRy3CJPhvk6Ua25kFlJPy7Yv3f6XQP5k1F+E4Dgl2Dw7mUbam0ps/StIIAyuekKHRkiYm8SnBSpfKSCAxju7KdI+oW6QLxEAZYNlxnpcDRGkAmqog0d24oyN7bQ5Kmk0I5ow6NlXx7A5YGAWvbDIyQtTUL13akCvRxVcE2aePZBhjzDrDzUoaERxTgV9GfBQ5yh7nce4R/4divrbhk+pmvcJfQ/ka5Lf02HZRmsu4vR0q/uRrDfDNSEkVAJDCmf0xGP5Kw2q6B7rsaAFLRiGJj6E1wraOr69gQ42LR0fVv1lM/sNYVPU8bl/Qu+y/X66QfxhcABpjsA9ZEXjeOzATfmImXrRfgr+c8cz7Z/Npl595/ItinKSRm4yfxjIIT3puCc+f9m8u5QVXZMaV5DtKnONVS02WpAXHyUeDrjhz9v1iq7h1LUdOKaU6Yv5bx+wNn3ZkieVDK2j5ys9OqlbYKa3CE8kniF+lu2unrwTjJEZL3z6NOIThntoUNUnbZZGR8rzTwSeAc6MQf0rPzu4hyz06q6COtQh02qdTIie0lPmFPmk4s9n72loGYNxon3in7aE2z9bTaXBcJk+pCw6n1+1pT0fXFaWE6m6+Z1v7DD/3RJVOdnSzOkMUsdWKTh74nlwDnDi6ahShl80rednpFuZ2bK7PNfS7e/KRJgPTLYmcXPUiZTAVOfNoohmT7ZObrOZtLyHqKHkwJDwDMUUmwlyrLfXFNAyekh5Hi7wJc4qm7XQg61Z7ICYCXBjmQwKG5/nIhHaGCOSCoWfJPkGswPAmYx76IZ4v0xZ77as/AUcKCDfTTAgXQ3MapIlNV/8fU2L4+JnPsa1fzLyc3nUU/Utfzci/5RrIQfAzsjMZPOy3uWKy2jSzQTdS7bRHKP8sj9Mr6AGpuQYyzZaC2JnO6oXH1soAQbzC5hQPaFcjyg6/3/z2GcHatPMwycW4MKJMZGL17asQNDpIVxGu5BtCtJCO/h8O4tKRtznqEr228tIS/kkZz6VtEmpjC+89jgvEx6wt4xZ7RzRQAetdeZPQsIBkXHRbaxInOePJwqZO2383mbs+jS/p6/Ktu22haXz7D1DtRa0T8VdvK+ykvwRyzroXqS9ZrGPLJ5S/2xqhPPgAiBogirgJKs2tLSlKGxOZoQ7qntAqvwwOutpQa8ZKMuSqXReZK87MJa116kSzhoGQLMALFI+Q6MiS11l+6OfISrDO0EbVkIOKvgQ3roFde0v1Nc3p+YuUZdzxiRzZhQo+ETmHH8+7y83LE9i89KW67UXPd31LbO3ZZ+QTt3VreiFRDM9euPvOhiAG8f7Jo+fO4Rf2huAHmSnUpOP3J0a+c/mm/0nAGZ2KAco6bSF3hPOs0PXJ6NyV7ylLA7ZOi6vkXytvBYHv8pXOb09GV49dRkU+PmmZlwnwj1K2gtldThSfR6y4GoyFfjiJV3nHK/rvHfugUWiMe/yzcSmZ260JKslzwXEQYLPESHjrACEidSKhygpoBtrwD4lxKr9UMRAuD+2gG4pP44oUldZIeM4fb6gAwvKehzQwYcqjEPieOMKvMr8m/5i8EYnogMYW2vr3/rTxm7XH48iTxCz6L8C24iy1zRvvJ5hKc+DQ7Bi5sHpedxIkehZHqi8EEJU6yVNKys4lsciDs3A98Pmj46DtCAvvn6ZO1iX5/WXacZolG0c/7OTltnlMfih7APbPVd+xn6vb2+HBP+Ehka0sO71V7DA3a1tM8TxsgHDiFR8pm5b0N5p3rfqG+vurdo2DiT0BlEQCVaHL5aWAwzNRBjELqYNtmRIvOwINIwdqq2FHoqseaKoqIbkJS4kQuSA9P0Z7HJ88CVnFrCHneIGWA1X4Jfhl1a1pg2j/8z6nF7EDDJbNBobwyiWRCzeBq1uMvP0IJupcr0axN94lyeTW+P0w30WRs/eUc3uJQOAD2mxW2+LRwy86GwipR9K38LDzzpWMJ/a0/m3Ie2D3OszdA+j7tJTf8HP32vphUZM2JOOWceffGE9WLXQ7DcvU4JoVnYG9OiIiqUxlVDEKJFb98+4ouJsa7iiYzG7QT6hSjsaLg7Bl1/mldrUxp3AwZrq4qHRVuKsdyJxvYk/556CTunqqLBXEnLStgt9hrXD03RpLhk/opnml9W60Nd5FMd3Llg0io0NgKS988JFF9qZd2vZ7F2Mmpb0Hb72Si6djMvec3MhKwDCqUrT4r24SknvzSzg68xcDdQuZjIhuBzgDX6jcVwjz7FpD+qwobMqOVfZj0QXG8KYZhuj039R8VuMf2Wgrjx+KO0H6kJWOPbaPsJJ13pvbdn3X+CCxyBzZx1mIkwB1uGYUUsLTaFouYv/CpOsHU163g00p0VzN7dNPutUp/idPFOHS5BGSI5kdrMG2BUo+U2qeXuqcevGTCV9JLBYy9Rm0BoMMCCu2HkYp6vR5d/z/1/+zpcvvr5sq7PQcgp6Gthtbjz52YPoeUHC9fvOHyahMhY3R+BjSjL++SnVjkky/1FiQXtaksBu3NK8N2xQRGxu6cJCiT8lRhSmQAn6m2+HgcD++ey3Ks6aVVHSXi2ocOFX5Dlw6OHmj/kvaY3v6gAszf9/Gfxaw/16SMozp/g3kZM33qGEkf7SROkbjf+4GxD5aSC26poml8eLsCGoX7HYL9vxm6f428HdyEER2qTDmTo/3JINaGYbhbkPX4yZCPI1Eysuj3RnwK6KECEffr1FgRVbpwIr3Q5wViRuBw4qAUeNgJAqbNeLx9Doq3H9xIZtxuuV3pypQnyG/gBLfFa55S9aOTjQN2qKRqqmXSPYjx7dr+10GsooJyqzlY8fPX7yBASr19KXVlr5iwqlrQ3lanUL9Ycgn5F7xAP7iaOLugGKnxVssLsa2J+KIpB3qJPiCVVoOVXe5QOaKBCNpN1MQLB0AYvlbyeGWOvMswlPrqTqI0ig0XdE0n+g0EJ22sNW60QxPlSliiEKTcO2YvOQCNKOJ/vKkrtaH9AEQip6a2mor02qxTaGXC952yeuwUvAmzQ2lnyKPdw/pLrx6CaLz9XnsQOdguLcbwcMA5rhaMLlJcyYkpW0Eka0AzyiZY75AkwG/CEtnVLUYOBL/0zKnu2ieASe2rhrL0isFH8Kk9MvK2uYpDWzLB1LZDYS4Bds1Bose/Q1DqGXNbOFCRgypCEnZZkNCIgYt6VLFclG79hHZNH7zyU5SweNUMg3WH9vp6aDz6mEWXXkUEThJ6EtuUyb85+6u2F7nA6i64tFWPwurKsOxBq43jAjgMapAZdGcL8TNq8TfzFZ0GC76AG0S5kJzxyn9FhBMXidqKr3NuS6zOhT5iY80gdyrFBeEDjSUzR3M1Lhq68hXZ/Nky7s69W6jDc9Y1XV/2wmHVjc3Sho15Azy8ENmdpFwMlUtXr8oQry0AHlatklEc/48Pv1lzQ9oAEBE9MWOuk78OLEz6UOf8JVyPdEXEZCTnp8WXpz2wEh33CGCG5Ifh20D/y4MxrDgFeOYOPpo6g4jf/FH9/eI20a+D6u8trrwyJPWNUuy5cilfoqf7Ounz0nmkwmEnX9y6fwzhxk8579bWS9rwZO90COvYUwrSnAOpxGjbBykCbKtPguTFJEynaUjaFTZjWtDitENN2v46pQ0nZJ6soLLizPDDnofcDsZanvBB8GSRqcJg8MBDBAAQbY46h0FuUzlVPSC9TCH/6p8Cdr9VuQ78aGAV9etqe8fPupBdB4u0Va8+96Q3X5EzIRf5ArhJeynVo3Q7ytTfBWNng5S4NicQK07T7AXhg+goHbQqMDiFwmcNHA6QGn36TU1/nm52gwmcjMJVb09X/mWi4bN0rP3ZsLw96+uO+Byuq7e/dg9clqQJvNN1vdZi6QD67HTt2zM3N/IXYtZuCB9Zaosy3XcHhO+nrfOe1+8K00f6Hu8519IVYqLCVe33nnqzSmggMGzub3wQaqMA1ZKOMlvOIpLpav58fPLNGkUR8PFvQvI4+0hPa2S0y6KdrvD/akU7CjbQv+wbjSx4Ge1fz0Tl2hmhbGhBKzUXx2zXvX1qdTD8zu5oryreeXbIbR8uQ1YsDXT8uPSZPdHe8dCPZrkpUZvC9H0U+Xn7peespqdlfDZ8+7MBouZmH4KTDexdHszYoe/1G37ZGIT2Um/FKvV4gjGUBSJ56F3e+nV/k4tD3144ZgjEShphRoaZTnHJgOTnYYULcjFXarrKGicVZwPMTtLtgHwO2ZFKeVWs6FDKzmQ4r6P04OwYlsEjuBc843Szyb4YBmUDaWMTESq/vpaANyTbn/D8uaE9tkp8Otz66b9noxfeLmMZxFrBe9Kjeb6vjEl02ONPUlL4Xujn0rOniTSTev48aZGcOINaFXFd9cHZ/26a6+uJZE3bcLMcl/3D1ttm1X/X7o0xxO2J4tRdIyV7Mpb1UPHWRzRLP5IxvTD7MPtX4MDUdZgZEUx8UdtS7TDfrmFjd66NqJLkzoLzODxdWnDmjAgrGjbRqxoXsy2r/O3P5tttFihDVLgTO3mYifTpxmnmmWDOOOpUDTIZgDJie+OcFnwh0K/9lamfOcyIiPlksOe0mGm7M6ZWIPSo3eEMitOqAVdb39n6env/UfV+N8NMleorh5eGJcy88mKy9i0h5NT5carKllJZRPKqQk9ploZna9RwKGQCuHbLMKhimTgGtAG1RSVZieU2ziDIJYGpepyvDY+KDVy/pjclhQFL0bbPcyLbFVOODjHH9UMuCwgyY3X6WOkggzoosAk4YsuMpEgncLWUfqmJ6UQNulCWOyMIetTBDnaXlpJYP1iPC53s+y65BHVxWtBYTYUdx2jshvDw+Tqi1cTHre8nBFCfnzOBglzX1TcWd5ITMzATepDYqckAJMyCtFTQ66N+Rxj+0sVEFFBGufvFeRpGsIkKcOFoCAJF6Gy6lbRPv9d0XNZfxOEWqrP8ZUS7iwGN+Y8VtV+LoivMiuENkxn9AQphTMsn+YGQotczcDJu24eHf2n75DfrNQt9rq2LHnm8ip2D+KSOE6RSHEoMJOqNAa9SXLz27EjQafuHZRCyo8k38XpupOdiv0ZRYGbsANHyo9Pvtg4CJ6PGyH2tLsl6Hbo7ZlU1jgLGCZMjuuc+h+GJMhT+n6oaPikJXYHx6nImlORSyCfiSchLpH5496F0k81WUzVCUggSqhD3AlCjXJxkn9Ehu/RjfW9mgbQBC/WPAGVovObi6s3cc7wHT2rKGPKMJjQ0Xpw5uR5UU857Td0cN+wgyIs7M025+xoaOyRozoOdFUyL+hl6r4TPhTk+XPXCcz7AeimeCGWTDjRDxHyUqt39bOBnxFLpiGtUz+I/gDXrxbyVxMvNr270+TMbV3D0Vat4DLgI6ZJvraylCznM/FVIXBGY5B6UiULqQddh7Cpg+gzu675Y7/08tt2LWsFU0LDA7Vue9GkpZ1n/uZoSrgTHqnKc7xA2AKLJtpcvwGY7EzlM6zlwhFqpRoxbltKnlCCy+q45zIwXt2UPvQzOVC12KpXOFaAQv2i+EU14s3ysp2aUC8snD4kaFFFw4X2wKgfsRlSKWByYtl0MmZqGna6MxAUHqI5fT6mHqgVcVhPAsMIYRoxloijYkp3Ao0rJiI86fOsV+8jS80ug3H2UGmRGx90fZkV3EirQ2N1tMuAZUXsiwXLCoE8V6x1LvElEibzr/aXwumYzJ/hf/309Irb+8RzvN0eO1qAPvn6yHibUhafBFOPgEgdwjHUTXfXPC7UedcVC1oqLXCKewdVDuwBcXocw7k4erD6ZnZWXVDD2kQLGnOwjooQA6v47VK3yHf3Z91va7FHtgB2y7gr3vqsrc/n3N61rf3eZ22de9rzveTeb126bh9MNa3Rarh/qyNVE++K4RpLXduHidQHjXd1gxc36C13eQvZQCNtxUg1DaY1jv/oMtVQM03xCALRtQQhf+3gZ72aMrv9G3pD8+FrHV7OjNIiJeCaN8aL3iUS+TyeIs8dNETsaiLm1ispoB1bpYvXeRR5Do5S6Dn66GX83rUGCnMBYLPZhWthq5f/QDJejoah8v42pgJWiuGssDPyA8z+8wjs5sOOkSM92jHER7rb96Pwm4Az++jG4pxPMg8RkX5rUjE8PCgI1wgKs6e0qqrrVdnvQh43r+rG/vJxJxUhCD0gxOoLueUV1ZvuYW4nFohC40Hlbb13H6sqs0xrxEAH18haI1oYIniAiox5owqQR6qtGz0IKgSpymxhDPDHu6ot1Z8dEAfKrUcQgw5f/vywku4o3f+5975a/jx1Ou1u2AEx/eNj8UcC32seNp/yTuyK42ALbjhhD5o3JHu+TDh/g+ebMYhN7UbH5J81NPlK3YFo/HwZfJK/hIvLhCzk2BDQUVAZYeXUtGcKcyL+KaJrm+mMucPnCPYSSpkvUB/fqNBYxOOZ/8hDBAiNivkMdGQtRoBsUlQjuE2+CFcBPvwPfiemta1RVTpNTYYgbnDIh5y9YdYPGV7s7KOiCPMyWdfronoXHp9lPxnLOnExcSJjeXO73Fxv2V97xrKwWJlsOU2SgZjyeqShiTqU0afR13nLSvKefWm+flqQBKJWsJ7a27C/O1Wxd3j1qS6uTh+NVCK8y7V+BCz5WGU9dfYLDZubWQqBoCgmefSpE0F596soOq29/J4KOmtJBLVVbFMeJoMiiLx8saNqUTLCBgsSVJqhyUWtTSUB7N98CaRLotcoWMDlErgAXU92KGyP5rWT8+jTlWbEV8PMjP3wbfrTScBJqlg+fAX6ymCXLpsyP0CrxiL98eDkixnIckzUWQ+TerPds6ltHHoRzdtpjjXW7j5Ry3EeYt2ar28EWJNcYb7OdOgt5MswzN7zhLHn9mk64okWU9q5JZDbj+fP5PDf77f8oQUkDd1G66MHip10IhspO079YbOgf/IIzlk3crox4j8mO+5kkOoD8Yl1xV6fZlEP9hZv0kYiBlZKTL6qU0+Iy2tLlRFW0+WY9BxUkiCraNbT4aw2BMDNQQMCsB3ghxE8I7/Dnv5RUyu+4Yls6ZjiIFl9uwgYYlTNo49/s6JciCa2VmTWemg7SIuKnfDVxLSQugOHkXoL6cXvfcQ1PF4bU0nZIlfLO52DaTOxCvt+3Vr73qCnvL22NTukc/0x4agDJp5zI/Ah9q1OCTCIVOQAAVCEEnUE8QBYN98bfVHdl2Lv1XYDCq2XW/p7/ZU6X32XYvuCJs+buM1OexyZ7DWZVevJGD+rDM+Ke5esbxjTHlnebYbVLuvRQNhdlJsv94iUCKx/3rzwWZanGh6ABm1ohavEuoEHriSrnOFWtL1m7+bP/z6r/qOXP/S61O31o7eaot0IiFZU7sem45/9t3lL3pcPB6ZLvRDJuR56TWnYiVjnovlNjw1SzheD9ZfehiWovOxG2mkKZahHDCB8q95M7Goi0Ms/kRZXg4LsieGrObg6FhVysHAOhMF6lANcDKJhUbztBQ1j6f8Q5Wm/y0tNQP8UFgYbpp7A36EWL9UytEgEGcPPp2nn+wX+KHnnQ2FUvsGQCe1J5/GKPsHzmFA/k0E2QyfonEzAS2pvnsLiDTGgA0GA0zx+e83aISsgGGvUBTyr0xesrS1+hX8JhIOrASjO2vY6xdT3g/GVoqNMP7dBNmVnHrK6gNKH4ibWy9aCf22Ym4HbfrBZpfC/oK6LJ8wTB6YQUvp0KyZLYViQASQe+EIj6zT6pMYc5GbhUE3WF4hjOYXVdiZpoYfUt7NVddvzPcAHtzugOc6gGO+omUBpEhmry24HQE1FmIbwy9BosNNnGk3wNN7EQ/waYaKBB5axgYVADXcA2vhAvx8/MKy0q8dvkMoqOkuEllIpDxvq+PoOvnTdPUi9g7h6ikkENApbnfSnZrfcck1IhsBuCJmdRGP3drjfYm1pycogC3OBlAbsoLKjXOjCULHjCsa87HXp934/P4oSIa+IM5aq7G4HuV4UQ8IU1XRCEHi3E4tQ4wpUTpDyBWqSfYpxsbRORcIcZLjvB6cLb6qJcWhiP6f8dXwbT3yj2R5bGLDHEQ62+yjLC0UDCLpU78D7WxZwajzynl3+WrL9l1ZH+KurdP8oV02tZ96qpeqyOc2guac6BdVJhxPNy53xB4PEbBeh/iD2wq1Ingy95Q2i3Sm1gOGU8q1M2AO9gmNAQFTbVBqyS8W9Y2Pvk4PhUsRE7xy6UuIi9VEcaHdBooiubSxp53wctkrgfGVPQ2OMyi8V5j9TiQ6EYsZkV+UYBcDMG4K9Bqbat2HEFi//V2phlUf8rOc2beWoYD0koYQr/uD/WEISLHu5+Ej6jgykf5uaB7dP0Lw4UUFBm8rJmcYNWOxhO9x0DeXfKWwbN0ZtsF4q1sEpFNGNaDR5TgFmbnzqDbElBaJI9l9ur10y/KN572nydmW7a3YWqlWB/Niz9FnbXrUQQmdbNJBCrPJxrAFXhD1FfGLTR7iD/hoG1U8J3VHj52jdxkVCkxi0TPJTjHweuhvw4kjR6XMAvkKxeeukvBGicUzW0CIEkUHaOjscrihoMDoNWKrne/w+qHx69VB5T43P3gruFCn5dSbI0G6xzswohesr+5Pq1Eyjpa2xB5FyU1E8UPYaX5NyDY9m2IerV5UvTJrpYHcQsMSbSDNkKh065k+Tb/2LVIUtEBOfpwLxw2hozSXXgo1iNH3VNjcoZLOjzGobWUoA8amXr/IQyTIuvTDB7vz682hZE8pcyZd40VigWbXduzS2u2TRn9xWHw3lsfcoUeIX2aJAH2y5Jwte+QCeQZIyQnl5KoAEwE9LsxBHtbia/AVX5jOVd8SKns8zwXz7nBP570pz6GlPUU1trddLPbEd6lnCIQuSjZCXhz9JJ30jGdfwluQ2+1cK9jj+Va8gb8u0Lrl0/LncUxUc2pY2xrvIUI0wyGyLHbHlcfjJyECrGHlt2j/qvEZf7xJOJxp7P1ai6VpLAeLIb6/buXL/3VKMNxtl0q/fPZJy7pjnact8YXS2I5iGfjerQ/YxrL7nDQeO8G1IADCtsqAOvus1cC3AJUqVk4rtJHDgzjSq2hnuZzQWLM00Q8aafBdiTFXLE9xUwUr4VJMC9XHVawlnPgSAozmIWvASfBoAznbQyOx87pRKGqQ3AUotpoTbruaZPljFXsgn6TwbzGRWIIdPMnuCJp2fHR03G+32axfNolFCN88BLrBuql9rvFlf4kvoH8stR1QaLmMknXgvPN5y1J/VUkXi5mQ6Hqw6IffsgGyimWQT1J4brFer1W/bBYJeZa58Wf/ohagEslf7mwstrLGvcByBuvmnbzRZf+gL7zwfPrT92fyhUI+a4Hjzz9nVFejUOySRMJ805pkn2cHJGYl5CgHe51xUW5KfEmtZhDTOrETMLSN9xdAJb1dnB60ZZkP0LAXcnfq6mTNODdLNV7X18GdRzTnELne19TxwGXIKo5EZtbHrZOB1UUX3FlkHBhKwEZT2mANVwr2G++AQ6a126vzUeV2X8PWPTcYZWHX9fl1JLpvgJGtisD6dI6JV+aC4Trd3doRAH1SIyXamsIGmf3n/wWlxvkfYaydP/7NjQqstv72ulMXb39n2WVpEwHmXoSez0BcbjQSBD4tG7gDLYmUzsK9MkNni7hfcXNfs2vCBB7XrCRr18axQdqEwBg8yAEHnDgHZytOTrTILXGo9fidaX3hdhsfGFo4tMZ1Cwh5iKkinhJFbGs9wXa1fmu6259ofiR+QHvTfFMmMyXh66fPXVQK6lSLvPnJfCtq33dr32FhK+1vC1JIkVjaepxWtmYE6ArLXt8zH4xkRhwqmsWtx2ltixpNpgaUgm5LQ5tbJyMW760gu1tFpM3QnZATWmG5660PvhF5dwR8iK81L2EonNY+KhfCkM2FVA5ReoybSRXJpXY2LxdzOT8uhOkWGhif7wx/WQ9Ou3Kp12D6SOUqpCj+XdTbo+AXEgboyzbV56933V/wtg75L9O3hV/zydqKzJjRXNagnHV1hTayuUQ8pv8rO5HXzhTspo7fzzLY7eu6LrYG08BZ+UJ2ZbfMGsdK+V+04U855JP7MtPz/eWeoTPyR94R7iCeKh3pHd5IEShK2LyZadHibB/Zits2CJ9KjzMS3ExxI2Ba7rxcB3zDgFYzLX1dgBY9f1TZusQF4g0kRbV2ONomJXy0C9emG/RCWMgjQQBPl0xVlox9XcotRD+En+80sqdQtFeU7LdLdXibDgVOVFqeDowaimrWnIoYmL5b7j0VZQ11dB0fkm4LdQPaHWhAo/I+r/QmVrkJ5nSaDLIQxhW4UIFkKTWOC8sH8qQGW6gmo7lR/IF8p5WqSZNv0ZuWn2SW/PYOrg+KU2ni+/eFg00/3xszHcXQc0OO51IHynzt8l/cuCOpLk6phixQO6ZoiWKmMCuXL5Yr/FIxn7My9bBaKW+lEh81ZeVLtbql6kvdkqRUqssONXQ15FiGIwY/osp9xxxsq1PsHVo/9UCST6TvgXRS+vZVc/QayjrOaSK0ypepYckrLpFJHRpScSFWLWWEvJIYNzfcY7s359+eCSd8RVg3mc4IigCsZXWtnt7yr5HoGv0RXa4nSDfaKo0Lx2mCrfhouxQwyJrbhwhxs+2eASGc0TAoyqJQcl3pkr4TRR7ItN1x/15zSnAZXJhenGUilrsLBqPDIgBUwGH9YxQkKU1aAZIjs4f0CI5P+qHIYTtW94YPekyCqoSwDE0arAILqsfTqAKvEHrYvyavQ2hp81RUUj9T3MwziOepMgSdxAP0eOn35a93lk946beNFVVqphucIDl+MLHWNcMx5DppkgyzvqPTQPz82tUpClPA4Xe2cuIVv4n/+AWzOpqpopdp78ILnhxVFxp3GlPMbCor2r5rwX2FwnqP899arFJ1VXWwWtJv87r+Ddei+fN19V2sY2rK4/TbbN5boJqtV5RnjjaGLA8wbr+t/SDH80tlzSbBNjSYMIbKvtu+hxk2e68JOq9nLe3ZVQXpGZ4nEIRq3mV72PyssTb4PFSUmOAMKouyvic6u5fPl04dz//ezn1lGIVCxrRqfOX8UbvQ3EuEbmZct9B0mZe6ZwxFy03i/3W5ZAD5F2m8qWM+gMsd0lh/odKlpnqdM2gd47qeeMbUAc0XCWtBw7+TFDjmxKMhc2H/+zXG1C0PgeuaiOGEfV7IK83j37z2xCXoHjvw/iZ45VbZy1zaE3/JqiHijbX73qUXfpUC0rCGR9Mr5smp20qN1UOK9e7AxdHh3+7HQJ01hqToV55ElQgOGDJXn1Db9RxB2KgCZFPBwIz5/bqCOjWdBqmZv990YRImIdrqqEOajuHLb5b26YWNrRscuRh0Uts0f1VE+/3W+8Ous1OjzgsXfB1pQs1r/Ryvv3TplBQq14HHLKSc8XMXWweKfK3eobCX6tPUUZ6ksHUC5Z1w6tk2QR+TwYthWi+hhR4ISHnb1+5gu+a0OZ9zG0JeMS63HJbgZaez75DfFrdOftyIdpAUegneC2dg2mG9LLbgyHIbnkZYG+528BDFMoo+SOcVcJ3pd55mz4k/Yw+ARJlwZRu7Y9AKspxQZmG0Qeu3jML241kyEU0xRKesEnQBKxH4Dm8D+rLeIitOyQUmFBM1P7G519a5iCqzX0S/tJ5PQdYOaX7TFxjYCTvXyjuuw9fXZP4/0TUhcZG8k+Jwv9XykU7045dmd4PJYqVURoTVjF51xngEaBfe56Cgr8cXIF9wW5H50GltVPe3cImCXNBk8sgi94Yeu31HuYZP6Jc3XKqneBLi9ru1jvoE44pEWubooKHyHKeMRVJzOITHlG4IEBzU08fxvdql2L0mE9JpeHmWT3nVU0rXe6kpK1MveYt5f2WE8wxPHNve3C5oYcLm4Mz2Yp33Edo2FviFP8HJFUfbs+dgruWCVhz2/f5oZjwS2M8ZsQU07/OJSAjbQrHMguv4uRzYBYaGeArMQPYV45fs74jSgcwZlpEovmrPz8X5As5gfdN5LBKZUZTLNIO/nuib/Q7pwzHG/3hU/HiH8rz3mJ01fVlkiHUSCkCrpSxL/FEoMQiYI+vgBaWVPpcN+D0PCNAAJ0YQkbdV10ve3CDhfdSN62uPjCCT72S8HPaiIu5K4cimiQ8b98EvQ2rQke+RljCOJAbw8YSgJgpZ9BpxGV1L2A3zMAJX4tfiV9a1HpZkK1d4B94HrzNZ+qSN57t4cbFN57mXvcUnzb41IOiedRfpbstsIrAft7A9F08N09jbOzADF/ak4AUNBYdFT6aRhE3taDRAKMvBL6vIVhpv7uY00k2IdqKaey/RWai1stAs0efRLs3gWLzEEx58AAKCp54ck17A+K6el67kTvOeqoPdmMC+SQ6syHu6ei0ov1SVB3pBWV9xxpOvgyZ4lCx4iDaJKeClONELvaJLnOPRfTIWt5iyBa3ekCJkxYQohIQmcK7l5QyObUqUzmNCV9N92cOh9UShULuE814s2+VYbruQTs+emW18GLIXnttMl8tLBVzqaZfsYpl1kLeGCcgAlPZ43q9LPnwn/XcM4KZJzdGPcNEr32LhkVMnAiw8MjI3rE7i94LsMYL0aLdCnwfwrMRFOefI+iwFBC5IF60/UbgbKIu8UYUNiZfD7BKSRVRvzpcsmEafDalgFrX5YKOAL8GKWLl2fWtd8NT2pDLgdiB5dJI1Mx8ukeSuCaoYQsjJIb0ShjK6OjyUFPI12wF5q4WzuIJodSfVTmXWxPCA1SQv5ZnBi0G/7GVhOlGvhcKCj8SownDiCamGeJCSVyt4SddW/CugpfdgdSVPyNij11Z3dWPUbmKcgquZiXqF+HjWbMpKdcvKv5gnS4etBeVck19RD+InOaZdlyk6binMC+JOX6/3Jem8BYIjwK1ml7ikZy8Smif3rq/zffWQqy1LKprMl67prN++WHYIWh4+6bU2IuvkLRUIoal29d8eWQiulfKFhWJNGWhGWA2Ibcx1M6UNyophp9mWgfJi8qAohaCyYDSYqfKlaMaL4QE5OWDsRthF20XMiD0gSHHvyR5pRFq8pQrTrflMhrhNjbNYlhBjxyqI1nq7z8pl2YCeamOnwcUvJz+G9i/Qe/5jX4Xcqo2GDa7RDflshjvLQs8ZEvT4MS4nIooyWAyMt3qidKsTIigrXpR///7veW+8XfGVa1+4HWoq28J7O2jaCuPuHRyxoH/pNmqTQjIUlE0jIitaXXnB9ZOPvntbGLennO/TxHgYcuO6oxKB15A1syyzkQon1ZjyMklrFdXGUGvpUA8qe1sblFveP61NBa0JZ1hB5/ToqaAi7SvBrwp8QPZ+oWBVjPujVxQiiq1aVa01rK5V/d7H8Y1bXtKn4KQCpiTvTOcprMD4m2IUr4MrVbDHygPvRgTPcMax7KDzJSl/JsNUhohgpWIMkhNSnB18Lnyc9HsbdFseXFFgUGqrsuNnbX6RD1P4scGJLZyAC71cmsk6vInVBqR6vRuOLhAILi9118KHMG+Bd0dk7N+04/lv+uYmnjsl7fCUkP6yfNRVrvaeeQSjoGfuqbO3gV7/1PzjT9Fq7P7pDeWy4rwFl5xnUfswmChkD190bIQETV0VuZvxlkDJBPahoGGp76Qa2PMpuBcWuN9zwozkig2OKeOBwlK8p+AJAaMOOJ8q/YJwSCs5FYWH6bBnllg8M3q1At9ZTsHbMkRAyi3gvy1X50+SDJ9+cU1QKBpAGDQb4Ct3ARe3mWgfAfu9YTAlkrAIEiXiiGSfcnw0W46flkjSOYYNeWql8hAxTEe8XZjwVPZ0h3ZWagwPA+X2akRv1pUTXGUzwksPqCcLK+GpxzB+MgoE31kZKRAGyaOsgxqCUhQfvA+FvhymCAzeC3BtcudH2SduEqZSGfLSmWG+Lgo4wg3O15cyAky75Vh3T2FGoGbR4N2fv2j4+nGgrl57HebjJOm1wMAMHBhFrr+BiydzpgBHYsjGa6ZitvHHnXGjFQi3yc27kVyQZD1OocorHEqDvegtTUUuweYV8fyIkxBuZLHeEC24KnUrjBN7cMaTrLBxIZDAIYFQF86Naf0428sFqAn0QlWB3IjYuFtEuBbUyrlLQa7V2errbke4UtEHNH5C5nOEVV9Q9svRJY/9kqoiCIZGYFar6yKHy/OzhpfpSZB3+lv9J9qHZJLXyrTbp1GJBncQfjazAA0Q2TbDtXEISIGzRywj/PFa3NFu1r0CkuTpaVV34z3rsj1MKee8Cow3KdBIyDTHXkSiVxgb8j0//8F634CR9NYigc6UQOOkwq8rInzPlzjHga2AhvSGgjSSEew/sJ9Xd2NKEvm8FoLQYzQUd2FLh7FfKMNhN2wr6wIdS57TdIGRcbjiKuv7vpc1Wx2VpRn51Zh5CxUf4nXTbzUjWGpHtv99K9fNv0bWo1pqHHSm/xHiZYsr2mCW7CQUwdnxauHLgn/b4iAUlKEuvg3fpENJxPnmNxBRCE6StRrvjbvQ3Y6h64aCzlXuxVYPbR3P/XUNjEB68zOjWL+p+4ubHcyllK9icjKtm/jUVOamF+9oi9OSaUp0jWVJajotUrhoUrp8Ltvep4G22YLlq9arF/iGhWe540nA04sY3VwqVW8WKrsIEhmbnZDHDvOnTuCTR14+S+Vd+vsfxeQmnh0fS41hSoM/A8bNOnnSMqnRTE7j2Yn68/SCigTEb3sXaA8UanGmNm+RZUTC32kFkG2QJ5NBdatvstfcgf8MBwqIGb4ocpTFdkv30a1OA0LITQIeXW/RbUU+OmDRaIOt/wuOCq2jLzu0zWKUc9xjWuRb9WDtDiwbfSG0MvRmcVNC+ohTYoUdDRsl7qL1g5MBlgqMjT3MzjN8KwJkcB5sbHUHUa1VVuMv9Q/2NVez2QswceG5p7PxWJGpXOGFu4U8lOYy0OFvJaNeoQwGH/N4p9K/8m6PKwGvoyD1cV5mnEajG+XSQcORTquLnf3zwSHKSpZ6gzp2i6efwDDMuarnNtzkO/1Sqac85uz3AtdxviiKodBuBfauWjX1sNYZ8C6SbTO78fAP837c/Cqpm0N21FZby4dnPpMFUbjcEB20SHw+j3qHCF9SIf/cT79YL+dy1RpNwWgfQzK5ecsQYg+yX+t6VRPF0pO/yedxuThZwkOFiXC6wcC6qYFYUnihOCryCF+CgD0bGhA7yPmaRtGNzZcZuzCfF9qsdIMyZDRYvKcefsxWUDGAVt+IQsCi2ixifWU4QikBNML0NMyousg2dm/r+WnlzMw+D3SFT/UcIDuj6vMqaGIjULGQosGtExLpAtEAxAmBxfre/1Kuupt/veVZj64t9b7IGNHEPy7RnG26WaI95nuGJYn9jOuhmQT4UskogirIDNGu3w2iOCJkUiNT8/s07aPATgYPNKYe4OwiqLCxb8pv0KLx5ynknfoIiAwYKJ3uCwE1yuYMuRwVjokHRDZzV8QIFAJAvRb2/imOrG/s/RdyjL8A/enMMffLSYZBQTXcofDv/AzkPnlKPCx5ebMfV3zP3fzYbJ0bvPFjOCDBfSt9CWZpi/PGfCn9O06y8WX34GlI8eX4QFvgWUPTSgLtwIj7aso0BmnXG62GRBfYzRGD9HS/OJffL0Srgc8dLg7YPTuo4sNxD/5Vd/wT65GuUeBmMCifCKYenX0K0euhWPw6YmhXoO2+6gT8SPBWHT60IVD2wzzdvmvYE16CKx9xI2TURbjgfTuF7yDyfVADQc3h1pKRYBwEQjCgzogaAwpmARoYlnPY0DMIhHDrFNmQ0nOIrgcrf/ZI1Dd80JhCA5h8sIH2yPyDqY+dQrz+Exuocw8gP/igdPn+yDH/KRgIRlKq+uAr3pGX+6P3L5o8bf9CeRzYFTg41cNpAh9z7agTxRn23iYwDiIjHPAe4SoYps2B/Ys4VZ2ajkEq1CEHVbyOWzWSxYnW5eGpIhcGxCJ5srUP9WfTYfvrsz1M4s18sXZrbGhtSAWbC2g+HnpF7X1PYYwNHT10Qc12/iV9ZNjc7phMi8ZcGhe+AJOMSmDe74u3t2jHl6YUhJIoWwh5v0cocJZPXtDhe1+vbM4sutET8xboF6A+vBkFdfrRQlSGD5KzNgDoEB3IfISw9K4tWE7IOHtfBu79YvLH5qxuAxh66q2zyAVIJiDnY9XoDtbnCZLC/oBEfizpXdXZAproF5iOUYOgjJTrF5yTet8r4X+I4Aiy2mhSLuurYO7qqxpyVV8J7Kb+WGzUn1XsWfv0pR3TV+Wc0lfAHNILWaNfmFminwWiGIYcnqA0KDr4CYhJ/+rhcWsUzSwhyxk9V8rIY0BTssli86avD3XDNJrdizdI7sNKEZNX6xZoTjjR+W85PSZmV6kmlDDbx0mKnG/hoWk+bRf7zQ+2NuamGytcfSbcw+2qsoGmp41lkiaFGhBimrhv1ocMKy8s0zRdvz/sHiyttNCYsCMgDMnx8H4X1kxuoSWkW/hvn485Y6b1uL7IpUHoYfiDS47beQGpCWw+suub7Ik5ZyU5CJaW2PSpJb8QUhUZqcTt+P5hbGn1DWJ7bAY5zWnyLdJYyMZ3ybLw+vExw4H306IsXvcDXcRDgWGmCHXSss6sxhw7r4jnEBaJeRgkPPMPvlFbb6cNSgLDjvFTc7HCud9s/y53euAzDtIgSLd4vHrAZGzLJ1bXRamB1je66ulFbL6jwbv4Tk01HczDggCHoAB3GtWBVw2bf8E+4pkfvK+CieVTxsKehzeoIBxNwOXPkn+PuFDcm8XqT9szmW9SWC44Gg8OxcEP7mv9z9fg/7XXIu969Pmq7/terd2hgyTkHq5yzSL60gV4Nj8+TZpQCJvZkxvzImdMfhX3suhoUJ25aZ1zqWsaML1VaOjnOQstI0EREXcDenClPSPyJxX34ueRMM85J4khKPHNCbc0bBvqclE+GCHGn2afmQvwZ0HzpzsYwGrd+xaR8ylxU2rCb+1L2LSwyzR+lY45LQZh/pmc20inK1L4HA2LmUMX2cSKpeAF0AdZVrd7aMAdnxKhX+AAi9QJ3QAeuZ4WGXNniCLULwGoM+lA5W1OEyGA1xwmTklI4rER0Vuc+66gNithz4ifpK70Lk3sI377OfiOA4YZuaxAMaQotldfW4sdMTtbQqYEcTnFSUb8CPPGGvhKgOKDQb203PEusscDKCr62jbQdGIpR9rsYTB2c9M6F1UmBuy6UCDeb4qKJpGs4ahOaNSGkh0lKSibkHXcpW2ympJJ4AS9NCqHoJnlgR+yo/kmFpcSlicAfu5gQxXT0bz7CZhLH2/NFFykk+Y9I9mgQEDohKFrwqEzSYgx+AWPxX2yg1s5ogKSjZKhpEun/VwuWsg+KBRqN/ZUcmsoHNGjIkEKpnnUTaKp1sJ0MOwX77U2Row75DgspCzhAEO8dt1VAN1nD/SU8ykZK5CX1rSYurwiwFIK3NQG8Ai2LmG21RmbakoVnoZyu38RNn9/rCXiE3cOuJaAYR7Tr3aiOm3o6XWnHrUVA3KPjSkkYOTx1UDXY+7UjbmhPKVWMM9DyLFBogYl7thgkCOWbunseaGivpA6iEoBo3ksX+vSF0lr3X6JgKGyPP472Y/ZbzwvcquPtC2zkDPqk62NtzjAu+bPHVd9ejwhUcuHc3RpuxBQqI57mAPsew88YtmiT9fuKZ9feaF3d2dtGrus1olLa54UunaD+LQpOIJN1ykOV4G0KzteSigHxqb/OLkQhS4GdkFUvMuaMbN//owqDB3wJ0O7R39TgXtnctde3mhVCkVn5Spg2ZqiXmqV+06Wr118k73o6qka+tDOlOZ5uPtzQBK19ir1+FXq0bzNeaF+8bW5Gw3nE5d9864GP7YaolvuLfK6iOe099AAkEaDKnmFz9ddGYY7JsjSm/+ANV0EqcuJGrYz6FANIabHRPAM9oarUD2eal/guhnPLflzouoVBNaHZzdKZYLQxRDns05bFQ2wpf7gO3Art95WlHAkkYdp6Wt3kUgyj1NVFPP11NUOHtsxVZrd6Shp3F6I5cKFoXwlfAqscJvww33eu/Z5P9OXLYapoXABgmRZUWTRitIw76EovhAKEfYWv0U2HqedHvjW2QNAJdGjWlPNVs9OMz+qCySdfCgEaPZJOZOLncqscNZ71r1KIJZLk7C0umCSoS5mkrrrbEj4TyYa8iQqhL6oK1AHnlnJjBI3fCuMA9cH90OCU3hCXB4Jk+17dh87NO/Y56P9//mwtF5ezCHlUgFKlEq6ziMQilWQWgnI7e2CTwA9dJYuLe0kS1KucVMLCkfDO+msf0qqBro+6P9OZhD5mB81u/XCocl83mQSG2owWkiFBuA1BdXz9j37ybkxihdpJV/AleSvOjmW31dx4NKD/989tRCNpsu1jQLOiIbRTKMug+VigXIWJKQG9CdgW4ewxZxe5hTLj40+/TvQz4e4tAvuB0mLMxQ9D6ZjunmS8CGOAYYTBtGZBUl7XYpExoNBnhHeg9APJCkpSnI2Fv7uCfAUAwVWTpGtzU7JzTg5+zRJo1tVrqfy5391WOvkZMaqJOj01YvkZDWXKel2ttk8wPW4WaDiYhD26Eoi4wsbhuY2wocY3OLC2JbITBwIi5Iq29c5+U168T1PxcFquX9onc7JUl7g7xnVaRoCRJBN+zfodqBwGfjPUfIvAY/ma/3ZPugAGLgFZDrmFeU7MGA9JKJOSCwgQFV0zkeqlZP77k38Mn9teGn8sVtC2RELoTSCI0bN8tKekdAhtbpgj3OuhCsq7sCs1fzVVZS3mqqV72UFSZbochbygVNU0fS1b6yZzR0miJsUPdcc015pMRRZJKt6hVoYUhu21BpGXWR8fSae5A3DktFZ18lT7TKbmZosSVvgW3J1BMtQspll00v5mvNIV+e3kzvlcmprhnBuvVLF8zvLSeHMSgYQRhT8pliu7FZSFVwJNTFav5LT07ED/NwHzxOhW6QJOm1HGFp86dPhT9gBN3+sgYK7BCYjTe68aNaDyyBWZ8485JiBk8rOa4FmjOJdGhR8AgjCSZzNsylmbgiqgv2BvM3S8ovij7iJ8xr554sv2kxjMzjshBaswmfw1Twp5ne57PIUZ1Uef9kx7nEFf2Zm7PjIMge9ros1tlNW1IbWq/q1Q+DQCSPhX9dPCpVmmtnyRAZZYeJ8ILu+1sPFB9Cl85ucNSMXoqxud3Dr+5uFxM5OKEZepoSTLU5ygliZLPdtksOXsoppoWPeu1+Y1TDip384QbZlgSyQXHOfXNZnMZ0gkqfN6plVucUDY3Mr4uSDZIW9cSEqvDJzTC8gw6NiK+C12AqJDrCYM4FDxG9CMjqptlg/o9OXSFGPAHQo00KOFi1SJ8Sx5t68M5J9OKi0fNyzg2EMLrg2oX6GHaNBilxYNjTZUEeeCk1mXdXMw/bmMbM3k+5ZDMiitEfedu4SfTOLL7LJZ1jTHzQ0aR8VefQLBfNpZkYMJMQXjblXXpGapua6pNV2fopZJyFY91ONDZr5leB/g1GIboSJ5Hni77I6dDEZDZybY0M7secnXv37K2gLzjZofMJsfrUGhoCPkWxDHNHbPOnyl6yc4aR5oMS+hUud8wHCBd5NOz/d9A79MV+bpquZ2pqgUCLGumSeEByGaW5ra+vllurNenZVHJ5eqQJlyT3sX48JsOc6+6AGrgI3YW52V5ko7Y322pY2CB9DD8ZZs9rggrH3wz2l9WkfFsIE/8rdmRj5XlM0r2yNbnHNgNdeWD3LakyZE7CYw2lTfYVrykxSrMXPurU+D5At7/K7Nv5OiRzEzFG4BuHwoDWv9LvOBpc5PQ8SxzklsaxCVg6nTgbOgfM8QVbMunV7KchzpFW9Nl8l1mvXWqL4h0A54HDsCBxaFSsrs7I8DhBupgmMxSl/YUKHNC+CLoGBsEvSMxOUpxfGDdKgOyd4YtAf4Nx0BCglWAP2fkb0UPaBKt7EK7XakK57ICkaZ0514CR3NsvW4FwaNoRKjUUlcBFMStXInNDhWcX0Zn8FAh19wConxbJ2fhq6oBnX8RRO+2NL299nzhqTCMsulOX+JAfYAuPncKdtB+f5t87uiPRW8gIzd0gdM+R7+O572irVby5x6M4uuuk0nlr6TF8Nnnauggrjm+3lWeWDRxyFHTHEeAuTOmETYmYbF3Jo6T4JRXL2jI2IFxjrBs4Q22+7zcTIkC6u2AzKMyd5GJP8TxsMvJpQBJEd3H7rAL2QDrAUKwVD9DqHHZt/BK+kRaAn6fYNgDZJ4QAH58Es7MLPxLcbkacWp4rLeRjnnXpp90g6OJwPrSYjwz0mLrrG8IZbnXWsu1ewJLkA64eMLNghoh5YgCwBlpbVBFlzgjY9qEUz02ShyoAk4C4mhYQQiqF12Xdcsuq1KutIO/l4NEavaLzAN0mWapj7RbrS59WmuuH35G15BNxysxd8YRJ8Ch9FaJL2ipSaQCZz1wK5biW8TiDsi8G1uvBgJFd2iGRWjrIe7L2pnWnnKWuQt/XnLgqtgIfw9iJtY85cYp3MdpicjbMYMmPvn7leRgiYzAI9PSahw7bj2Y4Wjrvc6buqb52X5R4cjiUi44TMcxduW5d1hC/Y5JWdZeYx6EARCJMShYuGk4k9g9lLM9rg4LXT0R3QZHHxzxzKD8/d9gQhiMmHASKAbLSdGeI6nU7HNGhLOL6ZDIdARmbZzyHpLVONPmTY2Ujpbk6R4r6yYEDfc4RjF7vReRwTGlM22BD1EkF53jXm1MffFTvHHfCg8rp5RfBcF5MneRKGMmNzGW0Vzz9sLNo+B8HF7OA/n9f7iu/7o5nt3Ly33wuUrxUD9f/YOk+jXgENuX0d0MjL/eBylAuXWtGL3eoF+9de8RauGVofL0cMR3qOIk2j/BNZmQ8trezlru3G07xo+0P/NXIcbXhzbClvwGHE60nh20jNZC5lXsbVeL7cSinbcMCu5Dfq3IVskGYtnXUKAhDZXjNvfndOBNUBkm1TwGYhOMmIaLKVQPgB3Yy5EgjgWvkklhUmFAGU2Hc0wy+nY8bM2Eb6InDiuiB8FVg6BZDPyWTDvdFstWdpe6erH8aPVhKzdVGhNrg8n19tGqKYyUhhVTbZt0UQf3O8Uu8NE61ypWauhHdd8wM9fT9JjkkpPAde2fefVziTQ9HATlTDTlbkjvDIPC+S5oyYaMLcj2HPTZz6GP7KLWwe+9HEVHmsM9/Vl8Vpx03EX7dOVcTeEdOyE/HEo21mNC0yyO+9/dhbGBlL5Nw5/vIYSn5sHcfZxT9GV3or/xXi885Hp+Q/1rhW3RN9i2f4Vsn2eHpl8PhqUhenFe0erW1913D5OQM+LWYBih6+erZ4QbK3kYg2m+CugRvjrB6cZroT/FG+PtB6zJtE3zuR5jYWhAZ9PBvYiUkDJQpUA3+XZyv5lpY0r2Rt4PPNCwzQ0BVGv4TQq/mxI0gCvr1O1XWuMArFVoeFO3FLgBwALL9PPH2+daNwnxvWnnaJjA8q0xdTERL4elGrW5a7HnPc/pSlfjn9775uvPUPJmVtk08f/87IKl9yX2/VafgI1P7Q7IJXZnLuXIuXMLR5+VUg9PbvG235qK5p+ldQMhXPO0t//MS5eS67+bioCM148vTFiq7HN7kZ7Vy/Qo/x/XRPYaW2vfFcr58/Bx/hPJTYdRrX41g4JIlzEuN+qU55H58soEvyycadli7SA3kaYxZPl1TaXF90kVFJTOOb8CHMR7SGby+yvMeoAH1jwQ7PMEBkkqL2rzsKQIkKKX3B5OLkyMn7g4YCwM5BhvhasinwCUJd7MWpB7c7+Ka9U9xxhgE8YrdmbHiusMObX3buRwmvj77J337ygXDDlzZxFoyGpiM04Ov/Do1+HvTE9BFQcz/XjIpvj7tIiy4UsHiPGkNPZs7DLcXse/ACCIg8nig6+oilIJ15O4FAmLsrq8Q3bB81ecQfaxtyFUdnuQjEBeagBWvwVfikH3lghuWVO5+Jpd4E28/PDF1UxQ19RaIHR48ojl3mAtLI4plNl3zsXjYm1H/63hgMXDhxUfJz378DrQNK13r7Ql18qrbj6psR7LN+9hrNna1qwGh32UzsqObqots/jl+WqnPi3hT3+N2am+vX9vZDS8Lw+UP/DebFOIRdTtJ03sym44918wSU+aB9sZ3ZqSy3Q+q106FJJnbvNtjqL5+4Nx+ve7Nh5hTseTh2Y2SpLRqqfaVTN7J0oW7ww644likbuRY76f3BS9EZiZbhHkfqNiuloimuGbIEaQFj9fS2rw1IQ2Bi54dMAcw6iCby9TlIQjDEJ6140D1MPsvTnGMxkXPypoE9c88OrQDPMzX79pnZQ+wGKRALOboHpcc9BdgWfGrSM2QJAxd4V1gx444yJwrC5RqTdpsPNT/XAX/VQgeb57sjg+MY2Ar7m1TVJ+JLzprc1fCgD1PpeNykDTviblCnhqnBrUKoxSA8hjFLzgA2VsWSnZiUPsxNhwfIhw2wAVIw9XRW1R2ZbifKka/Rcht518LhP0XocXUIHcuq2UgUUhZfzZegk2ay38y4wwVKKgsO32mp2qj7eYCf7XhpMSzR/VMcP8oIerp47M2JZCpt6UEhks0u9Y0sc/DtRTHopoSgXJXxXw5BzBVga17EOdIcAXY5RXKcNCH0hjS74aTZKeK4JcZ13ni2lsdoEU27G6U9Gs1Gt4t+BkOeYw+DPHmYY21Eo+yuIcXs3E6N3ofOM9xU1rObz9Q83wt8TBiuC9XY0tex9D1dumsGFMwc3uOyPPHCUVuh0l3PD6MuuCc8KhlyxXciToW4KCQwXWBlTqojLyHqz/jdbJxrNVGVEnYDOApiPrchePCdvvOT62m4C6AkCFU/t4S9yGG/An3QxdNJo7tFxO/ZyNgYUEE6QWQA6ZHDJrgk8aLEC++pfgVLsFYRRtOnckfVMpmpSrp7vaWtrLuH8Zr1ycmn2sLxlPU4jxgA8a0E06l4vP+i3d09vb2xXo7yc9ByOSlee6fiqfAd7unHxUeYh0kRUtOnWtDiUf+neNl1sDf1wlieJFaW2VS/P6KyssvTlYVtrOCippbWnot2Z3L5/LH+jZZuYhQYlcROf0neA3dSvmPvoftgb0PfjFdNk9P9DbEIYVPH3dvbPmVjb4/JNjfihkOBSjaNSHB7qd0Df4l/xJiin8lt/NhelRpuk76QUPrmBLWmV3Q+DgyI0S/xJFrIq72SFaSF2oeR0cs8rlwdNHfrIJvYTr4hhCzoqsI8YmPTcE6Rn0In+B06oMfB8Kzgx3n0YWTKO7Pd05Gb5d/IICa2g8YcKNSuEn1EJaB/rv5GM9MKoU0DY+jrAAtioQxkhCYi6Z3+jgoZJN/K5K4KBq6H64AZcOjitXhN2RKj58k6Hx3o45Ez2IBQL/ryaD69P9lseLL8TCUfXl0u4mQRlXQ0Gl6PWXbsjpbLBMbSaYQGbbye76Hy+Rw7kW9pW6UaWh6dLa5HEjQ72/l8SfMK3BaixqILyS569jXKBiavLdBprwb+sJ7r/qyNlSbm3G4mv1IGq76emM3FwuH1ctBudNGlVA9JF4WWXFIS3ZNpKxcKei9qPHhCdEIeF6bTJgUyC71iTUDYR/u6OUoIDHOkc41wSKKX8R0KtjKW9qzxSc1sWJay3e8yog0DAhe07Ti7jp94Odo+13lDrPM8u9mdOQdnoAnj0/NJPeqklZSzcJu0DrjRcRoRlWpW1GY0QKoZ/bUPub7J5HioSbGH62Mpy8PDLWwIpkJbgqUxV5CT8h2GHaF9f2S3uzhKXRM2YZ6MRQMpS/QJL+SbqfXnQEMZTeN4LQShXdFELuW4wkC/ztup+TtXFOwt3B3/BB7tB3nko0S3bY0PDM2xWtPzwzOoIMPAwb3LQvRxqCOsgFzcIrvBCblXdsnury3BZk/MnIsTiJPfsw+uID5H9fsKy7ImUwgtGZ9JgNjKErXCyUQ5de4xPtEzcaHZR+H7kgfkozJ/KsdOBU5+0w/BMOJetmGYiWDldCwGTIh0suKM9uOiqESFeadx2ipmPmL/hE5WVAkVk+5+ESL9s1aFIWm/3iTI/F1RyZDloT8K2zUW69KuwPVEzC8EapQDF7jWBo9BHm/M+9EQXEeUhKpYOLL09xkTpxQILICjiX1giSk2QYL+//lFkDsmepmau+VDeP/NG3KF+DPxTjFVQpOCIdUqmceX4ra10CPvWJIfwo6Vz0OP08RvNmc8TgySolSvrFPx5fGeze28+kUyLLhW2LVhV7DmY7Law4m25T+pPMLulh/+iL823rzbAp03KUf3my0OwZOZwblWV2xi2bGZvfGaquERf0e81imB+uFiJEq/Lvx4Dw/g4orkTD3/zZ/zE63KfbeOrTdexu/vPCfos5G55PK8BNyTXAB4Uw6YOWl2HFgxFrDj3Wpiscs9dMB03AHJ/71iBmfB+TDCbRe+A9+lpAbinvwifaDq25O0BuX0WnvMef4dJ5lV/8zmIl6f7qZkAizbBfs13aqsFBW1TpQw1DLbavarOvM3RD++on42Hi2Gf5U08VxNu3n7GPqnMpKmUzspbJAFxz/1noq1fcN6i6c7qV3c7xYq9vPXeDWlqGbO92xCBm53LhQ8m9VpT11s7hgmAGGA29JzpmxzruovfeSQr1B6vJK+b1uc4umGwF9lmR6j6GYEguuWCrDM19ttSqx+ldNKII5zi9HoE55gblSwHsKFnWUme4EZlGH+fcYfkeSFY57nkLHceOd+Rbu/EisKQOj5tU2btuTFN+XK4ZwPP0eCsFqTfBKJrpdLjlZPFvrnMRQ0kC9zTKkJVPT06ZJP1WAzbJR+jdHhFBNdSSkfkCvLTi+sAysls5i1EWj+qCF/1ATLhOUomopXkHCy7QeVbQJOyOLu0nXKIEmhz1hzoRBn9ZApW2q32AHfkogCmhkseJSIdPBsl00wd7xFBMtqbeyzDaFl4VzjWOHHEW6WJxd3ntPsWZ871+bW2gzcgEccdEsKJNFNp5ehDIlOwuyfZqQDjkMGVx6+phKJkDs82SO+tkoRPF6CkXxs+9JKHoFLuGA+INqIisbOTndegA5C0TBxGn56bH3ulbgWbY9NxY3C2AYD0ofoGqGZS+RoSurSibX16kbO3RFx8t41+syz1rZwlqHt/Nxs/9dTWy8dkyN1Z45MdTvPG0PyIr1RKrzXtDbuUJbmGt16d6o3XreRSmVQJJTJFJQa3VldEWsDTWcsWjBnKwT2zIPCGzXcGnL1udeTl6MxzGvS+ZmDGDMyIhNA+SJNFOLxQRBHOAj81RE0UEumO67rYraXJuqEiLeDjK5jOEm7LEaaw/M8cD+JZr+JOs2pDVcYXtZCnK+c3sGQDw9fJXjSJvFfLJyfSkHDA/zHaPbJ6T+58K/vsVtVnZypfevmg3rFfg8klytXMYJGNpKxyHEQ3WN0JuaZ5dPmL1IGerR7nqSlp+zEtzi3RQi9XVQEpaJ12MrTP0wS5c1YrzS7hbY231HHGRsY2u+yx8z/rI/5khpYxuxQOI3ZxQMLTp54Ie6bZz30Ydtf6/DBuYX0b18U/qZIB2mAa6Wl7FOnb+SSBUbcUBUZ/prqOtGcFc6Qoy+JHbVsUD5n1MekszVrDDtpcSROfDNzA4NVTtzG6iKSJTUrJeeTReK72NhxGk58TZHHzAFOvy3Ds8hC9gQ0pXdj6xx93S4GEm6VP99SOjDQRafDlwAUuEbII6KInwWm/knJOithXh17S8cQTw641xKEgorjNwsbmEzNWJ8uV2UHY7ZGh2XDBZ77VvsJ61Tfam35EHTRZU+Dzxgewk7JLNlxvawp2twUfpbMoqepl9vGeYSmQpWb8x6BvUg2802uIINZi9JV/kKlbYeAN4KsfAZmO5U25gvp8/3jja4TAVBR4hZnwuO8g25mI50U5EXRcRvG11jFyTjHSQMRt8YePkiEpKY9qUyOxtXMcLWuYqwGZceWmq5WfaBcsSW5smiU+t5ig52nTmaFkaRTqdcAnyJIGr+9DCplkWgDFITTSQG6QJbqaWg6UvHezCUCZZ/hQqQjEBCMAAa+ltgSmiLUTRmP6mxcNYkC9CbqdbpGK16sXc93929JmSVZJaYkQRCDyFFsnZLxgJakVdGD+tdBp4RjqmNa5UUogr1hD/FWp5/fF33zycOZ7UIsmYxGMy0Y2qkktoo72N1n47MulSPRkobr48k6dL0lj0X0rbIOY9fril/XOgW7tY0+nDBb756JOM+QVY3r9JUTgkiklN5q7IGFku2zLuZzUpfoSIhw+OloQJTl3Ep1zWnhw9c190tk8pFjE/L0qqCpWirn5JDjCW6SmgUJWCdxOJKTGRkn4Lzp4DMEZb8kR8TGIgbrq1CnR0bpt6RkVcn8cfUE/S/CufFmyJiWrq1Glft1GVgF8ouGzsulAPLsfZRy422gnk5YqHRrLsyja5OIIoXvAHkcUMdr8IZMpt0vdWQajJPulU6M7ncIG92ZhPj9iz6OEPU6Q9FqM8OK57vcltTK1MjJIMfxnOc47qekXAC4CbbwWFFy6W9zlY4jPMOMCvtuLaTADBUY2fBSR5M8IbnrYQL5r40UjOPq0TxKdURoOLRUTkYT5SLCTUQyf+V1Q4MDp81BuTMzZY4KZ0A6nOnrMBLPj+F3TMYi/wGM+Wec9Vfq85ziV2pUna/pPgCDVO13u84ZyDZ564zJbMcIDjIP7hMHerKv3brxYA/aR3dM17Tuijk8yWS4YenWiK+ZGUL+NzC981m3UU8gJCB/RZfGnqhoIwiX1/g2i55UNVdS2qRYQdzGNpzGQvIMO4R9HAA9DbdYccAN6do+5mHOhNSB05wxy1rz2utlAlls5mzOttlU/CAdJQ9aeMGnXaJxmbMSn/P4RLpWE6o9YvGG6MTp2lGazwHPoubAKYSFM3Dyoq4pszpYNlEszGC8JQXosax2vS05zrc2qjNwi4WTfOmiOWc9J0H9CDIY2kNZtUyng7GRVePwNRFwzRyYYiQqjAje2qzBpZ+W/BJqWcCX5QsBs+TODjqNuRw8PHUMVpf0KGUJJscnr5SwbzTaW6NMk4+mJCNwaBgNg1ZqwO2aacqVXY7Co2+FYEl3G81yb/qASYQJECG/NgGWyPKJJrac+r3n7nKtv5Up31vOGFw6+pCwg0y/gx3F9EXDHSBWNBvnL2OpsrPsOz9uUYoCGDlE19KVl1elnuoFf9S+h8ZgMZXK9ruPktO3rLU2DVkHht+Jf15YX5trNEjlkV4AixOERFUYhcyLCAsz1CxCZ/GztdsZjXu/e3bshB1ISVnVlGxPX0WUnyEkHisVmj+OmLqi7mEZMUP7+nQx29B0khrOtBeUsPGa1bbHXxHDRjmWT9kCPSgZwmdr4Wrha0AievuW5FY1Rky9Dh1rGmkopBXbsaLqbCz5OGlO7G51yY+EVgmdfZ3OuOFNcKNc4gEZEpQQwAUjhHZIVU+/eSecgcsDHbiIkGQVq4pOSpXtK3+piS5hww5JUqRjL7wSlG5Q6QduD3Wwee5WjVSpTb8w0scMR2lBxi0kxpGVgcjbnbfGKsUStJ3yBAKkPzNcKpRKPv+4aseiFHvFINJEhtVefzSxaf7oCC1ofIHFrpQtswgZKeHRN3FV9UTG963k/M4CW2gffu73hqcDbfnsJNdQdFh3y89nCLpTIrA/zN00amrpPNYPIr3J48arVfcVSi9pBykBTEpSKneCuGwrCyv5fDlo+QzMqdHcSKnHxuJqmnIUt7uhsdCFjM2YXmwByZi+Xna8FYyRkuhNYWTGYVtdy0dAkAT8wG3Vu5X+g3j9vq63Ty6Or8FwqN3ExcN0vEJH1yuGbAeUt+MxmBpScmgrkxuma2YjTGMwOF0ViP5WOOo3uFV4DY/L7CsjkD+z+LrqkrIL0mymWtL7YypD1fl5vc67Ig8XFFxh0XarcjbDshulvmch79nG1dE7//6K4VxI6ARg/drYmq5jc9RwQFN7f1Ik4Gz5xFyyJt9MTO6S3bMxZR5EKko/YfQC7zanC0eagOaaXNHBgGCk9lBFoyMyaRbvJqPjW85SaA6VtNXCROcZHVLjp8s1beJqmHUnqT6mPXpWlc0+YQ/IdcEOTbwJb8xk+v7JF8W8MrY05dm1p2NU/gjYIOPDa65xbjkvHFG/MJrr6x0iwKpn7hq1HX6OT0Rk/zpsafZwrWYvNqlLGYdGjk38EKBUCkKya7Fg0ONPNaBEApJK1k85KCNOsAEfgsi8NaHZbm1jYBvY2hiMQghD+cHG+bKTox0sLdQX4SZms556LU/d2KOJItkGN0Ed8BND3QFxV7Ca015jcw3c6fpAapo5R6xTHr8akuqGBaYrHFPvUWkTRBGhr8TUl+FZtw6BrdI6bjVWAK8KWABqufnhE+s4xo1Ir9aMF0yvr6hxxtYTV3O5sTecbodFvBhniUHb8JomGl0Tcl5A2DABCuUDDiLQQXAKTvtMg47NrML59/yEEMJslvb5FdYbzSZJ53GyCDF9i/DvINVaGS6VM/E14JH9BEHgyrh1vK2DzAYhZDYvCyMiMJORjwcp8o4inw5UmNkt9QPiXN8PihLcOEOe3i6fiPzqJEt4VB7p0bvrznKvUmE0mS0apgGuoXqUIPDppD7uzhSSVCSIztzoDV24ELisbPsU9dq76ATxs8X00dQeXkhjCfwpE3evXdU42EV3Sbwhnp+RU8Fktyal7pRkyJaLbffHPfBtf3i+d3p5obQuRPl1fdzsPjRVLln+aDbpUAmpjD5oBcaVz0hPvngsz7IswlCmn6Si14Aitl6x2g7mzp7SpZcjrk5NSYtU7yZnk2E+FX+CSBdcyYMLamM356Y7Yun5sPDW6pFZs1bFZdlL/lkNCLAJZLVG2hEy8B1SnnwRPucacJqu+KXaN57Tf2k1sMMbwKhfhUCrx0RZ4sg0CIhqCE+PJ7XUWHDcvb8hjKfqxn/4TbIqL+YH5yqS/O1S3rtyjtwRPT/pfcFsOn2pZVzY7do7frCJCEdbZjmXpJ1GQpHvMG3PqoMquez4ojHFBimUil77RkQPG4kVc54FU15w60tnD6fTw0qjWCLd0vfoq7O+WMQzgcrYBhGMlEddQoOsetWrCq/Y0+VyrA02xkKvLllXAygS2a3HO+ZivBehb6z5zNiMQskwYPpvMHvo+Xssw9BH+i1855zbLaVgsVH64qVcuUoT+sleIaWjW8XwvDHJKdZKrUc/Rz7O5+fnRxLJKNYLQscLYrOJV/Sp5aQ1GC6WOO9aMXUCydj3J4t1DY/Nh5cpDd/iC8/xTrpgWA4v2PdmtCpheiUFkJOKEkQRuuVGm5NiX+IgNc+JfIiKX3fGqoqzqxlZdk0HC2caQweCwzLJeL+HYxPEc45CU0a/Iohbp8AQeAXlrJcaQ5OjIPGNF/ziB0V47KBDis2UFk8DNa26so6d9cVdgBerP91fgwa8K+AxcmrndW87dOasSMQ63apix6YZPfSFp5kgjtwU889yu07nXIx4jr6IGoN0rkwAUriHDO9wQIiWd7mbmUsi+elccitJiNbagdufSqGHllLXIlz+nMDWRfSGyvmx5nfyInYCL9HG89fAttxPb3NZh0Iq1QGQmdUL6UepzSpM2mReyD45oKzrGzG0VNw9H4gEX4g2ksmOwKoE9DPW8ZGJG0kzGH4oIfCQWqKjIvPPn6xyfhWOiLY4GZFtyLXAHJJkAzOVDEK9xnLiIqvryVVTp2bf/sXKeOkjyOXjUVbrtF/vn61pPU8eV3TvaAZf3hl7/oiw6ui5b8OeRFL+foq22fNEH9Rtz9tn87Wv/dIk37/Ud/T97ncPF+UKmSdtNsNIJJ/MQZaNjT9pXQ9xFcgv0Z3ubWL411qamz9Qnmro6ERFZrITNnw2nvXkbWMh4/L2xxKz+6M7XguLZh06L0tHFN3Y0/Et0AJRMu7SXxmAW6DXO+epwrzJrd6stp04jmWQ/F5SVJEs9VpaGk7ZLlz8jVg/L//f8OyHH/KihuchLIbJ+OQR5cRHscaIk1JrR84DG5dPitrIfQ3vYhc0KhagN85bKd4Qyc2NBP/gYDbCHXv+Q3iyI6zr4cIJc2cVcWEMEePG2ni+8li+fRhNqB8Zywmlv1gazw0AwpHnL+zqqkvYtXC2t/eFnS0VxZb5s6gh3AFo0YHkhrdKGTXY9SfQNfGgt2gyaUUFXcYLQha7hku4QwlGUV/0Mre7sX0UOesQNA1I+b02E8t6oaes435v+EFGuVL65MQwfZcanyZWR58VS4jg4dEPYiMwl6vOCh/o8S6dmHr406b3ijFXqM4K/1LsLvfFx1z6RzgrMlqx2qiyp6IieOgXaQezXHWUGwe2OKVV52XfVpPAlJQ6K7wIb4C8tJTI+DbEkUlNqbPC2iJDtMyFw+H+QVgNEdFfOTphnNzx2MXPIk4XvQZx+7JqYEbyq41f9f0aPTsuzvQIRJAT/5J9gyBkao2OIqfPDdyl+0y6EPhk4Lg64MMPFUYIt47xoFgDLiEE4yr1+FxaWF0DXYO1A1TOZMIpbJIJDH6cxLq+xEs9cZMhYeWCNJuaZVrNhvIHMrYTYHx90f/f84+urpFET3tdIhGJ0wkmv31kK1p7JvIq+bLutmV9Wc2phA3ntNXg5NMdfcOD/W1NlW59iWvEx5A1v8kfaNjlaPBicsf6xxVpPN3KdMsDdLgNFQ0gTBVCL9YH6zeYPARBK9yNdVKPwFxoqijgGqIX8vT1mGjVHKjhcc8gFIzV2HU2fkvsh2NHO6e1ecESWAbzEuckzDscDMR2db25O3EiuKt8k0sr2ZPd1dqXXO+KipYWelcfb/fy4MIFsw7gVTy4Xl/8p5ZEeLUeD5/kOql4Mtbilehu/ioUecVhBAREeeUcS+OyCppXuTr5rIEVeuXcW8K2fxvF7+spl4TEaS+JKd9oVmLqVCRw4A0eFL7RXF5pa4PkXQxdvx56xH//6529HvWvg/OUAa/LJAeMWgIR0fh/wEOeP9KqLW/Aueoe49aajUCAj7KnvWWq+At0vQ53Mzvo4LKX2Iy3/4eKmRNyfvul6n3PsyqbEMtUoUEI7uanNHy9TiCwiXHnQMh7FjNglgBdQAfFUJ5YkTixdQEsDRq5++PSAHUSHi0peS+eC0zjuufOfXsVmQaok3Dod6kz8VxoGpSX/NV+UF3YNOz6MbGP5jTu6zFuGV2PK0+DWzw9CD4NIXCLeetyCGrgBN+PoQaTSLrXi2nd77nKv1dNAV+m9/+dxPdPVBGjHolPug65gGshNBqsDG2YUl6Yr5Wi09VuOlXK1EWdZflBATzm+TIqi3ag5QV20vzvSFVRQdkXyBpY/S3sqPa1WQMnJE0JQqpu+0XS7DFG83kFBnDKNGN6n3aT5R19gZzooD4IeTcUWHqBUp5lpZ6j/qCRQjIm/MR1W1jox7fsaWiYSX9f5fb79Rh/YJvMb+6kKXyPgqOMJoHtw+BJ3oLUpLSqqt3tlb7cRO28eqYLUHP0qO7u0zqOvHfTWeDKmkJdDjusnRqGskX1aLuFlVPTH2YBj+VA8Fm58rMa254IoY7cTrWq1BOqChjNaki4UVhzT7XMNrPNNWpUObeKG4u6j3uu0pVrOAXsCt+QJQFPViC864201P9Kigw+he3HXJvSiEnDkxT4rJA0bFgrpuMQ4mww9FndqmRWv4kHWdBIupHybd0QPQNHVOzO6kDYZ9pXtjOnU+UNGUR1f3spsf74QCib6KePjQjLjWFabQibClC0nidbOtXqc7pDnwMmrkfpjdY9hinF59UzzjUYo/DSmfy850F2Kn8JjUsQgfFQga2UuOPAGyEbORMVOgIub4h90SoMjQwGNxoa0bqpLcGYZ0KslHGZAs0YrUmtxP29zkemaJvczdT39Z06DcYCFpdXQ3+k+5kTby3i1CF/4M1tpYTX1E4zpSmrsExstZ17Yu032tW4uBNyhpne2Faaap1qp39K30+JDgX7aBdcC68u0PJ6c7DVr2+nvrz9AylS+2TAMmoptHE6eSks79+N1qDLFolHeRgFuSGh48+N+Mvr/DxtDJdwQc8+QvRoS4yENJpQG/OjyBF7vtdUXWhYHKdT6BGpaW1nou2TIy9j3H41rebphjzPhilZG9n9U1fCNmgdxCJpWQ/knXuRDD+6vIQm3LkKQ+YaRJQmBU+SUAXbq5l64WoFjFFzlg9t1UpgMo8VgEW1r8zeGmcv8IRKw/axwci49QozvOW95L9gLE9YsvriEvhDGQLDlZVxn5oJ3e8wjibUXo+S76oCDzVa5c2E/aJzOkViyWjZK1dLPo2zvqDz4FS4Yr5jTF3C3nZnNeMUuhLKBOQky0UTryEz67NhX2mnDUdfJqPXq1KmGv1mB5Zix6DO7kBnPt8K9REKcexEwjr4CbLgdNQxHjmSe2EmU5Z/DioHvwSrqzbAtmC9VWZYpXkNXAcKNQywV6BSdSLajeGPRfC0+SP0AKreQxD2DvKqxDNFZyYgaOzD78Q7jDAaYkA/BeTZmKng/29AfdtcFpJ1NFBFce/Entt5IsZdlXEEb3IryZ94o51K6YFYPB9L+ogOUGUnobbQws/34y+hTA408m+Cx00YB5YhMNcUGKJJgT03FQ2vTuphCBKv7f1PZC5GzHrfz0SFw7F4ZgiXdrllZ0zxuGsSVwKO8eK17b3zX5nWBuT6frpA9/sHGRJfIr0wNUcaj7smkBnweOh4JCqTJ3vpdsmuZ0amHQou0Gab8t+mZYDqxk1kZ+c2Y9thaOhJXRd5ndQyuvYMD5JfzHA4x7uMppa8GBJArsD5UxNQXidyWxrRKp6KegFJVnmxnCAyBm7Shy3PJZmX4xnd/QoCRg3mxbzBBA4MBrgbvg7/njh5kDYCuVqHLyf0OgKlLJXFaJrl1Z7OFi16XWdztSw/wF+p9piuer0/8WKj0o6Z1AL4Qlv8Gdkca9Fj54ldLesN8jetP7wr9rOJNvLsBkMqCd8hVlPFMpH4Jo3TIhokyrx6Tf3jMXxAjnmVHRlLFxw0rieLM4n1ar1K/Aak+mj4Cgp4por8oNLN8ywprewrXTTRIPp8zdD7VWwi7tCM7LcP1xFmqyUEWLXIWB52ftmEuT0l6UdJ0YpPLQ+blnnSE4WB5eOP/QWtMJMQPcrdqA3aV6mKM2mIbBZbnePWiOCmb08Z0Nyp1qt0m7oCTKL49Ki7F9WOZ5siG8yQytyrP2U5FmVXyZ7U+pmxBC2EIZM5qLfdYdnVp3KSuo/Pxzk+Co4AFG3mNEDRRor0UxOdlbpXmx3PytNgKG3BNGqfobu1DsvSJJ+vsMHHqV/0nOC0z2sCPKFnpPUH04Fz9J8YDfMpnctNhutNk3pdI5TrYibPlc0aaDwOFlN/4kInDUKR40KDwCvki4hGp1lWAZpOgr7gVA3dwvkJrCK0FaaTc9+fnc4zJcEnGZvRe8l0QMgfeOIIqam9qZYIHenNkKIObg9l3yQ7KZkVmyfzbcIOlA1PDdGJeUcSVkfkaOiNWI6NprkLeLmbvhKXJS6eHNK/+LvnQrHheZbKY/mwFP3pyaEwhfYIZA0aX32PnYtCgFcja2A3333Oo8BczqR3fsRZisaWzco6ZUtKjtOqvYnZf7+zc9bp071trt3e3tJknzBqtWhnbsQ+7gzBbpFEzqNgpsSqSdZRdOLJqGy86d2Sv+kyE5h3QqqYpdux2yrLKcpcxGCeMyG1OAb0jc9Ax1GzMx265QgVZcvV5Z7jsaMaLpbPHqzWUvJhBSNRgKtFcB4BU31HTtZzfu650iS1fJfaLGMTryYjcIuEbx4FJtB23bRcqyvOreHHfEYa100p/P47wcHDmYPdiOjNo8G0FB6FB9zvJEhKrG9zHSKsxGsnyqNBw7WUOq58KNTLOzAhi9bhEPZaWljrkTAd9H0JAy+2IT51XpC/+fGGznSzNyvM1BzWxA0cXSJqqVKFEI3E4/lc5FlXgX8fg4i1HgnTLawTHFkanqLuPJnkykkKjov0cemKQRwJrVCsidGhscndltqqB4Z0fTpAv/2Qxdmt1y1Wx5r1RTq8SaxWVJoQjF3IyLvOKKxmrHUZ4SAOuYSJSP8v+17gWOs7Wc1AwzDy3Emrqo491mPLUSqT5C241lMar4es1Yff9z7N5Dkz5HjUTxiNKsnqorinYpgjA07EQvqFwxB7IsvH/dNMl37jiOD3PUp7TJpMihpZqIEAp8zOwEjlir3P2yLzRUCQIJfQAv2LKHF4+CU4JQggTZw0jbG//HKw+eQ5aCd3izsbyXBDS4BKFVOcP+x9kddW1BSXSzq9PvUHLf9lkqYT2mjiWlAnt0aeDNN2qmrzmgHo1R8qMazfgEU1Gl75ZUJvqIgOp9e4uNN4sMRcg2+H6V3T/k8xRtosQULXk87u5gAfaBBzGjt1mKatdIwq+Uf0fqLfIUAiJRi+xhk8LUW2QAuz338dOAPlT/CN5BFJz96wBKE/vYy+CVNcT8O1HkIqi+rskF197aPOdBrpgnqZPCipZbwhvJIXqfwNc5dQO09G9hZD1qnBgzQ+gxs02o1r9+Baq306dc1CNMZ8i9OpaRqf0d4KQWNU4q1VrukBNNbInSrqI0kxZ7rAKtV2RQhnOamZxttdkkNmF5lE6ayzZ16jGX/JdCqvKjQyB43MWEBOXDGbT169ETV06Y4DdyTH9wKMqIiQXJ4PLZS9KHymgEHzJFahlfVGwb5GEFdhPEui0U1G/BytssPMAG4UvjctvuYESA3i/ShFo35nFonht8K04+eX+9jvhW/vRnm+3xP6XRvowBMrcMn3q8wOk2gpIb1+OQAFHlXn0UuKQyB2XaPz0AonXmaTpN3quvcnmvnIb65Ez3+tdrs6eMqm28TKOd8lmhYxjeAVNNCZR7MMEqCY9ZwawYtV3bGqrDbAy32F8n5lDY1NCQa0qzOy5q0HGkj/E75Bt9A7FCb1mEHp8z1ZRATNMHPvyiukKz+sOUwHl97DvVFuJ4CsIV9g2xSQBOu9/zTguqUpN+iD5FEvWa2mjOS16Q7Te8+9Qvspe1s1b7Ss09DkRq6MH+JB2kgNX2g00C6IOyOX6gZSJIkUXayLhnSO994rZHLrP+HR8ALXfQb132QFAG+CmbKrup2xa3gvw2gGST/14doW65pKaTBSpFip7jUlWIxGqw6dZt5l7Xkl2O3fdo0GsRWYrewrFMMVJV3J6927Pw8HcYLtntSOEryeeJ7xJk7hTeymyvySWVc/OSJ5C3NVeVDZG9yzJvqoPnRwirSf6NT0unrp33yzmF7GctSJD3rg/1FQrYTDWK+okVb4R6XoOv1FXfDZ6JQ+5hC1eX/ria0TjUvdDhX5dpimi5Sa16E5zjx8BmMhoCMTVgS0km0gz8viQ1w2LfG1luAFK1mXTlr36mbtKIIS1jhppfO6IyCTEEpWauzJS53OhdiqnDU9pM/nkpmtMO1U97kl+KXmeZxfRifZ6WZZQTlJCDNX5FLnxoU4yk4WdaLN/zAvti1tt7p+uo9srYdX0mwvvWtMaqvbNGuSNDONo0V9uKvs5DmkNDRPxUj3Yd5HKMLOP4dOPBITC8vGjTmp/fNVqp6Jb5G0eUPiL/aQZsksomO26MbmQRQ70tQkXlxhL0s5djU6kayTtxlB1YmE87ROpEW/nRXZKjGs34BLlipsDYZmSLPQjaQ0pQ4ial66xY7+Sp0gt51Oz739eFx3UpBy68eUY32Q5LDXJ+8h3eSJRgn1ky007RcTNJFdHDMnMK4hmKgvsWekHFbNdNjwHSHSZIwyLpcixQpvMe8hMmMoNhLm5rFHxcK4dc1I7z82lzSOgM5U0KNoi1hr3LtwnXRfl+FOHleN5QF/CpBwINBMgfnRj1EDOxU9rU4a/RivkphKGzjCxDDcPAjZx2kHrn6xPnq0e8YZlJe0xjOhzDi80W3R0GzbSaKM7lPPXqfF+B/u3LuWLqat+1fv0ViTQ7xNq2IsxfK+Mgj2f2fjpVSUH33NCaK63FvfT0lncunSQKhFRlaQdzcOcvbPllI83cq5N0RWMaVStxZAis5zkg/iQq0vqOQ0THklg2hx3kAxllYBKp/D8AP9wnEHoTW14zhmh6hkDUqrCEnO/CI/4tzzzJGssUczqUyaf0nvIMoZZZpSFw1/spElbnqEj8aouemHk6F7b5fWUmusrX2qfoPN2IqNSqNuxkoSUxFiorYvis19uTotdTNR51PSLhg6aY1DxHlxTOyKTfvEd6tffPbUwc77f0PhyRDJSFFTPMfZ7bd8h10lsyix41S8tpqO8lKTyaAr1b1UE1sFJ3Z5TwvwDUIyhU1tztPWYW8vipBNguk6sQOb2w9Uijm7ep2hUV1v+zWmupYJoSEdMOWI1um4r0CfmPh8QmySK6nk4gsKJZRknNxmPgXFLttGx3jZULHRCjtRVgJRTMhXwzVoSHNDhkaxK8b3LXbAFxnMIUZJsaMowy2wYoIVrcbiBUyxK2Zzw/K5YOAqTGPzMtGw1ULLJzWsXQwswPsyb7c1F4yxxIVr1zo6u8Xc4ukOPXs0la6uWJAwMJGfccv78vqjZWOsF7dE+5HZ0VGjhg+N9pindYpVV1csSHiY6iV6z9yJttd8vN2WxkTsuNMp77Hkrz7U3F++PTFcLvjVRx33Pi/NqDHes6m10TSkmCcV2lUzJ4hoPhptV6A0zm05PUhum4cod+miSaK1FoB3Lfk/CYLRwHZnsPMk5qLqu2rR8aFRFwchlJLevMFQ9xtOE9NX+8dL4ORu0fj4zEm7ND8JsdmHUzjKLKY4ftkMn1I/Bn4KXtx5l4C/+0hVgugrROTQU/FR4sPwMkcuwiUy1i9YQ9aYRZZkkk7+DE6XQgePRBgHxlUEW7Vbv8WekHFEDraCrvOmgs+Mg4VJM2/RZjcPosWEcNP9g6i8bTEmkPXlWSknwrZDEMUA6WSRvU3G8bh2HVo+3X0THca1TEtwj5DCK2/f3ugMVTrDOXmWY4AjUtsM16k95mOPe91eGPXH1tP58DMprGUFXaScTb9uXAEreiPWv299HQGHUK3paa1YSST0VqrknVZqM9ZjyxXhz5O5wdS6wU+yMMVxl4De7FY0Lu+NzlnxAWw/DxzszDYhatdeyUlCf7yPDA6idrIobT8jI1Xb5RMgWFbeP9L1DeKoQmkr6meqKfIuLW2J4tBx+FsO7aGrIE7W0D/mHeOhaPdfY4LU4jTtco/DDvvRe0r7dQRWijhMaR3macGUrKRwPN0K7yI0Y7DDqNWDnyPIcYq3MpnYxi7yPTmJynSX11Eb4z9SrrJG1rvDjmhX2+p1DSuP7aKSVzWJfqk4B2hxRpjyDu0i3/vmQWoN+7KaTT8pFW10Eyjew9SnihNkoa9vgr2BSmLFsmlHXwjKuM34cG8v1kTOljeDY2pdXRsrk0Tfdd2cPgocVEludHRZ0BVOqHvcrxbIR/mpYXCS3xfeY93Wflg7GqFKc3NS/e5pODYnIHuLdO5ENn3tjLo4myRjk6qWxPfZmXwxn6RUyxAXq1JV7C66efY4W4jicIYsI92RNVPkmw6aQdccEjupUVZcnTtd7lfQaARzU1lDmnYvBjMdWvnA3bUNbzRKwBRsabLAKvn7V/Ks1f8CwvrOklIRncPlMy/IizoUmhkmjCZrLH007DPt1CwJ0cypU5ztbsOKvIH2QCiozILuHKb1irCrAXpeUFsp3zTqdt0DjHAlMtXTolP6e92glSaWsllfZ9xtdwYeaO5HnsMbwmxdp3Ysy580rN3mrRFgV3J6wBOTKDns2q/MBAEbVToibB3bRWR/RtOheLzhZo7PwApEeeuZwYXBeXMGBVf8ifP7FsZHfL+xpc3Nnb2HT7V3R9zK4yEXcx41SD1Pk+JAjMt7xg+dKJu4JRzbFeaXl48dnZ8yu38hPONYXoDaHO+gjWPKPoYS99iiXn1UvvwXJwPH85e4wFkucHNA/8Gz5arNq+QRc+SoenfsaMpp9Xfe0t6zUJo83OdjEEFMmdUwkGf7OVRJCrhxdGasf8ixvEBHOYCH18jUy5dOEaKxoAxB+pLMi8B4xwDl16HO5IQbQRLbUZkS87bIGt2pSbg5qKBvWNGbCFSXDwYmDGiRk/47oHojEUXjWOq+9t602gVzEbMzQvT2zjSwDNwPMq1sUJxXOrQz3fZLl36tT+ZNlz117SGI+oWu5MUbyqKEOzY31+NNcF+fkV7J6OnprciGRXWqWRJ2+mkPEJ+XXbQQ236QsTpFxmwS47Fseomama/2+/bxQglj9O6djG/YMX9IQB/3mTycwAzIcjqRbOsMZ2kbdA5WpG/zSulyzSYf9JqfE+V5zYHsNR07vT+gAhV1WzmvUu7zeY7OXRVMbZXu08eGOdJ7/U+6TLNJQhUN6wXJ+OPkiYacFr9W+OO4RGfXxajAuBxP1IrWNPVy3mAOa2q0BPxqKiFUVUqN9eG6TQrEWwHqEEkeoOfiqo/ycskpgPqC074wC8dh4o8zkAPuU1QmED0EASsUNz6AumuB2RmfkGbhAySXGPsoDlnn+1EeuUnAP7XrLfFX6QnDWNC4KoiRe5+q4kww5azzuQZK6EHKQe+uRDoS0XZiTxl/2Iv6uPUdr/XIDfFp9w6MCNlCtn7lOrwr5t5xlWSHY6oRV55WsteH+8j0E394hyKkSBR0o1/bDBhKOnPuuKcxgizIl6JkGky2fMeedtF3p1DpLsO5txODhitZZnPtTlxsXZlCSX/FMvemEZOApa509xsDj5gJyFr6BDe5/XpVypShf/BTfxtBetdalZPo2vkXHW9KEiMqnKr/h1UsVGrO1/rdbgjOP4LqADx2oHLWGabqjkF6n3eJ9ORzyuZvzsCSkTm9DpBT+AWnCRuVUPHxCV7JAodKw+OUa3mF/n70dNmCUlspU/UzX/8sD1IaRinKL3zzNP0HoAcjn1AtyiUkRaLi1ESattPNXKYOkiJFRERUtdALHVSlUps8gu+mSOQm9SQsVHUnRWb/jg4awuEBaQfozq/htV2MNv31yfTKxNTgEkPu7CF+fxIq2490zfs34AxaB4S4gzwz9wkSFNJlsSs3zpgOEGgMMkGU9XZOX5fbCwKXwbyktBSlwlG+M3Wc2wVfHODyHKVkXmvjqHHWfLRymbnmgElNXVoTvDJp3J0j8AWjXH6MQC0wsvG9rnIXYYbZg9q29YyypHqOJlUAjVXD9iQx9a2vnKfh59+mOLc7xpHk6yikoq5QVAMeeb5pHGgiyZszaisi3YObIRKWgbrsoUn/8RrSaEXPUkvXK8GozhnT407AKoygx76wMswr6iH041XUQDN6pEeUL7eL8iX5u1uz2S/Gkorc01u0O8yozcdrUzvXzV9Cu98So2Snj7jdMgm72EputumHFA3M7Mjg/VjU9zcWZd9+ZISIFZOLmiKZ44UHXVm9dHItW0n1xb7bgKBCC0hSwlSw5isqEgVcVAACP8vrztaxkwZwCU5VDSutUlrbIXQsWj+7Cqb+n6nUq0gJge9c5kp+Mvoaj+d9h8n+LJ88GIa4GNbvEuVqOpXJVBZQrzjEovcUgHZ5T/PzDkih8xtaOq+uBn2H0vBZhDxiQPeQT4zqdx5oqT1T60eHVwyvnzIclz8wNaFabiMGuuX5hbI+R9ROIb0d0pCGaqiG/UoaQTv1FuAU3ImtaMTmuaf66IIG8kc0irORIsRBdf4A5m4TXhSFS2kItQqK1xqPkhNai23ga8TDlUC2wVk60P0EWRzAxBTVdxPNX6U+x+2G7bmjyGsyQ7zkEKad07dnt6Wr0xYzvYXVGN8wGd8Uu+OT/TezakmLR7dn7OZYxy5OOliVMIUcKb/lI7mVkUkzDHUzgjzLqzjdruBK807aoR7bGWqylMAlCNv9KrT3+aE2I0LNcfkMkgUrUXgub4BRgnbc+NC8RszPdOEUik9YXvcjut9EsR5xJWahiFd0jaIvOalXc/FkOW8xGgOxCBg90lmrE8DJNXgHWY3/4rSZnPVxsu0nM7rAlK3oKl4pTpBAPcypMSmkb04Nl1qiAUzeDJpUC2DhfRFv1kBBl3KOMG2//5LfMoC4eQeOPq10dEge3etdd4o347GNrSBSnqXDYj98qytuy+7climuoHhRbMdSFbnA3hpYRrWQ6Vogue9cUMPTSu4RQr3p85PcygpUcubZavGaEtyjPL6FZ1UQbcdTqsAYJyuOjcatymFbZ8nVFR/Mbt2W46gQ58BS/RjT7LMFCdJwpB7mRMArvz98aGrRmCdxiZuXXX61cPk470FmRjTgr02Ch+9QIjU0UlQNIDhjBqQ81zkyCbu5pgyNXdPrU2m+7Gzpq+1QFpsLVoxYsm73xZfs0fMDqEqzDGd2XW0ZInMhGjvtqUbNqnGpChHaLDMYo3ksTBLAzerPeYkTa2y4TY1zjR02UJs03StcfsyE52opQVQl+pjkEq9u2yA/8Bd8YE7mS8TCjmOHaTv+Uv6lH1dl9uLYXdnqXX1mdsQ29El9YNZTaWV6EXURu6fNeaGr1hSU4LKU9oXGb/rehWVsydmAMbKKhVh4ES5e95/ph5VYgyHbta9g/L+EA2/f2a1lj+yyszy5YZ5T1xmlCr7iD5ImHicNrf+2+Oaifgwngta5mUFkii43n4usETFzS9XEz0qC6vyC84asvhoKaadTF4FzE+ka8KlUcb5XD5r4FsRo2dHaOzWbvN8SaGEhdNcxPsdEwPPJoxB1uat0kZEnZmKX4jzioh/+YFZiox5i5xDdu3VMFFaYfTnDqtIelI2q4lpVfgDPEyoxS3hfWZnzKgLiNb83jyrYOiBODGDZQ8OTk6bXJ9ZFLLNUconUr0+1iq/nBO7u9R/PGJPHlQrUjzE+Fc4EeLJbSP9AE03hXYW5GvYOtso9pujvMsW+/5CiQS6osXaVZCQpuh3HKskeDp5vE4Uef+QozIFzanCqJSlC6Qw+wSu4lufDiFq73brXv9RKVdil6m/twfo/qDhRvNG9RP/LQk0k/7n8u55XKI7kgH+HN7BmBJZ1Te7gm2bW38Rc0qa5spkce7zh512qevwsduooCHuQ2U+OZp65WZY0NjfwKh5YciEvY0Pc0nvGrZw+ZW8wWzYhZE/500dumUWHNNp4Zp9vmhFfMCrB6sP5fpOkmYTf6d1j6C0l8vRnj9rG8LY92NlymjLLx3vKsYexzUk2TTWVsM2bOIU3mHWehH0vBeOLEMJ0No1h2Bktj9TlPq2uLdGuNvrzpw70+u3rbwdXu0g+5q2XN5U1pboStkF7vzvWKoS9aumaJS6rmRt46eew45YC9b56KFpZzSyAOXuoGS1kD8kfm8+6NcQ8LhNcnLoUKF0dClVCXu6cWDMhthGwJWdkBb7yr+2eZUreuRGLMVfw4E4CUWObFDIL11mnNEmdCyw/IslrwPkT/o2ZspjK2q1d1eZMePTsslsc6QzpNf1SgVuT7Gb10Kqyqg81GKwrO0F4XO5ikX2C3tpJXPxsFdVxmhIbzQ3gTGFTX+m23F5XK7n+p+1UhV60EvSQJGsvbd8lsilm29c9+2IkGwuj9qJZYJe0XwFZvnb73tnPYpFeOW46r7XmpPo71iz5QexeYfdjJrRH0FzJEVGJ1w3SeM144wDJTzw0kD4HjJMpk4DT63WDuGKRyLCd060n8ugDv+rEyAzW3g9g0sISgcu9qm1MG/qeGUdAs8ZgHFjGQAgl+IxzmlZ0P+Ngq2ujFg7WYrAShhind/6DkpIg+hbQQxF7M3rQiim9m2XhRji7l1Le9cuRCPccCh/TUHOvOqRwlczdW7HvoxO22A+2KY9xYB49gX2T+LS6RCeoHqtvuSca6fBSgo5U7J0qaGSf4n9IBjZLLxsZcAsvmb/+6P/5RkoaelFL9A4/eJTIxxE2OCioWEu3xc6qIq9hYLp4TgL1jBNfSufBIenDa1PTXiZd6hge1x7/o9ww/9J2i3SpOySRdBt+R1Xwep7L01PqGV8fT7F52y5v0ZX/GLeh7VTbxYP8loC4i49r2Cwcm5R1ET7LeqYueSFbtOfc/MR9DbrZhLqX0Wb9lZRUIiTa/cOjzJ0oOfdIMrJk8fy9FRnBALtNzl2CNOBSNuNZtqcqdvz9C2gL3zFdM9lesCsIavRYKfcA1O4cQiHzVHb/rEWmpnjzKXT7XzfRIE4D691l5U3UhmNwRSuJXFuMvf6eVvJQQJ0TiKFETWxf7Z63OczXTsDKktp79s77KBhnL0Xm4BJ2eXSZux2RFWXuU9VsrbIye8ZlNTVbVMTKejbvzAVCf2s2KXzQt+4qAX8EK2HtTKOHFvqN3UDYNSi67KwLcFmpWb58KPqWnz70S8rKNjyO4xvgqq+5cC7fddfgGHbPa4pNBGvZ5vE6tdKh0WkFrEQgJZqs9uaZgvvdmylpjqPKQd0rEQMKr2IjReid0aK7pxFIZb0YNfy79LpKEVZX2U7K8TTOIDAtq34mEH0cNmYxzEHIdl7f2bVIJ8mj7ZZfdmYV4RP26MBmlUhwyVWzj3zqR8vpIA6j8/pc+6w5yK7XtJ3g/m6HMdZgxAnCSfxyoHh2JMGkKfjDntcIH3rgwM8TL85KDEAzZWWRswqX6jDSZZfNRmz251XRHL7JEzGadhBeuGRExM1RvPI726kaB1Vr2r0v5xhzWSqopNLSe2Unuebhw1XU5ctVnKihWY4V5OEllhglLG+iAu9GKy06LLbT0BM1+9FWDVU4v4pnBWEVZlvoR+U7ad71B3jsoON4tlVI9GnN7kH9vy/xGWfAOr2vtlKwtb3jTiv7YmLu07tOUrivnEO1k3Qkyy9zT4IDJ3N9Wcj2tEXTyNA3fFrnw7ELHP+BfPQwXeWJzn5GOJSCoHxidJimWXQXfc5F3U8UvUIRUscF0nQ3zbS5IWoV79WYqaZK69BN9HVQH6K2JTgmPyW7Tca6L1X3p2Bv+7Qj8SWMoXYJjjSS/32ad5ZieG99m/CmtJ9Z+aAd2XL1S2pBczJHAV81vo/ST8s6zH/go9MYLhvVMeu/KYnRZ12HeItIpqKDlu+HbGsftZt4lh5g50OCwrrUxIRX+DCVgxUuyrN1geS2C18j+Z86MS4vWAm2kvgTj9Fw2P9joVPLlyd66FzHbEnSJzxiXYlFMA070zaP4nwp+nfY+YG+7XzQcmGnr7O1SV+1M/dql6Pueb3y6bgUrkddvx5r/pfUMmsgnBtfULO5dd8rT7BOrz/OAB10WzP85v39Wto4rUc40bhPBGagPgbJkm3FzRAwsxKd+J4L0963eAnUFGxdOkLaaMTuNaF2F1aETIlgYezg3ZIi++GDndRJQ8mFp5IGbIzTULXIkLR35wQsbNEK6v+yEzNmUGsnInFNTOE6ysy+UXTSSOeX89HHSz8jpfudoyUU0PEOpHDg7n2LCcOnIrTgMgrTcflyjK9xCswzed85Qr5/NEYbeQ9Tjgsd8XytM1ATrrtMJBLAfIBz0WubBb1IKsbaAYezj1CcBOZX1RCZzIFOVUyqem8+E6O+cbIIfXbwAoJNFkuw7TBNC+lOauKZyhPmGBYdD2g2DaJyW0gczOSu3U6MGDSXbqQ9UxWGxJpGS93pbKuNaP3oCjDgw9FidE7R1B7tvmGKpE8anR3ps/pkxvo84WN79LOZGvl6P34JHQ8I6brUahAQI/FZln8TtZutyHjy4xkbPwrfvnZs1aBHp8cCQ01uHfDBGKCzpkxfD7SfFP0Yn1Z1FDC1G4H+R7IzfTLolCocOBxwiyiVMR/sh3UtSvENf3qHskmTCmn118qxRuNB4lO2DYMTy9IzEpVOPWw3b3pXRONpapZ8TRsCKRw5e5820v2tJVJcagrY1rQppePQTMm19642U/Xeii40jFx7OinjmZV0fOQtb6Qb/D4pTuIMbDYlbZEkrjgzeF9qJs8Z7EIPas+JnToq4Qj3cEAjLWyRzkf0xSluCNYbOrId/oFX2UzRx7NddLqxe3S6AyedJxpPa1JuDRP6abo470afqoME3+jWx+ToWXj1tqoD3UHtXNR5ws3o+ThAg+gH1N5G4QquKLamQjdQd0BHtDnP4jWjPMe0gVkL3o3H8PgBWeoOqXXWAAnMWm84IiLd4K6d1/30F3dQQTwFMO2Pnrvze0804flDgjNhal1IYJxnsryYJBhufDHf7RLqV8TJM1aHf+CtHAlH/Kt30wLeVRh7mw1E44daKNxm36k65g5CohtfzDc0+YYrKQBZCeP2bfEwlcmPX7DMCFH10jGPzfuML+QolBTvUXhgjKQP0bpuLKWNOcayZBhnGeKLZYQp+1bLSnWh0gftJCSQ7TTV2zYSrv14cSy5vT0UQ7i0pYVwbMPHXtBb2OxE13QyBjxJtbgZgpkpw934eC4WbybmLR+78n1jYBkauXbVhQ0jyMyEcMOTCZQebpznQEGS0oziqzkBLRzgunGhQTKWGpK+4Qy6pt6l60sbPLbVE2oTL0OIslVUGqFYvHdjARoGrtcDndClEUd5pbUT2+9Wohyfs63m01FFui7tZ1BV7S29cYfQjUZUHb5Vh3BFZcXp2D+vha92gTHAx7HuVC+ZYZ+zjIdkYSmbMLEb9+89Ub5qmSjw7DG2oqg4LuWDzwS4FbWDhWfEC9VaMAvBrnRvV97o/eR/3C+nMphLkPta26RIGAupaPqLoDr1pRKmq9Yc6m/weipf6uO9t58C5/ry/CuWecK4sMlwWlMSni3TN2Pg9QNyZw7ONOwcVKXQCubhPSKMMmnZiNG7lKDgm5dV0Ps7gi/Wdky0xpBpiVKC8vlHYnxpBOr2O3PM7e772IDHfIieoONOZoQHrz1BC72HKqhXp479L3kqvs6rSKfbG4HIu37PZuLYnJy8AeKuMEhZ+ae5vKzGxCS51LlCb2KvnqjAISTtrTWF7dvfoTIfMyzjOD1GmmFKUMSshW1Oi7fBOR5ufdKFn3Yxb3nvRarfJcL0HTeNf6ngXh0P+rvnGGmzFtLMxlw0Mm1VkInG5uhXf1LXRvySvt4u8Iiuxh8Fcxo1X0ElFOk84/VD8vLqDxC3ikYuPqF2uFz/32yy555CYEUPEvmkrTGt+nNJm8LPv8Blk8FyvEOdKecAqkJIxa84MVcG4+YacJQH9f0OGOssWmfOEEk+9sw1cN82grdzpBR2Oe83r5IL+y8fSubiJfAfVgTJpREKvmBXUkZVZJ/v8c8H6SU0cOqqSVSzC2eqO3Dkoi2XVf9AYBrMhPFQlTgtcWpAeGwtTlBUsnIlFIFEB6tBERSLQCw4wRe/SK+DEUh0EIBk9WgEwBONKgh0MDgC14PzRIRHQEAZUwkJ9ApliYYHI4FEJzW4bzK42itkpqlyEkh0TKf9t6i6X0NpXaRoXjk/rFDO88uu7d4Vy7uQ9tfp3UdBLtjw9sRmDjuL5n1QLacmPX810l79GZlQ3tqkB6KWEByDQNECyngQUhNjxGq5JJdgRpLuWGjBklcxu+TFOh/1nzA8bHu+irXv2V+7lpd39hgA+9gBnHbPq5lRvFTJxxmA67lTlXUzUDXPidlVNuAuMCHYM9sRAkW4gsEwZiYDIgvdJEL41TH7B/r7Sb9i4klA04uCN+PYX93N7qbCwvfew/+KqSxm/b2gWXEKsHbo+98I5bQFdYinA0hgIX2wFWhgMV4RrpVQgBCsYxCFIM5qXPEaLN1NQthgmYwGPlhMbQUlGfXXMBKGtcyfIFVQhHU37n9VVn796zRhMdHGetQTLay72f3yTPfBS4hhMXWvsaJv/4o4aVincG79t1GJR6qJEDRRQRwWIn1QQR4W45V/iQoMsY5ltIazlSQ9qMQyM6REc5VF2F1pCLAEyqAMciE/sTihCOch0pW+0OCYO6fspD80+DZlgH2fB++Lm9Df6hUNjlm9erK3ZzT4CGEEuomDq+Le0SAejNfF37gw25p67N1YLSUS9ZsFu/igFX2lwWDKBNW5tdBDSP6EGe01DUIrq1dkDPJlhmEwuFW+7ZMjDaByFQuO2/veH6q4XH0ap+y7cPi/8QCaq+MmTmxuhnN1d7X8x0uS5sX5XHVWfOpXnvnUB+uryUvoahvMoH7TuYE9W2NETVcW/M6Koq7c2kqe6OVWr2+J2kMq2ro6VC3/p8PgtcWilfryA/Dp06v+/KAo+Ogmlzlxf69+gPUcUnv2g5LAMGPg4d0P4oH8xec86ffwB8dvtoi8/EFJCnwRDaG+/gCzFlT7+4OSZGjQJm5L9yvAk02t+2pq9TGIWj6tgGEwDLKgNHFQlBgScfCPb2rSPhTxgvQGeEMv4p03j1uy5EOXZsTxTvcQ6eiIxENsxD/Vy09/4oX11eCnI36Fru/yEXfq/vkqIvErp22w6Zin//+GhLsnunI6f+Y4vufeZUNQnqseXKoBZRPv3rihNPQvddN+VCD/IuPo+S+UVwnL2LlDHZ0z+63npEru3WQP8/6/v7CdaO+etY1vFUFr8SFCnNJ2Rhqm9Fw9opuO7kpWs5GZ3eeFfHuLqAo+vfcVs2akjtNrGrA4xZ7SKJHyL0Ob5ZRD003bnmzr+15AwZTqLUTrQHhLW0m3utvJMRZujSgnIAqlkiXry6vYJ+mGTor4rI4LNagkXzCAtMItW1PYS2OoRi/+ES9Bp6dCK6vvC1P9+e94s063QEVvcLi3hOizzeS63tnQkR7gRK2BjtDEKJu33JoApR4GkhgNKdvswHZsLG4P3qJmkiuVXcbGtbd2IN8AqpIGyJZ1Bkdx5mWYyHnTEe5GtJm+kVX82uv7DueupMqaUSrfiR2JM7fE1v7OoJX5pX3xNntTKyj7tTaAGRvvs/3ktKoKqNKwct1TmqjgFmeMKM585A+Qt3vVobwObkfDIceVyCzu5kifI9OmHm1mDOaR9SmMD9+OfVnxVYl0sWnHwDO/D5hrO949Zaav6i/R14Nj4yx+JP+F+2NgGzufDGDWKatU5nVNK3pNG37WGub4lOJ9c67ctc57N9EMp3JqBCG6rPRmnQhxDPUQDRK0CVPAf2Y+ruMDVloeIzmoKZnZN234ZsVomltXCVzOCtjj+l8r4ykeTnqbiJ/RtV+a/N4ebOyzMv4jqTHRbRxmeHsCIupPLaZ1fAdCJ6ZsgntBV1scprwzb5Pb/IdW0vnHDqC628i47Kq/vZZdJTOKR2YQtKnvziPCMmEE9aSrHg7XZXmZdvRZDtYMbnF4y6GoLIGxcLNJog011qBlB+M3yko1xvBls93acPpAM/mMtuwKYrcw0zCdzzur0TFakrJhQ5imI7TCyrJIzeYorSFTWvHh+Pe4vTDFTsSXZNVx9x6O/+I5KXC9V9CKh2ql5sf0IdhoLMRWdl51ISxaMN3cPkzbb/T51SuJ3SnY6XqQoLTJvWzUkt3oolFfr7+FW3z8THAloV/Vp9RBHdRsQKxbaRPv5Dvgb+XNpQbqmW540MVrXCXkwuzDsOmD7N55XN2lkgk2inCUlRld/NGVoafm3k+q4STTD98VBWN7Dmr8h/xvtPdS2elVWvRLUbFAcVEd5j6qvkM+WI2ZWzl5igB5hVMPRDV6Zi++NdUYn36Kpn3JTAIayRUyl/HJLMbMDx9FlTAXP/GSIYcZgaHJoYsyL308aV6n+zozDBVTxB28zqxpX1FYlsA4yJglyMHX07Q/PI/AYnXDTEHHQHZEhj1jBZ61gIB+tgZL3saBN2QGPAHb/mRyvOshidOFFck6VU7rrnr9j3pwZrSLq0Q7sWHtSVjd6v0xgrhuh5Jek9QcjVpCj61716laLr7zK2tOJ84gg1nzSb1rw2MkzNlcpLher6pHTBLnRvaKBCXZSvTKmDO0QWkQGKZkSGKaEhg3vmDA7Tao/MD1XDFcbq5CR6b26usfOf38cAkApvox51pSp4QrJPFb6sGlno7Em0ioWyrvtMlgzeO8piL3QqJwzFVLmWotdy6pUSbXMsn9/AhnK9KNSgJepfWmRzyo/Er5jVrmmb3VfuntuEtpZtYqwc3qpjXtclmtmuqZSbgBIU+qB8o0M+xO8cm8ms9Iq7mcXW1d3ibm/5C7G3NKhNsCveYhbHB4HVB6pfkprm13j8WtL+26luXDIS+Y2SiV5XNDTpXh9kI0daCnbMsb1Xtb6RLUlnGN5N0n8GNsPuq6uWYLgn/U/RNfx8+3OfKeTbjQt9UXCmlOYgjfc+w8DbNHkuPzHJfGb15mT8Kjagkn/yMEXogjleSK9o7Jg106r4OH8seOVrL3+GnwR/9efkQh0Pfugji9fDmNQKtl/3m9T+J9KoHa71/YN7/59ASdQKtlXSwu/zd62dGyj7yzvrc/r0Crxtdq7HXDSCq3QEpvu/Wx5RcoUosE71YJX5RTA0kyUHsw+itLEg20WvY3CmsF2UAIIzIvlAr66vmW/Ez1TFIMLj/i7Qc9l5B3f3pPx+j1qDnbsWFIqmbYmn/ZFRo/Lc2y9j4oGTUwKIbsy4vIvHsE4SurTW9bLYL9aPT//jfj8B/tIO/B29/yh7YiGJB+/C9//fm3cCDtbuCF688dNLEg/ay+c/faGtErIoCQnmuaDBTSDvK2Px1HAkPaWey1rqcZHNIO8jb0eIBIVwvPzZMO8mKw1S8ZusgHqgrd8mL5g5c+Az/8DHTDDrw5a2npw6b3cAxK95d07R70r7x+GTkr1h5AQy2uq4U3V+Gjm7f8+Nzg8oLmKjvBLcCMkk1m1ISKz7EeUhKWTqOafnSZR7AEWc5ZGiSQTXKKmt2vkcJazD2lHETH3VWkdIdYdXabJpwnZzTPouBZUtlEssoxJzzQrUUWL/MEm8XGi+XjImf2wHn7qcAR80wytlur3+mXKKNGLbnDUFto0Zhee+1NWmpiKZmDujaA2paviuammvubsJxBCLAi7bgKz2E6b0XrRDtVn9z1OG6j2yKEGwO6C8DcxejESRTI01bi8bgNQvX6LLJaIRMIZ194h0liEl3c13Nyy39ypPooMkthZr+/XsB6oNxjOcfzPllmVVK10k5lrbUo1hvLrvbj7DsXCIAKW3pnxc89L+5Ov9O1dgCe/PIn6avZPH/5EFP/43l4uo/bcQPQHiyBfaEBsGVuiwH2/v94jv/eihnw7W2y0uB1bvy4eFWTZ2MykJlVxxOX/U0pW1IjTES2ic5BIXWdKhSekUQtEYJT1YryIFu7lnVvABUQDCFgC5kQBZ5w7ZnEx4LiqGWENAJ46VjorQG5m4UK24Oz44ht1Ru1fGiwiwXfcN8catLMYc6ztYDHvNb2jiOH+Zk7FoO4zNxHgE4M+sTb0nIrgm9KUIaikFGOxEfyUsPqOs4oNdjh66EqCxT7CQewLvEYDUtCSEJwIcwBKB7DEYEeS2piqreRuK07Nii2uXAZDIElGU1I1p+ulvXNIUx8BbnG+DU/kmqDaQ1yK7vHJgcqVrRkcF9DsrpKcM5RIsAcTrThiDoM88d61hmEPi6wkiVVFzmXPWmM919GhfjcVchRuJyHyXmZSfQAMmZNwaaC4diRnfOaqtCD6lTsbzhti94ONDUEu93varyc8TN3aFZCt+XdjUatxyQ6ClkgWVIYIDaTemzbwU1MvOiksQBT5UOWuOU5tx2Cix2NjhGczf3YbcJIEmgGGsvKE+rdjT1pce2oOHFVIb4uIMaSmqx2L03EV5+m3JGUWE8Ejg4JHJYIHFUIHBIEjowEOjikBN4Qw1QqGOYGtgtp+rpwu4on0V1zsEl0xC08TYa1r+tEw9MmD36kQYbDBx49FZAlEu9BpusZavo1Q0GvsL/ME4HYXD9TDIIxXOC0M4V2mvvkj+f6YN9tArcGcIUJHuwSWe+KsJvExXVYFoXeUGRtB6nQic7pPe9ylhZCc5th+4u9vi9ti8HaOhlahnNPm7YAdBMkbwYOWxGPS86SJKcCMXknurW4ISWfHNn4yeY5U8/Tky37t6Y+C/UoGe1nihXR9+joXImskzaCntMj84Tv2FXGsyCin1s5vAeFNC9P5OY19JswjttUB6f63mbP7RFv2sZboRwlSfPhdy3Gd7+x2uPG9gJ43V5bhy56fUWexQNCE2hR+AJFWszI3gaJWR9ordtpa7Ucg2dPtsBR6mBZC4dFbP/C/8Qny1sYD8ya4PABNMx2KGjg5VeCRL6aBoajLZ9K48g7E5tG2RaBwWPMpjnsSK7eh//js4ntpe2uk7izYugVGz1PyzsqK0JiLHwzk+AgGm6VRqoIX13GBZGLyW0pk/Z+EBi20XssmRfpF9hS8VqGnBkTmyaRC/0KxJSKGJ+O8zRyqApIm0OCYsy66E6B7UmyPgVhU3Fg+CGEha0brm9z3bY1dT4G63JQp3A1XWvBmyCt+ZDOMrPSogEOtDMBuvw8B2PyiOes42m5EHtd+F3zO0kqBKPTKPntakUVE0cvwJXMMtLp2rPrle+U5jfp2S2Q847AcCpfoBbXsLGS4wnfrdyhDHeR3bFVwCnnMSAJBMCFNKBtSyp79NwwY1HEtaGFcz//QJN+3WWlUis7+iSNTlCyu63M09sxMbl73rf+wgcdEdyk5WOC7EXDfzTVqJId0iZDMG5FQOtj90fyfknW52FhOO94ZdQ4QpJSemXukKQp7t0GEWFfvTDSUFjoRgjJ6Y6TDFcSimGWzeUKTaic+92HM5c1U4h+D9sp8He2fQEGmjIGUrgCye4YJDGFlA6IW0mD8YIUHQR4dhyI1QxaThwR4DxyCwNhaMKCQaEVliDUJUKZ8JpPvDg+J6i+FBl/AfDOtBK+Rji9ZFcbcZVHAv+PrCfLZCgVmQsv7dlZoHQ4nBVIWJbjGwx4CLehGW7eqL7JiFG52F2vJ7pFEQbKMWa+/T3R6BKKpgAbGeA85/ZB7Uo7TY0teZt5lXjIre+oO4Y3Muyp+TS4h2aRMY+dQPcoJIQE8VcGfZZBjQknU81lTnxE1EpyPr2P03iyd9r3voKhlOrTxCdHbv0yANK6uWR2x7auaeBW23kxyYCn3OS+mJBFg8devl8RosDRvlqwtFb6u10SE7U6dq1BreeIlXuH16BkoseuIUvC9thyJnjR670NEJE4TZI4JoMLCJgjVZHISBTRhUJpRoydpnnC0WqB9v7bAAQ8hmC4Zl3BKO/uhGJs2xH0gyB7WGQA70FLml2p/kNIkgBoluXCEvd1sna9L9oesjypq/bjqHtO8UFJcUCoSrZ4Ql5WrMTKsQNsYNnB9D5lokGsVzlz2mvnAhFaPA2agtVp1oWafd1dkr5h7bzxOpaWo61D6xaM0M41ccHq2O6ypr67R9KMjfMEWjgFCATyx7qeF/aErQIR2tqylYh7RQ5VBbS+OIdAhONBa3qZNOvdsd06wQUIQxkbewbusdQP99/RaRrqm0ELjbIgG31/EtjFS34s2WRQFot6VMbKYqNUn13wRHLk+sAscESeAW04WWFOnX4W3pqjPa6OSD5zCYP14axxBjUhtP5ovxEIwwOgDfe80A6j/UWQ9S0+7UMf1Fhc/UCE+qDVLwWr429vaga7Oz60wMTKeAI1XAFqvGddldyn9VVwTVdh89qbTwHnWLKbldW7B2gfDHKQtIy6gglvjrhXBOG+UIEjEUDevsBdhf0g0Q4ToFX5uSy0qJV6aSb0Kqe0YeueOMKaZUMv84K4Ajgbw//NYwHfh7LAmaLHQ83AGy0/2ny0Zmjv0cKjd0FZokdCX4balvR7+gmm8EJ7j+3QW6AMkbAergvuBgRaeM5N5Ng0Lg1sThgXLOZlkRvyIh0CF7Tu2Ep4TeELLdM02fyiFnFATUK/iQGfDRnLy0vW6NPJvxc1bVxttHPIB32qoU0GT4MDeiT0Ndyqn8Izu1iBe4w7ngV9hqFZWB/9E44CPQ36ELw9tc9YH0O70Aaj78OPUSLrIgdsLy6pH9w/SEI/hJ4FR449el7oo9DroH9Cj4PeXXLyv2b3mbj2qFK7ta5YUO99wrHhPtbX7slYUtK3D9iRhXXBE/nsKslbG8mXV0mGrLzh5Okz+U0xelwDlK7hlXAnlS3gnf7oPuP/NprbgT0xtK9JCVcCZU9rWrxIxo94n5D2TpJBiagF+MaYvccuiHkAEkLMMJitf/vDeD78H+Zws75/6nvShJLusra4eriLNUjyErtU7EVJAumSVZJtSX94HbHfmpL2l3GGQr0xLrm2Yu+Mu3+7q/U/XO1eac0+kHp9lPmmKO5P+27fg+MD1E6u7TmAjs9uPs02g0AtFl4B9w7etCNYDQYswZSxB9Cey9puTX13pmYnD146QVYYA9Yl2+3wfrE+2XGnoxZ5hmKjdLuIpdgS94DxwVzPNcL2tDf/y/7ZViIIgE1sl5xqJQiW4K4YrRHI4T1r8IMFgG5LNn6wIEyAvtfpNwtwph5mCa7UL1lBcJ9trdlAbDuFrZc12UG5x9iDM3mcA8S6mhN4O/N1BhdUNu5yhVzQTwCvRaAQkxAciZHBLJfgsCdyCXgDJeGIJ6fqb4CBKxOrY+jA6ysYQO8zTOD3T1SA2fMESPpV6hj3GkKk3UQDWUtE6Jalif4cIUYyz9LCcH5KgnTuZAh/djOCNdmMYU46E4SzjimyyZEjmuA0m9WA2YgHMEeaN1ggzN9ZopsfskKUu83BOBdxRpGKuZgG44K5Pq6YOUmJpSK/d4B7XQjvocngW4z3BnCJqCPl+w41J6QznpR1vjviU2i8pH0K+zWlOLId+NXh1iLFpOa1E3BSYQz6NQMCD4uSBVQ4s3TYe8RhsZ/aMFGrdJNOmM6hPgKxXx3X/idV8BiQtUAVVFmjWtFgQHlhYqdjC58Bv0Po/OQb8WHBNyH4uWAkcFzhxT+misj/IdmpooIqUiSXkg+/2sXqWZNKXxbwLSrB7O9zFf6ghpumfpr2mzaScNHrM8YyW+bM4kaqtknbyfMkeftu210Le2fmPF2K/oIbTBuxN2w4H5XZsn63zkgpdb64bWTu2zdynn1SUBst2XDb8nndLGfQVZo3uxC3viUlkUESNNth3lQfPDUbDSngA0sWD803/QtbdWDH7C6IbN+Z8YaSK9lSCmY+bYU0VxpWcl4uU7IUHVaSd8+3GvmzA6bBQ+abnS+p6thD3t78Acdk9cuKSAxjY7VGbvYazgc1Xhx2xwqpxDiaOd+pPci5F+6gQ7pM17W11JISaAhyiTU3H2+Cm2rZCKDySyeyetkeM/MaKzzYccJ2Vrut/QFt5MrkAyGkO7oW6+bqDrblSKva8KX6K9CsYmqxhjRqMuWa/IZcryC1SkpVy3ZVKSPlAlIqPaJYQgrFiMoXT84UNbnijHJBcZdyfq7y0hkopDKPSGaBJ9IenQJNPHU3KpVSZAIKscTtokljiSS/Lhy3hGJIMOoFIvncH7EQkZsyHvZ8IdbiDR5W1gVnlLX+iHL5weL0380RsNgDt7O5gtWbUmY8m5u9zOT5lPFumbCIXhd0uqK0hnkaY7haWVTy65TSgh0jCvEZkdXvukJaLyi1IpZFpSuUsLRSCUohpVS8CyfXVvMWHzSXnlJT8bYa4x15VDcVPS/wW90QqYSdqXvO7O6BEbWpqBWO3ap2pRQ5MLCPHdjLuvOePC9u2eOy4paDeNnut45v9xD7U+q//7dm2T9ENviHnMPnUIX9jCnoZzaWxs4dP1CMS6yjH5Kb6CONQn8mFPwzZAUPuDlWx/LYf9kiC/4BX8pxV/4DX8dx2DAHMgdPgU/cpJCfEQX+TMbi2Dn0A8a42Dr8AXHlizboLN7ee6amH1IHp+2/01d9cwf7+yPUKsWcA3eKaIX1gcG+e7DVnD328xdBcs8o/eomHig4CnP3BPL4KMAle6E6A1NwkQbjIUkpJvATJkYu34VghPeIbVgNEF5w7jMjIhOZz6cCAfBJrBgA) format("woff2")}:host{--copilot-font-family: "Roboto", sans-serif;--copilot-font-size: .8125rem ;--copilot-letter-spacing: .25px;--copilot-line-height: 1.25rem ;--copilot-font-size-xs: .75rem;--copilot-font-size-sm: .8125rem;--copilot-font-size-md: .9375rem;--copilot-font-size-lg: 1.375rem;--copilot-font-size-xl: 1.5rem;--copilot-letter-spacing-xs: .4px;--copilot-letter-spacing-sm: .25px;--copilot-letter-spacing-md: .15px;--copilot-letter-spacing-lg: 0;--copilot-letter-spacing-xl: 0;--copilot-line-height-xs: 1.125rem;--copilot-line-height-sm: 1.25rem;--copilot-line-height-md: 1.5rem;--copilot-line-height-lg: 1.75rem;--copilot-line-height-xl: 2rem;--copilot-font-weight-normal: 400;--copilot-font-weight-medium: 500;--copilot-font-weight-semibold: 600;--copilot-font-weight-bold: 700;--copilot-font-xs: normal var(--copilot-font-weight-normal) var(--copilot-font-size-xs) / var(--copilot-line-height-xs) var(--copilot-font-family);--copilot-font-xs-medium: normal var(--copilot-font-weight-medium) var(--copilot-font-size-xs) / var(--copilot-line-height-xs) var(--copilot-font-family);--copilot-font-xs-semibold: normal var(--copilot-font-weight-semibold) var(--copilot-font-size-xs) / var(--copilot-line-height-xs) var(--copilot-font-family);--copilot-font-xs-bold: normal var(--copilot-font-weight-bold) var(--copilot-font-size-xs) / var(--copilot-line-height-xs) var(--copilot-font-family);--copilot-font-sm: normal var(--copilot-font-weight-normal) var(--copilot-font-size-sm) / var(--copilot-line-height-sm) var(--copilot-font-family);--copilot-font-sm-medium: normal var(--copilot-font-weight-medium) var(--copilot-font-size-sm) / var(--copilot-line-height-sm) var(--copilot-font-family);--copilot-font-sm-semibold: normal var(--copilot-font-weight-semibold) var(--copilot-font-size-sm) / var(--copilot-line-height-sm) var(--copilot-font-family);--copilot-font-sm-bold: normal var(--copilot-font-weight-bold) var(--copilot-font-size-sm) / var(--copilot-line-height-sm) var(--copilot-font-family);--copilot-font-md: normal var(--copilot-font-weight-normal) var(--copilot-font-size-md) / var(--copilot-line-height-md) var(--copilot-font-family);--copilot-font-md-medium: normal var(--copilot-font-weight-medium) var(--copilot-font-size-md) / var(--copilot-line-height-md) var(--copilot-font-family);--copilot-font-md-semibold: normal var(--copilot-font-weight-semibold) var(--copilot-font-size-md) / var(--copilot-line-height-md) var(--copilot-font-family);--copilot-font-md-bold: normal var(--copilot-font-weight-bold) var(--copilot-font-size-md) / var(--copilot-line-height-md) var(--copilot-font-family);--copilot-font-button: normal var(--copilot-font-weight-semibold) var(--copilot-font-size-sm) / var(--copilot-line-height-sm) var(--copilot-font-family);--copilot-font-tooltip: normal var(--copilot-font-weight-medium) var(--copilot-font-size-sm) / var(--copilot-line-height-sm) var(--copilot-font-family)}', nd = ".items-baseline{align-items:baseline}.items-center{align-items:center}.items-end{align-items:end}.items-start{align-items:start}.self-start{align-self:start}.animate-spin{animation:var(--animate-spin)}.animate-swirl{animation:var(--animate-swirl)}.bg-amber-3{background-color:var(--amber-3)}.bg-blue-3{background-color:var(--blue-3)}.bg-blue-11{background-color:var(--blue-11)}.bg-cover{background-size:cover}.bg-current{background-color:currentColor}.bg-gray-1{background-color:var(--gray-1)}.bg-gray-1\\/90{background-color:color-mix(in oklab,var(--gray-1) 90%,transparent)}.bg-gray-3{background-color:var(--gray-3)}.focus-within\\:bg-gray-3:focus-within{background-color:var(--gray-3)}.hover\\:bg-gray-3:hover{background-color:var(--gray-3)}.bg-gray-4{background-color:var(--gray-4)}.bg-gray-5{background-color:var(--gray-5)}.bg-ruby-3{background-color:var(--ruby-3)}@media (prefers-color-scheme: dark){.dark\\:bg-amber-5{background-color:var(--amber-5)}.dark\\:bg-amber-6{background-color:var(--amber-6)}.dark\\:bg-blue-5{background-color:var(--blue-5)}.dark\\:bg-blue-6{background-color:var(--blue-6)}.dark\\:bg-gray-2\\/90{background-color:color-mix(in oklab,var(--gray-2) 90%,transparent)}.dark\\:bg-gray-3\\/90{background-color:color-mix(in oklab,var(--gray-3) 90%,transparent)}.dark\\:bg-gray-4\\/90{background-color:color-mix(in oklab,var(--gray-4) 90%,transparent)}.dark\\:bg-gray-5{background-color:var(--gray-5)}.dark\\:bg-gray-5\\/90{background-color:color-mix(in oklab,var(--gray-5) 90%,transparent)}.dark\\:bg-gray-6{background-color:var(--gray-6)}.dark\\:focus-within\\:bg-gray-6:focus-within{background-color:var(--gray-6)}.dark\\:hover\\:bg-gray-6:hover{background-color:var(--gray-6)}.dark\\:bg-gray-7{background-color:var(--gray-7)}.dark\\:bg-ruby-6{background-color:var(--ruby-6)}.dark\\:bg-ruby-7{background-color:var(--ruby-7)}}.border{border:1px var(--border-style, solid) var(--border-color, var(--vaadin-divider-color))}.border-dashed{--border-style: dashed}.border-2{border-width:2px}.border-b{border-bottom:1px var(--border-style, solid) var(--border-color, var(--vaadin-divider-color))}.border-e-0{border-inline-end:none}.border-s-0{border-inline-start:none}.border-t{border-top:1px var(--border-style, solid) var(--border-color, var(--vaadin-divider-color))}.border-t-0{border-top:none}.border-amber-9{border-color:var(--amber-9)}.border-black\\/50{border-color:#00000080}.border-white\\/50{border-color:#ffffff80}.rounded-full{border-radius:9999px}.rounded-s-none{border-end-start-radius:0;border-start-start-radius:0}.rounded-sm{border-radius:var(--vaadin-radius-s)}.rounded-md{border-radius:var(--vaadin-radius-m)}.rounded-lg{border-radius:var(--vaadin-radius-l)}.shadow-2xs{box-shadow:0 1px #0000000d}.shadow-xs{box-shadow:0 1px 2px #0000000d}.shadow-sm{box-shadow:0 1px 3px #0000001a,0 1px 2px -1px #0000001a}.shadow-md{box-shadow:0 4px 6px -1px #0000001a,0 2px 4px -2px #0000001a}.shadow-lg{box-shadow:0 10px 15px -3px #0000001a,0 4px 6px -4px #0000001a}.shadow-xl{box-shadow:0 20px 25px -5px #0000001a,0 8px 10px -6px #0000001a}.shadow-2xl{box-shadow:0 25px 50px -12px #00000040}.shadow-none{box-shadow:0 0 #0000}.box-border{box-sizing:border-box}.box-content{box-sizing:content-box}.text-amber-11{color:var(--amber-11)}.text-amber-12{color:var(--amber-12)}.text-blue-11{color:var(--blue-11)}.text-blue-12{color:var(--blue-12)}.text-body{color:var(--vaadin-text-color)}.text-ruby-11{color:var(--ruby-11)}.text-secondary{color:var(--vaadin-text-color-secondary)}.text-teal-11{color:var(--teal-11)}.text-violet-11{color:var(--violet-11)}.text-white{color:#fff}@media (prefers-color-scheme: dark){.dark\\:text-amber-11{color:var(--amber-11)}.dark\\:text-amber-12{color:var(--amber-12)}.dark\\:text-blue-12{color:var(--blue-12)}}.cursor-inherit{cursor:inherit}.text-blue{color:var(--blue-color)}.text-blue-violet{background-clip:text;background-image:linear-gradient(90deg,var(--blue-color),var(--violet-color));color:transparent}.text-inherit{color:inherit}.text-white,.hover\\:text-white:hover{color:#fff}.contents{display:contents}.flex{display:flex}.grid{display:grid}.hidden{display:none}.inline-flex{display:inline-flex}.divide-y>:not(:last-child){border-bottom:1px solid var(--vaadin-divider-color)}.backdrop-blur-xs{-webkit-backdrop-filter:blur(4px);backdrop-filter:blur(4px)}.backdrop-blur-sm{-webkit-backdrop-filter:blur(8px);backdrop-filter:blur(8px)}.backdrop-blur-md{-webkit-backdrop-filter:blur(12px);backdrop-filter:blur(12px)}.backdrop-blur-lg{-webkit-backdrop-filter:blur(16px);backdrop-filter:blur(16px)}.backdrop-blur-xl{-webkit-backdrop-filter:blur(24px);backdrop-filter:blur(24px)}.backdrop-blur-2xl{-webkit-backdrop-filter:blur(40px);backdrop-filter:blur(40px)}.backdrop-blur-3xl{-webkit-backdrop-filter:blur(64px);backdrop-filter:blur(64px)}.flex-1{flex:1}.flex-col{flex-direction:column}.flex-grow{flex-grow:1}.font-sans{font-family:var(--copilot-font-family),sans-serif}.text-xs{font:var(--copilot-font-xs);letter-spacing:var(--copilot-letter-spacing-xs)}.text-sm{font:var(--copilot-font-sm);letter-spacing:var(--copilot-letter-spacing-sm)}.text-md{font:var(--copilot-font-md);letter-spacing:var(--copilot-letter-spacing-md)}.text-1{font-size:var(--copilot-font-size-xs);line-height:var(--copilot-line-height-sm)}.font-normal{font-weight:var(--copilot-font-weight-normal)}.font-medium{font-weight:var(--copilot-font-weight-medium)}.font-semibold{font-weight:var(--copilot-font-weight-semibold)}.font-bold{font-weight:var(--copilot-font-weight-bold)}.gap-1{gap:.25rem}.gap-1\\.5{gap:.375rem}.gap-2{gap:.5rem}.gap-3{gap:.75rem}.gap-4{gap:1rem}.gap-x-1{column-gap:.25rem}.gap-x-2{column-gap:.5rem}.gap-x-3{column-gap:.75rem}.gap-x-4{column-gap:1rem}.gap-y-1{row-gap:.25rem}.gap-y-2{row-gap:.5rem}.gap-y-3{row-gap:.75rem}.gap-y-4{row-gap:1rem}.gap-25{gap:var(--space-25)}.gap-50{gap:var(--space-50)}.gap-75{gap:var(--space-75)}.gap-100{gap:var(--space-100)}.gap-150{gap:var(--space-150)}.gap-200{gap:var(--space-200)}.gap-300{gap:var(--space-300)}.gap-400{gap:var(--space-400)}.icon-s svg{height:var(--icon-size-s);width:var(--icon-size-s)}.col-span-full{grid-column:1 / -1}.grid-cols-2{grid-template-columns:repeat(2,minmax(0,1fr))}.grid-cols-3{grid-template-columns:repeat(3,minmax(0,1fr))}.justify-center{justify-content:center}.justify-end{justify-content:end}.justify-start{justify-content:start}.list-none{list-style-type:none}.m-0{margin:0}.m-2{margin:.5rem}.m-3{margin:.75rem}.m-4{margin:1rem}.-m-2{margin:-.5rem}.mb-0{margin-bottom:0}.mb-1{margin-bottom:.25rem}.mb-2{margin-bottom:.5rem}.mb-3{margin-bottom:.75rem}.mb-4{margin-bottom:1rem}.me-auto{margin-inline-end:auto}.-me-2{margin-inline-end:-.5rem}.ms-auto{margin-inline-start:auto}.ms-1{margin-inline-start:.25rem}.ms-2{margin-inline-start:.5rem}.-ms-0\\.5{margin-inline-start:-.125rem}.mt-0{margin-top:0}.mt-1{margin-top:.25rem}.mt-2{margin-top:.5rem}.mx-1{margin-inline:.25rem}.mx-2{margin-inline:.5rem}.mx-3{margin-inline:.75rem}.mx-4{margin-inline:1rem}.my-0{margin-block:0}.my-px{margin-block:1px}.-my-1\\.5{margin-block:-.375rem}.-my-2{margin-block:-.5rem}.m-25{margin:var(--space-25)}.m-50{margin:var(--space-50)}.m-75{margin:var(--space-75)}.m-100{margin:var(--space-100)}.m-150{margin:var(--space-150)}.m-200{margin:var(--space-200)}.m-300{margin:var(--space-300)}.m-400{margin:var(--space-400)}.mb-25{margin-bottom:var(--space-25)}.mb-50{margin-bottom:var(--space-50)}.mb-75{margin-bottom:var(--space-75)}.mb-100{margin-bottom:var(--space-100)}.mb-150{margin-bottom:var(--space-150)}.mb-200{margin-bottom:var(--space-200)}.mb-300{margin-bottom:var(--space-300)}.mb-400{margin-bottom:var(--space-400)}.me-25{margin-inline-end:var(--space-25)}.me-50{margin-inline-end:var(--space-50)}.me-75{margin-inline-end:var(--space-75)}.me-100{margin-inline-end:var(--space-100)}.me-150{margin-inline-end:var(--space-150)}.me-200{margin-inline-end:var(--space-200)}.me-300{margin-inline-end:var(--space-300)}.me-400{margin-inline-end:var(--space-400)}.-me-25{margin-inline-end:calc(var(--space-25) * -1)}.-me-50{margin-inline-end:calc(var(--space-50) * -1)}.-me-75{margin-inline-end:calc(var(--space-75) * -1)}.-me-100{margin-inline-end:calc(var(--space-100) * -1)}.-me-150{margin-inline-end:calc(var(--space-150) * -1)}.-me-200{margin-inline-end:calc(var(--space-200) * -1)}.-me-300{margin-inline-end:calc(var(--space-300) * -1)}.-me-400{margin-inline-end:calc(var(--space-400) * -1)}.ms-25{margin-inline-start:var(--space-25)}.ms-50{margin-inline-start:var(--space-50)}.ms-75{margin-inline-start:var(--space-75)}.ms-100{margin-inline-start:var(--space-100)}.ms-150{margin-inline-start:var(--space-150)}.ms-200{margin-inline-start:var(--space-200)}.ms-300{margin-inline-start:var(--space-300)}.ms-400{margin-inline-start:var(--space-400)}.-ms-25{margin-inline-start:calc(var(--space-25) / -1)}.-ms-50{margin-inline-start:calc(var(--space-50) / -1)}.-ms-75{margin-inline-start:calc(var(--space-75) / -1)}.-ms-100{margin-inline-start:calc(var(--space-100) / -1)}.-ms-150{margin-inline-start:calc(var(--space-150) / -1)}.-ms-200{margin-inline-start:calc(var(--space-200) / -1)}.-ms-300{margin-inline-start:var(--space-300)}.-ms-400{margin-inline-start:var(--space-400)}.mt-25{margin-top:var(--space-25)}.mt-50{margin-top:var(--space-50)}.mt-75{margin-top:var(--space-75)}.mt-100{margin-top:var(--space-100)}.mt-150{margin-top:var(--space-150)}.mt-200{margin-top:var(--space-200)}.mt-300{margin-top:var(--space-300)}.mt-400{margin-top:var(--space-400)}.-mt-25{margin-top:calc(var(--space-25) * -1)}.-mt-50{margin-top:calc(var(--space-50) * -1)}.-mt-75{margin-top:calc(var(--space-75) * -1)}.-mt-100{margin-top:calc(var(--space-100) * -1)}.-mt-150{margin-top:calc(var(--space-150) * -1)}.-mt-200{margin-top:calc(var(--space-200) * -1)}.-mt-300{margin-top:calc(var(--space-300) * -1)}.-mt-400{margin-top:calc(var(--space-400) * -1)}.mx-25{margin-inline:var(--space-25)}.mx-50{margin-inline:var(--space-50)}.mx-75{margin-inline:var(--space-75)}.mx-100{margin-inline:var(--space-100)}.mx-150{margin-inline:var(--space-150)}.mx-200{margin-inline:var(--space-200)}.mx-300{margin-inline:var(--space-300)}.mx-400{margin-inline:var(--space-400)}.my-25{margin-block:var(--space-25)}.my-50{margin-block:var(--space-50)}.my-75{margin-block:var(--space-75)}.my-100{margin-block:var(--space-100)}.my-150{margin-block:var(--space-150)}.my-200{margin-block:var(--space-200)}.my-300{margin-block:var(--space-300)}.my-400{margin-block:var(--space-400)}.-my-25{margin-block:calc(var(--space-25) * -1)}.-my-50{margin-block:calc(var(--space-50) * -1)}.-my-75{margin-block:calc(var(--space-75) * -1)}.-my-100{margin-block:calc(var(--space-100) * -1)}.-my-150{margin-block:calc(var(--space-150) * -1)}.-my-200{margin-block:calc(var(--space-200) * -1)}.-my-300{margin-block:calc(var(--space-300) * -1)}.-my-400{margin-block:calc(var(--space-400) * -1)}.opacity-0{opacity:0}.opacity-50{opacity:.5}.opacity-100{opacity:1}.group:hover .group-hover\\:opacity-0{opacity:0}.group:hover .group-hover\\:opacity-100{opacity:1}.group:focus-within .group-focus-within\\:opacity-0{opacity:0}.group:focus-within .group-focus-within\\:opacity-100{opacity:1}.overflow-auto{overflow:auto}.overflow-hidden{overflow:hidden}.overflow-x-auto{overflow-x:auto}.overflow-x-hidden{overflow-x:hidden}.overflow-y-auto{overflow-y:auto}.overflow-y-hidden{overflow-y:hidden}.truncate{overflow:hidden;text-overflow:ellipsis;white-space:nowrap}.p-0{padding:0}.p-1{padding:.25rem}.p-2{padding:.5rem}.p-3{padding:.75rem}.p-4{padding:1rem}.pb-1\\.5{padding-bottom:.375rem}.pb-2{padding-bottom:.5rem}.pb-3{padding-bottom:.75rem}.pb-4{padding-bottom:1rem}.pe-1{padding-inline-end:.25rem}.pe-1\\.75{padding-inline-end:.4375rem}.pe-2{padding-inline-end:.5rem}.pe-3{padding-inline-end:.75rem}.pe-4{padding-inline-end:1rem}.pe-5{padding-inline-end:1.25rem}.pe-6{padding-inline-end:1.5rem}.pe-7{padding-inline-end:1.75rem}.pe-8{padding-inline-end:2rem}.ps-0\\.5{padding-inline-start:.125rem}.ps-1{padding-inline-start:.25rem}.ps-1\\.75{padding-inline-start:.4375rem}.ps-2{padding-inline-start:.5rem}.ps-3{padding-inline-start:.75rem}.ps-3\\.75{padding-inline-start:.9375rem}.ps-4{padding-inline-start:1rem}.pt-1{padding-top:.25rem}.px-0{padding-inline:0}.px-1{padding-inline:.25rem}.px-1\\.5{padding-inline:.375rem}.px-2{padding-inline:.5rem}.px-3{padding-inline:.75rem}.px-4{padding-inline:1rem}.py-1{padding-block:.25rem}.py-1\\.5{padding-block:.375rem}.py-1\\.75{padding-block:.4375rem}.py-2{padding-block:.5rem}.py-2\\.5{padding-block:.625rem}.py-3{padding-block:.75rem}.p-25{padding:var(--space-25)}.p-50{padding:var(--space-50)}.p-75{padding:var(--space-75)}.p-100{padding:var(--space-100)}.p-150{padding:var(--space-150)}.p-200{padding:var(--space-200)}.p-300{padding:var(--space-300)}.p-400{padding:var(--space-400)}.pb-25{padding-bottom:var(--space-25)}.pb-50{padding-bottom:var(--space-50)}.pb-75{padding-bottom:var(--space-75)}.pb-100{padding-bottom:var(--space-100)}.pb-150{padding-bottom:var(--space-150)}.pb-200{padding-bottom:var(--space-200)}.pb-300{padding-bottom:var(--space-300)}.pb-400{padding-bottom:var(--space-400)}.pe-25{padding-inline-end:var(--space-25)}.pe-50{padding-inline-end:var(--space-50)}.pe-75{padding-inline-end:var(--space-75)}.pe-100{padding-inline-end:var(--space-100)}.pe-150{padding-inline-end:var(--space-150)}.pe-200{padding-inline-end:var(--space-200)}.pe-300{padding-inline-end:var(--space-300)}.pe-400{padding-inline-end:var(--space-400)}.ps-25{padding-inline-start:var(--space-25)}.ps-50{padding-inline-start:var(--space-50)}.ps-75{padding-inline-start:var(--space-75)}.ps-100{padding-inline-start:var(--space-100)}.ps-150{padding-inline-start:var(--space-150)}.ps-200{padding-inline-start:var(--space-200)}.ps-300{padding-inline-start:var(--space-300)}.ps-400{padding-inline-start:var(--space-400)}.pt-25{padding-top:var(--space-25)}.pt-50{padding-top:var(--space-50)}.pt-75{padding-top:var(--space-75)}.pt-100{padding-top:var(--space-100)}.pt-150{padding-top:var(--space-150)}.pt-200{padding-top:var(--space-200)}.pt-300{padding-top:var(--space-300)}.pt-400{padding-top:var(--space-400)}.px-25{padding-inline:var(--space-25)}.px-50{padding-inline:var(--space-50)}.px-75{padding-inline:var(--space-75)}.px-100{padding-inline:var(--space-100)}.px-150{padding-inline:var(--space-150)}.px-200{padding-inline:var(--space-200)}.px-300{padding-inline:var(--space-300)}.px-400{padding-inline:var(--space-400)}.py-25{padding-block:var(--space-25)}.py-50{padding-block:var(--space-50)}.py-75{padding-block:var(--space-75)}.py-100{padding-block:var(--space-100)}.py-150{padding-block:var(--space-150)}.py-200{padding-block:var(--space-200)}.py-300{padding-block:var(--space-300)}.py-400{padding-block:var(--space-400)}.pointer-events-none{pointer-events:none}.absolute{position:absolute}.fixed{position:fixed}.relative{position:relative}.-end-4{inset-inline-end:-1rem}.-end-3{inset-inline-end:-.75rem}.-end-2{inset-inline-end:-.5rem}.-end-1{inset-inline-end:-.25rem}.-end-0\\.5{inset-inline-end:-.125rem}.end-0{inset-inline-end:0}.end-1{inset-inline-end:.25rem}.end-2{inset-inline-end:.5rem}.end-3{inset-inline-end:.75rem}.start-0{inset-inline-start:0}.-top-1\\.5{top:-.375rem}.-top-1{top:-.25rem}.-top-0\\.5{top:-.125rem}.top-0{top:0}.top-0\\.5{top:.125rem}.top-1{top:.25rem}.top-1\\.5{top:.375rem}.top-1\\.75{top:.4375rem}.h-5{height:1.25rem}.h-8{height:2rem}.h-9{height:2.25rem}.h-full{height:100%}.max-h-full{max-height:100%}.max-w-full{max-width:100%}.min-w-0{min-width:0}.size-md{height:var(--copilot-size-md);width:var(--copilot-size-md)}.size-1{height:.25rem;width:.25rem}.size-2{height:.5rem;width:.5rem}.size-3{height:.75rem;width:.75rem}.size-4{height:1rem;width:1rem}.size-5{height:1.25rem;width:1.25rem}.size-6{height:1.5rem;width:1.5rem}.size-7{height:1.75rem;width:1.75rem}.size-8{height:2rem;width:2rem}.size-10{height:2.5rem;width:2.5rem}.w-3xs{width:16rem}.w-2xs{width:18rem}.w-xs{width:20rem}.w-sm{width:24rem}.w-md{width:28rem}.w-lg{width:32rem}.w-xl{width:36rem}.w-2xl{width:42rem}.w-3xl{width:48rem}.w-4xl{width:56rem}.w-5xl{width:64rem}.w-6xl{width:72rem}.w-7xl{width:80rem}.w-fit{width:fit-content}.w-full{width:100%}.h-m{height:var(--copilot-size-md)}.h-75{height:var(--space-75)}.h-900{height:var(--space-900)}.w-75{width:var(--space-75)}.text-center{text-align:center}.uppercase{text-transform:uppercase}.text-balance{text-wrap:balance}.text-pretty{text-wrap:pretty}.rotate-90{transform:rotate(90deg)}.transform-none{transform:none}.transition{transition-property:color,background-color,border-color,outline-color,text-decoration-color,fill,stroke,opacity,box-shadow,transform,translate,scale,rotate,filter,-webkit-backdrop-filter,backdrop-filter,display,content-visibility,overlay,pointer-events;transition-timing-function:cubic-bezier(.4,0,.2,1);transition-duration:.15s}.transition-all{transition-property:all;transition-timing-function:cubic-bezier(.4,0,.2,1);transition-duration:.15s}.transition-colors{transition-property:color,background-color,border-color,outline-color,text-decoration-color,fill,stroke;transition-timing-function:cubic-bezier(.4,0,.2,1);transition-duration:.15s}.transition-opacity{transition-property:opacity;transition-timing-function:cubic-bezier(.4,0,.2,1);transition-duration:.15s}.transition-shadow{transition-property:box-shadow;transition-timing-function:cubic-bezier(.4,0,.2,1);transition-duration:.15s}.transition-transform{transition-property:transform,translate,scale,rotate;transition-timing-function:cubic-bezier(.4,0,.2,1);transition-duration:.15s}.transition-none{transition-property:none}.translate-y-0{transform:translateY(0)}.group:is(:hover,:focus-within) :is(.group-hover\\:-translate-x-1,.group-focus-within\\:-translate-x-1){transform:translate(-.25rem)}.select-none{-webkit-user-select:none;user-select:none}.invisible{visibility:hidden}.whitespace-nowrap{white-space:nowrap}.z-1{z-index:1}";
var iu = typeof globalThis < "u" ? globalThis : typeof window < "u" ? window : typeof global < "u" ? global : typeof self < "u" ? self : {};
function id(e) {
  return e && e.__esModule && Object.prototype.hasOwnProperty.call(e, "default") ? e.default : e;
}
function au(e) {
  if (Object.prototype.hasOwnProperty.call(e, "__esModule")) return e;
  var t = e.default;
  if (typeof t == "function") {
    var r = function n() {
      var i = !1;
      try {
        i = this instanceof n;
      } catch {
      }
      return i ? Reflect.construct(t, arguments, this.constructor) : t.apply(this, arguments);
    };
    r.prototype = t.prototype;
  } else r = {};
  return Object.defineProperty(r, "__esModule", { value: !0 }), Object.keys(e).forEach(function(n) {
    var i = Object.getOwnPropertyDescriptor(e, n);
    Object.defineProperty(r, n, i.get ? i : {
      enumerable: !0,
      get: function() {
        return e[n];
      }
    });
  }), r;
}
var jt = { exports: {} }, si;
function ad() {
  if (si) return jt.exports;
  si = 1;
  function e(t, r = 100, n = {}) {
    if (typeof t != "function")
      throw new TypeError(`Expected the first parameter to be a function, got \`${typeof t}\`.`);
    if (r < 0)
      throw new RangeError("`wait` must not be negative.");
    const { immediate: i } = typeof n == "boolean" ? { immediate: n } : n;
    let a, o, s, l, c;
    function d() {
      const h = a, m = o;
      return a = void 0, o = void 0, c = t.apply(h, m), c;
    }
    function u() {
      const h = Date.now() - l;
      h < r && h >= 0 ? s = setTimeout(u, r - h) : (s = void 0, i || (c = d()));
    }
    const v = function(...h) {
      if (a && this !== a && Object.getPrototypeOf(this) === Object.getPrototypeOf(a))
        throw new Error("Debounced method called with different contexts of the same prototype.");
      a = this, o = h, l = Date.now();
      const m = i && !s;
      return s || (s = setTimeout(u, r)), m && (c = d()), c;
    };
    return Object.defineProperty(v, "isPending", {
      get() {
        return s !== void 0;
      }
    }), v.clear = () => {
      s && (clearTimeout(s), s = void 0);
    }, v.flush = () => {
      s && v.trigger();
    }, v.trigger = () => {
      c = d(), v.clear();
    }, v;
  }
  return jt.exports.debounce = e, jt.exports = e, jt.exports;
}
var od = /* @__PURE__ */ ad();
const sd = /* @__PURE__ */ id(od);
class ld {
  constructor() {
    this.documentActive = !0, this.addListeners = () => {
      window.addEventListener("pageshow", this.handleWindowVisibilityChange), window.addEventListener("pagehide", this.handleWindowVisibilityChange), window.addEventListener("focus", this.handleWindowFocusChange), window.addEventListener("blur", this.handleWindowFocusChange), document.addEventListener("visibilitychange", this.handleDocumentVisibilityChange);
    }, this.removeListeners = () => {
      window.removeEventListener("pageshow", this.handleWindowVisibilityChange), window.removeEventListener("pagehide", this.handleWindowVisibilityChange), window.removeEventListener("focus", this.handleWindowFocusChange), window.removeEventListener("blur", this.handleWindowFocusChange), document.removeEventListener("visibilitychange", this.handleDocumentVisibilityChange);
    }, this.handleWindowVisibilityChange = (t) => {
      t.type === "pageshow" ? this.dispatch(!0) : this.dispatch(!1);
    }, this.handleWindowFocusChange = (t) => {
      t.type === "focus" ? this.dispatch(!0) : this.dispatch(!1);
    }, this.handleDocumentVisibilityChange = () => {
      this.dispatch(!document.hidden);
    }, this.dispatch = (t) => {
      if (t !== this.documentActive) {
        const r = window.Vaadin.copilot.eventbus;
        this.documentActive = t, r.emit("document-activation-change", { active: this.documentActive });
      }
    };
  }
  copilotActivated() {
    this.addListeners();
  }
  copilotDeactivated() {
    this.removeListeners();
  }
}
const li = new ld(), cd = "copilot-development-setup-user-guide";
function ou() {
  xt("use-dev-workflow-guide"), se.updatePanel(cd, { floating: !0 });
}
function Fa() {
  const e = wr.jdkInfo;
  return e ? e.jrebel ? "success" : e.hotswapAgentFound ? !e.hotswapVersionOk || !e.runningWithExtendClassDef || !e.runningWitHotswap || !e.runningInJavaDebugMode ? "error" : "success" : "warning" : null;
}
function su() {
  const e = wr.jdkInfo;
  return !e || Fa() !== "success" ? "none" : e.jrebel ? "jrebel" : e.runningWitHotswap ? "hotswap" : "none";
}
function dd() {
  return b.idePluginState !== void 0 && !b.idePluginState.active ? "warning" : "success";
}
function lu() {
  if (!wr.jdkInfo)
    return { status: "success" };
  const e = Fa(), t = dd();
  return e === "warning" ? t === "warning" ? { status: "warning", message: "IDE Plugin, Hotswap" } : { status: "warning", message: "Hotswap is not enabled" } : t === "warning" ? { status: "warning", message: "IDE Plugin is not active" } : e === "error" ? { status: "error", message: "Hotswap is partially enabled" } : { status: "success" };
}
function ud() {
  te(`${he}get-dev-setup-info`, {}), window.Vaadin.copilot.eventbus.on("copilot-get-dev-setup-info-response", (e) => {
    if (e.detail.content) {
      const t = JSON.parse(e.detail.content);
      b.setIdePluginState(t.ideInfo);
    }
  });
}
const ct = /* @__PURE__ */ new WeakMap();
class pd {
  constructor() {
    this.root = null, this.nodeUuidNodeMapFlat = /* @__PURE__ */ new Map(), this.aborted = !1, this._hasFlowComponent = !1, this.flowNodesInSource = {}, this.flowCustomComponentData = {}, this.hillaCustomComponentData = {}, this.componentDragDropApiInfosMap = {}, this.waitForHillaCustomComponentResponseData = () => new Promise((t, r) => {
      const n = setTimeout(() => {
        r(new Error("Timed out waiting for custom component data"));
      }, 1e4);
      y.emit("request-hilla-custom-component-data-with-callback", {
        tree: this,
        callback: (i) => {
          clearTimeout(n), t(i);
        }
      });
    });
  }
  async init() {
    const t = nl();
    if (t) {
      const r = await this.addToTree(t);
      r && this.root?.abstractRootNode && this.root.children.length === 1 && (this.root = this.root.children[0]), r && (await this.addOverlayContentToTreeIfExists("vaadin-popover[opened]"), await this.addOverlayContentToTreeIfExists("vaadin-dialog[opened]")), this.hillaCustomComponentData = await this.waitForHillaCustomComponentResponseData();
    }
  }
  getChildren(t) {
    return this.nodeUuidNodeMapFlat.get(t)?.children ?? [];
  }
  get allNodesFlat() {
    return Array.from(this.nodeUuidNodeMapFlat.values());
  }
  getNodeOfElement(t) {
    if (t)
      return this.allNodesFlat.find((r) => r.element === t);
  }
  /**
   * Handles route containers that should not be present in the tree. When this returns <code>true</code>, it means that given node is a route container so adding it to tree should be skipped
   *
   * @param node Node to check whether it is a route container or not
   * @param parentNode Parent of the given {@link node}
   */
  async handleRouteContainers(t, r) {
    const n = In(t);
    if (!n && ul(t)) {
      const i = nr(t);
      if (i && i.nextElementSibling)
        return await this.addToTree(i.nextElementSibling, r), !0;
    }
    if (n && t.localName === "react-router-outlet") {
      for (const i of Array.from(t.children)) {
        const a = rr(i);
        a && await this.addToTree(a, r);
      }
      return !0;
    }
    return !1;
  }
  includeReactNode(t) {
    return ut(t) === "PreconfiguredAuthProvider" || ut(t) === "RouterProvider" ? !1 : Nn(t) || sl(t);
  }
  async includeFlowNode(t) {
    return pl(t) || Ar(t)?.hiddenByServer ? !1 : this.isInitializedInProjectSources(t);
  }
  async isInitializedInProjectSources(t) {
    const r = Ar(t);
    if (!r)
      return !1;
    const { nodeId: n, uiId: i } = r;
    if (!this.flowNodesInSource[i]) {
      const a = await Nt("copilot-get-component-source-info", { uiId: i }, (o) => o.data);
      a.error && ce("Failed to get component source info", a.error), this.flowCustomComponentData[i] = a.customComponentResponse, this.flowNodesInSource[i] = new Set(a.nodeIdsInProject), this.componentDragDropApiInfosMap[i] = a.dragDropApiInfos;
    }
    return this.flowNodesInSource[i].has(n);
  }
  /**
   * Adds the given element into the tree and returns the result when added.
   * <p>
   *  It recursively travels through the children of given node. This method is called for each child ,but the result of adding a child is swallowed
   * </p>
   * @param node Node to add to tree
   * @param parentNode Parent of the node, might be null if it is the root element
   */
  async addToTree(t, r) {
    if (this.isAborted())
      return !1;
    const n = await this.handleRouteContainers(t, r);
    if (n)
      return n;
    const i = In(t);
    let a;
    if (!i)
      this.includeReactNode(t) && (a = this.generateNodeFromFiber(t, r));
    else if (await this.includeFlowNode(t)) {
      const l = this.generateNodeFromFlow(t, r);
      if (!l)
        return !1;
      this._hasFlowComponent = !0, a = l;
    }
    if (r)
      a && (a.parent = r, r.children || (r.children = []), r.children.push(a));
    else {
      if (!a) {
        if (!(t instanceof Element) && ca(t))
          return Ta({
            type: ye.WARNING,
            message: "Copilot is partly usable",
            details: `${ut(t)} should be a function component to make Copilot work properly`,
            dismissId: "react_route_component_is_class"
          }), !1;
        if (i ? a = this.generateNodeFromFlow(t) : a = this.generateNodeFromFiber(t), !a)
          return ce("Unable to add node", new Error("Tree root node is undefined")), !1;
        a.abstractRootNode = !0;
      }
      this.root = a;
    }
    a && this.nodeUuidNodeMapFlat.set(a.uuid, a);
    const o = a ?? r, s = i ? Array.from(t.children) : il(t);
    for (const l of s)
      await this.addToTree(l, o);
    return a !== void 0;
  }
  generateNodeFromFiber(t, r) {
    const n = Nn(t) ? nr(t) : void 0, i = r?.children.length ?? 0, a = this;
    return {
      node: t,
      parent: r,
      element: n,
      depth: r && r.depth + 1 || 0,
      children: [],
      siblingIndex: i,
      isFlowComponent: !1,
      isReactComponent: !0,
      isLitTemplate: !1,
      zeroSize: n ? Ln(n) : void 0,
      get uuid() {
        if (ct.has(t))
          return ct.get(t);
        if (t.alternate && ct.has(t.alternate))
          return ct.get(t.alternate);
        const s = qa();
        return ct.set(t, s), s;
      },
      get name() {
        return zn(ut(t));
      },
      get identifier() {
        return Un(n);
      },
      get nameAndIdentifier() {
        return di(this.name, this.identifier);
      },
      get previousSibling() {
        if (i !== 0)
          return r?.children[i - 1];
      },
      get nextSibling() {
        if (!(r === void 0 || i === r.children.length - 1))
          return r.children[i + 1];
      },
      get path() {
        return ci(this);
      },
      get customComponentData() {
        if (a.hillaCustomComponentData[this.uuid])
          return a.hillaCustomComponentData[this.uuid];
      }
    };
  }
  generateNodeFromFlow(t, r) {
    const n = Ar(t);
    if (!n)
      return;
    const i = r?.children.length ?? 0, a = this.flowCustomComponentData, o = this.componentDragDropApiInfosMap;
    return {
      node: n,
      parent: r,
      element: t,
      depth: r && r.depth + 1 || 0,
      children: [],
      siblingIndex: i,
      get uuid() {
        return `${n.uiId}#${n.nodeId}`;
      },
      isFlowComponent: !0,
      isReactComponent: !1,
      get isLitTemplate() {
        return !!this.customComponentData?.litTemplate;
      },
      zeroSize: t ? Ln(t) : void 0,
      get name() {
        return dl(n) ?? zn(n.element.localName);
      },
      get identifier() {
        return Un(t);
      },
      get nameAndIdentifier() {
        return di(this.name, this.identifier);
      },
      get previousSibling() {
        if (i !== 0)
          return r?.children[i - 1];
      },
      get nextSibling() {
        if (!(r === void 0 || i === r.children.length - 1))
          return r.children[i + 1];
      },
      get path() {
        return ci(this);
      },
      get customComponentData() {
        if (a[n.uiId])
          return a[n.uiId].allComponentsInfoForCustomComponentSupport[n.nodeId];
      },
      get componentDragDropApiInfo() {
        if (!o[n.uiId])
          return;
        const l = o[n.uiId];
        if (l[n.nodeId])
          return l[n.nodeId];
      }
    };
  }
  async addOverlayContentToTreeIfExists(t) {
    const r = document.body.querySelector(t);
    if (!r)
      return;
    let n = !0;
    if (!this.getNodeOfElement(r)) {
      const i = Ie(rr(r));
      n = await this.addToTree(i ?? r, this.root);
    }
    if (n)
      for (const i of Array.from(r.children))
        await this.addToTree(i, this.getNodeOfElement(r));
  }
  hasFlowComponents() {
    return this._hasFlowComponent;
  }
  findNodeByUuid(t) {
    if (t)
      return this.nodeUuidNodeMapFlat.get(t);
  }
  getElementByNodeUuid(t) {
    return this.findNodeByUuid(t)?.element;
  }
  findByTreePath(t) {
    if (t)
      return this.allNodesFlat.find((r) => r.path === t);
  }
  isAborted() {
    return this.aborted;
  }
  abort() {
    this.aborted = !0;
  }
  get customComponentDataLoaded() {
    return Object.keys(this.hillaCustomComponentData).length !== 0 || Object.keys(this.flowCustomComponentData).length !== 0;
  }
}
function ci(e) {
  if (!e.parent)
    return e.name;
  let t = 0;
  for (let r = 0; r < e.siblingIndex + 1; r++)
    e.parent.children[r].name === e.name && t++;
  return `${e.parent.path} > ${e.name}[${t}]`;
}
function di(e, t) {
  return t ? `${e} "${t}"` : e;
}
let He = null;
const fd = async () => {
  He && He.abort();
  const e = new pd();
  He = e, await e.init(), He = null, e.isAborted() || (window.Vaadin.copilot.tree.currentTree = e);
}, cu = () => {
  He && He.abort();
};
function vd() {
  const e = window.navigator.userAgent;
  return e.indexOf("Windows") !== -1 ? "Windows" : e.indexOf("Mac") !== -1 ? "Mac" : e.indexOf("Linux") !== -1 ? "Linux" : null;
}
function hd() {
  return vd() === "Mac";
}
function gd() {
  return hd() ? "⌘" : "Ctrl";
}
let ui = !1, dt = 0;
const Wr = (e) => {
  if (nt.isActivationShortcut())
    if (e.key === "Shift" && !e.ctrlKey && !e.altKey && !e.metaKey)
      ui = !0;
    else if (ui && e.shiftKey && (e.key === "Control" || e.key === "Meta")) {
      if (dt++, dt === 2)
        return b.toggleActive("shortcut"), dt = 0, !0;
      setTimeout(() => {
        dt = 0;
      }, 500);
    } else
      dt = 0;
  return !1;
};
function pi(e) {
  if ((e.ctrlKey || e.metaKey) && e.key === "c" && !e.shiftKey) {
    const t = document.querySelector("copilot-main")?.shadowRoot;
    let r;
    if (typeof t?.getSelection == "function" ? r = t?.getSelection() : r = document.getSelection() ?? void 0, r && r.rangeCount === 1) {
      const i = r.getRangeAt(0).commonAncestorContainer;
      if (i.nodeType === Node.TEXT_NODE)
        return fe(i);
    }
  }
  return !1;
}
function md(e) {
  const t = ar(e, "vaadin-context-menu-overlay");
  if (!t)
    return !1;
  const r = t.owner;
  return r ? !!ar(r, "copilot-component-overlay") : !1;
}
function bd() {
  return b.idePluginState?.supportedActions?.find((e) => e === "undo");
}
const fi = (e) => {
  if (!b.active)
    return;
  if (Wr(e)) {
    e.stopPropagation();
    return;
  }
  const t = kl();
  if (!t)
    return;
  const r = md(t), n = t.localName === "copilot-main", i = ar(t, "copilot-outline-panel") !== null;
  if (!n && !r && e.key !== "Escape" && !i) {
    e.stopPropagation();
    return;
  }
  let a = !0, o = !1;
  if (pi(e))
    a = !1;
  else if (e.key === "Escape") {
    if (b.loginCheckActive ? b.setLoginCheckActive(!1) : y.emit("close-drawers", {}), wd(t)) {
      e.stopPropagation();
      return;
    }
    y.emit("escape-key-pressed", { event: e });
  } else yd(e) ? (y.emit("delete-selected", {}), o = !0) : (e.ctrlKey || e.metaKey) && e.key === "d" ? (y.emit("duplicate-selected", {}), o = !0) : (e.ctrlKey || e.metaKey) && e.key === "b" ? (y.emit("show-selected-in-ide", { attach: e.shiftKey }), o = !0) : (e.ctrlKey || e.metaKey) && e.key === "z" && bd() ? (y.emit("undoRedo", { undo: !e.shiftKey }), o = !0) : pi(e) || y.emit("keyboard-event", { event: e });
  a && e.stopPropagation(), o && e.preventDefault();
}, yd = (e) => (e.key === "Backspace" || e.key === "Delete") && !e.shiftKey && !e.ctrlKey && !e.altKey && !e.metaKey;
function wd(e) {
  const t = e;
  if (Mn(e))
    return !0;
  const r = pa(t);
  for (const n of r)
    if (Mn(n))
      return !0;
  return !1;
}
const re = gd(), Rt = "⇧", du = {
  toggleCopilot: `<kbd>${Rt} + ${re} ${re}</kbd>`,
  openAiPopover: `<kbd>${Rt} + Space</kbd>`,
  undo: `<kbd>${re} + Z</kbd>`,
  redo: `<kbd>${re} + ${Rt} + Z</kbd>`,
  duplicate: `<kbd>${re} + D</kbd>`,
  goToSource: `<kbd>${re} + B</kbd>`,
  goToAttachSource: `<kbd>${re} + ${Rt} + B</kbd>`,
  selectParent: "<kbd>←</kbd>",
  selectPreviousSibling: "<kbd>↑</kbd>",
  selectNextSibling: "<kbd>↓</kbd>",
  delete: "<kbd>DEL</kbd>",
  copy: `<kbd>${re} + C</kbd>`,
  paste: `<kbd>${re} + V</kbd>`
};
var xd = Object.getOwnPropertyDescriptor, Od = (e, t, r, n) => {
  for (var i = n > 1 ? void 0 : n ? xd(t, r) : t, a = e.length - 1, o; a >= 0; a--)
    (o = e[a]) && (i = o(i) || i);
  return i;
};
let vi = class extends cc {
  constructor() {
    super(...arguments), this.removers = [], this.initialized = !1, this.active = !1, this.overlayListener = (e) => {
      if (!b.active)
        return;
      const { overlay: t } = e.detail;
      if (fe(t)) {
        const r = Ke.getOwner(t);
        r && this.addDescendantOverlayOwnersToDrawer(r);
        return;
      }
      this.ensureTopmostPopover();
    }, this.overlayCloseEventListener = (e) => {
      const t = e.detail?.overlay;
      if (!t || !fe(t))
        return;
      const r = Ke.getOwner(t);
      r && this.removeDescendantOverlayOwnersToDrawer(r);
    }, this.addDescendantOverlayOwnersToDrawer = (e) => {
      if (e.getAttribute("slot") === "submenu")
        return;
      const t = this.getAncestorDrawer(e);
      t && e && e.localName !== "vaadin-tooltip" && t.openedDescendantOverlayOwners.add(e);
    }, this.removeDescendantOverlayOwnersToDrawer = (e) => {
      if (e.getAttribute("slot") === "submenu")
        return;
      const t = this.getAncestorDrawer(e);
      t && t.openedDescendantOverlayOwners.has(e) && t.openedDescendantOverlayOwners.delete(e);
    }, this.getAncestorDrawer = (e) => {
      if (!e)
        return;
      const t = ar(e, "copilot-drawer-panel") ?? void 0;
      if (t)
        return t;
    }, this.toggleOperationInProgressAttr = () => {
      this.toggleAttribute("operation-in-progress", b.operationWaitsHmrUpdate !== void 0);
    }, this.operationInProgressCursorUpdateDebounceFunc = sd(this.toggleOperationInProgressAttr, 500), this.overlayOutsideClickListener = (e) => {
      fe(e.target?.owner) || (b.active || fe(e.detail.sourceEvent.target)) && e.preventDefault();
    }, this.mouseLeaveListener = () => {
      y.emit("close-drawers", {});
    };
  }
  static get styles() {
    return [
      L(Xc),
      L(Jc),
      L(Gc),
      L(Yc),
      L(Qc),
      L(_c),
      L($c),
      L(ed),
      L(td),
      L(rd),
      L(nd),
      jl`
        :host {
          color: var(--vaadin-text-color);
          contain: strict;
          cursor: var(--cursor, default);
          font: var(--copilot-font-xs);
          inset: 0;
          pointer-events: all;
          position: fixed;
          z-index: 9999;

          /* Override native [popover] user agent styles */
          width: auto;
          height: auto;
          border: none;
          padding: 0;
          background-color: transparent;
          overflow: visible;
        }

        :host([operation-in-progress]) {
          --cursor: wait;
          --lumo-clickable-cursor: wait;
        }

        :host(:not([active])) {
          visibility: hidden !important;
          pointer-events: none;
        }

        /* Hide floating panels when not active */

        :host(:not([active])) > copilot-section-panel-wrapper {
          display: none !important;
        }
        :host(:not([active])) > copilot-section-panel-wrapper[individual] {
          display: block !important;
          visibility: visible;
          pointer-events: all;
        }

        /* Keep activation button and menu visible */

        copilot-activation-button,
        .activation-button-menu {
          visibility: visible;
          display: flex !important;
        }

        copilot-activation-button {
          pointer-events: auto;
        }

        a {
          color: var(--blue-11);
        }

        :host([user-select-none]) {
          -webkit-touch-callout: none;
          -webkit-user-select: none;
          -moz-user-select: none;
          -ms-user-select: none;
          user-select: none;
        }

        /* Needed to prevent a JS error because of monkey patched '_attachOverlay'. It is some scope issue, */
        /* where 'this._placeholder.parentNode' is undefined - the scope if 'this' gets messed up at some point. */
        /* We also don't want animations on the overlays to make the feel faster, so this is fine. */
        :is(vaadin-tooltip-overlay) {
          z-index: calc(var(--copilot-notifications-container-z-index) + 10);
        }
        :is(
          vaadin-context-menu,
          vaadin-menu-bar,
          vaadin-select,
          vaadin-combo-box,
          vaadin-tooltip,
          vaadin-multi-select-combo-box
        ):is([opening], [closing]),
        :is(
          vaadin-context-menu,
          vaadin-menu-bar,
          vaadin-select,
          vaadin-combo-box,
          vaadin-tooltip,
          vaadin-multi-select-combo-box
        )::part(overlay) {
          animation: none !important;
        }

        :host(:not([active])) copilot-drawer-panel::before {
          animation: none;
        }

        :host .alwaysVisible {
          visibility: visible !important;
        }
      `
    ];
  }
  connectedCallback() {
    super.connectedCallback(), this.popover = "manual", this.ensureTopmostPopover(), document.body.addEventListener("vaadin-overlay-open", this.overlayListener), document.documentElement.addEventListener("vaadin-overlay-close", this.overlayCloseEventListener), this.init().catch((e) => ce("Unable to initialize copilot", e));
  }
  ensureTopmostPopover() {
    this.isConnected && (this.hidePopover(), this.showPopover());
  }
  async init() {
    if (this.initialized)
      return;
    await window.Vaadin.copilot._machineState.initializer.promise, await import("./copilot-global-vars-later-D1aE33Nd.js"), await import("./copilot-init-step2-D-it77gp.js"), bl(), dc(), this.tabIndex = 0, Mt.hostConnectedCallback(), window.addEventListener("keydown", Wr), this.addEventListener("keydown", fi), y.onSend(this.handleSendEvent), this.removers.push(y.on("close-drawers", this.closeDrawers.bind(this))), this.removers.push(
      y.on("open-attention-required-drawer", this.openDrawerIfPanelRequiresAttention.bind(this))
    ), this.removers.push(
      y.on("set-pointer-events", (r) => {
        this.style.pointerEvents = r.detail.enable ? "" : "none";
      })
    ), this.addEventListener("mousemove", this.mouseMoveListener), this.addEventListener("dragover", this.dragOverListener), this.addEventListener("dragleave", this.dragLeaveListener), this.addEventListener("drop", this.dropListener), Ke.addOverlayOutsideClickEvent();
    const e = window.matchMedia("(prefers-color-scheme: dark)");
    this.classList.toggle("dark", e.matches), e.addEventListener("change", (r) => {
      this.classList.toggle("dark", e.matches);
    }), this.reaction(
      () => b.active,
      () => {
        this.toggleAttribute("active", b.active), b.active ? this.activate() : this.deactivate(), We.saveCopilotActivation(b.active);
      }
    ), this.reaction(
      () => b.activatedAtLeastOnce,
      () => {
        La(), uc();
      }
    ), this.reaction(
      () => b.sectionPanelDragging,
      () => {
        b.sectionPanelDragging && Array.from(this.shadowRoot.children).filter((n) => n.localName.endsWith("-overlay")).forEach((n) => {
          n.close && n.close();
        });
      }
    ), this.reaction(
      () => b.operationWaitsHmrUpdate,
      () => {
        b.operationWaitsHmrUpdate ? this.operationInProgressCursorUpdateDebounceFunc() : (this.operationInProgressCursorUpdateDebounceFunc.clear(), this.toggleOperationInProgressAttr());
      }
    ), this.reaction(
      () => se.panels,
      () => {
        se.panels.find((r) => r.individual) && this.requestUpdate();
      }
    ), We.getCopilotActivation() && ua().then(() => {
      b.setActive(!0, "restore");
    }), this.removers.push(
      y.on("user-select", (r) => {
        const { allowSelection: n } = r.detail;
        this.toggleAttribute("user-select-none", !n);
      })
    ), this.removers.push(
      y.on("featureFlags", (r) => {
        const n = r.detail.features;
        b.setFeatureFlags(n);
      })
    ), this.removers.push(
      y.on("vaadin-drawer-opened-changed", (r) => {
        const { opened: n, owner: i } = r.detail;
        n ? this.addDescendantOverlayOwnersToDrawer(i) : this.removeDescendantOverlayOwnersToDrawer(i);
      })
    );
    const t = new ResizeObserver(() => {
      y.emit("copilot-main-resized", {});
    });
    t.observe(this), this.removers.push(() => {
      t.disconnect();
    }), za(), this.detectAppTheme(), this.initialized = !0, ud();
  }
  /**
   * Called when Copilot is activated. Good place to start attach listeners etc.
   */
  async activate() {
    Mt.activate(), li.copilotActivated(), pc(), this.openDrawerIfPanelRequiresAttention(), document.documentElement.addEventListener("mouseleave", this.mouseLeaveListener), Ke.onCopilotActivation(), await fd(), Va.loadPreviewConfiguration(), this.ensureTopmostPopover(), this.active = !0, nt.isActivationAnimation() && b.activatedFrom !== "restore" && this.getAllDrawers().forEach((e) => {
      e.setAttribute("bounce", "");
    });
  }
  /**
   * Called when Copilot is deactivated. Good place to remove listeners etc.
   */
  deactivate() {
    this.getAllDrawers().forEach((e) => {
      e.removeAttribute("bounce");
    }), this.closeDrawers(), Mt.deactivate(), li.copilotDeactivated(), document.documentElement.removeEventListener("mouseleave", this.mouseLeaveListener), Ke.onCopilotDeactivation(), this.active = !1;
  }
  getAllDrawers() {
    return Array.from(this.shadowRoot.querySelectorAll("copilot-drawer-panel"));
  }
  disconnectedCallback() {
    super.disconnectedCallback(), Mt.hostDisconnectedCallback(), window.removeEventListener("keydown", Wr), this.removeEventListener("keydown", fi), y.offSend(this.handleSendEvent), this.removers.forEach((e) => e()), this.removeEventListener("mousemove", this.mouseMoveListener), this.removeEventListener("dragover", this.dragOverListener), this.removeEventListener("dragleave", this.dragLeaveListener), this.removeEventListener("drop", this.dropListener), Ke.removeOverlayOutsideClickEvent(), document.documentElement.removeEventListener("vaadin-overlay-outside-click", this.overlayOutsideClickListener), document.body.removeEventListener("vaadin-overlay-open", this.overlayListener), document.documentElement.removeEventListener("vaadin-overlay-close", this.overlayCloseEventListener);
  }
  handleSendEvent(e) {
    const t = e.detail.command, r = e.detail.data;
    te(t, r);
  }
  /**
   * Opens the attention required drawer if there is any.
   */
  openDrawerIfPanelRequiresAttention() {
    const e = se.getAttentionRequiredPanelConfiguration();
    if (!e)
      return;
    const t = e.panel;
    if (!t || e.floating)
      return;
    const r = this.shadowRoot.querySelector(`copilot-drawer-panel[position="${t}"]`);
    r.opened = !0;
  }
  render() {
    return le`
      <copilot-activation-button
        @activation-btn-clicked="${() => {
      b.toggleActive("button"), b.setLoginCheckActive(!1);
    }}">
      </copilot-activation-button>
      <copilot-component-selector></copilot-component-selector>
      <copilot-label-editor-container></copilot-label-editor-container>
      <copilot-info-tooltip></copilot-info-tooltip>
      ${this.renderDrawer("left")} ${this.renderDrawer("right")} ${this.renderDrawer("bottom")} ${Rc()}
      <copilot-login-check></copilot-login-check>
      <copilot-ai-usage-confirmation-dialog></copilot-ai-usage-confirmation-dialog>
      <copilot-notifications-container></copilot-notifications-container>
      <copilot-report-exception-dialog></copilot-report-exception-dialog>
      <copilot-welcome></copilot-welcome>
    `;
  }
  renderDrawer(e) {
    return le` <copilot-drawer-panel position=${e}> ${jc(e)} </copilot-drawer-panel>`;
  }
  /**
   * Closes the open drawers if any opened unless an overlay is opened from drawer.
   */
  closeDrawers() {
    const e = this.getAllDrawers();
    if (Array.from(e).some((r) => r.opened))
      for (const r of e)
        r.openedDescendantOverlayOwners.size === 0 && (r.opened = !1);
  }
  updated(e) {
    super.updated(e), this.attachActivationButtonToBody(), Hc();
  }
  attachActivationButtonToBody() {
    const e = document.body.querySelectorAll("copilot-activation-button");
    e.length > 1 && e[0].remove();
  }
  mouseMoveListener(e) {
    e.composedPath().find((t) => t.localName === `${he}drawer-panel`) || this.closeDrawers();
  }
  dragOverListener(e) {
    this.mouseMoveListener(e), e.dataTransfer && (e.dataTransfer.dropEffect = "none"), e.preventDefault(), y.emit("drag-and-drop-in-progress", {});
  }
  dragLeaveListener(e) {
    Dl(e) && y.emit("end-drag-drop", {});
  }
  dropListener(e) {
    e.preventDefault(), y.emit("end-drag-drop", {});
  }
  detectAppTheme() {
    Tn("lumo") ? b.setAppTheme("lumo") : Tn("aura") ? b.setAppTheme("aura") : b.setAppTheme(null), Nt(`${he}set-app-theme`, { theme: b.appTheme }, () => {
    });
  }
};
vi = Od([
  Ml("copilot-main")
], vi);
const Ed = window.Vaadin, kd = {
  init(e) {
    da(
      () => window.Vaadin.devTools,
      (t) => {
        const r = t.handleFrontendMessage;
        t.handleFrontendMessage = (n) => {
          Wc(n) || r.call(t, n);
        };
      }
    );
  }
};
Ed.devToolsPlugins.push(kd);
function Cd(e, t, r = {}) {
  const n = { ...r };
  e.classNames.length > 0 && (n.className = e.classNames.join(" "));
  const i = Ad(e);
  Object.keys(i).length > 0 && (n.style = i);
  for (const a of Sd()) {
    const o = a(e, t);
    if (o) {
      n.className && o.props?.className && (o.props.className = `${String(o.props.className)} ${String(n.className)}`, delete n.className), o.props = { ...o.props, ...n };
      for (const [s, l] of Object.entries(o.props))
        l === void 0 && delete o.props[s];
      return o;
    }
  }
  console.warn(`No importer found for node: ${e.htmlTag} ${e.reactTag} (${e.type})`);
}
function Ad(e) {
  if (Object.keys(e.styles).length === 0)
    return {};
  const t = {};
  return Object.keys(e.styles).forEach((r) => {
    const n = e.styles[r];
    r.startsWith("--") || (r = r.replace(/-([a-z])/g, (i) => i[1].toUpperCase())), t[r] = n;
  }), t;
}
function uu(e) {
  Za().unshift(e);
}
function Sd() {
  return [...Za(), ...Pd()];
}
function Za() {
  return window.Vaadin.copilot.figmaImporters ??= [], window.Vaadin.copilot.figmaImporters;
}
function Pd() {
  return window.Vaadin.copilot.figmaBuiltInImporters ??= [], window.Vaadin.copilot.figmaBuiltInImporters;
}
function Wa(e, t) {
  const r = [];
  for (const n of e.children)
    t(n) && r.push(n);
  for (const n of e.children)
    r.push(...Wa(n, t));
  return r;
}
function pu(e, t, r) {
  return Wa(e, r).map((n) => Cd(n, t)).filter((n) => n !== void 0);
}
export {
  du as $,
  jl as A,
  Td as B,
  We as C,
  le as D,
  E,
  ht as F,
  xl as G,
  _d as H,
  cd as I,
  wr as J,
  Fa as K,
  Dd as L,
  cc as M,
  zd as N,
  $d as O,
  he as P,
  su as Q,
  Vd as R,
  nt as S,
  lu as T,
  Va as U,
  Id as V,
  Fd as W,
  Jc as X,
  Qc as Y,
  $c as Z,
  ou as _,
  id as a,
  ml as a0,
  tu as a1,
  ru as a2,
  vc as a3,
  Wd as a4,
  Ld as a5,
  xt as a6,
  Ta as a7,
  ye as a8,
  Nd as a9,
  pu as aA,
  Gc as aa,
  Rd as ab,
  Da as ac,
  fd as ad,
  cu as ae,
  Pa as af,
  eu as ag,
  Aa as ah,
  oc as ai,
  Sa as aj,
  ir as ak,
  qd as al,
  jd as am,
  Ua as an,
  Bt as ao,
  vl as ap,
  dd as aq,
  bc as ar,
  La as as,
  dn as at,
  xc as au,
  wa as av,
  Fr as aw,
  Mn as ax,
  Gd as ay,
  uu as az,
  y as b,
  iu as c,
  tr as d,
  $s as e,
  Md as f,
  au as g,
  Kd as h,
  Ud as i,
  b as j,
  Nt as k,
  ce as l,
  $e as m,
  Zd as n,
  C as o,
  nu as p,
  pd as q,
  Ml as r,
  te as s,
  Bd as t,
  wl as u,
  sd as v,
  se as w,
  L as x,
  Xc as y,
  nd as z
};
