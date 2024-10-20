package iff.poo.core.travel;

import iff.poo.core.user.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TravelService {
    @Inject
    TravelRepo travelRepo;

    @Inject
    UserService userService;
}
