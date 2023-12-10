package org.tg.web.support;

import org.springframework.context.annotation.Bean;
import org.tg.web.adapter.HandlerMethodAdapter;
import org.tg.web.adapter.RequestMappingHandlerMethodAdapter;
import org.tg.web.handler.HandlerMapping;
import org.tg.web.handler.RequestMappingHandlerMapping;
import org.tg.web.intercpetor.HandlerInterceptor;
import org.tg.web.intercpetor.InterceptorRegistry;
import org.tg.web.intercpetor.MappedInterceptor;
import org.tg.web.resolver.DefaultHandlerExceptionResolver;
import org.tg.web.resolver.ExceptionHandlerExceptionResolver;
import org.tg.web.resolver.HandlerExceptionResolver;

import java.util.List;

public abstract class WebMvcConfigurationSupport {

    @Bean
    public HandlerMapping handlerMapping(){
        RequestMappingHandlerMapping requestMappingHandlerMapping = new RequestMappingHandlerMapping();

        requestMappingHandlerMapping.setOrder(0);

        InterceptorRegistry registry = new InterceptorRegistry();

        getInterceptors(registry);

        List<MappedInterceptor> interceptors = registry.getInterceptors();

        requestMappingHandlerMapping.addHandlerInterceptors(interceptors);

        return requestMappingHandlerMapping;
    }

    protected abstract void getInterceptors(InterceptorRegistry registry);
    @Bean
    public HandlerMethodAdapter handlerMethodAdapter(){
        RequestMappingHandlerMethodAdapter requestMappingHandlerMethodAdapter = new RequestMappingHandlerMethodAdapter();


        return requestMappingHandlerMethodAdapter;
    }

    @Bean
    public HandlerExceptionResolver exceptionHandlerExceptionResolver(){
        ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver = new ExceptionHandlerExceptionResolver();
        exceptionHandlerExceptionResolver.setOrder(0);
        return exceptionHandlerExceptionResolver;
    }

    @Bean
    public HandlerExceptionResolver exceptionResolver(){
        DefaultHandlerExceptionResolver defaultHandlerExceptionResolver = new DefaultHandlerExceptionResolver();
        defaultHandlerExceptionResolver.setOrder(1);
        return defaultHandlerExceptionResolver;
    }

    protected abstract void addInterceptor(InterceptorRegistry registry);
}
