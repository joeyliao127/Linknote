package com.penguin.linknote.domain.auth;

import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.penguin.linknote.service.UserService;

@Component
public class AuthFacade {
	private final JWTProvider provider;
	private final UserService userService;

	public AuthFacade(JWTProvider provider, UserService userService) {
		this.provider = provider;
		this.userService = userService;
	}

	public boolean verify(String token) {
		var claim = provider.parseToken(token);
		boolean flag_existed = userService.existsById(claim.getUserId());
		return flag_existed;
	}

	public Authentication createAuthentication(String token) {
		AuthClaim claim = provider.parseToken(token);

		// 你可以先暫時用 SimpleGrantedAuthority("USER")
		return new UsernamePasswordAuthenticationToken(
			claim.getUserId(),
			null,
			List.of(new SimpleGrantedAuthority("USER"))
		);
	}
}
