package ch.sbb.exportservice.job.sepodi.sector.entity;

import ch.sbb.atlas.model.Status;
import ch.sbb.exportservice.job.sepodi.GeolocationBaseEntity;
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
public class SectorVersion extends GeolocationBaseEntity {

  private Long id;

  private Double edgeHeight;

  private String sloid;

  private String trafficPointSloid;

  private LocalDate validFrom;

  private LocalDate validTo;

  private String designation;

  private Double length;

  private Status status;

}
