package org.unibl.etf.soundflow.services.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unibl.etf.soundflow.exceptions.HttpException;
import org.unibl.etf.soundflow.exceptions.InvalidCredentialsException;
import org.unibl.etf.soundflow.exceptions.NotFoundException;
import org.unibl.etf.soundflow.models.dto.Client;
import org.unibl.etf.soundflow.models.dto.JwtClient;
import org.unibl.etf.soundflow.models.dto.LoginResponse;
import org.unibl.etf.soundflow.models.entities.ClientEntity;
import org.unibl.etf.soundflow.models.entities.RefreshTokenEntity;
import org.unibl.etf.soundflow.models.enums.AuthProvider;
import org.unibl.etf.soundflow.models.requests.ClientRequest;
import org.unibl.etf.soundflow.services.ClientService;
import org.unibl.etf.soundflow.services.JwtClientDetailsService;
import org.unibl.etf.soundflow.services.OAuthService;
import org.unibl.etf.soundflow.services.RefreshTokenService;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.UUID;

@Service
@Transactional
public class OAuthServiceImpl implements OAuthService {
    private final ClientService clientService;
    private final RefreshTokenService refreshTokenService;
    private final JwtClientDetailsService jwtClientDetailsService;
    private final ModelMapper modelMapper;

    @Value("${google.client.id}")
    private String GOOGLE_CLIENT_ID;


    public OAuthServiceImpl(ClientService clientService, JwtClientDetailsService jwtClientDetailsService, ModelMapper modelMapper, RefreshTokenService refreshTokenService) {
        this.clientService = clientService;
        this.jwtClientDetailsService = jwtClientDetailsService;
        this.modelMapper = modelMapper;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public LoginResponse login(String idTokenString) {
        try {
            GoogleIdToken idToken = getGoogleIdToken(idTokenString);

            if(isIdTokenInvalid(idToken))
                throw new InvalidCredentialsException("Google ID token is invalid");
            GoogleIdToken.Payload payload = idToken.getPayload();
            LoginResponse response = null;

            try {
                ClientEntity entity = clientService.findByEmail(payload.getEmail());
                response = addTokensToClient(entity);
            }
            catch(NotFoundException e) {
                Client registredClient = registration(payload);
                response = addTokensToClient(modelMapper.map(registredClient, ClientEntity.class));
            }
            return response;
        }
        catch(GeneralSecurityException e) {
            throw new InvalidCredentialsException("Google ID token is invalid");
        } catch(IOException e) {
            throw new HttpException("Network error");
        }
    }

    private GoogleIdToken getGoogleIdToken(String idTokenString) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance()
        )
                .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
                .build();
        return verifier.verify(idTokenString);
    }

    private boolean isIdTokenInvalid(GoogleIdToken token) {
        if(token == null)
            return true;
        GoogleIdToken.Payload payload = token.getPayload();
        if (!payload.getIssuer().equals("accounts.google.com") &&
                !payload.getIssuer().equals("https://accounts.google.com"))
            return true;
        return payload.getExpirationTimeSeconds() * 1000 < System.currentTimeMillis();
    }

    private Client registration(GoogleIdToken.Payload payload) {
        String email = payload.getEmail();
        String username = email.split("@")[0];
        String fullName = (String) payload.get("name");
        String firstName = (String) payload.get("given_name");
        String lastName = (String) payload.get("family_name");
        String password = UUID.randomUUID().toString();
        ClientRequest request = new ClientRequest(
                firstName, lastName, username, password, email
        );
        return clientService.registration(request, AuthProvider.GOOGLE);
    }

    private LoginResponse addTokensToClient(ClientEntity entity) {
        LoginResponse response = modelMapper.map(entity, LoginResponse.class);
        JwtClient jwtClient = new JwtClient(entity.getId(), entity.getUsername(), null);
        response.setAccessToken(jwtClientDetailsService.generateJwt(jwtClient));
        RefreshTokenEntity refreshToken = refreshTokenService.generate(entity);
        response.setRefreshToken(refreshToken.getToken());
        return response;
    }
}
