package ch.sbb.line.directory.module.ttfn.controller;

import ch.sbb.atlas.api.lidi.TimetableFieldNumberApiInternal;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberModel;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberVersionModel;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.model.Status;
import ch.sbb.line.directory.exception.TtfnidNotFoundException;
import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldNumber;
import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.module.ttfn.mapper.TimetableFieldNumberMapper;
import ch.sbb.line.directory.module.ttfn.search.TimetableFieldNumberSearchRestrictions;
import ch.sbb.line.directory.module.ttfn.service.TimetableFieldNumberService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TimetableFieldNumberControllerInternal implements TimetableFieldNumberApiInternal {

  private final TimetableFieldNumberService timetableFieldNumberService;

  @Override
  public Container<TimetableFieldNumberModel> getOverview(Pageable pageable,
      List<String> searchCriteria, String number, String businessOrganisation,
      LocalDate validOn, List<Status> statusChoices) {
    log.info(
        "Load TimetableFieldNumbers using pageable={}, searchCriteriaSpecification={}, validOn={} and statusChoices={}",
        pageable, searchCriteria, validOn, statusChoices);
    Page<TimetableFieldNumber> timetableFieldNumberPage = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(pageable)
            .searchCriterias(searchCriteria)
            .number(number)
            .statusRestrictions(statusChoices)
            .validOn(validOn)
            .businessOrganisation(businessOrganisation)
            .build());
    List<TimetableFieldNumberModel> versions = timetableFieldNumberPage.stream().map(TimetableFieldNumberMapper::toModel)
        .toList();
    return Container.<TimetableFieldNumberModel>builder()
        .objects(versions)
        .totalCount(timetableFieldNumberPage.getTotalElements())
        .build();
  }

  @Override
  public List<TimetableFieldNumberVersionModel> revokeTimetableFieldNumber(String ttfnId) {
    List<TimetableFieldNumberVersionModel> versions = timetableFieldNumberService.revokeTimetableFieldNumber(ttfnId)
        .stream()
        .map(TimetableFieldNumberMapper::toModel)
        .toList();
    if (versions.isEmpty()) {
      throw new TtfnidNotFoundException(ttfnId);
    }
    return versions;
  }

  @Override
  public void deleteVersions(String ttfnid) {
    List<TimetableFieldNumberVersion> allVersionsVersioned = timetableFieldNumberService.getAllVersionsVersioned(
        ttfnid);
    if (allVersionsVersioned.isEmpty()) {
      throw new TtfnidNotFoundException(ttfnid);
    }
    timetableFieldNumberService.deleteAll(allVersionsVersioned);
  }
}
