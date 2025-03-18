package ch.sbb.exportservice.job.relation;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.enumeration.TactileVisualAttributeType;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.entity.BaseEntity;
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
public class RelationVersion extends BaseEntity {

  private Long id;

  private String sloid;

  private String parentServicePointSloid;

  private ServicePointNumber parentServicePointNumber;

  private String referencePointSloid;

  private TactileVisualAttributeType tactileVisualMarks;

  private StandardAttributeType contrastingAreas;

  private StepFreeAccessAttributeType stepFreeAccess;

  private ReferencePointElementType referencePointElementType;

  private LocalDate validFrom;

  private LocalDate validTo;


}
