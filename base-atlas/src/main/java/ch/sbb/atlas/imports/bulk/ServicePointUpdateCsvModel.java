package ch.sbb.atlas.imports.bulk;

import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel.Fields;
import ch.sbb.atlas.imports.servicepoint.deserializer.LocalDateDeserializer;
import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepoint.enumeration.Category;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDate;
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
    Fields.operatingPointTrafficPointType, Fields.sortCodeOfDestinationStation, Fields.businessOrganisation, Fields.fotComment,
    Fields.east, Fields.north, Fields.spatialReference, Fields.height})
public class ServicePointUpdateCsvModel implements BulkImportContainer {

  private String sloid;

  private Integer number;

  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validFrom;

  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate validTo;

  private String designationOfficial;

  private String designationLong;

  private StopPointType stopPointType;

  private Boolean freightServicePoint;

  private OperatingPointType operatingPointType;

  private OperatingPointTechnicalTimetableType operatingPointTechnicalTimetableType;

  private Set<MeanOfTransport> meansOfTransport;

  private Set<Category> categories;

  private OperatingPointTrafficPointType operatingPointTrafficPointType;

  private String sortCodeOfDestinationStation;

  private String businessOrganisation;

  private String fotComment;

  private Double east;

  private Double north;

  private SpatialReference spatialReference;

  private Double height;

}
