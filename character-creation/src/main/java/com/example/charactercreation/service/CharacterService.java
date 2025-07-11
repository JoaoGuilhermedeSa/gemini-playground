package com.example.charactercreation.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.charactercreation.model.Account;
import com.example.charactercreation.model.Character;
import com.example.charactercreation.repository.AccountRepository;
import com.example.charactercreation.repository.CharacterRepository;

@Service
public class CharacterService {

	@Autowired
	private CharacterRepository characterRepository;

	@Autowired
	private AccountRepository accountRepository;

	public Character createCharacter(Character character) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Account account = accountRepository.findByUsername(username).orElseThrow();
		if (account.getCharacters().size() >= 20) {
			throw new IllegalStateException("Maximum number of characters reached for this account.");
		}
		character.setAccount(account);
		return characterRepository.save(character);
	}

	public Character editCharacterComment(Long characterId, String comment) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Character character = characterRepository.findById(characterId)
				.orElseThrow(() -> new IllegalArgumentException("Character not found"));
		if (!character.getAccount().getUsername().equals(username)) {
			throw new IllegalArgumentException("Character does not belong to the authenticated user.");
		}
		character.setComment(comment);
		return characterRepository.save(character);
	}

	public void markCharacterForDeletion(Long characterId) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Character character = characterRepository.findById(characterId)
				.orElseThrow(() -> new IllegalArgumentException("Character not found"));
		if (!character.getAccount().getUsername().equals(username)) {
			throw new IllegalArgumentException("Character does not belong to the authenticated user.");
		}
		character.setDeletionDate(LocalDate.now().plusDays(30));
		characterRepository.save(character);
	}

	@Scheduled(cron = "0 0 0 * * ?") // Run every day at midnight
	public void deleteMarkedCharacters() {
		List<Character> charactersToDelete = characterRepository.findByDeletionDateBefore(LocalDate.now());
		characterRepository.deleteAll(charactersToDelete);
	}
}
