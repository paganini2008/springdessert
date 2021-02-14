package indi.atlantis.framework.jdbc.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import indi.atlantis.framework.jdbc.ApplicationContextUtils;
import indi.atlantis.framework.jdbc.DaoScannerRegistrar;

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
