package ch.sbb.business.organisation.directory.entity;

import ch.sbb.atlas.api.bodi.enumeration.BusinessType;
import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.model.entity.BaseVersion;
import ch.sbb.atlas.model.entity.BusinessIdGeneration;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.business.organisation.directory.converter.BusinessTypeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
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
@Entity(name = "business_organisation_version")
@AtlasVersionable
public class BusinessOrganisationVersion extends BaseVersion implements Versionable {

  private static final String BUSINESS_ORGANISATION_VERSION_SEQ = "business_organisation_version_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = BUSINESS_ORGANISATION_VERSION_SEQ)
  @SequenceGenerator(name = BUSINESS_ORGANISATION_VERSION_SEQ, sequenceName = BUSINESS_ORGANISATION_VERSION_SEQ,
      allocationSize = 1, initialValue = 1000)
  private Long id;

  @BusinessIdGeneration(valueGenerator = SboidGenerator.class)
  @Column(updatable = false)
  @AtlasVersionableProperty
  private String sboid;

  @NotNull
  @AtlasVersionableProperty
  @Size(max = AtlasFieldLengths.LENGTH_60)
  private String descriptionDe;

  @NotNull
  @AtlasVersionableProperty
  @Size(max = AtlasFieldLengths.LENGTH_60)
  private String descriptionFr;

  @NotNull
  @AtlasVersionableProperty
  @Size(max = AtlasFieldLengths.LENGTH_60)
  private String descriptionIt;

  @NotNull
  @AtlasVersionableProperty
  @Size(max = AtlasFieldLengths.LENGTH_60)
  private String descriptionEn;

  @NotNull
  @AtlasVersionableProperty
  @Size(max = AtlasFieldLengths.LENGTH_10)
  private String abbreviationDe;

  @NotNull
  @AtlasVersionableProperty
  @Size(max = AtlasFieldLengths.LENGTH_10)
  private String abbreviationFr;

  @NotNull
  @AtlasVersionableProperty
  @Size(max = AtlasFieldLengths.LENGTH_10)
  private String abbreviationIt;

  @NotNull
  @AtlasVersionableProperty
  @Size(max = AtlasFieldLengths.LENGTH_10)
  private String abbreviationEn;

  @NotNull
  @AtlasVersionableProperty
  private Integer organisationNumber;

  @AtlasVersionableProperty
  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String contactEnterpriseEmail;

  @AtlasVersionableProperty
  @ElementCollection(targetClass = BusinessType.class, fetch = FetchType.EAGER)
  @Convert(converter = BusinessTypeConverter.class)
  private Set<BusinessType> businessTypes;

  @NotNull
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validFrom;

  @NotNull
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validTo;

  public Set<BusinessType> getBusinessTypes() {
    if (businessTypes == null) {
      return new HashSet<>();
    }
    return businessTypes;
  }
}
