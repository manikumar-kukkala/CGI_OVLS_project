package com.cgi.controller;

import com.cgi.model.Application;
import com.cgi.model.DrivingLicense;
import com.cgi.model.RTOOfficer;
import com.cgi.service.RTOOfficerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rto/officer")
public class RTOOfficerController {

    @Autowired
    private RTOOfficerService officerService;
    @PostMapping("/add")
    public RTOOfficer addOfficer(@RequestBody RTOOfficer officer) {
        return officerService.addOfficer(officer);
    }


    @PostMapping("/login")
    public String login(@RequestBody RTOOfficer officer) {
        return officerService.officeLogin(officer);
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
}
