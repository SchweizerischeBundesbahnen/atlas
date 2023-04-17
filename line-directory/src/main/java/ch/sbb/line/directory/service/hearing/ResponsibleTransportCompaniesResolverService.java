package ch.sbb.line.directory.service.hearing;

import ch.sbb.atlas.amazon.helper.FutureTimetableHelper;
import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.client.bodi.TransportCompanyClient;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.exception.NoValidVersionAtDateException;
import ch.sbb.line.directory.mapper.ResponsibleTransportCompanyMapper;
import ch.sbb.line.directory.service.TimetableFieldNumberService;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResponsibleTransportCompaniesResolverService {

  private final TimetableFieldNumberService timetableFieldNumberService;
  private final TransportCompanyClient transportCompanyClient;

  public List<TransportCompanyModel> getResponsibleTransportCompanies(String ttfnid, LocalDate validOn) {
    if (ttfnid != null) {
      String sboid = resolveBusinessOrganisationSboid(ttfnid, validOn);
      return resolveTransportCompanies(sboid);
    }
    return Collections.emptyList();
  }

  public List<TimetableHearingStatementResponsibleTransportCompanyModel> resolveResponsibleTransportCompanies(String ttfnid) {
    return getResponsibleTransportCompanies(ttfnid, LocalDate.now()).stream()
        .map(ResponsibleTransportCompanyMapper::toResponsibleTransportCompany)
        .toList();
  }

  private List<TransportCompanyModel> resolveTransportCompanies(String sboid) {
    if (sboid != null) {
      return transportCompanyClient.getTransportCompaniesBySboid(sboid);
    }
    return Collections.emptyList();
  }

  private String resolveBusinessOrganisationSboid(String ttfnid, LocalDate validOn) {
    if (ttfnid != null) {
      List<TimetableFieldNumberVersion> timetableFieldNumberVersions =
          timetableFieldNumberService.getAllVersionsVersioned(ttfnid);

      TimetableFieldNumberVersion versionValidOnNextTimetableYear = getVersionValidOn(timetableFieldNumberVersions, validOn);

      return versionValidOnNextTimetableYear.getBusinessOrganisation();
    }
    return null;
  }

  private static TimetableFieldNumberVersion getVersionValidOn(List<TimetableFieldNumberVersion> timetableFieldNumberVersions,
      LocalDate validOn) {
    LocalDate beginningOfNextTimetableYear = FutureTimetableHelper.getActualTimetableYearChangeDate(validOn);

    return timetableFieldNumberVersions.stream().filter(
            version -> !version.getValidFrom().isAfter(beginningOfNextTimetableYear) &&
                !version.getValidTo().isBefore(beginningOfNextTimetableYear))
        .findFirst()
        .orElseThrow(() -> new NoValidVersionAtDateException(beginningOfNextTimetableYear,
            timetableFieldNumberVersions.get(0).getTtfnid()));
  }

}
