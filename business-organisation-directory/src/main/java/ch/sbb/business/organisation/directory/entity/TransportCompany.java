package ch.sbb.business.organisation.directory.entity;

import ch.sbb.atlas.api.bodi.enumeration.TransportCompanyStatus;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Entity(name = "transport_company")
public class TransportCompany {

  @Id
  private Long id;

  private String number;

  private String abbreviation;

  private String description;

  private String businessRegisterName;

  @Enumerated(EnumType.STRING)
  private TransportCompanyStatus transportCompanyStatus;

  private String businessRegisterNumber;

  private String enterpriseId;

  private String ricsCode;

  private String businessOrganisationNumbers;

  private String comment;

  @CreationTimestamp
  @Column(columnDefinition = "TIMESTAMP", updatable = false)
  private LocalDateTime creationDate;

  @UpdateTimestamp
  @Column(columnDefinition = "TIMESTAMP")
  private LocalDateTime editionDate;

  @OneToMany(mappedBy = "transportCompany", fetch = FetchType.EAGER)
  private List<TransportCompanyRelation> transportCompanyRelations;

}
