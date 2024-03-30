package com.pkg.littlewriter.persistence;

import com.pkg.littlewriter.domain.model.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookPageRepository extends JpaRepository<BookEntity, String> {
    Optional<BookEntity> findById(String bookId);
    List<BookEntity> findAllByUserId(Long userId);
    List<BookEntity> findAllByCharacterId(Long characterId);
}
