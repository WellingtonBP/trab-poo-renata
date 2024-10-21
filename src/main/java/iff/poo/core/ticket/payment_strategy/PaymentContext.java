package iff.poo.core.ticket.payment_strategy;

import iff.poo.core.ticket.TicketModel;

public class PaymentContext {
    private PaymentStrategy strategy;

    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public void processPayment(TicketModel.PaymentModel payment) {
        if (strategy != null) {
            strategy.processPayment(payment);
        } else {
            throw new IllegalStateException("Payment strategy not set.");
        }
    }
}
