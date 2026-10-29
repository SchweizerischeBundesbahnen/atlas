package ch.sbb.atlas.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field that may only be populated on create and changed on update by users with at least the SUPERVISOR role of the
 * owning application. The enforcement itself is done by the validator of the respective module, which resolves the annotated
 * fields by reflection.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface SupervisorOnly {

}
