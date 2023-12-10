package org.tg.web.resolver;

import org.springframework.core.MethodParameter;
import org.tg.web.handler.HandlerMethod;
import org.tg.web.convert.ConvertComposite;
import org.tg.web.support.WebServletRequest;

public interface HandlerMethodArgumentResolver {

    boolean supportsParameter(MethodParameter parameter);

    Object resolveArgument(MethodParameter parameter , HandlerMethod handlerMethod, WebServletRequest webServletRequest, ConvertComposite convertComposites) throws Exception;
}
