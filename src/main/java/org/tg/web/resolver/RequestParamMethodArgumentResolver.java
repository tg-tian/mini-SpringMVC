package org.tg.web.resolver;

import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.util.ReflectionUtils;
import org.tg.web.annotation.RequestBody;
import org.tg.web.annotation.RequestParam;
import org.tg.web.handler.HandlerMethod;
import org.tg.web.multipart.MultipartFile;
import org.tg.web.convert.ConvertComposite;
import org.tg.web.support.WebServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class RequestParamMethodArgumentResolver implements HandlerMethodArgumentResolver{
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> parameterType = parameter.getParameterType();
        if(parameterType == HttpServletRequest.class || parameterType == HttpServletResponse.class){
            return false;
        }
        if(isMultipartFile(parameter)){
            return false;
        }
        if(parameterType == Map.class){
            return false;
        }
        if(parameter.hasParameterAnnotation(RequestBody.class)){
            return false;
        }
        return true;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, HandlerMethod handlerMethod, WebServletRequest webServletRequest, ConvertComposite convertComposites) throws Exception {

        Class<?> parameterType = parameter.getParameterType();

        HttpServletRequest request = webServletRequest.getRequest();

        if(BeanUtils.isSimpleProperty(parameterType)){
            String name = parameter.getParameterName();
            if(parameter.hasParameterAnnotation(RequestParam.class)){
                RequestParam parameterAnnotation = parameter.getParameterAnnotation(RequestParam.class);
                name = parameterAnnotation.value().equals("") ? parameter.getParameterName() : parameterAnnotation.value();

            }
            return convertComposites.convert(handlerMethod,parameter.getParameterType(),request.getParameter(name));
        }else{

            if(parameter.hasParameterAnnotation(RequestParam.class)){
                throw new IllegalArgumentException(handlerMethod.getBean().getClass().getName()+" "+handlerMethod.getMethod().getName()+"@RequestParam 不支持标注在对象上");
            }
            Map<String, String[]> parameterMap = request.getParameterMap();
            Object o = ReflectionUtils.accessibleConstructor(parameterType).newInstance();

            Field[] fields = parameterType.getDeclaredFields();

            for (Field field : fields) {
                if(parameterMap.containsKey(field.getName())){
                    field.setAccessible(true);
                    field.set(o,parameterMap.get(field.getName())[0]);
                    field.setAccessible(false);
                }
            }
            return o;

        }



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
