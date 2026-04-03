package org.unibl.etf.soundflow.models.dto;

import lombok.Data;
import org.unibl.etf.soundflow.models.enums.AuthProvider;

@Data
public class Client {
    private Integer id;

    private String name;

    private String surname;

    private String username;

    private String email;

    private AuthProvider authProvider;
}
