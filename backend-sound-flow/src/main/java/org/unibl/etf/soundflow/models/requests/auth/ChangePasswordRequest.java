package org.unibl.etf.soundflow.models.requests.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    // mozda napraviti ista ogranicenja za lozinku kao i kada se kreira korisnik
    @NotBlank
    private String newPassword;
    @NotBlank
    private String oldPassword;
}
