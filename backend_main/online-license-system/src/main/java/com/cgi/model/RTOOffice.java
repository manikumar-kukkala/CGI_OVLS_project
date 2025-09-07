package com.cgi.model;

import jakarta.persistence.*;

import java.util.Set;

import com.cgi.model.RTOOfficer;

@Entity
@Table(name = "rto_office")
public class RTOOffice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rto_id")
    private int rtoId;

    @Column(name = "rto_name", nullable = false)
    private String rtoName;

    @OneToMany(mappedBy = "rtoOffice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<RTOOfficer> officers;

    
    public RTOOffice() {}

    public RTOOffice(String rtoName) {
        this.rtoName = rtoName;
    }

    public int getRtoId() { return rtoId; }
    public void setRtoId(int rtoId) { this.rtoId = rtoId; }

    public String getRtoName() { return rtoName; }
    public void setRtoName(String rtoName) { this.rtoName = rtoName; }

    public Set<RTOOfficer> getOfficers() { return officers; }
    public void setOfficers(Set<RTOOfficer> officers) { this.officers = officers; }
}
