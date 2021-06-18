package com.chinjja.issue.web;

import javax.validation.Valid;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.support.SessionStatus;

import com.chinjja.issue.data.UserRepository;
import com.chinjja.issue.domain.User;
import com.chinjja.issue.security.RegisterForm;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Controller
@RequiredArgsConstructor
public class UserController {
	private final UserRepository userRepo;
	private final PasswordEncoder passwordEncoder;
	
	@GetMapping("/register")
	public String register() {
		return "register";
	}
	
	@PostMapping("/register")
	public String registerForm(@Valid RegisterForm form, Errors errors, SessionStatus status) {
		if(errors.hasErrors()) {
			return "register";
		}
		if(!form.getPassword().equals(form.getConfirm())) {
			errors.rejectValue("password", "different password");
			errors.rejectValue("confirm", "different password");
			return "register";
		}
		if(!status.isComplete()) {
			val user = new User();
			user.setUsername(form.getUsername());
			user.setPassword(passwordEncoder.encode(form.getPassword()));
			userRepo.save(user);
			status.setComplete();
		}
		return "redirect:/";
	}
	
	@GetMapping("/users/{id}")
	public String users(@PathVariable Long id, Model model) {
		val user = userRepo.findById(id).get();
		model.addAttribute("selectedUser", user);
		return "user";
	}
}
