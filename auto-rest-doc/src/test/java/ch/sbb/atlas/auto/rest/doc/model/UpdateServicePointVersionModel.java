package ch.sbb.atlas.auto.rest.doc.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@FieldNameConstants
@Schema(name = "UpdateServicePointVersion")
public class UpdateServicePointVersionModel extends ServicePointVersionModel {


  @Valid
  private GeolocationBaseCreateModel servicePointGeolocation;


}
