package org.tg.web.handler;

import org.springframework.core.MethodParameter;
import org.springframework.util.ObjectUtils;
import org.tg.web.annotation.RequestMethod;
import org.tg.web.convert.ConvertHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HandlerMethod {
    protected Object bean;

    protected Class type;

    protected Method method;

    protected MethodParameter[] parameters = new MethodParameter[0];

    protected String path;

    protected RequestMethod[] requestMethods = new RequestMethod[0];

    private Map<Class,ExceptionHandlerMethod> exceptionHandlerMethodMap = new HashMap<>();

    private Map<Class, ConvertHandler> convertHandlerMap = new HashMap<>();

    public HandlerMethod() {
    }

    public HandlerMethod(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
        this.type = bean.getClass();
        Parameter[] parameters = method.getParameters();
        MethodParameter[] methodParameters = new MethodParameter[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            methodParameters[i] = new MethodParameter(method,i);
        }
        this.parameters = methodParameters;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setRequestMethods(RequestMethod[] requestMethods) {
        if(ObjectUtils.isEmpty(requestMethods)){
            this.requestMethods = RequestMethod.values();
        }
        else{
            this.requestMethods = requestMethods;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HandlerMethod that = (HandlerMethod) o;
        return Objects.equals(path, that.path) && Arrays.equals(requestMethods, that.requestMethods);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(path);
        result = 31 * result + Arrays.hashCode(requestMethods);
        return result;
    }

    public RequestMethod[] getRequestMethods() {
        return requestMethods;
    }

    public MethodParameter[] getParameters() {
        return parameters;
    }

    public Method getMethod() {
        return method;
    }

    public Object getBean() {
        return bean;
    }

    public void setExceptionHandlerMethodMap(Map<Class, ExceptionHandlerMethod> exceptionHandlerMethodMap) {
        this.exceptionHandlerMethodMap = exceptionHandlerMethodMap;
    }

    public Map<Class, ExceptionHandlerMethod> getExceptionHandlerMethodMap() {
        return exceptionHandlerMethodMap;
    }

    public void setConvertHandlerMap(Map<Class, ConvertHandler> convertHandlerMap) {
        this.convertHandlerMap = convertHandlerMap;
    }

    public Map<Class, ConvertHandler> getConvertHandlerMap() {
        return convertHandlerMap;
    }
}
