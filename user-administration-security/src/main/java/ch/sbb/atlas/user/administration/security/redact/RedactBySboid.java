package ch.sbb.atlas.user.administration.security.redact;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedactBySboid {

  ApplicationType application();
}
