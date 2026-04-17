package org.unibl.etf.soundflow.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.unibl.etf.soundflow.models.entities.ClientEntity;

import java.util.Optional;

public interface ClientEntityRepository extends JpaRepository<ClientEntity, Integer> {
    Optional<ClientEntity> findByUsername(String username);
    Optional<ClientEntity> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
