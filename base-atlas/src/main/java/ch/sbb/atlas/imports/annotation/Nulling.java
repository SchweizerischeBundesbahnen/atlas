package ch.sbb.atlas.imports.annotation;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateDataMapper;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Supports setting to null indicated via <null> in a BulkImport value
 * For use with {@link BulkImportUpdateDataMapper}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Nulling {

  /**
   * Property in target model to be set to null
   */
  String property() default "";

}
