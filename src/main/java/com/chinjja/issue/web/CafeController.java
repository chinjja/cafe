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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.chinjja.issue.data.CafeRepository;
import com.chinjja.issue.data.CategoryRepository;
import com.chinjja.issue.data.CommentRepository;
import com.chinjja.issue.data.PostRepository;
import com.chinjja.issue.data.UserRepository;
import com.chinjja.issue.domain.PostData;
import com.chinjja.issue.domain.Cafe;
import com.chinjja.issue.domain.CafeData;
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
@SessionAttributes({"activeCafe", "activePost", "categoryList", "activeCategory", "postPage", "postPageSize"})
public class CafeController {
	private final CafeService cafeService;
	private final CategoryRepository categoryRepo;
	
	private final CafeRepository cafeRepo;
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
	@Secured("ROLE_USER")
	public String createCafe() {
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
	
	@GetMapping("/delete-cafe")
	@PreAuthorize("isAuthenticated() and (#cafe.owner.id == #user.id)")
	public String deleteCafe(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe) {
		cafeService.deleteCafe(cafe);
		return "redirect:/";
	}
	
	@GetMapping("/join-cafe")
	@Secured("ROLE_USER")
	public String joinCafeForm(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe) {
		cafe = cafeRepo.findById(cafe.getId()).get();
		if(!cafe.getOwner().getId().equals(user.getId())) {
			if(cafe.getMembers().add(user)) {
				cafeRepo.save(cafe);
			}
		}
		return "redirect:/cafe/"+cafe.getId();
	}
	
	@GetMapping("/cafe/{cafeId:[a-z0-9]+}")
	public String posts(
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
	
	@Secured("ROLE_USER")
	@GetMapping("/create-post")
	public String createPost() {
		return "createPost";
	}
	
	@Secured("ROLE_USER")
	@PostMapping("/create-post")
	public String createPostForm(@AuthenticationPrincipal User user, @Valid PostData form) {
		cafeService.createPost(user, form);
		return "redirect:/cafe/" + form.getCafeId() + "?category=" + form.getCategoryId();
	}
	
	@Secured("ROLE_USER")
	@GetMapping("/delete-post")
	public String deletePostForm(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe,
			@ModelAttribute("activeCategory") Category category,
			@ModelAttribute("postPage") Integer page,
			@ModelAttribute("postPageSize") Integer size,
			@RequestParam("postId") Long postId) {
		val post = postRepo.findById(postId).get();
		if(!post.getUser().getId().equals(user.getId())) {
			throw new IllegalArgumentException("only author can remove this post");
		}
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
		val categoryList = categoryRepo.findAllByCafeAndParentIsNull(cafe);
		model.addAttribute("categoryList", categoryList);
		
		cafeService.visit(user, post);
		return "posts";
	}
	
	@Secured("ROLE_USER")
	@PostMapping("/create-comment")
	public String commentForm(
			@AuthenticationPrincipal User user,
			@Valid CommentData form,
			HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		cafeService.createComment(user, form);
		return "redirect:" + referer;
	}
	
	@Secured("ROLE_USER")
	@GetMapping("/delete-comment")
	public String deleteCommentForm(
			@AuthenticationPrincipal User user,
			@RequestParam("commentId") Long commentId,
			HttpServletRequest request) {
		val comment = commentRepo.findById(commentId).get();
		if(!comment.getUser().getId().equals(user.getId())) {
			throw new IllegalArgumentException("only author can remove comment");
		}
		cafeService.deleteComment(comment);
		String referer = request.getHeader("Referer");
		return "redirect:" + referer;
	}
	
	@Secured("ROLE_USER")
	@PostMapping("/toggle-like")
	public String toggleLike(
			@AuthenticationPrincipal User user,
			@Valid LikeCountData form,
			HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		cafeService.toggleLikeCount(user, form);
		return "redirect:" + referer;
	}
	
	@PreAuthorize("isAuthenticated() and (#cafe.owner.id == #user.id)")
	@PostMapping("/create-category")
	public String categoryForm(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe,
			@Valid CategoryData form,
			HttpServletRequest request,
			Model model) {
		String referer = request.getHeader("Referer");
		cafeService.createCategory(user, form);
		val categoryList = categoryRepo.findAllByCafeAndParentIsNull(cafe);
		model.addAttribute("categoryList", categoryList);
		return "redirect:" + referer;
	}
	
	@PreAuthorize("isAuthenticated() and (#cafe.owner.id == #user.id)")
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
