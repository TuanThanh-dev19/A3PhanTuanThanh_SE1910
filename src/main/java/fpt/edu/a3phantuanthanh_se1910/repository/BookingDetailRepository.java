// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.repository;

import fpt.edu.a3phantuanthanh_se1910.entity.BookingDetail;
import fpt.edu.a3phantuanthanh_se1910.entity.BookingDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface BookingDetailRepository extends JpaRepository<BookingDetail, BookingDetailId> {
    List<BookingDetail> findByBookingReservationBookingReservationIdOrderByRoomRoomIdAsc(Integer bookingId);

    @Query("select count(d) from BookingDetail d where d.room.roomId = :roomId "
            + "and d.bookingReservation.bookingStatus = 1 and d.startDate < :endDate "
            + "and d.endDate > :startDate and d.bookingReservation.bookingReservationId <> :excludedId")
    long countOverlaps(@Param("roomId") Integer roomId, @Param("startDate") LocalDate startDate,
                       @Param("endDate") LocalDate endDate, @Param("excludedId") Integer excludedId);
    boolean existsByRoomRoomId(Integer roomId);
}
