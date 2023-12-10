package org.tg.web;

import org.springframework.context.ApplicationContext;
import org.springframework.util.ObjectUtils;
import org.tg.web.context.AnnotationConfigWebApplicationContext;
import org.tg.web.context.WebApplicationContext;

import javax.servlet.Filter;

public abstract class AbstractAnnotationConfigDispatcherServletInitializer extends AbstractDispatcherServletInitializer{

    //父容器创建
    @Override
    protected ApplicationContext createRootApplicaionContext() {
        Class<?>[] rootConfigClasser = getRootConfigClasses();
        if(!ObjectUtils.isEmpty(rootConfigClasser)){
            AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
            rootContext.register(rootConfigClasser);
            return rootContext;
        }
        return null;
    }

    //子容器创建
    @Override
    protected WebApplicationContext createWebApplicationConext() {
        Class<?>[] webConfigClasser = getWebConfigClasses();
        if(!ObjectUtils.isEmpty(webConfigClasser)){
            AnnotationConfigWebApplicationContext webContext = new AnnotationConfigWebApplicationContext();
            webContext.register(webConfigClasser);
            return webContext;
        }
        return null;
    }

    @Override
    protected Filter[] getFilters() {
        return new Filter[0];
    }
}
