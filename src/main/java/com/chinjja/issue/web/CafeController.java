package com.chinjja.issue.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.chinjja.issue.data.CafeMemberRepository;
import com.chinjja.issue.data.CafeRepository;
import com.chinjja.issue.data.CategoryRepository;
import com.chinjja.issue.data.CommentRepository;
import com.chinjja.issue.data.PostRepository;
import com.chinjja.issue.data.UserRepository;
import com.chinjja.issue.domain.PostData;
import com.chinjja.issue.domain.Cafe;
import com.chinjja.issue.domain.CafeData;
import com.chinjja.issue.domain.CafeMember;
import com.chinjja.issue.domain.CafeMemberId;
import com.chinjja.issue.domain.Category;
import com.chinjja.issue.domain.CategoryData;
import com.chinjja.issue.domain.CommentData;
import com.chinjja.issue.domain.LikeCountData;
import com.chinjja.issue.domain.User;
import com.chinjja.issue.service.CafeService;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Controller
@RequiredArgsConstructor
@SessionAttributes({"activeCafe", "activePost", "categoryList", "activeCategory", "postPage", "postPageSize", "isOnwer", "isJoined"})
public class CafeController {
	private final CafeService cafeService;
	private final CategoryRepository categoryRepo;
	
	private final CafeRepository cafeRepo;
	private final CafeMemberRepository cafeMemberRepo;
	private final PostRepository postRepo;
	private final UserRepository userRepo;
	private final CommentRepository commentRepo;
	
	@GetMapping("/")
	public String cafe(@AuthenticationPrincipal User user, Model model) {
		if(user != null) {
			user = userRepo.findById(user.getId()).get();
			model.addAttribute("user", user);
		}
		model.addAttribute("cafeList", cafeRepo.findAll());
		model.addAttribute("activeCafe", null);
		return "cafe";
	}
	
	@GetMapping("/create-cafe")
	@PreAuthorize("isAuthenticated()")
	public String createCafe() {
		return "createCafe";
	}
	
	@PostMapping("/create-cafe")
	@PreAuthorize("isAuthenticated()")
	public String createCafeForm(@AuthenticationPrincipal User user, @Valid CafeData form, BindingResult errors) {
		if(errors.hasErrors()) {
			return "createCafe";
		}
		val cafe = new Cafe();
		cafe.setData(form);
		cafe.setOwner(user);
		cafeRepo.save(cafe);
		return "redirect:/";
	}
	
	@GetMapping("/delete-cafe")
	@PreAuthorize("isAuthenticated() and @cafeService.isOwner(#cafe, #user)")
	public String deleteCafe(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe) {
		cafeService.deleteCafe(cafe);
		return "redirect:/";
	}
	
	@GetMapping("/join-cafe")
	@PreAuthorize("isAuthenticated()")
	public String joinCafe(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe) {
		if(cafeService.isJoined(cafe, user)) {
			return "redirect:/cafe/"+cafe.getId();
		}
		return "joinCafe";
	}
	
	@PostMapping("/join-cafe")
	@PreAuthorize("isAuthenticated()")
	public String joinCafeForm(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe,
			@Valid JoinCafeForm form,
			BindingResult errors) {
		if(errors.hasErrors()) {
			return "joinCafe";
		}
		if(!cafeService.isJoined(cafe, user)) {
			val cm = new CafeMember();
			cm.setId(new CafeMemberId(cafe, user));
			cm.setGreeting(form.getGreeting());
			cafeMemberRepo.save(cm);
		}
		return "redirect:/cafe/"+cafe.getId();
	}
	
	@GetMapping("/cafe/{cafeId:[a-z0-9]+}")
	public String posts(
			@AuthenticationPrincipal User user,
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
		
		val posts = cafeService.getPostList(cafe, activeCategory, pageable);
		if(user != null) {
			model.addAttribute("isOwner", cafeService.isOwner(cafe, user));
			model.addAttribute("isJoined", cafeService.isJoined(cafe, user));
		}
		model.addAttribute("activeCafe", cafe);
		model.addAttribute("categoryList", categoryList);
		model.addAttribute("postList", posts);
		model.addAttribute("activeCategory", activeCategory);
		model.addAttribute("postFirstPage", posts.getPageable().first());
		model.addAttribute("postPrevPage", posts.previousOrFirstPageable());
		model.addAttribute("postNextPage", posts.nextOrLastPageable());
		model.addAttribute("postPage", page);
		model.addAttribute("postPageSize", size);
		return "index";
	}
	
	@PreAuthorize("isAuthenticated() and @cafeService.isJoined(#cafe, #user)")
	@GetMapping("/create-post")
	public String createPost(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe) {
		return "createPost";
	}
	
	@PreAuthorize("isAuthenticated() and @cafeService.isJoined(#cafe, #user)")
	@PostMapping("/create-post")
	public String createPostForm(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe,
			@Valid PostData form,
			BindingResult errors) {
		if(errors.hasErrors()) {
			return "createPost";
		}
		cafeService.createPost(user, form);
		return "redirect:/cafe/" + cafe.getId() + "?category=" + form.getCategoryId();
	}
	
	@PreAuthorize("isAuthenticated() and @cafeService.isAuthor(@likableRepository.findById(#postId).get(), #user)")
	@GetMapping("/delete-post")
	public String deletePostForm(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe,
			@ModelAttribute("activeCategory") Category category,
			@ModelAttribute("postPage") Integer page,
			@ModelAttribute("postPageSize") Integer size,
			@RequestParam("postId") Long postId) {
		val post = postRepo.findById(postId).get();
		cafeService.deletePost(post);
		
		val url = UriComponentsBuilder.newInstance()
				.path("/cafe/{cafeId}")
				.queryParam("category", category.getId())
				.queryParam("page", page)
				.queryParam("size", size)
				.build()
				.expand(cafe.getId())
				.encode().toString();
		
		return "redirect:" + url;
	}
	
	@GetMapping("/cafe/{cafeId}/posts/{postId}")
	public String posts(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe,
			@PathVariable String cafeId,
			@PathVariable Long postId,
			Model model) {
		val post = postRepo.findById(postId).get();
		model.addAttribute("activePost", post);
		model.addAttribute("canLike", cafeService.canLikeCount(post, user));
		model.addAttribute("activeCategory", post.getCategory());
		
		cafeService.visit(user, post);
		return "posts";
	}
	
	@PreAuthorize("isAuthenticated() and @cafeService.isJoined(#cafe, #user)")
	@PostMapping("/create-comment")
	public String createCommentForm(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe,
			@Valid CommentData form,
			HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		cafeService.createComment(user, form);
		return "redirect:" + referer;
	}
	
	@PreAuthorize("isAuthenticated() and @cafeService.isAuthor(@likableRepository.findById(#commentId).get(), #user)")
	@GetMapping("/delete-comment")
	public String deleteCommentForm(
			@AuthenticationPrincipal User user,
			@RequestParam("commentId") Long commentId,
			HttpServletRequest request) {
		val comment = commentRepo.findById(commentId).get();
		cafeService.deleteComment(comment);
		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}
	
	@PreAuthorize("isAuthenticated() and @cafeService.isJoined(#cafe, #user)")
	@PostMapping("/toggle-like")
	public String toggleLike(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe,
			@Valid LikeCountData form,
			HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		cafeService.toggleLikeCount(user, form);
		return "redirect:" + referer;
	}
	
	@PreAuthorize("isAuthenticated() and @cafeService.isOwner(#cafe, #user)")
	@PostMapping("/create-category")
	public String categoryForm(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe,
			@Valid CategoryData form,
			HttpServletRequest request,
			Model model) {
		String referer = request.getHeader("Referer");
		cafeService.createCategory(cafe, user, form);
		val categoryList = categoryRepo.findAllByCafeAndParentIsNull(cafe);
		model.addAttribute("categoryList", categoryList);
		return "redirect:" + referer;
	}
	
	@PreAuthorize("isAuthenticated() and @cafeService.isOwner(#cafe, #user)")
	@GetMapping("/delete-category")
	public String deleteCategory(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe,
			@RequestParam("categoryId") Long categoryId,
			HttpServletRequest request,
			Model model) {
		val category = categoryRepo.findById(categoryId).get();
		cafeService.deleteCategory(category);
		val categoryList = categoryRepo.findAllByCafeAndParentIsNull(cafe);
		model.addAttribute("categoryList", categoryList);
		return "redirect:/cafe/" + cafe.getId();
	}
}
