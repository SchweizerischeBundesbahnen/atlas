package ch.sbb.atlas.servicepointdirectory.entity.sector;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.entity.BaseEntity;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder(toBuilder = true)
@FieldNameConstants
@MappedSuperclass
public abstract class BaseSectorEntity extends BaseEntity {

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

  @Schema(description = "Status")
  @NotNull
  @Enumerated(EnumType.STRING)
  private Status status;

}
