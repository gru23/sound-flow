package org.unibl.etf.soundflow.services;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.unibl.etf.soundflow.models.dto.JwtClient;

public interface JwtClientDetailsService extends UserDetailsService {
    String generateJwt(JwtClient client);
    Claims parseToken(String accessToken);
}
