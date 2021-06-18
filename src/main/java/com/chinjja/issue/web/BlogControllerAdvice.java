package com.chinjja.issue.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.chinjja.issue.domain.Blog;
import com.chinjja.issue.domain.BlogData;
import com.chinjja.issue.domain.Comment;
import com.chinjja.issue.domain.CommentData;
import com.chinjja.issue.domain.LikeCountData;

import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class BlogControllerAdvice {
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
}
