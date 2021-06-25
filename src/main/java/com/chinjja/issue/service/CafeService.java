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

import com.chinjja.issue.data.PostRepository;
import com.chinjja.issue.data.CafeMemberRepository;
import com.chinjja.issue.data.CafeRepository;
import com.chinjja.issue.data.CategoryRepository;
import com.chinjja.issue.data.CommentRepository;
import com.chinjja.issue.data.LikableRepository;
import com.chinjja.issue.data.LikeCountRepository;
import com.chinjja.issue.domain.Post;
import com.chinjja.issue.domain.PostData;
import com.chinjja.issue.domain.Cafe;
import com.chinjja.issue.domain.CafeData;
import com.chinjja.issue.domain.CafeMember;
import com.chinjja.issue.domain.CafeMemberId;
import com.chinjja.issue.domain.Category;
import com.chinjja.issue.domain.CategoryData;
import com.chinjja.issue.domain.Comment;
import com.chinjja.issue.domain.CommentData;
import com.chinjja.issue.domain.Likable;
import com.chinjja.issue.domain.LikeCount;
import com.chinjja.issue.domain.LikeCountData;
import com.chinjja.issue.domain.LikeCountId;
import com.chinjja.issue.domain.User;
import com.chinjja.issue.web.JoinCafeForm;

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
	
	public Post createPost(User user, PostData form) {
		val post = new Post();
		post.setData(form);
		post.setUser(user);
		post.setCategory(categoryRepo.findById(form.getCategoryId()).get());
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
		switch(category.getData().getType()) {
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
		val cm = cafeMemberRepo.findById(new CafeMemberId(cafe, user)).orElse(null);
		if(cm == null) return false;
		return cm.isApproved();
	}
	
	public boolean isJoined(Cafe cafe, User user) {
		return isOwner(cafe, user) || isMember(cafe, user);
	}
	
	public boolean isApproving(Cafe cafe, User user) {
		val cm = cafeMemberRepo.findById(new CafeMemberId(cafe, user)).orElse(null);
		if(cm == null) return false;
		return !cm.isApproved();
	}
	
	public void approveMember(Cafe cafe, User member) {
		val cm = cafeMemberRepo.findById(new CafeMemberId(cafe, member)).get();
		cm.setApproved(true);
		cafeMemberRepo.save(cm);
	}
	
	public void joinCafe(Cafe cafe, User user, JoinCafeForm form) {
		val cm = new CafeMember();
		cm.setId(new CafeMemberId(cafe, user));
		cm.setGreeting(form.getGreeting());
		cm.setApproved(!cafe.getNeedApproval());
		cafeMemberRepo.save(cm);
	}
	
	public void leaveCafe(Cafe cafe, User user) {
		cafeMemberRepo.deleteById(new CafeMemberId(cafe, user));
	}
	
	public boolean isAuthor(Likable target, User user) {
		return target.getUser().getId().equals(user.getId());
	}
	
	public boolean hasCafe(String id) {
		return cafeRepo.existsById(id);
	}
	
	public void createCafe(User user, CafeData form) {
		if(hasCafe(form.getId())) throw new IllegalArgumentException(form.getId() +" already exists");
		val cafe = new Cafe();
		cafe.setData(form);
		cafe.setOwner(user);
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
	
	public Comment createComment(User user, @Valid CommentData form) {
		val comment = new Comment();
		comment.setData(form);
		comment.setUser(user);
		comment.setLikable(likableRepo.findById(form.getLikableId()).get());
		return commentRepo.save(comment);
	}
	
	@Transactional
	public LikeCount createLikeCount(User user, LikeCountData form) {
		val target = likableRepo.findById(form.getLikableId()).get();
		val likeCount = new LikeCount();
		likeCount.setId(new LikeCountId(target, user));
		return likeCountRepo.save(likeCount);
	}
	
	public void toggleLikeCount(User user, LikeCountData form) {
		val target = likableRepo.findById(form.getLikableId()).get();
		val like = likeCountRepo.findById(new LikeCountId(target, user)).orElse(null);
		if(like == null) {
			createLikeCount(user, form);
		} else {
			likeCountRepo.delete(like);
		}
	}
	
	public Category createCategory(Cafe cafe, User user, CategoryData form) {
		Category parent = null;
		if(form.getParentId() != null) {
			parent = categoryRepo.findById(form.getParentId()).get();
		}
		val category = new Category();
		category.setData(form);
		category.setParent(parent);
		category.setCafe(cafe);
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
		val like = likeCountRepo.findById(new LikeCountId(target, user)).orElse(null);
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
