package com.cgi.model;

import jakarta.persistence.*;
import java.util.Date;

import com.cgi.model.RTOOfficer;

@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String testNumber;

    @Temporal(TemporalType.DATE)
    private Date testDate;

    private String timeSlot;
    private String testResult;

    // Relationship: One appointment belongs to 1 application
    @OneToOne
    @JoinColumn(name = "application_id")
    private Application application;

    // Relationship: approved by RTOOfficer
    @ManyToOne
    @JoinColumn(name = "officer_id")
    private RTOOfficer approver;

    // Constructors
    public Appointment() {}

    public Appointment(String testNumber, Date testDate, String timeSlot, String testResult) {
        this.testNumber = testNumber;
        this.testDate = testDate;
        this.timeSlot = timeSlot;
        this.testResult = testResult;
    }

    // Getters & Setters
    public Long getId() { return id; }

    public String getTestNumber() { return testNumber; }
    public void setTestNumber(String testNumber) { this.testNumber = testNumber; }

    public Date getTestDate() { return testDate; }
    public void setTestDate(Date testDate) { this.testDate = testDate; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public String getTestResult() { return testResult; }
    public void setTestResult(String testResult) { this.testResult = testResult; }

    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }

    public RTOOfficer getApprover() { return approver; }
    public void setApprover(RTOOfficer approver) { this.approver = approver; }

	public void setOfficer(RTOOfficer officer) {
		// TODO Auto-generated method stub
	    this.approver = officer;
		
	}
}

