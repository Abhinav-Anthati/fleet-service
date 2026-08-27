package com.abhinavanthati.fleet_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abhinavanthati.fleet_service.entity.Reservation;
import com.abhinavanthati.fleet_service.entity.User;
import com.abhinavanthati.fleet_service.enums.ReservationStatus;
import com.abhinavanthati.fleet_service.repository.UserRepository;
import com.abhinavanthati.fleet_service.service.ReservationService;

/**
 * REST controller for managing reservations.
 */
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public Reservation createReservation(@RequestBody Reservation reservation, Authentication auth) {
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
        boolean isManagerOrAdmin = hasManagerAuthority(auth);
        User requester = resolveRequester(reservation, currentUser, isManagerOrAdmin);
        return reservationService.createReservation(reservation, requester);
    }

    @GetMapping("/{id}")
    public Reservation getReservationById(@PathVariable Long id) {
        return reservationService.getReservationById(id);
    }

    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @PutMapping("/{id}")
    public Reservation updateReservationDetails(
            @PathVariable Long id, @RequestBody Reservation updatedDetails, Authentication auth) {
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
        boolean isManagerOrAdmin = hasManagerAuthority(auth);
        User newRequester = resolveRequester(updatedDetails, currentUser, isManagerOrAdmin);
        return reservationService.updateReservationDetails(id, updatedDetails, newRequester, currentUser.getEmail(), isManagerOrAdmin);
    }

    @PutMapping("/{id}/status")
    public Reservation updateReservationStatus(
            @PathVariable Long id, @RequestBody ReservationStatus newStatus) {
        return reservationService.updateReservationStatus(id, newStatus);
    }

    @DeleteMapping("/{id}")
    public void cancelReservation(@PathVariable Long id, Authentication auth) {
        reservationService.cancelReservation(id, auth.getName(), hasManagerAuthority(auth));
    }

    private boolean hasManagerAuthority(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER") || a.getAuthority().equals("ROLE_ADMIN"));
    }

    private User resolveRequester(Reservation incoming, User currentUser, boolean isManagerOrAdmin) {
        if (isManagerOrAdmin && incoming.getRequester() != null && incoming.getRequester().getId() != null) {
            return userRepository.findById(incoming.getRequester().getId())
                    .orElseThrow(() -> new IllegalStateException("Requester not found"));
        }
        return currentUser;
    }
}