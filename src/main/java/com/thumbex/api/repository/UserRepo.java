package com.thumbex.api.repository;

import java.util.Optional;

import com.thumbex.api.domain.User;

import org.springframework.data.repository.CrudRepository;

public interface UserRepo extends CrudRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
