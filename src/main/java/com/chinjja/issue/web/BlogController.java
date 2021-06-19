package com.chinjja.issue.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chinjja.issue.data.CategoryRepository;
import com.chinjja.issue.domain.BlogData;
import com.chinjja.issue.domain.Category;
import com.chinjja.issue.domain.CategoryData;
import com.chinjja.issue.domain.CommentData;
import com.chinjja.issue.domain.LikeCountData;
import com.chinjja.issue.domain.User;
import com.chinjja.issue.service.BlogService;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Controller
@RequiredArgsConstructor
public class BlogController {
	private final BlogService blogService;
	private final CategoryRepository categoryRepo;
	
	@GetMapping({"/", "/index"})
	public String blogs(
			@RequestParam(required = false) Long category,
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size,
			Model model) {
		Category activeCategory = null;
		if(category != null) {
			activeCategory = categoryRepo.findById(category).orElseThrow(() -> new IllegalArgumentException("unknown category id: "+ category));
		}
		if(page == null || size == null) {
			page = 0;
			size = 20;
		}
		val pageable = PageRequest.of(page, size, Direction.DESC, "createdAt");
		
		val blogList = blogService.getBlogList(activeCategory, pageable);
		model.addAttribute("blogList", blogList);
		model.addAttribute("activeCategory", activeCategory);
		model.addAttribute("blogFirstPage", blogList.getPageable().first());
		model.addAttribute("blogPrevPage", blogList.previousOrFirstPageable());
		model.addAttribute("blogNextPage", blogList.nextOrLastPageable());
		return "index";
	}
	
	@Secured("ROLE_USER")
	@PostMapping("/blogs")
	public String blogForm(@AuthenticationPrincipal User user, @Valid BlogData form) {
		blogService.createBlog(user, form);
		return "redirect:/?category="+form.getCategory();
	}
	
	@GetMapping("/blogs/{id}")
	public String blogs(@AuthenticationPrincipal User user, @PathVariable("id") Long id, Model model) {
		val blog = blogService.getBlogById(id);
		model.addAttribute("blog", blog);
		model.addAttribute("canLike", blogService.canLikeCount(blog, user));
		model.addAttribute("activeCategory", blog.getCategory());
		
		blogService.visit(user, blog);
		return "blogs";
	}
	
	@Secured("ROLE_USER")
	@PostMapping("/comments")
	public String commentForm(
			@AuthenticationPrincipal User user,
			@Valid CommentData form,
			HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		blogService.createComment(user, form);
		return "redirect:" + referer;
	}
	
	@Secured("ROLE_USER")
	@PostMapping("/likeCount")
	public String likeCount(
			@AuthenticationPrincipal User user,
			@Valid LikeCountData form,
			HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		blogService.toggleLikeCount(user, form);
		return "redirect:" + referer;
	}
	
	@Secured("ROLE_ADMIN")
	@PostMapping("/categories")
	public String categoryForm(
			@AuthenticationPrincipal User user,
			@Valid CategoryData form,
			HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		blogService.createCategory(user, form);
		return "redirect:" + referer;
	}
}
