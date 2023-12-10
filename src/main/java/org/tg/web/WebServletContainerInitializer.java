package org.tg.web;

import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//SPI对接SCI接口供servlet server调用，完成自己的容器初始化
@HandlesTypes(WebApplicationInitializer.class)
public class WebServletContainerInitializer implements ServletContainerInitializer {
    @Override
    //自动扫描HandlesTypes中的实现类，进行判断再调用他们的onStartUp方法
    public void onStartup(Set<Class<?>> configurations, ServletContext ctx) throws ServletException {
        if(!ObjectUtils.isEmpty(configurations)){
            ArrayList<WebApplicationInitializer> initializers = new ArrayList<>();

            for (Class<?> configuration : configurations) {
                if(!configuration.isInterface() && !Modifier.isAbstract(configuration.getModifiers())
                        && WebApplicationInitializer.class.isAssignableFrom(configuration)){
                    try {
                        initializers.add((WebApplicationInitializer) ReflectionUtils.accessibleConstructor(configuration).newInstance());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if(!ObjectUtils.isEmpty(initializers)){
                for (WebApplicationInitializer initializer : initializers) {

                    initializer.onStartUp(ctx);

                }
            }

        }

    }
}

