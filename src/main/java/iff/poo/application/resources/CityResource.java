package iff.poo.application.resources;

import iff.poo.application.dto.CityDto;
import iff.poo.core.city.CityService;
import iff.poo.core.exceptions.AuthException;
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

@Path("/city")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class CityResource {
    private static final Logger LOG = Logger.getLogger(CityResource.class);

    @Inject
    CityService cityService;

    @Inject
    JsonWebToken jwt;
    @Inject
    @Claim(standard = Claims.sub)
    String tokenSub;

    @POST
    @RolesAllowed("admin")
    public Response createCity(@Context SecurityContext ctx, CityDto city) {
        try {
            var generatedId = cityService.createCity(city.name, city.uf, tokenSub);
            return Response.status(201).entity(Map.of("id", generatedId)).build();
        } catch (AuthException aex) {
            return Response.status(aex.getHttpStatusCode()).entity(Map.of("message", aex.getMessage())).build();
        } catch (Exception ex) {
            LOG.error(ex);
            return Response.serverError().build();
        }
    }

    @GET
    public Response getCities() {
        try {
            var cities = cityService.getCities().stream().map(city -> {
                var cityDto = new CityDto();
                cityDto.id = city.getId();
                cityDto.name = city.getName();
                cityDto.uf = city.getUf();
                return cityDto;
            });
            return Response.ok().entity(cities).build();
        } catch (Exception ex) {
            LOG.error(ex);
            return Response.serverError().build();
        }
    }
}
