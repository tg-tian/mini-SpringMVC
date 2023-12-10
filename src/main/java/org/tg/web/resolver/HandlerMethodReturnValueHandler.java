package org.tg.web.resolver;

import org.tg.web.support.WebServletRequest;

import java.lang.reflect.Method;

public interface HandlerMethodReturnValueHandler {

    boolean supportsReturnType(Method method);

    void handleReturnValue(Object returnValue, WebServletRequest webServletRequest) throws Exception;

}
