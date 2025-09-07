package com.cgi.repository;

import com.cgi.model.RTOOfficer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RTOOfficerRepository extends JpaRepository<RTOOfficer, Integer> {

    // For login using email
    Optional<RTOOfficer> findByEmailAndPassword(String email, String password);

    // For login using username
    Optional<RTOOfficer> findByUsernameAndPassword(String username, String password);
}
