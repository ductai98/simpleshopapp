package com.taild.simpleshopapp.services;

import com.taild.simpleshopapp.models.Token;
import com.taild.simpleshopapp.models.User;
import org.springframework.stereotype.Service;

@Service

public interface ITokenService {
    Token addToken(User user, String token, boolean isMobileDevice);
    Token refreshToken(String refreshToken, User user) throws Exception;
}
