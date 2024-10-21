package iff.poo.core.travel;

import iff.poo.core.exceptions.AuthException;
import iff.poo.core.route.RouteModel;
import iff.poo.core.ticket.TicketService;
import iff.poo.core.user.UserService;
import iff.poo.core.vehicle.VehicleModel;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

@ApplicationScoped
public class TravelService {
    @Inject
    TravelRepo travelRepo;

    @Inject
    TicketService ticketService;

    @Inject
    UserService userService;

    public Long createTravel(Long routeId, LocalDateTime startDate, LocalDateTime endDate, String status, Long vehicleId, String currUserEmail) throws AuthException {
        var currUser = userService.getUserByEmail(currUserEmail);
        if(currUser == null || !currUser.getType().equals("admin")) {
            throw new AuthException("Only admins can create travels", 403);
        }

        var travel = new TravelModel();
        travel.setStartDate(startDate);
        travel.setEndDate(endDate);
        travel.setStatus(status);
        var route = new RouteModel();
        route.setId(routeId);
        travel.setRoute(route);
        var vehicle = new VehicleModel();
        vehicle.setId(vehicleId);
        travel.setVehicle(vehicle);

        return travelRepo.create(travel);
    }

    public List<TravelModel> getTravelsByCities(Long originCityId, Long destinyCityId) {
        var travels = travelRepo.getTravelByOriginAndDestinyCities(originCityId, destinyCityId);
        travels.forEach(t -> handleAvailability(t, originCityId, destinyCityId));
        return travels;
    }

    public boolean checkAvailability(Long travelId, Long originRouteStopId, Long destinyRouteStopId, int requestedSeat) {
        var travel = travelRepo.getById(travelId);
        Long originCityId = travel.getRoute().getOriginCity().getId();
        if(originRouteStopId != null && originRouteStopId != 0) {
            var originRouteStop = travel.getRoute().getRouteStops().stream().filter(rs -> rs.getId().equals(originRouteStopId)).findAny();
            if(originRouteStop.isPresent()) {
                originCityId = originRouteStop.get().getCity().getId();
            }
        }
        Long destinyCityId = travel.getRoute().getDestinyCity().getId();
        if(destinyRouteStopId != null && destinyRouteStopId != 0) {
            var destinyRouteStop = travel.getRoute().getRouteStops().stream().filter(rs -> rs.getId().equals(destinyRouteStopId)).findAny();
            if(destinyRouteStop.isPresent()) {
                originCityId = destinyRouteStop.get().getCity().getId();
            }
        }
        handleAvailability(travel, originCityId, destinyCityId);
        return travel.getAvailability().getAvailableSeats().contains(requestedSeat);
    }

    private void handleAvailability(TravelModel travel, Long originCityId, Long destinyCityId) {
        var ticketsForTravel = ticketService.getTicketsByTravelId(travel.getId());
        Set<Integer> availableSeats = new HashSet<>(IntStream.rangeClosed(1, travel.getVehicle().getCapacity()).boxed().toList());
        List<Long> travelCitiesIds = new ArrayList<>();
        travelCitiesIds.add(travel.getRoute().getOriginCity().getId());
        travelCitiesIds.addAll(travel.getRoute().getRouteStops().stream().map(rs -> rs.getCity().getId()).toList());
        travelCitiesIds.add(travel.getRoute().getDestinyCity().getId());

        for(var ticket : ticketsForTravel) {
            Long tkOriginCity = null;
            if(ticket.getOriginRouteStopId() == null || ticket.getOriginRouteStopId() == 0) {
                tkOriginCity = travel.getRoute().getOriginCity().getId();
            } else {
                var rsOrigin = travel.getRoute().getRouteStops().stream()
                        .filter(rs -> rs.getId().equals(ticket.getOriginRouteStopId())).findAny();
                if(rsOrigin.isPresent()) {
                    tkOriginCity = rsOrigin.get().getCity().getId();
                }
            }
            Long tkDestinyCity = null;
            if(ticket.getDestinyRouteStopId() == null || ticket.getDestinyRouteStopId() == 0) {
                tkDestinyCity = travel.getRoute().getDestinyCity().getId();
            } else {
                var rsDestiny = travel.getRoute().getRouteStops().stream()
                        .filter(rs -> rs.getId().equals(ticket.getDestinyRouteStopId())).findAny();
                if(rsDestiny.isPresent()) {
                    tkDestinyCity = rsDestiny.get().getCity().getId();
                }
            }

            int tkOriginCityIdx = 0;
            int tkDestinyCityIdx = 0;
            int originCityIdx = 0;
            int destinyCityIdx = 0;

            for(int i = 0; i < travelCitiesIds.size(); i++) {
                Long cityId = travelCitiesIds.get(i);
                if(tkOriginCity != null && tkOriginCity.equals(cityId)) tkOriginCityIdx = i;
                if(tkDestinyCity != null && tkDestinyCity.equals(cityId)) tkDestinyCityIdx = i;
                if(originCityId != null && originCityId.equals(cityId)) originCityIdx = i;
                if(destinyCityId != null && destinyCityId.equals(cityId)) destinyCityIdx = i;
            }
            System.out.println("AQUIIIIII");
            System.out.println("=========");
            System.out.println("ticketOrigin "+ tkOriginCityIdx);
            System.out.println("ticketDestiny "+ tkDestinyCityIdx);
            System.out.println("Origin "+ originCityIdx);
            System.out.println("Destiny "+ destinyCityIdx);


            if((tkOriginCityIdx <= originCityIdx && tkDestinyCityIdx >= destinyCityIdx) || (originCityIdx <= tkOriginCityIdx && destinyCityIdx >= tkDestinyCityIdx)) {
                availableSeats.remove(ticket.getSeatNumber());
            }
        }

        var availability = new TravelModel.Availability();
        availability.setAvailableSeats(availableSeats.stream().toList());
        availability.setRequestedDestinyCityId(destinyCityId);
        availability.setRequestedOriginCityId(originCityId);

        var pricePerKm = travel.getRoute().getBasePrice() / travel.getRoute().getDistance();
        var originCityDistance = 0D;
        var percent = 1F;
        if(originCityId != null && !originCityId.equals(travel.getRoute().getOriginCity().getId())) {
            var routeStop = travel.getRoute().getRouteStops().stream().filter(rs -> rs.getCity().getId().equals(originCityId)).findAny();
            if(routeStop.isPresent()) {
                originCityDistance = routeStop.get().getDistanceFromOrigin();
                percent += 0.05F;
            }
        }
        Double destinyCityDistance = travel.getRoute().getDistance();
        if(destinyCityId != null && !destinyCityId.equals(travel.getRoute().getDestinyCity().getId())) {
            var routeStop = travel.getRoute().getRouteStops().stream().filter(rs -> rs.getCity().getId().equals(destinyCityId)).findAny();
            if(routeStop.isPresent()) {
                destinyCityDistance = routeStop.get().getDistanceFromOrigin();
                percent += 0.05F;
            }
        }
        availability.setPrice((destinyCityDistance - originCityDistance) * pricePerKm * percent);
        travel.setAvailability(availability);
    }
}
