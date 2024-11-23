package com.HMS.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "doctors")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Doctor {
	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private long doctorId;

	    @Column(nullable = false)
	    private String degree;

	    @Column(nullable = false)
	    private String qualification;

	    private LocalDateTime createdDate = LocalDateTime.now();
	    
	    @Enumerated(EnumType.STRING)
	    private DoctorStatus status = DoctorStatus.ACTIVE;
	    
	 // One-to-one relationship with User
	    @OneToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "user_id", referencedColumnName = "uid")
	    @JsonIgnore
	    private User user;
	    
	    @OneToMany(mappedBy = "doctor", cascade = CascadeType.REMOVE, orphanRemoval = true)
	    @JsonIgnore
	    private List<Appointment> appointments;

	    
	    public enum DoctorStatus {
	        ACTIVE, INACTIVE
	    }

	    // Method to check if doctor is active
	    public boolean isActive() {
	        return this.status == DoctorStatus.ACTIVE;
	    }
}
