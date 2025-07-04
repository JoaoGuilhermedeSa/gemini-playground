package com.example.charactercreation.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.charactercreation.dto.AccountRequest;
import com.example.charactercreation.model.Account;
import com.example.charactercreation.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AccountService accountService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void createAccount_Success() throws Exception {
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setUsername("testuser");
		accountRequest.setPassword("password123");

		Account account = new Account();
		account.setId(1L);
		account.setUsername("testuser");

		when(accountService.createAccount(anyString(), anyString())).thenReturn(account);

		mockMvc.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(accountRequest))).andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("testuser"));
	}

	@Test
	void createAccount_UsernameExists() throws Exception {
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setUsername("testuser");
		accountRequest.setPassword("password123");

		when(accountService.createAccount(anyString(), anyString()))
				.thenThrow(new IllegalArgumentException("Username already exists"));

		jakarta.servlet.ServletException thrown = Assertions.assertThrows(jakarta.servlet.ServletException.class,
				() -> mockMvc
						.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(accountRequest)))
						.andExpect(status().isBadRequest()));

	}

	@Test
	void login_Success() throws Exception {
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setUsername("testuser");
		accountRequest.setPassword("password123");

		Account account = new Account();
		account.setId(1L);
		account.setUsername("testuser");

		when(accountService.login(anyString(), anyString())).thenReturn(account);

		mockMvc.perform(post("/accounts/login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(accountRequest))).andExpect(status().isOk())
				.andExpect(jsonPath("$.username").value("testuser"));
	}

	@Test
	void login_InvalidCredentials() throws Exception {
		AccountRequest accountRequest = new AccountRequest();
		accountRequest.setUsername("testuser");
		accountRequest.setPassword("wrongpassword");

		when(accountService.login(anyString(), anyString()))
				.thenThrow(new IllegalArgumentException("Invalid credentials"));

		jakarta.servlet.ServletException thrown = Assertions.assertThrows(jakarta.servlet.ServletException.class,
				() -> mockMvc
						.perform(post("/accounts/login").contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(accountRequest)))
						.andExpect(status().isBadRequest()).andExpect(status().isBadRequest()));
	}
}