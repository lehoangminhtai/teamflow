package com.teamflow.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.teamflow.backend.entity.User;
import com.teamflow.backend.exception.NotFoundException;
import com.teamflow.backend.repository.UserRepository;

@RestController
@RequestMapping("/api/dev/users")
public class DevUserController {
	private final UserRepository userRepository ;

	public DevUserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public record DevUser(Long id, String email, String fullName) {}
	
	@PostMapping
	public DevUser create(@RequestParam String email, @RequestParam String fullName) {
		User user = new User(email.toLowerCase(),"not hash", fullName);
		User saved = userRepository.save(user);
		return new DevUser(saved.getId(), saved.getEmail(), saved.getFullName());
	}
	
	@GetMapping
	public List<DevUser> list(){
		return userRepository.findAll()
				.stream()
				.map(u -> new DevUser(u.getId(), u.getEmail(), u.getFullName()))
				.toList();
	}
	
	@GetMapping("/{id}")
	public DevUser findOne(@PathVariable Long id) {
		User user =  userRepository.findById(id).orElseThrow(()-> new NotFoundException("not found user:"+ id));
		
		return new DevUser(user.getId(), user.getEmail(),user.getFullName());
	}
	
	@GetMapping("/search")
	public List<DevUser> search(@RequestParam String q){
		return userRepository.findByFullNameContainingIgnoreCase(q)
				.stream()
				.map(u -> new DevUser(u.getId(), u.getEmail(), u.getFullName()))
				.toList();
	}
	
}
