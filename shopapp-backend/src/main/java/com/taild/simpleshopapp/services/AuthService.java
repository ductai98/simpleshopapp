package com.taild.simpleshopapp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AuthService implements IAuthService{

    public String generateAuthUrl(String loginType) {
        String url = "";

        return url;
    }
    public Map<String, Object> authenticateAndFetchProfile(String code, String loginType) {
        return new HashMap<>();
    }
}
