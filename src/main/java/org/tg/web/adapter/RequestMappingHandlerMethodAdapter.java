package org.tg.web.adapter;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.tg.web.annotation.ControllerAdvice;
import org.tg.web.annotation.ConvertType;
import org.tg.web.annotation.RequestMapping;
import org.tg.web.convert.*;
import org.tg.web.handler.HandlerMethod;
import org.tg.web.handler.ServletInvocableMethod;
import org.tg.web.resolver.*;
import org.tg.web.support.WebServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.*;

public class RequestMappingHandlerMethodAdapter extends ApplicationObjectSupport implements HandlerMethodAdapter, InitializingBean {
    private int order;

    private HandlerMethodArgumentResolverComposite resolverComposite = new HandlerMethodArgumentResolverComposite();

    private ConvertComposite convertComposite = new ConvertComposite();

    private HandlerMethodReturnValueHandlerComposite returnValueHandlerComposite = new HandlerMethodReturnValueHandlerComposite();

    @Override
    public boolean support(Object o) {
        HandlerMethod handlerMethod = (HandlerMethod)o;
        return AnnotatedElementUtils.hasAnnotation(handlerMethod.getMethod(), RequestMapping.class);
    }

    @Override
    public void handler(HttpServletRequest req, HttpServletResponse res, HandlerMethod handler) throws Exception {

        WebServletRequest webServletRequest = new WebServletRequest(req, res);
        ServletInvocableMethod invocableMethod = new ServletInvocableMethod();

        invocableMethod.setHandlerMethod(handler);
        invocableMethod.setResolverComposite(resolverComposite);
        invocableMethod.setConvertComposite(convertComposite);
        invocableMethod.setReturnValueHandlerComposite(returnValueHandlerComposite);

        invocableMethod.invokeAndHandle(webServletRequest,handler);

    }




    @Override
    public void afterPropertiesSet() throws Exception {
        this.resolverComposite.addResolvers(getDefaultResolver());
        this.convertComposite.addConverts(getDefaultConvertHandler());
        this.convertComposite.addConverts(getDiyConvertHandler());
        this.returnValueHandlerComposite.addreturnValueHandler(getDefaultreturnValueHandler());
    }

    private Map<Class, ConvertHandler> getDiyConvertHandler() {
        Map<Class, ConvertHandler> convertHandlerMap = new HashMap<>();
        ApplicationContext context = obtainApplicationContext();
        String[] names = BeanFactoryUtils.beanNamesForAnnotationIncludingAncestors(context, ControllerAdvice.class);
        for (String name : names) {
            Class<?> type = context.getType(name);
            Method[] methods = type.getDeclaredMethods();
            for (Method method : methods) {
                if(AnnotatedElementUtils.hasAnnotation(method, ConvertType.class)){
                    ConvertType convertType = AnnotatedElementUtils.findMergedAnnotation(method, ConvertType.class);
                    convertHandlerMap.put(convertType.value(),new ConvertHandler(context.getBean(name),method));
                }
            }
        }
        return  convertHandlerMap;

    }

    private List<HandlerMethodArgumentResolver> getDefaultResolver(){
        ArrayList<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
        resolvers.add(new PathVariableMethodArgumentResolver());
        resolvers.add(new PathVariableMapMethodArgumentResolver());
        resolvers.add(new RequestCookieMethodArgumentResolver());
        resolvers.add(new RequestHeaderMethodArgumentResolver());
        resolvers.add(new RequestHeaderMapMethodArgumentResolver());
        resolvers.add(new RequestPartMethodArgumentResolver());
        resolvers.add(new RequestParamMapMethodArgumentResolver());
        resolvers.add(new RequestParamMethodArgumentResolver());
        resolvers.add(new RequestRequestBodyMethodArgumentResolver());
        resolvers.add(new ServletRequestMethodArgumentResolver());
        resolvers.add(new ServletResponseMethodArgumentResolver());
        return resolvers;
    }

    private Map<Class, ConvertHandler> getDefaultConvertHandler(){
        final Map<Class, ConvertHandler> convertMap = new HashMap<>();
        convertMap.put(Integer.class,getConvertHandler(new IntegerConvert(Integer.class)));
        convertMap.put(int.class,getConvertHandler(new IntegerConvert(Integer.class)));
        convertMap.put(String.class,getConvertHandler(new StringConvert(String.class)));
        convertMap.put(Long.class,getConvertHandler(new LongConvert(Long.class)));
        convertMap.put(long.class,getConvertHandler(new LongConvert(Long.class)));
        convertMap.put(Float.class,getConvertHandler(new FloatConvert(Float.class)));
        convertMap.put(float.class,getConvertHandler(new FloatConvert(Float.class)));
        convertMap.put(Boolean.class,getConvertHandler(new BooleanConvert(Boolean.class)));
        convertMap.put(boolean.class,getConvertHandler(new BooleanConvert(Boolean.class)));
        convertMap.put(Byte.class,getConvertHandler(new ByteConvert(Byte.class)));
        convertMap.put(byte.class,getConvertHandler(new ByteConvert(Byte.class)));
        convertMap.put(Short.class,getConvertHandler(new ShortConvert(Short.class)));
        convertMap.put(short.class,getConvertHandler(new ShortConvert(Short.class)));
        convertMap.put(Date.class,getConvertHandler(new DateConvert(Date.class)));
        convertMap.put(Map.class,getConvertHandler(new MapConvert(HashMap.class)));
        convertMap.put(Collection.class,getConvertHandler(new CollectionConvert(Collection.class)));
        convertMap.put(List.class,getConvertHandler(new ListConvert(ArrayList.class)));
        convertMap.put(Set.class,getConvertHandler(new SetConvert(HashSet.class)));
        return convertMap;
    }

    private List<HandlerMethodReturnValueHandler> getDefaultreturnValueHandler(){
        List<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<>();
        returnValueHandlers.add(new RequestResponseBodyMethodReturnValueHandler());
        return returnValueHandlers;
    }

    protected ConvertHandler getConvertHandler(Convert convert){
        try {
            final Method method = convert.getClass().getDeclaredMethod("convert", Object.class);
            return new ConvertHandler(convert,method);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
