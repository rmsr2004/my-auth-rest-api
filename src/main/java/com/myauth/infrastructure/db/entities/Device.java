package com.myauth.infrastructure.db.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    @Column(nullable=false)
    private String name;

    @Column(nullable=false)
    private Boolean admin;

    @ManyToOne
    @JoinColumn(name="users_id", nullable=false)
    private User user;

    @Override
    public String toString() {
        return id;
    }
}