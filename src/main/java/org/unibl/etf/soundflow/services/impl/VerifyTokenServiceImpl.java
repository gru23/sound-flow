package org.unibl.etf.soundflow.services.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.unibl.etf.soundflow.exceptions.NotFoundException;
import org.unibl.etf.soundflow.exceptions.UnauthorizedException;
import org.unibl.etf.soundflow.models.entities.VerifyTokenEntity;
import org.unibl.etf.soundflow.repositories.VerifyTokenEntityRepository;
import org.unibl.etf.soundflow.services.ClientService;
import org.unibl.etf.soundflow.services.VerifyTokenService;

import java.time.Instant;

@Service
@Transactional
public class VerifyTokenServiceImpl implements VerifyTokenService {
    private final VerifyTokenEntityRepository verifyTokenEntityRepository;
    private final ClientService clientService;

    public VerifyTokenServiceImpl(VerifyTokenEntityRepository verifyTokenEntityRepository, ClientService clientService) {
        this.verifyTokenEntityRepository = verifyTokenEntityRepository;
        this.clientService = clientService;
    }

    @Override
    public boolean isValid(String token) {
        VerifyTokenEntity entity = verifyTokenEntityRepository
                .findByToken(token)
                .orElseThrow(NotFoundException::new);
        if(entity.getExpiry().isBefore(Instant.now()))
            throw new UnauthorizedException("Token expired");
        clientService.setIsVerified(entity.getClient().getId());
        verifyTokenEntityRepository.deleteById(entity.getId());
        return true;
    }
}
