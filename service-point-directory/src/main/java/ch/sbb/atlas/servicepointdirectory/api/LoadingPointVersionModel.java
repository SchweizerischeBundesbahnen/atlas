package ch.sbb.atlas.servicepointdirectory.api;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.model.BaseVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.mapper.GeolocationMapper;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import ch.sbb.atlas.validation.DatesValidator;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
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
@Schema(name = "LoadingPointVersion")
public class LoadingPointVersionModel extends BaseVersionModel implements DatesValidator {

  @Schema(description = "Technical identifier", accessMode = AccessMode.READ_ONLY, example = "1")
  private Long id;

  @NotNull
  @Schema(description = "Loading Point Number", example = "4201")
  private Integer number;

  @NotNull
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_12)
  @Schema(description = "Designation", example = "Piazzale")
  private String designation;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_35)
  @Schema(description = "Designation Long", example = "Piazzale")
  private String designationLong;

  @Schema(description = "Is a connectionPoint")
  private boolean connectionPoint;

  @NotNull
  @Valid
  private ServicePointNumber servicePointNumber;

  private GeolocationBaseModel loadingPointGeolocation;

  @JsonInclude
  @Schema(description = "LoadingPoint has a Geolocation")
  public boolean isHasGeolocation() {
    return loadingPointGeolocation != null;
  }

  @NotNull
  private LocalDate validFrom;

  @NotNull
  private LocalDate validTo;

  public static LoadingPointVersionModel fromEntity(LoadingPointVersion loadingPointVersion) {
    return LoadingPointVersionModel.builder()
        .id(loadingPointVersion.getId())
        .number(loadingPointVersion.getNumber())
        .designation(loadingPointVersion.getDesignation())
        .designationLong(loadingPointVersion.getDesignationLong())
        .connectionPoint(loadingPointVersion.isConnectionPoint())
        .servicePointNumber(loadingPointVersion.getServicePointNumber())
        .validFrom(loadingPointVersion.getValidFrom())
        .validTo(loadingPointVersion.getValidTo())
        .loadingPointGeolocation(GeolocationMapper.toModel(loadingPointVersion.getLoadingPointGeolocation()))
        .creationDate(loadingPointVersion.getCreationDate())
        .creator(loadingPointVersion.getCreator())
        .editionDate(loadingPointVersion.getEditionDate())
        .editor(loadingPointVersion.getEditor())
        .build();
  }

}
