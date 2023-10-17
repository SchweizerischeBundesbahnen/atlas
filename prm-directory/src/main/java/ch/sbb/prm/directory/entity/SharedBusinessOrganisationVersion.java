package ch.sbb.prm.directory.entity;

import ch.sbb.atlas.business.organisation.entity.BusinessOrganisationVersionSharing;
import ch.sbb.atlas.model.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@FieldNameConstants
@Entity(name = "shared_business_organisation_version")
public class SharedBusinessOrganisationVersion implements BusinessOrganisationVersionSharing {

  @Id
  private Long id;

  @NotNull
  private String sboid;

  @NotNull
  private String descriptionDe;

  @NotNull
  private String descriptionFr;

  @NotNull
  private String descriptionIt;

  @NotNull
  private String descriptionEn;

  @NotNull
  private String abbreviationDe;

  @NotNull
  private String abbreviationFr;

  @NotNull
  private String abbreviationIt;

  @NotNull
  private String abbreviationEn;

  @NotNull
  private Integer organisationNumber;

  @NotNull
  @Enumerated(EnumType.STRING)
  private Status status;

  @NotNull
  private LocalDate validFrom;

  @NotNull
  private LocalDate validTo;

}
