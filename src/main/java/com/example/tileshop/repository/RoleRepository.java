package com.example.tileshop.repository;

import com.example.tileshop.constant.RoleConstant;
import com.example.tileshop.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Byte> {

    Optional<Role> findByCode(RoleConstant code);

}
