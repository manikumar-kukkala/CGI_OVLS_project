package com.cgi.service;

import java.util.List;

import com.cgi.model.User;

public interface UserService {

    String userRegistration(User user);

    String userLogin(String email, String password);

    String changePassword(User user);

    String forgotPassword(String email);


	String userLogin(User user);
	User addUser(User user);
	List<User> getAllUsers();

}
