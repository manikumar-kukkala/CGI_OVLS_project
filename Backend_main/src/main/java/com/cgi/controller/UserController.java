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
    public User registerUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    // Fetch all users (you'll need to add this method in UserServiceImpl)
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();  // Implement getAllUsers() in service
    }
}
