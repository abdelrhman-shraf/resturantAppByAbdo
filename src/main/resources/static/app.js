const CONFIG = {
  // If this frontend is copied into Spring Boot's src/main/resources/static,
  // leave this as "" to use the same origin.
  // If you run the frontend separately, use "http://localhost:8080".
  API_BASE: ""
};

const state = {
  page: 0,
  size: 6,
  categoryId: null,
  search: "",
  sort: "desc",
  menu: [],
  categories: [],
  cart: JSON.parse(localStorage.getItem("restaurantCart") || "[]"),
  customerId: localStorage.getItem("restaurantCustomerId") || "",
  orders: [],
  adminOrders: []
};

const $ = (id) => document.getElementById(id);
const api = (path) => `${CONFIG.API_BASE}${path}`;

function showToast(message, type = "success") {
  const el = document.createElement("div");
  el.className = `toast ${type}`;
  el.textContent = message;
  $("toast-root").appendChild(el);
  setTimeout(() => el.remove(), 3500);
}

async function request(path, options = {}) {
  const response = await fetch(api(path), {
    headers: { "Content-Type": "application/json", ...(options.headers || {}) },
    ...options
  });
  const text = await response.text();
  let data = null;
  try { data = text ? JSON.parse(text) : null; } catch { data = text; }
  if (!response.ok) {
    const message = typeof data === "string" ? data : (data?.message || `Request failed (${response.status})`);
    throw new Error(message);
  }
  return data;
}

function money(value) {
  const n = Number(value || 0);
  return `EGP ${n.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
}

function escapeHtml(value) {
  return String(value ?? "").replace(/[&<>"']/g, (c) => ({
    "&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#039;"
  }[c]));
}

function saveCart() {
  localStorage.setItem("restaurantCart", JSON.stringify(state.cart));
  renderCartCount();
}

function renderCartCount() {
  $("cart-count").textContent = state.cart.reduce((sum, item) => sum + item.quantity, 0);
}

function switchView(name) {
  document.querySelectorAll(".view").forEach(v => v.classList.remove("active"));
  document.querySelectorAll(".nav-link").forEach(v => v.classList.remove("active"));
  $(`view-${name}`).classList.add("active");
  document.querySelector(`.nav-link[data-view="${name}"]`)?.classList.add("active");
  window.scrollTo({ top: 0, behavior: "smooth" });

  if (name === "orders") loadOrders();
  if (name === "admin") loadDashboard();
}

async function loadCategories() {
  state.categories = await request("/category/getall");
  renderCategoryPills();
  renderCategorySelect();
}

function renderCategoryPills() {
  $("category-pills").innerHTML =
    `<button class="category-pill ${state.categoryId === null ? "active" : ""}" data-category="">All</button>` +
    state.categories.map(c =>
      `<button class="category-pill ${Number(state.categoryId) === Number(c.categoryId) ? "active" : ""}" data-category="${c.categoryId}">${escapeHtml(c.categoryName)}</button>`
    ).join("");

  document.querySelectorAll(".category-pill").forEach(btn => {
    btn.addEventListener("click", () => {
      state.categoryId = btn.dataset.category ? Number(btn.dataset.category) : null;
      state.page = 0;
      loadMenu();
    });
  });
}

function renderCategorySelect() {
  $("item-category").innerHTML =
    `<option value="">No category</option>` +
    state.categories.map(c => `<option value="${c.categoryId}">${escapeHtml(c.categoryName)}</option>`).join("");
}

async function loadMenu() {
  $("menu-grid").innerHTML = `<div class="loading">Loading the menu…</div>`;
  try {
    const params = new URLSearchParams({
      page: state.page,
      size: state.size
    });
    const data = await request(`/menuitem/getall?${params}`);
    // getall is paginated. When search/filter controls are used, use the backend
    // search endpoint because it supports category, price, name and sort.
    if (state.search || state.categoryId !== null) {
      const searchParams = new URLSearchParams({ sortStrategy: state.sort });
      if (state.search) searchParams.set("name", state.search);
      if (state.categoryId !== null) searchParams.set("categoryId", state.categoryId);
      state.menu = await request(`/menuitem/search?${searchParams}`);
      renderMenu(state.menu);
      $("menu-pagination").innerHTML = "";
      return;
    }
    state.menu = data?.content || [];
    renderMenu(state.menu);
    renderPagination(data);
  } catch (error) {
    $("menu-grid").innerHTML = `<div class="empty">Could not load the menu.<br><small>${escapeHtml(error.message)}</small></div>`;
    showToast(error.message, "error");
  }
}

function renderMenu(items) {
  if (!items.length) {
    $("menu-grid").innerHTML = `<div class="empty">No dishes match your search.</div>`;
    return;
  }
  $("menu-grid").innerHTML = items.map((item, index) => {
    const color = ["#e85c3a","#7e9a53","#d79c4d","#bd5b43","#6e8fa6"][index % 5];
    return `
      <article class="food-card ${item.available ? "" : "disabled-card"}">
        <div class="food-image" style="--food-color:${color}">
          <span class="available-dot">${item.available ? "Available" : "Unavailable"}</span>
        </div>
        <div class="food-card-body">
          <h3>${escapeHtml(item.itemName)}</h3>
          <p>${escapeHtml(item.description || "A delicious choice from our kitchen.")}</p>
          <div class="food-meta">
            <span class="price">${money(item.price)}</span>
            <button class="add-button" ${item.available ? "" : "disabled"} data-add="${item.itemId}" aria-label="Add ${escapeHtml(item.itemName)}">+</button>
          </div>
        </div>
      </article>`;
  }).join("");

  document.querySelectorAll("[data-add]").forEach(btn => {
    btn.addEventListener("click", () => addToCart(Number(btn.dataset.add)));
  });
}

function renderPagination(data) {
  const totalPages = Number(data?.totalPages || 0);
  if (totalPages <= 1) { $("menu-pagination").innerHTML = ""; return; }
  let html = "";
  for (let i = 0; i < totalPages; i++) {
    html += `<button class="page-button ${i === state.page ? "active" : ""}" data-page="${i}">${i + 1}</button>`;
  }
  $("menu-pagination").innerHTML = html;
  document.querySelectorAll("[data-page]").forEach(btn => btn.addEventListener("click", () => {
    state.page = Number(btn.dataset.page);
    loadMenu();
  }));
}

function addToCart(itemId) {
  const item = state.menu.find(x => Number(x.itemId) === itemId);
  if (!item) return;
  const existing = state.cart.find(x => Number(x.itemId) === itemId);
  if (existing) existing.quantity += 1;
  else state.cart.push({ itemId: item.itemId, itemName: item.itemName, price: Number(item.price), quantity: 1 });
  saveCart();
  renderCart();
  $("cart-drawer").classList.remove("hidden");
  showToast(`${item.itemName} added to cart`);
}

function changeQuantity(itemId, delta) {
  const item = state.cart.find(x => Number(x.itemId) === Number(itemId));
  if (!item) return;
  item.quantity += delta;
  if (item.quantity <= 0) state.cart = state.cart.filter(x => Number(x.itemId) !== Number(itemId));
  saveCart();
  renderCart();
}

function renderCart() {
  if (!state.cart.length) {
    $("cart-items").innerHTML = `<div class="empty">Your cart is empty.<br>Add something delicious from the menu.</div>`;
  } else {
    $("cart-items").innerHTML = state.cart.map(item => `
      <div class="cart-row">
        <div><h4>${escapeHtml(item.itemName)}</h4><small>${money(item.price)} each</small></div>
        <div class="qty-controls">
          <button data-qty="-1" data-id="${item.itemId}">−</button>
          <strong>${item.quantity}</strong>
          <button data-qty="1" data-id="${item.itemId}">+</button>
        </div>
      </div>`).join("");
  }
  const total = state.cart.reduce((sum, item) => sum + item.price * item.quantity, 0);
  $("cart-total").textContent = money(total);
  $("checkout-customer").value = state.customerId;
  document.querySelectorAll("[data-qty]").forEach(btn => btn.addEventListener("click", () => changeQuantity(btn.dataset.id, Number(btn.dataset.qty))));
}

async function checkout(event) {
  event.preventDefault();
  if (!state.cart.length) return showToast("Your cart is empty.", "error");
  const customerId = Number($("checkout-customer").value);
  if (!customerId) return showToast("Enter a valid customer ID.", "error");

  const payload = {
    customerId,
    paymentMethod: $("checkout-payment").value,
    orderedItems: state.cart.map(item => ({ itemId: item.itemId, quantity: item.quantity }))
  };

  try {
    const order = await request("/orders/makeOrder", {
      method: "POST",
      body: JSON.stringify(payload)
    });
    state.cart = [];
    state.customerId = String(customerId);
    localStorage.setItem("restaurantCustomerId", state.customerId);
    saveCart();
    renderCart();
    $("cart-drawer").classList.add("hidden");
    showToast(`Order #${order?.orderId ?? ""} created successfully.`);
    switchView("orders");
  } catch (error) {
    showToast(error.message, "error");
  }
}

async function loadOrders() {
  const customerId = Number(state.customerId);
  if (!customerId) {
    $("orders-content").innerHTML = `<div class="empty">No customer ID saved yet.<br>Register a customer in Profile, then come back here.</div>`;
    return;
  }
  $("orders-content").innerHTML = `<div class="loading">Loading orders…</div>`;
  try {
    const data = await request(`/orders/customer/history?customerId=${customerId}&page=0&size=20`);
    state.orders = data?.content || [];
    renderOrders();
  } catch (error) {
    $("orders-content").innerHTML = `<div class="empty">Could not load orders.<br><small>${escapeHtml(error.message)}</small></div>`;
  }
}

function renderOrders() {
  if (!state.orders.length) {
    $("orders-content").innerHTML = `<div class="empty">No orders yet.</div>`;
    return;
  }
  $("orders-content").innerHTML = state.orders.map(order => `
    <div class="order-card">
      <div>
        <div class="order-id">Order #${escapeHtml(order.orderId)}</div>
        <div class="order-sub">${escapeHtml(order.orderDatetime || order.dateTime || "Recent order")} · ${money(order.totalAmount)}</div>
      </div>
      <span class="status status-${escapeHtml(order.status)}">${escapeHtml(order.status)}</span>
      <div class="order-actions">
        <button class="text-button" data-view-order="${order.orderId}">Details</button>
        ${["PENDING","CONFIRMED","PREPARING"].includes(order.status) ? `<button class="text-button" data-cancel-order="${order.orderId}">Cancel</button>` : ""}
      </div>
    </div>`).join("");

  document.querySelectorAll("[data-view-order]").forEach(btn => btn.addEventListener("click", () => openOrderDetails(Number(btn.dataset.viewOrder))));
  document.querySelectorAll("[data-cancel-order]").forEach(btn => btn.addEventListener("click", () => cancelOrder(Number(btn.dataset.cancelOrder))));
}

async function cancelOrder(id) {
  if (!confirm(`Cancel order #${id}?`)) return;
  try {
    await request(`/orders/cancel?id=${id}`, { method: "PUT" });
    showToast("Order cancelled.");
    loadOrders();
  } catch (error) { showToast(error.message, "error"); }
}

async function openOrderDetails(id) {

  // Open the modal immediately
  $("order-modal").classList.remove("hidden");

  // Show loading while the second API request is running
  $("order-detail-content").innerHTML = `
    <div class="loading">
      Loading order details…
    </div>
  `;

  try {

    // This is the SECOND API call.
    // The id comes from the order the user clicked.
    const order = await request(
      `/orders/get_order_by_id?id=${id}`
    );

    /*
      Backend returns:

      {
        orderId: 8,
        dateTime: "...",
        status: "DELIVERED",
        paymentMethod: "CARD",
        totalAmount: 300,
        customer: {
          customerId: 1,
          firstName: "...",
          lastName: "...",
          phone: "..."
        },
        items: [
          {
            menuItemId: 3,
            name: "Burger",
            quantity: 1,
            unitPrice: 60,
            subtotal: 60
          }
        ]
      }
    */

    const customer = order.customer;
    const items = order.items || [];

    $("order-detail-content").innerHTML = `

      <!-- ORDER INFORMATION -->
      <div class="detail-grid">

        <div class="detail-box">
          <span>Order</span>
          <strong>
            #${escapeHtml(order.orderId)}
          </strong>
        </div>

        <div class="detail-box">
          <span>Status</span>
          <strong>
            ${escapeHtml(order.status)}
          </strong>
        </div>

        <div class="detail-box">
          <span>Payment</span>
          <strong>
            ${escapeHtml(order.paymentMethod)}
          </strong>
        </div>

        <div class="detail-box">
          <span>Total</span>
          <strong>
            ${money(order.totalAmount)}
          </strong>
        </div>

      </div>


      <!-- CUSTOMER INFORMATION -->
      <div class="customer-details">

        <h3>Customer</h3>

        ${
          customer
            ? `
              <div class="detail-grid">

                <div class="detail-box">
                  <span>Name</span>
                  <strong>
                    ${escapeHtml(customer.firstName)}
                    ${escapeHtml(customer.lastName)}
                  </strong>
                </div>

                <div class="detail-box">
                  <span>Customer ID</span>
                  <strong>
                    #${escapeHtml(customer.customerId)}
                  </strong>
                </div>

                <div class="detail-box">
                  <span>Phone</span>
                  <strong>
                    ${escapeHtml(customer.phone)}
                  </strong>
                </div>

              </div>
            `
            : `
              <div class="empty">
                No customer information available.
              </div>
            `
        }

      </div>


      <!-- ORDERED ITEMS -->
      <div class="ordered-items">

        <h3>Ordered items</h3>

        <div class="detail-items">

          ${
            items.length
              ? items.map(item => `

                  <div class="detail-item">

                    <div>
                      <strong>
                        ${escapeHtml(item.name)}
                      </strong>

                      <div class="item-meta">
                        ${money(item.unitPrice)}
                        ×
                        ${escapeHtml(item.quantity)}
                      </div>
                    </div>

                    <strong>
                      ${money(item.subtotal)}
                    </strong>

                  </div>

                `).join("")
              : `
                <div class="empty">
                  No items found.
                </div>
              `
          }

        </div>

      </div>

    `;

  } catch (error) {

    $("order-detail-content").innerHTML = `
      <div class="empty">
        Could not load order details.
        <br>
        <small>${escapeHtml(error.message)}</small>
      </div>
    `;

  }
}
async function saveProfile(event) {
  event.preventDefault();
  const id = Number($("profile-id").value);
  const payload = {
    firstName: $("profile-first").value.trim(),
    lastName: $("profile-last").value.trim(),
    phone: $("profile-phone").value.trim(),
    email: $("profile-email").value.trim(),
    address: $("profile-address").value.trim()
  };
  try {
    const result = id
      ? await request(`/customers/updateProfile?id=${id}`, { method:"PUT", body:JSON.stringify(payload) })
      : await request("/customers/register", { method:"POST", body:JSON.stringify(payload) });
    const customerId = result?.customerId ?? result?.id ?? id;
    if (customerId) {
      state.customerId = String(customerId);
      localStorage.setItem("restaurantCustomerId", state.customerId);
      $("profile-id").value = customerId;
      $("checkout-customer").value = customerId;
    }
    showToast("Profile saved successfully.");
  } catch (error) { showToast(error.message, "error"); }
}

async function loadProfile() {
  const id = Number($("profile-id").value);
  if (!id) return showToast("Enter a customer ID.", "error");
  try {
    const customer = await request(`/customers/getbyid?id=${id}`);
    $("profile-first").value = customer.firstName || "";
    $("profile-last").value = customer.lastName || "";
    $("profile-phone").value = customer.phone || "";
    $("profile-email").value = customer.email || "";
    $("profile-address").value = customer.address || "";
    state.customerId = String(id);
    localStorage.setItem("restaurantCustomerId", state.customerId);
    showToast("Customer loaded.");
  } catch (error) { showToast(error.message, "error"); }
}

async function loadDashboard() {
  $("stats-grid").innerHTML = `<div class="loading">Loading dashboard…</div>`;
  try {
    const [weekRevenue, orders, topSelling, loyal] = await Promise.all([
      request("/orders/revenue?duration=week").catch(() => 0),
      request("/orders/getall?page=0&size=6"),
      request("/orders/get_top_selling?orderBy=TIMESORDERD").catch(() => null),
      request("/orders/loyal?limit=5&sort=orders").catch(() => [])
    ]);

    const orderContent = orders?.content || [];
    state.adminOrders = orderContent;
    $("stats-grid").innerHTML = `
      <div class="stat-card"><span>Weekly revenue</span><strong>${money(weekRevenue)}</strong></div>
      <div class="stat-card"><span>Total orders</span><strong>${orders?.totalElements ?? "—"}</strong></div>
      <div class="stat-card"><span>Menu items</span><strong>${state.menu.length || "—"}</strong></div>
      <div class="stat-card"><span>Categories</span><strong>${state.categories.length || "—"}</strong></div>`;
    renderAdminMenu();
    renderAdminOrders();
    renderAdminCategories();
    renderAnalytics(topSelling, loyal);
  } catch (error) {
    $("stats-grid").innerHTML = `<div class="empty">Dashboard data could not be loaded.<br><small>${escapeHtml(error.message)}</small></div>`;
  }
}

function renderAdminMenu() {
  if (!state.menu.length) {
    $("admin-menu").innerHTML = `<div class="empty">No menu items loaded.</div>`;
    return;
  }
  $("admin-menu").innerHTML = state.menu.map(item => `
    <div class="admin-row">
      <div><strong>${escapeHtml(item.itemName)}</strong><small>${money(item.price)} · ${item.available ? "Available" : "Unavailable"}</small></div>
      <div class="row-actions">
        <button class="row-action" data-edit-item="${item.itemId}">Edit</button>
        <button class="row-action danger" data-delete-item="${item.itemId}">Delete</button>
      </div>
    </div>`).join("");
  document.querySelectorAll("[data-edit-item]").forEach(btn => btn.addEventListener("click", () => openItemModal(Number(btn.dataset.editItem))));
  document.querySelectorAll("[data-delete-item]").forEach(btn => btn.addEventListener("click", () => deleteItem(Number(btn.dataset.deleteItem))));
}

function renderAdminOrders() {
  if (!state.adminOrders.length) {
    $("admin-orders").innerHTML = `<div class="empty">No orders found.</div>`;
    return;
  }
  $("admin-orders").innerHTML = state.adminOrders.map(order => `
    <div class="admin-row">
      <div><strong>Order #${escapeHtml(order.orderId)}</strong><small>${money(order.totalAmount)} · ${escapeHtml(order.status)}</small></div>
      <div class="row-actions">
        <button class="row-action" data-admin-details="${order.orderId}">View</button>
        <select class="row-action" data-status-id="${order.orderId}" aria-label="Change status">
          ${["PENDING","CONFIRMED","PREPARING","READY","DELIVERED","CANCELLED"].map(s => `<option ${s === order.status ? "selected" : ""}>${s}</option>`).join("")}
        </select>
      </div>
    </div>`).join("");
  document.querySelectorAll("[data-admin-details]").forEach(btn => btn.addEventListener("click", () => openOrderDetails(Number(btn.dataset.adminDetails))));
  document.querySelectorAll("[data-status-id]").forEach(sel => sel.addEventListener("change", () => changeOrderStatus(Number(sel.dataset.statusId), sel.value)));
}

function renderAdminCategories() {
  $("admin-categories").innerHTML = state.categories.map(c => `
    <div class="admin-row">
      <div><strong>${escapeHtml(c.categoryName)}</strong><small>${escapeHtml(c.description || "")}</small></div>
      <div class="row-actions">
        <button class="row-action" data-rename-category="${c.categoryId}">Rename</button>
        <button class="row-action danger" data-delete-category="${c.categoryId}">Delete</button>
      </div>
    </div>`).join("");
  document.querySelectorAll("[data-rename-category]").forEach(btn => btn.addEventListener("click", () => renameCategory(Number(btn.dataset.renameCategory))));
  document.querySelectorAll("[data-delete-category]").forEach(btn => btn.addEventListener("click", () => deleteCategory(Number(btn.dataset.deleteCategory))));
}

function renderAnalytics(topSelling, loyal) {
  const top = topSelling?.content || topSelling || [];
  $("analytics").innerHTML = `
    <div><h4>Top selling items</h4>${top.length ? top.slice(0,5).map((x,i) =>
      `<div class="analytics-item"><span>${i+1}. ${escapeHtml(x.itemName || x.name || `Item #${x.itemId}`)}</span><strong>${escapeHtml(x.timesOrdered ?? x.quantity ?? x.orders ?? "")}</strong></div>`
    ).join("") : `<small>No top-selling data.</small>`}</div>
    <div><h4>Loyal customers</h4>${(loyal || []).length ? loyal.map(x =>
      `<div class="analytics-item"><span>${escapeHtml(x.firstName || x.name || "Customer")}</span><strong>${escapeHtml(x.orders ?? x.orderCount ?? "")}</strong></div>`
    ).join("") : `<small>No loyal-customer data.</small>`}</div>`;
}

async function changeOrderStatus(id, status) {
  try {
    await request(`/orders/changestatus?id=${id}&statusEnum=${encodeURIComponent(status)}`, { method:"PUT" });
    showToast(`Order #${id} is now ${status}.`);
    loadDashboard();
  } catch (error) { showToast(error.message, "error"); loadDashboard(); }
}

function openItemModal(id = null) {
  $("item-modal").classList.remove("hidden");
  $("item-form").reset();
  $("item-id").value = "";
  $("item-available").checked = true;
  $("item-modal-title").textContent = id ? "Edit menu item" : "New menu item";
  if (id) {
    const item = state.menu.find(x => Number(x.itemId) === id);
    if (!item) return;
    $("item-id").value = id;
    $("item-name").value = item.itemName || "";
    $("item-description").value = item.description || "";
    $("item-price").value = item.price ?? "";
    $("item-category").value = item.categoryId ?? "";
    $("item-available").checked = Boolean(item.available);
  }
}

async function saveItem(event) {
  event.preventDefault();
  const id = Number($("item-id").value);
  const payload = {
    itemName: $("item-name").value.trim(),
    description: $("item-description").value.trim(),
    price: Number($("item-price").value),
    available: $("item-available").checked,
    categoryId: $("item-category").value ? Number($("item-category").value) : null
  };
  try {
    if (id) await request(`/menuitem/update?id=${id}`, { method:"PUT", body:JSON.stringify(payload) });
    else await request("/menuitem/addmenuitem", { method:"POST", body:JSON.stringify(payload) });
    $("item-modal").classList.add("hidden");
    showToast("Menu item saved.");
    await loadMenu();
    renderAdminMenu();
  } catch (error) { showToast(error.message, "error"); }
}

async function deleteItem(id) {
  if (!confirm(`Delete menu item #${id}?`)) return;
  try {
    await request(`/menuitem/delete?id=${id}`, { method:"DELETE" });
    showToast("Menu item deleted.");
    loadMenu();
    loadDashboard();
  } catch (error) { showToast(error.message, "error"); }
}

async function renameCategory(id) {
  const current = state.categories.find(c => Number(c.categoryId) === id);
  const name = prompt("New category name:", current?.categoryName || "");
  if (!name?.trim()) return;
  try {
    await request(`/category/changename?id=${id}&name=${encodeURIComponent(name.trim())}`, { method:"PUT" });
    showToast("Category renamed.");
    await loadCategories();
    renderAdminCategories();
  } catch (error) { showToast(error.message, "error"); }
}

async function deleteCategory(id) {
  if (!confirm("Delete this category? The backend may reject the operation if menu items still use it.")) return;
  try {
    await request(`/category/delete?id=${id}`, { method:"DELETE" });
    showToast("Category deleted.");
    await loadCategories();
    loadMenu();
  } catch (error) { showToast(error.message, "error"); }
}

async function createCategory(event) {
  event.preventDefault();
  const payload = {
    categoryName: $("category-name").value.trim(),
    description: $("category-description").value.trim()
  };
  try {
    await request("/category/create", { method:"POST", body:JSON.stringify(payload) });
    $("category-form").reset();
    showToast("Category created.");
    await loadCategories();
    renderAdminCategories();
  } catch (error) { showToast(error.message, "error"); }
}

function bindEvents() {
  document.querySelectorAll(".nav-link").forEach(btn => btn.addEventListener("click", () => switchView(btn.dataset.view)));
  $("cart-button").addEventListener("click", () => { renderCart(); $("cart-drawer").classList.remove("hidden"); });
  $("close-cart").addEventListener("click", () => $("cart-drawer").classList.add("hidden"));
  $("cart-drawer").addEventListener("click", e => { if (e.target === $("cart-drawer")) $("cart-drawer").classList.add("hidden"); });
  $("checkout-form").addEventListener("submit", checkout);
  $("checkout-customer").addEventListener("change", e => {
    state.customerId = e.target.value;
    localStorage.setItem("restaurantCustomerId", state.customerId);
  });
  $("menu-search").addEventListener("input", e => {
    state.search = e.target.value.trim();
    state.page = 0;
    clearTimeout(window.searchTimer);
    window.searchTimer = setTimeout(loadMenu, 300);
  });
  $("sort-select").addEventListener("change", e => { state.sort = e.target.value; state.page = 0; loadMenu(); });
  $("refresh-orders").addEventListener("click", loadOrders);
  $("profile-form").addEventListener("submit", saveProfile);
  $("load-profile").addEventListener("click", loadProfile);
  $("refresh-dashboard").addEventListener("click", loadDashboard);
  $("refresh-admin-orders").addEventListener("click", loadDashboard);
  $("new-item").addEventListener("click", () => openItemModal());
  $("item-form").addEventListener("submit", saveItem);
  $("close-item-modal").addEventListener("click", () => $("item-modal").classList.add("hidden"));
  $("close-order-modal").addEventListener("click", () => $("order-modal").classList.add("hidden"));
  $("category-form").addEventListener("submit", createCategory);
  window.addEventListener("keydown", e => {
    if (e.key === "Escape") {
      $("cart-drawer").classList.add("hidden");
      $("item-modal").classList.add("hidden");
      $("order-modal").classList.add("hidden");
    }
  });
}

async function init() {
  bindEvents();
  renderCartCount();
  renderCart();
  $("profile-id").value = state.customerId;
  $("checkout-customer").value = state.customerId;
  try {
    await loadCategories();
    await loadMenu();
  } catch (error) {
    showToast(error.message, "error");
  }
}

init();
