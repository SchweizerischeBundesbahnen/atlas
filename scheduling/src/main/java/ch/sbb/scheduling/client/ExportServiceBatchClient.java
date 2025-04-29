package ch.sbb.scheduling.client;

import ch.sbb.scheduling.config.OAuthFeignConfig;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "exportServicePointBatch", url = "${atlas.client.gateway.url}/export-service/v2/export", configuration = OAuthFeignConfig.class)
public interface ExportServiceBatchClient {

  @PostMapping(value = "sepodi/service-point-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response exportServicePointBatch();

  @PostMapping(value = "sepodi/traffic-point-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response exportTrafficPointBatch();

  @PostMapping(value = "sepodi/loading-point-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response exportLoadingPointBatch();

  @PostMapping(value = "prm/stop-point-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response exportStopPointBatch();

  @PostMapping(value = "prm/platform-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response exportPlatformBatch();

  @PostMapping(value = "prm/reference-point-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response exportReferencePointBatch();

  @PostMapping(value = "prm/contact-point-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response exportContactPointBatch();

  @PostMapping(value = "prm/toilet-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response exportToiletBatch();

  @PostMapping(value = "prm/parking-lot-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response exportParkingLotBatch();

  @PostMapping(value = "prm/relation-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response exportRelationBatch();

  @PostMapping(value = "bodi/business-organisation-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response exportBusinessOrganisationBatch();

  @PostMapping(value = "bodi/transport-company-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response exportTransportCompanyBatch();

  @PostMapping(value = "lidi/line-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response exportLineBatch();

  @PostMapping(value = "lidi/subline-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response exportSublineBatch();

  @PostMapping(value = "lidi/ttfn-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response exportTimetableFieldNumberBatch();

  @PostMapping(value = "prm/recording-obligation-batch", produces = MediaType.APPLICATION_JSON_VALUE)
  Response exportRecordingObligationBatch();
}
