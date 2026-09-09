package com.teamflow.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class InfoController {
	public record InfoResponse(String appName, String version, String environment) {};
	
	@GetMapping("/info")
	public InfoResponse info() {
		return new InfoResponse("TeamFlow", "0.1.0", "development");
	}

}
