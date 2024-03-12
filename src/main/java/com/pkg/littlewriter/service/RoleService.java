package com.pkg.littlewriter.service;

import com.pkg.littlewriter.model.RoleEntity;
import com.pkg.littlewriter.persistence.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;
    public List<RoleEntity> getByName(final String name) {
        return roleRepository.findByName(name);
    }
}
