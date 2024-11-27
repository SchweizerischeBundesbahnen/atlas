package ch.sbb.atlas.imports.model.create;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.deserializer.LocalDateDeserializer;
import ch.sbb.atlas.imports.annotation.CopyFromCurrentVersion;
import ch.sbb.atlas.imports.annotation.CopyFromCurrentVersion.Mapping;
import ch.sbb.atlas.imports.annotation.DefaultMapping;
import ch.sbb.atlas.imports.annotation.Nulling;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.UpdateGeolocationModel;
import ch.sbb.atlas.imports.bulk.Validatable;
import ch.sbb.atlas.imports.model.create.ServicePointCreateCsvModel.Fields;
import ch.sbb.atlas.servicepoint.Country;
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
        Fields.designationLong, Fields.stopPointType, Fields.freightServicePoint, Fields.borderPoint, Fields.operatingPointType,
        Fields.operatingPointTechnicalTimetableType, Fields.meansOfTransport, Fields.categories,
        Fields.operatingPointTrafficPointType, Fields.sortCodeOfDestinationStation, Fields.businessOrganisation, Fields.fotComment,
        Fields.east, Fields.north, Fields.spatialReference, Fields.height})
@CopyFromCurrentVersion({
        @Mapping(target = "id", current = "id"),
        @Mapping(target = "etagVersion", current = "version"),
        @Mapping(target = "abbreviation", current = "abbreviation"),
        @Mapping(target = "operatingPointRouteNetwork", current = "operatingPointRouteNetwork"),
        @Mapping(target = "operatingPointKilometerMasterNumber", current = "operatingPointKilometerMaster.value")
})
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

    @Nulling
    private String designationLong;

    @Nulling
    private StopPointType stopPointType;

    @Nulling
    private Boolean freightServicePoint;

    @Nulling
    private Boolean borderPoint;

    @Nulling
    private OperatingPointType operatingPointType;

    @Nulling
    private OperatingPointTechnicalTimetableType operatingPointTechnicalTimetableType;

    @Nulling
    private Set<MeanOfTransport> meansOfTransport;

    @Nulling
    private Set<Category> categories;

    @Nulling
    private OperatingPointTrafficPointType operatingPointTrafficPointType;

    @Nulling
    private String sortCodeOfDestinationStation;

    @DefaultMapping
    private String businessOrganisation;

    @Nulling
    private String fotComment;

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
        return List.of();
    }

    @Override
    public List<UniqueField<ServicePointCreateCsvModel>> uniqueFields() {
        return List.of();
    }
}
