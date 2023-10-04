package ch.sbb.prm.directory.entity;

import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.prm.directory.enumeration.StandardAttributeType;
import jakarta.persistence.Entity;
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
@Entity(name = "ticket_counter_version")
@AtlasVersionable
public class TicketCounterVersion extends BasePrmEntityVersion implements Versionable {

  private static final String VERSION_SEQ = "ticket_counter_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @AtlasVersionableProperty
  private String designation;

  @AtlasVersionableProperty
  private String info;

  @AtlasVersionableProperty
  private StandardAttributeType inductionLoop;

  @AtlasVersionableProperty
  private String openingHours;

  @AtlasVersionableProperty
  private StandardAttributeType wheelchairAccess;

}
