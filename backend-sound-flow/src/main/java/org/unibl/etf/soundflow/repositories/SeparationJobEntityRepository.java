package org.unibl.etf.soundflow.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.unibl.etf.soundflow.models.entities.SeparationJobEntity;

public interface SeparationJobEntityRepository extends JpaRepository<SeparationJobEntity, Integer> {
}
