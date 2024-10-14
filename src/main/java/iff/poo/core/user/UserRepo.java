package iff.poo.core.user;

import iff.poo.core.BaseRepository;

public abstract class UserRepo implements BaseRepository<UserModel> {
    public UserModel getByEmail(String email) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
