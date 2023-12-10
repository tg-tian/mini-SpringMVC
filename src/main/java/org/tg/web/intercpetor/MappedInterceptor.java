package org.tg.web.intercpetor;

import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class MappedInterceptor implements HandlerInterceptor{
    private  HandlerInterceptor interceptor;
    // 拦截路径
    private List<String> includePatterns = new ArrayList<>();
    // 排除路径
    private List<String> excludePatterns = new ArrayList<>();

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public boolean match(String path){
        for (String excludePattern : this.excludePatterns) {
            if (antPathMatcher.match(excludePattern,path)){
                return false;
            }
        }
        for (String includePattern : this.includePatterns) {
            if (antPathMatcher.match(includePattern,path)){
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response) {
        return interceptor.preHandle(request, response);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response) {
        interceptor.postHandle(request, response);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        interceptor.afterCompletion(request, response, handler, ex);
    }

    public MappedInterceptor(InterceptorRegistration interceptorRegistration){
        this.interceptor = interceptorRegistration.getInterceptor();
        this.excludePatterns = interceptorRegistration.getExcludePatterns();
        this.includePatterns = interceptorRegistration.getIncludePatterns();
    }
}
