package iff.poo.core.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserService {
    @Inject
    UserRepo userRepo;

    public Long createUser(String name, String email, String password, String type) {
        UserModel user = new UserModel();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHashFromPassword(password);
        user.setType(type);
        return userRepo.create(user);
    }

    public UserModel getUserByEmail(String email) {
        return userRepo.getByEmail(email);
    }
}
