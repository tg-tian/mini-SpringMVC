package org.tg.web.resolver;

import org.springframework.core.MethodParameter;
import org.tg.web.annotation.Cookie;
import org.tg.web.exception.NotFoundExcpetion;
import org.tg.web.handler.HandlerMethod;
import org.tg.web.convert.ConvertComposite;
import org.tg.web.support.WebServletRequest;

import javax.servlet.http.HttpServletRequest;

public class RequestCookieMethodArgumentResolver implements HandlerMethodArgumentResolver{
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Cookie.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, HandlerMethod handlerMethod, WebServletRequest webServletRequest, ConvertComposite convertComposites) throws Exception {
        Cookie parameterAnnotation = parameter.getParameterAnnotation(Cookie.class);
        String name = "";
        name = parameterAnnotation.value().equals("") ? parameter.getParameterName():parameterAnnotation.value();
        HttpServletRequest request = webServletRequest.getRequest();
        javax.servlet.http.Cookie[] cookies = request.getCookies();
        for (javax.servlet.http.Cookie cookie : cookies) {
            if(cookie.getName().equals(name)){
                return convertComposites.convert(handlerMethod,parameter.getParameterType(),cookie.getValue());
            }
        }
        if(parameterAnnotation.require()){
            throw new NotFoundExcpetion(handlerMethod.getPath()+" cookie没有携带: "+ name);
        }
        return null;
    }
}
