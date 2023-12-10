package org.tg.web.adapter;

import org.springframework.core.Ordered;
import org.tg.web.handler.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HandlerMethodAdapter extends Ordered {

    boolean support(Object o);

    void handler(HttpServletRequest req , HttpServletResponse res , HandlerMethod handler) throws Exception;
}
