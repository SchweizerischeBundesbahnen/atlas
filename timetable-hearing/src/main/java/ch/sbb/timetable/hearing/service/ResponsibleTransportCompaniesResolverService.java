package ch.sbb.timetable.hearing.service;

import ch.sbb.atlas.api.bodi.TransportCompanyModel;
import ch.sbb.atlas.api.client.bodi.TransportCompanyClient;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberApiInternal;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.timetable.hearing.mapper.ResponsibleTransportCompanyMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResponsibleTransportCompaniesResolverService {

  private final TimetableFieldNumberApiInternal timetableFieldNumberApiInternal;
  private final TransportCompanyClient transportCompanyClient;

  public List<TransportCompanyModel> getResponsibleTransportCompanies(String ttfnid, LocalDate validOn) {
    if (ttfnid != null) {
      return resolveTransportCompanies(resolveBusinessOrganisationSboid(ttfnid));
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

  private String resolveBusinessOrganisationSboid(String ttfnid) {
    List<TimetableFieldNumberModel> timetableFieldNumbers = timetableFieldNumberApiInternal.getOverview(Pageable.unpaged(),
            null, null, null, null, null, List.of(ttfnid))
        .getObjects();

    if (timetableFieldNumbers.isEmpty()) {
      log.info("No timetable field number found for ttfnid={}", ttfnid);
      return null;
    }
    return timetableFieldNumbers.getFirst().getBusinessOrganisation();
  }

}
