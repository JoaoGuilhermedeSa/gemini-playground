package com.example.charactercreation.controller;

import com.example.charactercreation.dto.CharacterRequest;
import com.example.charactercreation.dto.CommentRequest;
import com.example.charactercreation.model.Character;
import com.example.charactercreation.service.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/characters")
public class CharacterController {

    @Autowired
    private CharacterService characterService;

    @PostMapping
    public Character createCharacter(@RequestBody CharacterRequest characterRequest) {
        Character character = new Character();
        character.setName(characterRequest.getName());
        character.setVocation(characterRequest.getVocation());
        character.setCharacterClass(characterRequest.getCharacterClass());
        return characterService.createCharacter(character);
    }

    @PutMapping("/{characterId}")
    public Character editCharacterComment(@PathVariable Long characterId, @RequestBody CommentRequest commentRequest) {
        return characterService.editCharacterComment(characterId, commentRequest.getComment());
    }

    @DeleteMapping("/{characterId}")
    public void markCharacterForDeletion(@PathVariable Long characterId) {
        characterService.markCharacterForDeletion(characterId);
    }
}
