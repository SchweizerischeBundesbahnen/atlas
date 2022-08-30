package ch.sbb.scheduling.client;

import ch.sbb.scheduling.config.OAuthFeignConfig;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "lidiClient", url = "${atlas.client.gateway.url}", configuration = OAuthFeignConfig.class)
public interface LiDiClient {

  @PostMapping(value = "/line-directory/v1/lines/export-csv/full/csv", produces = MediaType.APPLICATION_JSON_VALUE)
  Response putLiDiExportFullCsv();

  @PostMapping(value = "/line-directory/v1/lines/export-csv/full/zip", produces = MediaType.APPLICATION_JSON_VALUE)
  Response putLiDiExportFullZip();

  @PostMapping(value = "/line-directory/v1/lines/export-csv/actual/csv", produces = MediaType.APPLICATION_JSON_VALUE)
  Response putLiDiExportActualCsv();

  @PostMapping(value = "/line-directory/v1/lines/export-csv/actual/zip", produces = MediaType.APPLICATION_JSON_VALUE)
  Response putLiDiExportActualZip();

  @PostMapping(value = "/line-directory/v1/lines/export-csv/timetable-year-change/csv", produces = MediaType.APPLICATION_JSON_VALUE)
  Response putLiDiExportFutureTimetableVersionsCsv();

  @PostMapping(value = "/line-directory/v1/lines/export-csv/timetable-year-change/zip", produces = MediaType.APPLICATION_JSON_VALUE)
  Response putLiDiExportFutureTimetableVersionsZip();

}
