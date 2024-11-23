package com.HMS.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.HMS.model.Doctor;
import com.HMS.model.User;
@Repository
public interface DoctorRepo extends JpaRepository<Doctor, java.lang.Long> {

	Optional<Doctor> findByUser(User user);

}
