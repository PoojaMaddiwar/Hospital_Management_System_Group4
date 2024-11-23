package com.HMS.service;

import org.springframework.stereotype.Service;

import com.HMS.model.Doctor;
import com.HMS.model.User;

@Service
public class EmailService {
	 public void sendCancellationEmail(User patient, Doctor doctor) {
	        // Logic to send email to patient with cancellation details
	        System.out.println("Sending cancellation email to " + patient.getEmail() + " regarding doctor " + doctor.getDoctorId());
	        // Actual email sending logic goes here
	    }
}
