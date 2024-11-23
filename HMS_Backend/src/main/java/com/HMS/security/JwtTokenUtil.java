package com.HMS.security;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenUtil {
	 private static final long JWT_TOKEN_VALIDITY = 20 * 60 * 60 * 1000; // Token validity in milliseconds

	    // Generate a secure key for HS512
	    private final SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

	    public String getEmailFromToken(String token) {
	        return getClaimFromToken(token, Claims::getSubject);
	    }

	    public Date getExpirationDateFromToken(String token) {
	        return getClaimFromToken(token, Claims::getExpiration);
	    }

	    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
	        final Claims claims = getAllClaimsFromToken(token);
	        return claimsResolver.apply(claims);
	    }

	    private Claims getAllClaimsFromToken(String token) {
	        return Jwts.parserBuilder()
	                .setSigningKey(secretKey)
	                .build()
	                .parseClaimsJws(token)
	                .getBody();
	    }

	    private Boolean isTokenExpired(String token) {
	        final Date expiration = getExpirationDateFromToken(token);
	        return expiration.before(new Date());
	    }

	    public String generateToken(UserDetails userDetails) {
	        Map<String, Object> claims = new HashMap<>();
	        return doGenerateToken(claims, userDetails.getUsername()); // Using email as subject
	    }

	    private String doGenerateToken(Map<String, Object> claims, String subject) {
	        return Jwts.builder()
	                .setClaims(claims)
	                .setSubject(subject) // Subject is now email
	                .setIssuedAt(new Date(System.currentTimeMillis()))
	                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
	                .signWith(secretKey)
	                .compact();
	    }

	    public Boolean validateToken(String token, UserDetails userDetails) {
	        final String email = getEmailFromToken(token);
	        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
	    }
}
