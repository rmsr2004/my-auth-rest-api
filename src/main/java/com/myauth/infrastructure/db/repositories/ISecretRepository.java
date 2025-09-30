package com.myauth.infrastructure.db.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myauth.infrastructure.db.entities.Secret;
import com.myauth.infrastructure.db.entities.User;

public interface ISecretRepository extends JpaRepository<Secret, Long> {
    Optional<Secret> findByUserAndIssuer(User user, String issuer);
}