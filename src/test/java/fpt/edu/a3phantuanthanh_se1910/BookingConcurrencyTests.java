// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910;

import fpt.edu.a3phantuanthanh_se1910.dto.request.BookingRequest;
import fpt.edu.a3phantuanthanh_se1910.dto.request.BookingRoomRequest;
import fpt.edu.a3phantuanthanh_se1910.dto.respone.CustomerResponse;
import fpt.edu.a3phantuanthanh_se1910.dto.request.ProfileRequest;
import fpt.edu.a3phantuanthanh_se1910.dto.request.RegisterRequest;
import fpt.edu.a3phantuanthanh_se1910.entity.*;
import fpt.edu.a3phantuanthanh_se1910.exception.ApiException;
import fpt.edu.a3phantuanthanh_se1910.repository.*;
import fpt.edu.a3phantuanthanh_se1910.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class BookingConcurrencyTests {
    @Autowired AuthService auth;
    @Autowired BookingService service;
    @Autowired RoomInformationRepository rooms;
    @Autowired RoomTypeRepository types;
    @Autowired JdbcTemplate jdbc;

    @Test void simultaneousRequestsCannotDoubleBookTheSameRoom() throws Exception {
        String email=UUID.randomUUID()+"@test.local";
        CustomerResponse customer=auth.register(new RegisterRequest(new ProfileRequest("Concurrency Test",
                "0901234567",email,LocalDate.of(2000,1,1)),"Test!"+UUID.randomUUID()));
        RoomInformation room=new RoomInformation(); room.setRoomNumber("CT-"+UUID.randomUUID());
        room.setRoomType(types.findById(1).orElseThrow());room.setRoomMaxCapacity(2);room.setRoomStatus((short)1);
        room.setRoomPricePerDay(new BigDecimal("600000"));room=rooms.saveAndFlush(room);
        int roomId=room.getRoomId();
        BookingRequest request=new BookingRequest(List.of(new BookingRoomRequest(roomId,LocalDate.now().plusDays(60),LocalDate.now().plusDays(62))));
        ExecutorService executor=Executors.newFixedThreadPool(2);
        CountDownLatch ready=new CountDownLatch(2),start=new CountDownLatch(1);
        try {
            Callable<Integer> attempt=() -> {
                ready.countDown();
                if (!start.await(10,TimeUnit.SECONDS)) throw new IllegalStateException("Start latch timed out.");
                try { service.create(customer.customerId(),request); return 201; }
                catch (ApiException failure) { return failure.getStatus().value(); }
            };
            Future<Integer> first=executor.submit(attempt),second=executor.submit(attempt);
            assertThat(ready.await(10,TimeUnit.SECONDS)).isTrue();start.countDown();
            assertThat(List.of(first.get(20,TimeUnit.SECONDS),second.get(20,TimeUnit.SECONDS)))
                    .containsExactlyInAnyOrder(201,409);
            assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM dbo.BookingDetail WHERE RoomID=?",Integer.class,roomId)).isEqualTo(1);
        } finally {
            start.countDown();executor.shutdownNow();executor.awaitTermination(10,TimeUnit.SECONDS);
            // Remove only the dedicated fixtures created by this test, never application history.
            jdbc.update("DELETE FROM dbo.BookingDetail WHERE RoomID=?",roomId);
            jdbc.update("DELETE FROM dbo.BookingReservation WHERE CustomerID=?",customer.customerId());
            jdbc.update("DELETE FROM dbo.RoomInformation WHERE RoomID=?",roomId);
            jdbc.update("DELETE FROM dbo.Customer WHERE CustomerID=?",customer.customerId());
        }
    }
}
