package ch.sbb.atlas.auto.rest.doc.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@FieldNameConstants
@Schema(name = "CreateServicePointVersion")
public class DummyCreateServicePointVersionModel extends UpdateServicePointVersionModel {

  @Schema(description = "The country for the service point. Only needed if ServicePointNumber is created automatically",
      example = "SWITZERLAND")
  @NotNull
  private String country;

}
