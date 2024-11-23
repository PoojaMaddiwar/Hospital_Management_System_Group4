package com.HMS.controller;

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.HMS.model.JwtRequest;
import com.HMS.model.JwtResponse;
import com.HMS.model.RoleType;
import com.HMS.model.User;
import com.HMS.model.UserDTO;
import com.HMS.security.JwtTokenUtil;
import com.HMS.service.UserService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
	@Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

        // Authenticate the email and password
        authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());

        // Load user details by email
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());

     // Generate the JWT token
        final String token = jwtTokenUtil.generateToken(userDetails);
        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();

        // Create a response object that includes both the token and roles
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("roles", roles);

        return ResponseEntity.ok(response);  
    }

    private void authenticate(String email, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
    
    
    // Register user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        try {
            userService.registerUser(userDTO);  // Directly call the implementation
            return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Registration failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<User> getAllAdmin(){
    	return userService.getAllAdmin();
    }
    
    @GetMapping("/admin/me")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<User> getAdmindetails(Principal principal){
    	String email = principal.getName();
    	User admin = userService.getUserByEmailAndRole(email, RoleType.ADMIN);
    	return admin != null ? ResponseEntity.ok(admin) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    
    
}
