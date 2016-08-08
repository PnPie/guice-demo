package com.github.greengerong;

import com.github.greengerong.app.AppModule;
import com.github.greengerong.item.ItemService;
import com.github.greengerong.item.ItemServiceImpl1;
import com.github.greengerong.item.ItemServiceImpl2;
import com.github.greengerong.named.NamedService;
import com.github.greengerong.order.Order;
import com.github.greengerong.order.OrderService;
import com.github.greengerong.order.OrderServiceImpl;
import com.github.greengerong.price.PriceService;
import com.github.greengerong.runtime.RuntimeService;
import com.github.greengerong.runtime.RuntimeServiceImpl;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

public class AppModuleTest {

    private Injector injector;

    /**
     * 执行每一个{@link Test}方法之前执行{@link Before}
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        injector = Guice.createInjector(Guice.createInjector().getInstance(AppModule.class));
    }

    @Test
    public void should_get_order_service_from_guice_module() throws Exception {
        /**
         * 由于OrderService的实例在Module中绑定时被定义为SINGLETON,
         * 所以injector获取到的实例为同一个
         */
        final OrderService instance0 = injector.getInstance(OrderService.class);
        final OrderService instance1 = injector.getInstance(OrderService.class);
        //then
        assertThat(instance0, is(instanceOf(OrderServiceImpl.class)));
        assertSame(instance0, instance1);

        final List<ItemService> itemServices = Lists.newArrayList(((OrderServiceImpl) instance0).getItemServices());
        assertThat(itemServices.get(0), is(instanceOf(ItemServiceImpl1.class)));
        assertThat(itemServices.get(1), is(instanceOf(ItemServiceImpl2.class)));
        assertThat(((OrderServiceImpl) instance0).getPriceService(), is(instanceOf(PriceService.class)));
        instance0.add(new Order(100));
    }

    @Test
    public void should_get_all_item_service() throws Exception {
        //given

        //when
        final List<ItemService> instance = Lists.newArrayList(
                injector.getInstance(new Key<Set<ItemService>>() {
                })
        );
        //then
        assertThat(instance.size(), is(2));
        assertThat(instance.get(0), is(instanceOf(ItemServiceImpl1.class)));
        assertThat(instance.get(1), is(instanceOf(ItemServiceImpl2.class)));
    }

    @Test
    public void should_register_service_runtime() throws Exception {
        //given

        //when
        final RuntimeService instance = injector.getInstance(RuntimeService.class);
        //then

        assertThat(instance, is(instanceOf(RuntimeServiceImpl.class)));
    }

    @Test
    public void should_be_singleton_for_one_without_interface_bean() throws Exception {
        //given

        //when
        final PriceService first = injector.getInstance(PriceService.class);
        final PriceService second = injector.getInstance(PriceService.class);
        //then

        assertThat(first, is(sameInstance(second)));
    }

    @Test
    public void should_get_named_service_with_Provides_bean() throws Exception {
        //given

        //when
        final List<NamedService> namedServices = injector.getInstance(new Key<List<NamedService>>() {
        });
        //then

        assertThat(namedServices.size(), is(2));
    }
}