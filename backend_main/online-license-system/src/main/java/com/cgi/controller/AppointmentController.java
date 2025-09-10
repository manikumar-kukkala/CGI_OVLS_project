package com.cgi.controller;

import com.cgi.model.Application;
import com.cgi.model.Appointment;
import com.cgi.model.RTOOfficer;
import com.cgi.repository.ApplicationRepository;
import com.cgi.repository.AppointmentRepository;
import com.cgi.repository.RTOOfficerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/appointments") // class-level prefix
public class AppointmentController {

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
    public List<Appointment> getAll() {
        return appointmentRepository.findAll();
    }

    // DTO: officerId is Integer (matches repo)
    public static class AppointmentDTO {
        public Long applicationId; // Application is Long (BIGINT)
        public Integer officerId; // Officer is Integer (INT)
        public String testDate; // yyyy-MM-dd
        public String timeSlot;
        public String testNumber;
        public String testResult;
    }

    @PostMapping
    public ResponseEntity<Appointment> create(@RequestBody AppointmentDTO dto) {
        Appointment appt = new Appointment();

        if (dto.applicationId != null) {
            Application app = applicationRepository.findById(dto.applicationId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
            appt.setApplication(app);
        }

        if (dto.officerId != null) {
            RTOOfficer off = officerRepository.findById(dto.officerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Officer not found"));
            appt.setApprover(off);
        }

        if (dto.testDate != null)
            appt.setTestDate(LocalDate.parse(dto.testDate));
        appt.setTimeSlot(dto.timeSlot);
        appt.setTestNumber(dto.testNumber);
        appt.setTestResult(dto.testResult);

        return ResponseEntity.ok(appointmentRepository.save(appt));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Appointment> update(@PathVariable Long id, @RequestBody AppointmentDTO dto) {
        Appointment appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (dto.applicationId != null) {
            Application app = applicationRepository.findById(dto.applicationId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
            appt.setApplication(app);
        }

        if (dto.officerId != null) {
            RTOOfficer off = officerRepository.findById(dto.officerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Officer not found"));
            appt.setApprover(off);
        }

        if (dto.testDate != null)
            appt.setTestDate(LocalDate.parse(dto.testDate));
        if (dto.timeSlot != null)
            appt.setTimeSlot(dto.timeSlot);
        if (dto.testNumber != null)
            appt.setTestNumber(dto.testNumber);
        if (dto.testResult != null)
            appt.setTestResult(dto.testResult);

        return ResponseEntity.ok(appointmentRepository.save(appt));
    }
}

