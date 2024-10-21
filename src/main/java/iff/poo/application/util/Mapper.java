package iff.poo.application.util;

import iff.poo.application.dto.CityDto;
import iff.poo.application.dto.RouteDto;
import iff.poo.application.dto.TravelDto;
import iff.poo.application.dto.VehicleDto;
import iff.poo.core.city.CityModel;
import iff.poo.core.route.RouteModel;
import iff.poo.core.travel.TravelModel;
import iff.poo.core.vehicle.VehicleModel;

import java.util.List;

public class Mapper {
    public static List<CityDto> fromCity(List<CityModel> cities) {
        return cities.stream().map(Mapper::fromCity).toList();
    }

    public static CityDto fromCity(CityModel city) {
        var cityDto = new CityDto();
        cityDto.id = city.getId();
        cityDto.name = city.getName();
        cityDto.uf = city.getUf();
        return cityDto;
    }

    public static List<TravelDto> fromTravel(List<TravelModel> travelModels) {
        return travelModels.stream().map(Mapper::fromTravel).toList();
    }

    public static TravelDto fromTravel(TravelModel travelModel) {
        var travelDto = new TravelDto();
        travelDto.id = travelModel.getId();
        travelDto.endDate = travelModel.getEndDate();
        travelDto.startDate = travelModel.getStartDate();
        travelDto.status = travelModel.getStatus();
        travelDto.route = fromRoute(travelModel.getRoute());
        travelDto.vehicle = fromVehicle(travelModel.getVehicle());
        if(travelModel.getAvailability() != null) {
            var availabilityDto = new TravelDto.AvailabilityDto();
            availabilityDto.availableSeats = travelModel.getAvailability().getAvailableSeats();
            availabilityDto.requestedDestinyCityId = travelModel.getAvailability().getRequestedDestinyCityId();
            availabilityDto.requestedOriginCityId = travelModel.getAvailability().getRequestedOriginCityId();
            availabilityDto.price = travelModel.getAvailability().getPrice();
            travelDto.availability = availabilityDto;
        }
        return travelDto;
    }

    public static List<VehicleDto> fromVehicle(List<VehicleModel> vehicleModels) {
        return vehicleModels.stream().map(Mapper::fromVehicle).toList();
    }

    public static VehicleDto fromVehicle(VehicleModel vehicleModel) {
        var vehicleDto = new VehicleDto();
        vehicleDto.id = vehicleModel.getId();
        vehicleDto.licensePlate = vehicleModel.getLicensePlate();
        vehicleDto.model = vehicleModel.getModel();
        vehicleDto.capacity = vehicleModel.getCapacity();
        vehicleDto.lastMaintenance = vehicleModel.getLastMaintenance();
        return vehicleDto;
    }

    public static List<RouteDto> fromRoute(List<RouteModel> routes) {
        return routes.stream().map(Mapper::fromRoute).toList();
    }

    public static RouteDto fromRoute(RouteModel route) {
        var routeDto = new RouteDto();
        routeDto.id = route.getId();
        routeDto.distance = route.getDistance();
        routeDto.basePrice = route.getBasePrice();
        var originCity = new CityDto();
        originCity.id = route.getOriginCity().getId();
        originCity.name = route.getOriginCity().getName();
        originCity.uf = route.getOriginCity().getUf();
        routeDto.originCity = originCity;
        var destinyCity = new CityDto();
        destinyCity.id = route.getDestinyCity().getId();
        destinyCity.name = route.getDestinyCity().getName();
        destinyCity.uf = route.getDestinyCity().getUf();
        routeDto.destinyCity = destinyCity;
        routeDto.routeStops = route.getRouteStops().stream().map(routeStop -> {
            var routeStopDto = new RouteDto.RouteStop();
            routeStopDto.id = routeStop.getId();
            routeStopDto.stopOrder = routeStop.getStopOrder();
            routeStopDto.distanceFromOrigin = routeStop.getDistanceFromOrigin();
            var routeStopCity = new CityDto();
            routeStopCity.id = routeStop.getCity().getId();
            routeStopCity.name = routeStop.getCity().getName();
            routeStopCity.uf = routeStop.getCity().getUf();
            routeStopDto.city = routeStopCity;
            return routeStopDto;
        }).toList();
        return routeDto;
    }
}
