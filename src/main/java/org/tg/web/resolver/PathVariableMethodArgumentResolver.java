package org.tg.web.resolver;

import org.springframework.core.MethodParameter;
import org.tg.web.annotation.PathVariable;
import org.tg.web.handler.HandlerMethod;
import org.tg.web.convert.ConvertComposite;
import org.tg.web.support.WebServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class PathVariableMethodArgumentResolver implements HandlerMethodArgumentResolver{
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PathVariable.class) && parameter.getParameterType() != Map.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, HandlerMethod handlerMethod, WebServletRequest webServletRequest, ConvertComposite convertComposites) throws Exception {
        String name = "";
        PathVariable parameterAnnotation = parameter.getParameterAnnotation(PathVariable.class);
        name = parameterAnnotation.value().equals("") ? parameter.getParameterName() : parameterAnnotation.value();

        Object result = null;
        int index = -1;
        String path = handlerMethod.getPath();
        String[] split = path.split("/");
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            if(s.contains("{") && s.contains("}") && s.contains(name)){
                index = i;
                break;
            }
        }
        HttpServletRequest request = webServletRequest.getRequest();
        split = request.getRequestURI().split("/");
        if(index != -1){
            result = split[index];
        }
        return convertComposites.convert(handlerMethod,parameter.getParameterType(),result);
    }
}
