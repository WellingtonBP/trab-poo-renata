package iff.poo.core.route;

import iff.poo.core.city.CityModel;
import iff.poo.core.exceptions.AuthException;
import iff.poo.core.user.UserService;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class RouteService {
    @Inject
    RouteRepo routeRepo;

    @Inject
    UserService userService;

    public Long createRoute(
            Long originCityId,
            Long destinyCityId,
            Double distance,
            Double basePrice,
            int routeStopsCount,
            List<Long> routeStopsCitiesIds,
            List<Integer> routeStopsStopOrders,
            List<Double> routeStopsDistances,
            String currUserEmail
    ) throws AuthException {
        Log.info(currUserEmail);
        var currUser = userService.getUserByEmail(currUserEmail);
        if(currUser == null || !currUser.getType().equals("admin")) {
            throw new AuthException("Only admins can create routes", 403);
        }

        var originCity = new CityModel();
        originCity.setId(originCityId);
        var destinyCity = new CityModel();
        destinyCity.setId(destinyCityId);

        List<RouteModel.RouteStop> routeStops = new ArrayList<>();
        for(int i = 0; i < routeStopsCount; i++) {
            var routeStopCity = new CityModel();
            routeStopCity.setId(routeStopsCitiesIds.get(i));
            var routeStop = new RouteModel.RouteStop();
            routeStop.setCity(routeStopCity);
            routeStop.setStopOrder(routeStopsStopOrders.get(i));
            routeStop.setDistanceFromOrigin(routeStopsDistances.get(i));
            routeStops.add(routeStop);
        }

        var route = new RouteModel();
        route.setOriginCity(originCity);
        route.setDestinyCity(destinyCity);
        route.setDistance(distance);
        route.setBase_price(basePrice);
        route.setRouteStops(routeStops);

        return routeRepo.create(route);
    }

    public List<RouteModel> getRoutes(String currUserEmail) throws AuthException {
        var currUser = userService.getUserByEmail(currUserEmail);
        if(currUser == null || !currUser.getType().equals("admin")) {
            throw new AuthException("Only admins can list routes", 403);
        }

        return routeRepo.getAll();
    }
}
