package com.teamflow.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.teamflow.backend.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	@Query("select rt from RefreshToken rt join fetch rt.user where rt.tokenHash = :tokenHash")
	Optional<RefreshToken> findByTokenHash(String tokenHash);
	
	List<RefreshToken> findByUserIdAndRevokedAtIsNull(Long userId);
}
