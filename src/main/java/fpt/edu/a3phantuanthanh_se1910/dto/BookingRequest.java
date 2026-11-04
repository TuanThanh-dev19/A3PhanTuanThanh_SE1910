// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record BookingRequest(@NotEmpty @Size(max = 20) List<@NotNull @Valid BookingRoomRequest> rooms) {
}
