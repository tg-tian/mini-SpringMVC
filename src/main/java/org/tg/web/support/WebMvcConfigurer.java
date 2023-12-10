package org.tg.web.support;

import org.tg.web.intercpetor.InterceptorRegistry;

public interface WebMvcConfigurer {

    default void addInterceptor(InterceptorRegistry registry){}
}
