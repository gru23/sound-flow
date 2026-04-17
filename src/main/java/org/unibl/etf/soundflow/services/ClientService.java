package org.unibl.etf.soundflow.services;

import org.unibl.etf.soundflow.exceptions.NotFoundException;
import org.unibl.etf.soundflow.models.dto.Client;
import org.unibl.etf.soundflow.models.entities.ClientEntity;
import org.unibl.etf.soundflow.models.requests.ClientRequest;

public interface ClientService {
    ClientEntity findById(Integer id) throws NotFoundException;
    Client findByUsername(String username) throws NotFoundException;
    ClientEntity findByEmail(String email) throws NotFoundException;
    Client registration(ClientRequest clientRequest);
    void setIsVerified(int id);
    Client setNewPassword(ClientEntity client, String newPassword);
}
