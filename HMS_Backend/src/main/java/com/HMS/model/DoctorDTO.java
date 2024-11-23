package com.HMS.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDTO {
		private Long doctorId;
	 	private String degree;
	    private String qualification;
	    private LocalDateTime createdDate;
	    private UserDTO user; // Add UserDTO field
	    private Doctor.DoctorStatus status;

	    // Optionally, add getter and setter methods if you're not using Lombok's @Data
	    public void setUser(UserDTO user) {
	        this.user = user;
	    }

	    public UserDTO getUser() {
	        return user;
	    }
}
