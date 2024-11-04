package ch.sbb.atlas.imports.bulk;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Supports setting to null indicated via <null> in a BulkImport value
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Nulling {

  /**
   * Property in target model to be set to null
   */
  String property() default "";

}
