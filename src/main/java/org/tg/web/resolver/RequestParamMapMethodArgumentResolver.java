package org.tg.web.resolver;

import org.springframework.core.MethodParameter;
import org.tg.web.annotation.RequestBody;
import org.tg.web.handler.HandlerMethod;
import org.tg.web.multipart.MultipartFile;
import org.tg.web.convert.ConvertComposite;
import org.tg.web.support.WebServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestParamMapMethodArgumentResolver implements HandlerMethodArgumentResolver{
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        if(parameterType == HttpServletRequest.class || parameterType == HttpServletResponse.class){
            return false;
        }
        if(isMultipartFile(parameter)){
            return false;
        }
        if(parameterType != Map.class){
            return false;
        }
        if(parameter.hasParameterAnnotation(RequestBody.class)){
            return false;
        }
        return true;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, HandlerMethod handlerMethod, WebServletRequest webServletRequest, ConvertComposite convertComposites) throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        HttpServletRequest request = webServletRequest.getRequest();
        Map<String, String[]> parameterMap = request.getParameterMap();
        parameterMap.forEach((k,v)->{
            resultMap.put(k,v[0]);
        });
        return resultMap;
    }

    public boolean isMultipartFile(MethodParameter parameter){
        Class<?> parameterType = parameter.getParameterType();
        if(parameterType == MultipartFile.class){
            return true;
        }

        if(parameterType == List.class || parameterType == Collection.class){

            Type genericParameterType = parameter.getGenericParameterType();

            ParameterizedType type = (ParameterizedType) genericParameterType;

            if(type.getActualTypeArguments()[0] == MultipartFile.class){
                return true;
            }

            if(parameterType.isArray() && parameterType == MultipartFile.class){
                return true;
            }


        }
        return false;
    }
}
