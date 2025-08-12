package com.myauth.Infrastructure.Repositories.Entities;

import com.myauth.Domain.Entities.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, unique=true)
    private String username;

    @Column(nullable=false)
    private String password;

    public User toDomain() {
        return new User(this.id, this.username, this.password);
    }
}