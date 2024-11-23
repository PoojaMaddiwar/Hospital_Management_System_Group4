package com.HMS.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.HMS.model.Appointment;
import com.HMS.model.AppointmentStatus;
import com.HMS.model.Doctor;
import com.HMS.model.User;
@Repository
public interface AppointmentRepo extends JpaRepository<Appointment, Long> {
	List<Appointment> findByDoctor(User doctor);
    List<Appointment> findByPatient(User patient);
    
    List<Appointment> findByDoctor_User_Email(Doctor doctor);
    List<Appointment> findByDoctorAndAppointmentDateAfter(Doctor doctor, LocalDateTime date);
    List<Appointment> findByDoctorAndStatus(Doctor doctor, AppointmentStatus status);

    
}
