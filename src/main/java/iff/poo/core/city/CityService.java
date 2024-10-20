package iff.poo.core.city;

import iff.poo.core.exceptions.AuthException;
import iff.poo.core.user.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class CityService {
    @Inject
    CityRepo cityRepo;

    @Inject
    UserService userService;

    public Long createCity(String name, String uf, String currUserEmail) throws AuthException {
        var currUser = userService.getUserByEmail(currUserEmail);
        if(currUser == null || !currUser.getType().equals("admin")) {
            throw new AuthException("Only admins can create cities", 403);
        }

        var city = new CityModel();
        city.setName(name);
        city.setUf(uf);
        return cityRepo.create(city);
    }

    public List<CityModel> getCities() {
        return cityRepo.getAll();
    }
}
