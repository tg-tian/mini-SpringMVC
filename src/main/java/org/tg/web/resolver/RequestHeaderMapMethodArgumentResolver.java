package org.tg.web.resolver;

import org.springframework.core.MethodParameter;
import org.tg.web.annotation.RequestHeader;
import org.tg.web.handler.HandlerMethod;
import org.tg.web.convert.ConvertComposite;
import org.tg.web.support.WebServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class RequestHeaderMapMethodArgumentResolver implements HandlerMethodArgumentResolver{
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestHeader.class) && parameter.getParameterType() == Map.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, HandlerMethod handlerMethod, WebServletRequest webServletRequest, ConvertComposite convertComposites) throws Exception {
        Annotation parameterAnnotation = parameter.getParameterAnnotation(RequestHeader.class);
        HttpServletRequest request = webServletRequest.getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, String> resultMap = new HashMap<>();
        while(headerNames.hasMoreElements()){
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            resultMap.put(key,value);
        }
        return resultMap;
    }
}
