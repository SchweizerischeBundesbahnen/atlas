package ch.sbb.atlas.annotation;

import ch.sbb.atlas.configuration.Role;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Forbids access for unauthorized client credential from api-auth-gateway
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@PreAuthorize("!hasAuthority('" + Role.AUTHORITY_UNAUTHORIZED + "')")
public @interface AuthorizedOnly {

}
