package ch.sbb.prm.directory.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a property to be not used by Reduced PRM objects see
 * <a href="https://confluence.sbb.ch/x/vgdpl#DataFactMatrix-ErfassungsvariantenjeVerkehrsmittel">Erfassungsvarianten je
 * Verkehrsmittel</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface PrmVariant {

  /**
   * Used to specify if the annotated value is mandatory
   */
  boolean nullable() default true;

  RecordingVariant variant();

}
