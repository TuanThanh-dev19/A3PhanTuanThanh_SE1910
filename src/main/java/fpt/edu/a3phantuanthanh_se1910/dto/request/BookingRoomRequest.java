// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record BookingRoomRequest(@NotNull @Positive Integer roomId,
                                 @NotNull LocalDate startDate, @NotNull LocalDate endDate) {
}
