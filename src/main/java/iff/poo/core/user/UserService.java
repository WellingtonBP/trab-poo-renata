package iff.poo.core.user;

import iff.poo.core.exceptions.AuthException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserService {
    @Inject
    UserRepo userRepo;

    public Long createUser(String name, String email, String password, String type, String currUserEmail) throws AuthException {
        if(type.equals("admin")) {
            var currUser = getUserByEmail(currUserEmail);
            if(currUser == null || !currUser.getType().equals("admin")) {
                throw new AuthException("Admin users can be create by another admin only", 403);
            }
        }

        var user = new UserModel();
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
