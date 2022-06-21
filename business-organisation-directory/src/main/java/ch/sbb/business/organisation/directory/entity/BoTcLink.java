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
@FieldNameConstants
@Immutable
@Entity(name = "business_organisation_transport_company_link")
public class BoTcLink extends BaseVersion {

  private static final String BO_TC_LINK_SEQ = "business_organisation_transport_company_link_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = BO_TC_LINK_SEQ)
  @SequenceGenerator(name = BO_TC_LINK_SEQ, sequenceName = BO_TC_LINK_SEQ, allocationSize = 1, initialValue = 1000)
  private Integer id;

  @NotNull
  @JoinColumn(name = "transport_company_id", referencedColumnName = "id")
  private Integer transportCompanyId;

  @NotNull
  @Size(max = 32)
  private String sboid;

  @NotNull
  private LocalDate validFrom;

  @NotNull
  private LocalDate validTo;

}
