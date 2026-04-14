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
import org.unibl.etf.soundflow.models.requests.auth.LoginRequest;
import org.unibl.etf.soundflow.models.requests.auth.LogoutRequest;
import org.unibl.etf.soundflow.models.requests.auth.RefreshRequest;
import org.unibl.etf.soundflow.services.AuthService;
import org.unibl.etf.soundflow.services.ClientService;
import org.unibl.etf.soundflow.services.RefreshTokenService;
import org.unibl.etf.soundflow.services.VerifyTokenService;

import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {
    private final ClientService clientService;
    private final RefreshTokenService refreshTokenService;
    private final VerifyTokenService verifyTokenService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;

    @Value("${authorization.token.expiration-time}")
    private String tokenExpirationTime;
    @Value("${authorization.token.secret}")
    private String tokenSecret;

    public AuthServiceImpl(AuthenticationManager authenticationManager, ClientService clientService, RefreshTokenService refreshTokenService, VerifyTokenService verifyTokenService, ModelMapper modelMapper) {
        this.authenticationManager = authenticationManager;
        this.clientService = clientService;
        this.refreshTokenService = refreshTokenService;
        this.verifyTokenService = verifyTokenService;
        this.modelMapper = modelMapper;
    }

    @Override
    public void verify(String token) {
        if(!verifyTokenService.isValid(token))
            throw new UnauthorizedException("Invalid token");
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        LoginResponse response;
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            JwtClient client = (JwtClient) authenticate.getPrincipal();
            ClientEntity clientEntity = modelMapper.map(
                    clientService.findById(client.getId()),
                    ClientEntity.class
            );
            if(!clientEntity.getIsVerified())
                throw new UnauthorizedException("Account is not verified!");
            response = modelMapper.map(clientEntity, LoginResponse.class);
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

    /**
     * Refresh access token if refresh token is valid.
     * @return New Access token generated based on Refresh token from {@code request}.
     * @throws UnauthorizedException If Refresh token is not valid.
     */
    @Override
    public String refreshToken(RefreshRequest request) throws UnauthorizedException {
        RefreshTokenEntity token = refreshTokenService.getToken(request.getRefreshToken());
        return generateJwt(new JwtClient(
                            token.getClient().getId(), token.getClient().getUsername(), null)
        );
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
