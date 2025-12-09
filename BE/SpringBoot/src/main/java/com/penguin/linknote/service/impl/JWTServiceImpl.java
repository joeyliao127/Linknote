package com.penguin.linknote.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.penguin.linknote.domain.auth.AuthClaim;
import com.penguin.linknote.domain.auth.JWTProvider;
import com.penguin.linknote.domain.auth.exception.InvalidTokenException;
import com.penguin.linknote.domain.user.UserDTO;
import com.penguin.linknote.service.JWTService;
import com.penguin.linknote.service.UserService;

@Service
public class JWTServiceImpl implements JWTService {

	private JWTProvider jwtProvider;
	private UserService userService;

	public JWTServiceImpl(JWTProvider jwtProvider, UserService userService){
		this.jwtProvider = jwtProvider;
		this.userService = userService;
	}

	@Override
	public Boolean verifyToken(String token) {
		AuthClaim claim = jwtProvider.parseToken(token);
		UserDTO userDTO = userService.getUserById(claim.getUserId());
		if(userDTO != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String generateToken(UUID userId, String email, String username) {
		return jwtProvider.generateToken(userId, email, username);
	}


	@Override
	public AuthClaim parseBearerToken(String bearerToken) {
		String[] parts = bearerToken.split(" ");

		if(parts.length != 2) {
			throw new InvalidTokenException("Invalid bearerToken.");
		}

		return jwtProvider.parseToken(parts[1]);
	}
	
}
