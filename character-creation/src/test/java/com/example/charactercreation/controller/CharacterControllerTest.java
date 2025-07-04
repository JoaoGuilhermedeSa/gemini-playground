package com.example.charactercreation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import com.example.charactercreation.dto.CharacterRequest;
import com.example.charactercreation.dto.CommentRequest;
import com.example.charactercreation.model.Character;
import com.example.charactercreation.service.CharacterService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CharacterController.class)
@AutoConfigureMockMvc(addFilters = false)
class CharacterControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CharacterService characterService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void createCharacter_Success() throws Exception {
		Long accountId = 1L;
		CharacterRequest characterRequest = new CharacterRequest();
		characterRequest.setName("TestChar");
		characterRequest.setVocation("Warrior");
		characterRequest.setCharacterClass("Knight");

		Character character = new Character();
		character.setId(1L);
		character.setName("TestChar");

		when(characterService.createCharacter(anyLong(), any(Character.class))).thenReturn(character);

		mockMvc.perform(post("/characters/{accountId}", accountId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(characterRequest))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("TestChar"));
	}

	@Test
	void createCharacter_AccountNotFound() throws Exception {
		Long accountId = 1L;
		CharacterRequest characterRequest = new CharacterRequest();
		characterRequest.setName("TestChar");

		when(characterService.createCharacter(anyLong(), any(Character.class)))
				.thenThrow(new IllegalArgumentException("Account not found"));

		jakarta.servlet.ServletException thrown = Assertions.assertThrows(jakarta.servlet.ServletException.class,
				() -> mockMvc
						.perform(post("/characters/{accountId}", accountId).contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(characterRequest)))
						.andExpect(status().isBadRequest()));
	}

	@Test
	void editCharacterComment_Success() throws Exception {
		Long characterId = 1L;
		CommentRequest commentRequest = new CommentRequest();
		commentRequest.setComment("New comment");

		Character character = new Character();
		character.setId(characterId);
		character.setComment("New comment");

		when(characterService.editCharacterComment(anyLong(), anyString())).thenReturn(character);

		mockMvc.perform(put("/characters/{characterId}", characterId).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(commentRequest))).andExpect(status().isOk())
				.andExpect(jsonPath("$.comment").value("New comment"));
	}

	@Test
	void editCharacterComment_CharacterNotFound() throws Exception {
		Long characterId = 1L;
		CommentRequest commentRequest = new CommentRequest();
		commentRequest.setComment("New comment");

		when(characterService.editCharacterComment(anyLong(), anyString()))
				.thenThrow(new IllegalArgumentException("Character not found"));

		jakarta.servlet.ServletException thrown = Assertions.assertThrows(jakarta.servlet.ServletException.class,
				() -> mockMvc
						.perform(put("/characters/{characterId}", characterId).contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(commentRequest)))
						.andExpect(status().isBadRequest()));
	}

	@Test
	void markCharacterForDeletion_Success() throws Exception {
		Long characterId = 1L;

		doNothing().when(characterService).markCharacterForDeletion(anyLong());

		mockMvc.perform(delete("/characters/{characterId}", characterId)).andExpect(status().isOk());
	}

	@Test
	void markCharacterForDeletion_CharacterNotFound() throws Exception {
		Long characterId = 1L;

		doThrow(new IllegalArgumentException("Character not found")).when(characterService)
				.markCharacterForDeletion(anyLong());

		jakarta.servlet.ServletException thrown = Assertions.assertThrows(jakarta.servlet.ServletException.class,
				() -> mockMvc.perform(delete("/characters/{characterId}", characterId))
						.andExpect(status().isBadRequest()));
	}
}