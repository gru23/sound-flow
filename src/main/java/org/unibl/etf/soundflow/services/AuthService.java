package org.unibl.etf.soundflow.services;

import org.unibl.etf.soundflow.exceptions.UnauthorizedException;
import org.unibl.etf.soundflow.models.dto.LoginResponse;
import org.unibl.etf.soundflow.models.requests.LoginRequest;
import org.unibl.etf.soundflow.models.requests.LogoutRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void logout(LogoutRequest request) throws UnauthorizedException;
}
