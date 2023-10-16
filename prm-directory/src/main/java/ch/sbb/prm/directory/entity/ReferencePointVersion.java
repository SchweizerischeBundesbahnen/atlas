package ch.sbb.prm.directory.entity;

import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.prm.directory.enumeration.ReferencePointAttributeType;
import ch.sbb.prm.directory.service.PrmVersionable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;
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
@Entity(name = "reference_point_version")
@AtlasVersionable
public class ReferencePointVersion extends BasePrmEntityVersion implements PrmVersionable {

  private static final String VERSION_SEQ = "reference_point_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotNull
  @AtlasVersionableProperty
  private String designation;

  @NotNull
  @AtlasVersionableProperty
  private boolean mainReferencePoint;

  @NotNull
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private ReferencePointAttributeType referencePointType;


}
