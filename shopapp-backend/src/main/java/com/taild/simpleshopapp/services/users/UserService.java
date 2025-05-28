package com.taild.simpleshopapp.services.users;

import com.taild.simpleshopapp.dtos.users.UpdateUserDTO;
import com.taild.simpleshopapp.dtos.users.UserDTO;
import com.taild.simpleshopapp.dtos.users.UserLoginDTO;
import com.taild.simpleshopapp.exceptions.DataNotFoundException;
import com.taild.simpleshopapp.exceptions.ExpiredTokenException;
import com.taild.simpleshopapp.exceptions.InvalidPasswordException;
import com.taild.simpleshopapp.exceptions.PermissionDenyException;
import com.taild.simpleshopapp.models.Role;
import com.taild.simpleshopapp.models.Token;
import com.taild.simpleshopapp.models.User;
import com.taild.simpleshopapp.repositories.RoleRepository;
import com.taild.simpleshopapp.repositories.TokenRepository;
import com.taild.simpleshopapp.repositories.UserRepository;
import com.taild.simpleshopapp.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.taild.simpleshopapp.utils.ValidationUtils.isValidEmail;


@RequiredArgsConstructor
@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public User createUser(UserDTO userDTO) throws Exception {
        //register user
        if (!userDTO.getPhoneNumber().isBlank() && userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }
        if (!userDTO.getEmail().isBlank() && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DataIntegrityViolationException("Email already exists");
        }
        Role role = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role not found"));


        if (role.getName().equalsIgnoreCase(Role.ADMIN)) {
            throw new PermissionDenyException("Registering admin accounts is not allowed");
        }
        //convert from userDTO => user
        User newUser = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .address(userDTO.getAddress())
                .dateOfBirth(userDTO.getDateOfBirth())
                .facebookAccountId(userDTO.getFacebookAccountId())
                .googleAccountId(userDTO.getGoogleAccountId())
                .active(true)
                .build();

        newUser.setRole(role);

        String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
        newUser.setPassword(encodedPassword);

        /*if (userDTO.getFacebookAccountId().isEmpty() && userDTO.getGoogleAccountId().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(userDTO.getPassword());
            newUser.setPassword(encodedPassword);
        }*/

        return userRepository.save(newUser);
    }


    @Override
    public String login(UserLoginDTO userLoginDTO) throws Exception {
        Optional<User> optionalUser = Optional.empty();

        // Kiểm tra người dùng qua số điện thoại
        if (userLoginDTO.getPhoneNumber() != null && !userLoginDTO.getPhoneNumber().isBlank()) {
            optionalUser = userRepository.findByPhoneNumber(userLoginDTO.getPhoneNumber());
        }

        // Nếu không tìm thấy người dùng bằng số điện thoại, thử tìm qua email
        if (optionalUser.isEmpty() && userLoginDTO.getEmail() != null) {
            optionalUser = userRepository.findByEmail(userLoginDTO.getEmail());
        }

        // Nếu không tìm thấy người dùng, ném ngoại lệ
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException("Không tìm thấy người dùng");
        }

        User existingUser = optionalUser.get();

        // Kiểm tra tài khoản có bị khóa không
        if (!existingUser.isActive()) {
            throw new DataNotFoundException("Tài khoản đã bị khóa");
        }

        if (!passwordEncoder.matches(userLoginDTO.getPassword(), existingUser.getPassword())) {
            throw new BadCredentialsException("Phone number or email is incorrect");
        }

        /*if (userLoginDTO.getFacebookAccountId().isEmpty() && userLoginDTO.getGoogleAccountId().isEmpty()) {
            if (!passwordEncoder.matches(userLoginDTO.getPassword(), existingUser.getPassword())) {
                throw new BadCredentialsException("Phone number or email is incorrect");
            }
        }*/

        String phoneNumber = existingUser.getPhoneNumber();
        String password = userLoginDTO.getPassword();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(phoneNumber, password);
        authenticationManager.authenticate(authenticationToken);

        // Tạo JWT token cho người dùng
        return jwtTokenUtil.generateToken(existingUser);
    }


    @Override
    public String loginSocial(UserLoginDTO userLoginDTO) throws Exception {
        return null;
    }

    @Transactional
    @Override
    public User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        if (updatedUserDTO.getFullName() != null) {
            existingUser.setFullName(updatedUserDTO.getFullName());
        }
        if (updatedUserDTO.getAddress() != null) {
            existingUser.setAddress(updatedUserDTO.getAddress());
        }
        if (updatedUserDTO.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(updatedUserDTO.getDateOfBirth());
        }

        if (updatedUserDTO.getPassword() != null
                && !updatedUserDTO.getPassword().isEmpty()) {
            if(!updatedUserDTO.getPassword().equals(updatedUserDTO.getRetypePassword())) {
                throw new DataNotFoundException("Password and retype password not the same");
            }
            String newPassword = updatedUserDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(newPassword);
            existingUser.setPassword(encodedPassword);
        }

        return userRepository.save(existingUser);
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if(jwtTokenUtil.isTokenExpired(token)) {
            throw new ExpiredTokenException("Token is expired");
        }
        String subject = jwtTokenUtil.getSubject(token);
        Optional<User> user;
        user = userRepository.findByPhoneNumber(subject);
        if (user.isEmpty() && isValidEmail(subject)) {
            user = userRepository.findByEmail(subject);
        }
        return user.orElseThrow(() -> new Exception("User not found"));
    }
    @Override
    public User getUserDetailsFromRefreshToken(String refreshToken) throws Exception {
        Token existingToken = tokenRepository.findByRefreshToken(refreshToken);
        return getUserDetailsFromToken(existingToken.getToken());
    }

    @Override
    public Page<User> findAll(String keyword, Pageable pageable) {
        return userRepository.findAll(keyword, pageable);
    }

    @Override
    @Transactional
    public void resetPassword(Long userId, String newPassword)
            throws InvalidPasswordException, DataNotFoundException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        String encodedPassword = passwordEncoder.encode(newPassword);
        existingUser.setPassword(encodedPassword);
        userRepository.save(existingUser);
        //reset password => clear token
        List<Token> tokens = tokenRepository.findByUser(existingUser);
        tokenRepository.deleteAll(tokens);
    }

    @Override
    @Transactional
    public void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        existingUser.setActive(active);
        userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void changeProfileImage(Long userId, String imageName) throws Exception {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));
        existingUser.setProfileImage(imageName);
        userRepository.save(existingUser);
    }
}








