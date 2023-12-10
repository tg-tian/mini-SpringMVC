package org.tg.web;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractRefreshableConfigApplicationContext;
import org.springframework.util.ObjectUtils;
import org.tg.web.context.AbstractRefreshableWebApplicationContext;
import org.tg.web.context.AnnotationConfigWebApplicationContext;
import org.tg.web.context.WebApplicationContext;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

public abstract class AbstractDispatcherServletInitializer implements WebApplicationInitializer{

    public static final String DEFAULT_SERVELET_NAME = "dispatcher";

    public static final String DEFAULT_FILTER_NAME = "filters";

    public static final int M = 1024*1024;

    //根容器、子容器实例化，创建dispatcherServlet注册到servletContext中
    @Override
    public void onStartUp(ServletContext servletContext) {

        ApplicationContext rootApplicaionContext = createRootApplicaionContext();
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,rootApplicaionContext);

        rootApplicaionContext = (AbstractRefreshableWebApplicationContext) rootApplicaionContext;

        ((AbstractRefreshableWebApplicationContext) rootApplicaionContext).refresh();

        WebApplicationContext webApplicationContext = createWebApplicationConext();
        DispatchServlet dispatchServlet = new DispatchServlet(webApplicationContext);
        ServletRegistration.Dynamic dynamic = servletContext.addServlet(DEFAULT_SERVELET_NAME, dispatchServlet);

        dynamic.setLoadOnStartup(1);
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(null,5*M,5*M,5);
        dynamic.setMultipartConfig(multipartConfigElement);
        dynamic.addMapping(getMapping());
        Filter[] filters = getFilters();
        if(!ObjectUtils.isEmpty(filters)){
            for (Filter filter : filters) {
                servletContext.addFilter(DEFAULT_FILTER_NAME,filter);
            }
        }



    }

    //过滤器
    protected abstract Filter[] getFilters();

    //映射器
    protected  String[] getMapping(){
        return new String[]{"/"};
    }

    //创建根容器留给子类实现
    protected abstract ApplicationContext createRootApplicaionContext();
    //创建子容器
    protected abstract WebApplicationContext createWebApplicationConext();

    //获取根容器要管理的bean
    protected abstract Class<?>[] getRootConfigClasses();

    //获取子容器要管理的bean
    protected abstract Class<?>[] getWebConfigClasses();
}

