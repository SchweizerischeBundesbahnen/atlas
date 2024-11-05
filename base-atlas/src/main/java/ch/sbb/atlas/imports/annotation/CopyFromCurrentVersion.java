package ch.sbb.atlas.imports.annotation;

import ch.sbb.atlas.imports.bulk.BulkImportDataMapper;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates class field name based copying of value properties For use with {@link BulkImportDataMapper}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CopyFromCurrentVersion {

  Mapping[] value() default {};

  @Retention(RetentionPolicy.RUNTIME)
  @Target({ElementType.TYPE})
  @interface Mapping {

    String target();

    String current();
  }
}
