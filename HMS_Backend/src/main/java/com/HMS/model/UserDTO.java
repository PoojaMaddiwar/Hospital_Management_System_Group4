package com.HMS.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
public class UserDTO {
	private long uid;
	
	@NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    private String password; // Regular password
    private String cPassword; // Confirm password
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be 10 digits")
    private String mobileNumber;
    
    @NotBlank(message = "Aadhar number is required")
  //@Pattern(regexp = "^\\d{12}$", message = "Aadhar number must be 12 digits")
    private String aadharNumber;
    
    @NotBlank(message = "Gender is required")
    private String gender;
    
    @NotBlank(message = "Date of birth is required")
    private LocalDate dateOfBirth;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "State is required")
    private String state;
    
    @NotBlank(message = "Address is required")
    private String address;
    
    private LocalDateTime createdDate;
    private Set<Role> roles;
//    private DoctorDTO doctor;
    
	public UserDTO(long uid, @NotBlank(message = "First name is required") String firstName,
			@NotBlank(message = "Last name is required") String lastName, String password, String cPassword,
			@NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,
			@NotBlank(message = "Mobile number is required") @Pattern(regexp = "^\\d{10}$", message = "Mobile number must be 10 digits") String mobileNumber,
			@NotBlank(message = "Aadhar number is required") String aadharNumber,
			@NotBlank(message = "Gender is required") String gender,
			@NotBlank(message = "Date of birth is required") LocalDate dateOfBirth,
			@NotBlank(message = "City is required") String city, @NotBlank(message = "State is required") String state,
			@NotBlank(message = "Address is required") String address, LocalDateTime createdDate, Set<Role> roles
			) {
		super();
		this.uid = uid;
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.cPassword = cPassword;
		this.email = email;
		this.mobileNumber = mobileNumber;
		this.aadharNumber = aadharNumber;
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
		this.city = city;
		this.state = state;
		this.address = address;
		this.createdDate = createdDate;
		this.roles = roles;
		
	} 
    

  
}
