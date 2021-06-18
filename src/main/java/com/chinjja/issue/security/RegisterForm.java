package com.chinjja.issue.security;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class RegisterForm {
	@Size(min = 4, max = 100)
	private String username;
	@Size(min = 8, max = 100)
	private String password;
	@NotNull(message = "not match")
	private String confirm;
	
	public void setPassword(String password) {
		this.password = password;
		checkPassword();
	}
	
	public void setConfirm(String confirm) {
		this.confirm = confirm;
		checkPassword();
	}
	
	private void checkPassword() {
		if (this.password == null || this.confirm == null) {
			return;
		} else if (!this.password.equals(confirm)) {
			this.confirm = null;
		}
	}
}
