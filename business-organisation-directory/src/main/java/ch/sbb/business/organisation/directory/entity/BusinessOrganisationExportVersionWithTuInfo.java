package ch.sbb.business.organisation.directory.entity;

import ch.sbb.atlas.api.bodi.enumeration.BusinessType;
import ch.sbb.atlas.model.entity.BaseVersion;
import ch.sbb.business.organisation.directory.converter.BusinessTypeConverter;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
@Immutable
@Entity
@Subselect("")
public class BusinessOrganisationExportVersionWithTuInfo extends BaseVersion {

  @Id
  private Long id;
  private String sboid;

  private String abbreviationDe;
  private String abbreviationFr;
  private String abbreviationIt;
  private String abbreviationEn;

  private String descriptionDe;
  private String descriptionFr;
  private String descriptionIt;
  private String descriptionEn;

  private Integer organisationNumber;
  private String contactEnterpriseEmail;

  @ElementCollection(targetClass = BusinessType.class, fetch = FetchType.EAGER)
  @CollectionTable(name = "business_organisation_version_business_types", joinColumns = {
      @JoinColumn(name = "business_organisation_version_id")})
  @Convert(converter = BusinessTypeConverter.class)
  private Set<BusinessType> businessTypes;

  private LocalDate validFrom;
  private LocalDate validTo;

  private String number;
  private String abbreviation;
  private String businessRegisterName;

  public Set<BusinessType> getBusinessTypes() {
    if (businessTypes == null) {
      return new HashSet<>();
    }
    return businessTypes;
  }
}
