package com.abhinavanthati.fleet_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.abhinavanthati.fleet_service.entity.MaintenanceWindow;
import com.abhinavanthati.fleet_service.repository.MaintenanceWindowRepository;

/**
 * Service class for managing maintenance windows.
 */
@Service
public class MaintenanceWindowService {

    @Autowired
    private MaintenanceWindowRepository maintenanceWindowRepository;

    @Autowired
    private AvailabilityService availabilityService;

    /**
     * Creates a new maintenance window after checking vehicle availability.
     * 
     * @param maintenanceWindow The maintenance window to be created.
     * @return The created maintenance window.
     * @throws IllegalStateException if the vehicle is not available during the specified time.
     */
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public MaintenanceWindow createMaintenanceWindow(MaintenanceWindow maintenanceWindow) {
        boolean available = availabilityService.isVehicleAvailable(
            maintenanceWindow.getVehicle().getId(),
            maintenanceWindow.getStartTime(),
            maintenanceWindow.getEndTime()
        );
        if (!available) {
            throw new IllegalStateException("This vehicle is not available during that time.");
        }
        return maintenanceWindowRepository.save(maintenanceWindow);
    }

    /**
     * Deletes a maintenance window by its ID.
     * 
     * @param id The ID of the maintenance window to be deleted.
     */
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public void deleteMaintenanceWindow(Long id) {
        maintenanceWindowRepository.deleteById(id);
    }

    /**
     * Retrieves all maintenance windows.
     * 
     * @return A list of all maintenance windows.
     */
    @PreAuthorize("hasAnyRole('DRIVER', 'MANAGER', 'ADMIN')")
    public List<MaintenanceWindow> getAllMaintenanceWindows() {
        return maintenanceWindowRepository.findAll();
    }

    /**
     * Retrieves a maintenance window by its ID.
     * 
     * @param id The ID of the maintenance window to retrieve.
     * @return The maintenance window with the specified ID.
     * @throws IllegalStateException if the maintenance window is not found.
     */
    @PreAuthorize("hasAnyRole('DRIVER', 'MANAGER', 'ADMIN')")
    public MaintenanceWindow getMaintenanceWindowById(Long id) {
        return maintenanceWindowRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Maintenance window not found"));
    }

    /**
     * Updates the details of an existing maintenance window. Only users with MANAGER or ADMIN roles can update maintenance windows.
     * 
     * @param id The ID of the maintenance window to update.
     * @param updatedWindow The updated details for the maintenance window.
     * @return The updated maintenance window.
     * @throws IllegalStateException if the maintenance window is not found.
     */
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public MaintenanceWindow updateMaintenanceWindow(Long id, MaintenanceWindow updatedWindow) {
        MaintenanceWindow existingWindow = maintenanceWindowRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Maintenance window not found"));

        existingWindow.setStartTime(updatedWindow.getStartTime());
        existingWindow.setEndTime(updatedWindow.getEndTime());
        existingWindow.setVehicle(updatedWindow.getVehicle());
        existingWindow.setDescription(updatedWindow.getDescription());

        return maintenanceWindowRepository.save(existingWindow);
    }
}