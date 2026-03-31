import { GT_Cookie } from "./util.js";

function showHelloMsg() {
  // let id = GT_Cookie.getEmployeeID();
  // let lastName = GT_Cookie.getEmployeeLastName();
  // let firstName = GT_Cookie.getEmployeeFirstName();
  // let div = document.createElement("div");
  // div.innerHTML = `
  //       <div class="mt-4">
  //           <h5>Welcome <span class="badge text-bg-info">${firstName} ${lastName}</span></h5>
  //           <p>Employee ID: ${id}</p>
  //       </div>
  //   `;
  // let container = document.getElementById("main-container");
  // container.appendChild(div);
}

document.addEventListener("DOMContentLoaded", function () {
  fetch("/get_flash_messages")
    .then((response) => response.json())
    .then((messages) => {
      if (messages.length > 0) {
        let user = messages[0][1];
        // console.log(user);
        GT_Cookie.setUserCookie(user);
      }
      GT_Cookie.appendNav();
      showHelloMsg();
    });
});

console.log("home js");
