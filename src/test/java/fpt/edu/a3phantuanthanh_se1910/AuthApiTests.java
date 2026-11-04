// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910;

import fpt.edu.a3phantuanthanh_se1910.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import java.time.Instant;
import java.util.Map;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthApiTests extends ApiTestSupport {
    @Autowired CustomerRepository customers;
    @Autowired PasswordEncoder passwords;
    @Autowired JwtEncoder encoder;

    @Test void registrationHashesPasswordAndRejectsDuplicateEmail() throws Exception {
        String email=uniqueEmail(), password=password();
        register(email,password).andExpect(status().isCreated()).andExpect(jsonPath("$.password").doesNotExist());
        String hash=customers.findByEmailAddressIgnoreCase(email).orElseThrow().getPassword();
        assertThat(hash).isNotEqualTo(password);
        assertThat(passwords.matches(password,hash)).isTrue();
        register(email.toUpperCase(),password).andExpect(status().isConflict());
    }

    @Test void validCustomerTokenReadsAndUpdatesOwnProfile() throws Exception {
        String token=customerToken(), email=uniqueEmail();
        mvc.perform(get("/api/profile").header("Authorization","Bearer "+token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.password").doesNotExist());
        mvc.perform(put("/api/profile").header("Authorization","Bearer "+token)
                .contentType("application/json").content(json.writeValueAsString(profile(email))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.emailAddress").value(email));
    }

    @Test void wrongPasswordIsRejected() throws Exception {
        String email=uniqueEmail(); register(email,password()).andExpect(status().isCreated());
        mvc.perform(post("/api/auth/login").contentType("application/json")
                .content(json.writeValueAsString(Map.of("email",email,"password",password()))))
                .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.message").value("Invalid email or password."));
    }

    @Test void missingTokenAndWrongRolesAreRejected() throws Exception {
        mvc.perform(get("/api/profile")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/profile").header("Authorization","Bearer "+staffToken())).andExpect(status().isForbidden());
        mvc.perform(get("/api/staff/customers").header("Authorization","Bearer "+customerToken())).andExpect(status().isForbidden());
    }

    @Test void invalidAndExpiredTokensAreRejected() throws Exception {
        mvc.perform(get("/api/profile").header("Authorization","Bearer invalid.token.value")).andExpect(status().isUnauthorized());
        JwtClaimsSet claims=JwtClaimsSet.builder().issuer("fuminihotel").subject("3")
                .issuedAt(Instant.now().minusSeconds(7200)).expiresAt(Instant.now().minusSeconds(3600))
                .claim("role","CUSTOMER").build();
        String expired=encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(),claims)).getTokenValue();
        mvc.perform(get("/api/profile").header("Authorization","Bearer "+expired)).andExpect(status().isUnauthorized());
    }

    @Test void invalidRegistrationAndReservedStaffEmailAreRejected() throws Exception {
        register(staffEmail,password()).andExpect(status().isConflict());
        register(uniqueEmail(),"short").andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors.password").exists());
        mvc.perform(post("/api/auth/register").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test void invalidBirthdayAndDuplicateProfileEmailAreRejected() throws Exception {
        String firstEmail=uniqueEmail(); register(firstEmail,password()).andExpect(status().isCreated());
        String token=customerToken();
        mvc.perform(put("/api/profile").header("Authorization","Bearer "+token).contentType("application/json")
                .content(json.writeValueAsString(profile(firstEmail)))).andExpect(status().isConflict());
        mvc.perform(put("/api/profile").header("Authorization","Bearer "+token).contentType("application/json")
                .content("{\"customerFullName\":\"Test\",\"telephone\":\"0901234567\",\"emailAddress\":\"test@example.com\",\"customerBirthday\":\"2999-01-01\"}"))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.fieldErrors.customerBirthday").exists());
    }

    @Test void oversizedUtf8PasswordIsRejectedWithoutServerError() throws Exception {
        String email=uniqueEmail(); register(email,password()).andExpect(status().isCreated());
        mvc.perform(post("/api/auth/login").contentType("application/json")
                .content(json.writeValueAsString(Map.of("email",email,"password","\u00e9".repeat(64)))))
                .andExpect(status().isUnauthorized());
        register(uniqueEmail(),"\u00e9".repeat(64)).andExpect(status().isBadRequest());
    }
}
