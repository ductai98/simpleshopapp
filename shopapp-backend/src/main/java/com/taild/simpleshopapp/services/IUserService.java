package com.taild.simpleshopapp.services;

import com.taild.simpleshopapp.dtos.users.UpdateUserDTO;
import com.taild.simpleshopapp.dtos.users.UserDTO;
import com.taild.simpleshopapp.dtos.users.UserLoginDTO;
import com.taild.simpleshopapp.exceptions.DataNotFoundException;
import com.taild.simpleshopapp.exceptions.InvalidPasswordException;
import com.taild.simpleshopapp.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserService {
    User createUser(UserDTO userDTO) throws Exception;
    String login(UserLoginDTO userLoginDT) throws Exception;
    User getUserDetailsFromToken(String token) throws Exception;
    User getUserDetailsFromRefreshToken(String token) throws Exception;
    User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception;

    Page<User> findAll(String keyword, Pageable pageable) throws Exception;
    void resetPassword(Long userId, String newPassword)
            throws InvalidPasswordException, DataNotFoundException;
    void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException;
    void changeProfileImage(Long userId, String imageName) throws Exception;
    String loginSocial(UserLoginDTO userLoginDTO) throws Exception;
}
