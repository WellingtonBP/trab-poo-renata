package iff.poo.application.resources;

import iff.poo.application.dto.RouteDto;
import iff.poo.application.util.Mapper;
import iff.poo.core.exceptions.AuthException;
import iff.poo.core.route.RouteService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Path("/route")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class RouteResource {
    private static final Logger LOG = Logger.getLogger(RouteResource.class);

    @Inject
    RouteService routeService;

    @Inject
    JsonWebToken jwt;
    @Inject
    @Claim(standard = Claims.sub)
    String tokenSub;

    @POST
    @RolesAllowed("admin")
    public Response createRoute(@Context SecurityContext ctx, RouteDto routeDto) {
        try {
            List<Long> routeStopsCitiesIds = new ArrayList<>();
            List<Integer> routeStopsStopOrders = new ArrayList<>();
            List<Double> routeStopsDistances = new ArrayList<>();

            for(var routeStop : routeDto.routeStops) {
                routeStopsCitiesIds.add(routeStop.city.id);
                routeStopsStopOrders.add(routeStop.stopOrder);
                routeStopsDistances.add(routeStop.distanceFromOrigin);
            }

            Long generatedId = routeService.createRoute(
                    routeDto.originCity.id,
                    routeDto.destinyCity.id,
                    routeDto.distance,
                    routeDto.basePrice,
                    routeDto.routeStops.size(),
                    routeStopsCitiesIds,
                    routeStopsStopOrders,
                    routeStopsDistances,
                    tokenSub
            );
            return Response.status(201).entity(Map.of("id", generatedId)).build();
        } catch (AuthException aex) {
            return Response.status(aex.getHttpStatusCode()).entity(Map.of("message", aex.getMessage())).build();
        } catch (Exception ex) {
            LOG.error(ex);
            return Response.serverError().build();
        }
    }

    @GET
    @RolesAllowed("admin")
    public Response getRoutes(@Context SecurityContext ctx) {
        try {
            var routes = Mapper.fromRoute(routeService.getRoutes(tokenSub));

            return Response.ok().entity(routes).build();
        } catch (AuthException aex) {
            return Response.status(aex.getHttpStatusCode()).entity(Map.of("message", aex.getMessage())).build();
        } catch (Exception ex) {
            LOG.error(ex);
            return Response.serverError().build();
        }
    }
}
