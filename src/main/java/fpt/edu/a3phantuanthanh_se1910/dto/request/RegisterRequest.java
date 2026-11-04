// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record RegisterRequest(@NotNull @Valid ProfileRequest profile,
                              @NotBlank @Size(min = 8, max = 64) String password) {
}
