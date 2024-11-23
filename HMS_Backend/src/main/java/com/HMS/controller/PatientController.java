package com.HMS.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.HMS.model.User;
import com.HMS.model.UserDTO;
import com.HMS.service.PatientService;
import com.HMS.service.UserService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class PatientController {
	@Autowired
	private PatientService patientService;
	
	@Autowired
	UserService userService;
	
	 	@GetMapping("/patients")
	    @PreAuthorize("hasAuthority('PATIENT') or hasAuthority('DOCTOR') or hasAuthority('ADMIN')")
	    public List<User> getAllPatients() {
	        return patientService.getAllPatients();
	    }
	 	
	 // Update a patient
	    @PutMapping("/patients/{uid}")
	    @PreAuthorize("hasAuthority('ADMIN')")
	    public ResponseEntity<?> updatePatient(@PathVariable long uid, @RequestBody UserDTO updatedUserDTO) {
	        try {
	            UserDTO updatedPatient = patientService.updatePatient(uid, updatedUserDTO);
	            return ResponseEntity.ok(updatedPatient);
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Update failed: " + e.getMessage());
	        }
	    }

	    // Delete a patient
	    @DeleteMapping("/patients/{uid}")
	    @PreAuthorize("hasAuthority('ADMIN')")
	    public ResponseEntity<?> deletePatient(@PathVariable long uid) {
	        try {
	            patientService.deletePatient(uid);
	            return ResponseEntity.ok("Patient deleted successfully.");
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Delete failed: " + e.getMessage());
	        }
	    }

	    // Search patients by name or mobile number
	    @GetMapping("/patients/search")
	    @PreAuthorize("hasAuthority('ADMIN')")
	    public List<UserDTO> searchPatients(@RequestParam String keyword) {
	        return patientService.searchPatients(keyword);
	    }
	 
	 	//get single authenticated patient after login
	 	@GetMapping("/patient/me")
	    @PreAuthorize("hasAuthority('PATIENT') or hasAuthority('DOCTOR') or hasAuthority('ADMIN')")
	    public ResponseEntity<User> getAuthenticatedPatient(Principal principal) {
	        String email = principal.getName();
	        User patient = patientService.getAuthenticatedPatient(email);
	        return patient != null ? ResponseEntity.ok(patient) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	    }
	 	
	 	//patient will update his own profile after login
	 	@PostMapping("/patient/update")
	 	@PreAuthorize("hasAuthority('PATIENT') or hasAuthority('DOCTOR') or hasAuthority('ADMIN')")
	 	public ResponseEntity<?> updatePatientProfile(@RequestBody UserDTO updatedUserDTO, Principal principal) {
	 	    String email = principal.getName();
	 	    try {
	 	        User updatedPatient = patientService.updatePatientProfile(email, updatedUserDTO);
	 	        return ResponseEntity.ok(updatedPatient);
	 	    } catch (Exception e) {
	 	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Update failed: " + e.getMessage());
	 	    }
	 	}
	 	
	    
	    
}
