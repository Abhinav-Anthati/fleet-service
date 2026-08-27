package com.abhinavanthati.fleet_service.dto;

import java.time.LocalDateTime;

/**
 * DTO representing a busy window for a vehicle, which can be due to either a reservation or a maintenance window.
 */
public class BusyWindow {
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public BusyWindow(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
