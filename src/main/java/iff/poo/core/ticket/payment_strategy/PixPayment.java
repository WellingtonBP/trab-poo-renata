package iff.poo.core.ticket.payment_strategy;

import iff.poo.core.ticket.TicketModel;

public class PixPayment implements PaymentStrategy {
    @Override
    public void processPayment(TicketModel.PaymentModel payment) {
        System.out.println("Processing PIX payment for user: " + payment.getUser().getName());
        String pixKey = (String) payment.getMeta().get("pixKey");
        System.out.println("Pix: "+pixKey);
    }
}
