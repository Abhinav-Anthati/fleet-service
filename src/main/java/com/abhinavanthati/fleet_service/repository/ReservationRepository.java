package com.abhinavanthati.fleet_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhinavanthati.fleet_service.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByVehicleId(Long vehicleId);
    List<Reservation> findByRequesterEmail(String email);
}
