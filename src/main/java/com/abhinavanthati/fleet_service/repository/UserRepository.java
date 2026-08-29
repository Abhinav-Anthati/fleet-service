package com.abhinavanthati.fleet_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhinavanthati.fleet_service.entity.User;
import com.abhinavanthati.fleet_service.enums.UserRole;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(UserRole role);
}
