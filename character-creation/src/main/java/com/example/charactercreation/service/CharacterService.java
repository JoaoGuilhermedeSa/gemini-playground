package com.example.charactercreation.service;

import com.example.charactercreation.model.Account;
import com.example.charactercreation.model.Character;
import com.example.charactercreation.repository.AccountRepository;
import com.example.charactercreation.repository.CharacterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CharacterService {

    @Autowired
    private CharacterRepository characterRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Character createCharacter(Long accountId, Character character) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new IllegalArgumentException("Account not found"));
        if (account.getCharacters().size() >= 20) {
            throw new IllegalStateException("Maximum number of characters reached for this account.");
        }
        character.setAccount(account);
        return characterRepository.save(character);
    }

    public Character editCharacterComment(Long characterId, String comment) {
        Character character = characterRepository.findById(characterId).orElseThrow(() -> new IllegalArgumentException("Character not found"));
        character.setComment(comment);
        return characterRepository.save(character);
    }

    public void markCharacterForDeletion(Long characterId) {
        Character character = characterRepository.findById(characterId).orElseThrow(() -> new IllegalArgumentException("Character not found"));
        character.setDeletionDate(LocalDate.now().plusDays(30));
        characterRepository.save(character);
    }

    @Scheduled(cron = "0 0 0 * * ?") // Run every day at midnight
    public void deleteMarkedCharacters() {
        List<Character> charactersToDelete = characterRepository.findByDeletionDateBefore(LocalDate.now());
        characterRepository.deleteAll(charactersToDelete);
    }
}
