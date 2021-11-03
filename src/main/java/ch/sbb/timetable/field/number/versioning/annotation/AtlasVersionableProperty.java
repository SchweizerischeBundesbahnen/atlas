package ch.sbb.timetable.field.number.versioning.annotation;

import ch.sbb.timetable.field.number.versioning.model.VersionableProperty.RelationType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a property to be versioned. See {@link AtlasVersionable}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface AtlasVersionableProperty {

  String key() default "";

  RelationType relationType() default RelationType.NONE;

  /**
   * Used to specify which field of the related Object ({@link RelationType#ONE_TO_MANY} or {@link RelationType#ONE_TO_ONE})
   * must be versioned. See {@link AtlasVersionable}.
   */
  String[] relationsFields() default {};
}
