package org.tg.web.resolver;

import org.springframework.core.MethodParameter;
import org.tg.web.annotation.PathVariable;
import org.tg.web.handler.HandlerMethod;
import org.tg.web.convert.ConvertComposite;
import org.tg.web.support.WebServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class PathVariableMapMethodArgumentResolver implements HandlerMethodArgumentResolver{
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(PathVariable.class) && parameter.getParameterType() == Math.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, HandlerMethod handlerMethod, WebServletRequest webServletRequest, ConvertComposite convertComposites) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        Map<Integer,String> indexMap = new HashMap<>();
        String path = handlerMethod.getPath();
        String[] split = path.split("/");
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            if(s.contains("{")||s.contains("}")){
                indexMap.put(i,s.substring(1,s.length()-1));
            }
        }
        HttpServletRequest request = webServletRequest.getRequest();
        split = request.getRequestURI().split("/");
        for (Integer index : indexMap.keySet()) {
            resultMap.put(indexMap.get(index),split[index]);
        }
        return resultMap;
    }


}
