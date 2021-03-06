package com.chinjja.issue.web;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.chinjja.issue.domain.User;
import com.chinjja.issue.security.ChangePasswordForm;
import com.chinjja.issue.security.RegisterForm;
import com.chinjja.issue.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Controller
@RequiredArgsConstructor
@SessionAttributes("activeUser")
public class UserController {
	private final UserService userService;
	
	@GetMapping("/create-user")
	public String createUser() {
		return "createUser";
	}
	
	@PostMapping("/create-user")
	public String createUserForm(@Valid RegisterForm form, Errors errors) {
		if(errors.hasErrors()) {
			return "createUser";
		}
		userService.register(form);
		return "redirect:/login";
	}
	
	@GetMapping("/user/{username}")
	public String getUser(@PathVariable String username, Model model) {
		val user = userService.byUsername(username);
		model.addAttribute("activeUser", user);
		return "user";
	}
	
	@PreAuthorize("isAuthenticated() and (#activeUser.id == #user.id)")
	@PostMapping("/users-cp")
	public String changePassword(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeUser") User activeUser,
			@Valid ChangePasswordForm form,
			BindingResult errors) {

		if(!userService.matchPassword(user, form.getPassword())) {
			errors.addError(new FieldError("changePasswordForm", "password", "????????? ???????????? ????????????."));
		}
		if(errors.hasErrors()) {
			return "user";
		}
		userService.changePassword(user, form.getNewPassword());
		return "redirect:/user/" + user.getUsername();
	}
}
