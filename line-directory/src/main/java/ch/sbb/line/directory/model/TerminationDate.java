package ch.sbb.line.directory.model;

import ch.sbb.line.directory.service.LineService.AdjustmentDateStatus;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SuperBuilder
public class TerminationDate {

  private AdjustmentDateStatus status;

  private LocalDate validFrom;
  private LocalDate validTo;
}
