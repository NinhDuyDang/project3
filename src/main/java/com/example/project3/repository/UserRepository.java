package com.example.project3.repository;

import com.example.project3.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository <User, Integer>{
    User getUsersByUsername(String username);

    Optional<User> findByEmail(String email);
    @Transactional
    @Modifying
    @Query("UPDATE User u " +
            "SET u.enabled = TRUE WHERE u.email = ?1")
    int enableUser(String email);
    @Transactional
    @Modifying
    @Query("UPDATE User u " +
            "SET u.password = ?1 WHERE u.email = ?2")
    Integer updatePassword(String password, String email);
}
