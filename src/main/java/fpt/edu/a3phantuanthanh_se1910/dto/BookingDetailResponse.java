// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingDetailResponse(Integer roomId, String roomNumber, LocalDate startDate,
                                    LocalDate endDate, BigDecimal actualPrice, BigDecimal lineTotal) {
}
