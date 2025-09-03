package com.cgi.controller;

import com.cgi.model.Applicant;
import com.cgi.model.User;
import com.cgi.repository.ApplicantRepository;
import com.cgi.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/applicants")
public class ApplicantsController {

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private UserRepository userRepository;   // <-- inject it

    @PostMapping
    public Applicant addApplicant(@RequestBody Applicant applicant) {
        // Fetch full user from DB
        User user = userRepository.findById(applicant.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        applicant.setUser(user);  // attach full user
        return applicantRepository.save(applicant);
    }




    @GetMapping
    public List<Applicant> getAllApplicants() {
        return applicantRepository.findAll();
    }
}
