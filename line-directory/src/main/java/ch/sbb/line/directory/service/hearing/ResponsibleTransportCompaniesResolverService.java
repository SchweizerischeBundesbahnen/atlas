package ch.sbb.line.directory.service.hearing;

import ch.sbb.atlas.amazon.helper.FutureTimetableHelper;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.client.bodi.TransportCompanyClient;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
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

  public List<String> resolveResponsibleTransportCompanies(String ttfnid) {
    if (ttfnid != null) {
      String sboid = resolveBusinessOrganisationSboid(ttfnid);
      return resolveTransportCompanies(sboid);
    }
    return Collections.emptyList();
  }

  private List<String> resolveTransportCompanies(String sboid) {
    if (sboid != null) {
      List<TransportCompanyModel> transportCompaniesBySboid = transportCompanyClient.getTransportCompaniesBySboid(sboid);
      return transportCompaniesBySboid.stream().map(TransportCompanyModel::getNumber).toList();
    }
    return Collections.emptyList();
  }

  private String resolveBusinessOrganisationSboid(String ttfnid) {
    if (ttfnid != null) {
      List<TimetableFieldNumberVersion> timetableFieldNumberVersions =
          timetableFieldNumberService.getAllVersionsVersioned(ttfnid);

      LocalDate beginningOfNextTimetableYear = FutureTimetableHelper.getActualTimetableYearChangeDate(LocalDate.now());

      TimetableFieldNumberVersion versionValidOnNextTimetableYear = timetableFieldNumberVersions.stream().filter(
              version -> version.getValidFrom().isBefore(beginningOfNextTimetableYear) && version.getValidTo()
                  .isAfter(beginningOfNextTimetableYear))
          .findFirst()
          .orElseThrow(() -> new IllegalStateException("There is no version valid at " + beginningOfNextTimetableYear.format(
              DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN_CH))));

      return versionValidOnNextTimetableYear.getBusinessOrganisation();
    }
    return null;
  }

}
