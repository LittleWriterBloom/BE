package com.pkg.littlewriter.controller;

import com.pkg.littlewriter.domain.generativeAi.BookInProgress;
import com.pkg.littlewriter.domain.model.BookEntity;
import com.pkg.littlewriter.domain.model.CharacterEntity;
import com.pkg.littlewriter.domain.model.PageEntity;
import com.pkg.littlewriter.domain.model.redis.BookInProgressIdCache;
import com.pkg.littlewriter.dto.*;
import com.pkg.littlewriter.security.CustomUserDetails;
import com.pkg.littlewriter.service.*;
import com.pkg.littlewriter.utils.S3DirectoryEnum;
import com.pkg.littlewriter.utils.S3File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/books/builder")
public class BookController {
    @Autowired
    private BookService bookService;
    @Autowired
    private BookPageService bookPageService;
    @Autowired
    private CharacterService characterService;
    @Autowired
    private BookInProgressRedisService bookInProgressRedisService;
    @Autowired
    private S3BucketService s3BucketService;

    @PostMapping("/{bookId}/insight")
    public ResponseEntity<?> generateBookInProgress(@PathVariable String bookId, @AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody PageProgressRequestDTO pageProgressRequestDTO) {
        if (!bookInProgressRedisService.existsByUserId(customUserDetails.getId())) {
            return ResponseEntity.badRequest().build();
        }
        BookInProgressIdCache cache = bookInProgressRedisService.getByUserId(customUserDetails.getId());
        String currentBookId = cache.getBookId();
        if (!currentBookId.equals(bookId)) {
            return ResponseEntity.badRequest().build();
        }
        CharacterEntity characterEntity = characterService.getById(pageProgressRequestDTO.getCharacterId());
        CharacterDTO characterDTO = CharacterDTO.builder()
                .name(characterEntity.getName())
                .personality(characterEntity.getPersonality())
                .build();
        BookInProgress bookInProgress = BookInProgress.builder()
                .previousPages(pageProgressRequestDTO.getPreviousPages())
                .backgroundInfo(pageProgressRequestDTO.getBackgroundInfo())
                .currentCharacterActionInfo(pageProgressRequestDTO.getCharacterActionInfo())
                .currentContext(pageProgressRequestDTO.getUserContext())
                .characterDTO(characterDTO)
                .build();
        try {
            QuestionAndImageDTO questionAndImageDTO = bookService.generateHelperContents(bookInProgress);
            S3File temporaryUploadedFil = s3BucketService.uploadTemporaryFromUrl(questionAndImageDTO.getTemporaryGeneratedImageUrl(), S3DirectoryEnum.TEMPORARY);
            questionAndImageDTO.setTemporaryGeneratedImageUrl(temporaryUploadedFil.getUrl());
            ResponseDTO<QuestionAndImageDTO> responseDTO = ResponseDTO.<QuestionAndImageDTO>builder()
                    .data(List.of(questionAndImageDTO))
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                    .error("cannot get response from openAi api")
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        }
    }

    @PostMapping("{bookId}/save")
    public ResponseEntity<?> createBook(@PathVariable String bookId, @AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody BookCreationRequestDTO bookCreationRequestDTO) {
        if (!bookInProgressRedisService.existsByUserId(customUserDetails.getId())) {
            return ResponseEntity.badRequest().build();
        }
        BookInProgressIdCache cache = bookInProgressRedisService.getByUserId(customUserDetails.getId());
        String currentBookId = cache.getBookId();
        if (!currentBookId.equals(bookId)) {
            return ResponseEntity.badRequest().build();
        }
        BookEntity bookEntity = BookEntity.builder()
                .id(currentBookId)
                .characterId(bookCreationRequestDTO.getCharacterId())
                .title(bookCreationRequestDTO.getTitle())
                .userId(customUserDetails.getId())
                .createDate(Date.from(Instant.now()))
                .build();
        bookService.createEmptyBook(bookEntity);
        int[] pageNumber = {0};
        List<PageEntity> pages = bookCreationRequestDTO.getPages()
                .stream()
                .map(pageDTO -> PageEntity.builder()
                        .actionInfo(pageDTO.getCharacterActionInfo())
                        .context(pageDTO.getContext())
                        .bookId(currentBookId)
                        .imageUrl(pageDTO.getBackgroundImageUrl())
                        .pageNumber(pageNumber[0]++).build())
                .toList();
        pages.forEach(page -> {
            S3File backgroundImageFile = s3BucketService.copyTo(new S3File(page.getImageUrl()), S3DirectoryEnum.BOOK);
            page.setImageUrl(backgroundImageFile.getUrl());
            bookPageService.createPage(page);
        });
        List<BookDTO> bookDTOS = bookService.getAllByUserId(customUserDetails.getId())
                .stream()
                .map(book -> BookDTO.builder()
                        .title(book.getTitle())
                        .id(book.getId())
                        .userId(book.getUserId())
                        .characterId(book.getCharacterId())
                        .build())
                .collect(Collectors.toList());
        ResponseDTO<BookDTO> responseDTO = ResponseDTO.<BookDTO>builder()
                .data(bookDTOS)
                .build();
        bookInProgressRedisService.delete(cache);
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("init")
    public ResponseEntity<?> generateBackgroundImage(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody BookInitRequestDTO initRequestDTO) {
        String bookInProgressId = UUID.randomUUID().toString();
        BookInProgressIdCache bookInProgressIdCache = BookInProgressIdCache.builder()
                .bookId(bookInProgressId)
                .userId(customUserDetails.getId().toString())
                .build();
        bookInProgressRedisService.save(bookInProgressIdCache);
        try {
            String generatedImageUrl = bookService.generateImageUrl(initRequestDTO.getBackgroundInfo());
            S3File temporaryUploadedFile = s3BucketService.uploadTemporaryFromUrl(generatedImageUrl, S3DirectoryEnum.TEMPORARY);
            BookInitResponseDTO bookInitResponseDTO = BookInitResponseDTO.builder()
                    .bookId(bookInProgressId)
                    .imageUrl(temporaryUploadedFile.getUrl())
                    .build();
            ResponseDTO<BookInitResponseDTO> responseDTO = ResponseDTO.<BookInitResponseDTO>builder()
                    .data(List.of(bookInitResponseDTO))
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                    .error("cannot get response from openAi api")
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        }
    }
}
