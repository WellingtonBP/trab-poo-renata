package iff.poo.application.resources;

import iff.poo.application.dto.VehicleDto;
import iff.poo.application.util.Mapper;
import iff.poo.core.exceptions.AuthException;
import iff.poo.core.vehicle.VehicleService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import java.util.Map;

@Path("/vehicle")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class VehicleResource {
    private static final Logger LOG = Logger.getLogger(VehicleResource.class);

    @Inject
    VehicleService vehicleService;

    @Inject
    JsonWebToken jwt;
    @Inject
    @Claim(standard = Claims.sub)
    String tokenSub;

    @POST
    @RolesAllowed("admin")
    public Response createVehicle(VehicleDto vehicle) {
        try {
            var generatedId = vehicleService.createVehicle(vehicle.licensePlate, vehicle.model, vehicle.capacity, vehicle.lastMaintenance, tokenSub);
            return Response.status(201).entity(Map.of("id", generatedId)).build();
        } catch (AuthException aex) {
            return Response.status(aex.getHttpStatusCode()).entity(Map.of("message", aex.getMessage())).build();
        } catch (Exception ex) {
            LOG.error(ex);
            return Response.serverError().build();
        }
    }

    @GET
    public Response getVehicles() {
        try {
            var vehicles = Mapper.fromVehicle(vehicleService.getVehicles());
            return Response.ok().entity(vehicles).build();
        } catch (Exception ex) {
            LOG.error(ex);
            return Response.serverError().build();
        }
    }
}
