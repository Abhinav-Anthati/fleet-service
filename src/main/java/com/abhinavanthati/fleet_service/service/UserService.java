package com.abhinavanthati.fleet_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.abhinavanthati.fleet_service.entity.User;
import com.abhinavanthati.fleet_service.enums.UserRoles;
import com.abhinavanthati.fleet_service.repository.UserRepository;

/**
 * Service class for managing users.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Deletes a user by their ID.
     * 
     * @param id The ID of the user to be deleted.
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Retrieves a user by their ID.
     * 
     * @param id The ID of the user to retrieve.
     * @return The user with the specified ID.
     * @throws IllegalStateException if the user is not found.
     */
    @PreAuthorize("hasAnyRole('DRIVER', 'MANAGER', 'ADMIN')")
    public User getUserById(Long id, String currentUserEmail, boolean isAdmin) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new IllegalStateException("User not found"));

        boolean isSelf = user.getEmail().equals(currentUserEmail);
        if (!isSelf && !isAdmin) {
            throw new IllegalStateException("You do not have permission to view this user.");
        }

        return user;
    }

    /**
     * Updates the details of an existing user. Only the user themselves or an admin can update user details.
     * 
     * @param id The ID of the user to update.
     * @param updatedUser The updated details for the user.
     * @param currentUserName The username of the currently authenticated user.
     * @param isAdmin Whether the currently authenticated user has admin privileges.
     * @return The updated user.
     * @throws IllegalStateException if the user is not found or if the current user does not have permission to update the user.
     */
    @PreAuthorize("hasAnyRole('DRIVER', 'MANAGER', 'ADMIN')")
    public User updateUser(Long id, User updatedUser, String currentUserName, boolean isAdmin) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        boolean isCurrentUser = existingUser.getEmail().equals(currentUserName);

        if (!isCurrentUser && !isAdmin) {
            throw new IllegalStateException("You do not have permission to update this user.");
        }

        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        return userRepository.save(existingUser);
    }


    /**
     * Updates the role of an existing user. Only admins can update user roles.
     * 
     * @param id The ID of the user to update.
     * @param newRole The new role for the user.
     * @return The updated user.
     * @throws IllegalStateException if the user is not found.
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    public User updateUserRole(Long id, UserRoles newRole) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        existingUser.setRole(newRole);
        return userRepository.save(existingUser);
    }

    /**
     * Retrieves all users.
     * 
     * @return A list of all users.
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Creates a new user after checking for email uniqueness.
     * 
     * @param user The user to be created.
     * @return The created user.
     * @throws IllegalStateException if a user with the same email already exists.
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    public User createUser(User user) {
        List<User> existingUsers = userRepository.findAll();
        for (User existingUser : existingUsers) {
            if (existingUser.getEmail().equals(user.getEmail())) {
                throw new IllegalStateException("A user with this email already exists.");
            }
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }
}
