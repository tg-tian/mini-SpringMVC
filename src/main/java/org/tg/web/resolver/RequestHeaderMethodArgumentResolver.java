package org.tg.web.resolver;

import org.springframework.core.MethodParameter;
import org.tg.web.annotation.RequestHeader;
import org.tg.web.exception.NotFoundExcpetion;
import org.tg.web.handler.HandlerMethod;
import org.tg.web.convert.ConvertComposite;
import org.tg.web.support.WebServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class RequestHeaderMethodArgumentResolver implements HandlerMethodArgumentResolver{
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestHeader.class) && parameter.getParameterType() != Map.class;

    }

    @Override
    public Object resolveArgument(MethodParameter parameter, HandlerMethod handlerMethod, WebServletRequest webServletRequest, ConvertComposite convertComposites) throws Exception {

        String name = "";
        RequestHeader parameterAnnotation = parameter.getParameterAnnotation(RequestHeader.class);
        name = parameterAnnotation.value().equals("") ? parameter.getParameterName() : parameterAnnotation.value();

        HttpServletRequest request = webServletRequest.getRequest();

        if(parameterAnnotation.require() && request.getHeader(name) == null){
            throw new NotFoundExcpetion(handlerMethod.getPath()+ "请求头没有携带 " + name);
        }

        return convertComposites.convert(handlerMethod,parameter.getParameterType(),request.getHeader(name));
    }
}
