package com.teamflow.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.teamflow.backend.service.InfoService;

@RestController
@RequestMapping("/api")
public class InfoController {
	
	private final InfoService infoService;
	
	public InfoController(InfoService infoService) {
		this.infoService = infoService;
	}
	
	public record InfoResponse(String appName, String version, String environment) {};
	
	@GetMapping("/info")
	public InfoResponse info() {
		return new InfoResponse(infoService.appName(), infoService.version(), infoService.environment());
	}
	

}
