package com.teamflow.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamflow.backend.dto.user.ChangePasswordRequest;
import com.teamflow.backend.dto.user.UpdateProfileRequest;
import com.teamflow.backend.dto.user.UserResponse;
import com.teamflow.backend.dto.user.UserSummary;
import com.teamflow.backend.entity.User;
import com.teamflow.backend.exception.NotFoundException;
import com.teamflow.backend.exception.UnauthorizedException;
import com.teamflow.backend.exception.ValidationException;
import com.teamflow.backend.mapper.UserMapper;
import com.teamflow.backend.repository.UserRepository;
import com.teamflow.backend.security.RefreshTokenService;

@Service
public class UserService {
	private static final Logger log = LoggerFactory.getLogger(UserService.class);
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserMapper userMapper;
	private final RefreshTokenService refreshTokenService;
	
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper,
			RefreshTokenService refreshTokenService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.userMapper = userMapper;
		this.refreshTokenService = refreshTokenService;
	}
	
	@Transactional(readOnly = true)
	public UserResponse getById(Long userId) {
		return userMapper.toResponse(requireUser(userId));
	}
	
	@Transactional(readOnly = true)
	public UserSummary getSummary(Long userId) {
		return userMapper.toSummary(requireUser(userId));
	}
	
	@Transactional
	public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
		User user = requireUser(userId);
		
		if(request.fullName() != null) {
			user.setFullName(request.fullName().trim());
		}
		
		if(request.avatarUrl() != null) {
			user.setAvatarUrl(request.avatarUrl().isBlank() ? null : request.avatarUrl());
		}
		
		return userMapper.toResponse(userRepository.save(user));
	}
	
	@Transactional
	public void changePassword(Long userId, ChangePasswordRequest request) {
		User user = requireUser(userId);
		if(!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
			throw new UnauthorizedException("INVALID_CREDENTIALS", "Current password not correct");
		}
		
		if(passwordEncoder.matches(request.newPassword(), user.getPasswordHash())) {
			throw new ValidationException("New password must be different");
		}
		user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
		userRepository.save(user);
		
		refreshTokenService.revokeAllForUser(userId);
		
		log.info("Password changed: userId = {}", userId);
	}
	
	private User requireUser(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("Not found user"));
	}
}
