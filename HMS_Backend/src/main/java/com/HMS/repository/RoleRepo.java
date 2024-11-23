package com.HMS.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.HMS.model.Role;
import com.HMS.model.RoleType;
@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
	Optional<Role> findByName(RoleType name);
}
