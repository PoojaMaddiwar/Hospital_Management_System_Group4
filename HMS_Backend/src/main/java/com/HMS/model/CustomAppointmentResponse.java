package com.HMS.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomAppointmentResponse {
    private String message;
    private Appointment appointment;
    private String doctorFullName;
    private String doctorDegree;
    private String doctorQualification;
}
