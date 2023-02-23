package ch.sbb.atlas.timetable.hearing.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.timetable.hearing.entity.TimetableHearingYear;
import ch.sbb.atlas.timetable.hearing.enumeration.HearingStatus;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class TimetableHearingYearRepositoryTest {

  private static final long YEAR = 2023L;

  private final TimetableHearingYearRepository timetableHearingYearRepository;

  @Autowired
  public TimetableHearingYearRepositoryTest(TimetableHearingYearRepository timetableHearingYearRepository) {
    this.timetableHearingYearRepository = timetableHearingYearRepository;
  }

  @AfterEach
  void tearDown() {
    timetableHearingYearRepository.deleteAll();
  }

  @Test
  void shouldCreateNewHearingYear() {
    TimetableHearingYear timetableHearingYear = TimetableHearingYear.builder()
        .timetableYear(YEAR)
        .hearingStatus(HearingStatus.PLANNED)
        .hearingFrom(LocalDate.of(2022, 1, 1))
        .hearingTo(LocalDate.of(2022, 2, 1))
        .statementCreatableExternal(true)
        .statementCreatableInternal(true)
        .statementEditable(true)
        .build();

    TimetableHearingYear savedYear = timetableHearingYearRepository.save(timetableHearingYear);

    assertThat(savedYear.getTimetableYear()).isEqualTo(YEAR);
    assertThat(timetableHearingYearRepository.existsById(YEAR)).isTrue();
  }
}