package com.cgi.controller;

import com.cgi.model.Application;
import com.cgi.model.Appointment;
import com.cgi.model.RTOOfficer;
import com.cgi.repository.ApplicationRepository;
import com.cgi.repository.AppointmentRepository;
import com.cgi.repository.RTOOfficerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
  

    private final AppointmentRepository appointmentRepository;
    private final RTOOfficerRepository officerRepository;
    private final ApplicationRepository applicationRepository;
    @Autowired
    public AppointmentController(
            AppointmentRepository appointmentRepository,
            RTOOfficerRepository officerRepository,
            ApplicationRepository applicationRepository) {
        this.appointmentRepository = appointmentRepository;
        this.officerRepository = officerRepository;
        this.applicationRepository = applicationRepository;
    }



    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }
    @PostMapping
    public ResponseEntity<Appointment> addAppointment(@RequestBody Appointment appointment) {
        // Make sure the related entities are managed
        if (appointment.getApprover() != null) {
        	RTOOfficer officer = officerRepository.findById((long) appointment.getApprover().getId())
        	        .orElseThrow(() -> new RuntimeException("Officer not found"));

            appointment.setApprover(officer);
        }

        if (appointment.getApplication() != null) {
            Application application = applicationRepository.findById(appointment.getApplication().getApplicationId())
                .orElseThrow(() -> new RuntimeException("Application not found"));
            appointment.setApplication(application);
        }

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return ResponseEntity.ok(savedAppointment);
    }


}
