package com.HMS.controller;

import com.HMS.model.Appointment;
import com.HMS.model.AppointmentDTO;
import com.HMS.model.CustomAppointmentResponse;
import com.HMS.model.Doctor;
import com.HMS.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    // Endpoint to book a new appointment
    @PostMapping("/book")
    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<CustomAppointmentResponse> createAppointment(
            @RequestBody AppointmentDTO appointmentDTO, Principal principal) {

        String email = principal.getName(); // Get the logged-in user's email
        Appointment appointment = appointmentService.createAppointment(
                email,
                appointmentDTO.getDoctorId(),
                appointmentDTO.getAppointmentDate(),
                appointmentDTO.getDescription()
        );

        // Prepare response with doctor's details
        Doctor doctor = appointment.getDoctor();
        String doctorFullName = doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName();
        String doctorDegree = doctor.getDegree();
        String doctorQualification = doctor.getQualification();

        CustomAppointmentResponse response = new CustomAppointmentResponse(
                "Appointment created successfully!",
                appointment,
                doctorFullName,
                doctorDegree,
                doctorQualification
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint to get booked dates for a specific doctor
    @GetMapping("/booked-dates/{doctorId}")
    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<List<LocalDateTime>> getBookedDatesAndTimes(@PathVariable Long doctorId) {
        List<LocalDateTime> bookedDates = appointmentService.getBookedDateAndTime(doctorId);
        return ResponseEntity.ok(bookedDates);
    }

    // Endpoint for doctors to view their appointments
    @GetMapping("/doctor")
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<List<Appointment>> getAppointmentsForDoctor(Principal principal) {
        String email = principal.getName(); // Logged-in doctor's email
        List<Appointment> appointments = appointmentService.getAppointmentsForDoctor(email);

        return appointments != null && !appointments.isEmpty()
                ? ResponseEntity.ok(appointments)
                : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Endpoint for patients to view their appointments
    @GetMapping("/patient")
    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<List<Appointment>> getAppointmentsForPatient(Principal principal) {
        String email = principal.getName(); // Logged-in patient's email
        List<Appointment> appointments = appointmentService.getAppointmentsForPatient(email);

        return appointments != null && !appointments.isEmpty()
                ? ResponseEntity.ok(appointments)
                : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Endpoint to reschedule an appointment
    @PutMapping("/reschedule")
    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<CustomAppointmentResponse> rescheduleAppointment(
            @RequestBody AppointmentDTO appointmentDTO, Principal principal) {

        String email = principal.getName(); // Logged-in patient's email
        Appointment updatedAppointment = appointmentService.rescheduleAppointment(
                appointmentDTO.getAppointmentId(),
                email,
                appointmentDTO.getAppointmentDate()
        );

        // Prepare response with doctor's details
        Doctor doctor = updatedAppointment.getDoctor();
        String doctorFullName = doctor.getUser().getFirstName() + " " + doctor.getUser().getLastName();
        String doctorDegree = doctor.getDegree();
        String doctorQualification = doctor.getQualification();

        CustomAppointmentResponse response = new CustomAppointmentResponse(
                "Appointment rescheduled successfully!",
                updatedAppointment,
                doctorFullName,
                doctorDegree,
                doctorQualification
        );

        return ResponseEntity.ok(response);
    }

    // Endpoint to cancel an appointment
    @DeleteMapping("/cancel")
    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<String> cancelAppointment(@RequestBody AppointmentDTO appointmentDTO, Principal principal) {
        String email = principal.getName(); // Logged-in patient's email
        appointmentService.cancelAppointment(appointmentDTO.getAppointmentId(), email);
        return ResponseEntity.ok("Appointment canceled successfully!");
    }
}
