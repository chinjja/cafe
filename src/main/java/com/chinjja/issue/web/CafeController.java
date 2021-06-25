package com.chinjja.issue.web;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
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
import com.chinjja.issue.domain.Post;
import com.chinjja.issue.domain.User;
import com.chinjja.issue.service.CafeService;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Controller
@RequiredArgsConstructor
@SessionAttributes({"activeCafe", "activePost", "categoryList", "activeCategory", "postPage", "postPageSize", "isJoined", "canLike"})
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
			return "redirect:/my-cafe";
		}
		model.addAttribute("cafeList", cafeRepo.findAll());
		model.addAttribute("activeCafe", null);
		return "cafe";
	}
	
	@GetMapping("/my-cafe")
	@PreAuthorize("isAuthenticated()")
	public String myCafe(@AuthenticationPrincipal User user, Model model) {
		user = userRepo.findById(user.getId()).get();
		model.addAttribute("user", user);
		model.addAttribute("activeCafe", null);
		return "myCafe";
	}
	
	@GetMapping("/search-cafe")
	public String searchCafe(
			@RequestParam(required = false) String search,
			Model model) {
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
		if(cafeService.hasCafe(form.getId())) {
			errors.addError(new FieldError("cafeData", "id", form.getId() + " already exists"));
		}
		if(errors.hasErrors()) {
			return "createCafe";
		}
		cafeService.createCafe(user, form);
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
			cafeService.joinCafe(cafe, user, form);
		}
		return "redirect:/cafe/"+cafe.getId();
	}
	
	@GetMapping("/leave-cafe")
	@PreAuthorize("isAuthenticated()")
	public String leaveCafe(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe) {
		cafeService.leaveCafe(cafe, user);
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
		return "redirect:" + toPostsUrl(cafe, category, page, size);
	}
	
	@GetMapping("/cafe/{cafeId}/posts/{postId}")
	public String posts(
			@AuthenticationPrincipal User user,
			@PathVariable String cafeId,
			@PathVariable Long postId,
			Model model) {
		val post = postRepo.findById(postId).get();
		model.addAttribute("activeCafe", cafeRepo.findById(cafeId).get());
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
			@ModelAttribute("activePost") Post post,
			@Valid CommentData form,
			BindingResult errors) {
		if(errors.hasErrors()) {
			return "posts";
		}
		cafeService.createComment(user, form);
		return "redirect:" + toPostUrl(cafe, post);
	}
	
	@PreAuthorize("isAuthenticated() and @cafeService.isAuthor(@likableRepository.findById(#commentId).get(), #user)")
	@GetMapping("/delete-comment")
	public String deleteCommentForm(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe,
			@ModelAttribute("activePost") Post post,
			@RequestParam("commentId") Long commentId) {
		val comment = commentRepo.findById(commentId).get();
		cafeService.deleteComment(comment);
		return "redirect:" + toPostUrl(cafe, post);
	}
	
	@PreAuthorize("isAuthenticated() and @cafeService.isJoined(#cafe, #user)")
	@PostMapping("/toggle-like")
	public String toggleLike(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe,
			@ModelAttribute("activePost") Post post,
			@Valid LikeCountData form) {
		cafeService.toggleLikeCount(user, form);
		return "redirect:" + toPostUrl(cafe, post);
	}
	
	private String toPostUrl(Cafe cafe, Post post) {
		return UriComponentsBuilder.newInstance()
				.path("/cafe/{cafeId}/posts/{postId}")
				.buildAndExpand(cafe.getId(), post.getId())
				.encode().toUriString();
	}
	
	@PreAuthorize("isAuthenticated() and @cafeService.isOwner(#cafe, #user)")
	@PostMapping("/create-category")
	public String categoryForm(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe,
			@Valid CategoryData form,
			Model model) {
		cafeService.createCategory(cafe, user, form);
		val categoryList = categoryRepo.findAllByCafeAndParentIsNull(cafe);
		model.addAttribute("categoryList", categoryList);
		return "redirect:" + toPostsUrl(cafe, null, null, null);
	}
	
	@PreAuthorize("isAuthenticated() and @cafeService.isOwner(#cafe, #user)")
	@GetMapping("/delete-category")
	public String deleteCategory(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe,
			@RequestParam("categoryId") Long categoryId,
			Model model) {
		val category = categoryRepo.findById(categoryId).get();
		cafeService.deleteCategory(category);
		val categoryList = categoryRepo.findAllByCafeAndParentIsNull(cafe);
		model.addAttribute("categoryList", categoryList);
		return "redirect:" + toPostsUrl(cafe, null, null, null);
	}
	
	private String toPostsUrl(Cafe cafe, Category category, Integer page, Integer size) {
		return UriComponentsBuilder.newInstance()
				.path("/cafe/{cafeId}")
				.queryParam("category", category == null ? null : category.getId())
				.queryParam("page", page)
				.queryParam("size", size)
				.buildAndExpand(cafe.getId())
				.encode().toUriString();
	}
}
