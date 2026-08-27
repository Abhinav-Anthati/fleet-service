package com.abhinavanthati.fleet_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.abhinavanthati.fleet_service.entity.Vehicle;
import com.abhinavanthati.fleet_service.repository.VehicleRepository;

/**
 * Service class for managing vehicles.
 */
@Service
public class VehicleService {
    
    @Autowired
    private VehicleRepository vehicleRepository;

    /**
     * Creates a new vehicle. Only users with MANAGER or ADMIN roles can create vehicles.
     * 
     * @param vehicle The vehicle to be created.
     * @return The created vehicle.
     */
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public Vehicle createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    /**
     * Retrieves all vehicles.
     * 
     * @return A list of all vehicles.
     */
    @PreAuthorize("hasAnyRole('DRIVER', 'MANAGER', 'ADMIN')")
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    /**
     * Retrieves a vehicle by its ID.
     * 
     * @param id The ID of the vehicle to retrieve.
     * @return The vehicle with the specified ID.
     * @throws IllegalStateException if the vehicle is not found.
     */
    @PreAuthorize("hasAnyRole('DRIVER', 'MANAGER', 'ADMIN')")
    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Vehicle not found"));
    }

    /**
     * Deletes a vehicle by its ID.
     * 
     * @param id The ID of the vehicle to delete.
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }

    /**
     * Updates the details of an existing vehicle. Only users with MANAGER or ADMIN roles can update vehicles.
     * 
     * @param id The ID of the vehicle to update.
     * @param updatedVehicle The updated details for the vehicle.
     * @return The updated vehicle.
     * @throws IllegalStateException if the vehicle is not found.
     */
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public Vehicle updateVehicle(Long id, Vehicle updatedVehicle) {
        Vehicle existingVehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Vehicle not found"));

        existingVehicle.setMake(updatedVehicle.getMake());
        existingVehicle.setModel(updatedVehicle.getModel());
        existingVehicle.setYear(updatedVehicle.getYear());
        existingVehicle.setStatus(updatedVehicle.getStatus());
        existingVehicle.setLicensePlate(updatedVehicle.getLicensePlate());

        return vehicleRepository.save(existingVehicle);
    }
}
