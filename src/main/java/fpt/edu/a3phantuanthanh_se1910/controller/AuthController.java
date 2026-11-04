// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.controller;

import fpt.edu.a3phantuanthanh_se1910.dto.*;
import fpt.edu.a3phantuanthanh_se1910.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService auth;
    public AuthController(AuthService auth) { this.auth = auth; }

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse register(@Valid @RequestBody RegisterRequest request) { return auth.register(request); }

    @PostMapping("/auth/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) { return auth.login(request); }

    @GetMapping("/profile")
    public CustomerResponse profile(@AuthenticationPrincipal Jwt jwt) { return auth.profile(Integer.parseInt(jwt.getSubject())); }

    @PutMapping("/profile")
    public CustomerResponse update(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ProfileRequest request) {
        return auth.updateProfile(Integer.parseInt(jwt.getSubject()), request);
    }
}
