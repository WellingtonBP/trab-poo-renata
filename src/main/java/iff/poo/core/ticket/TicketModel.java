package iff.poo.core.ticket;

import iff.poo.core.user.UserModel;
import lombok.Data;

import java.util.Map;

@Data
public class TicketModel {
    private Long id;
    private Long travelId;
    private Long originRouteStopId;
    private Long destinyRouteStopId;
    private Long userId;
    private int seatNumber;
    private PaymentModel payment;

    @Data
    public static class PaymentModel {
        private Long id;
        private String type;
        private String status;
        private Map<String, Object> meta;
        private UserModel user;
    }
}
