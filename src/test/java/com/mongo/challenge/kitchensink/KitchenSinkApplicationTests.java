package com.mongo.challenge.kitchensink;

import com.mongo.challenge.kitchensink.config.MockSecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ContextConfiguration(classes = {MockSecurityConfig.class}) // Use the mock security config
class KitchenSinkApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
        // Verify the application context loads successfully
        assertThat(port).isGreaterThan(0);
    }

    @BeforeEach
    void setupMockAuthentication() {
        // Set up a mock security context with an admin role
        TestingAuthenticationToken auth = new TestingAuthenticationToken(
                "admin",
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
//    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    void testFetchMembersAsAdmin() {
        // Prepare admin credentials (e.g., using JWT or basic authentication)
        String adminToken = "Bearer valid_admin_jwt_token";

        // Create headers with the admin token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", adminToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Make a GET request to fetch all members
        ResponseEntity<String> response = restTemplate
                .exchange("http://localhost:" + port + "/members", HttpMethod.GET, entity, String.class);

//        ResponseEntity<String> response = restTemplate
//                .getForEntity("http://localhost:" + port + "/members", String.class);

        // Validate the response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("list_of_member_data");
    }

    @Test
    void testFetchOwnDetailsAsUser() {
        // Prepare user credentials
        String userToken = "Bearer valid_user_jwt_token";

        // Create headers with the user token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", userToken);

        // Make a GET request to fetch the user's own details
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("user", "password") // Replace with JWT if needed
                .getForEntity("http://localhost:" + port + "/api/members/me", String.class);

        // Validate the response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("specific_user_data");
    }
}
