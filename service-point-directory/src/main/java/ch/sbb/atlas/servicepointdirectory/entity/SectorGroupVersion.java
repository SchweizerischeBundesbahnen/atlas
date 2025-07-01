package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.model.entity.BaseEntity;
import ch.sbb.atlas.validation.DatesValidator;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.versioning.model.Versionable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
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
@Entity(name = "sector_group_version")
@AtlasVersionable
public class SectorGroupVersion extends BaseEntity implements Versionable, DatesValidator {

  private static final String VERSION_SEQ = "sector_group_version_seq";

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

  @Schema(description = "Length of the Sector", example = "18.000")
  @AtlasVersionableProperty
  private Double length;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "sector_group_relations",
      joinColumns = @JoinColumn(name = "sector_group_sloid", referencedColumnName = "sloid"),
      inverseJoinColumns = @JoinColumn(name = "sector_sloid", referencedColumnName = "sloid"))
  @Size(min = 2)
  @Builder.Default
  @ToString.Exclude
  @AtlasVersionableProperty
  private List<SectorVersion> sectorVersions = new ArrayList<>();
}
