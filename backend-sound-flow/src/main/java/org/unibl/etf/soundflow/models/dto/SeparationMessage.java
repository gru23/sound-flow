package org.unibl.etf.soundflow.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.unibl.etf.soundflow.models.enums.SeparationOption;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeparationMessage implements Serializable {
    private String jobId;
    private String sourcePath;
    private SeparationOption option;
}
