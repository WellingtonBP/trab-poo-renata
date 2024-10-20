package iff.poo.config;

import iff.poo.core.city.CityRepo;
import iff.poo.core.route.RouteRepo;
import iff.poo.core.user.UserRepo;
import iff.poo.infra.database.repositories.CityRepoImpl;
import iff.poo.infra.database.repositories.RouteRepoImpl;
import iff.poo.infra.database.repositories.UserRepoImpl;
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
}
