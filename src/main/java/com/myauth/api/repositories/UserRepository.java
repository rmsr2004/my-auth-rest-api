package com.myauth.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.myauth.api.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
}
