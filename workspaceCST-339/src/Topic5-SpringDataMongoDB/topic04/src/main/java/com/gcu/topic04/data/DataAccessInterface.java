package com.gcu.topic04.data;

import java.util.List;

public interface DataAccessInterface<T> {

    public List<T> findAll();

    public T findById(String id);

    public T create(T t);

    public boolean update(T t);

    public boolean delete(T t);
}