package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.validation.DatesValidator;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.atlas.servicepointdirectory.converter.ServicePointNumberConverter;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.LoadingPointGeolocation;
import ch.sbb.atlas.servicepointdirectory.model.ServicePointNumber;
import java.time.LocalDate;
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
@SuperBuilder
@FieldNameConstants
@Entity(name = "loading_point_version")
@AtlasVersionable
public class LoadingPointVersion extends BaseDidokImportEntity implements Versionable, DatesValidator {

  private static final String VERSION_SEQ = "loading_point_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotNull
  @AtlasVersionableProperty
  private Integer number;

  @NotNull
  @Size(min = 1, max = AtlasFieldLengths.LENGTH_12)
  @AtlasVersionableProperty
  private String designation;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_35)
  @AtlasVersionableProperty
  private String designationLong;

  @AtlasVersionableProperty
  private boolean connectionPoint;

  @NotNull
  @AtlasVersionableProperty
  @Convert(converter = ServicePointNumberConverter.class)
  @Valid
  private ServicePointNumber servicePointNumber;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "loading_point_geolocation_id", referencedColumnName = "id")
  private LoadingPointGeolocation loadingPointGeolocation;
  @NotNull
  @Column(columnDefinition = "DATE")
  private LocalDate validFrom;
  @NotNull
  @Column(columnDefinition = "DATE")
  private LocalDate validTo;

  public boolean hasGeolocation() {
    return loadingPointGeolocation != null;
  }

}
