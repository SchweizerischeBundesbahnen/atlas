package ch.sbb.line.directory.module.line.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field that requires a dedicated write permission instead of the regular LIDI write permission on the business
 * organisation. The permission is evaluated by {@link LineV3FieldPermissionService}, which resolves the annotated fields by
 * reflection.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FieldPermission {

  LineV3FieldPermission value();

}
