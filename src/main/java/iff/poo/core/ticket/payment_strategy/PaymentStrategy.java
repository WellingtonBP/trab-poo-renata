package iff.poo.core.ticket.payment_strategy;

import iff.poo.core.ticket.TicketModel;

public interface PaymentStrategy {
    void processPayment(TicketModel.PaymentModel payment);
}
