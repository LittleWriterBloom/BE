package com.pkg.littlewriter.persistence;

import com.pkg.littlewriter.domain.model.CharacterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterRepository extends JpaRepository<CharacterEntity, Long> {
    List<CharacterEntity> findByMemberId(Long memberId);
}
