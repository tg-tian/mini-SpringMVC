package org.tg.web.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.MethodParameter;
import org.tg.web.annotation.RequestBody;
import org.tg.web.handler.HandlerMethod;
import org.tg.web.convert.ConvertComposite;
import org.tg.web.support.WebServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;

public class RequestRequestBodyMethodArgumentResolver implements HandlerMethodArgumentResolver{

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestBody.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, HandlerMethod handlerMethod, WebServletRequest webServletRequest, ConvertComposite convertComposites) throws Exception {
        String json = getJson(webServletRequest.getRequest());
        Class<?> parameterType = parameter.getParameterType();
        return objectMapper.readValue(json, parameterType);

    }

    public String getJson(HttpServletRequest request){
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        try(BufferedReader reader = request.getReader()){
            while (line != (line = reader.readLine())){
                stringBuilder.append(line);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
