package com.myauth.infrastructure.db.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myauth.infrastructure.db.entities.Secret;

public interface ISecretRepository extends JpaRepository<Secret, Long> {}
