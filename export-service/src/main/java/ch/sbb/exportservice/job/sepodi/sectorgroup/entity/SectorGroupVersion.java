package ch.sbb.exportservice.job.sepodi.sectorgroup.entity;

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
public class SectorGroupVersion extends BaseEntity {

  private Long id;

  private String sloid;

  private String trafficPointSloid;

  private LocalDate validFrom;

  private LocalDate validTo;

  private String designation;

  private Double length;

  private Status status;

}
