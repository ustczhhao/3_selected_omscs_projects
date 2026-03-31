// console.log("router");

class Router {
  static loadPage() {
    document.addEventListener("DOMContentLoaded", function () {
      const currentPath = window.location.pathname;
      // console.log(currentPath);
      fetch(`${currentPath}.html`)
        .then((response) => {
          if (!response.ok) {
            throw new Error("Network response was not ok");
          }
          return response.text();
        })
        .then((data) => {
          document.body.innerHTML = data;
        })
        .catch((error) => console.error("Error fetching header:", error));
    });
  }
}
Router.loadPage();
