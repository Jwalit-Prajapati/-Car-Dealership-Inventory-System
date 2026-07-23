# Frontend Build Plan — Vehicle Inventory System

A step-by-step plan for building the React + Tailwind SPA against the existing Spring Boot backend. Written so each phase can be handed to a fresh conversation as `frontend-plan.md → do Phase N`. Check off phases as they're completed.

- [x] Phase 0 — Backend prerequisite fixes (**DONE**)
- [x] Phase 1 — Frontend foundation
- [x] Phase 2 — Auth (register/login)
- [x] Phase 3 — Vehicle dashboard (browse/search)
- [x] Phase 4 — Purchase flow
- [x] Phase 5 — Admin vehicle management
- [x] Phase 6 — Admin stats dashboard
- [x] Phase 7 — Polish & final QA

---

## Ground rules for every phase

- **Stack, strictly**: React (Vite), Tailwind CSS, `react-router-dom` for routing, native `fetch` for HTTP. No Redux, no axios, no UI kit, no form libraries — hand-rolled with React state/Context.
- **No new dependencies** beyond `react-router-dom` (already decided) without asking first.
- After finishing a phase, run it in the browser and verify the phase's checkpoint before moving to the next phase.
- Keep components in the folder structure below — don't invent a different structure mid-way.

---

## Backend API reference (verified against source, do not re-derive)

Base URL: `http://localhost:8080` (`frontend/.env` → `VITE_API_BASE_URL`)

| Method | Path | Auth | Body / Params | Response |
|---|---|---|---|---|
| POST | `/api/auth/register` | public | `{firstName,lastName,email,password}` | 201, no body |
| POST | `/api/auth/login` | public | `{email,password}` | `{token, role, fullName}` |
| GET | `/api/vehicles` | public | `?page&size&sort` | `Page<VehicleResponseDTO>` |
| GET | `/api/vehicles/search` | public | `?make&model&category&minPrice&maxPrice&page&size` | `Page<VehicleResponseDTO>` |
| POST | `/api/vehicles` | ADMIN | `{make,model,category,price,quantity}` | 201 `VehicleResponseDTO` |
| PUT | `/api/vehicles/{id}` | ADMIN | same as above | 200 `VehicleResponseDTO` |
| DELETE | `/api/vehicles/{id}` | ADMIN | — | 204 |
| POST | `/api/vehicles/{id}/restock` | ADMIN | `{quantity}` (>0) | 204 |
| POST | `/api/purchases` | authenticated (any role) | `{vehicleId, quantity}` | 201 `PurchaseResponseDTO` |
| GET | `/api/admin/stats/inventory` | ADMIN | — | `{totalVehicles, lowStockVehicles, outOfStockVehicles}` |
| GET | `/api/admin/stats/sales` | ADMIN | — | `{totalRevenue, totalSalesCount, mostPurchasedVehicle}` |
| GET | `/api/health` | public | — | health status |

**Shapes to know:**
- `VehicleResponseDTO`: `{id, make, model, category, price, quantity}`
- `Page<T>` (Spring Data): `{content: T[], totalElements, totalPages, number, size}` — always read `.content`, never the response root.
- `PurchaseResponseDTO`: `{purchaseId, vehicleId, vehicleName, quantityPurchased, purchasedPrice, purchaseDate, customerName}`
- Error responses (`ApiResponse` wrapper from `GlobalExceptionHandler`): `{success:false, status, message, data:null, timestamp}`. Validation failures (`@Valid` on the backend) always return the generic message `"Validation failed"` — **the backend does not return field-level errors**, so all field-specific validation messages must be done client-side, mirrored from the DTO constraints in the table below.
- Auth header: `Authorization: Bearer <jwt>`. Public without a token: `/api/auth/**`, GET `/api/vehicles`, GET `/api/vehicles/search`. Everything else needs a token; admin routes need role `ADMIN`.

**Client-side validation to mirror (since backend won't send field errors):**
- Register: firstName/lastName not blank, email valid format, password ≥ 8 chars.
- Login: email/password not blank.
- Vehicle form: make/model not blank, category optional, price > 0, quantity ≥ 0 (integer).
- Restock: quantity > 0 (integer).
- Purchase: quantity > 0 and ≤ vehicle's current stock (client-enforced; server also enforces via `InsufficientStockException`).

---

## Phase 0 — Backend prerequisite fixes ✅ DONE

Already completed and verified (`mvn test`: 136/136 passing):
1. `SecurityConfig.java` — added `CorsConfigurationSource` bean allowing origin `http://localhost:5173`, methods GET/POST/PUT/DELETE/OPTIONS, headers `Authorization`/`Content-Type`, wired via `.cors(...)`.
2. `LoginResponse` changed from `{token}` to `{token, role, fullName}`; `AuthService.login` now reads the authenticated `CustomUserDetails`'s `User` to populate role/name and embeds `role` as a JWT claim (`TokenService`/`JwtService` gained a `generateToken(Map, String)` interface method — already existed on the impl).
3. Fixed an unrelated pre-existing `pom.xml` break: Testcontainers 2.x renamed `org.testcontainers:junit-jupiter`→`testcontainers-junit-jupiter` and `postgresql`→`testcontainers-postgresql`; pinned both to `${testcontainers.version}`.
4. Updated `AuthServiceTest` and `AuthControllerTest` for the new `LoginResponse` shape.

Nothing left to do here — a fresh conversation can start straight at Phase 1.

---

## Phase 1 — Frontend foundation

**Goal:** Tailwind + routing + API/auth plumbing in place, app boots to an (unstyled-content-wise-simple) Login page.

Tasks:
1. `npm install tailwindcss @tailwindcss/vite react-router-dom` in `frontend/`.
2. `vite.config.js`: add `tailwindcss()` to `plugins`.
3. `src/index.css`: replace template CSS with `@import "tailwindcss";` (+ any `@theme` tokens for the color palette decided in Phase 7, or defaults for now).
4. Delete/ignore leftover Vite template cruft in `App.jsx`/`App.css`/`src/assets/*` (react.svg, vite.svg, hero.png) — not used by the real app.
5. `.env` / `.env.example` with `VITE_API_BASE_URL=http://localhost:8080`; add `.env` to `.gitignore` (keep `.env.example` tracked).
6. Folder structure under `frontend/src/`:
   ```
   api/
     client.js        # fetch wrapper: base URL, auth header injection, JSON parse, ApiError on non-2xx
     auth.js           # login(), register()
     vehicles.js       # list(), search(), create(), update(), remove(), restock()
     purchases.js      # purchase()
     stats.js          # inventoryStats(), salesStats()
   context/
     AuthContext.jsx   # { token, role, fullName }, persisted to localStorage, login()/logout(), isAdmin, isAuthenticated
   routes/
     ProtectedRoute.jsx  # redirect to /login if not authenticated
     AdminRoute.jsx       # redirect to / if not admin
   pages/
     LoginPage.jsx
     RegisterPage.jsx
     DashboardPage.jsx
     AdminVehiclesPage.jsx
     AdminStatsPage.jsx
   components/
     Navbar.jsx
     VehicleCard.jsx
     VehicleGrid.jsx
     SearchFilterBar.jsx
     PaginationControls.jsx
     PurchaseModal.jsx
     VehicleFormModal.jsx
     ConfirmDialog.jsx
     Toast.jsx / ToastProvider.jsx
     StatCard.jsx
   ```
7. `main.jsx`: wrap `<App />` in `<BrowserRouter>` and `<AuthProvider>`.
8. `App.jsx`: define `<Routes>` — `/login`, `/register`, `/` (dashboard, public), `/admin/vehicles` (AdminRoute), `/admin/stats` (AdminRoute). Render `<Navbar />` above the `<Routes>`.
9. `api/client.js` design:
   - Reads token via `localStorage.getItem('token')`, attaches `Authorization` header if present.
   - Handles `204 No Content` (return `null`).
   - Throws a custom `ApiError(message, status)` on non-2xx, extracting `message` from the `ApiResponse` error body (`data?.message`).
   - Small helper methods: `get`, `post`, `put`, `delete`, all accepting a path and optional body/params.
10. `AuthContext.jsx` design:
    - On mount, rehydrate `{token, role, fullName}` from `localStorage`.
    - `login({token, role, fullName})` — sets state + persists to `localStorage`.
    - `logout()` — clears state + `localStorage`.
    - Exposes `isAuthenticated = !!token`, `isAdmin = role === 'ADMIN'`.

**Checkpoint:** `npm run dev` boots, Tailwind utility classes render correctly, navigating to `/`, `/login`, `/register` works via the router (pages can be placeholder headings at this point), refreshing the page doesn't lose auth state (once Phase 2 wires up login).

---

## Phase 2 — Auth

Tasks:
1. `LoginPage`: email + password fields, client-side validation (not blank), calls `api/auth.login()`, on success calls `AuthContext.login()` and navigates to `/`. Server error (e.g. bad credentials → 401) shown inline/via toast.
2. `RegisterPage`: firstName/lastName/email/password fields with the validation rules listed above, on success navigate to `/login` with a success message (e.g. via a query param or router state). Duplicate-email error (400 "Email already in use") surfaced to the user.
3. `Navbar`: logged-out → Login/Register links; logged-in → welcome `{fullName}`, Logout button, and — only when `isAdmin` — links to `/admin/vehicles` and `/admin/stats`.
4. `Toast`/`ToastProvider`: minimal global toast (success/error variants), used for auth errors and later for purchase/CRUD feedback.

**Checkpoint:** Register a new user → land back on login → log in → see name + logout in navbar → refresh page → still logged in → log out → back to logged-out navbar.

---

## Phase 3 — Vehicle dashboard (browse/search)

Tasks:
1. `DashboardPage`: holds `{page, size, filters}` state; fetches `GET /api/vehicles` when no filters are active, `GET /api/vehicles/search` when filters are set; renders `VehicleGrid`.
2. `VehicleCard`: make/model/category, price formatted as currency, quantity badge (e.g. green "In stock", amber "Low stock" if <5, red "Out of stock" if 0 — reuses the same low-stock threshold the backend's `InventoryStatsService` uses), Purchase button (wired in Phase 4).
3. `SearchFilterBar`: make/model/category text inputs + min/max price number inputs, "Search" + "Clear filters" buttons — maps 1:1 to `VehicleSearchRequest`.
4. `PaginationControls`: prev/next + page numbers driven by `{number, totalPages, totalElements}` from the `Page` response.
5. Loading skeleton state and empty state ("No vehicles match your filters").

**Checkpoint:** Works logged-out. List renders and paginates. Filters narrow results and combine (e.g. make + price range together).

---

## Phase 4 — Purchase flow

Tasks:
1. `VehicleCard`'s Purchase button: `disabled` (greyed out, `cursor-not-allowed`) when `quantity === 0`. If the user isn't logged in, clicking redirects to `/login` instead of opening the modal.
2. `PurchaseModal`: quantity stepper capped at the vehicle's current stock, submit → `POST /api/purchases`, success → toast with `PurchaseResponseDTO` summary + update the vehicle's displayed quantity locally (avoid a full refetch), close modal.
3. Inline error handling for 4xx (e.g. stock changed since page load → `InsufficientStockException`).

**Checkpoint:** Purchasing reduces the shown stock immediately; button disables once a vehicle hits 0; purchasing while logged out redirects to login first.

---

## Phase 5 — Admin vehicle management

Tasks:
1. `AdminVehiclesPage` (wrapped in `AdminRoute`): reuse the dashboard's list+pagination (no search needed here, or reuse `SearchFilterBar` too — your call), plus per-row actions: Edit, Delete, Restock.
2. `VehicleFormModal`: shared Create/Edit form mapped to `VehicleRequestDTO` fields with the validation rules above; `POST /api/vehicles` for create, `PUT /api/vehicles/{id}` for edit.
3. `ConfirmDialog`: generic yes/no confirmation, used before `DELETE /api/vehicles/{id}`.
4. Inline restock control (small quantity input + button) calling `POST /api/vehicles/{id}/restock`.

**Checkpoint:** As an admin: create, edit, delete, and restock all work end-to-end and the list reflects changes. As a non-admin (or logged out): `/admin/vehicles` redirects away and the nav links aren't shown.

---

## Phase 6 — Admin stats dashboard

Tasks:
1. `AdminStatsPage`: fetch `/api/admin/stats/inventory` and `/api/admin/stats/sales` in parallel (`Promise.all`).
2. `StatCard` grid: Total Vehicles, Low Stock, Out of Stock, Total Revenue, Total Sales Count, plus a highlighted "Most Purchased Vehicle" card from `mostPurchasedVehicle`.

**Checkpoint:** Numbers reflect actual DB state — spot check by making a purchase/restock and confirming the stats update on refetch.

---

## Phase 7 — Polish & final QA

Tasks:
1. Responsive pass: mobile nav (hamburger or stacked links), grid breakpoints for `VehicleGrid`/`StatCard`s, forms usable on small screens.
2. Consistent visual system: pick a Tailwind color/spacing theme once (e.g. via `@theme` tokens in `index.css`) and apply it everywhere rather than ad hoc colors per component.
3. Loading spinners on every async action (list fetch, form submit, purchase, delete), disabled buttons while in flight.
4. Error boundaries / graceful fallback if the API is unreachable (e.g. banner "Can't reach the server").
5. Favicon + `<title>` per page (or a single descriptive title).
6. Full manual walkthrough of every flow in-browser: register → login → browse/search → purchase → logout → login as admin → CRUD → restock → stats. Fix anything that breaks.

---

## Verification commands

```bash
# backend (only needed if it's not already running)
cd backend && mvn spring-boot:run   # serves on :8080

# frontend
cd frontend && npm install && npm run dev   # serves on :5173, open in browser and click through
```

Since this is a visual SPA, "done" means clicking through it in a real browser for each phase's checkpoint — not just `npm run build` succeeding.
