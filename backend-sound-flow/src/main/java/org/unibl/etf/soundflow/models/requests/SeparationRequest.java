package org.unibl.etf.soundflow.models.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import org.unibl.etf.soundflow.models.enums.SeparationOption;

@Data
public class SeparationRequest {
    @NotNull
    private Integer clientId;

    @NotNull
    private MultipartFile file;

    @NotNull
    private SeparationOption option;
}
