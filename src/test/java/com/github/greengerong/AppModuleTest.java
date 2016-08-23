package com.github.greengerong;

import com.github.greengerong.app.AppModule;
import com.github.greengerong.item.ItemService;
import com.github.greengerong.item.ItemServiceImpl1;
import com.github.greengerong.item.ItemServiceImpl2;
import com.github.greengerong.named.NamedService;
import com.github.greengerong.named.NamedServiceImpl1;
import com.github.greengerong.named.NamedServiceImpl2;
import com.github.greengerong.order.Order;
import com.github.greengerong.order.OrderService;
import com.github.greengerong.order.OrderServiceImpl;
import com.github.greengerong.price.PriceService;
import com.github.greengerong.runtime.RuntimeService;
import com.github.greengerong.runtime.RuntimeServiceImpl;
import com.google.common.collect.Lists;
import com.google.inject.*;
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
        injector = Guice.createInjector(Guice.createInjector(Stage.DEVELOPMENT).getInstance(AppModule.class));
    }

    @Test
    public void basicInjectionTest() throws Exception {
        /**
         * 由于OrderService的实例在Module中绑定时被定义为SINGLETON,
         * 所以injector获取到的实例为同一个
         */
        final OrderService instance0 = injector.getInstance(OrderService.class);
        final OrderService instance1 = injector.getInstance(OrderService.class);
        //then
        assertThat(instance0, is(instanceOf(OrderServiceImpl.class)));
        assertThat(instance0, is(instanceOf(OrderService.class)));
        assertSame(instance0, instance1);

        /**
         * OrderServiceImpl的构造方法被{@link com.google.inject.Inject}注释,
         * 因此Injector会自动注入依赖
         * 由于ItemService绑定了Multibinder,所以自动创建Set<ItemService>实例
         *
         */
        final Set<ItemService> itemServiceSet = ((OrderServiceImpl) instance0).getItemServices();
        final List<ItemService> itemServices = Lists.newArrayList(itemServiceSet);
        assertThat(itemServices.size(), is(2));
        assertThat(itemServices.get(0), is(instanceOf(ItemServiceImpl1.class)));
        assertThat(itemServices.get(0), is(instanceOf(ItemService.class)));
        assertThat(itemServices.get(1), is(instanceOf(ItemServiceImpl2.class)));
        assertThat(((OrderServiceImpl) instance0).getPriceService(), is(instanceOf(PriceService.class)));
        instance0.add(new Order(100));
    }

    @Test
    public void keyInjectionTest() throws Exception {

        /**
         * Key<T>的继承式匿名内部类
         * Key中保存了要注入类的类型
         */
        final Key<Set<ItemService>> key = new Key<Set<ItemService>>() {
        };

        //injector还可以通过Key<T>获取实例
        final Set<ItemService> itemServiceSet = injector.getInstance(key);

        final List<ItemService> itemServiceList = Lists.newArrayList(itemServiceSet);

        //then
        assertThat(itemServiceList.size(), is(2));
        assertThat(itemServiceList.get(0), is(instanceOf(ItemServiceImpl1.class)));
        assertThat(itemServiceList.get(1), is(instanceOf(ItemServiceImpl2.class)));
    }

    @Test
    public void bindToInstanceTest() throws Exception {

        final RuntimeService instance = injector.getInstance(new Key<RuntimeService>() {
        });

        assertThat(instance, is(instanceOf(RuntimeServiceImpl.class)));
    }

    @Test
    public void singletonTest() throws Exception {

        // PriceService在Module中被定义为Singleton
        final PriceService first = injector.getInstance(PriceService.class);
        final PriceService second = injector.getInstance(PriceService.class);

        assertThat(first, is(sameInstance(second)));
    }

    @Test
    public void should_get_named_service_with_Provides_bean() throws Exception {
        /**
         * injector需要List<NamedService>的实例,Module中并没有绑定,
         * 但却在{@link com.google.inject.Provides}中提供.
         */
        final List<NamedService> namedServices = injector.getInstance(new Key<List<NamedService>>() {
        });

        assertThat(namedServices.size(), is(2));
        assertThat(namedServices.get(0), is(instanceOf(NamedServiceImpl1.class)));
        assertThat(namedServices.get(1), is(instanceOf(NamedServiceImpl2.class)));
    }
}