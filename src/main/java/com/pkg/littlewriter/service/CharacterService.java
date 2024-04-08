package com.pkg.littlewriter.service;

import com.pkg.littlewriter.domain.model.CharacterEntity;
import com.pkg.littlewriter.persistence.CharacterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CharacterService {

    @Autowired
    CharacterRepository characterRepository;

    public CharacterEntity create(CharacterEntity characterEntity) {
        validate(characterEntity);
        characterRepository.save(characterEntity);
        return characterRepository.findById(characterEntity.getId()).orElseThrow();
    }

    public List<CharacterEntity> update(CharacterEntity characterEntity) {
        validate(characterEntity);
        Optional<CharacterEntity> original = characterRepository.findById(characterEntity.getId());
        original.ifPresent((updateEntity) -> {
            updateEntity.setName(characterEntity.getName());
            updateEntity.setPersonality(characterEntity.getPersonality());
            updateEntity.setImageUrl(characterEntity.getImageUrl());
        });
        return retrieveByUserId(characterEntity.getMemberId());
    }

    public List<CharacterEntity> retrieveByUserId(Long userId) {
        return characterRepository.findByMemberId(userId);
    }

    public List<CharacterEntity> delete(CharacterEntity characterEntity) {
        validate(characterEntity);
        try {
            characterRepository.delete(characterEntity);
        } catch (Exception e) {
            log.error("err deleting characterEntity", characterEntity.getId(), e);
            throw new RuntimeException("err deleting characterEntity" + characterEntity.getId());
        }
        return retrieveByUserId(characterEntity.getMemberId());
    }

    private void validate(CharacterEntity characterEntity) {
        if ((characterEntity == null)) {
            log.warn("character Entity is null");
            throw new RuntimeException("character entity is null");
        }
        if ((characterEntity.getMemberId() == null)) {
            log.warn("user_id is null");
            throw new RuntimeException("user_id is null");
        }
    }

    public CharacterEntity getById(Long characterId) {
        return characterRepository.findById(characterId)
                .orElseThrow(()->new IllegalArgumentException("cant find by character_id"));
    }
}
