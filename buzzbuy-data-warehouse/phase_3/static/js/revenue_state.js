// document.addEventListener("DOMContentLoaded", function () {
//     fetch("/revenue_state_data")
//         .then((response) => response.json())
//         .then((results) => {
//             let container = document.getElementById("revenue-container");
//             container.innerHTML = "";
//             if (results.length > 0) {
//                 let div = document.createElement("div");
//                 div.innerHTML = `
//                     <div class="mt-3 rounded-2 overflow-hidden border border-info p-2">
//                         <table class="table table-info m-0">
//                             <thead>
//                                 <tr>
//                                     <th>Store Number</th>
//                                     <th>City</th>
//                                     <th>Year</th>
//                                     <th>Total Revenue</th>
//                                 </tr>
//                             </thead>
//                             <tbody id="tbody">
//                                 ${(() => {
//                                     return results
//                                         .map((result) => {
//                                             return `
//                                             <tr>
//                                                 <td>${result.store_number}</td>
//                                                 <td>${result.city_name}</td>
//                                                 <td>${result.year}</td>
//                                                 <td>${result.total_revenue}</td>
//                                             </tr>
//                                             `;
//                                         })
//                                         .join("");
//                                 })()}
//                             </tbody>
//                         </table>
//                     </div>
//                 `;
//                 container.appendChild(div);
//             } else {
//                 let message = document.createElement("p");
//                 message.textContent = "No Information available";
//                 container.appendChild(message);
//             }
//         });
// });
