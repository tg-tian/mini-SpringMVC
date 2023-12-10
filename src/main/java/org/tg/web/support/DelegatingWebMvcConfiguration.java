package org.tg.web.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.tg.web.intercpetor.HandlerInterceptor;
import org.tg.web.intercpetor.InterceptorRegistry;

import java.util.List;

public class DelegatingWebMvcConfiguration extends WebMvcConfigurationSupport{

    private WebMvcComposite webMvcComposite = new WebMvcComposite();


    @Autowired(required = false)
    public void setWebMvcComposite(List<WebMvcConfigurer> webMvcConfigurers) {
        webMvcComposite.addWebMvcConfigurer(webMvcConfigurers);
    }

    @Override
    protected void getInterceptors(InterceptorRegistry registry) {
        addInterceptor(registry);
    }

    @Override
    protected void addInterceptor(InterceptorRegistry registry) {
        webMvcComposite.addInterceptor(registry);
    }
}
