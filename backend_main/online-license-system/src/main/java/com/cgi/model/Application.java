package com.cgi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Application")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    @Column(unique = true, nullable = false)
    private String applicationNumber;

    @Transient 
     @Column(name = "applicant_name") 
    private String applicantName;

    private String applicationDate;
    private String modeOfPayment;
    private String paymentStatus;
    private String remarks;
    private String status;

    // Relationships
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "applicant_id")
    private Applicant applicant;

    @ManyToOne
    @JoinColumn(name = "document_id")
    private Documents documents;

    // Constructors
    public Application() {
    }

    public Application(String applicationNumber, String applicantName, String applicationDate,
                       String modeOfPayment, String paymentStatus, String remarks,
                       Applicant applicant, Documents documents, String status) {
        this.applicationNumber = applicationNumber;
        this.applicantName = applicantName;
        this.applicationDate = applicationDate;
        this.modeOfPayment = modeOfPayment;
        this.paymentStatus = paymentStatus;
        this.remarks = remarks;
        this.applicant = applicant;
        this.documents = documents;
        this.status = status;
    }

    // Getters & Setters
    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getApplicantName() {
        // derive from applicant → user
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        // not persisted, just to satisfy JSON setter
        this.applicantName = applicantName;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(String applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getModeOfPayment() {
        return modeOfPayment;
    }

    public void setModeOfPayment(String modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public Documents getDocuments() {
        return documents;
    }

    public void setDocuments(Documents documents) {
        this.documents = documents;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

