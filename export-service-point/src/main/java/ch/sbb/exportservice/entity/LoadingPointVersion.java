package ch.sbb.exportservice.entity;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.entity.geolocation.LoadingPointGeolocation;
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
public class LoadingPointVersion {

  private Long id;

  private Integer number;

  private String designation;

  private String designationLong;

  private boolean connectionPoint;

  private ServicePointNumber servicePointNumber;

  private LoadingPointGeolocation loadingPointGeolocation;
  private LocalDate validFrom;
  private LocalDate validTo;

}
