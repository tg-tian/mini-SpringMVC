package org.tg.web.resolver;

import org.tg.web.exception.NotFoundExcpetion;
import org.tg.web.support.WebServletRequest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class HandlerMethodReturnValueHandlerComposite {

    private List<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<>();

    public void addreturnValueHandler(List<HandlerMethodReturnValueHandler> returnValueHandlers){
        this.returnValueHandlers.addAll(returnValueHandlers);
    }


    public HandlerMethodReturnValueHandler selectHandler(Method method) throws Exception {
        for (HandlerMethodReturnValueHandler returnValueHandler : this.returnValueHandlers) {

            if(returnValueHandler.supportsReturnType(method)){
                return returnValueHandler;
            }

        }
        throw new NotFoundExcpetion(method.toString() + "找不到返回值处理器");
    }

    public void doInvoke(Object returnValue, Method method, WebServletRequest webServletRequest) throws Exception {
        final HandlerMethodReturnValueHandler returnValueHandler = selectHandler(method);
        returnValueHandler.handleReturnValue(returnValue,webServletRequest);
    }
}
