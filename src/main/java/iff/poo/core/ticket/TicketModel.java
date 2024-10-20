package iff.poo.core.ticket;

import iff.poo.core.payment.PaymentModel;
import iff.poo.core.route.RouteModel;
import iff.poo.core.travel.TravelModel;
import iff.poo.core.user.UserModel;
import lombok.Data;

@Data
public class TicketModel {
    private Long id;
    private TravelModel travel;
    private RouteModel.RouteStop originRouteStop;
    private RouteModel.RouteStop destinyRouteStop;
    private UserModel user;
    private int seatNumber;
    private PaymentModel payment;
}
