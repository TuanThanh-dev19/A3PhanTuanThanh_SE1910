// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record RoomRequest(@NotBlank @Size(max = 50) String roomNumber,
                          @Size(max = 220) String roomDetailDescription,
                          @NotNull @Min(1) Integer roomMaxCapacity,
                          @NotNull @Positive Integer roomTypeId,
                          @NotNull @Min(1) @Max(2) Short roomStatus,
                          @NotNull @DecimalMin("0.01") @DecimalMax("1000000000")
                          @Digits(integer = 10, fraction = 4) BigDecimal roomPricePerDay) {
}
