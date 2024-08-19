package ch.sbb.atlas.imports.bulk;

import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel.Fields;
import ch.sbb.atlas.imports.servicepoint.deserializer.LocalDateDeserializer;
import ch.sbb.atlas.imports.servicepoint.deserializer.PipedSetDeserializer;
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
    Fields.designationLong, Fields.stopPointType, Fields.freightServicePoint, Fields.borderPoint, Fields.operatingPointType,
    Fields.operatingPointTechnicalTimetableType, Fields.meansOfTransport, Fields.categories,
    Fields.operatingPointTrafficPointType, Fields.sortCodeOfDestinationStation, Fields.businessOrganisation, Fields.fotComment,
    Fields.lv95East, Fields.lv95North, Fields.wgs84East, Fields.wgs84North, Fields.height})
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

  private boolean freightServicePoint;

  // todo: muss weg
  private boolean borderPoint;

  private OperatingPointType operatingPointType;

  private OperatingPointTechnicalTimetableType operatingPointTechnicalTimetableType;

  @JsonDeserialize(using = PipedSetDeserializer.class)
  private Set<MeanOfTransport> meansOfTransport;

  private Set<Category> categories;

  private OperatingPointTrafficPointType operatingPointTrafficPointType;

  private String sortCodeOfDestinationStation;

  private String businessOrganisation;

  private String fotComment;

  private Double lv95East;

  private Double lv95North;

  private Double wgs84East;

  private Double wgs84North;

  private Double height;

}
