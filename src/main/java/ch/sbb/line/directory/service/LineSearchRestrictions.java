package ch.sbb.line.directory.service;

import ch.sbb.line.directory.enumaration.LineType;
import ch.sbb.line.directory.enumaration.Status;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
@Getter
@ToString
@Builder
public class LineSearchRestrictions {

  private final Pageable pageable;
  @Builder.Default
  private final Optional<String> swissLineNumber = Optional.empty();
  @Builder.Default
  private final List<String> searchCriteria = new ArrayList<>();
  @Builder.Default
  private final List<Status> statusRestrictions = new ArrayList<>();
  @Builder.Default
  private final List<LineType> typeRestrictions = new ArrayList<>();
  @Builder.Default
  private final Optional<LocalDate> validOn = Optional.empty();

}
