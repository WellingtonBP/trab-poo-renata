package iff.poo.core.ticket.payment_strategy;

import iff.poo.core.ticket.TicketModel;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PaymentProcessor {
    public void processPayment(TicketModel.PaymentModel payment) {
        PaymentContext context = new PaymentContext();

        switch (payment.getType().toLowerCase()) {
            case "credit_card":
                context.setPaymentStrategy(new CreditCardPayment());
                break;
            case "boleto":
                context.setPaymentStrategy(new BoletoPayment());
                break;
            case "pix":
                context.setPaymentStrategy(new PixPayment());
                break;
            default:
                throw new IllegalArgumentException("Unknown payment type: " + payment.getType());
        }
        context.processPayment(payment);
    }
}

