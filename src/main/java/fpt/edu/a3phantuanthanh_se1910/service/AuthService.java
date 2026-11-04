// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910.service;

import fpt.edu.a3phantuanthanh_se1910.dto.respone.AuthResponse;
import fpt.edu.a3phantuanthanh_se1910.dto.respone.CustomerResponse;
import fpt.edu.a3phantuanthanh_se1910.dto.request.LoginRequest;
import fpt.edu.a3phantuanthanh_se1910.dto.request.ProfileRequest;
import fpt.edu.a3phantuanthanh_se1910.dto.request.RegisterRequest;
import fpt.edu.a3phantuanthanh_se1910.entity.Customer;
import fpt.edu.a3phantuanthanh_se1910.exception.ApiException;
import fpt.edu.a3phantuanthanh_se1910.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Locale;

@Service
@Transactional
public class AuthService {
    private final CustomerRepository customers;
    private final PasswordEncoder passwords;
    private final JwtEncoder jwt;
    private final String staffEmail;
    private final String staffHash;

    public AuthService(CustomerRepository customers, PasswordEncoder passwords, JwtEncoder jwt,
                       @Value("${app.staff.email}") String staffEmail,
                       @Value("${app.staff.password}") String staffPassword) {
        if (staffEmail.isBlank() || staffPassword.length() < 8 || staffPassword.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalStateException("Configure a staff email and a staff password of 8 to 72 bytes.");
        }
        this.customers = customers;
        this.passwords = passwords;
        this.jwt = jwt;
        this.staffEmail = normalizeEmail(staffEmail);
        this.staffHash = passwords.encode(staffPassword);
    }

    public CustomerResponse register(RegisterRequest request) {
        Customer customer = new Customer();
        applyProfile(customer, request.profile());
        customer.setPassword(encodePassword(request.password()));
        customer.setCustomerStatus((short) 1);
        return CustomerResponse.from(customers.saveAndFlush(customer));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        if (email.equals(staffEmail) && passwords.matches(request.password(), staffHash)) {
            return issueToken("staff", "STAFF", "Staff");
        }
        Customer customer = customers.findByEmailAddressIgnoreCase(email)
                .filter(c -> Short.valueOf((short) 1).equals(c.getCustomerStatus()))
                .filter(c -> c.getPassword() != null && passwords.matches(request.password(), c.getPassword()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password."));
        return issueToken(customer.getCustomerId().toString(), "CUSTOMER", customer.getCustomerFullName());
    }

    public Customer requireCustomer(int id) {
        return customers.findById(id).filter(c -> Short.valueOf((short) 1).equals(c.getCustomerStatus()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Your customer account is unavailable."));
    }

    public CustomerResponse profile(int id) { return CustomerResponse.from(requireCustomer(id)); }

    public CustomerResponse updateProfile(int id, ProfileRequest request) {
        Customer customer = requireCustomer(id);
        applyProfile(customer, request);
        return CustomerResponse.from(customers.saveAndFlush(customer));
    }

    public void applyProfile(Customer customer, ProfileRequest request) {
        String email = normalizeEmail(request.emailAddress());
        boolean reserved = email.equals(staffEmail) || customers.findByEmailAddressIgnoreCase(email)
                .filter(existing -> !existing.getCustomerId().equals(customer.getCustomerId())).isPresent();
        if (reserved) throw new ApiException(HttpStatus.CONFLICT, "This email address is already in use.");
        customer.setCustomerFullName(request.customerFullName().strip());
        customer.setTelephone(request.telephone().strip());
        customer.setEmailAddress(email);
        customer.setCustomerBirthday(request.customerBirthday());
    }

    public String encodePassword(String password) {
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Password must not exceed 72 UTF-8 bytes.");
        }
        return passwords.encode(password);
    }

    private AuthResponse issueToken(String subject, String role, String name) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder().issuer("fuminihotel").subject(subject)
                .issuedAt(now).expiresAt(now.plusSeconds(3600)).claim("role", role).build();
        String token = jwt.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
        return new AuthResponse(token, role, name, 3600);
    }

    private String normalizeEmail(String email) { return email.strip().toLowerCase(Locale.ROOT); }
}
