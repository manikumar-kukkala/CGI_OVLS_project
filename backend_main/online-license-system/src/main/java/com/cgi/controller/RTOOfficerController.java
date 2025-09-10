package com.cgi.controller;

import com.cgi.model.Application;
import com.cgi.model.DrivingLicense;
import com.cgi.model.RTOOffice;
import com.cgi.model.RTOOfficer;
import com.cgi.repository.RTOOfficeRepository;
import com.cgi.service.RTOOfficerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/rto/officer")
public class RTOOfficerController {

    @Autowired
    private RTOOfficerService officerService;


@PostMapping("/add")
public ResponseEntity<?> addOfficer(@RequestBody RTOOfficer officer) {
    try {
        RTOOfficer savedOfficer = officerService.addOfficer(officer);
        return ResponseEntity.ok(savedOfficer);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Error: " + e.getMessage());
    }
}





    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody RTOOfficer officer) {
        boolean isValid = officerService.officeLogin(officer);

        if (isValid) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @GetMapping("/applications/pending")
    public List<Application> viewPending() {
        return officerService.viewAllPendingApplications();
    }

    @GetMapping("/applications/rejected")
    public List<Application> viewRejected() {
        return officerService.viewAllRejectedApplications();
    }

    @GetMapping("/applications/approved")
    public List<Application> viewApproved() {
        return officerService.viewAllApprovedApplications();
    }

    @GetMapping("/applications/{id}")
    public Application viewApplicationById(@PathVariable String id) {
        return officerService.viewApplicationById(id);
    }

    @PutMapping("/applications/{id}/test-result")
    public Application modifyTestResult(@PathVariable String id) {
        return officerService.modifyTestResultById(id);
    }

    @PutMapping("/applications/{id}/generate-ll")
    public Application generateLL(@PathVariable String id) {
        return officerService.generateLearnerLicense(id);
    }

    @PutMapping("/applications/{id}/generate-dl")
    public DrivingLicense generateDL(@PathVariable String id) {
        return officerService.generateDrivingLicense(id);
    }

    @PostMapping("/license/email")
    public String emailLicense(@RequestBody DrivingLicense license) {
        return officerService.emailLicense(license);
    }

    @GetMapping("/applications")
    public List<Application> getAllApplications() {
        return officerService.getAllApplications();
    }

    // NEW: stats for dashboard
    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        return officerService.getStats();
    }

    // NEW: approve/reject
    @PutMapping("/applications/{id}/review")
    public Application review(@PathVariable Long id, @RequestBody ReviewRequest req) {
        return officerService.reviewApplication(id, req.getStatus(), req.getReviewedBy());
    }

    public static class ReviewRequest {
        private String status; // "APPROVED" or "REJECTED"
        private String reviewedBy; // e.g., "admin"

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getReviewedBy() {
            return reviewedBy;
        }

        public void setReviewedBy(String reviewedBy) {
            this.reviewedBy = reviewedBy;
        }

    }
}
