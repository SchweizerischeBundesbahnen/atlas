package ch.sbb.atlas.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker Annotation to declare an endpoint as allowed for unauthorized access.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface UnauthorizedAllowed {

  FurtherLimitations limitations();

  enum FurtherLimitations {
    NONE,
    REDACTED,
  }

}
