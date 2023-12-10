package org.tg.web.resolver;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.tg.web.handler.HandlerMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface HandlerExceptionResolver extends Ordered {

    Boolean resolveException(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler, Exception ex) throws Exception;


    @Override
    int getOrder();
}
