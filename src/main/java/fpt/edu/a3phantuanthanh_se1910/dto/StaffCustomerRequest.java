// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record StaffCustomerRequest(@NotNull @Valid ProfileRequest profile,
                                   @NotNull @Min(1) @Max(2) Short customerStatus,
                                   @Size(min = 8, max = 64) String password) {
}
