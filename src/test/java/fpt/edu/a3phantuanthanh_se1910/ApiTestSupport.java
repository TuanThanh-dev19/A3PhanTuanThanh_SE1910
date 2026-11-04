// Author: Phan Tuan Thanh - CE190595
package fpt.edu.a3phantuanthanh_se1910;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.UUID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
abstract class ApiTestSupport {
    @Autowired protected MockMvc mvc;
    @Autowired protected ObjectMapper json;
    @Value("${app.staff.email}") protected String staffEmail;
    @Value("${app.staff.password}") protected String staffPassword;

    protected String uniqueEmail() { return UUID.randomUUID() + "@test.local"; }
    protected String password() { return "Test!" + UUID.randomUUID(); }
    protected Map<String, Object> profile(String email) {
        return Map.of("customerFullName", "API Test Customer", "telephone", "0901234567",
                "emailAddress", email, "customerBirthday", "2000-01-01");
    }
    protected ResultActions register(String email, String password) throws Exception {
        return mvc.perform(post("/api/auth/register").contentType("application/json")
                .content(json.writeValueAsString(Map.of("profile", profile(email), "password", password))));
    }
    protected String login(String email, String password) throws Exception {
        String body = mvc.perform(post("/api/auth/login").contentType("application/json")
                .content(json.writeValueAsString(Map.of("email", email, "password", password))))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return json.readTree(body).get("token").asText();
    }
    protected String customerToken() throws Exception {
        String email=uniqueEmail(), password=password();
        register(email,password).andExpect(status().isCreated());
        return login(email,password);
    }
    protected String staffToken() throws Exception { return login(staffEmail,staffPassword); }
}