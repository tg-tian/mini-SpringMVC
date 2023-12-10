package org.tg.web.convert;

import org.springframework.util.ObjectUtils;
import org.tg.web.exception.NotFoundExcpetion;
import org.tg.web.handler.HandlerMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConvertComposite {

    final Map<Class, ConvertHandler> convertHandlers = new HashMap<>();

    public void addConverts(Map<Class, ConvertHandler> convertMap){
        convertHandlers.putAll(convertMap);
    }

    public Object convert(HandlerMethod handlerMethod , Class<?> parameterType, Object result) throws Exception {

        Map<Class, ConvertHandler> convertHandlerMap = handlerMethod.getConvertHandlerMap();
        if(!ObjectUtils.isEmpty(convertHandlerMap)){
            ConvertHandler convertHandler = convertHandlerMap.get(parameterType);
            if(convertHandler != null){
                return convertHandler.convert(result);
            }
        }

        if(convertHandlers.containsKey(parameterType)){
            try {
                return convertHandlers.get(parameterType).convert(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        throw new NotFoundExcpetion(parameterType.getName() + "没有该参数类型的类型转换器");
    }
}
