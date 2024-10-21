package iff.poo.application.resources;

import iff.poo.application.dto.TicketDto;
import iff.poo.core.exceptions.AuthException;
import iff.poo.core.exceptions.InvalidDataProvidedException;
import iff.poo.core.ticket.TicketService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import java.util.Map;

@Path("/ticket")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class TicketResource {
    private static final Logger LOG = Logger.getLogger(TicketResource.class);

    @Inject
    TicketService ticketService;

    @Inject
    JsonWebToken jwt;
    @Inject
    @Claim(standard = Claims.sub)
    String tokenSub;

    @POST
    @RolesAllowed("traveler")
    public Response createTicker(@Context SecurityContext ctx, TicketDto ticket) {
        try {
            Long generatedId = ticketService.createTicket(
                ticket.travelId,
                ticket.originRouteStopId,
                ticket.destinyRouteStopId,
                ticket.seatNumber,
                ticket.payment.type,
                ticket.payment.status,
                ticket.payment.meta,
                tokenSub
            );
            return Response.status(201).entity(Map.of("id", generatedId)).build();
        } catch (AuthException aex) {
            return Response.status(aex.getHttpStatusCode()).entity(Map.of("message", aex.getMessage())).build();
        } catch (InvalidDataProvidedException ide) {
            return Response.status(422).entity(Map.of("message", ide.getMessage())).build();
        } catch (Exception ex) {
            LOG.error(ex);
            return Response.serverError().build();
        }
    }
}
