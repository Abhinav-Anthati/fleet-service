package com.abhinavanthati.fleet_service.service;

import com.abhinavanthati.fleet_service.entity.Reservation;
import com.abhinavanthati.fleet_service.entity.User;
import com.abhinavanthati.fleet_service.entity.Vehicle;
import com.abhinavanthati.fleet_service.enums.ReservationStatus;
import com.abhinavanthati.fleet_service.enums.UserRole;
import com.abhinavanthati.fleet_service.enums.VehicleStatus;
import com.abhinavanthati.fleet_service.repository.UserRepository;
import com.abhinavanthati.fleet_service.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ReservationService.
 */
@SpringBootTest
@Transactional
public class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    private User driver;
    private Vehicle vehicle;
    private Reservation reservation;

    /**
     * Sets up test data before each test.
     */
    @BeforeEach
    void setUp() {
        driver = new User();
        driver.setName("Test Driver");
        driver.setEmail("testdriver@fleet.com");
        driver.setPassword("hashed");
        driver.setRole(UserRole.DRIVER);
        driver = userRepository.save(driver);

        vehicle = new Vehicle();
        vehicle.setMake("Ford");
        vehicle.setModel("Transit");
        vehicle.setYear(2023);
        vehicle.setLicensePlate("TEST-0001");
        vehicle.setStatus(VehicleStatus.AVAILABLE);
        vehicle = vehicleRepository.save(vehicle);

        reservation = new Reservation();
        reservation.setVehicle(vehicle);
        reservation.setRequester(driver);
        reservation.setStartTime(LocalDateTime.now().plusDays(1));
        reservation.setEndTime(LocalDateTime.now().plusDays(1).plusHours(2));
        reservation.setStatus(ReservationStatus.PENDING);
        reservation = reservationService.createReservation(reservation, driver);
    }

    /**
     * Tests that a driver cannot approve their own reservation.
     */
    @Test
    @WithMockUser(username = "testdriver@fleet.com", roles = "DRIVER")
    void driverCannotApproveOwnReservation() {
        assertThrows(AccessDeniedException.class, () -> {
            reservationService.updateReservationStatus(reservation.getId(), ReservationStatus.APPROVED);
        });
    }

    /**
     * Tests that a manager can approve any reservation.
     */
    @Test
    @WithMockUser(username = "manager1@fleet.com", roles = "MANAGER")
    void managerCanApproveAnyReservation() {
        Reservation approved = reservationService.updateReservationStatus(reservation.getId(), ReservationStatus.APPROVED);
        assertEquals(ReservationStatus.APPROVED, approved.getStatus());
    }

    /**
     * Tests that editing an approved reservation demotes it back to pending.
     */
    @Test
    @WithMockUser(username = "testdriver@fleet.com", roles = "DRIVER")
    void editingApprovedReservationDemotesToPending() {
        reservation.setStatus(ReservationStatus.APPROVED);

        Reservation updated = reservationService.updateReservationDetails(
                reservation.getId(), reservation, driver, driver.getEmail(), false);

        assertEquals(ReservationStatus.PENDING, updated.getStatus());
    }
}