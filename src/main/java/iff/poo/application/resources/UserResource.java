package iff.poo.application.resources;

import iff.poo.application.dto.UserDto;
import iff.poo.core.user.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
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

    @POST
    public Response createUser(UserDto userDto) {
        try {
            Long generatedId = userService.createUser(userDto.name, userDto.email, userDto.password, "traveler");
            return Response.status(201).entity(Map.of("id", generatedId)).build();
        } catch (Exception ex) {
            LOG.error(ex);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/teste-role")
    @RolesAllowed("traveler")
    public Response testeUser(@Context SecurityContext ctx) {
        return Response.ok().entity(Map.of("Hello", ctx.getUserPrincipal() != null ? ctx.getUserPrincipal().getName() : "World")).build();
    }
}
