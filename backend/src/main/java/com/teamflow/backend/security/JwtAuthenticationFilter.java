package com.teamflow.backend.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{

	private static final String HEADER = "Authorization";
	private static final String PREFIX = "Bearer";
	
	private final JwtService jwtService;

	public JwtAuthenticationFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}
	@Override
	protected void doFilterInternal(HttpServletRequest request, 
			HttpServletResponse response, FilterChain chain) throws ServletException, IOException{
		String header = request.getHeader(HEADER);
		if(header != null && header.startsWith(PREFIX)) {
				String token = header.substring(PREFIX.length()).trim();
			if(jwtService.isValid(token)) {
				AuthUser authUser = new AuthUser(jwtService.extractUserId(token), jwtService.extractEmail(token));
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(authUser, null, List.of());
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		chain.doFilter(request, response);
	}
	
}
