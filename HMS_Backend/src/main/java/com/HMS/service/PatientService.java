package com.HMS.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.HMS.model.RoleType;
import com.HMS.model.User;
import com.HMS.model.UserDTO;
import com.HMS.repository.RoleRepo;
import com.HMS.repository.UserRepo;

@Service
public class PatientService {
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private RoleRepo roleRepo;
	
	// Convert User to UserDTO
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
            user.getUid(),
            user.getFirstName(),
            user.getLastName(),
            null,  // Password and cPassword should not be exposed
            null,  // Confirm password should not be exposed
            user.getEmail(),
            user.getMobileNumber(),
            user.getAadharNumber(),
            user.getGender(),
            user.getDateOfBirth(),
            user.getCity(),
            user.getState(),
            user.getAddress(),
            user.getCreatedDate(),
            user.getRoles()
        );
    }
	
	// Get all patients
    public List<User> getAllPatients() {
        return userRepo.findByRoles_Name(RoleType.PATIENT); // Adjust the role name as per your RoleType
    }
    
 // Update patient
    public UserDTO updatePatient(long uid, UserDTO updatedUserDTO) throws Exception {
        User patient = userRepo.findById(uid).orElse(null);
        
        if (patient == null) {
            throw new Exception("Patient not found");
        }

        // Update patient fields
        patient.setFirstName(updatedUserDTO.getFirstName());
        patient.setLastName(updatedUserDTO.getLastName());
        patient.setMobileNumber(updatedUserDTO.getMobileNumber());
        patient.setAadharNumber(updatedUserDTO.getAadharNumber());
        patient.setGender(updatedUserDTO.getGender());
        patient.setDateOfBirth(updatedUserDTO.getDateOfBirth());
        patient.setCity(updatedUserDTO.getCity());
        patient.setState(updatedUserDTO.getState());
        patient.setAddress(updatedUserDTO.getAddress());

        // Save the updated patient
        return convertToDTO(userRepo.save(patient));
    }

    // Delete patient
    public void deletePatient(long uid) throws Exception {
        User patient = userRepo.findById(uid).orElse(null);
        
        if (patient == null) {
            throw new Exception("Patient not found");
        }
        
        userRepo.delete(patient);
    }

    // Search patients by name or mobile number
    public List<UserDTO> searchPatients(String keyword) {
        List<User> patients = userRepo.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrMobileNumberContainingIgnoreCase(
            keyword, keyword, keyword);
        return patients.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    
    // Get authenticated patient by email
    public User getAuthenticatedPatient(String email) {
        return userRepo.findByEmailAndRoles_Name(email, RoleType.PATIENT); // Return the user if found, else return null
    }
    
    //patient will update his own profile after login
    public User updatePatientProfile(String email, UserDTO updatedUserDTO) throws Exception {
        User patient = userRepo.findByEmailAndRoles_Name(email, RoleType.PATIENT);
        
        if (patient == null) {
            throw new Exception("Patient not found");
        }

        // Update the fields of the patient as necessary
        patient.setFirstName(updatedUserDTO.getFirstName());
        patient.setLastName(updatedUserDTO.getLastName());
        patient.setMobileNumber(updatedUserDTO.getMobileNumber());
        patient.setAadharNumber(updatedUserDTO.getAadharNumber());
        patient.setGender(updatedUserDTO.getGender());
        patient.setDateOfBirth(updatedUserDTO.getDateOfBirth());
        patient.setCity(updatedUserDTO.getCity());
        patient.setState(updatedUserDTO.getState());
        patient.setAddress(updatedUserDTO.getAddress());
        patient.setCreatedDate(updatedUserDTO.getCreatedDate());
        
        // Save the updated patient
        return userRepo.save(patient);
    }
}
