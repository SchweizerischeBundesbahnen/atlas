package ch.sbb.exportservice.entity;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import ch.sbb.exportservice.entity.geolocation.TrafficPointElementGeolocation;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
public class TrafficPointElementVersion extends BaseEntity {

  private Long id;

  private String designation;

  private String designationOperational;

  private Double length;

  private Double boardingAreaHeight;

  private Double compassDirection;

  private TrafficPointElementType trafficPointElementType;

  private ServicePointNumber servicePointNumber;
  private String sloid;

  private String parentSloid;

  private TrafficPointElementGeolocation trafficPointElementGeolocation;
  private LocalDate validFrom;
  private LocalDate validTo;

}
