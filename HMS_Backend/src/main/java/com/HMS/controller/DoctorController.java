package com.HMS.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.HMS.model.Doctor;
import com.HMS.model.DoctorDTO;
import com.HMS.model.User;
import com.HMS.service.DoctorService;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    /**
     * Get all doctors with their user details.
     */
    @GetMapping
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        List<DoctorDTO> doctors = doctorService.getAllDoctors();
        
        if (doctors.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(doctors);
    }
    
    /**
     * Assign the doctor role to a user.
     */
    @PostMapping("/assign/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Doctor> assignDoctorRoleToPatient(@PathVariable Long userId, @RequestBody DoctorDTO doctorDTO) {
        try {
            Doctor assignedDoctor = doctorService.assignDoctorRoleToPatient(userId, doctorDTO);
            return ResponseEntity.ok(assignedDoctor);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    //get authenticated doctor after login
    
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('DOCTOR') or hasAuthority('ADMIN')")
    public ResponseEntity<User> getAuthenticatedDoctor(Principal principal){
    	String email = principal.getName();
    	User doctor = doctorService.getAuthenticatedDoctor(email);
    	return doctor != null ? ResponseEntity.ok(doctor) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    	
    }
    
    
    @PutMapping("/{doctorId}/status")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> updateDoctorStatus(@PathVariable Long doctorId, @RequestBody Doctor.DoctorStatus status) {
    	 try {
    	        doctorService.updateDoctorStatus(doctorId, status);
    	        return ResponseEntity.ok("Doctor status updated successfully");
    	    } catch (RuntimeException e) {
    	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating doctor status");
    	    }
    	}
    
    
    @PutMapping("/{doctorId}")
    @PreAuthorize("hasAuthority('ADMIN') OR hasAuthority('DOCTOR')")
    public ResponseEntity<Doctor> updateDoctorDetails(@PathVariable Long doctorId, @RequestBody DoctorDTO doctorDTO) {
        try {
            Doctor updatedDoctor = doctorService.updateDoctorDetails(doctorId, doctorDTO);
            return ResponseEntity.ok(updatedDoctor);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
    
    @DeleteMapping("/{doctorId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteDoctorById(@PathVariable Long doctorId) {
        try {
            String message = doctorService.deleteDoctorById(doctorId);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting doctor: " + e.getMessage());
        }
    }

} 
