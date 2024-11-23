package com.HMS.service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.HMS.model.Role;
import com.HMS.model.RoleType;
import com.HMS.model.User;
import com.HMS.model.UserDTO;
import com.HMS.repository.RoleRepo;
import com.HMS.repository.UserRepo;

@Service
public class UserService {
	@Autowired
	private UserRepo userRepo;

	@Autowired
	private RoleRepo roleRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	public void registerUser(UserDTO userDTO) throws Exception {
		// TODO Auto-generated method stub
		User user = new User();
		
		 // Map fields from UserDTO to User
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setMobileNumber(userDTO.getMobileNumber());
        user.setAadharNumber(userDTO.getAadharNumber());
        user.setGender(userDTO.getGender());
        user.setDateOfBirth(userDTO.getDateOfBirth());
        user.setCity(userDTO.getCity());
        user.setState(userDTO.getState());
        user.setAddress(userDTO.getAddress());
        
        user.setCreatedDate(LocalDateTime.now());
        
        // Set default role to PATIENT
        Set<Role> roles = new HashSet<>();
        Optional<Role> role = roleRepo.findByName(RoleType.PATIENT);
        if (role.isPresent()) {
            roles.add(role.get());
        } else {
            throw new Exception("PATIENT role not found in the database");
        }
        user.setRoles(roles);

        // Encrypt password
        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encodedPassword);
        
        // Set cPassword (confirm password)
        user.setCPassword(userDTO.getPassword()); 

        // Save user
        userRepo.save(user);
    }
	

	public List<User> getAllAdmin() {
		// TODO Auto-generated method stub
		return userRepo.findByRoles_Name(RoleType.ADMIN);
	}

	
	public User getUserByEmailAndRole(String email, RoleType role) {
		// TODO Auto-generated method stub
		return userRepo.findByEmailAndRoles_Name(email, RoleType.ADMIN);
	}
	}


