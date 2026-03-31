// document.addEventListener("DOMContentLoaded", function () {
//     const manufactureName = document.getElementById("manufacture-name").textContent;
//     fetch(`/manufacture/${manufactureName}/details`)
//         .then((response) => response.json())
//         .then((data) => {
//             const summary = document.getElementById("summary");
//             summary.innerHTML = `
//                 <p>Total Products: ${data.count}</p>
//                 <p>Average Price: ${data.avg_price}</p>
//                 <p>Min Price: ${data.min_price}</p>
//                 <p>Max Price: ${data.max_price}</p>
//             `;

//             const productTable = document.getElementById("product-table");
//             productTable.innerHTML = data.products.map((product) => `
//                 <tr>
//                     <td>${product.PID}</td>
//                     <td>${product.pname}</td>
//                     <td>${product.categories}</td>
//                     <td>${product.retail_price}</td>
//                 </tr>
//             `).join("");
//         });
// });
