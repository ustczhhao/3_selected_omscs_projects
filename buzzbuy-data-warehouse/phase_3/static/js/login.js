function login() {
  let loginBtn = document.getElementById("login");
  if (!loginBtn) return;

  cleanup();
  loginBtn.addEventListener("click", async () => {
    res = null;

    await fetch("/login_user", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        username: document.getElementById("username").value,
        password: document.getElementById("password").value,
      }),
    }).then(async (response) => {
      if (response.redirected) {
        // Handle the redirect
        window.location.href = response.url;
      } else if (response.status == 401) {
        let div = document.createElement("div");
        div.innerHTML = `
        <div class="alert alert-danger mt-4" role="alert" id="login-alert">
          Invalid Employee ID or Password.
        </div>`;
        document.querySelector("div").appendChild(div);
      } else {
        res = response.json();
      }
    });
    return res;
  });
}
function cleanup() {
  let warning = document.getElementById("login-alert");
  if (warning) warning.remove();
}

function cleanWarning() {
  let un = document.getElementById("username");
  un.addEventListener("change", () => {
    console.log("change");
    cleanup();
  });
  let pwd = document.getElementById("password");
  pwd.addEventListener("change", () => {
    cleanup();
  });
}

document.addEventListener("DOMContentLoaded", function () {
  login();
  cleanWarning();
});
