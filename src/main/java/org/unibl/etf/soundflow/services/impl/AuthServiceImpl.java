package org.unibl.etf.soundflow.services.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.unibl.etf.soundflow.exceptions.UnauthorizedException;
import org.unibl.etf.soundflow.models.dto.JwtClient;
import org.unibl.etf.soundflow.models.dto.LoginResponse;
import org.unibl.etf.soundflow.models.requests.LoginRequest;
import org.unibl.etf.soundflow.services.AuthService;
import org.unibl.etf.soundflow.services.ClientService;

import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final ClientService clientService;

    @Value("${authorization.token.expiration-time}")
    private String tokenExpirationTime;
    @Value("${authorization.token.secret}")
    private String tokenSecret;

    public AuthServiceImpl(AuthenticationManager authenticationManager, ClientService clientService) {
        this.authenticationManager = authenticationManager;
        this.clientService = clientService;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        LoginResponse response = null;
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            JwtClient client = (JwtClient) authenticate.getPrincipal();
            response = clientService.findById(client.getId());
            response.setToken(generateJwt(client));
        } catch(Exception ex) { // vidi kako ovo obraditi. problem je pravio kratak token secret
            System.err.println(ex.getMessage());
            throw new UnauthorizedException();
        }
        return response;
    }

    private String generateJwt(JwtClient client) {
        return Jwts.builder()
                .setId(client.getId().toString())
                .setSubject(client.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(tokenExpirationTime)))
                .signWith(SignatureAlgorithm.HS512, tokenSecret)
                .compact();
    }
}
