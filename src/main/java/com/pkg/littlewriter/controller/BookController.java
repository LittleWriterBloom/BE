package com.pkg.littlewriter.controller;

import com.pkg.littlewriter.domain.generativeAi.AiBookCreationHelper;
import com.pkg.littlewriter.domain.generativeAi.BookInProgress;
import com.pkg.littlewriter.domain.generativeAi.BookInit;
import com.pkg.littlewriter.domain.generativeAi.openAi.OpenAiException;
import com.pkg.littlewriter.domain.generativeAi.stableDiffusion.StableDiffusionException;
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
    private BookInProgressRedisService bookInProgressRedisService;
    @Autowired
    private S3BucketService s3BucketService;
    @Autowired
    private AiBookCreationHelper aiBookCreationHelper;

    @PostMapping("/insight")
    public ResponseEntity<?> buildBook(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody BookInsightRequestDTO bookInsightRequestDTO) {
        BookInProgressRedis bookInProgressRedis = bookInProgressRedisService.getByUserId(customUserDetails.getId());
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
            BookInsightDTO bookInsightDTO = aiBookCreationHelper.generateBookInsightDalle(bookInProgress);
            S3File temporaryUploadedFil = s3BucketService.uploadTemporaryFromUrl(bookInsightDTO.getTemporaryGeneratedImageUrl(), S3DirectoryEnum.TEMPORARY);
            bookInsightDTO.setTemporaryGeneratedImageUrl(temporaryUploadedFil.getUrl());
            PageDTO newPage = PageDTO.builder()
                    .characterActionInfo(bookInsightRequestDTO.getCharacterActionInfo())
                    .context(bookInsightDTO.getRefinedContext())
                    .backgroundImageUrl(bookInsightDTO.getTemporaryGeneratedImageUrl())
                    .pageNumber(bookInProgressRedis.getPreviousPages().size())
                    .build();
            BookInProgressRedis updatedBookInProgress = bookInProgressRedisService.appendPageTo(customUserDetails.getId(), newPage);
            BookInsightResponseDTO bookInitResponseDTO = BookInsightResponseDTO.builder()
                    .bookInsight(bookInsightDTO)
                    .createdPages(updatedBookInProgress.getPreviousPages())
                    .storyLength(updatedBookInProgress.getStoryLength())
                    .build();
            ResponseDTO<BookInsightResponseDTO> responseDTO = ResponseDTO.<BookInsightResponseDTO>builder()
                    .data(List.of(bookInitResponseDTO))
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (IOException | OpenAiException e) {
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
        BookInProgressRedis bookInProgressRedis = bookInProgressRedisService.getByUserId(customUserDetails.getId());
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
            BookInsightDTO bookInsightDTO = aiBookCreationHelper.generateBookInsightStableDiffusion(bookInProgress);
            PageDTO newPage = PageDTO.builder()
                    .characterActionInfo(bookInsightRequestDTO.getCharacterActionInfo())
                    .context(bookInsightDTO.getRefinedContext())
                    .backgroundImageUrl(bookInsightDTO.getTemporaryGeneratedImageUrl())
                    .pageNumber(bookInProgressRedis.getPreviousPages().size())
                    .build();
            BookInProgressRedis updatedBookInProgress = bookInProgressRedisService.appendPageTo(customUserDetails.getId(), newPage);
            BookInsightResponseDTO bookInitResponseDTO = BookInsightResponseDTO.builder()
                    .bookInsight(bookInsightDTO)
                    .createdPages(updatedBookInProgress.getPreviousPages())
                    .storyLength(updatedBookInProgress.getStoryLength())
                    .build();
            ResponseDTO<BookInsightResponseDTO> responseDTO = ResponseDTO.<BookInsightResponseDTO>builder()
                    .data(List.of(bookInitResponseDTO))
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (RuntimeException | OpenAiException | StableDiffusionException e) {
            ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                    .error("cannot get response from openAi api")
                    .build();
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> createBook(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody BookCompleteRequestDTO bookCompleteRequestDTO) {
        BookInProgressRedis bookInProgressRedis = bookInProgressRedisService.getByUserId(customUserDetails.getId());
        BookEntity bookEntity = BookEntity.builder()
                .id(bookInProgressRedis.getBookId())
                .characterId(bookInProgressRedis.getCharacterId())
                .title(bookCompleteRequestDTO.getTitle())
                .userId(customUserDetails.getId())
                .createDate(Date.from(Instant.now()))
                .bookColor(bookCompleteRequestDTO.getBookColor())
                .author(bookCompleteRequestDTO.getAuthor())
                .storyLength(bookInProgressRedis.getStoryLength())
                .coverImageUrl(bookInProgressRedis.getPreviousPages().get(0).getBackgroundImageUrl())
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
        CharacterDTO characterDTO = new CharacterDTO(characterService.getById(bookInProgressRedis.getCharacterId()));
        List<PageDTO> pageDTOs = bookPageService.getAllById(bookEntity.getId())
                .stream()
                .map(page -> PageDTO.builder()
                        .context(page.getContext())
                        .backgroundImageUrl(page.getImageUrl())
                        .characterActionInfo(page.getActionInfo())
                        .pageNumber(page.getPageNumber())
                        .build())
                .collect(Collectors.toList());
        BookDTO bookDTO = BookDTO.builder()
                .title(bookEntity.getTitle())
                .id(bookEntity.getId())
                .userId(bookEntity.getUserId())
                .pages(pageDTOs)
                .author(bookEntity.getAuthor())
                .bookColor(bookEntity.getBookColor())
                .storyLength(bookEntity.getStoryLength())
                .build();
        BookDetailDTO bookDetailDTO = BookDetailDTO.builder()
                .book(bookDTO)
                .character(characterDTO)
                .build();
        ResponseDTO<BookDetailDTO> responseDTO = ResponseDTO.<BookDetailDTO>builder()
                .data(List.of(bookDetailDTO))
                .build();
        bookInProgressRedisService.deleteByUserId(customUserDetails.getId());
        return ResponseEntity.ok().body(responseDTO);
    }

    @PostMapping("/save2")
    public ResponseEntity<?> createBook2(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody BookCompleteRequestDTO bookCompleteRequestDTO) throws IOException, InterruptedException {
        BookInProgressRedis bookInProgressRedis = bookInProgressRedisService.getByUserId(customUserDetails.getId());
        boolean isUrlDoneProcessing = isAllAvailable(bookInProgressRedis.getPreviousPages());
        while (!isUrlDoneProcessing) {
            isUrlDoneProcessing = isAllAvailable(bookInProgressRedis.getPreviousPages());
        }
        BookEntity bookEntity = BookEntity.builder()
                .id(bookInProgressRedis.getBookId())
                .characterId(bookInProgressRedis.getCharacterId())
                .title(bookCompleteRequestDTO.getTitle())
                .userId(customUserDetails.getId())
                .createDate(Date.from(Instant.now()))
                .bookColor(bookCompleteRequestDTO.getBookColor())
                .author(bookCompleteRequestDTO.getAuthor())
                .storyLength(bookInProgressRedis.getStoryLength())
                .coverImageUrl(bookInProgressRedis.getPreviousPages().get(0).getBackgroundImageUrl()) // 수정필요
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
            S3File backgroundImageFile = null;
            try {
                backgroundImageFile = s3BucketService.uploadTemporaryFromUrl(page.getImageUrl(), S3DirectoryEnum.BOOK);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            page.setImageUrl(backgroundImageFile.getUrl());
            bookPageService.createPage(page);
        });
        CharacterDTO characterDTO = new CharacterDTO(characterService.getById(bookInProgressRedis.getCharacterId()));
        List<PageDTO> pageDTOs = bookPageService.getAllById(bookEntity.getId())
                .stream()
                .map(page -> PageDTO.builder()
                        .context(page.getContext())
                        .backgroundImageUrl(page.getImageUrl())
                        .characterActionInfo(page.getActionInfo())
                        .pageNumber(page.getPageNumber())
                        .build())
                .collect(Collectors.toList());
        BookDTO bookDTO = BookDTO.builder()
                .title(bookEntity.getTitle())
                .id(bookEntity.getId())
                .userId(bookEntity.getUserId())
                .pages(pageDTOs)
                .author(bookEntity.getAuthor())
                .bookColor(bookEntity.getBookColor())
                .storyLength(bookEntity.getStoryLength())
                .build();
        BookDetailDTO bookDetailDTO = BookDetailDTO.builder()
                .book(bookDTO)
                .character(characterDTO)
                .build();
        ResponseDTO<BookDetailDTO> responseDTO = ResponseDTO.<BookDetailDTO>builder()
                .data(List.of(bookDetailDTO))
                .build();
        bookInProgressRedisService.deleteByUserId(customUserDetails.getId());
        return ResponseEntity.ok().body(responseDTO);
    }

    private boolean isAllAvailable(List<PageDTO> pageDTOS) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        for (PageDTO page : pageDTOS) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(page.getBackgroundImageUrl()))
                    .GET()
                    .build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            log.info("fetching... 404");
            if (response.statusCode() == 404) {
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
                .storyLength(initRequestDTO.getStoryLength())
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
                    .storyLength(initRequestDTO.getStoryLength())
                    .build();
            BookInsightDTO bookInsightDTO = aiBookCreationHelper.generateBookInsightDalle(bookInit);
            S3File temporaryUploadedFile = s3BucketService.uploadTemporaryFromUrl(bookInsightDTO.getTemporaryGeneratedImageUrl(), S3DirectoryEnum.TEMPORARY);
            bookInsightDTO.setTemporaryGeneratedImageUrl(temporaryUploadedFile.getUrl());
            PageDTO page = PageDTO.builder()
                    .context(bookInsightDTO.getRefinedContext())
                    .backgroundImageUrl(bookInsightDTO.getTemporaryGeneratedImageUrl())
                    .characterActionInfo("").build();
            bookInProgressRedis.setPreviousPages(List.of(page));
            bookInProgressRedisService.put(bookInProgressRedis);
            BookInsightResponseDTO bookInitResponseDTO = BookInsightResponseDTO.builder()
                    .bookInsight(bookInsightDTO)
                    .createdPages(bookInProgressRedis.getPreviousPages())
                    .storyLength(bookInProgressRedis.getStoryLength())
                    .build();
            ResponseDTO<BookInsightResponseDTO> responseDTO = ResponseDTO.<BookInsightResponseDTO>builder()
                    .data(List.of(bookInitResponseDTO))
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (RuntimeException e) {
            log.warn(e.getMessage());
            ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                    .error("cannot get response from openAi api")
                    .build();
            return ResponseEntity.internalServerError().body(responseDTO);
        } catch (OpenAiException e) {
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
                .storyLength(initRequestDTO.getStoryLength())
                .build();
        try {
            CharacterEntity characterEntity = characterService.getById(initRequestDTO.getCharacterId());
            BookInit bookInit = BookInit.builder()
                    .backgroundInfo(initRequestDTO.getBackgroundInfo())
                    .currentContext(initRequestDTO.getFirstContext())
                    .characterDTO(new CharacterDTO(characterEntity))
                    .build();
            BookInsightDTO bookInsightDTO = aiBookCreationHelper.generateBookInsightStableDiffusion(bookInit);
            PageDTO page = PageDTO.builder()
                    .context(bookInsightDTO.getRefinedContext())
                    .backgroundImageUrl(bookInsightDTO.getTemporaryGeneratedImageUrl())
                    .characterActionInfo("").build();
            bookInProgressRedis.setPreviousPages(List.of(page));
            bookInProgressRedisService.put(bookInProgressRedis);
            BookInsightResponseDTO bookInitResponseDTO = BookInsightResponseDTO.builder()
                    .bookInsight(bookInsightDTO)
                    .createdPages(bookInProgressRedis.getPreviousPages())
                    .storyLength(bookInProgressRedis.getStoryLength())
                    .build();
            ResponseDTO<BookInsightResponseDTO> responseDTO = ResponseDTO.<BookInsightResponseDTO>builder()
                    .data(List.of(bookInitResponseDTO))
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (StableDiffusionException | OpenAiException | RuntimeException e) {
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
