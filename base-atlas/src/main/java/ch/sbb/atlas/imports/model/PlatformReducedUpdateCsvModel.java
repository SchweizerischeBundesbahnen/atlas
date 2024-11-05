package ch.sbb.atlas.imports.model;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.deserializer.LocalDateDeserializer;
import ch.sbb.atlas.imports.annotation.CopyFromCurrentVersion;
import ch.sbb.atlas.imports.annotation.CopyFromCurrentVersion.Mapping;
import ch.sbb.atlas.imports.bulk.BulkImportErrors;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.annotation.DefaultMapping;
import ch.sbb.atlas.imports.annotation.Nulling;
import ch.sbb.atlas.imports.bulk.Validatable;
import ch.sbb.atlas.imports.model.PlatformReducedUpdateCsvModel.Fields;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@FieldNameConstants
@EqualsAndHashCode
@JsonPropertyOrder({Fields.sloid, Fields.validFrom, Fields.validTo, Fields.additionalInformation, Fields.height,
    Fields.inclinationLongitudinal, Fields.infoOpportunities, Fields.partialElevation, Fields.tactileSystem,
    Fields.vehicleAccess, Fields.wheelchairAreaLength, Fields.wheelchairAreaWidth})
@CopyFromCurrentVersion({
    @Mapping(target = "id", current = "id"),
    @Mapping(target = "etagVersion", current = "version"),
    @Mapping(target = "parentServicePointSloid", current = "parentServicePointSloid")
})
public class PlatformReducedUpdateCsvModel implements Validatable<PlatformReducedUpdateCsvModel> {

  private String sloid;

  @DefaultMapping
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validFrom;

  @DefaultMapping
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validTo;

  @DefaultMapping
  @Nulling
  private String additionalInformation;

  @DefaultMapping
  @Nulling
  private Double height;

  @DefaultMapping
  @Nulling
  private Double inclinationLongitudinal;

  @DefaultMapping
  @Nulling
  private Set<InfoOpportunityAttributeType> infoOpportunities;

  @DefaultMapping
  @Nulling
  private Boolean partialElevation;

  @DefaultMapping
  @Nulling
  private BooleanOptionalAttributeType tactileSystem;

  @DefaultMapping
  @Nulling
  private VehicleAccessAttributeType vehicleAccess;

  @DefaultMapping
  @Nulling
  private Double wheelchairAreaLength;

  @DefaultMapping
  @Nulling
  private Double wheelchairAreaWidth;

  @Override
  public List<BulkImportError> validate() {
    List<BulkImportError> errors = new ArrayList<>();
    mandatoryFieldIsNotNull(sloid, Fields.sloid, errors);
    mandatoryFieldIsNotNull(validFrom, Fields.validFrom, errors);
    mandatoryFieldIsNotNull(validTo, Fields.validTo, errors);
    return errors;
  }

  private void mandatoryFieldIsNotNull(Object fieldValue, String fieldName, List<BulkImportError> errors) {
    if (fieldValue == null) {
      errors.add(BulkImportErrors.notNull(fieldName));
    }
  }

  @Override
  public List<UniqueField<PlatformReducedUpdateCsvModel>> uniqueFields() {
    return List.of(new UniqueField<>(Fields.sloid, PlatformReducedUpdateCsvModel::getSloid));
  }

}
