package iff.poo.core.travel;

import iff.poo.core.vehicle.VehicleModel;
import io.vertx.ext.web.Route;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TravelModel {
    private Long id;
    private Route route;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private VehicleModel vehicle;
}
