package com.chinjja.issue.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.chinjja.issue.domain.Blog;
import com.chinjja.issue.domain.BlogData;
import com.chinjja.issue.domain.Category;
import com.chinjja.issue.domain.CategoryData;
import com.chinjja.issue.domain.Comment;
import com.chinjja.issue.domain.CommentData;
import com.chinjja.issue.domain.LikeCountData;
import com.chinjja.issue.service.BlogService;

import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class BlogControllerAdvice {
	private final BlogService blogService;
	
	@ModelAttribute("categoryList")
	public Iterable<Category> categoryList() {
		return blogService.getCategories();
	}
	
	@ModelAttribute
	public Blog blog() {
		return new Blog();
	}
	
	@ModelAttribute
	public BlogData blogData() {
		return new BlogData();
	}
	
	@ModelAttribute
	public Comment comment() {
		return new Comment();
	}
	
	@ModelAttribute
	public CommentData commentData() {
		return new CommentData();
	}
	
	@ModelAttribute
	public LikeCountData likeCountData() {
		return new LikeCountData();
	}
	
	@ModelAttribute
	public Category category() {
		return new Category();
	}
	
	@ModelAttribute
	public CategoryData categoryData() {
		return new CategoryData();
	}
}
