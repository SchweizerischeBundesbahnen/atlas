package ch.sbb.prm.directory.entity;

import ch.sbb.atlas.api.prm.enumeration.BasicAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.prm.directory.converter.InfoOpportunityTypeConverter;
import ch.sbb.prm.directory.service.PrmVersionable;
import ch.sbb.prm.directory.service.Relatable;
import ch.sbb.prm.directory.validation.VariantsReducedCompleteRecordable;
import ch.sbb.prm.directory.validation.annotation.NotForReducedPRM;
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
public class PlatformVersion extends BasePrmEntityVersion implements Relatable, PrmVersionable,
    VariantsReducedCompleteRecordable {

  private static final String VERSION_SEQ = "platform_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotForReducedPRM(defaultValueMandatory = true)
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private BoardingDeviceAttributeType boardingDevice;

  @AtlasVersionableProperty
  private String additionalInformation;

  @NotForReducedPRM
  @AtlasVersionableProperty
  private String adviceAccessInfo;

  @NotForReducedPRM(defaultValueMandatory = true)
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private BooleanOptionalAttributeType contrastingAreas;

  @NotForReducedPRM(defaultValueMandatory = true)
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private BasicAttributeType dynamicAudio;

  @NotForReducedPRM(defaultValueMandatory = true)
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private BasicAttributeType dynamicVisual;

  @AtlasVersionableProperty
  private Double height;

  @NotForReducedPRM
  @AtlasVersionableProperty
  private Double inclination;

  @AtlasVersionableProperty
  private Double inclinationLongitudinal;

  @NotForReducedPRM
  @AtlasVersionableProperty
  private Double inclinationWidth;

  @AtlasVersionableProperty
  @ElementCollection(targetClass = InfoOpportunityAttributeType.class, fetch = FetchType.EAGER)
  @Convert(converter = InfoOpportunityTypeConverter.class)
  private Set<InfoOpportunityAttributeType> infoOpportunities;

  @NotForReducedPRM(defaultValueMandatory = true)
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private BasicAttributeType levelAccessWheelchair;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private BooleanAttributeType partialElevation;

  @NotForReducedPRM
  @AtlasVersionableProperty
  private Double superelevation;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private BooleanOptionalAttributeType tactileSystem;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private VehicleAccessAttributeType vehicleAccess;

  @AtlasVersionableProperty
  private Double wheelchairAreaLength;

  @AtlasVersionableProperty
  private Double wheelchairAreaWidth;

}
