package com.cgi.repository;

import com.cgi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // For login
    Optional<User> findByEmailAndPassword(String email, String password);

    // For password change
    Optional<User> findByEmail(String email);
}
