package org.unibl.etf.soundflow.services.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.unibl.etf.soundflow.exceptions.NotFoundException;
import org.unibl.etf.soundflow.exceptions.UnauthorizedException;
import org.unibl.etf.soundflow.models.entities.ClientEntity;
import org.unibl.etf.soundflow.models.entities.RefreshTokenEntity;
import org.unibl.etf.soundflow.models.requests.auth.LogoutRequest;
import org.unibl.etf.soundflow.repositories.RefreshTokenEntityRepository;
import org.unibl.etf.soundflow.services.RefreshTokenService;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenEntityRepository refreshTokenEntityRepository;

    @Value("${authorization.refresh-token.expiration-time}")
    private String refreshTokenExpirationTime;

    public RefreshTokenServiceImpl(RefreshTokenEntityRepository refreshTokenEntityRepository) {
        this.refreshTokenEntityRepository = refreshTokenEntityRepository;
    }

    @Override
    public RefreshTokenEntity generate(ClientEntity client) {
        Instant expiry = Instant.now().plusMillis(Long.parseLong(refreshTokenExpirationTime));
        byte[] randomBytes = new byte[64]; // 512 bits
        new SecureRandom().nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        RefreshTokenEntity tokenObject =
                new RefreshTokenEntity(null, token, expiry, false, client);
        return refreshTokenEntityRepository.saveAndFlush(tokenObject);
    }

    @Override
    public void revoke(String token) throws NotFoundException {
        Optional<RefreshTokenEntity> optional = refreshTokenEntityRepository
                .findAllByTokenAndRevokedFalse(token)
                .stream()
                .findFirst();
        if(optional.isEmpty())
            throw new NotFoundException();
        optional.get().setRevoked(true);
        refreshTokenEntityRepository.saveAndFlush(optional.get());
    }

    /**
     * Checks for logged in client with {@code clientId} from request. For secure reasons it
     * checks client's refresh token if it is equal to request's token.
     * @param request contains client's id and refresh token
     * @return true - if client is logged in and has same refresh token, otherwise false
     */
    @Override
    public Boolean isLogoutRequestValid(LogoutRequest request)
            throws UnauthorizedException, NotFoundException {
        Optional<RefreshTokenEntity> optional = refreshTokenEntityRepository
                .findAllByClient_IdAndRevokedFalse(request.getClientId())
                .stream()
                .filter(token -> token.getToken().equals(request.getRefreshToken()))
                .findFirst();
        if(optional.isPresent() && isNotExpired(optional.get()))
            return true;
        throw new UnauthorizedException("Invalid refresh token");
    }

    @Override
    public RefreshTokenEntity getToken(String token) throws UnauthorizedException {
        Optional<RefreshTokenEntity> optional = refreshTokenEntityRepository
                .findAllByTokenAndRevokedFalse(token)
                .stream()
                .findFirst();
        if(optional.isPresent() && isNotExpired(optional.get()))
            return optional.get();
        throw new UnauthorizedException("Invalid refresh token");
    }

    private boolean isNotExpired(RefreshTokenEntity token) {
        return token.getExpiry().isAfter(Instant.now());
    }
}
