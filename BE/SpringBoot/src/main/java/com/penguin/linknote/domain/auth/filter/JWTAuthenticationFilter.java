package com.penguin.linknote.domain.auth.filter;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.penguin.linknote.domain.auth.AuthFacade;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter{

	private final AuthFacade authFacade;

	public JWTAuthenticationFilter(AuthFacade authFacade) {
		this.authFacade = authFacade;	
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String token = extractToken(request);

		if (token != null && authFacade.verify(token)) {
			var auth = authFacade.createAuthentication(token);
			SecurityContextHolder.getContext().setAuthentication(auth);
		}

		chain.doFilter(request, response);
	}

	private String extractToken(HttpServletRequest request) {
		return request.getHeader("Authorization").substring(7);
	}
}
