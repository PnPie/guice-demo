package com.github.greengerong.order;

import com.github.greengerong.item.ItemService;
import com.github.greengerong.price.PriceService;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * ***************************************
 * *
 * Auth: green gerong                     *
 * Date: 2014                             *
 * blog: http://greengerong.github.io/    *
 * github: https://github.com/greengerong *
 * *
 * ****************************************
 */
public class OrderServiceImpl implements OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);
    private Set<ItemService> itemServices;
    private PriceService priceService;

//    public OrderServiceImpl() {
//    }

    /**
     * 如果使用Guice创建对象,则调用{@link Inject}注释的构造函数,因此会自动创建并加载两个依赖
     *
     * @param itemServices ItemService被绑定到了一个Multibinder with a Set of implementations,
     *                     因此Guice的Injector可以自动创建并加载一个Set<ItemService>对象
     * @param priceService 依赖
     */
    @Inject
    public OrderServiceImpl(Set<ItemService> itemServices, PriceService priceService) {
        this.itemServices = itemServices;
        this.priceService = priceService;
    }

    public void add(Order order) {
        for (ItemService item : itemServices) {
            item.get(0);
        }
        priceService.getPrice();
    }

    public void remove(Order order) {
    }

    public Order get(int id) {
        for (ItemService item : itemServices) {
            item.get(id);
        }
        return new Order(id);
    }

    public Set<ItemService> getItemServices() {
        return itemServices;
    }

    public PriceService getPriceService() {
        return priceService;
    }
}
