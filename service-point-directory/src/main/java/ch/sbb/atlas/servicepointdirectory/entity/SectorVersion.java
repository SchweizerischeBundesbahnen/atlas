package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.model.entity.BaseEntity;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
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

  @NotNull
  @Column(columnDefinition = "DATE")
  private LocalDate validFrom;

  @NotNull
  @Column(columnDefinition = "DATE")
  private LocalDate validTo;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_8)
  @AtlasVersionableProperty
  @NotNull
  private String designation;

  @AtlasVersionableProperty
  @NotNull
  private Double north;

  @AtlasVersionableProperty
  @NotNull
  private Double east;

  @AtlasVersionableProperty
  private Double height;

  @NotNull
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private SpatialReference spatialReference;

  @Schema(description = "Length of the Sector", example = "18.000")
  private Double length;

  @Schema(description = "Edge Height of the Sector", example = "TODO")
  private Double edgeHeight;

  @ManyToMany(mappedBy = "sectorVersions", fetch = FetchType.EAGER)
  @Builder.Default
  private List<SectorGroupVersion> sectorGroupVersions = new ArrayList<>();
}
