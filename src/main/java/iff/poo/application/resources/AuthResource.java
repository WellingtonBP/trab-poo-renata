package iff.poo.application.resources;

import iff.poo.application.dto.UserDto;
import iff.poo.core.auth.AuthService;
import iff.poo.core.exceptions.AuthException;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.Map;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    private static final Logger LOG = Logger.getLogger(AuthResource.class);

    @Inject
    AuthService authService;

    @POST
    public Response getToken(UserDto userDto) {
        try {
            String token = authService.generateToken(userDto.email, userDto.password);
            return Response.status(201).entity(Map.of("token", token)).build();
        } catch (AuthException aex) {
            LOG.error(aex);
            return Response.status(aex.getHttpStatusCode()).entity(Map.of("message", aex.getMessage())).build();
        } catch (Exception ex) {
            LOG.error(ex);
            return Response.serverError().build();
        }
    }
}
