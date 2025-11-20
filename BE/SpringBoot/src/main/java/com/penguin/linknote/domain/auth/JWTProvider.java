package com.penguin.linknote.domain.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

@Component
public class JWTProvider {


	private final SecretKey key;

	// 自動讀取 IoC 裡面的 SecretKey Class，在 JWTConfig Config 中有設定
	public JWTProvider(SecretKey key) {
		this.key = key;
	}

	public String generateToken(UUID userId) {

		AuthClaim claim = new AuthClaim();
		claim.setUserId(userId);

		Instant now = Instant.now();
		Instant expiredDate = now.plus(30, ChronoUnit.DAYS);

		return Jwts.builder()
			.header()
				.add("alg", "HS256")
				.and()
			.claim("userId", userId)
			.expiration(Date.from(expiredDate))
			.issuedAt(Date.from(now))
			.signWith(key)
			.compact();
		
	}

	public AuthClaim parseToken(String token) {
		Jws<Claims> jws = Jwts.parser().verifyWith(key).build().parseSignedClaims(token);

		AuthClaim authClaim = new AuthClaim();
		String userId = jws.getPayload().get("userId", String.class);
		authClaim.setUserId(UUID.fromString(userId));

		return authClaim;
	}
}
