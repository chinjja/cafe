package com.chinjja.issue.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.chinjja.issue.data.UserRepository;
import com.chinjja.issue.domain.User;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private final UserDetailsService userDetailsService;
	private final UserRepository userRepo;
	
	@Bean
	public PasswordEncoder encoder() {
		return NoOpPasswordEncoder.getInstance();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
		.userDetailsService(userDetailsService)
		.passwordEncoder(encoder())
		;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
		.authorizeRequests()
		.antMatchers("/new_issue").hasRole("user")
		.antMatchers("/issues", "/register", "/", "/**").permitAll()
		
		.and()
		.formLogin()
		.loginPage("/login")
		
		.and()
		.logout()
		.logoutSuccessUrl("/")
		
		.and()
		.httpBasic()
		
		.and()
		.csrf()
		;
	}
	
	@Bean
	public CommandLineRunner createUser() {
		return args -> {
			val user = new User();
			user.setUsername("chinjja");
			user.setPassword("1234");
			userRepo.save(user);
		};
	}

}
