package com.hotel.hotel.repository;

import com.hotel.hotel.model.User;
import com.hotel.hotel.response.ResponseData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String username);
    boolean existsByEmail(String email);
    User findByActivationCode(String code);

    @Query("SELECT user.email FROM User user WHERE user.passwordResetCode = :code")
    String getEmailByPasswordResetCode(String code);
}
