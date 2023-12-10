package org.tg.web;

import javax.servlet.ServletContext;

public interface WebApplicationInitializer {

    void onStartUp(ServletContext servletContext);
}
