package org.tg.web.handler;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.annotation.Order;
import org.springframework.util.ObjectUtils;
import org.tg.web.annotation.RequestMethod;
import org.tg.web.exception.HttpRequestMethodNotSupport;
import org.tg.web.intercpetor.HandlerInterceptor;
import org.tg.web.intercpetor.MappedInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public abstract class AbstractHandlerMapping extends ApplicationObjectSupport implements HandlerMapping , InitializingBean {

    protected int order;

    private MapperRegister mapperRegister = new MapperRegister();

    private List<HandlerInterceptor> handlerInterceptors = new ArrayList<>();

    public void addHandlerInterceptors(List<MappedInterceptor> handlerInterceptors) {
        this.handlerInterceptors.addAll(handlerInterceptors);
    }

    @Override
    public HandlerExecutionChain getHandlerExecutionChain(HttpServletRequest request) throws Exception {
        HandlerMethod handlerMethod = getHandlerInteal(request);
        if(ObjectUtils.isEmpty(handlerMethod)){
            return null;
        }
        HandlerExecutionChain handlerExecutionChain = new HandlerExecutionChain(handlerMethod);

        handlerExecutionChain.setInterceptors(handlerInterceptors);
        return handlerExecutionChain;
    }


    protected abstract HandlerMethod getHandlerInteal(HttpServletRequest request) throws Exception;

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setOrder(0);
        initHandlerMethod();
    }

    protected void initHandlerMethod() throws Exception {
        ApplicationContext context = obtainApplicationContext();
        String[] names = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context, Object.class);
        for (String name : names) {
            Class type = null;
            try {
                type = context.getType(name);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (type != null && isHandler(type)) {
                detectHandlerMethod(name);
            }

        }
    }

    protected HandlerMethod lockUpPath(HttpServletRequest request) throws Exception {
        Map<String, Set<HandlerMethod>> accurateMatchingPath = mapperRegister.getAccurateMatchingPath();
        Map<String, Set<HandlerMethod>> fuzzyMatchingPath = mapperRegister.getFuzzyMatchingPath();

        String method = request.getMethod();
        String requestPath = request.getRequestURI();
        HandlerMethod handlerMethod = null;

        boolean flag =false;
        if (!accurateMatchingPath.containsKey(requestPath)) {

            Set<String> paths = fuzzyMatchingPath.keySet();
            paths = paths.stream().sorted(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return -(o1.compareTo(o2));
                }
            }).collect(Collectors.toCollection(LinkedHashSet::new));
            for (String path : paths) {
                if (Pattern.compile(path).matcher(requestPath).matches()) {
                    flag = true;
                    Set<HandlerMethod> handlerMethods = fuzzyMatchingPath.get(path);

                    handlerMethod = getHandlerMethod(request, handlerMethods);

                    if(!ObjectUtils.isEmpty(handlerMethod)){
                        return handlerMethod;
                    }
                }
            }


        } else {
            flag = true;
            Set<HandlerMethod> handlerMethods = accurateMatchingPath.get(requestPath);
            handlerMethod = getHandlerMethod(request, handlerMethods);
            if(!ObjectUtils.isEmpty(handlerMethod)){
                return handlerMethod;
            }
        }
        if(flag){
            throw new HttpRequestMethodNotSupport(requestPath + "请求类型不匹配");

        }
        return null;
    }

    protected HandlerMethod getHandlerMethod(HttpServletRequest request, Set<HandlerMethod> handlerMethods) throws Exception {

        String method = request.getMethod();
        String requestPath = request.getRequestURI();


        for (HandlerMethod handlerMethod : handlerMethods) {
            RequestMethod[] requestMethods = handlerMethod.getRequestMethods();
            for (RequestMethod requestMethod : requestMethods) {
                if (requestMethod.name().equals(method)) {
                    return handlerMethod;
                }
            }
        }
        return null;

    }
    protected void registerMapper(HandlerMethod handlerMethod) throws Exception {
        mapperRegister.register(handlerMethod);
    }

    protected void registerMappers(List<HandlerMethod> handlerMethods) throws Exception {
        for (HandlerMethod handlerMethod : handlerMethods) {
            mapperRegister.register(handlerMethod);
        }
    }

    class MapperRegister {

        Map<String, Set<HandlerMethod>> accurateMatchingPath = new HashMap<>();
        Map<String, Set<HandlerMethod>> fuzzyMatchingPath = new HashMap<>();

        public Map<String, Set<HandlerMethod>> getAccurateMatchingPath() {
            return accurateMatchingPath;
        }

        public Map<String, Set<HandlerMethod>> getFuzzyMatchingPath() {
            return fuzzyMatchingPath;
        }

        public void register(HandlerMethod handlerMethod) throws Exception {

            String path = handlerMethod.getPath();

            if (path.contains("{") && path.contains("}")) {
                register(fuzzyMatchingPath, path, handlerMethod);
            } else {
                register(accurateMatchingPath, path, handlerMethod);
            }

        }

        private void register(Map<String, Set<HandlerMethod>> mapPath, String path, HandlerMethod handlerMethod) throws Exception {
            path = path.replaceAll("\\{\\w+\\}", "(\\\\w+)");


            if (mapPath.containsKey(path) && mapPath.get(path).contains(handlerMethod)) {
                throw new HttpRequestMethodNotSupport(Arrays.toString(handlerMethod.getRequestMethods()) + handlerMethod.getPath() + "HandlerMethod相同");

            }
            if (!mapPath.containsKey(path)) {
                mapPath.put(path, new HashSet<>());
            }
            mapPath.get(path).add(handlerMethod);

        }
    }
    protected abstract void detectHandlerMethod(String name) throws Exception;


    protected abstract boolean isHandler(Class type);



}