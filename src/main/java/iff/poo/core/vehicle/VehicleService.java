package iff.poo.core.vehicle;

import iff.poo.core.exceptions.AuthException;
import iff.poo.core.user.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class VehicleService {
    @Inject
    VehicleRepo vehicleRepo;

    @Inject
    UserService userService;

    public Long createVehicle(String licensePlate, String model, int capacity, LocalDate lastMaintenance, String currUserEmail) throws AuthException {
        var currUser = userService.getUserByEmail(currUserEmail);
        if(currUser == null || !currUser.getType().equals("admin")) {
            throw new AuthException("Only admins can create vehicles", 403);
        }

        var vehicle = new VehicleModel();
        vehicle.setLicensePlate(licensePlate);
        vehicle.setModel(model);
        vehicle.setCapacity(capacity);
        vehicle.setLastMaintenance(lastMaintenance);

        return vehicleRepo.create(vehicle);
    }

    public List<VehicleModel> getVehicles() {
        return vehicleRepo.getAll();
    }
}
