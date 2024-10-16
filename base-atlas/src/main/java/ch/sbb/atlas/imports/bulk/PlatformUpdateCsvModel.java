package ch.sbb.atlas.imports.bulk;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.deserializer.LocalDateDeserializer;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.PlatformUpdateCsvModel.Fields;
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
public class PlatformUpdateCsvModel implements Validatable<PlatformUpdateCsvModel> {

  private String sloid;

  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validFrom;

  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validTo;

  private String additionalInformation;

  private Double height;

  private Double inclinationLongitudinal;

  private Set<InfoOpportunityAttributeType> infoOpportunities;

  private Boolean partialElevation;

  private BooleanOptionalAttributeType tactileSystem;

  private VehicleAccessAttributeType vehicleAccess;

  private Double wheelchairAreaLength;

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
  public List<UniqueField<PlatformUpdateCsvModel>> uniqueFields() {
    return List.of(new UniqueField<>(Fields.sloid, PlatformUpdateCsvModel::getSloid));
  }

}
