package org.unibl.etf.soundflow.models.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String surname;

    private String username;

    private String password;

    @NotBlank
    @Email
    private String email;

//    @NotNull
//    private AuthProvider authProvider;
}
