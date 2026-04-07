package org.unibl.etf.soundflow.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.unibl.etf.soundflow.models.entities.RefreshTokenEntity;

import java.util.Optional;

public interface RefreshTokenEntityRepository extends JpaRepository<RefreshTokenEntity, Integer> {
    Optional<RefreshTokenEntity> findByClient_IdAndRevokedFalse(Integer clientId);
    Optional<RefreshTokenEntity> findByTokenAndRevokedFalse(String token);
}
