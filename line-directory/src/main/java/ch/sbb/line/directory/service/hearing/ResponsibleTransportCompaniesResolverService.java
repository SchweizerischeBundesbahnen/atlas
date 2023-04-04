package ch.sbb.line.directory.service.hearing;

import ch.sbb.atlas.amazon.helper.FutureTimetableHelper;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.client.bodi.TransportCompanyClient;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.mapper.ResponsibleTransportCompanyMapper;
import ch.sbb.line.directory.service.TimetableFieldNumberService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

  public List<TransportCompanyModel> getResponsibleTransportCompanies(String ttfnid) {
    if (ttfnid != null) {
      String sboid = resolveBusinessOrganisationSboid(ttfnid);
      return resolveTransportCompanies(sboid);
    }
    return Collections.emptyList();
  }

  public List<TimetableHearingStatementResponsibleTransportCompanyModel> resolveResponsibleTransportCompanies(String ttfnid) {
    return getResponsibleTransportCompanies(ttfnid).stream()
        .map(ResponsibleTransportCompanyMapper::toResponsibleTransportCompany)
        .toList();
  }

  private List<TransportCompanyModel> resolveTransportCompanies(String sboid) {
    if (sboid != null) {
      return transportCompanyClient.getTransportCompaniesBySboid(sboid);
    }
    return Collections.emptyList();
  }

  private String resolveBusinessOrganisationSboid(String ttfnid) {
    if (ttfnid != null) {
      List<TimetableFieldNumberVersion> timetableFieldNumberVersions =
          timetableFieldNumberService.getAllVersionsVersioned(ttfnid);

      TimetableFieldNumberVersion versionValidOnNextTimetableYear = getVersionValidOnNextTimetableYear(
          timetableFieldNumberVersions);

      return versionValidOnNextTimetableYear.getBusinessOrganisation();
    }
    return null;
  }

  private static TimetableFieldNumberVersion getVersionValidOnNextTimetableYear(
      List<TimetableFieldNumberVersion> timetableFieldNumberVersions) {
    LocalDate beginningOfNextTimetableYear = FutureTimetableHelper.getActualTimetableYearChangeDate(LocalDate.now());

    return timetableFieldNumberVersions.stream().filter(
            version -> version.getValidFrom().isBefore(beginningOfNextTimetableYear) && version.getValidTo()
                .isAfter(beginningOfNextTimetableYear))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("There is no version valid at " + beginningOfNextTimetableYear.format(
            DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN_CH))));
  }

}
