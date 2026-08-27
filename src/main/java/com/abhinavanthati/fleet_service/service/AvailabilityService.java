package com.abhinavanthati.fleet_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.abhinavanthati.fleet_service.dto.BusyWindow;
import com.abhinavanthati.fleet_service.entity.MaintenanceWindow;
import com.abhinavanthati.fleet_service.entity.Reservation;
import com.abhinavanthati.fleet_service.enums.ReservationStatus;
import com.abhinavanthati.fleet_service.repository.MaintenanceWindowRepository;
import com.abhinavanthati.fleet_service.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    /**
     * Retrieves all busy windows (reservations and maintenance) for a given vehicle.
     *
     * @param vehicleId The ID of the vehicle.
     * @return A list of busy windows for the vehicle.
     */
    @PreAuthorize("hasAnyRole('DRIVER', 'MANAGER', 'ADMIN')")
    public List<BusyWindow> getBusyWindowsForVehicle(Long vehicleId) {
        List<BusyWindow> busy = new ArrayList<>();

        for (Reservation r : reservationRepository.findByVehicleId(vehicleId)) {
            if (r.getStatus() == ReservationStatus.CANCELLED || r.getStatus() == ReservationStatus.DENIED) {
                continue;
            }
            busy.add(new BusyWindow(r.getStartTime(), r.getEndTime()));
        }

        for (MaintenanceWindow w : maintenanceWindowRepository.findByVehicleId(vehicleId)) {
            busy.add(new BusyWindow(w.getStartTime(), w.getEndTime()));
        }

        return busy;
    }
}