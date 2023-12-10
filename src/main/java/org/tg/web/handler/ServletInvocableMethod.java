package org.tg.web.handler;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.tg.web.resolver.HandlerMethodArgumentResolverComposite;
import org.tg.web.convert.ConvertComposite;
import org.tg.web.resolver.HandlerMethodReturnValueHandlerComposite;
import org.tg.web.support.WebServletRequest;

import java.lang.reflect.Method;

public class ServletInvocableMethod extends HandlerMethod{

    private HandlerMethod handlerMethod;

    private ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    private HandlerMethodArgumentResolverComposite resolverComposite = new HandlerMethodArgumentResolverComposite();

    private ConvertComposite convertComposite = new ConvertComposite();

    private HandlerMethodReturnValueHandlerComposite returnValueHandlerComposite = new HandlerMethodReturnValueHandlerComposite();

    public ServletInvocableMethod(Object bean, Method method) {
        super(bean, method);
    }

    public ServletInvocableMethod(){

    }


    public void invokeAndHandle(WebServletRequest webServletRequest, HandlerMethod handler,Object... providerArgs) throws Exception {

        Object[] methodArguments = getMethodArguments(handlerMethod, webServletRequest,providerArgs);

        Object returnValue = doInvoke(methodArguments);

        this.returnValueHandlerComposite.doInvoke(returnValue,handler.getMethod(),webServletRequest);

       


    }
    public Object[] getMethodArguments(HandlerMethod handlerMethod , WebServletRequest webServletRequest,Object... providerArgs) throws Exception {
        MethodParameter[] parameters = handlerMethod.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            MethodParameter parameter = parameters[i];
            parameter.initParameterNameDiscovery(nameDiscoverer);

            args[i] =findPorviderArgs(parameter,providerArgs);

            if(args[i] != null){
                continue;
            }
            if(!resolverComposite.supportsParameter(parameter)){
                throw new Exception("没有参数解析器解析参数"+parameter.getParameterName());
            }

            args[i]=this.resolverComposite.resolveArgument(parameter,handlerMethod,webServletRequest,this.convertComposite);
        }
        return args;
    }

    private Object findPorviderArgs(MethodParameter parameter, Object[] providerArgs) {
        Class<?> parameterType = parameter.getParameterType();
        for (Object providerArg : providerArgs) {
            if(parameterType == providerArg.getClass() || parameterType.isAssignableFrom(providerArg.getClass())){
                return providerArg;
            }
        }
        return null;
    }

    public Object doInvoke(Object[] args) throws Exception {
        Object returnValue = this.handlerMethod.method.invoke(this.handlerMethod.bean,args);
        return returnValue;
    }

    public void setResolverComposite(HandlerMethodArgumentResolverComposite resolverComposite) {
        this.resolverComposite = resolverComposite;
    }

    public void setConvertComposite(ConvertComposite convertComposite) {
        this.convertComposite = convertComposite;
    }

    public void setHandlerMethod(HandlerMethod handlerMethod) {
        this.handlerMethod = handlerMethod;
    }

    public void setReturnValueHandlerComposite(HandlerMethodReturnValueHandlerComposite returnValueHandlerComposite){
        this.returnValueHandlerComposite = returnValueHandlerComposite;
    }
}
