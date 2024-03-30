package com.pkg.littlewriter.controller;

import com.pkg.littlewriter.domain.model.BookEntity;
import com.pkg.littlewriter.dto.BookDTO;
import com.pkg.littlewriter.dto.PageDTO;
import com.pkg.littlewriter.dto.ResponseDTO;
import com.pkg.littlewriter.security.CustomUserDetails;
import com.pkg.littlewriter.service.BookPageService;
import com.pkg.littlewriter.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/{bookId}")
    public ResponseEntity<?> getBook(@PathVariable String bookId) {
        BookEntity bookEntity = bookService.getById(bookId);
        List<PageDTO> pageDTOs = bookPageService.getAllById(bookId)
                .stream()
                .map(page -> PageDTO.builder()
                        .context(page.getContext())
                        .backgroundImageUrl(page.getImageUrl())
                        .characterActionInfo(page.getActionInfo())
                        .build())
                .collect(Collectors.toList());
        BookDTO bookDTO = BookDTO.builder()
                .pages(pageDTOs)
                .createDate(bookEntity.getCreateDate())
                .title(bookEntity.getTitle())
                .build();
        ResponseDTO<BookDTO> responseDTO = ResponseDTO.<BookDTO>builder()
                .data(List.of(bookDTO))
                .build();
        return ResponseEntity.ok().body(responseDTO);
    }

    @GetMapping("/my")
    public ResponseEntity<?> getBook(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<BookEntity> bookEntity = bookService.getAllByUserId(customUserDetails.getId());
        List<BookDTO> bookDTOs = bookEntity.stream()
                .map(book -> BookDTO.builder()
                        .id(book.getId())
                        .userId(book.getUserId())
                        .characterId(book.getCharacterId())
                        .title(book.getTitle())
                        .createDate(book.getCreateDate())
                        .build())
                .collect(Collectors.toList());
        ResponseDTO<BookDTO> responseDTO = ResponseDTO.<BookDTO>builder()
                .data(bookDTOs)
                .build();
        return ResponseEntity.ok().body(responseDTO);
    }
}
