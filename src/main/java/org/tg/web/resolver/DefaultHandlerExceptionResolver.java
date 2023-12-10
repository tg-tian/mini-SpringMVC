package org.tg.web.resolver;

import org.tg.web.exception.ConvertCastExcpetion;
import org.tg.web.exception.HttpRequestMethodNotSupport;
import org.tg.web.exception.NotFoundExcpetion;
import org.tg.web.handler.HandlerMethod;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DefaultHandlerExceptionResolver implements HandlerExceptionResolver{

    private int order;

    @Override
    public Boolean resolveException(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler, Exception ex) throws IOException {
        final Class<? extends Exception> type = ex.getClass();

        if(type == ConvertCastExcpetion.class){
            response.sendError(500,ex.getMessage());
            return true;
        }else if(type == HttpRequestMethodNotSupport.class){
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,ex.getMessage());
            return true;
        }else if(type == NotFoundExcpetion.class){
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            return true;
        }
        return true;
    }

    @Override
    public int getOrder() {
        return 1;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
