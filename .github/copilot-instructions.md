# Copilot Instructions for logistics-management-system

## Big picture architecture
- This repo is split into two apps: `logistics-server` (Spring Boot + MyBatis) and `logistics-web` (Vue 3 + Vite + Element Plus).
- The frontend talks directly to backend endpoints using hardcoded `http://localhost:8080` URLs (no Vite proxy configured).
- Backend uses a **controller -> mapper -> DB** flow; there is currently no service layer.
- SQL is defined inline with MyBatis annotations in mapper interfaces, not XML files.
- Core entities map to DB tables: `User -> t_user`, `Order -> t_order`, `LogisticsTrack -> t_track`.

## Backend conventions (Spring Boot)
- Main app entry: `logistics-server/src/main/java/com/logistics/server/LogisticsServerApplication.java`.
- REST controllers return either:
  - `Map<String,Object>` with `code/msg/data`, or
  - raw `List<Order>` for list endpoints.
- Keep this mixed response style unless explicitly refactoring API contracts.
- Role model is string-based (`ADMIN`, `WAREHOUSE`, `COURIER`, `USER`) and checked in controllers/router guards.
- Status flow for orders is numeric: `0待揽件 -> 1已揽件 -> 2运输中 -> 3派送中 -> 4已签收`.
- `OrderController.updateOrderStatus()` must both update `t_order.status` and insert a track record in `t_track` (transactional behavior).
- Fee logic lives in `OrderController.createOrder()` and is mirrored in frontend preview:
  - `fee = 10 + weight*2 + max(distance-100,0)*0.5`.
- DB config is in `logistics-server/src/main/resources/application.properties`; it expects local MySQL `logistics_db`.

## Frontend conventions (Vue 3)
- Router + role guard in `logistics-web/src/router/index.js` controls access by `meta.roles`.
- Login state is stored in `localStorage.userInfo`; layout/menu in `logistics-web/src/App.vue` reads this directly.
- Role-based landing pages after login are implemented in `logistics-web/src/views/Login.vue`.
- Views call backend via `axios` directly inside each view component (no shared API client module yet).
- Keep Element Plus UX patterns consistent: `ElMessage`, `ElMessageBox`, tables + status tags.

## Integration gotchas to preserve/fix carefully
- Endpoint shape mismatch exists today: backend `/api/orders/search` returns `{ code, data }`, while `OrderSearch.vue` currently checks `res.data.id`.
- `User-Role` header is optionally read by `/api/orders/status`, but frontend status updates currently do not send it.
- CORS is enabled with `@CrossOrigin(origins = "*")` at controller level.

## Developer workflows
- Backend dev run (from `logistics-server`): `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`).
- Backend tests: `./mvnw test` (currently only context-load test exists).
- Frontend setup/run (from `logistics-web`): `npm install`, then `npm run dev`.
- Frontend production build: `npm run build`; preview with `npm run preview`.

## When adding new features
- Follow existing package structure under `controller`, `entity`, `mapper`.
- Prefer extending existing status/role dictionaries in both backend and frontend together.
- If changing API response contracts, update all affected Vue views in the same change.
- Use existing Chinese domain wording/messages for UI and logistics track content consistency.
