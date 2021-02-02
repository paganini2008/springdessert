package org.springdessert.framework.jdbc.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springdessert.framework.jdbc.ApplicationContextUtils;
import org.springdessert.framework.jdbc.DaoScannerRegistrar;
import org.springframework.context.annotation.Import;

/**
 * 
 * DaoScan
 *
 * @author Jimmy Hoff
 * @since 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({ DaoScannerRegistrar.class, ApplicationContextUtils.class })
public @interface DaoScan {

	String[] basePackages();

}
