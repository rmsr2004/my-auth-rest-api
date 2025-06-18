package com.myauth.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="secrets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Secret {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String issuer;

    @Column(nullable = false)
    private String secret;

    @ManyToOne
    @JoinColumn(name="users_id", nullable = false)
    private User user;
}
