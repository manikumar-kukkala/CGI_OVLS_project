package com.cgi.model;

import jakarta.persistence.*;
import java.util.Date;

import com.cgi.model.RTOOffice;

@Entity
@Table(name = "driving_license")
public class DrivingLicense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String licenseNumber;

    @Temporal(TemporalType.DATE)
    private Date dateOfIssue;

    @Temporal(TemporalType.DATE)
    private Date validTill;

    // Relationship: One driving license belongs to 1 application
    @OneToOne
    @JoinColumn(name = "application_id")
    private Application application;

    // Relationship: issued by RTOOffice
    @ManyToOne
    @JoinColumn(name = "rto_office_id")
    private RTOOffice issuedBy;

    // Constructors
    public DrivingLicense() {}

    public DrivingLicense(String licenseNumber, Date dateOfIssue, Date validTill) {
        this.licenseNumber = licenseNumber;
        this.dateOfIssue = dateOfIssue;
        this.validTill = validTill;
    }

    // Getters & Setters
    public Long getId() { return id; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public Date getDateOfIssue() { return dateOfIssue; }
    public void setDateOfIssue(Date dateOfIssue) { this.dateOfIssue = dateOfIssue; }

    public Date getValidTill() { return validTill; }
    public void setValidTill(Date validTill) { this.validTill = validTill; }

    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }

    public RTOOffice getIssuedBy() { return issuedBy; }
    public void setIssuedBy(RTOOffice issuedBy) { this.issuedBy = issuedBy; }

	public void setApplicant(Applicant applicant) {
		// TODO Auto-generated method stub
		
	}


}

