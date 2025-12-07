package com.penguin.linknote.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.penguin.linknote.domain.auth.filter.JWTAuthenticationFilter;
import com.penguin.linknote.domain.rbac.RBACAuthorizationManager;

@Configuration
@EnableWebSecurity
public class WebSecurity {

	private final JWTAuthenticationFilter jwtFilter;
	private final RBACAuthorizationManager rbacManager;

	public WebSecurity(JWTAuthenticationFilter jwtFilter, RBACAuthorizationManager rbacManager) {
		this.jwtFilter = jwtFilter;
		this.rbacManager = rbacManager;
	}


	@Bean
	public InMemoryUserDetailsManager generateInMemoryUsers() {

		// password 算法 {} 可選 noop(明碼), bcrypt, pbkdf2, scrypt, sha256 ... 
		UserDetails user1 = User
							.withUsername("test")
							.password("{noop}123")
							.roles("ADMIN", "USER")
							.build();

		UserDetails user2 = User
							.withUsername("test2")
							// 222 bcrypt 後的結果
							.password("{bcrypt}$2a$12$3CF/9TksMbpPh5k4vaoVEO9LlGKLyRf5lhgDR/z1qUMch/oO9uOcG")
							.roles("ADMIN", "USER")
							.build();
		
		return new InMemoryUserDetailsManager(user1, user2);
	}

	@Bean
	public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
		return http
				.csrf(csrf -> csrf.disable())
				.httpBasic(Customizer.withDefaults())
				.formLogin(Customizer.withDefaults())
				.addFilterBefore(jwtFilter, BasicAuthenticationFilter.class)
				.authorizeHttpRequests(req -> req
					.requestMatchers("/swagger-ui/**").permitAll()
					.requestMatchers("/swagger-ui.html").permitAll()
					.requestMatchers("/swagger-ui").permitAll()
					.requestMatchers("/api/v3/api-docs/**").permitAll()
					.requestMatchers("/v3/api-docs/**").permitAll()
					.requestMatchers("/api/users/token").permitAll()
					.requestMatchers(HttpMethod.POST, "/api/users/signIn").permitAll()
					.requestMatchers(HttpMethod.POST, "/api/users/signUp").permitAll()
					.requestMatchers("/api/notebooks").permitAll()
					.requestMatchers("/api/notes").permitAll()
					.requestMatchers("/api/notebooks/**").access(rbacManager)
					.requestMatchers("/api/notes/**").access(rbacManager)
					.anyRequest().authenticated())
				.build();
	}

}
