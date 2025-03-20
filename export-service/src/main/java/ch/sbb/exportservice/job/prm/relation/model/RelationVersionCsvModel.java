package ch.sbb.exportservice.job.prm.relation.model;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.enumeration.TactileVisualAttributeType;
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

  private TactileVisualAttributeType tactileVisualMarks;

  private StandardAttributeType contrastingAreas;

  private StepFreeAccessAttributeType stepFreeAccess;

  private ReferencePointElementType referencePointElementType;

  private String validFrom;

  private String validTo;

  private String creationDate;

  private String editionDate;

}
