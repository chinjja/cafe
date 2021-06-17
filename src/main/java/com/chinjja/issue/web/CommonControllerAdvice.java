package com.chinjja.issue.web;

import java.util.Arrays;
import java.util.List;

import org.springframework.core.env.Environment;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.chinjja.issue.domain.User;

import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class CommonControllerAdvice {
	private final Environment env;
	
	@ModelAttribute("activeProfileList")
	public List<String> profile() {
		return Arrays.asList(env.getActiveProfiles());
	}
	
	@ModelAttribute
	public User getUser(@AuthenticationPrincipal User user) {
		return user;
	}
}
