package ch.sbb.prm.directory.entity;

import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
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
@Entity(name = "contact_point_version")
@AtlasVersionable
public class ContactPointVersion extends BasePrmEntityVersion implements Relatable, PrmVersionable {

  private static final String VERSION_SEQ = "contact_point_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @AtlasVersionableProperty
  private String designation;

  @AtlasVersionableProperty
  private String additionalInformation;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType inductionLoop;

  @AtlasVersionableProperty
  private String openingHours;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private StandardAttributeType wheelchairAccess;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private ContactPointType type;

}
