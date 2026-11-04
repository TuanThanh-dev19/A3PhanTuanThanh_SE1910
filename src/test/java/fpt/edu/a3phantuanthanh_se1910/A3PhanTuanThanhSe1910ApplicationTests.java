// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910;

import fpt.edu.a3phantuanthanh_se1910.entity.*;
import fpt.edu.a3phantuanthanh_se1910.repository.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class A3PhanTuanThanhSe1910ApplicationTests {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @Autowired EntityManager entityManager;
    @Autowired RoomTypeRepository roomTypes;
    @Autowired RoomInformationRepository rooms;
    @Autowired CustomerRepository customers;
    @Autowired BookingReservationRepository bookings;
    @Autowired BookingDetailRepository details;

    @Test
    void connectsToSqlServerAndMapsAllFiveTables() {
        assertThat(jdbc.queryForObject("SELECT DB_NAME()", String.class))
                .isEqualTo("A3PhanTuanThanh_SE1910");
        assertThat(entityManager.getMetamodel().getEntities()).hasSize(5);
        assertThat(roomTypes.count()).isGreaterThanOrEqualTo(2);
        assertThat(rooms.count()).isGreaterThanOrEqualTo(3);
    }

    @Test
    void publicCatalogReadsSeededRoomsWithoutCredentials() throws Exception {
        mvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roomNumber").value("101"))
                .andExpect(jsonPath("$[0].roomTypeName").value("Standard"))
                .andExpect(jsonPath("$[0].roomPricePerDay").value(500000))
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    @Test
    void publicCatalogReadsRoomTypes() throws Exception {
        mvc.perform(get("/api/room-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].roomTypeName").value("Standard"));
    }

    @Test
    void unauthenticatedWriteIsRejected() throws Exception {
        mvc.perform(post("/api/rooms").contentType("application/json").content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void passwordColumnStoresMoreThanFiftyCharacters() {
        Customer customer = createCustomer();
        customer.setPassword("x".repeat(60));
        customers.saveAndFlush(customer);
        entityManager.clear();
        assertThat(customers.findById(customer.getCustomerId()).orElseThrow().getPassword()).hasSize(60);
        assertThat(jdbc.queryForObject("SELECT CHARACTER_MAXIMUM_LENGTH FROM INFORMATION_SCHEMA.COLUMNS "
                + "WHERE TABLE_NAME = 'Customer' AND COLUMN_NAME = 'Password'", Integer.class)).isEqualTo(255);
    }

    @Test
    void persistsRelationshipsCompositeKeyAndSequenceAndRejectsHistoryDeletion() {
        Customer customer = customers.saveAndFlush(createCustomer());
        RoomInformation room = rooms.findAllByOrderByRoomIdAsc().getFirst();
        BookingReservation first = bookings.saveAndFlush(createBooking(customer));
        BookingReservation second = bookings.saveAndFlush(createBooking(customer));
        assertThat(first.getBookingReservationId()).isNotEqualTo(second.getBookingReservationId());

        BookingDetail detail = new BookingDetail();
        detail.setId(new BookingDetailId(first.getBookingReservationId(), room.getRoomId()));
        detail.setBookingReservation(first);
        detail.setRoom(room);
        detail.setStartDate(LocalDate.of(2026, 12, 1));
        detail.setEndDate(LocalDate.of(2026, 12, 2));
        detail.setActualPrice(new BigDecimal("500000.0000"));
        details.saveAndFlush(detail);
        entityManager.clear();

        BookingDetail loaded = details.findById(detail.getId()).orElseThrow();
        assertThat(loaded.getRoom().getRoomType().getRoomTypeName()).isEqualTo("Standard");
        assertThat(loaded.getBookingReservation().getCustomer().getCustomerId()).isEqualTo(customer.getCustomerId());
        assertThat(loaded.getActualPrice()).isEqualByComparingTo("500000.0000");
        assertThatThrownBy(() -> jdbc.update("DELETE FROM dbo.RoomInformation WHERE RoomID = ?", room.getRoomId()))
                .isInstanceOf(DataIntegrityViolationException.class);
        assertThat(details.existsById(detail.getId())).isTrue();
    }

    @Test
    void allForeignKeysRejectCascadingDeletes() {
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM sys.foreign_keys WHERE "
                + "parent_object_id IN (OBJECT_ID('dbo.RoomInformation'), OBJECT_ID('dbo.BookingReservation'), "
                + "OBJECT_ID('dbo.BookingDetail'))", Integer.class)).isEqualTo(4);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM sys.foreign_keys WHERE "
                + "parent_object_id IN (OBJECT_ID('dbo.RoomInformation'), OBJECT_ID('dbo.BookingReservation'), "
                + "OBJECT_ID('dbo.BookingDetail')) AND delete_referential_action <> 0", Integer.class)).isZero();
    }

    private Customer createCustomer() {
        Customer customer = new Customer();
        customer.setCustomerFullName("Bootstrap Test");
        customer.setEmailAddress(UUID.randomUUID() + "@test.local");
        customer.setCustomerStatus((short) 1);
        return customer;
    }

    private BookingReservation createBooking(Customer customer) {
        BookingReservation booking = new BookingReservation();
        booking.setCustomer(customer);
        booking.setBookingDate(LocalDate.of(2026, 11, 4));
        booking.setBookingStatus((short) 1);
        booking.setTotalPrice(new BigDecimal("500000.0000"));
        return booking;
    }
}
