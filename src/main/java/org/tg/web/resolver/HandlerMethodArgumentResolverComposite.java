package org.tg.web.resolver;

import org.springframework.core.MethodParameter;
import org.tg.web.handler.HandlerMethod;
import org.tg.web.convert.ConvertComposite;
import org.tg.web.support.WebServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandlerMethodArgumentResolverComposite  implements HandlerMethodArgumentResolver{

    ArrayList<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();

    Map<MethodParameter,HandlerMethodArgumentResolver> argumentResolverCache = new HashMap<>();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {



        for (HandlerMethodArgumentResolver resolver : resolvers) {
            if(resolver.supportsParameter(parameter)){
                argumentResolverCache.put(parameter,resolver);
                return true;
            }
        }

        return false;

    }

    protected HandlerMethodArgumentResolver getArgumentResolver(MethodParameter parameter){
        return argumentResolverCache.get(parameter);
    }


    @Override
    public Object resolveArgument(MethodParameter parameter, HandlerMethod handlerMethod, WebServletRequest webServletRequest, ConvertComposite convertComposites) throws Exception {


        HandlerMethodArgumentResolver argumentResolver = getArgumentResolver(parameter);
        return argumentResolver.resolveArgument(parameter,handlerMethod,webServletRequest,convertComposites);

    }

    public void addResolvers(List<HandlerMethodArgumentResolver> resolvers){
        this.resolvers.addAll(resolvers);
    }
}
