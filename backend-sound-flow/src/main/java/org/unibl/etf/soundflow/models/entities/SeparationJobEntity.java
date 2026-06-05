package org.unibl.etf.soundflow.models.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.unibl.etf.soundflow.models.enums.SeparationOption;
import org.unibl.etf.soundflow.models.enums.SeparationStatus;

import java.io.File;
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

    @Enumerated(EnumType.STRING)
    private SeparationOption option;

    @Enumerated(EnumType.STRING)
    private SeparationStatus status;

    private String sourcePath;
    private String separatedPath;
    private String title;

    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    private String errorMessage;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientEntity client;

    public SeparationJobEntity(SeparationOption option, String sourcePath, ClientEntity client) {
        this.option = option;
        status = SeparationStatus.QUEUED;
        this.sourcePath = sourcePath;
        this.title = new File(sourcePath).getName();
        createdAt = LocalDateTime.now();
        this.client = client;
    }
}

