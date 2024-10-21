package iff.poo.config;

import iff.poo.core.city.CityRepo;
import iff.poo.core.route.RouteRepo;
import iff.poo.core.ticket.TicketRepo;
import iff.poo.core.travel.TravelRepo;
import iff.poo.core.user.UserRepo;
import iff.poo.core.vehicle.VehicleRepo;
import iff.poo.infra.database.repositories.*;
import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Default;

@Dependent
public class Config {
    @Default
    public UserRepo userRepo(AgroalDataSource dataSource) {
        return new UserRepoImpl(dataSource);
    }

    @Default
    public CityRepo cityRepo(AgroalDataSource dataSource) {
        return new CityRepoImpl(dataSource);
    }

    @Default
    public RouteRepo routeRepo(AgroalDataSource dataSource) {
        return new RouteRepoImpl(dataSource);
    }

    @Default
    public TravelRepo travelRepo(AgroalDataSource dataSource) {
        return new TravelRepoImpl(dataSource);
    }

    @Default
    public VehicleRepo vehicleRepo(AgroalDataSource dataSource) {
        return new VehicleRepoImpl(dataSource);
    }

    @Default
    public TicketRepo ticketRepo(AgroalDataSource dataSource) {
        return new TicketRepoImpl(dataSource);
    }
}
