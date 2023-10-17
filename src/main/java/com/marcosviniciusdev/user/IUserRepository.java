package com.marcosviniciusdev.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IUserRepository extends JpaRepository<UserModel, UUID> {

    public UserModel findByUsername(String name);
}
