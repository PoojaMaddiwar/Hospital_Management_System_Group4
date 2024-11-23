package com.HMS.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.HMS.model.Appointment;
import com.HMS.model.AppointmentStatus;
import com.HMS.model.Doctor;
import com.HMS.model.RoleType;
import com.HMS.model.User;
import com.HMS.repository.AppointmentRepo;
import com.HMS.repository.DoctorRepo;
import com.HMS.repository.UserRepo;

@Service
public class AppointmentService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private DoctorRepo doctorRepo;

    @Autowired
    private AppointmentRepo appointmentRepo;

    /**
     * Updates the status of an appointment dynamically based on the current date.
     */
    private void updateAppointmentStatus(Appointment appointment) {
        LocalDate today = LocalDate.now();
        LocalDate appointmentDate = appointment.getAppointmentDate().toLocalDate();

        if (appointmentDate.isEqual(today)) {
            appointment.setStatus(AppointmentStatus.TODAYS_APPOINTMENT);
        } else if (appointmentDate.isAfter(today)) {
            appointment.setStatus(AppointmentStatus.UPCOMING);
        } else {
            appointment.setStatus(AppointmentStatus.CANCELLED);
        }
    }

    /**
     * Create a new appointment.
     */
    public Appointment createAppointment(String email, Long doctorId, LocalDateTime appointmentDate, String description) {
        Optional<User> patientOpt = userRepo.findByEmail(email);
        Optional<Doctor> doctorOpt = doctorRepo.findById(doctorId);

        if (patientOpt.isEmpty() || doctorOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid Patient or Doctor ID.");
        }

        if (appointmentDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Appointments cannot be scheduled in the past.");
        }

        // Validate the appointment slot
        validateAppointmentDate(doctorId, appointmentDate);

        Appointment appointment = new Appointment();
        appointment.setPatient(patientOpt.get());
        appointment.setDoctor(doctorOpt.get());
        appointment.setAppointmentDate(appointmentDate);
        appointment.setDescription(description);

        updateAppointmentStatus(appointment); // Set initial status dynamically
        return appointmentRepo.save(appointment);
    }

    /**
     * Retrieve all booked date and time slots for a specific doctor.
     */
    public List<LocalDateTime> getBookedDateAndTime(Long doctorId) {
        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + doctorId));

        return appointmentRepo.findByDoctorAndStatus(doctor, AppointmentStatus.UPCOMING).stream()
                .map(Appointment::getAppointmentDate)
                .collect(Collectors.toList());
    }

    /**
     * Validates if the given date and time are already booked for the doctor.
     */
    public void validateAppointmentDate(Long doctorId, LocalDateTime appointmentDate) {
        if (getBookedDateAndTime(doctorId).contains(appointmentDate)) {
            throw new IllegalArgumentException("This appointment slot is already booked. Please choose another.");
        }
    }

    /**
     * Retrieve appointments for a patient by email.
     */
    public List<Appointment> getAppointmentsForPatient(String email) {
        User patient = userRepo.findByEmailAndRoles_Name(email, RoleType.PATIENT);
        if (patient == null) {
            throw new IllegalArgumentException("The logged-in user is not a patient.");
        }

        return appointmentRepo.findByPatient(patient).stream()
                .peek(this::updateAppointmentStatus) // Dynamically update status
                .collect(Collectors.toList());
    }

    /**
     * Retrieve appointments for a doctor by email.
     */
    public List<Appointment> getAppointmentsForDoctor(String email) {
        User doctorUser = userRepo.findByEmailAndRoles_Name(email, RoleType.DOCTOR);
        if (doctorUser == null) {
            throw new IllegalArgumentException("The logged-in user is not a doctor.");
        }

        Doctor doctor = doctorUser.getDoctor();
        if (doctor == null) {
            throw new IllegalArgumentException("Doctor details not found.");
        }

        return appointmentRepo.findByDoctor_User_Email(doctor).stream()
                .peek(this::updateAppointmentStatus) // Dynamically update status
                .collect(Collectors.toList());
    }

    /**
     * Reschedule an appointment for a patient.
     */
    public Appointment rescheduleAppointment(Long appointmentId, String patientEmail, LocalDateTime newAppointmentDate) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        if (!appointment.getPatient().getEmail().equals(patientEmail)) {
            throw new IllegalArgumentException("Unauthorized reschedule attempt.");
        }

        if (newAppointmentDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot reschedule to a past date.");
        }

        validateAppointmentDate(appointment.getDoctor().getDoctorId(), newAppointmentDate);
        appointment.setAppointmentDate(newAppointmentDate);

        updateAppointmentStatus(appointment);
        return appointmentRepo.save(appointment);
    }

    /**
     * Cancel an appointment for a patient.
     */
    public Appointment cancelAppointment(Long appointmentId, String patientEmail) {
        Appointment appointment = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        if (!appointment.getPatient().getEmail().equals(patientEmail)) {
            throw new IllegalArgumentException("Unauthorized cancellation attempt.");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        return appointmentRepo.save(appointment);
    }

    /**
     * Cancel all appointments for an inactive doctor.
     */
    public void cancelAppointmentsByDoctor(Doctor doctor) {
        List<Appointment> appointments = appointmentRepo.findByDoctorAndAppointmentDateAfter(doctor, LocalDateTime.now());
        for (Appointment appointment : appointments) {
            appointment.setStatus(AppointmentStatus.CANCELLED);
            appointmentRepo.save(appointment);
        }
    }

    /**
     * Retrieve the authenticated user's email.
     */
    public String getAuthenticatedUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
