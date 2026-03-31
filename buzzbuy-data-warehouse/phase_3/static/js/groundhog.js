document.addEventListener("DOMContentLoaded", function () {
  fetch("/get_flash_messages")
    .then((response) => response.json())
    .then((messages) => {
      if (messages.length > 0) {
        let data = messages[0][1].data;

        let div = document.createElement("div");
        div.innerHTML = `
            <div class="mt-3 rounded-2 overflow-hidden border border-warning p-2">
              <table class="table table-warning m-0">
                <thead>
                  <tr>
                    <th>Year</th>
                    <th>Total Quantity</th>
                    <th>Average Per Day</th>
                    <th>Groundhog Day Quantity</th>
                  </tr>
                </thead>
                <tbody id="tbody">
                  ${(() => {
                    return data
                      .map((d) => {
                        return `
                        <tr>
                          <td>${d[0]}</td>
                          <td>${d[1]}</td>
                          <td>${d[2]}</td>
                          <td>${d[3]}</td>
                        </tr>
                        `;
                      })
                      .join("");
                  })()}
                </tbody>
              </table>
            </div>
          `;

        let container = document.getElementById("main-container");
        container.appendChild(div);
      }
    });
});
