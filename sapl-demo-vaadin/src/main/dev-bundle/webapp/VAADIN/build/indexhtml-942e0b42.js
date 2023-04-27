(function(){const e=document.createElement("link").relList;if(e&&e.supports&&e.supports("modulepreload"))return;for(const i of document.querySelectorAll('link[rel="modulepreload"]'))o(i);new MutationObserver(i=>{for(const r of i)if(r.type==="childList")for(const s of r.addedNodes)s.tagName==="LINK"&&s.rel==="modulepreload"&&o(s)}).observe(document,{childList:!0,subtree:!0});function t(i){const r={};return i.integrity&&(r.integrity=i.integrity),i.referrerPolicy&&(r.referrerPolicy=i.referrerPolicy),i.crossOrigin==="use-credentials"?r.credentials="include":i.crossOrigin==="anonymous"?r.credentials="omit":r.credentials="same-origin",r}function o(i){if(i.ep)return;i.ep=!0;const r=t(i);fetch(i.href,r)}})();window.Vaadin=window.Vaadin||{};window.Vaadin.featureFlags=window.Vaadin.featureFlags||{};window.Vaadin.featureFlags.exampleFeatureFlag=!1;window.Vaadin.featureFlags.collaborationEngineBackend=!1;const so="modulepreload",ao=function(n,e){return new URL(n,e).href},qt={},Fe=function(e,t,o){if(!t||t.length===0)return e();const i=document.getElementsByTagName("link");return Promise.all(t.map(r=>{if(r=ao(r,o),r in qt)return;qt[r]=!0;const s=r.endsWith(".css"),l=s?'[rel="stylesheet"]':"";if(!!o)for(let h=i.length-1;h>=0;h--){const f=i[h];if(f.href===r&&(!s||f.rel==="stylesheet"))return}else if(document.querySelector(`link[href="${r}"]${l}`))return;const c=document.createElement("link");if(c.rel=s?"stylesheet":so,s||(c.as="script",c.crossOrigin=""),c.href=r,document.head.appendChild(c),s)return new Promise((h,f)=>{c.addEventListener("load",h),c.addEventListener("error",()=>f(new Error(`Unable to preload CSS for ${r}`)))})})).then(()=>e())};function Be(n){return n=n||[],Array.isArray(n)?n:[n]}function V(n){return`[Vaadin.Router] ${n}`}function lo(n){if(typeof n!="object")return String(n);const e=Object.prototype.toString.call(n).match(/ (.*)\]$/)[1];return e==="Object"||e==="Array"?`${e} ${JSON.stringify(n)}`:e}const He="module",We="nomodule",Ct=[He,We];function Kt(n){if(!n.match(/.+\.[m]?js$/))throw new Error(V(`Unsupported type for bundle "${n}": .js or .mjs expected.`))}function xn(n){if(!n||!D(n.path))throw new Error(V('Expected route config to be an object with a "path" string property, or an array of such objects'));const e=n.bundle,t=["component","redirect","bundle"];if(!Q(n.action)&&!Array.isArray(n.children)&&!Q(n.children)&&!je(e)&&!t.some(o=>D(n[o])))throw new Error(V(`Expected route config "${n.path}" to include either "${t.join('", "')}" or "action" function but none found.`));if(e)if(D(e))Kt(e);else if(Ct.some(o=>o in e))Ct.forEach(o=>o in e&&Kt(e[o]));else throw new Error(V('Expected route bundle to include either "'+We+'" or "'+He+'" keys, or both'));n.redirect&&["bundle","component"].forEach(o=>{o in n&&console.warn(V(`Route config "${n.path}" has both "redirect" and "${o}" properties, and "redirect" will always override the latter. Did you mean to only use "${o}"?`))})}function Yt(n){Be(n).forEach(e=>xn(e))}function Jt(n,e){let t=document.head.querySelector('script[src="'+n+'"][async]');return t||(t=document.createElement("script"),t.setAttribute("src",n),e===He?t.setAttribute("type",He):e===We&&t.setAttribute(We,""),t.async=!0),new Promise((o,i)=>{t.onreadystatechange=t.onload=r=>{t.__dynamicImportLoaded=!0,o(r)},t.onerror=r=>{t.parentNode&&t.parentNode.removeChild(t),i(r)},t.parentNode===null?document.head.appendChild(t):t.__dynamicImportLoaded&&o()})}function co(n){return D(n)?Jt(n):Promise.race(Ct.filter(e=>e in n).map(e=>Jt(n[e],e)))}function _e(n,e){return!window.dispatchEvent(new CustomEvent(`vaadin-router-${n}`,{cancelable:n==="go",detail:e}))}function je(n){return typeof n=="object"&&!!n}function Q(n){return typeof n=="function"}function D(n){return typeof n=="string"}function $n(n){const e=new Error(V(`Page not found (${n.pathname})`));return e.context=n,e.code=404,e}const le=new class{};function ho(n){const e=n.port,t=n.protocol,r=t==="http:"&&e==="80"||t==="https:"&&e==="443"?n.hostname:n.host;return`${t}//${r}`}function Xt(n){if(n.defaultPrevented||n.button!==0||n.shiftKey||n.ctrlKey||n.altKey||n.metaKey)return;let e=n.target;const t=n.composedPath?n.composedPath():n.path||[];for(let l=0;l<t.length;l++){const a=t[l];if(a.nodeName&&a.nodeName.toLowerCase()==="a"){e=a;break}}for(;e&&e.nodeName.toLowerCase()!=="a";)e=e.parentNode;if(!e||e.nodeName.toLowerCase()!=="a"||e.target&&e.target.toLowerCase()!=="_self"||e.hasAttribute("download")||e.hasAttribute("router-ignore")||e.pathname===window.location.pathname&&e.hash!==""||(e.origin||ho(e))!==window.location.origin)return;const{pathname:i,search:r,hash:s}=e;_e("go",{pathname:i,search:r,hash:s})&&(n.preventDefault(),n&&n.type==="click"&&window.scrollTo(0,0))}const uo={activate(){window.document.addEventListener("click",Xt)},inactivate(){window.document.removeEventListener("click",Xt)}},po=/Trident/.test(navigator.userAgent);po&&!Q(window.PopStateEvent)&&(window.PopStateEvent=function(n,e){e=e||{};var t=document.createEvent("Event");return t.initEvent(n,Boolean(e.bubbles),Boolean(e.cancelable)),t.state=e.state||null,t},window.PopStateEvent.prototype=window.Event.prototype);function Qt(n){if(n.state==="vaadin-router-ignore")return;const{pathname:e,search:t,hash:o}=window.location;_e("go",{pathname:e,search:t,hash:o})}const fo={activate(){window.addEventListener("popstate",Qt)},inactivate(){window.removeEventListener("popstate",Qt)}};var fe=An,mo=It,go=bo,vo=Nn,_o=Ln,kn="/",Rn="./",yo=new RegExp(["(\\\\.)","(?:\\:(\\w+)(?:\\(((?:\\\\.|[^\\\\()])+)\\))?|\\(((?:\\\\.|[^\\\\()])+)\\))([+*?])?"].join("|"),"g");function It(n,e){for(var t=[],o=0,i=0,r="",s=e&&e.delimiter||kn,l=e&&e.delimiters||Rn,a=!1,c;(c=yo.exec(n))!==null;){var h=c[0],f=c[1],p=c.index;if(r+=n.slice(i,p),i=p+h.length,f){r+=f[1],a=!0;continue}var m="",G=n[i],q=c[2],H=c[3],Xe=c[4],A=c[5];if(!a&&r.length){var U=r.length-1;l.indexOf(r[U])>-1&&(m=r[U],r=r.slice(0,U))}r&&(t.push(r),r="",a=!1);var ne=m!==""&&G!==void 0&&G!==m,oe=A==="+"||A==="*",Qe=A==="?"||A==="*",W=m||s,$e=H||Xe;t.push({name:q||o++,prefix:m,delimiter:W,optional:Qe,repeat:oe,partial:ne,pattern:$e?wo($e):"[^"+K(W)+"]+?"})}return(r||i<n.length)&&t.push(r+n.substr(i)),t}function bo(n,e){return Nn(It(n,e))}function Nn(n){for(var e=new Array(n.length),t=0;t<n.length;t++)typeof n[t]=="object"&&(e[t]=new RegExp("^(?:"+n[t].pattern+")$"));return function(o,i){for(var r="",s=i&&i.encode||encodeURIComponent,l=0;l<n.length;l++){var a=n[l];if(typeof a=="string"){r+=a;continue}var c=o?o[a.name]:void 0,h;if(Array.isArray(c)){if(!a.repeat)throw new TypeError('Expected "'+a.name+'" to not repeat, but got array');if(c.length===0){if(a.optional)continue;throw new TypeError('Expected "'+a.name+'" to not be empty')}for(var f=0;f<c.length;f++){if(h=s(c[f],a),!e[l].test(h))throw new TypeError('Expected all "'+a.name+'" to match "'+a.pattern+'"');r+=(f===0?a.prefix:a.delimiter)+h}continue}if(typeof c=="string"||typeof c=="number"||typeof c=="boolean"){if(h=s(String(c),a),!e[l].test(h))throw new TypeError('Expected "'+a.name+'" to match "'+a.pattern+'", but got "'+h+'"');r+=a.prefix+h;continue}if(a.optional){a.partial&&(r+=a.prefix);continue}throw new TypeError('Expected "'+a.name+'" to be '+(a.repeat?"an array":"a string"))}return r}}function K(n){return n.replace(/([.+*?=^!:${}()[\]|/\\])/g,"\\$1")}function wo(n){return n.replace(/([=!:$/()])/g,"\\$1")}function In(n){return n&&n.sensitive?"":"i"}function So(n,e){if(!e)return n;var t=n.source.match(/\((?!\?)/g);if(t)for(var o=0;o<t.length;o++)e.push({name:o,prefix:null,delimiter:null,optional:!1,repeat:!1,partial:!1,pattern:null});return n}function Eo(n,e,t){for(var o=[],i=0;i<n.length;i++)o.push(An(n[i],e,t).source);return new RegExp("(?:"+o.join("|")+")",In(t))}function Co(n,e,t){return Ln(It(n,t),e,t)}function Ln(n,e,t){t=t||{};for(var o=t.strict,i=t.start!==!1,r=t.end!==!1,s=K(t.delimiter||kn),l=t.delimiters||Rn,a=[].concat(t.endsWith||[]).map(K).concat("$").join("|"),c=i?"^":"",h=n.length===0,f=0;f<n.length;f++){var p=n[f];if(typeof p=="string")c+=K(p),h=f===n.length-1&&l.indexOf(p[p.length-1])>-1;else{var m=p.repeat?"(?:"+p.pattern+")(?:"+K(p.delimiter)+"(?:"+p.pattern+"))*":p.pattern;e&&e.push(p),p.optional?p.partial?c+=K(p.prefix)+"("+m+")?":c+="(?:"+K(p.prefix)+"("+m+"))?":c+=K(p.prefix)+"("+m+")"}}return r?(o||(c+="(?:"+s+")?"),c+=a==="$"?"$":"(?="+a+")"):(o||(c+="(?:"+s+"(?="+a+"))?"),h||(c+="(?="+s+"|"+a+")")),new RegExp(c,In(t))}function An(n,e,t){return n instanceof RegExp?So(n,e):Array.isArray(n)?Eo(n,e,t):Co(n,e,t)}fe.parse=mo;fe.compile=go;fe.tokensToFunction=vo;fe.tokensToRegExp=_o;const{hasOwnProperty:To}=Object.prototype,Tt=new Map;Tt.set("|false",{keys:[],pattern:/(?:)/});function Zt(n){try{return decodeURIComponent(n)}catch{return n}}function xo(n,e,t,o,i){t=!!t;const r=`${n}|${t}`;let s=Tt.get(r);if(!s){const c=[];s={keys:c,pattern:fe(n,c,{end:t,strict:n===""})},Tt.set(r,s)}const l=s.pattern.exec(e);if(!l)return null;const a=Object.assign({},i);for(let c=1;c<l.length;c++){const h=s.keys[c-1],f=h.name,p=l[c];(p!==void 0||!To.call(a,f))&&(h.repeat?a[f]=p?p.split(h.delimiter).map(Zt):[]:a[f]=p&&Zt(p))}return{path:l[0],keys:(o||[]).concat(s.keys),params:a}}function Pn(n,e,t,o,i){let r,s,l=0,a=n.path||"";return a.charAt(0)==="/"&&(t&&(a=a.substr(1)),t=!0),{next(c){if(n===c)return{done:!0};const h=n.__children=n.__children||n.children;if(!r&&(r=xo(a,e,!h,o,i),r))return{done:!1,value:{route:n,keys:r.keys,params:r.params,path:r.path}};if(r&&h)for(;l<h.length;){if(!s){const p=h[l];p.parent=n;let m=r.path.length;m>0&&e.charAt(m)==="/"&&(m+=1),s=Pn(p,e.substr(m),t,r.keys,r.params)}const f=s.next(c);if(!f.done)return{done:!1,value:f.value};s=null,l++}return{done:!0}}}}function $o(n){if(Q(n.route.action))return n.route.action(n)}function ko(n,e){let t=e;for(;t;)if(t=t.parent,t===n)return!0;return!1}function Ro(n){let e=`Path '${n.pathname}' is not properly resolved due to an error.`;const t=(n.route||{}).path;return t&&(e+=` Resolution had failed on route: '${t}'`),e}function No(n,e){const{route:t,path:o}=e;if(t&&!t.__synthetic){const i={path:o,route:t};if(!n.chain)n.chain=[];else if(t.parent){let r=n.chain.length;for(;r--&&n.chain[r].route&&n.chain[r].route!==t.parent;)n.chain.pop()}n.chain.push(i)}}class be{constructor(e,t={}){if(Object(e)!==e)throw new TypeError("Invalid routes");this.baseUrl=t.baseUrl||"",this.errorHandler=t.errorHandler,this.resolveRoute=t.resolveRoute||$o,this.context=Object.assign({resolver:this},t.context),this.root=Array.isArray(e)?{path:"",__children:e,parent:null,__synthetic:!0}:e,this.root.parent=null}getRoutes(){return[...this.root.__children]}setRoutes(e){Yt(e);const t=[...Be(e)];this.root.__children=t}addRoutes(e){return Yt(e),this.root.__children.push(...Be(e)),this.getRoutes()}removeRoutes(){this.setRoutes([])}resolve(e){const t=Object.assign({},this.context,D(e)?{pathname:e}:e),o=Pn(this.root,this.__normalizePathname(t.pathname),this.baseUrl),i=this.resolveRoute;let r=null,s=null,l=t;function a(c,h=r.value.route,f){const p=f===null&&r.value.route;return r=s||o.next(p),s=null,!c&&(r.done||!ko(h,r.value.route))?(s=r,Promise.resolve(le)):r.done?Promise.reject($n(t)):(l=Object.assign(l?{chain:l.chain?l.chain.slice(0):[]}:{},t,r.value),No(l,r.value),Promise.resolve(i(l)).then(m=>m!=null&&m!==le?(l.result=m.result||m,l):a(c,h,m)))}return t.next=a,Promise.resolve().then(()=>a(!0,this.root)).catch(c=>{const h=Ro(l);if(c?console.warn(h):c=new Error(h),c.context=c.context||l,c instanceof DOMException||(c.code=c.code||500),this.errorHandler)return l.result=this.errorHandler(c),l;throw c})}static __createUrl(e,t){return new URL(e,t)}get __effectiveBaseUrl(){return this.baseUrl?this.constructor.__createUrl(this.baseUrl,document.baseURI||document.URL).href.replace(/[^\/]*$/,""):""}__normalizePathname(e){if(!this.baseUrl)return e;const t=this.__effectiveBaseUrl,o=this.constructor.__createUrl(e,t).href;if(o.slice(0,t.length)===t)return o.slice(t.length)}}be.pathToRegexp=fe;const{pathToRegexp:en}=be,tn=new Map;function On(n,e,t){const o=e.name||e.component;if(o&&(n.has(o)?n.get(o).push(e):n.set(o,[e])),Array.isArray(t))for(let i=0;i<t.length;i++){const r=t[i];r.parent=e,On(n,r,r.__children||r.children)}}function nn(n,e){const t=n.get(e);if(t&&t.length>1)throw new Error(`Duplicate route with name "${e}". Try seting unique 'name' route properties.`);return t&&t[0]}function on(n){let e=n.path;return e=Array.isArray(e)?e[0]:e,e!==void 0?e:""}function Io(n,e={}){if(!(n instanceof be))throw new TypeError("An instance of Resolver is expected");const t=new Map;return(o,i)=>{let r=nn(t,o);if(!r&&(t.clear(),On(t,n.root,n.root.__children),r=nn(t,o),!r))throw new Error(`Route "${o}" not found`);let s=tn.get(r.fullPath);if(!s){let a=on(r),c=r.parent;for(;c;){const m=on(c);m&&(a=m.replace(/\/$/,"")+"/"+a.replace(/^\//,"")),c=c.parent}const h=en.parse(a),f=en.tokensToFunction(h),p=Object.create(null);for(let m=0;m<h.length;m++)D(h[m])||(p[h[m].name]=!0);s={toPath:f,keys:p},tn.set(a,s),r.fullPath=a}let l=s.toPath(i,e)||"/";if(e.stringifyQueryParams&&i){const a={},c=Object.keys(i);for(let f=0;f<c.length;f++){const p=c[f];s.keys[p]||(a[p]=i[p])}const h=e.stringifyQueryParams(a);h&&(l+=h.charAt(0)==="?"?h:`?${h}`)}return l}}let rn=[];function Lo(n){rn.forEach(e=>e.inactivate()),n.forEach(e=>e.activate()),rn=n}const Ao=n=>{const e=getComputedStyle(n).getPropertyValue("animation-name");return e&&e!=="none"},Po=(n,e)=>{const t=()=>{n.removeEventListener("animationend",t),e()};n.addEventListener("animationend",t)};function sn(n,e){return n.classList.add(e),new Promise(t=>{if(Ao(n)){const o=n.getBoundingClientRect(),i=`height: ${o.bottom-o.top}px; width: ${o.right-o.left}px`;n.setAttribute("style",`position: absolute; ${i}`),Po(n,()=>{n.classList.remove(e),n.removeAttribute("style"),t()})}else n.classList.remove(e),t()})}const Oo=256;function nt(n){return n!=null}function Mo(n){const e=Object.assign({},n);return delete e.next,e}function O({pathname:n="",search:e="",hash:t="",chain:o=[],params:i={},redirectFrom:r,resolver:s},l){const a=o.map(c=>c.route);return{baseUrl:s&&s.baseUrl||"",pathname:n,search:e,hash:t,routes:a,route:l||a.length&&a[a.length-1]||null,params:i,redirectFrom:r,getUrl:(c={})=>Oe(Y.pathToRegexp.compile(Mn(a))(Object.assign({},i,c)),s)}}function an(n,e){const t=Object.assign({},n.params);return{redirect:{pathname:e,from:n.pathname,params:t}}}function Do(n,e){e.location=O(n);const t=n.chain.map(o=>o.route).indexOf(n.route);return n.chain[t].element=e,e}function Pe(n,e,t){if(Q(n))return n.apply(t,e)}function ln(n,e,t){return o=>{if(o&&(o.cancel||o.redirect))return o;if(t)return Pe(t[n],e,t)}}function Vo(n,e){if(!Array.isArray(n)&&!je(n))throw new Error(V(`Incorrect "children" value for the route ${e.path}: expected array or object, but got ${n}`));e.__children=[];const t=Be(n);for(let o=0;o<t.length;o++)xn(t[o]),e.__children.push(t[o])}function Le(n){if(n&&n.length){const e=n[0].parentNode;for(let t=0;t<n.length;t++)e.removeChild(n[t])}}function Oe(n,e){const t=e.__effectiveBaseUrl;return t?e.constructor.__createUrl(n.replace(/^\//,""),t).pathname:n}function Mn(n){return n.map(e=>e.path).reduce((e,t)=>t.length?e.replace(/\/$/,"")+"/"+t.replace(/^\//,""):e,"")}class Y extends be{constructor(e,t){const o=document.head.querySelector("base"),i=o&&o.getAttribute("href");super([],Object.assign({baseUrl:i&&be.__createUrl(i,document.URL).pathname.replace(/[^\/]*$/,"")},t)),this.resolveRoute=s=>this.__resolveRoute(s);const r=Y.NavigationTrigger;Y.setTriggers.apply(Y,Object.keys(r).map(s=>r[s])),this.baseUrl,this.ready,this.ready=Promise.resolve(e),this.location,this.location=O({resolver:this}),this.__lastStartedRenderId=0,this.__navigationEventHandler=this.__onNavigationEvent.bind(this),this.setOutlet(e),this.subscribe(),this.__createdByRouter=new WeakMap,this.__addedByRouter=new WeakMap}__resolveRoute(e){const t=e.route;let o=Promise.resolve();Q(t.children)&&(o=o.then(()=>t.children(Mo(e))).then(r=>{!nt(r)&&!Q(t.children)&&(r=t.children),Vo(r,t)}));const i={redirect:r=>an(e,r),component:r=>{const s=document.createElement(r);return this.__createdByRouter.set(s,!0),s}};return o.then(()=>{if(this.__isLatestRender(e))return Pe(t.action,[e,i],t)}).then(r=>{if(nt(r)&&(r instanceof HTMLElement||r.redirect||r===le))return r;if(D(t.redirect))return i.redirect(t.redirect);if(t.bundle)return co(t.bundle).then(()=>{},()=>{throw new Error(V(`Bundle not found: ${t.bundle}. Check if the file name is correct`))})}).then(r=>{if(nt(r))return r;if(D(t.component))return i.component(t.component)})}setOutlet(e){e&&this.__ensureOutlet(e),this.__outlet=e}getOutlet(){return this.__outlet}setRoutes(e,t=!1){return this.__previousContext=void 0,this.__urlForName=void 0,super.setRoutes(e),t||this.__onNavigationEvent(),this.ready}render(e,t){const o=++this.__lastStartedRenderId,i=Object.assign({search:"",hash:""},D(e)?{pathname:e}:e,{__renderId:o});return this.ready=this.resolve(i).then(r=>this.__fullyResolveChain(r)).then(r=>{if(this.__isLatestRender(r)){const s=this.__previousContext;if(r===s)return this.__updateBrowserHistory(s,!0),this.location;if(this.location=O(r),t&&this.__updateBrowserHistory(r,o===1),_e("location-changed",{router:this,location:this.location}),r.__skipAttach)return this.__copyUnchangedElements(r,s),this.__previousContext=r,this.location;this.__addAppearingContent(r,s);const l=this.__animateIfNeeded(r);return this.__runOnAfterEnterCallbacks(r),this.__runOnAfterLeaveCallbacks(r,s),l.then(()=>{if(this.__isLatestRender(r))return this.__removeDisappearingContent(),this.__previousContext=r,this.location})}}).catch(r=>{if(o===this.__lastStartedRenderId)throw t&&this.__updateBrowserHistory(i),Le(this.__outlet&&this.__outlet.children),this.location=O(Object.assign(i,{resolver:this})),_e("error",Object.assign({router:this,error:r},i)),r}),this.ready}__fullyResolveChain(e,t=e){return this.__findComponentContextAfterAllRedirects(t).then(o=>{const r=o!==t?o:e,l=Oe(Mn(o.chain),o.resolver)===o.pathname,a=(c,h=c.route,f)=>c.next(void 0,h,f).then(p=>p===null||p===le?l?c:h.parent!==null?a(c,h.parent,p):p:p);return a(o).then(c=>{if(c===null||c===le)throw $n(r);return c&&c!==le&&c!==o?this.__fullyResolveChain(r,c):this.__amendWithOnBeforeCallbacks(o)})})}__findComponentContextAfterAllRedirects(e){const t=e.result;return t instanceof HTMLElement?(Do(e,t),Promise.resolve(e)):t.redirect?this.__redirect(t.redirect,e.__redirectCount,e.__renderId).then(o=>this.__findComponentContextAfterAllRedirects(o)):t instanceof Error?Promise.reject(t):Promise.reject(new Error(V(`Invalid route resolution result for path "${e.pathname}". Expected redirect object or HTML element, but got: "${lo(t)}". Double check the action return value for the route.`)))}__amendWithOnBeforeCallbacks(e){return this.__runOnBeforeCallbacks(e).then(t=>t===this.__previousContext||t===e?t:this.__fullyResolveChain(t))}__runOnBeforeCallbacks(e){const t=this.__previousContext||{},o=t.chain||[],i=e.chain;let r=Promise.resolve();const s=()=>({cancel:!0}),l=a=>an(e,a);if(e.__divergedChainIndex=0,e.__skipAttach=!1,o.length){for(let a=0;a<Math.min(o.length,i.length)&&!(o[a].route!==i[a].route||o[a].path!==i[a].path&&o[a].element!==i[a].element||!this.__isReusableElement(o[a].element,i[a].element));a=++e.__divergedChainIndex);if(e.__skipAttach=i.length===o.length&&e.__divergedChainIndex==i.length&&this.__isReusableElement(e.result,t.result),e.__skipAttach){for(let a=i.length-1;a>=0;a--)r=this.__runOnBeforeLeaveCallbacks(r,e,{prevent:s},o[a]);for(let a=0;a<i.length;a++)r=this.__runOnBeforeEnterCallbacks(r,e,{prevent:s,redirect:l},i[a]),o[a].element.location=O(e,o[a].route)}else for(let a=o.length-1;a>=e.__divergedChainIndex;a--)r=this.__runOnBeforeLeaveCallbacks(r,e,{prevent:s},o[a])}if(!e.__skipAttach)for(let a=0;a<i.length;a++)a<e.__divergedChainIndex?a<o.length&&o[a].element&&(o[a].element.location=O(e,o[a].route)):(r=this.__runOnBeforeEnterCallbacks(r,e,{prevent:s,redirect:l},i[a]),i[a].element&&(i[a].element.location=O(e,i[a].route)));return r.then(a=>{if(a){if(a.cancel)return this.__previousContext.__renderId=e.__renderId,this.__previousContext;if(a.redirect)return this.__redirect(a.redirect,e.__redirectCount,e.__renderId)}return e})}__runOnBeforeLeaveCallbacks(e,t,o,i){const r=O(t);return e.then(s=>{if(this.__isLatestRender(t))return ln("onBeforeLeave",[r,o,this],i.element)(s)}).then(s=>{if(!(s||{}).redirect)return s})}__runOnBeforeEnterCallbacks(e,t,o,i){const r=O(t,i.route);return e.then(s=>{if(this.__isLatestRender(t))return ln("onBeforeEnter",[r,o,this],i.element)(s)})}__isReusableElement(e,t){return e&&t?this.__createdByRouter.get(e)&&this.__createdByRouter.get(t)?e.localName===t.localName:e===t:!1}__isLatestRender(e){return e.__renderId===this.__lastStartedRenderId}__redirect(e,t,o){if(t>Oo)throw new Error(V(`Too many redirects when rendering ${e.from}`));return this.resolve({pathname:this.urlForPath(e.pathname,e.params),redirectFrom:e.from,__redirectCount:(t||0)+1,__renderId:o})}__ensureOutlet(e=this.__outlet){if(!(e instanceof Node))throw new TypeError(V(`Expected router outlet to be a valid DOM Node (but got ${e})`))}__updateBrowserHistory({pathname:e,search:t="",hash:o=""},i){if(window.location.pathname!==e||window.location.search!==t||window.location.hash!==o){const r=i?"replaceState":"pushState";window.history[r](null,document.title,e+t+o),window.dispatchEvent(new PopStateEvent("popstate",{state:"vaadin-router-ignore"}))}}__copyUnchangedElements(e,t){let o=this.__outlet;for(let i=0;i<e.__divergedChainIndex;i++){const r=t&&t.chain[i].element;if(r)if(r.parentNode===o)e.chain[i].element=r,o=r;else break}return o}__addAppearingContent(e,t){this.__ensureOutlet(),this.__removeAppearingContent();const o=this.__copyUnchangedElements(e,t);this.__appearingContent=[],this.__disappearingContent=Array.from(o.children).filter(r=>this.__addedByRouter.get(r)&&r!==e.result);let i=o;for(let r=e.__divergedChainIndex;r<e.chain.length;r++){const s=e.chain[r].element;s&&(i.appendChild(s),this.__addedByRouter.set(s,!0),i===o&&this.__appearingContent.push(s),i=s)}}__removeDisappearingContent(){this.__disappearingContent&&Le(this.__disappearingContent),this.__disappearingContent=null,this.__appearingContent=null}__removeAppearingContent(){this.__disappearingContent&&this.__appearingContent&&(Le(this.__appearingContent),this.__disappearingContent=null,this.__appearingContent=null)}__runOnAfterLeaveCallbacks(e,t){if(t)for(let o=t.chain.length-1;o>=e.__divergedChainIndex&&this.__isLatestRender(e);o--){const i=t.chain[o].element;if(i)try{const r=O(e);Pe(i.onAfterLeave,[r,{},t.resolver],i)}finally{this.__disappearingContent.indexOf(i)>-1&&Le(i.children)}}}__runOnAfterEnterCallbacks(e){for(let t=e.__divergedChainIndex;t<e.chain.length&&this.__isLatestRender(e);t++){const o=e.chain[t].element||{},i=O(e,e.chain[t].route);Pe(o.onAfterEnter,[i,{},e.resolver],o)}}__animateIfNeeded(e){const t=(this.__disappearingContent||[])[0],o=(this.__appearingContent||[])[0],i=[],r=e.chain;let s;for(let l=r.length;l>0;l--)if(r[l-1].route.animate){s=r[l-1].route.animate;break}if(t&&o&&s){const l=je(s)&&s.leave||"leaving",a=je(s)&&s.enter||"entering";i.push(sn(t,l)),i.push(sn(o,a))}return Promise.all(i).then(()=>e)}subscribe(){window.addEventListener("vaadin-router-go",this.__navigationEventHandler)}unsubscribe(){window.removeEventListener("vaadin-router-go",this.__navigationEventHandler)}__onNavigationEvent(e){const{pathname:t,search:o,hash:i}=e?e.detail:window.location;D(this.__normalizePathname(t))&&(e&&e.preventDefault&&e.preventDefault(),this.render({pathname:t,search:o,hash:i},!0))}static setTriggers(...e){Lo(e)}urlForName(e,t){return this.__urlForName||(this.__urlForName=Io(this)),Oe(this.__urlForName(e,t),this)}urlForPath(e,t){return Oe(Y.pathToRegexp.compile(e)(t),this)}static go(e){const{pathname:t,search:o,hash:i}=D(e)?this.__createUrl(e,"http://a"):e;return _e("go",{pathname:t,search:o,hash:i})}}const Uo=/\/\*[\*!]\s+vaadin-dev-mode:start([\s\S]*)vaadin-dev-mode:end\s+\*\*\//i,Me=window.Vaadin&&window.Vaadin.Flow&&window.Vaadin.Flow.clients;function zo(){function n(){return!0}return Dn(n)}function Fo(){try{return Bo()?!0:Ho()?Me?!Wo():!zo():!1}catch{return!1}}function Bo(){return localStorage.getItem("vaadin.developmentmode.force")}function Ho(){return["localhost","127.0.0.1"].indexOf(window.location.hostname)>=0}function Wo(){return!!(Me&&Object.keys(Me).map(e=>Me[e]).filter(e=>e.productionMode).length>0)}function Dn(n,e){if(typeof n!="function")return;const t=Uo.exec(n.toString());if(t)try{n=new Function(t[1])}catch(o){console.log("vaadin-development-mode-detector: uncommentAndRun() failed",o)}return n(e)}window.Vaadin=window.Vaadin||{};const cn=function(n,e){if(window.Vaadin.developmentMode)return Dn(n,e)};window.Vaadin.developmentMode===void 0&&(window.Vaadin.developmentMode=Fo());function jo(){}const Go=function(){if(typeof cn=="function")return cn(jo)};window.Vaadin=window.Vaadin||{};window.Vaadin.registrations=window.Vaadin.registrations||[];window.Vaadin.registrations.push({is:"@vaadin/router",version:"1.7.4"});Go();Y.NavigationTrigger={POPSTATE:fo,CLICK:uo};var ot,E;(function(n){n.CONNECTED="connected",n.LOADING="loading",n.RECONNECTING="reconnecting",n.CONNECTION_LOST="connection-lost"})(E||(E={}));class qo{constructor(e){this.stateChangeListeners=new Set,this.loadingCount=0,this.connectionState=e,this.serviceWorkerMessageListener=this.serviceWorkerMessageListener.bind(this),navigator.serviceWorker&&(navigator.serviceWorker.addEventListener("message",this.serviceWorkerMessageListener),navigator.serviceWorker.ready.then(t=>{var o;(o=t==null?void 0:t.active)===null||o===void 0||o.postMessage({method:"Vaadin.ServiceWorker.isConnectionLost",id:"Vaadin.ServiceWorker.isConnectionLost"})}))}addStateChangeListener(e){this.stateChangeListeners.add(e)}removeStateChangeListener(e){this.stateChangeListeners.delete(e)}loadingStarted(){this.state=E.LOADING,this.loadingCount+=1}loadingFinished(){this.decreaseLoadingCount(E.CONNECTED)}loadingFailed(){this.decreaseLoadingCount(E.CONNECTION_LOST)}decreaseLoadingCount(e){this.loadingCount>0&&(this.loadingCount-=1,this.loadingCount===0&&(this.state=e))}get state(){return this.connectionState}set state(e){if(e!==this.connectionState){const t=this.connectionState;this.connectionState=e,this.loadingCount=0;for(const o of this.stateChangeListeners)o(t,this.connectionState)}}get online(){return this.connectionState===E.CONNECTED||this.connectionState===E.LOADING}get offline(){return!this.online}serviceWorkerMessageListener(e){typeof e.data=="object"&&e.data.id==="Vaadin.ServiceWorker.isConnectionLost"&&(e.data.result===!0&&(this.state=E.CONNECTION_LOST),navigator.serviceWorker.removeEventListener("message",this.serviceWorkerMessageListener))}}const Ko=n=>!!(n==="localhost"||n==="[::1]"||n.match(/^127\.\d+\.\d+\.\d+$/)),Ae=window;if(!(!((ot=Ae.Vaadin)===null||ot===void 0)&&ot.connectionState)){let n;Ko(window.location.hostname)?n=!0:n=navigator.onLine,Ae.Vaadin=Ae.Vaadin||{},Ae.Vaadin.connectionState=new qo(n?E.CONNECTED:E.CONNECTION_LOST)}function L(n,e,t,o){var i=arguments.length,r=i<3?e:o===null?o=Object.getOwnPropertyDescriptor(e,t):o,s;if(typeof Reflect=="object"&&typeof Reflect.decorate=="function")r=Reflect.decorate(n,e,t,o);else for(var l=n.length-1;l>=0;l--)(s=n[l])&&(r=(i<3?s(r):i>3?s(e,t,r):s(e,t))||r);return i>3&&r&&Object.defineProperty(e,t,r),r}/**
 * @license
 * Copyright 2019 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */const Yo=!1,De=window,Lt=De.ShadowRoot&&(De.ShadyCSS===void 0||De.ShadyCSS.nativeShadow)&&"adoptedStyleSheets"in Document.prototype&&"replace"in CSSStyleSheet.prototype,At=Symbol(),dn=new WeakMap;class Pt{constructor(e,t,o){if(this._$cssResult$=!0,o!==At)throw new Error("CSSResult is not constructable. Use `unsafeCSS` or `css` instead.");this.cssText=e,this._strings=t}get styleSheet(){let e=this._styleSheet;const t=this._strings;if(Lt&&e===void 0){const o=t!==void 0&&t.length===1;o&&(e=dn.get(t)),e===void 0&&((this._styleSheet=e=new CSSStyleSheet).replaceSync(this.cssText),o&&dn.set(t,e))}return e}toString(){return this.cssText}}const Jo=n=>{if(n._$cssResult$===!0)return n.cssText;if(typeof n=="number")return n;throw new Error(`Value passed to 'css' function must be a 'css' function result: ${n}. Use 'unsafeCSS' to pass non-literal values, but take care to ensure page security.`)},Xo=n=>new Pt(typeof n=="string"?n:String(n),void 0,At),$=(n,...e)=>{const t=n.length===1?n[0]:e.reduce((o,i,r)=>o+Jo(i)+n[r+1],n[0]);return new Pt(t,n,At)},Qo=(n,e)=>{Lt?n.adoptedStyleSheets=e.map(t=>t instanceof CSSStyleSheet?t:t.styleSheet):e.forEach(t=>{const o=document.createElement("style"),i=De.litNonce;i!==void 0&&o.setAttribute("nonce",i),o.textContent=t.cssText,n.appendChild(o)})},Zo=n=>{let e="";for(const t of n.cssRules)e+=t.cssText;return Xo(e)},hn=Lt||Yo?n=>n:n=>n instanceof CSSStyleSheet?Zo(n):n;/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */var it,rt,st,Vn;const F=window;let Un,J;const un=F.trustedTypes,ei=un?un.emptyScript:"",Ve=F.reactiveElementPolyfillSupportDevMode;{const n=(it=F.litIssuedWarnings)!==null&&it!==void 0?it:F.litIssuedWarnings=new Set;J=(e,t)=>{t+=` See https://lit.dev/msg/${e} for more information.`,n.has(t)||(console.warn(t),n.add(t))},J("dev-mode","Lit is in dev mode. Not recommended for production!"),!((rt=F.ShadyDOM)===null||rt===void 0)&&rt.inUse&&Ve===void 0&&J("polyfill-support-missing","Shadow DOM is being polyfilled via `ShadyDOM` but the `polyfill-support` module has not been loaded."),Un=e=>({then:(t,o)=>{J("request-update-promise",`The \`requestUpdate\` method should no longer return a Promise but does so on \`${e}\`. Use \`updateComplete\` instead.`),t!==void 0&&t(!1)}})}const at=n=>{F.emitLitDebugLogEvents&&F.dispatchEvent(new CustomEvent("lit-debug",{detail:n}))},zn=(n,e)=>n,xt={toAttribute(n,e){switch(e){case Boolean:n=n?ei:null;break;case Object:case Array:n=n==null?n:JSON.stringify(n);break}return n},fromAttribute(n,e){let t=n;switch(e){case Boolean:t=n!==null;break;case Number:t=n===null?null:Number(n);break;case Object:case Array:try{t=JSON.parse(n)}catch{t=null}break}return t}},Fn=(n,e)=>e!==n&&(e===e||n===n),lt={attribute:!0,type:String,converter:xt,reflect:!1,hasChanged:Fn},$t="finalized";class B extends HTMLElement{constructor(){super(),this.__instanceProperties=new Map,this.isUpdatePending=!1,this.hasUpdated=!1,this.__reflectingProperty=null,this._initialize()}static addInitializer(e){var t;this.finalize(),((t=this._initializers)!==null&&t!==void 0?t:this._initializers=[]).push(e)}static get observedAttributes(){this.finalize();const e=[];return this.elementProperties.forEach((t,o)=>{const i=this.__attributeNameForProperty(o,t);i!==void 0&&(this.__attributeToPropertyMap.set(i,o),e.push(i))}),e}static createProperty(e,t=lt){var o;if(t.state&&(t.attribute=!1),this.finalize(),this.elementProperties.set(e,t),!t.noAccessor&&!this.prototype.hasOwnProperty(e)){const i=typeof e=="symbol"?Symbol():`__${e}`,r=this.getPropertyDescriptor(e,i,t);r!==void 0&&(Object.defineProperty(this.prototype,e,r),this.hasOwnProperty("__reactivePropertyKeys")||(this.__reactivePropertyKeys=new Set((o=this.__reactivePropertyKeys)!==null&&o!==void 0?o:[])),this.__reactivePropertyKeys.add(e))}}static getPropertyDescriptor(e,t,o){return{get(){return this[t]},set(i){const r=this[e];this[t]=i,this.requestUpdate(e,r,o)},configurable:!0,enumerable:!0}}static getPropertyOptions(e){return this.elementProperties.get(e)||lt}static finalize(){if(this.hasOwnProperty($t))return!1;this[$t]=!0;const e=Object.getPrototypeOf(this);if(e.finalize(),e._initializers!==void 0&&(this._initializers=[...e._initializers]),this.elementProperties=new Map(e.elementProperties),this.__attributeToPropertyMap=new Map,this.hasOwnProperty(zn("properties"))){const t=this.properties,o=[...Object.getOwnPropertyNames(t),...Object.getOwnPropertySymbols(t)];for(const i of o)this.createProperty(i,t[i])}this.elementStyles=this.finalizeStyles(this.styles);{const t=(o,i=!1)=>{this.prototype.hasOwnProperty(o)&&J(i?"renamed-api":"removed-api",`\`${o}\` is implemented on class ${this.name}. It has been ${i?"renamed":"removed"} in this version of LitElement.`)};t("initialize"),t("requestUpdateInternal"),t("_getUpdateComplete",!0)}return!0}static finalizeStyles(e){const t=[];if(Array.isArray(e)){const o=new Set(e.flat(1/0).reverse());for(const i of o)t.unshift(hn(i))}else e!==void 0&&t.push(hn(e));return t}static __attributeNameForProperty(e,t){const o=t.attribute;return o===!1?void 0:typeof o=="string"?o:typeof e=="string"?e.toLowerCase():void 0}_initialize(){var e;this.__updatePromise=new Promise(t=>this.enableUpdating=t),this._$changedProperties=new Map,this.__saveInstanceProperties(),this.requestUpdate(),(e=this.constructor._initializers)===null||e===void 0||e.forEach(t=>t(this))}addController(e){var t,o;((t=this.__controllers)!==null&&t!==void 0?t:this.__controllers=[]).push(e),this.renderRoot!==void 0&&this.isConnected&&((o=e.hostConnected)===null||o===void 0||o.call(e))}removeController(e){var t;(t=this.__controllers)===null||t===void 0||t.splice(this.__controllers.indexOf(e)>>>0,1)}__saveInstanceProperties(){this.constructor.elementProperties.forEach((e,t)=>{this.hasOwnProperty(t)&&(this.__instanceProperties.set(t,this[t]),delete this[t])})}createRenderRoot(){var e;const t=(e=this.shadowRoot)!==null&&e!==void 0?e:this.attachShadow(this.constructor.shadowRootOptions);return Qo(t,this.constructor.elementStyles),t}connectedCallback(){var e;this.renderRoot===void 0&&(this.renderRoot=this.createRenderRoot()),this.enableUpdating(!0),(e=this.__controllers)===null||e===void 0||e.forEach(t=>{var o;return(o=t.hostConnected)===null||o===void 0?void 0:o.call(t)})}enableUpdating(e){}disconnectedCallback(){var e;(e=this.__controllers)===null||e===void 0||e.forEach(t=>{var o;return(o=t.hostDisconnected)===null||o===void 0?void 0:o.call(t)})}attributeChangedCallback(e,t,o){this._$attributeToProperty(e,o)}__propertyToAttribute(e,t,o=lt){var i;const r=this.constructor.__attributeNameForProperty(e,o);if(r!==void 0&&o.reflect===!0){const l=(((i=o.converter)===null||i===void 0?void 0:i.toAttribute)!==void 0?o.converter:xt).toAttribute(t,o.type);this.constructor.enabledWarnings.indexOf("migration")>=0&&l===void 0&&J("undefined-attribute-value",`The attribute value for the ${e} property is undefined on element ${this.localName}. The attribute will be removed, but in the previous version of \`ReactiveElement\`, the attribute would not have changed.`),this.__reflectingProperty=e,l==null?this.removeAttribute(r):this.setAttribute(r,l),this.__reflectingProperty=null}}_$attributeToProperty(e,t){var o;const i=this.constructor,r=i.__attributeToPropertyMap.get(e);if(r!==void 0&&this.__reflectingProperty!==r){const s=i.getPropertyOptions(r),l=typeof s.converter=="function"?{fromAttribute:s.converter}:((o=s.converter)===null||o===void 0?void 0:o.fromAttribute)!==void 0?s.converter:xt;this.__reflectingProperty=r,this[r]=l.fromAttribute(t,s.type),this.__reflectingProperty=null}}requestUpdate(e,t,o){let i=!0;return e!==void 0&&(o=o||this.constructor.getPropertyOptions(e),(o.hasChanged||Fn)(this[e],t)?(this._$changedProperties.has(e)||this._$changedProperties.set(e,t),o.reflect===!0&&this.__reflectingProperty!==e&&(this.__reflectingProperties===void 0&&(this.__reflectingProperties=new Map),this.__reflectingProperties.set(e,o))):i=!1),!this.isUpdatePending&&i&&(this.__updatePromise=this.__enqueueUpdate()),Un(this.localName)}async __enqueueUpdate(){this.isUpdatePending=!0;try{await this.__updatePromise}catch(t){Promise.reject(t)}const e=this.scheduleUpdate();return e!=null&&await e,!this.isUpdatePending}scheduleUpdate(){return this.performUpdate()}performUpdate(){var e,t;if(!this.isUpdatePending)return;if(at==null||at({kind:"update"}),!this.hasUpdated){const r=[];if((e=this.constructor.__reactivePropertyKeys)===null||e===void 0||e.forEach(s=>{var l;this.hasOwnProperty(s)&&!(!((l=this.__instanceProperties)===null||l===void 0)&&l.has(s))&&r.push(s)}),r.length)throw new Error(`The following properties on element ${this.localName} will not trigger updates as expected because they are set using class fields: ${r.join(", ")}. Native class fields and some compiled output will overwrite accessors used for detecting changes. See https://lit.dev/msg/class-field-shadowing for more information.`)}this.__instanceProperties&&(this.__instanceProperties.forEach((r,s)=>this[s]=r),this.__instanceProperties=void 0);let o=!1;const i=this._$changedProperties;try{o=this.shouldUpdate(i),o?(this.willUpdate(i),(t=this.__controllers)===null||t===void 0||t.forEach(r=>{var s;return(s=r.hostUpdate)===null||s===void 0?void 0:s.call(r)}),this.update(i)):this.__markUpdated()}catch(r){throw o=!1,this.__markUpdated(),r}o&&this._$didUpdate(i)}willUpdate(e){}_$didUpdate(e){var t;(t=this.__controllers)===null||t===void 0||t.forEach(o=>{var i;return(i=o.hostUpdated)===null||i===void 0?void 0:i.call(o)}),this.hasUpdated||(this.hasUpdated=!0,this.firstUpdated(e)),this.updated(e),this.isUpdatePending&&this.constructor.enabledWarnings.indexOf("change-in-update")>=0&&J("change-in-update",`Element ${this.localName} scheduled an update (generally because a property was set) after an update completed, causing a new update to be scheduled. This is inefficient and should be avoided unless the next update can only be scheduled as a side effect of the previous update.`)}__markUpdated(){this._$changedProperties=new Map,this.isUpdatePending=!1}get updateComplete(){return this.getUpdateComplete()}getUpdateComplete(){return this.__updatePromise}shouldUpdate(e){return!0}update(e){this.__reflectingProperties!==void 0&&(this.__reflectingProperties.forEach((t,o)=>this.__propertyToAttribute(o,this[o],t)),this.__reflectingProperties=void 0),this.__markUpdated()}updated(e){}firstUpdated(e){}}Vn=$t;B[Vn]=!0;B.elementProperties=new Map;B.elementStyles=[];B.shadowRootOptions={mode:"open"};Ve==null||Ve({ReactiveElement:B});{B.enabledWarnings=["change-in-update"];const n=function(e){e.hasOwnProperty(zn("enabledWarnings"))||(e.enabledWarnings=e.enabledWarnings.slice())};B.enableWarning=function(e){n(this),this.enabledWarnings.indexOf(e)<0&&this.enabledWarnings.push(e)},B.disableWarning=function(e){n(this);const t=this.enabledWarnings.indexOf(e);t>=0&&this.enabledWarnings.splice(t,1)}}((st=F.reactiveElementVersions)!==null&&st!==void 0?st:F.reactiveElementVersions=[]).push("1.6.1");F.reactiveElementVersions.length>1&&J("multiple-versions","Multiple versions of Lit loaded. Loading multiple versions is not recommended.");/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */var ct,dt,ht,ut;const I=window,g=n=>{I.emitLitDebugLogEvents&&I.dispatchEvent(new CustomEvent("lit-debug",{detail:n}))};let ti=0,Ge;(ct=I.litIssuedWarnings)!==null&&ct!==void 0||(I.litIssuedWarnings=new Set),Ge=(n,e)=>{e+=n?` See https://lit.dev/msg/${n} for more information.`:"",I.litIssuedWarnings.has(e)||(console.warn(e),I.litIssuedWarnings.add(e))},Ge("dev-mode","Lit is in dev mode. Not recommended for production!");const P=!((dt=I.ShadyDOM)===null||dt===void 0)&&dt.inUse&&((ht=I.ShadyDOM)===null||ht===void 0?void 0:ht.noPatch)===!0?I.ShadyDOM.wrap:n=>n,de=I.trustedTypes,pn=de?de.createPolicy("lit-html",{createHTML:n=>n}):void 0,ni=n=>n,Ye=(n,e,t)=>ni,oi=n=>{if(ee!==Ye)throw new Error("Attempted to overwrite existing lit-html security policy. setSanitizeDOMValueFactory should be called at most once.");ee=n},ii=()=>{ee=Ye},kt=(n,e,t)=>ee(n,e,t),Rt="$lit$",j=`lit$${String(Math.random()).slice(9)}$`,Bn="?"+j,ri=`<${Bn}>`,he=document,we=()=>he.createComment(""),Se=n=>n===null||typeof n!="object"&&typeof n!="function",Hn=Array.isArray,si=n=>Hn(n)||typeof(n==null?void 0:n[Symbol.iterator])=="function",pt=`[ 	
\f\r]`,ai=`[^ 	
\f\r"'\`<>=]`,li=`[^\\s"'>=/]`,ge=/<(?:(!--|\/[^a-zA-Z])|(\/?[a-zA-Z][^>\s]*)|(\/?$))/g,fn=1,ft=2,ci=3,mn=/-->/g,gn=/>/g,X=new RegExp(`>|${pt}(?:(${li}+)(${pt}*=${pt}*(?:${ai}|("|')|))|$)`,"g"),di=0,vn=1,hi=2,_n=3,mt=/'/g,gt=/"/g,Wn=/^(?:script|style|textarea|title)$/i,ui=1,qe=2,Ot=1,Ke=2,pi=3,fi=4,mi=5,Mt=6,gi=7,jn=n=>(e,...t)=>(e.some(o=>o===void 0)&&console.warn(`Some template strings are undefined.
This is probably caused by illegal octal escape sequences.`),{_$litType$:n,strings:e,values:t}),R=jn(ui),sr=jn(qe),Z=Symbol.for("lit-noChange"),S=Symbol.for("lit-nothing"),yn=new WeakMap,ce=he.createTreeWalker(he,129,null,!1);let ee=Ye;const vi=(n,e)=>{const t=n.length-1,o=[];let i=e===qe?"<svg>":"",r,s=ge;for(let a=0;a<t;a++){const c=n[a];let h=-1,f,p=0,m;for(;p<c.length&&(s.lastIndex=p,m=s.exec(c),m!==null);)if(p=s.lastIndex,s===ge){if(m[fn]==="!--")s=mn;else if(m[fn]!==void 0)s=gn;else if(m[ft]!==void 0)Wn.test(m[ft])&&(r=new RegExp(`</${m[ft]}`,"g")),s=X;else if(m[ci]!==void 0)throw new Error("Bindings in tag names are not supported. Please use static templates instead. See https://lit.dev/docs/templates/expressions/#static-expressions")}else s===X?m[di]===">"?(s=r??ge,h=-1):m[vn]===void 0?h=-2:(h=s.lastIndex-m[hi].length,f=m[vn],s=m[_n]===void 0?X:m[_n]==='"'?gt:mt):s===gt||s===mt?s=X:s===mn||s===gn?s=ge:(s=X,r=void 0);console.assert(h===-1||s===X||s===mt||s===gt,"unexpected parse state B");const G=s===X&&n[a+1].startsWith("/>")?" ":"";i+=s===ge?c+ri:h>=0?(o.push(f),c.slice(0,h)+Rt+c.slice(h)+j+G):c+j+(h===-2?(o.push(void 0),a):G)}const l=i+(n[t]||"<?>")+(e===qe?"</svg>":"");if(!Array.isArray(n)||!n.hasOwnProperty("raw")){let a="invalid template strings array";throw a=`
          Internal Error: expected template strings to be an array
          with a 'raw' field. Faking a template strings array by
          calling html or svg like an ordinary function is effectively
          the same as calling unsafeHtml and can lead to major security
          issues, e.g. opening your code up to XSS attacks.

          If you're using the html or svg tagged template functions normally
          and still seeing this error, please file a bug at
          https://github.com/lit/lit/issues/new?template=bug_report.md
          and include information about your build tooling, if any.
        `.trim().replace(/\n */g,`
`),new Error(a)}return[pn!==void 0?pn.createHTML(l):l,o]};class Ee{constructor({strings:e,["_$litType$"]:t},o){this.parts=[];let i,r=0,s=0;const l=e.length-1,a=this.parts,[c,h]=vi(e,t);if(this.el=Ee.createElement(c,o),ce.currentNode=this.el.content,t===qe){const f=this.el.content,p=f.firstChild;p.remove(),f.append(...p.childNodes)}for(;(i=ce.nextNode())!==null&&a.length<l;){if(i.nodeType===1){{const f=i.localName;if(/^(?:textarea|template)$/i.test(f)&&i.innerHTML.includes(j)){const p=`Expressions are not supported inside \`${f}\` elements. See https://lit.dev/msg/expression-in-${f} for more information.`;if(f==="template")throw new Error(p);Ge("",p)}}if(i.hasAttributes()){const f=[];for(const p of i.getAttributeNames())if(p.endsWith(Rt)||p.startsWith(j)){const m=h[s++];if(f.push(p),m!==void 0){const q=i.getAttribute(m.toLowerCase()+Rt).split(j),H=/([.?@])?(.*)/.exec(m);a.push({type:Ot,index:r,name:H[2],strings:q,ctor:H[1]==="."?yi:H[1]==="?"?wi:H[1]==="@"?Si:Je})}else a.push({type:Mt,index:r})}for(const p of f)i.removeAttribute(p)}if(Wn.test(i.tagName)){const f=i.textContent.split(j),p=f.length-1;if(p>0){i.textContent=de?de.emptyScript:"";for(let m=0;m<p;m++)i.append(f[m],we()),ce.nextNode(),a.push({type:Ke,index:++r});i.append(f[p],we())}}}else if(i.nodeType===8)if(i.data===Bn)a.push({type:Ke,index:r});else{let p=-1;for(;(p=i.data.indexOf(j,p+1))!==-1;)a.push({type:gi,index:r}),p+=j.length-1}r++}g==null||g({kind:"template prep",template:this,clonableTemplate:this.el,parts:this.parts,strings:e})}static createElement(e,t){const o=he.createElement("template");return o.innerHTML=e,o}}function ue(n,e,t=n,o){var i,r,s,l;if(e===Z)return e;let a=o!==void 0?(i=t.__directives)===null||i===void 0?void 0:i[o]:t.__directive;const c=Se(e)?void 0:e._$litDirective$;return(a==null?void 0:a.constructor)!==c&&((r=a==null?void 0:a._$notifyDirectiveConnectionChanged)===null||r===void 0||r.call(a,!1),c===void 0?a=void 0:(a=new c(n),a._$initialize(n,t,o)),o!==void 0?((s=(l=t).__directives)!==null&&s!==void 0?s:l.__directives=[])[o]=a:t.__directive=a),a!==void 0&&(e=ue(n,a._$resolve(n,e.values),a,o)),e}class _i{constructor(e,t){this._$parts=[],this._$disconnectableChildren=void 0,this._$template=e,this._$parent=t}get parentNode(){return this._$parent.parentNode}get _$isConnected(){return this._$parent._$isConnected}_clone(e){var t;const{el:{content:o},parts:i}=this._$template,r=((t=e==null?void 0:e.creationScope)!==null&&t!==void 0?t:he).importNode(o,!0);ce.currentNode=r;let s=ce.nextNode(),l=0,a=0,c=i[0];for(;c!==void 0;){if(l===c.index){let h;c.type===Ke?h=new xe(s,s.nextSibling,this,e):c.type===Ot?h=new c.ctor(s,c.name,c.strings,this,e):c.type===Mt&&(h=new Ei(s,this,e)),this._$parts.push(h),c=i[++a]}l!==(c==null?void 0:c.index)&&(s=ce.nextNode(),l++)}return r}_update(e){let t=0;for(const o of this._$parts)o!==void 0&&(g==null||g({kind:"set part",part:o,value:e[t],valueIndex:t,values:e,templateInstance:this}),o.strings!==void 0?(o._$setValue(e,o,t),t+=o.strings.length-2):o._$setValue(e[t])),t++}}class xe{constructor(e,t,o,i){var r;this.type=Ke,this._$committedValue=S,this._$disconnectableChildren=void 0,this._$startNode=e,this._$endNode=t,this._$parent=o,this.options=i,this.__isConnected=(r=i==null?void 0:i.isConnected)!==null&&r!==void 0?r:!0,this._textSanitizer=void 0}get _$isConnected(){var e,t;return(t=(e=this._$parent)===null||e===void 0?void 0:e._$isConnected)!==null&&t!==void 0?t:this.__isConnected}get parentNode(){let e=P(this._$startNode).parentNode;const t=this._$parent;return t!==void 0&&(e==null?void 0:e.nodeType)===11&&(e=t.parentNode),e}get startNode(){return this._$startNode}get endNode(){return this._$endNode}_$setValue(e,t=this){var o;if(this.parentNode===null)throw new Error("This `ChildPart` has no `parentNode` and therefore cannot accept a value. This likely means the element containing the part was manipulated in an unsupported way outside of Lit's control such that the part's marker nodes were ejected from DOM. For example, setting the element's `innerHTML` or `textContent` can do this.");if(e=ue(this,e,t),Se(e))e===S||e==null||e===""?(this._$committedValue!==S&&(g==null||g({kind:"commit nothing to child",start:this._$startNode,end:this._$endNode,parent:this._$parent,options:this.options}),this._$clear()),this._$committedValue=S):e!==this._$committedValue&&e!==Z&&this._commitText(e);else if(e._$litType$!==void 0)this._commitTemplateResult(e);else if(e.nodeType!==void 0){if(((o=this.options)===null||o===void 0?void 0:o.host)===e){this._commitText("[probable mistake: rendered a template's host in itself (commonly caused by writing ${this} in a template]"),console.warn("Attempted to render the template host",e,"inside itself. This is almost always a mistake, and in dev mode ","we render some warning text. In production however, we'll ","render it, which will usually result in an error, and sometimes ","in the element disappearing from the DOM.");return}this._commitNode(e)}else si(e)?this._commitIterable(e):this._commitText(e)}_insert(e){return P(P(this._$startNode).parentNode).insertBefore(e,this._$endNode)}_commitNode(e){var t;if(this._$committedValue!==e){if(this._$clear(),ee!==Ye){const o=(t=this._$startNode.parentNode)===null||t===void 0?void 0:t.nodeName;if(o==="STYLE"||o==="SCRIPT"){let i="Forbidden";throw o==="STYLE"?i="Lit does not support binding inside style nodes. This is a security risk, as style injection attacks can exfiltrate data and spoof UIs. Consider instead using css`...` literals to compose styles, and make do dynamic styling with css custom properties, ::parts, <slot>s, and by mutating the DOM rather than stylesheets.":i="Lit does not support binding inside script nodes. This is a security risk, as it could allow arbitrary code execution.",new Error(i)}}g==null||g({kind:"commit node",start:this._$startNode,parent:this._$parent,value:e,options:this.options}),this._$committedValue=this._insert(e)}}_commitText(e){if(this._$committedValue!==S&&Se(this._$committedValue)){const t=P(this._$startNode).nextSibling;this._textSanitizer===void 0&&(this._textSanitizer=kt(t,"data","property")),e=this._textSanitizer(e),g==null||g({kind:"commit text",node:t,value:e,options:this.options}),t.data=e}else{const t=he.createTextNode("");this._commitNode(t),this._textSanitizer===void 0&&(this._textSanitizer=kt(t,"data","property")),e=this._textSanitizer(e),g==null||g({kind:"commit text",node:t,value:e,options:this.options}),t.data=e}this._$committedValue=e}_commitTemplateResult(e){var t;const{values:o,["_$litType$"]:i}=e,r=typeof i=="number"?this._$getTemplate(e):(i.el===void 0&&(i.el=Ee.createElement(i.h,this.options)),i);if(((t=this._$committedValue)===null||t===void 0?void 0:t._$template)===r)g==null||g({kind:"template updating",template:r,instance:this._$committedValue,parts:this._$committedValue._$parts,options:this.options,values:o}),this._$committedValue._update(o);else{const s=new _i(r,this),l=s._clone(this.options);g==null||g({kind:"template instantiated",template:r,instance:s,parts:s._$parts,options:this.options,fragment:l,values:o}),s._update(o),g==null||g({kind:"template instantiated and updated",template:r,instance:s,parts:s._$parts,options:this.options,fragment:l,values:o}),this._commitNode(l),this._$committedValue=s}}_$getTemplate(e){let t=yn.get(e.strings);return t===void 0&&yn.set(e.strings,t=new Ee(e)),t}_commitIterable(e){Hn(this._$committedValue)||(this._$committedValue=[],this._$clear());const t=this._$committedValue;let o=0,i;for(const r of e)o===t.length?t.push(i=new xe(this._insert(we()),this._insert(we()),this,this.options)):i=t[o],i._$setValue(r),o++;o<t.length&&(this._$clear(i&&P(i._$endNode).nextSibling,o),t.length=o)}_$clear(e=P(this._$startNode).nextSibling,t){var o;for((o=this._$notifyConnectionChanged)===null||o===void 0||o.call(this,!1,!0,t);e&&e!==this._$endNode;){const i=P(e).nextSibling;P(e).remove(),e=i}}setConnected(e){var t;if(this._$parent===void 0)this.__isConnected=e,(t=this._$notifyConnectionChanged)===null||t===void 0||t.call(this,e);else throw new Error("part.setConnected() may only be called on a RootPart returned from render().")}}class Je{constructor(e,t,o,i,r){this.type=Ot,this._$committedValue=S,this._$disconnectableChildren=void 0,this.element=e,this.name=t,this._$parent=i,this.options=r,o.length>2||o[0]!==""||o[1]!==""?(this._$committedValue=new Array(o.length-1).fill(new String),this.strings=o):this._$committedValue=S,this._sanitizer=void 0}get tagName(){return this.element.tagName}get _$isConnected(){return this._$parent._$isConnected}_$setValue(e,t=this,o,i){const r=this.strings;let s=!1;if(r===void 0)e=ue(this,e,t,0),s=!Se(e)||e!==this._$committedValue&&e!==Z,s&&(this._$committedValue=e);else{const l=e;e=r[0];let a,c;for(a=0;a<r.length-1;a++)c=ue(this,l[o+a],t,a),c===Z&&(c=this._$committedValue[a]),s||(s=!Se(c)||c!==this._$committedValue[a]),c===S?e=S:e!==S&&(e+=(c??"")+r[a+1]),this._$committedValue[a]=c}s&&!i&&this._commitValue(e)}_commitValue(e){e===S?P(this.element).removeAttribute(this.name):(this._sanitizer===void 0&&(this._sanitizer=ee(this.element,this.name,"attribute")),e=this._sanitizer(e??""),g==null||g({kind:"commit attribute",element:this.element,name:this.name,value:e,options:this.options}),P(this.element).setAttribute(this.name,e??""))}}class yi extends Je{constructor(){super(...arguments),this.type=pi}_commitValue(e){this._sanitizer===void 0&&(this._sanitizer=ee(this.element,this.name,"property")),e=this._sanitizer(e),g==null||g({kind:"commit property",element:this.element,name:this.name,value:e,options:this.options}),this.element[this.name]=e===S?void 0:e}}const bi=de?de.emptyScript:"";class wi extends Je{constructor(){super(...arguments),this.type=fi}_commitValue(e){g==null||g({kind:"commit boolean attribute",element:this.element,name:this.name,value:!!(e&&e!==S),options:this.options}),e&&e!==S?P(this.element).setAttribute(this.name,bi):P(this.element).removeAttribute(this.name)}}class Si extends Je{constructor(e,t,o,i,r){if(super(e,t,o,i,r),this.type=mi,this.strings!==void 0)throw new Error(`A \`<${e.localName}>\` has a \`@${t}=...\` listener with invalid content. Event listeners in templates must have exactly one expression and no surrounding text.`)}_$setValue(e,t=this){var o;if(e=(o=ue(this,e,t,0))!==null&&o!==void 0?o:S,e===Z)return;const i=this._$committedValue,r=e===S&&i!==S||e.capture!==i.capture||e.once!==i.once||e.passive!==i.passive,s=e!==S&&(i===S||r);g==null||g({kind:"commit event listener",element:this.element,name:this.name,value:e,options:this.options,removeListener:r,addListener:s,oldListener:i}),r&&this.element.removeEventListener(this.name,this,i),s&&this.element.addEventListener(this.name,this,e),this._$committedValue=e}handleEvent(e){var t,o;typeof this._$committedValue=="function"?this._$committedValue.call((o=(t=this.options)===null||t===void 0?void 0:t.host)!==null&&o!==void 0?o:this.element,e):this._$committedValue.handleEvent(e)}}class Ei{constructor(e,t,o){this.element=e,this.type=Mt,this._$disconnectableChildren=void 0,this._$parent=t,this.options=o}get _$isConnected(){return this._$parent._$isConnected}_$setValue(e){g==null||g({kind:"commit to element binding",element:this.element,value:e,options:this.options}),ue(this,e)}}const vt=I.litHtmlPolyfillSupportDevMode;vt==null||vt(Ee,xe);((ut=I.litHtmlVersions)!==null&&ut!==void 0?ut:I.litHtmlVersions=[]).push("2.7.2");I.litHtmlVersions.length>1&&Ge("multiple-versions","Multiple versions of Lit loaded. Loading multiple versions is not recommended.");const Ue=(n,e,t)=>{var o,i;if(e==null)throw new TypeError(`The container to render into may not be ${e}`);const r=ti++,s=(o=t==null?void 0:t.renderBefore)!==null&&o!==void 0?o:e;let l=s._$litPart$;if(g==null||g({kind:"begin render",id:r,value:n,container:e,options:t,part:l}),l===void 0){const a=(i=t==null?void 0:t.renderBefore)!==null&&i!==void 0?i:null;s._$litPart$=l=new xe(e.insertBefore(we(),a),a,void 0,t??{})}return l._$setValue(n),g==null||g({kind:"end render",id:r,value:n,container:e,options:t,part:l}),l};Ue.setSanitizer=oi,Ue.createSanitizer=kt,Ue._testOnlyClearSanitizerFactoryDoNotCallOrElse=ii;/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */var _t,yt,bt;let Dt;{const n=(_t=globalThis.litIssuedWarnings)!==null&&_t!==void 0?_t:globalThis.litIssuedWarnings=new Set;Dt=(e,t)=>{t+=` See https://lit.dev/msg/${e} for more information.`,n.has(t)||(console.warn(t),n.add(t))}}class te extends B{constructor(){super(...arguments),this.renderOptions={host:this},this.__childPart=void 0}createRenderRoot(){var e,t;const o=super.createRenderRoot();return(e=(t=this.renderOptions).renderBefore)!==null&&e!==void 0||(t.renderBefore=o.firstChild),o}update(e){const t=this.render();this.hasUpdated||(this.renderOptions.isConnected=this.isConnected),super.update(e),this.__childPart=Ue(t,this.renderRoot,this.renderOptions)}connectedCallback(){var e;super.connectedCallback(),(e=this.__childPart)===null||e===void 0||e.setConnected(!0)}disconnectedCallback(){var e;super.disconnectedCallback(),(e=this.__childPart)===null||e===void 0||e.setConnected(!1)}render(){return Z}}te.finalized=!0;te._$litElement$=!0;(yt=globalThis.litElementHydrateSupport)===null||yt===void 0||yt.call(globalThis,{LitElement:te});const wt=globalThis.litElementPolyfillSupportDevMode;wt==null||wt({LitElement:te});te.finalize=function(){if(!B.finalize.call(this))return!1;const e=(t,o,i=!1)=>{if(t.hasOwnProperty(o)){const r=(typeof t=="function"?t:t.constructor).name;Dt(i?"renamed-api":"removed-api",`\`${o}\` is implemented on class ${r}. It has been ${i?"renamed":"removed"} in this version of LitElement.`)}};return e(this,"render"),e(this,"getStyles",!0),e(this.prototype,"adoptStyles"),!0};((bt=globalThis.litElementVersions)!==null&&bt!==void 0?bt:globalThis.litElementVersions=[]).push("3.3.1");globalThis.litElementVersions.length>1&&Dt("multiple-versions","Multiple versions of Lit loaded. Loading multiple versions is not recommended.");/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */const Ci=(n,e)=>e.kind==="method"&&e.descriptor&&!("value"in e.descriptor)?{...e,finisher(t){t.createProperty(e.key,n)}}:{kind:"field",key:Symbol(),placement:"own",descriptor:{},originalKey:e.key,initializer(){typeof e.initializer=="function"&&(this[e.key]=e.initializer.call(this))},finisher(t){t.createProperty(e.key,n)}},Ti=(n,e,t)=>{e.constructor.createProperty(t,n)};function w(n){return(e,t)=>t!==void 0?Ti(n,e,t):Ci(n,e)}/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */function me(n){return w({...n,state:!0})}/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */const xi=({finisher:n,descriptor:e})=>(t,o)=>{var i;if(o!==void 0){const r=t.constructor;e!==void 0&&Object.defineProperty(t,o,e(o)),n==null||n(r,o)}else{const r=(i=t.originalKey)!==null&&i!==void 0?i:t.key,s=e!=null?{kind:"method",placement:"prototype",key:r,descriptor:e(t.key)}:{...t,key:r};return n!=null&&(s.finisher=function(l){n(l,r)}),s}};/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */function $i(n,e){return xi({descriptor:t=>{const o={get(){var i,r;return(r=(i=this.renderRoot)===null||i===void 0?void 0:i.querySelector(n))!==null&&r!==void 0?r:null},enumerable:!0,configurable:!0};if(e){const i=typeof t=="symbol"?Symbol():`__${t}`;o.get=function(){var r,s;return this[i]===void 0&&(this[i]=(s=(r=this.renderRoot)===null||r===void 0?void 0:r.querySelector(n))!==null&&s!==void 0?s:null),this[i]}}return o}})}/**
 * @license
 * Copyright 2021 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */var St;const ki=window;((St=ki.HTMLSlotElement)===null||St===void 0?void 0:St.prototype.assignedElements)!=null;/**
 * @license
 * Copyright 2017 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */const Ri={ATTRIBUTE:1,CHILD:2,PROPERTY:3,BOOLEAN_ATTRIBUTE:4,EVENT:5,ELEMENT:6},Ni=n=>(...e)=>({_$litDirective$:n,values:e});class Ii{constructor(e){}get _$isConnected(){return this._$parent._$isConnected}_$initialize(e,t,o){this.__part=e,this._$parent=t,this.__attributeIndex=o}_$resolve(e,t){return this.update(e,t)}update(e,t){return this.render(...t)}}/**
 * @license
 * Copyright 2018 Google LLC
 * SPDX-License-Identifier: BSD-3-Clause
 */class Li extends Ii{constructor(e){var t;if(super(e),e.type!==Ri.ATTRIBUTE||e.name!=="class"||((t=e.strings)===null||t===void 0?void 0:t.length)>2)throw new Error("`classMap()` can only be used in the `class` attribute and must be the only part in the attribute.")}render(e){return" "+Object.keys(e).filter(t=>e[t]).join(" ")+" "}update(e,[t]){var o,i;if(this._previousClasses===void 0){this._previousClasses=new Set,e.strings!==void 0&&(this._staticClasses=new Set(e.strings.join(" ").split(/\s/).filter(s=>s!=="")));for(const s in t)t[s]&&!(!((o=this._staticClasses)===null||o===void 0)&&o.has(s))&&this._previousClasses.add(s);return this.render(t)}const r=e.element.classList;this._previousClasses.forEach(s=>{s in t||(r.remove(s),this._previousClasses.delete(s))});for(const s in t){const l=!!t[s];l!==this._previousClasses.has(s)&&!(!((i=this._staticClasses)===null||i===void 0)&&i.has(s))&&(l?(r.add(s),this._previousClasses.add(s)):(r.remove(s),this._previousClasses.delete(s)))}return Z}}const Gn=Ni(Li),Et="css-loading-indicator";var M;(function(n){n.IDLE="",n.FIRST="first",n.SECOND="second",n.THIRD="third"})(M||(M={}));class C extends te{constructor(){super(),this.firstDelay=450,this.secondDelay=1500,this.thirdDelay=5e3,this.expandedDuration=2e3,this.onlineText="Online",this.offlineText="Connection lost",this.reconnectingText="Connection lost, trying to reconnect...",this.offline=!1,this.reconnecting=!1,this.expanded=!1,this.loading=!1,this.loadingBarState=M.IDLE,this.applyDefaultThemeState=!0,this.firstTimeout=0,this.secondTimeout=0,this.thirdTimeout=0,this.expandedTimeout=0,this.lastMessageState=E.CONNECTED,this.connectionStateListener=()=>{this.expanded=this.updateConnectionState(),this.expandedTimeout=this.timeoutFor(this.expandedTimeout,this.expanded,()=>{this.expanded=!1},this.expandedDuration)}}static create(){var e,t;const o=window;return!((e=o.Vaadin)===null||e===void 0)&&e.connectionIndicator||(o.Vaadin=o.Vaadin||{},o.Vaadin.connectionIndicator=document.createElement("vaadin-connection-indicator"),document.body.appendChild(o.Vaadin.connectionIndicator)),(t=o.Vaadin)===null||t===void 0?void 0:t.connectionIndicator}render(){return R`
      <div class="v-loading-indicator ${this.loadingBarState}" style=${this.getLoadingBarStyle()}></div>

      <div
        class="v-status-message ${Gn({active:this.reconnecting})}"
      >
        <span class="text"> ${this.renderMessage()} </span>
      </div>
    `}connectedCallback(){var e;super.connectedCallback();const t=window;!((e=t.Vaadin)===null||e===void 0)&&e.connectionState&&(this.connectionStateStore=t.Vaadin.connectionState,this.connectionStateStore.addStateChangeListener(this.connectionStateListener),this.updateConnectionState()),this.updateTheme()}disconnectedCallback(){super.disconnectedCallback(),this.connectionStateStore&&this.connectionStateStore.removeStateChangeListener(this.connectionStateListener),this.updateTheme()}get applyDefaultTheme(){return this.applyDefaultThemeState}set applyDefaultTheme(e){e!==this.applyDefaultThemeState&&(this.applyDefaultThemeState=e,this.updateTheme())}createRenderRoot(){return this}updateConnectionState(){var e;const t=(e=this.connectionStateStore)===null||e===void 0?void 0:e.state;return this.offline=t===E.CONNECTION_LOST,this.reconnecting=t===E.RECONNECTING,this.updateLoading(t===E.LOADING),this.loading?!1:t!==this.lastMessageState?(this.lastMessageState=t,!0):!1}updateLoading(e){this.loading=e,this.loadingBarState=M.IDLE,this.firstTimeout=this.timeoutFor(this.firstTimeout,e,()=>{this.loadingBarState=M.FIRST},this.firstDelay),this.secondTimeout=this.timeoutFor(this.secondTimeout,e,()=>{this.loadingBarState=M.SECOND},this.secondDelay),this.thirdTimeout=this.timeoutFor(this.thirdTimeout,e,()=>{this.loadingBarState=M.THIRD},this.thirdDelay)}renderMessage(){return this.reconnecting?this.reconnectingText:this.offline?this.offlineText:this.onlineText}updateTheme(){if(this.applyDefaultThemeState&&this.isConnected){if(!document.getElementById(Et)){const e=document.createElement("style");e.id=Et,e.textContent=this.getDefaultStyle(),document.head.appendChild(e)}}else{const e=document.getElementById(Et);e&&document.head.removeChild(e)}}getDefaultStyle(){return`
      @keyframes v-progress-start {
        0% {
          width: 0%;
        }
        100% {
          width: 50%;
        }
      }
      @keyframes v-progress-delay {
        0% {
          width: 50%;
        }
        100% {
          width: 90%;
        }
      }
      @keyframes v-progress-wait {
        0% {
          width: 90%;
          height: 4px;
        }
        3% {
          width: 91%;
          height: 7px;
        }
        100% {
          width: 96%;
          height: 7px;
        }
      }
      @keyframes v-progress-wait-pulse {
        0% {
          opacity: 1;
        }
        50% {
          opacity: 0.1;
        }
        100% {
          opacity: 1;
        }
      }
      .v-loading-indicator,
      .v-status-message {
        position: fixed;
        z-index: 251;
        left: 0;
        right: auto;
        top: 0;
        background-color: var(--lumo-primary-color, var(--material-primary-color, blue));
        transition: none;
      }
      .v-loading-indicator {
        width: 50%;
        height: 4px;
        opacity: 1;
        pointer-events: none;
        animation: v-progress-start 1000ms 200ms both;
      }
      .v-loading-indicator[style*='none'] {
        display: block !important;
        width: 100%;
        opacity: 0;
        animation: none;
        transition: opacity 500ms 300ms, width 300ms;
      }
      .v-loading-indicator.second {
        width: 90%;
        animation: v-progress-delay 3.8s forwards;
      }
      .v-loading-indicator.third {
        width: 96%;
        animation: v-progress-wait 5s forwards, v-progress-wait-pulse 1s 4s infinite backwards;
      }

      vaadin-connection-indicator[offline] .v-loading-indicator,
      vaadin-connection-indicator[reconnecting] .v-loading-indicator {
        display: none;
      }

      .v-status-message {
        opacity: 0;
        width: 100%;
        max-height: var(--status-height-collapsed, 8px);
        overflow: hidden;
        background-color: var(--status-bg-color-online, var(--lumo-primary-color, var(--material-primary-color, blue)));
        color: var(
          --status-text-color-online,
          var(--lumo-primary-contrast-color, var(--material-primary-contrast-color, #fff))
        );
        font-size: 0.75rem;
        font-weight: 600;
        line-height: 1;
        transition: all 0.5s;
        padding: 0 0.5em;
      }

      vaadin-connection-indicator[offline] .v-status-message,
      vaadin-connection-indicator[reconnecting] .v-status-message {
        opacity: 1;
        background-color: var(--status-bg-color-offline, var(--lumo-shade, #333));
        color: var(
          --status-text-color-offline,
          var(--lumo-primary-contrast-color, var(--material-primary-contrast-color, #fff))
        );
        background-image: repeating-linear-gradient(
          45deg,
          rgba(255, 255, 255, 0),
          rgba(255, 255, 255, 0) 10px,
          rgba(255, 255, 255, 0.1) 10px,
          rgba(255, 255, 255, 0.1) 20px
        );
      }

      vaadin-connection-indicator[reconnecting] .v-status-message {
        animation: show-reconnecting-status 2s;
      }

      vaadin-connection-indicator[offline] .v-status-message:hover,
      vaadin-connection-indicator[reconnecting] .v-status-message:hover,
      vaadin-connection-indicator[expanded] .v-status-message {
        max-height: var(--status-height, 1.75rem);
      }

      vaadin-connection-indicator[expanded] .v-status-message {
        opacity: 1;
      }

      .v-status-message span {
        display: flex;
        align-items: center;
        justify-content: center;
        height: var(--status-height, 1.75rem);
      }

      vaadin-connection-indicator[reconnecting] .v-status-message span::before {
        content: '';
        width: 1em;
        height: 1em;
        border-top: 2px solid
          var(--status-spinner-color, var(--lumo-primary-color, var(--material-primary-color, blue)));
        border-left: 2px solid
          var(--status-spinner-color, var(--lumo-primary-color, var(--material-primary-color, blue)));
        border-right: 2px solid transparent;
        border-bottom: 2px solid transparent;
        border-radius: 50%;
        box-sizing: border-box;
        animation: v-spin 0.4s linear infinite;
        margin: 0 0.5em;
      }

      @keyframes v-spin {
        100% {
          transform: rotate(360deg);
        }
      }
    `}getLoadingBarStyle(){switch(this.loadingBarState){case M.IDLE:return"display: none";case M.FIRST:case M.SECOND:case M.THIRD:return"display: block";default:return""}}timeoutFor(e,t,o,i){return e!==0&&window.clearTimeout(e),t?window.setTimeout(o,i):0}static get instance(){return C.create()}}L([w({type:Number})],C.prototype,"firstDelay",void 0);L([w({type:Number})],C.prototype,"secondDelay",void 0);L([w({type:Number})],C.prototype,"thirdDelay",void 0);L([w({type:Number})],C.prototype,"expandedDuration",void 0);L([w({type:String})],C.prototype,"onlineText",void 0);L([w({type:String})],C.prototype,"offlineText",void 0);L([w({type:String})],C.prototype,"reconnectingText",void 0);L([w({type:Boolean,reflect:!0})],C.prototype,"offline",void 0);L([w({type:Boolean,reflect:!0})],C.prototype,"reconnecting",void 0);L([w({type:Boolean,reflect:!0})],C.prototype,"expanded",void 0);L([w({type:Boolean,reflect:!0})],C.prototype,"loading",void 0);L([w({type:String})],C.prototype,"loadingBarState",void 0);L([w({type:Boolean})],C.prototype,"applyDefaultTheme",null);customElements.get("vaadin-connection-indicator")===void 0&&customElements.define("vaadin-connection-indicator",C);C.instance;const Ce=window;Ce.Vaadin=Ce.Vaadin||{};Ce.Vaadin.registrations=Ce.Vaadin.registrations||[];Ce.Vaadin.registrations.push({is:"@vaadin/common-frontend",version:"0.0.18"});class bn extends Error{}const ve=window.document.body,b=window;class Ai{constructor(e){this.response=void 0,this.pathname="",this.isActive=!1,this.baseRegex=/^\//,ve.$=ve.$||[],this.config=e||{},b.Vaadin=b.Vaadin||{},b.Vaadin.Flow=b.Vaadin.Flow||{},b.Vaadin.Flow.clients={TypeScript:{isActive:()=>this.isActive}};const t=document.head.querySelector("base");this.baseRegex=new RegExp(`^${(document.baseURI||t&&t.href||"/").replace(/^https?:\/\/[^/]+/i,"")}`),this.appShellTitle=document.title,this.addConnectionIndicator()}get serverSideRoutes(){return[{path:"(.*)",action:this.action}]}loadingStarted(){this.isActive=!0,b.Vaadin.connectionState.loadingStarted()}loadingFinished(){this.isActive=!1,b.Vaadin.connectionState.loadingFinished()}get action(){return async e=>{if(this.pathname=e.pathname,b.Vaadin.connectionState.online)try{await this.flowInit()}catch(t){if(t instanceof bn)return b.Vaadin.connectionState.state=E.CONNECTION_LOST,this.offlineStubAction();throw t}else return this.offlineStubAction();return this.container.onBeforeEnter=(t,o)=>this.flowNavigate(t,o),this.container.onBeforeLeave=(t,o)=>this.flowLeave(t,o),this.container}}async flowLeave(e,t){const{connectionState:o}=b.Vaadin;return this.pathname===e.pathname||!this.isFlowClientLoaded()||o.offline?Promise.resolve({}):new Promise(i=>{this.loadingStarted(),this.container.serverConnected=r=>{i(t&&r?t.prevent():{}),this.loadingFinished()},ve.$server.leaveNavigation(this.getFlowRoutePath(e),this.getFlowRouteQuery(e))})}async flowNavigate(e,t){return this.response?new Promise(o=>{this.loadingStarted(),this.container.serverConnected=(i,r)=>{t&&i?o(t.prevent()):t&&t.redirect&&r?o(t.redirect(r.pathname)):(this.container.style.display="",o(this.container)),this.loadingFinished()},ve.$server.connectClient(this.container.localName,this.container.id,this.getFlowRoutePath(e),this.getFlowRouteQuery(e),this.appShellTitle,history.state)}):Promise.resolve(this.container)}getFlowRoutePath(e){return decodeURIComponent(e.pathname).replace(this.baseRegex,"")}getFlowRouteQuery(e){return e.search&&e.search.substring(1)||""}async flowInit(e=!1){if(!this.isFlowClientLoaded()){this.loadingStarted(),this.response=await this.flowInitUi(e),this.response.appConfig.clientRouting=!e;const{pushScript:t,appConfig:o}=this.response;typeof t=="string"&&await this.loadScript(t);const{appId:i}=o;await(await Fe(()=>import("./FlowBootstrap-feff2646.js"),[],import.meta.url)).init(this.response),typeof this.config.imports=="function"&&(this.injectAppIdScript(i),await this.config.imports());const s=await Fe(()=>import("./FlowClient-e0ae8105.js"),[],import.meta.url);if(await this.flowInitClient(s),!e){const l=`flow-container-${i.toLowerCase()}`;this.container=document.createElement(l),ve.$[i]=this.container,this.container.id=i}this.loadingFinished()}return this.container&&!this.container.isConnected&&(this.container.style.display="none",document.body.appendChild(this.container)),this.response}async loadScript(e){return new Promise((t,o)=>{const i=document.createElement("script");i.onload=()=>t(),i.onerror=o,i.src=e,document.body.appendChild(i)})}injectAppIdScript(e){const t=e.substring(0,e.lastIndexOf("-")),o=document.createElement("script");o.type="module",o.setAttribute("data-app-id",t),document.body.append(o)}async flowInitClient(e){return e.init(),new Promise(t=>{const o=setInterval(()=>{Object.keys(b.Vaadin.Flow.clients).filter(r=>r!=="TypeScript").reduce((r,s)=>r||b.Vaadin.Flow.clients[s].isActive(),!1)||(clearInterval(o),t())},5)})}async flowInitUi(e){const t=b.Vaadin&&b.Vaadin.TypeScript&&b.Vaadin.TypeScript.initial;return t?(b.Vaadin.TypeScript.initial=void 0,Promise.resolve(t)):new Promise((o,i)=>{const s=new XMLHttpRequest,l=e?"&serverSideRouting":"",a=`?v-r=init&location=${encodeURIComponent(this.getFlowRoutePath(location))}&query=${encodeURIComponent(this.getFlowRouteQuery(location))}${l}`;s.open("GET",a),s.onerror=()=>i(new bn(`Invalid server response when initializing Flow UI.
        ${s.status}
        ${s.responseText}`)),s.onload=()=>{const c=s.getResponseHeader("content-type");c&&c.indexOf("application/json")!==-1?o(JSON.parse(s.responseText)):s.onerror()},s.send()})}addConnectionIndicator(){C.create(),b.addEventListener("online",()=>{if(!this.isFlowClientLoaded()){b.Vaadin.connectionState.state=E.RECONNECTING;const e=new XMLHttpRequest;e.open("HEAD","sw.js"),e.onload=()=>{b.Vaadin.connectionState.state=E.CONNECTED},e.onerror=()=>{b.Vaadin.connectionState.state=E.CONNECTION_LOST},setTimeout(()=>e.send(),50)}}),b.addEventListener("offline",()=>{this.isFlowClientLoaded()||(b.Vaadin.connectionState.state=E.CONNECTION_LOST)})}async offlineStubAction(){const e=document.createElement("iframe"),t="./offline-stub.html";e.setAttribute("src",t),e.setAttribute("style","width: 100%; height: 100%; border: 0"),this.response=void 0;let o;const i=()=>{o!==void 0&&(b.Vaadin.connectionState.removeStateChangeListener(o),o=void 0)};return e.onBeforeEnter=(r,s,l)=>{o=()=>{b.Vaadin.connectionState.online&&(i(),l.render(r,!1))},b.Vaadin.connectionState.addStateChangeListener(o)},e.onBeforeLeave=(r,s,l)=>{i()},e}isFlowClientLoaded(){return this.response!==void 0}}const{serverSideRoutes:Pi}=new Ai({imports:()=>Fe(()=>import("./generated-flow-imports-a4ca5f8e.js"),[],import.meta.url)}),Oi=[...Pi],Mi=new Y(document.querySelector("#outlet"));Mi.setRoutes(Oi);var Di=function(){var n=document.getSelection();if(!n.rangeCount)return function(){};for(var e=document.activeElement,t=[],o=0;o<n.rangeCount;o++)t.push(n.getRangeAt(o));switch(e.tagName.toUpperCase()){case"INPUT":case"TEXTAREA":e.blur();break;default:e=null;break}return n.removeAllRanges(),function(){n.type==="Caret"&&n.removeAllRanges(),n.rangeCount||t.forEach(function(i){n.addRange(i)}),e&&e.focus()}},wn={"text/plain":"Text","text/html":"Url",default:"Text"},Vi="Copy to clipboard: #{key}, Enter";function Ui(n){var e=(/mac os x/i.test(navigator.userAgent)?"⌘":"Ctrl")+"+C";return n.replace(/#{\s*key\s*}/g,e)}function zi(n,e){var t,o,i,r,s,l,a=!1;e||(e={}),t=e.debug||!1;try{i=Di(),r=document.createRange(),s=document.getSelection(),l=document.createElement("span"),l.textContent=n,l.style.all="unset",l.style.position="fixed",l.style.top=0,l.style.clip="rect(0, 0, 0, 0)",l.style.whiteSpace="pre",l.style.webkitUserSelect="text",l.style.MozUserSelect="text",l.style.msUserSelect="text",l.style.userSelect="text",l.addEventListener("copy",function(h){if(h.stopPropagation(),e.format)if(h.preventDefault(),typeof h.clipboardData>"u"){t&&console.warn("unable to use e.clipboardData"),t&&console.warn("trying IE specific stuff"),window.clipboardData.clearData();var f=wn[e.format]||wn.default;window.clipboardData.setData(f,n)}else h.clipboardData.clearData(),h.clipboardData.setData(e.format,n);e.onCopy&&(h.preventDefault(),e.onCopy(h.clipboardData))}),document.body.appendChild(l),r.selectNodeContents(l),s.addRange(r);var c=document.execCommand("copy");if(!c)throw new Error("copy command was unsuccessful");a=!0}catch(h){t&&console.error("unable to copy using execCommand: ",h),t&&console.warn("trying IE specific stuff");try{window.clipboardData.setData(e.format||"text",n),e.onCopy&&e.onCopy(window.clipboardData),a=!0}catch(f){t&&console.error("unable to copy using clipboardData: ",f),t&&console.error("falling back to prompt"),o=Ui("message"in e?e.message:Vi),window.prompt(o,n)}}finally{s&&(typeof s.removeRange=="function"?s.removeRange(r):s.removeAllRanges()),l&&document.body.removeChild(l),i()}return a}const Vt=1e3,Ut=(n,e)=>{const t=Array.from(n.querySelectorAll(e.join(", "))),o=Array.from(n.querySelectorAll("*")).filter(i=>i.shadowRoot).flatMap(i=>Ut(i.shadowRoot,e));return[...t,...o]};let Sn=!1;const Te=(n,e)=>{Sn||(window.addEventListener("message",i=>{i.data==="validate-license"&&window.location.reload()},!1),Sn=!0);const t=n._overlayElement;if(t){if(t.shadowRoot){const i=t.shadowRoot.querySelector("slot:not([name])");if(i&&i.assignedElements().length>0){Te(i.assignedElements()[0],e);return}}Te(t,e);return}const o=e.messageHtml?e.messageHtml:`${e.message} <p>Component: ${e.product.name} ${e.product.version}</p>`.replace(/https:([^ ]*)/g,"<a href='https:$1'>https:$1</a>");n.isConnected&&(n.outerHTML=`<no-license style="display:flex;align-items:center;text-align:center;justify-content:center;"><div>${o}</div></no-license>`)},ye={},En={},pe={},qn={},z=n=>`${n.name}_${n.version}`,Cn=n=>{const{cvdlName:e,version:t}=n.constructor,o={name:e,version:t},i=n.tagName.toLowerCase();ye[e]=ye[e]??[],ye[e].push(i);const r=pe[z(o)];r&&setTimeout(()=>Te(n,r),Vt),pe[z(o)]||qn[z(o)]||En[z(o)]||(En[z(o)]=!0,window.Vaadin.devTools.checkLicense(o))},Fi=n=>{qn[z(n)]=!0,console.debug("License check ok for",n)},Kn=n=>{const e=n.product.name;pe[z(n.product)]=n,console.error("License check failed for",e);const t=ye[e];(t==null?void 0:t.length)>0&&Ut(document,t).forEach(o=>{setTimeout(()=>Te(o,pe[z(n.product)]),Vt)})},Bi=n=>{const e=n.message,t=n.product.name;n.messageHtml=`No license found. <a target=_blank onclick="javascript:window.open(this.href);return false;" href="${e}">Go here to start a trial or retrieve your license.</a>`,pe[z(n.product)]=n,console.error("No license found when checking",t);const o=ye[t];(o==null?void 0:o.length)>0&&Ut(document,o).forEach(i=>{setTimeout(()=>Te(i,pe[z(n.product)]),Vt)})},Hi=()=>{window.Vaadin.devTools.createdCvdlElements.forEach(n=>{Cn(n)}),window.Vaadin.devTools.createdCvdlElements={push:n=>{Cn(n)}}};var Wi=Object.defineProperty,ji=Object.getOwnPropertyDescriptor,x=(n,e,t,o)=>{for(var i=o>1?void 0:o?ji(e,t):e,r=n.length-1,s;r>=0;r--)(s=n[r])&&(i=(o?s(e,t,i):s(i))||i);return o&&i&&Wi(e,t,i),i};const Yn=class extends Object{constructor(n){super(),this.status="unavailable",n&&(this.webSocket=new WebSocket(n),this.webSocket.onmessage=e=>this.handleMessage(e),this.webSocket.onerror=e=>this.handleError(e),this.webSocket.onclose=e=>{this.status!=="error"&&this.setStatus("unavailable"),this.webSocket=void 0}),setInterval(()=>{this.webSocket&&self.status!=="error"&&this.status!=="unavailable"&&this.webSocket.send("")},Yn.HEARTBEAT_INTERVAL)}onHandshake(){}onReload(){}onConnectionError(n){}onStatusChange(n){}onMessage(n){console.error("Unknown message received from the live reload server:",n)}handleMessage(n){let e;try{e=JSON.parse(n.data)}catch(t){this.handleError(`[${t.name}: ${t.message}`);return}e.command==="hello"?(this.setStatus("active"),this.onHandshake()):e.command==="reload"?this.status==="active"&&this.onReload():e.command==="license-check-ok"?Fi(e.data):e.command==="license-check-failed"?Kn(e.data):e.command==="license-check-nokey"?Bi(e.data):this.onMessage(e)}handleError(n){console.error(n),this.setStatus("error"),n instanceof Event&&this.webSocket?this.onConnectionError(`Error in WebSocket connection to ${this.webSocket.url}`):this.onConnectionError(n)}setActive(n){!n&&this.status==="active"?this.setStatus("inactive"):n&&this.status==="inactive"&&this.setStatus("active")}setStatus(n){this.status!==n&&(this.status=n,this.onStatusChange(n))}send(n,e){const t=JSON.stringify({command:n,data:e});this.webSocket?this.webSocket.readyState!==WebSocket.OPEN?this.webSocket.addEventListener("open",()=>this.webSocket.send(t)):this.webSocket.send(t):console.error(`Unable to send message ${n}. No websocket is available`)}setFeature(n,e){this.send("setFeature",{featureId:n,enabled:e})}sendTelemetry(n){this.send("reportTelemetry",{browserData:n})}sendLicenseCheck(n){this.send("checkLicense",n)}sendShowComponentCreateLocation(n){this.send("showComponentCreateLocation",n)}sendShowComponentAttachLocation(n){this.send("showComponentAttachLocation",n)}};let ze=Yn;ze.HEARTBEAT_INTERVAL=18e4;const Gi=$`
  .popup {
    width: auto;
    position: fixed;
    background-color: var(--dev-tools-background-color-active-blurred);
    color: var(--dev-tools-text-color-primary);
    padding: 0.1875rem 0.75rem 0.1875rem 1rem;
    background-clip: padding-box;
    border-radius: var(--dev-tools-border-radius);
    overflow: hidden;
    margin: 0.5rem;
    width: 30rem;
    max-width: calc(100% - 1rem);
    max-height: calc(100vh - 1rem);
    flex-shrink: 1;
    background-color: var(--dev-tools-background-color-active);
    color: var(--dev-tools-text-color);
    transition: var(--dev-tools-transition-duration);
    transform-origin: bottom right;
    display: flex;
    flex-direction: column;
    box-shadow: var(--dev-tools-box-shadow);
    outline: none;
  }
`,y=class extends te{constructor(){super(),this.expanded=!1,this.messages=[],this.notifications=[],this.frontendStatus="unavailable",this.javaStatus="unavailable",this.tabs=[{id:"log",title:"Log",render:this.renderLog,activate:this.activateLog},{id:"info",title:"Info",render:this.renderInfo},{id:"features",title:"Feature Flags",render:this.renderFeatures}],this.activeTab="log",this.serverInfo={flowVersion:"",vaadinVersion:"",javaVersion:"",osVersion:"",productName:""},this.features=[],this.unreadErrors=!1,this.componentPickActive=!1,this.nextMessageId=1,this.transitionDuration=0,window.Vaadin.Flow&&this.tabs.push({id:"code",title:"Code",render:this.renderCode})}static get styles(){return[$`
        :host {
          --dev-tools-font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen-Sans, Ubuntu, Cantarell,
            'Helvetica Neue', sans-serif;
          --dev-tools-font-family-monospace: SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New',
            monospace;

          --dev-tools-font-size: 0.8125rem;
          --dev-tools-font-size-small: 0.75rem;

          --dev-tools-text-color: rgba(255, 255, 255, 0.8);
          --dev-tools-text-color-secondary: rgba(255, 255, 255, 0.65);
          --dev-tools-text-color-emphasis: rgba(255, 255, 255, 0.95);
          --dev-tools-text-color-active: rgba(255, 255, 255, 1);

          --dev-tools-background-color-inactive: rgba(45, 45, 45, 0.25);
          --dev-tools-background-color-active: rgba(45, 45, 45, 0.98);
          --dev-tools-background-color-active-blurred: rgba(45, 45, 45, 0.85);

          --dev-tools-border-radius: 0.5rem;
          --dev-tools-box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.05), 0 4px 12px -2px rgba(0, 0, 0, 0.4);

          --dev-tools-blue-hsl: ${this.BLUE_HSL};
          --dev-tools-blue-color: hsl(var(--dev-tools-blue-hsl));
          --dev-tools-green-hsl: ${this.GREEN_HSL};
          --dev-tools-green-color: hsl(var(--dev-tools-green-hsl));
          --dev-tools-grey-hsl: ${this.GREY_HSL};
          --dev-tools-grey-color: hsl(var(--dev-tools-grey-hsl));
          --dev-tools-yellow-hsl: ${this.YELLOW_HSL};
          --dev-tools-yellow-color: hsl(var(--dev-tools-yellow-hsl));
          --dev-tools-red-hsl: ${this.RED_HSL};
          --dev-tools-red-color: hsl(var(--dev-tools-red-hsl));

          /* Needs to be in ms, used in JavaScript as well */
          --dev-tools-transition-duration: 180ms;

          all: initial;

          direction: ltr;
          cursor: default;
          font: normal 400 var(--dev-tools-font-size) / 1.125rem var(--dev-tools-font-family);
          color: var(--dev-tools-text-color);
          -webkit-user-select: none;
          -moz-user-select: none;
          user-select: none;

          position: fixed;
          z-index: 20000;
          pointer-events: none;
          bottom: 0;
          right: 0;
          width: 100%;
          height: 100%;
          display: flex;
          flex-direction: column-reverse;
          align-items: flex-end;
        }

        .dev-tools {
          pointer-events: auto;
          display: flex;
          align-items: center;
          position: fixed;
          z-index: inherit;
          right: 0.5rem;
          bottom: 0.5rem;
          min-width: 1.75rem;
          height: 1.75rem;
          max-width: 1.75rem;
          border-radius: 0.5rem;
          padding: 0.375rem;
          box-sizing: border-box;
          background-color: var(--dev-tools-background-color-inactive);
          box-shadow: 0 0 0 1px rgba(255, 255, 255, 0.05);
          color: var(--dev-tools-text-color);
          transition: var(--dev-tools-transition-duration);
          white-space: nowrap;
          line-height: 1rem;
        }

        .dev-tools:hover,
        .dev-tools.active {
          background-color: var(--dev-tools-background-color-active);
          box-shadow: var(--dev-tools-box-shadow);
        }

        .dev-tools.active {
          max-width: calc(100% - 1rem);
        }

        .dev-tools .dev-tools-icon {
          flex: none;
          pointer-events: none;
          display: inline-block;
          width: 1rem;
          height: 1rem;
          fill: #fff;
          transition: var(--dev-tools-transition-duration);
          margin: 0;
        }

        .dev-tools.active .dev-tools-icon {
          opacity: 0;
          position: absolute;
          transform: scale(0.5);
        }

        .dev-tools .status-blip {
          flex: none;
          display: block;
          width: 6px;
          height: 6px;
          border-radius: 50%;
          z-index: 20001;
          background: var(--dev-tools-grey-color);
          position: absolute;
          top: -1px;
          right: -1px;
        }

        .dev-tools .status-description {
          overflow: hidden;
          text-overflow: ellipsis;
          padding: 0 0.25rem;
        }

        .dev-tools.error {
          background-color: hsla(var(--dev-tools-red-hsl), 0.15);
          animation: bounce 0.5s;
          animation-iteration-count: 2;
        }

        .switch {
          display: inline-flex;
          align-items: center;
        }

        .switch input {
          opacity: 0;
          width: 0;
          height: 0;
          position: absolute;
        }

        .switch .slider {
          display: block;
          flex: none;
          width: 28px;
          height: 18px;
          border-radius: 9px;
          background-color: rgba(255, 255, 255, 0.3);
          transition: var(--dev-tools-transition-duration);
          margin-right: 0.5rem;
        }

        .switch:focus-within .slider,
        .switch .slider:hover {
          background-color: rgba(255, 255, 255, 0.35);
          transition: none;
        }

        .switch input:focus-visible ~ .slider {
          box-shadow: 0 0 0 2px var(--dev-tools-background-color-active), 0 0 0 4px var(--dev-tools-blue-color);
        }

        .switch .slider::before {
          content: '';
          display: block;
          margin: 2px;
          width: 14px;
          height: 14px;
          background-color: #fff;
          transition: var(--dev-tools-transition-duration);
          border-radius: 50%;
        }

        .switch input:checked + .slider {
          background-color: var(--dev-tools-green-color);
        }

        .switch input:checked + .slider::before {
          transform: translateX(10px);
        }

        .switch input:disabled + .slider::before {
          background-color: var(--dev-tools-grey-color);
        }

        .window.hidden {
          opacity: 0;
          transform: scale(0);
          position: absolute;
        }

        .window.visible {
          transform: none;
          opacity: 1;
          pointer-events: auto;
        }

        .window.visible ~ .dev-tools {
          opacity: 0;
          pointer-events: none;
        }

        .window.visible ~ .dev-tools .dev-tools-icon,
        .window.visible ~ .dev-tools .status-blip {
          transition: none;
          opacity: 0;
        }

        .window {
          border-radius: var(--dev-tools-border-radius);
          overflow: hidden;
          margin: 0.5rem;
          width: 30rem;
          max-width: calc(100% - 1rem);
          max-height: calc(100vh - 1rem);
          flex-shrink: 1;
          background-color: var(--dev-tools-background-color-active);
          color: var(--dev-tools-text-color);
          transition: var(--dev-tools-transition-duration);
          transform-origin: bottom right;
          display: flex;
          flex-direction: column;
          box-shadow: var(--dev-tools-box-shadow);
          outline: none;
        }

        .window-toolbar {
          display: flex;
          flex: none;
          align-items: center;
          padding: 0.375rem;
          white-space: nowrap;
          order: 1;
          background-color: rgba(0, 0, 0, 0.2);
          gap: 0.5rem;
        }

        .tab {
          color: var(--dev-tools-text-color-secondary);
          font: inherit;
          font-size: var(--dev-tools-font-size-small);
          font-weight: 500;
          line-height: 1;
          padding: 0.25rem 0.375rem;
          background: none;
          border: none;
          margin: 0;
          border-radius: 0.25rem;
          transition: var(--dev-tools-transition-duration);
        }

        .tab:hover,
        .tab.active {
          color: var(--dev-tools-text-color-active);
        }

        .tab.active {
          background-color: rgba(255, 255, 255, 0.12);
        }

        .tab.unreadErrors::after {
          content: '•';
          color: hsl(var(--dev-tools-red-hsl));
          font-size: 1.5rem;
          position: absolute;
          transform: translate(0, -50%);
        }

        .ahreflike {
          font-weight: 500;
          color: var(--dev-tools-text-color-secondary);
          text-decoration: underline;
          cursor: pointer;
        }

        .ahreflike:hover {
          color: var(--dev-tools-text-color-emphasis);
        }

        .button {
          all: initial;
          font-family: inherit;
          font-size: var(--dev-tools-font-size-small);
          line-height: 1;
          white-space: nowrap;
          background-color: rgba(0, 0, 0, 0.2);
          color: inherit;
          font-weight: 600;
          padding: 0.25rem 0.375rem;
          border-radius: 0.25rem;
        }

        .button:focus,
        .button:hover {
          color: var(--dev-tools-text-color-emphasis);
        }

        .minimize-button {
          flex: none;
          width: 1rem;
          height: 1rem;
          color: inherit;
          background-color: transparent;
          border: 0;
          padding: 0;
          margin: 0 0 0 auto;
          opacity: 0.8;
        }

        .minimize-button:hover {
          opacity: 1;
        }

        .minimize-button svg {
          max-width: 100%;
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
          display: flex;
          padding: 0.1875rem 0.75rem 0.1875rem 2rem;
          background-clip: padding-box;
        }

        .message.log {
          padding-left: 0.75rem;
        }

        .message-content {
          margin-right: 0.5rem;
          -webkit-user-select: text;
          -moz-user-select: text;
          user-select: text;
        }

        .message-heading {
          position: relative;
          display: flex;
          align-items: center;
          margin: 0.125rem 0;
        }

        .message.log {
          color: var(--dev-tools-text-color-secondary);
        }

        .message:not(.log) .message-heading {
          font-weight: 500;
        }

        .message.has-details .message-heading {
          color: var(--dev-tools-text-color-emphasis);
          font-weight: 600;
        }

        .message-heading::before {
          position: absolute;
          margin-left: -1.5rem;
          display: inline-block;
          text-align: center;
          font-size: 0.875em;
          font-weight: 600;
          line-height: calc(1.25em - 2px);
          width: 14px;
          height: 14px;
          box-sizing: border-box;
          border: 1px solid transparent;
          border-radius: 50%;
        }

        .message.information .message-heading::before {
          content: 'i';
          border-color: currentColor;
          color: var(--dev-tools-notification-color);
        }

        .message.warning .message-heading::before,
        .message.error .message-heading::before {
          content: '!';
          color: var(--dev-tools-background-color-active);
          background-color: var(--dev-tools-notification-color);
        }

        .features-tray {
          padding: 0.75rem;
          flex: auto;
          overflow: auto;
          animation: fade-in var(--dev-tools-transition-duration) ease-in;
          user-select: text;
        }

        .features-tray p {
          margin-top: 0;
          color: var(--dev-tools-text-color-secondary);
        }

        .features-tray .feature {
          display: flex;
          align-items: center;
          gap: 1rem;
          padding-bottom: 0.5em;
        }

        .message .message-details {
          font-weight: 400;
          color: var(--dev-tools-text-color-secondary);
          margin: 0.25rem 0;
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

        .message .persist {
          color: var(--dev-tools-text-color-secondary);
          white-space: nowrap;
          margin: 0.375rem 0;
          display: flex;
          align-items: center;
          position: relative;
          -webkit-user-select: none;
          -moz-user-select: none;
          user-select: none;
        }

        .message .persist::before {
          content: '';
          width: 1em;
          height: 1em;
          border-radius: 0.2em;
          margin-right: 0.375em;
          background-color: rgba(255, 255, 255, 0.3);
        }

        .message .persist:hover::before {
          background-color: rgba(255, 255, 255, 0.4);
        }

        .message .persist.on::before {
          background-color: rgba(255, 255, 255, 0.9);
        }

        .message .persist.on::after {
          content: '';
          order: -1;
          position: absolute;
          width: 0.75em;
          height: 0.25em;
          border: 2px solid var(--dev-tools-background-color-active);
          border-width: 0 0 2px 2px;
          transform: translate(0.05em, -0.05em) rotate(-45deg) scale(0.8, 0.9);
        }

        .message .dismiss-message {
          font-weight: 600;
          align-self: stretch;
          display: flex;
          align-items: center;
          padding: 0 0.25rem;
          margin-left: 0.5rem;
          color: var(--dev-tools-text-color-secondary);
        }

        .message .dismiss-message:hover {
          color: var(--dev-tools-text-color);
        }

        .notification-tray {
          display: flex;
          flex-direction: column-reverse;
          align-items: flex-end;
          margin: 0.5rem;
          flex: none;
        }

        .window.hidden + .notification-tray {
          margin-bottom: 3rem;
        }

        .notification-tray .message {
          pointer-events: auto;
          background-color: var(--dev-tools-background-color-active);
          color: var(--dev-tools-text-color);
          max-width: 30rem;
          box-sizing: border-box;
          border-radius: var(--dev-tools-border-radius);
          margin-top: 0.5rem;
          transition: var(--dev-tools-transition-duration);
          transform-origin: bottom right;
          animation: slideIn var(--dev-tools-transition-duration);
          box-shadow: var(--dev-tools-box-shadow);
          padding-top: 0.25rem;
          padding-bottom: 0.25rem;
        }

        .notification-tray .message.animate-out {
          animation: slideOut forwards var(--dev-tools-transition-duration);
        }

        .notification-tray .message .message-details {
          max-height: 10em;
          overflow: hidden;
        }

        .message-tray {
          flex: auto;
          overflow: auto;
          max-height: 20rem;
          user-select: text;
        }

        .message-tray .message {
          animation: fade-in var(--dev-tools-transition-duration) ease-in;
          padding-left: 2.25rem;
        }

        .message-tray .message.warning {
          background-color: hsla(var(--dev-tools-yellow-hsl), 0.09);
        }

        .message-tray .message.error {
          background-color: hsla(var(--dev-tools-red-hsl), 0.09);
        }

        .message-tray .message.error .message-heading {
          color: hsl(var(--dev-tools-red-hsl));
        }

        .message-tray .message.warning .message-heading {
          color: hsl(var(--dev-tools-yellow-hsl));
        }

        .message-tray .message + .message {
          border-top: 1px solid rgba(255, 255, 255, 0.07);
        }

        .message-tray .dismiss-message,
        .message-tray .persist {
          display: none;
        }

        .info-tray {
          padding: 0.75rem;
          position: relative;
          flex: auto;
          overflow: auto;
          animation: fade-in var(--dev-tools-transition-duration) ease-in;
          user-select: text;
        }

        .info-tray dl {
          margin: 0;
          display: grid;
          grid-template-columns: max-content 1fr;
          column-gap: 0.75rem;
          position: relative;
        }

        .info-tray dt {
          grid-column: 1;
          color: var(--dev-tools-text-color-emphasis);
        }

        .info-tray dt:not(:first-child)::before {
          content: '';
          width: 100%;
          position: absolute;
          height: 1px;
          background-color: rgba(255, 255, 255, 0.1);
          margin-top: -0.375rem;
        }

        .info-tray dd {
          grid-column: 2;
          margin: 0;
        }

        .info-tray :is(dt, dd):not(:last-child) {
          margin-bottom: 0.75rem;
        }

        .info-tray dd + dd {
          margin-top: -0.5rem;
        }

        .info-tray .live-reload-status::before {
          content: '•';
          color: var(--status-color);
          width: 0.75rem;
          display: inline-block;
          font-size: 1rem;
          line-height: 0.5rem;
        }

        .info-tray .copy {
          position: fixed;
          z-index: 1;
          top: 0.5rem;
          right: 0.5rem;
        }

        .info-tray .switch {
          vertical-align: -4px;
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

        @supports (backdrop-filter: blur(1px)) {
          .dev-tools,
          .window,
          .notification-tray .message {
            backdrop-filter: blur(8px);
          }
          .dev-tools:hover,
          .dev-tools.active,
          .window,
          .notification-tray .message {
            background-color: var(--dev-tools-background-color-active-blurred);
          }
        }
      `,Gi]}static get isActive(){const n=window.sessionStorage.getItem(y.ACTIVE_KEY_IN_SESSION_STORAGE);return n===null||n!=="false"}static notificationDismissed(n){const e=window.localStorage.getItem(y.DISMISSED_NOTIFICATIONS_IN_LOCAL_STORAGE);return e!==null&&e.includes(n)}elementTelemetry(){let n={};try{const e=localStorage.getItem("vaadin.statistics.basket");if(!e)return;n=JSON.parse(e)}catch{return}this.frontendConnection&&this.frontendConnection.sendTelemetry(n)}openWebSocketConnection(){this.frontendStatus="unavailable",this.javaStatus="unavailable";const n=s=>this.log("error",s),e=()=>{if(this.liveReloadDisabled)return;this.showSplashMessage("Reloading…");const s=window.sessionStorage.getItem(y.TRIGGERED_COUNT_KEY_IN_SESSION_STORAGE),l=s?parseInt(s,10)+1:1;window.sessionStorage.setItem(y.TRIGGERED_COUNT_KEY_IN_SESSION_STORAGE,l.toString()),window.sessionStorage.setItem(y.TRIGGERED_KEY_IN_SESSION_STORAGE,"true"),window.location.reload()},t=new ze(this.getDedicatedWebSocketUrl());t.onHandshake=()=>{this.log("log","Vaadin development mode initialized"),y.isActive||t.setActive(!1),this.elementTelemetry()},t.onConnectionError=n,t.onReload=e,t.onStatusChange=s=>{this.frontendStatus=s},t.onMessage=s=>{(s==null?void 0:s.command)==="serverInfo"?this.serverInfo=s.data:(s==null?void 0:s.command)==="featureFlags"?this.features=s.data.features:console.error("Unknown message from front-end connection:",JSON.stringify(s))},this.frontendConnection=t;let o;this.backend===y.SPRING_BOOT_DEVTOOLS&&this.springBootLiveReloadPort?(o=new ze(this.getSpringBootWebSocketUrl(window.location)),o.onHandshake=()=>{y.isActive||o.setActive(!1)},o.onReload=e,o.onConnectionError=n):this.backend===y.JREBEL||this.backend===y.HOTSWAP_AGENT?o=t:o=new ze(void 0);const i=o.onStatusChange;o.onStatusChange=s=>{i(s),this.javaStatus=s};const r=o.onHandshake;o.onHandshake=()=>{r(),this.backend&&this.log("information",`Java live reload available: ${y.BACKEND_DISPLAY_NAME[this.backend]}`)},this.javaConnection=o,this.backend||this.showNotification("warning","Java live reload unavailable","Live reload for Java changes is currently not set up. Find out how to make use of this functionality to boost your workflow.","https://vaadin.com/docs/latest/flow/configuration/live-reload","liveReloadUnavailable")}getDedicatedWebSocketUrl(){function n(t){const o=document.createElement("div");return o.innerHTML=`<a href="${t}"/>`,o.firstChild.href}if(this.url===void 0)return;const e=n(this.url);if(!e.startsWith("http://")&&!e.startsWith("https://")){console.error("The protocol of the url should be http or https for live reload to work.");return}return`${e.replace(/^http/,"ws")}?v-r=push&debug_window`}getSpringBootWebSocketUrl(n){const{hostname:e}=n,t=n.protocol==="https:"?"wss":"ws";if(e.endsWith("gitpod.io")){const o=e.replace(/.*?-/,"");return`${t}://${this.springBootLiveReloadPort}-${o}`}else return`${t}://${e}:${this.springBootLiveReloadPort}`}connectedCallback(){if(super.connectedCallback(),this.catchErrors(),this.disableEventListener=t=>this.demoteSplashMessage(),document.body.addEventListener("focus",this.disableEventListener),document.body.addEventListener("click",this.disableEventListener),this.openWebSocketConnection(),window.sessionStorage.getItem(y.TRIGGERED_KEY_IN_SESSION_STORAGE)){const t=new Date,o=`${`0${t.getHours()}`.slice(-2)}:${`0${t.getMinutes()}`.slice(-2)}:${`0${t.getSeconds()}`.slice(-2)}`;this.showSplashMessage(`Page reloaded at ${o}`),window.sessionStorage.removeItem(y.TRIGGERED_KEY_IN_SESSION_STORAGE)}this.transitionDuration=parseInt(window.getComputedStyle(this).getPropertyValue("--dev-tools-transition-duration"),10);const e=window;e.Vaadin=e.Vaadin||{},e.Vaadin.devTools=Object.assign(this,e.Vaadin.devTools),Hi()}format(n){return n.toString()}catchErrors(){const n=window.Vaadin.ConsoleErrors;n&&n.forEach(e=>{this.log("error",e.map(t=>this.format(t)).join(" "))}),window.Vaadin.ConsoleErrors={push:e=>{this.log("error",e.map(t=>this.format(t)).join(" "))}}}disconnectedCallback(){this.disableEventListener&&(document.body.removeEventListener("focus",this.disableEventListener),document.body.removeEventListener("click",this.disableEventListener)),super.disconnectedCallback()}toggleExpanded(){this.notifications.slice().forEach(n=>this.dismissNotification(n.id)),this.expanded=!this.expanded,this.expanded&&this.root.focus()}showSplashMessage(n){this.splashMessage=n,this.splashMessage&&(this.expanded?this.demoteSplashMessage():setTimeout(()=>{this.demoteSplashMessage()},y.AUTO_DEMOTE_NOTIFICATION_DELAY))}demoteSplashMessage(){this.splashMessage&&this.log("log",this.splashMessage),this.showSplashMessage(void 0)}checkLicense(n){this.frontendConnection?this.frontendConnection.sendLicenseCheck(n):Kn({message:"Internal error: no connection",product:n})}log(n,e,t,o){const i=this.nextMessageId;for(this.nextMessageId+=1,this.messages.push({id:i,type:n,message:e,details:t,link:o,dontShowAgain:!1,deleted:!1});this.messages.length>y.MAX_LOG_ROWS;)this.messages.shift();this.requestUpdate(),this.updateComplete.then(()=>{const r=this.renderRoot.querySelector(".message-tray .message:last-child");this.expanded&&r?(setTimeout(()=>r.scrollIntoView({behavior:"smooth"}),this.transitionDuration),this.unreadErrors=!1):n==="error"&&(this.unreadErrors=!0)})}showNotification(n,e,t,o,i){if(i===void 0||!y.notificationDismissed(i)){if(this.notifications.filter(l=>l.persistentId===i).filter(l=>!l.deleted).length>0)return;const s=this.nextMessageId;this.nextMessageId+=1,this.notifications.push({id:s,type:n,message:e,details:t,link:o,persistentId:i,dontShowAgain:!1,deleted:!1}),o===void 0&&setTimeout(()=>{this.dismissNotification(s)},y.AUTO_DEMOTE_NOTIFICATION_DELAY),this.requestUpdate()}else this.log(n,e,t,o)}dismissNotification(n){const e=this.findNotificationIndex(n);if(e!==-1&&!this.notifications[e].deleted){const t=this.notifications[e];if(t.dontShowAgain&&t.persistentId&&!y.notificationDismissed(t.persistentId)){let o=window.localStorage.getItem(y.DISMISSED_NOTIFICATIONS_IN_LOCAL_STORAGE);o=o===null?t.persistentId:`${o},${t.persistentId}`,window.localStorage.setItem(y.DISMISSED_NOTIFICATIONS_IN_LOCAL_STORAGE,o)}t.deleted=!0,this.log(t.type,t.message,t.details,t.link),setTimeout(()=>{const o=this.findNotificationIndex(n);o!==-1&&(this.notifications.splice(o,1),this.requestUpdate())},this.transitionDuration)}}findNotificationIndex(n){let e=-1;return this.notifications.some((t,o)=>t.id===n?(e=o,!0):!1),e}toggleDontShowAgain(n){const e=this.findNotificationIndex(n);if(e!==-1&&!this.notifications[e].deleted){const t=this.notifications[e];t.dontShowAgain=!t.dontShowAgain,this.requestUpdate()}}setActive(n){var e,t;(e=this.frontendConnection)==null||e.setActive(n),(t=this.javaConnection)==null||t.setActive(n),window.sessionStorage.setItem(y.ACTIVE_KEY_IN_SESSION_STORAGE,n?"true":"false")}getStatusColor(n){return n==="active"?$`hsl(${y.GREEN_HSL})`:n==="inactive"?$`hsl(${y.GREY_HSL})`:n==="unavailable"?$`hsl(${y.YELLOW_HSL})`:n==="error"?$`hsl(${y.RED_HSL})`:$`none`}renderMessage(n){return R`
      <div
        class="message ${n.type} ${n.deleted?"animate-out":""} ${n.details||n.link?"has-details":""}"
      >
        <div class="message-content">
          <div class="message-heading">${n.message}</div>
          <div class="message-details" ?hidden="${!n.details&&!n.link}">
            ${n.details?R`<p>${n.details}</p>`:""}
            ${n.link?R`<a class="ahreflike" href="${n.link}" target="_blank">Learn more</a>`:""}
          </div>
          ${n.persistentId?R`<div
                class="persist ${n.dontShowAgain?"on":"off"}"
                @click=${()=>this.toggleDontShowAgain(n.id)}
              >
                Don’t show again
              </div>`:""}
        </div>
        <div class="dismiss-message" @click=${()=>this.dismissNotification(n.id)}>Dismiss</div>
      </div>
    `}render(){return R` <div
        class="window ${this.expanded&&!this.componentPickActive?"visible":"hidden"}"
        tabindex="0"
        @keydown=${n=>n.key==="Escape"&&this.expanded&&this.toggleExpanded()}
      >
        <div class="window-toolbar">
          ${this.tabs.map(n=>R`<button
                class=${Gn({tab:!0,active:this.activeTab===n.id,unreadErrors:n.id==="log"&&this.unreadErrors})}
                id="${n.id}"
                @click=${()=>{this.activeTab=n.id,n.activate&&n.activate.call(this)}}
              >
                ${n.title}
              </button> `)}
          <button class="minimize-button" title="Minimize" @click=${()=>this.toggleExpanded()}>
            <svg fill="none" height="16" viewBox="0 0 16 16" width="16" xmlns="http://www.w3.org/2000/svg">
              <g fill="#fff" opacity=".8">
                <path
                  d="m7.25 1.75c0-.41421.33579-.75.75-.75h3.25c2.0711 0 3.75 1.67893 3.75 3.75v6.5c0 2.0711-1.6789 3.75-3.75 3.75h-6.5c-2.07107 0-3.75-1.6789-3.75-3.75v-3.25c0-.41421.33579-.75.75-.75s.75.33579.75.75v3.25c0 1.2426 1.00736 2.25 2.25 2.25h6.5c1.2426 0 2.25-1.0074 2.25-2.25v-6.5c0-1.24264-1.0074-2.25-2.25-2.25h-3.25c-.41421 0-.75-.33579-.75-.75z"
                />
                <path
                  d="m2.96967 2.96967c.29289-.29289.76777-.29289 1.06066 0l5.46967 5.46967v-2.68934c0-.41421.33579-.75.75-.75.4142 0 .75.33579.75.75v4.5c0 .4142-.3358.75-.75.75h-4.5c-.41421 0-.75-.3358-.75-.75 0-.41421.33579-.75.75-.75h2.68934l-5.46967-5.46967c-.29289-.29289-.29289-.76777 0-1.06066z"
                />
              </g>
            </svg>
          </button>
        </div>
        ${this.tabs.map(n=>this.activeTab===n.id?n.render.call(this):S)}
      </div>

      <div class="notification-tray">${this.notifications.map(n=>this.renderMessage(n))}</div>
      <vaadin-dev-tools-component-picker
        .active=${this.componentPickActive}
        @component-picker-pick=${n=>{const e=n.detail.component;this.renderRoot.querySelector("#locationType").value==="create"?this.frontendConnection.sendShowComponentCreateLocation(e):this.frontendConnection.sendShowComponentAttachLocation(e),this.componentPickActive=!1}}
        @component-picker-abort=${n=>{this.componentPickActive=!1}}
      ></vaadin-dev-tools-component-picker>
      <div
        class="dev-tools ${this.splashMessage?"active":""}${this.unreadErrors?" error":""}"
        @click=${()=>this.toggleExpanded()}
      >
        ${this.unreadErrors?R`<svg
              fill="none"
              height="16"
              viewBox="0 0 16 16"
              width="16"
              xmlns="http://www.w3.org/2000/svg"
              xmlns:xlink="http://www.w3.org/1999/xlink"
              class="dev-tools-icon error"
            >
              <clipPath id="a"><path d="m0 0h16v16h-16z" /></clipPath>
              <g clip-path="url(#a)">
                <path
                  d="m6.25685 2.09894c.76461-1.359306 2.72169-1.359308 3.4863 0l5.58035 9.92056c.7499 1.3332-.2135 2.9805-1.7432 2.9805h-11.1606c-1.529658 0-2.4930857-1.6473-1.743156-2.9805z"
                  fill="#ff5c69"
                />
                <path
                  d="m7.99699 4c-.45693 0-.82368.37726-.81077.834l.09533 3.37352c.01094.38726.32803.69551.71544.69551.38741 0 .70449-.30825.71544-.69551l.09533-3.37352c.0129-.45674-.35384-.834-.81077-.834zm.00301 8c.60843 0 1-.3879 1-.979 0-.5972-.39157-.9851-1-.9851s-1 .3879-1 .9851c0 .5911.39157.979 1 .979z"
                  fill="#fff"
                />
              </g>
            </svg>`:R`<svg
              fill="none"
              height="17"
              viewBox="0 0 16 17"
              width="16"
              xmlns="http://www.w3.org/2000/svg"
              class="dev-tools-icon logo"
            >
              <g fill="#fff">
                <path
                  d="m8.88273 5.97926c0 .04401-.0032.08898-.00801.12913-.02467.42848-.37813.76767-.8117.76767-.43358 0-.78704-.34112-.81171-.76928-.00481-.04015-.00801-.08351-.00801-.12752 0-.42784-.10255-.87656-1.14434-.87656h-3.48364c-1.57118 0-2.315271-.72849-2.315271-2.21758v-1.26683c0-.42431.324618-.768314.748261-.768314.42331 0 .74441.344004.74441.768314v.42784c0 .47924.39576.81265 1.11293.81265h3.41538c1.5542 0 1.67373 1.156 1.725 1.7679h.03429c.05095-.6119.17048-1.7679 1.72468-1.7679h3.4154c.7172 0 1.0145-.32924 1.0145-.80847l-.0067-.43202c0-.42431.3227-.768314.7463-.768314.4234 0 .7255.344004.7255.768314v1.26683c0 1.48909-.6181 2.21758-2.1893 2.21758h-3.4836c-1.04182 0-1.14437.44872-1.14437.87656z"
                />
                <path
                  d="m8.82577 15.1648c-.14311.3144-.4588.5335-.82635.5335-.37268 0-.69252-.2249-.83244-.5466-.00206-.0037-.00412-.0073-.00617-.0108-.00275-.0047-.00549-.0094-.00824-.0145l-3.16998-5.87318c-.08773-.15366-.13383-.32816-.13383-.50395 0-.56168.45592-1.01879 1.01621-1.01879.45048 0 .75656.22069.96595.6993l2.16882 4.05042 2.17166-4.05524c.2069-.47379.513-.69448.9634-.69448.5603 0 1.0166.45711 1.0166 1.01879 0 .17579-.0465.35029-.1348.50523l-3.1697 5.8725c-.00503.0096-.01006.0184-.01509.0272-.00201.0036-.00402.0071-.00604.0106z"
                />
              </g>
            </svg>`}

        <span
          class="status-blip"
          style="background: linear-gradient(to right, ${this.getStatusColor(this.frontendStatus)} 50%, ${this.getStatusColor(this.javaStatus)} 50%)"
        ></span>
        ${this.splashMessage?R`<span class="status-description">${this.splashMessage}</span></div>`:S}
      </div>`}renderLog(){return R`<div class="message-tray">${this.messages.map(n=>this.renderMessage(n))}</div>`}activateLog(){this.unreadErrors=!1,this.updateComplete.then(()=>{const n=this.renderRoot.querySelector(".message-tray .message:last-child");n&&n.scrollIntoView()})}renderCode(){return R`<div class="info-tray">
      <div>
        <select id="locationType">
          <option value="create" selected>Create</option>
          <option value="attach">Attach</option>
        </select>
        <button
          class="button pick"
          @click=${()=>{this.componentPickActive=!0,Fe(()=>import("./component-picker-b3241b63.js"),[],import.meta.url)}}
        >
          Find component in code
        </button>
      </div>
      </div>
    </div>`}renderInfo(){return R`<div class="info-tray">
      <button class="button copy" @click=${this.copyInfoToClipboard}>Copy</button>
      <dl>
        <dt>${this.serverInfo.productName}</dt>
        <dd>${this.serverInfo.vaadinVersion}</dd>
        <dt>Flow</dt>
        <dd>${this.serverInfo.flowVersion}</dd>
        <dt>Java</dt>
        <dd>${this.serverInfo.javaVersion}</dd>
        <dt>OS</dt>
        <dd>${this.serverInfo.osVersion}</dd>
        <dt>Browser</dt>
        <dd>${navigator.userAgent}</dd>
        <dt>
          Live reload
          <label class="switch">
            <input
              id="toggle"
              type="checkbox"
              ?disabled=${this.liveReloadDisabled||(this.frontendStatus==="unavailable"||this.frontendStatus==="error")&&(this.javaStatus==="unavailable"||this.javaStatus==="error")}
              ?checked="${this.frontendStatus==="active"||this.javaStatus==="active"}"
              @change=${n=>this.setActive(n.target.checked)}
            />
            <span class="slider"></span>
          </label>
        </dt>
        <dd class="live-reload-status" style="--status-color: ${this.getStatusColor(this.javaStatus)}">
          Java ${this.javaStatus} ${this.backend?`(${y.BACKEND_DISPLAY_NAME[this.backend]})`:""}
        </dd>
        <dd class="live-reload-status" style="--status-color: ${this.getStatusColor(this.frontendStatus)}">
          Front end ${this.frontendStatus}
        </dd>
      </dl>
    </div>`}renderFeatures(){return R`<div class="features-tray">
      ${this.features.map(n=>R`<div class="feature">
          <label class="switch">
            <input
              class="feature-toggle"
              id="feature-toggle-${n.id}"
              type="checkbox"
              ?checked=${n.enabled}
              @change=${e=>this.toggleFeatureFlag(e,n)}
            />
            <span class="slider"></span>
            ${n.title}
          </label>
          <a class="ahreflike" href="${n.moreInfoLink}" target="_blank">Learn more</a>
        </div>`)}
    </div>`}copyInfoToClipboard(){const n=this.renderRoot.querySelectorAll(".info-tray dt, .info-tray dd"),e=Array.from(n).map(t=>(t.localName==="dd"?": ":`
`)+t.textContent.trim()).join("").replace(/^\n/,"");zi(e),this.showNotification("information","Environment information copied to clipboard",void 0,void 0,"versionInfoCopied")}toggleFeatureFlag(n,e){const t=n.target.checked;this.frontendConnection?(this.frontendConnection.setFeature(e.id,t),this.showNotification("information",`“${e.title}” ${t?"enabled":"disabled"}`,e.requiresServerRestart?"This feature requires a server restart":void 0,void 0,`feature${e.id}${t?"Enabled":"Disabled"}`)):this.log("error",`Unable to toggle feature ${e.title}: No server connection available`)}};let _=y;_.BLUE_HSL=$`206, 100%, 70%`;_.GREEN_HSL=$`145, 80%, 42%`;_.GREY_HSL=$`0, 0%, 50%`;_.YELLOW_HSL=$`38, 98%, 64%`;_.RED_HSL=$`355, 100%, 68%`;_.MAX_LOG_ROWS=1e3;_.DISMISSED_NOTIFICATIONS_IN_LOCAL_STORAGE="vaadin.live-reload.dismissedNotifications";_.ACTIVE_KEY_IN_SESSION_STORAGE="vaadin.live-reload.active";_.TRIGGERED_KEY_IN_SESSION_STORAGE="vaadin.live-reload.triggered";_.TRIGGERED_COUNT_KEY_IN_SESSION_STORAGE="vaadin.live-reload.triggeredCount";_.AUTO_DEMOTE_NOTIFICATION_DELAY=5e3;_.HOTSWAP_AGENT="HOTSWAP_AGENT";_.JREBEL="JREBEL";_.SPRING_BOOT_DEVTOOLS="SPRING_BOOT_DEVTOOLS";_.BACKEND_DISPLAY_NAME={HOTSWAP_AGENT:"HotswapAgent",JREBEL:"JRebel",SPRING_BOOT_DEVTOOLS:"Spring Boot Devtools"};x([w({type:String})],_.prototype,"url",2);x([w({type:Boolean,attribute:!0})],_.prototype,"liveReloadDisabled",2);x([w({type:String})],_.prototype,"backend",2);x([w({type:Number})],_.prototype,"springBootLiveReloadPort",2);x([w({type:Boolean,attribute:!1})],_.prototype,"expanded",2);x([w({type:Array,attribute:!1})],_.prototype,"messages",2);x([w({type:String,attribute:!1})],_.prototype,"splashMessage",2);x([w({type:Array,attribute:!1})],_.prototype,"notifications",2);x([w({type:String,attribute:!1})],_.prototype,"frontendStatus",2);x([w({type:String,attribute:!1})],_.prototype,"javaStatus",2);x([me()],_.prototype,"tabs",2);x([me()],_.prototype,"activeTab",2);x([me()],_.prototype,"serverInfo",2);x([me()],_.prototype,"features",2);x([me()],_.prototype,"unreadErrors",2);x([$i(".window")],_.prototype,"root",2);x([me()],_.prototype,"componentPickActive",2);customElements.get("vaadin-dev-tools")===void 0&&customElements.define("vaadin-dev-tools",_);(function(){if(typeof document>"u"||"adoptedStyleSheets"in document)return;var n="ShadyCSS"in window&&!ShadyCSS.nativeShadow,e=document.implementation.createHTMLDocument(""),t=new WeakMap,o=typeof DOMException=="object"?Error:DOMException,i=Object.defineProperty,r=Array.prototype.forEach,s=/@import.+?;?$/gm;function l(d){var u=d.replace(s,"");return u!==d&&console.warn("@import rules are not allowed here. See https://github.com/WICG/construct-stylesheets/issues/119#issuecomment-588352418"),u.trim()}function a(d){return"isConnected"in d?d.isConnected:document.contains(d)}function c(d){return d.filter(function(u,v){return d.indexOf(u)===v})}function h(d,u){return d.filter(function(v){return u.indexOf(v)===-1})}function f(d){d.parentNode.removeChild(d)}function p(d){return d.shadowRoot||t.get(d)}var m=["addRule","deleteRule","insertRule","removeRule"],G=CSSStyleSheet,q=G.prototype;q.replace=function(){return Promise.reject(new o("Can't call replace on non-constructed CSSStyleSheets."))},q.replaceSync=function(){throw new o("Failed to execute 'replaceSync' on 'CSSStyleSheet': Can't call replaceSync on non-constructed CSSStyleSheets.")};function H(d){return typeof d=="object"?ie.isPrototypeOf(d)||q.isPrototypeOf(d):!1}function Xe(d){return typeof d=="object"?q.isPrototypeOf(d):!1}var A=new WeakMap,U=new WeakMap,ne=new WeakMap,oe=new WeakMap;function Qe(d,u){var v=document.createElement("style");return ne.get(d).set(u,v),U.get(d).push(u),v}function W(d,u){return ne.get(d).get(u)}function $e(d,u){ne.get(d).delete(u),U.set(d,U.get(d).filter(function(v){return v!==u}))}function zt(d,u){requestAnimationFrame(function(){u.textContent=A.get(d).textContent,oe.get(d).forEach(function(v){return u.sheet[v.method].apply(u.sheet,v.args)})})}function ke(d){if(!A.has(d))throw new TypeError("Illegal invocation")}function Ze(){var d=this,u=document.createElement("style");e.body.appendChild(u),A.set(d,u),U.set(d,[]),ne.set(d,new WeakMap),oe.set(d,[])}var ie=Ze.prototype;ie.replace=function(u){try{return this.replaceSync(u),Promise.resolve(this)}catch(v){return Promise.reject(v)}},ie.replaceSync=function(u){if(ke(this),typeof u=="string"){var v=this;A.get(v).textContent=l(u),oe.set(v,[]),U.get(v).forEach(function(k){k.isConnected()&&zt(v,W(v,k))})}},i(ie,"cssRules",{configurable:!0,enumerable:!0,get:function(){return ke(this),A.get(this).sheet.cssRules}}),i(ie,"media",{configurable:!0,enumerable:!0,get:function(){return ke(this),A.get(this).sheet.media}}),m.forEach(function(d){ie[d]=function(){var u=this;ke(u);var v=arguments;oe.get(u).push({method:d,args:v}),U.get(u).forEach(function(N){if(N.isConnected()){var T=W(u,N).sheet;T[d].apply(T,v)}});var k=A.get(u).sheet;return k[d].apply(k,v)}}),i(Ze,Symbol.hasInstance,{configurable:!0,value:H});var Ft={childList:!0,subtree:!0},Bt=new WeakMap;function re(d){var u=Bt.get(d);return u||(u=new jt(d),Bt.set(d,u)),u}function Ht(d){i(d.prototype,"adoptedStyleSheets",{configurable:!0,enumerable:!0,get:function(){return re(this).sheets},set:function(u){re(this).update(u)}})}function et(d,u){for(var v=document.createNodeIterator(d,NodeFilter.SHOW_ELEMENT,function(N){return p(N)?NodeFilter.FILTER_ACCEPT:NodeFilter.FILTER_REJECT},null,!1),k=void 0;k=v.nextNode();)u(p(k))}var Re=new WeakMap,se=new WeakMap,Ne=new WeakMap;function io(d,u){return u instanceof HTMLStyleElement&&se.get(d).some(function(v){return W(v,d)})}function Wt(d){var u=Re.get(d);return u instanceof Document?u.body:u}function tt(d){var u=document.createDocumentFragment(),v=se.get(d),k=Ne.get(d),N=Wt(d);k.disconnect(),v.forEach(function(T){u.appendChild(W(T,d)||Qe(T,d))}),N.insertBefore(u,null),k.observe(N,Ft),v.forEach(function(T){zt(T,W(T,d))})}function jt(d){var u=this;u.sheets=[],Re.set(u,d),se.set(u,[]),Ne.set(u,new MutationObserver(function(v,k){if(!document){k.disconnect();return}v.forEach(function(N){n||r.call(N.addedNodes,function(T){T instanceof Element&&et(T,function(ae){re(ae).connect()})}),r.call(N.removedNodes,function(T){T instanceof Element&&(io(u,T)&&tt(u),n||et(T,function(ae){re(ae).disconnect()}))})})}))}if(jt.prototype={isConnected:function(){var d=Re.get(this);return d instanceof Document?d.readyState!=="loading":a(d.host)},connect:function(){var d=Wt(this);Ne.get(this).observe(d,Ft),se.get(this).length>0&&tt(this),et(d,function(u){re(u).connect()})},disconnect:function(){Ne.get(this).disconnect()},update:function(d){var u=this,v=Re.get(u)===document?"Document":"ShadowRoot";if(!Array.isArray(d))throw new TypeError("Failed to set the 'adoptedStyleSheets' property on "+v+": Iterator getter is not callable.");if(!d.every(H))throw new TypeError("Failed to set the 'adoptedStyleSheets' property on "+v+": Failed to convert value to 'CSSStyleSheet'");if(d.some(Xe))throw new TypeError("Failed to set the 'adoptedStyleSheets' property on "+v+": Can't adopt non-constructed stylesheets");u.sheets=d;var k=se.get(u),N=c(d),T=h(k,N);T.forEach(function(ae){f(W(ae,u)),$e(ae,u)}),se.set(u,N),u.isConnected()&&N.length>0&&tt(u)}},window.CSSStyleSheet=Ze,Ht(Document),"ShadowRoot"in window){Ht(ShadowRoot);var Gt=Element.prototype,ro=Gt.attachShadow;Gt.attachShadow=function(u){var v=ro.call(this,u);return u.mode==="closed"&&t.set(this,v),v}}var Ie=re(document);Ie.isConnected()?Ie.connect():document.addEventListener("DOMContentLoaded",Ie.connect.bind(Ie))})();/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */class qi extends HTMLElement{static get version(){return"24.0.2"}}customElements.define("vaadin-lumo-styles",qi);/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Ki=n=>class extends n{static get properties(){return{_theme:{type:String,readOnly:!0}}}static get observedAttributes(){return[...super.observedAttributes,"theme"]}attributeChangedCallback(t,o,i){super.attributeChangedCallback(t,o,i),t==="theme"&&this._set_theme(i)}};/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const Jn=[];function Xn(n){return n&&Object.prototype.hasOwnProperty.call(n,"__themes")}function Yi(n){return Xn(customElements.get(n))}function Ji(n=[]){return[n].flat(1/0).filter(e=>e instanceof Pt?!0:(console.warn("An item in styles is not of type CSSResult. Use `unsafeCSS` or `css`."),!1))}function Qn(n,e,t={}){n&&Yi(n)&&console.warn(`The custom element definition for "${n}"
      was finalized before a style module was registered.
      Make sure to add component specific style modules before
      importing the corresponding custom element.`),e=Ji(e),window.Vaadin&&window.Vaadin.styleModules?window.Vaadin.styleModules.registerStyles(n,e,t):Jn.push({themeFor:n,styles:e,include:t.include,moduleId:t.moduleId})}function Nt(){return window.Vaadin&&window.Vaadin.styleModules?window.Vaadin.styleModules.getAllThemes():Jn}function Xi(n,e){return(n||"").split(" ").some(t=>new RegExp(`^${t.split("*").join(".*")}$`,"u").test(e))}function Qi(n=""){let e=0;return n.startsWith("lumo-")||n.startsWith("material-")?e=1:n.startsWith("vaadin-")&&(e=2),e}function Zn(n){const e=[];return n.include&&[].concat(n.include).forEach(t=>{const o=Nt().find(i=>i.moduleId===t);o?e.push(...Zn(o),...o.styles):console.warn(`Included moduleId ${t} not found in style registry`)},n.styles),e}function Zi(n,e){const t=document.createElement("style");t.innerHTML=n.map(o=>o.cssText).join(`
`),e.content.appendChild(t)}function er(n){const e=`${n}-default-theme`,t=Nt().filter(o=>o.moduleId!==e&&Xi(o.themeFor,n)).map(o=>({...o,styles:[...Zn(o),...o.styles],includePriority:Qi(o.moduleId)})).sort((o,i)=>i.includePriority-o.includePriority);return t.length>0?t:Nt().filter(o=>o.moduleId===e)}const lr=n=>class extends Ki(n){static finalize(){if(super.finalize(),this.elementStyles)return;const t=this.prototype._template;!t||Xn(this)||Zi(this.getStylesForThis(),t)}static finalizeStyles(t){const o=this.getStylesForThis();return t?[...super.finalizeStyles(t),...o]:o}static getStylesForThis(){const t=Object.getPrototypeOf(this.prototype),o=(t?t.constructor.__themes:[])||[];this.__themes=[...o,...er(this.is)];const i=this.__themes.flatMap(r=>r.styles);return i.filter((r,s)=>s===i.lastIndexOf(r))}};/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const tr=$`
  :host {
    /* Base (background) */
    --lumo-base-color: #fff;

    /* Tint */
    --lumo-tint-5pct: hsla(0, 0%, 100%, 0.3);
    --lumo-tint-10pct: hsla(0, 0%, 100%, 0.37);
    --lumo-tint-20pct: hsla(0, 0%, 100%, 0.44);
    --lumo-tint-30pct: hsla(0, 0%, 100%, 0.5);
    --lumo-tint-40pct: hsla(0, 0%, 100%, 0.57);
    --lumo-tint-50pct: hsla(0, 0%, 100%, 0.64);
    --lumo-tint-60pct: hsla(0, 0%, 100%, 0.7);
    --lumo-tint-70pct: hsla(0, 0%, 100%, 0.77);
    --lumo-tint-80pct: hsla(0, 0%, 100%, 0.84);
    --lumo-tint-90pct: hsla(0, 0%, 100%, 0.9);
    --lumo-tint: #fff;

    /* Shade */
    --lumo-shade-5pct: hsla(214, 61%, 25%, 0.05);
    --lumo-shade-10pct: hsla(214, 57%, 24%, 0.1);
    --lumo-shade-20pct: hsla(214, 53%, 23%, 0.16);
    --lumo-shade-30pct: hsla(214, 50%, 22%, 0.26);
    --lumo-shade-40pct: hsla(214, 47%, 21%, 0.38);
    --lumo-shade-50pct: hsla(214, 45%, 20%, 0.52);
    --lumo-shade-60pct: hsla(214, 43%, 19%, 0.6);
    --lumo-shade-70pct: hsla(214, 42%, 18%, 0.69);
    --lumo-shade-80pct: hsla(214, 41%, 17%, 0.83);
    --lumo-shade-90pct: hsla(214, 40%, 16%, 0.94);
    --lumo-shade: hsl(214, 35%, 15%);

    /* Contrast */
    --lumo-contrast-5pct: var(--lumo-shade-5pct);
    --lumo-contrast-10pct: var(--lumo-shade-10pct);
    --lumo-contrast-20pct: var(--lumo-shade-20pct);
    --lumo-contrast-30pct: var(--lumo-shade-30pct);
    --lumo-contrast-40pct: var(--lumo-shade-40pct);
    --lumo-contrast-50pct: var(--lumo-shade-50pct);
    --lumo-contrast-60pct: var(--lumo-shade-60pct);
    --lumo-contrast-70pct: var(--lumo-shade-70pct);
    --lumo-contrast-80pct: var(--lumo-shade-80pct);
    --lumo-contrast-90pct: var(--lumo-shade-90pct);
    --lumo-contrast: var(--lumo-shade);

    /* Text */
    --lumo-header-text-color: var(--lumo-contrast);
    --lumo-body-text-color: var(--lumo-contrast-90pct);
    --lumo-secondary-text-color: var(--lumo-contrast-70pct);
    --lumo-tertiary-text-color: var(--lumo-contrast-50pct);
    --lumo-disabled-text-color: var(--lumo-contrast-30pct);

    /* Primary */
    --lumo-primary-color: hsl(214, 100%, 48%);
    --lumo-primary-color-50pct: hsla(214, 100%, 49%, 0.76);
    --lumo-primary-color-10pct: hsla(214, 100%, 60%, 0.13);
    --lumo-primary-text-color: hsl(214, 100%, 43%);
    --lumo-primary-contrast-color: #fff;

    /* Error */
    --lumo-error-color: hsl(3, 85%, 48%);
    --lumo-error-color-50pct: hsla(3, 85%, 49%, 0.5);
    --lumo-error-color-10pct: hsla(3, 85%, 49%, 0.1);
    --lumo-error-text-color: hsl(3, 89%, 42%);
    --lumo-error-contrast-color: #fff;

    /* Success */
    --lumo-success-color: hsl(145, 72%, 30%);
    --lumo-success-color-50pct: hsla(145, 72%, 31%, 0.5);
    --lumo-success-color-10pct: hsla(145, 72%, 31%, 0.1);
    --lumo-success-text-color: hsl(145, 85%, 25%);
    --lumo-success-contrast-color: #fff;
  }
`,eo=document.createElement("template");eo.innerHTML=`<style>${tr.toString().replace(":host","html")}</style>`;document.head.appendChild(eo.content);const to=$`
  [theme~='dark'] {
    /* Base (background) */
    --lumo-base-color: hsl(214, 35%, 21%);

    /* Tint */
    --lumo-tint-5pct: hsla(214, 65%, 85%, 0.06);
    --lumo-tint-10pct: hsla(214, 60%, 80%, 0.14);
    --lumo-tint-20pct: hsla(214, 64%, 82%, 0.23);
    --lumo-tint-30pct: hsla(214, 69%, 84%, 0.32);
    --lumo-tint-40pct: hsla(214, 73%, 86%, 0.41);
    --lumo-tint-50pct: hsla(214, 78%, 88%, 0.5);
    --lumo-tint-60pct: hsla(214, 82%, 90%, 0.58);
    --lumo-tint-70pct: hsla(214, 87%, 92%, 0.69);
    --lumo-tint-80pct: hsla(214, 91%, 94%, 0.8);
    --lumo-tint-90pct: hsla(214, 96%, 96%, 0.9);
    --lumo-tint: hsl(214, 100%, 98%);

    /* Shade */
    --lumo-shade-5pct: hsla(214, 0%, 0%, 0.07);
    --lumo-shade-10pct: hsla(214, 4%, 2%, 0.15);
    --lumo-shade-20pct: hsla(214, 8%, 4%, 0.23);
    --lumo-shade-30pct: hsla(214, 12%, 6%, 0.32);
    --lumo-shade-40pct: hsla(214, 16%, 8%, 0.41);
    --lumo-shade-50pct: hsla(214, 20%, 10%, 0.5);
    --lumo-shade-60pct: hsla(214, 24%, 12%, 0.6);
    --lumo-shade-70pct: hsla(214, 28%, 13%, 0.7);
    --lumo-shade-80pct: hsla(214, 32%, 13%, 0.8);
    --lumo-shade-90pct: hsla(214, 33%, 13%, 0.9);
    --lumo-shade: hsl(214, 33%, 13%);

    /* Contrast */
    --lumo-contrast-5pct: var(--lumo-tint-5pct);
    --lumo-contrast-10pct: var(--lumo-tint-10pct);
    --lumo-contrast-20pct: var(--lumo-tint-20pct);
    --lumo-contrast-30pct: var(--lumo-tint-30pct);
    --lumo-contrast-40pct: var(--lumo-tint-40pct);
    --lumo-contrast-50pct: var(--lumo-tint-50pct);
    --lumo-contrast-60pct: var(--lumo-tint-60pct);
    --lumo-contrast-70pct: var(--lumo-tint-70pct);
    --lumo-contrast-80pct: var(--lumo-tint-80pct);
    --lumo-contrast-90pct: var(--lumo-tint-90pct);
    --lumo-contrast: var(--lumo-tint);

    /* Text */
    --lumo-header-text-color: var(--lumo-contrast);
    --lumo-body-text-color: var(--lumo-contrast-90pct);
    --lumo-secondary-text-color: var(--lumo-contrast-70pct);
    --lumo-tertiary-text-color: var(--lumo-contrast-50pct);
    --lumo-disabled-text-color: var(--lumo-contrast-30pct);

    /* Primary */
    --lumo-primary-color: hsl(214, 90%, 48%);
    --lumo-primary-color-50pct: hsla(214, 90%, 70%, 0.69);
    --lumo-primary-color-10pct: hsla(214, 90%, 55%, 0.13);
    --lumo-primary-text-color: hsl(214, 90%, 77%);
    --lumo-primary-contrast-color: #fff;

    /* Error */
    --lumo-error-color: hsl(3, 79%, 49%);
    --lumo-error-color-50pct: hsla(3, 75%, 62%, 0.5);
    --lumo-error-color-10pct: hsla(3, 75%, 62%, 0.14);
    --lumo-error-text-color: hsl(3, 100%, 80%);

    /* Success */
    --lumo-success-color: hsl(145, 72%, 30%);
    --lumo-success-color-50pct: hsla(145, 92%, 51%, 0.5);
    --lumo-success-color-10pct: hsla(145, 92%, 51%, 0.1);
    --lumo-success-text-color: hsl(145, 85%, 46%);
  }

  html {
    color: var(--lumo-body-text-color);
    background-color: var(--lumo-base-color);
    color-scheme: light;
  }

  [theme~='dark'] {
    color: var(--lumo-body-text-color);
    background-color: var(--lumo-base-color);
    color-scheme: dark;
  }

  h1,
  h2,
  h3,
  h4,
  h5,
  h6 {
    color: var(--lumo-header-text-color);
  }

  a:where(:any-link) {
    color: var(--lumo-primary-text-color);
  }

  a:not(:any-link) {
    color: var(--lumo-disabled-text-color);
  }

  blockquote {
    color: var(--lumo-secondary-text-color);
  }

  code,
  pre {
    background-color: var(--lumo-contrast-10pct);
    border-radius: var(--lumo-border-radius-m);
  }
`;Qn("",to,{moduleId:"lumo-color"});/**
 * @license
 * Copyright (c) 2017 - 2023 Vaadin Ltd.
 * This program is available under Apache License Version 2.0, available at https://vaadin.com/license/
 */const nr=$`
  :host {
    /* prettier-ignore */
    --lumo-font-family: -apple-system, BlinkMacSystemFont, 'Roboto', 'Segoe UI', Helvetica, Arial, sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol';

    /* Font sizes */
    --lumo-font-size-xxs: 0.75rem;
    --lumo-font-size-xs: 0.8125rem;
    --lumo-font-size-s: 0.875rem;
    --lumo-font-size-m: 1rem;
    --lumo-font-size-l: 1.125rem;
    --lumo-font-size-xl: 1.375rem;
    --lumo-font-size-xxl: 1.75rem;
    --lumo-font-size-xxxl: 2.5rem;

    /* Line heights */
    --lumo-line-height-xs: 1.25;
    --lumo-line-height-s: 1.375;
    --lumo-line-height-m: 1.625;
  }
`,no=$`
  body,
  :host {
    font-family: var(--lumo-font-family);
    font-size: var(--lumo-font-size-m);
    line-height: var(--lumo-line-height-m);
    -webkit-text-size-adjust: 100%;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
  }

  small,
  [theme~='font-size-s'] {
    font-size: var(--lumo-font-size-s);
    line-height: var(--lumo-line-height-s);
  }

  [theme~='font-size-xs'] {
    font-size: var(--lumo-font-size-xs);
    line-height: var(--lumo-line-height-xs);
  }

  :where(h1, h2, h3, h4, h5, h6) {
    font-weight: 600;
    line-height: var(--lumo-line-height-xs);
    margin: 0;
  }

  :where(h1) {
    font-size: var(--lumo-font-size-xxxl);
  }

  :where(h2) {
    font-size: var(--lumo-font-size-xxl);
  }

  :where(h3) {
    font-size: var(--lumo-font-size-xl);
  }

  :where(h4) {
    font-size: var(--lumo-font-size-l);
  }

  :where(h5) {
    font-size: var(--lumo-font-size-m);
  }

  :where(h6) {
    font-size: var(--lumo-font-size-xs);
    text-transform: uppercase;
    letter-spacing: 0.03em;
  }

  p,
  blockquote {
    margin-top: 0.5em;
    margin-bottom: 0.75em;
  }

  a {
    text-decoration: none;
  }

  a:where(:any-link):hover {
    text-decoration: underline;
  }

  hr {
    display: block;
    align-self: stretch;
    height: 1px;
    border: 0;
    padding: 0;
    margin: var(--lumo-space-s) calc(var(--lumo-border-radius-m) / 2);
    background-color: var(--lumo-contrast-10pct);
  }

  blockquote {
    border-left: 2px solid var(--lumo-contrast-30pct);
  }

  b,
  strong {
    font-weight: 600;
  }

  /* RTL specific styles */
  blockquote[dir='rtl'] {
    border-left: none;
    border-right: 2px solid var(--lumo-contrast-30pct);
  }
`;Qn("",no,{moduleId:"lumo-typography"});const oo=document.createElement("template");oo.innerHTML=`<style>${nr.toString().replace(":host","html")}</style>`;document.head.appendChild(oo.content);const Tn=(n,e)=>{const t=document.createElement("style");t.type="text/css",t.appendChild(document.createTextNode(n)),e===document?document.head.appendChild(t):e.appendChild(t)};window.Vaadin=window.Vaadin||{};window.Vaadin.theme=window.Vaadin.theme||{};window.Vaadin.theme.injectedGlobalCss=[];const or=n=>{document._vaadintheme_collaborationengine_componentCss||(document._vaadintheme_collaborationengine_componentCss=!0),Tn(to.cssText,n),Tn(no.cssText,n)},ir=or;ir(document);export{Ii as D,te as L,Ri as P,lr as T,Ki as a,Ue as b,$ as c,Z as d,Ni as e,to as f,Jn as g,R as h,w as i,me as j,S as n,Gi as p,$i as q,Qn as r,sr as s,no as t,Xo as u};
