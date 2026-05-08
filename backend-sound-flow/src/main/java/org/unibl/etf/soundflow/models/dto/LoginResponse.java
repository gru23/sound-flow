package org.unibl.etf.soundflow.models.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LoginResponse extends Client {
    private String accessToken;
    private String refreshToken;
}
