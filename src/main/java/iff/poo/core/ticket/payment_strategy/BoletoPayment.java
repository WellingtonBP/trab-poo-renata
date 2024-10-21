package iff.poo.core.ticket.payment_strategy;

import iff.poo.core.ticket.TicketModel;

public class BoletoPayment implements PaymentStrategy {

    @Override
    public void processPayment(TicketModel.PaymentModel payment) {
        System.out.println("Processing boleto payment for user: " + payment.getUser().getName());
        String boletoNumber = (String) payment.getMeta().get("boletoNumber");
        System.out.println("Boleto: "+boletoNumber);
    }
}
