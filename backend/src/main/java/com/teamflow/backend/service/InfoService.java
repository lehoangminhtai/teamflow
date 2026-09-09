package com.teamflow.backend.service;

import org.springframework.stereotype.Service;

@Service
public class InfoService {
	public String appName() {
		return "TeamFlow API";
	}
	
	public String version() {
		return "0.1.0";
	}
	
	public String environment() {
		return "development";
	}
}
