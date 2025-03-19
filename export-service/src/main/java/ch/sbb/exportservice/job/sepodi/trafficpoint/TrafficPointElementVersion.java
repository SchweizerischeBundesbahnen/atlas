package ch.sbb.exportservice.job.sepodi.trafficpoint;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import ch.sbb.exportservice.job.BaseEntity;
import ch.sbb.exportservice.job.sepodi.SharedBusinessOrganisation;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

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

  private SharedBusinessOrganisation servicePointSharedBusinessOrganisation;

  private String parentSloid;

  private String parentSloidServicePoint;

  private String servicePointDesignationOfficial;

  private TrafficPointElementGeolocation trafficPointElementGeolocation;

  private LocalDate validFrom;

  private LocalDate validTo;

}
