package com.HMS.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.HMS.model.Doctor;
import com.HMS.model.DoctorDTO;
import com.HMS.model.Role;
import com.HMS.model.RoleType;
import com.HMS.model.User;
import com.HMS.model.UserDTO;
import com.HMS.repository.DoctorRepo;
import com.HMS.repository.RoleRepo;
import com.HMS.repository.UserRepo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class DoctorService {
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private DoctorRepo doctorRepo;
	
	@Autowired
	private RoleRepo roleRepo;
	
	@Autowired
	private AppointmentService appointmentService;
	
	 @PersistenceContext
	 private EntityManager entityManager;

	
	/**
     * Helper method to map a User and Doctor to DoctorDTO.
     */
    private DoctorDTO mapUserToDoctorDTO(User user) {
    	Optional<Doctor> optionalDoctor = doctorRepo.findByUser(user); 
        DoctorDTO doctorDTO = new DoctorDTO();

        // Map User details
        UserDTO userDTO = new UserDTO(
            user.getUid(),
            user.getFirstName(),
            user.getLastName(),
            user.getPassword(),
            user.getCPassword(),
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
        doctorDTO.setUser(userDTO); // Set userDTO to doctorDTO

        // Set Doctor details if available
        if (optionalDoctor.isPresent()) {
            Doctor doctor = optionalDoctor.get(); // Get the Doctor if present
            doctorDTO.setDoctorId(doctor.getDoctorId());
            doctorDTO.setDegree(doctor.getDegree());
            doctorDTO.setQualification(doctor.getQualification());
            doctorDTO.setCreatedDate(doctor.getCreatedDate());
            doctorDTO.setStatus(doctor.getStatus());
        } else {
            System.out.println("Doctor not found for user: " + user.getFirstName());
        }

        return doctorDTO;
    }

    /**
     * Get all doctors with associated user details.
     */
    public List<DoctorDTO> getAllDoctors() {
        List<User> users = userRepo.findByRoles_Name(RoleType.DOCTOR); // Fetch users with DOCTOR role

        if (users.isEmpty()) {
            System.out.println("No doctors found.");
        }

        // Use the helper method to map each User to DoctorDTO
        return users.stream()
                    .map(this::mapUserToDoctorDTO) // Call the helper method
                    .collect(Collectors.toList());
    }

    // Method to assign the doctor role and save doctor data
    /**
     * Assign the doctor role and save doctor data for an existing patient.
     */
    @Transactional
    public Doctor assignDoctorRoleToPatient(Long userId, DoctorDTO doctorDTO) {
        Optional<User> userOptional = userRepo.findById(userId);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        User user = userOptional.get();

        // Check if the user has the PATIENT role
        Role patientRole = roleRepo.findByName(RoleType.PATIENT)
            .orElseThrow(() -> new RuntimeException("PATIENT role not found in the database"));
        
        if (!user.getRoles().contains(patientRole)) {
            throw new RuntimeException("User with ID " + userId + " is not a patient.");
        }

        // Add the DOCTOR role if not already present
        Role doctorRole = roleRepo.findByName(RoleType.DOCTOR)
            .orElseThrow(() -> new RuntimeException("DOCTOR role not found in the database"));

        if (!user.getRoles().contains(doctorRole)) {
            user.getRoles().add(doctorRole); // Add the DOCTOR role to the user
            userRepo.save(user); // Save updated user with new role
        }

        // Create or update Doctor entity associated with this user
        Optional<Doctor> existingDoctor = doctorRepo.findByUser(user); // Fetch associated Doctor
        Doctor doctor;
        if (existingDoctor.isPresent()) {
            doctor = existingDoctor.get(); // If exists, get it
        } else {
            doctor = new Doctor(); // If not exists, create new
        }

        doctor.setUser(user); // Associate the doctor with the user
        doctor.setDegree(doctorDTO.getDegree());
        doctor.setQualification(doctorDTO.getQualification());

        // Log the user and doctor details
        System.out.println("Assigning doctor role to user: " + user.getFirstName());

        return doctorRepo.save(doctor); // Save doctor details
    }

	public User getAuthenticatedDoctor(String email) {
		// TODO Auto-generated method stub
		Optional<User> userOptional = userRepo.findByEmail(email);
		if (userOptional.isPresent()) {
			User user = userOptional.get();
			if (user.getRoles().stream().anyMatch(role -> role.getName() == RoleType.DOCTOR)) {
				return user;
			}
		}
		return null;
	}
	
	
	 // Method to update Doctor's status
	public String updateDoctorStatus(Long doctorId, Doctor.DoctorStatus status) {
	    Doctor doctor = doctorRepo.findById(doctorId)
	            .orElseThrow(() -> new RuntimeException("Doctor not found"));

	    doctor.setStatus(status);
	    doctorRepo.save(doctor);

	    // Cancel appointments if the doctor is set to INACTIVE
	    if (status == Doctor.DoctorStatus.INACTIVE) {
	    	 appointmentService.cancelAppointmentsByDoctor(doctor);
	         
	    }
	    return "Appointments canceled for Doctor ID: " + doctorId;
	}
	
	
	@Transactional
	public Doctor updateDoctorDetails(Long doctorId, DoctorDTO doctorDTO) {
	    Doctor doctor = doctorRepo.findById(doctorId)
	            .orElseThrow(() -> new RuntimeException("Doctor not found"));

	    doctor.setDegree(doctorDTO.getDegree());
	    doctor.setQualification(doctorDTO.getQualification());

	    User user = doctor.getUser();
	    if (doctorDTO.getUser() != null) {
	        UserDTO userDTO = doctorDTO.getUser();
	        user.setFirstName(userDTO.getFirstName());
	        user.setLastName(userDTO.getLastName());
	        user.setEmail(userDTO.getEmail());
	        user.setMobileNumber(userDTO.getMobileNumber());
	        user.setAadharNumber(userDTO.getAadharNumber());
	        user.setCity(userDTO.getCity());
	        user.setState(userDTO.getState());
	        user.setAddress(userDTO.getAddress());
	    }

	    userRepo.save(user);
	    return doctorRepo.save(doctor);
	}

	

	@Transactional
	public String deleteDoctorById(Long doctorId) {
	    // Fetch the doctor by ID
	    Doctor doctor = doctorRepo.findById(doctorId)
	            .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));

	    // Fetch associated user
	    User user = doctor.getUser();

	    // Cancel all appointments associated with the doctor
	    appointmentService.cancelAppointmentsByDoctor(doctor);

	    // Clear the appointments collection
	    if (doctor.getAppointments() != null && !doctor.getAppointments().isEmpty()) {
	        doctor.getAppointments().clear();
	    }

	    // Remove doctor role from the user
	    Role doctorRole = roleRepo.findByName(RoleType.DOCTOR)
	            .orElseThrow(() -> new RuntimeException("Doctor role not found in the database"));

	    if (user.getRoles().contains(doctorRole)) {
	        user.getRoles().remove(doctorRole); // Remove the doctor role
	    }

	    // Break associations
	    doctor.setUser(null);

	    // Save updated user to ensure roles are updated
	    userRepo.save(user);

	    // Delete the doctor directly
	    doctorRepo.deleteById(doctorId); // Use deleteById to avoid managing detached entities

	    // Handle user cleanup
	    if (user.getRoles().isEmpty()) {
	        userRepo.delete(user); // Delete the user if no roles are left
	    }

	    return "Doctor, associated role, and appointments deleted successfully.";
	}
	}


	

