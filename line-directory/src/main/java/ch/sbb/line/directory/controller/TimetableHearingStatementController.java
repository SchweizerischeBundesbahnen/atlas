package ch.sbb.line.directory.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementApiV1;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementRequestParams;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import ch.sbb.line.directory.mapper.TimeTableHearingStatementMapper;
import ch.sbb.line.directory.model.TimetableHearingStatementSearchRestrictions;
import ch.sbb.line.directory.service.hearing.ResponsibleTransportCompaniesResolverService;
import ch.sbb.line.directory.service.hearing.TimetableFieldNumberResolverService;
import ch.sbb.line.directory.service.hearing.TimetableHearingStatementService;
import ch.sbb.line.directory.service.hearing.TimetableHearingYearService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TimetableHearingStatementController implements TimetableHearingStatementApiV1 {

    private final TimetableHearingStatementService timetableHearingStatementService;
    private final TimetableHearingYearService timetableHearingYearService;
    private final TimetableFieldNumberResolverService timetableFieldNumberResolverService;
    private final ResponsibleTransportCompaniesResolverService responsibleTransportCompaniesResolverService;

    @Override
    public Container<TimetableHearingStatementModel> getStatements(Pageable pageable,
        TimetableHearingStatementRequestParams statementRequestParams) {
        Page<TimetableHearingStatement> hearingStatements = timetableHearingStatementService.getHearingStatements(
            TimetableHearingStatementSearchRestrictions.builder()
                .pageable(pageable)
                .statementRequestParams(statementRequestParams).build());
        return Container.<TimetableHearingStatementModel>builder()
            .objects(hearingStatements.stream().map(TimeTableHearingStatementMapper::toModel).toList())
            .totalCount(hearingStatements.getTotalElements())
            .build();
    }

    @Override
    public TimetableHearingStatementModel getStatement(Long id) {
        return TimeTableHearingStatementMapper.toModel(timetableHearingStatementService.getStatementById(id));
    }

    @Override
    public TimetableHearingStatementModel createStatement(TimetableHearingStatementModel statement, List<MultipartFile> documents) {
        TimetableHearingStatement statementToCreate = TimeTableHearingStatementMapper.toEntity(statement);
        TimetableHearingStatement hearingStatement;
        if (documents!=null && !documents.isEmpty()) {
            hearingStatement = timetableHearingStatementService.createHearingStatement(statementToCreate, documents);
        } else {
            hearingStatement = timetableHearingStatementService.createHearingStatement(statementToCreate);
        }
        return TimeTableHearingStatementMapper.toModel(hearingStatement);
    }

    @Override
    public TimetableHearingStatementModel createStatementExternal(TimetableHearingStatementModel statement,
        List<MultipartFile> documents) {
        String resolvedTtfnid =
            timetableFieldNumberResolverService.resolveTtfnid(statement.getTimetableFieldNumber());
        statement.setTtfnid(resolvedTtfnid);

        List<TimetableHearingStatementResponsibleTransportCompanyModel> responsibleTransportCompanies =
            responsibleTransportCompaniesResolverService.resolveResponsibleTransportCompanies(
                resolvedTtfnid);
        statement.setResponsibleTransportCompanies(responsibleTransportCompanies);

        Long activeHearingYear = timetableHearingYearService.getActiveHearingYear().getTimetableYear();
        statement.setTimetableYear(activeHearingYear);

        return createStatement(statement, documents);
    }

    @Override
    public TimetableHearingStatementModel updateHearingStatement(Long id, TimetableHearingStatementModel statement,
        List<MultipartFile> documents) {
        TimetableHearingStatement hearingStatement;

        TimetableHearingStatement timetableHearingStatementUpdate = TimeTableHearingStatementMapper.toEntity(statement);

        if (documents!=null && !documents.isEmpty() && documents.get(0).getOriginalFilename()!="") {
            hearingStatement = timetableHearingStatementService.updateHearingStatement(timetableHearingStatementUpdate, timetableHearingStatementService.getStatementById(id), documents);
        } else {
            hearingStatement = timetableHearingStatementService.updateHearingStatement(timetableHearingStatementUpdate, timetableHearingStatementService.getStatementById(id));
        }
        return TimeTableHearingStatementMapper.toModel(hearingStatement);
    }

}
