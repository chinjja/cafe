package com.chinjja.issue.form;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class LikeCountForm {
	@NotNull
	private Long likableId;
}
