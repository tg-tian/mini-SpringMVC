package org.tg.web.resolver;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ObjectUtils;
import org.tg.web.annotation.ControllerAdvice;
import org.tg.web.annotation.ExceptionHandler;
import org.tg.web.handler.ExceptionHandlerMethod;
import org.tg.web.handler.HandlerMethod;
import org.tg.web.handler.ServletInvocableMethod;
import org.tg.web.support.WebServletRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExceptionHandlerExceptionResolver extends ApplicationObjectSupport implements InitializingBean, HandlerExceptionResolver {

    private int order;

    private Map<Class, ExceptionHandlerMethod> exceptionHandlerMethodMap = new HashMap<>();

    private HandlerMethodArgumentResolverComposite methodArgumentResolverComposite = new HandlerMethodArgumentResolverComposite();

    private HandlerMethodReturnValueHandlerComposite returnValueHandlerComposite = new HandlerMethodReturnValueHandlerComposite();

    @Override
    public Boolean resolveException(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler, Exception ex) throws Exception {
        ExceptionHandlerMethod exceptionHandlerMethod = getExceptionHandlerMethod(handler, ex);
        if(!ObjectUtils.isEmpty(exceptionHandlerMethod)){
            WebServletRequest webServletRequest = new WebServletRequest(request, response);

            ServletInvocableMethod servletInvocableMethod = new ServletInvocableMethod();
            servletInvocableMethod.setExceptionHandlerMethodMap(exceptionHandlerMethodMap);
            servletInvocableMethod.setReturnValueHandlerComposite(returnValueHandlerComposite);
            servletInvocableMethod.setResolverComposite(methodArgumentResolverComposite);
            servletInvocableMethod.invokeAndHandle(webServletRequest,handler,ex);
            return true;
        }
        return false;
    }

    public ExceptionHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod,Exception ex) {
        Class aClass = ex.getClass();
        ExceptionHandlerMethod exceptionHandlerMethod = null;
        if(handlerMethod != null && handlerMethod.getExceptionHandlerMethodMap().size() != 0){
            Map<Class, ExceptionHandlerMethod> exMap = handlerMethod.getExceptionHandlerMethodMap();
            while (exceptionHandlerMethod == null){
                exceptionHandlerMethod = exMap.get(aClass);
                aClass = aClass.getSuperclass();
                if(aClass == Throwable.class && exceptionHandlerMethod == null){
                    break;
                }
            }
        }
        aClass = ex.getClass();
       while(exceptionHandlerMethod == null){
           exceptionHandlerMethod = this.exceptionHandlerMethodMap.get(aClass);
           aClass = aClass.getSuperclass();
           if(aClass == Throwable.class && exceptionHandlerMethod == null){
               break;
           }
       }
       return exceptionHandlerMethod;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initExceptionHandlerMap();
        methodArgumentResolverComposite.addResolvers(getDefaultArgumentResolver());
        returnValueHandlerComposite.addreturnValueHandler(getDefaultReturnValueResolver());
    }

    public List<HandlerMethodReturnValueHandler> getDefaultReturnValueResolver(){
        List<HandlerMethodReturnValueHandler> handlerMethodReturnValueHandlers = new ArrayList<>();
        handlerMethodReturnValueHandlers.add(new RequestResponseBodyMethodReturnValueHandler());
        return handlerMethodReturnValueHandlers;
    }

    public List<HandlerMethodArgumentResolver> getDefaultArgumentResolver(){
        List<HandlerMethodArgumentResolver> handlerMethodArgumentResolvers = new ArrayList<>();
        handlerMethodArgumentResolvers.add(new ServletRequestMethodArgumentResolver());
        handlerMethodArgumentResolvers.add(new ServletResponseMethodArgumentResolver());

        return handlerMethodArgumentResolvers;
    }

    public void initExceptionHandlerMap() {
        Map<Class, ExceptionHandlerMethod> exceptionHandlerMethodMap = new HashMap<>();
        ApplicationContext context = obtainApplicationContext();
        String[] names = BeanFactoryUtils.beanNamesForAnnotationIncludingAncestors(context, ControllerAdvice.class);
        for (String name : names) {
            Class<?> type = context.getType(name);
            Method[] methods = type.getDeclaredMethods();
            for (Method method : methods) {
                if (AnnotatedElementUtils.hasAnnotation(method, ExceptionHandler.class)) {
                    ExceptionHandler exceptionHandler = AnnotatedElementUtils.findMergedAnnotation(method, ExceptionHandler.class);
                    Class<? extends Throwable> exType = exceptionHandler.value();
                    exceptionHandlerMethodMap.put(exType,new ExceptionHandlerMethod(context.getBean(name), method));
                }
            }
        }
        this.exceptionHandlerMethodMap.putAll(exceptionHandlerMethodMap);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
