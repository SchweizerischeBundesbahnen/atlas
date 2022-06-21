package ch.sbb.business.organisation.directory.entity;

import ch.sbb.atlas.model.entity.BaseVersion;
import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Immutable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@Immutable
@Entity(name = "transport_company_relation")
public class TransportCompanyRelation extends BaseVersion {

  private static final String BO_TC_LINK_SEQ = "transport_company_relation_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = BO_TC_LINK_SEQ)
  @SequenceGenerator(name = BO_TC_LINK_SEQ, sequenceName = BO_TC_LINK_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @NotNull
  @JoinColumn(name = "transport_company_id", referencedColumnName = "id")
  private Long transportCompanyId;

  @NotNull
  @Size(max = 32)
  private String sboid;

  @NotNull
  private LocalDate validFrom;

  @NotNull
  private LocalDate validTo;

}
