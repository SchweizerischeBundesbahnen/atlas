package ch.sbb.atlas.imports.model;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.deserializer.LocalDateDeserializer;
import ch.sbb.atlas.imports.bulk.BulkImportErrors;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.UpdateGeolocationModel;
import ch.sbb.atlas.imports.bulk.Validatable;
import ch.sbb.atlas.imports.model.TrafficPointUpdateCsvModel.Fields;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
@JsonPropertyOrder({Fields.sloid, Fields.validFrom, Fields.validTo, Fields.designation,
    Fields.designationOperational, Fields.length, Fields.boardingAreaHeight, Fields.compassDirection,
    Fields.east, Fields.north, Fields.spatialReference, Fields.height, Fields.parentSloid})
public class TrafficPointUpdateCsvModel implements Validatable<TrafficPointUpdateCsvModel>, UpdateGeolocationModel {

  private String sloid;

  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validFrom;

  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validTo;

  private String designation;

  private String designationOperational;

  private Double length;

  private Double boardingAreaHeight;

  private Double compassDirection;

  private Double east;

  private Double north;

  private SpatialReference spatialReference;

  private Double height;

  private String parentSloid;

  @Override
  public List<BulkImportError> validate() {
    List<BulkImportError> errors = new ArrayList<>();
    if (sloid == null) {
      errors.add(BulkImportErrors.notNull(Fields.sloid));
    }
    if (validFrom == null) {
      errors.add(BulkImportErrors.notNull(Fields.validFrom));
    }
    if (validTo == null) {
      errors.add(BulkImportErrors.notNull(Fields.validTo));
    }
    return errors;
  }

  @Override
  public List<UniqueField<TrafficPointUpdateCsvModel>> uniqueFields() {
    return List.of(new UniqueField<>(Fields.sloid, TrafficPointUpdateCsvModel::getSloid));
  }

}
