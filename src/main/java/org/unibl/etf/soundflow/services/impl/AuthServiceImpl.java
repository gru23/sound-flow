package org.unibl.etf.soundflow.services.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.unibl.etf.soundflow.exceptions.NotFoundException;
import org.unibl.etf.soundflow.exceptions.UnauthorizedException;
import org.unibl.etf.soundflow.models.dto.Client;
import org.unibl.etf.soundflow.models.dto.JwtClient;
import org.unibl.etf.soundflow.models.dto.LoginResponse;
import org.unibl.etf.soundflow.models.entities.ClientEntity;
import org.unibl.etf.soundflow.models.entities.RefreshTokenEntity;
import org.unibl.etf.soundflow.models.requests.LoginRequest;
import org.unibl.etf.soundflow.models.requests.LogoutRequest;
import org.unibl.etf.soundflow.models.requests.auth.CheckClientRequest;
import org.unibl.etf.soundflow.services.AuthService;
import org.unibl.etf.soundflow.services.ClientService;
import org.unibl.etf.soundflow.services.RefreshTokenService;

import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final ClientService clientService;
    private final RefreshTokenService refreshTokenService;
    private final ModelMapper modelMapper;

    @Value("${authorization.token.expiration-time}")
    private String tokenExpirationTime;
    @Value("${authorization.token.secret}")
    private String tokenSecret;

    public AuthServiceImpl(AuthenticationManager authenticationManager, ClientService clientService, RefreshTokenService refreshTokenService, ModelMapper modelMapper) {
        this.authenticationManager = authenticationManager;
        this.clientService = clientService;
        this.refreshTokenService = refreshTokenService;
        this.modelMapper = modelMapper;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        LoginResponse response;
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            JwtClient client = (JwtClient) authenticate.getPrincipal();
            response = clientService.findById(client.getId());
            response.setAccessToken(generateJwt(client));
            RefreshTokenEntity refreshToken = refreshTokenService.generate(
                    modelMapper.map(response, ClientEntity.class)
            );
            response.setRefreshToken(refreshToken.getToken());
        } catch(Exception ex) {
            ex.printStackTrace();
            throw new UnauthorizedException(ex.getMessage());
        }
        return response;
    }

    @Override
    public void logout(LogoutRequest request) {
        if(!refreshTokenService.isLogoutRequestValid(request))
            throw new UnauthorizedException("Invalid refresh token");
        refreshTokenService.revoke(request.getClientId());
    }

    @Override
    public Client checkClient(String accessToken)
    // public LoginResponse checkClient(CheckClientRequest request)
            throws UnauthorizedException, NotFoundException {
        String username = parseToken(accessToken).getSubject();
        return clientService.findByUsername(username);
    }

    private String generateJwt(JwtClient client) {
        return Jwts.builder()
                .setId(client.getId().toString())
                .setSubject(client.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(tokenExpirationTime)))
                .signWith(SignatureAlgorithm.HS512, tokenSecret)
                .compact();
    }

    private Claims parseToken(String accessToken) {
        return Jwts.parser()
                .setSigningKey(tokenSecret)
                .parseClaimsJws(accessToken)
                .getBody();
    }
}
