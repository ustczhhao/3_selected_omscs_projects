import { GT_Cookie } from "./util.js";

// Fetch full_access information
document.addEventListener("DOMContentLoaded", function () {
  let employeeID = GT_Cookie.getEmployeeID();

  let full_access = GT_Cookie.getEmployeeFullAccess();
  if (!full_access) {
    document.getElementById("add-holiday-button").style.display = "none";
    fetchHolidays(false);
  } else {
    fetchHolidays(true);
  }

  document
    .getElementById("add-holiday-button")
    .addEventListener("click", function () {
      let modal = new bootstrap.Modal(
        document.getElementById("addHolidayModal")
      );
      modal.show();
    });

  document
    .getElementById("add-holiday-form")
    .addEventListener("submit", function (event) {
      event.preventDefault();
      addHoliday();
    });
});

// Fetch Holiday Information
function fetchHolidays(hasFullAccess) {
  fetch("/get_holidays")
    .then((response) => response.json())
    .then((data) => {
      console.log(data);
      let tbody = document.getElementById("holiday-table-body");
      let thead = document.querySelector("thead tr");
      tbody.innerHTML = "";

      // Hide "Actions" Column
      if (hasFullAccess) {
        thead.innerHTML = `
          <th>Business Date</th>
          <th>Holiday Name</th>
          <th>Employee ID</th>
          <th>Actions</th>
        `;
      } else {
        thead.innerHTML = `
          <th>Business Date</th>
          <th>Holiday Name</th>
          <th>Employee ID</th>
          <th> </th>
        `;
      }

      data.forEach((holiday) => {
        let row = document.createElement("tr");
        row.innerHTML = `
          <td>${holiday.business_date}</td>
          <td>${holiday.holiday_name}</td>
          <td>${holiday.employeeID}</td>
          <td>${
            hasFullAccess
              ? `<button class="btn btn-danger" onclick="deleteHoliday('${holiday.business_date}')">Delete</button>`
              : ""
          }</td>
        `;
        tbody.appendChild(row);
      });
      
    })
    .catch((error) => {
      console.error("Error in fetchHolidays fetch:", error);
    });
}

// Add Holiday
function addHoliday() {
  let businessDate = document.getElementById("business-date").value;
  let holidayName = document.getElementById("holiday-name").value;

  fetch("/add_holiday", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      business_date: businessDate,
      holiday_name: holidayName,
      employeeID: GT_Cookie.getEmployeeID(),
    }),
  })
    .then((response) => response.json())
    .then((data) => {
      if (data.success) {
        fetchHolidays(true);
        let modal = bootstrap.Modal.getInstance(
          document.getElementById("addHolidayModal")
        );
        modal.hide();
      } else {
        alert("Error adding holiday: " + data.message);
      }
    });
}

// Delete Holiday (global)
window.deleteHoliday = function (businessDate, holidayName) {
  fetch("/delete_holiday", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      business_date: businessDate,
    }),
  })
    .then((response) => response.json())
    .then((data) => {
      console.log("Delete response:", data); // Debug
      if (data.success) {
        fetchHolidays(true);
      } else {
        alert("Error deleting holiday: " + data.message);
      }
    })
    .catch((error) => {
      console.error("Error in deleteHoliday fetch:", error);
    });
};
