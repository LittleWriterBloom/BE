package com.pkg.littlewriter.controller;

import com.pkg.littlewriter.domain.generativeAi.BookInProgress;
import com.pkg.littlewriter.domain.generativeAi.BookInit;
import com.pkg.littlewriter.domain.model.BookEntity;
import com.pkg.littlewriter.domain.model.CharacterEntity;
import com.pkg.littlewriter.domain.model.PageEntity;
import com.pkg.littlewriter.domain.model.redis.BookInProgressRedis;
import com.pkg.littlewriter.dto.*;
import com.pkg.littlewriter.security.CustomUserDetails;
import com.pkg.littlewriter.service.*;
import com.pkg.littlewriter.service.redis.BookInProgressRedisService;
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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
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
    private BookInProgressRedisService bookInProgressRedisService2;
    @Autowired
    private S3BucketService s3BucketService;

    @PostMapping("/insight")
    public ResponseEntity<?> buildBook(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody BookInsightRequestDTO bookInsightRequestDTO) {
        BookInProgressRedis bookInProgressRedis = bookInProgressRedisService2.getByUserId(customUserDetails.getId());
        CharacterEntity characterEntity = characterService.getById(bookInProgressRedis.getCharacterId());
        CharacterDTO characterDTO = CharacterDTO.builder()
                .name(characterEntity.getName())
                .personality(characterEntity.getPersonality())
                .build();
        BookInProgress bookInProgress = BookInProgress.builder()
                .previousPages(bookInProgressRedis.getPreviousPages())
                .backgroundInfo(bookInProgressRedis.getBackgroundInfo())
                .currentCharacterActionInfo(bookInsightRequestDTO.getCharacterActionInfo())
                .currentContext(bookInsightRequestDTO.getUserContext())
                .characterDTO(characterDTO)
                .build();
        try {
            BookInsightDTO bookInsightDTO = bookService.generateHelperContents(bookInProgress);
            S3File temporaryUploadedFil = s3BucketService.uploadTemporaryFromUrl(bookInsightDTO.getTemporaryGeneratedImageUrl(), S3DirectoryEnum.TEMPORARY);
            bookInsightDTO.setTemporaryGeneratedImageUrl(temporaryUploadedFil.getUrl());
            PageDTO newPage = PageDTO.builder()
                    .characterActionInfo(bookInsightRequestDTO.getCharacterActionInfo())
                    .context(bookInsightDTO.getRefinedContext())
                    .backgroundImageUrl(bookInsightDTO.getTemporaryGeneratedImageUrl())
                    .pageNumber(bookInProgressRedis.getPreviousPages().size())
                    .build();
            BookInProgressRedis updatedBookInProgress = bookInProgressRedisService2.appendPageTo(customUserDetails.getId(), newPage);
            BookInitResponseDTO bookInitResponseDTO = BookInitResponseDTO.builder()
                    .bookInsight(bookInsightDTO)
                    .createdPages(updatedBookInProgress.getPreviousPages())
                    .build();
            ResponseDTO<BookInitResponseDTO> responseDTO = ResponseDTO.<BookInitResponseDTO>builder()
                    .data(List.of(bookInitResponseDTO))
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                    .error("cannot get response from openAi api")
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        }
    }

    @PostMapping("/insight2")
    public ResponseEntity<?> buildBook2(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody BookInsightRequestDTO bookInsightRequestDTO) {
        BookInProgressRedis bookInProgressRedis = bookInProgressRedisService2.getByUserId(customUserDetails.getId());
        CharacterEntity characterEntity = characterService.getById(bookInProgressRedis.getCharacterId());
        CharacterDTO characterDTO = CharacterDTO.builder()
                .name(characterEntity.getName())
                .personality(characterEntity.getPersonality())
                .appearanceKeywords(characterEntity.getAppearanceKeywords())
                .description(characterEntity.getUserDescription())
                .build();
        BookInProgress bookInProgress = BookInProgress.builder()
                .previousPages(bookInProgressRedis.getPreviousPages())
                .backgroundInfo(bookInProgressRedis.getBackgroundInfo())
                .currentCharacterActionInfo(bookInsightRequestDTO.getCharacterActionInfo())
                .currentContext(bookInsightRequestDTO.getUserContext())
                .characterDTO(characterDTO)
                .build();
        try {
            BookInsightDTO bookInsightDTO = bookService.generateHelperContents2(bookInProgress);
            PageDTO newPage = PageDTO.builder()
                    .characterActionInfo(bookInsightRequestDTO.getCharacterActionInfo())
                    .context(bookInsightDTO.getRefinedContext())
                    .backgroundImageUrl(bookInsightDTO.getTemporaryGeneratedImageUrl())
                    .pageNumber(bookInProgressRedis.getPreviousPages().size())
                    .build();
            BookInProgressRedis updatedBookInProgress = bookInProgressRedisService2.appendPageTo(customUserDetails.getId(), newPage);
            BookInitResponseDTO bookInitResponseDTO = BookInitResponseDTO.builder()
                    .bookInsight(bookInsightDTO)
                    .createdPages(updatedBookInProgress.getPreviousPages())
                    .build();
            ResponseDTO<BookInitResponseDTO> responseDTO = ResponseDTO.<BookInitResponseDTO>builder()
                    .data(List.of(bookInitResponseDTO))
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (RuntimeException e) {
            ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                    .error("cannot get response from openAi api")
                    .build();
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> createBook(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody BookCompleteRequestDTO bookCompleteRequestDTO) {
        BookInProgressRedis bookInProgressRedis = bookInProgressRedisService2.getByUserId(customUserDetails.getId());
        BookEntity bookEntity = BookEntity.builder()
                .id(bookInProgressRedis.getBookId())
                .characterId(bookInProgressRedis.getCharacterId())
                .title(bookCompleteRequestDTO.getTitle())
                .userId(customUserDetails.getId())
                .createDate(Date.from(Instant.now()))
                .bookColor(bookCompleteRequestDTO.getBookColor())
                .author(bookCompleteRequestDTO.getAuthor())
                .build();
        bookService.createEmptyBook(bookEntity);
        int[] pageNumber = {0};
        List<PageEntity> pages = bookInProgressRedis.getPreviousPages()
                .stream()
                .map(pageDTO -> PageEntity.builder()
                        .actionInfo(pageDTO.getCharacterActionInfo())
                        .context(pageDTO.getContext())
                        .bookId(bookInProgressRedis.getBookId())
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
                        .author(book.getAuthor())
                        .bookColor(book.getBookColor())
                        .build())
                .collect(Collectors.toList());
        ResponseDTO<BookDTO> responseDTO = ResponseDTO.<BookDTO>builder()
                .data(bookDTOS)
                .build();
        bookInProgressRedisService2.deleteByUserId(customUserDetails.getId());
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/save2")
    public ResponseEntity<?> createBook2(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody BookCompleteRequestDTO bookCompleteRequestDTO) throws IOException, InterruptedException {
        BookInProgressRedis bookInProgressRedis = bookInProgressRedisService2.getByUserId(customUserDetails.getId());
        BookEntity bookEntity = BookEntity.builder()
                .id(bookInProgressRedis.getBookId())
                .characterId(bookInProgressRedis.getCharacterId())
                .title(bookCompleteRequestDTO.getTitle())
                .userId(customUserDetails.getId())
                .createDate(Date.from(Instant.now()))
                .bookColor(bookCompleteRequestDTO.getBookColor())
                .author(bookCompleteRequestDTO.getAuthor())
                .build();
        boolean isUrlDoneProcessing = isAllAvailable(bookInProgressRedis.getPreviousPages());
        while(!isUrlDoneProcessing) {
            isUrlDoneProcessing = isAllAvailable(bookInProgressRedis.getPreviousPages());
        }
        bookService.createEmptyBook(bookEntity);
        int[] pageNumber = {0};
        List<PageEntity> pages = bookInProgressRedis.getPreviousPages()
                .stream()
                .map(pageDTO -> PageEntity.builder()
                        .actionInfo(pageDTO.getCharacterActionInfo())
                        .context(pageDTO.getContext())
                        .bookId(bookInProgressRedis.getBookId())
                        .imageUrl(pageDTO.getBackgroundImageUrl())
                        .pageNumber(pageNumber[0]++).build())
                .toList();
        pages.forEach(page -> {
            S3File backgroundImageFile = null;
            try {
                backgroundImageFile = s3BucketService.uploadTemporaryFromUrl(page.getImageUrl(), S3DirectoryEnum.BOOK);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
                        .author(book.getAuthor())
                        .bookColor(book.getBookColor())
                        .build())
                .collect(Collectors.toList());
        ResponseDTO<BookDTO> responseDTO = ResponseDTO.<BookDTO>builder()
                .data(bookDTOS)
                .build();
        bookInProgressRedisService2.deleteByUserId(customUserDetails.getId());
        return ResponseEntity.ok().body(responseDTO);
    }

    private boolean isAllAvailable(List<PageDTO> pageDTOS) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        for(PageDTO page : pageDTOS) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(page.getBackgroundImageUrl()))
                    .GET()
                    .build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            System.out.println("status code: " + response.statusCode());
            if(response.statusCode() == 404) {
                return false;
            }
        }
        return true;
    }

    @PostMapping("init")
    public ResponseEntity<?> initBook(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody BookInitRequestDTO initRequestDTO) throws IOException {
        String bookInProgressId = UUID.randomUUID().toString();
        BookInProgressRedis bookInProgressRedis = BookInProgressRedis.builder()
                .characterId(initRequestDTO.getCharacterId())
                .bookId(bookInProgressId)
                .userId(customUserDetails.getId())
                .backgroundInfo(initRequestDTO.getBackgroundInfo())
                .build();
        try {
            CharacterEntity characterEntity = characterService.getById(initRequestDTO.getCharacterId());
            BookInit bookInit = BookInit.builder()
                    .backgroundInfo(initRequestDTO.getBackgroundInfo())
                    .currentContext(initRequestDTO.getFirstContext())
                    .characterDTO(CharacterDTO.builder()
                            .name(characterEntity.getName())
                            .personality(characterEntity.getPersonality())
                            .build())
                    .build();
            BookInsightDTO bookInsightDTO = bookService.generateHelperContents(bookInit);
            S3File temporaryUploadedFile = s3BucketService.uploadTemporaryFromUrl(bookInsightDTO.getTemporaryGeneratedImageUrl(), S3DirectoryEnum.TEMPORARY);
            bookInsightDTO.setTemporaryGeneratedImageUrl(temporaryUploadedFile.getUrl());
            PageDTO page = PageDTO.builder()
                    .context(bookInsightDTO.getRefinedContext())
                    .backgroundImageUrl(bookInsightDTO.getTemporaryGeneratedImageUrl())
                    .characterActionInfo("").build();
            bookInProgressRedis.setPreviousPages(List.of(page));
            bookInProgressRedisService2.put(bookInProgressRedis);
            BookInitResponseDTO bookInitResponseDTO = BookInitResponseDTO.builder()
                    .bookInsight(bookInsightDTO)
                    .createdPages(bookInProgressRedis.getPreviousPages())
                    .build();
            ResponseDTO<BookInitResponseDTO> responseDTO = ResponseDTO.<BookInitResponseDTO>builder()
                    .data(List.of(bookInitResponseDTO))
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (RuntimeException e) {
            log.warn(e.getMessage());
            ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                    .error("cannot get response from openAi api")
                    .build();
            return ResponseEntity.internalServerError().body(responseDTO);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("init2")
    public ResponseEntity<?> initBook2(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody BookInitRequestDTO initRequestDTO) throws IOException {
        String bookInProgressId = UUID.randomUUID().toString();
        BookInProgressRedis bookInProgressRedis = BookInProgressRedis.builder()
                .characterId(initRequestDTO.getCharacterId())
                .bookId(bookInProgressId)
                .userId(customUserDetails.getId())
                .backgroundInfo(initRequestDTO.getBackgroundInfo())
                .build();
        try {
            CharacterEntity characterEntity = characterService.getById(initRequestDTO.getCharacterId());
            BookInit bookInit = BookInit.builder()
                    .backgroundInfo(initRequestDTO.getBackgroundInfo())
                    .currentContext(initRequestDTO.getFirstContext())
                    .characterDTO(new CharacterDTO(characterEntity))
                    .build();
            BookInsightDTO bookInsightDTO = bookService.generateHelperContents2(bookInit);
//            S3File temporaryUploadedFile = s3BucketService.uploadTemporaryFromUrl(bookInsightDTO.getTemporaryGeneratedImageUrl(), S3DirectoryEnum.TEMPORARY);
//            bookInsightDTO.setTemporaryGeneratedImageUrl(temporaryUploadedFile.getUrl());
            PageDTO page = PageDTO.builder()
                    .context(bookInsightDTO.getRefinedContext())
                    .backgroundImageUrl(bookInsightDTO.getTemporaryGeneratedImageUrl())
                    .characterActionInfo("").build();
            bookInProgressRedis.setPreviousPages(List.of(page));
            bookInProgressRedisService2.put(bookInProgressRedis);
            BookInitResponseDTO bookInitResponseDTO = BookInitResponseDTO.builder()
                    .bookInsight(bookInsightDTO)
                    .createdPages(bookInProgressRedis.getPreviousPages())
                    .build();
            ResponseDTO<BookInitResponseDTO> responseDTO = ResponseDTO.<BookInitResponseDTO>builder()
                    .data(List.of(bookInitResponseDTO))
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (RuntimeException e) {
            log.warn(e.getMessage());
            ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                    .error("cannot get response from openAi api")
                    .build();
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    @PostMapping("/dictionary")
    public ResponseEntity<?> generateWordQuestionAnswer(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody WordQuestionDTO wordQuestionDTO) {
        try {
            String answer = bookService.generateWordQuestionAnswer(wordQuestionDTO);
            ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                    .data(List.of(answer))
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (RuntimeException e) {
            ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                    .error("cannot get response from openAi api")
                    .build();
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }
}
