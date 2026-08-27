package com.abhinavanthati.fleet_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.abhinavanthati.fleet_service.entity.MaintenanceWindow;
import com.abhinavanthati.fleet_service.entity.Reservation;
import com.abhinavanthati.fleet_service.enums.ReservationStatus;
import com.abhinavanthati.fleet_service.repository.MaintenanceWindowRepository;
import com.abhinavanthati.fleet_service.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for checking vehicle availability.
 */
@Service
public class AvailabilityService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MaintenanceWindowRepository maintenanceWindowRepository;

    /**
     * Checks if a vehicle is available for a given time range.
     *
     * @param vehicleId The ID of the vehicle to check.
     * @param start The start time of the desired time range.
     * @param end The end time of the desired time range.
     * @return true if the vehicle is available, false otherwise.
     */
    @PreAuthorize("hasAnyRole('DRIVER', 'MANAGER', 'ADMIN')")
    public boolean isVehicleAvailable(Long vehicleId, LocalDateTime start, LocalDateTime end) {
        List<Reservation> reservations = reservationRepository.findByVehicleId(vehicleId);
        for (Reservation r : reservations) {
            if (r.getStatus() == ReservationStatus.CANCELLED || r.getStatus() == ReservationStatus.DENIED) {
                continue;
            }
            if (start.isBefore(r.getEndTime()) && end.isAfter(r.getStartTime())) {
                return false;
            }
        }

        List<MaintenanceWindow> windows = maintenanceWindowRepository.findByVehicleId(vehicleId);
        for (MaintenanceWindow w : windows) {
            if (start.isBefore(w.getEndTime()) && end.isAfter(w.getStartTime())) {
                return false;
            }
        }

        return true;
    }
}