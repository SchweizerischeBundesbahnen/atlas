package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.converter.ServicePointNumberConverter;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.GeolocationBaseEntity;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.TrafficPointElementGeolocation;
import ch.sbb.atlas.validation.DatesValidator;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.atlas.versioning.model.VersionableProperty.RelationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
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
@Entity(name = "traffic_point_element_version")
@AtlasVersionable
public class TrafficPointElementVersion extends BasePointVersion<TrafficPointElementVersion> implements Versionable,
    DatesValidator {

  private static final String VERSION_SEQ = "traffic_point_element_version_seq";

  @Override
  public boolean hasGeolocation() {
    return trafficPointElementGeolocation != null;
  }

  @Override
  public void referenceGeolocationTo(TrafficPointElementVersion version) {
    trafficPointElementGeolocation.setTrafficPointElementVersion(version);
  }

  @Override
  public GeolocationBaseEntity geolocation() {
    return trafficPointElementGeolocation;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1,
      initialValue = 1000)
  private Long id;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_40)
  @AtlasVersionableProperty
  private String designation;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_20)
  @AtlasVersionableProperty
  private String designationOperational;

  @AtlasVersionableProperty
  @Digits(integer = 13, fraction = 3)
  private Double length;

  @AtlasVersionableProperty
  @Digits(integer = 5, fraction = 2)
  private Double boardingAreaHeight;

  @AtlasVersionableProperty
  @Digits(integer = 5, fraction = 2)
  private Double compassDirection;

  @AtlasVersionableProperty
  private TrafficPointElementType trafficPointElementType;

  @NotNull
  @AtlasVersionableProperty
  @Convert(converter = ServicePointNumberConverter.class)
  @Valid
  private ServicePointNumber servicePointNumber;

  @NotNull
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_128)
  @AtlasVersionableProperty
  private String sloid;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_128)
  @AtlasVersionableProperty
  private String parentSloid;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "traffic_point_geolocation_id", referencedColumnName = "id")
  @AtlasVersionableProperty(relationType = RelationType.ONE_TO_ONE, relationsFields = {
      GeolocationBaseEntity.Fields.east,
      GeolocationBaseEntity.Fields.north,
      GeolocationBaseEntity.Fields.spatialReference,
      GeolocationBaseEntity.Fields.height,
  })
  private TrafficPointElementGeolocation trafficPointElementGeolocation;

  @NotNull
  @Column(columnDefinition = "DATE")
  private LocalDate validFrom;

  @NotNull
  @Column(columnDefinition = "DATE")
  private LocalDate validTo;

}
