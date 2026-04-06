package org.unibl.etf.soundflow.services;

import org.unibl.etf.soundflow.exceptions.NotFoundException;
import org.unibl.etf.soundflow.exceptions.UnauthorizedException;
import org.unibl.etf.soundflow.models.entities.RefreshTokenEntity;
import org.unibl.etf.soundflow.models.requests.LogoutRequest;

public interface RefreshTokenService {
    RefreshTokenEntity generate(Integer clientId);
    void revoke(Integer clientId) throws NotFoundException;
    Boolean isLogoutRequestValid(LogoutRequest request) throws UnauthorizedException, NotFoundException;
}
