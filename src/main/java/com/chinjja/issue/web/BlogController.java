package com.chinjja.issue.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.chinjja.issue.data.CafeRepository;
import com.chinjja.issue.data.CategoryRepository;
import com.chinjja.issue.domain.BlogData;
import com.chinjja.issue.domain.Cafe;
import com.chinjja.issue.domain.CafeData;
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
@SessionAttributes({"cafe", "categoryList", "activeCategory"})
public class BlogController {
	private final BlogService blogService;
	private final CategoryRepository categoryRepo;
	
	private final CafeRepository cafeRepo;
	
	@GetMapping("/")
	public String cafe(Model model, SessionStatus status) {
		model.addAttribute("cafeList", cafeRepo.findAll());
		model.addAttribute("cafe", null);
		return "cafe";
	}
	
	@GetMapping("/create-cafe")
	@Secured("ROLE_USER")
	public String createCafe(@AuthenticationPrincipal User user) {
		return "createCafe";
	}
	
	@PostMapping("/create-cafe")
	@Secured("ROLE_USER")
	public String createCafeForm(@AuthenticationPrincipal User user, CafeData form) {
		val cafe = new Cafe();
		cafe.setData(form);
		cafe.setOwner(user);
		cafeRepo.save(cafe);
		return "redirect:/";
	}
	
	@GetMapping("/{cafeId}")
	public String blogs(
			@PathVariable String cafeId,
			@RequestParam(required = false) Long category,
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size,
			Model model) {
		val cafe = cafeRepo.findById(cafeId).get();
		Category activeCategory = null;
		if(category != null) {
			activeCategory = categoryRepo.findById(category).orElseThrow(() -> new IllegalArgumentException("unknown category id: "+ category));
		}
		val categoryList = categoryRepo.findAllByCafeAndParentIsNull(cafe);
		if(page == null || size == null) {
			page = 0;
			size = 20;
		}
		val pageable = PageRequest.of(page, size, Direction.DESC, "createdAt");
		
		val blogList = blogService.getBlogList(cafe, activeCategory, pageable);
		model.addAttribute("cafe", cafe);
		model.addAttribute("categoryList", categoryList);
		model.addAttribute("blogList", blogList);
		model.addAttribute("activeCategory", activeCategory);
		model.addAttribute("blogFirstPage", blogList.getPageable().first());
		model.addAttribute("blogPrevPage", blogList.previousOrFirstPageable());
		model.addAttribute("blogNextPage", blogList.nextOrLastPageable());
		return "index";
	}
	
	@Secured("ROLE_USER")
	@PostMapping("/create-blog")
	public String blogForm(@AuthenticationPrincipal User user, @Valid BlogData form) {
		blogService.createBlog(user, form);
		return "redirect:/" + form.getCafeId() + "?category=" + form.getCategoryId();
	}
	
	@GetMapping("/{cafeId}/blogs/{blogId}")
	public String blogs(
			@AuthenticationPrincipal User user,
			@PathVariable String cafeId,
			@PathVariable Long blogId,
			Model model) {
		val blog = blogService.getBlogById(blogId);
		model.addAttribute("blog", blog);
		model.addAttribute("canLike", blogService.canLikeCount(blog, user));
		
		blogService.visit(user, blog);
		return "blogs";
	}
	
	@Secured("ROLE_USER")
	@PostMapping("/create-comment")
	public String commentForm(
			@AuthenticationPrincipal User user,
			@Valid CommentData form,
			HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		blogService.createComment(user, form);
		return "redirect:" + referer;
	}
	
	@Secured("ROLE_USER")
	@PostMapping("/toggle-like")
	public String toggleLike(
			@AuthenticationPrincipal User user,
			@Valid LikeCountData form,
			HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		blogService.toggleLikeCount(user, form);
		return "redirect:" + referer;
	}
	
	@PreAuthorize("isAuthenticated() and (#cafe.owner.id == #user.id)")
	@PostMapping("/create-category")
	public String categoryForm(
			@AuthenticationPrincipal User user,
			Cafe cafe,
			@Valid CategoryData form,
			HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		blogService.createCategory(user, form);
		return "redirect:" + referer;
	}
}
