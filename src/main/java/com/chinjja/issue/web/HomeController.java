package com.chinjja.issue.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.chinjja.issue.data.IssueRepository;
import com.chinjja.issue.domain.Issue;
import com.chinjja.issue.domain.IssueData;
import com.chinjja.issue.domain.User;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Controller
@RequiredArgsConstructor
public class HomeController {
	private final IssueRepository issueRepo;
	
	@GetMapping("/")
	public String getIssues(@AuthenticationPrincipal User user, Model model) {
		if(user != null) {
			model.addAttribute("user", user);
		}
		model.addAttribute("issueList", issueRepo.findAll());
		return "home";
	}
	
	@PostMapping("/issue")
	public String createIssue(@AuthenticationPrincipal User user, IssueData data) {
		val issue = new Issue();
		issue.setUser(user);
		issue.setData(data);
		issueRepo.save(issue);
		return "redirect:/";
	}
}
