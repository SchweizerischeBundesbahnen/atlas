package ch.sbb.prm.directory.entity;

import ch.sbb.atlas.api.prm.enumeration.BasicAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.prm.directory.converter.InfoOpportunityTypeConverter;
import ch.sbb.prm.directory.service.PrmVersionable;
import ch.sbb.prm.directory.service.Relatable;
import ch.sbb.prm.directory.validation.VariantsReducedCompleteRecordable;
import ch.sbb.prm.directory.validation.annotation.PrmVariant;
import ch.sbb.prm.directory.validation.annotation.RecordingVariant;
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

import java.util.HashSet;
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

  @PrmVariant(variant = RecordingVariant.COMPLETE, nullable = false)
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private BoardingDeviceAttributeType boardingDevice;

  @AtlasVersionableProperty
  private String additionalInformation;

  @PrmVariant(variant = RecordingVariant.COMPLETE)
  @AtlasVersionableProperty
  private String adviceAccessInfo;

  @PrmVariant(variant = RecordingVariant.COMPLETE,nullable = false)
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private BooleanOptionalAttributeType contrastingAreas;

  @PrmVariant(variant = RecordingVariant.COMPLETE,nullable = false)
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private BasicAttributeType dynamicAudio;

  @PrmVariant(variant = RecordingVariant.COMPLETE,nullable = false)
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private BasicAttributeType dynamicVisual;

  @AtlasVersionableProperty
  @PrmVariant(variant = RecordingVariant.REDUCED)
  private Double height;

  @PrmVariant(variant = RecordingVariant.COMPLETE)
  @AtlasVersionableProperty
  private Double inclination;

  @PrmVariant(variant = RecordingVariant.REDUCED)
  @AtlasVersionableProperty
  private Double inclinationLongitudinal;

  @PrmVariant(variant = RecordingVariant.COMPLETE)
  @AtlasVersionableProperty
  private Double inclinationWidth;

  @PrmVariant(variant = RecordingVariant.REDUCED)
  @AtlasVersionableProperty
  @ElementCollection(targetClass = InfoOpportunityAttributeType.class, fetch = FetchType.EAGER)
  @Convert(converter = InfoOpportunityTypeConverter.class)
  private Set<InfoOpportunityAttributeType> infoOpportunities;

  @PrmVariant(variant = RecordingVariant.COMPLETE,nullable = false)
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private BasicAttributeType levelAccessWheelchair;

  @PrmVariant(variant = RecordingVariant.REDUCED)
  @AtlasVersionableProperty
  private Boolean partialElevation;

  @PrmVariant(variant = RecordingVariant.COMPLETE)
  @AtlasVersionableProperty
  private Double superelevation;

  @PrmVariant(variant = RecordingVariant.REDUCED)
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private BooleanOptionalAttributeType tactileSystem;

  @PrmVariant(variant = RecordingVariant.REDUCED)
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private VehicleAccessAttributeType vehicleAccess;

  @PrmVariant(variant = RecordingVariant.REDUCED)
  @AtlasVersionableProperty
  private Double wheelchairAreaLength;

  @PrmVariant(variant = RecordingVariant.REDUCED)
  @AtlasVersionableProperty
  private Double wheelchairAreaWidth;

  public Set<InfoOpportunityAttributeType> getInfoOpportunities() {
    if (infoOpportunities == null) {
      return new HashSet<>();
    }
    return infoOpportunities;
  }

}
