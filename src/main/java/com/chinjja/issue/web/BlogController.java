package com.chinjja.issue.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.chinjja.issue.data.CommentRepository;
import com.chinjja.issue.data.ElementRepository;
import com.chinjja.issue.domain.BlogData;
import com.chinjja.issue.domain.CommentData;
import com.chinjja.issue.domain.LikeCountData;
import com.chinjja.issue.domain.User;
import com.chinjja.issue.service.BlogService;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Controller
@RequiredArgsConstructor
public class BlogController {
	private final ElementRepository elementRepo;
	private final CommentRepository commentRepo;
	private final BlogService blogService;
	
	@GetMapping("/blogs")
	public String blogs() {
		return "blogs";
	}
	
	@PostMapping("/blogs")
	public String blogForm(@AuthenticationPrincipal User user, @Valid BlogData form) {
		blogService.createBlog(user, form);
		return "redirect:/blogs";
	}
	
	@GetMapping("/blogs/{id}")
	public String blogs(@PathVariable("id") Long id, Model model) {
		val blog = blogService.getBlogById(id);
		model.addAttribute("blog", blog);
		model.addAttribute("commentList", commentRepo.findAllByTarget(blog, Sort.by(Order.asc("createdAt"))));
		return "blogDetails";
	}
	
	@PostMapping("/comments")
	public String commentForm(
			@AuthenticationPrincipal User user,
			@Valid CommentData form,
			HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		blogService.createComment(user, form);
		return "redirect:" + referer;
	}
	
	@PostMapping("/likeCount")
	public String likeCount(
			@AuthenticationPrincipal User user,
			@Valid LikeCountData form,
			HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		blogService.likeCount(elementRepo.findById(form.getTarget()).get());
		return "redirect:" + referer;
	}
}
