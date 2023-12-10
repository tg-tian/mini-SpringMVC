package org.tg.web.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.util.ObjectUtils;
import org.tg.web.handler.HandlerMethod;
import org.tg.web.multipart.MultipartFile;
import org.tg.web.multipart.StandardMultipartFile;
import org.tg.web.convert.ConvertComposite;
import org.tg.web.support.WebServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RequestPartMethodArgumentResolver implements HandlerMethodArgumentResolver{
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return isMultipartFile(parameter);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, HandlerMethod handlerMethod, WebServletRequest webServletRequest, ConvertComposite convertComposites) throws Exception {
        HttpServletRequest request = webServletRequest.getRequest();
        Collection<Part> parts = request.getParts();
        ArrayList<MultipartFile> files = new ArrayList<>();
        for (Part part : parts) {
            if(!ObjectUtils.isEmpty(part)){
                StandardMultipartFile standardMultipartFile = new StandardMultipartFile(part, part.getSubmittedFileName());
                files.add(standardMultipartFile);
            }
        }
        Class<?> parameterType = parameter.getParameterType();
        if(parameterType == MultipartFile.class){
            return files.get(0);
        }else if (parameterType == List.class ||parameterType == Collection.class){
            return files;
        }else if(parameterType.isArray()){
            return files.toArray(new MultipartFile[files.size()]);
        }
        return null;
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
