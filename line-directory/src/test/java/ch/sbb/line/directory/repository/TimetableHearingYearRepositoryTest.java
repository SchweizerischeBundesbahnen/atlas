package ch.sbb.line.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.entity.TimetableHearingYear;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
 class TimetableHearingYearRepositoryTest {

  private static final long YEAR = 2023L;

  private final TimetableHearingYearRepository timetableHearingYearRepository;

  @Autowired
   TimetableHearingYearRepositoryTest(TimetableHearingYearRepository timetableHearingYearRepository) {
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