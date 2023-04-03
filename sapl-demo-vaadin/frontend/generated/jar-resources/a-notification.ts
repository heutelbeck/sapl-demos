import '@vaadin/vaadin-notification';

interface Options {
  position?: string;
  theme?: string;
  duration?: number;
}
export const showNotification = (
  text: string,
  options: Options = { position: "middle" }
) => {
  _showNotification(text, options);
};

export const showErrorNotification = (
  text: string,
  options: Options = { position: "middle", duration: -1, theme: "error" }
) => {
  _showNotification(text, options);
};

const _showNotification = (text: string, options: Options) => {
  const n: any = document.createElement("vaadin-notification");

  n.renderer = (root: HTMLElement) => {
    root.innerHTML = `<span></span>`;
    root.querySelector("span")!.innerText = text;
  };
  document.body.appendChild(n);
  n.opened = true;
  n.addEventListener("opened-changed", (e: CustomEvent) => {
    if (!e.detail.value) {
      document.body.removeChild(n);
    }
  });
  n._container.addEventListener("click", () => {
    n.opened = false;
  });

  if (options.theme) {
    n.setAttribute("theme", options.theme);
  }
  if (options.position) {
    n.position = options.position;
  }
  if (options.duration) {
    n.duration = options.duration;
  }

  return n;
};
