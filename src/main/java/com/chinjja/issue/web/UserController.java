package com.chinjja.issue.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.chinjja.issue.data.UserRepository;
import com.chinjja.issue.domain.User;
import com.chinjja.issue.security.ChangePasswordForm;
import com.chinjja.issue.security.RegisterForm;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Controller
@RequiredArgsConstructor
@SessionAttributes("selectedUser")
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
	
	@PostMapping("/users-cp")
	public String changePassword(
			@AuthenticationPrincipal User user,
			@Valid ChangePasswordForm form,
			BindingResult errors,
			SessionStatus status,
			HttpServletRequest request) {

		if(!form.getUserId().equals(user.getId())) {
			throw new IllegalArgumentException("not match user id: " + form.getUserId());
		}
		if(!passwordEncoder.matches(form.getPassword(), user.getPassword())) {
			errors.addError(new FieldError("changePasswordForm", "password", "암호가 일치하지 않습니다."));
		}
		if(errors.hasErrors()) {
			return "user";
		}
		val referer = request.getHeader("Referer");
		if(!status.isComplete()) {
			user.setPassword(passwordEncoder.encode(form.getNewPassword()));
			userRepo.save(user);
			status.setComplete();
		}
		return "redirect:" + referer;
	}
}
