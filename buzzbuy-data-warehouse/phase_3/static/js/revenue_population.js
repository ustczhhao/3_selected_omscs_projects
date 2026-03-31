document.addEventListener("DOMContentLoaded", function () {
  fetch("/get_flash_messages")
    .then((response) => response.json())
    .then((messages) => {
      if (messages.length > 0) {
        let data = messages[0][1].data;
        let div = document.createElement("div");

        div.innerHTML = `
            <div class="mt-3 rounded-2 overflow-hidden border border-warning p-2">
              <table class="table table-warning m-0" style="width: 100%;">
                <thead>
                  <tr style=border-bottom-width:2px>
                    <th>City Size</th>
                    <th>Year</th>
                    <th>Total Revenue</th>
                  </tr>
                </thead>
                <tbody id="tbody">
                  ${(() => {
                    let rows = [];
                    ["Small", "Medium", "Large", "Extra Large"].forEach(
                      (size, i) => {
                        data[size].forEach((d, i) => {
                          let formattedRevenue = d["total_revenue"].toLocaleString('en-US');
                          rows.push(`
                        <tr style=border-bottom-width:${i == 2 ? "2px" : "1px"}>
                          <td>${size}</td>
                          <td>${d["year"]}</td>
                          <td>$${formattedRevenue}</td>
                        </tr>
                        `);
                        });
                      }
                    );
                    return rows.join("");
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
