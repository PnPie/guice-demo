package com.github.greengerong.order;


import com.google.inject.Singleton;

public interface OrderService {

    void add(Order order);

    void remove(Order order);

    Order get(int id);
}
