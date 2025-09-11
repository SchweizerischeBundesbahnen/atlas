package ch.sbb.exportservice.job.sepodi.sector.entity;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.model.Status;
import ch.sbb.exportservice.job.BaseEntity;
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
public class SectorVersion extends BaseEntity {

  private Long id;

  private Double north;

  private Double east;

  private Double height;

  private SpatialReference spatialReference;

  private Double edgeHeight;

  private String sloid;

  private String trafficPointSloid;

  private LocalDate validFrom;

  private LocalDate validTo;

  private String designation;

  private Double length;

  private Status status;

}
