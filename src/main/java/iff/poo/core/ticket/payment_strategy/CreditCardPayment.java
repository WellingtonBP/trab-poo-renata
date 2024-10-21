package iff.poo.core.ticket.payment_strategy;

import iff.poo.core.ticket.TicketModel;

public class CreditCardPayment implements PaymentStrategy {

    @Override
    public void processPayment(TicketModel.PaymentModel payment) {
        System.out.println("Processing credit card payment for user: " + payment.getUser().getName());
        String cardNumber = (String) payment.getMeta().get("cardNumber");
        String cvv = (String) payment.getMeta().get("cvv");
        System.out.println("Card number: "+cardNumber);
        System.out.println("Card cvv: "+cvv);
    }
}