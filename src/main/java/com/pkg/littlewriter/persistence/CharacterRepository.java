package com.pkg.littlewriter.persistence;

import com.pkg.littlewriter.domain.model.CharacterEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CharacterRepository extends JpaRepository<CharacterEntity, Long> {
//    List<CharacterEntity> findByMemberId(Long memberId);
    Page<CharacterEntity> findByMemberId(Long memberId, Pageable pageable);
}
