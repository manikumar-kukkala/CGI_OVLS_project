package com.cgi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
@Table(name = "applicants")
public class Applicant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicantId;

    @JsonProperty("learner_license_status")
    private String learnerLicenseStatus;

    @JsonProperty("driving_license_status")
    private String drivingLicenseStatus;


    @ManyToOne
    private User user;
    @OneToOne
    private Application application;


    // Constructors
    public Applicant() {
    }

    public Applicant(User user, String learnerLicenseStatus, String drivingLicenseStatus) {
        this.user = user;
        this.learnerLicenseStatus = learnerLicenseStatus;
        this.drivingLicenseStatus = drivingLicenseStatus;
    }

    // Getters & Setters
    public Long getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(Long applicantId) {
        this.applicantId = applicantId;
    }

    public String getLearnerLicenseStatus() {
        return learnerLicenseStatus;
    }

    public void setLearnerLicenseStatus(String learnerLicenseStatus) {
        this.learnerLicenseStatus = learnerLicenseStatus;
    }

    public String getDrivingLicenseStatus() {
        return drivingLicenseStatus;
    }

    public void setDrivingLicenseStatus(String drivingLicenseStatus) {
        this.drivingLicenseStatus = drivingLicenseStatus;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // Convenience methods to get user details
    public String getFirstName() {
        return user != null ? user.getName() : null;
    }

    public String getEmail() {
        return user != null ? user.getEmail() : null;
    }

    @Override
    public String toString() {
        return "Applicant{" +
                "applicantId=" + applicantId +
                ", learnerLicenseStatus='" + learnerLicenseStatus + '\'' +
                ", drivingLicenseStatus='" + drivingLicenseStatus + '\'' +
                ", user=" + (user != null ? user.getEmail() : null) +
                '}';
    }
}
