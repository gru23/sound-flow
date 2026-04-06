package org.unibl.etf.soundflow.services;

import org.unibl.etf.soundflow.exceptions.NotFoundException;
import org.unibl.etf.soundflow.models.dto.Client;
import org.unibl.etf.soundflow.models.dto.LoginResponse;
import org.unibl.etf.soundflow.models.requests.ClientRequest;

public interface ClientService {
    LoginResponse findById(Integer id) throws NotFoundException;
    Client findByUsername(String username) throws NotFoundException;
    Client registration(ClientRequest clientRequest);
}
