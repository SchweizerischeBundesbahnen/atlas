package ch.sbb.atlas.imports.annotation;

import ch.sbb.atlas.imports.bulk.BulkImportDataMapper;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates fieldName based copying of value property
 * For use with {@link BulkImportDataMapper}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface DefaultMapping {

}
