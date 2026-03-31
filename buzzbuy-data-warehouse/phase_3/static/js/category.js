// document.addEventListener("DOMContentLoaded", function () {
//   fetch("/get_flash_messages")
//     .then((response) => response.json())
//     .then((messages) => {
//       if (messages.length > 0) {
//         let data = messages[0][1].data;

//         let div = document.createElement("div");
//         div.innerHTML = `
//           <div class="mt-3 rounded-2 overflow-hidden border border-warning p-2">
//             <table class="table table-warning m-0">
//               <thead>
//                 <tr>
//                   <th>Category</th>
//                   <th>Total Products</th>
//                   <th>Total Manufactures</th>
//                   <th>Average Price</th>
//                 </tr>
//               </thead>
//               <tbody id="tbody">
//                 ${(() => {
//                   return data
//                     .map((d) => {
//                       console.log(d);
//                       return `
//                       <tr>
//                         <td>${d["CategoryName"]}</td>
//                         <td>${d["Product_Number"]}</td>
//                         <td>${d["Manufacturer_Number"]}</td>
//                         <td>${d["AverageRetailPrice"]}</td>
//                       </tr>
//                       `;
//                     })
//                     .join("");
//                 })()}
//               </tbody>
//             </table>
//           </div>
//         `;

//         let container = document.getElementById("main-container");
//         container.appendChild(div);
//       }
//     });
// });
