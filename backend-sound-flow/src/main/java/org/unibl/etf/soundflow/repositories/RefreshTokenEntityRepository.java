package org.unibl.etf.soundflow.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.unibl.etf.soundflow.models.entities.RefreshTokenEntity;

import java.util.List;

public interface RefreshTokenEntityRepository extends JpaRepository<RefreshTokenEntity, Integer> {
    List<RefreshTokenEntity> findAllByClient_IdAndRevokedFalse(Integer clientId);
    List<RefreshTokenEntity> findAllByTokenAndRevokedFalse(String token);
}
