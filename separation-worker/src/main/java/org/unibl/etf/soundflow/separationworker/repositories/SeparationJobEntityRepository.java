package org.unibl.etf.soundflow.separationworker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.unibl.etf.soundflow.separationworker.models.entities.SeparationJobEntity;

public interface SeparationJobEntityRepository extends JpaRepository<SeparationJobEntity, String> {
}
