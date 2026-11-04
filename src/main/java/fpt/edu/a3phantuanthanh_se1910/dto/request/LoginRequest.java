// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.dto.request;

import jakarta.validation.constraints.*;

public record LoginRequest(@NotBlank @Email @Size(max = 50) String email,
                           @NotBlank @Size(max = 64) String password) {
}
