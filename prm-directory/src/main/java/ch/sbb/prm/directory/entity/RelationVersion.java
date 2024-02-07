package ch.sbb.prm.directory.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.enumeration.TactileVisualAttributeType;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.prm.directory.service.PrmVersionable;
import ch.sbb.prm.directory.service.Relatable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Entity(name = "relation_version")
@AtlasVersionable
public class RelationVersion extends BasePrmEntityVersion implements Relatable, PrmVersionable {

  private static final String VERSION_SEQ = "relation_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @AtlasVersionableProperty
  @NotNull
  private String referencePointSloid;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private TactileVisualAttributeType tactileVisualMarks;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType contrastingAreas;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StepFreeAccessAttributeType stepFreeAccess;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private ReferencePointElementType referencePointElementType;

}
