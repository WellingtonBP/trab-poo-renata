package iff.poo.application.resources;

import iff.poo.application.dto.UserDto;
import iff.poo.core.exceptions.AuthException;
import iff.poo.core.user.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import java.util.Map;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class UserResource {
    private static final Logger LOG = Logger.getLogger(UserResource.class);

    @Inject
    UserService userService;

    @Inject
    JsonWebToken jwt;
    @Inject
    @Claim(standard = Claims.sub)
    String tokenSub;

    @POST
    public Response createUser(UserDto user) {
        try {
            var generatedId = userService.createUser(user.name, user.email, user.password, "traveler", null);
            return Response.status(201).entity(Map.of("id", generatedId)).build();
        } catch (Exception ex) {
            LOG.error(ex);
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/admin")
    @RolesAllowed("admin")
    public Response createAdminUser(@Context SecurityContext ctx, UserDto user) {
        try {
            var generatedId = userService.createUser(user.name, user.email, user.password, "admin", tokenSub);
            return Response.status(201).entity(Map.of("id", generatedId)).build();
        } catch (AuthException aex) {
            return Response.status(aex.getHttpStatusCode()).entity(Map.of("message", aex.getMessage())).build();
        } catch (Exception ex) {
            LOG.error(ex);
            return Response.serverError().build();
        }
    }
}
