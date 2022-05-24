package ch.sbb.business.organisation.directory.entity;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.entity.BaseVersion;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.business.organisation.directory.converter.BusinessTypeConverter;
import java.time.LocalDate;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;


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
  @SequenceGenerator(name = BUSINESS_ORGANISATION_VERSION_SEQ, sequenceName = BUSINESS_ORGANISATION_VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @GeneratorType(type = SboidGenerator.class, when = GenerationTime.INSERT)
  @Column(updatable = false)
  @AtlasVersionableProperty
  private String sboid;

  @NotNull
  @AtlasVersionableProperty
  private String descriptionDe;

  @NotNull
  @AtlasVersionableProperty
  private String descriptionFr;

  @NotNull
  @AtlasVersionableProperty
  private String descriptionIt;

  @NotNull
  @AtlasVersionableProperty
  private String descriptionEn;

  @NotNull
  @AtlasVersionableProperty
  private String abbreviationDe;

  @NotNull
  @AtlasVersionableProperty
  private String abbreviationFr;

  @NotNull
  @AtlasVersionableProperty
  private String abbreviationIt;

  @NotNull
  @AtlasVersionableProperty
  private String abbreviationEn;

  @NotNull
  @AtlasVersionableProperty
  private Integer organisationNumber;

  @AtlasVersionableProperty
  private String contactEnterpriseEmail;

  @NotNull
  @Enumerated(EnumType.STRING)
  private Status status;

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

}
