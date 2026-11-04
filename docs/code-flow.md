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

## Registration/login/JWT (planned A3-02)

Not implemented. Intended flow: validate input -> customer lookup/configured staff check -> BCrypt verification -> JWT -> filter/role checks on later requests. No authentication class or route is claimed complete.

## Customer multi-room booking/history (planned A3-04/A3-06)

Not implemented. Intended flow: authenticated customer -> validated room/date selections -> one transactional reservation with details -> server total -> own history. JPA entities exist; business endpoints and React forms do not.

## Staff management and referenced-room deletion (planned A3-03/A3-05)

Not implemented. Intended flow: staff authorization -> validation/search/CRUD -> check booking-detail references -> hard delete only an unused room, otherwise update status. Database FKs already block history deletion; service-level status behavior is pending.
