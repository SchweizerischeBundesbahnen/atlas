package ch.sbb.line.directory.model;

import ch.sbb.line.directory.enumaration.Status;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Pageable;

@Getter
@ToString
@Builder
public class SearchRestrictions<Type> {

  private final Pageable pageable;
  @Builder.Default
  private Optional<String> swissLineNumber = Optional.empty();
  @Builder.Default
  private List<String> searchCriteria = new ArrayList<>();
  @Builder.Default
  private List<Status> statusRestrictions = new ArrayList<>();
  @Builder.Default
  private List<Type> typeRestrictions = new ArrayList<>();
  @Builder.Default
  private Optional<LocalDate> validOn = Optional.empty();

  public SearchRestrictions(Pageable pageable,
      Optional<String> swissLineNumber,
      List<String> searchCriteria,
      List<Status> statusRestrictions,
      List<Type> typeRestrictions,
      Optional<LocalDate> validOn) {
    this.pageable = pageable;
    this.swissLineNumber = swissLineNumber;
    this.searchCriteria = searchCriteria;
    this.statusRestrictions = statusRestrictions == null ? new ArrayList<>() : statusRestrictions;
    this.typeRestrictions = typeRestrictions == null ? new ArrayList<>() : typeRestrictions;
    this.validOn = validOn;
  }
}
