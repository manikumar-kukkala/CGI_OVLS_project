package com.cgi.model;

import jakarta.persistence.*;

@Entity
@Table(name = "documents") // use lowercase, safer on Linux/MySQL
public class Documents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long documentId;

    @Column(name = "photo")
    private String photo;

    @Column(name = "id_proof")
    private String idProof;

    @Column(name = "address_proof")
    private String addressProof;

    public Documents() {
    }

    public Documents(String photo, String idProof, String addressProof) {
        this.photo = photo;
        this.idProof = idProof;
        this.addressProof = addressProof;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getIdProof() {
        return idProof;
    }

    public void setIdProof(String idProof) {
        this.idProof = idProof;
    }

    public String getAddressProof() {
        return addressProof;
    }

    public void setAddressProof(String addressProof) {
        this.addressProof = addressProof;
    }
}
