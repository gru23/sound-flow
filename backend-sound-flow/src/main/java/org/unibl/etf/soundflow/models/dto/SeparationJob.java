package org.unibl.etf.soundflow.models.dto;

import lombok.Data;
import org.unibl.etf.soundflow.models.enums.SeparationOption;
import org.unibl.etf.soundflow.models.enums.SeparationStatus;

import java.time.LocalDateTime;

@Data
public class SeparationJob {
    // sta ovdje slati... Mozda mi treba ime pjesme kao polje
    private String id;

    private SeparationOption option;

    private SeparationStatus status;

    private String sourcePath;
    private String separatedPath;

    private LocalDateTime finishedAt;
}
