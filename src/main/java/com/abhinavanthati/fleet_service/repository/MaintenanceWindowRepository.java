package com.abhinavanthati.fleet_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhinavanthati.fleet_service.entity.MaintenanceWindow;

public interface MaintenanceWindowRepository extends JpaRepository<MaintenanceWindow, Long> {
    
    List<MaintenanceWindow> findByVehicleId(Long vehicleId);
}
