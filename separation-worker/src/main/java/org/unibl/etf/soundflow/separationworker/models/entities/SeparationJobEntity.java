package org.unibl.etf.soundflow.separationworker.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.unibl.etf.soundflow.separationworker.models.enums.SeparationOption;
import org.unibl.etf.soundflow.separationworker.models.enums.SeparationStatus;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "separation_jobs")
public class SeparationJobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Integer clientId;

    @Enumerated(EnumType.STRING)
    private SeparationOption option;

    @Enumerated(EnumType.STRING)
    private SeparationStatus status;

    private String sourcePath;
    private String separatedPath;

    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    private String errorMessage;
}


