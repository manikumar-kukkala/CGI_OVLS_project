package com.cgi.controller;

import com.cgi.model.Applicant;
import com.cgi.model.Application;
import com.cgi.model.Documents;
import com.cgi.repository.ApplicantRepository;
import com.cgi.repository.ApplicationRepository;
import com.cgi.repository.DocumentsRepository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin(origins = "http://localhost:4200", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.PATCH, RequestMethod.DELETE,
        RequestMethod.OPTIONS }, allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/applications")
public class ApplicationController {

    @Autowired
    private ApplicationRepository applicationRepository;
    @Autowired
    private ApplicantRepository applicantRepository;
    @Autowired
    private DocumentsRepository documentsRepository;

    @GetMapping("/user")
    public List<Application> byUser(@RequestParam String email) {
        return applicationRepository.findByApplicantUserEmailOrderByApplicationDateDesc(email);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Application> createApplication(@RequestBody Application appRequest) {

        Applicant applicant = resolveApplicant(appRequest.getApplicant());
        Documents documents = resolveDocuments(appRequest.getDocuments());

        Application application = new Application();
        application.setApplicationNumber(appRequest.getApplicationNumber());
        application.setApplicationDate(appRequest.getApplicationDate());
        application.setModeOfPayment(appRequest.getModeOfPayment());
        application.setPaymentStatus(appRequest.getPaymentStatus());
        application.setRemarks(appRequest.getRemarks());
        application.setStatus(appRequest.getStatus());
        application.setApplicant(applicant);
        application.setDocuments(documents);
        // ✅ Prefer incoming name; if missing, use relation's user.name if available
        String incomingName = appRequest.getApplicantName();
        if (incomingName != null && !incomingName.isBlank()) {
            application.setApplicantName(incomingName);
        } else if (applicant != null && applicant.getUser() != null) {
            application.setApplicantName(applicant.getUser().getName());
        } else {
            application.setApplicantName(null); // fine; @PrePersist may still fill if relation exists
        }

        Application saved = applicationRepository.save(application);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Use existing applicant when ID is present; otherwise create a new one.
     * If the provided ID does NOT exist, create a fresh row (do NOT throw).
     */
    private Applicant resolveApplicant(Applicant incoming) {
        if (incoming == null) {
            Applicant a = new Applicant();
            a.setLearnerLicenseStatus("NEW");
            a.setDrivingLicenseStatus("NEW");
            return applicantRepository.save(a);
        }
        Long id = incoming.getApplicantId();
        if (id != null && id > 0) {
            return applicantRepository.findById(id).orElseGet(() -> {
                // ID given but not found → create new, ignore client id
                Applicant a = new Applicant();
                a.setLearnerLicenseStatus(
                        incoming.getLearnerLicenseStatus() != null ? incoming.getLearnerLicenseStatus() : "NEW");
                a.setDrivingLicenseStatus(
                        incoming.getDrivingLicenseStatus() != null ? incoming.getDrivingLicenseStatus() : "NEW");
                a.setUser(incoming.getUser()); // optional
                return applicantRepository.save(a);
            });
        }
        // No id → create with whatever fields came
        if (incoming.getLearnerLicenseStatus() == null)
            incoming.setLearnerLicenseStatus("NEW");
        if (incoming.getDrivingLicenseStatus() == null)
            incoming.setDrivingLicenseStatus("NEW");
        return applicantRepository.save(incoming);
    }

    /**
     * Use existing documents when ID is present; otherwise create new.
     * If ID is provided but not found, create a fresh row with a NULL id.
     */
    private Documents resolveDocuments(Documents incoming) {
        if (incoming == null)
            return null;

        Long id = incoming.getDocumentId();
        if (id != null && id > 0) {
            return documentsRepository.findById(id).orElseGet(() -> {
                // ID given but not found → make a brand-new entity (id must be null)
                Documents d = new Documents();
                d.setIdProof(incoming.getIdProof());
                d.setPhoto(incoming.getPhoto());
                d.setAddressProof(incoming.getAddressProof());
                return documentsRepository.save(d);
            });
        }
        // No id → create new with the given fields
        return documentsRepository.save(incoming);
    }

    @GetMapping("/status/latest/by-user/{userId}")
    public ResponseEntity<ApplicationRepository.LatestStatus> latestStatusByUser(@PathVariable Long userId) {
        ApplicationRepository.LatestStatus view = applicationRepository.findLatestByUserId(userId);
        return (view == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(view);
    }

    @GetMapping("/by-number/{number}")
    public ResponseEntity<Application> byNumber(@PathVariable String number) {
        return applicationRepository
                .findFirstByApplicationNumberIgnoreCase(number)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Application not found"));
    }

    // 3) Keep path; validate & return most recent-first list for an email.
    @GetMapping("/user/email")
    public List<Application> byUserEmail(@RequestParam String email) {
        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email is required");
        }
        return applicationRepository.findByApplicantUserEmailOrderByApplicationDateDesc(email);
    }

    // 4) NEW (tiny): Provide GET /applications so your Angular fallback stops
    // getting 405.
    @GetMapping
    public List<ApplicationRepository.LatestStatus> listAll() {
        return applicationRepository.findAllWithApplicantName();
    }

    // --- DTO for payment patch ---
    public static class PaymentPatch {
        public String modeOfPayment; // "online" | "challan" (your strings)
        public String paymentStatus; // "Completed" | "Pending" | ...
    }

    @PatchMapping("/{id}/payment")
    @Transactional
    public ResponseEntity<Application> patchPayment(
            @PathVariable Long id,
            @RequestBody PaymentPatch body) {

        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        if (body.modeOfPayment != null)
            app.setModeOfPayment(body.modeOfPayment);
        if (body.paymentStatus != null)
            app.setPaymentStatus(body.paymentStatus);

        return ResponseEntity.ok(applicationRepository.save(app));
    }
}
