package iff.poo.core.travel;

import iff.poo.core.route.RouteModel;
import iff.poo.core.vehicle.VehicleModel;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TravelModel {
    private Long id;
    private RouteModel route;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private VehicleModel vehicle;
    private Availability availability;

    @Data
    public static class Availability {
        private List<Integer> availableSeats;
        private Long requestedOriginCityId;
        private Long requestedDestinyCityId;
        private Double price;
    }
}
