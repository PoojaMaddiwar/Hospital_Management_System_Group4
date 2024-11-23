package com.HMS.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.HMS.model.RoleType;
import com.HMS.model.User;
@Repository
public interface UserRepo extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	 List<User> findByRoles_Name(RoleType roleName); 
	 
	 User findByEmailAndRoles_Name(String email, RoleType role);
	List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrMobileNumberContainingIgnoreCase(
			String firstName, String lastName, String mobileNumber);
}
