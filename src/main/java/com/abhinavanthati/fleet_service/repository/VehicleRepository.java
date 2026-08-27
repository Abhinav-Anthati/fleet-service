package com.abhinavanthati.fleet_service.repository;

import com.abhinavanthati.fleet_service.entity.Vehicle;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
}