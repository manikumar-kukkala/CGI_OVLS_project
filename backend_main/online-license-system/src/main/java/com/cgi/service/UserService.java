package com.cgi.service;

import java.util.List;

import com.cgi.model.User;

public interface UserService {

    String userRegistration(User user);

    String changePassword(User user);

    String forgotPassword(String email);

    User addUser(User user);

    boolean userLogin(User user);

    List<User> getAllUsers();

}
