package com.chinjja.issue.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.chinjja.issue.data.CafeMemberRepository;
import com.chinjja.issue.data.CafeRepository;
import com.chinjja.issue.data.CategoryRepository;
import com.chinjja.issue.data.CommentRepository;
import com.chinjja.issue.data.LikableRepository;
import com.chinjja.issue.data.LikeCountRepository;
import com.chinjja.issue.data.PostRepository;
import com.chinjja.issue.domain.Cafe;
import com.chinjja.issue.domain.CafeMember;
import com.chinjja.issue.domain.Category;
import com.chinjja.issue.domain.Comment;
import com.chinjja.issue.domain.Likable;
import com.chinjja.issue.domain.LikeCount;
import com.chinjja.issue.domain.Post;
import com.chinjja.issue.domain.User;
import com.chinjja.issue.form.CafeForm;
import com.chinjja.issue.form.CategoryForm;
import com.chinjja.issue.form.CommentForm;
import com.chinjja.issue.form.JoinCafeForm;
import com.chinjja.issue.form.LikeCountForm;
import com.chinjja.issue.form.PostForm;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@RequiredArgsConstructor
public class CafeService {
	private final LikableRepository likableRepo;
	private final PostRepository postRepo;
	private final CommentRepository commentRepo;
	private final LikeCountRepository likeCountRepo;
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
	
	public Post createPost(User user, PostForm form) {
		Post post = Post.builder()
				.user(user)
				.category(categoryRepo.findById(form.getCategoryId()).get())
				.title(form.getTitle())
				.contents(form.getContents())
				.build();
		return postRepo.save(post);
	}
	
	@Transactional
	public void deletePost(Post post) {
		for(val comment : post.getComments()) {
			deleteComment(comment);
		}
		postRepo.delete(post);
	}
	
	@Transactional
	public void deleteComment(Comment comment) {
		for(val child : comment.getComments()) {
			deleteComment(child);
		}
		commentRepo.delete(comment);
	}
	
	@Transactional
	public void deleteCategory(Category category) {
		for(val child : category.getCategories()) {
			deleteCategory(child);
		}
		switch(category.getType()) {
		case POST:
			for(val post : category.getPosts()) {
				deletePost(post);
			}
		case DIRECTORY:
			categoryRepo.delete(category);
			break;
		}
	}
	
	public boolean isOwner(Cafe cafe, User user) {
		return cafe.getOwner().getId().equals(user.getId());
	}
	
	public boolean isMember(Cafe cafe, User user) {
		val cm = cafeMemberRepo.findById(new CafeMember.Id(cafe, user)).orElse(null);
		if(cm == null) return false;
		return cm.isApproved();
	}
	
	public boolean isJoined(Cafe cafe, User user) {
		return isOwner(cafe, user) || isMember(cafe, user);
	}
	
	public boolean isApproving(Cafe cafe, User user) {
		val cm = cafeMemberRepo.findById(new CafeMember.Id(cafe, user)).orElse(null);
		if(cm == null) return false;
		return !cm.isApproved();
	}
	
	public void approveMember(Cafe cafe, User member) {
		val cm = cafeMemberRepo.findById(new CafeMember.Id(cafe, member)).get();
		cm.setApproved(true);
		cafeMemberRepo.save(cm);
	}
	
	public void joinCafe(Cafe cafe, User user, JoinCafeForm form) {
		val cm = CafeMember.builder()
				.id(new CafeMember.Id(cafe, user))
				.greeting(form.getGreeting())
				.approved(!cafe.isNeedApproval())
				.build();
		cafeMemberRepo.save(cm);
	}
	
	public void leaveCafe(Cafe cafe, User user) {
		cafeMemberRepo.deleteById(new CafeMember.Id(cafe, user));
	}
	
	public boolean isAuthor(Likable target, User user) {
		return target.getUser().getId().equals(user.getId());
	}
	
	public boolean hasCafe(String id) {
		return cafeRepo.existsById(id);
	}
	
	public void createCafe(CafeForm form, User user) {
		if(hasCafe(form.getId())) throw new IllegalArgumentException(form.getId() +" already exists");
		val cafe = Cafe.builder()
				.id(form.getId())
				.name(form.getName())
				.description(form.getDescription())
				.needApproval(form.isNeedApproval())
				.owner(user)
				.build();
		cafeRepo.save(cafe);
	}
	
	@Transactional
	public void deleteCafe(Cafe cafe) {
		for(val category : categoryRepo.findAllByCafeAndParentIsNull(cafe)) {
			deleteCategory(category);
		}
		cafe = cafeRepo.findById(cafe.getId()).get();
		for(val member : cafe.getMembers()) {
			cafeMemberRepo.delete(member);
		}
		cafeRepo.delete(cafe);
	}
	
	public Comment createComment(User user, @Valid CommentForm form) {
		Comment comment = Comment.builder()
				.user(user)
				.likable(likableRepo.findById(form.getLikableId()).get())
				.comment(form.getComment())
				.build();
		return commentRepo.save(comment);
	}
	
	@Transactional
	public LikeCount createLikeCount(User user, LikeCountForm form) {
		val target = likableRepo.findById(form.getLikableId()).get();
		val likeCount = LikeCount.create(target, user);
		return likeCountRepo.save(likeCount);
	}
	
	public void toggleLikeCount(User user, LikeCountForm form) {
		val target = likableRepo.findById(form.getLikableId()).get();
		val like = likeCountRepo.findById(new LikeCount.Id(target, user)).orElse(null);
		if(like == null) {
			createLikeCount(user, form);
		} else {
			likeCountRepo.delete(like);
		}
	}
	
	public Category createCategory(Cafe cafe, User user, CategoryForm form) {
		Category parent = null;
		if(form.getParentCategoryId() != null) {
			parent = categoryRepo.findById(form.getParentCategoryId()).get();
		}
		val category = Category.builder()
				.cafe(cafe)
				.parent(parent)
				.name(form.getName())
				.type(form.getType())
				.build();
		return categoryRepo.save(category);
	}
	
	public Iterable<CafeMember> getNotApprovedMembers(User owner) {
		val list = new ArrayList<CafeMember>();
		for(val cafe : owner.getCafes()) {
			list.addAll(cafeMemberRepo.findByIdCafeAndApproved(cafe, false));
		}
		return list;
	}
	
	public boolean canLikeCount(Likable target, User user) {
		val like = likeCountRepo.findById(new LikeCount.Id(target, user)).orElse(null);
		return like == null;
	}
	
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
