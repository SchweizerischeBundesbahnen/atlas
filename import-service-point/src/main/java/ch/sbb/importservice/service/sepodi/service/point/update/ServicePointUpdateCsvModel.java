package ch.sbb.importservice.service.sepodi.service.point.update;

import ch.sbb.atlas.imports.BulkImportContainer;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTechnicalTimetableType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointType;
import ch.sbb.atlas.servicepoint.enumeration.StopPointType;
import ch.sbb.importservice.service.sepodi.service.point.update.ServicePointUpdateCsvModel.Fields;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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

  private String validFrom;

  private String validTo;

  private String designationOfficial;

  private String designationLong;

  private StopPointType stopPointType;

  private boolean freightServicePoint;

  private boolean borderPoint;

  private OperatingPointType operatingPointType;

  private OperatingPointTechnicalTimetableType operatingPointTechnicalTimetableType;

  private String meansOfTransport;

  private String categories;

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
