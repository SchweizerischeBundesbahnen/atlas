package ch.sbb.atlas.redact;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for {@link RedactAspect}.
 * Mark a parameter or field with this annotation to check the users rights on a given BusinessOrganisation
 */
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedactBySboid {

  ApplicationType application();
}
