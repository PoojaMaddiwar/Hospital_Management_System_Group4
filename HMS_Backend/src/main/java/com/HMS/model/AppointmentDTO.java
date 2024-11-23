package com.HMS.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDTO {
	private Long AppointmentId;
    private Long patientId;
    private Long doctorId;
    private LocalDateTime appointmentDate;
    private String description;
}
