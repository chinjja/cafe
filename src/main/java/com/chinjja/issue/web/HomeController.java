package com.chinjja.issue.web;

import javax.validation.Valid;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.chinjja.issue.data.IssueRepository;
import com.chinjja.issue.data.UserRepository;
import com.chinjja.issue.domain.Issue;
import com.chinjja.issue.domain.IssueData;
import com.chinjja.issue.domain.User;
import com.chinjja.issue.security.RegisterForm;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Controller
@RequiredArgsConstructor
@SessionAttributes({"user", "issueList"})
public class HomeController {
	private final IssueRepository issueRepo;
	private final UserRepository userRepo;
	private final PasswordEncoder passwordEncoder;
	
	@GetMapping("/")
	public String home(@AuthenticationPrincipal User user, Model model) {
		if(user != null) {
			model.addAttribute("user", user);
		}
		model.addAttribute("issueList", issueRepo.findAll());
		return "home";
	}
	
	@Secured("ROLE_USER")
	@PostMapping("/new_issue")
	public String createIssue(@AuthenticationPrincipal User user, @Valid IssueData data, Errors errors, SessionStatus status) {
		if(errors.hasErrors()) {
			return "home";
		}
		val issue = new Issue();
		issue.setUser(user);
		issue.setData(data);
		issueRepo.save(issue);
		status.setComplete();
		return "redirect:/";
	}
	
	@GetMapping("/register")
	public String register() {
		return "register";
	}
	
	@PostMapping("/register")
	public String registerForm(RegisterForm form) {
		val user = new User();
		user.setUsername(form.getUsername());
		user.setPassword(passwordEncoder.encode(form.getPassword()));
		userRepo.save(user);
		return "redirect:/";
	}
}
