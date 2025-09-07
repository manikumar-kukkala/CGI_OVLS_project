package com.cgi.service.impl;

import com.cgi.model.Applicant;
import com.cgi.model.Application;
import com.cgi.model.Appointment;
import com.cgi.model.DrivingLicense;
import com.cgi.model.RTOOfficer;
import com.cgi.repository.ApplicationRepository;
import com.cgi.repository.AppointmentRepository;
import com.cgi.repository.DrivingLicenseRepository;
import com.cgi.repository.RTOOfficerRepository;
import com.cgi.service.RTOOfficerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RTOOfficerServiceImpl implements RTOOfficerService {

    @Autowired
    private RTOOfficerRepository officerRepo;
    @Autowired
    private ApplicationRepository applicationRepo;
    @Autowired
    private DrivingLicenseRepository licenseRepo;
    @Autowired
    private AppointmentRepository appointmentRepo;
    @Autowired
    private RTOOfficerRepository officerRepository;

    @Override
    public boolean officeLogin(RTOOfficer officer) {
        Optional<RTOOfficer> dbOfficer = officerRepo.findByEmailAndPassword(
                officer.getEmail(), officer.getPassword());
        return dbOfficer.isPresent() ? true : false;
    }

    @Override
    public List<Application> viewAllPendingApplications() {
        return applicationRepo.findByStatus("PENDING");
    }

    @Override
    public List<Application> viewAllRejectedApplications() {
        return applicationRepo.findByStatus("REJECTED");
    }

    @Override
    public List<Application> viewAllApprovedApplications() {
        return applicationRepo.findByStatus("APPROVED");
    }

    @Override
    public Application viewApplicationById(String applicationId) {
        return applicationRepo.findById(Long.parseLong(applicationId)).orElse(null);
    }

    @Override
    public String checkChallanByVehicleNumber(String vehicleNumber) {
        // TODO: link challan repo
        return "Checked challan for vehicle: " + vehicleNumber;
    }

    @Override
    public String checkAllChallan() {
        return "All challans checked";
    }

    @Override
    public Application modifyTestResultById(String applicationId) {
        Optional<Application> appOpt = applicationRepo.findById(Long.parseLong(applicationId));
        if (appOpt.isPresent()) {
            Application application = appOpt.get();

            Appointment appointment = appointmentRepo.findByApplication(application);
            if (appointment != null) {
                appointment.setTestResult("PASSED"); // or "FAILED"
                appointmentRepo.save(appointment);
            }

            return application;
        }
        return null;
    }

    @Override
    public Application generateLearnerLicense(String applicationId) {
        Optional<Application> app = applicationRepo.findById(Long.parseLong(applicationId));
        if (app.isPresent()) {
            Application application = app.get();

            // Update applicant's learner license status
            Applicant applicant = application.getApplicant();
            applicant.setLearnerLicenseStatus("APPROVED");

            // Save application (it will also update applicant if cascade is set properly)
            return applicationRepo.save(application);
        }
        return null;
    }

    @Override
    public DrivingLicense generateDrivingLicense(String applicationId) {
        Optional<Application> app = applicationRepo.findById(Long.parseLong(applicationId));
        if (app.isPresent()) {
            DrivingLicense dl = new DrivingLicense();
            dl.setApplication(app.get());
            dl.setLicenseNumber("DL-" + System.currentTimeMillis());
            return licenseRepo.save(dl);
        }
        return null;
    }

    @Override
    public String emailLicense(DrivingLicense license) {
        // TODO: send email logic
        return "License emailed successfully to " + license.getApplication().getApplicant().getUser().getEmail();
    }

    @Override
    public RTOOfficer addOfficer(RTOOfficer officer) {
        // TODO Auto-generated method stub
        return officerRepository.save(officer);
    }

    @Override
    public List<Application> getAllApplications() {
        return applicationRepo.findAll();
    }

    @Override
    public Map<String, Long> getStats() {
        long approved = applicationRepo.countByStatus("APPROVED");
        long rejected = applicationRepo.countByStatus("REJECTED");
        long pending = applicationRepo.countByStatus("PENDING");
        long total = applicationRepo.count();
        Map<String, Long> m = new HashMap<>();
        m.put("approved", approved);
        m.put("rejected", rejected);
        m.put("pending", pending);
        m.put("total", total);
        return m;
    }

    @Override
    public Application reviewApplication(Long id, String status, String reviewedBy) {
        return applicationRepo.findById(id).map(app -> {
            // normalize to uppercase; default to PENDING if unknown
            String s = (status == null) ? "PENDING" : status.trim().toUpperCase();
            if (!s.equals("APPROVED") && !s.equals("REJECTED"))
                s = "PENDING";
            app.setStatus(s);
            // optional: record who reviewed in remarks
            String note = (app.getRemarks() == null ? "" : app.getRemarks() + " | ");
            app.setRemarks(note + "Reviewed by " + reviewedBy);
            return applicationRepo.save(app);
        }).orElse(null);
    }
}
