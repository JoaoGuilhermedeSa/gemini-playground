package com.example.charactercreation.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import com.example.charactercreation.dto.CharacterRequest;
import com.example.charactercreation.dto.CommentRequest;
import com.example.charactercreation.model.Character;
import com.example.charactercreation.service.CharacterService;
import com.example.charactercreation.service.JwtService;
import com.example.charactercreation.service.UserDetailsServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;

@WebMvcTest(CharacterController.class)
@AutoConfigureMockMvc(addFilters = false)
class CharacterControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CharacterService characterService;

	@MockBean
	private JwtService jwtService;

	@MockBean
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private ObjectMapper objectMapper;

	private final String TEST_USERNAME = "testuser";

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		UserDetails userDetails = new User(TEST_USERNAME, "password", new ArrayList<>());
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@Test
	void createCharacter_Success() throws Exception {
		CharacterRequest characterRequest = new CharacterRequest();
		characterRequest.setName("Gandalf");

		Character character = new Character();
		character.setId(1L);
		character.setName("Gandalf");

		when(characterService.createCharacter(any(Character.class))).thenReturn(character);

		mockMvc.perform(post("/characters").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(characterRequest))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Gandalf"));
	}

	@Test
	void createCharacter_AccountNotFound() throws Exception {
		CharacterRequest characterRequest = new CharacterRequest();
		characterRequest.setName("Gandalf");

		when(characterService.createCharacter(any(Character.class)))
				.thenThrow(new IllegalArgumentException("Account not found"));

		Exception exception = assertThrows(ServletException.class, () -> {
			mockMvc.perform(post("/characters").contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(characterRequest))).andExpect(status().isBadRequest());
		});
	}

	@Test
	void editCharacterComment_Success() throws Exception {
		CommentRequest commentRequest = new CommentRequest();
		commentRequest.setComment("A wizard is never late.");

		Character character = new Character();
		character.setId(1L);
		character.setName("Gandalf");
		character.setComment("A wizard is never late.");

		when(characterService.editCharacterComment(anyLong(), any(String.class))).thenReturn(character);

		mockMvc.perform(put("/characters/1").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(commentRequest))).andExpect(status().isOk())
				.andExpect(jsonPath("$.comment").value("A wizard is never late."));
	}

	@Test
	void editCharacterComment_CharacterNotFound() throws Exception {
		CommentRequest commentRequest = new CommentRequest();
		commentRequest.setComment("A wizard is never late.");

		when(characterService.editCharacterComment(anyLong(), any(String.class)))
				.thenThrow(new IllegalArgumentException("Character not found"));
		Exception exception = assertThrows(ServletException.class, () -> {
			mockMvc.perform(put("/characters/1").contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(commentRequest)))
					.andExpect(status().isInternalServerError());
		});
	}

	@Test
	void markCharacterForDeletion_Success() throws Exception {
		doNothing().when(characterService).markCharacterForDeletion(anyLong());

		mockMvc.perform(delete("/characters/1")).andExpect(status().isOk());
	}

	@Test
	void markCharacterForDeletion_CharacterNotFound() throws Exception {
		doThrow(new IllegalArgumentException("Character not found")).when(characterService)
				.markCharacterForDeletion(anyLong());
		Exception exception = assertThrows(ServletException.class, () -> {
			mockMvc.perform(delete("/characters/1")).andExpect(status().isBadRequest());
		});
	}
}
