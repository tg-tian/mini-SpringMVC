package org.tg.web.handler;


import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.tg.web.annotation.ConvertType;
import org.tg.web.annotation.ExceptionHandler;
import org.tg.web.annotation.RequestMapping;
import org.tg.web.annotation.RequestMethod;
import org.tg.web.convert.ConvertHandler;
import org.tg.web.intercpetor.HandlerInterceptor;
import org.tg.web.intercpetor.MappedInterceptor;
import org.tg.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Pattern;

public class RequestMappingHandlerMapping extends AbstractHandlerMapping {


    @Override
    protected HandlerMethod getHandlerInteal(HttpServletRequest request) throws Exception {
        return lockUpPath(request);
    }






    @Override
    protected void detectHandlerMethod(String name) throws Exception {
        ApplicationContext context = obtainApplicationContext();
        Class<?> type = context.getType(name);
        Method[] methods = type.getDeclaredMethods();
        List<HandlerMethod> handlerMethods = new ArrayList<>();

        String path = "";
        if (AnnotatedElementUtils.hasAnnotation(type, RequestMapping.class)) {
            RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(type, RequestMapping.class);
            String value = requestMapping.value();
            path = value.equals("") ? "" : value;
        }

        Map<Class,ExceptionHandlerMethod> exceptionHandlerMethodMap = new HashMap<>();
        Map<Class, ConvertHandler> convertHandlerMap = new HashMap<>();
        Object bean = context.getBean(name);

        for (Method method : methods) {
            if (AnnotatedElementUtils.hasAnnotation(method, ExceptionHandler.class)){
                ExceptionHandler exceptionHandler = AnnotatedElementUtils.findMergedAnnotation(method, ExceptionHandler.class);
                exceptionHandlerMethodMap.put(exceptionHandler.value(),new ExceptionHandlerMethod(bean,method));
            }
            if (AnnotatedElementUtils.hasAnnotation(method, ConvertType.class)){
                ConvertType convertType = AnnotatedElementUtils.findMergedAnnotation(method, ConvertType.class);
                convertHandlerMap.put(convertType.value(),new ConvertHandler(bean,method));
            }
            if (AnnotatedElementUtils.hasAnnotation(method, RequestMapping.class)) {

                HandlerMethod handlerMethod = new HandlerMethod(bean, method);

                if (AnnotatedElementUtils.hasAnnotation(method, RequestMapping.class)) {
                    RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
                    String value = requestMapping.value();
                    String childPath = value.equals("") ? "" : value;
                    handlerMethod.setPath(path + childPath);
                    handlerMethod.setRequestMethods(requestMapping.requestMethod());

                    handlerMethods.add(handlerMethod);
                }
            }
        }
        if (!ObjectUtils.isEmpty(handlerMethods)) {
            for (HandlerMethod handlerMethod : handlerMethods) {
                handlerMethod.setExceptionHandlerMethodMap(exceptionHandlerMethodMap);
                handlerMethod.setConvertHandlerMap(convertHandlerMap);
                registerMapper(handlerMethod);
            }
        }
    }

    @Override
    protected boolean isHandler(Class type) {
        return AnnotatedElementUtils.hasAnnotation(type, Controller.class);
    }


    @Override
    public void setOrder(int order) {
        this.order = 1;
    }
}
