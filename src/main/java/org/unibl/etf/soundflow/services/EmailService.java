package org.unibl.etf.soundflow.services;

import org.unibl.etf.soundflow.models.entities.ClientEntity;

public interface EmailService {
    void sendVerificationEmail(ClientEntity recipient);
}
