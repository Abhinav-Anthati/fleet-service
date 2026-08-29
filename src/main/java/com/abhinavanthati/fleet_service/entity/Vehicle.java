package com.abhinavanthati.fleet_service.entity;

import java.util.List;

import com.abhinavanthati.fleet_service.enums.VehicleStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String make;

    @NotBlank
    private String model;

    @NotNull
    private int year;
    
    @NotBlank
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    @OneToMany(mappedBy = "vehicle")
    @JsonIgnoreProperties("vehicle")
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "vehicle")
    @JsonIgnoreProperties("vehicle")
    private List<MaintenanceWindow> maintenanceWindows;

    public Vehicle() {}

    public Long getId() { return id; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public VehicleStatus getStatus() { return status; }
    public void setStatus(VehicleStatus status) { this.status = status; }
    public List<Reservation> getReservations() { return reservations; }
    public List<MaintenanceWindow> getMaintenanceWindows() { return maintenanceWindows; }
}
