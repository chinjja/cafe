package com.chinjja.issue.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.chinjja.issue.data.BlogRepository;
import com.chinjja.issue.data.CategoryRepository;
import com.chinjja.issue.data.CommentRepository;
import com.chinjja.issue.data.ElementRepository;
import com.chinjja.issue.data.LikeCountRepository;
import com.chinjja.issue.domain.Blog;
import com.chinjja.issue.domain.BlogData;
import com.chinjja.issue.domain.Category;
import com.chinjja.issue.domain.CategoryData;
import com.chinjja.issue.domain.Comment;
import com.chinjja.issue.domain.CommentData;
import com.chinjja.issue.domain.Element;
import com.chinjja.issue.domain.LikeCount;
import com.chinjja.issue.domain.LikeCountData;
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
	
	private final Map<HashKey, LocalDateTime> lastVisitedTime = new HashMap<>();
	
	public Page<Blog> getBlogList(Category category, Pageable pageable) {
		Page<Blog> blogs;
		if(category == null) {
			blogs = blogRepo.findAll(pageable);
		} else {
			blogs = blogRepo.findAllByCategory(category, pageable);
		}
		for(Blog blog : blogs) {
			bind(blog);
		}
		return blogs;
	}
	
	public Blog getBlogById(Long id) {
		val blog = blogRepo.findById(id).get();
		bind(blog);
		return blog;
	}
	
	private void bind(Blog blog) {
		int count = likeCountRepo.countByTarget(blog);
		blog.setLikeCount(count);
	}
	
	public Blog createBlog(User user, BlogData form) {
		val blog = new Blog();
		blog.setData(form);
		blog.setUser(user);
		blog.setCategory(categoryRepo.findById(form.getCategory()).get());
		return blogRepo.save(blog);
	}
	
	public Comment createComment(User user, CommentData form) {
		val comment = new Comment();
		comment.setData(form);
		comment.setUser(user);
		comment.setTarget(elementRepo.findById(form.getTarget()).get());
		return commentRepo.save(comment);
	}
	
	@Transactional
	public LikeCount createLikeCount(User user, LikeCountData form) {
		val likeCount = new LikeCount();
		likeCount.setTarget(elementRepo.findById(form.getTarget()).get());
		likeCount.setUser(user);
		return likeCountRepo.save(likeCount);
	}
	
	public void toggleLikeCount(User user, LikeCountData form) {
		val target = elementRepo.findById(form.getTarget()).get();
		val like = likeCountRepo.findByTargetAndUser(target, user);
		if(like == null) {
			createLikeCount(user, form);
		} else {
			likeCountRepo.delete(like);
		}
	}
	
	public Iterable<Category> getCategories() {
		return categoryRepo.findAllRootByParentIsNull();
	}
	
	public Category createCategory(User user, CategoryData form) {
		Category parent = null;
		if(form.getParent() != null) {
			parent = categoryRepo.findById(form.getParent()).get();
		}
		val category = new Category();
		category.setData(form);
		category.setParent(parent);
		return categoryRepo.save(category);
	}
	
	public boolean canLikeCount(Element target, User user) {
		val like = likeCountRepo.findByTargetAndUser(target, user);
		return like == null;
	}
	
	public synchronized void visit(User user, Blog blog) {
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
	public synchronized void cleanupTake() {
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
