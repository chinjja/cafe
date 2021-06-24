package com.chinjja.issue.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.chinjja.issue.domain.Post;
import com.chinjja.issue.domain.PostData;
import com.chinjja.issue.domain.CafeData;
import com.chinjja.issue.domain.Category;
import com.chinjja.issue.domain.CategoryData;
import com.chinjja.issue.domain.Comment;
import com.chinjja.issue.domain.CommentData;
import com.chinjja.issue.domain.LikeCountData;

import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class CafeControllerAdvice {
	
	@ModelAttribute
	public Post post() {
		return new Post();
	}
	
	@ModelAttribute
	public PostData postData() {
		return new PostData();
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
	
	@ModelAttribute
	public CafeData cafeData() {
		return new CafeData();
	}
	
	@ModelAttribute
	public JoinCafeForm joinCafeForm() {
		return new JoinCafeForm();
	}
}
