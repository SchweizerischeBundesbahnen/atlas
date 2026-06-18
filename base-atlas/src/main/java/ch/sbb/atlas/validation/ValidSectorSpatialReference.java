package ch.sbb.atlas.validation;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.api.servicepoint.sector.CreateSectorVersionModel;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {ValidSectorSpatialReference.Validator.class})
public @interface ValidSectorSpatialReference {

  String ATLAS_CONSTRAINT_VALID_SECTOR_SPATIAL_REFERENCE = "Only LV95 and WGS84 are allowed";

  String message() default ATLAS_CONSTRAINT_VALID_SECTOR_SPATIAL_REFERENCE;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  class Validator implements ConstraintValidator<ValidSectorSpatialReference, CreateSectorVersionModel> {

    @Override
    public boolean isValid(CreateSectorVersionModel model, ConstraintValidatorContext context) {
      if (model == null || model.getSectorGeolocation() == null) {
        return true;
      }

      SpatialReference spatialReference = model.getSectorGeolocation().getSpatialReference();
      return spatialReference == SpatialReference.LV95 || spatialReference == SpatialReference.WGS84;
    }
  }
}
