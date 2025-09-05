package com.cgi.controller;

import com.cgi.model.User;
import com.cgi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Register user
    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        return userService.userRegistration(user);
    }

    // User login
    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password) {
        return userService.userLogin(email, password);
    }

    // Change password
    @PutMapping("/change-password")
    public String changePassword(@RequestBody User user) {
        return userService.changePassword(user);
    }

    // Forgot password
    @GetMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        return userService.forgotPassword(email);
    }

    // Fetch all users
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}

