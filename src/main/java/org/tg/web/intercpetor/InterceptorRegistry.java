package org.tg.web.intercpetor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InterceptorRegistry {
    private List<InterceptorRegistration> interceptorRegistrations = new ArrayList<>();

    public InterceptorRegistration addInterceptor(HandlerInterceptor interceptor){
        InterceptorRegistration interceptorRegistration = new InterceptorRegistration();
        interceptorRegistration.setInterceptor(interceptor);
        interceptorRegistrations.add(interceptorRegistration);
        return interceptorRegistration;
    }

    public List<MappedInterceptor> getInterceptors() {
        List<MappedInterceptor> mappedInterceptors = this.interceptorRegistrations.stream().map(MappedInterceptor::new).collect(Collectors.toList());

        return mappedInterceptors;
    }
}
