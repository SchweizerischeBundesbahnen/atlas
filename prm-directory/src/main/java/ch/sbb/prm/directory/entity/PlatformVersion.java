package ch.sbb.prm.directory.entity;

import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.prm.directory.converter.InfoOpportunityTypeConverter;
import ch.sbb.prm.directory.enumeration.BasicAttributeType;
import ch.sbb.prm.directory.enumeration.BoardingDeviceAttributeType;
import ch.sbb.prm.directory.enumeration.BooleanAttributeType;
import ch.sbb.prm.directory.enumeration.BooleanOptionalAttributeType;
import ch.sbb.prm.directory.enumeration.InfoOpportunityAttributeType;
import ch.sbb.prm.directory.enumeration.VehicleAccessAttributeType;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.Digits;
import java.util.Set;
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
@Entity(name = "platform_version")
@AtlasVersionable
public class PlatformVersion extends BasePrmEntityVersion implements Versionable {

  private static final String VERSION_SEQ = "platform_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private BoardingDeviceAttributeType boardingDevice;

  @AtlasVersionableProperty
  private String additionalInfo;

  @AtlasVersionableProperty
  private String adviceAccessInfo;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private BooleanOptionalAttributeType contrastingAreas;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private BasicAttributeType dynamicAudio;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private BasicAttributeType dynamicVisual;

  @AtlasVersionableProperty
  @Digits(integer = 10, fraction = 3)
  private Double height;

  @AtlasVersionableProperty
  @Digits(integer = 10, fraction = 3)
  private Double inclination;

  @AtlasVersionableProperty
  @Digits(integer = 10, fraction = 3)
  private Double inclinationLongitudinal;

  @AtlasVersionableProperty
  @Digits(integer = 10, fraction = 3)
  private Double inclinationWidth;

  @AtlasVersionableProperty
  @ElementCollection(targetClass = InfoOpportunityAttributeType.class, fetch = FetchType.EAGER)
  @Convert(converter = InfoOpportunityTypeConverter.class)
  private Set<InfoOpportunityAttributeType> infoOpportunities;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private BasicAttributeType levelAccessWheelchair;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private BooleanAttributeType partialElevation;

  @AtlasVersionableProperty
  @Digits(integer = 10, fraction = 3)
  private Double superelevation;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private BooleanOptionalAttributeType tactileSystem;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private VehicleAccessAttributeType vehicleAccess;

  @AtlasVersionableProperty
  @Digits(integer = 10, fraction = 3)
  private Double wheelchairAreaLength;

  @AtlasVersionableProperty
  @Digits(integer = 10, fraction = 3)
  private Double wheelchairAreaWidth;

}
