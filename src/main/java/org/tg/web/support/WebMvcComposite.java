package org.tg.web.support;

import org.tg.web.intercpetor.InterceptorRegistry;

import java.util.ArrayList;
import java.util.List;

public class WebMvcComposite implements WebMvcConfigurer{

    private List<WebMvcConfigurer> webMvcConfigurers = new ArrayList<>();

    public void addWebMvcConfigurer(List<WebMvcConfigurer> webMvcConfigurers){
        this.webMvcConfigurers.addAll(webMvcConfigurers);
    }

    @Override
    public void addInterceptor(InterceptorRegistry registry) {
        for (WebMvcConfigurer webMvcConfigurer : webMvcConfigurers) {
            webMvcConfigurer.addInterceptor(registry);
        }
    }
}
