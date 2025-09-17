package com.myauth.Infrastructure.Repositories.Entities;

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
public class DeviceEntity {
    @Id
    @Column(nullable=false, unique=true)
    private String id;

    @ManyToOne
    @JoinColumn(name="users_id", nullable=false)
    private UserEntity user;

    @Override
    public String toString() {
        return id;
    }
}