package org.unibl.etf.soundflow.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.unibl.etf.soundflow.models.entities.VerifyTokenEntity;

import java.util.Optional;

public interface VerifyTokenEntityRepository extends JpaRepository<VerifyTokenEntity, Integer> {
    Optional<VerifyTokenEntity> findByToken(String token);
}
