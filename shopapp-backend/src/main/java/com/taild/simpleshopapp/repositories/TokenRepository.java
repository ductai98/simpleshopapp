package com.taild.simpleshopapp.repositories;


import com.taild.simpleshopapp.models.Token;
import com.taild.simpleshopapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findByUser(User user);
    Token findByToken(String token);
    Token findByRefreshToken(String token);
}

