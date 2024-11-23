package com.HMS.security;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	   @Autowired
	    private UserDetailsService jwtUserDetailsService;

	    @Autowired
	    private JwtTokenUtil jwtTokenUtil;

	    @Override
	    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
	            throws ServletException, IOException {

	        final String requestTokenHeader = request.getHeader("Authorization");

	        String email = null;
	        String jwtToken = null;

	        // JWT token is expected to be in the form "Bearer <token>"
	        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
	            jwtToken = requestTokenHeader.substring(7);
	            try {
	                // Extract email from the JWT token
	                email = jwtTokenUtil.getEmailFromToken(jwtToken);
	            } catch (IllegalArgumentException e) {
	                System.out.println("Unable to get JWT Token");
	            } catch (ExpiredJwtException e) {
	                System.out.println("JWT Token has expired");
	            }
	        } else {
	            logger.warn("JWT Token does not begin with Bearer String");
	        }

	        // Validate token and set security context if email is extracted successfully
	        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

	            UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(email);

	            // Validate the token
	            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
	            	logger.info("Token validated for user: " + email);
	                // Set the authentication details in the security context
	                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
	                        userDetails, null, userDetails.getAuthorities());
	                usernamePasswordAuthenticationToken
	                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
	            }else {
	                logger.warn("Token validation failed for user: " + email);
	            }
	        }
	        chain.doFilter(request, response);
	    }
}
