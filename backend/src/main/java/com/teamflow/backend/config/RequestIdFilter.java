package com.teamflow.backend.config;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestIdFilter extends OncePerRequestFilter {
	public static final String REQUEST_ID = "requestId";
	private static final String HEADER = "X-Request-Id";
	private static final Logger log = LoggerFactory.getLogger(RequestIdFilter.class);
	
	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain chain
			) throws ServletException, IOException {
		String requestId = request.getHeader(HEADER);
		if(requestId == null || requestId.isBlank()) {
			requestId = UUID.randomUUID().toString().substring(0,8);
		}
		MDC.put(REQUEST_ID, requestId);
		response.setHeader(HEADER, requestId);
		
		long startedAt = System.currentTimeMillis();
		
		try {
			chain.doFilter(request, response);
		} finally {
			long tookMs = System.currentTimeMillis() - startedAt;
			log.info("{} {}->{} ({}ms)",
					request.getMethod(),
					request.getRequestURI(),
					response.getStatus(),
					tookMs
					);
			MDC.clear();
		}
	}
	
}
