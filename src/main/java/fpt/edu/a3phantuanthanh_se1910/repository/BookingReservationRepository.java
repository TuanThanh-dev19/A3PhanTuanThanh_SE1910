// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.repository;

import fpt.edu.a3phantuanthanh_se1910.entity.BookingReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface BookingReservationRepository extends JpaRepository<BookingReservation, Integer> {
    List<BookingReservation> findAllByOrderByBookingReservationIdDesc();
    List<BookingReservation> findByCustomerCustomerIdOrderByBookingReservationIdDesc(Integer customerId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from BookingReservation b where b.bookingReservationId = :id")
    Optional<BookingReservation> findLockedById(@Param("id") Integer id);
    boolean existsByCustomerCustomerId(Integer customerId);
}
