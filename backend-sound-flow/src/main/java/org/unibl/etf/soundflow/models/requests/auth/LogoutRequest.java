package org.unibl.etf.soundflow.models.requests.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LogoutRequest {
    @NotNull
    Integer clientId;
    @NotBlank
    String refreshToken;
}
