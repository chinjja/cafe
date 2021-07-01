package com.chinjja.issue.web;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.chinjja.issue.domain.Cafe;
import com.chinjja.issue.domain.Category;
import com.chinjja.issue.domain.Post;
import com.chinjja.issue.domain.User;
import com.chinjja.issue.form.CafeForm;
import com.chinjja.issue.form.CategoryForm;
import com.chinjja.issue.form.CommentForm;
import com.chinjja.issue.form.JoinCafeForm;
import com.chinjja.issue.form.PostForm;
import com.chinjja.issue.service.CafeService;
import com.chinjja.issue.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Controller
@RequiredArgsConstructor
@SessionAttributes({"activeCafe", "activePost", "activeCategory", "postPage"})
public class CafeController {
	private final CafeService cafeService;
	private final UserService userService;
	
	@GetMapping("/")
	public String home(@AuthenticationPrincipal User user, Model model) {
		if(user != null) {
			return "redirect:/my-cafe";
		}
		model.addAttribute("cafeList", cafeService.getPublicCafeList());
		model.addAttribute("activeCafe", null);
		return "cafes";
	}
	
	@GetMapping("/my-cafe")
	@PreAuthorize("isAuthenticated()")
	public String myCafe(@AuthenticationPrincipal User user, Model model) {
		user = userService.byId(user.getId());
		model.addAttribute("user", user);
		model.addAttribute("activeCafe", null);
		model.addAttribute("notApprovedMemberList", cafeService.getNotApprovedMembers(user));
		return "myCafe";
	}
	
	@GetMapping("/search-cafe")
	public String searchCafe(
			@RequestParam(required = false) String search,
			Model model) {
		model.addAttribute("cafeList", cafeService.getPublicCafeList());
		model.addAttribute("activeCafe", null);
		return "cafes";
	}
	
	@GetMapping("/create-cafe")
	@PreAuthorize("isAuthenticated()")
	public String createCafe() {
		return "createCafe";
	}
	
	@PostMapping("/create-cafe")
	@PreAuthorize("isAuthenticated()")
	public String createCafeForm(
			@AuthenticationPrincipal User user,
			@ModelAttribute("cafeForm") @Valid CafeForm form, BindingResult errors) {
		if(cafeService.hasCafe(form.getId())) {
			errors.addError(new FieldError("cafeData", "id", form.getId() + " already exists"));
		}
		
		if(errors.hasErrors()) {
			return "createCafe";
		}
		cafeService.createCafe(form, user);
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
			return "redirect:" + toCafeUrl(cafe, null, null);
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
		return "redirect:" + toCafeUrl(cafe, null, null);
	}
	
	@GetMapping("/leave-cafe")
	@PreAuthorize("isAuthenticated()")
	public String leaveCafe(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe) {
		cafeService.leaveCafe(cafe, user);
		return "redirect:" + toCafeUrl(cafe, null, null);
	}
	
	@GetMapping("/approve-member")
	@PreAuthorize("isAuthenticated() and @cafeService.isOwner(#cafeId, #user)")
	public String approveMember(
			@AuthenticationPrincipal User user,
			@RequestParam String cafeId,
			@RequestParam Long memberId,
			RedirectAttributes rttr) {
		val cafe = cafeService.getCafeById(cafeId);
		val member = userService.byId(memberId);
		cafeService.approveMember(cafe, member);
		rttr.addFlashAttribute("active_tab", 2);
		return "redirect:/my-cafe";
	}
	
	@GetMapping("/cafe/{cafeId:[a-z0-9]+}")
	public String cafe(
			@AuthenticationPrincipal User user,
			@PathVariable String cafeId,
			@RequestParam(required = false) Long category,
			@RequestParam(required = false) Integer page,
			@RequestParam(required = false) Integer size,
			Model model) {
		val cafe = cafeService.getCafeById(cafeId);
		Category activeCategory = null;
		if(category != null) {
			activeCategory = cafeService.getCategoryById(category);
		}
		if(page == null || size == null) {
			page = 0;
			size = 20;
		}
		val pageable = PageRequest.of(page, size, Direction.DESC, "createdAt");
		
		val posts = cafeService.getPostList(cafe, activeCategory, pageable);
		model.addAttribute("activeCafe", cafe);
		model.addAttribute("postPage", posts);
		model.addAttribute("activeCategory", activeCategory);
		return "cafe";
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
			@ModelAttribute("activeCategory") Category category,
			@Valid PostForm form,
			BindingResult errors) {
		if(errors.hasErrors()) {
			return "createPost";
		}
		cafeService.createPost(user, category, form);
		return "redirect:" + toCafeUrl(cafe, category, null);
	}
	
	@PreAuthorize("isAuthenticated() and @cafeService.isAuthor(#postId, #user)")
	@GetMapping("/delete-post")
	public String deletePostForm(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe,
			@ModelAttribute("activeCategory") Category category,
			@ModelAttribute("postPage") Page<Post> postPage,
			@RequestParam("postId") Long postId) {
		val post = cafeService.getPostById(postId);
		cafeService.deletePost(post);
		return "redirect:" + toCafeUrl(cafe, category, postPage);
	}
	
	@GetMapping("/post/{postId}")
	public String posts(
			@AuthenticationPrincipal User user,
			@PathVariable Long postId,
			Model model) {
		val post = cafeService.getPostById(postId);
		model.addAttribute("activeCafe", post.getCategory().getCafe());
		model.addAttribute("activePost", post);
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
			@Valid CommentForm form,
			BindingResult errors) {
		if(errors.hasErrors()) {
			return "posts";
		}
		cafeService.createComment(user, post, form);
		return "redirect:" + toPostUrl(post);
	}
	
	@PreAuthorize("isAuthenticated() and @cafeService.isAuthor(#commentId, #user)")
	@GetMapping("/delete-comment")
	public String deleteCommentForm(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activePost") Post post,
			@RequestParam("commentId") Long commentId) {
		val comment = cafeService.getCommentById(commentId);
		cafeService.deleteComment(comment);
		return "redirect:" + toPostUrl(post);
	}
	
	@PreAuthorize("isAuthenticated() and @cafeService.isJoined(#cafe, #user)")
	@GetMapping("/toggle-like")
	public String toggleLike(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe,
			@ModelAttribute("activePost") Post post) {
		cafeService.toggleLikeCount(user, post);
		return "redirect:" + toPostUrl(post);
	}
	
	private String toPostUrl(Post post) {
		return UriComponentsBuilder.newInstance()
				.path("/post/{postId}")
				.buildAndExpand(post.getId())
				.encode().toUriString();
	}
	
	@PreAuthorize("isAuthenticated() and @cafeService.isOwner(#cafe, #user)")
	@PostMapping("/create-category")
	public String createCategoryForm(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe,
			@Valid CategoryForm form,
			BindingResult errors,
			Model model) {
		if(errors.hasErrors()) {
			return "cafe";
		}
		cafeService.createCategory(cafe, form);
		return "redirect:" + toCafeUrl(cafe, null, null);
	}
	
	@PreAuthorize("isAuthenticated() and @cafeService.isOwner(#cafe, #user)")
	@GetMapping("/delete-category")
	public String deleteCategory(
			@AuthenticationPrincipal User user,
			@ModelAttribute("activeCafe") Cafe cafe,
			@RequestParam("categoryId") Long categoryId,
			Model model) {
		val category = cafeService.getCategoryById(categoryId);
		cafeService.deleteCategory(category);
		return "redirect:" + toCafeUrl(cafe, null, null);
	}
	
	private String toCafeUrl(Cafe cafe, Category category, Page<Post> postPage) {
		val builder =  UriComponentsBuilder.newInstance()
				.path("/cafe/{cafeId}");
		if(category != null) {
			builder.queryParam("category", category.getId());
		}
		if(postPage != null) {
			builder
			.queryParam("page", postPage.getNumber())
			.queryParam("size", postPage.getSize());
		}
		return builder.buildAndExpand(cafe.getId())
				.encode().toUriString();
	}
}
