// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record ProfileRequest(
        @NotBlank @Size(max = 50) String customerFullName,
        @NotBlank @Pattern(regexp = "[0-9+() -]{7,12}", message = "must contain 7 to 12 telephone characters") String telephone,
        @NotBlank @Email @Size(max = 50) String emailAddress,
        @NotNull @Past LocalDate customerBirthday) {
}
