package com.example.charactercreation.repository;

import com.example.charactercreation.model.Character;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CharacterRepository extends JpaRepository<Character, Long> {
    List<Character> findByDeletionDateBefore(LocalDate date);
}
