package org.tg.web.annotation;

import org.springframework.context.annotation.Import;
import org.tg.web.support.DelegatingWebMvcConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(DelegatingWebMvcConfiguration.class)
public @interface EnableWebMvc {
}
