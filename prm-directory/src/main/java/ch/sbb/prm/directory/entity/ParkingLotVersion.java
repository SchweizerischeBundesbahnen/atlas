package ch.sbb.prm.directory.entity;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.prm.directory.service.PrmVersionable;
import ch.sbb.prm.directory.service.Relatable;
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
@Entity(name = "parking_lot_version")
@AtlasVersionable
public class ParkingLotVersion extends BasePrmEntityVersion implements Relatable, PrmVersionable {

  private static final String VERSION_SEQ = "parking_lot_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @AtlasVersionableProperty
  private String additionalInformation;

  @AtlasVersionableProperty
  private String designation;

  @AtlasVersionableProperty
  private BooleanOptionalAttributeType placesAvailable;

  @AtlasVersionableProperty
  private BooleanOptionalAttributeType prmPlacesAvailable;

}
