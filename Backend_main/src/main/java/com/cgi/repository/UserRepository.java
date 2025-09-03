package com.cgi.repository;

import com.cgi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // For login
    Optional<User> findByEmailAndPassword(String email, String password);

    // For password change
    Optional<User> findByEmail(String email);
}
