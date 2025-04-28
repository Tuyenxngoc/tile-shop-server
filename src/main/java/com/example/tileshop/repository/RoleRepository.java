package com.example.tileshop.repository;

import com.example.tileshop.constant.RoleConstant;
import com.example.tileshop.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByCode(RoleConstant code);
}
