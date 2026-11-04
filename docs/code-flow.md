<!-- Author: Phan Tuan Thanh - CE190595 -->

# Code flow

Backend paths below are relative to src/main/java/fpt/edu/a3phantuanthanh_se1910/; frontend paths are relative to frontend/A3PhanTuanThanh_SE1910/src/.

## 1. Registration, login, JWT and profile

pages/AuthPages.jsx -> Axios api.js -> controller/AuthController.java -> service/AuthService.java -> repository/CustomerRepository.java. Register validates the nested profile/password DTO, normalizes and checks email, BCrypt-hashes the password and saves an active customer. Login verifies the customer hash or configured staff hash, then JwtEncoder returns token/role/name/expiresIn.

config/SecurityConfig.java installs Spring Security's bearer filter, validates signature/issuer/time and maps roles before controller access. AuthContext.jsx/session.js preserve the one-hour tab session. The Axios interceptor attaches JWT and clears session on 401. pages/ProfilePage.jsx GETs/PUTs /profile; the backend derives ID from JWT and rechecks active status. CustomerResponse contains no password/hash.

## 2. Guest room browsing

App.jsx -> pages/RoomsPage.jsx -> GET /api/rooms through the Vite proxy -> controller/RoomCatalogController.java -> service/RoomCatalogService.java -> RoomInformationRepository/RoomTypeRepository -> SQL Server. The read-only service maps room/type fields into DTO records while its persistence context is open. React renders rooms, status, price or an actual error/empty/loading state. No mock catalog is used.

## 3. Customer multi-room booking and own history

pages/CustomerBooking.jsx loads active rooms; components/BookingEditor.jsx collects distinct room IDs and check-in/out dates. POST /api/bookings -> controller/BookingController.java -> service/BookingService.java#create/save. JWT supplies customer ID. The service validates the full draft, locks rooms in order, checks status/overlap, allocates a sequence ID, calculates prices and saves all details in one transaction.

The response navigates to /history. pages/HistoryPage.jsx calls GET /api/bookings; BookingService.history/ownBooking restrict results to the authenticated customer. components/BookingDetails.jsx renders stored dates, nightly prices and line totals. Another customer's item returns 404. Server overlap and validation messages pass through errorMessage/Notice.

## 4. Staff CRUD/search and safe deletion

pages/StaffCustomers.jsx / pages/StaffRooms.jsx -> /api/staff/... -> controller/StaffManagementController.java -> service/StaffManagementService.java -> repositories. Only STAFF reaches these endpoints. Validated create/edit forms persist real data; query methods search name/email or number/description. Room writes use a row lock and a database unique-number constraint.

deleteRoom checks BookingDetailRepository.existsByRoomRoomId: referenced room -> status 2; unused room -> delete. deleteCustomer checks reservation history and rejects deletion with 409. Four NO ACTION FKs provide a second history safeguard.

pages/StaffBookings.jsx -> BookingController staff methods -> BookingService list/get/saveStaff/changeStatus. Staff can create offline reservations, edit room/date details and cancel/reactivate. Cancellation preserves reservation/detail rows; availability is checked on reactivation.

## Verification locations

src/test/java/fpt/edu/a3phantuanthanh_se1910/: bootstrap mapping tests, AuthApiTests, StaffManagementApiTests, BookingApiTests and BookingConcurrencyTests. The concurrency test uses two independent transactions; its dedicated fixtures are cleaned explicitly. Other backend fixtures roll back.

date.test.js verifies local calendar boundaries with Node's test runner. postman/a3-07-regression.postman_collection.json exercises the running API with runtime-only credentials/tokens. Browser steps and actual screenshots are linked from [test-matrix.md](test-matrix.md).
