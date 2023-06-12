package ch.sbb.atlas.kafka.model.business.organisation;

import ch.sbb.atlas.kafka.model.Status;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class SharedBusinessOrganisationVersionModel {

  @NotNull
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
  private Status status;

  @NotNull
  private LocalDate validFrom;

  @NotNull
  private LocalDate validTo;

}
