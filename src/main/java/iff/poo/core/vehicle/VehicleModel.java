package iff.poo.core.vehicle;

import lombok.Data;

import java.time.LocalDate;

@Data
public class VehicleModel {
    private Long id;
    private String licensePlate;
    private String model;
    private int capacity;
    private LocalDate lastMaintenance;
}
