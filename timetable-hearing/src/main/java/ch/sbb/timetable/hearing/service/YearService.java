package ch.sbb.timetable.hearing.service;

import ch.sbb.atlas.api.timetable.hearing.TimetableHearingYearModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.HearingStatus;
import ch.sbb.atlas.api.workflow.tth.dossier.DossierStatus;
import ch.sbb.timetable.hearing.aop.LoggingAspect.WorkflowType;
import ch.sbb.timetable.hearing.aop.MethodLogged;
import ch.sbb.timetable.hearing.entity.TimetableHearingYear;
import ch.sbb.timetable.hearing.mapper.TimeTableHearingYearMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class YearService {

  private final TimetableHearingYearService timetableHearingYearService;
  private final DossierService dossierService;

  @Transactional
  @MethodLogged(workflowType = WorkflowType.TTH_DOSSIER_WORKFLOW)
  public TimetableHearingYearModel startTimetableHearingYear(Long year) {
    TimetableHearingYear hearingYear = timetableHearingYearService.getHearingYear(year);
    TimetableHearingYear startedHearing = timetableHearingYearService.startTimetableHearing(hearingYear);
    return TimeTableHearingYearMapper.toModel(startedHearing);
  }

  @Transactional
  @MethodLogged(workflowType = WorkflowType.TTH_DOSSIER_WORKFLOW)
  public TimetableHearingYearModel closeTimetableHearingYear(Long year) {
    List<Long> statementIdsToRemoveFromDossier = dossierService.getStatementIdsFromDossierStatus(List.of(
        DossierStatus.ADDED, DossierStatus.DOSSIER_BO_CHECK, DossierStatus.DOSSIER_CANTON_CHECK, DossierStatus.MOVED
    ));
    dossierService.updateDossierStatusClosingYear();

    TimetableHearingYear hearingYear = timetableHearingYearService.getHearingYear(year);
    timetableHearingYearService.mayTransitionToHearingStatus(hearingYear, HearingStatus.ARCHIVED);
    TimetableHearingYear closedHearing = timetableHearingYearService.closeTimetableHearing(hearingYear,
        statementIdsToRemoveFromDossier);
    return TimeTableHearingYearMapper.toModel(closedHearing);
  }
}


