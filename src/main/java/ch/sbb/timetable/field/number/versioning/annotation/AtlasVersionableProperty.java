package ch.sbb.timetable.field.number.versioning.annotation;

import ch.sbb.timetable.field.number.versioning.model.VersionableProperty.RelationType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface AtlasVersionableProperty {
  String key() default "";
  String[] relationsFields() default {};
  RelationType relationType() default RelationType.NONE;
}
