package com.cgi.controller;

import com.cgi.model.Applicant;
import com.cgi.model.Application;
import com.cgi.model.ApplicationType;
import com.cgi.model.Documents;
import com.cgi.repository.ApplicantRepository;
import com.cgi.repository.ApplicationRepository;
import com.cgi.repository.DocumentsRepository;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
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

    // --------------------------------------------------------------------
    // READS
    // --------------------------------------------------------------------
    @GetMapping("/user")
    public List<Application> byUser(@RequestParam String email) {
        return applicationRepository.findByApplicantUserEmailOrderByApplicationDateDesc(email);
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

    // Angular list fallback
    @GetMapping
    public List<ApplicationRepository.LatestStatus> listAll() {
        return applicationRepository.findAllWithApplicantName();
    }

    // --------------------------------------------------------------------
    // CREATE
    // --------------------------------------------------------------------
    @PostMapping
    @Transactional
    public ResponseEntity<Application> createApplication(@RequestBody Application appRequest) {

        // ---- attach/create relations (user never needs IDs)
        Applicant applicant = resolveApplicant(appRequest.getApplicant());
        Documents documents = resolveDocuments(appRequest.getDocuments());

        // ---- basic validation
        if (appRequest.getLicenceType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "licenceType is required (LL/DL).");
        }

        // ---- Licence type rules
        if (appRequest.getLicenceType() == ApplicationType.DL) {
            List<Application> existing = applicationRepository
                    .findByApplicantApplicantIdOrderByApplicationDateDesc(applicant.getApplicantId());

            boolean hasApprovedLearning = existing.stream()
                    .anyMatch(app -> app.getLicenceType() == ApplicationType.LL &&
                            "APPROVED".equalsIgnoreCase(app.getStatus()));

            if (!hasApprovedLearning) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "You must have an approved Learning Licence before applying for a Permanent Licence.");
            }
        } else if (appRequest.getLicenceType() == ApplicationType.LL) {
            List<Application> existing = applicationRepository
                    .findByApplicantApplicantIdOrderByApplicationDateDesc(applicant.getApplicantId());

            boolean hasActiveLearning = existing.stream().anyMatch(app -> app.getLicenceType() == ApplicationType.LL &&
                    ("PENDING".equalsIgnoreCase(app.getStatus()) || "APPROVED".equalsIgnoreCase(app.getStatus())));
            if (hasActiveLearning) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "You already have an active Learning Licence application.");
            }
        }

        // ---- build entity
        Application application = new Application();
        application.setApplicationNumber(appRequest.getApplicationNumber());
        application.setApplicationDate(appRequest.getApplicationDate());
        application.setModeOfPayment(appRequest.getModeOfPayment());
        application.setPaymentStatus(appRequest.getPaymentStatus());
        application.setRemarks(appRequest.getRemarks());
        application.setStatus(safeUpper(appRequest.getStatus(), "PENDING"));
        application.setLicenceType(appRequest.getLicenceType());
        application.setApplicant(applicant);
        application.setDocuments(documents);

        // applicant_name column convenience
        String incomingName = appRequest.getApplicantName();
        if (incomingName != null && !incomingName.isBlank()) {
            application.setApplicantName(incomingName);
        } else if (applicant != null && applicant.getUser() != null) {
            application.setApplicantName(applicant.getUser().getName());
        } else {
            application.setApplicantName(null);
        }

        Application saved = applicationRepository.save(application);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // --------------------------------------------------------------------
    // PATCH: Payment
    // --------------------------------------------------------------------
    public static class PaymentPatch {
        public String modeOfPayment; // "online" | "challan" | ...
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

    // --------------------------------------------------------------------
    // PATCH: Review (Approve/Reject) with remarks (shown to user)
    // --------------------------------------------------------------------
    public static class ReviewPatch {
        public String status; // "APPROVED" | "REJECTED"
        public String reviewedBy; // e.g., "rto_officer"
        public String remarks; // optional note for the applicant
    }

    @PatchMapping("/{id}/review")
    @Transactional
    public ResponseEntity<Application> review(
            @PathVariable Long id,
            @RequestBody ReviewPatch body) {
        Application app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        String norm = safeUpper(body.status, null);
        if (!"APPROVED".equals(norm) && !"REJECTED".equals(norm)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status must be APPROVED or REJECTED");
        }

        app.setStatus(norm);
        if (body.remarks != null && !body.remarks.isBlank()) {
            app.setRemarks(body.remarks.trim()); // stored & later visible to user
        }
        // If you want to log reviewedBy somewhere, add a field and persist. For now we
        // ignore it.

        return ResponseEntity.ok(applicationRepository.save(app));
        // (Consider updating Applicant learner/driving status here if needed.)
    }

    // ====================================================================
    // Helpers
    // ====================================================================
    private String safeUpper(String s, String def) {
        if (s == null || s.isBlank())
            return def;
        return s.toUpperCase(Locale.ROOT);
    }

    /** use existing when id is present; otherwise create minimal row */
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
                Applicant a = new Applicant();
                a.setLearnerLicenseStatus(
                        incoming.getLearnerLicenseStatus() != null ? incoming.getLearnerLicenseStatus() : "NEW");
                a.setDrivingLicenseStatus(
                        incoming.getDrivingLicenseStatus() != null ? incoming.getDrivingLicenseStatus() : "NEW");
                a.setUser(incoming.getUser()); // optional
                return applicantRepository.save(a);
            });
        }
        if (incoming.getLearnerLicenseStatus() == null)
            incoming.setLearnerLicenseStatus("NEW");
        if (incoming.getDrivingLicenseStatus() == null)
            incoming.setDrivingLicenseStatus("NEW");
        return applicantRepository.save(incoming);
    }

    /** use existing when id present; otherwise create new when fields exist */
    private Documents resolveDocuments(Documents incoming) {
        if (incoming == null)
            return null;

        Long id = incoming.getDocumentId();
        if (id != null && id > 0) {
            return documentsRepository.findById(id).orElseGet(() -> {
                Documents d = new Documents();
                d.setIdProof(incoming.getIdProof());
                d.setPhoto(incoming.getPhoto());
                d.setAddressProof(incoming.getAddressProof());
                return documentsRepository.save(d);
            });
        }
        return documentsRepository.save(incoming);
    }
}
