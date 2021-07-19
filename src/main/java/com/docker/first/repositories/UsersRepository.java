package com.docker.first.repositories;

import java.util.Optional;

import com.docker.first.models.User;

public interface UsersRepository extends GenericRepository<User, Long> {
    // public Optional<User> findByUsername(String username);
    public Optional<User> findByEmail(String email);
}

