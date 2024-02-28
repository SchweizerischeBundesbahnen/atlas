package ch.sbb.atlas.export.model.prm;

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
public class RelationVersionCsvModel {

  private String elementSloid;

  private String parentSloidServicePoint;

  private Integer parentNumberServicePoint;

  private String referencePointSloid;

  private String tactileVisualMarks;

  private String contrastingAreas;

  private String stepFreeAccess;

  private String referencePointElementType;

  private String validFrom;

  private String validTo;

  private String creationDate;

  private String editionDate;

}
