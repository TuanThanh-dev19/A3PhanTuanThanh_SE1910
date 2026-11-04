// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.dto;

public record AuthResponse(String token, String role, String name, long expiresIn) {
}
