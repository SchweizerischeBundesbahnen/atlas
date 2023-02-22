package ch.sbb.atlas.base.service.versioning.annotation;

import static ch.sbb.atlas.base.service.versioning.model.VersionableProperty.RelationType.NONE;

import ch.sbb.atlas.base.service.versioning.model.Versionable;
import ch.sbb.atlas.base.service.versioning.model.VersionableProperty.RelationType;
import ch.sbb.atlas.base.service.versioning.service.VersionableService;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * Mark a property to be versioned. See {@link AtlasVersionable}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface AtlasVersionableProperty {

  String key() default "";

  /**
   * Used to specify that the property must be ignored during the compare between objects
   */
  boolean ignoreDiff() default false;

  /**
   * Used to specify that the property must not be overridden. Note that this property works only when the versioning is executed
   * with the option "deletePropertyWhenNull" otherwise it is ignored during the versioning process. See
   * {@link VersionableService#versioningObjectsWithDeleteByNullProperties(Versionable, Versionable, List)}
   */
  boolean doNotOverride() default false;

  RelationType relationType() default NONE;

  /**
   * Used to specify which field of the related Object ({@link RelationType#ONE_TO_MANY} or {@link RelationType#ONE_TO_ONE}) must
   * be versioned. See {@link AtlasVersionable}.
   */
  String[] relationsFields() default {};
}
