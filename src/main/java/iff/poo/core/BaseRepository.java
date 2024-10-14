package iff.poo.core;

import java.util.List;

public abstract interface BaseRepository<T> {
    public T getById(int id);
    public Long create(T object);
    public List<T> getAll();
    public boolean updateById(T object, Long id);
}
