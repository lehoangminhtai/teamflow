package com.teamflow.backend.controller;

import java.time.Instant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {
	public record HealthResponse(String status, String service, Instant time) {}
	
	@GetMapping("/health")
	public HealthResponse health() {
		return new HealthResponse("UP", "teamflow-backend", Instant.now());
	}
	
}
