package com.myauth.infrastructure.db.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="devices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Device {
    @Id
    @Column(nullable=false, unique=true)
    private String id;

    @ManyToOne
    @JoinColumn(name="users_id", nullable=false)
    private User user;

    @Override
    public String toString() {
        return id;
    }
}