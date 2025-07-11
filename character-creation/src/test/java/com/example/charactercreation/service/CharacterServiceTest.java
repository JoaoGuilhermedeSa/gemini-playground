package com.example.charactercreation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.charactercreation.model.Account;
import com.example.charactercreation.model.Character;
import com.example.charactercreation.repository.AccountRepository;
import com.example.charactercreation.repository.CharacterRepository;

class CharacterServiceTest {

    @Mock
    private CharacterRepository characterRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private CharacterService characterService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
		UserDetails userDetails = new User("testuser", "password", new ArrayList<>());
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void createCharacter_Success() {
        String username = "testuser";
        Account account = new Account();
        account.setUsername(username);
        account.setCharacters(new ArrayList<>());

        Character character = new Character();
        character.setName("TestChar");

        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(account));
        when(characterRepository.save(any(Character.class))).thenAnswer(invocation -> {
            Character savedChar = invocation.getArgument(0);
            savedChar.setId(1L);
            return savedChar;
        });

        Character createdCharacter = characterService.createCharacter(character);

        assertNotNull(createdCharacter);
        assertEquals("TestChar", createdCharacter.getName());
        assertEquals(account, createdCharacter.getAccount());
        verify(accountRepository, times(1)).findByUsername(username);
        verify(characterRepository, times(1)).save(any(Character.class));
    }

    @Test
    void createCharacter_AccountNotFound() {
        String username = "testuser";
        Character character = new Character();

        when(accountRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> characterService.createCharacter(character));
        verify(accountRepository, times(1)).findByUsername(username);
        verify(characterRepository, never()).save(any(Character.class));
    }

    @Test
    void createCharacter_MaxCharactersReached() {
        String username = "testuser";
        Account account = new Account();
        account.setUsername(username);
        List<Character> characters = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            characters.add(new Character());
        }
        account.setCharacters(characters);

        Character character = new Character();

        when(accountRepository.findByUsername(username)).thenReturn(Optional.of(account));

        assertThrows(IllegalStateException.class, () -> characterService.createCharacter(character));
        verify(accountRepository, times(1)).findByUsername(username);
        verify(characterRepository, never()).save(any(Character.class));
    }

    @Test
    void editCharacterComment_Success() {
        String username = "testuser";
        Long characterId = 1L;
        String newComment = "New comment";
        Account account = new Account();
        account.setUsername(username);
        Character character = new Character();
        character.setId(characterId);
        character.setComment("Old comment");
        character.setAccount(account);

        when(characterRepository.findById(characterId)).thenReturn(Optional.of(character));
        when(characterRepository.save(any(Character.class))).thenReturn(character);

        Character updatedCharacter = characterService.editCharacterComment(characterId, newComment);

        assertNotNull(updatedCharacter);
        assertEquals(newComment, updatedCharacter.getComment());
        verify(characterRepository, times(1)).findById(characterId);
        verify(characterRepository, times(1)).save(any(Character.class));
    }

    @Test
    void editCharacterComment_CharacterNotFound() {
        String username = "testuser";
        Long characterId = 1L;
        String newComment = "New comment";

        when(characterRepository.findById(characterId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> characterService.editCharacterComment(characterId, newComment));
        verify(characterRepository, times(1)).findById(characterId);
        verify(characterRepository, never()).save(any(Character.class));
    }

    @Test
    void editCharacterComment_CharacterDoesNotBelongToUser() {
        String username = "testuser";
        Long characterId = 1L;
        String newComment = "New comment";
        Account otherAccount = new Account();
        otherAccount.setUsername("otheruser");
        Character character = new Character();
        character.setId(characterId);
        character.setAccount(otherAccount);

        when(characterRepository.findById(characterId)).thenReturn(Optional.of(character));

        assertThrows(IllegalArgumentException.class, () -> characterService.editCharacterComment(characterId, newComment));
        verify(characterRepository, times(1)).findById(characterId);
        verify(characterRepository, never()).save(any(Character.class));
    }

    @Test
    void markCharacterForDeletion_Success() {
        String username = "testuser";
        Long characterId = 1L;
        Account account = new Account();
        account.setUsername(username);
        Character character = new Character();
        character.setId(characterId);
        character.setAccount(account);

        when(characterRepository.findById(characterId)).thenReturn(Optional.of(character));
        when(characterRepository.save(any(Character.class))).thenReturn(character);

        characterService.markCharacterForDeletion(characterId);

        assertNotNull(character.getDeletionDate());
        verify(characterRepository, times(1)).findById(characterId);
        verify(characterRepository, times(1)).save(any(Character.class));
    }

    @Test
    void markCharacterForDeletion_CharacterNotFound() {
        String username = "testuser";
        Long characterId = 1L;

        when(characterRepository.findById(characterId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> characterService.markCharacterForDeletion(characterId));
        verify(characterRepository, times(1)).findById(characterId);
        verify(characterRepository, never()).save(any(Character.class));
    }

    @Test
    void markCharacterForDeletion_CharacterDoesNotBelongToUser() {
        String username = "testuser";
        Long characterId = 1L;
        Account otherAccount = new Account();
        otherAccount.setUsername("otheruser");
        Character character = new Character();
        character.setId(characterId);
        character.setAccount(otherAccount);

        when(characterRepository.findById(characterId)).thenReturn(Optional.of(character));

        assertThrows(IllegalArgumentException.class, () -> characterService.markCharacterForDeletion(characterId));
        verify(characterRepository, times(1)).findById(characterId);
        verify(characterRepository, never()).save(any(Character.class));
    }

    @Test
    void deleteMarkedCharacters() {
        Character char1 = new Character();
        char1.setId(1L);
        char1.setDeletionDate(LocalDate.now().minusDays(1));

        Character char2 = new Character();
        char2.setId(2L);
        char2.setDeletionDate(LocalDate.now().plusDays(1));

        when(characterRepository.findByDeletionDateBefore(LocalDate.now()))
                .thenReturn(Arrays.asList(char1));

        characterService.deleteMarkedCharacters();

        verify(characterRepository, times(1)).findByDeletionDateBefore(LocalDate.now());
        verify(characterRepository, times(1)).deleteAll(argThat(list -> ((java.util.List<Character>) list).size() == 1 && ((java.util.List<Character>) list).contains(char1)));
    }
}