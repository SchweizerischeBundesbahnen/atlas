package ch.sbb.atlas.imports.model.create;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.deserializer.LocalDateDeserializer;
import ch.sbb.atlas.imports.annotation.DefaultMapping;
import ch.sbb.atlas.imports.bulk.BulkImportErrors;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.UpdateGeolocationModel;
import ch.sbb.atlas.imports.bulk.Validatable;
import ch.sbb.atlas.imports.model.create.TrafficPointCreateCsvModel.Fields;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
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
@JsonPropertyOrder({Fields.sloid, Fields.validFrom, Fields.validTo,
    Fields.trafficPointElementType, Fields.parentSloid, Fields.stopPointSloid, Fields.number,
    Fields.designation, Fields.designationOperational, Fields.length, Fields.boardingAreaHeight, Fields.compassDirection,
    Fields.east, Fields.north, Fields.spatialReference, Fields.height})
public class TrafficPointCreateCsvModel implements Validatable<TrafficPointCreateCsvModel>, UpdateGeolocationModel {

  @DefaultMapping
  private String sloid;

  @DefaultMapping
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validFrom;

  @DefaultMapping
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validTo;

  @DefaultMapping
  private TrafficPointElementType trafficPointElementType;

  @DefaultMapping
  private String parentSloid;

  private String stopPointSloid;

  private Integer number;

  @DefaultMapping
  private String designation;

  @DefaultMapping
  private String designationOperational;

  @DefaultMapping
  private Double length;

  @DefaultMapping
  private Double boardingAreaHeight;

  @DefaultMapping
  private Double compassDirection;

  private Double east;

  private Double north;

  private SpatialReference spatialReference;

  private Double height;

  @Override
  public List<BulkImportError> validate() {
    List<BulkImportError> errors = new ArrayList<>();
    if ((stopPointSloid == null) == (number == null)) {
      errors.add(BulkImportErrors.stopPointSloidXorNumber());
    }
    if (trafficPointElementType == null) {
      errors.add(BulkImportErrors.notNull(TrafficPointCreateCsvModel.Fields.trafficPointElementType));
    }
    if (validFrom == null) {
      errors.add(BulkImportErrors.notNull(TrafficPointCreateCsvModel.Fields.validFrom));
    }
    if (validTo == null) {
      errors.add(BulkImportErrors.notNull(TrafficPointCreateCsvModel.Fields.validTo));
    }
    return errors;
  }

  @Override
  public List<UniqueField<TrafficPointCreateCsvModel>> uniqueFields() {
    return List.of(new UniqueField<>(TrafficPointCreateCsvModel.Fields.sloid, TrafficPointCreateCsvModel::getSloid));
  }

}
