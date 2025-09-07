package com.cgi.model;

import com.cgi.model.Applicant;
import jakarta.persistence.*;

@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String state;
    private String city;
    private String house;
    private String landmark;
    private String pincode;

    // Relationship: Many addresses can belong to 1 applicant
    @ManyToOne
    @JoinColumn(name = "applicant_id")
    private Applicant applicant;

    // Constructors
    public Address() {}

    public Address(String state, String city, String house, String landmark, String pincode) {
        this.state = state;
        this.city = city;
        this.house = house;
        this.landmark = landmark;
        this.pincode = pincode;
    }

    // Getters & Setters
    public Long getId() { return id; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getHouse() { return house; }
    public void setHouse(String house) { this.house = house; }

    public String getLandmark() { return landmark; }
    public void setLandmark(String landmark) { this.landmark = landmark; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public Applicant getApplicant() { return applicant; }
    public void setApplicant(Applicant applicant) { this.applicant = applicant; }
}
