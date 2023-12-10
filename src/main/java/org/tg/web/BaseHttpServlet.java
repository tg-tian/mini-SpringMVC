package org.tg.web;

import org.springframework.context.ApplicationContext;
import org.springframework.util.ObjectUtils;
import org.tg.web.context.AbstractRefreshableWebApplicationContext;
import org.tg.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public abstract class BaseHttpServlet extends HttpServlet {
    protected WebApplicationContext webApplicationContext;

    public BaseHttpServlet(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();

        ApplicationContext rootContext = (ApplicationContext) servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        if(!ObjectUtils.isEmpty(webApplicationContext)){

            AbstractRefreshableWebApplicationContext wac = (AbstractRefreshableWebApplicationContext) this.webApplicationContext;

            if(wac.getParent() == null){

                wac.setParent(rootContext);
            }

            wac.setServletContext(servletContext);
            wac.setServletConfig(getServletConfig());

            wac.refresh();
            onRefresh(webApplicationContext);
        }

    }

    protected abstract void onRefresh(WebApplicationContext webApplicationContext);

}
