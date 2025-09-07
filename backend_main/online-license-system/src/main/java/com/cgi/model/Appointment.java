package com.cgi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "appointment")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String testNumber;

    // Use LocalDate without @Temporal; add JSON format for readability
    @Column(name = "test_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate testDate;

    private String timeSlot;
    private String testResult;

    // One appointment belongs to one application
    @OneToOne
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    // Approved by RTOOfficer
    @ManyToOne
    @JoinColumn(name = "officer_id")
    private RTOOfficer approver;

    public Appointment() {
    }

    public Appointment(String testNumber, LocalDate testDate, String timeSlot, String testResult) {
        this.testNumber = testNumber;
        this.testDate = testDate;
        this.timeSlot = timeSlot;
        this.testResult = testResult;
    }

    public Long getId() {
        return id;
    }

    public String getTestNumber() {
        return testNumber;
    }

    public void setTestNumber(String testNumber) {
        this.testNumber = testNumber;
    }

    public LocalDate getTestDate() {
        return testDate;
    }

    public void setTestDate(LocalDate testDate) {
        this.testDate = testDate;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public RTOOfficer getApprover() {
        return approver;
    }

    public void setApprover(RTOOfficer approver) {
        this.approver = approver;
    }

    // ❌ REMOVE these buggy methods — Jackson will call them and crash
    // public void setOfficer(RTOOfficer officer) { this.approver = officer; }
    // public Object getOfficerId() { throw new
    // UnsupportedOperationException("Unimplemented method 'getOfficerId'"); }
}
