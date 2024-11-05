package ch.sbb.atlas.imports.model;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.deserializer.LocalDateDeserializer;
import ch.sbb.atlas.imports.annotation.AdditionalDefaultMappings;
import ch.sbb.atlas.imports.annotation.DefaultMapping;
import ch.sbb.atlas.imports.annotation.Nulling;
import ch.sbb.atlas.imports.bulk.BulkImportErrors;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.UpdateGeolocationModel;
import ch.sbb.atlas.imports.bulk.Validatable;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel.Fields;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
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
@JsonPropertyOrder({Fields.sloid, Fields.number, Fields.validFrom, Fields.validTo, Fields.designationOfficial,
    Fields.designationLong, Fields.stopPointType, Fields.freightServicePoint, Fields.operatingPointType,
    Fields.operatingPointTechnicalTimetableType, Fields.meansOfTransport, Fields.categories,
    Fields.operatingPointTrafficPointType, Fields.sortCodeOfDestinationStation, Fields.businessOrganisation,
    Fields.east, Fields.north, Fields.spatialReference, Fields.height})
@AdditionalDefaultMappings({
    @DefaultMapping(target = "id", current = "id"),
    @DefaultMapping(target = "etagVersion", current = "version"),
    @DefaultMapping(target = "abbreviation", current = "abbreviation"),
    @DefaultMapping(target = "operatingPointRouteNetwork", current = "operatingPointRouteNetwork"),
    @DefaultMapping(target = "operatingPointKilometerMasterNumber", current = "operatingPointKilometerMaster.value")
})
public class ServicePointUpdateCsvModel implements Validatable<ServicePointUpdateCsvModel>, UpdateGeolocationModel {

  private String sloid;

  private Integer number;

  @DefaultMapping
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validFrom;

  @DefaultMapping
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validTo;

  @DefaultMapping
  @Nulling
  private String designationOfficial;

  @DefaultMapping
  @Nulling
  private String designationLong;

  @DefaultMapping
  @Nulling
  private StopPointType stopPointType;

  @DefaultMapping
  @Nulling
  private Boolean freightServicePoint;

  @DefaultMapping
  @Nulling
  private OperatingPointType operatingPointType;

  @DefaultMapping
  @Nulling
  private OperatingPointTechnicalTimetableType operatingPointTechnicalTimetableType;

  @DefaultMapping
  @Nulling
  private Set<MeanOfTransport> meansOfTransport;

  @DefaultMapping
  @Nulling
  private Set<Category> categories;

  @DefaultMapping
  @Nulling
  private OperatingPointTrafficPointType operatingPointTrafficPointType;

  @DefaultMapping
  @Nulling
  private String sortCodeOfDestinationStation;

  @DefaultMapping
  private String businessOrganisation;

  @Nulling(property = "servicePointGeolocation")
  private Double east;

  @Nulling(property = "servicePointGeolocation")
  private Double north;

  @Nulling(property = "servicePointGeolocation")
  private SpatialReference spatialReference;

  @Nulling(property = "servicePointGeolocation.height")
  private Double height;

  @Override
  public List<BulkImportError> validate() {
    List<BulkImportError> errors = new ArrayList<>();
    if ((sloid == null) == (number == null)) {
      errors.add(BulkImportErrors.sloidXorNumber());
    }
    if (number != null) {
      try {
        ServicePointNumber.ofNumberWithoutCheckDigit(number);
      } catch (Exception e) {
        errors.add(BulkImportErrors.invalidServicePointNumber());
      }
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
  public List<UniqueField<ServicePointUpdateCsvModel>> uniqueFields() {
    return List.of(new UniqueField<>(Fields.sloid, ServicePointUpdateCsvModel::getSloid),
        new UniqueField<>(Fields.number, ServicePointUpdateCsvModel::getNumber));
  }
}
