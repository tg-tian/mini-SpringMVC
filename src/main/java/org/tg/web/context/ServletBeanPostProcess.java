package org.tg.web.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

public class ServletBeanPostProcess implements BeanPostProcessor {
    private ServletContext servletContext;
    private ServletConfig servletConfig;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean != null && bean instanceof ServletContextAware){
          ((ServletContextAware) bean).setServletContext(this.servletContext);

        }
        if(bean != null && bean instanceof ServletConfigAware){
            ((ServletConfigAware)bean).setServletConfig(this.servletConfig);

        }
        return null;
    }

    public ServletBeanPostProcess(ServletContext servletContext, ServletConfig servletConfig) {

        this.servletContext = servletContext;
        this.servletConfig = servletConfig;


    }
}
