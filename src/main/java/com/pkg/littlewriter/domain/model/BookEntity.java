package com.pkg.littlewriter.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "book")
public class BookEntity {
    @Id
    private String id;
    private Long userId;
    private Long characterId;
    private String title;
    @Temporal(TemporalType.TIMESTAMP)

    private Date createDate;
    private Long bookColor;
    private String author;
}
