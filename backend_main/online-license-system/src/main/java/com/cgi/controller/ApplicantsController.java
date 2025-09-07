package com.cgi.controller;

import com.cgi.model.Address;
import com.cgi.model.Applicant;
import com.cgi.model.User;
import com.cgi.repository.AddressRepository;
import com.cgi.repository.ApplicantRepository;
import com.cgi.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/applicants")
public class ApplicantsController {

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private UserRepository userRepository; // <-- inject it
    @Autowired
    private AddressRepository addressRepository;

    @PostMapping
    public Applicant addApplicant(@RequestBody Applicant applicant) {
        // Fetch full user from DB
        User user = userRepository.findById(applicant.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        applicant.setUser(user); // attach full user
        return applicantRepository.save(applicant);
    }

    @GetMapping
    public List<Applicant> getAllApplicants() {
        return applicantRepository.findAll();
    }

    // --- Add inside ApplicantsController (below getAllApplicants) ---

    // DTO for the patch request
    public static class ApplicantLicensePatch {
        public String type; // "learning" | "permanent"
        public String status; // optional; default we'll set if null
    }

    @PatchMapping("/{id}/license")
    public Applicant patchApplicantLicense(
            @PathVariable Long id,
            @RequestBody ApplicantLicensePatch body) {

        if (body == null || body.type == null || body.type.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type is required (learning|permanent)");
        }

        Applicant a = applicantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));

        // Default status if not supplied
        String newStatus = (body.status == null || body.status.isBlank()) ? "APPLIED" : body.status;

        if ("learning".equalsIgnoreCase(body.type)) {
            a.setLearnerLicenseStatus(newStatus);
        } else if ("permanent".equalsIgnoreCase(body.type)) {
            a.setDrivingLicenseStatus(newStatus);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "type must be 'learning' or 'permanent'");
        }

        return applicantRepository.save(a);
    }

    // DTO for address upsert (all fields optional)
    public static class AddressDTO {
        public String state;
        public String city;
        public String house;
        public String landmark;
        public String pincode;
    }

    // Create a new address row for an applicant
    @PostMapping("/{applicantId}/address")
    public Address addAddressToApplicant(
            @PathVariable Long applicantId,
            @RequestBody AddressDTO body) {

        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Applicant not found"));

        Address addr = new Address();
        addr.setApplicant(applicant);
        addr.setState(body.state);
        addr.setCity(body.city);
        addr.setHouse(body.house);
        addr.setLandmark(body.landmark);
        addr.setPincode(body.pincode);

        return addressRepository.save(addr);
    }

    // Optional: partial update of an existing address row
    @PatchMapping("/{applicantId}/address/{addressId}")
    public Address patchApplicantAddress(
            @PathVariable Long applicantId,
            @PathVariable Long addressId,
            @RequestBody AddressDTO body) {

        Address addr = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found"));

        // (Optional safety) ensure it belongs to this applicant
        if (addr.getApplicant() == null || !addr.getApplicant().getApplicantId().equals(applicantId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address does not belong to applicant");
        }

        if (body.state != null)
            addr.setState(body.state);
        if (body.city != null)
            addr.setCity(body.city);
        if (body.house != null)
            addr.setHouse(body.house);
        if (body.landmark != null)
            addr.setLandmark(body.landmark);
        if (body.pincode != null)
            addr.setPincode(body.pincode);

        return addressRepository.save(addr);
    }

}
