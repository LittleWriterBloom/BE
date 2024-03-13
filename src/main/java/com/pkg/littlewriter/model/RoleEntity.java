package com.pkg.littlewriter.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "role")
public class RoleEntity {

    @Id
    private int id;
    private String name;
    @OneToMany(mappedBy = "role")
    private List<MemberEntity> memberEntities;
}
