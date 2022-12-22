package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.base.service.model.entity.BaseEntity;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.base.service.versioning.model.Versionable;
import ch.sbb.atlas.servicepointdirectory.enumeration.TrafficPointElementType;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
@Entity(name = "traffic_point_element_version")
@AtlasVersionable
public class TrafficPointElementVersion extends BaseEntity implements Versionable {

  private static final String VERSION_SEQ = "traffic_point_element_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1,
      initialValue = 1000)
  private Long id;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @AtlasVersionableProperty
  private String designation;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_50)
  @AtlasVersionableProperty
  private String designationOperational;

  @AtlasVersionableProperty
  private Double length;

  @AtlasVersionableProperty
  private Double boardingAreaHeight;

  @AtlasVersionableProperty
  private Double compassDirection;

  @AtlasVersionableProperty
  private TrafficPointElementType trafficPointElementType;

  @NotNull
  @AtlasVersionableProperty
  private Integer servicePointNumber;

  @NotNull
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @AtlasVersionableProperty
  private String sloid;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @AtlasVersionableProperty
  private String parentSloid;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "traffic_point_geolocation_id", referencedColumnName = "id")
  private TrafficPointElementGeolocation trafficPointElementGeolocation;

  public boolean hasGeolocation() {
    return trafficPointElementGeolocation != null;
  }

  @NotNull
  @Column(columnDefinition = "DATE")
  private LocalDate validFrom;

  @NotNull
  @Column(columnDefinition = "DATE")
  private LocalDate validTo;

}
