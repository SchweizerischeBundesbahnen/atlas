package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.model.entity.BaseEntity;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@SuperBuilder(toBuilder = true)
@FieldNameConstants
@Entity(name = "sector_version")
@AtlasVersionable
public class SectorVersion extends BaseEntity {

  private static final String VERSION_SEQ = "sector_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_128)
  @AtlasVersionableProperty
  @NotNull
  private String sloid;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_128)
  @AtlasVersionableProperty
  @NotNull
  private String trafficPointSloid;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_8)
  @AtlasVersionableProperty
  @NotNull
  private String designation;

  @NotNull
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private SpatialReference spatialReference;

  @AtlasVersionableProperty
  @NotNull
  private Double east;

  @AtlasVersionableProperty
  @NotNull
  private Double north;

  @AtlasVersionableProperty
  private Double height;
}
