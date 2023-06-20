package ch.sbb.exportservice.entity;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.exportservice.entity.geolocation.LoadingPointGeolocation;
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
@AtlasVersionable
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
