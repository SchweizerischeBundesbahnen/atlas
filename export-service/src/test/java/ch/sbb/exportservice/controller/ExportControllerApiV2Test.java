package ch.sbb.exportservice.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.exportservice.job.BaseExportJobService;
import ch.sbb.exportservice.job.bodi.businessorganisation.service.ExportBusinessOrganisationJobService;
import ch.sbb.exportservice.job.bodi.transportcompany.service.ExportTransportCompanyJobService;
import ch.sbb.exportservice.job.lidi.line.service.ExportLineJobService;
import ch.sbb.exportservice.job.lidi.subline.service.ExportSublineJobService;
import ch.sbb.exportservice.job.lidi.ttfn.service.ExportTimetableFieldNumberJobService;
import ch.sbb.exportservice.job.prm.contactpoint.service.ExportContactPointJobService;
import ch.sbb.exportservice.job.prm.parkinglot.service.ExportParkingLotJobService;
import ch.sbb.exportservice.job.prm.platform.service.ExportPlatformJobService;
import ch.sbb.exportservice.job.prm.referencepoint.service.ExportReferencePointJobService;
import ch.sbb.exportservice.job.prm.relation.service.ExportRelationJobService;
import ch.sbb.exportservice.job.prm.stoppoint.service.ExportStopPointJobService;
import ch.sbb.exportservice.job.prm.toilet.service.ExportToiletJobService;
import ch.sbb.exportservice.job.sepodi.loadingpoint.service.ExportLoadingPointJobService;
import ch.sbb.exportservice.job.sepodi.servicepoint.service.ExportServicePointJobService;
import ch.sbb.exportservice.job.sepodi.trafficpoint.service.ExportTrafficPointElementJobService;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@MockitoSpyBean(types = {
    ExportBusinessOrganisationJobService.class,
    ExportTransportCompanyJobService.class,

    ExportStopPointJobService.class,
    ExportPlatformJobService.class,
    ExportReferencePointJobService.class,
    ExportContactPointJobService.class,
    ExportToiletJobService.class,
    ExportParkingLotJobService.class,
    ExportRelationJobService.class,

    ExportServicePointJobService.class,
    ExportTrafficPointElementJobService.class,
    ExportLoadingPointJobService.class,

    ExportLineJobService.class,
    ExportSublineJobService.class,
    ExportTimetableFieldNumberJobService.class
})
class ExportControllerApiV2Test extends BaseControllerApiTest {

  private static Stream<Arguments> provideArguments() {
    return Stream.of(
        Arguments.of("bodi/business-organisation-batch", ExportBusinessOrganisationJobService.class),
        Arguments.of("bodi/transport-company-batch", ExportTransportCompanyJobService.class),

        Arguments.of("prm/stop-point-batch", ExportStopPointJobService.class),
        Arguments.of("prm/platform-batch", ExportPlatformJobService.class),
        Arguments.of("prm/reference-point-batch", ExportReferencePointJobService.class),
        Arguments.of("prm/contact-point-batch", ExportContactPointJobService.class),
        Arguments.of("prm/toilet-batch", ExportToiletJobService.class),
        Arguments.of("prm/parking-lot-batch", ExportParkingLotJobService.class),
        Arguments.of("prm/relation-batch", ExportRelationJobService.class),

        Arguments.of("sepodi/service-point-batch", ExportServicePointJobService.class),
        Arguments.of("sepodi/traffic-point-batch", ExportTrafficPointElementJobService.class),
        Arguments.of("sepodi/loading-point-batch", ExportLoadingPointJobService.class),

        Arguments.of("lidi/line-batch", ExportLineJobService.class),
        Arguments.of("lidi/subline-batch", ExportSublineJobService.class),
        Arguments.of("lidi/ttfn-batch", ExportTimetableFieldNumberJobService.class)
    );
  }

  @ParameterizedTest
  @MethodSource("provideArguments")
  void startExport(String url, Class<BaseExportJobService> jobServiceClass) throws Exception {
    // given
    final BaseExportJobService jobServiceBean = context.getBean(jobServiceClass);
    Mockito.doNothing().when(jobServiceBean).startExportJobs();

    // when
    mvc.perform(post("/v2/export/" + url)
            .contentType(contentType)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    // then
    Mockito.verify(jobServiceBean).startExportJobs();
  }

}
