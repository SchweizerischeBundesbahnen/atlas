package ch.sbb.exportservice.job.sepodi.sector.model;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.model.Status;
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
public class SectorAndSectorGroupCsvModel {

  private String sloid;

  private String type;

  private String trafficPointSloid;

  private String validFrom;

  private String validTo;

  private String designation;

  private Double length;

  private Double edgeHeight;

  private Double lv95East;

  private Double lv95North;

  private Double wgs84East;

  private Double wgs84North;

  private Double height;

  private SpatialReference spatialReference;

  private String relatedGroups;

  private String relatedSectors;

  private Status status;

  private String creationDate;

  private String editionDate;


}
