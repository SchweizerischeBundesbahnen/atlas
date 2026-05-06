package ch.sbb.atlas.annotation;

import ch.sbb.atlas.configuration.Role;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.annotation.Secured;

/**
 * Access restricted to admin users
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Secured(Role.SECURED_FOR_ATLAS_ADMIN)
public @interface AdminOnly {

}
