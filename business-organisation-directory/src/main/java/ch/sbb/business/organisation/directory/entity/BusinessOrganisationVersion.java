package ch.sbb.business.organisation.directory.entity;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.versioning.model.Versionable;
import ch.sbb.business.organisation.directory.converter.BusinessTypeConverter;
import ch.sbb.business.organisation.directory.entity.BusinessIdGenerator.SboidGenerator;
import java.time.LocalDate;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

  @AtlasVersionableProperty
  private String descriptionDe;

  @AtlasVersionableProperty
  private String descriptionFr;

  @AtlasVersionableProperty
  private String descriptionIt;

  @AtlasVersionableProperty
  private String descriptionEn;

  @AtlasVersionableProperty
  private String abbreviationDe;

  @AtlasVersionableProperty
  private String abbreviationFr;

  @AtlasVersionableProperty
  private String abbreviationIt;

  @AtlasVersionableProperty
  private String abbreviationEn;

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
