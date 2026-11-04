// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910;

import fpt.edu.a3phantuanthanh_se1910.entity.*;
import fpt.edu.a3phantuanthanh_se1910.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookingApiTests extends ApiTestSupport {
    @Autowired RoomInformationRepository rooms;
    @Autowired RoomTypeRepository types;
    @Autowired BookingReservationRepository bookings;
    @Autowired BookingDetailRepository details;
    int first,second;
    LocalDate start=LocalDate.now().plusDays(30);

    @BeforeEach void createRooms() {
        first=createRoom("500000"); second=createRoom("550000");
    }
    private int createRoom(String price) {
        RoomInformation room=new RoomInformation();room.setRoomNumber("BT-"+UUID.randomUUID());
        room.setRoomType(types.findById(1).orElseThrow());room.setRoomMaxCapacity(2);room.setRoomStatus((short)1);
        room.setRoomPricePerDay(new BigDecimal(price));return rooms.saveAndFlush(room).getRoomId();
    }
    private List<Map<String,Object>> lines(int[] ids,LocalDate from,LocalDate to) {
        return Arrays.stream(ids).mapToObj(id -> Map.<String,Object>of("roomId",id,"startDate",from.toString(),"endDate",to.toString())).toList();
    }
    private ResultActions book(String token,int[] ids,LocalDate from,LocalDate to) throws Exception {
        return mvc.perform(post("/api/bookings").header("Authorization","Bearer "+token).contentType("application/json")
                .content(json.writeValueAsString(Map.of("rooms",lines(ids,from,to),"totalPrice",1))));
    }
    private int bookingId(ResultActions result) throws Exception {
        return json.readTree(result.andReturn().getResponse().getContentAsString()).get("bookingReservationId").asInt();
    }

    @Test void createsOneAndManyRoomsWithServerTotalsAndDifferentIds() throws Exception {
        String token=customerToken();
        int one=bookingId(book(token,new int[]{first},start,start.plusDays(2)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalPrice").value(1000000)).andExpect(jsonPath("$.details.length()").value(1)));
        int multi=bookingId(book(token,new int[]{first,second},start.plusDays(2),start.plusDays(4)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalPrice").value(2100000)).andExpect(jsonPath("$.details.length()").value(2)));
        assertThat(one).isNotEqualTo(multi);
    }

    @Test void rejectsOverlapsButAllowsAdjacentStays() throws Exception {
        String token=customerToken();
        book(token,new int[]{first},start,start.plusDays(2)).andExpect(status().isCreated());
        book(token,new int[]{first},start.plusDays(1),start.plusDays(3)).andExpect(status().isConflict());
        book(token,new int[]{first},start.plusDays(2),start.plusDays(3)).andExpect(status().isCreated());
    }

    @Test void invalidOrMissingRoomLeavesNoPartialReservation() throws Exception {
        String token=customerToken();long count=bookings.count();
        book(token,new int[]{first,2147483647},start,start.plusDays(2)).andExpect(status().isNotFound());
        assertThat(bookings.count()).isEqualTo(count);
        book(token,new int[]{first,first},start,start.plusDays(2)).andExpect(status().isBadRequest());
        book(token,new int[]{first},start,start).andExpect(status().isBadRequest());
        book(token,new int[]{first},LocalDate.now().minusDays(1),start).andExpect(status().isBadRequest());
        assertThat(bookings.count()).isEqualTo(count);
    }

    @Test void inactiveRoomAndEmptyDetailsAreRejected() throws Exception {
        rooms.findById(first).orElseThrow().setRoomStatus((short)2);rooms.flush();
        String token=customerToken();book(token,new int[]{first},start,start.plusDays(1)).andExpect(status().isConflict());
        mvc.perform(post("/api/bookings").header("Authorization","Bearer "+token).contentType("application/json").content("{\"rooms\":[]}"))
                .andExpect(status().isBadRequest());
    }

    @Test void historyAndItemLookupRespectCustomerOwnership() throws Exception {
        String owner=customerToken();int id=bookingId(book(owner,new int[]{first},start,start.plusDays(1)).andExpect(status().isCreated()));
        String other=customerToken();
        mvc.perform(get("/api/bookings").header("Authorization","Bearer "+owner)).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookingReservationId").value(id));
        mvc.perform(get("/api/bookings/"+id).header("Authorization","Bearer "+other)).andExpect(status().isNotFound());
        mvc.perform(get("/api/bookings").header("Authorization","Bearer "+other)).andExpect(jsonPath("$.length()").value(0));
    }

    @Test void staffEditsDetailsAndCancellationPreservesHistory() throws Exception {
        String owner=customerToken(),staff=staffToken();
        int id=bookingId(book(owner,new int[]{first},start,start.plusDays(2)).andExpect(status().isCreated()));
        int customerId=bookings.findById(id).orElseThrow().getCustomer().getCustomerId();
        Map<String,Object> update=Map.of("customerId",customerId,"bookingStatus",1,"rooms",lines(new int[]{first,second},start,start.plusDays(2)));
        mvc.perform(put("/api/staff/bookings/"+id).header("Authorization","Bearer "+staff).contentType("application/json")
                .content(json.writeValueAsString(update))).andExpect(status().isOk()).andExpect(jsonPath("$.details.length()").value(2))
                .andExpect(jsonPath("$.totalPrice").value(2100000));
        mvc.perform(delete("/api/staff/bookings/"+id).header("Authorization","Bearer "+staff)).andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingStatus").value(2)).andExpect(jsonPath("$.details.length()").value(2));
        assertThat(details.findByBookingReservationBookingReservationIdOrderByRoomRoomIdAsc(id)).hasSize(2);
        book(owner,new int[]{first},start,start.plusDays(2)).andExpect(status().isCreated());
        mvc.perform(patch("/api/staff/bookings/"+id+"/status").header("Authorization","Bearer "+staff).contentType("application/json")
                .content("{\"bookingStatus\":1}")).andExpect(status().isConflict());
    }

    @Test void wrongRoleCannotCreateCustomerBookingOrReadStaffBookings() throws Exception {
        book(staffToken(),new int[]{first},start,start.plusDays(1)).andExpect(status().isForbidden());
        mvc.perform(get("/api/staff/bookings").header("Authorization","Bearer "+customerToken())).andExpect(status().isForbidden());
        mvc.perform(get("/api/bookings")).andExpect(status().isUnauthorized());
    }

    @Test void failedStaffEditKeepsOriginalDetailsAndTotal() throws Exception {
        String token=customerToken(), staff=staffToken();
        int id=bookingId(book(token,new int[]{first},start,start.plusDays(2)).andExpect(status().isCreated()));
        book(token,new int[]{second},start,start.plusDays(2)).andExpect(status().isCreated());
        int customerId=bookings.findById(id).orElseThrow().getCustomer().getCustomerId();
        mvc.perform(put("/api/staff/bookings/"+id).header("Authorization","Bearer "+staff).contentType("application/json")
                .content(json.writeValueAsString(Map.of("customerId",customerId,"bookingStatus",1,
                        "rooms",lines(new int[]{first,second},start,start.plusDays(2))))))
                .andExpect(status().isConflict());
        assertThat(details.findByBookingReservationBookingReservationIdOrderByRoomRoomIdAsc(id)).hasSize(1);
        assertThat(bookings.findById(id).orElseThrow().getTotalPrice()).isEqualByComparingTo("1000000");
    }
}
