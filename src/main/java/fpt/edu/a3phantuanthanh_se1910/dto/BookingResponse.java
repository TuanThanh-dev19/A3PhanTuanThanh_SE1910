// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BookingResponse(Integer bookingReservationId, LocalDate bookingDate, BigDecimal totalPrice,
                              Integer customerId, String customerName, String customerEmail,
                              Short bookingStatus, List<BookingDetailResponse> details) {
}
