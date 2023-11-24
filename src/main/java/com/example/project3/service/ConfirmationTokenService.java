package com.example.project3.service;

import com.example.project3.entity.ConfirmationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ConfirmationTokenService {
    void saveConfirmationToken(ConfirmationToken confirmationToken);
    public Optional<ConfirmationToken> getToken(String token);

    public int setConfirmedAt(String token);
}
