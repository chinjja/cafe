package com.chinjja.issue.service;

import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.chinjja.issue.data.BlogRepository;
import com.chinjja.issue.data.CommentRepository;
import com.chinjja.issue.data.ElementRepository;
import com.chinjja.issue.data.LikeCountRepository;
import com.chinjja.issue.domain.Blog;
import com.chinjja.issue.domain.BlogData;
import com.chinjja.issue.domain.Comment;
import com.chinjja.issue.domain.CommentData;
import com.chinjja.issue.domain.Element;
import com.chinjja.issue.domain.LikeCount;
import com.chinjja.issue.domain.User;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Service
@RequiredArgsConstructor
@Transactional
public class BlogService {
	private final ElementRepository elementRepo;
	private final BlogRepository blogRepo;
	private final CommentRepository commentRepo;
	private final LikeCountRepository likeCountRepo;
	
	public Iterable<Blog> getBlogList() {
		val blogs = blogRepo.findAll(Sort.by(Order.desc("createdAt")));
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
		int count = likeCountRepo.countByElement(blog);
		blog.setLikeCount(count);
	}
	
	public Blog createBlog(User user, BlogData form) {
		val blog = new Blog();
		blog.setData(form);
		blog.setUser(user);
		return blogRepo.save(blog);
	}
	
	public Iterable<Comment> getCommentList(Element target) {
		return commentRepo.findAllByTarget(target, Sort.by(Order.asc("createdAt")));
	}
	
	public Comment createComment(User user, CommentData form) {
		val comment = new Comment();
		comment.setData(form);
		comment.setUser(user);
		comment.setTarget(elementRepo.findById(form.getTarget()).get());
		return commentRepo.save(comment);
	}
	
	public LikeCount likeCount(Element target) {
		val likeCount = new LikeCount();
		likeCount.setElement(target);
		return likeCountRepo.save(likeCount);
	}
	
	public Blog visit(Blog blog) {
		blog.setViews(blog.getViews() + 1);
		return blogRepo.save(blog);
	}
}
