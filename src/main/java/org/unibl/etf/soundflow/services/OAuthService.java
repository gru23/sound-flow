package org.unibl.etf.soundflow.services;

import org.unibl.etf.soundflow.models.dto.LoginResponse;

public interface OAuthService {
    LoginResponse login(String idToken);
}
