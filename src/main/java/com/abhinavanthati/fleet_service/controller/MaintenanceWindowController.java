package com.abhinavanthati.fleet_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abhinavanthati.fleet_service.entity.MaintenanceWindow;
import com.abhinavanthati.fleet_service.service.MaintenanceWindowService;

@RestController
@RequestMapping("/api/maintenance-windows")
public class MaintenanceWindowController {
    @Autowired
    private MaintenanceWindowService maintenanceWindowService;

    @PostMapping
    public MaintenanceWindow createMaintenanceWindow(@RequestBody MaintenanceWindow maintenanceWindow) {
        return maintenanceWindowService.createMaintenanceWindow(maintenanceWindow);
    }

    @GetMapping
    public List<MaintenanceWindow> getAllMaintenanceWindows() {
        return maintenanceWindowService.getAllMaintenanceWindows();
    }

    @DeleteMapping("/{id}")
    public void deleteMaintenanceWindow(@PathVariable Long id) {
        maintenanceWindowService.deleteMaintenanceWindow(id);
    }

    @GetMapping("/{id}")
    public MaintenanceWindow getMaintenanceWindowById(@PathVariable Long id) {
        return maintenanceWindowService.getMaintenanceWindowById(id);
    }

    @PostMapping("/{id}")
    public MaintenanceWindow updateMaintenanceWindow(@PathVariable Long id, @RequestBody MaintenanceWindow updatedWindow) {
        return maintenanceWindowService.updateMaintenanceWindow(id, updatedWindow);
    }
}