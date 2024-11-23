package com.HMS.security;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.HMS.model.User;
import com.HMS.repository.UserRepo;
@Service
public class JwtUserDetailsService implements UserDetailsService {
@Autowired
private UserRepo userRepo;
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		Optional<User> byEmail = userRepo.findByEmail(email);
		
		if(!byEmail.isPresent()) {
			throw new UsernameNotFoundException("User not found with email: " + email);
			
		}
		 // Retrieve the user from Optional
        User user = byEmail.get();
		return new org.springframework.security.core.userdetails.User(
				 user.getEmail(),
	                user.getPassword(),
	                user.getRoles().stream()
	                        .map(role -> new SimpleGrantedAuthority(role.getName().name())) // Map roles to GrantedAuthority
	                        .collect(Collectors.toList())
				);
	}

}
