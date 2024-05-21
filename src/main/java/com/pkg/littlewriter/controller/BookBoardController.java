package com.pkg.littlewriter.controller;

import com.pkg.littlewriter.domain.model.BookEntity;
import com.pkg.littlewriter.domain.model.CharacterEntity;
import com.pkg.littlewriter.dto.*;
import com.pkg.littlewriter.security.CustomUserDetails;
import com.pkg.littlewriter.service.BookPageService;
import com.pkg.littlewriter.service.BookService;
import com.pkg.littlewriter.service.CharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/books/board")
public class BookBoardController {
    @Autowired
    private BookService bookService;
    @Autowired
    private BookPageService bookPageService;
    @Autowired
    private CharacterService characterService;

    @GetMapping("/{bookId}")
    public ResponseEntity<?> getBook(@PathVariable String bookId) {
        BookEntity bookEntity = bookService.getById(bookId);
        List<PageDTO> pageDTOs = bookPageService.getAllById(bookId)
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
                .pages(pageDTOs)
                .createDate(bookEntity.getCreateDate())
                .title(bookEntity.getTitle())
                .bookColor(bookEntity.getBookColor())
                .author(bookEntity.getAuthor())
                .build();
        CharacterEntity characterEntity = characterService.getById(bookEntity.getCharacterId());
        CharacterDTO characterDTO = CharacterDTO.builder()
                .id(characterEntity.getId())
                .name(characterEntity.getName())
                .description(characterEntity.getUserDescription())
                .appearanceKeywords(characterEntity.getAppearanceKeywords())
                .imageUrl(characterEntity.getImageUrl())
                .build();
        BookDetailDTO bookDetail = BookDetailDTO.builder()
                .book(bookDTO)
                .character(characterDTO).build();
        ResponseDTO<BookDetailDTO> responseDTO = ResponseDTO.<BookDetailDTO>builder()
                .data(List.of(bookDetail))
                .build();
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getBook(@AuthenticationPrincipal CustomUserDetails customUserDetails, @PageableDefault(size=9, sort="createDate",direction= Sort.Direction.DESC) Pageable pageable) {
        Page<BookEntity> pageBook = bookService.getAllByUserId(customUserDetails.getId(), pageable);
//        List<BookEntity> bookEntity = bookService.getAllByUserId(customUserDetails.getId());
        List<BookEntity> bookEntity = pageBook.getContent();
        List<BookCoverDTO> bookCoverDTOs = bookEntity.stream()
                .map(book -> BookCoverDTO.builder()
                        .bookId(book.getId())
                        .firstPageImageUrl(book.getCoverImageUrl())
                        .userId(book.getUserId().toString())
                        .author(book.getAuthor())
                        .title(book.getTitle())
                        .character(getCharacterDTO(book))
                        .createDate(book.getCreateDate())
                        .bookColor(book.getBookColor())
                        .storyLength(book.getStoryLength())
                        .build())
                .collect(Collectors.toList());
        Pagination<BookEntity> pagination = new Pagination<>(pageBook);
        RetrieveBookCoverResponseDTO retrieveBookCoverResponseDTO = RetrieveBookCoverResponseDTO.builder()
                .books(bookCoverDTOs)
                .pageInfo(pagination)
                .build();
        ResponseDTO<RetrieveBookCoverResponseDTO> responseDTO = ResponseDTO.<RetrieveBookCoverResponseDTO>builder()
                .data(List.of(retrieveBookCoverResponseDTO))
                .build();
        return ResponseEntity.ok().body(responseDTO);
    }

    private CharacterDTO getCharacterDTO(BookEntity bookEntity) {
        CharacterEntity characterEntity = characterService.getById(bookEntity.getCharacterId());
        return CharacterDTO.builder()
                .name(characterEntity.getName())
                .personality(characterEntity.getPersonality())
                .imageUrl(characterEntity.getImageUrl())
                .build();
    }
}
