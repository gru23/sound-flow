package org.unibl.etf.soundflow.services.impl;

import org.modelmapper.ModelMapper;
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
import org.unibl.etf.soundflow.models.requests.auth.*;
import org.unibl.etf.soundflow.services.*;

@Service
public class AuthServiceImpl implements AuthService {
    private final ClientService clientService;
    private final RefreshTokenService refreshTokenService;
    private final VerifyTokenService verifyTokenService;
    private final EmailService emailService;
    private final JwtClientDetailsService jwtClientDetailsService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           ClientService clientService, RefreshTokenService refreshTokenService,
                           VerifyTokenService verifyTokenService, EmailService emailService,
                           JwtClientDetailsService jwtClientDetailsService, ModelMapper modelMapper) {
        this.authenticationManager = authenticationManager;
        this.clientService = clientService;
        this.refreshTokenService = refreshTokenService;
        this.verifyTokenService = verifyTokenService;
        this.emailService = emailService;
        this.jwtClientDetailsService = jwtClientDetailsService;
        this.modelMapper = modelMapper;
    }

    @Override
    public void verify(String token) {
        if(!verifyTokenService.isVerificationValid(token))
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
            response.setAccessToken(jwtClientDetailsService.generateJwt(client));
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
        refreshTokenService.revoke(request.getRefreshToken());
    }

    @Override
    public Client checkClient(String accessToken)
            throws UnauthorizedException, NotFoundException {
        String username = jwtClientDetailsService.parseToken(accessToken).getSubject();
        return modelMapper.map(clientService.findByUsername(username), Client.class);
    }

    /**
     * Refresh access token if refresh token is valid.
     * @return New Access token generated based on Refresh token from {@code request}.
     * @throws UnauthorizedException If Refresh token is not valid.
     */
    @Override
    public String refreshToken(RefreshRequest request) throws UnauthorizedException {
        RefreshTokenEntity token = refreshTokenService.getToken(request.getRefreshToken());
        return jwtClientDetailsService.generateJwt(new JwtClient(
                            token.getClient().getId(), token.getClient().getUsername(), null)
        );
    }

    @Override
    public void requestResetingPassword(ResetPasswordRequest request) {
        ClientEntity client;
        if(request.getIdentifier().contains("@"))
            client = clientService.findByEmail(request.getIdentifier());
        else
            client = clientService.findByUsername(request.getIdentifier());
        emailService.sendResetPasswordEmail(client);
    }

    @Override
    public void resetPassword(ConfirmResetPasswordRequest request) {
        ClientEntity client = verifyTokenService.isResetPasswordTokenValid(request.getToken());
        clientService.setNewPassword(client, request.getNewPassword());
    }
}
