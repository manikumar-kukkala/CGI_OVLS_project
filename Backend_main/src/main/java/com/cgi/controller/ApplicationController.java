package com.cgi.controller;

import com.cgi.model.Applicant;
import com.cgi.model.Application;
import com.cgi.model.Documents;
import com.cgi.repository.ApplicantRepository;
import com.cgi.repository.ApplicationRepository;
import com.cgi.repository.DocumentsRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private DocumentsRepository documentsRepository;

    @PostMapping
    public ResponseEntity<Application> createApplication(@RequestBody Application appRequest) {
        // fetch Applicant from DB
        Applicant applicant = applicantRepository.findById(appRequest.getApplicant().getApplicantId())
                .orElseThrow(() -> new RuntimeException("Applicant not found"));

        // fetch Documents from DB (optional if you provide it)
        Documents documents = null;
        if (appRequest.getDocuments() != null && appRequest.getDocuments().getDocumentId() != 0) {
            Long docId = Long.valueOf(appRequest.getDocuments().getDocumentId());
            documents = documentsRepository.findById(docId)
                    .orElseThrow(() -> new RuntimeException("Document not found"));
        }


        // create new Application
        Application application = new Application();
        application.setApplicationNumber(appRequest.getApplicationNumber());
        application.setApplicationDate(appRequest.getApplicationDate());
        application.setModeOfPayment(appRequest.getModeOfPayment());
        application.setPaymentStatus(appRequest.getPaymentStatus());
        application.setRemarks(appRequest.getRemarks());
        application.setStatus("PENDING"); // default
        application.setApplicant(applicant);
        application.setDocuments(documents);

        Application saved = applicationRepository.save(application);
        return ResponseEntity.ok(saved);
    }
}
