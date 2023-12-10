package org.tg.web.context;

import org.springframework.beans.factory.Aware;

import javax.servlet.ServletConfig;

//实现该接口的类获取ServletConfig
public interface ServletConfigAware extends Aware {
    void setServletConfig(ServletConfig servletConfig);
}
