package org.unibl.etf.soundflow.services;

import org.unibl.etf.soundflow.exceptions.NotFoundException;
import org.unibl.etf.soundflow.exceptions.UnauthorizedException;
import org.unibl.etf.soundflow.models.dto.Client;
import org.unibl.etf.soundflow.models.dto.LoginResponse;
import org.unibl.etf.soundflow.models.requests.LoginRequest;
import org.unibl.etf.soundflow.models.requests.LogoutRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void logout(LogoutRequest request) throws UnauthorizedException;
    Client checkClient(String accessToken) throws UnauthorizedException, NotFoundException;
    // ovaj mozda ostaviti ako cu slati zahtjev u kojem cu morati naglasiti o kojoj autentikaciji je rijec
//    LoginResponse checkClient(CheckClientRequest request) throws UnauthorizedException, NotFoundException;
}
