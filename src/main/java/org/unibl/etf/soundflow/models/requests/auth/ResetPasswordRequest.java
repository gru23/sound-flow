package org.unibl.etf.soundflow.models.requests.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank
    private String identifier;
}
