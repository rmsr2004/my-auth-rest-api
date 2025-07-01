package com.myauth.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.myauth.api.entities.Secret;

public interface SecretRepository extends JpaRepository<Secret, Long> {
    List<Secret> findByUserId(Long id);
}
