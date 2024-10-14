package iff.poo.core.auth;

import iff.poo.core.exceptions.AuthException;
import iff.poo.core.user.UserService;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.Claims;

import java.util.Collections;
import java.util.HashSet;

@ApplicationScoped
public class AuthService {
    @Inject
    UserService userService;

    public String generateToken(String userEmail, String userPassword) throws AuthException {
        var user = userService.getUserByEmail(userEmail);
        if(user == null) {
            throw new AuthException("User not found", 404);
        }
        if(!user.checkPassword(userPassword)) {
            throw new AuthException("Invalid password", 401);
        }
        return Jwt
                .upn(user.getType())
                .groups(new HashSet<>(Collections.singletonList(user.getType())))
                .claim(Claims.sub.name(), user.getEmail())
                .sign();
    }
}
