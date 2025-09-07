package com.cgi.service.impl;

import com.cgi.exception.InvalidDataException;
import com.cgi.model.RTOOfficer;
import com.cgi.model.User;
import com.cgi.repository.UserRepository;
import com.cgi.service.UserService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String userRegistration(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new InvalidDataException("Invalid email");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new InvalidDataException("Password too short");
        }
        userRepository.save(user);
        return "User registered successfully: " + user.getEmail();
    }

    @Override
    public boolean userLogin(User user) {
        Optional<User> dbOfficer = userRepository.findByEmailAndPassword(
                user.getEmail(), user.getPassword());
        return dbOfficer.isPresent();
    }

    @Override
    public String changePassword(User user) {
        return userRepository.findByEmail(user.getEmail())
                .map(existingUser -> {
                    existingUser.setPassword(user.getPassword());
                    userRepository.save(existingUser);
                    return "Password updated for: " + existingUser.getEmail();
                })
                .orElse("User not found");
    }

    @Override
    public String forgotPassword(String email) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public User addUser(User user) {
        // Save user and return the saved entity
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}
