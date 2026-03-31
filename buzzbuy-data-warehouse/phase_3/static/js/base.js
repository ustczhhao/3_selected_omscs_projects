import { GT_Cookie } from "./util.js";

document.addEventListener("DOMContentLoaded", function () {
  let page = window.location.pathname;
  if (page.slice(1, 5) == "home") return;

  GT_Cookie.appendNav();
});

console.log("basejs");
