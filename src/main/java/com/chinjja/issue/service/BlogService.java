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

import com.chinjja.issue.data.BlogRepository;
import com.chinjja.issue.data.CafeRepository;
import com.chinjja.issue.data.CategoryRepository;
import com.chinjja.issue.data.CommentRepository;
import com.chinjja.issue.data.ElementRepository;
import com.chinjja.issue.data.LikeCountRepository;
import com.chinjja.issue.domain.Blog;
import com.chinjja.issue.domain.BlogData;
import com.chinjja.issue.domain.Cafe;
import com.chinjja.issue.domain.Category;
import com.chinjja.issue.domain.CategoryData;
import com.chinjja.issue.domain.Comment;
import com.chinjja.issue.domain.CommentData;
import com.chinjja.issue.domain.Element;
import com.chinjja.issue.domain.LikeCount;
import com.chinjja.issue.domain.LikeCountData;
import com.chinjja.issue.domain.LikeCountId;
import com.chinjja.issue.domain.User;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@RequiredArgsConstructor
public class BlogService {
	private final ElementRepository elementRepo;
	private final BlogRepository blogRepo;
	private final CommentRepository commentRepo;
	private final LikeCountRepository likeCountRepo;
	private final CategoryRepository categoryRepo;
	private final CafeRepository cafeRepo;
	
	private final Map<HashKey, LocalDateTime> lastVisitedTime = new ConcurrentHashMap<>();
	
	public Page<Blog> getBlogList(Cafe cafe, Category category, Pageable pageable) {
		Page<Blog> blogs;
		if(category == null) {
			val allCategory = categoryRepo.findAllByCafe(cafe);
			blogs = blogRepo.findAllByCategoryIn(allCategory, pageable);
		} else {
			blogs = blogRepo.findAllByCategory(category, pageable);
		}
		return blogs;
	}
	
	public Blog getBlogById(Long id) {
		val blog = blogRepo.findById(id).get();
		return blog;
	}
	
	public Blog createBlog(User user, BlogData form) {
		val blog = new Blog();
		blog.setData(form);
		blog.setUser(user);
		blog.setCategory(categoryRepo.findById(form.getCategoryId()).get());
		return blogRepo.save(blog);
	}
	
	public Comment createComment(User user, @Valid CommentData form) {
		val comment = new Comment();
		comment.setData(form);
		comment.setUser(user);
		comment.setTarget(elementRepo.findById(form.getTargetId()).get());
		return commentRepo.save(comment);
	}
	
	@Transactional
	public LikeCount createLikeCount(User user, LikeCountData form) {
		val target = elementRepo.findById(form.getTarget()).get();
		val likeCount = new LikeCount();
		likeCount.setId(new LikeCountId(target, user));
		return likeCountRepo.save(likeCount);
	}
	
	public void toggleLikeCount(User user, LikeCountData form) {
		val target = elementRepo.findById(form.getTarget()).get();
		val like = likeCountRepo.findById(new LikeCountId(target, user)).orElse(null);
		if(like == null) {
			createLikeCount(user, form);
		} else {
			likeCountRepo.delete(like);
		}
	}
	
	public Category createCategory(User user, CategoryData form) {
		Category parent = null;
		if(form.getParentId() != null) {
			parent = categoryRepo.findById(form.getParentId()).get();
		}
		val category = new Category();
		category.setData(form);
		category.setParent(parent);
		category.setCafe(cafeRepo.findById(form.getCafeId()).get());
		return categoryRepo.save(category);
	}
	
	public boolean canLikeCount(Element target, User user) {
		val like = likeCountRepo.findById(new LikeCountId(target, user)).orElse(null);
		return like == null;
	}
	
	public void visit(User user, Blog blog) {
		if(user == null) return;
		val now = LocalDateTime.now();
		val key = new HashKey(blog.getId(), user.getId());
		val lastTime = lastVisitedTime.get(key);
		if(lastTime == null || lastTime.compareTo(now.minusMinutes(1)) < 0) {
			blog.setViewCount(blog.getViewCount() + 1);
			blogRepo.save(blog);
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
