package ch.sbb.prm.directory.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.converter.ServicePointNumberConverter;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.prm.directory.converter.InfoOpportunityTypeConverter;
import ch.sbb.prm.directory.enumeration.BasicAttributeType;
import ch.sbb.prm.directory.enumeration.BoardingDeviceType;
import ch.sbb.prm.directory.enumeration.BooleanAttributeType;
import ch.sbb.prm.directory.enumeration.BooleanOptionalAttributeType;
import ch.sbb.prm.directory.enumeration.InfoOpportunityType;
import ch.sbb.prm.directory.enumeration.VehicleAccessType;
import jakarta.persistence.Column;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
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
public class PlatformVersion extends BasePrmImportEntity implements Versionable {

  private static final String VERSION_SEQ = "platform_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @AtlasVersionableProperty
  private String sloid;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @AtlasVersionableProperty
  private String parentServicePointSloid;

  @NotNull
  @AtlasVersionableProperty
  @Convert(converter = ServicePointNumberConverter.class)
  @Valid
  private ServicePointNumber number;

  @NotNull
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validFrom;

  @NotNull
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validTo;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private BoardingDeviceType boardingDevice;

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
  @ElementCollection(targetClass = InfoOpportunityType.class, fetch = FetchType.EAGER)
  @Convert(converter = InfoOpportunityTypeConverter.class)
  private Set<InfoOpportunityType> infoOpportunities;

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
  private VehicleAccessType vehicleAccess;

  @AtlasVersionableProperty
  @Digits(integer = 10, fraction = 3)
  private Double wheelchairAreaLength;

  @AtlasVersionableProperty
  @Digits(integer = 10, fraction = 3)
  private Double wheelchairAreaWidth;

}
