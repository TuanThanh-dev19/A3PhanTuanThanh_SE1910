// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record StaffBookingRequest(@NotNull @Positive Integer customerId,
                                  @NotNull @Min(1) @Max(2) Short bookingStatus,
                                  @NotEmpty @Size(max = 20) List<@NotNull @Valid BookingRoomRequest> rooms) {
}
