package com.taild.simpleshopapp.repositories;

import com.taild.simpleshopapp.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByEmail(String email);

    @Query("SELECT o FROM User o WHERE o.active = true AND (:keyword IS NULL OR :keyword = '' OR " +
            " o.fullName LIKE %:keyword% " +
            " OR o.address LIKE %:keyword% " +
            " OR o.phoneNumber LIKE %:keyword%) " +
            " AND LOWER(o.role.name) = 'user'")
    Page<User> findAll(@Param("keyword") String keyword, Pageable pageable);
    List<User> findByRoleId(Long roleId);

    Optional<User> findByFacebookAccountId(String facebookAccountId);
    Optional<User> findByGoogleAccountId(String googleAccountId);
}

