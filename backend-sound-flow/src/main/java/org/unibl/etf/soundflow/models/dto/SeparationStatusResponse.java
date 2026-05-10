package org.unibl.etf.soundflow.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.unibl.etf.soundflow.models.enums.SeparationStatus;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SeparationStatusResponse {
    private String jobId;
    private SeparationStatus status;
    private String resultUrl;
}

