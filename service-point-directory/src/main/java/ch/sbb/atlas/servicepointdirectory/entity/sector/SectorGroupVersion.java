package ch.sbb.atlas.servicepointdirectory.entity.sector;

import ch.sbb.atlas.validation.DatesValidator;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.model.Versionable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@FieldNameConstants
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "sector_group_version")
@AtlasVersionable
public class SectorGroupVersion extends AbstractSectorEntity implements Versionable, DatesValidator {

  private static final String VERSION_SEQ = "sector_group_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

}
