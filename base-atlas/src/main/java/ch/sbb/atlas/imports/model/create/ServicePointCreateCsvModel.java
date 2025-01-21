package ch.sbb.atlas.imports.model.create;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.deserializer.LocalDateDeserializer;
import ch.sbb.atlas.imports.annotation.DefaultMapping;
import ch.sbb.atlas.imports.bulk.BulkImportErrors;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.UpdateGeolocationModel;
import ch.sbb.atlas.imports.bulk.Validatable;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.model.create.ServicePointCreateCsvModel.Fields;
import ch.sbb.atlas.servicepoint.Country;
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
@JsonPropertyOrder({Fields.numberShort, Fields.uicCountryCode, Fields.validFrom, Fields.validTo, Fields.designationOfficial,
    Fields.designationLong, Fields.stopPointType, Fields.freightServicePoint, Fields.operatingPointType,
    Fields.operatingPointTechnicalTimetableType, Fields.meansOfTransport, Fields.categories,
    Fields.operatingPointTrafficPointType, Fields.sortCodeOfDestinationStation, Fields.businessOrganisation,
    Fields.east, Fields.north, Fields.spatialReference, Fields.height})
public class ServicePointCreateCsvModel implements Validatable<ServicePointCreateCsvModel>, UpdateGeolocationModel {

  @DefaultMapping
  private Integer numberShort;

  private Integer uicCountryCode;

  @DefaultMapping
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validFrom;

  @DefaultMapping
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validTo;

  @DefaultMapping
  private String designationOfficial;

  @DefaultMapping
  private String designationLong;

  @DefaultMapping
  private StopPointType stopPointType;

  @DefaultMapping
  private boolean freightServicePoint;

  @DefaultMapping
  private OperatingPointType operatingPointType;

  @DefaultMapping
  private OperatingPointTechnicalTimetableType operatingPointTechnicalTimetableType;

  @DefaultMapping
  private Set<MeanOfTransport> meansOfTransport;

  @DefaultMapping
  private Set<Category> categories;

  @DefaultMapping
  private OperatingPointTrafficPointType operatingPointTrafficPointType;

  @DefaultMapping
  private String sortCodeOfDestinationStation;

  @DefaultMapping
  private String businessOrganisation;

  private Double east;

  private Double north;

  private SpatialReference spatialReference;

  private Double height;

  @Override
  public List<BulkImportError> validate() {
    List<BulkImportError> errors = new ArrayList<>();

    validateNumberShort(errors);
    validateUicCountryCode(errors);
    validateNotNullFields(errors);
    validateHeight(errors);
    validateGeography(errors);
    validateStopPointType(errors);

    return errors;
  }

  private void validateNumberShort(List<BulkImportError> errors) {
    if (isNumberShortRequired(uicCountryCode)) {
      if (numberShort == null) {
        errors.add(BulkImportErrors.notNull(Fields.numberShort));
      } else if (numberShort < AtlasFieldLengths.MIN_NUMBER || numberShort > AtlasFieldLengths.MAX_FIVE_DIGITS_NUMBER) {
        errors.add(BulkImportErrors.invalidNumberShort(Fields.numberShort));
      }
    }
  }

  private void validateUicCountryCode(List<BulkImportError> errors) {
    if (uicCountryCode == null) {
      errors.add(BulkImportErrors.notNull(Fields.uicCountryCode));
    } else if (Country.from(uicCountryCode) == null) {
      errors.add(BulkImportErrors.isUicCountryCodeValid(Fields.uicCountryCode));
    }
  }

  private void validateNotNullFields(List<BulkImportError> errors) {
    if (designationOfficial == null) {
      errors.add(BulkImportErrors.notNull(Fields.designationOfficial));
    }
    if (businessOrganisation == null) {
      errors.add(BulkImportErrors.notNull(Fields.businessOrganisation));
    }
    if (validFrom == null) {
      errors.add(BulkImportErrors.notNull(ServicePointUpdateCsvModel.Fields.validFrom));
    }
    if (validTo == null) {
      errors.add(BulkImportErrors.notNull(ServicePointUpdateCsvModel.Fields.validTo));
    }
  }

  private void validateHeight(List<BulkImportError> errors) {
    if (height != null && height > AtlasFieldLengths.MAX_HEIGHT) {
      errors.add(BulkImportErrors.invalidHeight(Fields.height));
    }
  }

  private void validateGeography(List<BulkImportError> errors) {
    if (!isGeographyValid()) {
      errors.add(BulkImportErrors.invalidGeography(Fields.height));
    }
  }

  private void validateStopPointType(List<BulkImportError> errors) {
    if (!isStopPointTypeValid()) {
      errors.add(BulkImportErrors.isMeansOfTransportMissing(Fields.stopPointType));
    }
  }

  private boolean isNumberShortRequired(Integer uicCountryCode) {
    return uicCountryCode == null || (uicCountryCode != 85 && (uicCountryCode < 11 || uicCountryCode > 14));
  }

  private boolean isGeographyValid() {
    return (north == null && east == null && spatialReference == null) || (north != null && east != null
        && spatialReference != null);
  }

  private boolean isStopPointTypeValid() {
    return (stopPointType == null && meansOfTransport == null) || (stopPointType != null && meansOfTransport != null);
  }

  @Override
  public List<UniqueField<ServicePointCreateCsvModel>> uniqueFields() {
    return List.of(new UniqueField<>("number", ServicePointCreateCsvModel::getNumber));
  }

  public String getNumber() {
    if (Country.from(uicCountryCode) == null || uicCountryCode == null) {
      return null;
    }
    return ServicePointNumber.of(Country.from(uicCountryCode), numberShort).asString();
  }
}
