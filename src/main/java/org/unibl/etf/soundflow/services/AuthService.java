package org.unibl.etf.soundflow.services;

import org.unibl.etf.soundflow.models.dto.LoginResponse;
import org.unibl.etf.soundflow.models.requests.LoginRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
