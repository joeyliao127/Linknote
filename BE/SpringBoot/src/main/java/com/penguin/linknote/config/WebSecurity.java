package com.penguin.linknote.config;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
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

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Configuration
@EnableWebSecurity
public class WebSecurity {

	@Value("${jwt.secret.key}")
	private String key;

	@Bean
    public SecretKey jwtSecretKey() {
        // 從環境變量讀取,轉換成 SecretKey
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
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
	public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception{
		return http
				.csrf(csrf -> csrf.disable())
				.httpBasic(Customizer.withDefaults())
				.formLogin(Customizer.withDefaults())
				.authorizeHttpRequests(req -> req
					.requestMatchers("/api/users/token").permitAll()
					.requestMatchers(HttpMethod.POST, "/api/users/signIn").permitAll()
					.requestMatchers(HttpMethod.POST, "/api/users/signUp").permitAll()
					.anyRequest().authenticated())
				//TODO: JWTFilter
				// .addFilterBefore(new JWTAuthenticationFilter(), BasicAuthenticationFilter.class)
				.build();
	}

	
}
