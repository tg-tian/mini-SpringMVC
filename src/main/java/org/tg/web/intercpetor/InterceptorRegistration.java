package org.tg.web.intercpetor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InterceptorRegistration {

    // 拦截器
    private  HandlerInterceptor interceptor;
    // 拦截路径
    private List<String> includePatterns = new ArrayList<>();
    // 排除路径
    private List<String> excludePatterns = new ArrayList<>();

    public InterceptorRegistration addExcludePatterns(String... path) {
        this.excludePatterns.addAll(Arrays.asList(path));
        return this;
    }

    public InterceptorRegistration addIncludePatterns(String... path) {
        this.includePatterns.addAll(Arrays.asList(path));
        return this;
    }

    public InterceptorRegistration setInterceptor(HandlerInterceptor interceptor) {
        this.interceptor = interceptor;
        return this;
    }

    public HandlerInterceptor getInterceptor() {
        return interceptor;
    }

    public List<String> getExcludePatterns() {
        return excludePatterns;
    }

    public List<String> getIncludePatterns() {
        return includePatterns;
    }
}
