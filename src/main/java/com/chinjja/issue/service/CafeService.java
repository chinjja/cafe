package com.chinjja.issue.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chinjja.issue.data.CafeMemberRepository;
import com.chinjja.issue.data.CafeRepository;
import com.chinjja.issue.data.CategoryRepository;
import com.chinjja.issue.data.CommentRepository;
import com.chinjja.issue.data.LikableRepository;
import com.chinjja.issue.data.LikeUserRepository;
import com.chinjja.issue.data.PostRepository;
import com.chinjja.issue.domain.Cafe;
import com.chinjja.issue.domain.CafeMember;
import com.chinjja.issue.domain.Category;
import com.chinjja.issue.domain.Comment;
import com.chinjja.issue.domain.Likable;
import com.chinjja.issue.domain.LikeUser;
import com.chinjja.issue.domain.Post;
import com.chinjja.issue.domain.User;
import com.chinjja.issue.form.CafeForm;
import com.chinjja.issue.form.CategoryForm;
import com.chinjja.issue.form.CommentForm;
import com.chinjja.issue.form.JoinCafeForm;
import com.chinjja.issue.form.PostForm;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CafeService {
	private final LikableRepository likableRepo;
	private final PostRepository postRepo;
	private final CommentRepository commentRepo;
	private final LikeUserRepository likeCountRepo;
	private final CategoryRepository categoryRepo;
	private final CafeRepository cafeRepo;
	private final CafeMemberRepository cafeMemberRepo;
	
	private final Map<HashKey, LocalDateTime> lastVisitedTime = new ConcurrentHashMap<>();
	
	public Page<Post> getPostList(Cafe cafe, Category category, Pageable pageable) {
		Page<Post> posts;
		if(category == null) {
			val allCategory = categoryRepo.findAllByCafe(cafe);
			posts = postRepo.findAllByCategoryIn(allCategory, pageable);
		} else {
			posts = postRepo.findAllByCategory(category, pageable);
		}
		return posts;
	}
	
	@Transactional
	public Post createPost(User user, Category category, PostForm form) {
		val post = new Post();
		post.setUser(user);
		post.setCategory(category);
		post.setTitle(form.getTitle());
		post.setText(form.getText());
		return postRepo.save(post);
	}
	
	@Transactional
	public void deletePost(Post post) {
		for(val comment : post.getComments()) {
			deleteComment(comment);
		}
		for(val like : post.getLikes()) {
			deleteLikeUser(like);
		}
		postRepo.delete(post);
	}
	
	public Post getPostById(Long id) {
		return postRepo.findById(id).orElse(null);
	}
	
	@Transactional
	public void deleteComment(Comment comment) {
		for(val child : comment.getComments()) {
			deleteComment(child);
		}
		for(val like : comment.getLikes()) {
			deleteLikeUser(like);
		}
		commentRepo.delete(comment);
	}
	
	@Transactional
	public void deleteCategory(Category category) {
		for(val child : category.getCategories()) {
			deleteCategory(child);
		}
		for(val post : category.getPosts()) {
			deletePost(post);
		}
		categoryRepo.delete(category);
	}
	
	public boolean isOwner(Cafe cafe, User user) {
		if(user == null) return false;
		return cafe.getOwner().getId().equals(user.getId());
	}
	
	public boolean isOwner(String cafeId, User user) {
		if(user == null) return false;
		return isOwner(getCafeById(cafeId), user);
	}
	
	public boolean isMember(Cafe cafe, User user) {
		if(user == null) return false;
		val cm = getCafeMemberById(new CafeMember.Id(cafe, user));
		if(cm == null) return false;
		return cm.isApproved();
	}
	
	public boolean isJoined(Cafe cafe, User user) {
		return isOwner(cafe, user) || isMember(cafe, user);
	}
	
	public boolean isApproving(Cafe cafe, User user) {
		if(user == null) return false;
		val cm = getCafeMemberById(new CafeMember.Id(cafe, user));
		if(cm == null) return false;
		return !cm.isApproved();
	}
	
	public CafeMember getCafeMemberById(CafeMember.Id id) {
		return cafeMemberRepo.findById(id).orElse(null);
	}
	@Transactional
	public CafeMember approveMember(Cafe cafe, User member) {
		val cm = getCafeMemberById(new CafeMember.Id(cafe, member));
		cm.setApproved(true);
		return cafeMemberRepo.save(cm);
	}
	
	@Transactional
	public CafeMember joinCafe(Cafe cafe, User user, JoinCafeForm form) {
		if(isOwner(cafe, user))
			throw new IllegalArgumentException("owner cannot be member");
		
		val cm = new CafeMember();
		cm.setId(new CafeMember.Id(cafe, user));
		cm.setGreeting(form.getGreeting());
		cm.setApproved(!cafe.isNeedApproval());
		return cafeMemberRepo.save(cm);
	}
	
	@Transactional
	public void leaveCafe(Cafe cafe, User user) {
		cafeMemberRepo.deleteById(new CafeMember.Id(cafe, user));
	}
	
	public boolean isAuthor(Likable likable, User user) {
		if(user == null) return false;
		return likable.getUser().getId().equals(user.getId());
	}
	
	public boolean isAuthor(Long likableId, User user) {
		return isAuthor(getLikableById(likableId), user);
	}
	
	public boolean hasCafe(String id) {
		return cafeRepo.existsById(id);
	}
	
	@Transactional
	public Cafe createCafe(CafeForm form, User user) {
		if(hasCafe(form.getId())) throw new IllegalArgumentException(form.getId() +" already exists");
		val cafe = new Cafe();
		cafe.setId(form.getId());
		cafe.setTitle(form.getTitle());
		cafe.setDescription(form.getDescription());
		cafe.setWelcome(form.getWelcome());
		cafe.setNeedApproval(form.isNeedApproval());
		cafe.setOwner(user);
		cafe.setPrivacy(form.isPrivacy());
		return cafeRepo.save(cafe);
	}
	
	@Transactional
	public Cafe editCafe(Cafe cafe, CafeForm form) {
		cafe.setTitle(form.getTitle());
		cafe.setDescription(form.getDescription());
		cafe.setWelcome(form.getWelcome());
		cafe.setNeedApproval(form.isNeedApproval());
		cafe.setPrivacy(form.isPrivacy());
		return cafeRepo.save(cafe);
	}
	
	@Transactional
	public void deleteCafe(Cafe cafe) {
		cafe = getCafeById(cafe.getId());
		for(val category : cafe.getRootCategories()) {
			deleteCategory(category);
		}
		for(val member : cafe.getAllMembers()) {
			cafeMemberRepo.delete(member);
		}
		cafeRepo.delete(cafe);
	}
	
	public Iterable<Cafe> getPublicCafeList() {
		return cafeRepo.findAllByPrivacy(false);
	}
	
	public long countPublicCafeList() {
		return cafeRepo.countByPrivacy(false);
	}
	
	public Cafe getCafeById(String id) {
		return cafeRepo.findById(id).orElse(null);
	}
	
	@Transactional
	public Comment createComment(User user, CommentForm form) {
		val comment = new Comment();
		comment.setUser(user);
		comment.setLikable(likableRepo.findById(form.getLikableId()).get());
		comment.setText(form.getText());
		return commentRepo.save(comment);
	}
	
	public Comment getCommentById(Long id) {
		return commentRepo.findById(id).orElse(null);
	}
	
	public Likable getLikableById(Long id) {
		return likableRepo.findById(id).orElse(null);
	}
	
	public LikeUser getLikeCountById(LikeUser.Id id) {
		return likeCountRepo.findById(id).orElse(null);
	}
	
	public LikeUser getLikeUser(Likable likable, User user) {
		return getLikeCountById(new LikeUser.Id(likable, user));
	}
	
	public boolean isLiked(LikeUser likeCount) {
		return isLiked(likeCount.getId());
	}
	
	public boolean isLiked(LikeUser.Id id) {
		return likeCountRepo.existsById(id);
	}
	
	public boolean isLiked(Likable likable, User user) {
		return likeCountRepo.existsById(new LikeUser.Id(likable, user));
	}
	
	@Transactional
	public LikeUser createLikeUser(User user, Likable likable) {
		val likeCount = LikeUser.create(likable, user);
		return likeCountRepo.save(likeCount);
	}
	
	@Transactional
	public void deleteLikeUser(LikeUser id) {
		likeCountRepo.delete(id);
	}
	
	@Transactional
	public void toggleLike(LikeUser likeCount) {
		toggleLike(likeCount.getId().getUser(), likeCount.getId().getLikable());
	}
	
	@Transactional
	public void toggleLike(User user, Likable likable) {
		val like = getLikeUser(likable, user);
		if(like == null) {
			createLikeUser(user, likable);
		} else {
			deleteLikeUser(like);
		}
	}
	
	@Transactional
	public Category createCategory(Cafe cafe, CategoryForm form) {
		Category parent = null;
		if(form.getParentCategoryId() != null) {
			parent = getCategoryById(form.getParentCategoryId());
		}
		val category = new Category();
		category.setCafe(cafe);
		category.setParent(parent);
		category.setTitle(form.getTitle());
		category.setType(form.getType());
		return categoryRepo.save(category);
	}
	
	public Category getCategoryById(Long id) {
		return categoryRepo.findById(id).orElse(null);
	}
	
	public Iterable<Category> getRootCateforyList(Cafe cafe) {
		return categoryRepo.findAllByCafeAndParentIsNull(cafe);
	}
	
	public Iterable<CafeMember> getNotApprovedMembers(User owner) {
		val list = new ArrayList<CafeMember>();
		for(val cafe : owner.getCafes()) {
			list.addAll(cafeMemberRepo.findByIdCafeAndApproved(cafe, false));
		}
		return list;
	}
	
	@Transactional
	public void visit(User user, Post post) {
		if(user == null) return;
		val now = LocalDateTime.now();
		val key = new HashKey(post.getId(), user.getId());
		val lastTime = lastVisitedTime.get(key);
		if(lastTime == null || lastTime.compareTo(now.minusMinutes(1)) < 0) {
			post.setViewCount(post.getViewCount() + 1);
			postRepo.save(post);
		}
		lastVisitedTime.put(key, now);
	}
	
	@Scheduled(fixedDelay = 5*1000)
	public void cleanupTake() {
		val bias = LocalDateTime.now().minusMinutes(1);
		val rem = new ArrayList<>();
		for(val i : lastVisitedTime.entrySet()) {
			val k = i.getKey();
			val v = i.getValue();
			if(v.compareTo(bias) < 0) {
				rem.add(k);
			}
		}
		for(val i : rem) {
			lastVisitedTime.remove(i);
		}
	}
	
	@Data
	static class HashKey {
		final Long blog;
		final Long user;
	}
}
