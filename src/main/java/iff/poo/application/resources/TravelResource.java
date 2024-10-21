package iff.poo.application.resources;

import iff.poo.application.dto.TravelDto;
import iff.poo.application.util.Mapper;
import iff.poo.core.exceptions.AuthException;
import iff.poo.core.travel.TravelService;
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

@Path("/travel")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class TravelResource {
    private static final Logger LOG = Logger.getLogger(TravelResource.class);

    @Inject
    TravelService travelService;

    @Inject
    JsonWebToken jwt;
    @Inject
    @Claim(standard = Claims.sub)
    String tokenSub;

    @POST
    @RolesAllowed("admin")
    public Response createTravel(@Context SecurityContext ctx, TravelDto travel) {
        try {
            Long generatedId = travelService.createTravel(travel.route.id, travel.startDate, travel.endDate, travel.status, travel.vehicle.id, tokenSub);

            return Response.status(201).entity(Map.of("id", generatedId)).build();
        } catch (AuthException aex) {
            return Response.status(aex.getHttpStatusCode()).entity(Map.of("message", aex.getMessage())).build();
        } catch (Exception ex) {
            LOG.error(ex);
            return Response.serverError().build();
        }
    }

    @GET
    public Response getTravel(@QueryParam("cidade_origem_id") Long originCityId, @QueryParam("cidade_destino_id") Long destinyCityId) {
        try {
            var travels = Mapper.fromTravel(travelService.getTravelsByCities(originCityId, destinyCityId));

            return Response.ok().entity(travels).build();
        } catch (Exception ex) {
            LOG.error(ex);
            return Response.serverError().build();
        }
    }
}
