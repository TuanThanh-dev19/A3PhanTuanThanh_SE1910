// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910;

import fpt.edu.a3phantuanthanh_se1910.entity.*;
import fpt.edu.a3phantuanthanh_se1910.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StaffManagementApiTests extends ApiTestSupport {
    @Autowired JdbcTemplate jdbc;
    @Autowired RoomInformationRepository rooms;
    @Autowired CustomerRepository customers;
    @Autowired BookingReservationRepository bookings;
    @Autowired BookingDetailRepository details;

    private Map<String,Object> room(String number) {
        return Map.of("roomNumber",number,"roomDetailDescription","Test room","roomMaxCapacity",2,
                "roomTypeId",1,"roomStatus",1,"roomPricePerDay",650000);
    }

    @Test void staffCreatesSearchesUpdatesAndDeletesCustomer() throws Exception {
        String staff=staffToken(), email=uniqueEmail();
        String body=mvc.perform(post("/api/staff/customers").header("Authorization","Bearer "+staff)
                .contentType("application/json").content(json.writeValueAsString(Map.of("profile",profile(email),"password",password(),"customerStatus",1))))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.password").doesNotExist()).andReturn().getResponse().getContentAsString();
        int id=json.readTree(body).get("customerId").asInt();
        mvc.perform(get("/api/staff/customers").param("search",email).header("Authorization","Bearer "+staff))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(put("/api/staff/customers/"+id).header("Authorization","Bearer "+staff).contentType("application/json")
                .content(json.writeValueAsString(Map.of("profile",profile(email),"customerStatus",2))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.customerStatus").value(2));
        mvc.perform(delete("/api/staff/customers/"+id).header("Authorization","Bearer "+staff)).andExpect(status().isNoContent());
        assertThat(customers.existsById(id)).isFalse();
    }

    @Test void staffCreatesSearchesUpdatesAndDeletesUnusedRoom() throws Exception {
        String staff=staffToken(), number="T-"+System.nanoTime();
        String body=mvc.perform(post("/api/staff/rooms").header("Authorization","Bearer "+staff).contentType("application/json")
                .content(json.writeValueAsString(room(number)))).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        int id=json.readTree(body).get("roomId").asInt();
        mvc.perform(get("/api/staff/rooms").param("search",number).header("Authorization","Bearer "+staff))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        mvc.perform(put("/api/staff/rooms/"+id).header("Authorization","Bearer "+staff).contentType("application/json")
                .content(json.writeValueAsString(room(number+"-edit")))).andExpect(status().isOk());
        mvc.perform(delete("/api/staff/rooms/"+id).header("Authorization","Bearer "+staff)).andExpect(status().isOk());
        assertThat(rooms.existsById(id)).isFalse();
    }

    @Test void deletionDeactivatesBookedRoomAndProtectsCustomerHistory() throws Exception {
        String email=uniqueEmail(); register(email,password()).andExpect(status().isCreated());
        Customer customer=customers.findByEmailAddressIgnoreCase(email).orElseThrow();
        RoomInformation room=rooms.findById(1).orElseThrow();
        BookingReservation booking=new BookingReservation(); booking.setCustomer(customer); booking.setBookingDate(LocalDate.now());
        booking.setBookingStatus((short)1); booking.setTotalPrice(new BigDecimal("500000")); bookings.saveAndFlush(booking);
        BookingDetail detail=new BookingDetail(); detail.setId(new BookingDetailId(booking.getBookingReservationId(),room.getRoomId()));
        detail.setBookingReservation(booking);detail.setRoom(room);detail.setStartDate(LocalDate.now().plusDays(1));
        detail.setEndDate(LocalDate.now().plusDays(2));detail.setActualPrice(new BigDecimal("500000"));details.saveAndFlush(detail);
        String staff=staffToken();
        mvc.perform(delete("/api/staff/rooms/1").header("Authorization","Bearer "+staff)).andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Room deactivated because it has booking history."));
        assertThat(rooms.findById(1).orElseThrow().getRoomStatus()).isEqualTo((short)2);
        assertThat(details.existsById(detail.getId())).isTrue();
        mvc.perform(delete("/api/staff/customers/"+customer.getCustomerId()).header("Authorization","Bearer "+staff)).andExpect(status().isConflict());
        assertThat(customers.existsById(customer.getCustomerId())).isTrue();
    }

    @Test void validationDuplicatesAndMissingRecordsAreRejected() throws Exception {
        String staff=staffToken();
        mvc.perform(post("/api/staff/rooms").header("Authorization","Bearer "+staff).contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/staff/rooms").header("Authorization","Bearer "+staff).contentType("application/json")
                .content(json.writeValueAsString(room("101")))).andExpect(status().isConflict());
        mvc.perform(get("/api/staff/customers/2147483647").header("Authorization","Bearer "+staff)).andExpect(status().isNotFound());
        mvc.perform(get("/api/staff/rooms/2147483647").header("Authorization","Bearer "+staff)).andExpect(status().isNotFound());
    }

    @Test void databaseAlsoRejectsDuplicateRoomNumbers() {
        assertThatThrownBy(() -> jdbc.update("INSERT dbo.RoomInformation (RoomNumber,RoomTypeID) VALUES (?,?)","101",1))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
