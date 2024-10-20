package iff.poo.core;

import java.util.List;

public interface BaseRepository<T> {
    public T getById(Long id);
    public Long create(T object);
    public List<T> getAll();
    public boolean updateById(T object, Long id);
}
