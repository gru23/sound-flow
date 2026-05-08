package org.unibl.etf.soundflow.models.requests.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ConfirmResetPasswordRequest {
    @NotBlank
    private String token;

    @NotBlank
    @Size(min = 2, max = 64)
    private String newPassword;
}
