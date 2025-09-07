package com.cgi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Documents")
public class Documents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;

    private String photo;
    private String idProof;
    private String addressProof;

    // Constructors
    public Documents() {}

    public Documents(String photo, String idProof, String addressProof) {
        this.photo = photo;
        this.idProof = idProof;
        this.addressProof = addressProof;
    }

    // Getters & Setters
    public Long getDocumentId() { return documentId; }
    public void setDocumentId(Long documentId) { this.documentId = documentId; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public String getIdProof() { return idProof; }
    public void setIdProof(String idProof) { this.idProof = idProof; }

    public String getAddressProof() { return addressProof; }
    public void setAddressProof(String addressProof) { this.addressProof = addressProof; }
}
