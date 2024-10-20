package iff.poo.core.payment;

import iff.poo.core.user.UserModel;
import lombok.Data;

@Data
public class PaymentModel {
    private Long id;
    private String type;
    private String status;
    private String meta;
    private UserModel user;
}
