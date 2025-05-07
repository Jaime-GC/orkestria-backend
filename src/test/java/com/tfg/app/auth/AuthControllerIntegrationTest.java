package com.tfg.app.auth;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {
    @Autowired private MockMvc mvc;

    @Test void registerAndLogin_flow() throws Exception {
        String userJson = "{\"username\":\"test\",\"email\":\"a@b\",\"password\":\"p\"}";
        mvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(userJson))
          .andExpect(status().isOk());
        var res = mvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"test\",\"password\":\"p\"}"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.token").isString())
          .andReturn();
    }
}
