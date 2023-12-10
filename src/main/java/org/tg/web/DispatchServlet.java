package org.tg.web;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.tg.web.adapter.HandlerMethodAdapter;
import org.tg.web.context.WebApplicationContext;
import org.tg.web.exception.NotFoundExcpetion;
import org.tg.web.handler.HandlerExecutionChain;
import org.tg.web.handler.HandlerMapping;
import org.tg.web.handler.HandlerMethod;
import org.tg.web.resolver.HandlerExceptionResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class DispatchServlet extends BaseHttpServlet {

    private Properties defaultStrategies;

    private List<HandlerMapping> handlerMappingList = new ArrayList<>();

    private List<HandlerMethodAdapter> handlerMethodAdapters = new ArrayList<>();

    private List<HandlerExceptionResolver> handlerExceptionResolvers = new ArrayList<>();

    public static final String DEFAULT_STRATEGIES_PATH = "DispatchServlet.properties";

    public DispatchServlet(WebApplicationContext webApplicationContext) {
        super(webApplicationContext);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Exception ex = null;
        HandlerExecutionChain handlerExecutionChain = null;

        try {
            handlerExecutionChain = getHandler(req);
            if(ObjectUtils.isEmpty(handlerExecutionChain)){
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return ;
            }
            HandlerMethodAdapter ha = getHandlerMethodAdapter(handlerExecutionChain.getHandlerMethod());

            if(!handlerExecutionChain.applyPreInterceptor(req,resp)){
                return;
            }

            ha.handler(req,resp,handlerExecutionChain.getHandlerMethod());

            handlerExecutionChain.applyPostInterceptor(req,resp);
        } catch (Exception e) {
            ex = e;
        }

        try {
            processResult(req,resp,handlerExecutionChain,ex);
        } catch (Exception e) {
            throw new ServletException(e.getMessage());
        }

    }

    private void processResult(HttpServletRequest req, HttpServletResponse resp, HandlerExecutionChain handlerExecutionChain, Exception ex) throws Exception {
        if(ex != null){
            processResultException(req,resp,handlerExecutionChain.getHandlerMethod(),ex);
        }
        handlerExecutionChain.afterCompletion(req,resp,handlerExecutionChain.getHandlerMethod(),ex);
    }

    private void processResultException(HttpServletRequest req, HttpServletResponse resp, HandlerMethod handlerMethod, Exception ex) throws Exception {
        for (HandlerExceptionResolver handlerExceptionResolver : this.handlerExceptionResolvers) {
            if(handlerExceptionResolver.resolveException(req,resp,handlerMethod,ex)){
                return;
            }
        }
        throw new ServletException(ex.getMessage());
    }

    private HandlerMethodAdapter getHandlerMethodAdapter(HandlerMethod handlerMethod) throws Exception {
        for (HandlerMethodAdapter handlerMethodAdapter : this.handlerMethodAdapters) {
            if(handlerMethodAdapter.support(handlerMethod)){
                return handlerMethodAdapter;
            }
        }
        throw new NotFoundExcpetion(handlerMethod + "没有支持的适配器");
    }


    private HandlerExecutionChain getHandler(HttpServletRequest req) throws Exception {
        for (HandlerMapping handlerMapping : handlerMappingList) {
            HandlerExecutionChain handler = handlerMapping.getHandlerExecutionChain(req);
            if(handler != null){
                return  handler;
            }
        }
        return null;
    }

    //组件初始化
    @Override
    protected void onRefresh(WebApplicationContext webApplicationContext) {
        initHandlerMapping(webApplicationContext);
        initHandlerAdapter(webApplicationContext);
        initHandlerException(webApplicationContext);
    }

    private void initHandlerException(WebApplicationContext webApplicationContext) {

        Map<String, HandlerExceptionResolver> map = BeanFactoryUtils.beansOfTypeIncludingAncestors(webApplicationContext, HandlerExceptionResolver.class, true, false);

        if(!ObjectUtils.isEmpty(map)){
            this.handlerExceptionResolvers = new ArrayList<>(map.values());
        }else{
            this.handlerExceptionResolvers.addAll(getDefaultStrategies(webApplicationContext,HandlerExceptionResolver.class));
        }
        this.handlerExceptionResolvers.sort(new Comparator<HandlerExceptionResolver>() {
            @Override
            public int compare(HandlerExceptionResolver o1, HandlerExceptionResolver o2) {
                return o2.getOrder() - o1.getOrder();
            }
        });


    }

    private void initHandlerAdapter(WebApplicationContext webApplicationContext) {

        Map<String, HandlerMethodAdapter> map = BeanFactoryUtils.beansOfTypeIncludingAncestors(webApplicationContext, HandlerMethodAdapter.class, true, false);

        if(!ObjectUtils.isEmpty(map)){
            this.handlerMethodAdapters = new ArrayList<>(map.values());
        }else{
            this.handlerMethodAdapters.addAll(getDefaultStrategies(webApplicationContext,HandlerMethodAdapter.class));
        }
        this.handlerMethodAdapters.sort(new Comparator<HandlerMethodAdapter>() {
            @Override
            public int compare(HandlerMethodAdapter o1, HandlerMethodAdapter o2) {
                return o2.getOrder() - o1.getOrder();
            }
        });

    }

    private void initHandlerMapping(WebApplicationContext webApplicationContext) {
        Map<String, HandlerMapping> map = BeanFactoryUtils.beansOfTypeIncludingAncestors(webApplicationContext, HandlerMapping.class, true, false);

        if(!ObjectUtils.isEmpty(map)){
            this.handlerMappingList = new ArrayList<>(map.values());
        }else{
            this.handlerMappingList.addAll(getDefaultStrategies(webApplicationContext,HandlerMapping.class));
        }
        this.handlerMappingList.sort(new Comparator<HandlerMapping>() {
            @Override
            public int compare(HandlerMapping o1, HandlerMapping o2) {
                return o2.getOrder() - o1.getOrder();
            }
        });

    }

    protected <T> List<T> getDefaultStrategies(ApplicationContext context, Class<T> strategyInterface) {
        if (defaultStrategies == null) {
            try {
                ClassPathResource resource = new ClassPathResource(DEFAULT_STRATEGIES_PATH, DispatchServlet.class);
                defaultStrategies = PropertiesLoaderUtils.loadProperties(resource);
            } catch (IOException ex) {
                throw new IllegalStateException("Could not load '" + DEFAULT_STRATEGIES_PATH + "': " + ex.getMessage());
            }
        }

        String key = strategyInterface.getName();
        String value = defaultStrategies.getProperty(key);
        if (value != null) {
            String[] classNames = StringUtils.commaDelimitedListToStringArray(value);
            List<T> strategies = new ArrayList<>(classNames.length);
            for (String className : classNames) {
                try {
                    Class<?> clazz = ClassUtils.forName(className, DispatchServlet.class.getClassLoader());
                    Object strategy = createDefaultStrategy(context, clazz);
                    strategies.add((T) strategy);
                } catch (ClassNotFoundException ex) {
                    throw new BeanInitializationException(
                            "Could not find DispatcherServlet's default strategy class [" + className +
                                    "] for interface [" + key + "]", ex);
                } catch (LinkageError err) {
                    throw new BeanInitializationException(
                            "Unresolvable class definition for DispatcherServlet's default strategy class [" +
                                    className + "] for interface [" + key + "]", err);
                }
            }
            return strategies;
        } else {
            return Collections.emptyList();
        }
    }

    protected Object createDefaultStrategy(ApplicationContext context, Class<?> clazz) {
        return context.getAutowireCapableBeanFactory().createBean(clazz);
    }
}
