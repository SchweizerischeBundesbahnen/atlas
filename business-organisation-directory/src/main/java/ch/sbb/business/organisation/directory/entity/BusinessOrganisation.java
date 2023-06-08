package ch.sbb.business.organisation.directory.entity;

import ch.sbb.atlas.api.bodi.enumeration.BusinessType;
import ch.sbb.atlas.kafka.model.Status;
import ch.sbb.business.organisation.directory.converter.BusinessTypeConverter;
import java.time.LocalDate;
import java.util.Set;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
@FieldNameConstants
@Immutable
@Entity
@Subselect(BusinessOrganisation.VIEW_DEFINITION)
public class BusinessOrganisation {

  @Id
  private Long id;
  
  private String sboid;

  private String descriptionDe;

  private String descriptionFr;

  private String descriptionIt;

  private String descriptionEn;

  private String abbreviationDe;

  private String abbreviationFr;

  private String abbreviationIt;

  private String abbreviationEn;

  private Integer organisationNumber;

  private String contactEnterpriseEmail;

  @Enumerated(EnumType.STRING)
  private Status status;

  @ElementCollection(targetClass = BusinessType.class, fetch = FetchType.EAGER)
  @CollectionTable(name = "business_organisation_version_business_types", joinColumns = {
      @JoinColumn(name = "business_organisation_version_id")})
  @Convert(converter = BusinessTypeConverter.class)
  private Set<BusinessType> businessTypes;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validFrom;

  @Column(columnDefinition = "TIMESTAMP")
  private LocalDate validTo;

  static final String VIEW_DEFINITION =
      "select *"
          + " from ("
          + "         select f.*, v.valid_from, v.valid_to"
          + "         from ("
          + "                  select id,"
          + "                         sboid,"
          + "                         abbreviation_de,"
          + "                         abbreviation_fr,"
          + "                         abbreviation_it,"
          + "                         abbreviation_en,"
          + "                         description_de,"
          + "                         description_fr,"
          + "                         description_it,"
          + "                         description_en,"
          + "                         organisation_number,"
          + "                         contact_enterprise_email,"
          + "                         status,"
          + "                         valid_from as vf"
          + "                  from ("
          + "                           select distinct on (sboid) *"
          + "                           from ((select distinct on (sboid) 1 as rank, *"
          + "                                  from business_organisation_version"
          + "                                  where valid_from <= current_date"
          + "                                    and current_date <= valid_to)"
          + "                                 union all"
          + "                                 (select distinct on (sboid) 2 as rank, *"
          + "                                  from business_organisation_version"
          + "                                  where valid_from >= current_date"
          + "                                  order by sboid, valid_from)"
          + "                                 union all"
          + "                                 (select distinct on (sboid) 3 as rank, *"
          + "                                  from business_organisation_version"
          + "                                  where valid_to <= current_date"
          + "                                  order by sboid, valid_to desc)) as ranked order by sboid, rank"
          + "                       ) as chosen"
          + "              ) f"
          + "                  join ("
          + "             select sboid, min(valid_from) as valid_from, max(valid_to) as valid_to"
          + "             from business_organisation_version"
          + "             group by sboid"
          + "         ) v on f.sboid = v.sboid" + "     ) as business_organisations";

}
