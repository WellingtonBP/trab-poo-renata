package iff.poo.core.ticket;

import iff.poo.core.exceptions.AuthException;
import iff.poo.core.exceptions.InvalidDataProvidedException;
import iff.poo.core.ticket.payment_strategy.PaymentProcessor;
import iff.poo.core.travel.TravelService;
import iff.poo.core.user.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TicketService {
    @Inject
    TicketRepo ticketRepo;

    @Inject
    TravelService travelService;

    @Inject
    UserService userService;

    @Inject
    PaymentProcessor paymentProcessor;

    public Long createTicket(
            Long travelId,
            Long originRouteStopId,
            Long destinyRouteStopId,
            int seatNumber,
            String paymentType,
            String paymentStatus,
            Map<String, Object> paymentMeta,
            String currUserEmail
    ) throws AuthException, InvalidDataProvidedException {
        var currUser = userService.getUserByEmail(currUserEmail);
        if(currUser == null || !currUser.getType().equals("traveler")) {
            throw new AuthException("Only travelers can create ticket", 403);
        }

        if(!travelService.checkAvailability(travelId, originRouteStopId, destinyRouteStopId, seatNumber)) {
            throw new InvalidDataProvidedException("Seat already taken");
        }

        var ticket = new TicketModel();
        ticket.setTravelId(travelId);
        ticket.setOriginRouteStopId(originRouteStopId);
        ticket.setDestinyRouteStopId(destinyRouteStopId);
        ticket.setUserId(currUser.getId());
        ticket.setSeatNumber(seatNumber);
        var payment = new TicketModel.PaymentModel();
        payment.setType(paymentType);
        payment.setStatus(paymentStatus);
        payment.setMeta(paymentMeta);
        payment.setUser(currUser);
        ticket.setPayment(payment);

        paymentProcessor.processPayment(payment);

        return ticketRepo.create(ticket);
    }

    public List<TicketModel> getTicketsByTravelId(Long id) {
        return ticketRepo.getTicketsByTravelId(id);
    }
}
