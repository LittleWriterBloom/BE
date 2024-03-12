package com.pkg.littlewriter.persistence;

import com.pkg.littlewriter.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, String> {
    List<RoleEntity> findByName(String name);
}
