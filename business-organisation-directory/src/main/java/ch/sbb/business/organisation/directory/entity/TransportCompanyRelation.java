package ch.sbb.business.organisation.directory.entity;

import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.base.service.model.entity.BaseEntity;
import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@FieldNameConstants
@Entity(name = "transport_company_relation")
public class TransportCompanyRelation extends BaseEntity {

  private static final String TRANSPORT_COMPANY_RELATION_SEQ = "transport_company_relation_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TRANSPORT_COMPANY_RELATION_SEQ)
  @SequenceGenerator(name = TRANSPORT_COMPANY_RELATION_SEQ, sequenceName = TRANSPORT_COMPANY_RELATION_SEQ, allocationSize = 1,
      initialValue = 1000)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "transport_company_id", referencedColumnName = "id")
  @NotNull
  private TransportCompany transportCompany;

  @NotNull
  @Size(max = AtlasFieldLengths.LENGTH_32)
  private String sboid;

  @NotNull
  private LocalDate validFrom;

  @NotNull
  private LocalDate validTo;

}
