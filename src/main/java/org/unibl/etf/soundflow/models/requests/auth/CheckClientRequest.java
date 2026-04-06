package org.unibl.etf.soundflow.models.requests.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckClientRequest {
    @NotBlank
    private String accessToken;
//    @NotNull
//    private AuthProvider authProvider;
}
