package com.cgi.service.impl;

import com.cgi.model.Applicant;
import com.cgi.model.Application;
import com.cgi.model.ApplicationStatus;
import com.cgi.model.ApplicationType;
import com.cgi.model.Appointment;
import com.cgi.model.DrivingLicense;
import com.cgi.model.RTOOffice;
import com.cgi.model.RTOOfficer;
import com.cgi.repository.ApplicationRepository;
import com.cgi.repository.AppointmentRepository;
import com.cgi.repository.DrivingLicenseRepository;
import com.cgi.repository.RTOOfficeRepository;
import com.cgi.repository.RTOOfficerRepository;
import com.cgi.service.RTOOfficerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cgi.model.ApplicationStatus;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class RTOOfficerServiceImpl implements RTOOfficerService {

    @Autowired
    private RTOOfficerRepository officerRepo;

    @Autowired
    private RTOOfficeRepository officeRepository;

    @Autowired
    private ApplicationRepository applicationRepo;

    @Autowired
    private DrivingLicenseRepository licenseRepo;

    @Autowired
    private AppointmentRepository appointmentRepo;

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
        Long appId = Long.parseLong(applicationId);
        Optional<Application> optionalApp = applicationRepo.findById(appId);

        if (optionalApp.isEmpty()) {
            throw new RuntimeException("Application not found");
        }

        Application application = optionalApp.get();

        // ✅ Correct guard: must be APPROVED *and* of type DL, otherwise block
        if (application.getApplicationStatus() != ApplicationStatus.APPROVED ||
                application.getLicenceType() != ApplicationType.DL) {
            throw new RuntimeException("Application not approved for permanent license");
        }

        RTOOffice rtoOffice = application.getRtoOffice();
        if (rtoOffice == null) {
            throw new RuntimeException("No RTO office associated with this application");
        }

        DrivingLicense license = new DrivingLicense();
        license.setApplication(application);
        license.setIssuedBy(rtoOffice);
        license.setLicenseNumber("DL-" + java.util.UUID.randomUUID().toString().substring(0, 8));

        java.time.LocalDate issueDate = java.time.LocalDate.now();
        license.setDateOfIssue(java.sql.Date.valueOf(issueDate));
        license.setValidTill(java.sql.Date.valueOf(issueDate.plusYears(20)));

        // Mark application as LICENSE_ISSUED (ensure enum has this constant)
        application.setApplicationStatus(ApplicationStatus.LICENSE_ISSUED);
        applicationRepo.save(application);

        return licenseRepo.save(license);
    }

    @Override
    public String emailLicense(DrivingLicense license) {
        // TODO: send email logic
        return "License emailed successfully to " + license.getApplication().getApplicant().getUser().getEmail();
    }

    @Override
    public RTOOfficer addOfficer(RTOOfficer officer) {
        if (officer.getRtoOffice() != null && officer.getRtoOffice().getRtoId() != 0) {
            // fetch existing office from DB
            RTOOffice office = officeRepository.findById(officer.getRtoOffice().getRtoId())
                    .orElseThrow(() -> new RuntimeException(
                            "RTO Office not found with id " + officer.getRtoOffice().getRtoId()));
            officer.setRtoOffice(office);
        }
        return officerRepo.save(officer);
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
    @Transactional
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

            // Save the app first
            Application savedApp = applicationRepo.save(app);

            // If application is APPROVED and license type is DL, generate license
            // automatically
            if (ApplicationStatus.valueOf(s) == ApplicationStatus.APPROVED &&
                    app.getLicenceType() == ApplicationType.DL) {

                try {
                    generateDrivingLicense(String.valueOf(app.getApplicationId()));
                } catch (RuntimeException e) {
                    // Log error, but don’t break the review process
                    System.err.println("License generation failed: " + e.getMessage());
                }
            }

            return savedApp;
        }).orElse(null);
    }

    @Override
    public Optional<RTOOfficer> findById(int rtoId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }
}