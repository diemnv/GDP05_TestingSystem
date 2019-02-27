package com.cmcglobal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cmcglobal.entity.User;
import com.cmcglobal.service.UserService;

@RestController
@RequestMapping(value = "user")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class UserController {
	@Autowired
	private UserService userService;

	@GetMapping(value = "/listuser")
	public List<User> listUser() {
		return userService.findAll();
	}
}
