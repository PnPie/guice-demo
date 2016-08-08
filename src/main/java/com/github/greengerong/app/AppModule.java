package com.github.greengerong.app;

import com.github.greengerong.item.ItemService;
import com.github.greengerong.item.ItemServiceImpl1;
import com.github.greengerong.item.ItemServiceImpl2;
import com.github.greengerong.named.NamedService;
import com.github.greengerong.named.NamedServiceImpl1;
import com.github.greengerong.named.NamedServiceImpl2;
import com.github.greengerong.order.OrderService;
import com.github.greengerong.order.OrderServiceImpl;
import com.github.greengerong.price.PriceService;
import com.github.greengerong.runtime.RuntimeService;
import com.github.greengerong.runtime.RuntimeServiceImpl;
import com.google.inject.*;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.github.greengerong.app.ExceptionMethodInterceptor.exception;
import static com.google.common.collect.ImmutableList.of;
import static com.google.inject.matcher.Matchers.any;

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
public class AppModule extends AbstractModule {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppModule.class);
    private final RuntimeServiceImpl runtimeService;

    /**
     * AppModule类对象对RuntimeServiceImpl实例的依赖可以通过Guice的Injector自动创建加载
     *
     * @param runtimeService
     */
    @Inject
    public AppModule(RuntimeServiceImpl runtimeService) {
        this.runtimeService = runtimeService;
    }

    @Override
    public void configure() {
        final Binder binder = binder();
        if (LOGGER.isDebugEnabled()) {
            binder.bindInterceptor(any(), any(), exception());
        }
        //TODO: bind interface
        //在整个应用程序生命周期,OrderService的实例为单例
        binder.bind(OrderService.class).to(OrderServiceImpl.class).in(Scopes.SINGLETON);

        //TODO: bind self class(without interface or base class)
        //直接绑定一个类,让Guice可以注入,且为单例!!!
        binder.bind(PriceService.class).in(Scopes.SINGLETON);

        //TODO: Multibinder
        // A multibinder binds the Set of implementations to the given type(interface)
        final Multibinder<ItemService> itemServiceMultibinder = Multibinder.newSetBinder(binder, ItemService.class);
        itemServiceMultibinder.addBinding().to(ItemServiceImpl1.class);
        itemServiceMultibinder.addBinding().to(ItemServiceImpl2.class);

        //TODO: bind to instance not class.
        binder.bind(RuntimeService.class).toInstance(runtimeService);

        //TODO: bind named instance;
        binder.bind(NamedService.class).annotatedWith(Names.named("impl1")).to(NamedServiceImpl1.class);
        binder.bind(NamedService.class).annotatedWith(Names.named("impl2")).to(NamedServiceImpl2.class);
    }

    /**
     * {@link Provides}注释的作用: 当{@link Injector}需要List<NamedService>实例时,
     * 调用此方法,获取对象
     * @param nameService1
     * @param nameService2
     * @return
     */
    @Provides
    public List<NamedService> getAllItemServices(@Named("impl1") NamedService nameService1,
                                                 @Named("impl2") NamedService nameService2) {
        return of(nameService1, nameService2);
    }


}
