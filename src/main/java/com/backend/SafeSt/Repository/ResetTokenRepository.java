package com.backend.SafeSt.Repository;

import com.backend.SafeSt.Entity.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetTokenRepository extends JpaRepository<ResetToken,Long> {

    Optional< ResetToken> findByResetToken(String token);
}
