package com.chinjja.issue.web;

import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.springframework.core.env.Environment;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.chinjja.issue.data.IssueRepository;
import com.chinjja.issue.data.ReplyRepository;
import com.chinjja.issue.data.UserRepository;
import com.chinjja.issue.domain.Issue;
import com.chinjja.issue.domain.IssueData;
import com.chinjja.issue.domain.Reply;
import com.chinjja.issue.domain.ReplyForm;
import com.chinjja.issue.domain.User;
import com.chinjja.issue.security.RegisterForm;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Controller
@RequiredArgsConstructor
@SessionAttributes(names = {"issueList", "replyList"}, types = {Issue.class})
public class HomeController {
	private final Environment env;
	private final IssueRepository issueRepo;
	private final UserRepository userRepo;
	private final ReplyRepository replyRepo;
	private final PasswordEncoder passwordEncoder;
	
	@ModelAttribute("activeProfileList")
	public List<String> profile() {
		return Arrays.asList(env.getActiveProfiles());
	}
	@ModelAttribute("issueList")
	public Iterable<Issue> issueList() {
		return issueRepo.findAll();
	}
	
	@ModelAttribute
	public User getUser(@AuthenticationPrincipal User user) {
		return user;
	}
	
	@ModelAttribute
	public IssueData getIssueData() {
		return new IssueData();
	}
	
	@ModelAttribute
	public Reply getReply() {
		return new Reply();
	}
	
	@ModelAttribute
	public ReplyForm getReplyForm() {
		return new ReplyForm();
	}
	
	@GetMapping("/")
	public String home() {
		return "home";
	}
	
	@Secured("ROLE_USER")
	@PostMapping("/issues")
	public String issueForm(@AuthenticationPrincipal User user, @Valid IssueData data, Errors errors, SessionStatus status) {
		if(errors.hasErrors()) {
			return "home";
		}
		if(!status.isComplete()) {
			val issue = new Issue();
			issue.setUser(user);
			issue.setData(data);
			issueRepo.save(issue);
			status.setComplete();
		}
		return "redirect:/";
	}
	
	@GetMapping("/register")
	public String register() {
		return "register";
	}
	
	@PostMapping("/register")
	public String registerForm(@Valid RegisterForm form) {
		val user = new User();
		user.setUsername(form.getUsername());
		user.setPassword(passwordEncoder.encode(form.getPassword()));
		userRepo.save(user);
		return "redirect:/";
	}
	
	@GetMapping("/users/{username}")
	public String users(@PathVariable String username, Model model) {
		val user = userRepo.findByUsername(username);
		model.addAttribute("user", user);
		return "user";
	}
	
	@GetMapping("/issues/{id}")
	public String issues(@PathVariable Long id, Model model) {
		val issue = issueRepo.findById(id).get();
		model.addAttribute("issue", issue);
		
		val replyList = replyRepo.findAllByIssue(issue);
		model.addAttribute("replyList", replyList);
		
		return "issue";
	}
	
	@Secured("ROLE_USER")
	@PostMapping("/reply")
	public String replyForm(@AuthenticationPrincipal User user, @Valid ReplyForm replyForm, Errors errors, Issue issue, SessionStatus status) {
		if(errors.hasErrors()) {
			return "issue";
		}
		if(!status.isComplete()) {
			val reply = new Reply();
			reply.setIssue(issue);
			reply.setUser(user);
			reply.setComment(replyForm.getComment());
			replyRepo.save(reply);
			status.setComplete();
		}
		return "redirect:/issues/"+issue.getId();
	}
}
