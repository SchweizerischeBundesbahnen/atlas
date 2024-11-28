package ch.sbb.atlas.redact;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for {@link RedactAspect}.
 * The aspect is designed to mask parts of Objects.
 * To use this feature, annotate the method of your service with @Redacted. This will trigger the whole redacting mechanism.
 * <br>
 * The return object and the fields you want to redact, should also be annotated. This works with nested objects, Lists and Sets.
 * Unimplemented redacting scenarios will raise an exception.
 * <br>
 * If you need to check the users rights on a given BusinessOrganisation take a look at {@link RedactBySboid}
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Redacted {

  boolean showFirstChar() default false;

}
