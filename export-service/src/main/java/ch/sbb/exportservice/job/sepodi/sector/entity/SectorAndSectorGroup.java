package ch.sbb.exportservice.job.sepodi.sector.entity;

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
public class SectorAndSectorGroup extends SectorVersion {

  private String type;

  private String relatedGroups;

  private String relatedSectors;

}
