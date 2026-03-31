export class GT_Cookie {
  static setUserCookie(user) {
    GT_Cookie._deleteAllCookies();

    const expirationDate = new Date();
    expirationDate.setHours(24);
    const cookieStr = `user=${JSON.stringify(
      user
    )};expires=${expirationDate.toLocaleString()};path=/`;
    document.cookie = cookieStr;
    // console.log(cookieStr);
  }
  static _deleteAllCookies() {
    const cookies = document.cookie.split(";");
    for (let i = 0; i < cookies.length; i++) {
      const cookie = cookies[i];
      const eqPos = cookie.indexOf("=");
      const name = eqPos > -1 ? cookie[(0, eqPos)] : cookie;
      document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/";
    }
  }

  static getEmployeeID() {
    return GT_Cookie._getUserCookieValue(
      GT_Cookie._getCookieValue("user"),
      "employeeID"
    );
  }

  static getEmployeeLastName() {
    return GT_Cookie._getUserCookieValue(
      GT_Cookie._getCookieValue("user"),
      "last_name"
    );
  }

  static getEmployeeFirstName() {
    return GT_Cookie._getUserCookieValue(
      GT_Cookie._getCookieValue("user"),
      "first_name"
    );
  }

  static getEmployeeAuditLog() {
    return GT_Cookie._getUserCookieValue(
      GT_Cookie._getCookieValue("user"),
      "can_view_audit_log"
    );
  }

  static getEmployeeFullAccess() {
    return GT_Cookie._getUserCookieValue(
      GT_Cookie._getCookieValue("user"),
      "has_all_district"
    );
  }

  // static hasFullAccess() {
  //   const districtsAssigned = GT_Cookie._getUserCookieValue(
  //     GT_Cookie._getCookieValue("user"),
  //     "districts_assigned"
  //   );
  //   if (!districtsAssigned) return false;
  //   const allDistricts = ["1", "2", "3", "4", "5", "6"];
  //   const assignedDistricts = districtsAssigned.split(",");
  //   return allDistricts.every(district => assignedDistricts.includes(district));
  // }

  static _getCookieValue(key) {
    const cookies = document.cookie.split("; ");
    // console.log(cookies);
    for (const cookie of cookies) {
      const [name, value] = cookie.split("=");
      if (name == key) return value;
    }
  }

  static _getUserCookieValue(jsonStr, key) {
    try {
      let obj = JSON.parse(jsonStr);
      // console.log(obj);
      return obj[key];
    } catch (e) {}
    return;
  }

  static appendNav() {
    let id = GT_Cookie.getEmployeeID();
    let homeLink = document.getElementById("home-link");
    homeLink.href = `/home/${id}`;

    let view_audit_log = GT_Cookie.getEmployeeAuditLog();
    // console.log(view_audit_log);
    let link = document.getElementById("audit-log-link");
    if (view_audit_log == 1) {
      link.innerHTML = '<a class="nav-link" href="/audit_log">Audit Log</a>';
    }

    let full_access = GT_Cookie.getEmployeeFullAccess();
    console.log("full_access:", full_access);
    let corpReports = document.getElementById("nav-ul");
    if (full_access) {
      console.log("append");
      let report = document.createElement("div");
      report.innerHTML = `<li><hr class="dropdown-divider" /></li>
                    <li>
                      <a class="dropdown-item" href="/revenue_state"
                        >Store Revenue by Year by State</a
                      >
                    </li>
                    <li>
                      <a class="dropdown-item" href="/district_volume"
                        >District with Highest Volume for each Category</a
                      >
                    </li>
                    <li>
                      <a class="dropdown-item" href="/revenue_population"
                        >Revenue by Population</a
                      >
                    </li>`;
      corpReports.appendChild(report);
    }
  }
}
