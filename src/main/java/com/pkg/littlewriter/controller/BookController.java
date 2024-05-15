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
            S3File sketchImageFile = s3BucketService.uploadTemporaryFromUrl(bookInsightDTO.getSketchImageUrl(), S3DirectoryEnum.TEMPORARY);
            bookInsightDTO.setSketchImageUrl(sketchImageFile.getUrl());
            PageDTO newPage = PageDTO.builder()
                    .characterActionInfo(bookInsightRequestDTO.getCharacterActionInfo())
                    .context(bookInsightDTO.getRefinedContext())
                    .sketchImageUrl(sketchImageFile.getUrl())
                    .coloredImageUrl(bookInsightDTO.getProcessingImage().getImageUrl())
                    .pageNumber(bookInProgressRedis.getPreviousPages().size())
                    .build();
            BookInProgressRedis updatedBookInProgress = bookInProgressRedisService.appendPageTo(customUserDetails.getId(), newPage);
            BookInsightResponseDTO bookInitResponseDTO = BookInsightResponseDTO.builder()
                    .bookInsight(new BookInsightSketchDTO(bookInsightDTO))
//                    .createdPages(updatedBookInProgress.getPreviousPages())
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/save")
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
                .coverImageUrl(bookInProgressRedis.getPreviousPages().get(0).getColoredImageUrl()) // 수정필요
                .build();
        bookService.createEmptyBook(bookEntity);
        int[] pageNumber = {0};
        List<PageEntity> pages = bookInProgressRedis.getPreviousPages()
                .stream()
                .map(pageDTO -> PageEntity.builder()
                        .actionInfo(pageDTO.getCharacterActionInfo())
                        .context(pageDTO.getContext())
                        .bookId(bookInProgressRedis.getBookId())
                        .colorImageUrl(pageDTO.getColoredImageUrl())
                        .sketchImageUrl(pageDTO.getSketchImageUrl())
                        .pageNumber(pageNumber[0]++).build())
                .toList();
        pages.forEach(page -> {
            S3File colorImageFile = null;
            S3File sketchImageFile = null;
            try {
                colorImageFile = s3BucketService.uploadTemporaryFromUrl(page.getColorImageUrl(), S3DirectoryEnum.BOOK);
                sketchImageFile = s3BucketService.uploadTemporaryFromUrl(page.getSketchImageUrl(), S3DirectoryEnum.BOOK);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            page.setColorImageUrl(colorImageFile.getUrl());
            page.setSketchImageUrl(sketchImageFile.getUrl());
            bookPageService.createPage(page);
        });
        CharacterDTO characterDTO = new CharacterDTO(characterService.getById(bookInProgressRedis.getCharacterId()));
        List<PageDTO> pageDTOs = bookPageService.getAllById(bookEntity.getId())
                .stream()
                .map(page -> PageDTO.builder()
                        .context(page.getContext())
                        .sketchImageUrl(page.getSketchImageUrl())
                        .coloredImageUrl(page.getColorImageUrl())
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
                    .uri(URI.create(page.getColoredImageUrl()))
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
            S3File sketchImageFile = s3BucketService.uploadTemporaryFromUrl(bookInsightDTO.getSketchImageUrl(), S3DirectoryEnum.TEMPORARY);
            PageDTO page = PageDTO.builder()
                    .context(bookInsightDTO.getRefinedContext())
                    .sketchImageUrl(sketchImageFile.getUrl())
                    .coloredImageUrl(bookInsightDTO.getProcessingImage().getImageUrl())
                    .characterActionInfo("")
                    .build();
            bookInsightDTO.setSketchImageUrl(sketchImageFile.getUrl());
            bookInProgressRedis.setPreviousPages(List.of(page));
            bookInProgressRedisService.put(bookInProgressRedis);
            BookInsightResponseDTO bookInitResponseDTO = BookInsightResponseDTO.builder()
                    .bookInsight(new BookInsightSketchDTO(bookInsightDTO))
//                    .createdPages(bookInProgressRedis.getPreviousPages())
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
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    @PostMapping("dalle-init")
    public ResponseEntity<?> dalleBookInit(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody BookInitRequestDTO initRequestDTO) throws IOException {
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
            BookInsightDalleDTO bookInsightDTO = aiBookCreationHelper.generateBookInsightDalleFrom(bookInit);
            S3File imageFile = s3BucketService.uploadTemporaryFromUrl(bookInsightDTO.getImageUrl(), S3DirectoryEnum.TEMPORARY);
            PageDTO page = PageDTO.builder()
                    .context(bookInsightDTO.getRefinedContext())
                    .coloredImageUrl(bookInsightDTO.getImageUrl())
                    .characterActionInfo("")
                    .build();
            bookInsightDTO.setImageUrl(imageFile.getUrl());
            bookInProgressRedis.setPreviousPages(List.of(page));
            bookInProgressRedisService.put(bookInProgressRedis);
            BookInsightDalleResponseDTO bookInitResponseDTO = BookInsightDalleResponseDTO.builder()
                    .bookInsight(bookInsightDTO)
                    .storyLength(bookInProgressRedis.getStoryLength())
                    .build();
            ResponseDTO<BookInsightDalleResponseDTO> responseDTO = ResponseDTO.<BookInsightDalleResponseDTO>builder()
                    .data(List.of(bookInitResponseDTO))
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (OpenAiException | RuntimeException e) {
            log.warn(e.getMessage());
            ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                    .error("cannot get response from openAi api")
                    .error(e.getMessage())
                    .build();
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }

    @PostMapping("/dalle-insight")
    public ResponseEntity<?> dalleBuildBook(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody BookInsightRequestDTO bookInsightRequestDTO) {
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
            BookInsightDalleDTO bookInsightDTO = aiBookCreationHelper.generateBookInsightDalleFrom(bookInProgress);
            S3File imageFile = s3BucketService.uploadTemporaryFromUrl(bookInsightDTO.getImageUrl(), S3DirectoryEnum.TEMPORARY);
            bookInsightDTO.setImageUrl(imageFile.getUrl());
            PageDTO newPage = PageDTO.builder()
                    .characterActionInfo(bookInsightRequestDTO.getCharacterActionInfo())
                    .context(bookInsightDTO.getRefinedContext())
                    .coloredImageUrl(bookInsightDTO.getImageUrl())
                    .pageNumber(bookInProgressRedis.getPreviousPages().size())
                    .build();
            BookInProgressRedis updatedBookInProgress = bookInProgressRedisService.appendPageTo(customUserDetails.getId(), newPage);
            BookInsightDalleResponseDTO bookInitResponseDTO = BookInsightDalleResponseDTO.builder()
                    .bookInsight(bookInsightDTO)
                    .storyLength(updatedBookInProgress.getStoryLength())
                    .build();
            ResponseDTO<BookInsightDalleResponseDTO> responseDTO = ResponseDTO.<BookInsightDalleResponseDTO>builder()
                    .data(List.of(bookInitResponseDTO))
                    .build();
            return ResponseEntity.ok().body(responseDTO);
        } catch (RuntimeException | OpenAiException e) {
            ResponseDTO<String> responseDTO = ResponseDTO.<String>builder()
                    .error("cannot get response from openAi api")
                    .build();
            return ResponseEntity.internalServerError().body(responseDTO);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/dalle-save")
    public ResponseEntity<?> dalleCreateBook(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody BookCompleteRequestDTO bookCompleteRequestDTO) throws IOException, InterruptedException {
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
                .build();
        int[] pageNumber = {0};
        List<PageEntity> pages = bookInProgressRedis.getPreviousPages()
                .stream()
                .map(pageDTO -> PageEntity.builder()
                        .actionInfo(pageDTO.getCharacterActionInfo())
                        .context(pageDTO.getContext())
                        .bookId(bookInProgressRedis.getBookId())
                        .colorImageUrl(pageDTO.getColoredImageUrl())
                        .pageNumber(pageNumber[0]++).build())
                .toList();
        pages.forEach(page -> {
            S3File colorImageFile = null;
            try {
                colorImageFile = s3BucketService.uploadTemporaryFromUrl(page.getColorImageUrl(), S3DirectoryEnum.BOOK);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            page.setColorImageUrl(colorImageFile.getUrl());
            bookPageService.createPage(page);
        });
        List<PageEntity> createdPages = bookPageService.getAllById(bookEntity.getId());
        bookEntity.setCoverImageUrl(createdPages.get(0).getColorImageUrl());
        bookService.createEmptyBook(bookEntity);
        CharacterDTO characterDTO = new CharacterDTO(characterService.getById(bookInProgressRedis.getCharacterId()));
        List<PageDTO> pageDTOs = createdPages
                .stream()
                .map(page -> PageDTO.builder()
                        .context(page.getContext())
                        .coloredImageUrl(page.getColorImageUrl())
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
                    .build();
            return ResponseEntity.internalServerError().body(responseDTO);
        }
    }
}
