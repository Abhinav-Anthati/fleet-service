package com.abhinavanthati.fleet_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.abhinavanthati.fleet_service.entity.Reservation;
import com.abhinavanthati.fleet_service.entity.User;
import com.abhinavanthati.fleet_service.enums.ReservationStatus;
import com.abhinavanthati.fleet_service.repository.ReservationRepository;

/**
 * Service class for managing reservations.
 */
@Service
public class ReservationService {
    
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private AvailabilityService availabilityService;

    /**
     * Creates a new reservation after checking vehicle availability.
     * 
     * @param reservation The reservation to be created.
     * @param requester The user this reservation is actually being created for.
     * @return The created reservation.
     * @throws IllegalStateException if the vehicle is not available during the requested time.
     */
    @PreAuthorize("hasAnyRole('DRIVER', 'MANAGER', 'ADMIN')")
    public Reservation createReservation(Reservation reservation, User requester) {
        boolean available = availabilityService.isVehicleAvailable(
            reservation.getVehicle().getId(),
            reservation.getStartTime(),
            reservation.getEndTime()
        );
        if (!available) {
            throw new IllegalStateException("This vehicle is not available during that time.");
        }
        reservation.setRequester(requester);
        reservation.setStatus(ReservationStatus.PENDING);
        return reservationRepository.save(reservation);
    }

    /**
     * Retrieves a reservation by its ID.
     * 
     * @param id The ID of the reservation to retrieve.
     * @return The reservation with the specified ID.
     * @throws IllegalStateException if the reservation is not found.
     */
    @PreAuthorize("hasAnyRole('DRIVER', 'MANAGER', 'ADMIN')")
    public Reservation getReservationById(Long id, String requestingUserEmail, boolean isManagerOrAdmin) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Reservation not found"));

        boolean isOwner = reservation.getRequester().getEmail().equals(requestingUserEmail);
        if (!isOwner && !isManagerOrAdmin) {
            throw new AccessDeniedException("Not your reservation");
        }

        return reservation;
    }

    /**
     * Retrieves all reservations.
     * 
     * @return A list of all reservations.
     */
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    /**
     * Updates the details of an existing reservation. Only the owning driver or a manager/admin can update reservation details.
     * 
     * @param id The ID of the reservation to update.
     * @param updatedDetails The updated details for the reservation.
     * @param newRequester The user this reservation is actually being updated for.
     * @param requestingUserEmail The email of the user requesting the update.
     * @param isManagerOrAdmin Whether the currently authenticated user has manager or admin privileges.
     * @return The updated reservation.
     * @throws IllegalStateException if the reservation is not found or if the current user does not have permission to update the reservation.
     */
    @PreAuthorize("hasAnyRole('DRIVER', 'MANAGER', 'ADMIN')")
    public Reservation updateReservationDetails(
            Long id, Reservation updatedDetails, User newRequester, String requestingUserEmail, boolean isManagerOrAdmin) {
        Reservation existing = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Reservation not found"));

        boolean isOwner = existing.getRequester().getEmail().equals(requestingUserEmail);
        if (!isOwner && !isManagerOrAdmin) {
            throw new AccessDeniedException("Not your reservation");
        }

        existing.setStartTime(updatedDetails.getStartTime());
        existing.setEndTime(updatedDetails.getEndTime());
        existing.setVehicle(updatedDetails.getVehicle());
        existing.setDescription(updatedDetails.getDescription());
        existing.setRequester(newRequester);

        if (existing.getStatus() == ReservationStatus.APPROVED) {
            existing.setStatus(ReservationStatus.PENDING);
        }

        return reservationRepository.save(existing);
    }
    
    /**
     * Updates the status of a reservation. Only managers or admins may call this.
     * 
     * @param id The ID of the reservation to update.
     * @param newStatus The new status for the reservation.
     * @return The updated reservation.
     * @throws IllegalStateException if the reservation is not found.
     */
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public Reservation updateReservationStatus(Long id, ReservationStatus newStatus) {
        Reservation existing = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Reservation not found"));
        existing.setStatus(newStatus);
        return reservationRepository.save(existing);
    }

    /**
     * Cancels a reservation. Only the owning driver or a manager/admin may call this.
     * 
     * @param id The ID of the reservation to cancel.
     * @param requestingUserEmail The email of the user requesting the cancellation.
     * @param isManagerOrAdmin A flag indicating if the user is a manager or admin.
     * @return The updated reservation.
     * @throws IllegalStateException if the reservation is not found.
     */
    @PreAuthorize("hasAnyRole('DRIVER', 'MANAGER', 'ADMIN')")
    public Reservation cancelReservation(Long id, String requestingUserEmail, boolean isManagerOrAdmin) {
        Reservation existing = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Reservation not found"));

        boolean isOwner = existing.getRequester().getEmail().equals(requestingUserEmail);
        if (!isOwner && !isManagerOrAdmin) {
            throw new AccessDeniedException("Not your reservation");
        }

        existing.setStatus(ReservationStatus.CANCELLED);
        return reservationRepository.save(existing);
    }

    /**
     * Deletes a reservation by its ID. Only admins may call this.
     * 
     * @param id The ID of the reservation to delete.
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    /**
     * Retrieves all reservations made by a specific user.
     * 
     * @param email The email of the user whose reservations are to be retrieved.
     * @return A list of reservations made by the specified user.
     */
    @PreAuthorize("hasAnyRole('DRIVER', 'MANAGER', 'ADMIN')")
    public List<Reservation> getReservationsByRequesterEmail(String email) {
        return reservationRepository.findByRequesterEmail(email);
    }
}
