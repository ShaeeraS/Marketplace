package com.makers.marketplace.controller;

import com.makers.marketplace.model.User;
import com.makers.marketplace.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void testShowSignupForm() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/signup"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("signup"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("user"));
    }

    @Test
    void testRegisterUserSuccess() throws Exception {
        User user = new User();
        user.setUsername("newuser");
        user.setPassword("password");

        // Simulate successful registration (no exception thrown)
        Mockito.doNothing().when(userService).registerNewUser(Mockito.any(User.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "newuser")
                        .param("password", "password"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/login?success"));

        // Verify that service was called with the correct parameters
        Mockito.verify(userService).registerNewUser(Mockito.argThat(u -> "newuser".equals(u.getUsername())));
    }

}