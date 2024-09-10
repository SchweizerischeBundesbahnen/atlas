package ch.sbb.atlas.user.administration.security.aspect;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation should be used with a bulkImport execution. This annotation creates a temporarily downgraded JwtAuthentication
 * for the specified <b>userName</b>  marked with {@link RunAsUserParameter}.
 * <p>
 * To use this annotation in your service make sure that a method String parameter is marked with {@link RunAsUserParameter}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface RunAsUser {

}
