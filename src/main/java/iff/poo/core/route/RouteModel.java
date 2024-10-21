package iff.poo.core.route;

import iff.poo.core.city.CityModel;
import lombok.Data;

import java.util.List;

@Data
public class RouteModel {
    private Long id;
    private CityModel originCity;
    private CityModel destinyCity;
    private Double distance;
    private Double basePrice;
    private List<RouteStop> routeStops;

    @Data
    public static class RouteStop {
        private Long id;
        private CityModel city;
        private int stopOrder;
        private Double distanceFromOrigin;
    }
}
