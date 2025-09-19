package com.myauth.infrastructure.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myauth.infrastructure.db.entities.User;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
