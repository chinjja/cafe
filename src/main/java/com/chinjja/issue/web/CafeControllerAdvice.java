package com.chinjja.issue.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.chinjja.issue.domain.Category;
import com.chinjja.issue.domain.Comment;
import com.chinjja.issue.domain.Post;
import com.chinjja.issue.form.CafeForm;
import com.chinjja.issue.form.CategoryForm;
import com.chinjja.issue.form.CommentForm;
import com.chinjja.issue.form.JoinCafeForm;
import com.chinjja.issue.form.PostForm;

import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class CafeControllerAdvice {
	
	@ModelAttribute
	public PostForm postData() {
		return new PostForm();
	}
	
	@ModelAttribute
	public CommentForm commentData() {
		return new CommentForm();
	}
	
	@ModelAttribute
	public CategoryForm categoryForm() {
		return new CategoryForm();
	}
	
	@ModelAttribute
	public CafeForm cafeForm() {
		return new CafeForm();
	}
	
	@ModelAttribute
	public JoinCafeForm joinCafeForm() {
		return new JoinCafeForm();
	}
}
