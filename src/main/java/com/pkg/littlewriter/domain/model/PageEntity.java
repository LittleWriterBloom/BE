package com.pkg.littlewriter.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "book_page")
public class PageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String bookId;
    @Column(length = 511)
    private String context;
    private String colorImageUrl;
    private String sketchImageUrl;
    private String actionInfo;
    private int pageNumber;
}
