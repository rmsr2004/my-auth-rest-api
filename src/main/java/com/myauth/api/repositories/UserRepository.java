package com.myauth.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.myauth.api.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
}
