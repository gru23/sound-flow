package org.unibl.etf.soundflow.services;

import org.unibl.etf.soundflow.models.dto.LoginResponse;
import org.unibl.etf.soundflow.models.requests.LoginRequest;
import org.unibl.etf.soundflow.models.requests.LogoutRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    Boolean logout(LogoutRequest request);
}
