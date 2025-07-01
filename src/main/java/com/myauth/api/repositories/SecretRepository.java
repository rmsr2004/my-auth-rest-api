package com.myauth.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.myauth.api.model.Secret;

public interface SecretRepository extends JpaRepository<Secret, Long> {
    List<Secret> findByUserId(Long id);
}
