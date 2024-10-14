package iff.poo.core.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import lombok.Data;

@Data
public class UserModel {
    private Long id;
    private String name;
    private String email;
    private String password_hash;
    private String type;

    public boolean checkPassword(String password) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), password_hash);
        return result.verified;
    }

    public void setPasswordHashFromPassword(String password) {
        this.password_hash = BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }
}
