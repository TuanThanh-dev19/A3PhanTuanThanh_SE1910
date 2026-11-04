<!-- Author: Phan Tuan Thanh - CE190595 -->

# Code flow

All backend paths below begin at `src/main/java/fpt/edu/a3phantuanthanh_se1910/`.

## Guest room catalog (implemented)

1. `frontend/A3PhanTuanThanh_SE1910/src/main.jsx` mounts `App`.
2. `src/App.jsx` uses an effect to call Axios `api.get('/rooms')` and `api.get('/room-types')`; `src/api.js` sets base URL `/api` and a ten-second timeout. Cleanup aborts stale requests.
3. `vite.config.js` forwards `/api` from port 5173 to backend port 8081.
4. `controller/RoomCatalogController.java` routes GET requests to `service/RoomCatalogService.java` (`listRooms`, `listRoomTypes`).
5. The read-only service calls the ordered query methods in `repository/RoomInformationRepository.java` and `repository/RoomTypeRepository.java`. SQL Server supplies the persisted records.
6. The service maps entities to `dto/RoomResponse.java` / `dto/RoomTypeResponse.java` while the transaction is open. Spring serializes JSON; React renders the data, empty state, loading state or English error message.
7. `controller/ApiExceptionHandler.java` maps database access failures to HTTP 503 with a generic message.

## Schema and persistence (implemented)

`A3PhanTuanThanhSe1910Application.main` starts Spring and scans five JPA repositories. `application.properties` loads environment/ignored local settings and validates SQL mappings. With explicit `bootstrap`, `application-bootstrap.properties` enables schema creation through Hibernate. `entity/BookingDetailId.java` implements serializable composite identity with equality/hash code; `entity/BookingDetail.java` maps both FK components using `@MapsId`. `entity/BookingReservation.java` allocates IDs through `BookingReservationSequence`.

`src/test/java/fpt/edu/a3phantuanthanh_se1910/A3PhanTuanThanhSe1910ApplicationTests.java` exercises the real SQL Server and rolls back test rows. `scripts/verify-a3-01.ps1` compares direct API responses with frontend-proxied data.

## Registration/login/JWT and profile (implemented A3-02)

`controller/AuthController.java` validates registration/login/profile DTOs and calls `service/AuthService.java`. Registration normalizes email, checks uniqueness/reserved staff email, hashes the password and saves a customer. Login compares BCrypt against the customer hash or the configured staff password hash and uses JwtEncoder to issue a one-hour token. Spring Security's bearer-token filter validates signature/issuer/time and maps role claims before controller access. GET/PUT `/api/profile` derive the customer ID from the validated JWT subject and require an active account; callers cannot choose another customer ID.

`controller/ApiExceptionHandler.java` returns English messages, field-level validation errors and generic database conflict/unavailable responses. `AuthApiTests.java` checks BCrypt, duplicate/reserved emails, valid/invalid/expired tokens, role boundaries and profile validation. The local Postman collection checks the running application; tokens/credentials remain runtime variables.

## Booking and staff flows

A3-03 staff management is implemented: StaffManagementController validates DTOs and role security permits only STAFF. StaffManagementService searches repositories, applies AuthService profile/password validation and saves records. deleteRoom locks the room, calls BookingDetailRepository.existsByRoomRoomId, then deactivates referenced rooms or deletes unused rooms. deleteCustomer rejects existing reservation history. StaffManagementApiTests exercises CRUD/search/validation and actual history preservation.

BookingController validates BookingRequest/StaffBookingRequest and calls BookingService. Customer creation reads identity from JWT; save locks affected rooms, validates active status/dates/overlap, allocates a sequence ID, calculates totals and saves all details in one transaction. history/ownBooking restrict customer ownership; staff methods use STAFF routes. changeStatus cancels/reactivates while retaining details, with availability validation on reactivation. BookingApiTests exercises server totals, adjacent/overlapping dates, failed multi-room rollback, ownership, detail edits and cancellation. React authentication/role screens remain planned A3-05/A3-06.
