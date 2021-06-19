package com.chinjja.issue.security;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordForm {
	@NotNull
	private Long userId;
	@NotBlank
	private String password;
	@Size(min = 8, max = 100)
	private String newPassword;
	@NotNull(message = "not match")
	private String confirmPassword;
	
	public void setNewPassword(String password) {
		this.newPassword = password;
		checkPassword();
	}
	
	public void setConfirmPassword(String confirm) {
		this.confirmPassword = confirm;
		checkPassword();
	}
	
	private void checkPassword() {
		if (this.newPassword == null || this.confirmPassword == null) {
			return;
		} else if (!this.newPassword.equals(confirmPassword)) {
			this.confirmPassword = null;
		}
	}
}
