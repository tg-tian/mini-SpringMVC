package org.tg.web.handler;

import org.springframework.core.Ordered;

import javax.servlet.http.HttpServletRequest;

public interface HandlerMapping extends Ordered {

    HandlerExecutionChain getHandlerExecutionChain(HttpServletRequest httpServletRequest) throws Exception;
}
