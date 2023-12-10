package org.tg.web.handler;

import javax.servlet.http.HttpServletRequest;

public class BeanNameUrlHandlerMapping extends AbstractHandlerMapping{
    private int order;
    @Override
    protected HandlerMethod getHandlerInteal(HttpServletRequest request) {
        return null;
    }

    @Override
    protected void detectHandlerMethod(String name) throws Exception {

    }

    @Override
    protected boolean isHandler(Class type) {
        return false;
    }

    @Override
    public void setOrder(int order) {
        this.order = 2;
    }

}
