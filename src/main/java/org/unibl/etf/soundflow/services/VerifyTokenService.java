package org.unibl.etf.soundflow.services;

import org.unibl.etf.soundflow.models.entities.ClientEntity;

public interface VerifyTokenService {
    boolean isVerificationValid(String token);
    ClientEntity isResetPasswordTokenValid(String token);
}
