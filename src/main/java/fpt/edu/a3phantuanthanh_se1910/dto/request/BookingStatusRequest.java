// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.dto.request;

import jakarta.validation.constraints.*;

public record BookingStatusRequest(@NotNull @Min(1) @Max(2) Short bookingStatus) {
}
