package org.tg.web.context;

import org.springframework.beans.factory.Aware;

import javax.servlet.ServletContext;

//实现该接口的类获取ServletCotext
public interface ServletContextAware extends Aware {
    void setServletContext(ServletContext servletContext);
}
