// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.repository;

import fpt.edu.a3phantuanthanh_se1910.entity.BookingReservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingReservationRepository extends JpaRepository<BookingReservation, Integer> {
    boolean existsByCustomerCustomerId(Integer customerId);
}
