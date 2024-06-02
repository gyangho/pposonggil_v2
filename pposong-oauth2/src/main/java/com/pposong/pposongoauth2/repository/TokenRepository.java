package com.pposong.pposongoauth2.repository;

import com.pposong.pposongoauth2.member.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findById(Long Id);
}
