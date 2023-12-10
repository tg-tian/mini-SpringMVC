package org.tg.web.resolver;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.tg.web.annotation.RequestBody;
import org.tg.web.annotation.ResponseBody;
import org.tg.web.support.WebServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

public class RequestResponseBodyMethodReturnValueHandler implements HandlerMethodReturnValueHandler{

    ObjectMapper objectMapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    @Override
    public boolean supportsReturnType(Method method) {
        return AnnotatedElementUtils.hasAnnotation(method.getDeclaringClass(), ResponseBody.class) || method.isAnnotationPresent(ResponseBody.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, WebServletRequest webServletRequest) throws Exception {

        responseBody(returnValue, webServletRequest.getResponse());
    }

    private void responseBody(Object value, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().println(objectMapper.writeValueAsString(value));
        response.getWriter().flush();
    }
}
