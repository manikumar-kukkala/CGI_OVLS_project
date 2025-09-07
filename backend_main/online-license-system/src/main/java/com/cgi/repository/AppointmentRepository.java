package com.cgi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cgi.model.Application;
import com.cgi.model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Custom finder method to fetch appointment by application
    Appointment findByApplication(Application application);
}
