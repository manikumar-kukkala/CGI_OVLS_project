package com.cgi.controller;

import com.cgi.model.RTOOfficer;
import com.cgi.model.User;
import com.cgi.service.UserService;
import com.cgi.service.impl.UserServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Register user
    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    // Fetch all users (you'll need to add this method in UserServiceImpl)
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers(); // Implement getAllUsers() in service
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        boolean isValid = userService.userLogin(user);

        if (isValid) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}

