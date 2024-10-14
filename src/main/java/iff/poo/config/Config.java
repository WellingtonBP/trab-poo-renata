package iff.poo.config;

import iff.poo.core.user.UserRepo;
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
}
