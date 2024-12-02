package ch.sbb.atlas.imports.model.create;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.deserializer.LocalDateDeserializer;
import ch.sbb.atlas.imports.annotation.DefaultMapping;
import ch.sbb.atlas.imports.annotation.Nulling;
import ch.sbb.atlas.imports.bulk.BulkImportErrors;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.UpdateGeolocationModel;
import ch.sbb.atlas.imports.bulk.Validatable;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.model.create.ServicePointCreateCsvModel.Fields;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    @Nulling
    private String designationLong;

    @DefaultMapping
    @Nulling
    private StopPointType stopPointType;

    @DefaultMapping
    private boolean freightServicePoint;

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
        if (numberShort == null && isNumberShortRequired(uicCountryCode)) {
            errors.add(BulkImportErrors.notNull(Fields.numberShort));
        }
        if (uicCountryCode == null) {
            errors.add(BulkImportErrors.notNull(Fields.uicCountryCode));
        }
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
        return errors;
    }

    @Override
    public List<UniqueField<ServicePointCreateCsvModel>> uniqueFields() {
        return List.of(new UniqueField<>(Fields.numberShort, ServicePointCreateCsvModel::getNumberShort));
    }

    private boolean isNumberShortRequired(Integer uicCountryCode) {
        return uicCountryCode == null || (uicCountryCode != 85 && (uicCountryCode < 11 || uicCountryCode > 14));
    }
}
