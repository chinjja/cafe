package com.chinjja.issue.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.chinjja.issue.data.UserRepository;
import com.chinjja.issue.domain.User;

import lombok.RequiredArgsConstructor;
import lombok.val;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private final UserDetailsService userDetailsService;
	private final UserRepository userRepo;
	
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
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
		.formLogin()
		
		.and()
		.logout()
		.logoutSuccessUrl("/")
		
		.and()
		.httpBasic()
		
		.and()
		.csrf()
		.ignoringAntMatchers("/mes/**")
		;
	}
	
	@Bean
	public CommandLineRunner createUser() {
		return args -> {
			if(userRepo.count() == 0) {
				val user = new User();
				user.setUsername("admin");
				user.setPassword(encoder().encode("1234"));
				user.setRoles(new String[] { "ROLE_USER", "ROLE_ADMIN" });
				userRepo.save(user);
			}
		};
	}

}
