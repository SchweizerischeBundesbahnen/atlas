package ch.sbb.line.directory.model;

import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.line.directory.entity.TimetableHearingYear;
import ch.sbb.atlas.searching.specification.EnumSpecification;
import ch.sbb.line.directory.entity.TimetableHearingYear_;
import java.util.List;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@SuperBuilder
public class TimetableHearingYearSearchRestrictions {

  private final Pageable pageable;

  @Singular(ignoreNullCollections = true)
  private List<HearingStatus> statusRestrictions;

  public Specification<TimetableHearingYear> getSpecification() {
    return new EnumSpecification<>(statusRestrictions, TimetableHearingYear_.hearingStatus);
  }

}
